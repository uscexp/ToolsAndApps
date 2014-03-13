/* *****************************************************************
 * Project: common
 * File:    SkipTodayCrontabSchedulerEvent.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler.events;

import haui.exception.AppSystemException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import haui.app.dao.execution.ExecutionDAO;
import haui.app.execution.AppExecutable;
import haui.app.logic.scheduler.DeregisterStrategyCode;
import haui.app.logic.scheduler.ISchedulerEvent;
import haui.app.logic.scheduler.Scheduler;
import haui.model.execution.ExecutionInfoDTO;
import haui.util.DateUtil;
import haui.util.GlobalApplicationContext;

/**
 * SkipTodayCrontabSchedulerEvent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SkipTodayCrontabSchedulerEvent extends CrontabSchedulerEvent
{
  private static final Log LOG = LogFactory.getLog(SkipTodayCrontabSchedulerEvent.class);

  private String executableId = "";

  protected SkipTodayCrontabSchedulerEvent()
  {
    super();
  }

  public SkipTodayCrontabSchedulerEvent(String cronstring, AppExecutable executable)
  {
    super(cronstring, executable);
    executableId = executable.getIdentifier();
    this.setDeregisterStrategies(new DeregisterStrategyCode[] { DeregisterStrategyCode.EXECUTION_FAILED });
  }

  public boolean isTimeForNextExecutionReached()
  {
    if(super.isTimeForNextExecutionReached())
    {
      return !isSkipTodaysExecution();
    }
    return false;
  }

  // checks if this job has run already today and therefore should never run again today triggered
  // by the scheduler
  public boolean isSkipTodaysExecution()
  {
    if(!isJobConfiguredToSkip())
      return false;
    try
    {

      ExecutionInfoDTO[] execInfo = new ExecutionDAO().findLatestExecutionByExecutableIdentifiers(new String[] { executableId });
      if(execInfo != null && execInfo.length > 0 && execInfo[0] == null)
        return false;

      // get start day of the latest execution of this admin job
      Date dateStarted = execInfo[0].getDateStarted();
      String dayStarted = DateUtil.format(dateStarted, false);

      Date current = DateUtil.truncate(DateUtil.getCurrentDay());
      String currentDay = DateUtil.format(current, false);
      LOG.debug("Check if Job (" + executableId + ") has been executed today: Day of last execution: " + dayStarted + " Current day: " + currentDay);
      if(dayStarted.equals(currentDay))
      {
        // today an execution of this job occurred already -> do not execute again today.
        LOG.info("Job (" + executableId + ") has been executed already today (manually or scheduled): Skip scheduled execution of this Job");
        return true;
      }
      return false;
    }
    catch(Exception e)
    {
      LOG.error("Runtime Exception occured in SkipTodayCrontabEvent! Should never happened");
      return false;
    }
  }

  // check in properties if job is configured to skip scheduled execution if once executed today.
  private boolean isJobConfiguredToSkip()
  {
    Properties properties = GlobalApplicationContext.instance().getApplicationProperties();
    List keys = new ArrayList(properties.keySet());
    Collections.sort(keys);
    for(Iterator it = keys.iterator(); it.hasNext();)
    {
      String key = (String)it.next();
      String value = (String)properties.get(key);
      if(!key.startsWith("arte.job.skiptoday.jobname."))
        continue;
      if(executableId.toLowerCase().endsWith(value.trim().toLowerCase()))
        return true;
    }
    return false;
  }

  public void finalise(DeregisterStrategyCode code)
  {
    if(code.equals(DeregisterStrategyCode.EXECUTION_FAILED))
    {
      try
      {
        LOG.info("scheduled (" + this.getExecutable().getIdentifier()
            + ") job execution failed (blocked by other running job) -> execute when preceding job has finished");
        SkipTodayCrontabSchedulerEvent skipTodayCrontabSchedulerEvent = new SkipTodayCrontabSchedulerEvent(this.getCronString(), this.getExecutable());
        Scheduler.getInstance().register(skipTodayCrontabSchedulerEvent);

        /*
         * Check if execute once event has been created and registered while a job block before.
         * This check is done in order to prevent multiple execute once events of the same admin job
         * are created and registered.
         */
        ISchedulerEvent events[] = Scheduler.getInstance().getEvents();
        boolean arteRateEventExists = false;
        for(int i = 0; i < events.length; i++)
        {
          if(events[i] instanceof ArteRateEvent
              && events[i].getExecutable().getIdentifier().equalsIgnoreCase(skipTodayCrontabSchedulerEvent.getExecutable().getIdentifier()))
          {
            arteRateEventExists = true;
          }
        }

        if(!arteRateEventExists)
        {
          ArteRateEvent arteRateEvent = new ArteRateEvent(this.getExecutable());
          arteRateEvent
              .setDeregisterStrategies(new DeregisterStrategyCode[] { DeregisterStrategyCode.EVENT_NOT_ACTIVE, DeregisterStrategyCode.AFTER_FIRST_RUN });
          Scheduler.getInstance().register(arteRateEvent);
        }
      }
      catch(AppSystemException e)
      {}
    }
  }

  // checks if execution will run in the next <minutes> (used for ASI file transfer arrival)
  public boolean isNextExecutionWithinTimeRange(int minutes)
  {
    if(!isActive())
      return false;

    boolean result = false;
    TimeParser timeParser = super.parser;
    long millis = DateUtil.currentTimeMillis();
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(millis);
    for(int i = 0; i < minutes; i++)
    {
      cal.set(Calendar.ZONE_OFFSET, cal.get(Calendar.ZONE_OFFSET) - 60000);
      result = timeParser.isTime(cal.getTime()) || result;
    }
    return result;
  }
}

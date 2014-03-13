/* *****************************************************************
 * Project: common
 * File:    SchedulerController.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler;

import haui.app.core.Application;
import haui.app.dao.db.AbstractDataSource;
import haui.app.execution.AppExecutable;
import haui.app.execution.AppRunnable;
import haui.app.execution.ExecutionContext;
import haui.app.service.command.ServiceContext;
import haui.app.threading.ManagedRunnable;
import haui.app.util.Counter;
import haui.model.data.ExecutionTerminationCode;
import haui.model.execution.ExecutionInfoDTO;
import haui.model.util.SystemPrincipal;
import haui.util.DateUtil;
import haui.util.GlobalApplicationContext;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SchedulerController
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class SchedulerController extends AppRunnable implements ManagedRunnable, Runnable, DeregisterStrategyCode, SchedulerConstants
{
  private static final Log LOG = LogFactory.getLog(SchedulerController.class);
  private boolean canceled = false;

  private int intervallTime = GlobalApplicationContext.instance().getApplicationProperties().getProperty(SCHEDULER_INTERVALL_TIME_PROPERTY, INTERVAL_TIME);

  private int startUpWaitingTime = GlobalApplicationContext.instance().getApplicationProperties().getProperty(SCHEDULER_STARTUP_WAITING_MILLIS_PROPERTY, STARTUP_WAITING_MILLIS);

  private String serviceName = GlobalApplicationContext.instance().getApplicationProperties().getProperty(SCHEDULER_SERVICE_NAME_PROPERTY, SCHEDULER_SERVICE_NAME);

  public SchedulerController()
  {
  }

  public SchedulerController(int intervallTime)
  {
    this.intervallTime = intervallTime;
  }

  public SchedulerController(int intervallTime, int startUpWaitingTime)
  {
    this.intervallTime = intervallTime;
    this.startUpWaitingTime = startUpWaitingTime;
  }

  public void cancel()
  {
    this.canceled = true;
  }

  public String getLabel()
  {
    return "Scheduler";
  }

  public void run()
  {
    run(null);
  }

  public void run(ExecutionContext context)
  {
    try
    {
      runController();
    }
    catch(Throwable t)
    {
      LOG.fatal("The scheduler is not running anymore! Restart the application immediatly!", t);
      t.printStackTrace();
    }
  }

  protected abstract void registerExternalEvents();

  protected abstract void checkExecutionTimeouts();

  /**
   * @return the intervallTime
   */
  public final int getIntervallTime()
  {
    return intervallTime;
  }

  /**
   * @return the startUpWaitingTime
   */
  public final int getStartUpWaitingTime()
  {
    return startUpWaitingTime;
  }

  private void runController() throws Throwable
  {

    ServiceContext.create(new SystemPrincipal(), serviceName);
    // http://www.inventage.com/jira/browse/ARTE-4833
    ServiceContext.remove(ServiceContext.STICKY_DATE);
    DateUtil.setStickyDate(null);

    try
    {
      // wait x minutes
      LOG.info("Scheduler is waiting " + (getStartUpWaitingTime() / 1000) + "s before getting active");
      Thread.sleep(getStartUpWaitingTime());

      // wait until the next minute and add 5 seconds, so we never come near 59 seconds
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(System.currentTimeMillis());
      int seconds = (60 - c.get(Calendar.SECOND));
      if(seconds < 60)
      {
        Thread.sleep((seconds + 5) * 1000);
      }
    }
    catch(InterruptedException e)
    {
      throw new RuntimeException(e);
    }

    LOG.info("Starting the scheduler controller. Precision time is: " + intervallTime);

    Scheduler scheduler = Scheduler.getInstance();
    Map runningEvents = new HashMap();
    Map runCounters = new HashMap();

    while(!canceled)
    {

      if(scheduler.isDisabled())
      {
        LOG.warn("Scheduler is disabled -> Stopping scheduler controller");
        break;
      }

      checkExecutionTimeouts();
      registerExternalEvents();

      ISchedulerEvent[] events = scheduler.getEvents();
      LOG.debug("Checking " + events.length + " events...");

      for(int n = 0; n < events.length; n++)
      {

        if(scheduler.isDisabled())
        {
          LOG.warn("Scheduler is disabled -> Stopping scheduler controller");
          break;
        }

        if(scheduler.isLocked())
        {
          LOG.debug("Scheduler is locked -> no events will be executed");
          break;
        }

        ISchedulerEvent event = events[n];
        String identifier = event.getIdentifier();

        LOG.debug("Checking event: " + identifier);

        Counter counter = (Counter)runCounters.get(identifier);
        if(counter == null)
        {
          runCounters.put(identifier, new Counter());
        }

        ExecutionInfoDTO executionInfo = (ExecutionInfoDTO)runningEvents.get(identifier);
        if(executionInfo != null)
        {

          if(executionInfo.isRunning())
          {
            LOG.debug("Event is running -> no action. Event: " + event.getIdentifier());
            continue;
          }

          runningEvents.remove(identifier);
          counter = (Counter)runCounters.get(identifier);
          counter.increase();
          LOG.debug("Event run counter: " + counter.getLong() + ". Event: " + identifier);

          if(hasStrategy(event, AFTER_FIRST_RUN))
          {
            if(!hasStrategy(event, NOT_DEREGISTRERABLE))
            {
              scheduler.deregister(event, AFTER_FIRST_RUN);
              runCounters.remove(identifier);
            }
            else
            {
              LOG.warn("Event eligible for deregistering but code NOT_DEREGISTRERABLE set as well. Code: " + AFTER_FIRST_RUN + " Event: " + identifier);
            }
            continue;
          }

          ExecutionTerminationCode executionCode = executionInfo.getTerminationCode();
          if(hasStrategy(event, TERMINATION_CODE_FAILED) && executionCode == ExecutionTerminationCode.FAILED)
          {
            if(!hasStrategy(event, NOT_DEREGISTRERABLE))
            {
              scheduler.deregister(event, TERMINATION_CODE_FAILED);
              runCounters.remove(identifier);
            }
            else
            {
              LOG.warn("Event eligible for deregistering but code NOT_DEREGISTRERABLE set as well. Code: " + TERMINATION_CODE_FAILED + " Event: " + identifier);
            }
            continue;
          }

          if(hasStrategy(event, TERMINATION_CODE_CANCEL) && executionCode == ExecutionTerminationCode.CANCELED)
          {
            if(!hasStrategy(event, NOT_DEREGISTRERABLE))
            {
              scheduler.deregister(event, TERMINATION_CODE_CANCEL);
              runCounters.remove(identifier);
            }
            else
            {
              LOG.warn("Event eligible for deregistering but code NOT_DEREGISTRERABLE set as well. Code: " + TERMINATION_CODE_CANCEL + " Event: " + identifier);
            }
            continue;
          }

          if(hasStrategy(event, TERMINATION_CODE_SUCCESSFUL) && executionCode == ExecutionTerminationCode.SUCCESSFUL)
          {
            if(!hasStrategy(event, NOT_DEREGISTRERABLE))
            {
              scheduler.deregister(event, TERMINATION_CODE_SUCCESSFUL);
              runCounters.remove(identifier);
            }
            else
            {
              LOG.warn("Event eligible for deregistering but code NOT_DEREGISTRERABLE set as well. Code: " + TERMINATION_CODE_SUCCESSFUL + " Event: "
                  + identifier);
            }
            continue;
          }
        }

        if(!isActive(event))
        {
          LOG.debug("Event is not active anymore or has been already executed today. Event: " + identifier);
          counter = (Counter)runCounters.get(identifier);
          if(counter.getValue() == Counter.INITIAL_VALUE)
          {
            LOG.debug("Event is not active but has never been executed or has been already executed today -> ignore. Event: " + identifier);
            continue;
          }
          if(hasStrategy(event, EVENT_NOT_ACTIVE))
          {
            if(!hasStrategy(event, NOT_DEREGISTRERABLE))
            {
              scheduler.deregister(event, EVENT_NOT_ACTIVE);
              runCounters.remove(identifier);
            }
            else
            {
              LOG.warn("Event eligible for deregistering but code NOT_DEREGISTRERABLE set as well. Code: " + EVENT_NOT_ACTIVE + " Event: " + identifier);
            }
          }
          continue;
        }

        if(!isTimeForNextExecutionReached(event))
        {
          LOG.debug("Event has time for next execution not reached -> no action. Event: " + identifier);
          continue;
        }

        try
        {
          LOG.debug("Executing event: " + identifier);
          AppExecutable executable = event.getExecutable();
          executionInfo = Application.instance().getExecutionManager().executeAsynchron(executable);
          runningEvents.put(identifier, executionInfo);
        }
        catch(Exception e)
        {
          LOG.error("Failed to execute the event! Event: " + identifier + " Executable: " + event.getExecutable(), e);
          if(hasStrategy(event, EXECUTION_FAILED))
          {
            if(!hasStrategy(event, NOT_DEREGISTRERABLE))
            {
              scheduler.deregister(event, EXECUTION_FAILED);
              runCounters.remove(identifier);
            }
            else
            {
              LOG.warn("Event eligible for deregistering but code NOT_DEREGISTRERABLE set as well. Code: " + EXECUTION_FAILED + " Event: " + identifier);
            }
          }
        }
      }

      try
      {
        // close the session if one exists (avoid long running sessions)
        if(AbstractDataSource.instance().sessionExists())
        {
          AbstractDataSource.instance().closeSession();
        }
      }
      catch(Exception e)
      {
        LOG.error("Failed to reset session in scheduler", e);
      }

      try
      {
        LOG.debug("Waiting " + (intervallTime / 1000) + "s...");
        Thread.sleep(intervallTime);
      }
      catch(InterruptedException e)
      {
        LOG.warn("Scheduler thread has been interrupted -> canceling the SchedulerController!");
        cancel();
      }
    }
    LOG.warn("Scheduler has been canceled!");
  }

  private boolean isTimeForNextExecutionReached(ISchedulerEvent event)
  {
    try
    {
      return event.isTimeForNextExecutionReached();
    }
    catch(Exception e)
    {
      LOG.error("Event method threw an exception (according to convention event methods are actually not allowed to throw exceptions) -> deregistering event",
          e);
      Scheduler scheduler = Scheduler.getInstance();
      scheduler.deregister(event);
      return false;
    }
  }

  private boolean isActive(ISchedulerEvent event)
  {
    try
    {
      return event.isActive();
    }
    catch(Exception e)
    {
      LOG.error("Event method threw an exception (according to convention event methods are actually not allowed to throw exceptions) -> deregistering event",
          e);
      Scheduler scheduler = Scheduler.getInstance();
      scheduler.deregister(event);
      return false;
    }
  }

  private boolean hasStrategy(ISchedulerEvent event, DeregisterStrategyCode code)
  {
    DeregisterStrategyCode[] codes = event.getDeregisterStrategies();
    if(codes == null)
      return false;
    for(int n = 0; n < codes.length; n++)
    {
      if(codes[n] == code)
        return true;
    }
    return false;
  }
}

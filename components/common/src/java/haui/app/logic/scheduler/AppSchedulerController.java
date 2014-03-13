/* *****************************************************************
 * Project: common
 * File:    AppSchedulerController.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler;

import haui.app.dao.execution.ExecutionEventDAO;
import haui.app.execution.AppExecutionManager;
import haui.model.execution.AbstractEventDTO;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AppSchedulerController
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AppSchedulerController extends SchedulerController
{
  private static final Log LOG = LogFactory.getLog(AppSchedulerController.class);

  private ExecutionEventDAO dao = new ExecutionEventDAO();
  private Scheduler scheduler = Scheduler.getInstance();

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.arte.app.logic.scheduler.SchedulerController#registerExternalEvents()
   */
  protected void registerExternalEvents()
  {
    try
    {
      List openEvents = dao.findAllOpenExecutionEvents();

      for(int i = 0; i < openEvents.size(); ++i)
      {
        AbstractEventDTO executionEvent = (AbstractEventDTO)openEvents.get(i);
        ISchedulerEvent schedulerEvent = ExternalExecutionEventBuilder.createSchedulerEventForExternalEvent(executionEvent);
        if(!scheduler.isRegistered(schedulerEvent.getIdentifier()))
        {
          scheduler.register(schedulerEvent);
        }
      }
    }
    catch(Exception e)
    {
      LOG.fatal("Failed to register external events!", e);
    }
  }

  @Override
  protected void checkExecutionTimeouts()
  {
    try
    {
      AppExecutionManager.getInstance().terminateExecutionsWithTimeout();
    }
    catch(Exception e)
    {
      LOG.error("error checking execution timeouts", e);
    }
  }
}

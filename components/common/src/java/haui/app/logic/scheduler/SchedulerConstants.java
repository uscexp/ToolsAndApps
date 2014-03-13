/* *****************************************************************
 * Project: common
 * File:    SchedulerConstants.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler;

/**
 * SchedulerConstants
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface SchedulerConstants
{
  public static final String SCHEDULER_INTERVALL_TIME_PROPERTY = "app.scheduler.intervall.time";
  public static final String SCHEDULER_STARTUP_WAITING_MILLIS_PROPERTY = "app.scheduler.stratup.waiting.millis";
  public static final String SCHEDULER_SERVICE_NAME_PROPERTY = "app.scheduler.service.name";
  public static final String SCHEDULER_EXECUTION_TERMINATION_WAITTIME_PROPERTY = "app.scheduler.execution.termination.waittime";
  public static final int SCHEDULER_DEFAULT_EXECUTION_TERMINATION_WAITTIME = 150000;
  public static final int INTERVAL_TIME = 60000; // be aware that a rate less than 60000ms could cause errors
  public static final int STARTUP_WAITING_MILLIS = 1 * 60000;
  public static final String SCHEDULER_SERVICE_NAME = "/app-scheduler";
}

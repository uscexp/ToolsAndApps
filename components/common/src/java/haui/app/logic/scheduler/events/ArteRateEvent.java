/* *****************************************************************
 * Project: common
 * File:    ArteRateEvent.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler.events;

import haui.app.execution.AppExecutable;
import haui.app.logic.scheduler.Scheduler;

/**
 * An event that is executed every X seconds where X is what is defined under: ArteConstants.SCHEDULER_RATE
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ArteRateEvent extends AbstractSchedulerEvent
{
  public ArteRateEvent()
  {
    super();
  }

  public ArteRateEvent(AppExecutable executable)
  {
    super(executable);
  }

  public int getRateInMillis()
  {
    return Scheduler.getInstance().getPrecisionMillis();
  }

  public boolean isTimeForNextExecutionReached()
  {
    return isActive();
  }
}

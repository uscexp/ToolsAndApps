/* *****************************************************************
 * Project: common
 * File:    ExecuteOnceEvent.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler.events;

import haui.app.execution.AppExecutable;
import haui.app.logic.scheduler.DeregisterStrategyCode;

/**
 * ExecuteOnceEvent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecuteOnceEvent extends ArteRateEvent
{
  public ExecuteOnceEvent()
  {
  }

  public ExecuteOnceEvent(AppExecutable executable)
  {
    super(executable);
  }

  public DeregisterStrategyCode[] getDeregisterStrategies()
  {
    return new DeregisterStrategyCode[] { DeregisterStrategyCode.AFTER_FIRST_RUN };
  }
}

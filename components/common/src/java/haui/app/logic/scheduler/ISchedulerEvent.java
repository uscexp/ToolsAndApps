/* *****************************************************************
 * Project: common
 * File:    ISchedulerEvent.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler;

import haui.app.execution.AppExecutable;

/**
 * ISchedulerEvent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ISchedulerEvent
{
  public void initialize();
  public void finalise(DeregisterStrategyCode code); 
 
  public DeregisterStrategyCode[] getDeregisterStrategies();
  public String getIdentifier();
  public String getDescription();
  public boolean isActive();
  public boolean isTimeForNextExecutionReached();
  
  public AppExecutable getExecutable();
}

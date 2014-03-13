/* *****************************************************************
 * Project: common
 * File:    AbstractSchedulerEvent.java
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
import haui.app.logic.scheduler.ISchedulerEvent;
import haui.util.DateUtil;

import java.util.Date;

/**
 * AbstractSchedulerEvent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractSchedulerEvent implements ISchedulerEvent
{
  private static int counter = 0;
  private String identifier;
  private AppExecutable executable;
  private boolean active = true;
  private final long creationTime = DateUtil.currentTimeMillis();
  private DeregisterStrategyCode[] codes = new DeregisterStrategyCode[] { DeregisterStrategyCode.MANUALLY };

  public AbstractSchedulerEvent()
  {
  }

  public AbstractSchedulerEvent(AppExecutable executable)
  {
    this.executable = executable;
  }

  public DeregisterStrategyCode[] getDeregisterStrategies()
  {
    return codes;
  }

  public void setDeregisterStrategies(DeregisterStrategyCode[] codes)
  {
    assert codes != null && codes.length > 0 : "codes is empty";
    this.codes = codes;
  }

  public final Date getCreationTime()
  {
    return new Date(creationTime);
  }

  public String getIdentifier()
  {
    if(identifier == null)
    {
      setIdentifier(getClass().getName() + "-#" + counter++);
    }
    return identifier;
  }

  public synchronized void setIdentifier(String identifier)
  {
    assert identifier != null : "identifier is null";
    this.identifier = identifier;
  }

  public String getDescription()
  {
    return "Event: " + getIdentifier();
  }

  public boolean isActive()
  {
    return active && executable != null;
  }

  public void setActive(boolean active)
  {
    this.active = active;
  }

  public void setExecutable(AppExecutable executable)
  {
    this.executable = executable;
  }

  public AppExecutable getExecutable()
  {
    return executable;
  }

  public void initialize()
  {
  }

  public void finalise(DeregisterStrategyCode code)
  {
  }

  public abstract boolean isTimeForNextExecutionReached();
}

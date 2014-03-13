/* *****************************************************************
 * Project: common
 * File:    IntervalSchedulerEvent.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler.events;

import haui.app.execution.AppExecutable;
import haui.util.DateUtil;

/**
 * IntervalSchedulerEvent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class IntervalSchedulerEvent extends AbstractSchedulerEvent
{
  private long last = -1;
  private int intervalInSeconds;
  private int intervals = -1;
  private int intervalCounter = 0;

  public IntervalSchedulerEvent()
  {
  }

  public IntervalSchedulerEvent(int intervalInSeconds, AppExecutable executable)
  {
    this(intervalInSeconds, -1, executable);
  }

  public IntervalSchedulerEvent(int intervalInSeconds, int maxIntervals, AppExecutable executable)
  {
    super(executable);
    setIntervalTime(intervalInSeconds);
    setMaxIntervals(maxIntervals);
  }

  public void resetIntervalCounter()
  {
    intervalCounter = 0;
  }

  public boolean isTimeForNextExecutionReached()
  {
    if(!isActive())
      return false;
    long current = DateUtil.currentTimeMillis();
    if(current > last + (intervalInSeconds * 1000))
    {
      last = current;
      intervalCounter++;
      if(intervals > -1 && intervalCounter >= intervals)
      {
        setActive(false);
      }
      return true;
    }
    return false;
  }

  public void initialize()
  {
    last = DateUtil.currentTimeMillis();
    super.initialize();
  }

  public void setMaxIntervals(int intervals)
  {
    this.intervals = intervals;
  }

  public void setIntervalTime(int intervalInSeconds)
  {
    this.intervalInSeconds = intervalInSeconds;
  }
}

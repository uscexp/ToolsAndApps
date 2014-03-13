/* *****************************************************************
 * Project: common
 * File:    SpecificDateEvent.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler.events;

import java.util.Date;

import haui.app.execution.AppExecutable;
import haui.util.DateUtil;

/**
 * SpecificDateEvent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SpecificDateEvent extends AbstractSchedulerEvent
{
  private long time = -1;

  public SpecificDateEvent()
  {
  }

  public SpecificDateEvent(Date date, AppExecutable executable)
  {
    super(executable);
    assert date != null : "date is null";
    time = date.getTime();
  }

  public void setDate(Date date)
  {
    assert date != null : "date is null";
    time = date.getTime();
  }

  public Date getDate()
  {
    return new Date(time);
  }

  public boolean isTimeForNextExecutionReached()
  {
    if(!isActive())
      return false;
    if(DateUtil.currentTimeMillis() >= time)
    {
      setActive(false);
      return false;
    }
    return true;
  }
}

/* *****************************************************************
 * Project: common
 * File:    ExternalExecuteOnceEvent.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler.events;

import haui.app.execution.AppExecutable;
import haui.model.execution.AbstractEventDTO;

/**
 * ExternalExecuteOnceEvent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExternalExecuteOnceEvent extends ExecuteOnceEvent
{
  private AbstractEventDTO executionEvent;
  private String id = null;
  
  protected ExternalExecuteOnceEvent() {
    super();
  }

  protected ExternalExecuteOnceEvent(AppExecutable executable) {
    super(executable);
  }

  public ExternalExecuteOnceEvent(AppExecutable executable, AbstractEventDTO executionEvent) {
    super(executable);
    this.executionEvent = executionEvent;
  }

  public AbstractEventDTO getExecutionEvent() {
    return executionEvent;
  }

  public String getIdentifier() {
        if(id == null)
            id = getClass().getName() + executionEvent.getClass().getName() + executionEvent.getEventTimeStamp().getTime();
        return id;
  }
}

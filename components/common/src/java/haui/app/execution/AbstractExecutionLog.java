/* *****************************************************************
 * Project: common
 * File:    AbstractExecutionLog.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.execution.ExecutionLog;

/**
 * AbstractExecutionLog
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractExecutionLog implements ExecutionLog
{

  public void debug(Object message)
  {
    debug(null, message);
  }

  public void error(Object message, Throwable t)
  {
    error(null, message, t);
  }

  public void warn(Object message)
  {
    warn(null, message);
  }

  public void info(Object message)
  {
    info(null, message);
  }

}

/* *****************************************************************
 * Project: common
 * File:    AppExecutable.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.exception.AppLogicException;

/**
 * interface for all 'executables' in Application. An executable is an object that
 * can be executed in the {@link AppExecutionManager}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface AppExecutable
{
  public final static int NO_TIMEOUT = -1;
  
  public String getIdentifier();
  public void init(ExecutionContext context) throws AppLogicException;
  
  /**
   * get timeout in ms
   * 
   * @return timeout in ms
   */
  public int getTimeout();
}

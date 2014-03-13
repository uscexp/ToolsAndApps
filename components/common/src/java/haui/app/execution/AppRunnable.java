/* *****************************************************************
 * Project: common
 * File:    AppRunnable.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.common.id.STIDGenerator;
import haui.exception.AppLogicException;
import haui.app.execution.ExecutionContext;

/**
 * AppRunnable
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AppRunnable implements AppExecutable
{

  public int getTimeout() {
      return NO_TIMEOUT;
  }

  /**
   * 
   * @param context
   * @throws AppLogicException
   */
  public abstract void run(ExecutionContext context) throws AppLogicException;
  
  /**
   * Answers whether this UOW supports a cleared session.
   * @return <code>true</code> if a session clean up is required, 
   * <code>false</code> otherwise
   */
  public boolean supportsSessionClear() {
      return true;
  }
  
  /**
   * @see AppExecutable#init(ExecutionContext)
   */
  public void init(ExecutionContext context) throws AppLogicException {       
  }

  /**
   * Clients should overwrite if a more meaningful identifier (such as the uuid 
   * of the object beeing processed) is available.
   */
  public String getIdentifier() {
      return STIDGenerator.generate();
  }   
}

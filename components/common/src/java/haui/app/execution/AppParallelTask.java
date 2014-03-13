/* *****************************************************************
 * Project: common
 * File:    AppParallelTask.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.exception.AppLogicException;
import haui.app.execution.ExecutionContext;
import haui.common.id.STIDGenerator;

/**
 * AppParallelTask
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AppParallelTask implements AppExecutable
{
  public abstract AppExecutable[] getRunnables() throws AppLogicException;
  
  public int getBatchSizeHint() {
      return 1;
  }
  
  /**
   * @see ArteExecutable#init(ExecutionContext)
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

  public int getTimeout() {
      return NO_TIMEOUT;
  }
}

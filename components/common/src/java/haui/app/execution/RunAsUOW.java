/* *****************************************************************
 * Project: common
 * File:    RunAsUOW.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import java.security.Principal;

import haui.app.service.command.ServiceContext;
import haui.exception.AppLogicException;

/**
 * RunAsUOW
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class RunAsUOW extends AppRunnable
{
  
  private AppExecutable executable;
  
  private Principal principal;
  
  public RunAsUOW(AppExecutable executable, Principal principal) {
      super();
      this.executable = executable;
      this.principal = principal;
  }

  public void run(ExecutionContext context) throws AppLogicException {
      if (ServiceContext.getPrincipal().equals(principal)) {
          ExecutionManager.instance().executeSynchron(executable);
      }else{
          ExecutionManager.instance().executeAsynchron(executable, principal);  
      }       
  }
}

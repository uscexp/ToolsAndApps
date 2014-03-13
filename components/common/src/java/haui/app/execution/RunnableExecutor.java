/* *****************************************************************
 * Project: common
 * File:    RunnableExecutor.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.dao.db.AbstractDataSource;
import haui.app.service.command.ServiceContext;

/**
 * RunnableExecutor
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
class RunnableExecutor implements Executor
{
  private ExecutionContext context;

  public void init(ExecutionContext context)
  {
    this.context = context;
  }

  private ExecutionContext getContext()
  {
    return context;
  }

  public void execute(AppExecutable executable) throws Throwable
  {
    if(getContext().isCanceled())
      return;
    AppRunnable runnable = (AppRunnable)executable;

    try
    {

      runnable.run(getContext());

    }
    finally
    {
      cleanUp(runnable);
    }
  }

  private void cleanUp(AppRunnable runnable)
  {
    try
    {

      if(runnable.supportsSessionClear())
      {
        AbstractDataSource.instance().clearSessionIfRequired();
      }
      // remove the transaction from the service context
      ServiceContext.remove(ServiceContext.TRANSACTION);
      ServiceContext.remove(ServiceContext.TRANSACTION_ID);
    }
    catch(Throwable t)
    {
      getContext().getLog().error("error cleaning up after runnable", t);
    }
  }
}

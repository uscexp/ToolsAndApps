/* *****************************************************************
 * Project: common
 * File:    JobExecutor.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;


/**
 * {@link Executor} for {@link ArteJob}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
class JobExecutor implements Executor
{
  private ExecutionContext context;

  public void init(ExecutionContext context)
  {
    this.context = context;
  }

  public void execute(AppExecutable executable) throws Throwable
  {
    IJob job = (IJob)executable;
    if(context.isCanceled())
      return;

    job.run(context);
  }
}

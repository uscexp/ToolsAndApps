/* *****************************************************************
 * Project: common
 * File:    SequencialTaskExecutor.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;


/**
 * {@link Executor} for {@link ArteSequencialTask}s.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
class SequencialTaskExecutor implements Executor
{
  private ExecutionContext context;

  /**
   * @param jobExecutor
   * @param task
   */
  public SequencialTaskExecutor()
  {
  }

  public void init(ExecutionContext context)
  {
    this.context = context;
  }

  public void execute(AppExecutable executable) throws Throwable
  {
    if(context.isCanceled())
      return;

    AppSequencialTask sequencialTask = (AppSequencialTask)executable;
    AppExecutable[] executables = sequencialTask.getRunnables();
    int count = executables.length;
    for(int i = 0; i < executables.length; i++)
    {
      if(context.isCanceled())
        return;

      AppExecutable runnable = executables[i];
      try
      {
        context.execute(runnable, 1d / count);
      }
      catch(Exception e)
      {
        if(sequencialTask.abortOnFirstError())
        {
          throw e;
        }
        else
        {
          context.getLog().error(e.getMessage(), e);
        }
      }
    }
  }
}

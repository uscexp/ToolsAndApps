/* *****************************************************************
 * Project: common
 * File:    ExecutorFactory.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.exception.AppSystemException;
import haui.model.core.AbstractMultiApplicationSingleton;

/**
 * ExecutorFactory
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutorFactory extends AbstractMultiApplicationSingleton
{
  static
  {
    AbstractMultiApplicationSingleton.setInstance(new ExecutorFactory());
  }
  /**
   * A private Constructor (this is a singleton)
   */
  private ExecutorFactory()
  {
  }

  /**
   * The singleton accessor
   * 
   * @return the shared instance of {@link ExecutorFactory}
   */
  public static ExecutorFactory instance()
  {
    return (ExecutorFactory)AbstractMultiApplicationSingleton.instance();
  }

  /**
   * Creates a suitable {@link Executor} for the given {@link AppExecutable}
   * 
   * @param executable
   * @return the {@link Executor} created
   */
  Executor createExecutor(AppExecutable executable)
  {
    if(executable instanceof AppUnitOfWork)
    {
      return new UnitOfWorkExecutor();
    }

    if(executable instanceof AppRunnable)
    {
      return new RunnableExecutor();
    }

    if(executable instanceof AppSequencialTask)
    {
      return new SequencialTaskExecutor();
    }

    if(executable instanceof AppParallelTask)
    {
      return new ParallelTaskExecutor();
    }

    if(executable instanceof IJob)
    {
      return new JobExecutor();
    }

    return new UnsuportedExecutableExecutor();
  }

  /**
   * Default Executor... is used whenever a suitable {@link Executor} is not found for a
   * {@link AppExecutable}... it simply reports an error when the {@link AppExecutable} is
   * executed
   * 
   * @author <a href="mailto:mirko.tschaeni@ubs.com">Mirko Tschäni (t290861)</a>
   *         $LastChangedRevision$
   * @since 1.0
   */
  class UnsuportedExecutableExecutor implements Executor
  {
    public void init(ExecutionContext executionContext)
    {
    }

    public void execute(AppExecutable executable) throws Throwable
    {
      throw new AppSystemException("Unknown executable: " + executable);
    }
  }
}

/* *****************************************************************
 * Project: common
 * File:    Executor.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

/**
 * An {@link Executor} is responsible for the execution of a certain kind of {@link ArteExecutable}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface Executor
{
  /**
   * This method is called prior to the execution in order to initialize the {@link Executor}
   * 
   * @param context
   */
  public void init(ExecutionContext context);

  /**
   * Executes the given {@link ArteExecutable}
   * 
   * @param executable
   * @throws Throwable
   */
  public void execute(AppExecutable executable) throws Throwable;
}

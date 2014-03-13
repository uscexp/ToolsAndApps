/* *****************************************************************
 * Project: common
 * File:    NestedExecutionContext.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

/**
 * {@link ExecutionContext} for nested executions of {@link ArteExecutable}s.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class NestedExecutionContext extends AbstractExecutionContext
{
  private ExecutionContext parentContext;
  private ExecutionLog log;
  private ProgressMonitor progressMonitor;

  /**
   * @param execution
   */
  NestedExecutionContext(Execution execution, AppExecutable executable, ExecutionContext parentContext, double progressContribution)
  {
    super(execution, executable);
    this.parentContext = parentContext;
    this.log = new NestedExecutionLog(this, getExecution().getRootLog());
    this.progressMonitor = new NestedProgressMonitor(parentContext.getProgressMonitor(), progressContribution);
  }

  public String getContextId()
  {
    return parentContext.getContextId() + "/" + super.getContextId();
  }

  public ExecutionLog getLog()
  {
    return log;
  }

  public ProgressMonitor getProgressMonitor()
  {
    return progressMonitor;
  }
}

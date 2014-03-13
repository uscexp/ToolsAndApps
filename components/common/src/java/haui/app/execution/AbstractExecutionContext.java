/* *****************************************************************
 * Project: common
 * File:    AbstractExecutionContext.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.service.command.ServiceContext;
import haui.app.threading.ParallelWorkManager;
import haui.exception.AppLogicException;

import java.util.Map;

/**
 * AbstractExecutionContext
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractExecutionContext implements ExecutionContext
{
  private Execution execution;
  private AppExecutable executable;

  public AbstractExecutionContext(Execution execution, AppExecutable executable)
  {
    this.execution = execution;
    this.executable = executable;
  }

  public String getContextId()
  {
    return getExecutable().getIdentifier();
  }

  public boolean hasPendingTranaction()
  {
    return ServiceContext.insideTransaction();
  }

  public void initializeThread(ServiceContext parentServiceContext) throws AppLogicException
  {
    assert parentServiceContext != null : "a parent service context must be provided";
    execution.initializeThread(parentServiceContext);
  }

  public void cleanUpThread()
  {
    execution.cleanUpThread();
  }

  protected Execution getExecution()
  {
    return execution;
  }

  public boolean isCanceled()
  {
    return execution.isCanceled();
  }

  public ParallelWorkManager createParallelWorkManager()
  {
    return execution.createParallelWorkManager();
  }

  public ExecutionStatistics getStatistics()
  {
    return getExecution().getAppJobExecutionStatistics();
  }

  public void execute(AppExecutable executable, double progressContribution)
  {
    this.execution.execute(this, executable, progressContribution);
  }

  AppExecutable getExecutable()
  {
    return executable;
  }

  public boolean put(String key, Object value)
  {
    execution.put(key, value);
    return true;
  }

  public Object get(String key)
  {
    return execution.get(key);
  }

  public Map getKeyValuePairs()
  {
    return execution.getKeyValuePairs();
  }
}

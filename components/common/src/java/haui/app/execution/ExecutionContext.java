/* *****************************************************************
 * Project: common
 * File:    ExecutionContext.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import java.util.Map;

import haui.app.service.command.ServiceContext;
import haui.app.threading.ParallelWorkManager;
import haui.exception.AppLogicException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The context of the execution of a {@link AppExecutable}.
 * <p>
 * The {@link AppExecutable} can use the {@link ExecutionContext} it is executed
 * with to start nested executions of {@link AppExecutable}s. In addition to 
 * that the {@link ExecutionContext} provides access to {@link ExecutionLog},
 * {@link ExecutionStatistics} and {@link ProgressMonitor}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ExecutionContext
{
  public static final Log LOG = LogFactory.getLog(ExecutionContext.class);
  
  /**
   * Initializes {@link Thread#currentThread()} with a valid
   * @throws AppLogicException
   */
  public void initializeThread(ServiceContext parentServiceContext) throws AppLogicException;
  /**
   * Cleans up {@link Thread#currentThread()}
   *
   */
  public void cleanUpThread();
  /**
   * Answers a new {@link ParallelWorkManager} to be used in the context
   * <p>
   * 
   * @return the {@link ParallelWorkManager}
   */
  public ParallelWorkManager createParallelWorkManager();    
  /**
   * 
   * @return the string id of this {@link ExecutionContext}
   */
  public String getContextId();
  /**
   * 
   * @return the {@link ExecutionLog} of this {@link ExecutionContext}
   */
  public ExecutionLog getLog();
  /**
   * 
   * @return the {@link ProgressMonitor} of this {@link ExecutionContext}
   */
  public ProgressMonitor getProgressMonitor();
  /**
   * 
   * @return the {@link ExecutionStatistics} of this {@link ExecutionContext}
   */
  public ExecutionStatistics getStatistics();
  /**
   * put a key value pair
   */
  public boolean put(String key, Object value);
  /**
   * get a value with a given key
   */
  public Object get(String key);
  /**
   * get a value with a given key
   */
  public Map getKeyValuePairs();
  /**
   * 
   * @return <code>true</code> if the execution has been canceled, <code>false
   * </code> otherwise
   */
  public boolean isCanceled();
  /**
   * Starts a nested exectution with the given {@link AppExecutable}
   * @param executable the {@link AppExecutable} to execute
   * @param progressContribution the relative (0-1) contribution to the progress
   * of this {@link ExecutionContext}
   */
  public void execute(AppExecutable executable, double progressContribution);
  /**
   * Checks if at the moment a trasaction is open.
   * @return <code>true</code> if a transaction is open at the moment
   */
  public boolean hasPendingTranaction();    
}

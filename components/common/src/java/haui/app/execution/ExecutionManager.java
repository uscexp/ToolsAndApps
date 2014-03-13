/* *****************************************************************
 * Project: common
 * File:    ExecutionManager.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.dao.admin.AdminJobDAO;
import haui.app.service.command.ServiceContext;
import haui.app.service.command.ServiceValidationContext;
import haui.exception.AppLogicException;
import haui.exception.AppSystemException;
import haui.exception.ValidationException;
import haui.model.admin.AdminJobDTO;
import haui.model.core.AbstractMultiApplicationSingleton;
import haui.model.execution.ExecutionIdentifier;
import haui.model.execution.ExecutionInfoDTO;
import haui.util.ClassUtil;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ExecutionManager
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class ExecutionManager extends AbstractMultiApplicationSingleton
{

  // log
  private static final Log LOG = LogFactory.getLog(ExecutionManager.class);

  /**
   * resolves {@link ExecutionIdentifier} to {@link Execution}s
   */
  private Map executionIdentifierToExecution = new HashMap();

  protected ExecutionManager()
  {
  }
  
  public static ExecutionManager instance()
  {
    return (ExecutionManager)AbstractMultiApplicationSingleton.instance();
  }

  /**
   * Answers the {@link Execution} that is currently execution the given {@link ExecutionIdentifier}
   * or <code>null</code> if no such {@link Execution} exists.
   * 
   * @param executionIdentifier
   * @return the {@link Execution} running the {@link Execution} identified by the given
   *         {@link ExecutionIdentifier} or <code>null</code>
   */
  protected Execution getExecutor(ExecutionIdentifier executionIdentifier)
  {
    return (Execution)executionIdentifierToExecution.get(executionIdentifier);
  }

  /**
   * Checks if the given exclusionKey is currently locked (an execution is running that locks the
   * key).
   * 
   * @param mutualExclusionKey
   * @return <code>true</code> if the mutex key is locked, <code>false</code> otherwise
   */
  protected boolean isLocked(String mutualExclusionKey)
  {
    /*
     * this implementation might not perform all too good if executions are used very frequently...
     * 
     * should be replaced by a map based implementation instead of an iteration
     * 
     * At the moment this is not a problem because executions are most only used for jobs.
     */
    Iterator executorIterator = executionIdentifierToExecution.values().iterator();

    while(executorIterator.hasNext())
    {
      Execution currentExecutor = (Execution)executorIterator.next();

      if(currentExecutor.locks(mutualExclusionKey))
      {
        return true;
      }
    }

    return false;
  }

  protected String getBlockingJobName(String mutualExclusionKey)
  {
    Iterator executorIterator = executionIdentifierToExecution.values().iterator();

    while(executorIterator.hasNext())
    {
      Execution currentExecutor = (Execution)executorIterator.next();

      if(currentExecutor.locks(mutualExclusionKey))
      {
        String label = currentExecutor.getLabel();
        AppExecutable executable = currentExecutor.getExecutable();
        if(executable == null)
          return null;

        String identifier = executable.getIdentifier();
        return identifier.substring(AdminJobDTO.EXECUTABLE_IDENTIFIER_PREFIX.length());
      }
    }

    return null;
  }

  protected void remember(Execution executor)
  {
    executionIdentifierToExecution.put(executor.getExecutionIdentifier(), executor);
  }

  protected void forget(Execution executor)
  {
    executionIdentifierToExecution.remove(executor.getExecutionIdentifier());
  }

  /**
   * Cancels the execution identified by the given {@link ExecutionIdentifier}
   * 
   * @param executionIdentifier
   * @return the {@link ExecutionInfoDTO} representing the execution identified by the given
   *         {@link ExecutionIdentifier}, null if the exeution is not running
   * @throws ArteBusinessException
   */
  public synchronized ExecutionInfoDTO cancel(ExecutionIdentifier executionIdentifier)
  {
    Execution executor = getExecutor(executionIdentifier);

    if(executor == null)
    {
      return null;
    }

    executor.cancel();

    return executor.getExecutionInfo();
  }

  /**
   * Answers all currently running {@link ExecutionInfoDTO} representing a {@link AdminJobDTO}
   * 
   * @return all running jobs
   */
  public ExecutionInfoDTO[] findAllRunningJobs()
  {
    ExecutionInfoDTO[] allRunningExecutions = findAllRunningExecutions();
    Set runningJobs = new HashSet();

    for(int i = 0; i < allRunningExecutions.length; i++)
    {
      if(AdminJobDAO.isJob(allRunningExecutions[i]))
      {
        runningJobs.add(allRunningExecutions[i]);
      }
    }

    return (ExecutionInfoDTO[])runningJobs.toArray(new ExecutionInfoDTO[runningJobs.size()]);
  }

  /**
   * Answers all currently maintained {@link ExecutionInfoDTO}s
   * 
   * @return all running {@link ExecutionInfoDTO}s
   */
  public synchronized ExecutionInfoDTO[] findAllRunningExecutions()
  {
    Set allRunningExecutions = new HashSet();
    Collection newExecutionIdentifier = Collections.synchronizedCollection(executionIdentifierToExecution.values());

    if(newExecutionIdentifier == null)
    {
      newExecutionIdentifier = new HashSet();
    }
    Iterator executorIterator = newExecutionIdentifier.iterator();

    while(executorIterator.hasNext())
    {
      Execution currentExecutor = (Execution)executorIterator.next();

      allRunningExecutions.add(currentExecutor.getExecutionInfo());
    }

    return (ExecutionInfoDTO[])allRunningExecutions.toArray(new ExecutionInfoDTO[allRunningExecutions.size()]);
  }

  /**
   * Answers the {@link ExecutionInfoDTO} representing the given {@link ExecutionIdentifier} if
   * there is one, <code>null</code> otherwise.
   * 
   * @param executionIdentifier
   * @return the {@link ExecutionInfoDTO} representing the given {@link ExecutionIdentifier} or
   *         <code>null</code>
   */
  public synchronized ExecutionInfoDTO findExecutionByIdentifier(ExecutionIdentifier executionIdentifier)
  {
    Execution executor = getExecutor(executionIdentifier);

    if(executor != null)
    {
      return executor.getExecutionInfo();
    }

    return null;
  }

  /**
   * Answers the {@link ExecutionInfoDTO}s representing the given {@link ExecutionIdentifier}s
   * 
   * @param executionIdentifiers
   * @return the {@link ExecutionInfoDTO}s corresponging to the given {@link ExecutionIdentifier}s
   */
  public synchronized ExecutionInfoDTO[] findExecutionsByIdentifier(ExecutionIdentifier[] executionIdentifiers)
  {
    Set executions = new HashSet();

    for(int i = 0; i < executionIdentifiers.length; i++)
    {
      Execution executor = getExecutor(executionIdentifiers[i]);
      if(executor != null)
      {
        executions.add(executor.getExecutionInfo());
      }
    }

    return (ExecutionInfoDTO[])executions.toArray(new ExecutionInfoDTO[executions.size()]);
  }

  /**
   * Implements all possible modes of the execution of an ArteExecutable
   * 
   * @param executable
   * @param waitForParentServiceTermination whether or not the started thread should wait until the
   *          current {@link ServiceContext} has been destroyed
   * @return the {@link ExecutionInfoDTO} representing the execution of the given
   *         {@link ArteExecutable}
   * @throws AppLogicException
   */
  protected Execution startExecutable(AppExecutable executable, ExecutionMode mode) throws AppLogicException, AppSystemException
  {
    Principal principal = ServiceContext.getPrincipal();
    ServiceValidationContext validationContext = ServiceContext.getValidationContext();
    Set lockers = ServiceContext.getLockers();

    return startExecutable(executable, mode, principal, validationContext, lockers);
  }

  /**
   * Implements all possible modes of the execution of an ArteExecutable
   * 
   * @param executable
   * @param waitForParentServiceTermination whether or not the started thread should wait until the
   *          current {@link ServiceContext} has been destroyed
   * @return the {@link ExecutionInfoDTO} representing the execution of the given
   *         {@link ArteExecutable}
   * @throws AppLogicException
   */
  protected Execution startExecutable(AppExecutable executable, ExecutionMode mode, Principal principal, ServiceValidationContext validationContext,
      Set lockers) throws AppLogicException, AppSystemException
  {

    Execution execution = null;
    synchronized(this)
    {
      checkMutualExclusion(executable);
      // setup the execution
      // use principal and owned lockers from the current servicecontext

      execution = new Execution(executable, mode, principal, validationContext, lockers);
      remember(execution);

      if(mode != ExecutionMode.SYNCHRON)
      {
        execution.start(mode == ExecutionMode.POSTPONED);
      }
    }

    if(mode == ExecutionMode.SYNCHRON)
    {
      execution.run();
    }

    return execution;
  }

  /**
   * Asynchronously runs the given {@link ArteExecutable} (spawns a {@link Thread}
   * 
   * @param executable
   * @param waitForParentServiceTermination whether or not the started thread should wait until the
   *          current {@link ServiceContext} has been destroyed
   * @return the {@link ExecutionInfoDTO} representing the execution of the given
   *         {@link ArteExecutable}
   * @throws AppLogicException
   */
  public ExecutionInfoDTO executeAsynchron(AppExecutable executable, Principal principal) throws AppLogicException
  {
    // ServiceValidationContext validationContext = new ServiceValidationContext(AdminJobBO.class,
    // executable.getIdentifier(), true);
    Class serviceBOClass = null;
    try
    {
      serviceBOClass = ClassUtil.loadClass("com.ubs.arte.app.logic.admin.AdminJobBO");
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage());
    }
    ServiceValidationContext validationContext = new ServiceValidationContext(serviceBOClass, executable.getIdentifier(), true);
    Set lockers = new HashSet();
    return startExecutable(executable, ExecutionMode.ASYNCHRON, principal, validationContext, lockers).getExecutionInfo();
  }

  /**
   * Asynchronously runs the given {@link ArteExecutable} (spawns a {@link Thread}
   * 
   * @param executable
   * @param waitForParentServiceTermination whether or not the started thread should wait until the
   *          current {@link ServiceContext} has been destroyed
   * @return the {@link ExecutionInfoDTO} representing the execution of the given
   *         {@link ArteExecutable}
   * @throws AppLogicException
   */
  public ExecutionInfoDTO executeAsynchron(AppExecutable executable) throws AppLogicException
  {
    return startExecutable(executable, ExecutionMode.ASYNCHRON).getExecutionInfo();
  }

  /**
   * Executes the given {@link ArteExecutable} as soon as the current {@link ServiceContext} was
   * destroyed.
   * 
   * @param executable
   * @param waitForParentServiceTermination whether or not the started thread should wait until the
   *          current {@link ServiceContext} has been destroyed
   * @throws AppLogicException
   */
  public void executePostponed(AppExecutable executable, Principal principal) throws AppLogicException
  {
    ServiceContext.addPostponedExecutions(new RunAsUOW(executable, principal));
    // ServiceValidationContext validationContext = new
    // ServiceValidationContext(ArteBusinessObject.class, executable.getIdentifier(), true);
    // Set lockers = new HashSet();
    // return startExecutable(executable, ExecutionMode.POSTPONED, principal, validationContext,
    // lockers).getExecutionInfo();
  }

  /**
   * Executes the given {@link ArteExecutable} as soon as the current {@link ServiceContext} was
   * destroyed.
   * 
   * @param executable
   * @param waitForParentServiceTermination whether or not the started thread should wait until the
   *          current {@link ServiceContext} has been destroyed
   * @throws AppLogicException
   */
  public void executePostponed(AppExecutable executable) throws AppLogicException
  {
    ServiceContext.addPostponedExecutions(new RunAsUOW(executable, ServiceContext.getPrincipal()));
  }

  /**
   * Synchronously runs an executable.
   * 
   * @param executable
   * @throws AppLogicException
   */
  public void executeSynchron(AppExecutable executable) throws AppLogicException, AppSystemException
  {
    startExecutable(executable, ExecutionMode.SYNCHRON);
  }

  /**
   * Checks if the given {@link ArteJob} is allowed to run and throws a {@link ValidationException}
   * if it is blocked by a mutual exclusion key.
   * 
   * @param job
   * @throws AppLogicException
   */
  protected void checkMutualExclusion(AppExecutable executable) throws AppLogicException
  {
    if(executable instanceof IJob)
    {
      IJob job = (IJob)executable;

      // check mutual exclusion
      String mutualExclusionKey = job.getAdminJob().getCode();
      if(mutualExclusionKey != null)
      {
        if(isLocked(mutualExclusionKey))
        {
          String blockingJob = getBlockingJobName(mutualExclusionKey);
          if(blockingJob == null)
            blockingJob = "unknown";
          throw new AppLogicException("Job is locked by " + blockingJob);
        }

      }
    }
  }

  /**
   * Blocks until the execution associated with the gieven {@link ExecutionIdentifier} has
   * terminated.
   * 
   * @param executionIdentifier
   */
  public void join(ExecutionIdentifier executionIdentifier)
  {
    Execution executor = getExecutor(executionIdentifier);
    if(executor != null)
    {
      try
      {
        synchronized(executor)
        {
          executor.wait();
        }
      }
      catch(Throwable t)
      {
        LOG.error("Error joining execution", t);
      }
    }
  }

  /**
   * This method is called when an {@link Execution} is terminated.
   * <p>
   * The {@link Execution} is then removed from the known Executions (it is now in the DB) and all
   * Threads waiting for the Execution to terminate are notified.
   * 
   * @param execution
   */
  protected void executionDone(Execution execution)
  {
    // wake up all threads that are waiting for the terminated execution
    synchronized(execution)
    {
      execution.notifyAll();
    }
    forget(execution);

    LOG.debug("executionDone: " + execution);
  }
}

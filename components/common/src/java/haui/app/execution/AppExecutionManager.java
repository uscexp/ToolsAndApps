/* *****************************************************************
 * Project: common
 * File:    AppExecutionManager.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import haui.app.logic.admin.jobxmls.Job;
import haui.app.service.command.ServiceValidationContext;
import haui.exception.AppLogicException;
import haui.exception.AppSystemException;
import haui.exception.ValidationException;
import haui.model.execution.ExecutionInfoDTO;
import haui.util.DateUtil;

/**
 * Singleton starting point for executing {@link AppExecutable}s.
 * <p>
 * This class provides a singleton {@link AppExecutionManager#instance()} instance to be used for managing
 * {@link AppExecutable}s such as {@link AppJob}s.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AppExecutionManager extends ExecutionManager
{
  private static final Log LOG = LogFactory.getLog(AppExecutionManager.class);

  // singleton instance
  static
  {
    setInstance(new AppExecutionManager());
  }

  /**
   * Private constructor... (singleton)
   */
  private AppExecutionManager()
  {
  }

  public static AppExecutionManager getInstance()
  {
    return (AppExecutionManager)instance();
  }

  /**
   * Answers the {@link ExecutionInfoDTO}s representing the given substring identifier of the
   * executable
   * 
   * @param substring identifier of the executable
   * @return the {@link ExecutionInfoDTO}s
   */
  public synchronized ExecutionInfoDTO[] findExecutionsBySubstringExecutableIdentifier(String executableIdentifierSubstring)
  {
    ExecutionInfoDTO[] executionInfos = findAllRunningExecutions();
    List<ExecutionInfoDTO> result = new ArrayList<ExecutionInfoDTO>();
    if(executionInfos == null)
      return null;

    for(int i = 0; i < executionInfos.length; ++i)
    {
      ExecutionInfoDTO executionInfoDTO = executionInfos[i];
      if(executionInfoDTO.getExecutableId().indexOf(executableIdentifierSubstring) > -1)
      {
        result.add(executionInfoDTO);
      }
    }
    return (ExecutionInfoDTO[])result.toArray(new ExecutionInfoDTO[result.size()]);
  }

  @Override
  protected Execution startExecutable(AppExecutable executable, ExecutionMode mode, Principal principal, ServiceValidationContext validationContext,
      Set lockers) throws AppLogicException, AppSystemException
  {

    Execution execution = null;
    synchronized(this)
    {
      checkMutualExclusion(executable);
      checkPreconditions(executable);
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
   * check job preconditions defined in job dependency xml
   * 
   * @param executable
   * @throws ValidationException 
   */
  protected void checkPreconditions(AppExecutable executable)
  {
    if(executable instanceof AppJob)
    {
      AppJob riskJob = (AppJob)executable;

      // check job preconditions
      Job job = riskJob.getAppjobdependencies().getJob(riskJob.getJobName());
      if(job != null)
      {
        if(!job.evaluateCondition())
        {
          throw new AppSystemException("Preconditon for job " + job.getName() + " failed!");
        }

      }
    }
  }

  public void terminateExecutionsWithTimeout()
  {
    try
    {
      ExecutionInfoDTO[] executionInfos = findAllRunningExecutions();
      List executionsToTerminate = new ArrayList();

      for(int i = 0; i < executionInfos.length; ++i)
      {
        ExecutionInfoDTO executionInfo = executionInfos[i];

        if(!executionInfo.isCanceled() && !executionInfo.isTerminated())
        {
          Execution execution = getExecutor(executionInfo.getExecutionIdentifier());
          AppExecutable executable = execution.getExecutable();

          Date starteDate = executionInfo.getDateStarted();
          int timeout = executable.getTimeout();
          if(timeout != AppExecutable.NO_TIMEOUT)
          {
            Date timeoutDate = DateUtil.addMillis(starteDate, timeout);
            if(DateUtil.isAfter(DateUtil.getCurrentTimeStamp(), timeoutDate))
              executionsToTerminate.add(execution);
          }
        }
      }

      for(int i = 0; i < executionsToTerminate.size(); ++i)
      {
        Execution execution = (Execution)executionsToTerminate.get(i);

        try
        {
          execution.forceTermination();
        }
        catch(Exception e)
        {
          LOG.error("error terminating execution with timeout", e);
        }
      }
    }
    catch(Exception e)
    {
      LOG.error("error terminating executions with timeout", e);
    }
  }
}

/* *****************************************************************
 * Project: common
 * File:    RootExecutionContext.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;
import haui.app.dao.execution.ExecutionLogDAO;
import haui.app.dao.partner.UserDAO;
import haui.model.data.ExecutionStatusCode;
import haui.model.data.ExecutionTerminationCode;
import haui.model.execution.ExecutionInfoDTO;
import haui.model.execution.ExecutionLogDTO;
import haui.model.partner.UserDTO;
import haui.util.DateUtil;

/**
 * RootExecutionContext
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
class RootExecutionContext extends AbstractExecutionContext
{

  private static final int MAX_EXECUTION_LOG_FILE_SIZE = 10485760; // in Bytes = 10 MB

  private RootExecutionLog log;
  private ProgressMonitor progressMonitor;
  private ExecutionInfoDTO executionInfo;

  public RootExecutionContext(Execution execution)
  {
    super(execution, execution.getExecutable());
    UserDTO user = new UserDAO().getCurrentUser();
    this.executionInfo = new ExecutionInfoDTO(getExecution().getExecutable().getIdentifier(), getExecution().getExecutionIdentifier(), user);
    this.executionInfo.setDateScheduled(DateUtil.getCurrentTimeStamp());

    this.log = new RootExecutionLog(execution);
    this.progressMonitor = new RootProgressMonitor();
  }

  public ExecutionInfoDTO getExecutionInfo()
  {
    executionInfo.setSummaryLog(createSummaryLog());
    Set keys = getKeyValuePairs().keySet();
    Iterator iterator = keys.iterator();

    while(iterator.hasNext())
    {
      String key = (String)iterator.next();
      executionInfo.put(key, get(key));
    }
    return executionInfo;
  }

  private String createSummaryLog()
  {
    String statistics = getExecution().getAppJobExecutionStatistics().getTextualStatistics();

    if(statistics.length() > 3000)
    {
      statistics = statistics.substring(0, 3000) + "\n...";
    }
    return statistics;
  }

  // create execution log file when execution has terminated.
  public ExecutionLogDTO createLogFile()
  {
    ExecutionLogDAO executionLogDAO = new ExecutionLogDAO();

    // no log content available -> do not create any log file
    if(log.getTextualErrors().length() == 0 && log.getTextualWarnings().length() == 0)
    {
      return null;
    }

    // check if execution info is persistent
    assert executionInfo.getStid() != null : "executionInfo has no stid";

    ExecutionLogDTO executionLogFile = executionLogDAO.findExecutionLogByIdentifier(executionInfo);

    if(executionLogFile != null)
    {
      LOG.error("Execution Info exists already -> must never happen!");
      return null;
    }

    executionLogFile = new ExecutionLogDTO(executionInfo);

    String fullLOG = log.getTextualErrors() + "\n\n" + log.getTextualWarnings();

    LOG.debug("Execution log file creation! Log string length is: " + fullLOG.getBytes().length);
    LOG.debug("Log file content is:\n" + fullLOG);

    if(fullLOG.length() > MAX_EXECUTION_LOG_FILE_SIZE)
    {
      fullLOG = fullLOG.substring(0, MAX_EXECUTION_LOG_FILE_SIZE) + "\n...";
    }

    executionLogFile.setDeflatedLogFile(fullLOG.getBytes());

    return executionLogFile;
  }

  RootExecutionLog getRootLog()
  {
    return log;
  }

  public ExecutionLog getLog()
  {
    return log;
  }

  public ProgressMonitor getProgressMonitor()
  {
    return progressMonitor;
  }

  public void executionStarted()
  {
    executionInfo.setExecutionStatusCode(ExecutionStatusCode.RUNNING);
    executionInfo.setDateStarted(DateUtil.getCurrentTimeStamp());
    getRootLog().executionStarted();
  }

  public void executionSucessful()
  {
    executionInfo.setExecutionStatusCode(ExecutionStatusCode.TERMINATED);
    executionInfo.setTerminationCode(ExecutionTerminationCode.SUCCESSFUL);
    if(isCanceled())
    {
      executionInfo.setTerminationCode(ExecutionTerminationCode.CANCELED);
    }
  }

  public void executionCanceled()
  {
    executionInfo.setExecutionStatusCode(ExecutionStatusCode.WAITING_FOR_CANCEL);
    executionInfo.setTerminationCode(ExecutionTerminationCode.CANCELED);
    executionInfo.setDateCanceled(DateUtil.getCurrentTimeStamp());
  }

  public void executionFailed(Throwable t)
  {
    executionInfo.setExecutionStatusCode(ExecutionStatusCode.TERMINATED);
    executionInfo.setTerminationCode(ExecutionTerminationCode.FAILED);
  }

  public void executionTerminated()
  {
    executionInfo.setExecutionStatusCode(ExecutionStatusCode.TERMINATED);
    executionInfo.setProgress(1);
    executionInfo.setDateTerminated(DateUtil.getCurrentTimeStamp());
  }

  private class RootProgressMonitor implements ProgressMonitor
  {

    public void done()
    {
      executionInfo.setProgress(1);
      getExecution().touched();
    }

    public void worked(double work)
    {
      double progress = executionInfo.getProgress();
      if(progress < 0)
        progress = 0;

      progress += work;

      if(progress < 0)
        progress = 0;
      if(progress > 1)
        progress = 1;

      executionInfo.setProgress(progress);
      getExecution().touched();
    }
  }
}

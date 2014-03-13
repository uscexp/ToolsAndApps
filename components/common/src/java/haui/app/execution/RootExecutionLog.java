/* *****************************************************************
 * Project: common
 * File:    RootExecutionLog.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.dao.AppBaseDAO;
import haui.exception.AppLogicException;
import haui.model.DataTransferObject;
import haui.util.AppUtil;
import haui.util.GlobalApplicationContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * RootExecutionLog
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
class RootExecutionLog extends AbstractExecutionLog
{
  public static final String LOG_IMEDIATELY_PROPERTY_KEY = ExecutionLog.class.getName() + ".logImediately";
  private Execution execution;

  private List errorLog = new ArrayList();
  private List warnLog = new ArrayList();
  private List infoLog = new ArrayList();

  /**
   * 
   */
  public RootExecutionLog(Execution execution)
  {
    this.execution = execution;
  }

  Execution getExecution()
  {
    return execution;
  }

  private boolean logImediately()
  {
    return GlobalApplicationContext.instance().getApplicationProperties().getProperty(LOG_IMEDIATELY_PROPERTY_KEY, true);
  }

  public void error(String stid, Object message, Throwable t)
  {
    error(getExecution().getRootExecutionContext(), stid, message, t);
  }

  public void debug(String stid, Object message)
  {
    debug(getExecution().getRootExecutionContext(), stid, message);
  }

  public void warn(String stid, Object message)
  {
    warn(getExecution().getRootExecutionContext(), stid, message);
  }

  public void info(String stid, Object message)
  {
    info(getExecution().getRootExecutionContext(), stid, message);
  }

  protected synchronized void info(ExecutionContext context, String stid, Object message)
  {
    LogEntry enty = new LogEntry(context, stid, String.valueOf(message), null);
    infoLog.add(enty);
    if(logImediately())
      LOG.info(enty);
    getExecution().touched();
  }

  protected synchronized void debug(ExecutionContext context, String stid, Object message)
  {
    LogEntry enty = new LogEntry(context, stid, String.valueOf(message), null);
    LOG.debug(enty);
    getExecution().touched();
  }

  protected synchronized void error(ExecutionContext context, String stid, Object message, Throwable t)
  {
    getExecution().getRootExecutionContext().getStatistics().increase(AppJobExecutionStatistics.DEFAULT_TOTAL_ERROR);
    LogEntry enty = new LogEntry(context, stid, String.valueOf(message), t);
    errorLog.add(enty);
    if(logImediately())
      LOG.error(enty);
    getExecution().touched();
  }

  protected synchronized void warn(ExecutionContext context, String stid, Object message)
  {
    LogEntry enty = new LogEntry(context, stid, String.valueOf(message), null);
    warnLog.add(enty);
    if(logImediately())
      LOG.warn(enty);
    getExecution().touched();
  }

  void executionStarted()
  {
  }

  String getTextualWarnings()
  {
    return formatLogMessages("Warnings:", warnLog, true);
  }

  String getTextualErrors()
  {
    return formatLogMessages("Errors:", errorLog, true);
  }

  void executionTerminated()
  {
    // dump error
    if(LOG.isErrorEnabled())
    {
      String message = formatLogMessages("ERROR", errorLog, false);
      if(!message.equals(""))
      {
        LOG.error(message);
      }
    }

    // dump warnings
    if(LOG.isWarnEnabled())
    {
      String message = formatLogMessages("WARNING", warnLog, false);
      if(!message.equals(""))
      {
        LOG.warn(message);
      }

    }

    // dump warnings
    if(LOG.isInfoEnabled())
    {
      String message = formatLogMessages("INFO", infoLog, false);
      if(!message.equals(""))
      {
        LOG.info(message);
      }
    }
  }

  private synchronized String formatLogMessages(String type, List logEntries, boolean plain)
  {
    if(logEntries.size() == 0)
      return "";
    StringBuffer messageBuffer = new StringBuffer();

    if(plain)
    {
      messageBuffer.append(type + "\n");
    }
    else
    {
      messageBuffer.append("<Execution " + type + " Report " + logEntries.size());
    }

    Iterator entryIterator = logEntries.iterator();
    while(entryIterator.hasNext())
    {
      LogEntry entry = (LogEntry)entryIterator.next();
      messageBuffer.append("\n" + entry);
    }

    return messageBuffer.toString().trim();
  }

  private class LogEntry
  {
    ExecutionContext context;
    String stid;
    String message;
    Throwable throwable;
    private String threadName;
    long memUsage;

    /**
       * 
       */
    public LogEntry(ExecutionContext context, String stid, String message, Throwable throwable)
    {
      this.context = context;
      this.stid = stid;
      this.message = message;
      this.throwable = throwable;
      this.threadName = Thread.currentThread().getName();
      this.memUsage = AppUtil.getMemoryUsage();
    }

    public String toString()
    {
      // Format:
      // <Execution xxxxxxxxxxx /iEOD_EuropeOtherProducts/stateChanges/ xxxxxxxxxxx> StateTransition
      // FIN.SGN -> PRO.RES Object: MPR_xxxxxx
      // executionId context thread message objectIdentifier
      // newline
      // exception (incl stack trace)
      StringBuffer messageBuffer = new StringBuffer();

      // intro

      messageBuffer.append("<Execution ");
      // executionId
      messageBuffer.append(getExecution().getExecutionIdentifier() + " ");
      // context path
      messageBuffer.append("/" + context.getContextId());
      // thread
      if(!getExecution().isMainThread())
      {
        messageBuffer.append(" " + threadName + ">");
      }
      else
      {
        messageBuffer.append(">");
      }

      // message
      messageBuffer.append(" " + message + " ");
      if(stid != null)
      {
        messageBuffer.append("Object id: " + stid + ") ");
        DataTransferObject dto = (DataTransferObject)new AppBaseDAO().findByStid(DataTransferObject.class, stid);
        if(dto != null)
        {
          messageBuffer.append("[Short description: " + dto.getShortDescription() + "] ");
        }
      }

      // only the message is logged as long as its a business exception
      if(throwable != null && throwable instanceof AppLogicException)
      {
        messageBuffer.append("\n" + throwable.getMessage());
      }

      // otherwise log the stacktrace
      if(throwable != null && !(throwable instanceof AppLogicException))
      {
        messageBuffer.append("\n" + stackTraceToString(throwable));
      }

      return messageBuffer.toString();
    }
  }

  private static String stackTraceToString(Throwable t)
  {
    if(t == null)
      return null;
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    pw.flush();
    pw.close();
    sw.flush();
    return sw.toString();
  }
}

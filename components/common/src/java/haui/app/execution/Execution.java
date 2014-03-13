/* *****************************************************************
 * Project: common
 * File:    Execution.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.core.Application;
import haui.app.dao.db.AbstractDataSource;
import haui.app.dao.execution.ExecutionDAO;
import haui.app.dao.execution.ExecutionLogDAO;
import haui.app.dao.partner.UserDAO;
import haui.app.service.command.ServiceContext;
import haui.app.service.command.ServiceValidationContext;
import haui.app.service.lock.Locker;
import haui.app.threading.ManagedRunnable;
import haui.app.threading.ParallelWorkManager;
import haui.app.threading.ThreadingComponent;
import haui.exception.AppLogicException;
import haui.exception.AppSystemException;
import haui.model.execution.ExecutionIdentifier;
import haui.model.execution.ExecutionInfoDTO;
import haui.model.execution.ExecutionLogDTO;
import haui.model.partner.UserDTO;
import haui.model.util.SystemPrincipal;
import haui.util.AppUtil;
import haui.util.DateUtil;
import haui.util.GlobalApplicationContext;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neodatis.odb.impl.core.transaction.Session;

/**
 * Manages an execution of an {@link AppExecutable}. This is the {@link ManagedRunnable} that is executes an {@link Executable}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Execution implements ManagedRunnable
{
  private static final Log LOG = LogFactory.getLog(Execution.class);
  
  // threading support
  private Thread mainThread;

  // security context
  private Principal principal;
  private Set lockers;

  // validation context
  private ServiceValidationContext validationContext;

  // the service context this execution must wait for before it can start
  private ServiceContext waitForServiceContext = null;

  // job and execution identifier plus execution info
  private ExecutionIdentifier executionIdentifier = ExecutionIdentifier.next();
  private RootExecutionContext rootExecutionContext;
  private AppJobExecutionStatistics arteJobExecutionStatistics;

  private AppExecutable executable;
  private ExecutionMode executionMode;
  private boolean canceled = false;
  private boolean terminated = false;
  private Map keyValuePairs = new HashMap();
  
  protected Execution() {
  }

  /**
   * Constructs an {@link Execution} for the given {@link AppExecutable}
   * 
   * @param executable
   */
  public Execution(AppExecutable executable, ExecutionMode executionMode, Principal principal, ServiceValidationContext validationContext, Set lockers) {
      this.executable = executable;
      this.executionMode = executionMode;

      this.principal = principal;
      this.lockers = lockers;
      this.validationContext = validationContext;

      // jobs run as system
      if (executable instanceof IJob) {
          this.principal = new SystemPrincipal();
      }

      this.rootExecutionContext = new RootExecutionContext(this);
      this.arteJobExecutionStatistics = new AppJobExecutionStatistics(this);
  }

//  protected static IServerApp getServerApp() {
//      Class sa = null;
//      if(serverApp == null) {
//          try {
//              sa = ClassUtil.loadClass(
//                      PropertyUtil.getProperty(IsPcConstants.SERVER_APP_CLASS,
//                              IsPcConstants.SERVER_APP_DEFAULT_CLASS));
//
//              if(sa != null) {
//                  Class[] parameterTypes = new Class[0];
//                  Method method = sa.getMethod("getInstance", parameterTypes);
//                  
//                  serverApp = (IServerApp)method.invoke(sa, new Object[0]);
//              }
//          }
//          catch (Exception e) {
//              MessageText messageText = new MessageText(MessageTextCode.CREATE_EXCEPTION);
//              messageText.addArgument(MessageParameterCode.MESSAGE, "Failed to load class "
//                      + PropertyUtil.getProperty(IsPcConstants.SERVER_APP_CLASS,
//                      IsPcConstants.SERVER_APP_DEFAULT_CLASS));
//              throw new ArteSystemException(ExceptionCodes.CREATE_EXCEPTION, messageText, e);
//          }
//      }
//      return serverApp;
//  }

  /**
   * Initialize {@link Thread#currentThread()} so that it has a valid {@link ServiceContext}
   * 
   * @throws ArteBusinessException
   */
  protected void initializeThread(ServiceContext parentServiceContext) throws AppLogicException {
      if (executionMode == ExecutionMode.SYNCHRON && Thread.currentThread() == mainThread) return;
      if (parentServiceContext == null) {
          // there is no parent service context... this is the first
          // thread of the execution
          ServiceContext.create(getPrincipal(), getServiceURI());
      } else {
          // craete using the parent service context
          ServiceContext.create((ServiceContext) parentServiceContext);
      }
      // set the validation context
      ServiceContext.setValidationContext(validationContext);

      // load the current user
      UserDTO user = new UserDAO().getCurrentUser(getPrincipal());
      ServiceContext.setUser(user);
  }

  /**
   * Cleans up {@link Thread#currentThread()}. Closes the {@link Session} and destorys the {@link ServiceContext}
   * 
   */
  protected void cleanUpThread() {
      if (executionMode == ExecutionMode.SYNCHRON && isMainThread()) return;
      try {
          // make sure hibernate session is closed
//          DefaultDataSource.getInstance().closeSession();
          for (Iterator iter = AbstractDataSource.instance().getDataSources().iterator(); iter.hasNext();) {
              String dataSourceId = (String) iter.next();
              AbstractDataSource.closeSession(dataSourceId);
          }
      } catch (Throwable t) {
          LOG.error("Failed to close the hibernate session! " + t);
      } finally {

          try {
              // destroy the service context
              ServiceContext.destroy();
          } catch (Throwable t) {
              LOG.error("Failed to destroy the service context! " + t);
          }
      }
  }
  
  public boolean forceTermination() throws AppLogicException {
      boolean success = false;
      boolean cancelledNormally = false;
      if(mainThread.isAlive()) {
          cancel();
          
          try {
              Thread.sleep(GlobalApplicationContext.instance().getApplicationProperties().getProperty(GlobalApplicationContext.SCHEDULER_EXECUTION_TERMINATION_WAITTIME_PROPERTY,
                  GlobalApplicationContext.SCHEDULER_DEFAULT_EXECUTION_TERMINATION_WAITTIME));
              
              if(mainThread.isAlive()) {
                  mainThread.interrupt();
                  Thread.sleep(GlobalApplicationContext.instance().getApplicationProperties().getProperty(GlobalApplicationContext.SCHEDULER_EXECUTION_TERMINATION_WAITTIME_PROPERTY,
                      GlobalApplicationContext.SCHEDULER_DEFAULT_EXECUTION_TERMINATION_WAITTIME));
              }
              else {
                  cancelledNormally = true;
              }
          } catch (Exception e) {
              LOG.error("error terminating execution " + getLabel(), e);
          }
          if(mainThread.isAlive()) {
              mainThread.stop();
              
              try {
                  Thread.sleep(GlobalApplicationContext.instance().getApplicationProperties().getProperty(GlobalApplicationContext.SCHEDULER_EXECUTION_TERMINATION_WAITTIME_PROPERTY,
                      GlobalApplicationContext.SCHEDULER_DEFAULT_EXECUTION_TERMINATION_WAITTIME));
              } catch (InterruptedException e) {
                  LOG.error("error terminating execution " + getLabel(), e);
              }
              if(mainThread.isAlive()) {
                  throw new AppSystemException("Could not terminate execution " + getLabel());
              }
          }
      }
      if(!mainThread.isAlive()) {
          success = true;
          LOG.warn(getLabel() + " terminated!");
//          if(!cancelledNormally)
//              cleanUpAfterForcedTermination();
      }
      return success;
  }

  /**
   * Spawns a the execution on a new {@link Thread} using the underlying {@link ThreadingComponent}
   */
  public void start(boolean waitForParentServiceTermination) throws AppLogicException {
      if (waitForParentServiceTermination) waitForServiceContext = ServiceContext.getServiceContext();
      Application.instance().getThreadingComponent().fireAndForget(this);
  }

  /**
   * Waits until the current {@link ServiceContext} has terminated
   * 
   * @throws ArteBusinessException
   */
  protected void waitForParentServiceTermination() throws AppLogicException {
      if (waitForServiceContext == null) return;

      // for the moment use a busy wait (poll for the termination of the service)
      while (ServiceContext.isAlive(waitForServiceContext)) {
          try {
              if (isCanceled()) return;
              Thread.sleep(1000);
              // LOG.debug("Eexcution " + this + " waiting for termination of " + waitForServiceContext);
          } catch (InterruptedException iex) {
              throw new AppLogicException("Interrupted while waiting for parent service to terminate", iex);
          }
      }

      waitForServiceContext = null;
  }

  public void cancel() {
      if (!terminated) {
          canceled = true;
          rootExecutionContext.executionCanceled();
      }
  }

  public void run() {
      mainThread = Thread.currentThread();
      logExecutionProgress();
      rootExecutionContext.executionStarted();
      try {
          waitForParentServiceTermination();
          initializeThread(null);
          execute(rootExecutionContext, executable);
          rootExecutionContext.executionSucessful();

      } catch (Throwable t) {
          rootExecutionContext.executionFailed(t);
      } finally {
          cleanUp();
      }
  }

  /**
   * Cleans up after the {@link Execution} has terminated
   */
  protected void cleanUpAfterForcedTermination() {
      rootExecutionContext.executionTerminated();
      logExecutionProgress();
      getRootLog().executionTerminated();
      persistExecutionInfo();
      ExecutionManager.instance().executionDone(Execution.this);
  }

  /**
   * Cleans up after the {@link Execution} has terminated
   */
  protected void cleanUp() {
      rootExecutionContext.executionTerminated();
      logExecutionProgress();
      getRootLog().executionTerminated();
      persistExecutionInfo();
      cleanUpThread();
      ExecutionManager.instance().executionDone(Execution.this);
  }

  /**
   * Persists the {@link ExecutionInfoDTO} representing this {@link Execution} to the DB.
   */
  protected void persistExecutionInfo() {
      ExecutionInfoDTO info = getExecutionInfo();

      StoreExecutionInfoUOW storeUow = new StoreExecutionInfoUOW(info);
      StoreExecutionLogUOW storeLogUow = new StoreExecutionLogUOW();
      try {
          execute(new NoopExecutionContext(), storeUow);
          // create Log File (ExecutionLogDTO)
          execute(new NoopExecutionContext(), storeLogUow);
      } catch (Throwable t) {
          LOG.warn("error persisting execution info or log");
      }
  }

  public String getLabel() {
      return executionIdentifier.getShortDescription();
  }

  /*
   * Package visible helper methods
   */
  /**
   * Checks if the given key is locked by this {@link Execution}
   */
  protected boolean locks(String mutualExclusionKey) {
      if (executable instanceof IJob) {
          IJob job = (IJob) executable;
          String[] mutualExclusionKeys = job.getMutualExclusionKeys();
          if (mutualExclusionKeys != null) {
              for (int i = 0; i < mutualExclusionKeys.length; i++) {
                  if (AppUtil.equals(mutualExclusionKey, mutualExclusionKeys[i])) {
                      return true;
                  }
              }
          }
      }
      return false;
  }

  /**
   * Executes the given {@link AppExecutable} within this Execution. This method is called {@link #executable} trough the {@link ExecutionContext} in order to execute nested {@link AppExecutable}s
   * 
   * @param parentContext
   * @param executable
   * @param progressContribution
   */
  protected void execute(AbstractExecutionContext parentContext, AppExecutable executable, double progressContribution) {

    assert parentContext != null : "parentContext is null";
    assert executable != null : "executable is null";

      // check unit of work does not execute parallel task
      if (parentContext.getExecutable() instanceof AppUnitOfWork && executable instanceof AppParallelTask) {
          throw new AppSystemException("cannot execute a parallel task from a unit of work");
      }

      // create the child context
      ExecutionContext context = new NestedExecutionContext(this, executable, parentContext, progressContribution);

      execute(context, executable);
  }

  /**
   * Executes the given {@link AppExecutable} with the given {@link ExecutionContext}
   * 
   * @param context
   * @param executable
   */
  protected void execute(ExecutionContext context, AppExecutable executable) {
      // cancel exit point
      if (context.isCanceled()) return;

      // create the executor
      Executor executor = ExecutorFactory.instance().createExecutor(executable);

      // initialize the executor
      executor.init(context);

      try {
          executable.init(context);

          // cancel exit point
          if (context.isCanceled()) return;

          executor.execute(executable);
          AbstractDataSource.instance().getSession().clear();

      } catch (Throwable t) {
          String errorMSG = "Execution Failed... Executable: " + executable + " Executable_id: " + executable.getIdentifier();
          LOG.error(errorMSG, t);
          throw new AppLogicException(errorMSG, t);
      } finally {
          // make sure the pm is done...
          context.getProgressMonitor().done();
      }
  }

  /*
   * Package visible accessors
   * 
   */
  /**
   * @return the {@link Principal} this {@link Execution} is running with (is propagated to all {@link Thread}s spawned)
   */
  protected Principal getPrincipal() {
      return principal;
  }

  /**
   * 
   * @return the {@link Locker}s owned by this Execution (is propagated to all {@link Thread}s spawned)
   */
  protected Set getLockers() {
      return lockers;
  }

  /**
   * 
   * @return the service uri (is set on all {@link ServiceContext} created (one for every thread spawned)
   */
  protected String getServiceURI() {
      return "JobExecution/" + executable.getIdentifier();
  }

  /**
   * 
   * @return the {@link ExecutionIdentifier} representing this execution
   */
  protected ExecutionIdentifier getExecutionIdentifier() {
      return executionIdentifier;
  }

  /**
   * 
   * @return <code>true</code> if this {@link Execution} has been canceled, <code>false</code> otherwise
   */
  protected boolean isCanceled() {
      return canceled;
  }

  /**
   * 
   * @return the {@link AppExecutable} that is executed
   */
  public AppExecutable getExecutable() {
      return executable;
  }

  /**
   * 
   * @return the {@link ExecutionInfoDTO}
   */
  protected ExecutionInfoDTO getExecutionInfo() {
      return rootExecutionContext.getExecutionInfo();
  }

  /**
   * 
   * @return the {@link RootExecutionContext} of this {@link Execution}
   */
  protected RootExecutionContext getRootExecutionContext() {
      return rootExecutionContext;
  }

  /**
   * 
   * @return the {@link RootExecutionLog} of this {@link Execution}
   */
  protected RootExecutionLog getRootLog() {
      return getRootExecutionContext().getRootLog();
  }

  /**
   * 
   * @return the {@link ArteJobExecutionStatistics} of this {@link Execution}
   */
  protected AppJobExecutionStatistics getAppJobExecutionStatistics() {
      return arteJobExecutionStatistics;
  }

  /**
   * 
   * @return the {@link ParallelWorkManager} to be used within this {@link Execution}
   */
  protected synchronized ParallelWorkManager createParallelWorkManager() {
      return Application.instance().getThreadingComponent().createParallelWorkManager("Execution " + getExecutionIdentifier());
  }

  protected boolean isMainThread() {
      return Thread.currentThread().equals(mainThread);
  }

  /**
   * A {@link AppUnitOfWork} that stores an {@link ExecutionInfoDTO} to the db. This UOW stores the {@link ExecutionInfoDTO} representing this {@link Execution} to the DB when the {@link Execution}
   * has terminated.
   * 
   * @author <a href="mailto:mirko.tschaeni@ubs.com">Mirko Tschäni (t290861)</a> $LastChangedRevision$
   * @since 1.0
   */
  protected class StoreExecutionInfoUOW extends AppUnitOfWork {
      private ExecutionInfoDTO executionInfo;

      StoreExecutionInfoUOW(ExecutionInfoDTO executionInfo) {
          this.executionInfo = executionInfo;
      }

      public void run(ExecutionContext context) {
          AbstractDataSource.instance().getSession().clear();
          UserDTO user = (UserDTO) new UserDAO().findByStid(UserDTO.class, executionInfo.getStartUser().getStid());
          executionInfo.setStartUser(user);
          new ExecutionDAO().save(executionInfo);
      }

      public String getIdentifier() {
          return "store execution to db";
      }
  }

  protected class StoreExecutionLogUOW extends AppUnitOfWork {
      StoreExecutionLogUOW() {}

      public void run(ExecutionContext context) {
          ExecutionLogDTO executionLogFile = rootExecutionContext.createLogFile();
          if (executionLogFile != null) {
              new ExecutionLogDAO().save(executionLogFile);
          }
      }

      public String getIdentifier() {
          return "store execution log file to db";
      }
  }

  public String toString() {
      StringBuffer buffer = new StringBuffer();

      buffer.append(getExecutable() + " " + getExecutionIdentifier());

      return buffer.toString();
  }

  /*
   * Simple output to the default arte.log...
   * 
   * 
   * 
   */
  // how long to wait in a minimum between two log outputs
  private static final long MIN_ELAPSED_FOR_LOG_OUTPUT = 1000 * 30; // 30 secs
  // time of the last output
  private long lastOutput = 0;

  /**
   * {@link #rootExecutionContext}, {@link #rootExecutionStatistics} call this method whenever they got called from an {@link AppExecutable}. Triggers intermediate log outputs at regular
   * intervalls.
   * 
   */
  protected void touched() {
      Date now = DateUtil.getCurrentTimeStamp();
      long elapsed = now.getTime() - lastOutput;
      if (elapsed >= MIN_ELAPSED_FOR_LOG_OUTPUT) {
          logExecutionProgress();
      }
  }

  /**
   * Logs the status of this {@link Execution} to the default log
   * 
   */
  protected void logExecutionProgress() {
      if (LOG.isDebugEnabled()) {
          lastOutput = DateUtil.getCurrentTimeStamp().getTime();

          ExecutionInfoDTO info = getExecutionInfo();
          String executableId = info.getExecutableId();
          String executionId = info.getExecutionIdentifier().getId();
          String executionState = info.getExecutionStatusCode().getCode();
          Date dateStarted = info.getDateStarted();
          Date dateTerminated = info.getDateTerminated();
          Date dateScheduled = info.getDateScheduled();
          boolean canceled = info.isCanceled();
          double progress = info.getProgress();

          // time
          String startTimestampFormated = "";
          String endTimestampFormated = "";
          String dateScheduledFormated = "";
          if (dateStarted != null) startTimestampFormated = DateUtil.format(dateStarted, true);
          if (dateTerminated != null) endTimestampFormated = DateUtil.format(dateTerminated, true);
          if (dateScheduledFormated != null) dateScheduledFormated = DateUtil.format(dateScheduled, true);

          // progress
          String progressMessage = "unknown";

          if (progress > 0) {
              progressMessage = new BigDecimal(progress * 100).setScale(0, BigDecimal.ROUND_HALF_DOWN) + " %";
          }

          List lines = new ArrayList();

          lines.add(new MessageLine("State", executionState));
          if (info.isTerminated()) {
              lines.add(new MessageLine("Termination", info.getTerminationCode()));
          }

          lines.add(new MessageLine("Scheduled", dateScheduledFormated));
          lines.add(new MessageLine("Started", startTimestampFormated));
          lines.add(new MessageLine("Finished", endTimestampFormated));
          if (dateStarted != null) {
              Date end = dateTerminated;
              if (end == null) end = DateUtil.getCurrentTimeStamp();
              long elapsed = end.getTime() - dateStarted.getTime();
              lines.add(new MessageLine("Elapsed", DateUtil.formatElapsedTime(elapsed)));
          }
          lines.add(new MessageLine("Canceled", new Boolean(canceled)));
          lines.add(new MessageLine("Progress", progressMessage));
          lines.add(new MessageLine("Running As", getPrincipal()));

          String[] kounterKeys = getAppJobExecutionStatistics().getCounterNames();
          if (kounterKeys.length > 0) lines.add(new MessageLine("", ""));
          for (int i = 0; i < kounterKeys.length; i++) {
              String counter = kounterKeys[i];
              Integer count = getAppJobExecutionStatistics().getCount(counter);

              lines.add(new MessageLine(counter, count));
          }

          StringBuffer output = new StringBuffer();
          String header = " Execution '" + executableId + "' id '" + executionId + "'";
          if (header.length() % 2 != 0) header = header + " ";
          while (header.length() < 90)
              header = "-" + header + "-";
          output.append("\n" + header + "\n");
          Iterator lineIterator = lines.iterator();
          while (lineIterator.hasNext()) {
              MessageLine line = (MessageLine) lineIterator.next();
              String key = String.valueOf(line.key);
              String value = String.valueOf(line.value);

              while (key.length() < 30)
                  key = key + " ";

              output.append(key + " " + value + "\n");
          }
          output.append("------------------------------------------------------------------------------------------");

          LOG.debug(output);
      }
  }

  /**
   * Helper class that represents a line in the log output
   * 
   * @author <a href="mailto:mirko.tschaeni@ubs.com">Mirko Tschäni (t290861)</a> $LastChangedRevision$
   * @since 1.0
   */
  protected class MessageLine {
      Object key;
      Object value;

      public MessageLine(Object key, Object value) {
          this.key = key;
          this.value = value;
      }
  }

  public boolean put(String key, Object value) {
      keyValuePairs.put(key, value);
      return true;
  }

  public Object get(String key) {
      return keyValuePairs.get(key);
  }

  public Map getKeyValuePairs() {
      return keyValuePairs;
  }
}

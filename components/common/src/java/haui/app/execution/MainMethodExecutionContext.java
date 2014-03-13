/* *****************************************************************
 * Project: common
 * File:    MainMethodExecutionContext.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.service.command.ServiceContext;
import haui.app.threading.ParallelWorkManager;

import java.util.HashMap;
import java.util.Map;

import haui.app.security.ICurrentUserMapper;
import haui.app.util.SimpleStatisticCounter;
import haui.common.id.STIDGenerator;
import haui.exception.AppLogicException;
import haui.exception.AppSystemException;
import haui.model.execution.ExecutionIdentifier;
import haui.model.execution.ExecutionInfoDTO;
import haui.model.partner.UserDTO;
import haui.util.ClassUtil;
import haui.util.DateUtil;
import haui.util.GlobalApplicationContext;

/**
 * MainMethodExecutionContext
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class MainMethodExecutionContext implements ExecutionContext
{

  // private static Log LOG = LogFactory.getLog(MainMethodExecutionContext.class);

  private ProgressMonitor progressMonitor;
  private ExecutionInfoDTO executionInfo;
  private ExecutionStatistics statistics;
  private Map keyValuePairs = new HashMap();

  private static ICurrentUserMapper currentUserMapper = null;

  /**
   * constructor
   */
  public MainMethodExecutionContext(String id)
  {
    super();
    UserDTO currentUser = getCurrentUserMapper().getCurrentUser(ServiceContext.getPrincipal());
    this.executionInfo = new ExecutionInfoDTO(MainMethodExecutionContext.class.getName(), new ExecutionIdentifier(id), currentUser);
    this.executionInfo.setDateScheduled(DateUtil.getCurrentTimeStamp());

    this.progressMonitor = new MainMethodProgressMonitor();
    this.statistics = new MainMethodExecutionStatistics();
  }

  public void cleanUpThread()
  {
  }

  public ParallelWorkManager createParallelWorkManager()
  {
    return null;
  }

  public void execute(AppExecutable executable, double progressContribution)
  {
    throw new UnsupportedOperationException("MainMethodExecutionContext cannot execute Executables");
  }

  public void fatale(Throwable t)
  {
  }

  public String getContextId()
  {
    return STIDGenerator.generate();
  }

  public ExecutionLog getLog()
  {
    return new ExecutionLog()
    {
      public void error(String stid, Object message, Throwable t)
      {
        LOG.error(stid + ", " + message, t);
      }

      public void error(Object message, Throwable t)
      {
        LOG.error(message, t);
      }

      public void debug(String stid, Object message)
      {
        LOG.debug(stid + ", " + message);
      }

      public void debug(Object message)
      {
        LOG.debug(message);
      }

      public void warn(String stid, Object message)
      {
        LOG.warn(stid + ", " + message);
      }

      public void warn(Object message)
      {
        LOG.warn(message);
      }

      public void info(String stid, Object message)
      {
        LOG.info(stid + ", " + message);
      }

      public void info(Object message)
      {
        LOG.info(message);
      }
    };
  }

  public ProgressMonitor getProgressMonitor()
  {
    return progressMonitor;
  }

  public ExecutionStatistics getStatistics()
  {
    return statistics;
  }

  public boolean hasPendingTranaction()
  {
    return false;
  }

  public void initializeThread(ServiceContext parentServiceContext) throws AppLogicException
  {
  }

  public boolean isCanceled()
  {
    return false;
  }

  /**
   * @param principal
   * @return
   */
  protected static ICurrentUserMapper getCurrentUserMapper()
  {
    if(currentUserMapper == null)
    {
      String className = GlobalApplicationContext.instance().getApplicationProperties().getProperty(
          GlobalApplicationContext.CURRENT_USER_MAPPER_CLASS, GlobalApplicationContext.CURRENT_USER_MAPPER_CLASS_DEFAULT_CLASS);
      try
      {
        setCurrentUserMapper((ICurrentUserMapper)ClassUtil.newInstance(className));
      }
      catch(Exception e)
      {
        throw new AppSystemException("Failed to create ICurrentUserMapper class." + className, e);
      }

    }
    return currentUserMapper;
  }

  protected static void setCurrentUserMapper(ICurrentUserMapper mapper)
  {
    currentUserMapper = mapper;
  }

  private class MainMethodProgressMonitor implements ProgressMonitor
  {

    public void done()
    {
      executionInfo.setProgress(1);
    }

    public void worked(double work)
    {
      double progress = executionInfo.getProgress();
      if(progress < 0)
        progress = 0;

      progress += work;

      if(progress < 0)
      {
        progress = 0;
      }
      if(progress > 1)
      {
        progress = 1;
      }

      executionInfo.setProgress(progress);
    }
  }

  public class MainMethodExecutionStatistics extends SimpleStatisticCounter implements ExecutionStatistics
  {

    Execution getExecution()
    {
      return null;
    }

    public synchronized int increase(String subStatistic, String counter, int amount)
    {
      return super.increase(counter, amount);
    }
  }

  public boolean put(String key, Object value)
  {
    keyValuePairs.put(key, value);
    return true;
  }

  public Object get(String key)
  {
    return keyValuePairs.get(key);
  }

  public Map getKeyValuePairs()
  {
    return keyValuePairs;
  }
}

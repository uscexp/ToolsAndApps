/* *****************************************************************
 * Project: common
 * File:    NoopExecutionContext.java
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

import haui.common.id.STIDGenerator;

/**
 * NoopExecutionContext
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class NoopExecutionContext implements ExecutionContext
{
  private Map keyValuePairs = new HashMap();

  public void execute(AppExecutable executable, double progressContribution)
  {
    throw new UnsupportedOperationException("NoopExecutionContext cannot execute Executables");
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
      }

      public void error(Object message, Throwable t)
      {
      }

      public void debug(String stid, Object message)
      {
      }

      public void debug(Object message)
      {
      }

      public void warn(String stid, Object message)
      {
      }

      public void warn(Object message)
      {
      }

      public void info(String stid, Object message)
      {
      }

      public void info(Object message)
      {
      }
    };
  }

  public ParallelWorkManager createParallelWorkManager()
  {
    return null;
  }

  public ProgressMonitor getProgressMonitor()
  {
    return new ProgressMonitor()
    {
      public void done()
      {
      }

      public void worked(double work)
      {
      }
    };
  }

  public ExecutionStatistics getStatistics()
  {
    return new ExecutionStatistics()
    {
      public int increase(String counter, int amount)
      {
        return 0;
      }

      public int increase(String counter)
      {
        return 0;
      }

      public void remove(String counter)
      {

      }

      public int touch(String counter)
      {
        return 0;
      }

      public String getStatistic(String key)
      {
        return null;
      }

      public void setStatistic(String key, String value)
      {
      }

      public int increase(String subStatistic, String counter, int amount)
      {
        return 0;
      }

      public int increase(String subStatistic, String counter)
      {
        return 0;
      }

      public int touch(String subStatistic, String counter)
      {
        return 0;
      }

      public String getStatistic(String subStatistic, String key)
      {
        return null;
      }

      public void setStatistic(String subStatistic, String key, String value)
      {
      }
    };
  }

  public boolean hasPendingTranaction()
  {
    return false;
  }

  public void initializeThread(ServiceContext parentServiceContext)
  {
  }

  public void cleanUpThread()
  {
  }

  public boolean isCanceled()
  {
    return false;
  }

  public void fatale(Throwable t)
  {
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

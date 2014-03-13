/* *****************************************************************
 * Project: common
 * File:    ParallelTaskExecutor.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import haui.app.service.command.ServiceContext;
import haui.app.threading.ManagedRunnable;
import haui.common.id.STIDGenerator;
import haui.exception.AppLogicException;
import haui.util.GlobalApplicationContext;

/**
 * {@link Executor} for {@link AppParallelTask}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
class ParallelTaskExecutor implements Executor
{
  private static final Log LOG = LogFactory.getLog(ParallelTaskExecutor.class);
  private static final String LOG_PREFIX = "ParallelTaskExecutor: ";

  private static final String PROPERTY_KEY_WORKER_THREAD_COUNT = "app.execution.parallel.threadsPerParallelTask";
  private static final String PROPERTY_KEY_AUTO_BATCH_DURATION = "app.execution.parallel.autoTargetBatchExecutionTime";
  private static final int DEFAULT_WORKER_THREAD_COUNT = 10;
  private static final int FALLBACK_BATCH_SIZE = 1;

  private ServiceContext parentServiceContext;
  private ExecutionContext context;
  private AppParallelTask parallelTask;
  private String internalLabel;
  private int totalWorkItems;
  private List remainingWork;
  private int runningWorkers = 0;
  private BatchExecutionStatistics latestBatchExecutionStatistics;

  public ParallelTaskExecutor()
  {
  }

  private static int getWorkerThreadCount()
  {
    return GlobalApplicationContext.instance().getApplicationProperties().getProperty(PROPERTY_KEY_WORKER_THREAD_COUNT, DEFAULT_WORKER_THREAD_COUNT);
  }

  private static int getAutoTargetBatchExecutionTime()
  {
    return GlobalApplicationContext.instance().getApplicationProperties().getProperty(PROPERTY_KEY_AUTO_BATCH_DURATION, -1);
  }

  public void init(ExecutionContext context)
  {
    this.context = context;
    // remember the service context of this thread
    this.parentServiceContext = ServiceContext.getServiceContext();
  }

  private ExecutionContext getContext()
  {
    return context;
  }

  private int batchSize()
  {
    int batchSize = FALLBACK_BATCH_SIZE;
    long autoTargetBatchExecutionTime = getAutoTargetBatchExecutionTime() * 1000;
    boolean auto = autoTargetBatchExecutionTime > 0;

    if(auto)
    {
      if(latestBatchExecutionStatistics != null)
      {
        // calculate the next batch size
        latestBatchExecutionStatistics.getAverageBatchExecutionTime();
        long averageExecutionTime = latestBatchExecutionStatistics.getAverageBatchExecutionTime();
        if(averageExecutionTime == 0)
          averageExecutionTime = 1;
        batchSize = (int)(autoTargetBatchExecutionTime / averageExecutionTime);
      }
      else if(parallelTask.getBatchSizeHint() > 0)
      {
        batchSize = parallelTask.getBatchSizeHint();
      }
    }
    else
    {
      if(parallelTask.getBatchSizeHint() > 0)
      {
        batchSize = parallelTask.getBatchSizeHint();
      }
    }

    if(batchSize < 1)
    {
      batchSize = 1;
    }

    return batchSize;
  }

  private synchronized AppExecutable[] nextBatch()
  {
    if(getContext().isCanceled())
      return new AppRunnable[0];

    Set batch = new HashSet();
    Iterator it = remainingWork.iterator();
    int batchSize = batchSize();
    while(it.hasNext() && batch.size() < batchSize)
    {
      batch.add(it.next());
      it.remove();
    }

    return (AppExecutable[])batch.toArray(new AppExecutable[batch.size()]);
  }

  public void execute(AppExecutable executable) throws AppLogicException
  {
    if(getContext().isCanceled())
      return;

    parallelTask = (AppParallelTask)executable;
    this.internalLabel = parallelTask.getIdentifier();
    if(this.internalLabel == null)
      this.internalLabel = STIDGenerator.generate();

    this.remainingWork = new ArrayList(Arrays.asList(parallelTask.getRunnables()));
    this.totalWorkItems = this.remainingWork.size();

    // start the workers...
    Worker[] workers = new Worker[getWorkerThreadCount()];
    for(int i = 0; i < workers.length; i++)
    {
      workers[i] = new Worker();
    }
    getContext().createParallelWorkManager().run(workers);
  }

  /**
   * Worker thread {@link ManagedRunnable}
   * 
   * @author <a href="mailto:mirko.tschaeni@ubs.com">Mirko Tschäni (t290861)</a>
   *         $LastChangedRevision$
   * @since 1.0
   */
  private class Worker implements ManagedRunnable
  {
    private String uniqueId = STIDGenerator.generate();

    private Worker()
    {
    }

    public void cancel()
    {
    }

    public String getLabel()
    {
      return uniqueId;
    }

    public void run()
    {
      runningWorkers++;
      try
      {
        getContext().initializeThread(parentServiceContext);

        AppExecutable[] executables;
        while((executables = nextBatch()).length > 0)
        {
          long batchStartedTime = System.currentTimeMillis();
          int batchSize = executables.length;

          for(int i = 0; i < executables.length; i++)
          {
            AppExecutable executable = executables[i];

            try
            {
              getContext().execute(executable, 1d / totalWorkItems);
            }
            catch(Throwable t)
            {
              LOG.error(LOG_PREFIX + "work failed... executable: " + executable, t);
            }
          }

          reportBatchExecutionStatistics(batchSize, batchStartedTime);
        }
      }
      catch(Throwable t)
      {
        LOG.error(LOG_PREFIX + "Worker '" + getLabel() + "' Error:", t);
      }
      finally
      {
        getContext().cleanUpThread();
      }
      runningWorkers--;
    }

    private void reportBatchExecutionStatistics(int batchSize, long startTime)
    {
      long executionTime = System.currentTimeMillis() - startTime;
      BatchExecutionStatistics statistics = new BatchExecutionStatistics(batchSize, executionTime);
      if(statistics.isValidSample())
      {
        latestBatchExecutionStatistics = statistics;
      }
    }
  }

  /**
   * Helper class maintaining batch execution time statistics... For dynamic calculation of batch
   * size.
   * 
   * @author <a href="mailto:mirko.tschaeni@ubs.com">Mirko Tschäni (t290861)</a>
   *         $LastChangedRevision$
   * @since 1.0
   */
  private class BatchExecutionStatistics
  {
    private int batchSize;
    private long executionTime;

    private BatchExecutionStatistics(int batchSize, long executionTime)
    {
      this.batchSize = batchSize;
      this.executionTime = executionTime;
    }

    int getBatchSize()
    {
      return batchSize;
    }

    long getExecutionTime()
    {
      return executionTime;
    }

    private long getAverageBatchExecutionTime()
    {
      if(batchSize > 0 && executionTime > 0)
      {
        return executionTime / batchSize;
      }
      return -1;
    }

    private boolean isValidSample()
    {
      return getAverageBatchExecutionTime() >= 0;
    }

    public String toString()
    {
      return "batchSize: '" + batchSize + " executionTime: " + executionTime + " averageExecutionTime: " + getAverageBatchExecutionTime() + "'";
    }
  }
}

/* *****************************************************************
 * Project: common
 * File:    PlainParallelWorkManagerImpl.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl.plain;

import haui.app.threading.ManagedRunnable;
import haui.app.threading.ParallelWorkManager;
import haui.app.threading.impl.AbstractRunnableManagerImpl;
import haui.app.threading.impl.Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PlainParallelWorkManagerImpl
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class PlainParallelWorkManagerImpl extends AbstractRunnableManagerImpl implements ParallelWorkManager
{

  // ==============================================================
  // Class Declarations
  // ==============================================================

  protected static final Log LOG = LogFactory.getLog(PlainParallelWorkManagerImpl.class);

  // ==============================================================
  // Class Methods
  // ==============================================================

  protected static boolean syncThreads(Thread[] threads, int waitTime) throws InterruptedException
  {
    long sysTime = System.currentTimeMillis();
    long endTime = sysTime + waitTime;
    boolean inTime = true;

    for(int i = 0; inTime && i < threads.length; i++)
    {

      long actualWaitTime = endTime - sysTime;

      if(actualWaitTime > 0)
      {

        threads[i].join(actualWaitTime);

        sysTime = System.currentTimeMillis();
        inTime = sysTime < endTime;

      }
      else
      {
        inTime = false;
      }
    }

    return inTime && !threads[threads.length - 1].isAlive();
  }

  // ==============================================================
  // Instance Declarations
  // ==============================================================

  // ==============================================================
  // Constructors
  // ==============================================================

  /**
   *
   */
  public PlainParallelWorkManagerImpl(Registry registry)
  {
    this(DEFAULT_NAME, registry);
  }

  /**
   *
   */
  public PlainParallelWorkManagerImpl(String label, Registry registry)
  {
    super(label, registry);
  }

  // ==============================================================
  // Instance Methods
  // ==============================================================

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.impl.ParallelWorkManager#run(java.lang.ManagedRunnable[])
   */
  public void run(ManagedRunnable[] workUnits)
  {
    LOG.debug("run() - starting ...");

    Thread[] threads = startWorkUnits(workUnits);

    try
    {
      for(int i = 0; i < threads.length; i++)
      {
        threads[i].join();
      }
    }
    catch(InterruptedException e)
    {
      cancel();
    }

    LOG.debug("run() - finished");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.eval.worker.ParallelWorkManager#processJoinableWork(java.util.List,
   * long)
   */
  public boolean run(ManagedRunnable[] workUnits, int waitTime)
  {

    boolean ok;

    LOG.debug("run() - starting ...");

    if(waitTime <= 0)
      throw new IllegalArgumentException("Invalid wait time: " + waitTime);

    WorkerThread[] threads = startWorkUnits(workUnits);

    try
    {
      ok = syncThreads(threads, waitTime);
    }
    catch(InterruptedException e)
    {
      cancel();
      ok = false;
    }

    if(LOG.isDebugEnabled())
    {
      LOG.debug("run() - finished ok=" + ok);
    }

    if(!ok)
    {
      for(int i = 0; i < threads.length; i++)
      {
        threads[i].cancel();
      }
    }

    return ok;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.eval.worker.ParallelWorkManager#processJoinableWork(java.util.List,
   * long)
   */
  private WorkerThread[] startWorkUnits(ManagedRunnable[] workUnits)
  {
    boolean ok = true;

    LOG.debug("startWorkUnits() - starting ...");

    WorkerThread[] threads = new WorkerThread[workUnits.length];

    for(int i = 0; i < workUnits.length; i++)
    {

      PlainRunnableWrapper prw = new PlainRunnableWrapper(workUnits[i], this);

      WorkerThread workerThread = new WorkerThread(prw);
      workerThread.start();

      threads[i] = workerThread;
    }

    if(LOG.isDebugEnabled())
    {
      LOG.debug("startWorkUnits() - finished ok=" + ok);
    }

    return threads;
  }

  // ==============================================================
  // Inner Classes
  // ==============================================================

  private static class WorkerThread extends Thread
  {

    private PlainRunnableWrapper prw;

    public WorkerThread(PlainRunnableWrapper prw)
    {
      super(prw, prw.getInternalLabel());
      this.prw = prw;
    }

    public void cancel()
    {
      if(!(prw.isStopped() || prw.isCancelled()))
      {
        prw.cancel();
      }
    }
  }
}

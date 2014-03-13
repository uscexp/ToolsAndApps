/* *****************************************************************
 * Project: common
 * File:    PlainDemonManagerImpl.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl.plain;

import haui.app.threading.DemonManager;
import haui.app.threading.ManagedRunnable;
import haui.app.threading.impl.AbstractRunnableManagerImpl;
import haui.app.threading.impl.AbstractRunnableWrapper;
import haui.app.threading.impl.Registry;

import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PlainDemonManagerImpl
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class PlainDemonManagerImpl extends AbstractRunnableManagerImpl implements DemonManager
{

  // ==============================================================
  // Class Declarations
  // ==============================================================

  protected static final Log LOG = LogFactory.getLog(PlainDemonManagerImpl.class);

  // ==============================================================
  // Class Methods
  // ==============================================================

  // ==============================================================
  // Instance Declarations
  // ==============================================================

  // ==============================================================
  // Constructors
  // ==============================================================

  /**
   *
   */
  public PlainDemonManagerImpl(Registry registry)
  {
    this(DEFAULT_NAME, registry);
  }

  /**
  *
  */
  public PlainDemonManagerImpl(String label, Registry registry)
  {
    super(label, registry);
  }

  // ==============================================================
  // Instance Methods
  // ==============================================================

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.eval.worker.DemonManager#start(java.util.List, long)
   */
  public void schedule(ManagedRunnable task, long delay)
  {
    Timer timer = new Timer(true);
    timer.schedule(new WorkTimerTask(task, timer), delay);

    if(LOG.isDebugEnabled())
    {
      LOG.debug("schedule() - task label=" + task.getLabel() + " has been scheduled");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.eval.worker.DemonManager#start(java.util.List, long, long)
   */
  public void schedule(ManagedRunnable task, long delay, long period)
  {
    Timer timer = new Timer(true);
    timer.schedule(new WorkTimerTask(task, timer), delay, period);

    if(LOG.isDebugEnabled())
    {
      LOG.debug("schedule() - task label=" + task.getLabel() + " has been scheduled");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.DemonManager#scheduleAtFixedRate(com.ubs.istoolset.framework
   * .threading.ManagedRunnable, long, long)
   */
  public void scheduleAtFixedRate(ManagedRunnable task, long delay, long period)
  {
    Timer timer = new Timer(true);
    timer.scheduleAtFixedRate(new WorkTimerTask(task, timer), delay, period);

    if(LOG.isDebugEnabled())
    {
      LOG.debug("scheduleAtFixedRate() - task label=" + task.getLabel() + " has been scheduled");
    }
  }

  // ==============================================================
  // Inner Classes
  // ==============================================================

  private final class WorkTimerTask extends java.util.TimerTask
  {

    private AbstractRunnableWrapper runnableWrapper;

    private Timer timer;

    public WorkTimerTask(ManagedRunnable task, Timer timer)
    {
      this.runnableWrapper = new PlainRunnableWrapper(task, PlainDemonManagerImpl.this);
      this.timer = timer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    public void run()
    {
      runnableWrapper.run();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#cancel()
     */
    public boolean cancel()
    {

      timer.cancel();

      boolean cancelable = super.cancel();

      if(cancelable)
      {
        runnableWrapper.cancel();
      }

      return cancelable;
    }
  }
}

/* *****************************************************************
 * Project: common
 * File:    ThreadingComponentManagement.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading;

import haui.app.threading.impl.AbstractThreadingComponentImpl;

/**
 * ThreadingComponentManagement
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ThreadingComponentManagement implements ThreadingComponentManagementMBean
{

  private AbstractThreadingComponentImpl thrComp;

  public ThreadingComponentManagement(AbstractThreadingComponentImpl thrComp)
  {
    this.thrComp = thrComp;
  }

  // ------------------------------------------
  // ThreadingComponent level
  // ------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#getOverview
   * ()
   */
  public String getOverview()
  {
    return thrComp.getOverview();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getManagerCount()
   */
  public int getManagerCount()
  {
    return thrComp.getManagerCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getRunnableCount()
   */
  public int getRunnableCount()
  {
    return thrComp.getRunnableCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#cancel
   * ()
   */
  public void cancel()
  {
    thrComp.cancel();
  }

  // ------------------------------------------
  // DemonManager level
  // ------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getDemonOverview()
   */
  public String getDemonOverview()
  {
    return thrComp.getDemonOverview();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getDemonManagerCount()
   */
  public int getDemonManagerCount()
  {
    return thrComp.getDemonManagerCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getDemonRunnableCount()
   */
  public int getDemonRunnableCount()
  {
    return thrComp.getDemonRunnableCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * cancelAllDemonRunnables()
   */
  public void cancelAllDemonRunnables()
  {
    thrComp.cancelAllDemonRunnables();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * cancelAllDemonRunnables(java.lang.String)
   */
  public void cancelAllDemonRunnables(String mangerLabel)
  {
    thrComp.cancelAllDemonRunnables(mangerLabel);
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * cancelDemonRunnable(java.lang.String, java.lang.String)
   */
  public void cancelDemonRunnable(String mangerLabel, String runnableLabel)
  {
    thrComp.cancelDemonRunnable(mangerLabel, runnableLabel);
  }

  // ------------------------------------------
  // ParallelWorkManager level
  // ------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getParallelOverview()
   */
  public String getParallelOverview()
  {
    return thrComp.getParallelOverview();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getParallelWorkManagerCount()
   */
  public int getParallelWorkManagerCount()
  {
    return thrComp.getParallelWorkManagerCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getParallelRunnableCount()
   */
  public int getParallelRunnableCount()
  {
    return thrComp.getParallelRunnableCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * cancelAllParallelWorkRunnables()
   */
  public void cancelAllParallelWorkRunnables()
  {
    thrComp.cancelAllParallelWorkRunnables();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * cancelAllParallelWorkRunnables(java.lang.String)
   */
  public void cancelAllParallelWorkRunnables(String mangerLabel)
  {
    thrComp.cancelAllParallelWorkRunnables(mangerLabel);
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * cancelParallelWorkRunnable(java.lang.String, java.lang.String)
   */
  public void cancelParallelWorkRunnable(String mangerLabel, String runnableLabel)
  {
    thrComp.cancelParallelWorkRunnable(mangerLabel, runnableLabel);
  }

  // ------------------------------------------
  // FireAndForget level
  // ------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getFireAndForgetOverview()
   */
  public String getFireAndForgetOverview()
  {
    return thrComp.getDemonOverview();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getFireAndForgetRunnableCount()
   */
  public int getFireAndForgetRunnableCount()
  {
    return thrComp.getFireAndForgetRunnableCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * cancelAllFireAndForgetRunnables()
   */
  public void cancelAllFireAndForgetRunnables()
  {
    thrComp.cancelAllFireAndForgetRunnables();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * cancelFireAndForgetRunnable(java.lang.String)
   */
  public void cancelFireAndForgetRunnable(String runnableLabel)
  {
    thrComp.cancelFireAndForgetRunnable(runnableLabel);
  }
}

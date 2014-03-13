/* *****************************************************************
 * Project: common
 * File:    ThreadingComponentManagementMBean.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading;

/**
 * ThreadingComponentManagementMBean
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ThreadingComponentManagementMBean
{
  String MBEAN_DOMAIN_NAME = "ThreadingComponent";
  String MBEAN_TYPE = "component";

  // ------------------------------------------ 
  // ThreadingComponent level
  // ------------------------------------------ 

  /**
   * Returns an overview about all rgistered RannableManagerS and their registered
   * MangedRunnableS.
   * 
   * @return
   */
  String getOverview();

  /**
   * Returns the number of all currently registered RunnableManagerS.
   * 
   * @return The number of RunnableManagerS.
   */
  int getManagerCount();

  /**
   * Returns the total number of all currently registered ManagedRunnableS.
   * 
   * @return The number of all currently registered runnables.
   */
  int getRunnableCount();

  /**
   * Cancels all currently running ManagedRunnableS
   *
   */
  void cancel();

  // ------------------------------------------ 
  // DemonManager level
  // ------------------------------------------ 

  /**
   * Returns an overview of all registered DemonMangerS and all their registered
   * ManagedRunnableS
   * 
   * @return The textual output of the overview.
   */
  String getDemonOverview();

  /**
   * Returns the number of all currently registered DemonManagerS.
   * 
   * @return The number of DemonManagerS.
   */
  int getDemonManagerCount();

  /**
   * Returns the number of all currently registered ManagedRunnableS started
   * by some DemonManager.
   * 
   * @return The number of runnables.
   */
  int getDemonRunnableCount();

  /**
   * Cancels all currently running ManagedRunnableS started by some DemonManager
   *
   */
  void cancelAllDemonRunnables();

  /**
   * Cancels all currently running ManagedRunnableS started the DemonManager with
   * the specified mangerLabel.
   * 
   * @param mangerLabel The label of the DemonManager
   */
  void cancelAllDemonRunnables(String mangerLabel);

  /**
   * Cancels the specific ManagedRunnable which has the label code>runnableLabel</code>
   * and is registered with the DemonManager whos label is <code>mangerLabel</code>.
   * 
   * @param mangerLabel The label of the DemonManager
   * @param runnableLabel The label of the ManagedRunnable which shell be cancelled.
   */
  void cancelDemonRunnable(String mangerLabel, String runnableLabel);

  // ------------------------------------------ 
  // ParallelWorkManager level
  // ------------------------------------------ 

  /**
   * Returns an overview of all registered ParallelWorkMangerS and all their registered
   * ManagedRunnableS
   * 
   * @return The textual output of the overview.
   */
  String getParallelOverview();

  /**
   * Returns the number of all currently registered ParallelWorkManagerS.
   * 
   * @return The number of DemonManagerS.
   */
  int getParallelWorkManagerCount();

  /**
   * Returns the number of all currently registered ManagedRunnableS started
   * by some ParallelWorkManager.
   * 
   * @return The number of runnables.
   */
  int getParallelRunnableCount();

  /**
   * Cancels all currently running ManagedRunnableS started by some ParallelWorkManager
   *
   */
  void cancelAllParallelWorkRunnables();

  /**
   * Cancels all currently running ManagedRunnableS started the ParallelWorkManager with
   * the specified mangerLabel.
   * 
   * @param mangerLabel The label of the ParallelWorkManager
   */
  void cancelAllParallelWorkRunnables(String mangerLabel);

  /**
   * Cancels the specific ManagedRunnable which has the label code>runnableLabel</code>
   * and is registered with the ParallelWorkManager whos label is <code>mangerLabel</code>.
   * 
   * @param mangerLabel The label of the ParallelWorkManager
   * @param runnableLabel The label of the ManagedRunnable which shell be cancelled.
   */
  void cancelParallelWorkRunnable(String mangerLabel, String runnableLabel);

  // ------------------------------------------ 
  // FireAndForgetManager level
  // ------------------------------------------ 

  /**
   * Returns an overview of all registered registered ManagedRunnableS
   * started in a fire and forget manner.
   * 
   * @return The textual output of the overview.
   */
  String getFireAndForgetOverview();

  /**
   * Returns the number of all currently registered ManagedRunnableS started
   * in a fire and forget manner.
   * 
   * @return The number of runnables.
   */
  int getFireAndForgetRunnableCount();

  /**
   * Cancels all currently running ManagedRunnableS started in a fire & forget manner
   * 
   */
  void cancelAllFireAndForgetRunnables();

  /**
   * Cancels the specific ManagedRunnable which has the label code>runnableLabel</code>
   * and has been started in a fire & forget manner.
   * 
   * @param runnableLabel The label of the ManagedRunnable which shell be cancelled.
   */
  void cancelFireAndForgetRunnable(String runnableLabel);
}

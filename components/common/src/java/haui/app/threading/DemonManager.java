/* *****************************************************************
 * Project: common
 * File:    DemonManager.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading;

import haui.app.threading.RunnableManager;

/**
 * DemonManager
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface DemonManager extends RunnableManager
{
  String DEFAULT_NAME = "DemonManager";

  /**
   * Schedules a permanently running daemon.
   *
   * @param task  daemon implementation.
   * @param delay initial delay.
   */
  void schedule(ManagedRunnable task, long delay);

  /**
   * Schedules a daemon for repeated execution.
   *
   * @param task   daemon implementation.
   * @param delay  initial delay before first execution.
   * @param period delay between executions.
   */
  void schedule(ManagedRunnable task, long delay, long period);

  /**
   * Schedules a daemon for repeated execution.
   *
   * @param task   daemon implementation.
   * @param delay  initial delay before first execution.
   * @param period delay between executions.
   */
  void scheduleAtFixedRate(ManagedRunnable task, long delay, long period);
}

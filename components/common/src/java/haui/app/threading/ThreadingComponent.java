/* *****************************************************************
 * Project: common
 * File:    ThreadingComponent.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading;

/**
 * ThreadingComponent
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ThreadingComponent
{
  /**
   * Starts the runnable immediately.
   *
   * @param runnable
   */
  void fireAndForget(ManagedRunnable runnable);

  /**
   * Creates a new ParallelWorkManager instance and registers it.
   * 
   * This ParallelWorkManager is able to execute a bunch of work units in  parallel.
   * Optionally the ParallelWorkManager is able to synchronize all started work 
   * units within a given time period.
   *
   * @param label
   * @return An instance of a ParallelWorkManager
   */
  ParallelWorkManager createParallelWorkManager(String label);

  /**
   * Creates a new DemonManager instance and registers it.
   * 
   * This DemonManager is able either to start a work unit immediately or to schedule
   * the execution of a work unit by a specified delay and optionally by a specified
   * period.
   *
   * @param label
   * @return An instance of a ParallelWorkManager
   */
  DemonManager createDemonManager(String label);
    
    /**
     * Shuts the threading component down
     *
     */
    void shutdown();
}

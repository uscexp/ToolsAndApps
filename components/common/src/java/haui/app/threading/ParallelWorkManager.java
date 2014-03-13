/* *****************************************************************
 * Project: common
 * File:    ParallelWorkManager.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading;


/**
 * ParallelWorkManager
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ParallelWorkManager extends RunnableManager
{

  String DEFAULT_NAME = "ParallelWorkManager";

  /**
   * Processes the given work units in parallel and waits for their finishing.
   *
   * @param workUnits All ManagedRunnable to process in parallel now.
   */
  void run(ManagedRunnable[] workUnits);

  /**
   * Processes the given work units in parallel and waits for the specified time
   * for finishing these work units.
   *
   * @param workUnits All ManagedRunnable to process in parallel now.
   */
  boolean run(ManagedRunnable[] workUnits, int waitTime);
}

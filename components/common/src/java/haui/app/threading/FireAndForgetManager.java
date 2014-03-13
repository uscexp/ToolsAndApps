/* *****************************************************************
 * Project: common
 * File:    FireAndForgetManager.java
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
 * FireAndForgetManager
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface FireAndForgetManager extends RunnableManager
{
  String DEFAULT_NAME = "FireAndForgetManager";

  /**
   * Processes the given work unit in parallel in a fire and forget manner.
   *
   * @param workUnits All Runnables to process in parallel now.
   */
  void run(ManagedRunnable workUnit);

  /**
   * Processes the given work units in parallel in a fire and forget manner.
   *
   * @param workUnits All Runnables to process in parallel now.
   */
  void run(ManagedRunnable[] workUnits);
}

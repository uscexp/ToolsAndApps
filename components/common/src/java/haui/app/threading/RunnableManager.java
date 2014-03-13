/* *****************************************************************
 * Project: common
 * File:    RunnableManager.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading;

/**
 * RunnableManager
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface RunnableManager
{

  /**
   * @return a short label describing the purpose of the RunnableManager.
   */
  String getLabel();

  /**
   * @return The label enhanced by an internal serial number.
   */
  String getInternalLabel();

  /**
   * Cancels all still running ManagedRunnables instance under this manager.
   */
  void cancel();
}

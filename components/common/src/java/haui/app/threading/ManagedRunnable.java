/* *****************************************************************
 * Project: common
 * File:    ManagedRunnable.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading;

/**
 * ManagedRunnable
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ManagedRunnable extends Runnable
{

  /**
   * Returns the label.
   * 
   * @return the label
   */
  String getLabel();

  /**
   * Invoking this method should cause the runnable to terminate
   * (i.e., return from the {@link ManagedRunnable#run()} method)
   * as soon as possible.
   * The implementation may not block the caller (e.g., to wait for termination).
   */
  void cancel();

}

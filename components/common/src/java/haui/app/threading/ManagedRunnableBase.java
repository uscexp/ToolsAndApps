/* *****************************************************************
 * Project: common
 * File:    ManagedRunnableBase.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading;

import java.io.Serializable;

import haui.app.threading.ManagedRunnable;

/**
 * ManagedRunnableBase
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class ManagedRunnableBase implements ManagedRunnable, Serializable
{

  static final long serialVersionUID = 1742588015217185151L;

  private String label;

  private boolean cancelled = false;

  /**
   * 
   */
  public ManagedRunnableBase(String label) {
    super();
    this.label = label;
  }

  /* (non-Javadoc)
   * @see com.ubs.istoolset.framework.threading.ManagedRunnable#getLabel()
   */
  public final String getLabel() {
    return label;
  }

  /* (non-Javadoc)
   * @see com.ubs.istoolset.framework.threading.ManagedRunnable#isCancelled()
   */
  public final boolean isCancelled() {
    return cancelled;
  }

  /* (non-Javadoc)
   * @see com.ubs.istoolset.framework.threading.ManagedRunnable#cancel()
   */
  public final void cancel() {
    cancelled = true;
  }
}

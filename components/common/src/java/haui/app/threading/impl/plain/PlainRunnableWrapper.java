/* *****************************************************************
 * Project: common
 * File:    PlainRunnableWrapper.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl.plain;

import haui.app.threading.impl.AbstractRunnableWrapper;
import haui.app.threading.ManagedRunnable;
import haui.app.threading.impl.Registry;

/**
 * PlainRunnableWrapper
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class PlainRunnableWrapper extends AbstractRunnableWrapper
{
  /**
   * @param workUnit
   * @param managedRunnableRegistry
   */
  public PlainRunnableWrapper(ManagedRunnable workUnit, Registry registry)
  {
    super(workUnit, registry);
  }
}

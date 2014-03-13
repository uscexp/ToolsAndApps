/* *****************************************************************
 * Project: common
 * File:    PlainThreadingComponentImpl.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl.plain;

import haui.app.threading.DemonManager;
import haui.app.threading.FireAndForgetManager;
import haui.app.threading.ParallelWorkManager;
import haui.app.threading.impl.AbstractThreadingComponentImpl;
import haui.app.threading.impl.Registry;

/**
 * PlainThreadingComponentImpl
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class PlainThreadingComponentImpl extends AbstractThreadingComponentImpl
{

  // ==============================================================
  // Class Declarations
  // ==============================================================

  // ==============================================================
  // Class Methods
  // ==============================================================

  // ==============================================================
  // Instance Declarations
  // ==============================================================

  // ==============================================================
  // Constructors
  // ==============================================================

  /**
   *
   */
  public PlainThreadingComponentImpl()
  {
    super();
  }

  // ==============================================================
  // Instance Methods
  // ==============================================================

  protected FireAndForgetManager doCreateFireAndForgetManager(String label, Registry registry)
  {
    return new PlainFireAndForgetManagerImpl(label, registry);
  }

  protected ParallelWorkManager doCreateParallelWorkManager(String label, Registry registry)
  {
    return new PlainParallelWorkManagerImpl(label, registry);
  }

  protected DemonManager doCreateDemonManager(String label, Registry registry)
  {
    return new PlainDemonManagerImpl(label, registry);
  }

  public void shutdown()
  {

  }
}

/* *****************************************************************
 * Project: common
 * File:    PlainFireAndForgetManagerImpl.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl.plain;

import haui.app.threading.FireAndForgetManager;
import haui.app.threading.ManagedRunnable;
import haui.app.threading.impl.AbstractRunnableManagerImpl;
import haui.app.threading.impl.AbstractRunnableWrapper;
import haui.app.threading.impl.Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PlainFireAndForgetManagerImpl
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class PlainFireAndForgetManagerImpl extends AbstractRunnableManagerImpl implements FireAndForgetManager
{

  // ==============================================================
  // Class Declarations
  // ==============================================================

  protected static final Log LOG = LogFactory.getLog(PlainFireAndForgetManagerImpl.class);

  // ==============================================================
  // Instance Declarations
  // ==============================================================

  // ==============================================================
  // Constructors
  // ==============================================================

  /**
   *
   */
  public PlainFireAndForgetManagerImpl(Registry registry)
  {
    this(DEFAULT_NAME, registry);
  }

  /**
   *
   */
  public PlainFireAndForgetManagerImpl(String label, Registry registry)
  {
    super(label, registry);
  }

  // ==============================================================
  // Instance Methods
  // ==============================================================

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.eval.worker.ParallelWorkManager#processJoinableWork(java.util.List,
   * long)
   */
  public void run(ManagedRunnable workUnit)
  {
    run(new ManagedRunnable[] { workUnit });
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.eval.worker.ParallelWorkManager#processJoinableWork(java.util.List,
   * long)
   */
  public void run(ManagedRunnable[] workUnits)
  {

    LOG.debug("run() - starting managed runnables ...");

    for(int i = 0; i < workUnits.length; i++)
    {

      AbstractRunnableWrapper arw = new PlainRunnableWrapper(workUnits[i], this);
      new Thread(arw, arw.getInternalLabel()).start();
    }

    LOG.debug("run() - managed runnables started");
  }
}

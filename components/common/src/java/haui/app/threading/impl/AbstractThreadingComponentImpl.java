/* *****************************************************************
 * Project: common
 * File:    AbstractThreadingComponentImpl.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import haui.app.threading.DemonManager;
import haui.app.threading.FireAndForgetManager;
import haui.app.threading.ManagedRunnable;
import haui.app.threading.ParallelWorkManager;
import haui.app.threading.RunnableManager;
import haui.app.threading.ThreadingComponent;

/**
 * AbstractThreadingComponentImpl
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractThreadingComponentImpl implements ThreadingComponent
{
  protected static final Log LOG = LogFactory.getLog(ThreadingComponent.class);

  // ==============================================================
  // Class Methods
  // ==============================================================

  private static void cancelAllManagedRunnables(ManagerRegistry managerRegistry)
  {
    RunnableManager[] runMgs = managerRegistry.getAllRunnableManagers();
    for(int i = 0; i < runMgs.length; i++)
    {
      runMgs[i].cancel();
    }
  }

  private static void cancelAllManagedRunnables(ManagerRegistry managerRegistry, String managerLabel)
  {
    RunnableManager rm = managerRegistry.getRunnableManager(managerLabel);
    if(rm != null)
    {
      rm.cancel();
    }
  }

  private static void cancelAllManagedRunnables(ManagerRegistry managerRegistry, String managerLabel, String runnableLabel)
  {

    AbstractRunnableManagerImpl arm = (AbstractRunnableManagerImpl)managerRegistry.getRunnableManager(managerLabel);

    if(arm != null)
    {
      ManagedRunnable mr = arm.getManagedRunnable(runnableLabel);
      if(mr != null)
      {
        mr.cancel();
      }
    }
  }

  // ==============================================================
  // Instance Declarations
  // ==============================================================

  /** The only FireAndForget manager for internal purpose only */
  private FireAndForgetManager fafMan = null;

  private final ManagerRegistry parallelManRegistry = new ManagerRegistry("ParalleWork-Managers");

  private final ManagerRegistry demonManRegistry = new ManagerRegistry("Demon-Managers");

  // ==============================================================
  // Constructors
  // ==============================================================

  /**
   * 
   */
  public AbstractThreadingComponentImpl()
  {
    super();
  }

  // ==============================================================
  // Instance Methods
  // ==============================================================

  // --------------------------------------------------------------
  // Component Lifecycle Methods
  // --------------------------------------------------------------

  // --------------------------------------------------------------
  // Component Methods
  // --------------------------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.ThreadingComponent#fireAndForget(com.ubs.istoolset.framework
   * .threading.ManagedRunnable)
   */
  public synchronized final void fireAndForget(ManagedRunnable runnable)
  {
    getFireAndForgetManager().run(runnable);
  }

  /**
   * @param label
   * @return the created {@link FireAndForgetManager}
   * @throws ThreadingException
   */
  public final FireAndForgetManager createFireAndForgetManager(String label)
  {
    return doCreateFireAndForgetManager(label, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.ThreadingComponent#createParallelWorkManager(java.lang
   * .String)
   */
  public final ParallelWorkManager createParallelWorkManager(String label)
  {
    return doCreateParallelWorkManager(label, parallelManRegistry);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.ThreadingComponent#createDemonManager(java.lang.String)
   */
  public synchronized final DemonManager createDemonManager(String label)
  {
    return doCreateDemonManager(label, demonManRegistry);
  }

  protected abstract FireAndForgetManager doCreateFireAndForgetManager(String label, Registry registry);

  protected abstract ParallelWorkManager doCreateParallelWorkManager(String label, Registry registry);

  protected abstract DemonManager doCreateDemonManager(String label, Registry registry);

  // --------------------------------------------------------------
  // Management Methods
  // --------------------------------------------------------------

  private void writeOverviewHeader(Writer wrt)
  {
    try
    {

      wrt.write("Overview of ");
      wrt.write(getClass().getName());
      wrt.write(" at ");
      wrt.write(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()));
      wrt.write('\n');

    }
    catch(IOException e)
    {
      LOG.warn("writeOverviewHeader() - overview could not be provided: " + e.getMessage(), e);
    }
  }

  // -------------------- ThreadingComponent Level --------------------

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#getOverview()
   */
  public String getOverview()
  {
    StringWriter strWrt = new StringWriter();

    writeOverviewHeader(strWrt);

    writeDemonOverview(strWrt);
    writeParallelOverview(strWrt);
    writeFireAndForgetOverview(strWrt);

    return strWrt.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.ThreadingComponent#getRegisteredManagerCount()
   */
  public int getManagerCount()
  {
    return getDemonManagerCount() + getParallelWorkManagerCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#getRunnableCount()
   */
  public int getRunnableCount()
  {
    return getFireAndForgetRunnableCount() + getDemonRunnableCount() + getParallelRunnableCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#cancel()
   */
  public void cancel()
  {
    cancelAllDemonRunnables();
    cancelAllFireAndForgetRunnables();
    cancelAllFireAndForgetRunnables();
  }

  // -------------------- Demon Level --------------------

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getDemonOverview()
   */
  public String getDemonOverview()
  {
    StringWriter strWrt = new StringWriter();

    writeOverviewHeader(strWrt);
    writeDemonOverview(strWrt);

    return strWrt.toString();
  }

  private void writeDemonOverview(Writer wrt)
  {

    try
    {

      demonManRegistry.writeOverview(wrt, 1);

    }
    catch(IOException e)
    {
      LOG.warn("writeDemonOverview() - overview could not be provided: " + e.getMessage(), e);
    }
  }

  /**
   * @return the number of existing {@link DemonManager}
   */
  public int getDemonManagerCount()
  {
    return demonManRegistry.getManagerCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#getDemonRunnableCount
   * ()
   */
  public int getDemonRunnableCount()
  {
    return demonManRegistry.getRunnableCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * cancelAllDemonRunnables()
   */
  public void cancelAllDemonRunnables()
  {
    cancelAllManagedRunnables(demonManRegistry);
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * cancelAllDemonRunnables(java.lang.String)
   */
  public void cancelAllDemonRunnables(String mangerLabel)
  {
    cancelAllManagedRunnables(demonManRegistry, mangerLabel);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#cancelDemonRunnable
   * (java.lang.String, java.lang.String)
   */
  public void cancelDemonRunnable(String mangerLabel, String runnableLabel)
  {
    cancelAllManagedRunnables(demonManRegistry, mangerLabel, runnableLabel);
  }

  // -------------------- Parallel Work Level --------------------

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getParallelOverview()
   */
  public String getParallelOverview()
  {
    StringWriter strWrt = new StringWriter();

    writeOverviewHeader(strWrt);
    writeParallelOverview(strWrt);

    return strWrt.toString();
  }

  private void writeParallelOverview(Writer wrt)
  {

    try
    {

      parallelManRegistry.writeOverview(wrt, 1);

    }
    catch(IOException e)
    {
      LOG.warn("writeParallelOverview() - overview could not be provided: " + e.getMessage(), e);
    }
  }

  /**
   * @return the number of existing {@link ParallelWorkManager}
   */
  public int getParallelWorkManagerCount()
  {
    return parallelManRegistry.getManagerCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * getParallelRunnableCount()
   */
  public int getParallelRunnableCount()
  {
    return parallelManRegistry.getRunnableCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * cancelAllParallelWorkRunnables()
   */
  public void cancelAllParallelWorkRunnables()
  {
    cancelAllManagedRunnables(parallelManRegistry);
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * cancelAllParallelWorkRunnables(java.lang.String)
   */
  public void cancelAllParallelWorkRunnables(String mangerLabel)
  {
    cancelAllManagedRunnables(parallelManRegistry, mangerLabel);
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * cancelParallelWorkRunnable(java.lang.String, java.lang.String)
   */
  public void cancelParallelWorkRunnable(String mangerLabel, String runnableLabel)
  {
    cancelAllManagedRunnables(parallelManRegistry, mangerLabel, runnableLabel);
  }

  // -------------------- Fire And Forget Level --------------------

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.back.framework.components.threading.ThreadingComponentManagementMBean#
   * getFireAndForgetOverview()
   */
  public String getFireAndForgetOverview()
  {
    StringWriter strWrt = new StringWriter();

    writeOverviewHeader(strWrt);
    writeFireAndForgetOverview(strWrt);

    return strWrt.toString();
  }

  private void writeFireAndForgetOverview(Writer wrt)
  {

    try
    {

      if(fafMan != null)
      {

        AbstractRunnableManagerImpl arm = (AbstractRunnableManagerImpl)fafMan;
        if(arm.getRegisteredRunnableCount() > 0)
        {
          arm.writeOverview(wrt, 1);
        }
      }

    }
    catch(IOException e)
    {
      LOG.warn("writeFireAndForgetOverview() - overview could not be provided: " + e.getMessage(), e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * getFireAndForgetRunnableCount()
   */
  public int getFireAndForgetRunnableCount()
  {
    return (fafMan == null ? 0 : ((AbstractRunnableManagerImpl)fafMan).getRegisteredRunnableCount());
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * cancelAllFireAndForgetRunnables()
   */
  public void cancelAllFireAndForgetRunnables()
  {
    if(fafMan != null)
    {
      fafMan.cancel();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seecom.ubs.istoolset.framework.threading.impl.ThreadingComponentManagementMBean#
   * cancelFireAndForgetRunnable(java.lang.String)
   */
  public void cancelFireAndForgetRunnable(String runnableLabel)
  {
    if(fafMan != null)
    {
      ManagedRunnable mr = ((AbstractRunnableManagerImpl)fafMan).getManagedRunnable(runnableLabel);
      if(mr != null)
      {
        mr.cancel();
      }
    }
  }

  private FireAndForgetManager getFireAndForgetManager()
  {
    if(fafMan == null)
    {
      fafMan = doCreateFireAndForgetManager(FireAndForgetManager.DEFAULT_NAME, null);
    }
    return fafMan;
  }

  // ==============================================================
  // Inner Classes
  // ==============================================================

  private static class ManagerRegistry implements Registry
  {

    private static final int SER_NUMBER_SIZE = 5;

    private String name;

    /**
     * The SerialNumberFactory to provide the serial number used to enhance the label of all
     * registered RunnableManagerS
     */
    private SerialNumberFactory serialNumberFactory = new SerialNumberFactory(SER_NUMBER_SIZE);

    /** All currently registered RunnableMangerS */
    private final Map registeredManagers = new Hashtable();

    /**
     * 
     * @param name
     */
    public ManagerRegistry(String name)
    {
      this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
      return "ManagerRegistry name=" + name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ubs.istoolset.framework.threading.impl.Registry#createInternalLabel(java.lang.String)
     */
    public String createInternalLabel(String label)
    {
      StringBuffer b = new StringBuffer(label);
      b.append('-').append(serialNumberFactory.getNewNumberString());

      return b.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ubs.istoolset.framework.threading.impl.Registry#register(com.ubs.istoolset.framework.
     * threading.impl.Registerable)
     */
    public void register(Registerable registerable)
    {
      synchronized(registeredManagers)
      {
        registeredManagers.put(registerable.getInternalLabel(), registerable);
      }

      if(LOG.isDebugEnabled())
      {
        LOG.debug("register() - registry=" + this + ": registerable=" + registerable + " has been registered");
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ubs.istoolset.framework.threading.impl.Registry#unregister(com.ubs.istoolset.framework
     * .threading.impl.Registerable)
     */
    public boolean unregister(Registerable registerable)
    {
      boolean removed;

      synchronized(registeredManagers)
      {
        removed = registeredManagers.remove(registerable.getInternalLabel()) != null;
      }

      if(removed)
      {
        if(LOG.isDebugEnabled())
        {
          LOG.debug("unregister() - registry=" + this + ": registerable=" + registerable + " has been successfully removed");
        }
      }
      else
      {
        LOG.warn("unregister() - registry=" + this + ": registerable=" + registerable + " could NOT be removed");
      }

      return removed;
    }

    public int getManagerCount()
    {
      return registeredManagers.size();
    }

    public int getRunnableCount()
    {
      RunnableManager[] runMgs = getAllRunnableManagers();

      int result = 0;

      for(int i = 0; i < runMgs.length; i++)
      {
        result += ((AbstractRunnableManagerImpl)runMgs[i]).getRegisteredRunnableCount();
      }

      return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ubs.istoolset.framework.threading.impl.Registry#writeOverView(java.io.Writer)
     */
    public void writeOverview(Writer wrt, int indent) throws IOException
    {
      RunnableManager[] runMgs = getAllRunnableManagers();

      for(int i = 0; i < runMgs.length; i++)
      {
        ((Registerable)runMgs[i]).writeOverview(wrt, indent);
      }
    }

    public final RunnableManager[] getAllRunnableManagers()
    {
      RunnableManager[] runMgs;

      synchronized(registeredManagers)
      {
        runMgs = new RunnableManager[registeredManagers.size()];
        registeredManagers.values().toArray(runMgs);
      }

      return runMgs;
    }

    public final RunnableManager getRunnableManager(String label)
    {
      return (RunnableManager)registeredManagers.get(label);
    }
  }
}

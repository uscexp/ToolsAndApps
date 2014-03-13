/* *****************************************************************
 * Project: common
 * File:    AbstractRunnableManagerImpl.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl;

import haui.app.threading.ManagedRunnable;
import haui.app.threading.RunnableManager;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AbstractRunnableManagerImpl
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractRunnableManagerImpl implements RunnableManager, Registry, Registerable
{

  // ==============================================================
  // Class Declarations
  // ==============================================================

  private static final Log LOG = LogFactory.getLog(AbstractRunnableManagerImpl.class);

  private static final int SERIAL_NUMBER_LENGTH = 5;

  // ==============================================================
  // Class Methods
  // ==============================================================

  // ==============================================================
  // Instance Declarations
  // ==============================================================

  private final String label;

  private String internalLabel = null;;

  private final Registry parentRegistry;

  private final Map registry = new Hashtable();

  private final SerialNumberFactory serialNumberFactory = new SerialNumberFactory(SERIAL_NUMBER_LENGTH);

  // ==============================================================
  // Constructors
  // ==============================================================

  /**
   * 
   */
  public AbstractRunnableManagerImpl(String label, Registry parentRegistry)
  {
    super();

    if(label == null)
    {
      throw new IllegalArgumentException("AbstractRunnableManagerImpl - label must not be null");
    }

    this.label = label;
    this.parentRegistry = parentRegistry;

    if(parentRegistry != null)
    {
      this.internalLabel = parentRegistry.createInternalLabel(label);
      this.parentRegistry.register(this);
    }
  }

  // ==============================================================
  // Instance Methods
  // ==============================================================

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return internalLabel.hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("AbstractRunnableManagerImpl: type=").append(getClass().getName()).append(", label=").append(label).append(", internalLabel=").append(
        internalLabel);
    return buffer.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if(this == obj)
    {
      return true;
    }

    if(!(obj instanceof AbstractRunnableManagerImpl))
    {
      return false;
    }

    return internalLabel.equals(((AbstractRunnableManagerImpl)obj).internalLabel);
  }

  /**
   * @return the label
   */
  public String getLabel()
  {
    return label;
  }

  /**
   * @return the internal label
   */
  public String getInternalLabel()
  {
    return internalLabel != null ? internalLabel : label;
  }

  public String createInternalLabel(String label)
  {
    StringBuffer buf = new StringBuffer(label == null || label.trim().length() == 0 ? getLabel() : label);
    buf.append('-').append(serialNumberFactory.getNewNumberString());

    return buf.toString();
  }

  public ManagedRunnable getManagedRunnable(String runnableLabel)
  {
    ManagedRunnable mr = null;
    Object obj = registry.get(runnableLabel);
    if(obj != null)
    {
      if(obj instanceof AbstractRunnableManagerImpl)
      {
        mr = ((AbstractRunnableManagerImpl)obj).getManagedRunnable(runnableLabel);
      }
      else
      {
        mr = (ManagedRunnable)obj;
      }
    }
    return mr;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.impl.ManagedRunnableRegistry#register(com.ubs.istoolset
   * .framework.threading.InternalManagedRunnable)
   */
  public void register(Registerable registerable)
  {

    synchronized(registry)
    {
      registry.put(registerable.getInternalLabel(), registerable);
    }

    if(LOG.isDebugEnabled())
    {
      LOG.debug("register() - registry=" + this + ": registerable=" + registerable + " has been registred");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.impl.ManagedRunnableRegistry#unregister(com.ubs.istoolset
   * .framework.threading.InternalManagedRunnable)
   */
  public boolean unregister(Registerable registerable)
  {
    boolean removed;

    synchronized(registry)
    {

      removed = registry.remove(registerable.getInternalLabel()) != null;

      // If the manager does not manage Registerable instances anymore then unregister it
      // in the parentRegistry
      if(parentRegistry != null && removed && registry.isEmpty())
      {
        parentRegistry.unregister(this);
      }
    }

    if(LOG.isDebugEnabled())
    {
      LOG.debug("unregister() - registry=" + this + ": registerable=" + registerable
          + (removed ? " has been successfully removed from runnable registry" : " could not be removed"));
    }

    return removed;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.RunnableManager#cancel()
   */
  public void cancel()
  {

    if(LOG.isDebugEnabled())
    {
      LOG.debug("cancel() - " + this + " cancelling ...");
    }

    Object[] regArr;

    synchronized(registry)
    {
      regArr = registry.values().toArray();
    }

    for(int i = 0; i < regArr.length; i++)
    {
      ((Registerable)regArr[i]).cancel();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.RunnableManager#getManagedRunnableCount()
   */
  public int getManagedRunnableCount()
  {
    return registry.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.istoolset.framework.threading.impl.RegisterableManager#getRegisteredRunnableCount()
   */
  public int getRegisteredRunnableCount()
  {
    return registry.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.impl.Registerable#writeOverview(java.io.Writer)
   */
  public void writeOverview(Writer wrt, int indent) throws IOException
  {
    wrt.write(Registry.INDENTS[indent]);
    wrt.write("RunnableManager: type=");
    wrt.write(getClass().getName());
    wrt.write(", label=");
    wrt.write(label);
    wrt.write(", internalLabel=");
    wrt.write(internalLabel);
    wrt.write('\n');

    Registerable[] regArr;

    synchronized(registry)
    {
      regArr = new Registerable[registry.size()];
      registry.values().toArray(regArr);
    }

    int regIndent = indent + 1;
    for(int i = 0; i < regArr.length; i++)
    {
      regArr[i].writeOverview(wrt, regIndent);
    }
  }
}

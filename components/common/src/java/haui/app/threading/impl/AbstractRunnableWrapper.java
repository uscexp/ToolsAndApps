/* *****************************************************************
 * Project: common
 * File:    AbstractRunnableWrapper.java
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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import haui.app.threading.ManagedRunnable;

/**
 * AbstractRunnableWrapper
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractRunnableWrapper implements ManagedRunnable, Registerable
{

  // ==============================================================
  // Class Declarations
  // ==============================================================

  private static final Log LOG = LogFactory.getLog(AbstractRunnableWrapper.class);

  private static final int MSECS_PER_SEC = 1000;
  private static final int MSECS_PER_MIN = MSECS_PER_SEC * 60;
  private static final int MSECS_PER_HOUR = MSECS_PER_MIN * 60;
  private static final int MSECS_PER_DAY = MSECS_PER_HOUR * 24;

  private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

  // ==============================================================
  // Class Methods
  // ==============================================================

  private static String formatDuration(long duration)
  {
    StringWriter wrt = new StringWriter();
    try
    {
      formatDuration(duration, wrt);
    }
    catch(IOException e)
    {
      LOG.warn("formatDuration() - " + e.getMessage());
    }
    return wrt.toString();
  }

  private static void formatDuration(long duration, Writer wrt) throws IOException
  {
    long l = duration;

    wrt.write(Long.toString(l / MSECS_PER_DAY));
    wrt.write("d, ");
    l %= MSECS_PER_DAY;

    wrt.write(Long.toString(l / MSECS_PER_HOUR));
    wrt.write("h, ");
    l %= MSECS_PER_HOUR;

    wrt.write(Long.toString(l / MSECS_PER_MIN));
    wrt.write("m, ");
    l %= MSECS_PER_MIN;

    wrt.write(Long.toString(l / MSECS_PER_SEC));
    wrt.write("s, ");
    l %= MSECS_PER_SEC;

    wrt.write(Long.toString(l));
    wrt.write("ms");
  }

  // ==============================================================
  // Instance Declarations
  // ==============================================================

  private String internalLabel;

  private ManagedRunnable managedRunnable;

  private Registry registry;

  private long startTime;

  private long stopTime;

  private boolean cancelled = false;

  private Thread myThread = null;

  // ==============================================================
  // Comstructors
  // ==============================================================

  /**
   *
   */
  public AbstractRunnableWrapper(ManagedRunnable workUnit, Registry registry)
  {
    super();

    this.managedRunnable = workUnit;

    this.registry = registry;
    this.internalLabel = registry.createInternalLabel(workUnit.getLabel());

    this.registry.register(this);
  }

  // ==============================================================
  // Instance methods
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
    buffer.append("AbstractRunnableWrapper: type=").append(managedRunnable.getClass().getName()).append(", label=").append(getLabel()).append(
        ", internalLabel=").append(internalLabel);
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
      return true;

    if(!(obj instanceof AbstractRunnableWrapper))
      return false;

    return internalLabel.equals(((AbstractRunnableWrapper)obj).internalLabel);
  }

  public String getInternalLabel()
  {
    return internalLabel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.impl.Registerable#writeOverview(java.io.Writer)
   */
  public void writeOverview(Writer wrt) throws IOException
  {
    wrt.write("State of ManagedRunnable: ");
    wrt.write("type=");
    wrt.write(managedRunnable.getClass().getName());
    wrt.write(", label=");
    wrt.write(getLabel());
    wrt.write(", internalLabel=");
    wrt.write(internalLabel);

    wrt.write(", started at=");
    wrt.write(startTime > 0 ? DATE_FORMATTER.format(new Date(startTime)) : "not started");

    if(stopTime != 0)
    {

      wrt.write(", ended at=");
      wrt.write(DATE_FORMATTER.format(new Date(stopTime)));

      wrt.write(", consumed time=");
      formatDuration(stopTime - startTime, wrt);

    }
    else
    {

      wrt.write(", consumed time=");
      formatDuration(System.currentTimeMillis() - startTime, wrt);
    }
  }

  protected final void setStarted()
  {

    myThread = Thread.currentThread();

    startTime = System.currentTimeMillis();

    if(LOG.isDebugEnabled())
    {
      LOG.debug("setStarted() - " + this + " started");
    }
  }

  private final void setFinished()
  {
    if(isStopped())
    {
      return;
    }

    stopTime = System.currentTimeMillis();

    registry.unregister(this);

    if(LOG.isDebugEnabled())
    {
      LOG.debug("setFinished() - " + this + " finished after " + formatDuration(stopTime - startTime));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.ManagedRunnable#run()
   */
  public void run()
  {
    try
    {

      setStarted();

      managedRunnable.run();

    }
    catch(RuntimeException re)
    {

      LOG.warn("run() - " + this + " finished with unchecked exeption:", re);

    }
    finally
    {

      setFinished();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.ManagedRunnable#cancel()
   */
  public void cancel()
  {
    if(cancelled)
    {
      return;
    }

    cancelled = true;

    // To speed up the cancellation set the thread to interrupted
    if(myThread != null)
    {
      myThread.interrupt();
    }

    // Propagate the cancel to actual Runnable
    managedRunnable.cancel();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.ManagedRunnable#getLabel()
   */
  public String getLabel()
  {
    return managedRunnable.getLabel();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.ManagedRunnable#isCancelled()
   */
  public boolean isStopped()
  {
    return stopTime != 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.ManagedRunnable#isCancelled()
   */
  public boolean isCancelled()
  {
    return cancelled;
  }

  /**
   * @return the managed runnable
   */
  protected final ManagedRunnable getManagedRunnable()
  {
    return managedRunnable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.istoolset.framework.threading.impl.Registerable#writeOverview(java.io.Writer, int)
   */
  public void writeOverview(Writer wrt, int indent) throws IOException
  {

    wrt.write(Registry.INDENTS[indent]);

    wrt.write("RunnableWrapper: type=");
    wrt.write(getClass().getName());
    wrt.write(", internalLabel=");
    wrt.write(internalLabel);
    wrt.write(", started at=");
    wrt.write(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(startTime)));
    wrt.write(", duration=");
    formatDuration(System.currentTimeMillis() - startTime, wrt);

    wrt.write(" - ManagedRunnable: type=");
    wrt.write(managedRunnable.getClass().getName());
    wrt.write(", label=");
    wrt.write(getLabel());
    wrt.write('\n');
  }
}

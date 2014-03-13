/* *****************************************************************
 * Project: common
 * File:    Trace.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Trace
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Trace
{

  private static final Log LOG = LogFactory.getLog(Trace.class);

  private long startTime = System.currentTimeMillis();

  private String name;
  private Log log;

  public Trace(String name, Log log)
  {
    this.name = name;
    this.log = log;
  }

  public Trace()
  {
  }

  public void measure(String message)
  {
    long duration = getDuration();
    startTime = System.currentTimeMillis();
    if(isEnabled())
    {
      trace(getName() + message + " executed in " + duration + " (ms)!");
    }
  }

  private final String getName()
  {
    if(name == null)
      return "";
    return name + ": ";
  }

  private final Log getLog()
  {
    if(log == null)
      return LOG;
    return log;
  }

  public void reset()
  {
    startTime = System.currentTimeMillis();
  }

  public final long getDuration()
  {
    return System.currentTimeMillis() - startTime;
  }

  protected boolean isEnabled()
  {
    return getLog().isTraceEnabled();
  }

  protected void trace(String message)
  {
    getLog().trace(message);
  }

  public long getStartTime()
  {
    return startTime;
  }
}

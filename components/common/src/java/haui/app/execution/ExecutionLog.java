/* *****************************************************************
 * Project: common
 * File:    ExecutionLog.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ExecutionLog
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ExecutionLog
{
  public static final Log LOG = LogFactory.getLog(ExecutionLog.class);    
  /**
   * Logs an error
   * @param identifier
   * @param message
   * @param t
   */
  public void error(String stid, Object message, Throwable t);    
  /**
   * Logs an error
   * @param message
   * @param t
   */
  public void error(Object message, Throwable t);            
  /**
   * Logs a warning
   * 
   * @param identifier
   * @param message
   */
  public void warn(String stid, Object message);    
  /**
   * Logs a warning
   * 
   * @param message
   */
  public void warn(Object message);   
  /**
   * Logs a debug message
   * 
   * @param identifier
   * @param message
   */
  public void debug(String stid, Object message);    
  /**
   * Logs a debug message
   * 
   * @param message
   */
  public void debug(Object message);
  
  /**
   * Logs a debug message
   * 
   * @param identifier
   * @param message
   */
  public void info(String stid, Object message);    
  /**
   * Logs a debug message
   * 
   * @param message
   */
  public void info(Object message);
}

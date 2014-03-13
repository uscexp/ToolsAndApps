/* *****************************************************************
 * Project: common
 * File:    NestedExecutionLog.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

/**
 * {@link ExecutionLog} for nested executions.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class NestedExecutionLog extends AbstractExecutionLog
{
  private NestedExecutionContext context;
  private RootExecutionLog root;
  
  public NestedExecutionLog(NestedExecutionContext context, RootExecutionLog root) {
      this.context = context;
      this.root = root;
  }    
  
  public void error(String stid, Object message, Throwable t) {
      root.error(context, stid, message, t);
  }

  public void debug(String stid, Object message) {
      root.debug(context, stid, message);
  }

  public void warn(String stid, Object message) {
      root.warn(context, stid, message);
  }

  public void info(String stid, Object message) {
      root.info(context, stid, message);        
  }
}

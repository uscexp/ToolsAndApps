/* *****************************************************************
 * Project: common
 * File:    ExecutionMode.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.model.data.Enum;

import java.io.ObjectStreamException;

/**
 * Enum for the different kinds of execution modes. (SYNCHRON, ASYNCHRON and 
 * POSTPONED)
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionMode extends Enum
{
  static final long serialVersionUID = 7459645226492948863L;

  /**
   * Execute inline, on the same thread. If called from a service implementation (BO method) for
   * instance, then the execution takes place in the within the request processint thread of the
   * service.
   */
  public static final ExecutionMode SYNCHRON = new ExecutionMode();

  /**
   * The Execution spawns a new thread and the the control returns to the starting thread
   * immediatelly
   */
  public static final ExecutionMode ASYNCHRON = new ExecutionMode();

  /**
   * The execution is postponed until the ServiceContext of the current thread is destroyed.
   * Postponed is useful for instance, if the execution is started from a transactional service and
   * the transaction of the service needs to be compleeted bevore the execution can be run.
   */
  public static final ExecutionMode POSTPONED = new ExecutionMode();

  private ExecutionMode()
  {
  }

  private Object readResolve() throws ObjectStreamException
  {
    return Enum.getCode(getClass(), getCode());
  }
}

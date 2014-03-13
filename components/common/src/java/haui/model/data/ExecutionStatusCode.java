/* *****************************************************************
 * Project: common
 * File:    ExecutionStatusCode.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.data;

import java.io.ObjectStreamException;

/**
 * ExecutionStatusCode
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionStatusCode extends Enum
{
  static final long serialVersionUID = 7459645226492948863L;

  private ExecutionStatusCode()
  {
  }

  /**
   * the execution has been scheduled but did not jet start
   */
  public static final ExecutionStatusCode WAITING_FOR_START = new ExecutionStatusCode();
  /**
   * the execution is running
   */
  public static final ExecutionStatusCode RUNNING = new ExecutionStatusCode();
  /**
   * the execution has been canceled by the user and is waiting for termination
   */
  public static final ExecutionStatusCode WAITING_FOR_CANCEL = new ExecutionStatusCode();
  /**
   * the execution has terminated
   */
  public static final ExecutionStatusCode TERMINATED = new ExecutionStatusCode();

  private Object readResolve() throws ObjectStreamException
  {
    return Enum.getCode(getClass(), getCode());
  }
}

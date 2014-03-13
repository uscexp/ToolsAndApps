/* *****************************************************************
 * Project: common
 * File:    ExecutionTerminationCode.java
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
 * ExecutionTerminationCode
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionTerminationCode extends Enum
{

  static final long serialVersionUID = 7459645226492948863L;

  /**
   * for an execution that has not yet terminated at all
   */
  public static final ExecutionTerminationCode NOT_TERMINATED = new ExecutionTerminationCode();

  /**
   * successfully terminated
   */
  public static final ExecutionTerminationCode SUCCESSFUL = new ExecutionTerminationCode();

  /**
   * canceled by user interaction
   */
  public static final ExecutionTerminationCode CANCELED = new ExecutionTerminationCode();

  /**
   * execution failed
   */
  public static final ExecutionTerminationCode FAILED = new ExecutionTerminationCode();

  private ExecutionTerminationCode()
  {
  }

  private Object readResolve() throws ObjectStreamException
  {
    return Enum.getCode(getClass(), getCode());
  }
}

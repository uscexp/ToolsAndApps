/* *****************************************************************
 * Project: common
 * File:    ValidationException.java
 * 
 * Creation:     14.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.exception;

/**
 * ValidationException
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ValidationException extends Exception
{

  static final long serialVersionUID = 7902949050393667071L;

  public ValidationException()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  public ValidationException(String message, Throwable cause)
  {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public ValidationException(String message)
  {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public ValidationException(Throwable cause)
  {
    super(cause);
    // TODO Auto-generated constructor stub
  }

}

/* *****************************************************************
 * Project: common
 * File:    AppSystemExeption.java
 * 
 * Creation:     21.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.exception;

/**
 * AppSystemExeption
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AppSystemException extends RuntimeException
{

  static final long serialVersionUID = -4659744569119698433L;

  public AppSystemException()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  public AppSystemException(String message, Throwable cause)
  {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public AppSystemException(String message)
  {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public AppSystemException(Throwable cause)
  {
    super(cause);
    // TODO Auto-generated constructor stub
  }

}

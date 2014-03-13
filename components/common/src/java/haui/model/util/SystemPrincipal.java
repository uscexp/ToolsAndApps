/* *****************************************************************
 * Project: common
 * File:    SystemPrincipal.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.util;

import java.io.Serializable;
import java.security.Principal;

/**
 * SystemPrincipal
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SystemPrincipal implements Principal, Serializable
{
  static final long serialVersionUID = 1375584729992294783L;

  /**
   * Creates a new SystemPrincipal
   */
  public SystemPrincipal()
  {
  }

  public String getName()
  {
    return SecurityConstants.SYSTEM_USER_GPN;
  }

  public boolean equals(Object obj)
  {
    return obj instanceof SystemPrincipal;
  }

  public int hashCode()
  {
    return SecurityConstants.SYSTEM_USER_GPN.hashCode();
  }

  public String toString()
  {
    return "SystemPrincipal";
  }
}

/* *****************************************************************
 * Project: common
 * File:    DefaultCurrentUserMapper.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.security;

import haui.model.partner.UserDTO;

import java.security.Principal;

import haui.app.dao.partner.UserDAO;

/**
 * DefaultCurrentUserMapper
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class DefaultCurrentUserMapper implements ICurrentUserMapper
{

  /**
   * 
   */
  public DefaultCurrentUserMapper() {
  }

  /* (non-Javadoc)
   * @see com.ubs.arte.app.security.ICurrentUserMapper#getCurrentUser(java.security.Principal)
   */
  public UserDTO getCurrentUser(Principal principal) {
     return new UserDAO().getCurrentUser(principal);
  }
}

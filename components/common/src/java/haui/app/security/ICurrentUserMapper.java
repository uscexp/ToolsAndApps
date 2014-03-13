/* *****************************************************************
 * Project: common
 * File:    ICurrentUserMapper.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.security;

import java.security.Principal;

import haui.model.partner.UserDTO;

/**
 *  Map the current user to the given principal
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ICurrentUserMapper
{
  /**
   * Maps the given principal object into a UserDTO object.
   * @param principal
   * @return a UserDTO
   */
  public UserDTO getCurrentUser(Principal principal);
}

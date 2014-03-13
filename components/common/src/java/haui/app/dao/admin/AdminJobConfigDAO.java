/* *****************************************************************
 * Project: common
 * File:    AdminJobConfigDAO.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.admin;

import haui.app.dao.AppBaseDAO;
import haui.model.admin.AdminJobConfigDTO;

/**
 * AdminJobConfigDAO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AdminJobConfigDAO extends AppBaseDAO
{

  /**
   * Retrieve the <code>AdminJobConfigDTO</code> given a name.
   * 
   * @param name of the object
   * @return the <code>AdminJobConfigDTO</code> or <code>null</code>
   * @throws DataAccessException if the operation is not successful
   */
  public AdminJobConfigDTO findByName(String name)
  {
    return (AdminJobConfigDTO)super.findObject(AdminJobConfigDTO.class, AdminJobConfigDTO.PN_FILE_NAME, name);
  }

  public void save(AdminJobConfigDTO adminJobConfig)
  {
    super.save(adminJobConfig);
    getSession().flush();
  }
}

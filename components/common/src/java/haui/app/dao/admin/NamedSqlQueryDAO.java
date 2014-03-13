/* *****************************************************************
 * Project: common
 * File:    NamedSqlQueryDAO.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.admin;

import haui.app.dao.AppBaseDAO;
import haui.model.admin.NamedSqlQueryDTO;
import haui.model.db.ICriteria;
import haui.model.db.Restrictions;

/**
 * NamedSqlQueryDAO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class NamedSqlQueryDAO extends AppBaseDAO
{

  /**
   * find SQL query by name
   * @param name SQL query name
   * @return {@link NamedSqlQueryDTO} or null if not found
   */
  public NamedSqlQueryDTO findByName(String name) {
    ICriteria criteria = getSession().createCriteria(NamedSqlQueryDTO.class);
    criteria.add(Restrictions.instance().eq(NamedSqlQueryDTO.PN_NAME, name));
    
    return (NamedSqlQueryDTO)criteria.uniqueResult();
  }
}

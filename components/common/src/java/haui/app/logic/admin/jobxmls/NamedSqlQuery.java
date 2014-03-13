/* *****************************************************************
 * Project: common
 * File:    NamedSqlQuery.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import haui.app.dao.admin.NamedSqlQueryDAO;
import haui.exception.AppSystemException;
import haui.model.admin.NamedSqlQueryDTO;

import org.w3c.dom.Element;

/**
 * NamedSqlQuery
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class NamedSqlQuery extends SqlExpression
{

  public static final String TAG = "namedSqlQuery";

  private String name;

  public boolean init(Element element)
  {
    String name = element.getAttribute("name");
    setName(name);

    return super.init(element);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.hdv.app.logic.admin.jobxml.SqlExpression#getResolvedSql()
   */
  @Override
  public String getSql()
  {
    NamedSqlQueryDAO namedSqlQueryDAO = new NamedSqlQueryDAO();
    NamedSqlQueryDTO namedSqlQuery = namedSqlQueryDAO.findByName(name);

    if(namedSqlQuery == null)
    {
      throw new AppSystemException("No query for name " + name + " found!");
    }
    return namedSqlQuery.getSqlQuery();
  }
}

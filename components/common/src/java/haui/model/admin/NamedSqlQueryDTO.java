/* *****************************************************************
 * Project: common
 * File:    NamedSqlQueryDTO.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.admin;

import haui.model.DataTransferObject;

/**
 * NamedSqlQueryDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class NamedSqlQueryDTO extends DataTransferObject
{
  static final long serialVersionUID = -8669224045276401281L;

  // pn attributes
  public static final String PN_SQL_QUERY = "sqlQuery";
  public static final String PN_DESCRIPTION = "description";
  public static final String PN_NAME = "name";

  // property members
  private String sqlQuery;
  private String description;
  private String name;

  /**
   * default constructor
   * 
   */
  public NamedSqlQueryDTO()
  {
    super();
  }

  // accessor methods

  /**
   * // TODO insert comment into xml for [sqlQuery]
   * 
   * @return the sqlQuery property
   */
  public final String getSqlQuery()
  {
    return sqlQuery;
  }

  /**
   * // TODO insert comment into xml for [sqlQuery]
   * 
   */
  public final void setSqlQuery(String sqlQuery)
  {
    this.sqlQuery = sqlQuery;
  }

  /**
   * // TODO insert comment into xml for [description]
   * 
   * @return the description property
   */
  public final String getDescription()
  {
    return description;
  }

  /**
   * // TODO insert comment into xml for [description]
   * 
   */
  public final void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * // TODO insert comment into xml for [name]
   * 
   * @return the name property
   */
  public final String getName()
  {
    return name;
  }

  /**
   * // TODO insert comment into xml for [name]
   * 
   */
  public final void setName(String name)
  {
    this.name = name;
  }
}

/* *****************************************************************
 * Project: common
 * File:    SqlQuery.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SqlQuery
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SqlQuery extends SqlExpression
{

  public static final String TAG = "sqlQuery";

  private String sql;

  public void setSql(String sql)
  {
    this.sql = sql;
  }

  public boolean init(Element element)
  {
    NodeList listOfSubElements = element.getChildNodes();

    for(int i = 0; i < listOfSubElements.getLength(); i++)
    {
      Node node = listOfSubElements.item(i);
      if(node.getNodeType() == Node.TEXT_NODE)
      {
        String sql = node.getNodeValue().trim();

        if(sql != null && sql.length() > 0)
        {
          setSql(sql);
          break;
        }
      }
    }

    return super.init(element);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.hdv.app.logic.admin.jobxml.SqlExpression#getResolvedSql()
   */
  @Override
  public String getSql()
  {
    return sql;
  }
}

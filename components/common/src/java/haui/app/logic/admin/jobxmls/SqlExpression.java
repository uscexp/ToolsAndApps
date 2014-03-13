/* *****************************************************************
 * Project: common
 * File:    SqlExpression.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import haui.app.dao.db.AbstractDataSource;
import haui.app.logic.admin.NamedQueryUtil;
import haui.exception.AppSystemException;
import haui.model.db.ISQLQuery;
import haui.model.db.ISession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * SqlExpression
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class SqlExpression extends Expression
{

  private String resultType;

  private List<SqlVariable> variables = new ArrayList<SqlVariable>();

  private Map<String, Object> params = null;

  public boolean init(Element element)
  {
    boolean result = true;

    String type = element.getAttribute("resultType");
    setResultType(type);

    NodeList listOfSubElements = element.getElementsByTagName(SqlVariable.TAG);

    for(int i = 0; i < listOfSubElements.getLength(); i++)
    {
      Element subElement = (Element)listOfSubElements.item(i);

      SqlVariable variable = new SqlVariable();
      variable.init(subElement);
      addVariable(variable);
    }
    return result;
  }

  public List<SqlVariable> getVariables()
  {
    return variables;
  }

  public void setVariables(List<SqlVariable> variables)
  {
    this.variables = variables;
  }

  public boolean addVariable(SqlVariable variable)
  {
    return variables.add(variable);
  }

  public boolean removeVariable(SqlVariable variable)
  {
    return variables.remove(variable);
  }

  public String getResultType()
  {
    return resultType;
  }

  public void setResultType(String resultType)
  {
    this.resultType = resultType;
  }

  public abstract String getSql();

  public String getResolvedSql()
  {
    String sql = getSql();
    String resolvedSql = NamedQueryUtil.substituteParmsInQuery(sql, getParams());
    resolvedSql = NamedQueryUtil.addSchemaPrefixToTablenames(resolvedSql);
    return resolvedSql;
  }

  @Override
  public Object getValue()
  {
    Object result = null;
    String sql = "";
    try
    {
      sql = getResolvedSql();
      ISession session = AbstractDataSource.instance().getSession();
      ISQLQuery query = session.createSQLQuery(sql);

      result = query.uniqueResult();
      if(result != null && resultType != null)
      {
        result = convertToType(resultType, result);
      }
    }
    catch(Exception e)
    {
      throw new AppSystemException("Can't execute query!" + sql, e);
    }
    return result;
  }

  public Map<String, Object> getParams()
  {
    if(params == null)
    {
      params = new HashMap<String, Object>();
      for(int i = 0; i < variables.size(); ++i)
      {
        SqlVariable variable = variables.get(i);

        params.put(variable.getName(), variable.getValue());
      }
    }
    return params;
  }
}

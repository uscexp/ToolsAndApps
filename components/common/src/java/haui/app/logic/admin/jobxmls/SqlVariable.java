/* *****************************************************************
 * Project: common
 * File:    SqlVariable.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import haui.exception.AppSystemException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SqlVariable
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SqlVariable extends Expression
{

  public static final String TAG = "sqlVariable";

  private String name;

  private Expression expression;

  public boolean init(Element element)
  {
    String name = element.getAttribute("name");
    setName(name);

    NodeList listOfSubElements = element.getChildNodes();

    int idx = 0;
    for(int i = 0; i < listOfSubElements.getLength(); i++)
    {
      Node node = listOfSubElements.item(i);
      if(node.getNodeType() == Node.ELEMENT_NODE)
      {
        Element subElement = (Element)node;
        if(idx == 0)
          setExpression(ExpressionFactory.createExpression(subElement));
        else
        {
          throw new AppSystemException("There are more than one expression in sqlVariable " + name);
        }
        ++idx;
      }
    }
    if(idx < 1)
    {
      throw new AppSystemException("There must be one expression in sqlVariable " + name);
    }
    return true;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Expression getExpression()
  {
    return expression;
  }

  public void setExpression(Expression expression)
  {
    this.expression = expression;
  }

  @Override
  public Object getValue()
  {
    return expression.getValue();
  }
}

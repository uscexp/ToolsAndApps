/* *****************************************************************
 * Project: common
 * File:    CompareCondition.java
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
 * CompareCondition
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class CompareCondition extends ConditionalExpression
{

  private Expression expression;

  private Expression expressionToCompare;

  public boolean init(Element element)
  {
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
        else if(idx == 1)
          setExpressionToCompare(ExpressionFactory.createExpression(subElement));
        else
        {
          throw new AppSystemException("There are more than two expressions in condition " + getTag());
        }
        ++idx;
      }
    }
    if(idx < 2)
    {
      throw new AppSystemException("There must be two expressions in condition " + getTag());
    }
    return true;
  }

  public abstract String getTag();

  public Expression getExpression()
  {
    return expression;
  }

  public void setExpression(Expression expression)
  {
    this.expression = expression;
  }

  public Expression getExpressionToCompare()
  {
    return expressionToCompare;
  }

  public void setExpressionToCompare(Expression expressionToCompare)
  {
    this.expressionToCompare = expressionToCompare;
  }

  protected int compareTo(Expression expression, Expression expressionToCompare)
  {
    if((expression == null || expression.getValue() == null) && (expressionToCompare == null || expressionToCompare.getValue() == null))
    {
      return 0;
    }
    else if((expression == null || expression.getValue() == null) || (expressionToCompare == null || expressionToCompare.getValue() == null))
    {
      throw new AppSystemException("One of the values is null");
    }
    else if((expression != null && expression.getValue() instanceof Comparable)
        && (expressionToCompare != null && expressionToCompare.getValue() instanceof Comparable))
    {
      return ((Comparable)expression.getValue()).compareTo((Comparable)expressionToCompare.getValue());
    }
    else
    {
      throw new AppSystemException("At least one of the values is not Comparable");
    }
  }
}

/* *****************************************************************
 * Project: common
 * File:    ExpressionFactory.java
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

/**
 * ExpressionFactory
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExpressionFactory
{

  public static ConditionalExpression createConditionalExpression(Element element)
  {
    String tagName = element.getNodeName();
    ConditionalExpression conditionalExpression = null;

    if(And.TAG.equals(tagName))
    {
      conditionalExpression = new And();
    }
    else if(Or.TAG.equals(tagName))
    {
      conditionalExpression = new Or();
    }
    else if(GreaterAs.TAG.equals(tagName))
    {
      conditionalExpression = new GreaterAs();
    }
    else if(GreaterEquals.TAG.equals(tagName))
    {
      conditionalExpression = new GreaterEquals();
    }
    else if(LesserAs.TAG.equals(tagName))
    {
      conditionalExpression = new LesserAs();
    }
    else if(LesserEquals.TAG.equals(tagName))
    {
      conditionalExpression = new LesserEquals();
    }
    else if(Equals.TAG.equals(tagName))
    {
      conditionalExpression = new Equals();
    }
    else if(NotEquals.TAG.equals(tagName))
    {
      conditionalExpression = new NotEquals();
    }
    else
    {
      throw new AppSystemException("There exists no ConditionalExpression for tag: " + tagName);
    }

    if(conditionalExpression != null)
      conditionalExpression.init(element);

    return conditionalExpression;
  }

  public static Expression createExpression(Element element)
  {
    String tagName = element.getNodeName();
    Expression expression = null;

    if(Value.TAG.equals(tagName))
    {
      expression = new Value();
    }
    else if(SqlQuery.TAG.equals(tagName))
    {
      expression = new SqlQuery();
    }
    else if(NamedSqlQuery.TAG.equals(tagName))
    {
      expression = new NamedSqlQuery();
    }
    else
    {
      throw new AppSystemException("There exists no Expression for tag: " + tagName);
    }

    if(expression != null)
      expression.init(element);

    return expression;
  }
}

/* *****************************************************************
 * Project: common
 * File:    Or.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Or
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Or extends ConditionalExpression
{

  public static final String TAG = "or";

  private List<ConditionalExpression> conditionalExpressions = new ArrayList<ConditionalExpression>();

  public List<ConditionalExpression> getConditionalExpressions()
  {
    return conditionalExpressions;
  }

  public void setConditionalExpressions(List<ConditionalExpression> conditionalExpressions)
  {
    this.conditionalExpressions = conditionalExpressions;
  }

  public boolean addConditionalExpression(ConditionalExpression conditionalExpression)
  {
    return conditionalExpressions.add(conditionalExpression);
  }

  public boolean removeConditionalExpression(ConditionalExpression conditionalExpression)
  {
    return conditionalExpressions.remove(conditionalExpression);
  }

  @Override
  public boolean evaluate()
  {
    boolean result = false;

    for(int i = 0; i < conditionalExpressions.size(); ++i)
    {
      ConditionalExpression expression = conditionalExpressions.get(i);

      result = expression.evaluate();

      if(result)
        break;
    }
    return result;
  }

  public boolean init(Element element)
  {
    List<Element> condElements = XmlJobDependencyUtil.getConditionalExpressionElements(element);

    for(int i = 0; i < condElements.size(); i++)
    {
      Element subElement = (Element)condElements.get(i);

      ConditionalExpression conditionalExpression = ExpressionFactory.createConditionalExpression(subElement);
      if(conditionalExpression != null)
        addConditionalExpression(conditionalExpression);
    }
    return false;
  }
}

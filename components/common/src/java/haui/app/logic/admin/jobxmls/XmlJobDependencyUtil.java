/* *****************************************************************
 * Project: common
 * File:    XmlJobDependencyUtil.java
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XmlJobDependencyUtil
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class XmlJobDependencyUtil
{

  private String[] tags = { And.TAG, Or.TAG, GreaterAs.TAG, GreaterEquals.TAG, LesserAs.TAG, LesserEquals.TAG, Equals.TAG, NotEquals.TAG };

  public static List<Element> getConditionalExpressionElements(Element element)
  {
    List<Element> conditionalExpressionsElements = new ArrayList<Element>();

    NodeList listOfSubElements = element.getChildNodes();

    for(int i = 0; i < listOfSubElements.getLength(); i++)
    {
      Node node = listOfSubElements.item(i);
      if(node.getNodeType() == Node.ELEMENT_NODE)
      {
        Element subElement = (Element)node;

        conditionalExpressionsElements.add(subElement);
      }
    }

    return conditionalExpressionsElements;
  }
}

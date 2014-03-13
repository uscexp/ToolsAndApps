/* *****************************************************************
 * Project: common
 * File:    Blockedjob.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import org.w3c.dom.Element;

import haui.app.logic.admin.jobxmls.XmlElement;

/**
 * Blockedjob
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Blockedjob implements XmlElement
{

  public static final String TAG = "blockedjob";

  private String name;

  public boolean init(Element element)
  {
    if(element.getFirstChild() != null)
    {
      String str = element.getFirstChild().getNodeValue();
      setName(str);
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
}

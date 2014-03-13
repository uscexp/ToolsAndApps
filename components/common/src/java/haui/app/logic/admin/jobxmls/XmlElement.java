/* *****************************************************************
 * Project: common
 * File:    XmlElement.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import org.w3c.dom.Element;

/**
 * XmlElement
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface XmlElement
{
  /**
   * initialize this object with the xml element
   * 
   * @param element xml {@link Element} to parse
   * @return true if init was successfull
   */
  public abstract boolean init(Element element);
}

/* *****************************************************************
 * Project: common
 * File:    Value.java
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
 * Value
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Value extends Expression
{

  public static final String TAG = "value";

  private String type;

  private Object value;

  public void setValue(Object value)
  {
    this.value = value;
  }

  @Override
  public Object getValue()
  {
    return value;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public boolean init(Element element)
  {
    boolean result = true;

    String type = element.getAttribute("type");
    setType(type);
    String value = null;
    if(element.getFirstChild() != null)
      value = element.getFirstChild().getNodeValue();

    if(value != null && type != null)
    {
      Object object = convertToType(type, value);
      setValue(object);
    }
    return result;
  }
}

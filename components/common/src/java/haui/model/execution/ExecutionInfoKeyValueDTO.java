/* *****************************************************************
 * Project: common
 * File:    ExecutionInfoKeyValueDTO.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.execution;

import haui.model.DataTransferObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExecutionInfoKeyValueDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionInfoKeyValueDTO extends DataTransferObject
{
  static final long serialVersionUID = 8259957190114587007L;

  public static final String DATE_TIME_PATTERN = "HH:mm:ss dd.MM.yyyy";

  // pn attributes
  public static final String PN_KEY = "key";
  public static final String PN_VALUE = "value";
  public static final String PN_TYPE = "type";

  // property members
  private String key;
  private String value;
  private String type;

  /**
   * default constructor
   * 
   */
  public ExecutionInfoKeyValueDTO()
  {
    super();
  }

  public ExecutionInfoKeyValueDTO(String key, Object value)
  {
    super();
    setKeyValue(key, value);
  }

  // accessor methods

  /**
   * // TODO insert comment into xml for [PersistentProperty key]
   * 
   * @return the key property
   */
  public final String getKey()
  {
    return key;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty key]
   * 
   */
  public final void setKey(String key)
  {
    this.key = key;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty value]
   * 
   * @return the value property
   */
  public final String getValue()
  {
    return value;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty value]
   * 
   */
  public final void setValue(String value)
  {
    this.value = value;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty type]
   * 
   * @return the type property
   */
  public final String getType()
  {

    if(type == null)
      type = String.class.getName();
    return type;

  }

  /**
   * // TODO insert comment into xml for [PersistentProperty type]
   * 
   */
  public final void setType(String type)
  {
    this.type = type;
  }

  public void setKeyValue(String key, Object value)
  {
    setKey(key);
    if(value instanceof String)
      setValue((String)value);
    else if(value instanceof Date)
    {
      SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_PATTERN);
      String date = format.format((Date)value);
      setValue(date);
    }
    else
      setValue(value.toString());

    setType(value.getClass().getName());
  }
}

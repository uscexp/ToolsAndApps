/* *****************************************************************
 * Project: common
 * File:    Expression.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import haui.model.execution.ExecutionInfoKeyValueDTO;
import haui.util.AppUtil;

/**
 * Expression
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class Expression implements XmlElement
{

  public abstract Object getValue();
  
  public Object convertToType(String type, Object value) {
    return AppUtil.convertToType(type, value, ExecutionInfoKeyValueDTO.DATE_TIME_PATTERN);
  }
}

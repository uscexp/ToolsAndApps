/* *****************************************************************
 * Project: common
 * File:    Equals.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import haui.util.AppUtil;

/**
 * Equals
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Equals extends CompareCondition
{

  public static final String TAG = "equals";

  @Override
  public boolean evaluate() {
    return AppUtil.equals(getExpression().getValue(), getExpressionToCompare().getValue());
  }

  @Override
  public String getTag() {
    return TAG;
  }
}

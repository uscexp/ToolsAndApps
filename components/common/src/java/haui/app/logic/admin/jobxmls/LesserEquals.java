/* *****************************************************************
 * Project: common
 * File:    LesserEquals.java
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
 * LesserEquals
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class LesserEquals extends CompareCondition
{

  public static final String TAG = "lesserEquals";

  @Override
  public boolean evaluate()
  {
    return compareTo(getExpression(), getExpressionToCompare()) < 0
        || AppUtil.equals(getExpression().getValue(), getExpressionToCompare().getValue());
  }

  @Override
  public String getTag()
  {
    return TAG;
  }
}

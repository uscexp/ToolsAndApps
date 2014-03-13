/* *****************************************************************
 * Project: common
 * File:    LesserAs.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

/**
 * LesserAs
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class LesserAs extends CompareCondition
{

  public static final String TAG = "lesserAs";

  @Override
  public boolean evaluate()
  {
    return compareTo(getExpression(), getExpressionToCompare()) < 0;
  }

  @Override
  public String getTag()
  {
    return TAG;
  }
}

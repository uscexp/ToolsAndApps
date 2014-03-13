/* *****************************************************************
 * Project: common
 * File:    ConditionalExpression.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;


/**
 * ConditionalExpression
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class ConditionalExpression implements XmlElement
{
  public abstract boolean evaluate();
}

/* *****************************************************************
 * Project: common
 * File:    ISQLQuery.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.db;

import java.util.List;

/**
 * ISQLQuery
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ISQLQuery
{
  public List<?> list();
  public Object uniqueResult();
}

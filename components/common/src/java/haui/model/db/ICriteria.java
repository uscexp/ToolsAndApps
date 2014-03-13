/* *****************************************************************
 * Project: common
 * File:    ICriteria.java
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
 * ICriteria
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ICriteria
{
  public List<?> list();
  public ICriteria setCacheable(boolean cachable);
  public ICriteria add(ICriterion expression);
  public Object uniqueResult();
  public ICriteria addOrder(Order order);
}

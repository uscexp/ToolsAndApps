/* *****************************************************************
 * Project: common
 * File:    IRestrictions.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.db;

import java.util.Collection;


/**
 * IRestrictions
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class Restrictions
{
  private static Restrictions instance = null;

  protected Restrictions()
  {
  }

  public static final Restrictions instance()
  {
    if(instance == null)
    {
      throw new IllegalStateException("Restrictions.instance must be initialized first!");
    }
    return instance;
  }

  protected static final synchronized void setInstance(Restrictions restrictions)
  {
    if(instance != null)
    {
      throw new IllegalStateException("Restrictions.setInstance() must never be called twice!");
    }
    else
    {
      instance = restrictions;
      return;
    }
  }

  public abstract ICriterion not(ICriterion expression);
  public abstract ISimpleExpression eq(String propertyName, Object value);
  public abstract ISimpleExpression gt(String propertyName, Object value);
  public abstract ISimpleExpression in(String propertyName, Collection<?> values);
  public abstract ISimpleExpression in(String propertyName, Object[] values);
  public abstract ICriterion isNull(String propertyName);
  public abstract ICriterion like(String propertyName, Object value);
}

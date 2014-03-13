/* *****************************************************************
 * Project: common
 * File:    Order.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.db;

/**
 * Order
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class Order
{
  private static Order instance = null;

  protected Order()
  {
  }

  public static final Order instance()
  {
    if(instance == null)
    {
      throw new IllegalStateException("Order.instance must be initialized first!");
    }
    return instance;
  }

  protected static final synchronized void setInstance(Order order)
  {
    if(instance != null)
    {
      throw new IllegalStateException("Order.setInstance() must never be called twice!");
    }
    else
    {
      instance = order;
      return;
    }
  }
  
  public abstract Order asc(String propertyName);
  public abstract Order desc(String propertyName);
}

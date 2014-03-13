/* *****************************************************************
 * Project: common
 * File:    AbstractMultiApplicationSingleton.java
 * 
 * Creation:     09.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.core;

import haui.common.id.STIDGenerator;
import haui.util.GlobalApplicationContext;

/**
 * AbstractMultiApplicationSingleton
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractMultiApplicationSingleton implements MultiApplicationSingleton
{

  private static final String singletonId = STIDGenerator.getStandardIdentifier();

  protected static MultiApplicationSingleton instance()
  {
    return (MultiApplicationSingleton)GlobalApplicationContext.instance().getSingletonInstance(singletonId);
  }
  
  /* (non-Javadoc)
   * @see haui.model.core.MultiApplicationSingleton#setInstance(haui.model.core.MultiApplicationSingleton)
   */
  public static void setInstance(MultiApplicationSingleton instance)
  {
    if(GlobalApplicationContext.instance().getSingletonInstance(singletonId) != null)
    {
      throw new IllegalStateException("AbstractMultiApplicationSingleton.setInstance() should never be called twice!");
    }
    else
    {
      GlobalApplicationContext.instance().setSingletonInstance(singletonId, instance);
      return;
    }
  }
}

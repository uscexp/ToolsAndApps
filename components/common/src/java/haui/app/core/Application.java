/* *****************************************************************
 * Project: common
 * File:    Application.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.core;

import haui.app.execution.ExecutionManager;
import haui.app.security.ICurrentUserMapper;
import haui.app.threading.ThreadingComponent;
import haui.model.core.AbstractMultiApplicationSingleton;

/**
 * Application
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class Application extends AbstractMultiApplicationSingleton
{

//  private static Application instance = null;

  protected Application()
  {
  }

  public static final Application instance()
  {
    if(AbstractMultiApplicationSingleton.instance() == null)
    {
      throw new IllegalStateException("Application.instance must be initialized first!");
    }
      return (Application)AbstractMultiApplicationSingleton.instance();
  }

  protected static final synchronized void setInstance(Application application)
  {
      if(AbstractMultiApplicationSingleton.instance() != null)
      {
          throw new IllegalStateException("Application.setInstance() should never be called twice!");
      } else
      {
          AbstractMultiApplicationSingleton.setInstance(application);
          return;
      }
  }

  public abstract void init();

  public abstract ThreadingComponent getThreadingComponent();

  public abstract ExecutionManager getExecutionManager();

  public abstract ICurrentUserMapper getCurrentUserMapper();
}

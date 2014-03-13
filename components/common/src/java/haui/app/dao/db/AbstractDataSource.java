/* *****************************************************************
 * Project: common
 * File:    AbstractDataSource.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.db;

import haui.app.service.command.ServiceContext;
import haui.model.db.ISession;
import haui.util.GlobalApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * AbstractDataSource
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AbstractDataSource implements IDataSource
{
  private static AbstractDataSource instance = null;

  protected static final String PROPERTY_KEY_MIN_SESSION_SIZE_FOR_CLEAR = "app.session.clear.min.size";

  protected static final Map configurations = Collections.synchronizedMap(new HashMap());
  protected static final Map sessionFactories = Collections.synchronizedMap(new HashMap());
  protected static final Set dataSources = Collections.synchronizedSet(new HashSet());

  protected final String dataSourceId;
  protected final String proprtiesFile;
  protected final String configurationFile;

  protected AbstractDataSource(String dataSourceId, String proprtiesFile, String configurationFile)
  {
    dataSources.add(dataSourceId);

    this.configurationFile = configurationFile;
    this.proprtiesFile = proprtiesFile;
    this.dataSourceId = dataSourceId;
  }

  public static final AbstractDataSource instance()
  {
    if(instance == null)
    {
      throw new IllegalStateException("AbstractDataSource.instance must be initialized first!");
    }
    return instance;
  }

  protected static final synchronized void setInstance(AbstractDataSource abstractDataSource)
  {
    if(instance != null)
    {
      throw new IllegalStateException("AbstractDataSource.setInstance() must never be called twice!");
    }
    else
    {
      instance = abstractDataSource;
      return;
    }
  }

  public void clearSessionIfRequired()
  {
    ISession session = (ISession)ServiceContext.get(dataSourceId);
    if(session != null)
    {
      if(estimateSessionSize(session) > minimalEstimatedSessionSizeForClear())
      {
        session.flush();
        session.clear();
      }
    }
  }

  public static int estimateSessionSize(ISession session)
  {
    // FIXME
//    if(session != null && session instanceof SessionImpl)
//    {
//      SessionImpl sessionImpl = (SessionImpl)session;
//      int totalObjects = sessionImpl.getPersistenceContext().getEntityEntries().size();
//      int totalCollections = sessionImpl.getPersistenceContext().getCollectionEntries().size();
//      return totalObjects + totalCollections;
//    }
    return 0;
  }

  private int minimalEstimatedSessionSizeForClear()
  {
    return GlobalApplicationContext.instance().getApplicationProperties().getProperty(PROPERTY_KEY_MIN_SESSION_SIZE_FOR_CLEAR, 500);
  }

  public Collection<IDataSource> getDataSources()
  {
    return Collections.synchronizedSet(dataSources);
  }

  public static synchronized void closeSession(String dataSourceId)
  {
    ISession session = (ISession)ServiceContext.get(dataSourceId);
    if(session != null)
    {
      session.close();
      ServiceContext.remove(dataSourceId);
    }
  }

}

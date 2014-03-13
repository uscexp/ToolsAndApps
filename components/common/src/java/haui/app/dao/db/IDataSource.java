/* *****************************************************************
 * Project: common
 * File:    IDataSource.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.db;

import java.util.Collection;

import haui.model.db.IConfiguration;
import haui.model.db.ISession;
import haui.model.db.ISessionFactory;

/**
 * IDataSource
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface IDataSource
{
  public ISession createSession(boolean interceptor);

  public ISession getSession();

  public ISession getSession(boolean interceptor);
  
  public boolean sessionExists();

  public void closeSession();

  public void flush();

  public String getSchemaPrefix();

  // public SessionFactory createSessionFactory(String configurationFile, String propertyFile,
  // String defaultPropertyFile);
  public ISessionFactory getSessionFactory();

  public IConfiguration getConfiguration();

  public String getDataSourceId();

  public void loadLazy(Object objectForLoad);
}

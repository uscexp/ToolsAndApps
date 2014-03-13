/* *****************************************************************
 * Project: common
 * File:    OdbDataSource.java
 * 
 * Creation:     05.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.db.odb;

import haui.app.dao.db.AbstractDataSource;
import haui.model.db.IConfiguration;
import haui.model.db.ISession;
import haui.model.db.ISessionFactory;

/**
 * DataSource for NeoDatis object database
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class OdbDataSource extends AbstractDataSource
{
  public OdbDataSource(String dataSourceId, String proprtiesFile, String configurationFile)
  {
    super(dataSourceId, proprtiesFile, configurationFile);
  }
  
  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#closeSession()
   */
  public void closeSession()
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#createSession(boolean)
   */
  public ISession createSession(boolean interceptor)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#flush()
   */
  public void flush()
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#getConfiguration()
   */
  public IConfiguration getConfiguration()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#getDataSourceId()
   */
  public String getDataSourceId()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#getSchemaPrefix()
   */
  public String getSchemaPrefix()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#getSession()
   */
  public ISession getSession()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#getSession(boolean)
   */
  public ISession getSession(boolean interceptor)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#getSessionFactory()
   */
  public ISessionFactory getSessionFactory()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#loadLazy(java.lang.Object)
   */
  public void loadLazy(Object objectForLoad)
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see haui.app.dao.db.IDataSource#sessionExists()
   */
  public boolean sessionExists()
  {
    // TODO Auto-generated method stub
    return false;
  }

}

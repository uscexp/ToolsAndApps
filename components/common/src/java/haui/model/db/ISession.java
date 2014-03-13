/* *****************************************************************
 * Project: common
 * File:    ISession.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.db;

import haui.model.DataTransferObject;

import java.io.Serializable;

import javax.transaction.UserTransaction;

/**
 * ISession
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ISession
{
  public boolean isOpen();
  public void save(DataTransferObject dto);
  public void update(DataTransferObject dto);
  public void saveOrUpdate(DataTransferObject dto);
  public void delete(DataTransferObject dto);
  public ICriteria createCriteria(Class clazz);
  public Object get(Class clazz, Serializable id);
  public ISQLQuery createSQLQuery(String queryString);
  public void flush();
  public void clear();
  public void close();
}

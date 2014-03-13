/* *****************************************************************
 * Project: common
 * File:    OdbSession.java
 * 
 * Creation:     05.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.db.odb;

import haui.model.DataTransferObject;
import haui.model.db.ICriteria;
import haui.model.db.ISQLQuery;
import haui.model.db.ISession;

import java.io.Serializable;

import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IOSocketParameter;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.server.layers.layer3.ServerFileParameter;
import org.neodatis.odb.core.transaction.ITransaction;
import org.neodatis.odb.impl.core.layers.layer3.engine.LocalStorageEngine;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ClientStorageEngine;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ServerStorageEngine;
import org.neodatis.odb.impl.core.server.transaction.ServerSession;
import org.neodatis.odb.impl.core.transaction.ClientSession;
import org.neodatis.odb.impl.core.transaction.LocalSession;

/**
 * OdbSession
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class OdbSession implements ISession
{
  private static final int LOCAL = 1;
  private static final int SERVER = 2;
  private static final int CLIENT = 3;

  private int mode = LOCAL;
  private org.neodatis.odb.core.transaction.ISession session;
  private String sessionId;

  /**
   * constructor
   * 
   * @param baseIdentification
   * @param sessionId
   * @param user
   * @param pwd
   */
  public OdbSession(IBaseIdentification baseIdentification, String sessionId)
  {
    super();
    this.sessionId = sessionId;
    if(baseIdentification instanceof ServerFileParameter)
    {
      this.mode = SERVER;
      session = new ServerSession(new ServerStorageEngine(baseIdentification), sessionId);
    }
    else if(baseIdentification instanceof IOFileParameter)
    {
      this.mode = LOCAL;
      session = new LocalSession(new LocalStorageEngine(baseIdentification));
    }
    else if(baseIdentification instanceof IOSocketParameter)
    {
      this.mode = CLIENT;
      session = new ClientSession(new ClientStorageEngine((IOSocketParameter)baseIdentification));
    }
  }

  /**
   * @return the session
   */
  public org.neodatis.odb.core.transaction.ISession getOrgSession()
  {
    return session;
  }

  /**
   * @return the mode
   */
  public int getMode()
  {
    return mode;
  }

  /**
   * @return the sessionId
   */
  public String getSessionId()
  {
    return sessionId;
  }

  public ITransaction beginTransaction()
  {
    return session.getTransaction();
  }

  public void clear()
  {
    session.clear();
  }

  public void close()
  {
    session.close();
  }

  public ICriteria createCriteria(Class clazz)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ISQLQuery createSQLQuery(String queryString)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public void delete(DataTransferObject dto)
  {
    session.getStorageEngine().delete(dto, false);
  }

  public void flush()
  {
    // TODO Auto-generated method stub
  }

  public Object get(Class clazz, Serializable id)
  {
    IQuery query = new CriteriaQuery(clazz, Where.equal(DataTransferObject.PN_STID, id));
    Objects objects = session.getStorageEngine().getObjects(query, true, -1, -1);
    Object object = null;
    if(objects.hasNext())
      object = objects.next();
    return object;
  }

  public boolean isOpen()
  {
    return !session.getStorageEngine().isClosed();
  }

  public void save(DataTransferObject dto)
  {
    session.getStorageEngine().store(dto);
  }

  public void saveOrUpdate(DataTransferObject dto)
  {
    session.getStorageEngine().store(dto);
  }

  public void update(DataTransferObject dto)
  {
    session.getStorageEngine().store(dto);
  }
}

/* *****************************************************************
 * Project: common
 * File:    AppBaseDAO.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao;

import haui.app.dao.db.AbstractDataSource;
import haui.model.DataTransferObject;
import haui.model.db.ICriteria;
import haui.model.db.ISession;
import haui.model.db.Restrictions;
import haui.util.AppUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * AppBaseDAO
 * 
 * FIXME implement when adding a DB-layer
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AppBaseDAO
{

  public AppBaseDAO()
  {
    super();
  }

  /**
   * split sets to sets with maxsize 'LIMIT' because there is a limit in SQL IN (...) statements
   * 
   * @param uuidCollection
   * @return array of smaller sets
   */
  protected Set[] splitSet(Collection uuidCollection, int limit)
  {
    int count = 0;
    if(uuidCollection.size() % limit == 0)
      count = (uuidCollection.size() / limit);
    else
      count = (uuidCollection.size() / limit) + 1;
    Set[] sets = new HashSet[count];

    Iterator it = uuidCollection.iterator();

    for(int i = 0; i < count; ++i)
    {
      sets[i] = new HashSet();

      for(int ii = 0; it.hasNext() && ii < limit; ++ii)
      {
        String uuid = (String)it.next();

        sets[i].add(uuid);
      }
    }
    return sets;
  }

  /**
   * Retreives for the given entity the persistent object from data storage.
   * 
   * @param entity to be retreived.
   * @return the persistent entity or <code>null</code> if there is no such persistent entity.
   */
  public final DataTransferObject reload(DataTransferObject entity)
  {
    if(null == entity)
      return null;
    return findByStid(entity.getClass(), entity.getStid());
  }

  /**
   * default implementation to save the spezified object in the database.
   * 
   * @param entity the entity to be saved.
   * @return the saven object
   */
  public final DataTransferObject save(DataTransferObject entity)
  {
    if(null == entity)
      return null;
    ISession session = getSession();
    session.save(entity);
    return entity;
  }

  /**
   * updates the spezified entity in the database.
   * 
   * @param entity to be updated.
   */
  public final void update(DataTransferObject entity)
  {
    if(null == entity)
      return;
    ISession session = getSession();
    session.update(entity);
  }

  /**
   * Save or update the spezified entity object in the database.
   * 
   * @param entity the entity to be saved.
   * @return the saved object
   */
  public final DataTransferObject saveOrUpdate(DataTransferObject entity)
  {
    if(null == entity)
    {
      return null;
    }
    ISession session = getSession();
    session.saveOrUpdate(entity);
    return entity;
  }

  /**
   * removes the spezified object from the database. This implementation is with a flush() after the
   * operation.
   * 
   * @param entity to be removed form the database.
   */
  public final void remove(DataTransferObject entity)
  {
    if(null == entity || entity.getStid() == null)
      return;
    ISession session = getSession();
    session.delete(entity);
  }

//  public final void evict(FundamentalEntity entity) throws HibernateException
//  {
//    if(null == entity || entity.getUuid() == null)
//      return;
//    Session session = getSession();
//    session.evict(entity);
//  }

  /**
   * Find an list of {@link IVersion} by stids
   * 
   * @param clazz the version class to search for
   * @param stids the stids
   * @return the verions found
   */
  public List findByUuids(Class clazz, String[] stids)
  {
    if(null == clazz || stids == null || stids.length == 0)
      return Collections.EMPTY_LIST;

    int inMaxSize = 1000;

    List ids = AppUtil.toList(stids);
    List entities = new ArrayList(stids.length);
    ISession session = getSession();
    while(!ids.isEmpty())
    {
      int size = ids.size() > inMaxSize ? inMaxSize : ids.size();
      List inStids = AppUtil.subList(ids, 0, size);
      ICriteria criteria = session.createCriteria(clazz);
      criteria.add(Restrictions.instance().in(DataTransferObject.PN_STID, inStids));
      entities.addAll(criteria.list());

      ids.removeAll(inStids);
    }

    return entities;
  }

//  public final FundamentalEntity findByIdentifier(IBusinessEntityIdentifier identifier) throws HibernateException
//  {
//    if(identifier == null || identifier.getIBusinessEntityType() == null)
//      return null;
//    return findByUuid(identifier.getIBusinessEntityType().getVersionClass(), identifier.getUuid());
//  }

//  public final Set findByIdentifiers(IBusinessEntityIdentifier[] identifiers) throws HibernateException
//  {
//    if(identifiers == null || identifiers.length == 0)
//      return Collections.EMPTY_SET;
//
//    // some pre-sorting as we cannot be sure that all the identifiers are of the same type (class)
//    MultiValueMap map = new MultiValueSetMap();
//    for(int n = 0; n < identifiers.length; n++)
//    {
//      IBusinessEntityIdentifier identifier = identifiers[n];
//      if(identifier == null || identifier.getIBusinessEntityType() == null)
//        continue;
//      map.put(identifier.getEntityClass(), identifier.getUuid());
//    }
//
//    Set entities = new HashSet(identifiers.length);
//    for(Iterator it = map.keys(); it.hasNext();)
//    {
//      Class clazz = (Class)it.next();
//      Collection ids = map.get(clazz);
//      entities.addAll(findByUuids(clazz, (String[])ids.toArray(new String[ids.size()])));
//    }
//
//    return entities;
//  }

  /**
   * Retreive for the given uuid an object from the persitent data storage.
   * 
   * @param clazz the type of the object
   * @param uuid the identifier of the object
   * @return the object or <code>null</code> if there is no such object
   * @throws HibernateException is something goes wrong
   */
//  public final FundamentalEntity findByUuid(Class clazz, String uuid) throws HibernateException
//  {
//    if(null == clazz || null == uuid)
//      return null;
//    Session session = getSession();
//    return (FundamentalEntity)session.get(clazz, uuid);
//  }

  /**
   * Retreive for the given stid an object.
   * 
   * @param clazz the type of the object
   * @param stid the identifier of the object
   * @return the object or <code>null</code> if there is no such object
   */
  public final DataTransferObject findByStid(Class clazz, String stid)
  {
    if(null == clazz || null == stid)
      return null;
      ISession session = getSession();
      return (DataTransferObject)session.get(clazz, stid);
  }

  /**
   * delivers all Objects of the type in the paramenter
   * 
   * @return a List of all Objects of the spezified type
   */
  protected List findAll(Class clazz)
  {
    if(null == clazz)
      return new ArrayList(0);
    ISession session = getSession();
    return session.createCriteria(clazz).list();
  }

  /**
   * delivers an object of the specified type and attribute identifier.
   * 
   * @param clazz the type of the persitend object
   * @param attribute name of the identifier
   * @param code the identifier of the object
   * @return the object or <code>null</code> if there is no such object
   */
  protected final DataTransferObject findObject(Class clazz, String attribute, String code)
  {
    if(null == code || "".equals(code.trim()))
      return null;
    ISession session = getSession();
    ICriteria crit = session.createCriteria(clazz);
    crit.add(Restrictions.instance().eq(attribute, code));
    return (DataTransferObject)crit.uniqueResult();
  }

  /**
   * delivers a hibernate session associated with the actual thread. If there is no session
   * associated to this thread a new one will be created.
   * 
   * @return a hibernate session
   */
  public final ISession getSession()
  {
    return AbstractDataSource.instance().getSession();
  }

  /**
   * Workaround, parameter replacement does only work in where clauses. Schema name is replaced by
   * own regex in a preprocessing. Do it before invoking setParameter(...)
   * 
   * @param queryString
   */
  protected String schemaReplacement(String queryString)
  {
    String schemaPrefix = AbstractDataSource.instance().getSchemaPrefix();
    if(schemaPrefix.length() != 0)
    {
      return queryString.replaceAll("\\s\\*\\.", " " + schemaPrefix);
    }
    else
    {
      return queryString.replaceAll("\\s\\*\\.", " ");
    }
  }
}

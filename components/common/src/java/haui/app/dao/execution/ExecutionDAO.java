/* *****************************************************************
 * Project: common
 * File:    ExecutionDAO.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.execution;

import haui.app.dao.AppBaseDAO;
import haui.app.execution.ExecutionManager;
import haui.model.db.ICriteria;
import haui.model.db.Order;
import haui.model.db.Restrictions;
import haui.model.execution.ExecutionIdentifier;
import haui.model.execution.ExecutionInfoDTO;

import java.util.LinkedList;
import java.util.List;

/**
 * ExecutionDAO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionDAO extends AppBaseDAO
{

  /**
   * Searches an {@link ExecutionInfoDTO} by its identifier.
   * 
   * @param executionIdentifier
   * @return the {@link ExecutionInfoDTO} with the given identifier or <code>
   * null</code>
   */
  public ExecutionInfoDTO findExecutionByIdentifier(ExecutionIdentifier executionIdentifier)
  {
    ICriteria criteria = getSession().createCriteria(ExecutionInfoDTO.class);
    criteria.add(Restrictions.instance().eq(ExecutionInfoDTO.PN_EXECUTION_ID, executionIdentifier.getId()));
    return (ExecutionInfoDTO)criteria.uniqueResult();
  }

  /**
   * Searches an {@link ExecutionInfoDTO} by its identifier id. it is mainly used by
   * executionlogfile.jsp
   * 
   * @param executionIdentifier.getId()
   * @return the {@link ExecutionInfoDTO} with the given identifier or <code>
   * null</code>
   */
  public ExecutionInfoDTO findExecutionByIdentifierId(String executionIdentifierId)
  {
    ICriteria criteria = getSession().createCriteria(ExecutionInfoDTO.class);
    criteria.add(Restrictions.instance().eq(ExecutionInfoDTO.PN_EXECUTION_ID, executionIdentifierId));
    return (ExecutionInfoDTO)criteria.uniqueResult();
  }

  /**
   * Looks for the latest {@link ExecutionInfoDTO}
   * 
   * @param executableIdentifier
   * @return {@link ExecutionInfoDTO}
   */
  public ExecutionInfoDTO findLatestExecutionByExecutableIdentifier(String executableIdentifier)
  {
    ICriteria criteria = getSession().createCriteria(ExecutionInfoDTO.class);
    criteria.add(Restrictions.instance().eq(ExecutionInfoDTO.PN_EXECUTABLE_ID, executableIdentifier));
    criteria.addOrder(Order.instance().asc(ExecutionInfoDTO.PN_DATE_TERMINATED));
    List list = criteria.list();

    if(list.size() > 0)
      return (ExecutionInfoDTO)list.get(0);
    return null;
  }

  /**
   * Searches all {@link ExecutionInfoDTO}s
   * 
   * @return all {@link ExecutionInfoDTO}s
   */
  public List findAll()
  {
    return findAll(ExecutionInfoDTO.class);
  }

  /**
   * Finds all {@link ExecutionInfoDTO} with one of the given executable ids
   * 
   * @param executableId
   * @return all {@link ExecutionInfoDTO} that are executions of one of the given executable ids
   */
  public ExecutionInfoDTO[] findExecutionsByExecutableIdentifier(String[] executableIds)
  {
    ICriteria criteria = getSession().createCriteria(ExecutionInfoDTO.class);
    criteria.add(Restrictions.instance().in(ExecutionInfoDTO.PN_EXECUTABLE_ID, executableIds));
    criteria.addOrder(Order.instance().desc(ExecutionInfoDTO.PN_DATE_STARTED));
    List result = criteria.list();
    return (ExecutionInfoDTO[])result.toArray(new ExecutionInfoDTO[result.size()]);
  }

  /**
   * Finds the latest {@link ExecutionInfoDTO} belonging to given executableIdentifiers
   * 
   * @param executableIdentifiers
   * @return the latest (running or terninated) {@link ExecutionInfoDTO}s of given
   *         executableIdentifiers if one {@link ExecutionInfoDTO} does not exist for one
   *         executableIdentifiers the element value is null.
   */
  public ExecutionInfoDTO[] findLatestExecutionByExecutableIdentifiers(String[] executableIdentifiers)
  {
    List executions = new LinkedList();

    ExecutionInfoDTO[] allRunningExecutions = ExecutionManager.instance().findAllRunningExecutions();

    for(int k = 0; k < executableIdentifiers.length; k++)
    {
      boolean found = false;
      for(int i = 0; i < allRunningExecutions.length; i++)
      {
        if(executableIdentifiers[k].equals(allRunningExecutions[i].getExecutableId()))
        {
          executions.add(allRunningExecutions[i]);
          found = true;
        }
      }

      // then in the db if not running, null if no execution exists
      if(!found)
      {
        ExecutionInfoDTO[] resultExecutions = findExecutionsByExecutableIdentifier(new String[] { executableIdentifiers[k] });
        if(resultExecutions.length <= 0)
          executions.add(null);
        else
          executions.add(resultExecutions[0]);
      }
    }

    return (ExecutionInfoDTO[])executions.toArray(new ExecutionInfoDTO[executableIdentifiers.length]);
  }
}

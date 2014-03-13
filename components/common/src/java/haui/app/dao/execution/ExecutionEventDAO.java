/* *****************************************************************
 * Project: common
 * File:    ExecutionEventDAO.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.execution;

import haui.app.dao.AppBaseDAO;
import haui.app.execution.AppExecutionManager;
import haui.model.db.ICriteria;
import haui.model.db.Order;
import haui.model.db.Restrictions;
import haui.model.execution.AbstractEventDTO;
import haui.model.execution.ExecutionInfoDTO;

import java.util.List;

import haui.model.execution.ExecutionEventDTO;

/**
 * ExecutionEventDAO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionEventDAO extends AppBaseDAO
{

  public ExecutionEventDAO()
  {
    super();
  }

  public List findAll()
  {
    return findAll(AbstractEventDTO.class);
  }

  // public AbstractEventDTO findByUuid(String uuid) {
  // AbstractEventDTO abstractEvent = (AbstractEventDTO)findByUuid(GeneralEventDTO.class, uuid);
  //    
  // if(abstractEvent == null)
  // abstractEvent = (AbstractEventDTO)findByUuid(ImportEventDTO.class, uuid);
  // return abstractEvent;
  // }

  public List findAllOpenExecutionEvents()
  {

    ICriteria criteria = getSession().createCriteria(AbstractEventDTO.class);
    criteria.add(Restrictions.instance().isNull(AbstractEventDTO.PN_DONE_TIME_STAMP));

    return criteria.list();
  }

  /**
   * find latest {@link ExecutionInfoDTO} with a given {@link AbstractEventDTO}
   * 
   * @param executionEvent {@link ExecutionEventDTO}
   * @param onlyRunningExecutables search only for running executables (exclude DB query)
   * 
   * @return {@link ExecutionEventDTO}
   */
  public ExecutionInfoDTO findLatestExecutionInfoByExecutionEvent(AbstractEventDTO executionEvent, boolean onlyRunningExecutables)
  {
    ExecutionInfoDTO executionInfo = null;

    if(!onlyRunningExecutables)
    {
      // search for terminated executables
      ICriteria criteria = getSession().createCriteria(ExecutionInfoDTO.class);
      criteria.add(Restrictions.instance().like(ExecutionInfoDTO.PN_EXECUTABLE_ID, "%" + executionEvent.getStid()));
      criteria.addOrder(Order.instance().desc(ExecutionInfoDTO.PN_DATE_TERMINATED));

      List result = criteria.list();
      if(result != null && result.size() > 0)
      {
        executionInfo = (ExecutionInfoDTO)result.get(0);
      }
    }

    if(executionInfo == null)
    {
      // search for running executables
      ExecutionInfoDTO[] executionInfos = AppExecutionManager.getInstance().findExecutionsBySubstringExecutableIdentifier(executionEvent.getStid());
      if(executionInfos != null)
      {
        for(int i = 0; i < executionInfos.length; ++i)
        {
          ExecutionInfoDTO info = executionInfos[i];
          if(executionInfo == null)
            executionInfo = info;
          else if(executionInfo.getDateStarted().compareTo(info.getDateStarted()) > 0)
          {
            executionInfo = info;
          }
        }
      }
    }

    return executionInfo;
  }
}

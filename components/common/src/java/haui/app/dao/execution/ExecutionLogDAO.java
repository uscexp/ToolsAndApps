/* *****************************************************************
 * Project: common
 * File:    ExecutionLogDAO.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.dao.execution;

import haui.app.dao.AppBaseDAO;
import haui.model.db.ICriteria;
import haui.model.db.Restrictions;
import haui.model.execution.ExecutionInfoDTO;
import haui.model.execution.ExecutionLogDTO;

import java.util.List;

/**
 * ExecutionLogDAO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExecutionLogDAO extends AppBaseDAO
{

  /**
   * Searches an {@link ExecutionInfoDTO} by its identifier.
   * 
   * @param executionIdentifier
   * @return the {@link ExecutionInfoDTO} with the given identifier or <code>
   * null</code>
   */
  public ExecutionLogDTO findExecutionLogByIdentifier(ExecutionInfoDTO info)
  {
    assert info != null : "info is null";

    ICriteria criteria = getSession().createCriteria(ExecutionLogDTO.class);
    criteria.add(Restrictions.instance().eq(ExecutionLogDTO.PN_EXECUTION_INFO, info));

    return (ExecutionLogDTO)criteria.uniqueResult();
  }

  public List findAllExecutionLog()
  {
    return findAll(ExecutionLogDTO.class);
  }
}

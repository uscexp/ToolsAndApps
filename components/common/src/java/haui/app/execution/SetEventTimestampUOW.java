/* *****************************************************************
 * Project: common
 * File:    SetEventTimestampUOW.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.exception.AppLogicException;
import haui.app.dao.execution.ExecutionEventDAO;
import haui.model.execution.AbstractEventDTO;
import haui.util.DateUtil;

/**
 * SetEventTimestampUOW
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class SetEventTimestampUOW extends AppUnitOfWork
{
  private AbstractEventDTO executionEvent;

  public SetEventTimestampUOW(AbstractEventDTO executionEvent)
  {
    super();
    this.executionEvent = executionEvent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.arte.app.execution.ArteUnitOfWork#run(com.ubs.arte.app.execution.ExecutionContext)
   */
  @Override
  public void run(ExecutionContext arg0) throws AppLogicException
  {
    ExecutionEventDAO executionEventDAO = new ExecutionEventDAO();
    executionEvent = (AbstractEventDTO)executionEventDAO.findByStid(executionEvent.getClass(), executionEvent.getStid());
    if(executionEvent.getEventTimeStamp() == null)
    {
      executionEvent.setEventTimeStamp(DateUtil.getCurrentTimeStamp());
    }
    executionEvent.setDoneTimeStamp(DateUtil.getCurrentTimeStamp());
    executionEventDAO.saveOrUpdate(executionEvent);
  }
}

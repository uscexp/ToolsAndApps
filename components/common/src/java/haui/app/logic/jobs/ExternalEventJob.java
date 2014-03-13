/* *****************************************************************
 * Project: common
 * File:    ExternalEventJob.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.jobs;

import haui.app.execution.AppJob;
import haui.app.execution.ExecutionContext;
import haui.app.execution.ExternalExecutableTask;
import haui.exception.AppLogicException;
import haui.model.admin.AdminJobDTO;
import haui.model.admin.ExternalEventJobDTO;

/**
 * ExternalEventJob
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExternalEventJob extends AppJob
{
  private ExternalExecutableTask externalExecutableTask;

  public ExternalEventJob(ExternalExecutableTask externalExecutableTask)
  {
    super();
    this.externalExecutableTask = externalExecutableTask;
    AdminJobDTO adminJob = new ExternalEventJobDTO(externalExecutableTask.getExecutionEvent());
    setAdminJob(adminJob);
    checkForSelfBlockingJob();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.arte.app.execution.ArteJob#run(com.ubs.arte.app.execution.ExecutionContext)
   */
  public void run(ExecutionContext executioncontext) throws AppLogicException
  {
    executioncontext.execute(externalExecutableTask, 1);
  }
}

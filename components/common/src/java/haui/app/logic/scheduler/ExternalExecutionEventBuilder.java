/* *****************************************************************
 * Project: common
 * File:    ExternalExecutionEventBuilder.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler;

import java.lang.reflect.Constructor;

import haui.app.execution.AppExecutable;
import haui.app.execution.AppJob;
import haui.app.execution.ExternalExecutableTask;
import haui.app.logic.jobs.ExternalEventJob;
import haui.model.admin.AdminJobDTO;
import haui.model.admin.ExternalEventJobDTO;
import haui.model.execution.AbstractEventDTO;
import haui.util.ClassUtil;

/**
 * ExternalExecutionEventBuilder
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExternalExecutionEventBuilder
{
  public static ISchedulerEvent createSchedulerEventForExternalEvent(AbstractEventDTO executionEvent) throws Exception
  {
    assert executionEvent != null: "executionEvent is null";

    // create instance of AppExecutable
    Class eventClass = ClassUtil.loadClass(executionEvent.getEventType());
    AppJob job = new ExternalEventJob(new ExternalExecutableTask(executionEvent));
    AdminJobDTO adminJob = new ExternalEventJobDTO(executionEvent);

    job.setAdminJob(adminJob);

    // create instance of ISchedulerEvent
    Class[] paramTypes = new Class[2];
    paramTypes[0] = AppExecutable.class;
    paramTypes[1] = AbstractEventDTO.class;
    Constructor constructor = eventClass.getDeclaredConstructor(paramTypes);

    Object[] params = new Object[2];
    params[0] = job;
    params[1] = executionEvent;
    ISchedulerEvent schedulerEvent = (ISchedulerEvent)constructor.newInstance(params);

    return schedulerEvent;
  }
}

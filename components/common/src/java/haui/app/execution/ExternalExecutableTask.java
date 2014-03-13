/* *****************************************************************
 * Project: common
 * File:    ExternalExecutableTask.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.exception.AppLogicException;

import java.lang.reflect.Constructor;

import haui.model.execution.AbstractEventDTO;
import haui.util.ClassUtil;

/**
 * ExternalExecutableTask
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ExternalExecutableTask extends AppSequencialTask
{
  private AbstractEventDTO executionEvent;
  private AppExecutable realExecutable;
  private AppExecutable[] executables;

  public ExternalExecutableTask(AbstractEventDTO executionEvent)
  {
    super();
    this.executionEvent = executionEvent;
  }

  public AbstractEventDTO getExecutionEvent()
  {
    return executionEvent;
  }

  public void init(ExecutionContext executioncontext) throws AppLogicException
  {
    super.init(executioncontext);
    try
    {
      Class executableClass = ClassUtil.loadClass(executionEvent.getExecutionClass());

      Class[] paramTypes = new Class[1];
      paramTypes[0] = AbstractEventDTO.class;
      Constructor constructor = executableClass.getDeclaredConstructor(paramTypes);

      Object[] params = new Object[1];
      params[0] = executionEvent;
      realExecutable = (AppExecutable)constructor.newInstance(params);

      executables = new AppExecutable[3];
      executables[0] = new SetEventTimestampUOW(executionEvent);
      executables[1] = realExecutable;
      executables[2] = new SetDoneTimestampUOW(executionEvent);
    }
    catch(Exception e)
    {
      throw new AppLogicException("Error running ExternalExecutableTask!", e);
    }
  }

  public AppExecutable[] getRunnables() throws AppLogicException
  {
    return executables;
  }

  public String getIdentifier()
  {
    String id = ClassUtil.getShortName(ExternalExecutableTask.class);
    id += executionEvent.getStid();
    return id;
  }
}

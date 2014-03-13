/* *****************************************************************
 * Project: common
 * File:    TriggerPostponedExecutionTask.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.service.command;

import haui.app.execution.AppExecutable;
import haui.app.execution.AppParallelTask;
import haui.exception.AppLogicException;

import java.util.List;

/**
 * TriggerPostponedExecutionTask
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class TriggerPostponedExecutionTask extends AppParallelTask
{
  private List postponedExecutions;

  public TriggerPostponedExecutionTask(List postponedExecutions)
  {
    this.postponedExecutions = postponedExecutions;
  }

  public AppExecutable[] getRunnables() throws AppLogicException
  {
    return (AppExecutable[])postponedExecutions.toArray(new AppExecutable[postponedExecutions.size()]);
  }
}

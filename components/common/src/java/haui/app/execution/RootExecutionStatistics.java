/* *****************************************************************
 * Project: common
 * File:    RootExecutionStatistics.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.util.SimpleStatisticCounter;
import haui.app.execution.ExecutionStatistics;

/**
 * RootExecutionStatistics
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class RootExecutionStatistics extends SimpleStatisticCounter implements ExecutionStatistics
{
  private Execution execution;

  public RootExecutionStatistics(Execution execution)
  {
    super();
    this.execution = execution;
  }

  Execution getExecution()
  {
    return execution;
  }

  public synchronized int increase(String subStatistic, String counter, int amount)
  {
    int counterValue = super.increase(counter, amount);

    getExecution().touched();

    return counterValue;
  }
}

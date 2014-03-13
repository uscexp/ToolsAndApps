/* *****************************************************************
 * Project: common
 * File:    NestedProgressMonitor.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.execution.ProgressMonitor;

/**
 * {@link ProgressMonitor} for nested executions.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class NestedProgressMonitor implements ProgressMonitor
{
  private ProgressMonitor parent;
  private double contribution;
  private double internalProgress = 0;
  private boolean done = false;

  public NestedProgressMonitor(ProgressMonitor parent, double contribution)
  {
    this.parent = parent;
    this.contribution = contribution;
  }

  public void done()
  {
    if(!done)
    {
      double remainingWork = 1 - internalProgress;
      worked(remainingWork);
      done = true;
    }
  }

  public void worked(double work)
  {
    double progress = internalProgress;
    if(progress < 0)
      progress = 0;

    progress += work;

    if(progress < 0)
      progress = 0;
    if(progress > 1)
      progress = 1;

    double actualWork = progress - internalProgress;
    double parentWork = actualWork * contribution;
    parent.worked(parentWork);

    internalProgress = progress;
  }
}

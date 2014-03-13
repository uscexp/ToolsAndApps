/* *****************************************************************
 * Project: common
 * File:    ProgressMonitor.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

/**
 * Monitors the progress of the execution of an {@link ArteExecutable}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface ProgressMonitor
{
  /**
   * Increases the progress by the given relative amount of work (0-1) The sum of all calls should
   * sum up to 1.0
   * 
   * @param work
   */
  public void worked(double work);

  /**
   * Increases the progress to 1.0d which means all work is done.
   */
  public void done();
}

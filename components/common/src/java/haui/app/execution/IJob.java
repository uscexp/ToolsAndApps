/* *****************************************************************
 * Project: common
 * File:    IJob.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.exception.AppLogicException;
import haui.model.admin.AdminJobDTO;

/**
 * IJob
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface IJob extends AppExecutable
{

  /**
   * Default init implementation (does nothing)
   */
  public abstract void init(ExecutionContext context) throws AppLogicException;

  /**
   * Answers all keys this {@link ArteJob} locks for mutual exclusion.
   * <p>
   * Any {@link ArteJob} locking a sub set of the keys locked by this {@link ArteJob} cannot be
   * started as long as this {@link ArteJob} is running.
   * 
   * @return a {@link String} array holding the keys that this {@link ArteJob} locks during it's
   *         execution.
   *         <p>
   *         Default implementation returns {@link #getIdentifier()} as the only key
   * 
   */
  public abstract String[] getMutualExclusionKeys();

  /**
   * Performs this {@link ArteJob}
   * 
   * @param context the {@link ExecutionContext} in which the {@link ArteJob} is executed
   * @throws ArteBusinessException
   */
  public abstract void run(ExecutionContext context) throws AppLogicException;

  /**
   * final implementation returns JOB_<jobCode>
   */
  public abstract String getIdentifier();

  public abstract AdminJobDTO getAdminJob();

  public abstract void setAdminJob(AdminJobDTO aAdminJob);
}

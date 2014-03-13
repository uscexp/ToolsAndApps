/* *****************************************************************
 * Project: common
 * File:    AppUnitOfWork.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.app.dao.db.AbstractDataSource;
import haui.app.dao.db.IDataSource;
import haui.common.id.STIDGenerator;
import haui.exception.AppLogicException;

/**
 * A unit of work is consistent task that cannot be parallelized. 
 * <p>
 * A unit of work can specify whether it needs a transaction. Transaction handling
 * is then automatically done by the {@link UnitOfWorkExecutor}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AppUnitOfWork implements AppExecutable
{

  /**
   * 
   * @return <code>true</code> if this {@link ArteUnitOfWork} requires a long transaction timeout,
   *         <code>false</code> otherwise
   */
  public boolean requiresLongTransactionTimeout()
  {
    return false;
  }

  /**
   * Answers whether this UOW supports a cleared session.
   * 
   * @return <code>true</code> if a session clean up is required, <code>false</code> otherwise
   */
  public boolean supportsSessionClear()
  {
    return true;
  }

  /**
   * Carries out the work for this {@link ArteUnitOfWork}. If this method returns normally and a
   * transaction is required {@link #requiresTransaction()} then the transaction is commited, if
   * this method throws an {@link Execution}, the transaction is rolled back.
   * 
   * @throws ArteBusinessException
   */
  public abstract void run(ExecutionContext context) throws AppLogicException;

  /**
   * @see ArteExecutable#init(ExecutionContext)
   */
  public void init(ExecutionContext context) throws AppLogicException
  {
  }

  /**
   * Clients should overwrite if a more meaningful identifier (such as the uuid of the object beeing
   * processed) is available.
   */
  public String getIdentifier()
  {
    return STIDGenerator.generate();
  }

  /**
   * Overwrite this method if you want to change the datasource
   * 
   * @return
   */
  public IDataSource getDataSource()
  {
    return AbstractDataSource.instance();
  }

  public int getTimeout()
  {
    return NO_TIMEOUT;
  }
}

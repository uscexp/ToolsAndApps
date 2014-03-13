/* *****************************************************************
 * Project: common
 * File:    UnitOfWorkExecutor.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import javax.transaction.UserTransaction;

import haui.app.dao.db.AbstractDataSource;
import haui.app.service.command.ServiceContext;
import haui.app.transaction.TransactionHelper;
import haui.util.GlobalApplicationContext;

/**
 * {@link Executor} for {@link ArteUnitOfWork}.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
class UnitOfWorkExecutor implements Executor
{
  private ExecutionContext context;

  public void init(ExecutionContext context)
  {
    this.context = context;
  }

  private ExecutionContext getContext()
  {
    return context;
  }

  public void execute(AppExecutable executable) throws Throwable
  {
    if(getContext().isCanceled())
      return;
    AppUnitOfWork unitOfWork = (AppUnitOfWork)executable;
    UserTransaction tx = null;
    boolean transactionStarted = false;

    try
    {

      if(!ServiceContext.insideTransaction() && unitOfWork.getDataSource() != null)
      {
        tx = TransactionHelper.begin(((AbstractDataSource)unitOfWork.getDataSource()), getTransactionTimeout(unitOfWork));
        transactionStarted = true;
      }

      unitOfWork.run(getContext());

      if(!getContext().isCanceled() && transactionStarted)
      {
        TransactionHelper.commit(((AbstractDataSource)unitOfWork.getDataSource()), tx, true);
        tx = null;
      }

    }
    catch(Throwable t)
    {
      throw t;
    }
    finally
    {
      if(tx != null && transactionStarted)
      {
        TransactionHelper.rollback(((AbstractDataSource)unitOfWork.getDataSource()), tx, true);
      }
      cleanUp(unitOfWork);
    }
  }

  private void cleanUp(AppUnitOfWork unitOfWork)
  {
    try
    {

      if(unitOfWork.supportsSessionClear() && unitOfWork.getDataSource() != null)
      {
        ((AbstractDataSource)unitOfWork.getDataSource()).clearSessionIfRequired();
      }
      // remove the transaction from the service context
      ServiceContext.remove(ServiceContext.TRANSACTION);
      ServiceContext.remove(ServiceContext.TRANSACTION_ID);
    }
    catch(Throwable t)
    {
      getContext().getLog().error("error cleaning up after uow", t);
    }
  }

  private int getTransactionTimeout(AppUnitOfWork unitOfWork)
  {
    int transactionTimeout = -1; // default tx timeout
    if(unitOfWork.requiresLongTransactionTimeout())
    {
      transactionTimeout = getLongTransactionTimeout();
    }
    return transactionTimeout;
  }

  private int getLongTransactionTimeout()
  {
    return GlobalApplicationContext.instance().getApplicationProperties().getProperty(GlobalApplicationContext.LONG_TRANSACTION_TIMEOUTS, 3600);
  }
}

/* *****************************************************************
 * Project: common
 * File:    TransactionHelper.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.transaction;

import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import haui.app.dao.db.AbstractDataSource;
import haui.app.dao.db.DefaultDataSource;
import haui.app.service.command.ServiceContext;
import haui.common.id.STIDGenerator;
import haui.exception.AppSystemException;

/**
 * TransactionHelper
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class TransactionHelper
{
  private static final Log LOG = LogFactory.getLog(TransactionHelper.class);

  /**
   * @see javax.transaction.UserTransaction#begin()
   * @param timeout timeout to be used as transaction timeout -1 = default timeout.
   */
  public static UserTransaction begin(AbstractDataSource datasource, int timeout) throws AppSystemException
  {
    try
    {

      UserTransaction tx = UserTransactionFactory.createUserTransaction(datasource);

      if(timeout > 0)
      {
        tx.setTransactionTimeout(timeout);
      }
      tx.begin();
      if(ServiceContext.exists())
      {
        ServiceContext.put(ServiceContext.TRANSACTION, tx);
        ServiceContext.put(ServiceContext.TRANSACTION_ID, STIDGenerator.generate());
      }
      return tx;
    }
    catch(Throwable throwable)
    {
      LOG.warn("Failed to begin transaction", throwable);
      throw new AppSystemException("Failed to begin transaction", throwable);
    }
  }

  public static UserTransaction begin(AbstractDataSource datasource) throws AppSystemException
  {
    return begin(datasource, -1);
  }

  public static UserTransaction begin() throws AppSystemException
  {
    return begin(DefaultDataSource.instance(), -1);
  }

  public static UserTransaction begin(int timeout) throws AppSystemException
  {
    return begin(DefaultDataSource.instance(), timeout);
  }

  public static void commit(UserTransaction tx) throws AppSystemException
  {
    TransactionHelper.commit(DefaultDataSource.instance(), tx);
  }

  public static void commit(AbstractDataSource datasource, UserTransaction tx) throws AppSystemException
  {
    if(tx == null)
    {
      LOG.warn("Failed to commit null transaction!");
      return;
    }
    try
    {
      datasource.getSession().flush();
//      new FundamentalEntityAuditLogger().writeLog();
      tx.commit();
      if(ServiceContext.exists())
      {
        ServiceContext.remove(ServiceContext.TRANSACTION);
        ServiceContext.remove(ServiceContext.TRANSACTION_ID);
      }
    }
    catch(Throwable throwable)
    {
      LOG.warn("Failed to commit transaction", throwable);
      throw new AppSystemException("Failed to commit transaction", throwable);
    }

//    Application.instance().getTransactionPostProcess().doPostActions(datasource);
  }

  public static void commit(UserTransaction tx, boolean writePostProcessingObjects) throws AppSystemException
  {
    TransactionHelper.commit(DefaultDataSource.instance(), tx, writePostProcessingObjects);
  }

  /*
   * @see javax.transaction.UserTransaction#commit()
   */
  public static void commit(AbstractDataSource datasource, UserTransaction tx, boolean writePostProcessingObjects) throws AppSystemException
  {
    if(tx == null)
    {
      LOG.warn("Failed to commit null transaction!");
      return;
    }
    try
    {
      datasource.getSession().flush();
//      new FundamentalEntityAuditLogger().writeLog();
      tx.commit();
      if(ServiceContext.exists())
      {
        ServiceContext.remove(ServiceContext.TRANSACTION);
        ServiceContext.remove(ServiceContext.TRANSACTION_ID);
      }
      // write pending issues
//      if(writePostProcessingObjects)
//        Application.instance().getTransactionPostProcess().setWritePPObjects();

    }
    catch(Throwable throwable)
    {
      LOG.warn("Failed to commit transaction", throwable);
      throw new AppSystemException("Failed to commit transaction", throwable);
    }

//    Application.instance().getTransactionPostProcess().doPostActions(datasource);
  }

  public static void rollback(UserTransaction tx) throws AppSystemException
  {
    TransactionHelper.rollback(DefaultDataSource.instance(), tx);
  }

  /*
   * @see javax.transaction.UserTransaction#rollback()
   */
  public static void rollback(AbstractDataSource datasource, UserTransaction tx) throws AppSystemException
  {
    if(tx == null)
    {
      LOG.warn("Failed to rollback null transaction!");
      return;
    }
    try
    {
      tx.rollback();
    }
    catch(Throwable throwable)
    {
      LOG.warn("Failed to rollback transaction", throwable);
      throw new AppSystemException("Failed to rollback transaction", throwable);
    }
    finally
    {
      if(ServiceContext.exists())
      {
//        new FundamentalEntityAuditLogger().clear();
        ServiceContext.remove(ServiceContext.TRANSACTION);
        ServiceContext.remove(ServiceContext.TRANSACTION_ID);
      }
    }

    // first clear the session
    datasource.getSession().clear();
//    Application.instance().getTransactionPostProcess().doPostActions(datasource);
  }

  public static void rollback(UserTransaction tx, boolean writePostProcessingObjects) throws AppSystemException
  {
    TransactionHelper.rollback(DefaultDataSource.instance(), tx, writePostProcessingObjects);
  }

  public static void rollback(AbstractDataSource datasource, UserTransaction tx, boolean writePostProcessingObjects) throws AppSystemException
  {
    if(tx == null)
    {
      LOG.warn("Failed to rollback null transaction!");
      return;
    }
    try
    {
      tx.rollback();
    }
    catch(Throwable throwable)
    {
      LOG.warn("Failed to rollback transaction", throwable);
      throw new AppSystemException("Failed to rollback transaction", throwable);
    }
    finally
    {
      if(ServiceContext.exists())
      {
//        new FundamentalEntityAuditLogger().clear();
        ServiceContext.remove(ServiceContext.TRANSACTION);
        ServiceContext.remove(ServiceContext.TRANSACTION_ID);

      }
    }

    // write pending issues
//    if(writePostProcessingObjects)
//      Application.instance().getTransactionPostProcess().setWritePPObjects();

    // first clear the session
    datasource.getSession().clear();
//    Application.instance().getTransactionPostProcess().doPostActions(datasource);
  }
}

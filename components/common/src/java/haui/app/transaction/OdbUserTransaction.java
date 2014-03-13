/* *****************************************************************
 * Project: common
 * File:    ArteHibernateUserTransaction.java
 * 
 * Creation:     04.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.transaction;

import haui.app.dao.db.AbstractDataSource;
import haui.model.db.ISession;
import haui.model.db.odb.OdbSession;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.neodatis.odb.core.transaction.ITransaction;

/**
 * Wraps a ??Transaction into a JTA UserTransaction.
 * FIXME for real DataSource
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class OdbUserTransaction implements UserTransaction
{

  private final AbstractDataSource datasource;
  private int timeout = -1;

  private ITransaction tx = null;

  // public ArteHibernateUserTransaction() {
  // this(DefaultHibernateDataSource.getInstance());
  // }

  public OdbUserTransaction(AbstractDataSource datasource)
  {
    this(datasource, -1);
  }

  public OdbUserTransaction(AbstractDataSource datasource, int timeout)
  {
    this.datasource = datasource;
    this.timeout = timeout;
  }

  public void begin() throws NotSupportedException, SystemException
  {
    try
    {
      ISession session = datasource.getSession();
      if(session instanceof OdbSession)
        tx = ((OdbSession)session).beginTransaction();
      if(timeout >= 0)
      {
//        tx.setTimeout(timeout);
      }
    }
    catch(Exception he)
    {
      handleBeginException(he);
    }
    finally
    {

    }

  }

  public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
  {
    try
    {
      datasource.getSession().flush();
      tx.commit();
    }
    catch(Exception he)
    {
      handleCommitException(he);
    }
    finally
    {

    }
  }

  /*
   * @see javax.transaction.UserTransaction#getStatus()
   */
  public int getStatus()
  {
    int status = Status.STATUS_UNKNOWN;
    ISession session = datasource.getSession();

    if(tx.isCommited())
    {
      status = Status.STATUS_COMMITTED;
    }
    else if(session instanceof OdbSession && ((OdbSession)session).getOrgSession().isRollbacked())
    {
      status = Status.STATUS_ROLLEDBACK;
    }
    else if(session.isOpen())
    {
      status = Status.STATUS_ACTIVE;
    }

    return status;
  }

  /*
   * @see javax.transaction.UserTransaction#rollback()
   */
  public void rollback() throws IllegalStateException, SecurityException, SystemException
  {
    try
    {
      tx.rollback();
    }
    catch(Exception he)
    {
      handleRollbackException(he);
    }
  }

  protected void closeSession() throws SystemException
  {
    try
    {
      datasource.closeSession();
    }
    catch(Exception he)
    {
      throw new SystemException(he.getMessage() + ": " + he);
    }
  }

  /*
   * @see javax.transaction.UserTransaction#setRollbackOnly()
   */
  public void setRollbackOnly()
  {
    throw new UnsupportedOperationException("setRollbackOnly() is not supported");
  }

  /*
   * @see javax.transaction.UserTransaction#setTransactionTimeout(int)
   */
  public void setTransactionTimeout(int timeout)
  {
    this.timeout = timeout;
    if(tx != null && timeout >= 0)
    {
//      tx.setTimeout(timeout);
    }
  }

  private void handleBeginException(Exception he) throws NotSupportedException, SystemException
  {
    Throwable cause = he.getCause();
    if(cause instanceof NotSupportedException)
    {
      throw (NotSupportedException)cause;
    }
    if(cause instanceof SystemException)
    {
      throw (SystemException)cause;
    }
    throw new SystemException(he.toString());
  }

  private void handleCommitException(Exception he) throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException,
      IllegalStateException, SystemException
  {

    Throwable cause = he.getCause();
    if(cause instanceof RollbackException)
    {
      throw (RollbackException)cause;
    }
    if(cause instanceof HeuristicMixedException)
    {
      throw (HeuristicMixedException)cause;
    }
    if(cause instanceof HeuristicRollbackException)
    {
      throw (HeuristicRollbackException)cause;
    }
    if(cause instanceof SecurityException)
    {
      throw (SecurityException)cause;
    }
    if(cause instanceof IllegalStateException)
    {
      throw (IllegalStateException)cause;
    }
    if(cause instanceof SecurityException)
    {
      throw (SecurityException)cause;
    }
    throw new SystemException(he.toString());
  }

  private void handleRollbackException(Exception he) throws SecurityException, IllegalStateException, SystemException
  {
    Throwable cause = he.getCause();
    if(cause instanceof SecurityException)
    {
      throw (SecurityException)cause;
    }
    if(cause instanceof IllegalStateException)
    {
      throw (IllegalStateException)cause;
    }
    if(cause instanceof SecurityException)
    {
      throw (SecurityException)cause;
    }
    throw new SystemException(he.toString());
  }
}

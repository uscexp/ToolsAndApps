/* *****************************************************************
 * Project: common
 * File:    UserTransactionFactory.java
 * 
 * Creation:     05.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.transaction;

import haui.app.dao.db.AbstractDataSource;
import haui.app.dao.db.odb.OdbDataSource;

import javax.transaction.UserTransaction;

/**
 * UserTransactionFactory
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class UserTransactionFactory
{

  public static UserTransaction createUserTransaction(AbstractDataSource dataSource)
  {
    UserTransaction transaction = null;
    
    if(dataSource instanceof OdbDataSource) {
      transaction = new OdbUserTransaction(dataSource);
    }
    return transaction;
  }
}

/* *****************************************************************
 * Project: common
 * File:    Lock.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.service.lock;

import haui.app.service.command.ServiceContext;
import haui.model.DataTransferObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Lock is globally used. This class guarantees that operations are blocked on registered locks. Only the service which created the locks is able to do actions on the registered locks.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Lock
{

  private static final Lock lock = new Lock();
  private static final List registeredLockers = new LinkedList();

  private Lock()
  {
  }

  public static Lock getInstance()
  {
    return lock;
  }

  public synchronized boolean addLocker(Locker locker)
  {
    ServiceContext.addLocker(locker);
    return registeredLockers.add(locker);
  }

  public synchronized boolean removeLocker(Locker locker)
  {
    ServiceContext.removeLocker(locker);
    return registeredLockers.remove(locker);
  }

  /**
   * Checks if the given {@link IVersion} is locked.
   * 
   * @param dto
   * @return <code>null</code> if it is not locked, the reason for the lock if it is locked.
   */
  public synchronized String getLock(DataTransferObject dto)
  {
    for(Iterator it = registeredLockers.iterator(); it.hasNext();)
    {
      Locker locker = (Locker)it.next();
      // if the service context is the owner of the
      // lock go to the next lock
      if(ServiceContext.ownsLocker(locker))
        continue;
      if(locker.isLocked(dto))
      {
        String reason = locker.getLockReason();
        if(reason == null)
        {
          reason = "Unknown Reason";
        }
        return reason;
      }
    }
    return null;
  }

  public boolean isLocked(DataTransferObject dto)
  {
    return getLock(dto) != null;
  }
}

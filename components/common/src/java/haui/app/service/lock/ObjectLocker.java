/* *****************************************************************
 * Project: common
 * File:    ObjectLocker.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.service.lock;

import haui.model.DataTransferObject;

import java.util.HashSet;
import java.util.Set;

/**
 * ObjectLocker
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ObjectLocker implements Locker
{
  private String reason;
  private Set lockedObject = new HashSet();

  public ObjectLocker(String reason, DataTransferObject[] dtos)
  {
    this.reason = reason;
    addObjectLocks(dtos);
  }

  /**
   * @param versions
   */
  private void addObjectLocks(DataTransferObject[] dtos)
  {

    for(int i = 0; i < dtos.length; i++)
    {
      DataTransferObject dto = dtos[i];
      addObjectLock(dto);
    }
  }

  public boolean isLocked(DataTransferObject dto)
  {

    if(lockedObject.contains(dto.getStid())) // FIXME change to identifier instead of stid
      return true;
    return false;
  }

  public String getLockReason()
  {
    return reason;
  }

  public boolean addObjectLock(DataTransferObject dto)
  {
    return lockedObject.add(dto.getStid()); // FIXME change to identifier instead of stid
  }
}

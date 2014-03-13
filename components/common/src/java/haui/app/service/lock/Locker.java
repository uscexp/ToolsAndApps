/* *****************************************************************
 * Project: common
 * File:    Locker.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.service.lock;

import haui.model.DataTransferObject;

/**
 * Locker
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface Locker
{

  public boolean isLocked(DataTransferObject dto);
  
  public String getLockReason();
}

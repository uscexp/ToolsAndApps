/* *****************************************************************
 * Project: common
 * File:    IConfiguration.java
 * 
 * Creation:     27.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.db;

/**
 * IConfiguration
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface IConfiguration
{
  public boolean canWrite();

  public String getDirectory();

  public String getIdentification();

  public boolean isLocal();

  public boolean isNew();
}

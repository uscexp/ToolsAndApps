/* *****************************************************************
 * Project: common
 * File:    OdbConfiguration.java
 * 
 * Creation:     05.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.db.odb;

import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IOSocketParameter;
import org.neodatis.odb.core.server.layers.layer3.ServerFileParameter;

import haui.model.db.IConfiguration;

/**
 * OdbConfiguration
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class OdbConfiguration implements IConfiguration
{

  private IBaseIdentification baseIdentification;
  
  /**
   * constructor for local db access
   * 
   * @param ioFileParameter
   */
  public OdbConfiguration(IOFileParameter ioFileParameter) {
    this.baseIdentification = ioFileParameter;
  }
  
  /**
   * constructor for client db access
   * 
   * @param ioSocketParameter
   */
  public OdbConfiguration(IOSocketParameter ioSocketParameter) {
    this.baseIdentification = ioSocketParameter;
  }
  
  /**
   * constructor for server db access
   * 
   * @param serverFileParameter
   */
  public OdbConfiguration(ServerFileParameter serverFileParameter) {
    this.baseIdentification = serverFileParameter;
  }
  
  /**
   * @return {@link IBaseIdentification}
   */
  public IBaseIdentification getBaseIdentification()
  {
    return baseIdentification;
  }
  
  /* (non-Javadoc)
   * @see haui.model.db.IConfiguration#canWrite()
   */
  public boolean canWrite()
  {
    return baseIdentification.canWrite();
  }

  /* (non-Javadoc)
   * @see haui.model.db.IConfiguration#getDirectory()
   */
  public String getDirectory()
  {
    return baseIdentification.getDirectory();
  }

  /* (non-Javadoc)
   * @see haui.model.db.IConfiguration#getIdentification()
   */
  public String getIdentification()
  {
    return baseIdentification.getIdentification();
  }

  /* (non-Javadoc)
   * @see haui.model.db.IConfiguration#isLocal()
   */
  public boolean isLocal()
  {
    return baseIdentification.isLocal();
  }

  /* (non-Javadoc)
   * @see haui.model.db.IConfiguration#isNew()
   */
  public boolean isNew()
  {
    return baseIdentification.isNew();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return baseIdentification.toString();
  }
}

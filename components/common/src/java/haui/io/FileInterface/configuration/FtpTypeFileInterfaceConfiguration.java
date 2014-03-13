/* *****************************************************************
 * Project: common
 * File:    FtpTypeFileInterfaceConfiguration.java
 * 
 * Creation:     20.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import cz.dhl.ftp.Ftp;
import cz.dhl.ftp.FtpConnect;
import haui.util.AppProperties;

/**
 * Module:      FtpTypeFileInterfaceConfiguration<br> <p> Description: FtpTypeFileInterfaceConfiguration<br> </p><p> Created:     20.03.2006 by Andreas Eisenhauer </p><p>
 * @history      20.03.2006 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2006; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public class FtpTypeFileInterfaceConfiguration extends FileInterfaceConfiguration
{

  protected Ftp m_ftpObj = null;
  
  public FtpTypeFileInterfaceConfiguration( Ftp obj, String appName, AppProperties props, boolean cached)
  {
    super( appName, props, cached);
    m_ftpObj = obj;
  }

  public Ftp getFtpObj()
  {
    return m_ftpObj;
  }

  public void setFtpObj( Ftp obj)
  {
    m_ftpObj = obj;
  }

  public boolean isLocal()
  {
    return false;
  }
  
  public FtpConnect connect()
  {
    return getFtpObj().getConnect();
  }
  
  public void disconnect()
  {
    getFtpObj().disconnect();
  }
}

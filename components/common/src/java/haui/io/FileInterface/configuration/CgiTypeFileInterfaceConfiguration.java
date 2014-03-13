/* *****************************************************************
 * Project: common
 * File:    CgiTypeFileInterfaceConfiguration.java
 * 
 * Creation:     17.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.components.ConnectionManager;
import haui.util.AppProperties;

/**
 * Module:      CgiTypeFileInterfaceConfiguration<br> <p> Description: CgiTypeFileInterfaceConfiguration<br> </p><p> Created:     17.03.2006 by Andreas Eisenhauer </p><p>
 * @history      17.03.2006 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2006; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public class CgiTypeFileInterfaceConfiguration extends FileInterfaceConfiguration
{
  protected ConnectionManager m_cm;
  protected String m_strHost;

  public CgiTypeFileInterfaceConfiguration( String host, ConnectionManager cm, String appName, AppProperties props, boolean cached)
  {
    super( appName, props, cached);
    this.m_cm = cm;
    m_strHost = host;
  }

  public ConnectionManager getConnectionManager()
  {
    return m_cm;
  }

  public void setConnectionManager( ConnectionManager cm)
  {
    this.m_cm = cm;
  }

  public String getHost()
  {
    return m_strHost;
  }

  public void setHost( String host)
  {
    m_strHost = host;
  }

  public boolean isLocal()
  {
    return false;
  }

}

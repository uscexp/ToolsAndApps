/* *****************************************************************
 * Project: common
 * File:    CommPortTypeFileInterfaceConfiguration.java
 * 
 * Creation:     17.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.io.FileInterface.remote.CommPortConnection;
import haui.util.AppProperties;

/**
 * Module:      CommPortTypeFileInterfaceConfiguration<br> <p> Description: CommPortTypeFileInterfaceConfiguration<br> </p><p> Created:     17.03.2006 by Andreas Eisenhauer </p><p>
 * @history      17.03.2006 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2006; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public class CommPortTypeFileInterfaceConfiguration extends FileInterfaceConfiguration
{

  private CommPortConnection m_cc = null;
  protected String m_strHost;

  public CommPortTypeFileInterfaceConfiguration( String host, CommPortConnection cc, String appName, AppProperties props, boolean cached)
  {
    super( appName, props, cached);
    this.m_cc = cc;
    m_strHost = host;
  }

  public CommPortConnection getCommPortConnection()
  {
    return m_cc;
  }

  public void setCommPortConnection( CommPortConnection cc)
  {
    this.m_cc = cc;
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

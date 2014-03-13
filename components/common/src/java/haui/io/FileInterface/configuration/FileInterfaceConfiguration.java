/* *****************************************************************
 * Project: common
 * File:    FileInterfaceConfiguration.java
 * 
 * Creation:     16.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.util.AppProperties;

/**
 * Module:      FileInterfaceConfiguration<br> <p> Description: FileInterfaceConfiguration<br> </p><p> Created:     16.03.2006 by Andreas Eisenhauer </p><p>
 * @history      16.03.2006 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2006; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public abstract class FileInterfaceConfiguration
{
  protected String m_strAppName;
  protected AppProperties m_appProps;
  
  private boolean m_blLocal = true;
  private boolean m_blCached = false;

  public FileInterfaceConfiguration( String appName, AppProperties props, boolean cached)
  {
    super();
    m_strAppName = appName;
    m_appProps = props;
    m_blCached = cached;
  }

  public AppProperties getAppProperties()
  {
    return m_appProps;
  }

  public void setAppProperties( AppProperties props)
  {
    m_appProps = props;
  }

  public String getAppName()
  {
    return m_strAppName;
  }

  public void setAppName( String appName)
  {
    m_strAppName = appName;
  }

  public boolean isLocal()
  {
    return m_blLocal;
  }

  public boolean isCached()
  {
    return m_blCached;
  }

  public void setCached( boolean cached)
  {
    m_blCached = cached;
  }
}

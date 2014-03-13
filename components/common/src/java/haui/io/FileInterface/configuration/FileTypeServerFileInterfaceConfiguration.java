/* *****************************************************************
 * Project: common
 * File:    FileTypeServerFileInterfaceConfiguration.java
 * 
 * Creation:     23.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.components.FileTypeServerDialog;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;

/**
 * Module:      FileTypeServerFileInterfaceConfiguration<br> <p> Description: FileTypeServerFileInterfaceConfiguration<br> </p><p> Created:     23.03.2006 by Andreas Eisenhauer </p><p>
 * @history      23.03.2006 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2006; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public class FileTypeServerFileInterfaceConfiguration extends FileInterfaceConfiguration
{

  protected FileTypeServerDialog m_ftsd = null;

  public FileTypeServerFileInterfaceConfiguration( FileTypeServerDialog ftsd, String appName, AppProperties props, boolean cached)
  {
    super( appName, props, cached);
    this.m_ftsd = ftsd;
  }

  public void append( String strText)
  {
    if( m_ftsd != null && strText != null)
      m_ftsd.append( strText);
    else
    {
      if( !strText.equals( ""))
        GlobalApplicationContext.instance().getOutputPrintStream().println( strText);
        //System.out.println( strText);
    }
  }
  
  public void setDialog( FileTypeServerDialog ftsd)
  {
    m_ftsd = ftsd;
  }

  public FileTypeServerDialog getDialog()
  {
    return m_ftsd;
  }

  public boolean isLocal()
  {
    return false;
  }
}

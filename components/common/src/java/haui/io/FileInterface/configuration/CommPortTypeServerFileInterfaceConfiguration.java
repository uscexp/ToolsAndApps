/* *****************************************************************
 * Project: common
 * File:    CommPortTypeServerFileInterfaceConfiguration.java
 * 
 * Creation:     20.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.components.CommPortTypeServerDialog;
import haui.components.FileTypeServerDialog;
import haui.util.AppProperties;

import javax.comm.CommPort;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;

/**
 * Module:      CommPortTypeServerFileInterfaceConfiguration<br>
 *<p>
 * Description: CommPortTypeServerFileInterfaceConfiguration<br>
 *</p><p>
 * Created:     20.03.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     20.03.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class CommPortTypeServerFileInterfaceConfiguration extends FileTypeServerFileInterfaceConfiguration
{

  protected CommPortIdentifier m_portId = null;
  protected CommPort m_port = null;
  protected int m_mode;
  protected int m_baud;

  public CommPortTypeServerFileInterfaceConfiguration( CommPortIdentifier id, FileTypeServerDialog ftsd, String appName, AppProperties props, boolean cached)
  {
    this( id, ftsd, -1, 4800, appName, props, cached);
  }

  public CommPortTypeServerFileInterfaceConfiguration( CommPortIdentifier id, FileTypeServerDialog ftsd, int mode, int baud, String appName, AppProperties props, boolean cached)
  {
    super( ftsd, appName, props, cached);
    m_portId = id;
    setMode( mode);
    setBaud( baud);
  }

  public int getMode()
  {
    if( getDialog() != null)
      setMode( ((CommPortTypeServerDialog)getDialog()).getMode());
    return m_mode;
  }
  
  public void setMode( int mode)
  {
    m_mode = mode;
  }
  
  public int getBaud()
  {
    if( getDialog() != null)
      setBaud( ((CommPortTypeServerDialog)getDialog()).getBaud());
    return m_baud;
  }
  
  public void setBaud( int baud)
  {
    m_baud = baud;
  }

  public CommPortIdentifier getCommPortIdentifier()
  {
    if( getDialog() != null)
    {
      setCommPortIdentifier( ((CommPortTypeServerDialog)getDialog()).getCommPortId());
    }
    return m_portId;
  }

  public void setCommPortIdentifier( CommPortIdentifier id)
  {
    m_portId = id;
  }

  public CommPort getPort()
  {
    return m_port;
  }

  public void setPort( CommPort port)
  {
    this.m_port = port;
  }
  
  public CommPort open( String className, int timeout) throws PortInUseException
  {
    m_port = getCommPortIdentifier().open( className, timeout );
    return m_port;
  }
  
  public void disconnect()
  {
    getPort().close();
    setPort( null);
  }
}

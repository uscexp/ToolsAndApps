/* *****************************************************************
 * Project: common
 * File:    SocketTypeServerFileInterfaceConfiguration.java
 * 
 * Creation:     22.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.components.FileTypeServerDialog;
import haui.components.SocketTypeServerDialog;
import haui.util.AppProperties;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Module:      SocketTypeServerFileInterfaceConfiguration<br>
 *<p>
 * Description: SocketTypeServerFileInterfaceConfiguration<br>
 *</p><p>
 * Created:     22.03.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     22.03.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class SocketTypeServerFileInterfaceConfiguration extends FileTypeServerFileInterfaceConfiguration
{

  // constants
  public final static int DEFAULTPORT = 9000;

  // member variables
  private int m_iPort = DEFAULTPORT;
  private ServerSocket m_serverListener = null;
  private Socket m_serverSock = null;
  private ServerSocket m_tranferListener = null;
  private Socket m_transferSock= null;
  
  public SocketTypeServerFileInterfaceConfiguration(int port, FileTypeServerDialog ftsd, String appName, AppProperties props, boolean cached)
  {
    super( ftsd, appName, props, cached);
    m_iPort = port;
  }

  public boolean serverSocketListen() throws IOException
  {
    serverSocketListen( 0);
    return true;
  }

  public boolean serverSocketListen( int portOffset) throws IOException
  {
    if( getDialog() != null)
    {
      m_iPort = ((SocketTypeServerDialog)m_ftsd).getPort();
      if( m_iPort == -1)
        m_iPort = DEFAULTPORT;
    }
    append( "Listen on port: " + m_iPort + String.valueOf(portOffset) + "\n");
    m_serverListener = new ServerSocket( m_iPort+portOffset );
    m_serverSock = m_serverListener.accept();
    m_serverListener.close();
    return true;
  }
  
  public boolean disconnectServerSocket() throws IOException
  {
    if( getServerSocket() != null) {
      getServerSocket().close();
      setServerSocket( null);
      return true;
    }
    return false;
  }

  public boolean disconnectTransferSocket() throws IOException
  {
    if( getTransferSocket() != null) {
      getTransferSocket().close();
      setTransferSocket( null);
      return true;
    }
    return false;
  }

  public int getPort()
  {
    return m_iPort;
  }

  public void setPort( int port)
  {
    m_iPort = port;
  }

  public ServerSocket getServerListener()
  {
    return m_serverListener;
  }

  public void setServerListener( ServerSocket listener)
  {
    m_serverListener = listener;
  }

  public Socket getServerSocket()
  {
    return m_serverSock;
  }

  public void setServerSocket( Socket sock)
  {
    m_serverSock = sock;
  }

  public ServerSocket getTranferListener()
  {
    return m_tranferListener;
  }

  public void setTranferListener( ServerSocket listener)
  {
    m_tranferListener = listener;
  }

  public Socket getTransferSocket()
  {
    return m_transferSock;
  }

  public void setTransferSocket( Socket sock)
  {
    m_transferSock = sock;
  }

}

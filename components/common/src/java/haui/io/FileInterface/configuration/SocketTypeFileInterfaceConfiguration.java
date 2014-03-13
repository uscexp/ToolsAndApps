/* *****************************************************************
 * Project: common
 * File:    SocketTypeFileInterfaceConfiguration.java
 * 
 * Creation:     21.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.io.FileInterface.remote.SocketConnection;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Module:      SocketTypeFileInterfaceConfiguration<br> <p> Description: SocketTypeFileInterfaceConfiguration<br> </p><p> Created:     21.03.2006 by Andreas Eisenhauer </p><p>
 * @history      21.03.2006 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2006; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public class SocketTypeFileInterfaceConfiguration extends FileInterfaceConfiguration
{

  private SocketConnection m_sc = null;
  private Socket m_transferSock = null;

  public SocketTypeFileInterfaceConfiguration( SocketConnection sc, String appName, AppProperties props, boolean cached)
  {
    super( appName, props, cached);
    this.m_sc = sc;
  }

  public SocketConnection getSocketConnection()
  {
    return m_sc;
  }

  public void setSocketConnection( SocketConnection sc)
  {
    this.m_sc = sc;
  }

  public String getHost()
  {
    return getSocketConnection().getHost();
  }
  
  public Socket allocateNewTransferSocket() throws UnknownHostException, IOException
  {
    if( m_transferSock != null )
    {
      for( int i = 0; i < 30; ++i)
      {
        try
        {
          Thread.sleep( 1000);
        }
        catch( InterruptedException ex)
        {
          ex.printStackTrace();
        }
        if( m_transferSock == null )
          break;
      }
      if( m_transferSock != null )
      {
        GlobalApplicationContext.instance().getOutputPrintStream().println( "ERROR: Timeout while waiting for the socket to be freed!");
        //System.err.println( "ERROR: Timeout while waiting for the socket to be freed!");
        throw new IOException( "Timeout while waiting for the socket to be freed!");
      }
    }
    setTransferSocket( new Socket( getSocketConnection().getHost(), getSocketConnection().getPort()+1));
    return m_transferSock;
  }

  public Socket getTransferSocket()
  {
    return m_transferSock;
  }

  public void setTransferSocket( Socket sock)
  {
    m_transferSock = sock;
  }
  
  public Socket connect() throws IOException
  {
    Socket sock = null;
    if( getSocketConnection() != null
        && !getSocketConnection().isConnected())
    {
      sock = getSocketConnection().getSocket();
    }
    return sock;
  }
  
  public void disconnect()
  {
    if( getSocketConnection() != null )
    {
      getSocketConnection().close();
      //setSocketConnection( null);
    }
  }
  
  public void closeTransferSocket() throws IOException
  {
    if( getTransferSocket() != null)
    {
      getTransferSocket().close();
      setTransferSocket( null);
    }
  }

  public boolean isLocal()
  {
    return false;
  }
}

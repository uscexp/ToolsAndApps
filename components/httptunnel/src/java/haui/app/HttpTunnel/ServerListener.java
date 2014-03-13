package haui.app.HttpTunnel;

import java.net.*;
import java.io.*;
import javax.swing.JTextArea;
import haui.util.AppProperties;

/**
 *		Module:					ServerListener.java<br>
 *										$Source: $
 *<p>
 *		Description:    Listener for the client connection.<br>
 *</p><p>
 *		Created:				06.12.2001	by	AE
 *</p><p>
 *		@history				06.12.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2001; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ServerListener
  extends Thread
{
  AppProperties m_appProps;
  private ServerSocket m_serverSocket = null;
  private ConnectionMap m_cm;
  private boolean m_listening = true;
  private String m_servletUrl;
  private JTextArea m_jTextAreaOut;

  public ServerListener( ConnectionMap cm, String servletUrl, JTextArea ta, AppProperties appProps)
  {
    super( "ServerListener");
    m_cm = cm;
    m_servletUrl = servletUrl;
    m_jTextAreaOut = ta;
    m_appProps = appProps;
  }

  public void setServletUrl( String servletUrl)
  {
    m_servletUrl = servletUrl;
  }

  public ConnectionMap getConnectionMap()
  {
    return m_cm;
  }

  public void stopListening()
  {
    m_listening = false;
  }

  protected void finalize()
    throws Throwable
  {
    closeAll();
  }

  private void closeAll()
  {
    try
    {
      if( m_serverSocket != null)
      {
        m_serverSocket.close();
        m_serverSocket = null;
      }
    }
    catch( IOException e)
    {
      m_jTextAreaOut.append( "ERROR: " + e.toString() + "\n");
      e.printStackTrace();
    }
  }

  public void stopIt()
  {
    closeAll();
    stopListening();
    super.stop();
  }

  public void interrupt()
  {
    closeAll();
    stopListening();
    super.interrupt();
  }

  public void run()
  {
    try
    {
      m_serverSocket = new ServerSocket( m_cm.getLocalPort());
    }
    catch( IOException e)
    {
      m_jTextAreaOut.append( "Could not listen on port " + String.valueOf( m_cm.getLocalPort()) + ".\n");
      //System.err.println( "Could not listen on port " + String.valueOf( m_cm.getLocalPort()) + ".");
      e.printStackTrace();
    }
    try
    {
      m_jTextAreaOut.append( "Listen on port " + String.valueOf( m_cm.getLocalPort()) + ".\n");
      //System.out.println( "Listen on port " + String.valueOf( m_cm.getLocalPort()) + ".");
      while( m_listening)
      {
        new ServerConnection( m_serverSocket.accept(), m_cm, m_servletUrl, m_jTextAreaOut, m_appProps).start();
      }
    }
    catch( IOException e)
    {
      if( e.toString().indexOf( "socket closed") == -1)
      {
        //m_jTextAreaOut.append( "ERROR: " + e.toString() + "\n");
        e.printStackTrace();
      }
    }
    closeAll();
  }
}

package haui.io.FileInterface.remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Module:      SocketConnection.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\SocketConnection.java,v $
 *<p>
 * Description: connection data for socket connections.<br>
 *</p><p>
 * Created:	    27.05.2004  by AE
 *</p><p>
 * @history     27.05.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: SocketConnection.java,v $
 * Revision 1.0  2004-06-22 14:06:57+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\SocketConnection.java,v 1.0 2004-06-22 14:06:57+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class SocketConnection
{
  private String m_strHost = null;
  private int m_iPort = 0;
  private Socket m_clientSock = null;
  private ObjectInputStream m_ois = null;
  private ObjectOutputStream m_oos = null;

  public SocketConnection( String strHost, int iPort)
  {
    m_strHost = strHost;
    m_iPort = iPort;
  }

  public Socket getSocket()
    throws IOException
  {
    if( m_clientSock == null)
    {
      m_clientSock = new Socket( m_strHost, m_iPort);
      //m_clientSock.connect( new InetSocketAddress( m_strHost, m_iPort), 10000);
      m_oos = new ObjectOutputStream( m_clientSock.getOutputStream());
      m_ois = new ObjectInputStream( m_clientSock.getInputStream());
    }
    return m_clientSock;
  }

  public String getHost()
  {
    return m_strHost;
  }

  public int getPort()
  {
    return m_iPort;
  }

  public ObjectInputStream getObjectInputStream()
  {
    return m_ois;
  }

  public ObjectOutputStream getObjectOutputStream()
  {
    return m_oos;
  }

  public boolean isConnected()
  {
    boolean blRet = false;
    if( m_clientSock != null)
      blRet = m_clientSock.isConnected();
    return blRet;
  }

  public void close()
  {
    try
    {
      if( m_oos != null )
      {
        m_oos.close();
        m_oos = null;
      }
      if( m_ois != null )
      {
        m_ois.close();
        m_ois = null;
      }
      if( m_clientSock != null)
      {
        m_clientSock.close();
        m_clientSock = null;
      }
      System.out.println( "Connection closed!\n");
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }
  }
}
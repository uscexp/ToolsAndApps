package haui.components;

import com.sun.net.ssl.HostnameVerifier;
import com.sun.net.ssl.HttpsURLConnection;
import com.sun.net.ssl.internal.ssl.Provider;
import haui.util.AppProperties;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Security;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 *		Module:					SecureConnectionManager.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\SecureConnectionManager.java,v $
 *<p>
 *		Description:    HTTP connection class.<br>
 *</p><p>
 *		Created:				04.10.2000	by	AE
 *</p><p>
 *		@history				04.10.2000 - 05.10.2000	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: SecureConnectionManager.java,v $
 *		Revision 1.2  2004-08-31 16:03:07+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.1  2003-05-28 14:19:49+02  t026843
 *		reorganisations
 *
 *		Revision 1.0  2002-01-15 11:15:06+01  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: 1.2 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\SecureConnectionManager.java,v 1.2 2004-08-31 16:03:07+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class SecureConnectionManager
  extends ConnectionManager
{
  final static String PROTOCOL = "com.sun.net.ssl.internal.www.protocol";

  //HttpsURLConnection m_httpConn;

  public Object clone()
  {
    SecureConnectionManager cm = new SecureConnectionManager( new String( m_url.toString()), m_appProps, m_strAppName);
    cm.setFrame( m_frame);
    return cm;
  }

  public SecureConnectionManager()
  {
    super();
  }

  public SecureConnectionManager( URL url, AppProperties appProps, String strAppName)
  {
    super( url, appProps, strAppName);
  }

  public SecureConnectionManager( String strUrl, AppProperties appProps, String strAppName)
  {
    super( strUrl, appProps, strAppName);
  }

  public SecureConnectionManager( URL url, String sessionId, AppProperties appProps, String strAppName)
  {
    super( url, sessionId, appProps, strAppName);
  }

  public SecureConnectionManager( String strUrl, String sessionId, AppProperties appProps, String strAppName)
  {
    super( strUrl, sessionId, appProps, strAppName);
  }

  protected void setProtocol()
  {
    System.setProperty( "java.protocol.handler.pkgs", PROTOCOL);
    Security.addProvider( new Provider());
    //HttpsURLConnection.setDefaultSSLSocketFactory( (SSLSocketFactory)SSLSocketFactory.getDefault());
  }

  public void connect()
    throws IOException
  {
    super.connect();
    //m_httpConn = (HttpsURLConnection)m_connectUrl.openConnection();
    if( m_httpConn instanceof HttpsURLConnection)
    {
      ((HttpsURLConnection)m_httpConn).setSSLSocketFactory(
          new SSLTunnelSocketFactory( m_proxyHandler.getProxyHost(), m_proxyHandler.getProxyPort()));
      ((HttpsURLConnection)m_httpConn).setDefaultHostnameVerifier(new myHostNameVerifier());
      if( usingProxy())
      {
        ((HttpsURLConnection)m_httpConn).setSSLSocketFactory(
            new SSLTunnelSocketFactory( m_proxyHandler.getProxyHost(), m_proxyHandler.getProxyPort(),
            m_proxyHandler.getUser(), m_proxyHandler.getPassword()));
        /*
        String auth = m_proxyHandler.getAuthorization();
        m_httpConn.setRequestProperty("Proxy-Authorization", auth);
        String msg =
            "CONNECT "
            + m_proxyHandler.getProxyHost()
            + ":"
            + String.valueOf( m_proxyHandler.getProxyPort())
            + " HTTP/1.0\r\n"
            + "User-Agent: "
            + sun.net.www.protocol.http.HttpURLConnection.userAgent
            + "\r\n"
            + "Proxy-Authorization: " + auth
            + "\r\n"
            + "Pragma: no-cache"
            + "\r\n\r\n";
        byte b[];
        try
        {
          //we really do want ASCII7 as the http protocol doesnt change with locale
          b = msg.getBytes("ASCII7");
        }
        catch (UnsupportedEncodingException ignored)
        {
          //If ASCII7 isn't there, something is seriously wrong!
          b = msg.getBytes();
        }
        System.out.write( b);
        Socket tunnel = new Socket( m_proxyHandler.getProxyHost(), m_proxyHandler.getProxyPort());
        OutputStream out = tunnel.getOutputStream();
        out.write(b);
        out.flush();
        */
      }
    }
  }
}

class myHostNameVerifier
  implements HostnameVerifier
{
  public boolean verify(String hostname,String certname)
  {
    return true;
  }
}

/**
 * SSLSocket used to tunnel through a proxy
 */
class SSLTunnelSocketFactory extends SSLSocketFactory {
    private String tunnelHost;
    private int tunnelPort;
    private SSLSocketFactory dfactory;
    private String tunnelPassword;
    private String tunnelUserName;
    private boolean socketConnected = false;
    //private int falsecount = 0;
    /**
     *  Constructor for the SSLTunnelSocketFactory object
     *
     *@param  proxyHost  The url of the proxy host
     *@param  proxyPort  the port of the proxy
     */
    public SSLTunnelSocketFactory(String proxyHost, int proxyPort) {
        System.err.println("creating Socket Factory");
        tunnelHost = proxyHost;
        tunnelPort = proxyPort;
        dfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }
    /**
     *  Constructor for the SSLTunnelSocketFactory object
     *
     *@param  proxyHost      The url of the proxy host
     *@param  proxyPort      the port of the proxy
     *@param  proxyUserName  username for authenticating with the proxy
     *@param  proxyPassword  password for authenticating with the proxy
     */
    public SSLTunnelSocketFactory(String proxyHost, int proxyPort, String proxyUserName, String proxyPassword) {
        System.err.println("creating Socket Factory with password/username");
        tunnelHost = proxyHost;
        tunnelPort = proxyPort;
        tunnelUserName = proxyUserName;
        tunnelPassword = proxyPassword;
        dfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }
    /**
     *  Sets the proxyUserName attribute of the SSLTunnelSocketFactory object
     *
     *@param  proxyUserName  The new proxyUserName value
     */
    public void setProxyUserName(String proxyUserName) {
        tunnelUserName = proxyUserName;
    }
    /**
     *  Sets the proxyPassword attribute of the SSLTunnelSocketFactory object
     *
     *@param  proxyPassword  The new proxyPassword value
     */
    public void setProxyPassword(String proxyPassword) {
        tunnelPassword = proxyPassword;
    }
    /**
     *  Gets the supportedCipherSuites attribute of the SSLTunnelSocketFactory
     *  object
     *
     *@return    The supportedCipherSuites value
     */
    public String[] getSupportedCipherSuites() {
        return dfactory.getSupportedCipherSuites();
    }
    /**
     *  Gets the defaultCipherSuites attribute of the SSLTunnelSocketFactory
     *  object
     *
     *@return    The defaultCipherSuites value
     */
    public String[] getDefaultCipherSuites() {
        return dfactory.getDefaultCipherSuites();
    }
    /**
     * Gets the socketConnected attribute of the SSLTunnelSocketFactory object
     * @return     The socketConnected value
     * @uml.property  name="socketConnected"
     */
    public synchronized boolean getSocketConnected() {
        return socketConnected;
    }
    /**
     *  Creates a new SSL Tunneled Socket
     *
     *@param  s                         Ignored
     *@param  host                      destination host
     *@param  port                      destination port
     *@param  autoClose                 wether to close the socket automaticly
     *@return                           proxy tunneled socket
     *@exception  IOException           raised by an IO error
     *@exception  UnknownHostException  raised when the host is unknown
     */
    public Socket createSocket(Socket s, String host, int port, boolean autoClose)
             throws IOException, UnknownHostException {
        Socket tunnel = new Socket(tunnelHost, tunnelPort);
        doTunnelHandshake(tunnel, host, port);
        SSLSocket result = (SSLSocket) dfactory.createSocket(tunnel, host, port, autoClose);
        result.addHandshakeCompletedListener(
            new HandshakeCompletedListener() {
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    System.out.println("Handshake Finished!");
                    System.out.println("\t CipherSuite :" + event.getCipherSuite());
                    System.out.println("\t SessionId: " + event.getSession());
                    System.out.println("\t PeerHost: " + event.getSession().getPeerHost());
                    setSocketConnected(true);
                }
            });
        // thanks to David Lord in the java forums for figuring out this line is the problem
        // result.startHandshake(); //this line is the bug which stops Tip111 from working correctly
        return result;
    }
    /**
     *  Creates a new SSL Tunneled Socket
     *
     *@param  host                      destination host
     *@param  port                      destination port
     *@return                           tunneled SSL Socket
     *@exception  IOException           raised by IO error
     *@exception  UnknownHostException  raised when the host is unknown
     */
    public Socket createSocket(String host, int port)
             throws IOException, UnknownHostException {
        return createSocket(null, host, port, true);
    }
    /**
     *  Creates a new SSL Tunneled Socket
     *
     *@param  host                      Destination Host
     *@param  port                      Destination Port
     *@param  clientHost                Ignored
     *@param  clientPort                Ignored
     *@return                           SSL Tunneled Socket
     *@exception  IOException           Raised when IO error occurs
     *@exception  UnknownHostException  Raised when the destination host is
     *      unknown
     */
    public Socket createSocket(String host, int port, InetAddress clientHost,
            int clientPort)
             throws IOException, UnknownHostException {
        return createSocket(null, host, port, true);
    }
    /**
     *  Creates a new SSL Tunneled Socket
     *
     *@param  host             destination host
     *@param  port             destination port
     *@return                  tunneled SSL Socket
     *@exception  IOException  raised when IO error occurs
     */
    public Socket createSocket(InetAddress host, int port)
             throws IOException {
        return createSocket(null, host.getHostName(), port, true);
    }
    /**
     *  Creates a new SSL Tunneled Socket
     *
     *@param  address          destination host
     *@param  port             destination port
     *@param  clientAddress    ignored
     *@param  clientPort       ignored
     *@return                  tunneled SSL Socket
     *@exception  IOException  raised when IO exception occurs
     */
    public Socket createSocket(InetAddress address, int port,
            InetAddress clientAddress, int clientPort)
             throws IOException {
        return createSocket(null, address.getHostName(), port, true);
    }
    /**
     * Sets the socketConnected attribute of the SSLTunnelSocketFactory object
     * @param b   The new socketConnected value
     * @uml.property  name="socketConnected"
     */
    private synchronized void setSocketConnected(boolean b) {
        socketConnected = b;
    }
    /**
     *  Description of the Method
     *
     *@param  tunnel           tunnel socket
     *@param  host             destination host
     *@param  port             destination port
     *@exception  IOException  raised when an IO error occurs
     */
    private void doTunnelHandshake(Socket tunnel, String host, int port) throws IOException {
        OutputStream out = tunnel.getOutputStream();
        //generate connection string
        String msg = "CONNECT " + host + ":" + port + " HTTP/1.0\n"
                 + "User-Agent: "
                 + sun.net.www.protocol.http.HttpURLConnection.userAgent;
        if (tunnelUserName != null && tunnelPassword != null) {
            //add basic authentication header for the proxy
            sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
            String encodedPassword = enc.encode((tunnelUserName + ":" + tunnelPassword).getBytes());
            msg = msg + "\nProxy-Authorization: Basic " + encodedPassword;
        }
        msg = msg + "\nContent-Length: 0";
        msg = msg + "\nPragma: no-cache";
        msg = msg + "\r\n\r\n";
        byte b[];
        try {
            //we really do want ASCII7 as the http protocol doesnt change with locale
            b = msg.getBytes("ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            //If ASCII7 isn't there, something is seriously wrong!
            b = msg.getBytes();
        }
        out.write(b);
        out.flush();
        System.err.write(b);
        byte reply[] = new byte[200];
        int replyLen = 0;
        int newlinesSeen = 0;
        boolean headerDone = false;
        InputStream in = tunnel.getInputStream();
        //boolean error = false;
        while (newlinesSeen < 2) {
            int i = in.read();
            if (i < 0) {
                throw new IOException("Unexpected EOF from Proxy");
            }
            if (i == '\n') {
                headerDone = true;
                ++newlinesSeen;
            } else
                    if (i != '\r') {
                newlinesSeen = 0;
                if (!headerDone && replyLen < reply.length) {
                    reply[replyLen++] = (byte) i;
                }
            }
        }
        //convert byte array to string
        String replyStr;
        try {
            replyStr = new String(reply, 0, replyLen, "ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            replyStr = new String(reply, 0, replyLen);
        }
        //we check for connection established because our proxy returns http/1.1 instead of 1.0
        if (replyStr.toLowerCase().indexOf("200 connection established") == -1) {
            System.err.println(replyStr);
            throw new IOException("Unable to tunnel through " + tunnelHost + ":" + tunnelPort + ". Proxy returns\"" + replyStr + "\"");
        }
        //tunneling hanshake was successful
    }

}
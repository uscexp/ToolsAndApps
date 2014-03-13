package haui.components;

import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Module:					ConnectionManager.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ConnectionManager.java,v $ <p> Description:    HTTP connection class.<br> </p><p> Created:				04.10.2000	by	AE </p><p>
 * @history  				04.10.2000 - 05.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ConnectionManager.java,v $  Revision 1.10  2004-08-31 16:03:07+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.9  2003-05-28 14:19:57+02  t026843  reorganisations  Revision 1.8  2002-09-18 11:16:21+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.7  2002-09-03 17:09:05+02  t026843  - Changes for the CgiTypeFile.  Revision 1.6  2002-08-28 14:23:39+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.5  2002-04-08 15:20:08+02  t026843  Polling, Compressed, stateless- and non stateless connections.  Revision 1.4  2002-01-15 11:14:24+01  t026843  Data header changed to a dynamic header and extended with host and port  Revision 1.3  2002-01-10 16:59:43+01  t026843  <>  Revision 1.2  2001-07-20 16:29:02+02  t026843  FileManager changes  Revision 1.1  2000-10-13 09:09:46+02  t026843  bugfixes + changes  Revision 1.0  2000-10-05 14:48:34+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.10 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\ConnectionManager.java,v 1.10 2004-08-31 16:03:07+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ConnectionManager
extends Object
{
  // constants
  public final static String EOF = "<EOF>";
  public final static String SOF = "<SOF>";
  public final static String CEOF = "<CEOF>";
  public final static int BUFFERSIZE = 32768;
  //public final static String BINDATA_CONTENTTYPE = "application/x-www-form-urlencoded-mixed-with-binary-data";
  public final static String NORMAL_CONTENTTYPE = "application/x-www-form-urlencoded";
  public final static String BINDATA_CONTENTTYPE = "application/octet-stream";

  // Upload constants
  final static String CONTENT_BOUNDARY = "-----------------------------7d22a63810058e";
  final static String MULITPARTDATA_CONTENTTYPE = "multipart/form-data";

  // member variables
  AppProperties m_appProps;
  String m_strAppName;
  AuthorizationDialog m_authDlg;
  HttpURLConnection m_httpConn;
  URL m_url;
  URL m_connectUrl;
  HttpProxyHandler m_proxyHandler;
  BufferedReader m_brd;
  BufferedInputStream m_bis;
  BufferedOutputStream m_bos;
  Vector m_postParams = new Vector();
  Vector m_cookies = new Vector();
  int m_iRetries;
  Component m_frame;
  String m_auth;
  String m_sessionId;

  public Object clone()
  {
    ConnectionManager cm = new ConnectionManager( new String( m_url.toString()), m_appProps, m_strAppName);
    cm.setFrame( m_frame);
    return cm;
  }

  public ConnectionManager()
  {
    setProtocol();
    m_url = null;
    m_appProps = null;
    Authenticator.setDefault( new URLAuthenticator());
  }

  public ConnectionManager( URL url, AppProperties appProps, String strAppName)
  {
    m_strAppName = strAppName;
    setProtocol();
    m_url = url;
    m_appProps = appProps;
    m_proxyHandler = new HttpProxyHandler( m_appProps, strAppName);
    Authenticator.setDefault( new URLAuthenticator());
  }

  public ConnectionManager( String strUrl, AppProperties appProps, String strAppName)
  {
    m_strAppName = strAppName;
    setProtocol();
    setURL( strUrl);
    m_appProps = appProps;
    m_proxyHandler = new HttpProxyHandler( m_appProps, strAppName);
    Authenticator.setDefault( new URLAuthenticator());
  }

  public ConnectionManager( URL url, String sessionId, AppProperties appProps, String strAppName)
  {
    this( url, appProps, strAppName);
    setSessionId( sessionId);
  }

  public ConnectionManager( String strUrl, String sessionId, AppProperties appProps, String strAppName)
  {
    this( strUrl, appProps, strAppName);
    setSessionId( sessionId);
  }

  protected void setProtocol()
  {
  }

  public void setFrame( Component frame)
  {
    m_frame = frame;
  }

  public URL getURL()
  {
    return m_url;
  }

  public URL getConnectURL()
  {
    return m_connectUrl;
  }

  public void setURL( URL url)
  {
    m_url = url;
    if( m_sessionId == null)
      m_connectUrl = url;
    else
      setConnectURL( url.toString());
  }

  public void setURL( String strUrl)
  {
    try
    {
      setURL( new URL( strUrl));
    }
    catch( MalformedURLException muex)
    {
      muex.printStackTrace();
    }
  }

  private void setConnectURL( String strUrl)
  {
    try
    {
      if( m_sessionId != null)
        m_connectUrl = new URL( strUrl + ";jsessionid=" + m_sessionId);
      else
        m_connectUrl = m_url;
    }
    catch( MalformedURLException muex)
    {
      muex.printStackTrace();
    }
  }

  public HttpURLConnection getHttpURLConnection()
  {
    return m_httpConn;
  }

  public void setHttpURLConnection( HttpURLConnection httpConn)
  {
    m_httpConn = httpConn;
  }

  public AppProperties getAppProperties()
  {
    return m_appProps;
  }

  public void setAppProperties( AppProperties appProps)
  {
    m_appProps = appProps;
    m_proxyHandler = new HttpProxyHandler( m_appProps, m_strAppName);
  }

  public boolean usingProxy()
  {
    return m_appProps.getBooleanProperty( GlobalApplicationContext.PROXYENABLE);
  }

  public void disconnect()
  {
    m_httpConn.disconnect();
    closeBufferedReader();
    closeBufferedInputStream();
    closeBufferedOutputStream();
    m_iRetries = 0;
  }

  void closeBufferedInputStream()
  {
    if( m_bis != null)
    {
      try
      {
        m_bis.close();
      }
      catch( IOException ioex)
      {
        m_bis = null;
        ioex.printStackTrace();
      }
      m_bis = null;
    }
  }

  void closeBufferedOutputStream()
  {
    if( m_bos != null)
    {
      try
      {
        m_bos.close();
      }
      catch( IOException ioex)
      {
        m_bos = null;
        ioex.printStackTrace();
      }
      m_bos = null;
    }
  }

  void closeBufferedReader()
  {
    if( m_brd != null)
    {
      try
      {
        m_brd.close();
      }
      catch( IOException ioex)
      {
        m_brd = null;
        ioex.printStackTrace();
      }
      m_brd = null;
    }
  }

  public void connect()
  throws IOException
  {
    m_httpConn = (HttpURLConnection)m_connectUrl.openConnection();
    if( usingProxy())
      m_httpConn.setRequestProperty("Proxy-Authorization", m_proxyHandler.getAuthorization());
  }

  public HttpURLConnection openConnection()
  {
    try
    {
      connect();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    return m_httpConn;
  }

  public void resetPostParams()
  {
    m_postParams.removeAllElements();
  }

  public void addPostParam( String name, String value)
  {
    String str[] = new String[2];
    str[0] = name;
    str[1] = value;
    m_postParams.add( str);
  }

  public void resetCookies()
  {
    m_cookies.removeAllElements();
  }

  public void addCookie( String name, String value)
  {
    String str[] = new String[2];
    str[0] = name;
    str[1] = value;
    m_cookies.add( str);
  }

  public void setSessionId( String sessionId)
  {
    m_sessionId = sessionId;
    if( m_url != null && m_sessionId != null)
      setConnectURL( m_url.toString());
  }

  public String getSessionId()
  {
    return m_sessionId;
  }

  public void post( byte[] buffer, int iLen, boolean sendParams)
  {
    try
    {
      m_httpConn.setDoOutput(true);
      m_httpConn.setDoInput( true);
      m_httpConn.setRequestProperty("Content-type", BINDATA_CONTENTTYPE);
      transferCookies();
      m_bos = new BufferedOutputStream( m_httpConn.getOutputStream());
      //int iRet = 0;

      if( sendParams)
      {
        String line = getParameters();
        byte[] paramBuf;
        int iParamLen;
        if( line != null)
        {
          line += "\n\n";
          paramBuf = line.getBytes();
          iParamLen = Array.getLength( paramBuf);
          m_bos.write( paramBuf, 0, iParamLen);
        }
      }

      m_bos.write( buffer, 0, iLen);
      m_bos.flush();
      m_bos.close();
      m_bos = null;
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
  }

  public void postEndUpload( BufferedOutputStream bos)
  {
    try
    {
      bos.write( ( "\r\n" + CONTENT_BOUNDARY + "--\r\n" ).getBytes() );
      bos.flush();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
  }

  public BufferedOutputStream postInitialUpload( String name)
  {
    try
    {
      m_httpConn.setAllowUserInteraction( true);
      m_httpConn.setDoOutput(true);
      m_httpConn.setDoInput( true);
      m_httpConn.setRequestProperty( "Content-Type", MULITPARTDATA_CONTENTTYPE + ";boundary=" + CONTENT_BOUNDARY );
      m_httpConn.setRequestProperty( "User-Agent", "Mozilla/4.7 [en] (WinNT; U)" );
      m_httpConn.setRequestProperty( "Accept-Language", "en-us" );
      m_httpConn.setRequestProperty( "Accept-Encoding", "gzip, deflate" );
      m_httpConn.setRequestProperty( "Accept", "image/gif, image/x-xbitmap,image/jpeg, image/pjpeg, application/vnd.ms-excel, "
        + BINDATA_CONTENTTYPE + ", application/msword,application/vnd.ms-powerpoint, application/pdf, application/x-comet, */*" );
      m_httpConn.setRequestProperty( "CACHE-CONTROL", "no-cache" );
      //transferCookies();
      m_bos = new BufferedOutputStream( m_httpConn.getOutputStream());
      //int iRet = 0;

      // send parameter
      String line = getParameters();
      byte[] paramBuf;
      int iParamLen;
      if( line != null)
      {
        line += "\n\n";
        paramBuf = line.getBytes();
        iParamLen = Array.getLength( paramBuf);
        m_bos.write( paramBuf, 0, iParamLen);
      }

      // Field 1 a Field of data
      m_bos.write( ( CONTENT_BOUNDARY + "\r\n" +
          "Content-Disposition: form-data; name=\"txtTitle\"\r\n\r\nTestSegment\r\n" ).getBytes() );
      // Field 2 - the file, name"
      m_bos.write( ( CONTENT_BOUNDARY + "\r\n" +
          "Content-Disposition: form-data; name=\"uploadFile\"; filename=\"" +
          name
           + "\"\r\nContent-Type: " + BINDATA_CONTENTTYPE + "\r\n\r\n" ).getBytes() );

      m_bos.flush();
      return m_bos;
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    return null;
  }

  public BufferedOutputStream postInitial( byte[] buffer, int iLen, boolean sendParams)
  {
    try
    {
      m_httpConn.setDoOutput(true);
      m_httpConn.setDoInput( true);
      m_httpConn.setRequestProperty("Content-type", BINDATA_CONTENTTYPE);
      transferCookies();
      m_bos = new BufferedOutputStream( m_httpConn.getOutputStream());
      //int iRet = 0;

      if( sendParams)
      {
        String line = getParameters();
        byte[] paramBuf;
        int iParamLen;
        if( line != null)
        {
          line += "\n\n";
          paramBuf = line.getBytes();
          iParamLen = Array.getLength( paramBuf);
          m_bos.write( paramBuf, 0, iParamLen);
        }
      }

      m_bos.write( buffer, 0, iLen);
      m_bos.flush();
      return m_bos;
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    return null;
  }

  public void post( BufferedInputStream bi)
  {
    try
    {
      if( m_bos == null)
      {
        m_httpConn.setDoOutput(true);
        m_httpConn.setDoInput( true);
        transferCookies();
        m_bos = new BufferedOutputStream( m_httpConn.getOutputStream());
      }
      int iRet = 0;
      byte buffer[] = new byte[BUFFERSIZE];
      while( (iRet = bi.read( buffer, 0, BUFFERSIZE)) > 0)
      {
        m_bos.write( buffer, 0, iRet);
      }
      m_bos.flush();
      m_bos.close();
      m_bos = null;
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
  }

  public String getParameters()
  {
    // Construct data.
    String line = null;
    for( int i = 0; i < m_postParams.size(); i++)
    {
      if( i == 0)
      {
        line = URLEncoder.encode( ((String[])m_postParams.elementAt( i))[0]) + "=" + URLEncoder.encode( ((String[])m_postParams.elementAt( i))[1]);
      }
      else
        line += "&" + URLEncoder.encode( ((String[])m_postParams.elementAt( i))[0]) + "=" + URLEncoder.encode( ((String[])m_postParams.elementAt( i))[1]);
    }
    return line;
  }

  public void transferCookies()
  {
    String line = null;
    for( int i = 0; i < m_cookies.size(); i++)
    {
      line = ((String[])m_cookies.elementAt(i))[0] + "=" + ((String[])m_cookies.elementAt(i))[1];
      m_httpConn.setRequestProperty( "Cookie", line);
      //System.out.println( "Cookie: " + line);
    }
    if( m_cookies.size() > 0)
    {
      m_httpConn.setRequestProperty( "Cookie2", "$Version=\"1\"");
      //System.out.println( "Cookie2: $Version=\"1\"");
    }
  }

  public String[] getCookie()
  {
    String[] strCookie = null;
    String line = null;
    line = m_httpConn.getHeaderField("set-cookie");

    if( line != null)
    {
      StringTokenizer st = new StringTokenizer( line, "=", false);

      if( st.countTokens() >= 2)
      {
        strCookie = new String[2];
        strCookie[0] = st.nextToken();
        String str = st.nextToken();
        strCookie[1] = str.substring( 0, str.indexOf( ";"));
      }
    }
    return strCookie;
  }

  public void post()
  {
    String line = getParameters();
    try
    {
      m_httpConn.setDoOutput(true);
      m_httpConn.setDoInput( true);
      m_httpConn.setRequestProperty("Content-type", NORMAL_CONTENTTYPE);
      transferCookies();
      if( line != null)
      {
        OutputStreamWriter wr = new OutputStreamWriter( m_httpConn.getOutputStream());
        wr.write(line);
        wr.flush();
        wr.close();
      }
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
  }

  public void post( String line)
  {
    try
    {
      m_httpConn.setDoOutput(true);
      m_httpConn.setDoInput( true);
      transferCookies();
      if( line != null)
      {
        OutputStreamWriter wr = new OutputStreamWriter( m_httpConn.getOutputStream());
        wr.write(line);
        wr.flush();
        wr.close();
      }
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
  }

  public String readLine()
  {
    String line = null;
    try
    {
      if( m_brd == null)
        m_brd = new BufferedReader( new InputStreamReader( m_httpConn.getInputStream()));
      line = m_brd.readLine();
      if( line == null)
      {
        m_brd.close();
        m_brd = null;
        m_iRetries = 0;
      }
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
      disconnect();
    }
    return line;
  }

/*
  public String readLineFromInputStream()
  {
    String line = null;
    try
    {
      if( m_bis == null)
        m_bis = new DataInputStream( m_httpConn.getInputStream());
      line = m_bis.readLine();
      if( line == null)
      {
        m_bis.close();
        m_bis = null;
        m_iRetries = 0;
      }
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
      disconnect();
    }
    return line;
  }
*/

  public String readLineFromInputStream()
  {
    String line = null;
    byte buffer[] = new byte[1];
    //int iRead = 0;
    try
    {
      if( m_bis == null)
        m_bis = new BufferedInputStream( m_httpConn.getInputStream());
      int i = 0;
      int iRet = 0;
      String strNew;
      while( (iRet = m_bis.read( buffer, 0, 1)) > 0)
      {
        if( i == 0)
        {
          line = "";
          i++;
        }
        strNew = new String( buffer);
        if( strNew.equals( "\n"))
          break;
        line += strNew;
      }
      if( line == null)
      {
        m_bis.close();
        m_bis = null;
        m_iRetries = 0;
      }
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
      disconnect();
    }
    return line;
  }

  public int read( byte buffer[], int iOff, int iSize)
  {
    int iRead = 0;
    try
    {
      if( m_bis == null)
        m_bis = new BufferedInputStream( m_httpConn.getInputStream());
      iRead = m_bis.read( buffer, iOff, iSize);
      if( iRead == 0)
      {
        m_bis.close();
        m_bis = null;
        m_iRetries = 0;
      }
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
      disconnect();
    }
    return iRead;
  }

  public BufferedInputStream getBufferedInputStream()
  {
    try
    {
      if( m_bis == null)
        m_bis = new BufferedInputStream( m_httpConn.getInputStream());
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
      disconnect();
    }
    return m_bis;
  }

  public BufferedOutputStream getBufferedOutputStream()
  {
    try
    {
      m_httpConn.setDoOutput(true);
      m_httpConn.setDoInput( true);
      m_bos = new BufferedOutputStream( m_httpConn.getOutputStream());
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
      disconnect();
    }
    return m_bos;
  }

  public String getAuthorization()
  {
    // set url authentication string
    String authStr = m_appProps.getProperty( GlobalApplicationContext.URLUSER) + ":"
        + m_appProps.getProperty( GlobalApplicationContext.URLPASSWORD);
    m_auth = "Basic" + new sun.misc.BASE64Encoder().encode(authStr.getBytes());

    return m_auth;
  }

  class URLAuthenticator
  extends Authenticator
  {
    protected PasswordAuthentication getPasswordAuthentication()
    {
      boolean blURL = true;
      String strPrompt = getRequestingPrompt();
      //String strScheme = getRequestingScheme();
      //String strProt = getRequestingProtocol();
      String strPort = (new Integer( getRequestingPort())).toString();
      if( m_appProps.getBooleanProperty( GlobalApplicationContext.PROXYENABLE)
          && strPort.equals( m_appProps.getProperty( GlobalApplicationContext.PROXYPORT)))
      {
        blURL = false;
      }
      if( (blURL && (m_appProps.getProperty( GlobalApplicationContext.URLUSER) == null
          || m_appProps.getProperty( GlobalApplicationContext.URLPASSWORD) == null)
          || !blURL && (m_appProps.getProperty( GlobalApplicationContext.PROXYUSER) == null
              || m_appProps.getProperty( GlobalApplicationContext.PROXYPASSWORD) == null))
          || m_iRetries > 0)
      {
        m_authDlg = new AuthorizationDialog( null, "Authorization", true, m_appProps, AuthorizationDialog.URLAUTH, m_strAppName);
        m_authDlg.setFrame( m_frame);
        m_authDlg.setRequestText( strPrompt);
        m_authDlg.setVisible( true);
      }
      m_iRetries++;

      if( blURL)
      {
        String strPass = m_appProps.getProperty( GlobalApplicationContext.URLPASSWORD);
        return new PasswordAuthentication( m_appProps.getProperty( GlobalApplicationContext.URLUSER),
            (strPass != null ? strPass.toCharArray() : (new String("")).toCharArray()));
      }
      else
      {
        String strPass = m_appProps.getProperty( GlobalApplicationContext.PROXYPASSWORD);
        return new PasswordAuthentication( m_appProps.getProperty( GlobalApplicationContext.PROXYUSER),
            (strPass != null ? strPass.toCharArray() : (new String("")).toCharArray()));
      }
    }
  }
}


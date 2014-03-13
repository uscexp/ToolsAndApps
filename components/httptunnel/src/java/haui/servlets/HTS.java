
//package moe99;
package haui.servlets;

import haui.app.HttpTunnel.DataManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import xjava.security.Cipher;
import xjava.security.Mode;
import xjava.security.PaddingScheme;
import cryptix.provider.cipher.Blowfish;
import cryptix.provider.rsa.RawRSACipher;
import cryptix.provider.rsa.RawRSAPrivateKey;
import cryptix.provider.rsa.RawRSAPublicKey;

/**
 * Module:      HTS.java<br>
 *              $Source: $
 *<p>
 * Description: Servlet to tunnel tcp connections.<br>
 *</p><p>
 * Created:     07.03.2001 by AE
 *</p><p>
 *
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     07. March 2001
 * @history     07.03.2001	by	AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class HTS
  extends HttpServlet
{
  // constants
  public String SERVLET = "/servlet/haui.servlet.HTS";
  //public final static String BINDATA_CONTENTTYPE = "application/x-www-form-urlencoded-mixed-with-binary-data";
  public final static String BINDATA_CONTENTTYPE = "application/octet-stream";
  public final static int BUFFERSIZE = 2048;
  public final static int MAXDOWNLOADSIZE = 32768;
  public final static int MAXINACTIVEINTERVAL = 180;

  // parameter names
  public final static String ACTION = "action";
  public final static String HOSTNAME = "hostname";
  public final static String PORT = "port";

  // action parameter values
  public final static String CONNECT = "conne";
  public final static String CONNECTANDCHECKFORDATANONCONTINUOUS = "cochn";
  public final static String EXCHANGEKEY = "exkey";
  public final static String DISCONNECT = "disco";
  public final static String REQUEST = "reque";
  public final static String RESEND = "resen";
  public final static String CHECKFORDATA = "check";
  public final static String CHECKFORDATANONCONTINUOUS = "chnco";
  public final static String RESPONSE = "respo";
  public final static String SUCCESS = "succe";
  public final static String ERROR = "error";

  public void init(ServletConfig cfig)
    throws ServletException
  {
    super.init(cfig);
  }

  /**
   *Gets the ServletInfo attribute of the HttpTunnelServlet object
   *
   * @return    The ServletInfo value
   */
  public String getServletInfo()
  {
    return "A http tunnel servlet";
  }


  /**
   * This method handles the "POST" submission.
   *
   * @param  req                   Request from client
   * @param  res                   Response to client
   * @exception  ServletException  Thrown on servlet error
   * @exception  IOException       Thrown on io error
   */
  public void doPost( HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
  {
    executeAction( req, res );
  }


  /**
   * This method handles the GET requests for the client.
   *
   * @param  req                   Request from client
   * @param  res                   Response to client
   * @exception  ServletException  Thrown on servlet error
   * @exception  IOException       Thrown on io error
   */
  public void doGet( HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
  {
    executeAction( req, res );
  }


  /**
   * This method handles the GET and POST requests for the client.
   *
   * @param  req                   Request from client
   * @param  res                   Response to client
   * @exception  ServletException  Thrown on servlet error
   * @exception  IOException       Thrown on io error
   */
  private void executeAction( HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
  {
    String action;
    String reaction = ERROR;
    String host;
    String strPort;
    int port = 0;
    String test = "";
    int iLen = 0;
    byte buf[] = new byte[BUFFERSIZE];
    boolean blOutput = true;
    boolean blTimeout = false;
    DataManager dm = null;
    Hashtable fields = new Hashtable();
    ByteArrayOutputStream inBaos = new ByteArrayOutputStream();


    SERVLET = req.getRequestURL().toString();
    ServletOutputStream out = res.getOutputStream();


    ConnectionData cd = null;
    HttpSession ssn = null;

    try
    {
      String content = req.getContentType();
      parseInput( req, res, fields, inBaos);

      res.setHeader("Cache-Control", "no-cache");
      res.setHeader("Pragma", "no-cache");
      res.setDateHeader("Expires", 0);
      res.setContentType( BINDATA_CONTENTTYPE);

      ssn = req.getSession( false );
      if( ssn == null)
        dm = new DataManager( inBaos.toByteArray(), inBaos.size(), cd.getInCipher());
      else
      {
        cd = getCD( ssn);
        if( cd != null)
          dm = cd.getDataManager( inBaos.toByteArray(), inBaos.size());
        else
          dm = new DataManager( inBaos.toByteArray(), inBaos.size(), cd.getInCipher());
      }

      action = dm.getAction();
      host = dm.getRemoteHost();
      strPort = String.valueOf( dm.getRemotePort());
      //System.out.println();
      //System.out.println("--------------Receive---------");
      //System.out.println( dm.getMode());
      //System.out.println( dm.getHeader());
      //System.out.println( new String( dm.getInputBuffer()));
      //System.out.println("------------------------------");

      if( action == null)
        throw new ServletException( "No action specified!");

      if( strPort != null)
      {
        port = Integer.parseInt( strPort);
      }

      // get the session
      if( action != null )
      {
        if( action.equalsIgnoreCase( EXCHANGEKEY ))
          ssn = req.getSession( true );
        else if( action.equalsIgnoreCase( CONNECT ) || action.equalsIgnoreCase( CONNECTANDCHECKFORDATANONCONTINUOUS ))
        {
          ssn = req.getSession( false );
          if( ssn == null)
            ssn = req.getSession( true );
          else
          {
            cd = getCD( ssn );

            if( cd == null )
              throw new ServletException( "No ConnectionData object found!");
          }
        }
        else
        {
          ssn = req.getSession( false );
          cd = getCD( ssn );

          if( ssn == null )
            throw new ServletException( "Servlet session lost!");

          if( cd == null )
            throw new ServletException( "No ConnectionData object found!");
        }
      }

      // ***** parse actions *****
      if( action != null && ssn != null )
      {
        if( action.equalsIgnoreCase( EXCHANGEKEY ) )
        {
          // initial login
          cd = prepareCryptSession( cd, ssn, req, dm.getMode());
          if( dm.isBufferLengthCorrect() && dm.isCheckSumCorrect())
          {
            buf = cd.generatePublicKey( dm.getInputBuffer());
            iLen = Array.getLength( buf);
            dm = null;
            reaction = EXCHANGEKEY;
          }
          else
            throw new ServletException( "Corrupted data received!");
        }
        else if( action.equalsIgnoreCase( CONNECT ) )
        {
          // initial login
          cd = doLogin( host, port, cd, ssn, req);
          if( dm.isBufferLengthCorrect() && dm.isCheckSumCorrect())
          {
            cd.write( dm.getInputBuffer(), dm.getRealInputBufferLength());
            dm = null;
            reaction = SUCCESS;
          }
          else
          {
            iLen = 0;
            buf[0] = 0;
            dm = null;
            reaction = RESEND;
          }
        }
        else if( action.equalsIgnoreCase( CONNECTANDCHECKFORDATANONCONTINUOUS ) )
        {
          // initial login
          cd = doLogin( host, port, cd, ssn, req);
          if( dm.isBufferLengthCorrect() && dm.isCheckSumCorrect())
          {
            cd.write( dm.getInputBuffer(), dm.getRealInputBufferLength());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while( (iLen = cd.read( buf, 0, BUFFERSIZE)) >= 0)
            {
              if( iLen == 0)
              {
                Thread.sleep( 100 );
                continue;
              }

              baos.write( buf, 0, iLen);
              baos.flush();
              if( cd.available() == 0)
                Thread.sleep( 200);
            }
            buf = baos.toByteArray();
            iLen = baos.size();
            dm = null;
            reaction = RESPONSE;
          }
          else
          {
            iLen = 0;
            buf[0] = 0;
            dm = null;
            reaction = RESEND;
          }
        }
        else if( action.equalsIgnoreCase( CHECKFORDATA ))
        {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          int iDlSize = 0;
          while( cd.available() > 0)
          {
            if( (iLen = cd.read( buf, 0, BUFFERSIZE)) >= 0)
            {
              if( iLen == 0)
              {
                Thread.sleep( 100 );
                continue;
              }

              baos.write( buf, 0, iLen);
              baos.flush();
            }
            iDlSize += iLen;
            if( iDlSize >= MAXDOWNLOADSIZE)
              break;
            if( cd.available() <= 0)
              Thread.sleep( 200);
          }
          buf = baos.toByteArray();
          iLen = baos.size();
          dm = null;
          reaction = RESPONSE;
        }
        else if( action.equalsIgnoreCase( CHECKFORDATANONCONTINUOUS ))
        {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          while( (iLen = cd.read( buf, 0, BUFFERSIZE)) >= 0)
          {
            if( iLen == 0)
            {
              Thread.sleep( 100 );
              continue;
            }

            baos.write( buf, 0, iLen);
            baos.flush();
            if( cd.available() == 0)
              Thread.sleep( 200);
          }
          buf = baos.toByteArray();
          iLen = baos.size();
          dm = null;
          reaction = RESPONSE;
        }
        else if( action.equalsIgnoreCase( REQUEST ))
        {
          if( dm.isBufferLengthCorrect() && dm.isCheckSumCorrect())
          {
            //System.out.println();
            //System.out.write( dm.getInputBuffer(), 0, dm.getRealInputBufferLength());
            //System.out.println();
            cd.write( dm.getInputBuffer(), dm.getRealInputBufferLength());
            dm = null;
            reaction = SUCCESS;
          }
          else
          {
            iLen = 0;
            buf[0] = 0;
            dm = null;
            reaction = RESEND;
          }
        }
        else if( action.equalsIgnoreCase( RESEND ))
        {
          if( cd.getHistDm() != null)
          {
            dm = cd.getHistDm();
            reaction = RESPONSE;
          }
        }
        else if( action.equalsIgnoreCase( DISCONNECT ))
        {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          while( cd.available() > 0)
          {
            if( (iLen = cd.read( buf, 0, BUFFERSIZE)) >= 0)
            {
              if( iLen == 0)
              {
                Thread.sleep( 100 );
                continue;
              }

              baos.write( buf, 0, iLen);
              baos.flush();
            }
            if( cd.available() <= 0)
              Thread.sleep( 200);
          }
          buf = baos.toByteArray();
          iLen = baos.size();
          dm = null;
          reaction = DISCONNECT;
          cd.closeAll();
          if( req.getSession( false ) != null)
            ssn.invalidate();
        }
        else
        {
          buf = null;
          iLen = 0;
          dm = null;
          reaction = DISCONNECT;
          cd.closeAll();
          if( req.getSession( false ) != null)
            ssn.invalidate();
        }
        /*
        if( iLen <= 0 && !reaction.equalsIgnoreCase( DISCONNECT)  && !reaction.equalsIgnoreCase( SUCCESS)
          && !action.equalsIgnoreCase( RESEND ) && !action.equalsIgnoreCase( CONNECT ))
        {
          reaction = DISCONNECT;
          ssn.invalidate();
        }
        */
        if( !action.equalsIgnoreCase( EXCHANGEKEY ) && !cd.checkConnection())
        {
          reaction = DISCONNECT;
          //if( req.getSession( false ) != null)
            //ssn.invalidate();
        }
        if( blOutput)
        {
          if( dm == null)
            //dm = new DataManager( reaction, buf, iLen, null, 0, ssn.getId(), cd.getMode());
            dm = cd.getDataManager( reaction, buf, iLen, ssn.getId());
          //System.out.println();
          //System.out.println("--------------Send------------");
          //System.out.println( dm.getMode());
          //System.out.println( dm.getHeader());
          //System.out.println( new String( dm.getInputBuffer()));
          //System.out.println("------------------------------");
          cd.setHistDm( dm);
          out.write( dm.getBody(), 0, dm.getBodyLength());
          out.flush();
        }
        if( action.equalsIgnoreCase( CHECKFORDATANONCONTINUOUS ))
        {
          cd.closeAll();
          if( req.getSession( false ) != null)
            ssn.invalidate();
        }
      }
    }
    catch( Exception ex )
    {
      StringWriter sw = new StringWriter();
      ex.printStackTrace();
      ex.printStackTrace( new PrintWriter( sw));
      if( cd != null)
        sendError( test + ex.toString() + "\n" + sw.toString() + "\n", out, cd);
      else
        sendError( test + ex.toString() + "\n" + sw.toString() + "\n", out, null);
    }
    finally
    {
      out.close();
    }
  }

  private void sendError( String error, ServletOutputStream out, ConnectionData cd)
    throws IOException
  {
    byte[] bErr = error.getBytes();

    DataManager dm = null;

    if( cd == null)
      dm = new DataManager( ERROR, bErr, Array.getLength( bErr), null, 0, null, DataManager.COMP, null);
    else
      dm = cd.getDataManager( ERROR, bErr, Array.getLength( bErr), null);

    out.write( dm.getBody(), 0, dm.getBodyLength());
  }

  private void ReadAndSend( HttpServletRequest req, HttpServletResponse res)
    throws IOException
  {
    int iLen;
    byte buf[] = new byte[BUFFERSIZE];
    ServletOutputStream out = res.getOutputStream();

    ServletInputStream in = req.getInputStream();

    while( (iLen = in.read( buf, 0, BUFFERSIZE)) >= 0)
    {
      if( iLen == 0)
        continue;

      out.write( buf, 0, iLen);
    }
    in.close();
  }

  private void parseInput( HttpServletRequest req, HttpServletResponse res, Hashtable fields, ByteArrayOutputStream inBaos)
    throws IOException, ServletException
  {
    int iLen;
    byte buf[] = new byte[BUFFERSIZE];
    boolean blVPParsed = false;
    String str = null;

    ServletInputStream in = req.getInputStream();

    while( (iLen = in.read( buf, 0, BUFFERSIZE)) >= 0)
    {
      if( iLen == 0)
        continue;

      if( !blVPParsed)
      {
        str = new String( buf, 0, iLen);
        int idx = str.indexOf( "\n\n");
        if( idx == -1)
          throw new ServletException( "Parsing error: No '\\n\\n' found!");
        str = str.substring( 0, idx);
        StringTokenizer stVP = new StringTokenizer( str, "&", false);

        if( stVP.countTokens() == 0)
          throw new ServletException( "Parsing error: No name, value pairs found!");
        while( stVP.hasMoreTokens())
        {
          String nvp = stVP.nextToken();
          StringTokenizer stElem = new StringTokenizer( nvp, "=", false);

          if( stElem.countTokens() != 2)
            throw new ServletException( "Parsing error: Element " + nvp + " has less than two tokens!");
          while( stElem.countTokens() == 2)
          {
            String name = stElem.nextToken();
            String value = stElem.nextToken();
            fields.put( name, value);
          }
        }
        if( iLen >= idx+2)
        {
          blVPParsed = true;
          inBaos.write( buf, idx+2, iLen-idx-2);
        }
      }
      else
        inBaos.write( buf, 0, iLen);
    }
    in.close();
  }

  /**
   * Prepare the crypted session
   *
   * @param  host             host
   * @param  port             port
   * @param  cd              Session user data
   * @param  ssn              HttpSession
   * @param  req              Request from client
   * @return                  ConnectionData
   * @exception  IOException  Thrown on io error
   */
  private ConnectionData prepareCryptSession( ConnectionData cd, HttpSession ssn, HttpServletRequest req, int mode)
    throws IOException
  {
    // close old session
    if( cd != null)
    {
      if( req.getSession( false ) != null)
        ssn.invalidate();
      cd = null;
    }

    // create
    ssn = req.getSession( true );
    cd = new ConnectionData();
    ssn.setAttribute( "httptunnelservlet", cd );
    ssn.setMaxInactiveInterval( MAXINACTIVEINTERVAL);

    cd.setMode( mode);

    return cd;
  }

  /**
   * Logs in to the specified server
   *
   * @param  host             host
   * @param  port             port
   * @param  cd              Session user data
   * @param  ssn              HttpSession
   * @param  req              Request from client
   * @return                  ConnectionData
   * @exception  IOException  Thrown on io error
   */
  private ConnectionData doLogin( String host, int port, ConnectionData cd, HttpSession ssn, HttpServletRequest req)
    throws Exception
  {
    // close old session
    if( cd != null && !( cd.getMode() == DataManager.CRYPT || cd.getMode() == DataManager.COMPCRYPT))
    {
      if( req.getSession( false ) != null)
        ssn.invalidate();
      cd = null;
    }

    // create
    if( cd == null)
    {
      ssn = req.getSession( true );
      cd = new ConnectionData();
      ssn.setAttribute( "httptunnelservlet", cd );
    }
    ssn.setMaxInactiveInterval( MAXINACTIVEINTERVAL);

    // save stuff into MUD
    cd.setHost( host);
    cd.setPort( port );
    cd.init();

    return cd;
  }


  // utility method; retrieve the ConnectionData
  // from the HttpSession and return it
  /**
   *Gets the ConnectionData of the HttpTunnelServlet session object
   *
   * @param  ssn              session
   * @return                  The ConnectionData value
   * @exception  IOException  Exception
   */
  private ConnectionData getCD( HttpSession ssn )
    throws IOException
  {
    ConnectionData cd = null;

    if( ssn == null )
    {
      return null;
    }
    else
    {
      if( ( cd = (ConnectionData)ssn.getAttribute( "httptunnelservlet" ) ) == null )
      {
        return null;
      }
    }
    return cd;
  }
}

/**
 * This class is used to store session data for each connection. It
 * is stored in the HttpSession.
 *
 * @author     Andreas Eisenhauer
 * @created    26. Oktober 2001
 */
class ConnectionData
  extends Object
{
  // constants
  public static final byte[] keyBytes = new byte[] {
    -84,-19,0,5,115,114,0,33,99,114,121,112,116,105,120,46,
    112,114,111,118,105,100,101,114,46,107,101,121,46,82,97,119,
    83,101,99,114,101,116,75,101,121,-40,-6,56,25,50,127,95,
    18,2,0,0,120,114,0,27,99,114,121,112,116,105,120,46,
    112,114,111,118,105,100,101,114,46,107,101,121,46,82,97,119,
    75,101,121,-122,59,33,83,-105,76,-2,56,2,0,2,76,0,
    9,97,108,103,111,114,105,116,104,109,116,0,18,76,106,97,
    118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,91,
    0,4,100,97,116,97,116,0,2,91,66,120,112,116,0,8,
    66,108,111,119,102,105,115,104,117,114,0,2,91,66,-84,-13,
    23,-8,6,8,84,-32,2,0,0,120,112,0,0,0,16,66,
    102,11,118,-112,9,-117,77,-46,-98,-64,93,20,116,46,108,
  };
  public final static int BUFFERSIZE = 2048;
  public final static int LOCKTIMEOUT = 1000;
  public final static int TIMEOUT = 120000;

  // member variables
  private String m_host;
  private int m_port;
  private Socket m_socket = null;
  private BufferedInputStream m_bis = null;
  private BufferedOutputStream m_bos = null;
  private byte m_buf[] = new byte[BUFFERSIZE];
  boolean m_locked = false;
  private byte[] m_reqData;
  private int m_reqSize;
  private byte[] m_serverData;
  private int m_serverSize;
  private DataManager m_histDm;
  private boolean m_blCompressed = false;
  private int m_iMode = 0;
  private Cipher m_inCipher = null;
  private Cipher m_outCipher = null;
  private KeyPairGenerator m_keyPairGen;
  private KeyPair m_keyPair;
  private RawRSAPrivateKey m_privKey;
  private RawRSAPublicKey m_pubKey;
  private RawRSAPublicKey m_clientPubKey;

  /**
   *Constructor for the ConnectionData object
   */
  public ConnectionData()
  {
  }

  /**
   *Sets the host attribute of the ConnectionData object
   *
   * @param  s  The new host value
   */
  public void setHost( String s )
  {
    m_host = s;
  }

  /**
   *Sets the port attribute of the ConnectionData object
   *
   * @param  i  The new port value
   */
  public void setPort( int i )
  {
    m_port = i;
  }

  /**
   *Sets the reqData attribute of the ConnectionData object
   *
   * @param  i  The new reqData value
   */
  public void setReqData( byte[] reqData )
  {
    m_reqSize = Array.getLength( reqData);
    //m_reqData = new byte[m_reqSize];
    m_reqData = reqData;
  }

  /**
   *Gets the host attribute of the ConnectionData object
   *
   * @return    The host value
   */
  public String getHost()
  {
    return m_host;
  }

  /**
   *Gets the port attribute of the ConnectionData object
   *
   * @return    The port value
   */
  public int getPort()
  {
    return m_port;
  }

  /**
   *Gets the blCompressed attribute of the ConnectionData object
   *
   * @return    The blCompressed value
   */
  public boolean isCompressed()
  {
    return m_blCompressed;
  }

  /**
   *Sets the blCompressed attribute of the ConnectionData object
   *
   * @param  blCompressed The blCompressed attribute
   */
  public void setCompressed( boolean blCompressed)
  {
   m_blCompressed = blCompressed;
  }

  /**
   *Gets the iMode attribute of the ConnectionData object
   *
   * @return    The iMode value
   */
  public int getMode()
  {
    return m_iMode;
  }

  /**
   *Sets the iMode attribute of the ConnectionData object
   *
   * @param  mode The iMode attribute
   */
  public void setMode( int mode)
  {
   m_iMode = mode;
  }

  /**
   *Sets the history DataManager of the ConnectionData object
   *
   * @param  histDm The history DataManager
   */
  public void setHistDm( DataManager histDm)
  {
   m_histDm = histDm;
  }

  /**
   *Gets the history DataManager of the ConnectionData object
   *
   * @return    The history DataManager
   */
  public DataManager getHistDm()
  {
    return m_histDm;
  }

  public byte[] generatePublicKey( byte[] b)
    throws Exception
  {
    //Security.addProvider( new cryptix.provider.Cryptix());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream( baos);

    // generate Cipher objects for encoding and decoding
    m_inCipher = Cipher.getInstance( new RawRSACipher(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));
    m_outCipher = Cipher.getInstance( new RawRSACipher(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));

    m_keyPairGen = KeyPairGenerator.getInstance("RSA", "Cryptix");
    m_keyPairGen.initialize( 1024);
    m_keyPair = m_keyPairGen.generateKeyPair();
    m_privKey = (RawRSAPrivateKey)m_keyPair.getPrivate();
    m_pubKey = (RawRSAPublicKey)m_keyPair.getPublic();

    ByteArrayInputStream bais = new ByteArrayInputStream( b);
    ObjectInputStream ois = new ObjectInputStream( bais);
    m_clientPubKey = (RawRSAPublicKey)ois.readObject();
    ois.close();

    m_inCipher.initDecrypt(m_privKey);
    m_outCipher.initEncrypt( m_clientPubKey);
    //System.out.println();
    //System.out.println(m_clientPubKey.toString());

    oos.writeObject( m_pubKey);
    oos.flush();
    oos.close();
    return baos.toByteArray();
  }

  public DataManager getDataManager( String action, byte[] buf, int bufLen, String sessionId)
    throws IOException
  {
    DataManager dm = new DataManager( action, buf, bufLen, null, 0, sessionId, m_iMode, m_outCipher);
    return dm;
  }

  public DataManager getDataManager( byte[] body, int bodyLen)
    throws IOException
  {
    DataManager dm = new DataManager( body, bodyLen, m_inCipher);
    m_iMode = dm.getMode();
    return dm;
  }

  public void init()
    throws Exception
  {
    if( m_socket == null)
      m_socket = new Socket( m_host, m_port);

    if( m_bos == null)
      m_bos = new BufferedOutputStream( m_socket.getOutputStream());

    if( m_bis == null)
    {
      m_bis = new BufferedInputStream( m_socket.getInputStream());
    }

    if( m_inCipher == null || m_outCipher == null)
    {
      //Security.addProvider( new cryptix.provider.Cryptix());
      Key key;

      ByteArrayInputStream bais = new ByteArrayInputStream( keyBytes);
      ObjectInputStream ois = new ObjectInputStream( bais);
      key = (Key)ois.readObject();
      ois.close();

      // generate Cipher objects for encoding and decoding
      m_inCipher = Cipher.getInstance( new Blowfish(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));
      m_outCipher = Cipher.getInstance( new Blowfish(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));

      m_inCipher.initDecrypt( key);
      m_outCipher.initEncrypt( key);
    }
  }

  public static Cipher getInCipher()
    throws Exception
  {
    //Security.addProvider( new cryptix.provider.Cryptix());
    Key key;

    ByteArrayInputStream bais = new ByteArrayInputStream( keyBytes);
    ObjectInputStream ois = new ObjectInputStream( bais);
    key = (Key)ois.readObject();
    ois.close();

    // generate Cipher objects for encoding and decoding
    Cipher inCipher = Cipher.getInstance( new Blowfish(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));

    inCipher.initDecrypt( key);

    return inCipher;
  }

  public void closeAll()
    throws IOException
  {
    if( m_bos != null)
    {
      m_bos.flush();
      m_bos.close();
      m_bos = null;
    }
    if( m_bis != null)
    {
      m_bis.close();
      m_bis = null;
    }
    if( m_socket != null)
    {
      m_socket.close();
      m_socket = null;
    }
  }

  public void write( byte[] b, int iSize)
    throws IOException
  {
    if( m_bos == null)
      return;
    if( iSize > 0)
    {
      if( b == null)
      {
        m_bos.write( new String( "0").getBytes(), 0, 0);
      }
      else
        m_bos.write( b, 0, iSize);
      m_bos.flush();
    }
  }

  public boolean hasMoreData()
  {
    if( m_serverSize > 0)
      return true;
    else
      return false;
  }

  public int available()
    throws IOException
  {
    return m_bis.available();
  }

  public boolean checkConnection()
  {
    boolean blRet = true;
    try
    {
      if( m_bos == null || m_bis == null)
        blRet = false;
      else if( m_bis.available() < 0)
        blRet = false;
    }
    catch( Exception ex)
    {
      blRet = false;
    }
    return blRet;
  }

  public int read( byte[] b, int off, int iSize)
    throws IOException
  {
    int iLen = 0;

    // receive server data
    if( m_bis != null)
      iLen = m_bis.read( b, off, iSize);
    return iLen;
  }
}

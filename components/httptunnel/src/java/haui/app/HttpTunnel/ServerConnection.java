package haui.app.HttpTunnel;

import java.net.*;
import java.io.*;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.util.*;
import java.lang.reflect.Array;
import haui.asynch.Mutex;
import haui.components.ConnectionManager;
import haui.util.AppProperties;
import haui.util.GlobalAppProperties;

import javax.swing.JTextArea;
import java.util.zip.*;
import java.security.*;
import java.security.interfaces.*;
import xjava.security.*;
import xjava.security.interfaces.*;
import cryptix.provider.rsa.*;
import cryptix.provider.cipher.Blowfish;

/**
 *		Module:					ServerConection.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\HttpTunnel\\ServerConnection.java,v $
 *<p>
 *		Description:    JPanel to config ConnectionMap(s).<br>
 *</p><p>
 *		Created:				06.12.2001	by	AE
 *</p><p>
 *		@history				06.12.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: ServerConnection.java,v $
 *		Revision 1.2  2004-08-31 16:03:08+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.1  2004-02-17 16:27:36+01  t026843
 *		<>
 *
 *		Revision 1.0  2003-05-28 14:21:26+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2001; $Revision: 1.2 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\HttpTunnel\\ServerConnection.java,v 1.2 2004-08-31 16:03:08+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ServerConnection
  extends Thread
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
  public final static int LOCKTIMEOUT = 1000;
  public final static int BUFFERSIZE = 2048;
  public final static String EXCHANGEKEY = "exkey";
  public final static String CONNECT = "conne";
  public final static String CONNECTANDCHECKFORDATANONCONTINUOUS = "cochn";
  public final static String DISCONNECT = "disco";
  public final static String REQUEST = "reque";
  public final static String RESEND = "resen";
  public final static String CHECKFORDATA = "check";
  public final static String CHECKFORDATANONCONTINUOUS = "chnco";
  public final static String RESPONSE = "respo";
  public final static String SUCCESS = "succe";
  public final static String ERROR = "error";
  public final static int TIMEOUT = 30000;

  // parameter names
  public final static String ACTION = "action";
  public final static String HOSTNAME = "hostname";
  public final static String PORT = "port";

  // member variables
  AppProperties m_appProps;
  private Socket m_socket = null;
  private Mutex m_mutLock = new Mutex();
  private ConnectionMap m_cm;
  private BufferedInputStream m_bis = null;
  private BufferedOutputStream m_bos = null;
  private byte m_buf[] = new byte[BUFFERSIZE];
  private byte m_serverBuf[] = new byte[BUFFERSIZE];
  private byte[] m_resData;
  private int m_resSize;
  private String m_servletUrl;
  private JTextArea m_jTextAreaOut;
  private DataManager m_histDm;
  private String m_sessionId = null;
  private String[] m_cookie = null;
  private boolean m_blCompressed = false;
  private boolean m_blSendCompressed = true;
  private InflaterInputStream m_iis;
  private InputStream m_serverIs;
  private boolean m_blCrypted = false;
  private int m_iMode = 0;
  private Cipher m_inCipher;
  private Cipher m_outCipher;
  private KeyPairGenerator m_keyPairGen;
  private KeyPair m_keyPair;
  private RawRSAPrivateKey m_privKey;
  private RawRSAPublicKey m_pubKey;
  private RawRSAPublicKey m_serverPubKey;
  private HttpTunnelClientServer m_htcs;
  private String m_status = "";
  private long m_lTotBytes = 0;
  private InputReader m_serverIr = null;
  private InputReader m_ir = null;
  boolean m_blStop = false;

  public ServerConnection( Socket socket, ConnectionMap cm, String servletUrl, JTextArea ta, AppProperties appProps)
  {
    super( "ServerConection");
    m_socket = socket;
    m_cm = cm;
    if( m_cm.sendCompressed() && m_cm.isCrypted())
      m_iMode = DataManager.COMPCRYPT;
    else if( m_cm.sendCompressed())
      m_iMode = DataManager.COMP;
    else if( m_cm.isCrypted())
      m_iMode = DataManager.CRYPT;
    m_blSendCompressed = m_cm.sendCompressed();
    m_servletUrl = servletUrl;
    m_jTextAreaOut = ta;
    m_appProps = appProps;
    Component c[] = ((JFrame)(GlobalAppProperties.instance().getRootComponent( HttpTunnelClientServer.APPNAME))).getContentPane().getComponents();
    for( int i = 0; i < Array.getLength( c); ++i)
    {
      if( c[i] instanceof HttpTunnelClientServer)
      {
          m_htcs = (HttpTunnelClientServer)c[i];
          break;
      }
    }
    m_jTextAreaOut.append( "Connected on port " + String.valueOf( cm.getLocalPort()) + ".\n");
    //System.out.println( "Connected on port " + String.valueOf( cm.getLocalPort()) + ".");
  }

  public ConnectionMap getConnectionMap()
  {
    return m_cm;
  }

  protected void finalize()
    throws Throwable
  {
    closeAll();
  }

  public String getStatus()
  {
    return m_status;
  }

  public long getTotalBytesReceived()
  {
    return m_lTotBytes;
  }

  private void closeAll()
  {
    try
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
      if( m_serverIs != null)
      {
        m_serverIs.close();
        m_serverIs = null;
      }
      if( m_socket != null)
      {
        m_socket.close();
        m_jTextAreaOut.append( "Connection closed on port " + String.valueOf( m_cm.getLocalPort()) + ".\n");
        //System.out.println( "Connection closed on port " + String.valueOf( m_cm.getLocalPort()) + ".");
        m_socket = null;
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
    m_blStop = true;
    if( m_serverIr != null)
    {
      m_serverIr.stopIt();
    }
    if( m_ir != null)
    {
      m_ir.stopIt();
    }
    closeAll();
    stop();
    m_htcs.removeConnection( this);
  }

  public void interrupt()
  {
    m_blStop = true;
    if( m_serverIr != null)
    {
      m_serverIr.interrupt();
    }
    if( m_ir != null)
    {
      m_ir.interrupt();
    }
    closeAll();
    super.interrupt();
    if( isAlive())
    {
      try
      {
        Thread.sleep( 3000);
        if( isAlive())
          super.stop();
      }
      catch( InterruptedException iex)
      {
        iex.printStackTrace();
      }
    }
  }

  public void run()
  {
    m_htcs.addConnection( this);
    try
    {
      int iLen = 0;
      boolean blStart = true;
      boolean blDisconnect = false;
      boolean blContinuous = true;
      boolean blMustPoll = true;
      ConnectionManager connMgr = null;
      String[] cookie = null;

      m_bis = new BufferedInputStream( m_socket.getInputStream());
      m_bos = new BufferedOutputStream( m_socket.getOutputStream());
      if( /* m_cm.mustPoll() */ true)
      {
        m_ir = new InputReader( m_bis, m_jTextAreaOut, false);
        m_ir.start();
      }

      if( m_cm.isCrypted())
      {
        Security.addProvider( new cryptix.provider.Cryptix());
        Key key;

        ByteArrayInputStream bais = new ByteArrayInputStream( keyBytes);
        ObjectInputStream ois = new ObjectInputStream( bais);
        key = (Key)ois.readObject();
        ois.close();

        // generate Cipher objects for encoding and decoding
        //m_inCipher = Cipher.getInstance( "Blowfish", "Cryptix");
        //m_outCipher = Cipher.getInstance( "Blowfish", "Cryptix");
        m_inCipher = Cipher.getInstance( new Blowfish(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));
        m_outCipher = Cipher.getInstance( new Blowfish(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));

        m_inCipher.initDecrypt( key);
        m_outCipher.initEncrypt( key);
        m_blCrypted = true;
      }
      /* Public key exchange. Works (slow) but not with mycgiserver!
      if( m_cm.isCrypted())
      {
        Security.addProvider( new cryptix.provider.Cryptix());
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

        connMgr = BuildRequest( EXCHANGEKEY, m_cookie, m_sessionId);
        oos.writeObject( m_pubKey);
        oos.flush();
        oos.close();
        byte b[] = baos.toByteArray();
        iLen = baos.size();
        DataManager dm = new DataManager( EXCHANGEKEY, b, iLen, null, 0, m_sessionId, DataManager.COMP, m_outCipher);
        connMgr.openConnection();
        connMgr.getHttpURLConnection().setUseCaches( false);
        connMgr.post( dm.getBody(), dm.getBodyLength(), true);
        m_jTextAreaOut.append( "Exchange keys: Send " + String.valueOf( dm.getRealInputBufferLength()) + " bytes to " + m_servletUrl + "\n");
        DataManager resDm = readHeader( connMgr.getBufferedInputStream());

        if( resDm.getAction().equalsIgnoreCase( EXCHANGEKEY))
        {
          baos.reset();
          while( (iLen = m_serverIs.read( m_serverBuf, 0, BUFFERSIZE)) > 0)
          {
            m_jTextAreaOut.append( "Received " + String.valueOf( iLen)
                + " bytes from " + m_servletUrl + "\n");
            //System.out.write( m_serverBuf, 0, iLen);
            baos.write( m_serverBuf, 0, iLen);
            baos.flush();
          }
          ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray());
          ObjectInputStream ois = new ObjectInputStream( bais);
          m_serverPubKey = (RawRSAPublicKey)ois.readObject();
          ois.close();
          //System.out.println();
          //System.out.println( m_serverPubKey.toString());

          m_inCipher.initDecrypt( m_privKey);
          m_outCipher.initEncrypt( m_serverPubKey);
          m_blCrypted = true;
        }
        else if( resDm.getAction().equalsIgnoreCase( ERROR))
        {
          byte resBuf[] = new byte[BUFFERSIZE];
          int iResLen = 0;
          baos.reset();
          baos = new ByteArrayOutputStream();
          if( resDm.getRealInputBufferLength() > 0)
          {
            baos.write( resDm.getInputBuffer(), 0 , resDm.getInputBufferLength());
          }
          while( (iResLen = m_serverIs.read( resBuf, 0, BUFFERSIZE)) > 0)
          {
            baos.write( resBuf, 0 , iResLen);
          }
          blDisconnect = true;
          String str = null;
          if( resDm.getInputBufferLength() > 0)
            str = new String( baos.toByteArray());
          if( str != null)
            m_jTextAreaOut.append( "ERROR: " + str + "\n");
          else
            m_jTextAreaOut.append( "ERROR: Unknown server error occured!\n");
          System.out.println( "REMOTE ERROR: " + str);
          m_outCipher = null;
          m_inCipher = null;
          blContinuous = false;
        }
        else
        {
          m_outCipher = null;
          m_inCipher = null;
          blContinuous = false;
        }
        connMgr.disconnect();
      }
      */

      do
      {
        ByteArrayOutputStream irBaos = new ByteArrayOutputStream();
        boolean blOutput = false;
        byte[] body = null;
        String action = null;
        Thread.sleep( 500);
        if( m_serverIr != null)
        {
          do
          {
            if( m_serverIr != null && !m_serverIr.hasMoreData())
              sleep( 200);
            iLen = m_serverIr.read( m_serverBuf, BUFFERSIZE);
            if( iLen > 0)
            {
              m_lTotBytes += iLen;
              m_jTextAreaOut.append( "Received " + String.valueOf( iLen)
                  + " bytes from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
              //System.out.write( m_serverBuf, 0, iLen);
              m_bos.write( m_serverBuf, 0, iLen);
              m_bos.flush();
              blMustPoll = m_cm.mustPoll();
            }
            else
            {
              if( !m_serverIr.isAlive() && !m_serverIr.hasMoreData())
              {
                m_serverIr.closeAll();
                m_serverIr = null;
                if( !blMustPoll)
                  blContinuous = false;
              }
            }
            if( m_serverIr != null && !m_serverIr.hasMoreData())
              sleep( 200);
          }while( m_serverIr != null && m_serverIr.hasMoreData());
        }
        if( m_blStop || !blContinuous || !checkConnection())
        {
          blDisconnect = true;
          connMgr = BuildRequest( DISCONNECT, m_cookie, m_sessionId);
          byte b[] = new byte[1];
          b[0] = 0;
          DataManager dm = new DataManager( DISCONNECT, b, 1, m_cm.getRemoteAddr(), m_cm.getRemotePort(), m_sessionId, m_iMode, m_outCipher);
          connMgr.openConnection();
          connMgr.getHttpURLConnection().setUseCaches( false);
          connMgr.post( dm.getBody(), dm.getBodyLength(), true);
          m_jTextAreaOut.append( "Disconnecting: Send " + String.valueOf( dm.getRealInputBufferLength()) + " bytes to " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
          connMgr.disconnect();
          m_ir.closeAll();
          m_ir = null;
          //m_ir.stop();
          closeAll();
          break;
        }
        if( /* m_cm.mustPoll() */ true)
        {
          iLen = 0;
          if( m_ir != null)
          {
            do
            {
              iLen = m_ir.read( m_buf, BUFFERSIZE);
              irBaos.write( m_buf, 0, iLen);
              if( !m_ir.hasMoreData())
                sleep( 100);
            }while( m_ir.hasMoreData());
          }
        }
        else
        {
          while( m_bis.available() > 0 && (iLen = m_bis.read( m_buf, 0, BUFFERSIZE)) >= 0)
          {
            irBaos.write( m_buf, 0, iLen);
            irBaos.flush();
            if( m_bis.available() == 0)
              sleep( 100);
          }
        }
        if( /*irBaos.size() > 0 ||*/ blContinuous || blStart)
        {
          if( blStart)
          {
            blStart = false;
            /*
            if( m_cm.mustPoll())
              action = CONNECT;
            else
              action = CONNECTANDCHECKFORDATANONCONTINUOUS;
            */
            action = CONNECT;
          }
          else if( irBaos.size() <= 0)
          {
            if( m_ir != null && !m_ir.isAlive())
            {
              action = DISCONNECT;
              blOutput = false;
              m_buf[0] = 0;
              iLen = 1;
              irBaos.write( m_buf, 0, iLen);
            }
            else if( m_serverIr == null)
            {
              blOutput = false;
              m_buf[0] = 0;
              iLen = 1;
              irBaos.write( m_buf, 0, iLen);
              //if( !m_cm.mustPoll())
                //blContinuous = false;
              action = CHECKFORDATA;
              /*
              if( blContinuous)
                action = CHECKFORDATA;
              else
                action = CHECKFORDATANONCONTINUOUS;
              */
            }
            else
              continue;
          }
          else
          {
            action = REQUEST;
          }
          m_status = action;
          connMgr = BuildRequest( action, m_cookie, m_sessionId);
          String strTmp;
          DataManager dm = new DataManager( action, irBaos.toByteArray(), irBaos.size(), m_cm.getRemoteAddr(), m_cm.getRemotePort(), m_sessionId, m_iMode, m_outCipher);
          body = dm.getBody();
          int iBodyLen = dm.getBodyLength();
          if( iBodyLen > 0)
          {
            //System.out.write( dm.getBody(), 0, dm.getBodyLength());
            //System.out.println();
            m_histDm = dm;
            connMgr.openConnection();
            connMgr.getHttpURLConnection().setUseCaches( false);
            connMgr.post( body, iBodyLen, true);
            /*
            cookie = connMgr.getCookie();
            if( cookie != null)
              m_cookie = cookie;
            */
            m_jTextAreaOut.append( "Sent " + String.valueOf( dm.getRealInputBufferLength()) + " bytes to " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");

            DataManager resDm = readHeader( connMgr.getBufferedInputStream());
            if( resDm == null)
              break;
            if( resDm.getRealInputBufferLength() <= 0 && action.equalsIgnoreCase( CHECKFORDATA) && resDm.getAction().equalsIgnoreCase( RESPONSE))
              sleep( 1000);
            if( resDm.getAction().equalsIgnoreCase( RESEND))
            {
              m_jTextAreaOut.append( "ERROR: Receiving resend request from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
              connMgr = BuildRequest( m_histDm.getAction(), m_cookie, m_histDm.getSessionId());
              body = m_histDm.getBody();
              iBodyLen = m_histDm.getBodyLength();
              connMgr.openConnection();
              connMgr.getHttpURLConnection().setUseCaches( false);
              connMgr.post( body, iBodyLen, true);
              /*
              cookie = connMgr.getCookie();
              if( cookie != null)
                m_cookie = cookie;
              */
              m_jTextAreaOut.append( "Resent " + String.valueOf( m_histDm.getRealInputBufferLength()) + " bytes to " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
              resDm = readHeader( connMgr.getBufferedInputStream());
              if( resDm == null)
                break;
            }
            else if( resDm.getAction().equalsIgnoreCase( DISCONNECT))
            {
              blDisconnect = true;
              m_jTextAreaOut.append( "Disconnect was sent from servlet!!!!!!!!\n");
              if( m_serverIr != null)
              {
                while( (iLen = m_serverIr.read( m_serverBuf, BUFFERSIZE)) > 0 || m_serverIr.isAlive())
                {
                  m_lTotBytes += iLen;
                  m_jTextAreaOut.append( "Received " + String.valueOf( iLen)
                      + " bytes from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
                  m_bos.write( m_serverBuf, 0, iLen);
                  m_bos.flush();
                }
                m_serverIr.closeAll();
                m_serverIr = null;

                if( resDm.getRealInputBufferLength() > 0)
                {
                  m_jTextAreaOut.append( "Received " + String.valueOf( resDm.getRealInputBufferLength())
                      + " bytes from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
                  //System.out.write( m_serverBuf, 0, iLen);
                  m_bos.write( resDm.getInputBuffer(), 0, resDm.getRealInputBufferLength());
                  m_bos.flush();
                }
                m_serverIr = new InputReader( m_serverIs, m_jTextAreaOut, m_blCompressed);
                m_serverIr.start();
                while( (iLen = m_serverIr.read( m_serverBuf, BUFFERSIZE)) > 0 || m_serverIr.isAlive() || m_serverIr.hasMoreData())
                {
                  m_lTotBytes += iLen;
                  if( iLen > 0)
                    m_jTextAreaOut.append( "Received " + String.valueOf( iLen)
                        + " bytes from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
                  //System.out.write( m_serverBuf, 0, iLen);
                  m_bos.write( m_serverBuf, 0, iLen);
                  m_bos.flush();
                }
                m_serverIr.closeAll();
                m_serverIr = null;
              }
            }
            else if( resDm.getAction().equalsIgnoreCase( ERROR))
            {
              byte resBuf[] = new byte[BUFFERSIZE];
              int iResLen = 0;
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              if( resDm.getRealInputBufferLength() > 0)
              {
                baos.write( resDm.getInputBuffer(), 0 , resDm.getInputBufferLength());
              }
              while( (iResLen = m_serverIs.read( resBuf, 0, BUFFERSIZE)) > 0)
              {
                baos.write( resBuf, 0 , iResLen);
              }
              blOutput = false;
              blDisconnect = true;
              String str = null;
              if( resDm.getInputBufferLength() > 0)
                str = new String( baos.toByteArray());
              if( str != null)
                m_jTextAreaOut.append( "ERROR: " + str + "\n");
              else
                m_jTextAreaOut.append( "ERROR: Unknown server error occured!\n");
              System.out.println( "REMOTE ERROR: " + str);
            }
            else if( resDm.getAction().equalsIgnoreCase( RESPONSE))
            {
              if( m_serverIr != null)
              {
                while( (iLen = m_serverIr.read( m_serverBuf, BUFFERSIZE)) > 0 || m_serverIr.isAlive() || m_serverIr.hasMoreData())
                {
                  m_lTotBytes += iLen;
                  if( iLen > 0)
                    m_jTextAreaOut.append( "Received " + String.valueOf( iLen)
                        + " bytes from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
                  //System.out.write( m_serverBuf, 0, iLen);
                  m_bos.write( m_serverBuf, 0, iLen);
                  m_bos.flush();
                }
                m_serverIr.closeAll();
                m_serverIr = null;
              }
              if( resDm.getRealInputBufferLength() > 0)
              {
                m_jTextAreaOut.append( "Received " + String.valueOf( resDm.getRealInputBufferLength())
                    + " bytes from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
                //System.out.write( m_serverBuf, 0, iLen);
                m_bos.write( resDm.getInputBuffer(), 0, resDm.getRealInputBufferLength());
                m_bos.flush();
              }
              //if( blContinuous)
              if( true)
              {
                m_serverIr = new InputReader( m_serverIs, m_jTextAreaOut, m_blCompressed);
                m_serverIr.start();
              }
              else
              {
                while( (iLen = m_serverIs.read( m_serverBuf, 0, BUFFERSIZE)) > 0)
                {
                  m_lTotBytes += iLen;
                  //System.out.println( m_lTotBytes);
                  m_jTextAreaOut.append( "Received " + String.valueOf( iLen)
                      + " bytes from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
                  //System.out.write( m_serverBuf, 0, iLen);
                  m_bos.write( m_serverBuf, 0, iLen);
                  m_bos.flush();
                  if( m_serverIs.available() == 0)
                    sleep( 500);
                }
                /*
                boolean blTimeout = false;
                Date dTOut = getTimeOut();
                while( !blTimeout)
                {
                  if( m_serverIs.available() <= 0)
                  {
                    if( m_serverIs.available() < 0)
                    {
                      blTimeout = true;
                      break;
                    }
                    if( dTOut.before( new Date()))
                    {
                      m_jTextAreaOut.append( "Error: Cancel connection, timeout reached!\n");
                      blTimeout = true;
                      break;
                    }
                    Thread.sleep( 500);
                  }
                  else
                  {
                    if( (iLen = m_serverIs.read( m_serverBuf, 0, BUFFERSIZE)) > 0)
                    {
                      m_lTotBytes += iLen;
                      //System.out.println( m_lTotBytes);
                      m_jTextAreaOut.append( "Received " + String.valueOf( iLen)
                          + " bytes from " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
                      //System.out.write( m_serverBuf, 0, iLen);
                      m_bos.write( m_serverBuf, 0, iLen);
                      m_bos.flush();
                      dTOut = getTimeOut();
                    }
                    if( iLen == -1)
                    {
                      blTimeout = true;
                      break;
                    }
                  }
                }
                */
                connMgr.disconnect();
                sleep( 500);
                blDisconnect = true;
              }
            }
            if( !resDm.getAction().equalsIgnoreCase( RESPONSE))
            {
              connMgr.disconnect();
            }
            //System.out.write( resDm.getBody(), 0, resDm.getBodyLength());
            //System.out.println();
            if( blDisconnect)
            {
              if( m_ir != null)
              {
                m_ir.closeAll();
                m_ir = null;
                //m_ir.stop();
              }
              closeAll();
              break;
            }
          }
        }
      }while( ((m_ir != null && m_ir.isAlive()) || !blMustPoll) && !blDisconnect);
      if( !blDisconnect)
      {
        m_jTextAreaOut.append( "ERROR: Cient is interrupting connection to " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
        connMgr = BuildRequest( DISCONNECT, m_cookie, m_sessionId);
        byte b[] = new byte[1];
        b[0] = 0;
        DataManager dm = new DataManager( DISCONNECT, b, 1, m_cm.getRemoteAddr(), m_cm.getRemotePort(), m_sessionId, m_iMode, m_outCipher);
        connMgr.openConnection();
        connMgr.getHttpURLConnection().setUseCaches( false);
        connMgr.post( dm.getBody(), dm.getBodyLength(), true);
        m_jTextAreaOut.append( "Disconnecting: Send " + String.valueOf( dm.getRealInputBufferLength()) + " bytes to " + m_cm.getRemoteAddr() + ":" + m_cm.getRemotePort() + "\n");
        connMgr.disconnect();
      }
    }
    catch( Exception e)
    {
      if( e.toString().indexOf( "socket closed") == -1)
      {
        m_jTextAreaOut.append( "ERROR: " + e.toString() + "\n" );
        e.printStackTrace();
      }
    }
    finally
    {
      closeAll();
      m_htcs.removeConnection( this );
    }
  }

  private void serverInfo( String response)
  {
    if( response.equalsIgnoreCase( RESPONSE))
    {
      m_jTextAreaOut.append( "Response from server ...\n");
    }
    else if( response.equalsIgnoreCase( SUCCESS))
    {
      m_jTextAreaOut.append( "Successfully connected to server ...\n");
    }
    else if( response.equalsIgnoreCase( EXCHANGEKEY))
    {
      m_jTextAreaOut.append( "Successfully exchanged public keys with servlet ...\n");
    }
    else if( response.equalsIgnoreCase( RESEND))
    {
      m_jTextAreaOut.append( "Resend request from server ...\n");
    }
    else if( response.equalsIgnoreCase( DISCONNECT))
    {
      m_jTextAreaOut.append( "Disconnect order from server ...\n");
    }
    else if( response.equalsIgnoreCase( ERROR))
    {
      m_jTextAreaOut.append( "Error occured from server or servlet ...\n");
    }
  }

  private DataManager readHeader( BufferedInputStream bis)
    throws IOException
  {
    int iTotalLen = 0;
    int iHeaderLen = 0;
    int iToRead = 0;
    byte resBuf[] = new byte[BUFFERSIZE];
    int iResLen = 0;
    DataManager resDm = null;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // read header length
    if( bis == null)
      return null;
    iResLen = bis.read( resBuf, 0, 1);
    try
    {
      m_mutLock.acquire( LOCKTIMEOUT);
      if( iResLen == 1)
      {
        iHeaderLen = (new Byte( resBuf[0])).intValue();
        //System.out.println( iHeaderLen);
        int iLen = 0;
        switch( iHeaderLen)
        {
          case DataManager.COMP:
            m_blCompressed = true;
            m_serverIs = new InflaterInputStream( bis);
            break;

          case DataManager.CRYPT:
            m_serverIs = new BufferedInputStream( new CipherInputStream( bis, m_inCipher));
            break;

          case DataManager.COMPCRYPT:
            m_blCompressed = true;
            m_serverIs = new InflaterInputStream( new CipherInputStream( bis, m_inCipher));
            break;

          default:
            m_serverIs = bis;
            break;
        }

        //System.out.println();
        //System.out.println( m_serverIs.available());
        if( iHeaderLen <= DataManager.COMPCRYPT && (iLen = m_serverIs.read( resBuf, 0, 1)) > 0)
        {
          iHeaderLen = (new Byte( resBuf[0])).intValue();
          //System.out.write( resBuf, 0, iLen);
          baos.write( resBuf, 0, iLen);
        }
        else
        {
          resBuf[0] = (byte)(iHeaderLen-1);
          iLen = 1;
          baos.write( resBuf, 0, iLen);
        }
        iHeaderLen--;
        iToRead = iHeaderLen;
        //System.out.println( iHeaderLen);
        //System.out.println();
        for( iTotalLen = 0; (iResLen = m_serverIs.read( resBuf, 0, iToRead)) > 0 && iTotalLen <= iHeaderLen;
            iTotalLen += iResLen, iToRead -= iResLen)
        {
          //System.out.write( resBuf, 0, iResLen);
          //System.out.println( new String( resBuf));
          baos.write( resBuf, 0 , iResLen);
        }
        //System.out.println(baos.toString());
      }
      resDm = new DataManager( baos.toByteArray(), baos.size(), m_inCipher);
      serverInfo( resDm.getAction());
      m_sessionId = resDm.getSessionId();
      //System.out.println( m_sessionId);
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }

    return resDm;
  }

  private ConnectionManager BuildRequest( String action, String[] cookie, String sessionId)
  {
    ConnectionManager connMgr = new ConnectionManager( m_servletUrl, m_appProps, HttpTunnelClientServer.APPNAME);
    connMgr.setSessionId( sessionId);
    connMgr.setFrame( m_jTextAreaOut);
    connMgr.resetPostParams();
    connMgr.resetCookies();
    connMgr.addPostParam( "view", "true");
    if( cookie != null)
      connMgr.addCookie( cookie[0], cookie[1]);
    //System.out.println( connMgr.getConnectURL().toString());
    //System.out.println( connMgr.getParameters());

    return connMgr;
  }

  private void emptyBuffer()
  {
    try
    {
      m_mutLock.acquire( LOCKTIMEOUT);
      m_resData = null;
      m_resSize = 0;
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
  }

  private boolean checkConnection()
  {
    boolean blRet = true;
    try
    {
      m_mutLock.acquire( LOCKTIMEOUT);
      if( m_bos == null || m_bis == null)
        blRet = false;
    }
    catch( Exception ex)
    {
      blRet = false;
    }
    finally
    {
      m_mutLock.release();
    }
    return blRet;
  }

  private void appendBytes( byte[] b, int inSize)
  {
    try
    {
      m_mutLock.acquire( LOCKTIMEOUT);
      int i = 0;
      if( m_resData == null )
      {
        m_resData = new byte[inSize];
        for( i = 0; i < inSize; i++ )
          m_resData[i] = b[i];
        m_resSize = inSize;
      }
      else
      {
        int size = Array.getLength( m_resData );
        byte[] oldBytes = m_resData;
        m_resData = new byte[m_resSize + inSize];
        for( i = 0; i < size; i++ )
          m_resData[i] = oldBytes[i];
        for( int j = 0; j < inSize; i++, j++ )
          m_resData[i] = b[j];
        m_resSize = m_resSize + inSize;
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      m_mutLock.release();
    }
  }
}

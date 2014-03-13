package haui.io.FileInterface;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.SocketTypeServerFileInterfaceConfiguration;
import haui.io.FileInterface.remote.RemoteRequestObject;
import haui.io.FileInterface.remote.RemoteResponseObject;
import haui.io.StreamConnectThread;
import haui.tool.shell.engine.JShellEnv;
import haui.util.CommandClass;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Vector;

/**
 * Module:      SocketTypeServer.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\SocketTypeServer.java,v $ <p> Description: FileTypeServer for socket connections.<br> </p><p> Created:	19.05.2004  by AE </p><p>
 * @history      19.05.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: SocketTypeServer.java,v $  Revision 1.1  2004-08-31 16:03:14+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.0  2004-06-22 14:06:58+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\SocketTypeServer.java,v 1.1 2004-08-31 16:03:14+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.3  </p>
 */
public class SocketTypeServer
  extends FileTypeServer
{

  // member variables
  public static Vector connections = new Vector();

  private ObjectInputStream m_oisServer = null;
  private ObjectOutputStream m_oosServer = null;
  private BufferedInputStream m_bisTransfer = null;
  private BufferedOutputStream m_bosTransfer = null;
  private JShellEnv m_jShellEnv = null;
  private ThreadGroup m_tg = new ThreadGroup( "SocketConnection");
  private boolean m_blBisInUse = false;
  private boolean m_blBosInUse = false;

  public SocketTypeServer( FileInterface fi, FileInterfaceConfiguration fic)
  {
    super( fic);
    setInternalFileInterface( fi);
  }

  public void setShellEnv( JShellEnv jShellEnv)
  {
    m_jShellEnv = jShellEnv;
  }

  public void listen()
  {
    try
    {
      m_blConnected = ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).serverSocketListen();

      if( ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getServerSocket() != null
          && ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getServerSocket().isConnected())
      {
        m_oisServer = new ObjectInputStream( 
            ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getServerSocket().getInputStream() );
        m_oosServer = new ObjectOutputStream(
            ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getServerSocket().getOutputStream() );
        ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Client connected!\n");
      }

      if( m_oisServer != null && m_oosServer != null)
      {
        while( isConnected())
        {
          RemoteRequestObject rro = readRequestObject();
          if( rro != null)
          {
            Class classObject = this.getClass();
            Method methodToCall = null;

            try
            {
              if( rro.getMethod().equals( "getBufferedInputStream"))
              {
                if( !m_blBisInUse && !m_blBosInUse)
                {
                  startTransferStreams();
                }
                if( m_blBosInUse)
                {
                  closeTransfer();
                  startTransferStreams();
                }
                BufferedInputStream bis = getInternalFileInterface().getBufferedInputStream();
                StreamConnectThread sct = new StreamConnectThread( bis, m_bosTransfer );
                m_blBosInUse = true;
                sct.start();
                StreamObserveThread sot = new StreamObserveThread( m_tg, sct );
                sot.start();
              }
              else if( rro.getMethod().equals( "getBufferedOutputStream"))
              {
                String strFile = null;
                if( rro.hasMoreParams())
                  strFile = (String)rro.nextParam();
                if( strFile != null)
                {
                  if( !m_blBisInUse && !m_blBosInUse)
                  {
                    startTransferStreams();
                  }
                  if( m_blBisInUse)
                  {
                    closeTransfer();
                    startTransferStreams();
                  }
                  BufferedOutputStream bos = getInternalFileInterface().getBufferedOutputStream( strFile);
                  StreamConnectThread sct = new StreamConnectThread( m_bisTransfer, bos);
                  sct.start();
                  StreamObserveThread sot = new StreamObserveThread( m_tg, sct);
                  sot.start();
                }
              }
              else if( rro.getMethod().equals( "exec"))
              {
                String strTargetPath = null;
                if( rro.hasMoreParams())
                  strTargetPath = (String)rro.nextParam();
                CommandClass cmd = null;
                if( rro.hasMoreParams())
                  cmd = (CommandClass)rro.nextParam();

                if( cmd != null)
                {
                  OutputStream osOut = System.out;
                  OutputStream osErr = System.err;
                  if( m_jShellEnv != null)
                  {
                    osOut = m_jShellEnv.getOut();
                    osErr = m_jShellEnv.getErr();
                  }
                  exec( strTargetPath, cmd, osOut, osErr, null);
                }
              }
              else
              {
                methodToCall = classObject.getMethod( rro.getMethod(), rro.getParamTypes() );
                Object objRet = methodToCall.invoke( this, rro.getParamArray() );
                if( methodToCall.getName().equals( "duplicate"))
                  objRet = new Boolean(true);
                RemoteResponseObject response = new RemoteResponseObject();
                response.setObject( objRet );
                sendResponseObject( response );
              }
            }
            catch( Exception ex )
            {
              m_blConnected = false;
              ex.printStackTrace();
            }
          }
          else
          {
            m_blConnected = false;
          }
        }
      }
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }
    disconnect();
  }

  public int getPort()
  {
    return ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort();
  }

  public FileInterface getCanonicalFile() throws IOException {
    return new SocketTypeServer( this, getFileInterfaceConfiguration());
  }

  public boolean startTransferStreams()
  {
    boolean blRet = false;
    Object objRet = new Boolean( false);
    try
    {
      Socket transferSock = ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getTransferSocket();
      if( transferSock != null)
      {
        for( int i = 0; i < 30; ++i)
        {
          Thread.sleep( 1000);
          transferSock = ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getTransferSocket();
          if( transferSock == null )
            break;
        }
        if( transferSock != null )
        {
          RemoteResponseObject response = new RemoteResponseObject();
          response.setObject( objRet );
          sendResponseObject( response );
          return blRet;
        }
      }
      // send response
      objRet = new Boolean( true);
      RemoteResponseObject response = new RemoteResponseObject();
      response.setObject( objRet );
      sendResponseObject( response );

      ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).serverSocketListen( 1);

      if( ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getTransferSocket() != null
          && ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getTransferSocket().isConnected() )
      {
        m_bisTransfer = new BufferedInputStream( 
            ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getTransferSocket().getInputStream() );
        m_bosTransfer = new BufferedOutputStream( 
            ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getTransferSocket().getOutputStream() );
        ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Transferstream connected!\n");
      }
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }
    catch( InterruptedException iex )
    {
      iex.printStackTrace();
    }

    return blRet;
  }

  public void disconnect()
  {
    m_blConnected = false;
    try
    {
      m_tg.stop();
      closeTransfer();
      ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).disconnectServerSocket();
      if( m_oosServer != null)
      {
        m_oosServer.close();
        m_oosServer = null;
      }
      if( m_oisServer != null)
      {
        m_oisServer.close();
        m_oisServer = null;
      }
      if( ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getDialog() != null)
        ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getDialog().stop();
      ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Client disconnected!\n");
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  public void closeTransfer()
  {
    try
    {
      if( m_bisTransfer != null)
      {
        m_bisTransfer.close();
        m_bisTransfer = null;
      }
      if( m_bosTransfer != null)
      {
        m_bosTransfer.close();
        m_bosTransfer = null;
      }
      ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).disconnectTransferSocket();
      m_blBisInUse = false;
      m_blBosInUse = false;
      ((SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Transfer complete!\n");
    }
    catch( IOException ex )
    {
      m_blBisInUse = false;
      m_blBosInUse = false;
      ex.printStackTrace();
    }
  }

  public RemoteRequestObject readRequestObject()
  {
    RemoteRequestObject rroRet = null;
    if( m_oisServer != null)
    {
      try
      {
        rroRet = ( RemoteRequestObject )m_oisServer.readObject();
        //System.out.println( "RemoteRequestObject: " + rroRet.toString());
        //append( "Request read!\n");
      }
      catch( ClassNotFoundException cnfex )
      {
        cnfex.printStackTrace();
      }
      catch( IOException ioex )
      {
        ioex.printStackTrace();
      }
    }
    return rroRet;
  }

  public void sendResponseObject( RemoteResponseObject rro)
  {
    if( m_oosServer != null)
    {
      try
      {
        Object obj = rro.getObject();
        boolean blArr = false;
        try
        {
          Array.getLength( obj );
          blArr = true;
          if( Array.get( obj, 0) instanceof NormalFile)
          {
            FileInterface[] fis = new FileInterface[Array.getLength( obj)];
            for( int i = 0; i < Array.getLength( obj); ++i)
            {
              fis[i] = new SocketTypeFile( (NormalFile)Array.get( obj, i));
            }
            rro.setObject( fis);
          }
        }
        catch( Exception ex )
        {
        }
        if( !blArr && obj != null && obj instanceof NormalFile)
        {
          FileInterface fi = new SocketTypeFile( (NormalFile)obj);
          rro.setObject( fi);
        }
        m_oosServer.writeObject( rro );
        //System.out.println( "RemoteResponseObject: " + rro.toString());
        //append( "Response sent!\n");
      }
      catch( IOException ioex )
      {
        ioex.printStackTrace();
      }
    }
  }

  public int prepareTeminalStart()
  {
    SocketSubServerThread ssst = null;
    boolean blFound = false;

    for( int i = 0; i < connections.size(); ++i)
    {
      ssst = (SocketSubServerThread)connections.elementAt( i);
      if( !ssst.isConnected())
      {
        ssst.disconnect();
        SocketTypeServerFileInterfaceConfiguration stsfic =
          (SocketTypeServerFileInterfaceConfiguration)ssst.getSocketTypeServer().getFileInterfaceConfiguration();
        stsfic.setPort( ssst.getPort());
        ssst = new SocketSubServerThread( stsfic);
        connections.setElementAt( ssst, i);
        blFound = true;
      }
    }
    if( !blFound)
    {
      SocketTypeServerFileInterfaceConfiguration stsfic =
        (SocketTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration();
      SocketTypeServerFileInterfaceConfiguration newStsfic = new SocketTypeServerFileInterfaceConfiguration(
          connections.size() * 2 + 2, stsfic.getDialog(), getAppName(), getAppProperties(), stsfic.isCached());
      stsfic.setPort( connections.size() * 2 + 2);
      ssst = new SocketSubServerThread( newStsfic );
      connections.add( ssst );
    }

    ssst.start();
    return ssst.getPort();
  }

  public FileInterface duplicate()
  {
    /*
    closeOpenConnectionThreads();
    final SocketTypeServer sts = new SocketTypeServer( getInternalFileInterface().duplicate(), getPort()+1, m_appProps);
    //sts.setDialog( m_ftsd);
    Thread th = new Thread()
    {
      public void run()
      {
        sts.listen();
      }
    };
    th.start();
    m_vecConnections.add( sts);
    */
    return getInternalFileInterface().duplicate();
  }

  /**
   * Module:      SocketTypeServer$SocketSubServerThread<br> <p> Description: SocketTypeServer$SocketSubServerThread<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
   * @history      08.04.2008 by AE: Created.<br>  </p><p>
   * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
   * @version      v0.1, 2008; %version: %<br>  </p><p>
   * @since        JDK1.4  </p>
   */
  public class SocketSubServerThread
    extends Thread
  {
    private SocketTypeServer m_sts = null;

    public SocketSubServerThread( FileInterfaceConfiguration fic)
    {
      m_sts = new SocketTypeServer( SocketTypeServer.this.getInternalFileInterface().duplicate(), m_fic);
      //m_sts.setDialog( m_ftsd);
    }

    public int getPort()
    {
      return ((SocketTypeServerFileInterfaceConfiguration)m_sts.getFileInterfaceConfiguration()).getPort();
    }

    public SocketTypeServer getSocketTypeServer()
    {
      return m_sts;
    }

    public void setSocketTypeServer( SocketTypeServer sts)
    {
      this.m_sts = sts;
    }

    public boolean isConnected()
    {
      boolean blRet = false;

      if( m_sts != null)
        blRet = m_sts.isConnected();
      return blRet;
    }

    public void disconnect()
    {
      if( m_sts != null)
        m_sts.disconnect();
    }

    public void run()
    {
      if( m_sts != null)
        m_sts.listen();
    }
  }

  /**
   * Module:      SocketTypeServer$StreamObserveThread<br> <p> Description: SocketTypeServer$StreamObserveThread<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
   * @history      08.04.2008 by AE: Created.<br>  </p><p>
   * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
   * @version      v0.1, 2008; %version: %<br>  </p><p>
   * @since        JDK1.4  </p>
   */
  public class StreamObserveThread
    extends Thread
  {
    StreamConnectThread m_sct = null;

    public StreamObserveThread( ThreadGroup tg, StreamConnectThread sct)
    {
      super( tg, "SocketServerType");
      m_sct = sct;
    }

    public void run()
    {
      if( m_sct != null )
      {
        try
        {
          while( m_sct.isAlive() )
          {
            sleep( 250 );
          }
          closeTransfer();
        }
        catch( Exception ex )
        {
          ex.printStackTrace();
        }
      }
    }
  }
}
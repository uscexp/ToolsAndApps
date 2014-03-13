package haui.io.FileInterface;

import haui.io.FileInterface.configuration.CommPortTypeServerFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
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
import java.util.Vector;
import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

/**
 * Module:      CommPortTypeServer.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\CommPortTypeServer.java,v $ <p> Description: FileTypeServer for comm port connections.<br> </p><p> Created:	    08.06.2004  by AE </p><p>
 * @history      08.06.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: CommPortTypeServer.java,v $  Revision 1.1  2004-08-31 16:03:20+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.0  2004-06-22 14:06:55+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\CommPortTypeServer.java,v 1.1 2004-08-31 16:03:20+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.3  </p>
 */
public class CommPortTypeServer
  extends FileTypeServer
{
  // constants
  public static final int TIMEOUT = 5000;
  public static final int BAUD = 9600;

  // member variables
  public static Vector connections = new Vector();

  private ObjectInputStream m_oisServer = null;
  private ObjectOutputStream m_oosServer = null;
  private BufferedInputStream m_bisTransfer = null;
  private BufferedOutputStream m_bosTransfer = null;
  private JShellEnv m_jShellEnv = null;
  private ThreadGroup m_tg = new ThreadGroup( "CommPortConnection");
  private boolean m_blBisInUse = false;
  private boolean m_blBosInUse = false;

  public CommPortTypeServer( FileInterface fi, FileInterfaceConfiguration fic)
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
      if( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getDialog() != null)
      {
        int iMode = ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getMode();
        int iBaud = ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getBaud();
        switch( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getCommPortIdentifier().getPortType())
        {
          case CommPortIdentifier.PORT_SERIAL:
            try
            {
              ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).open(
                      getClass().getName(), TIMEOUT );
              SerialPort sp = (SerialPort)((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort();
              sp.setSerialPortParams( iBaud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                                        SerialPort.PARITY_NONE );
            }
            catch( PortInUseException piuex )
            {
              piuex.printStackTrace();
            }
            catch( UnsupportedCommOperationException ucoex )
            {
              ucoex.printStackTrace();
            }
            break;

          case CommPortIdentifier.PORT_PARALLEL:
            try
            {
              ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).open(
                      getClass().getName(), TIMEOUT );
              ParallelPort pp = (ParallelPort)((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort();
              pp.setMode( iMode);

              switch( pp.getMode())
              {
                case ParallelPort.LPT_MODE_ECP:
                  ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append(
                      "Parallel port mode is: ECP");
                  break;

                case ParallelPort.LPT_MODE_EPP:
                  ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append(
                      "Parallel port mode is: EPP");
                  break;

                case ParallelPort.LPT_MODE_NIBBLE:
                  ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append(
                      "Parallel port mode is: NIBBLE");
                  break;

                case ParallelPort.LPT_MODE_PS2:
                  ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append(
                      "Parallel port mode is: Byte");
                  break;

                case ParallelPort.LPT_MODE_SPP:
                  ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append(
                      "Parallel port mode is: Compatibility");
                  break;

                default:
                  ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append(
                      "No mode found, will be set to compatibility mode");
                  pp.setMode( ParallelPort.LPT_MODE_SPP);
                  break;
              }
            }
            catch( PortInUseException piuex )
            {
              piuex.printStackTrace();
            }
            catch( UnsupportedCommOperationException ucoex )
            {
              ucoex.printStackTrace();
            }
            break;

            default:
              throw new IllegalStateException( "Unknown port type: "
                  + ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getCommPortIdentifier());
        }
      }
      ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Listen on port: "
          + ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort().getName() + "\n");
      m_blConnected = true;

      if( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort() != null)
      {
        m_oosServer = new ObjectOutputStream(
            ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort().getOutputStream() );
        m_oisServer = new ObjectInputStream(
            ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort().getInputStream() );
        ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Client connected!\n");
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
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    disconnect();
  }

  public void closeConnectionStreams()
  {
    try
    {
      if( m_oosServer != null )
      {
        m_oosServer.close();
        m_oosServer = null;
      }
      if( m_oisServer != null )
      {
        m_oisServer.close();
        m_oisServer = null;
      }
    }
    catch( IOException ioex )
    {
      ioex.printStackTrace();
    }
  }

  public void reopenConnectionStreams()
  {
    try
    {
      if( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort() != null )
      {
        m_oosServer = new ObjectOutputStream(
            ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort().getOutputStream() );
        m_oisServer = new ObjectInputStream(
            ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort().getInputStream() );
      }
    }
    catch( IOException ioex )
    {
      ioex.printStackTrace();
    }
  }

  public FileInterface getCanonicalFile() throws IOException
  {
    return new CommPortTypeServer( this, getFileInterfaceConfiguration());
  }

  public boolean startTransferStreams()
  {
    boolean blRet = false;
    Object objRet = new Boolean( false);
    try
    {
      if( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort() != null
          && (m_oosServer != null || m_oisServer != null))
      {
        closeConnectionStreams();
      }
      // send response
      objRet = new Boolean( true);
      RemoteResponseObject response = new RemoteResponseObject();
      response.setObject( objRet );
      sendResponseObject( response );

      if( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort() != null )
      {
        m_bisTransfer = new BufferedInputStream(
            ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort().getInputStream() );
        m_bosTransfer = new BufferedOutputStream(
            ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort().getOutputStream() );
        ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Transferstream connected!\n");
      }
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }

    return blRet;
  }

  public void disconnect()
  {
    m_blConnected = false;
    try
    {
      m_tg.stop();
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
      m_blBisInUse = false;
      m_blBosInUse = false;
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
      ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).disconnect();
      if( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getDialog() != null)
        ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getDialog().stop();
      ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Client disconnected!\n");
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
      m_blBisInUse = false;
      m_blBosInUse = false;
      ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).append( "Transfer complete!\n");
      reopenConnectionStreams();
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
    if( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort() != null
        && (m_bosTransfer != null || m_bisTransfer != null))
    {
      closeTransfer();
    }
    if( m_oisServer != null)
    {
      try
      {
        rroRet = ( RemoteRequestObject )m_oisServer.readObject();
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
    if( ((CommPortTypeServerFileInterfaceConfiguration)getFileInterfaceConfiguration()).getPort() != null
        && (m_bosTransfer != null || m_bisTransfer != null))
    {
      closeTransfer();
    }
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
              fis[i] = new CommPortTypeFile( (NormalFile)Array.get( obj, i));
            }
            rro.setObject( fis);
          }
        }
        catch( IllegalArgumentException ex )
        {
        }
        if( !blArr && obj instanceof NormalFile)
        {
          FileInterface fi = new CommPortTypeFile( (NormalFile)obj);
          rro.setObject( fi);
        }
        m_oosServer.writeObject( rro );
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
    return 0;
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
   * Module:      CommPortTypeServer$CommPortSubServerThread<br> <p> Description: CommPortTypeServer$CommPortSubServerThread<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
   * @history      08.04.2008 by AE: Created.<br>  </p><p>
   * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
   * @version      v0.1, 2008; %version: %<br>  </p><p>
   * @since        JDK1.4  </p>
   */
  public class CommPortSubServerThread
    extends Thread
  {
    //private CommPortIdentifier m_portId;
    private CommPortTypeServer m_cts = null;
    //private FileTypeServerDialog m_ftsd = null;

    public CommPortSubServerThread( /*CommPortIdentifier portId, FileTypeServerDialog ftsd*/)
    {
      //m_portId = portId;
      //m_ftsd = ftsd;
      m_cts = new CommPortTypeServer( CommPortTypeServer.this.getInternalFileInterface().duplicate(),
          getFileInterfaceConfiguration());
      //m_cts.setDialog( m_ftsd);
    }

    public boolean isConnected()
    {
      boolean blRet = false;

      if( m_cts != null)
        blRet = m_cts.isConnected();
      return blRet;
    }

    public void disconnect()
    {
      if( m_cts != null)
        m_cts.disconnect();
    }

    public void run()
    {
      if( m_cts != null)
        m_cts.listen();
    }
  }

  /**
   * Module:      CommPortTypeServer$StreamObserveThread<br> <p> Description: CommPortTypeServer$StreamObserveThread<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
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
      super( tg, "CommPortServerType");
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

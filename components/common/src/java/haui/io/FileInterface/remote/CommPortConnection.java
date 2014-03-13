package haui.io.FileInterface.remote;

import haui.util.GlobalApplicationContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.comm.CommPort;
import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

/**
 * Module:      CommPortConnection.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\CommPortConnection.java,v $
 *<p>
 * Description: connection data for socket connections.<br>
 *</p><p>
 * Created:	    17.06.2004  by AE
 *</p><p>
 * @history     17.06.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: CommPortConnection.java,v $
 * Revision 1.1  2004-08-31 16:03:19+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:54+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\CommPortConnection.java,v 1.1 2004-08-31 16:03:19+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class CommPortConnection
{
  // constants
  public static final int TIMEOUT = 5000;
  public static final int BAUD = 9600;

  // member variables
  String m_strAppName;
  CommPortIdentifier m_portId;
  CommPort m_port;
  int m_iBaud = 9600;
  int m_iMode = ParallelPort.LPT_MODE_ECP;
  private ObjectInputStream m_ois = null;
  private ObjectOutputStream m_oos = null;

  public CommPortConnection( String strAppName,  CommPortIdentifier portId, int iMode, int iBaud)
  {
    m_strAppName = strAppName;
    m_portId = portId;
    m_iMode = iMode;
    m_iBaud = iBaud;
  }

  public CommPort getCommPort()
    throws IOException
  {
    if( m_port == null)
    {
      switch( m_portId.getPortType())
      {
        case CommPortIdentifier.PORT_SERIAL:
          try
          {
            m_port = m_portId.open( getClass().getName(), TIMEOUT );
            SerialPort sp = (SerialPort)m_port;
            sp.setSerialPortParams( m_iBaud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
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
            m_port = m_portId.open( getClass().getName(), TIMEOUT );
            ParallelPort pp = (ParallelPort)m_port;
            pp.setMode( m_iMode);

            switch( pp.getMode())
            {
              case ParallelPort.LPT_MODE_ECP:
                GlobalApplicationContext.instance().getOutputPrintStream().println( "Parallel port mode is: ECP");
                //System.out.println( "Parallel port mode is: ECP");
                break;

              case ParallelPort.LPT_MODE_EPP:
                GlobalApplicationContext.instance().getOutputPrintStream().println( "Parallel port mode is: EPP");
                //System.out.println( "Parallel port mode is: EPP");
                break;

              case ParallelPort.LPT_MODE_NIBBLE:
                GlobalApplicationContext.instance().getOutputPrintStream().println( "Parallel port mode is: NIBBLE");
                //System.out.println( "Parallel port mode is: NIBBLE");
                break;

              case ParallelPort.LPT_MODE_PS2:
                GlobalApplicationContext.instance().getOutputPrintStream().println( "Parallel port mode is: Byte");
                //System.out.println( "Parallel port mode is: Byte");
                break;

              case ParallelPort.LPT_MODE_SPP:
                GlobalApplicationContext.instance().getOutputPrintStream().println( "Parallel port mode is: Compatibility");
                //System.out.println( "Parallel port mode is: Compatibility");
                break;

              default:
                GlobalApplicationContext.instance().getOutputPrintStream().println( "No mode found, will be set to compatibility mode");
                //System.out.println( "No mode found, will be set to compatibility mode");
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
            throw new IllegalStateException( "Unknown port type: " + m_portId);
      }
    }
    if( m_oos == null)
      m_oos = new ObjectOutputStream( m_port.getOutputStream());
    if( m_ois == null)
      m_ois = new ObjectInputStream( m_port.getInputStream());
    return m_port;
  }

  public void closeConnectionStreams()
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
      if( m_port != null )
      {
        m_oos = new ObjectOutputStream( m_port.getOutputStream() );
        m_ois = new ObjectInputStream( m_port.getInputStream() );
      }
    }
    catch( IOException ioex )
    {
      ioex.printStackTrace();
    }
  }

  public CommPortIdentifier getCommPortId()
  {
    return m_portId;
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
    if( m_port != null && m_oos != null && m_ois != null)
      blRet = true;
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
      if( m_port != null)
      {
        m_port.close();
        m_port = null;
      }
      GlobalApplicationContext.instance().getOutputPrintStream().println( "Connection closed!\n");
      //System.out.println( "Connection closed!\n");
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }
  }
}

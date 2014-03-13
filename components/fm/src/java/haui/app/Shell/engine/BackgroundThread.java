package haui.app.Shell.engine;

import haui.app.Shell.*;
import haui.app.Shell.util.*;
import haui.app.Shell.command.*;
import haui.io.*;
import haui.util.*;

import org.javagroup.process.JProcess;

import java.text.Collator;

/**
 * Module:      BackgroundThread.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\BackgroundThread.java,v $
 *<p>
 * Description: background command execution.<br>
 *</p><p>
 * Created:     01.04.2004  by AE
 *</p><p>
 * @history     01.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: BackgroundThread.java,v $
 * Revision 1.1  2004-08-31 16:03:22+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:50+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\BackgroundThread.java,v 1.1 2004-08-31 16:03:22+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class BackgroundThread
  extends Thread
{
  private String m_strCommandLine;
  private JShellEnv m_jse;
  private JShellCommandProcessor m_jscp;
  private FileInterface m_fi = null;
  private JShellCommandInterface m_jsci = null;
  private JProcess m_proc = null;
  private String m_strTargetPath = null;

  public BackgroundThread( String strCommandLine, String strTargetPath, JShellEnv jse, JShellCommandProcessor jscp)
  {
    m_strCommandLine = strCommandLine;
    m_strTargetPath = strTargetPath;
    m_jse = jse;
    m_jscp = jscp;
  }

  public BackgroundThread( String strCommandLine, String strTargetPath, FileInterface fi)
  {
    m_strCommandLine = strCommandLine;
    m_strTargetPath = strTargetPath;
    m_fi = fi;
    m_jse = null;
    m_jscp = null;
  }

  public String getTargetPath()
  {
    if( m_strTargetPath == null || m_strTargetPath.equals( ""))
      return m_fi.getAbsolutePath();
    return m_strTargetPath;
  }

  public String getCommandLine()
  {
    return m_strCommandLine;
  }

  public JShellEnv getShellEnv()
  {
    return m_jse;
  }

  public JShellCommandProcessor getCommandProcessor()
  {
    return m_jscp;
  }

  public void setProcess( JProcess proc)
  {
    m_proc = proc;
  }

  public JProcess getProcess()
  {
    return m_proc;
  }

  public void kill()
  {
    if( m_fi != null)
      m_fi.killCurrentProcess();
    else if( m_proc != null)
      m_proc.kill();

    try
    {
      interrupt();
      sleep( 100 );
      if( isAlive())
        stop();
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
  }

  public void run()
  {
    try
    {
      Collator collator = m_jse.getCollatorInstance();
      int iStatus = JShellEnv.DEFAULTERRORVALUE;

      String strCommand = m_jscp.getCommand();
      if( strCommand == null )
      {
        return;
      }

      if( collator.equals( strCommand, "exec" ) )
      {
        String strSubCommand = m_jscp.readNextArgument();

        CommandClass cmd = new CommandClass( "Temp", m_jse.getFileInterface().getAbsolutePath(),
                                             strSubCommand,
                                             m_jscp.getArgumentsFromPosition(), 0, 0, m_jse.execLocal(),
                                             ( Object )null,
                                             null,
                                             m_jse.getAppName(),
                                             m_jse.getAppPropperties() );
        FileInterface fi = m_jse.getFileInterface().duplicate();
        m_fi = fi;
        iStatus = fi.exec( getTargetPath(), cmd,
          m_jse.getOut(), m_jse.getErr(), null );
      }
      else if( (m_jsci = m_jse.getCommandList().getShellCommand( m_jse, m_jscp)) != null )
      {
        if( m_jsci instanceof JShellCommandJexec)
          ((JShellCommandJexec)m_jsci).setBackgroundThread( this);
        iStatus = m_jsci.processCommand( m_jse, m_jscp );
        if( iStatus == JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND )
          m_jse.getCommandList().usage( m_jse, m_jscp );
        return;
      }
      else
      {
        if( collator.equals( strCommand, "" ) )
        {
          return;
        }

        CommandClass cmd = new CommandClass( "Temp", m_jse.getFileInterface().getAbsolutePath(),
                                             strCommand,
                                             m_jscp.getArguments(), 0, 0, m_jse.execLocal(), ( Object )null,
                                             null,
                                             m_jse.getAppName(),
                                             m_jse.getAppPropperties() );
        FileInterface fi = m_jse.getFileInterface().duplicate();
        m_fi = fi;
        iStatus = fi.exec( getTargetPath(), cmd,
          m_jse.getOut(), m_jse.getErr(), null );
      }
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      m_jse.getOut().println( ex.toString());
    }
  }
}
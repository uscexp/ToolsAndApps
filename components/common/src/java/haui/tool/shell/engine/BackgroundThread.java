package haui.tool.shell.engine;

import haui.io.FileInterface.FileInterface;
import haui.process.JProcess;
import haui.tool.shell.command.JShellCommandInterface;
import haui.tool.shell.command.JShellCommandJexec;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.util.CommandClass;
import java.awt.Component;
import java.text.Collator;

//import org.javagroup.process.JProcess;

/**
 * Module:      BackgroundThread.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\BackgroundThread.java,v $ <p> Description: background command execution.<br> </p><p> Created:     01.04.2004  by AE </p><p>
 * @history      01.04.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: BackgroundThread.java,v $  Revision 1.1  2004-08-31 16:03:22+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.0  2004-06-22 14:06:50+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\BackgroundThread.java,v 1.1 2004-08-31 16:03:22+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.3  </p>
 */
public class BackgroundThread
  extends Thread
{
  private String m_strCommandLine;
  private JShellEnv m_jse;
  private CommandLineContainer m_clc;
  private ArgumentIteration m_ai;
  private FileInterface m_fi = null;
  private JShellCommandInterface m_jsci = null;
  private JProcess m_proc = null;
  private String m_strTargetPath = null;

  public BackgroundThread( String strCommandLine, String strTargetPath, JShellEnv jse, CommandLineContainer clc)
  {
    m_strCommandLine = strCommandLine;
    m_strTargetPath = strTargetPath;
    m_jse = jse;
    m_clc = clc;
    m_ai = m_clc.iterationObject();
  }

  public BackgroundThread( String strCommandLine, String strTargetPath, FileInterface fi)
  {
    m_strCommandLine = strCommandLine;
    m_strTargetPath = strTargetPath;
    m_fi = fi;
    m_jse = null;
    m_clc = null;
    m_ai = null;
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

  public CommandLineContainer getCommandLineContainer()
  {
    return m_clc;
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

      String strCommand = m_clc.getCommand();
      if( strCommand == null )
      {
        return;
      }

      if( collator.equals( strCommand, "exec" ) )
      {
        String strSubCommand = m_ai.readNextArgument();

        FileInterface fi = m_jse.getFileInterface().duplicate();
        m_fi = fi;
        CommandClass cmd = new CommandClass( "Temp", m_jse.getFileInterface().getAbsolutePath(),
                                             strSubCommand,
                                             m_ai.getArgumentsFromPosition(), 0, CommandClass.NO, m_jse.execLocal(),
                                             ( Component )null,
                                             m_fi.getFileInterfaceConfiguration());
        iStatus = fi.exec( getTargetPath(), cmd,
          m_jse.getOut(), m_jse.getErr(), null );
      }
      else if( (m_jsci = m_jse.getCommandList().getShellCommand( m_jse, m_clc)) != null )
      {
        if( m_jsci instanceof JShellCommandJexec)
          ((JShellCommandJexec)m_jsci).setBackgroundThread( this);
        iStatus = m_jsci.processCommand( m_jse, m_clc);
        if( iStatus == JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND )
          m_jse.getCommandList().usage( m_jse, m_clc);
        return;
      }
      else
      {
        if( collator.equals( strCommand, "" ) )
        {
          return;
        }

        FileInterface fi = m_jse.getFileInterface().duplicate();
        m_fi = fi;
        CommandClass cmd = new CommandClass( "Temp", m_jse.getFileInterface().getAbsolutePath(),
                                             strCommand,
                                             m_ai.getArguments(), 0, CommandClass.NO, m_jse.execLocal(),
                                             ( Component )null,
                                             m_fi.getFileInterfaceConfiguration() );
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
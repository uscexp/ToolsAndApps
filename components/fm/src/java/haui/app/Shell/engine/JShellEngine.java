package haui.app.Shell.engine;

import haui.app.Shell.*;
import haui.app.Shell.util.*;
import haui.io.*;
import haui.util.*;

import org.javagroup.process.JProcess;

import java.io.*;
import java.util.*;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.awt.Component;

/**
 *    Module:       JShellEngine.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellEngine.java,v $
 *<p>
 *    Description:  Java shell process engine.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: JShellEngine.java,v $
 *    Revision 1.1  2004-08-31 16:03:16+02  t026843
 *    Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *    Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *    Revision 1.0  2004-06-22 14:06:51+02  t026843
 *    Initial revision
 *
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: 1.1 $<br>
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellEngine.java,v 1.1 2004-08-31 16:03:16+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class JShellEngine
{
  // member variables
  private JShellEnv m_jse;
  private boolean m_blRun = true;
  private boolean m_blSubEng = false;

  /**
   * History
   */
  //private Vector vecCommandHistory = new Vector( 1024 );

  public JShellEngine( JShellEnv jse, boolean blSubEngine )
  {
    m_jse = jse;
    m_blSubEng = blSubEngine;

    init();
  }

  public void init()
  {
  }

  public void close()
  {
  }

  public void stop()
  {
    try
    {
      m_blRun = false;
      if( !m_blSubEng)
      {
        m_jse.getFileInterface().killCurrentProcess();
        m_jse.getOut().close();
        m_jse.getErr().close();
        m_jse.getIn().close();
      }
    }
    catch( Exception ex )
    {
    }
  }

  /**
   * entry point for this class.
   */
  public int process()
  {
    try
    {
      if( !m_blSubEng )
        m_jse.getOut().println( m_jse.getAppName() + " " + JShellEnv.APPVERSION + ", "
                                + JShellEnv.COPYRIGHT );
      while( m_blRun)
      {
        Thread.yield();
        m_jse.getOut().print( m_jse.getFileInterface().getAbsolutePath() + "> " );

        m_jse.clearLine();
        String strRead = m_jse.getNextLine();

        if( strRead == null )
        {
          // Next.
          continue;
        }
        try
        {
          int iStatus = JShellEnv.DEFAULTERRORVALUE;
          Calendar calStart = Calendar.getInstance();
          SimpleDateFormat sdFormat = new SimpleDateFormat( "HH:mm:ss" );
          {
            StringBuffer strbufWrk = new StringBuffer();
            strbufWrk.append( sdFormat.format( calStart.getTime() ));
            strbufWrk.append( "; Started command: [" );
            strbufWrk.append( strRead);
            strbufWrk.append( "]" );
            m_jse.getOut().println( strbufWrk.toString() );
          }

          if( ( iStatus = processCommands( strRead, m_jse, m_blSubEng ) ) == JShellEnv.EXITVALUE )
          {
            if( m_blSubEng )
              // Finish subcommand processing!!!
              m_blRun = false;
            else
              // Finish!!!
              break;
          }

          Calendar calEnd = Calendar.getInstance();
          String strMillisec = NumberFormat.getNumberInstance().format( ( double ) ( calEnd.getTime().
            getTime() - calStart.getTime().getTime() ) / 1000 );

          m_jse.getOut().println();

          StringBuffer strbufWrk = new StringBuffer();
          strbufWrk.append( sdFormat.format( calEnd.getTime() ));
          strbufWrk.append( "; Command completed: " );
          strbufWrk.append( strMillisec);
          strbufWrk.append( " sec; return value = ");
          strbufWrk.append( iStatus );
          m_jse.getOut().println( strbufWrk.toString() );

        }
        catch( JShellCancelException ex )
        {
          if( JShellEnv.ISDEBUG )
            AppProperties.getPrintStreamOutput( m_jse.getAppName()).println( "canceled: " + ex.toString());
            //System.err.println( "canceled: " + ex.toString() );
        }
        catch( JShellException ex )
        {
          if( JShellEnv.ISDEBUG )
            AppProperties.getPrintStreamOutput( m_jse.getAppName()).println( "exited: " + ex.toString());
            //System.err.println( "exited: " + ex.toString() );
        }
      }
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
      m_jse.getOut().println( ex.toString() );
    }
    return 0;
  }

  /**
   * Process command.
   * @return -999 means exit.
   */
  public static int processCommands( String strReadLine, JShellEnv jse, boolean blSubEngine )
    throws JShellException, IOException
  {
    JShellCommandProcessor jscp = new JShellCommandProcessor( strReadLine, jse);
    String strCmd = null;
    int iStatus = JShellEnv.DEFAULTERRORVALUE;

    while( iStatus != JShellEnv.EXITVALUE && (strCmd = jscp.readNextCommandLine()) != null)
    {
      JShellCommandProcessor jscpPart = new JShellCommandProcessor( strCmd, jse);
      if( blSubEngine)
        iStatus = processSubCommand( strCmd, jse, jscpPart);
      else
        iStatus = processCommand( strCmd, jse, jscpPart );
    }
    jse.setTargetPath( null);
    return iStatus;
  }

  /**
   * Process subcommand.
   * @return -999 means exit subcommand processor.
   */
  public static int processSubCommand( String strCommandLine, JShellEnv jse, JShellCommandProcessor jscp )
    throws JShellException, IOException
  {
    Collator collator = jse.getCollatorInstance();
    int iStatus = JShellEnv.DEFAULTERRORVALUE;

    String strCommand = jscp.getCommand();
    if( strCommand == null )
    {
      return iStatus;
    }

    jse.setProcess( null);
    if( collator.equals( strCommand, "exit" )
        || collator.equals( strCommand, "quit" ) )
    {
      return JShellEnv.EXITVALUE;
    }

    jse.setCurrentShellCommand( jse.getSubCommandList().getShellCommand( jse, jscp));
    if( jse.getCurrentShellCommand() != null )
    {
      iStatus = jse.getCurrentShellCommand().processCommand( jse, jscp );
      if( iStatus == JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND )
        jse.getSubCommandList().usage( jse, jscp );
      return iStatus;
    }
    return iStatus;
  }

  /**
   * Process command.
   * @return -999 means exit.
   */
  public static int processCommand( String strCommandLine, JShellEnv jse, JShellCommandProcessor jscp )
    throws JShellException, IOException
  {
    Collator collator = jse.getCollatorInstance();
    int iStatus = JShellEnv.DEFAULTERRORVALUE;

    String strCommand = jscp.getCommand();
    if( strCommand == null )
    {
      return iStatus;
    }

    jse.setProcess( null);
    if( collator.equals( strCommand, "exit" )
        || collator.equals( strCommand, "quit" ) )
    {
      return JShellEnv.EXITVALUE;
    }

    jse.setCurrentShellCommand( jse.getCommandList().getShellCommand( jse, jscp));
    if( jscp.execInBackground())
    {
      jse.setCurrentShellCommand( null);
      processBackgroundCommand( strCommandLine, jse, jscp);
      iStatus = JShellEnv.OKVALUE;
    }
    else if( collator.equals( strCommand, "exec" ) )
    {
      String strSubCommand = jscp.readNextArgument();

      CommandClass cmd = new CommandClass( "Temp", jse.getFileInterface().getAbsolutePath(), strSubCommand,
                                           jscp.getArgumentsFromPosition(), 0, 0, true, (Object)null, (Component)null,
                                           jse.getAppName(), jse.getAppPropperties());
      iStatus = jse.getFileInterface().exec( jse.getTargetPath(), cmd, jse.getOut(),
                                     jse.getErr(), jse.getIn());
    }
    else if( jse.getCurrentShellCommand() != null )
    {
      iStatus = jse.getCurrentShellCommand().processCommand( jse, jscp );
      if( iStatus == JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND )
        jse.getCommandList().usage( jse, jscp );
      return iStatus;
    }
    else
    {
      if( collator.equals( strCommand, "" ) )
      {
        return iStatus;
      }

      CommandClass cmd = new CommandClass( "Temp", jse.getFileInterface().getAbsolutePath(), strCommand,
                                           jscp.getArguments(), 0, 0, true, (Object)null, (Component)null,
                                           jse.getAppName(), jse.getAppPropperties());
      iStatus = jse.getFileInterface().exec( jse.getTargetPath(), cmd,
                                     jse.getOut(), jse.getErr(), jse.getIn());
    }
    return iStatus;
  }

  /**
   * Process command in background.
   * @return -999 means exit.
   */
  private static void processBackgroundCommand( String strCommandLine, JShellEnv jse, JShellCommandProcessor jscp )
    throws JShellException, IOException
  {
    BackgroundThread btProc = new BackgroundThread( strCommandLine, jse.getTargetPath(), jse, jscp);
    jse.addProcess( btProc);
    btProc.start();
    try
    {
      btProc.join(3000);
    }
    catch( InterruptedException ex )
    {
      ex.printStackTrace();
    }
  }

  /**
   * Show command history.
   */
  public void showCommandHistory()
  {
  }
}
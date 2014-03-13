package haui.tool.shell.command;

import haui.tool.shell.engine.ArgumentIteration;
import haui.tool.shell.engine.JShellCancelException;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEngine;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellException;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.util.GlobalApplicationContext;

/**
 *    Module:       JShellCommandAlias.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandAlias.java,v $
 *<p>
 *    Description:  Java shell alias command.<br>
 *</p><p>
 *    Created:	    24.03.2004  by AE
 *</p><p>
 *    @history      24.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: JShellCommandAlias.java,v $
 *    Revision 1.1  2004-08-31 16:03:16+02  t026843
 *    Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *    Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *    Revision 1.0  2004-06-22 14:06:43+02  t026843
 *    Initial revision
 *
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: 1.1 $<br>
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandAlias.java,v 1.1 2004-08-31 16:03:16+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class JShellCommandAlias
  extends JShellCommandDefault
{
  private String m_strCmdPath = null;

  public JShellCommandAlias( CommandLineContainer clc, String strDisplayName, String strCmdPath )
  {
    setCommand( clc);
    setDisplayString( strDisplayName );
    setFullPathName( strCmdPath );
  }

  public JShellCommandAlias( CommandLineContainer clc)
  {
    this( clc, null, null );
  }

  public JShellCommandAlias( CommandLineContainer clc, String strDisplayName )
  {
    this( clc, strDisplayName, null );
  }

  public final void setFullPathName( String strCmdPath )
  {
    m_strCmdPath = strCmdPath;
  }

  public final String getFullPathName()
  {
    return m_strCmdPath;
  }

  public final boolean isHiddenCommand()
  {
    if( getCommandString().charAt( 0 ) == '$' )
    {
      return true;
    }
    return false;
  }

  public int processCommand( JShellEnv jse, CommandLineContainer clc )
    throws JShellException
  {
    ArgumentIteration ai = clc.iterationObject();
    int iStatus = JShellEnv.DEFAULTERRORVALUE;
    String strFindCommand = null;
    if( getFullPathName() != null )
    {
      strFindCommand = getFullPathName();
    }
    else
    {
      strFindCommand = getCommandString();
    }
    String strArgs = ai.getArguments();
    if( strArgs != null )
      strFindCommand += " " + strArgs;

    JShellCommandProcessor jscpAlias = new JShellCommandProcessor( jse, strFindCommand );
    try
    {
      if( jse.isSubEnv())
        iStatus = JShellEngine.processSubCommand( strFindCommand, jse, jscpAlias.getCommandLineContainer());
      else
        iStatus = JShellEngine.processCommand( strFindCommand, jse, jscpAlias.getCommandLineContainer());
    }
    catch( JShellCancelException ex )
    {
      if( JShellEnv.ISDEBUG )
        GlobalApplicationContext.instance().getOutputPrintStream().println( "canceled: " + ex.toString());
        //System.err.println( "canceled: " + ex.toString() );
    }
    catch( JShellException ex )
    {
      if( JShellEnv.ISDEBUG )
        GlobalApplicationContext.instance().getOutputPrintStream().println( "exited: " + ex.toString());
        //System.err.println( "exited: " + ex.toString() );
    }
    catch( Exception ex )
    {
      jse.getOut().println( ex.toString() );
      ex.printStackTrace();
    }
    return iStatus;
  }

  public void usage( JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());

    jse.getOut().println( strbufUsage.toString());
  }
}
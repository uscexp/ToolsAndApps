package haui.tool.shell.command;

import haui.io.FileInterface.FileInterface;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.util.HashMap;

/**
 * Module:      JShellCommandHelp.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandHelp.java,v $
 *<p>
 * Description: help command.<br>
 *</p><p>
 * Created:     26.03.2004  by AE
 *</p><p>
 * @history     26.03.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandHelp.java,v $
 * Revision 1.1  2004-08-31 16:03:26+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:46+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandHelp.java,v 1.1 2004-08-31 16:03:26+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandHelp
  extends JShellCommandDefault
{
  public JShellCommandHelp( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "help")).getCommandLineContainer());
    setDisplayString("help to JShell commands");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <command>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n <command>\tshow description of this command");

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, CommandLineContainer clc)
    throws haui.tool.shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    if( strArgs == null && hmOptions.size() == 0)
    {
      jse.getOut().println( jse.getAppName() + " " + JShellEnv.APPVERSION + ", " + JShellEnv.COPYRIGHT );
      jse.getOut().println( "Console mode shell program." );
      jse.getOut().println();
      jse.getOut().println( "    This program is free software; you can redistribute it." );
      jse.getOut().println();
      jse.getOut().println( "    This program is distributed in the hope that it will be useful," );
      jse.getOut().println( "    but WITHOUT ANY WARRANTY; even without the implied warranty of" );
      jse.getOut().println( "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE." );
      jse.getOut().println();
      jse.getOut().println( jse.getAppName() + " command list:" );

      if( jse.isSubEnv())
        jse.getOut().println( jse.getSubCommandList().getImplementedCommandListForDisplayString() );
      else
        jse.getOut().println( jse.getCommandList().getImplementedCommandListForDisplayString() );
      iStatus = JShellEnv.OKVALUE;
    }
    else if( strArgs != null  && strArgs.length == 1)
    {
      String strArg = strArgs[0];
      JShellCommandProcessor jscpCmd = new JShellCommandProcessor( jse, strArg);
      jse.getCommandList().usage( jse, jscpCmd.getCommandLineContainer());
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
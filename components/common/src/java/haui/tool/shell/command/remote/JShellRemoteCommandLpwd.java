package haui.tool.shell.command.remote;

import haui.io.FileInterface.FileInterface;
import haui.tool.shell.command.JShellCommandDefault;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.util.HashMap;

/**
 * Module:      JShellFtpCommandLpwd.java<br>
 *              $Source: $
 *<p>
 * Description: pwd command for the local system.<br>
 *</p><p>
 * Created:     12.04.2004  by AE
 *</p><p>
 * @history     12.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellRemoteCommandLpwd
  extends JShellCommandDefault
{
  public JShellRemoteCommandLpwd( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "lpwd")).getCommandLineContainer());
    setDisplayString("show current local path");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, CommandLineContainer clc)
    throws haui.tool.shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getBaseFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    if( strArgs == null && hmOptions.size() == 0)
    {
      jse.getOut().println( fi.getAbsolutePath());
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
package haui.tool.shell.command.remote;

import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.SocketTypeFile;
import haui.tool.shell.command.JShellCommandDefault;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.util.HashMap;

import cz.dhl.ftp.Ftp;

/**
 * Module:      JShellFtpCommandBye.java<br>
 *              $Source: $
 *<p>
 * Description: bye command. Closes the connection and exits the ftp session.<br>
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
public class JShellRemoteCommandBye
  extends JShellCommandDefault
{
  public JShellRemoteCommandBye( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "bye")).getCommandLineContainer());
    setDisplayString("quit and close ftp session");
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

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    if( strArgs == null && hmOptions.size() == 0)
    {
      if( fi.getConnObj() instanceof Ftp)
        ((Ftp)fi.getConnObj()).disconnect();
      else if( fi instanceof SocketTypeFile)
        ((SocketTypeFile)fi).disconnect();
      iStatus = JShellEnv.EXITVALUE;
    }

    return iStatus;
  }
}

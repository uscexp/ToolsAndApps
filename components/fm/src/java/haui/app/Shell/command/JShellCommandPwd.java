package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.FileInterface;

import java.util.HashMap;

/**
 * Module:      JShellCommandPwd.java<br>
 *              $Source: $
 *<p>
 * Description: pwd command.<br>
 *</p><p>
 * Created:     26.03.2004  by AE
 *</p><p>
 * @history     26.03.2004  by AE: Created.<br>
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
public class JShellCommandPwd
  extends JShellCommandDefault
{
  public JShellCommandPwd( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "pwd", jse));
    setDisplayString("show current path");
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

  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp)
    throws haui.app.Shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    if( strArgs == null && hmOptions.size() == 0)
    {
      jse.getOut().println( fi.getAbsolutePath());
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
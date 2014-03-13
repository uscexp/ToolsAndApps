package haui.app.Shell.command.remote;

import haui.app.Shell.command.*;
import haui.app.Shell.engine.*;
import haui.app.Shell.util.*;
import haui.io.*;

import java.text.Collator;
import java.util.*;

/**
 * Module:      JShellCommandLcd.java<br>
 *              $Source: $
 *<p>
 * Description: cd command for the local system.<br>
 *</p><p>
 * Created:	    24.03.2004  by AE
 *</p><p>
 * @history     24.03.2004  by AE: Created.<br>
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
public class JShellRemoteCommandLcd
  extends JShellCommandDefault
{
  public JShellRemoteCommandLcd( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "lcd", jse));
    setDisplayString("change local directory");
  }

  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp)
    throws JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getBaseFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    String strArgs = jscp.getArguments();
    if( strArgs == null || strArgs.equals( ""))
      return iStatus;
    FileInterface fiNew = FileConnector.changeDirectory( fi, strArgs, fi.getConnObj(), jse.getAppName(),
      jse.getAppPropperties());
    if( fiNew != null)
    {
      jse.setBaseFileInterface( fiNew);
      iStatus = JShellEnv.OKVALUE;
    }
    jse.getOut().println( jse.getBaseFileInterface().getAbsolutePath());

    return iStatus;
  }

  public void usage( JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <Path>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());

    jse.getOut().println( strbufUsage.toString());
  }
}
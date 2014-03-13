package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.FileInterface;

import java.util.HashMap;

/**
 * Module:      JShellCommandEcho.java<br>
 *              $Source: $
 *<p>
 * Description: print string.<br>
 *</p><p>
 * Created:     31.03.2004  by AE
 *</p><p>
 * @history     31.03.2004  by AE: Created.<br>
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
public class JShellCommandEcho
  extends JShellCommandDefault
{
  public JShellCommandEcho( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "echo", jse));
    setDisplayString("print a string");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <string>");
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
    String strArg = null;
    String strEcho = "";
    if( strArgs != null && strArgs.length > 0)
      strArg = jscp.getArguments();
    if( strArg != null)
    {
      if( (strArg.startsWith( "\"") && strArg.endsWith( "\"")) || (strArg.startsWith( "\'") && strArg.endsWith( "\'")))
        strArg = strArg.substring( 1, strArg.length()-1);
      strArg = replaceSpecialChar( strArg);
      jse.getOut().println( strArg);
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }

  private String replaceSpecialChar( String str)
  {
    int iIdx = 0;
    int iIdxOld = iIdx;
    StringBuffer strEcho = new StringBuffer( str);
    while( (iIdx = strEcho.indexOf( "\\\\")) != -1)
    {
      strEcho.setCharAt( iIdx, '\\');
      strEcho.deleteCharAt( iIdx+1);
    }
    while( (iIdx = strEcho.indexOf( "\\n")) != -1)
    {
      strEcho.setCharAt( iIdx, '\n');
      strEcho.deleteCharAt( iIdx+1);
    }
    while( (iIdx = strEcho.indexOf( "\\t")) != -1)
    {
      strEcho.setCharAt( iIdx, '\t');
      strEcho.deleteCharAt( iIdx+1);
    }
    while( (iIdx = strEcho.indexOf( "\\r")) != -1)
    {
      strEcho.setCharAt( iIdx, '\r');
      strEcho.deleteCharAt( iIdx+1);
    }
    while( (iIdx = strEcho.indexOf( "\\b")) != -1)
    {
      strEcho.deleteCharAt( iIdx+1);
      strEcho.deleteCharAt( iIdx);
      if( iIdx > 0)
        strEcho.deleteCharAt( iIdx-1);
    }
    while( (iIdx = strEcho.indexOf( "\\\"")) != -1)
    {
      strEcho.setCharAt( iIdx, '\"');
      strEcho.deleteCharAt( iIdx+1);
    }
    while( (iIdx = strEcho.indexOf( "\\f")) != -1)
    {
      strEcho.setCharAt( iIdx, '\f');
      strEcho.deleteCharAt( iIdx+1);
    }
    while( (iIdx = strEcho.indexOf( "\\\'", iIdx)) != -1)
    {
      strEcho.setCharAt( iIdx, '\'');
      strEcho.deleteCharAt( iIdx+1);
    }
    return strEcho.toString();
  }
}
package haui.tool.shell.command;

import haui.io.FileInterface.FileInterface;
import haui.tool.shell.engine.ArgumentIteration;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.util.HashMap;

/**
 * Module:      JShellCommandSet.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandSet.java,v $
 *<p>
 * Description: set environment variable.<br>
 *</p><p>
 * Created:     31.03.2004  by AE
 *</p><p>
 * @history     31.03.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandSet.java,v $
 * Revision 1.1  2004-08-31 16:03:25+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:48+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandSet.java,v 1.1 2004-08-31 16:03:25+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandSet
  extends JShellCommandDefault
{
  public JShellCommandSet( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "set")).getCommandLineContainer());
    setDisplayString("set environment variable");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <variable=value>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, CommandLineContainer clc)
    throws haui.tool.shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;
    ArgumentIteration ai = clc.iterationObject();

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    String strArg = null;
    if( strArgs != null && strArgs.length > 0)
      strArg = ai.getArguments();
    if( strArg != null && hmOptions.size() == 0)
    {
      int iIdx = strArg.indexOf( "=");
      if( iIdx != -1)
      {
        String strKey = strArg.substring( 0, iIdx);
        String strValue = null;
        if( iIdx+1 < strArg.length())
        {
          strValue = strArg.substring( iIdx+1);
        }
        if( strKey != null && strValue != null )
        {
          strKey = strKey.trim();
          strValue = strValue.trim();
          System.setProperty( strKey, strValue );
          jse.getOut().println( strKey + "; set to value: " + strValue);
          iStatus = JShellEnv.OKVALUE;
        }
      }
    }

    return iStatus;
  }
}
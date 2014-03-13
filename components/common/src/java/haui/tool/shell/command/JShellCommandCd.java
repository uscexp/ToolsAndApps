package haui.tool.shell.command;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.tool.shell.engine.ArgumentIteration;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellException;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

/**
 * Module:      JShellCommandCd.java<br>
 *              $Source: $
 *<p>
 * Description: cd command.<br>
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
public class JShellCommandCd
  extends JShellCommandDefault
{
  public JShellCommandCd( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "cd")).getCommandLineContainer());
    setDisplayString("change directory");
  }

  public int processCommand(JShellEnv jse, CommandLineContainer clc)
    throws JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;
    ArgumentIteration ai = clc.iterationObject();

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    String strArgs = ai.getArguments();
    if( strArgs == null || strArgs.equals( ""))
      return iStatus;
    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, fi.getConnObj(), 0, 0,
        jse.getAppName(), jse.getAppPropperties(), true);
    FileInterface fiNew = FileConnector.changeDirectory( fi, strArgs, fic);
    if( fiNew != null)
    {
      jse.setFileInterface( fiNew);
      iStatus = JShellEnv.OKVALUE;
    }
    jse.getOut().println( jse.getFileInterface().getAbsolutePath());

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
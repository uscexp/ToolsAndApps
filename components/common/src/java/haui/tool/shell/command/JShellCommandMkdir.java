package haui.tool.shell.command;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellException;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.util.HashMap;

/**
 * Module:      JShellCommandMkdir.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandMkdir.java,v $
 *<p>
 * Description: make directory command.<br>
 *</p><p>
 * Created:     26.04.2004  by AE
 *</p><p>
 * @history     26.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandMkdir.java,v $
 * Revision 1.1  2004-08-31 16:03:23+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:47+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandMkdir.java,v 1.1 2004-08-31 16:03:23+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandMkdir
  extends JShellCommandDefault
{
  public JShellCommandMkdir( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "mkdir")).getCommandLineContainer());
    setDisplayString("make directory");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <path ...>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, CommandLineContainer clc)
    throws JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    if( strArgs.length >= 1 && hmOptions.size() == 0)
    {
      StringBuffer strbufPath = new StringBuffer( fi.getAbsolutePath());
      if( strbufPath.charAt( strbufPath.length()-1) == fi.separatorChar())
        strbufPath.deleteCharAt( strbufPath.length()-1);

      for( int i = 0; i < strArgs.length; ++i)
      {
        /*
        StringBuffer strbufTarget = new StringBuffer( strbufPath.toString() );
        strbufTarget.append( fi.separatorChar() );
        strbufTarget.append( strArgs[i] );
        */
        StringBuffer strbufTarget = new StringBuffer();
        if( fi.isAbsolutePath( strArgs[i]))
        {
          strbufTarget.append( strArgs[i]);
        }
        else
        {
          strbufTarget.append( strbufPath.toString());
          strbufTarget.append( fi.separatorChar());
          strbufTarget.append( strArgs[i] );
        }

        FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, fi.getConnObj(), 0, 0,
            jse.getAppName(), jse.getAppPropperties(), true);
        FileInterface fiTarget = FileConnector.createFileInterface( strbufTarget.toString(), null, false, fic);
        /*
        FileInterface fiParent = fiTarget.getParentFileInterface();
        if( !fiParent.exists() )
        {
          fiTarget = FileConnector.createFileInterface( strArgs[i], null,
            false, fi.getConnObj(), jse.getAppPropperties() );
          fiParent = fiTarget.getParentFileInterface();
        }
        */
        if( fiTarget.exists())
        {
          jse.getErr().println( "Error:\tFile " + strArgs[i] + " already exists!");
        }
        else
        {
          fiTarget.mkdir();
        }
      }

      jse.getOut().println( "dir(s) created");
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
package haui.tool.shell.command.remote;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.tool.shell.command.JShellCommandDefault;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.tool.shell.util.FileInterfaceUtil;

import java.util.HashMap;
import java.util.Vector;

/**
 * Module:      JShellFtpCommandPut.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\remote\\JShellRemoteCommandPut.java,v $
 *<p>
 * Description: put command.<br>
 *</p><p>
 * Created:     18.05.2004  by AE
 *</p><p>
 * @history     18.05.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellRemoteCommandPut.java,v $
 * Revision 1.1  2004-08-31 16:03:22+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:50+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\remote\\JShellRemoteCommandPut.java,v 1.1 2004-08-31 16:03:22+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellRemoteCommandPut
  extends JShellCommandDefault
{
  public JShellRemoteCommandPut( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "put")).getCommandLineContainer());
    setDisplayString("put file/dir from local system to remote system");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <sourcefile> [<targetfile>]\n");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " [-f] [-r] <sourcefile sourcedir ...> <targetdir>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n -f\tforce override existing files");
    strbufUsage.append( "\n -r\trecursive directory copy");

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, CommandLineContainer clc)
    throws haui.tool.shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fiRemote = jse.getFileInterface();
    FileInterface fiLocal = jse.getBaseFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    boolean blForce = false;
    if( strArgs != null && strArgs.length > 0 && hmOptions.size() >= 0)
    {
      boolean blRecursive = false;
      if( hmOptions.get( "-r") != null)
        blRecursive = true;
      if( hmOptions.get( "-f") != null)
        blForce = true;
      Vector vecFiSources = new Vector();

      StringBuffer strbufPath = new StringBuffer( fiLocal.getAbsolutePath());
      if( strbufPath.charAt( strbufPath.length()-1) == fiLocal.separatorChar())
        strbufPath.deleteCharAt( strbufPath.length()-1);

      if( strArgs.length == 1)
      {
        Vector vec = FileInterfaceUtil.getSourceFileInterfaces( fiLocal, jse, strbufPath.toString(),
          strArgs[0] );
        vecFiSources.addAll( vec );
      }
      else
      {
        for( int i = 0; i < strArgs.length - 1; ++i )
        {
          Vector vec = FileInterfaceUtil.getSourceFileInterfaces( fiLocal, jse, strbufPath.toString(),
            strArgs[i] );
          vecFiSources.addAll( vec );
        }
      }

      strbufPath = new StringBuffer( fiRemote.getAbsolutePath());
      if( strbufPath.charAt( strbufPath.length()-1) == fiRemote.separatorChar())
        strbufPath.deleteCharAt( strbufPath.length()-1);

      StringBuffer strbufTarget = new StringBuffer( strbufPath.toString());
      if( strArgs.length > 1 && !strArgs[strArgs.length - 1].equals( "."))
      {
        strbufTarget.append( fiRemote.separatorChar() );
        strbufTarget.append( strArgs[strArgs.length - 1] );
      }
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, fiRemote.getConnObj(), 0, 0,
          jse.getAppName(), jse.getAppPropperties(), true);
      FileInterface fiTarget = FileConnector.createFileInterface( strbufTarget.toString(), null, false, fic);
      FileInterface fiParent = fiTarget.getParentFileInterface();
      if( !fiParent.exists())
      {
        fiTarget = FileConnector.createFileInterface( strArgs[ strArgs.length-1], null, false, fic);
        fiParent = fiTarget.getParentFileInterface();
      }
      if( fiTarget.exists() && fiTarget.isDirectory() )
      {
        FileInterfaceUtil.copy( jse, vecFiSources, fiTarget, false, blForce, blRecursive);
      }
      else if( vecFiSources.size() == 1 && (((FileInterface)vecFiSources.elementAt(0)).isFile()
                                            || ((FileInterface)vecFiSources.elementAt(0)).isArchive()))
      {
        FileInterfaceUtil.copy( jse, vecFiSources, fiTarget, true, blForce, blRecursive);
      }
      else
        return iStatus;

      jse.getOut().print( "file(s) copied to ");
      jse.getOut().println( strArgs[ strArgs.length-1]);
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
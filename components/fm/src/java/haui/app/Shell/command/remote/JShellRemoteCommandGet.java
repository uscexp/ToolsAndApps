package haui.app.Shell.command.remote;

import haui.app.Shell.command.*;
import haui.app.Shell.engine.*;
import haui.io.FileInterface;
import haui.io.FileConnector;
import haui.app.Shell.util.FileInterfaceUtil;

import java.util.HashMap;
import java.util.Vector;

/**
 * Module:      JShellFtpCommandGet.java<br>
 *              $Source: $
 *<p>
 * Description: get command.<br>
 *</p><p>
 * Created:     19.05.2004  by AE
 *</p><p>
 * @history     19.05.2004  by AE: Created.<br>
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
public class JShellRemoteCommandGet
  extends JShellCommandDefault
{
  public JShellRemoteCommandGet( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "get", jse));
    setDisplayString("get file/dir from remote system to local system");
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

  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp)
    throws haui.app.Shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fiRemote = jse.getFileInterface();
    FileInterface fiLocal = jse.getBaseFileInterface();
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    boolean blForce = false;
    if( strArgs != null && strArgs.length > 0 && hmOptions.size() >= 0)
    {
      boolean blRecursive = false;
      if( hmOptions.get( "-r") != null)
        blRecursive = true;
      if( hmOptions.get( "-f") != null)
        blForce = true;
      Vector vecFiSources = new Vector();

      StringBuffer strbufPath = new StringBuffer( fiRemote.getAbsolutePath());
      if( strbufPath.charAt( strbufPath.length()-1) == fiRemote.separatorChar())
        strbufPath.deleteCharAt( strbufPath.length()-1);

      if( strArgs.length == 1)
      {
        Vector vec = FileInterfaceUtil.getSourceFileInterfaces( fiRemote, jse, strbufPath.toString(),
          strArgs[0] );
        vecFiSources.addAll( vec );
      }
      else
      {
        for( int i = 0; i < strArgs.length - 1; ++i )
        {
          Vector vec = FileInterfaceUtil.getSourceFileInterfaces( fiRemote, jse, strbufPath.toString(),
            strArgs[i] );
          vecFiSources.addAll( vec );
        }
      }

      strbufPath = new StringBuffer( fiLocal.getAbsolutePath());
      if( strbufPath.charAt( strbufPath.length()-1) == fiLocal.separatorChar())
        strbufPath.deleteCharAt( strbufPath.length()-1);

      StringBuffer strbufTarget = new StringBuffer( strbufPath.toString());
      if( strArgs.length > 1 && !strArgs[strArgs.length - 1].equals( "."))
      {
        strbufTarget.append( fiLocal.separatorChar() );
        strbufTarget.append( strArgs[strArgs.length - 1] );
      }
      FileInterface fiTarget = FileConnector.createFileInterface( strbufTarget.toString(), null,
          false, fiLocal.getConnObj(), jse.getAppName(), jse.getAppPropperties() );
      FileInterface fiParent = fiTarget.getParentFileInterface();
      if( !fiParent.exists())
      {
        fiTarget = FileConnector.createFileInterface( strArgs[ strArgs.length-1], null,
          false, fiLocal.getConnObj(), jse.getAppName(), jse.getAppPropperties() );
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
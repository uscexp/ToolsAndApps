package haui.app.Shell.command;

import haui.util.AppProperties;
import haui.app.Shell.engine.*;
import haui.io.*;
import haui.app.Shell.util.FileInterfaceUtil;

import java.util.Vector;
import java.util.HashMap;

/**
 * Module:      JShellCommandMv.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandMv.java,v $
 *<p>
 * Description: move command.<br>
 *</p><p>
 * Created:     22.04.2004  by AE
 *</p><p>
 * @history     22.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandMv.java,v $
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
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandMv.java,v 1.1 2004-08-31 16:03:23+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandMv
  extends JShellCommandDefault
{
  public JShellCommandMv( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "mv", jse));
    setDisplayString("move/rename file");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <sourcefile sourcedir ...> <targetfile or targetdir>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp)
    throws JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    if( strArgs.length >= 2 && hmOptions.size() == 0)
    {
      Vector vecFiSource = new Vector();
      StringBuffer strbufPath = new StringBuffer( fi.getAbsolutePath());
      if( strbufPath.charAt( strbufPath.length()-1) == fi.separatorChar())
        strbufPath.deleteCharAt( strbufPath.length()-1);
      for( int i = 0; i < strArgs.length-1; ++i)
      {
        Vector vec = FileInterfaceUtil.getSourceFileInterfaces( fi, jse, strbufPath.toString(), strArgs[i]);
        vecFiSource.addAll( vec);
      }
      /*
      StringBuffer strbufTarget = new StringBuffer( strbufPath.toString());
      strbufTarget.append( fi.separatorChar());
      strbufTarget.append( strArgs[ strArgs.length-1]);
      */
      StringBuffer strbufTarget = new StringBuffer();
      if( fi.isAbsolutePath( strArgs[strArgs.length-1]))
      {
        strbufTarget.append( strArgs[strArgs.length-1]);
      }
      else
      {
        strbufTarget.append( strbufPath.toString());
        strbufTarget.append( fi.separatorChar());
        strbufTarget.append( strArgs[strArgs.length-1] );
      }
      FileInterface fiTarget = FileConnector.createFileInterface( strbufTarget.toString(), null,
          false, fi.getConnObj(), jse.getAppName(), jse.getAppPropperties() );
      /*
      FileInterface fiParent = fiTarget.getParentFileInterface();
      if( !fiParent.exists())
      {
        fiTarget = FileConnector.createFileInterface( strArgs[ strArgs.length-1], null,
          false, fi.getConnObj(), jse.getAppPropperties() );
        fiParent = fiTarget.getParentFileInterface();
      }
      */
      if( fiTarget.exists() && fiTarget.isDirectory() )
      {
        for( int i = 0; i < vecFiSource.size(); ++i)
        {
          strbufTarget = new StringBuffer( fiTarget.getAbsolutePath() );
          strbufTarget.append( fi.separatorChar() );
          strbufTarget.append( ((FileInterface)vecFiSource.elementAt(i)).getName() );
          String strTarget = strbufTarget.toString();
          FileInterface fiNewTarget = FileConnector.createFileInterface( strTarget, null,
            false, fi.getConnObj(), jse.getAppName(), jse.getAppPropperties() );
          ((FileInterface)vecFiSource.elementAt(i)).renameTo( fiNewTarget);
        }
      }
      else if( vecFiSource.size() == 1)
        ((FileInterface)vecFiSource.elementAt(0)).renameTo( fiTarget);
      else
        return iStatus;
      jse.getOut().print( strArgs[0]);
      jse.getOut().print( " moved to ");
      jse.getOut().println( strArgs[1]);
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
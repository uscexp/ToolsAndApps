package haui.app.Shell.command;

import haui.util.AppProperties;
import haui.app.Shell.engine.*;
import haui.io.*;
import haui.app.Shell.util.FileInterfaceUtil;

import java.io.*;
import java.util.Vector;
import java.util.HashMap;

/**
 * Module:      JShellCommandCp.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandCp.java,v $
 *<p>
 * Description: copy command.<br>
 *</p><p>
 * Created:     26.04.2004  by AE
 *</p><p>
 * @history     26.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandCp.java,v $
 * Revision 1.1  2004-08-31 16:03:23+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:44+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandCp.java,v 1.1 2004-08-31 16:03:23+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandCp
  extends JShellCommandDefault
{
  public JShellCommandCp( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "cp", jse));
    setDisplayString("copy file(s)");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <sourcefile> <targetfile>\n");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " [-f] [-r] <sourcefile sourcedir ...> <targetdir>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n -f\tforce override existing files");
    strbufUsage.append( "\n -r\trecursive directory copy");

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
    boolean blForce = false;
    if( strArgs.length >= 2 && hmOptions.size() <= 1)
    {
      boolean blRecursive = false;
      if( hmOptions.get( "-r") != null)
        blRecursive = true;
      if( hmOptions.get( "-f") != null)
        blForce = true;
      Vector vecFiSources = new Vector();

      StringBuffer strbufPath = new StringBuffer( fi.getAbsolutePath());
      if( strbufPath.charAt( strbufPath.length()-1) == fi.separatorChar())
        strbufPath.deleteCharAt( strbufPath.length()-1);

      for( int i = 0; i < strArgs.length-1; ++i)
      {
        Vector vec = FileInterfaceUtil.getSourceFileInterfaces( fi, jse, strbufPath.toString(), strArgs[i]);
        vecFiSources.addAll( vec);
      }
      StringBuffer strbufTarget = new StringBuffer();
      /*
      StringBuffer strbufTarget = new StringBuffer( strbufPath.toString());
      strbufTarget.append( fi.separatorChar());
      strbufTarget.append( strArgs[ strArgs.length-1]);
      */
      if( fi.isAbsolutePath( strArgs[ strArgs.length-1]))
      {
        strbufTarget.append( strArgs[ strArgs.length-1]);
      }
      else
      {
        strbufTarget.append( strbufPath.toString());
        strbufTarget.append( fi.separatorChar());
        strbufTarget.append( strArgs[ strArgs.length-1]);
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
package haui.tool.shell.command;

import haui.io.FileInterface.FileInterface;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellException;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.tool.shell.util.FileInterfaceUtil;

import java.util.HashMap;
import java.util.Vector;

/**
 * Module:      JShellCommandRm.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandRm.java,v $
 *<p>
 * Description: remove command.<br>
 *</p><p>
 * Created:     26.04.2004  by AE
 *</p><p>
 * @history     26.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandRm.java,v $
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
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandRm.java,v 1.1 2004-08-31 16:03:25+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandRm
  extends JShellCommandDefault
{
  public JShellCommandRm( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "rm")).getCommandLineContainer());
    setDisplayString("remove file(s)");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " [-r] <file dir ...>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n -r\tremove directory recursive");

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
    Vector vecFiSource = new Vector();
    if( strArgs.length >= 1 && hmOptions.size() <= 1)
    {
      boolean blRecursive = false;
      if( hmOptions.get( "-r") != null)
        blRecursive = true;
      //FileInterface[] fiSources = new FileInterface[ strArgs.length];

      StringBuffer strbufPath = new StringBuffer( fi.getAbsolutePath());
      if( strbufPath.charAt( strbufPath.length()-1) == fi.separatorChar())
        strbufPath.deleteCharAt( strbufPath.length()-1);

      for( int i = 0; i < strArgs.length; ++i)
      {
        Vector vec = FileInterfaceUtil.getSourceFileInterfaces( fi, jse, strbufPath.toString(), strArgs[i]);
        vecFiSource.addAll( vec);
      }
      remove( jse, vecFiSource, blRecursive);

      jse.getOut().println( "file(s) removed");
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }

  private void remove( JShellEnv jse, Vector vecFi, boolean blRecursive)
  {
    Vector vecDirs = new Vector();
    for( int i = 0; i < vecFi.size(); ++i)
    {
      FileInterface fiSource = ((FileInterface)vecFi.elementAt(i));
      if( fiSource.exists())
      {
        if( fiSource.isDirectory())
        {
          FileInterface[] fiSubSources = fiSource._listFiles();
          Vector vecSubFiles = new Vector();
          for( int ii = 0; ii < fiSubSources.length; ++ii)
          {
            FileInterface fiTmp = fiSubSources[ii];
            vecSubFiles.add( fiTmp);
          }
          vecDirs.add( fiSource);
          if( blRecursive )
            remove( jse, vecSubFiles, blRecursive );
        }
        else
        {
          fiSource.delete();
        }
      }
    }
    for( int ii = 0; ii < vecDirs.size(); ++ii)
    {
      FileInterface fi = (FileInterface)vecDirs.elementAt( ii);
      FileInterface[] fiSubSources = fi._listFiles();

      if( fiSubSources == null || fiSubSources.length == 0 )
        fi.delete();
    }
  }
}
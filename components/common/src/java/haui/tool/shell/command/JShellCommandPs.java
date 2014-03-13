package haui.tool.shell.command;

import haui.io.FileInterface.FileInterface;
import haui.tool.shell.engine.BackgroundThread;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.util.HashMap;
import java.util.Vector;

/**
 * Module:      JShellCommandPs.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandPs.java,v $
 *<p>
 * Description: show current background processes command.<br>
 *</p><p>
 * Created:     06.04.2004  by AE
 *</p><p>
 * @history     06.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandPs.java,v $
 * Revision 1.1  2004-08-31 16:03:25+02  t026843
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
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandPs.java,v 1.1 2004-08-31 16:03:25+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandPs
  extends JShellCommandDefault
{
  public JShellCommandPs( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "ps")).getCommandLineContainer());
    setDisplayString("show current background processes");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, CommandLineContainer clc)
    throws haui.tool.shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    if( strArgs == null && hmOptions.size() == 0)
    {
      try
      {
        jse.clearInactivProcesses();
        Vector vec = jse.getBackgroundProcessVector();
        jse.getOut().print( "NR.");
        jse.getOut().print( "\t");
        jse.getOut().println( "Command");
        for( int i = 1; i <= vec.size(); ++i )
        {
          BackgroundThread bt = (BackgroundThread)vec.elementAt( i-1);
          jse.getOut().print( i);
          jse.getOut().print( "\t");
          jse.getOut().println( bt.getCommandLine());
        }
        iStatus = JShellEnv.OKVALUE;
      }
      catch( Exception ex)
      {
        //ex.printStackTrace();
      }
    }

    return iStatus;
  }
}
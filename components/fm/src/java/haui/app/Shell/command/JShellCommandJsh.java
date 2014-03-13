package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.FileInterface;

import java.util.HashMap;
import java.io.*;

/**
 * Module:      JShellCommandJsh.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandJsh.java,v $
 *<p>
 * Description: JShell batch file execution.<br>
 *</p><p>
 * Created:     31.03.2004  by AE
 *</p><p>
 * @history     31.03.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandJsh.java,v $
 * Revision 1.1  2004-08-31 16:03:26+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:46+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandJsh.java,v 1.1 2004-08-31 16:03:26+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandJsh
  extends JShellCommandDefault
{
  public JShellCommandJsh( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "jsh", jse));
    setDisplayString("execute JShell batch file");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <batchfile>");
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
    if( strArgs != null && strArgs.length > 0)
      strArg = strArgs[0];
    if( strArg != null && hmOptions.size() == 0)
    {
      try
      {
        StringBuffer strbufCmds = new StringBuffer();
        BufferedReader br = new BufferedReader( new FileReader( strArg ) );
        String strLine = null;
        while( ( strLine = br.readLine() ) != null )
        {
          strbufCmds.append( strLine);
          strbufCmds.append( "\n");
        }
        JShellEngine.processCommands( strbufCmds.toString(), jse, false);
        iStatus = JShellEnv.OKVALUE;
      }
      catch( FileNotFoundException fnfex )
      {
        fnfex.printStackTrace();
        jse.getOut().println( fnfex.toString());
      }
      catch( IOException ioex )
      {
        ioex.printStackTrace();
        jse.getOut().println( ioex.toString());
      }
    }

    return iStatus;
  }
}
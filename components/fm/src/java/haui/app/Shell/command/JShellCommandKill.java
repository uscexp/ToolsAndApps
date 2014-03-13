package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.FileInterface;

import java.util.HashMap;

/**
 * Module:      JShellCommandKill.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandKill.java,v $
 *<p>
 * Description: kill background processes command.<br>
 *</p><p>
 * Created:     06.04.2004  by AE
 *</p><p>
 * @history     06.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandKill.java,v $
 * Revision 1.1  2004-08-31 16:03:26+02  t026843
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
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandKill.java,v 1.1 2004-08-31 16:03:26+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandKill
  extends JShellCommandDefault
{
  public JShellCommandKill( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "kill", jse));
    setDisplayString("kill background process");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <processnumber>");
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
    if( strArgs != null  && strArgs.length == 1)
    {
      try
      {
        String strArg = strArgs[0];
        int iIdx = Integer.parseInt( strArg );
        jse.getProcess( iIdx - 1 ).kill();
        jse.getOut().print( "Process Nr. ");
        jse.getOut().print( iIdx);
        jse.getOut().println( " killed!");
        iStatus = JShellEnv.OKVALUE;
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }

    return iStatus;
  }
}
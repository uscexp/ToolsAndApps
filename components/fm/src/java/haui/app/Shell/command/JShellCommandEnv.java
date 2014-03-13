package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.FileInterface;

import java.util.HashMap;
import java.util.Properties;
import java.util.Iterator;

/**
 * Module:      JShellCommandEnv.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandEnv.java,v $
 *<p>
 * Description: env command.<br>
 *</p><p>
 * Created:     31.03.2004  by AE
 *</p><p>
 * @history     31.03.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandEnv.java,v $
 * Revision 1.1  2004-08-31 16:03:26+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:45+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandEnv.java,v 1.1 2004-08-31 16:03:26+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandEnv
  extends JShellCommandDefault
{
  public JShellCommandEnv( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "env", jse));
    setDisplayString("show environment");
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

  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp)
    throws haui.app.Shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    if( strArgs == null && hmOptions.size() == 0)
    {
      Properties props = System.getProperties();
      Iterator it = props.keySet().iterator();

      while( it.hasNext())
      {
        String strKey = (String)it.next();
        String strValue = (String)props.get( strKey);

        jse.getOut().print( strKey);
        jse.getOut().print( "=");
        jse.getOut().println( strValue);
      }
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
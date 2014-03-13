package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.*;

import java.util.HashMap;
import java.io.*;

/**
 * Module:      JShellCommandCat.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandCat.java,v $
 *<p>
 * Description: cat command.<br>
 *</p><p>
 * Created:     26.03.2004  by AE
 *</p><p>
 * @history     26.03.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandCat.java,v $
 * Revision 1.1  2004-08-31 16:03:21+02  t026843
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
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandCat.java,v 1.1 2004-08-31 16:03:21+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandCat
  extends JShellCommandDefault
{
  public JShellCommandCat( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "cat", jse));
    setDisplayString("view the content of a text file");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " <file>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp) throws haui.app.Shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    String strArg = null;
    for( int i = 0; strArgs != null && i < strArgs.length; ++i)
    {
      strArg = strArgs[i];
      WildcardFileInterfaceFilter wfif = null;
      String strDir = null;
      String strName = null;
      if( strArg != null )
      {
        NormalFile nf = null;
        FileConnector fc = null;
        FileInterface fiTmp = null;
        if( fi.getConnObj() == null )
        {
          nf = new NormalFile( strArg, null, jse.getAppName(), fi.getAppProperties() );
          fiTmp = nf;
        }
        else
        {
          fiTmp = FileConnector.createFileInterface( strArg, null, false, fi.getConnObj(), jse.getAppName(), jse.getAppPropperties() );
        }
        strName = fiTmp.getName();
        strDir = fiTmp.getParent();
        wfif = new WildcardFileInterfaceFilter( strName, jse.getAppName() );
      }
      if( hmOptions.size() == 0 )
      {
        FileInterface[] files = getFileInterfaces( fi, wfif);

        for( int ii = 0; ii < files.length; ++ii)
        {
          jse.getOut().println();
          jse.getOut().println( "**** Show file: " + files[ii].getName());
          jse.getOut().println();
          if( files[ii].isFile())
          {
            int iBufSize = 1024;
            byte[] buf = new byte[iBufSize];
            int iRead= 0;
            BufferedInputStream bis = null;
            try
            {
              bis = files[ii].getBufferedInputStream();

              while( ( iRead = bis.read( buf, 0, iBufSize)) != -1)
              {
                jse.getOut().write( buf, 0, iRead);
              }
              bis.close();
            }
            catch( IOException ex )
            {
              ex.printStackTrace();
              jse.getOut().println( ex.toString() );
            }
          }
          jse.getOut().println();
          jse.getOut().println( "**** End file: " + files[ii].getName());
        }
        iStatus = JShellEnv.OKVALUE;
      }
    }

    return iStatus;
  }

  private FileInterface[] getFileInterfaces( FileInterface fi, FileInterfaceFilter filter)
  {
    FileInterface[] files = null;
    if( filter == null)
      files = fi._listFiles();
    else
      files = fi._listFiles( filter);
    return files;
  }
}
package haui.tool.shell.command;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.filter.WildcardFileInterfaceFilter;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;

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
    setCommand( (new JShellCommandProcessor( jse, "cat")).getCommandLineContainer());
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

  public int processCommand(JShellEnv jse, CommandLineContainer clc) throws haui.tool.shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    String strArg = null;
    for( int i = 0; strArgs != null && i < strArgs.length; ++i)
    {
      strArg = strArgs[i];
      WildcardFileInterfaceFilter wfif = null;
      //String strDir = null;
      String strName = null;
      if( strArg != null )
      {
        FileInterface fiTmp = null;
        FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, fi.getConnObj(), 0, 0,
            jse.getAppName(), jse.getAppPropperties(), true);
        fiTmp = FileConnector.createFileInterface( strArg, null, false, fic);
        strName = fiTmp.getName();
        //strDir = fiTmp.getParent();
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
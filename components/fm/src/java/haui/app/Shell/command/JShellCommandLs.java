package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.app.Shell.util.*;
import haui.io.*;

import java.text.Collator;
import java.util.*;

/**
 * Module:      JShellCommandLs.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandLs.java,v $
 *<p>
 * Description: ls command.<br>
 *</p><p>
 * Created:	    24.03.2004  by AE
 *</p><p>
 * @history     24.03.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandLs.java,v $
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
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandLs.java,v 1.1 2004-08-31 16:03:25+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandLs
  extends JShellCommandDefault
{
  FileInterface[] m_files = null;

  public JShellCommandLs( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "ls", jse));
    setDisplayString("list directory");
  }

  public int processCommand( JShellEnv jse, JShellCommandProcessor jscp)
    throws JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    boolean blOk = false;
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    Vector vecFiSource = new Vector();
    StringBuffer strbufPath = new StringBuffer( fi.getAbsolutePath() );
    if( strbufPath.charAt( strbufPath.length() - 1 ) == fi.separatorChar() )
      strbufPath.deleteCharAt( strbufPath.length() - 1 );
    if( strArgs != null)
    {
      for( int i = 0; i < strArgs.length; ++i )
      {
        Vector vec = FileInterfaceUtil.getSourceFileInterfaces( fi, jse, strbufPath.toString(),
          strArgs[i] );
        if( vec.size() == 1)
        {
          FileInterface fiTmp = (FileInterface)vec.elementAt( 0);
          if( strArgs[0].endsWith( fiTmp.getName()) && fiTmp.exists())
          {
            blOk = true;
            if( fiTmp.isDirectory() )
            {
              Vector vecSub = FileInterfaceUtil.getSourceFileInterfaces( fi, jse,
                fiTmp.getAbsolutePath(),
                "*" );
              vec.remove( 0 );
              vecFiSource.addAll( vecSub );
            }
          }
        }
        vecFiSource.addAll( vec );
      }
    }
    else
    {
      blOk = true;
      vecFiSource = FileInterfaceUtil.getSourceFileInterfaces( fi, jse, strbufPath.toString(),
          "*" );
    }
    if( hmOptions.get( "-l") == null )
    {
      String str = formatShort( vecFiSource);
      if( str != null)
      {
        blOk = true;
        jse.getOut().println( str);
      }
      else
        jse.getOut().println();

      if( blOk)
      {
        jse.getOut().println( finalLine( vecFiSource));
        iStatus = JShellEnv.OKVALUE;
      }
    }
    else
    {
      String str = formatLong( vecFiSource);
      if( str != null)
      {
        jse.getOut().println( str);
        jse.getOut().println( finalLine( vecFiSource));
        iStatus = JShellEnv.OKVALUE;
      }
    }
    return iStatus;
  }

  public void usage( JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " [-l] <WildCardString ...>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n -l\tlong format");

    jse.getOut().println( strbufUsage.toString());
  }

  private String finalLine( Vector vecFi)
  {
    long[] lCAndL = getDirContentCountAndLength( vecFi);
    String str = "File(s)\t" + lCAndL[0] + ",\t" + lCAndL[1] +  " bytes";
    return str;
  }

  private long[] getDirContentCountAndLength( Vector vecFi)
  {
    long[] lRet = new long[2];
    long lSize = 0;
    long lCount = 0;
    lCount = vecFi.size();
    for( int i = 0; i < vecFi.size(); i++)
    {
      FileInterface fi = (FileInterface)vecFi.elementAt( i);
      lSize += fi.length();
    }
    lRet[0] = lCount;
    lRet[1] = lSize;
    return lRet;
  }

  private String formatLong( Vector vecFi)
  {
    StringBuffer strbufOut = new StringBuffer();
    for( int i = 0; i < vecFi.size(); ++i )
    {
      FileInterface fi = ( FileInterface )vecFi.elementAt( i );
      String str = fi.getName();

      if( fi.isArchive() )
        strbufOut.append( "a" );
      else if( fi.isDirectory() )
        strbufOut.append( "d" );
      else
        strbufOut.append( "-" );

      if( fi.canRead() )
        strbufOut.append( "r" );
      else
        strbufOut.append( "-" );

      if( fi.canWrite() )
        strbufOut.append( "w" );
      else
        strbufOut.append( "-" );

      if( fi.isArchive() || fi.isDirectory() )
        strbufOut.append( "x " );
      else
        strbufOut.append( "? " );

      strbufOut.append( StringUtil.numberToString( fi.length(), 12 ) );
      strbufOut.append( "  " );
      strbufOut.append( StringUtil.convertLastModifiedToString( fi.lastModified() ) );
      strbufOut.append( "  " );

      strbufOut.append( str );
      strbufOut.append( "\n" );
    }
    return strbufOut.toString();
  }

  private String formatShort( Vector vecFi)
  {
    String strOut = null;
    for( int i = 0; i < vecFi.size(); ++i )
    {
      FileInterface fi = ( FileInterface )vecFi.elementAt( i );
      String str = fi.getName();
      if( i == 0 )
        strOut = str + "\n";
      else
        strOut += str + "\n";
    }
    return strOut;
  }
}
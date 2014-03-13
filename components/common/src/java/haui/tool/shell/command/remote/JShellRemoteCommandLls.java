package haui.tool.shell.command.remote;

import haui.io.FileInterface.FileInterface;
import haui.tool.shell.command.JShellCommandDefault;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellException;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;
import haui.tool.shell.util.FileInterfaceUtil;
import haui.tool.shell.util.StringUtil;

import java.util.HashMap;
import java.util.Vector;

/**
 * Module:      JShellCommandLls.java<br>
 *              $Source: $
 *<p>
 * Description: ls command for the local system.<br>
 *</p><p>
 * Created:	    19.05.2004  by AE
 *</p><p>
 * @history     19.05.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellRemoteCommandLls
  extends JShellCommandDefault
{
  public JShellRemoteCommandLls( JShellEnv jse)
  {
    setCommand( (new JShellCommandProcessor( jse, "lls")).getCommandLineContainer());
    setDisplayString("list local directory");
  }

  public int processCommand( JShellEnv jse, CommandLineContainer clc)
    throws JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getBaseFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    boolean blOk = false;
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
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
package haui.app.Shell.util;

import haui.app.Shell.engine.JShellEnv;
import haui.util.AppProperties;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 *    Module:       StringUtil.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\util\\StringUtil.java,v $
 *<p>
 *    Description:  string utility.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: StringUtil.java,v $
 *    Revision 1.1  2004-08-31 16:03:16+02  t026843
 *    Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *    Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *    Revision 1.0  2004-06-22 14:06:52+02  t026843
 *    Initial revision
 *
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: 1.1 $<br>
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\util\\StringUtil.java,v 1.1 2004-08-31 16:03:16+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class StringUtil
{
  private static SimpleDateFormat m_simpleDateFormat = null;

  /**
   * error= -1.
   */
  public static final int parseInt( String strNumber )
  {
    try
    {
      return Integer.parseInt( strNumber );
    }
    catch( NumberFormatException ex )
    {
      return -1;
    }
  }

  /**
   * reading one line.
   */
  public static final String readLine( InputStream inStream )
  {
    try
    {
      BufferedReader reader = new BufferedReader( new InputStreamReader( inStream ) );
      return reader.readLine();
    }
    catch( IOException ex )
    {
      AppProperties.getPrintStreamOutput( JShellEnv.APPNAME).println( ex.toString());
      //System.err.println( ex.toString() );
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * get first command token
   */
  public static final String readFirstCommandToken( String strReadLine )
  {
    StringTokenizer toknizerCommand = new StringTokenizer( strReadLine, " \t\n", false );
    String strCommand = null;
    try
    {
      strCommand = toknizerCommand.nextToken();
    }
    catch( NoSuchElementException ex )
    {
      // Nothing is here.
      return null;
    }
    return strCommand;
  }

  /**
   * full fill string length
   */
  public static final String fullfillStringLengthRight( String strArg, int fullfillLength )
  {
    StringBuffer strbufWrk = new StringBuffer();
    for( int iLength = strArg.length(); iLength < fullfillLength; iLength++ )
    {
      strbufWrk.append( " " );
    }
    return strbufWrk.toString() + strArg;
  }

  /**
   * full fill string length
   */
  public static final String fullfillStringLengthLeft( String strArg, int fullfillLength )
  {
    StringBuffer strbufWrk = new StringBuffer();
    for( int iLength = strArg.length(); iLength < fullfillLength; iLength++ )
    {
      strbufWrk.append( " " );
    }
    return strArg + strbufWrk.toString();
  }

  public static final String eraseChar( String strArg, char cErase )
  {
    int iFind = strArg.indexOf( cErase );
    while( iFind >= 0)
    {
      StringBuffer strbufWrk = new StringBuffer();
      strbufWrk.append( strArg.substring( 0, iFind ) );
      strbufWrk.append( strArg.substring( iFind + 1, strArg.length() ) );
      strArg = strbufWrk.toString();
      iFind = strArg.indexOf( cErase );
    }
    return strArg;
  }

  public static final String[] vectorToStringarray( Vector vecArgs )
  {
    String[] args = new String[vecArgs.size()];
    for( int index = 0; index < vecArgs.size(); index++ )
    {
      args[index] = ( String )vecArgs.elementAt( index );
    }
    return args;
  }

  private static SimpleDateFormat getSimpleDateFormatInstance()
  {
    if( m_simpleDateFormat == null )
    {
      m_simpleDateFormat = new SimpleDateFormat( "dd.MM.yyyy HH:mm" );
    }
    return m_simpleDateFormat;
  }

  public static String convertLastModifiedToString( long lastModified )
  {
    return getSimpleDateFormatInstance().format( new Date( lastModified ) );
  }

  public static String numberToString( long lLength, int fullfillLength )
  {
    NumberFormat numberfmt = NumberFormat.getNumberInstance();
    return StringUtil.fullfillStringLengthRight( numberfmt.format( lLength ), fullfillLength );
  }
}
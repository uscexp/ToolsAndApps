package haui.util;

import java.io.File;
import java.util.Vector;

/**
 * Module:      ConfigPathUtil.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\ConfigPathUtil.java,v $
 *<p>
 * Description: selects the path to load and save for config files.<br>
 *</p><p>
 * Created:     29.04.2004  by AE
 *</p><p>
 * @history     29.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: ConfigPathUtil.java,v $
 * Revision 1.0  2004-06-22 14:07:01+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\ConfigPathUtil.java,v 1.0 2004-06-22 14:07:01+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class ConfigPathUtil
{
  // Constants
  public static final String APPDIRKEY = "user.dir";
  public static final String USERDIRKEY = "user.home";
  private static Vector vecAppPathWriteable = new Vector();
  private static Vector vecAppl = new Vector();
  private static Vector vecAppPath = new Vector();
  private static Vector vecUserPath = new Vector();

  public static void init( String strAppName)
  {
    int iIdx = vecAppl.indexOf( strAppName);
    if( iIdx == -1)
    {
      vecAppl.add( strAppName );
      String strAppPath = System.getProperty( APPDIRKEY );
      String strUserPath = System.getProperty( USERDIRKEY );
      vecAppPath.add( strAppPath );
      vecUserPath.add( strUserPath );
      iIdx = vecAppl.indexOf( strAppName );
      File file = new File( ( String )vecAppPath.elementAt( iIdx ) );

      if( !file.exists() || !file.canWrite() )
        vecAppPathWriteable.add( new Boolean( false));
      else
        vecAppPathWriteable.add( new Boolean( true));
    }
  }

  public static String getAppPath( String strAppName)
  {
    int iIdx = vecAppl.indexOf( strAppName);
    if( iIdx == -1)
      return null;
    return (String)vecAppPath.elementAt( iIdx);
  }

  public static String getUserPath( String strAppName)
  {
    int iIdx = vecAppl.indexOf( strAppName);
    if( iIdx == -1)
      return null;
    return (String)vecUserPath.elementAt( iIdx);
  }

  /**
   * Search file in user home directory + appname, if not exist the file will
   * be searched in user directory. Read priority is home directory + appname.
   *
   * @param strAppName: Application name
   * @param strFileName: Filename
   *
   * @return absolut path string to the readable file.
   */
  public static String getCurrentReadPath( String strAppName, String strFileName)
  {
    init( strAppName);
    String strRet = null;
    int iIdx = vecAppl.indexOf( strAppName);
    if( iIdx == -1)
      return null;
    String strUserPath = (String)vecUserPath.elementAt( iIdx);
    String strAppPath = (String)vecAppPath.elementAt( iIdx);
    StringBuffer strbufPath = new StringBuffer();
    strbufPath.append( strUserPath);
    strbufPath.append( File.separatorChar);
    strbufPath.append( strAppName);
    strbufPath.append( File.separatorChar);
    strbufPath.append( strFileName);
    strRet = strbufPath.toString();

    File file = new File( strRet);

    if( !file.exists())
    {
      strbufPath = new StringBuffer();
      strbufPath.append( strAppPath);
      strbufPath.append( File.separatorChar);
      strbufPath.append( strFileName);
      strRet = strbufPath.toString();
    }
    return strRet;
  }

  /**
   * Checks if user directory is writeable, if not home directory + appname
   * will be returned. Save priority is user directory, if home directory + appname
   * exist, this is the priority.
   *
   * @param strAppName: Application name
   * @param strFileName: Filename
   *
   * @return absolut path string to the writeable file.
   */
  public static String getCurrentSavePath( String strAppName, String strFileName)
  {
    init( strAppName);
    String strRet = null;
    int iIdx = vecAppl.indexOf( strAppName);
    if( iIdx == -1)
      return null;
    String strUserPath = (String)vecUserPath.elementAt( iIdx);
    String strAppPath = (String)vecAppPath.elementAt( iIdx);
    boolean blAppPathWriteable = ((Boolean)vecAppPathWriteable.elementAt( iIdx)).booleanValue();
    StringBuffer strbufPath = new StringBuffer();
    File file = null;
    if( blAppPathWriteable)
    {
      strbufPath.append( strAppPath);
      strbufPath.append( File.separatorChar);
      strbufPath.append( strFileName);

      file = new File( strbufPath.toString());
      if( !file.exists())
      {
        Vector vecFiles = new Vector();
        File fileDir = file.getParentFile();
        while( !fileDir.exists())
        {
          vecFiles.add( fileDir );
          fileDir = fileDir.getParentFile();
        }
        for( int i = vecFiles.size()-1; i >= 0; --i)
        {
          fileDir = (File)vecFiles.elementAt( i);
          fileDir.mkdir();
        }
      }
      strRet = strbufPath.toString();
    }

    if( file == null || (file.exists() && !file.canWrite()) || !blAppPathWriteable)
    {
      strbufPath = new StringBuffer();
      strbufPath.append( strUserPath );
      strbufPath.append( File.separatorChar );
      strbufPath.append( strAppName );

      file = new File( strbufPath.toString());
      if( file.exists())
        file.mkdir();

      strbufPath.append( File.separatorChar );
      strbufPath.append( strFileName );

      file = new File( strbufPath.toString());
      if( !file.exists())
      {
        Vector vecFiles = new Vector();
        File fileDir = file.getParentFile();
        while( !fileDir.exists())
        {
          vecFiles.add( fileDir );
          fileDir = fileDir.getParentFile();
        }
        for( int i = vecFiles.size()-1; i >= 0; --i)
        {
          fileDir = (File)vecFiles.elementAt( i);
          fileDir.mkdir();
        }
      }
      strRet = strbufPath.toString();
    }
    else
    {
      strbufPath = new StringBuffer();
      strbufPath.append( strUserPath );
      strbufPath.append( File.separatorChar );
      strbufPath.append( strAppName );

      file = new File( strbufPath.toString());
      if( file.exists())
      {
        strbufPath.append( File.separatorChar );
        strbufPath.append( strFileName );

        file = new File( strbufPath.toString());
        if( !file.exists())
        {
          Vector vecFiles = new Vector();
          File fileDir = file.getParentFile();
          while( !fileDir.exists())
          {
            vecFiles.add( fileDir );
            fileDir = fileDir.getParentFile();
          }
          for( int i = vecFiles.size()-1; i >= 0; --i)
          {
            fileDir = (File)vecFiles.elementAt( i);
            fileDir.mkdir();
          }
        }
        strRet = strbufPath.toString();
      }
    }
    return strRet;
  }
}
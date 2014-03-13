package haui.io;

import haui.app.filesync.FileSyncTableModel;
import haui.app.filesync.FsDataContainer;
import haui.components.CommPortTypeServerDialog;
import haui.components.ConnectionManager;
import haui.components.FileTypeServerDialog;
import haui.io.FileInterface.BZip2TypeFile;
import haui.io.FileInterface.CgiTypeFile;
import haui.io.FileInterface.CommPortTypeFile;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.FtpTypeFile;
import haui.io.FileInterface.GZipTypeFile;
import haui.io.FileInterface.JarTypeFile;
import haui.io.FileInterface.NormalFile;
import haui.io.FileInterface.SocketTypeFile;
import haui.io.FileInterface.TarTypeFile;
import haui.io.FileInterface.configuration.CgiTypeFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.CommPortTypeFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.CommPortTypeServerFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.configuration.FtpTypeFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.GeneralFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.SocketTypeFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.SocketTypeServerFileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.remote.CommPortConnection;
import haui.io.FileInterface.remote.SocketConnection;
import haui.util.AppProperties;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import javax.comm.CommPortIdentifier;

import cz.dhl.ftp.Ftp;

/**
 *		Module:					FileConnector.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\FileConnector.java,v $
 *<p>
 *		Description:    Cache for file information.<br>
 *</p><p>
 *		Created:				11.06.2002	by	AE
 *</p><p>
 *		@history				11.06.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: FileConnector.java,v $
 *		Revision 1.2  2004-08-31 16:03:15+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.1  2004-06-22 14:08:49+02  t026843
 *		bigger changes
 *
 *		Revision 1.0  2003-06-06 10:05:35+02  t026843
 *		Initial revision
 *
 *		Revision 1.2  2003-06-04 15:35:56+02  t026843
 *		bugfixes
 *
 *		Revision 1.1  2003-05-28 14:19:55+02  t026843
 *		reorganisations
 *
 *		Revision 1.0  2003-05-21 16:25:45+02  t026843
 *		Initial revision
 *
 *		Revision 1.6  2002-09-18 11:16:18+02  t026843
 *		- changes to fit extended filemanager.pl
 *		- logon and logoff moved to 'TypeFile's
 *		- startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal
 *		- LRShell changed to work with filemanager.pl
 *
 *		Revision 1.5  2002-09-03 17:08:00+02  t026843
 *		- CgiTypeFile is now full functional.
 *		- Migrated to the extended filemanager.pl script.
 *
 *		Revision 1.4  2002-08-28 14:22:41+02  t026843
 *		- filmanager.pl upload added.
 *		- first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.
 *
 *		Revision 1.3  2002-08-07 15:25:25+02  t026843
 *		Ftp support via filetype added.
 *		Some bugfixes.
 *
 *		Revision 1.2  2002-06-21 11:00:17+02  t026843
 *		Added gzip and bzip2 file support
 *
 *		Revision 1.1  2002-06-19 16:13:52+02  t026843
 *		Zip file support; writing doesn't work yet!
 *
 *		Revision 1.0  2002-06-17 17:21:19+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: 1.2 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\FileConnector.java,v 1.2 2004-08-31 16:03:15+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class FileConnector
{
  // constants
  public final static String SUPPORTEDFILES[] = { ".zip", ".jar", ".tar", ".gz", ".tgz", ".bz2", ".tbz" };

  // member variables
  public static String TempDirectory = null;
  public static String ExtPath = null;
  public static int SearchDepth = 0;
  private static int CurrentSearchDepth = 0;

  public static boolean isSupported( String strPath)
  {
    boolean blRet = false;
    //int idx = -1;
    for( int i = 0; i < SUPPORTEDFILES.length; ++i)
    {
      if( strPath.toLowerCase().endsWith( SUPPORTEDFILES[i].toLowerCase()))
      {
        blRet = true;
        break;
      }
    }
    return blRet;
  }

  public static FileInterface createFileInterface( FileInterface fiDir, File file)
  {
    return createFileInterface( fiDir.getClass(), file.getAbsolutePath(), null, null, null, fiDir.getFileInterfaceConfiguration());
  }

  /**
   * Creates a new FileConnector instance.
   *
   * @param strCurPath: current path
   */
  public static FileInterface createFileInterface( FileInterface fiDir, String strName, FileInterfaceConfiguration fic)
  {
    if( fiDir == null || strName == null)
      return null;
    StringBuffer strbufPath = new StringBuffer( fiDir.getAbsolutePath());
    if( !fiDir.getAbsolutePath().endsWith( String.valueOf( fiDir.separatorChar())))
      strbufPath.append( fiDir.separatorChar());
    strbufPath.append( strName);
    boolean blExtract = false;
    if( fiDir.getInternalPath() != null && !fiDir.getInternalPath().equals( "") && fiDir.isArchive())
      blExtract = true;
    return constructor( strbufPath.toString(), fiDir.getAbsolutePath(), blExtract, fic);
  }

  /**
   * Creates a new FileInterface instance.
   *
   * @param strCurPath: current path
   */
  public static FileInterface createFileInterface( String strCurPath, String strParentPath, boolean blExtract,
    FileInterfaceConfiguration fic)
  {
    return constructor( strCurPath, strParentPath, blExtract, fic);
  }

  /**
   * Creates a new FileInterface instance.
   *
   * @param strCurPath: current path
   */
  public static FileInterface createTemplateFileInterface( FileInterfaceConfiguration fic)
  {
    return constructor( ".", null, false, fic);
  }

  public static FileInterface createFileInterface(
      Class<? extends FileInterface> fiType, String strCurPath, String strArchPath,
      String strIntPath, String strParentPath, FileInterfaceConfiguration fic)
  {
    int iIdx = 0;
    FileInterface fiRet = null;

    if( NormalFile.class.isAssignableFrom(fiType))
      iIdx = -1;
    else if( JarTypeFile.class.isAssignableFrom(fiType))
    {
      iIdx = getSupportetFileIndex( ".jar" );
      if( iIdx == -1)
        iIdx = getSupportetFileIndex( ".zip" );
    }
    else if( TarTypeFile.class.isAssignableFrom(fiType))
      iIdx = getSupportetFileIndex( ".tar");
    else if( GZipTypeFile.class.isAssignableFrom(fiType))
    {
      iIdx = getSupportetFileIndex( ".gz" );
      if( iIdx == -1)
        iIdx = getSupportetFileIndex( ".tgz" );
    }
    else if( BZip2TypeFile.class.isAssignableFrom(fiType))
    {
      iIdx = getSupportetFileIndex( ".bz2" );
      if( iIdx == -1 )
        iIdx = getSupportetFileIndex( ".tbz" );
    }
    //else if( fi instanceof ZipFile)
      //iIdx = getSupportetFileIndex( ".zip" );;
    else if( FtpTypeFile.class.isAssignableFrom(fiType))
      iIdx = 50;
    else if( CgiTypeFile.class.isAssignableFrom(fiType))
      iIdx = 51;
    else if( SocketTypeFile.class.isAssignableFrom(fiType))
      iIdx = 52;
    else if( CommPortTypeFile.class.isAssignableFrom(fiType))
      iIdx = 53;

    try
    {
      fiRet = getFile( iIdx, strCurPath, strArchPath, strIntPath, strParentPath, fic);
    }
    catch( IOException ex )
    {
      ex.printStackTrace( GlobalApplicationContext.instance().getErrorPrintStream());
    }
    return fiRet;
  }
  
  public static FileInterfaceConfiguration createFileInterfaceConfiguration( String host, int port, FileTypeServerDialog ftsd, Object connObj, int mode, int baud, String appName, AppProperties props, boolean cached)
  {
    if( ftsd != null)
    {
      if( ftsd instanceof CommPortTypeServerDialog)
        return FileConnector.createFileInterfaceConfiguration( (CommPortIdentifier)connObj, ftsd, mode, baud, appName, props, cached);
      else
        return FileConnector.createFileInterfaceConfiguration( port, ftsd, appName, props, cached);
    }
    if( connObj instanceof CommPortConnection)
      return FileConnector.createFileInterfaceConfiguration( host, (CommPortConnection)connObj, appName, props, cached);
    else if( connObj instanceof Ftp)
      return FileConnector.createFileInterfaceConfiguration( (Ftp)connObj, appName, props, cached);
    else if( connObj instanceof SocketConnection)
      return FileConnector.createFileInterfaceConfiguration( (SocketConnection)connObj, appName, props, cached);
    else if( connObj instanceof ConnectionManager)
      return FileConnector.createFileInterfaceConfiguration( host, (ConnectionManager)connObj, appName, props, cached);
    else if( connObj instanceof CommPortIdentifier)
      return FileConnector.createFileInterfaceConfiguration( (CommPortIdentifier)connObj, ftsd, mode, baud, appName, props, cached);
    else
    {
      if( port > 0)
        return FileConnector.createFileInterfaceConfiguration( port, ftsd, appName, props, cached);
      else
        return FileConnector.createFileInterfaceConfiguration( appName, props, cached);
    }
  }
  
  private static FileInterfaceConfiguration createFileInterfaceConfiguration( int port, FileTypeServerDialog ftsd, String appName, AppProperties props, boolean cached)
  {
    return new SocketTypeServerFileInterfaceConfiguration( port, ftsd, appName, props, cached);
  }
  
  private static FileInterfaceConfiguration createFileInterfaceConfiguration( CommPortIdentifier id, FileTypeServerDialog ftsd, int mode, int baud, String appName, AppProperties props, boolean cached)
  {
    return new CommPortTypeServerFileInterfaceConfiguration( id, ftsd, mode, baud, appName, props, cached);
  }
  
  private static FileInterfaceConfiguration createFileInterfaceConfiguration( String host, CommPortConnection cc, String appName, AppProperties props, boolean cached)
  {
    return new CommPortTypeFileInterfaceConfiguration( host, cc, appName, props, cached);
  }
  
  private static FileInterfaceConfiguration createFileInterfaceConfiguration( Ftp obj, String appName, AppProperties props, boolean cached)
  {
    return new FtpTypeFileInterfaceConfiguration( obj, appName, props, cached);
  }
  
  private static FileInterfaceConfiguration createFileInterfaceConfiguration( SocketConnection sc, String appName, AppProperties props, boolean cached)
  {
    return new SocketTypeFileInterfaceConfiguration( sc, appName, props, cached);
  }
  
  private static FileInterfaceConfiguration createFileInterfaceConfiguration( String host, ConnectionManager cm, String appName, AppProperties props, boolean cached)
  {
    return new CgiTypeFileInterfaceConfiguration( host, cm, appName, props, cached);
  }
  
  private static FileInterfaceConfiguration createFileInterfaceConfiguration( String appName, AppProperties props, boolean cached)
  {
    return new GeneralFileInterfaceConfiguration( appName, props, cached);
  }
  
  /**
   * Creates a new FileIntervace instance.
   *
   * @param strCurPath: current path
   */
  private static FileInterface constructor( String strCurPath, String strParentPath, boolean blExtract,
      FileInterfaceConfiguration fic)
  {
    int iIdx = -1;
    //int iOldIdx = iIdx;
    int iFirstIdx = -1;
    int iFirstFile = -1;
    int iSecondIdx = -1;
    //int iSecondFile = -1;
    int i = -1;
    String strArchPath = "";
    String strIntPath = "";
    FileInterface file = null;

    if( strCurPath == null || strCurPath.equals( ""))
      return null;

    if( fic.isLocal())
    {
      String strCurPathLow = strCurPath.toLowerCase();
      StringTokenizer st = new StringTokenizer( strCurPathLow, ".", false);
      if( st.hasMoreTokens())
      {
        String str = st.nextToken();
        if( iIdx == -1 )
          iIdx = str.length();

        boolean blFound = false;
        boolean blInArchive = false;
        while( st.hasMoreTokens() )
        {
          blFound = false;
          str = "." + st.nextToken();
          for( i = 0; i < SUPPORTEDFILES.length; ++i )
          {
            String strFileExt = SUPPORTEDFILES[i].toLowerCase();
            if( str.startsWith( strFileExt ) )
            {
              blInArchive = false;
              if( str.length() > strFileExt.length() &&
                  str.charAt( strFileExt.length()) != '.' && str.charAt( strFileExt.length()) != '\\'
                   && str.charAt( strFileExt.length()) != '/')
              {
                iFirstIdx = -1;
                iFirstFile = -1;
                iSecondIdx = -1;
                //iSecondFile = -1;
                strIntPath = "";
                //iIdx += str.length();
                continue;
              }
              else if( str.length() > strFileExt.length() &&
                       (str.charAt( strFileExt.length()) == '\\' || str.charAt( strFileExt.length()) == '/'))
                blInArchive = true;
              if( iFirstIdx != -1 && iSecondIdx == -1 )
              {
                DataContainer dc = new DataContainer();
                dc.i = i;
                dc.iIdx = iIdx;
                dc.str = str;
                dc.strExt = strFileExt;
                dc.st = st;
                if( !isLastExtention( dc, strCurPathLow))
                {
                  i = dc.i;
                  iIdx = dc.iIdx;
                  str = dc.str;
                  st = dc.st;
                }
                //iSecondFile = i;
                iSecondIdx = iIdx;
              }
              else if( iFirstIdx == -1 )
              {
                DataContainer dc = new DataContainer();
                dc.i = i;
                dc.iIdx = iIdx;
                dc.str = str;
                dc.strExt = strFileExt;
                dc.st = st;
                if( !isLastExtention( dc, strCurPathLow))
                {
                  i = dc.i;
                  iIdx = dc.iIdx;
                  str = dc.str;
                  st = dc.st;
                }
                iFirstFile = i;
                iFirstIdx = iIdx;
              }
              blFound = true;
              break;
            }
          }
          iIdx += str.length();
        }
        if( !blFound && !blInArchive)
        {
          iFirstIdx = -1;
          iFirstFile = -1;
          iSecondIdx = -1;
          //iSecondFile = -1;
          strIntPath = "";
          iIdx = -1;
        }
      }
      /*
      iOldIdx = strCurPathLow.length();
      for( i = 0; i < Array.getLength( SUPPORTEDFILES); ++i)
      {
        boolean blFound = false;
        String strFileExt = SUPPORTEDFILES[i].toLowerCase();
        while( ( iIdx = strCurPathLow.lastIndexOf( strFileExt, iOldIdx)) != -1)
        {
          strCurPathLow = strCurPathLow.substring( 0, iIdx);
          iOldIdx = iIdx;
          if( iFirstIdx != -1 && iFirstIdx < iIdx)
          {
            iSecondFile = i;
            iSecondIdx = iIdx;
            continue;
          }
          else if( iFirstIdx != -1)
          {
            iSecondFile = iFirstFile;
            iSecondIdx = iFirstIdx;
            iFirstFile = i;
            iFirstIdx = iIdx;
          }
          else
          {
            iFirstFile = i;
            iFirstIdx = iIdx;
          }
          blFound = true;
        }
        if( blFound)
          i = 0;
      }
      */
      if( blExtract && iSecondIdx != -1 && iFirstIdx != -1)
      {
        strArchPath = strCurPath.substring( 0, iFirstIdx+SUPPORTEDFILES[iFirstFile].length());
        if( strCurPath.length() > iFirstIdx+SUPPORTEDFILES[iFirstFile].length())
          strIntPath = strCurPath.substring( iFirstIdx+SUPPORTEDFILES[iFirstFile].length()+1, strCurPath.length());
        strCurPath = FileConnector.extractToTempDir( strArchPath, strIntPath, strParentPath, fic);
        if( strCurPath != null)
        {
          iFirstIdx = -1;
          iFirstFile = -1;
          iSecondIdx = -1;
          //iSecondFile = -1;
          strIntPath = "";
          iIdx = -1;

          strCurPathLow = strCurPath.toLowerCase();
          st = new StringTokenizer( strCurPathLow, ".", false);
          if( st.hasMoreTokens())
          {
            String str = st.nextToken();
            if( iIdx == -1 )
              iIdx = str.length();

            boolean blFound = false;
            boolean blInArchive = false;
            while( st.hasMoreTokens() )
            {
              blFound = false;
              str = "." + st.nextToken();
              for( i = 0; i < SUPPORTEDFILES.length; ++i )
              {
                String strFileExt = SUPPORTEDFILES[i].toLowerCase();
                if( str.startsWith( strFileExt ) )
                {
                  blInArchive = false;
                  if( str.length() > strFileExt.length() &&
                      str.charAt( strFileExt.length()) != '.' && str.charAt( strFileExt.length()) != '\\'
                       && str.charAt( strFileExt.length()) != '/')
                  {
                    iFirstIdx = -1;
                    iFirstFile = -1;
                    iSecondIdx = -1;
                    //iSecondFile = -1;
                    strIntPath = "";
                    //iIdx = -1;
                    continue;
                  }
                  else if( str.length() > strFileExt.length() &&
                           (str.charAt( strFileExt.length()) == '\\' || str.charAt( strFileExt.length()) == '/'))
                    blInArchive = true;
                  if( iFirstIdx != -1 && iSecondIdx == -1 )
                  {
                    DataContainer dc = new DataContainer();
                    dc.i = i;
                    dc.iIdx = iIdx;
                    dc.str = str;
                    dc.strExt = strFileExt;
                    dc.st = st;
                    if( !isLastExtention( dc, strCurPathLow))
                    {
                      i = dc.i;
                      iIdx = dc.iIdx;
                      str = dc.str;
                      st = dc.st;
                    }
                    //iSecondFile = i;
                    iSecondIdx = iIdx;
                  }
                  else if( iFirstIdx == -1 )
                  {
                    DataContainer dc = new DataContainer();
                    dc.i = i;
                    dc.iIdx = iIdx;
                    dc.str = str;
                    dc.strExt = strFileExt;
                    dc.st = st;
                    if( !isLastExtention( dc, strCurPathLow))
                    {
                      i = dc.i;
                      iIdx = dc.iIdx;
                      str = dc.str;
                      st = dc.st;
                    }
                    iFirstFile = i;
                    iFirstIdx = iIdx;
                  }
                  blFound = true;
                  break;
                }
              }
              iIdx += str.length();
            }
            if( !blFound && !blInArchive)
            {
              iFirstIdx = -1;
              iFirstFile = -1;
              iSecondIdx = -1;
              //iSecondFile = -1;
              strIntPath = "";
              iIdx = -1;
            }
          }
          /*
          strCurPathLow = strCurPath.toLowerCase();
          iOldIdx = strCurPathLow.length();
          for( i = 0; i < Array.getLength( SUPPORTEDFILES); ++i)
          {
            boolean blFound = false;
            String strFileExt = SUPPORTEDFILES[i].toLowerCase();
            while( ( iIdx = strCurPathLow.lastIndexOf( strFileExt, iOldIdx)) != -1)
            {
              strCurPathLow = strCurPathLow.substring( 0, iIdx);
              iOldIdx = iIdx;
              if( iFirstIdx != -1 && iFirstIdx < iIdx)
              {
                iSecondFile = i;
                iSecondIdx = iIdx;
                continue;
              }
              else if( iFirstIdx != -1)
              {
                iSecondFile = iFirstFile;
                iSecondIdx = iFirstIdx;
                iFirstFile = i;
                iFirstIdx = iIdx;
              }
              else
              {
                iFirstFile = i;
                iFirstIdx = iIdx;
              }
              blFound = true;
            }
            if( blFound)
              i = 0;
          }
          */
        }
      }
      if( iFirstIdx != -1)
      {
        strArchPath = strCurPath.substring( 0, iFirstIdx+SUPPORTEDFILES[iFirstFile].length());
        if( strCurPath.length() > iFirstIdx+SUPPORTEDFILES[iFirstFile].length())
          strIntPath = strCurPath.substring( iFirstIdx+SUPPORTEDFILES[iFirstFile].length()+1, strCurPath.length());
      }
      //if( !blExtract && strIntPath.equals( ""))
        //iFirstFile = -1;
    }
    else
    {
      if( fic instanceof FtpTypeFileInterfaceConfiguration)
        iFirstFile = 50;
      if( fic instanceof CgiTypeFileInterfaceConfiguration)
        iFirstFile = 51;
      if( fic instanceof SocketTypeFileInterfaceConfiguration)
        iFirstFile = 52;
      if( fic instanceof CommPortTypeFileInterfaceConfiguration)
        iFirstFile = 53;
    }

    try
    {
      file = getFile( iFirstFile, strCurPath, strArchPath, strIntPath, strParentPath, fic);
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    return file;
  }

  public static int getSupportetFileIndex( String str)
  {
    int iRet = -1;
    str = str.toLowerCase();
    for( int i = 0; i < SUPPORTEDFILES.length; ++i )
    {
      String strFileExt = SUPPORTEDFILES[i].toLowerCase();
      if( str.equals( strFileExt ) )
      {
        iRet = i;
        break;
      }
    }
    return iRet;
  }

  protected static boolean isLastExtention( DataContainer dc, String strCurPathLow)
  {
    boolean blRet = true;
    String strTmp = null;
    boolean blFound = false;
    do
    {
      blFound = false;
      int iIdxFin = dc.iIdx + dc.strExt.length();
      if( iIdxFin < strCurPathLow.length() )
      {
        strTmp = strCurPathLow.substring( iIdxFin);
        for( int ii = 0; ii < SUPPORTEDFILES.length; ++ii )
        {
          String strFileExt2 = SUPPORTEDFILES[ii].toLowerCase();
          if( strTmp.startsWith( strFileExt2 ) )
          {
            blFound = true;
            blRet = false;
            dc.iIdx = iIdxFin;
            if( dc.st.hasMoreTokens())
            {
              //++dc.i;
              dc.i = ii;
              dc.str = "." + dc.st.nextToken();
            }
            break;
          }
        }
      }
    }while( blFound);
    return blRet;
  }

  /**
   * Creates a new FileInterface instance.
   *
   * @param fi: FileInterface
   */
  public static FileInterface createFileInterface( FileInterface fi)
  {
    FileInterface fiNew = null;
    if( fi instanceof NormalFile)
      fiNew = (NormalFile)fi;
    else if( fi instanceof JarTypeFile)
      fiNew = (JarTypeFile)fi;
    else if( fi instanceof TarTypeFile)
      fiNew = (TarTypeFile)fi;
    else if( fi instanceof GZipTypeFile)
      fiNew = (GZipTypeFile)fi;
    else if( fi instanceof BZip2TypeFile)
      fiNew = (BZip2TypeFile)fi;
    //else if( fi instanceof ZipFile)
      //fiNew = (ZipTypeFile)fi;
    else if( fi instanceof FtpTypeFile)
      fiNew = (FtpTypeFile)fi;
    else if( fi instanceof CgiTypeFile)
      fiNew = (CgiTypeFile)fi;
    else if( fi instanceof SocketTypeFile)
      fiNew = (SocketTypeFile)fi;
    else if( fi instanceof CommPortTypeFile)
      fiNew = (CommPortTypeFile)fi;
    return fiNew;
  }

  public static FileInterface[] allocateFileInterfaceArray( FileInterface fi, int iSize)
  {
    FileInterface[] fis = null;

    if( fi instanceof NormalFile)
      fis = new NormalFile[iSize];
    else if( fi instanceof JarTypeFile)
      fis = new JarTypeFile[iSize];
    else if( fi instanceof TarTypeFile)
      fis = new TarTypeFile[iSize];
    else if( fi instanceof GZipTypeFile)
      fis = new GZipTypeFile[iSize];
    else if( fi instanceof BZip2TypeFile)
      fis = new BZip2TypeFile[iSize];
    //else if( fi instanceof ZipFile)
      //fis = new ZipFile[iSize];
    else if( fi instanceof FtpTypeFile)
      fis = new FtpTypeFile[iSize];
    else if( fi instanceof CgiTypeFile)
      fis = new CgiTypeFile[iSize];
    else if( fi instanceof SocketTypeFile)
      fis = new SocketTypeFile[iSize];
    else if( fi instanceof CommPortTypeFile)
      fis = new CommPortTypeFile[iSize];

    return fis;
  }

  static private FileInterface getFile( int idx, String strCurPath, String strArchPath, String strIntPath,
                                        String strParentPath, FileInterfaceConfiguration fic)
    throws IOException
  {
    FileInterface file = null;
    switch( idx)
    {
      case 0:
        file = new JarTypeFile( strArchPath, strIntPath, strParentPath, fic);
        //file = new ZipTypeFile( strArchPath, strIntPath, strParentPath);
        break;

      case 1:
        file = new JarTypeFile( strArchPath, strIntPath, strParentPath, fic);
        break;

      case 2:
        file = new TarTypeFile( strArchPath, strIntPath, strParentPath, fic);
        break;

      case 3:
      case 4:
        file = new GZipTypeFile( strArchPath, strIntPath, strParentPath, fic);
        break;

      case 5:
      case 6:
        file = new BZip2TypeFile( strArchPath, strIntPath, strParentPath, fic);
        break;

      case 50:
        file = new FtpTypeFile( strCurPath, strParentPath, null, fic);
        break;

      case 51:
        file = new CgiTypeFile( strCurPath, strParentPath, fic);
        break;

      case 52:
        file = new SocketTypeFile( strCurPath, strParentPath, fic);
        break;

      case 53:
        file = new CommPortTypeFile( strCurPath, strParentPath, fic);
        break;

      default:
        file = new NormalFile( strCurPath, strParentPath, fic);
        break;
    }
    return file;
  }
/*
  public FileConnector[] listRoots()
  {
    FileInterface fi[] = m_file._listRoots();
    if( fi == null)
      return null;

    FileConnector[] fc = new FileConnector[ Array.getLength( fi)];
    for( int i = 0; i < Array.getLength( fi); ++i)
    {
      fc[i] = new FileConnector( fi[i]);
    }
    return fc;
  }

  public FileInterface getFileInterface()
  {
    return m_file;
  }

  public char separatorChar()
  {
    return m_file.separatorChar();
  }

  public String getTempDirectory()
  {
    return m_file.getTempDirectory();
  }

  public BufferedInputStream getBufferedInputStream()
    throws FileNotFoundException, IOException
  {
    return m_file.getBufferedInputStream();
  }

  public BufferedOutputStream getBufferedOutputStream( String strNewPath)
    throws FileNotFoundException
  {
    return m_file.getBufferedOutputStream( strNewPath);
  }

  public Object getConnObj()
  {
    return m_file.getConnObj();
  }

  public boolean canRead()
  {
    return m_file.canRead();
  }

  public boolean canWrite()
  {
    return m_file.canWrite();
  }

  public boolean isDirectory()
  {
    return m_file.isDirectory();
  }

  public boolean isArchive()
  {
    return m_file.isArchive();
  }

  public boolean isFile()
  {
    return m_file.isFile();
  }

  public boolean isHidden()
  {
    return m_file.isHidden();
  }

  public long length()
  {
    return m_file.length();
  }

  public String getId()
  {
    return m_file.getId();
  }

  public String getHost()
  {
    return m_file.getHost();
  }

  public String getName()
  {
    return m_file.getName();
  }

  public String getAbsolutePath()
  {
    return m_file.getAbsolutePath();
  }

  public String getPath()
  {
    return m_file.getPath();
  }

  public String getParent()
  {
    return m_file.getParent();
  }

  public long lastModified()
  {
    return m_file.lastModified();
  }

  public String[] list()
  {
    return m_file.list();
  }

  public FileConnector[] listFiles( FileInterfaceFilter filter)
  {
    FileInterface fi[] = m_file._listFiles( filter);
    if( fi == null)
      return null;

    FileConnector[] fc = new FileConnector[ Array.getLength( fi)];
    for( int i = 0; i < Array.getLength( fi); ++i)
    {
      fc[i] = new FileConnector( fi[i]);
    }
    return fc;
  }

  public FileConnector[] listFiles()
  {
    FileInterface fi[] = m_file._listFiles();
    if( fi == null)
      return null;

    FileConnector[] fc = new FileConnector[ Array.getLength( fi)];
    for( int i = 0; i < Array.getLength( fi); ++i)
    {
      fc[i] = new FileConnector( fi[i]);
    }
    return fc;
  }

  public boolean renameTo( FileConnector file)
  {
    return m_file.renameTo( file.getFileInterface());
  }

  public boolean delete()
  {
    return m_file.delete();
  }

  public boolean mkdir()
  {
    return m_file.mkdir();
  }
*/
  static public String findCommandPathInExtendedPath( FileInterface fi, String strCmd, FileInterfaceConfiguration fic)
  {
    CurrentSearchDepth = 0;
    String strRet = null;
    int iIdx = strCmd.lastIndexOf( fi.separatorChar());
    if( iIdx != -1)
    {
      strCmd = strCmd.substring( iIdx + 1 );
    }
    if( FileConnector.ExtPath != null)
    {
      StringTokenizer st = new StringTokenizer( FileConnector.ExtPath, ";", false );

      while( st.hasMoreTokens() && strRet == null)
      {
        String str = st.nextToken().trim();
        strRet = findPath( str, strCmd, true, fic);
      }
    }
    return strRet;
  }

  static public String findPath( String strStartDir, String strFileName, boolean blRecursive, FileInterfaceConfiguration fic)
  {
    String strPath = null;
    FileInterface file = FileConnector.createFileInterface( strStartDir, null, true, fic);
    if( file.exists() && (file.isDirectory() || file.isArchive()))
    {
      strPath = file.getAbsolutePath();
      String strFile = file.getAbsolutePath() + file.separatorChar() + strFileName;
      if( !FileConnector.exists( strFile, fic))
      {
        strPath = null;
        if( blRecursive)
        {
          FileInterface[] fiArray = file._listFiles();
          for( int i = 0; i < fiArray.length && strPath == null; ++i )
          {
            FileInterface fiCur = fiArray[i];
            strPath = findPath( fiCur, strFileName, blRecursive, fic);
          }
        }
      }
    }
    return strPath;
  }

  static protected String findPath( FileInterface fiCurDir, String strFileName, boolean blRecursive, FileInterfaceConfiguration fic)
  {
    ++CurrentSearchDepth;
    String strPath = null;
    try
    {
      if( CurrentSearchDepth <= SearchDepth )
      {
        if( fiCurDir.exists() && fiCurDir.isDirectory() )
        {
          strPath = fiCurDir.getAbsolutePath();
          String strFile = fiCurDir.getAbsolutePath() + fiCurDir.separatorChar() + strFileName;
          if( !FileConnector.exists( strFile, fic) )
          {
            strPath = null;
            if( blRecursive )
            {
              FileInterface[] fiArray = fiCurDir._listFiles();
              for( int i = 0; i < fiArray.length; ++i )
              {
                FileInterface fiCur = fiArray[i];
                strPath = findPath( fiCur, strFileName, blRecursive, fic);
                if( strPath != null )
                  break;
              }
            }
          }
        }
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      --CurrentSearchDepth;
    }
    return strPath;
  }

  static public String findCommandInExtendedPath( FileInterface fi, String strCmd, FileInterfaceConfiguration fic)
  {
    CurrentSearchDepth = 0;
    String strRet = null;
    int iIdx = strCmd.lastIndexOf( fi.separatorChar());
    if( iIdx != -1)
    {
      strCmd = strCmd.substring( iIdx + 1 );
    }
    if( FileConnector.ExtPath != null)
    {
      StringTokenizer st = new StringTokenizer( FileConnector.ExtPath, ";", false );

      while( st.hasMoreTokens() && strRet == null)
      {
        String str = st.nextToken().trim();
        strRet = findFile( str, strCmd, true, fic);
      }
    }
    return strRet;
  }

  static public String findFile( String strStartDir, String strFileName, boolean blRecursive, FileInterfaceConfiguration fic)
  {
    String strPath = null;
    FileInterface file = FileConnector.createFileInterface( strStartDir, null, true, fic);
    if( file.exists() && (file.isDirectory() || file.isArchive()))
    {
      strPath = file.getAbsolutePath() + file.separatorChar() + strFileName;
      if( !FileConnector.exists( strPath, fic))
      {
        strPath = null;
        if( blRecursive)
        {
          FileInterface[] fiArray = file._listFiles();
          for( int i = 0; i < fiArray.length && strPath == null; ++i )
          {
            FileInterface fiCur = fiArray[i];
            strPath = findFile( fiCur, strFileName, blRecursive, fic);
          }
        }
      }
    }
    return strPath;
  }

  static protected String findFile( FileInterface fiCurDir, String strFileName, boolean blRecursive, FileInterfaceConfiguration fic)
  {
    ++CurrentSearchDepth;
    String strPath = null;
    try
    {
      if( CurrentSearchDepth <= SearchDepth )
      {
        if( fiCurDir.exists() && ( fiCurDir.isDirectory() || fiCurDir.isArchive() ) )
        {
          strPath = fiCurDir.getAbsolutePath() + fiCurDir.separatorChar() + strFileName;
          if( !FileConnector.exists( strPath, fic) )
          {
            strPath = null;
            if( blRecursive )
            {
              FileInterface[] fiArray = fiCurDir._listFiles();
              for( int i = 0; i < fiArray.length; ++i )
              {
                FileInterface fiCur = fiArray[i];
                strPath = findFile( fiCur, strFileName, blRecursive, fic);
                if( strPath != null )
                  break;
              }
            }
          }
        }
      }
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      --CurrentSearchDepth;
    }
    return strPath;
  }

  static public boolean exists( String strPath, FileInterfaceConfiguration fic)
  {
    boolean blRet = false;
    FileInterface file = FileConnector.createFileInterface( strPath, null, false, fic);

/*
    int iIdx = -1;
    int iFirstIdx = -1;
    int iFirstFile = -1;
    int iSecondIdx = -1;
    int iSecondFile = -1;
    int i = -1;
    String strArchPath = "";
    String strIntPath = "";

    String strCurPathLow = strPath.toLowerCase();
    StringTokenizer st = new StringTokenizer( strCurPathLow, ".", false);
    if( st.hasMoreTokens())
    {
      String str = st.nextToken();
      if( iIdx == -1 )
        iIdx = str.length();

      while( st.hasMoreTokens() )
      {
        str = st.nextToken();
        for( i = 0; i < Array.getLength( SUPPORTEDFILES ); ++i )
        {
          String strFileExt = SUPPORTEDFILES[i].toLowerCase();
          if( str.startsWith( strFileExt ) )
          {
            if( iFirstIdx != -1 && iSecondIdx == -1 )
            {
              iSecondFile = i;
              iSecondIdx = iIdx;
            }
            else if( iFirstIdx == -1 )
            {
              iFirstFile = i;
              iFirstIdx = iIdx;
            }
            break;
          }
        }
        iIdx += str.length() + 1;
      }
    }
    if( iFirstIdx != -1)
    {
      strArchPath = strPath.substring( 0, iFirstIdx+SUPPORTEDFILES[iFirstFile].length());
      if( strPath.length() > iFirstIdx+SUPPORTEDFILES[iFirstFile].length())
        strIntPath = strPath.substring( iFirstIdx+SUPPORTEDFILES[iFirstFile].length()+1, strPath.length());
    }

    try
    {
      file = getFile( iFirstFile, strPath, strArchPath, strIntPath, null, connObj, appProps);
      if( file != null)
        blRet = file.exists();
    }
    catch( IOException ioex)
    {
      blRet = false;
    }
*/
    if( file != null)
      blRet = file.exists();
    return blRet;
  }

  static public boolean deleteRecusively( FileInterface fi)
  {
    boolean blRet = true;

    if( fi.isDirectory())
    {
      FileInterface[] fis = fi._listFiles();

      for( int i = 0; i < fis.length && blRet; ++i)
      {
        blRet = deleteRecusively( fis[i]);
      }
      if( blRet)
        blRet = fi.delete();
    }
    else
    {
      blRet = fi.delete();
    }
    return blRet;
  }

  /**
   * length and count of all files in current path
   *
   * @return long[0] = length of all files and long[1] = count of all files (without dirs)
   */
  static public long[] lengthAndCountOfFilesOfDir( FileInterface fiDir, FileInterfaceFilter fif, boolean blIncludeSubdirs)
  {
    long[] lRet = new long[2];
    lRet[0] = 0;
    lRet[1] = 0;
    FileInterface[] fis = fiDir._listFiles( fif);

    if( fis != null && fis.length > 0)
    {
      long lLength = 0;
      long lCount = 0;

      for( int i = 0; i < fis.length; i++)
      {
        if( fis[i].isDirectory())
        {
          if( blIncludeSubdirs)
          {
            long[] lTmp = new long[2];
            lTmp = lengthAndCountOfFilesOfDir( fis[i], fif, blIncludeSubdirs );
            lLength += lTmp[0];
            lCount += lTmp[1];
          }
        }
        else
        {
          lLength += fis[i].length();
          ++lCount;
        }
      }
      lRet[0] = lLength;
      lRet[1] = lCount;
    }
    return lRet;
  }

  /**
   * length and count of all files in array
   *
   * @return long[0] = length of all files and long[1] = count of all files (including dirs)
   */
  static public long[] lengthAndCountOfFiles( FsDataContainer[] dcs)
  {
    long[] lRet = new long[2];
    lRet[0] = 0;
    lRet[1] = 0;

    if( dcs != null && dcs.length > 0)
    {
      long lLength = 0;
      long lCount = 0;
      for( int i = 0; i < dcs.length; i++)
      {
        FsDataContainer dc = dcs[i];

        if( dc.strCompare.equals( FileSyncTableModel.GE ) ||
            dc.strCompare.equals( FileSyncTableModel.LE ))
          ++lCount;
      }
      FileInterface[] fis = new FileInterface[(int)lCount];

      int iFi = 0;
      for( int i = 0; i < dcs.length; i++)
      {
        FsDataContainer dc = dcs[i];

        if( dc.strCompare.equals( FileSyncTableModel.GE ))
          fis[iFi++] = dc.fiLeft;
        else if( dc.strCompare.equals( FileSyncTableModel.LE ))
          fis[iFi++] = dc.fiRight;
      }
      for( int i = 0; i < fis.length; i++)
      {
        lLength += fis[i].length();
        ++lCount;
      }
      lRet[0] = lLength;
      lRet[1] = lCount;
    }
    return lRet;
  }

  /**
   * length and count of all files in array
   *
   * @return long[0] = length of all files and long[1] = count of all files (without dirs)
   */
  static public long[] lengthAndCountOfFiles(FileInterface[] fis, FileInterfaceFilter fif, boolean blIncludeSubdirs)
  {
    long[] lRet = new long[2];
    lRet[0] = 0;
    lRet[1] = 0;

    if( fis != null && fis.length > 0)
    {
      long lLength = 0;
      long lCount = 0;

      for( int i = 0; i < fis.length; i++)
      {
        if( fis[i].isDirectory())
        {
          if( blIncludeSubdirs)
          {
            long[] lTmp = new long[2];
            lTmp = lengthAndCountOfFilesOfDir( fis[i], fif, blIncludeSubdirs );
            lLength += lTmp[0];
            lCount += lTmp[1];
          }
        }
        else
        {
          lLength += fis[i].length();
          ++lCount;
        }
      }
      lRet[0] = lLength;
      lRet[1] = lCount;
    }
    return lRet;
  }

  static public FileInterface changeDirectory( FileInterface fiCurrent, String strPath, FileInterfaceConfiguration fic)
  {
    String strFullPath = null;
    if( strPath.startsWith( "\""))
      strPath = strPath.substring( 1);
    if( strPath.endsWith( "\""))
      strPath = strPath.substring( 0, strPath.length()-1);
    if( strPath.equals( "."))
      return fiCurrent;
    else if( strPath.equals( ".."))
      strFullPath = fiCurrent.getParent();
    else if( strPath.equals( String.valueOf( fiCurrent.separatorChar())))
    {
      FileInterface fiTmp = fiCurrent.getRootFileInterface();
      if( fiTmp == null)
        strFullPath = String.valueOf( fiCurrent.separatorChar());
      else
        strFullPath = fiTmp.getAbsolutePath();
    }
    else
    {
      if( fiCurrent.isAbsolutePath( strPath))
        strFullPath = strPath;
      else
        strFullPath = fiCurrent.getAbsolutePath() + fiCurrent.separatorChar() + strPath;
    }

    if( strFullPath == null)
      return fiCurrent;

    /*
    if( !FileConnector.exists( strFullPath, fiCurrent.getConnObj(), appProps))
    {
      strFullPath = strPath;
      if( !FileConnector.exists( strFullPath, fiCurrent.getConnObj(), appProps))
      {
        return fiCurrent;
      }
    }
    */

    FileInterface file = null;
    try
    {
      file = FileConnector.createFileInterface( strFullPath, null, false, fic);
    }
    catch( Exception ex )
    {
      file = fiCurrent;
      ex.printStackTrace();
    }
/*
    boolean blRet = false;
    FileInterface file = fiCurrent;
    int iIdx = -1;
    int iFirstIdx = -1;
    int iFirstFile = -1;
    int iSecondIdx = -1;
    int iSecondFile = -1;
    int i = -1;
    String strArchPath = "";
    String strIntPath = "";

    String strCurPathLow = strFullPath.toLowerCase();
    StringTokenizer st = new StringTokenizer( strCurPathLow, ".", false);
    if( st.hasMoreTokens())
    {
      String str = st.nextToken();
      if( iIdx == -1 )
        iIdx = str.length();

      while( st.hasMoreTokens() )
      {
        str = "." + st.nextToken();
        for( i = 0; i < Array.getLength( SUPPORTEDFILES ); ++i )
        {
          String strFileExt = SUPPORTEDFILES[i].toLowerCase();
          if( str.startsWith( strFileExt ) )
          {
            if( iFirstIdx != -1 && iSecondIdx == -1 )
            {
              iSecondFile = i;
              iSecondIdx = iIdx;
            }
            else if( iFirstIdx == -1 )
            {
              iFirstFile = i;
              iFirstIdx = iIdx;
            }
            break;
          }
        }
        iIdx += str.length() + 1;
      }
    }
    if( iFirstIdx != -1)
    {
      strArchPath = strFullPath.substring( 0, iFirstIdx+SUPPORTEDFILES[iFirstFile].length());
      if( strPath.length() > iFirstIdx+SUPPORTEDFILES[iFirstFile].length())
        strIntPath = strFullPath.substring( iFirstIdx+SUPPORTEDFILES[iFirstFile].length()+1, strFullPath.length());
    }

    try
    {
      file = getFile( iFirstFile, strFullPath, strArchPath, strIntPath, null, connObj, appProps);
    }
    catch( IOException ioex)
    {
      blRet = false;
    }
 */
    return file;
  }

/*
  public void logon( AppProperties appProps)
  {
    m_file.logon( appProps);
    return;
  }

  public void logoff()
  {
    m_file.logoff();
    return;
  }

  public void startTerminal( AppProperties appProps)
  {
    m_file.startTerminal( appProps);
  }
*/
  /**
   * ececute command
   *
   * @param strTargetPath traget path
   * @param cmd CommandClass
   */
  static public int exec( FileInterface fi, String strCmd, String strTargetPath, CommandClass cmd,
                          OutputStream osOut, OutputStream osErr, InputStream is )
    throws IOException
  {
    if( cmd == null)
      strCmd = strTargetPath;
    StringTokenizer st = new StringTokenizer( strCmd, " \t\n", false);
    int iCount = st.countTokens();
    String[] strArrCmd = new String[iCount];
    int i = 0;

    while( st.hasMoreTokens())
    {
      strArrCmd[i] = st.nextToken();
      ++i;
    }
    return exec( fi, strArrCmd, strTargetPath, cmd, osOut, osErr, is);
  }

  /**
   * ececute command
   *
   * @param strTargetPath traget path
   * @param cmd CommandClass
   */
  static public int exec( FileInterface fi, String[] strCmd, String strTargetPath, CommandClass cmd,
                           OutputStream osOut, OutputStream osErr, InputStream is)
    throws IOException
  {
    int i_status = -1;
    File fiStartDir = null;
    if( cmd != null)
    {
      String strStartDir = cmd.getCompleteStartDir( fi, strTargetPath);
      if( strStartDir != null && !strStartDir.equals( ""))
      {
        fiStartDir = new File( strStartDir );
        if( !fiStartDir.isDirectory())
          fiStartDir = fiStartDir.getParentFile();
      }
    }
    if( strCmd == null)
      return i_status;

    Process proc = null;
    if( strCmd != null && !strCmd.equals( "") && !strCmd.equals( " "))
    {
      try
      {
        proc = Runtime.getRuntime().exec( strCmd, null, fiStartDir );
        fi.setCurrentProcess( proc);
      }
      catch( IOException ioex)
      {
        if( osErr != null)
        {
          String strErr = ioex.toString();
          try
          {
            ioex.printStackTrace( new PrintStream( osErr ) );
          }
          catch (Exception ex)
          {
            System.err.println( strErr);
          }
        }
        else
          ioex.printStackTrace();
        return i_status;
      }
    }
    if( proc != null)
    {
      StreamConnectThread threadStdin = null;

      // Redirect input only when synchronism is carried out.
      if( is != null )
      {
        threadStdin = new StreamConnectThread( is, proc.getOutputStream() );
        threadStdin.start();
      }

      StreamConnectThread threadStdout = null;
      StreamConnectThread threadStderr = null;
      if( osOut != null)
        threadStdout = new StreamConnectThread( proc.getInputStream(),
          osOut );
      if( osErr != null)
        threadStderr = new StreamConnectThread( proc.getErrorStream(),
          osErr );

      if( threadStdout != null)
        threadStdout.start();
      if( threadStderr != null)
        threadStderr.start();


      /*
      try
      {
        if( threadStdout != null)
          // Thread It joins the completion.
          threadStdout.join();
        if( threadStderr != null)
          // Thread It joins the completion.
          threadStderr.join();
      }
      catch( InterruptedException ex )
      {
        System.out.println( ex.toString() );
      }
      */

      //boolean blExit = false;
      //while( !blExit )
      {
        try
        {
          i_status = proc.waitFor();
          //i_status = proc.exitValue();
          //blExit = true;
        }
        catch( Exception ex )
        {
          if( osErr != null)
          {
            String strErr = ex.toString();
            try
            {
              (new PrintStream( osErr)).println( strErr);
            }
            catch (Exception ex1)
            {
              System.err.println( strErr);
            }
          }
          else
            System.err.println( ex.toString());
          //continue;
        }
      }

      if( threadStdin != null )
        threadStdin.close();

      if( threadStdout != null)
        threadStdout.close();
      if( threadStderr != null)
        threadStderr.close();

      try
      {
        proc.getOutputStream().close();
        proc.getInputStream().close();
        proc.destroy();
        fi.setCurrentProcess( null);
      }
      catch( IOException ex )
      {
        if( osErr != null)
        {
          String strErr = ex.toString();
          try
          {
            (new PrintStream( osErr)).println( strErr);
          }
          catch (Exception ex1)
          {
            System.err.println( strErr);
          }
        }
        else
          System.err.println( ex.toString());
        ex.printStackTrace();
      }

    }
    return i_status;
  }


  /**
   * copy file
   *
   * @param bi: BufferedInputStream from source
   * @param bo: BufferedOutputStream to destination
   */
  /*
  public boolean copyFile( BufferedInputStream bi, BufferedOutputStream bo)
      throws IOException
  {
    return m_file.copyFile( bi, bo);
  }

  public String extractToTempDir()
  {
    return m_file.extractToTempDir();
  }
*/
  static public String extractToTempDir( String strJarPath, String strIntPath, String strParentPath, FileInterfaceConfiguration fic)
  {
    int idx = strJarPath.lastIndexOf( File.separatorChar);
    if( idx > 0 &&  idx == strJarPath.length()-1)
      strJarPath = strJarPath.substring( 0, strJarPath.length()-1);
    idx = strIntPath.indexOf( File.separatorChar);
    if( idx == 0)
      strIntPath = strIntPath.substring( 1, strIntPath.length());
    idx = strIntPath.lastIndexOf( File.separatorChar);
    if( idx > 0 &&  idx == strIntPath.length()-1)
      strIntPath = strIntPath.substring( 0, strIntPath.length()-1);
    if( strParentPath == null)
    {
      String path = strJarPath + File.separatorChar + strIntPath;
      idx = path.lastIndexOf( File.separatorChar);
      strParentPath = path.substring( 0, idx);
    }
    FileInterface fi = FileConnector.createFileInterface( strJarPath + File.separatorChar + strIntPath, strParentPath,
      false, fic);
    String strPath = fi.extractToTempDir();

    /*
    if( strPath != null)
    {
      strPath += File.separatorChar + fi.getName();
    }
    */
    return strPath;
  }
}

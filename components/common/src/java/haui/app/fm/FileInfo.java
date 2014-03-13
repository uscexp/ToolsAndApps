package haui.app.fm;

import cz.dhl.ftp.Ftp;
import haui.components.ConnectionManager;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * Module:					FileInfo.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfo.java,v $ <p> Description:    Cache for file information.<br> </p><p> Created:				20.10.2000	by	AE </p><p>
 * @history  				20.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileInfo.java,v $  Revision 1.4  2004-08-31 16:03:22+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.3  2004-06-22 14:08:51+02  t026843  bigger changes  Revision 1.2  2003-06-06 10:04:02+02  t026843  modifications because of the moving the 'TypeFile's to haui.io package  Revision 1.1  2003-05-28 14:19:55+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:45+02  t026843  Initial revision  Revision 1.10  2002-09-18 11:16:18+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.9  2002-09-03 17:07:59+02  t026843  - CgiTypeFile is now full functional.  - Migrated to the extended filemanager.pl script.  Revision 1.8  2002-08-28 14:22:40+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.7  2002-08-07 15:25:24+02  t026843  Ftp support via filetype added.  Some bugfixes.  Revision 1.6  2002-06-26 12:15:12+02  t026843  bugfix  Revision 1.5  2002-06-26 12:06:45+02  t026843  History extended, simple bookmark system added.  Revision 1.4  2002-06-21 11:00:16+02  t026843  Added gzip and bzip2 file support  Revision 1.3  2002-06-19 16:13:52+02  t026843  Zip file support; writing doesn't work yet!  Revision 1.2  2002-06-17 17:19:18+02  t026843  Zip and Jar filetype read only support.  Revision 1.1  2002-05-29 11:18:18+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.0  2001-07-20 16:32:55+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.4 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfo.java,v 1.4 2004-08-31 16:03:22+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FileInfo
{
  // constants
  public final static String ACTION = "action";
  public final static String PATH = "path";
  public final static String PARAM = "param";
  public final static int LOCALTOLOCAL = 1;
  public final static int LOCALTOREMOTE = 2;
  public final static int REMOTETOLOCAL = 3;
  public final static int REMOTETOREMOTE = 4;
  public final static int ANYTOANY = 5;
  public final static int BUFFERSIZE = 32768;

  // member variables
  Object m_connObj;
  String m_strAppName;
  String m_strCurPath;
  FileInterface m_fCurrent;
  boolean m_blLocal;
  String m_strFileInfoId;
  AppProperties m_appProps;

  // cached file information
  char m_cSeparator;
  boolean m_blRead;
  boolean m_blWrite;
  boolean m_blDirectory;
  boolean m_blArchive;
  boolean m_blFile;
  boolean m_blHidden;
  long m_lLength;
  String m_strName;
  String m_strParent;
  String m_strPath;
  String m_strAbsolutePath;
  String[] m_strRoots;
  Date m_dateModified;
  /**
   * @uml.property  name="m_finfoArray"
   * @uml.associationEnd  multiplicity="(0 -1)"
   */
  FileInfo[] m_finfoArray;
  FileInterfaceFilter m_fifOld = null;
  FileInterfaceFilter m_fifCurrent = null;

  public Object clone()
  {
    Object cm = null;
    if( m_connObj != null)
    {
      if( m_connObj instanceof Ftp)
      {
        cm = (Ftp)m_connObj;
      }
      if( m_connObj instanceof ConnectionManager)
      {
        cm = ((ConnectionManager)cm).clone();
      }
    }
    FileInfo finfo = new FileInfo( m_strCurPath, m_strParent, m_blLocal, cm, m_strAppName, m_appProps);
    finfo.setFileInfoId( m_strFileInfoId);
    finfo.setSeperatorChar( m_cSeparator);
    finfo.setReadable( m_blRead);
    finfo.setWriteable( m_blWrite);
    finfo.setDirectory( m_blDirectory);
    finfo.setArchive( m_blArchive);
    finfo.setFile( m_blFile);
    finfo.setHidden( m_blHidden);
    finfo.setLength( m_lLength);
    finfo.setName( m_strName);
    finfo.setParent( m_strParent);
    finfo.setPath( m_strPath);
    finfo.setAbsolutePath( m_strAbsolutePath);
    finfo.setFileInterfaceFilter( m_fifCurrent);
    if( isLocal())
    {
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
          m_strAppName, m_appProps, true);
      FileInterface fi = FileConnector.createFileInterface( m_strCurPath, m_strParent, false, fic);
      finfo.setCurrentFile( fi);
    }
    String strRoots[] = new String[ Array.getLength( m_strRoots)];
    for( int i = 0; i < Array.getLength( m_strRoots); i++)
    {
      strRoots[i] = new String( m_strRoots[i]);
    }
    finfo.setRoots( strRoots);
    if( m_finfoArray != null)
    {
      FileInfo finfoArray[] = new FileInfo[ Array.getLength( m_finfoArray)];
      for( int i = 0; i < Array.getLength( m_finfoArray); i++)
      {
        finfoArray[i] = (FileInfo)m_finfoArray[i].clone();
      }
      finfo.setFileInfoArray( finfoArray);
    }
    finfo.setLastModified( (Date)m_dateModified.clone());
    return finfo;
  }

  static public String findFile( String strStartDir, String strFileName, boolean blRecursive, FileInterfaceConfiguration fic)
  {
    return FileConnector.findFile( strStartDir, strFileName, blRecursive, fic);
  }

  public String getFileInfoId()
  {
    return m_strFileInfoId;
  }

  public FileInterface getFileInterface()
  {
    if( m_fCurrent == null)
      return null;
    return m_fCurrent;
  }

  public void setFileInfoId( String strFiId)
  {
    m_strFileInfoId = strFiId;
  }

  public FileInterfaceFilter getFileInterfaceFilter()
  {
    return m_fifCurrent;
  }

  public void setFileInterfaceFilter( FileInterfaceFilter fif)
  {
    m_fifOld = m_fifCurrent;
    m_fifCurrent = fif;
  }

  // set accessors only for method initFileInfoArray()
  public void setCurrentFile( FileInterface f)
  {
    m_fCurrent = f;
  }

  public void setSeperatorChar( char cSeperator)
  {
    m_cSeparator = cSeperator;
  }

  public void setReadable( boolean blRead)
  {
    m_blRead = blRead;
  }

  public void setWriteable( boolean blWrite)
  {
    m_blWrite = blWrite;
  }

  public void setDirectory( boolean blDirectory)
  {
    m_blDirectory = blDirectory;
  }

  public void setArchive( boolean blArchive)
  {
    m_blArchive = blArchive;
  }

  public void setFile( boolean blFile)
  {
    m_blFile = blFile;
  }

  public void setHidden( boolean blHidden)
  {
    m_blHidden = blHidden;
  }

  public void setLength( long lLength)
  {
    m_lLength = lLength;
  }

  public void setName( String strName)
  {
    m_strName = strName;
  }

  public void setParent( String strParent)
  {
    m_strParent = strParent;
  }

  public void setPath( String strPath)
  {
    m_strPath = strPath;
  }

  public void setAbsolutePath( String strAbsolutePath)
  {
    m_strAbsolutePath = strAbsolutePath;
  }

  public void setRoots( String[] strRoots)
  {
    m_strRoots = strRoots;
  }

  public void setLastModified( Date dateModified)
  {
    m_dateModified = dateModified;
  }

  public void setFileInfoArray( FileInfo[] finfoArray)
  {
    m_finfoArray = finfoArray;
  }

  public boolean isLocal()
  {
    return m_blLocal;
  }

  public AppProperties getAppProperties()
  {
    return m_appProps;
  }

  public ConnectionManager getConnectionManager()
  {
    if( m_connObj instanceof ConnectionManager)
      return (ConnectionManager)m_connObj;
    return null;
  }

  public Object getConnectionObject()
  {
    return m_connObj;
  }

  /**
   * Creates a new FileInfo instance.
   *
   * @param strCurPath: current path
   * @param blLocal: true, for a local file; false, for a file access via cgi
   */
  public FileInfo( String strCurPath, String strParentPath, boolean blLocal, Object connObj, String strAppName, AppProperties appProps)
  {
    m_appProps = appProps;
    m_strAppName = strAppName;
    m_strCurPath = strCurPath;
    m_strParent = strParentPath;
    m_blLocal = blLocal;
    m_connObj = connObj;
    setCurPath( m_strCurPath);
  }

  public BufferedInputStream getBufferedInputStream()
    throws FileNotFoundException, IOException
  {
    if( m_fCurrent != null)
      return m_fCurrent.getBufferedInputStream();
    return null;
  }

  public BufferedOutputStream getBufferedOutputStream( String strNewPath)
    throws FileNotFoundException
  {
    if( m_fCurrent != null)
      return m_fCurrent.getBufferedOutputStream( strNewPath);
    return null;
  }

  /**
   * Remove a FileInfo from array
   *
   * @param iIdx: index of the FileInfo in current path
   */
  private void remove( int iIdx)
  {
    if( m_finfoArray != null && iIdx < Array.getLength( m_finfoArray))
    {
      FileInfo[] finfoOld = m_finfoArray;
      m_finfoArray = new FileInfo[ Array.getLength( finfoOld)-1];
      for( int i = 0; i < Array.getLength( finfoOld); i++)
      {
        if( i == iIdx)
          continue;
        m_finfoArray[i] = finfoOld[i];
      }
    }
  }

  /**
   * Add new FileInfo to current path if the parent path (of strPath) is equal to this.getPath()
   *
   * @param strDir: path of the directory
   */
  private void add( String strPath, String strParentPath)
  {
    if( m_finfoArray != null)
    {
      FileInfo finfo = new FileInfo( strPath, strParentPath, m_blLocal, m_connObj, m_strAppName, m_appProps);
      finfo.setFileInterfaceFilter( getFileInterfaceFilter());
      finfo.init( false);
      if( finfo.getParent().equals( getPath()))
      {
        FileInfo[] finfoOld = m_finfoArray;
        m_finfoArray = new FileInfo[ Array.getLength( finfoOld)+1];
        int i = 0;
        for( i = 0; i < Array.getLength( finfoOld); i++)
        {
          m_finfoArray[i] = finfoOld[i];
        }
        m_finfoArray[i] = finfo;
      }
    }
  }

  /**
   * Read and initialize the cached information
   *
   * @param blInitFInfoArray: true to initialize the FileInfo array for directorys
   */
  public void init(  boolean blInitFInfoArray)
  {
    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
        m_strAppName, m_appProps, true);
    FileInterface fi = FileConnector.createFileInterface( m_strCurPath, m_strParent, false, fic);
    //FileInterface fi = FileConnector.createFileInterface( m_strCurPath, m_strParent, blInitFInfoArray, m_connObj, m_strAppName, m_appProps);
    if( fi == null)
      return;
    m_fCurrent = fi;
    m_strFileInfoId = m_fCurrent.getId();
    m_cSeparator = m_fCurrent.separatorChar();
    m_blRead = m_fCurrent.canRead();
    m_blWrite = m_fCurrent.canWrite();
    m_blDirectory = m_fCurrent.isDirectory();
    m_blArchive = m_fCurrent.isArchive();
    m_blFile = m_fCurrent.isFile();
    m_blHidden = m_fCurrent.isHidden();
    m_lLength = m_fCurrent.length();
    m_strName = m_fCurrent.getName();
    m_strAbsolutePath = m_fCurrent.getAbsolutePath();
    //m_strParent = m_fCurrent.getParent();
    if( m_strParent == null)
    {
      m_strParent = m_fCurrent.getAbsolutePath();
      int iIdx = -1;
      if( m_strParent.length() > 2)
      {
        for( int i = m_strParent.length()-3; i >= 0; i--)
        {
          char c = m_strParent.charAt( i);
          if( c == m_cSeparator)
          {
            iIdx = i+1;
            break;
          }
        }
      }
      if( iIdx == -1)
        m_strParent = null;
      else
        m_strParent = m_strParent.substring( 0, iIdx);
    }
    m_strPath = m_fCurrent.getPath();

    FileInterface[] fRoots = m_fCurrent._listRoots();
    if( /* m_blLocal && */fRoots != null && fRoots.length > 0)
    {
      String[] strNames = new String[ fRoots.length];

      for( int i = 0; i < Array.getLength( fRoots); i++)
      {
        strNames[i] = fRoots[i].getPath();
      }
      m_strRoots = strNames;
    }

    m_dateModified = new Date( m_fCurrent.lastModified());
    if( blInitFInfoArray)
      initFileInfoArray();
    else
      m_finfoArray = null;
  }

  public char getSeparatorChar()
  {
    return m_cSeparator;
  }

  /**
   * Read and initialize the cached information
   *
   * @param blInitFInfoArray: true to initialize the FileInfo array for directorys
   */
  protected void init(  FileInterface fi)
  {
    m_strFileInfoId = fi.getId();
    m_fCurrent = fi;
    m_cSeparator = m_fCurrent.separatorChar();
    m_blRead = m_fCurrent.canRead();
    m_blWrite = m_fCurrent.canWrite();
    m_blDirectory = m_fCurrent.isDirectory();
    m_blArchive = m_fCurrent.isArchive();
    m_blFile = m_fCurrent.isFile();
    m_blHidden = m_fCurrent.isHidden();
    m_lLength = m_fCurrent.length();
    m_strName = m_fCurrent.getName();
    m_strAbsolutePath = m_fCurrent.getAbsolutePath();
    //m_strParent = m_fCurrent.getParent();
    if( m_strParent == null)
    {
      m_strParent = m_fCurrent.getAbsolutePath();
      int iIdx = -1;
      if( m_strParent.length() > 2)
      {
        for( int i = m_strParent.length()-3; i >= 0; i--)
        {
          char c = m_strParent.charAt( i);
          if( c == m_cSeparator)
          {
            iIdx = i+1;
            break;
          }
        }
      }
      if( iIdx == -1)
        m_strParent = null;
      else
        m_strParent = m_strParent.substring( 0, iIdx);
    }
    m_strPath = m_fCurrent.getPath();

    FileInterface[] fRoots = m_fCurrent._listRoots();
    if( /* m_blLocal && */fRoots != null && fRoots.length > 0)
    {
      String[] strNames = new String[ fRoots.length];

      for( int i = 0; i < Array.getLength( fRoots); i++)
      {
        strNames[i] = fRoots[i].getPath();
      }
      m_strRoots = strNames;
    }

    m_dateModified = new Date( m_fCurrent.lastModified());
  }

  /**
   * Read and initialize all FileInfos for the directory
   */
  public void initFileInfoArray()
  {
    if( m_fCurrent == null)
      init( false);
    if( isDirectory() || isArchive())
    {
      FileInterface[] files = m_fCurrent._listFiles( m_fifCurrent);
      m_finfoArray = new FileInfo[Array.getLength( files)];
      for( int i = 0; i < Array.getLength( files); i++)
      {
        m_finfoArray[i] = new FileInfo( files[i].getAbsolutePath(), files[i].getParent(), m_blLocal, m_connObj, m_strAppName, m_appProps);
        m_finfoArray[i].setFileInterfaceFilter( getFileInterfaceFilter());
        m_finfoArray[i].init( files[i]);
      }
      m_fifOld = m_fifCurrent;
    }
  }

  public void refresh()
  {
    init( true);
  }

  /**
   * get path of current directory
   *
   * @return current path
   */
  public String getCurPath()
  {
    return m_strCurPath;
  }

  /**
   * set path of current directory
   *
   * @param strCurPath: current path
   */
  public void setCurPath( String strCurPath)
  {
    m_strCurPath = strCurPath;
  }

  /**
   * Checks if file is a directory
   *
   * @return true if the file is a directory
   */
  public boolean isDirectory()
  {
    return m_blDirectory;
  }

  /**
   * Checks if file at index is a directory
   *
   * @param iIdx: index of the file in current path
   *
   * @return true if the file is a directory
   */
  public boolean isDirectory( int iIdx)
  {
    boolean blRet = false;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      blRet = finfo.isDirectory();
    }

    return blRet;
  }

  /**
   * Checks if file is a archive
   *
   * @return true if the file is a archive
   */
  public boolean isArchive()
  {
    return m_blArchive;
  }

  /**
   * Checks if file at index is a archive
   *
   * @param iIdx: index of the file in current path
   *
   * @return true if the file is a archive
   */
  public boolean isArchive( int iIdx)
  {
    boolean blRet = false;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      blRet = finfo.isArchive();
    }

    return blRet;
  }

  /**
   * Checks if file is a file
   *
   * @return true if the file is a file
   */
  public boolean isFile()
  {
    return m_blFile;
  }

  /**
   * Checks if file at index is a file
   *
   * @param iIdx: index of the file in current path
   *
   * @return true if the file is a file
   */
  public boolean isFile( int iIdx)
  {
    boolean blRet = false;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      blRet = finfo.isFile();
    }

    return blRet;
  }

  /**
   * Gives all files in current path
   *
   * @return absolute pathnames of all files in current path
   */
  public String[] listAbsolutePath()
  {
    String[] strRet = null;

    if( listFiles() != null && Array.getLength( listFiles()) > 0)
    {
      String[] strNames = new String[ Array.getLength( listFiles())];
      for( int i = 0; i < Array.getLength( listFiles()); i++)
      {
        strNames[i] = getAbsolutePath( i);
      }
      strRet = strNames;
    }
    return strRet;
  }

  /**
   * Gives all files in current path
   *
   * @return pathnames of all files in current path
   */
  public String[] list()
  {
    String[] strRet = null;

    if( listFiles() != null && Array.getLength( listFiles()) > 0)
    {
      String[] strNames = new String[ Array.getLength( listFiles())];
      for( int i = 0; i < Array.getLength( listFiles()); i++)
      {
        strNames[i] = getAbsolutePath( i);
      }
      strRet = strNames;
    }
    return strRet;
  }

  /**
   * Gives all FileInfos in current path
   *
   * @return pathnames of all files in current path
   */
  public FileInfo[] listFiles()
  {
    if( m_fifOld != m_fifCurrent)
      initFileInfoArray();
    return m_finfoArray;
  }

  /**
   * Gives all names of files in current path
   *
   * @return names of all files in current path
   */
  public String[] listNames()
  {
    String[] strRet = null;

    if( listFiles() != null && Array.getLength( listFiles()) > 0)
    {
      String[] strNames = new String[ Array.getLength( listFiles())];

      for( int i = 0; i < Array.getLength( listFiles()); i++)
      {
        strNames[i] = getName( i);
      }
      strRet = strNames;
    }
    return strRet;
  }

  /**
   * get roots
   *
   * @return array of roots
   */
  public String[] getRoots()
  {
    return m_strRoots;
  }

  /**
   * get name of the file
   *
   * @return name the file
   */
  public String getName()
  {
    return m_strName;
  }

  /**
   * get name of the file at index
   *
   * @param iIdx: index of the file in current path
   *
   * @return name the file
   */
  public String getName( int iIdx)
  {
    String strRet = null;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      strRet = finfo.getName();
    }

    return strRet;
  }

  /**
   * get absolute path of the file
   *
   * @return absolute path the file
   */
  public String getAbsolutePath()
  {
    return m_strAbsolutePath;
  }

  /**
   * get absolute path of the file at index
   *
   * @param iIdx: index of the file in current path
   *
   * @return absolute path the file
   */
  public String getAbsolutePath( int iIdx)
  {
    String strRet = null;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      strRet = finfo.getAbsolutePath();
    }

    return strRet;
  }

  /**
   * get path of the file
   *
   * @return path the file
   */
  public String getPath()
  {
    return m_strPath;
  }

  /**
   * get path of the file at index
   *
   * @param iIdx: index of the file in current path
   *
   * @return path the file
   */
  public String getPath( int iIdx)
  {
    String strRet = null;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      strRet = finfo.getPath();
    }

    return strRet;
  }

  /**
   * get parent path of the current path
   *
   * @return parent path
   */
  public String getParent()
  {
    return m_strParent;
  }

  /**
   * delete file
   *
   * @return true if the file or directory is successfully deleted; false otherwise
   */
  public boolean delete()
  {
    boolean blRet = false;

    if( isDirectory())
    {
      initFileInfoArray();
      FileInfo finfos[] = listFiles();

      for( int i = 0; i < Array.getLength( finfos); i++)
      {
        finfos[i].delete();
      }
    }
    if( m_fCurrent == null)
    {
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
          m_strAppName, m_appProps, true);
      FileInterface fFile = FileConnector.createFileInterface( getAbsolutePath(), getParent(), false, fic);
      if( fFile == null )
        return blRet;
      blRet = fFile.delete();
    }
    else
    {
      blRet = m_fCurrent.delete();
    }
    if( !blRet)
      JOptionPane.showMessageDialog(
          GlobalApplicationContext.instance().getRootComponent(), "Can't delete file!", "Info", JOptionPane.WARNING_MESSAGE);
    return blRet;
  }

  /**
   * delete file at index
   *
   * @param iIdx: index of the file in current path
   *
   * @return true if the file or directory is successfully deleted; false otherwise
   */
  public boolean delete( int iIdx)
  {
    boolean blRet = false;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      blRet = finfo.delete();
      if( blRet)
        remove( iIdx);
    }

    if( !blRet)
      JOptionPane.showMessageDialog(
          GlobalApplicationContext.instance().getRootComponent(), "Can't delete file!", "Info", JOptionPane.WARNING_MESSAGE);
    return blRet;
  }

  /**
   * get date of last modification of the file
   *
   * @return date of last modification
   */
  public Date lastModified()
  {
    return m_dateModified;
  }

  /**
   * get date of last modification of the file at index
   *
   * @param iIdx: index of the file in current path
   *
   * @return date of last modification
   */
  public Date lastModified(  int iIdx)
  {
    Date dateRet = null;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      dateRet = finfo.lastModified();
    }

    return dateRet;
  }

  /**
   * lenght of the file
   *
   * @return lenght of the file
   */
  public long length()
  {
    return m_lLength;
  }

  /**
   * lenght of the file at index
   *
   * @param iIdx: index of the file in current path
   *
   * @return lenght of the file
   */
  public long length(  int iIdx)
  {
    long lRet = 0;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      lRet = finfo.length();
    }

    return lRet;
  }

  /**
   * length of all files in current path
   *
   * @return length of all files
   */
  public long lengthOfFilesOfDir()
  {
    long lRet = 0;

    if( listFiles() != null && Array.getLength( listFiles()) > 0)
    {
      long lLength = 0;

      for( int i = 0; i < Array.getLength( listFiles()); i++)
      {
        lLength += listFiles()[i].length();
      }
      lRet = lLength;
    }
    return lRet;
  }

  /**
   * List the available filesystem roots.
   *
   * @return filesystem roots
   */
  public String[] listRoots()
  {
    return m_strRoots;
  }

  /**
   * make directory
   *
   * @param strDir: path (name) of the directory
   *
   * @return true if the directory is successfully created; false otherwise
   */
  public boolean mkdir( String strDir)
  {
    boolean blRet = false;

    if( isDirectory())
    {
      String strPath = null;
      String strNewPath = getAbsolutePath();
      if( strNewPath.charAt( strNewPath.length()-1) != getSeparatorChar())
        strNewPath += getSeparatorChar();
      strNewPath += strDir;
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
          m_strAppName, m_appProps, true);
      FileInterface fFile = FileConnector.createFileInterface( getAbsolutePath(), getParent(), false, fic);
      if( fFile == null)
        return blRet;
      blRet = fFile.mkdir();
      strPath = fFile.getPath();
      if( blRet)
        add( strPath, getAbsolutePath());
    }

    return blRet;
  }

  /**
   * rename file
   *
   * @param strNew: new file name
   *
   * @return true if the file or directory is successfully renamed; false otherwise
   */
  public boolean renameTo( String strNew)
  {
    boolean blRet = false;

    String strNewPath = getParent();
    if( strNewPath.charAt( strNewPath.length()-1) != getSeparatorChar())
      strNewPath += getSeparatorChar();
    strNewPath += strNew;
    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
        m_strAppName, m_appProps, true);
    FileInterface fNew = FileConnector.createFileInterface( strNewPath, getParent(), false, fic);
    if( fNew == null)
      return blRet;
    FileInterface fFile = m_fCurrent;
    if( fFile == null)
    {
      fFile = FileConnector.createFileInterface( getPath(), getParent(), false, fic);
      if( fFile == null )
        return blRet;
    }
    blRet = fFile.renameTo( fNew);
    m_strName = fNew.getName();
    m_strPath = fNew.getPath();
    m_dateModified = new Date( fNew.lastModified());

    return blRet;
  }

  /**
   * rename file at index
   *
   * @param strNew: new file name
   * @param iIdx: index of the file in current path
   *
   * @return true if the file or directory is successfully renamed; false otherwise
   */
  public boolean renameTo( String strNew, int iIdx)
  {
    boolean blRet = false;

    FileInfo finfo = listFiles()[iIdx];
    blRet = finfo.renameTo( strNew);
    if( blRet)
    {
      remove( iIdx);
      add( finfo.getPath(), finfo.getParent());
    }

    return blRet;
  }

  /**
   * Tests wether the file exists
   *
   * @param strFile: path of the file
   *
   * @return true if the file exists; false otherwise
   */
  public boolean exists( String strFile)
  {
    boolean blRet = false;

    //FileConnector fFile = new FileConnector( strFile, null, false);
    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
        m_strAppName, m_appProps, true);
    blRet = FileConnector.exists( strFile, fic);

    return blRet;
  }

  /**
   * Tests whether the application can read the file
   *
   * @return true if the file is readable; false otherwise
   */
  public boolean canRead()
  {
    return m_blRead;
  }

  /**
   * Tests whether the application can read the file
   *
   * @param iIdx: index of the file in current path
   *
   * @return true if the file is readable; false otherwise
   */
  public boolean canRead( int iIdx)
  {
    boolean blRet = false;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      blRet = finfo.canRead();
    }

    return blRet;
  }

  /**
   * Tests whether the application can write the file
   *
   * @return true if the file is writeable; false otherwise
   */
  public boolean canWrite()
  {
    return m_blWrite;
  }

  /**
   * Tests whether the application can write the file
   *
   * @param iIdx: index of the file in current path
   *
   * @return true if the file is writeable; false otherwise
   */
  public boolean canWrite( int iIdx)
  {
    boolean blRet = false;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      blRet = finfo.canWrite();
    }

    return blRet;
  }

  /**
   * Tests whether the file is hidden
   *
   * @return true if the file is hidden; false otherwise
   */
  public boolean isHidden()
  {
    return m_blHidden;
  }

  /**
   * Tests whether the file is hidden
   *
   * @param iIdx: index of the file in current path
   *
   * @return true if the file is hidden; false otherwise
   */
  public boolean isHidden( int iIdx)
  {
    boolean blRet = false;

    if( listFiles() != null)
    {
      FileInfo finfo = listFiles()[iIdx];
      blRet = finfo.isHidden();
    }

    return blRet;
  }

  public void logon()
  {
    m_fCurrent.logon();
    return;
  }

  public void logoff()
  {
    m_fCurrent.logoff();
    m_appProps.remove( GlobalApplicationContext.LOGONPASSWORD);
    return;
  }

  public void startTerminal()
  {
    m_fCurrent.startTerminal();
  }

  /**
   * ececute command
   *
   * @param strTargetPath traget path
   * @param cmd CommandClass
   */
  public void exec( String strTargetPath, CommandClass cmd,
                    OutputStream osOut, OutputStream osErr, InputStream is )
    throws IOException
  {
    m_fCurrent.exec( strTargetPath, cmd, osOut, osErr, is);
  }

  /**
   * move file
   *
   * @param strNewPath: new file path
   * @param mode: move mode e.g. LOCALTOLOCAL
   *
   * @return true if the file or directory is successfully moved; false otherwise
   */
  public boolean move( String strNewPath, int mode)
  {
    boolean blRet = false;

    if( strNewPath.charAt( strNewPath.length()-1) != getSeparatorChar())
      strNewPath += getSeparatorChar();
    strNewPath += getName();
    switch( mode)
    {
      case LOCALTOLOCAL: // move from local to local
        FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, m_connObj, 0, 0,
            m_strAppName, m_appProps, true);
        FileInterface fNew = FileConnector.createFileInterface( strNewPath, null, false, fic);
        if( fNew == null)
          return blRet;
        blRet = m_fCurrent.renameTo( fNew);
        break;

      case REMOTETOREMOTE: // move from remote to remote, same host
        break;
    }
    if( blRet)
    {
      m_strCurPath = strNewPath;

      setCurPath( m_strCurPath);
      init( false);
    }

    return blRet;
  }

  /**
   * copy file
   *
   * @param strNewPath: new file path
   * @param mode: copy mode e.g. LOCALTOLOCAL
   *
   * @return true if the file or directory is successfully copied; false otherwise
   */
  public boolean copy( FileInfo finfoDest, String strNewPath, int mode)
  {
    boolean blRet = false;

    // check if the last char in path is the seperator
    if( strNewPath.charAt( strNewPath.length()-1) != getSeparatorChar())
      strNewPath += finfoDest.getSeparatorChar();
    strNewPath += getName();
    switch( mode)
    {
      case LOCALTOLOCAL: // copy from local to local
        try
        {
          BufferedOutputStream bo = finfoDest.getBufferedOutputStream( strNewPath);

          m_fCurrent.copyFile( bo);
          bo.flush();
          bo.close();
          blRet = true;
        }
        catch( FileNotFoundException fnfex)
        {
          fnfex.printStackTrace();
        }
        catch( IOException ioex)
        {
          ioex.printStackTrace();
        }
        break;

      case REMOTETOREMOTE: // copy from remote to remote, same host
        break;

      case REMOTETOLOCAL: // copy from remote to local
        break;
    }

    if( !blRet)
      JOptionPane.showMessageDialog(
          GlobalApplicationContext.instance().getRootComponent(), "Can't copy file!", "Info", JOptionPane.WARNING_MESSAGE);
    return blRet;
  }

  /**
   * copy file
   *
   * @param buffer: Buffer of bytes
   * @param bo: BufferedOutputStream to destination
   */
  void copyFile( byte buffer[], int iLen, BufferedOutputStream bo)
      throws IOException
  {
    bo.write(buffer, 0, iLen);
  }

  /**
   * copy file
   *
   * @param bo: BufferedOutputStream to destination
   */
  boolean copyFile( BufferedOutputStream bo)
      throws IOException
  {
    return m_fCurrent.copyFile( bo);
  }
}
package haui.io.FileInterface;

import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.util.GlobalApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Module:      JarTypeFile.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\JarTypeFile.java,v $
 *<p>
 * Description: Cache for file information.<br>
 *</p><p>
 * Created:     11.06.2002 by AE
 *</p><p>
 * @history     11.06.2002 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JarTypeFile.java,v $
 * Revision 1.2  2004-08-31 16:03:15+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.1  2004-06-22 14:08:50+02  t026843
 * bigger changes
 *
 * Revision 1.0  2003-06-06 10:05:37+02  t026843
 * Initial revision
 *
 * Revision 1.2  2003-06-04 15:35:55+02  t026843
 * bugfixes
 *
 * Revision 1.1  2003-05-28 14:19:52+02  t026843
 * reorganisations
 *
 * Revision 1.0  2003-05-21 16:25:52+02  t026843
 * Initial revision
 *
 * Revision 1.5  2002-09-18 11:16:20+02  t026843
 * - changes to fit extended filemanager.pl
 * - logon and logoff moved to 'TypeFile's
 * - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal
 * - LRShell changed to work with filemanager.pl
 *
 * Revision 1.4  2002-09-03 17:07:58+02  t026843
 * - CgiTypeFile is now full functional.
 * - Migrated to the extended filemanager.pl script.
 *
 * Revision 1.3  2002-08-07 15:25:22+02  t026843
 * Ftp support via filetype added.
 * Some bugfixes.
 *
 * Revision 1.2  2002-06-21 11:00:17+02  t026843
 * Added gzip and bzip2 file support
 *
 * Revision 1.1  2002-06-19 16:13:51+02  t026843
 * Zip file support; writing doesn't work yet!
 *
 * Revision 1.0  2002-06-17 17:21:27+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2002; $Revision: 1.2 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\JarTypeFile.java,v 1.2 2004-08-31 16:03:15+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class JarTypeFile
  extends ArchiveTypeFile
{
  // constants
  public final static String EXTENSION = ".jar";

  // member variables
  Vector m_zipEntries = new Vector();
  JarEntry m_curJarEntry;
  JarFile m_jarFile;

  /**
   * Creates a new JarTypeFile instance.
   *
   * @param strCurPath: current path
   */
  public JarTypeFile( String strArchPath, String strIntPath, String strParentPath, FileInterfaceConfiguration fic)
    throws IOException
  {
    super( strArchPath, strIntPath, strParentPath, fic);
    try
    {
      //if( super.exists())
        m_jarFile = new JarFile( strArchPath, true );
    }
    catch( Exception ex )
    {
      GlobalApplicationContext.instance().getOutputPrintStream().println( ex.toString());
      //System.err.println( ex.toString());
    }
    if( m_jarFile != null )
    {
      Enumeration enumeration = m_jarFile.entries();
      while( enumeration.hasMoreElements() )
      {
        m_zipEntries.add( enumeration.nextElement() );
      }
      String entryPath = m_intPath;
      entryPath = entryPath.replace( separatorChar(), '/' );
      m_curJarEntry = m_jarFile.getJarEntry( entryPath + "/" );
      if( m_curJarEntry == null )
        m_curJarEntry = m_jarFile.getJarEntry( entryPath );
    }
    //m_file = new File( m_archPath);

    // init
    if( !m_intPath.equals( "") && m_curJarEntry == null)
    {
      m_blRead = false;
      m_blWrite = false;
      m_strAbsolutePath = m_archPath + separatorChar() + m_intPath;
      m_strPath = m_intPath;
      if( FileConnector.isSupported( getPath()))
        m_blArchive = true;
      m_blDirectory = true;
      m_blFile = false;
      m_blHidden = false;
      m_lModified = m_file.lastModified();
      m_lLength = 0;
    }
    else if( m_curJarEntry == null)
    {
      m_blRead = m_file.canRead();
      m_blWrite = m_file.canWrite();
      try
      {
        m_strAbsolutePath = m_file.getAbsolutePath();
      }
      catch( Exception ex1 )
      {
      }
      if( m_strAbsolutePath == null)
        m_strAbsolutePath = m_file.getPath();
      m_strPath = m_file.getPath();
      m_strName = m_file.getName();
      m_strParent = m_file.getParent();
      if( FileConnector.isSupported( getPath()))
        m_blArchive = true;
      m_blDirectory = m_file.isDirectory();
      m_blFile = m_file.isFile();
      m_blHidden = m_file.isHidden();
      m_lModified = m_file.lastModified();
      m_lLength = m_file.length();
    }
    else
    {
      m_blRead = false;
      m_blWrite = false;
      m_strAbsolutePath = m_archPath + separatorChar() + m_intPath;
      m_strPath = m_intPath;
      if( FileConnector.isSupported( getPath()))
        m_blArchive = true;
      m_blDirectory = m_curJarEntry.isDirectory();
      m_blFile = !m_curJarEntry.isDirectory();
      m_blHidden = false;
      m_lModified = m_curJarEntry.getTime();
      m_lLength = m_curJarEntry.getSize();
    }
    _init();
  }

  /**
   * Creates a new JarTypeFile instance.
   *
   * @param strCurPath: current path
   */
  public JarTypeFile( String strArchPath, String strIntPath, FileInterfaceConfiguration fic)
    throws IOException
  {
    this( strArchPath, strIntPath, null, fic);
  }

  public FileInterface duplicate()
  {
    JarTypeFile jtf = null;
    try
    {
      jtf = new JarTypeFile( m_archPath, m_intPath, getParent(), getFileInterfaceConfiguration() );
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }
    return jtf;
  }

  public BufferedInputStream getBufferedInputStream()
    throws FileNotFoundException, IOException
  {
    BufferedInputStream bis = null;
    if( m_curJarEntry != null)
    {
      bis = new BufferedInputStream( m_jarFile.getInputStream( m_curJarEntry));
    }
    else if( m_intPath.equals( ""))
    {
      bis = new BufferedInputStream( new FileInputStream( getAbsolutePath()));
    }
    return bis;
  }

  public BufferedOutputStream getBufferedOutputStream( String strNewPath)
    throws FileNotFoundException
  {
    return new BufferedOutputStream( new FileOutputStream( strNewPath));
  }

  public FileInterface getCanonicalFile() throws IOException {
    return new JarTypeFile( m_archPath, m_intPath, m_strParent, getFileInterfaceConfiguration());
  }

  public boolean setLastModified( long time)
  {
    if( m_curJarEntry == null)
    {
      return m_file.setLastModified( time);
    }
    showNotSupportedText();
    return false;
  }

  public String[] list()
  {
    if( m_strList != null)
      return m_strList;
    Vector list = new Vector();
    m_strList = new String[ 0];
    if( isDirectory() || ( m_curJarEntry == null && isArchive()))
    {
      for( int i = 0; i < m_zipEntries.size(); ++i)
      {
        String path = "";
        ZipEntry ze = (ZipEntry)m_zipEntries.elementAt( i);
        String zipName = ze.getName();
        //boolean blDir = ze.isDirectory();
        boolean blInsert = false;
        String strCheck = "";
        if( !m_intPath.equals( ""))
        {
          strCheck = m_intPath + "/";
          strCheck = strCheck.replace( separatorChar(), '/');
        }
        int idx = zipName.indexOf( strCheck);
        if( idx != -1)
        {
          if( idx > 0 || !m_intPath.equals( ""))
            idx += m_intPath.length();
          if( idx < zipName.length())
          {
            path = zipName.substring( idx, zipName.length());
            int pos = -1;
            if( path.length() > pos)
            {
              StringTokenizer sto = new StringTokenizer( path, "\\/", false);
              if( sto.hasMoreTokens())
              {
                //boolean blFound = false;
                path = sto.nextToken();
                String tmp = getAbsolutePath() + separatorChar() + path;
                if( !list.contains( tmp))
                  blInsert = true;
              }
            }
          }
          else
          {
            path = zipName;
            blInsert = true;
          }
          if( blInsert)
          {
            list.add( getAbsolutePath() + separatorChar() + path);
            blInsert = false;
          }
        }
      }
      m_strList = new String[ list.size()];
      for( int j = 0; j < list.size(); ++j)
      {
        m_strList[j] = (String)list.elementAt( j);
      }
    }
    return m_strList;
  }

  public boolean renameTo( FileInterface file)
  {
    if( m_curJarEntry == null)
    {
      return m_file.renameTo( ( File )file );
    }
    showNotSupportedMessage();
    return false;
  }

  public boolean delete()
  {
    if( m_curJarEntry == null)
    {
      return m_file.delete();
    }
    showNotSupportedMessage();
    return false;
  }

  public boolean mkdir()
  {
    if( m_curJarEntry == null)
    {
      m_fiList = null;
      m_strList = null;
      m_blArchive = false;
      m_blDirectory = true;
      m_blFile = false;
      return m_file.mkdir();
    }
    showNotSupportedMessage();
    return false;
  }

  public boolean exists()
  {
    boolean blRet = false;
    if( m_curJarEntry == null && m_intPath.equals(""))
      return m_file.exists();
    blRet = m_file.exists();
    if( blRet && m_curJarEntry != null)
    {
      blRet = false;
      StringBuffer strbufEntry = new StringBuffer( m_curJarEntry.getName());
      if( strbufEntry.charAt( strbufEntry.length()-1) == '/'
          || strbufEntry.charAt( strbufEntry.length()-1) == separatorChar())
        strbufEntry.deleteCharAt( strbufEntry.length()-1);
      StringBuffer strbufIntPath = new StringBuffer( m_intPath);
      if( strbufIntPath.charAt( strbufIntPath.length()-1) == separatorChar())
        strbufIntPath.deleteCharAt( strbufIntPath.length()-1);
      String strEntry = strbufEntry.toString();
      strEntry = strEntry.replace( '/', separatorChar());
      String strIntPath = strbufIntPath.toString();
      if( strEntry.equals( strIntPath))
        blRet = true;
    }
    else
      blRet = false;
    return blRet;
  }
}
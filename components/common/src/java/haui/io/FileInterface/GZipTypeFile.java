package haui.io.FileInterface;

import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Module:      GZipTypeFile.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\GZipTypeFile.java,v $
 *<p>
 * Description: Cache for file information.<br>
 *</p><p>
 * Created:     20.06.2002 by AE
 *</p><p>
 * @history     20.06.2002 by AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: GZipTypeFile.java,v $
 *		Revision 1.2  2004-08-31 16:03:18+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.1  2004-06-22 14:08:50+02  t026843
 *		bigger changes
 *
 *		Revision 1.0  2003-06-06 10:05:36+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2002; $Revision: 1.2 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\GZipTypeFile.java,v 1.2 2004-08-31 16:03:18+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class GZipTypeFile
  extends ArchiveTypeFile
{
  // constants
  public final static String EXTENSION = ".gz";
  public final static String EXTENSION1 = ".tgz";

  // member variables
  Vector m_gzipEntries = new Vector();
  String m_curGZipEntry;

  BufferedOutputStream m_bos = null;

  /**
   * Creates a new GZipTypeFile instance.
   *
   * @param strCurPath: current path
   */
  public GZipTypeFile( String strArchPath, String strIntPath, String strParentPath, FileInterfaceConfiguration fic)
    throws IOException
  {
    super( strArchPath, strIntPath, strParentPath, fic);
    String ge = "";
    ge = m_file.getName();
    int idx = -1;
    if( (idx = m_archPath.toLowerCase().indexOf( EXTENSION)) != -1)
      ge = ge.substring( 0, ge.length()-EXTENSION.length());
    else if( (idx = m_archPath.toLowerCase().indexOf( EXTENSION1)) != -1)
      ge = ge.substring( 0, ge.length()-EXTENSION1.length()) + TarTypeFile.EXTENSION;
    m_gzipEntries.add( ge);
    String entryPath = m_intPath;
    m_curGZipEntry = getEntry( entryPath);

    // init
    if( m_curGZipEntry == null)
    {
      m_blRead = m_file.canRead();
      m_blWrite = m_file.canWrite();
      m_strAbsolutePath = m_archPath;
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
      idx = m_intPath.lastIndexOf( separatorChar());
      m_strName = m_file.getName();
      if( (idx = m_archPath.toLowerCase().indexOf( EXTENSION)) != -1)
        m_strName = m_strName.substring( 0, m_strName.length()-EXTENSION.length());
      else if( (idx = m_archPath.toLowerCase().indexOf( EXTENSION1)) != -1)
        m_strName = m_strName.substring( 0, m_strName.length()-EXTENSION1.length()) + TarTypeFile.EXTENSION;
      if( FileConnector.isSupported( getPath()))
        m_blArchive = true;
      m_blDirectory = false;
      m_blFile = true;
      m_blHidden = false;
      m_lModified = m_file.lastModified();
      m_lLength = 0;
    }
    _init();
  }

  protected String getEntry( String path)
  {
    String ge = null;
    if( !path.equals(""))
      ge = path;
    return ge;
  }

  /**
   * Creates a new GZipTypeFile instance.
   *
   * @param strCurPath: current path
   */
  public GZipTypeFile( String strArchPath, String strIntPath, FileInterfaceConfiguration fic)
    throws IOException
  {
    this( strArchPath, strIntPath, null, fic);
  }

  public FileInterface duplicate()
  {
    GZipTypeFile gztf = null;
    try
    {
      gztf = new GZipTypeFile( m_archPath, m_intPath, getParent(), getFileInterfaceConfiguration());
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }
    return gztf;
  }

  public BufferedInputStream getBufferedInputStream()
    throws FileNotFoundException, IOException
  {
    BufferedInputStream bis = new BufferedInputStream( new GZIPInputStream( new FileInputStream( m_archPath)));
    return bis;
  }

  public BufferedOutputStream getBufferedOutputStream( String strNewPath)
    throws FileNotFoundException
  {
    if( strNewPath.indexOf( getParent()) == -1)
    {
      m_bos = new BufferedOutputStream( new FileOutputStream( strNewPath));
    }
    else
    {
      m_bos = null;
    }
    return m_bos;
  }

  public FileInterface getCanonicalFile() throws IOException {
    return new GZipTypeFile( m_archPath, m_intPath, m_strParent, getFileInterfaceConfiguration());
  }

  public long length()
  {
    if( m_lLength == 0)
      m_lLength = countLength();
    return m_lLength;
  }

  public boolean setLastModified( long time)
  {
    if( m_curGZipEntry == null)
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
    m_strList = new String[ 0];
    if( isDirectory() || ( m_curGZipEntry == null && isArchive()))
    {
      if( m_gzipEntries.size() > 0)
      {
        m_strList = new String[ 1];
        m_strList[0] = getAbsolutePath() + separatorChar() + (String)m_gzipEntries.elementAt( 0);
      }
    }
    return m_strList;
  }

  public boolean renameTo( FileInterface file)
  {
    if( m_curGZipEntry == null)
    {
      return m_file.renameTo( ( File )file );
    }
    showNotSupportedMessage();
    return false;
  }

  public boolean delete()
  {
    if( m_curGZipEntry == null)
    {
      return m_file.delete();
    }
    showNotSupportedMessage();
    return false;
  }

  public boolean mkdir()
  {
    if( m_curGZipEntry == null)
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
    if( m_curGZipEntry == null && m_intPath.equals(""))
      return m_file.exists();
    blRet = m_file.exists();
    if( blRet && m_curGZipEntry != null)
    {
      blRet = false;
      StringBuffer strbufEntry = new StringBuffer( m_curGZipEntry);
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

  protected long countLength()
  {
    long lRet = 0;
    try
    {
      BufferedInputStream bis = getBufferedInputStream();
      byte buffer[] = new byte[BUFFERSIZE];
      int bytesRead;
      while((bytesRead = bis.read(buffer, 0, BUFFERSIZE)) > 0)
        lRet += bytesRead;
      bis.close();
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
      lRet = 0;
    }
    return lRet;
  }
}
package haui.io.FileInterface;

import com.ice.tar.TarArchive;
import com.ice.tar.TarBuffer;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
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
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Module:      TarTypeFile.java<br> $Source: $ <p> Description: Cache for file information.<br> </p><p> Created:     20.06.2002 by AE </p><p>
 * @history      20.06.2002 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2002; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.2  </p>
 */
public class TarTypeFile
  extends ArchiveTypeFile
{
  // constants
  public final static String EXTENSION = ".tar";

  // member variables
  Vector m_tarEntries = new Vector();
  TarEntry m_curTarEntry;
  TarFile m_tarArchive;

  BufferedOutputStream m_bos = null;

  /**
   * Creates a new TarTypeFile instance.
   *
   * @param strCurPath: current path
   */
  public TarTypeFile( String strTarPath, String strIntPath, String strParentPath, FileInterfaceConfiguration fic)
    throws IOException
  {
    super( strTarPath, strIntPath, strParentPath, fic);
    try
    {
      //if( super.exists())
        m_tarArchive = new TarFile( new FileInputStream( strTarPath));
    }
    catch (Exception ex)
    {
      GlobalApplicationContext.instance().getOutputPrintStream().println( ex.toString());
      //System.err.println( ex.toString());
    }
    //super( new BufferedInputStream( new FileInputStream( strTarPath)));
    if( m_tarArchive != null)
    {
      TarEntry te = null;
      while( ( te = m_tarArchive.getTatIn().getNextEntry() ) != null )
      {
        m_tarEntries.add( ( TarEntry )te );
      }
      String entryPath = m_intPath;
      //entryPath = entryPath.replace( separatorChar(), '/');
      m_curTarEntry = getEntry( entryPath );
      //if( m_curTarEntry == null)
      //m_curTarEntry = super.getEntry( entryPath);
      //m_file = new File( m_tarPath);
    }

    // init
    if( !m_intPath.equals( "") && m_curTarEntry == null)
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
    else if( m_curTarEntry == null)
    {
      m_blRead = m_file.canRead();
      m_blWrite = m_file.canWrite();
      m_strAbsolutePath = m_file.getAbsolutePath();
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
      m_blDirectory = m_curTarEntry.isDirectory();
      m_blFile = !m_curTarEntry.isDirectory();
      m_blHidden = false;
      m_lModified = m_curTarEntry.getModTime().getTime();
      m_lLength = m_curTarEntry.getSize();
    }
    if( m_strParent == null)
    {
      m_strParent = getAbsolutePath();
      int iIdx = -1;
      if( m_strParent.length() > 2 )
      {
        for( int i = m_strParent.length() - 3; i >= 0; i-- )
        {
          char c = m_strParent.charAt( i );
          if( c == separatorChar() )
          {
            iIdx = i + 1;
            break;
          }
        }
      }
      if( iIdx == -1 )
        m_strParent = null;
      else
        m_strParent = m_strParent.substring( 0, iIdx );
    }
    _init();
  }

  protected TarEntry getEntry( String path)
  {
    TarEntry te = null;
    for( int i = 0; i < m_tarEntries.size(); ++i)
    {
      String strTe = ((TarEntry)m_tarEntries.elementAt(i)).getName();
      if( separatorChar() != '/')
        strTe = strTe.replace( '/', separatorChar());
      if( strTe.equals( path))
      {
        te = (TarEntry)m_tarEntries.elementAt(i);
        break;
      }
    }
    return te;
  }

  /**
   * Creates a new TarTypeFile instance.
   *
   * @param strCurPath: current path
   */
  public TarTypeFile( String strTarPath, String strIntPath, FileInterfaceConfiguration fic)
    throws IOException
  {
    this( strTarPath, strIntPath, null, fic);
  }

  public FileInterface duplicate()
  {
    TarTypeFile ttf = null;
    try
    {
      ttf = new TarTypeFile( m_archPath, m_intPath, getParent(), getFileInterfaceConfiguration());
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }
    return ttf;
  }

  public BufferedInputStream getBufferedInputStream()
    throws FileNotFoundException, IOException
  {
    BufferedInputStream bis = null;
    if( m_curTarEntry != null)
    {
      bis = new BufferedInputStream( getInputStream( m_curTarEntry));
    }
    else if( m_intPath.equals( ""))
    {
      bis = new BufferedInputStream( new FileInputStream( getAbsolutePath()));
    }
    return bis;
  }

  protected TarInputStream getInputStream( TarEntry te)
    throws FileNotFoundException, IOException
  {
    TarInputStream tis = new TarInputStream( new FileInputStream( m_archPath));
    TarEntry teSearch = null;
    boolean blFound = false;
    while( (teSearch = tis.getNextEntry()) != null)
    {
      if( teSearch.getName().equals( te.getName()))
      {
        blFound = true;
        break;
      }
    }
    if( blFound)
      return tis;
    else
    {
      tis.close();
      return null;
    }
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
    return new TarTypeFile( m_archPath, m_intPath, m_strParent, getFileInterfaceConfiguration());
  }

  public boolean setLastModified( long time)
  {
    if( m_curTarEntry == null)
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
    if( isDirectory() || ( m_curTarEntry == null && isArchive()))
    {
      for( int i = 0; i < m_tarEntries.size(); ++i)
      {
        String path = "";
        TarEntry te = (TarEntry)m_tarEntries.elementAt( i);
        String tarName = te.getName();
        if( separatorChar() != '/')
          tarName = tarName.replace( '/', separatorChar());
        //boolean blDir = te.isDirectory();
        boolean blInsert = false;
        String strCheck = m_intPath;
        /*
        if( !m_intPath.equals( ""))
        {
          strCheck = m_intPath + "/";
          strCheck = strCheck.replace( separatorChar(), '/');
        }
        */
        int idx = -1;
        if( tarName.startsWith( strCheck))
          idx = 0;
        if( idx != -1)
        {
          if( idx > 0 || !m_intPath.equals( ""))
            idx += m_intPath.length();
          if( idx < tarName.length())
          {
            path = tarName.substring( idx, tarName.length());
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
            path = tarName;
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
    if( m_curTarEntry == null)
    {
      return m_file.renameTo( ( File )file );
    }
    showNotSupportedMessage();
    return false;
  }

  public boolean delete()
  {
    if( m_curTarEntry == null)
    {
      return m_file.delete();
    }
    showNotSupportedMessage();
    return false;
  }

  public boolean mkdir()
  {
    if( m_curTarEntry == null)
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
    if( m_curTarEntry == null && m_intPath.equals(""))
      return m_file.exists();
    blRet = m_file.exists();
    if( blRet && m_curTarEntry != null)
    {
      blRet = false;
      StringBuffer strbufEntry = new StringBuffer( m_curTarEntry.getName());
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

  public class TarFile
    extends TarArchive
  {
    public TarFile( InputStream inStream )
    {
      super( inStream, TarBuffer.DEFAULT_BLKSIZE );
    }

    public TarInputStream getTatIn()
    {
      return tarIn;
    }
  }
}

package haui.io.FileInterface;

import haui.components.CancelProgressDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * Module:      BaseTypeFile<br> $Source: $ <p> Description: base FileInterface class.<br> </p><p> Created:     17.12.2004 by AE </p><p>
 * @history      17.12.2004 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.2  </p>
 */
public abstract class BaseTypeFile
  implements FileInterface
{
  // member variables
  protected FileInterfaceConfiguration m_fic;
  protected Process m_procCurrent = null;

  // cache variables
  protected boolean m_blRead = false;
  protected boolean m_blWrite = false;
  protected String m_strAbsolutePath;
  protected String m_strPath;
  protected String m_strName;
  protected String m_strParent;
  protected boolean m_blArchive = false;
  protected boolean m_blDirectory = false;
  protected boolean m_blFile = false;
  protected boolean m_blHidden;
  protected long m_lModified;
  protected long m_lLength;
  protected String[] m_strList = null;
  /**
   * @uml.property  name="m_fiList"
   * @uml.associationEnd  multiplicity="(0 -1)"
   */
  protected FileInterface[] m_fiList = null;
  /**
   * @uml.property  name="m_fiRoots"
   * @uml.associationEnd  multiplicity="(0 -1)"
   */
  protected FileInterface[] m_fiRoots = null;
  protected String m_strHost = LOCAL;
  private boolean m_blInit = false;

  public BaseTypeFile( FileInterfaceConfiguration fic)
  {
    setFileInterfaceConfiguration( fic);
  }
  
  /**
   * must be called from subclasses after initialising an FileInterface
   * to cache (if set to true) important attributes!
   */
  public void _init()
  {
    if( getFileInterfaceConfiguration().isCached())
    {
      m_blRead = canRead();
      m_blWrite = canWrite();
      m_blArchive = isArchive();
      m_blDirectory = isDirectory();
      m_blFile = isFile();
      m_blHidden = isHidden();
      m_lModified = lastModified();
      m_lLength = length();
      m_blInit = true;
    }
  }

  public boolean equals( Object obj)
  {
    boolean blRet = false;

    if( obj instanceof FileInterface)
    {
      FileInterface fi = (FileInterface)obj;
      if( fi.getAbsolutePath().equals( getAbsolutePath())
          && fi.canRead() == canWrite()
          && fi.length() == length()
          && fi.getId() == getId())
      {
        blRet = true;
      }
    }
    return blRet;
  }

  public FileInterfaceConfiguration getFileInterfaceConfiguration()
  {
    return m_fic;
  }

  public void setFileInterfaceConfiguration( FileInterfaceConfiguration fic)
  {
    this.m_fic = fic;
  }

  public String getTempDirectory()
  {
    String strTmpDir = FileConnector.TempDirectory;
    if( strTmpDir == null)
    {
      strTmpDir = new File( ".").getAbsolutePath();
      if( strTmpDir == null)
        strTmpDir = "";
      FileConnector.TempDirectory = strTmpDir;
    }
    else
    {
      int idx = strTmpDir.lastIndexOf( separatorChar());
      if( idx > 0 &&  idx == strTmpDir.length()-1)
        strTmpDir = strTmpDir.substring( 0, strTmpDir.length()-1);
    }
    return strTmpDir;
  }

  public FileInterface[] _listRoots()
  {
    if( m_fiRoots != null)
      return m_fiRoots;
    File f[] = File.listRoots();
    if( f == null)
      return null;

    FileInterface[] file = new FileInterface[ Array.getLength( f)];
    for( int i = 0; i < Array.getLength( f); ++i)
    {
      file[i] = FileConnector.createFileInterface( f[i].getAbsolutePath(), f[i].getParent(), false,
          getFileInterfaceConfiguration());
    }
    m_fiRoots = (FileInterface[])file;
    return m_fiRoots;
  }

  public boolean isCached()
  {
    return getFileInterfaceConfiguration().isCached() && m_blInit;
  }

  public boolean mkdirs()
  {
    return mkdirs( this);
  }

  private static boolean mkdirs( FileInterface fiDir)
  {
    boolean blRet = false;

    if( !fiDir.exists())
    {
      FileInterface fiPar = fiDir.getParentFileInterface();
      if( fiPar.equals( fiDir.getRootFileInterface()))
          blRet = fiDir.mkdir();
      else
      {
        blRet = mkdirs( fiPar );
        if( blRet)
          blRet = fiDir.mkdir();
      }
    }
    else
      blRet = true;

    return blRet;
  }

  public boolean isAbsolutePath( String strPath)
  {
    boolean blRet = false;
    FileInterface[] fi = _listRoots();

    int idx = strPath.lastIndexOf( separatorChar());
    if( idx == -1 || idx != strPath.length()-1)
      strPath = strPath + separatorChar();

    for( int i = 0; i < fi.length; ++i)
    {
      if( strPath.toUpperCase().startsWith( fi[i].getAbsolutePath().toUpperCase()))
      {
        blRet = true;
        break;
      }
    }
    return blRet;
  }

  public String getCanonicalPath() throws IOException {
    return getAbsolutePath();
  }

  public boolean isAbsolute() {
    return isAbsolutePath( getPath());
  }

  public boolean isArchive()
  {
    if( isCached())
      return m_blArchive;
    if( FileConnector.isSupported( getAbsolutePath()))
      return true;
    return false;
  }

  public String getHost()
  {
    return m_strHost;
  }

  public FileInterface getRootFileInterface()
  {
    FileInterface fiRet = null;
    FileInterface fiRoots[] = null;

    if( ( fiRoots = _listRoots() ) != null )
    {
      for( int i = 0; i < fiRoots.length; ++i)
      {
        if( getAbsolutePath().toUpperCase().startsWith( fiRoots[i].getAbsolutePath().toUpperCase()))
        {
          fiRet = fiRoots[i];
          break;
        }
      }
    }
    return fiRet;
  }
  
  public boolean isRoot()
  {
    FileInterface fiRoot = getRootFileInterface();
    if(fiRoot != null && getAbsolutePath().equalsIgnoreCase(fiRoot.getAbsolutePath()))
      return true;
    else
      return false;
  }

  public AppProperties getAppProperties()
  {
    return getFileInterfaceConfiguration().getAppProperties();
  }

  public void setAbsolutePath( String absolutePath)
  {
    m_strAbsolutePath = absolutePath;
  }

  public void setName( String name)
  {
    m_strName = name;
  }

  public void setParent( String parent)
  {
    m_strParent = parent;
  }

  public void setPath( String path)
  {
    m_strPath = path;
  }

  public String getAppName()
  {
    return getFileInterfaceConfiguration().getAppName();
  }

  public FileInterface[] _listFiles()
  {
    return _listFiles( null);
  }

  public FileInterface[] _listFiles(boolean dontShowHidden)
  {
    return _listFiles(null, dontShowHidden);
  }

  public FileInterface[] _listFiles(FileInterfaceFilter filter, boolean dontShowHidden)
  {
    FileInterface[] fileInterfaces = _listFiles( filter);
    ArrayList<FileInterface> interfaces = new ArrayList<FileInterface>();
    
    for(int i = 0; i < fileInterfaces.length; i++)
    {
      FileInterface fileInterface = fileInterfaces[i];
      
      if(!fileInterface.isHidden()) {
        interfaces.add(fileInterface);
      }
    }
    return interfaces.toArray(new FileInterface[interfaces.size()]);
  }

  public Process getCurrentProcess()
  {
    return m_procCurrent;
  }

  public void setCurrentProcess( Process proc)
  {
    m_procCurrent = proc;
  }

  public void killCurrentProcess()
  {
    if( m_procCurrent != null)
    {
      m_procCurrent.destroy();
      m_procCurrent = null;
    }
  }

  /**
   * copy file
   *
   * @param bo: BufferedOutputStream to destination
   */
  public boolean copyFile( BufferedOutputStream bo)
      throws IOException
  {
    return copyFile( bo, null);
  }

  /**
   * copy file
   *
   * @param bo: BufferedOutputStream to destination
   * @param cpd: CancelProgressDialog
   */
  public boolean copyFile( BufferedOutputStream bo, CancelProgressDialog cpd) throws IOException
  {
    BufferedInputStream bi = getBufferedInputStream();
    byte buffer[] = new byte[BUFFERSIZE];
    int bytesRead;
    while( ( bytesRead = bi.read( buffer, 0, BUFFERSIZE) ) > 0)
    {
      bo.write( buffer, 0, bytesRead);
      if( cpd != null)
        cpd.getDetailProgressbar().setValue( cpd.getDetailProgressbar().getValue() + bytesRead);
    }
    bi.close();
    return true;
  }

  public String extractToTempDir()
  {
    String strPath = getTempDirectory();
    char sepChar = '/';
    if( strPath.indexOf( '\\') != -1)
      sepChar = '\\';
    strPath += sepChar + getName();
    try
    {
      File file = new File( strPath);
      if( !file.exists() || ( file.exists() && (file.length() != length() || file.lastModified() < lastModified())))
      {
        BufferedInputStream bis = getBufferedInputStream();
        BufferedOutputStream bos = getBufferedOutputStream( strPath );
        int iLen = 0;
        byte buf[] = new byte[512];

        while( ( iLen = bis.read( buf, 0, 512 ) ) > 0 )
        {
          bos.write( buf, 0, iLen );
        }
        bos.flush();
        bis.close();
        bos.close();
      }
    }
    catch( FileNotFoundException fnfex)
    {
      fnfex.printStackTrace();
      return null;
    }
    catch( IOException ioex)
    {
      ioex.printStackTrace();
    }
    return strPath;
  }

  public int compareTo(Object file)
  {
    // FIXME ignore case for windows
    return getAbsolutePath().compareTo(((FileInterface)file).getAbsolutePath());
  }

  public boolean createNewFile() throws IOException
  {
    // TODO Auto-generated method stub
    return false;
  }

  public void deleteOnExit()
  {
    // TODO Auto-generated method stub
  }

  public boolean setReadOnly()
  {
    // TODO Auto-generated method stub
    return false;
  }

  protected void showNotSupportedMessage()
  {
    JOptionPane.showMessageDialog( GlobalApplicationContext.instance().getRootComponent(),
                                   "This function is not supported for this filetype!",
                                   "Filetype info", JOptionPane.WARNING_MESSAGE);
  }

  protected void showNotSupportedText()
  {
    GlobalApplicationContext.instance().getErrorPrintStream().println( "This function is not supported for this filetype!");
  }
}

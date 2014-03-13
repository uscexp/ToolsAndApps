package haui.io.FileInterface;

import haui.components.AuthorizationDialog;
import haui.components.CancelProgressDialog;
import haui.components.ConnectionManager;
import haui.components.JExDialog;
import haui.components.LRShell;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.CgiTypeFileInterfaceConfiguration;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Module: CgiTypeFile.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\CgiTypeFile.java,v $ <p> Description: Cache for file information.<br> </p> <p> Created: 22.08.2002 by AE </p> <p>
 * @history  22.08.2002 by AE: Created.<br>  </p>  <p>  Modification:<br>  $Log: CgiTypeFile.java,v $ Revision 1.2 2004-08-31 16:03:08+02 t026843 Large redesign  for application dependent outputstreams, mainframes, AppProperties! Bugfixes to  DbTreeTableView, additional features for jDirWork.  Revision 1.1 2004-06-22 14:08:50+02 t026843 bigger changes  Revision 1.0 2003-06-06 10:05:34+02 t026843 Initial revision  </p>  <p>
 * @author  Andreas Eisenhauer  </p>  <p>
 * @version  v1.0, 2002; $Revision: 1.2 $<br>  $Header:  M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\CgiTypeFile.java,v 1.2  2004-08-31 16:03:08+02 t026843 Exp t026843 $  </p>  <p>
 * @since  JDK1.2  </p>
 */
public class CgiTypeFile extends DuFile
// extends File
    implements FileInterface
{
  // constants
  public final static String PROTOCOL = "cgi:";
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
  String m_parentPath;
  Vector m_cgiEntries = new Vector();
  String m_curCgiEntry;
  LRShell m_lrsTerm = null;
  JDialog m_dlgTerm = null;
  boolean m_blLogon = false;

  BufferedOutputStream m_bos = null;

  /**
   * Creates a new CgiTypeFile instance.
   * 
   * @param strCurPath: current path
   */
  private CgiTypeFile( String strPath, boolean blDirectory, char cSeparator, FileInterfaceConfiguration fic)
  {
    super( strPath, blDirectory, cSeparator, fic);
    m_curCgiEntry = super.getName();
    m_blRead = super.canRead();
    m_blWrite = super.canWrite();
    m_strAbsolutePath = super.getAbsolutePath();
    m_strHost = super.getHost();
    m_strPath = super.getAbsolutePath();
    m_strName = super.getName();
    m_strParent = super.getParent();
    if( m_parentPath == null)
    {
      m_parentPath = getAbsolutePath();
      int iIdx = -1;
      if( m_parentPath.length() > 2)
      {
        for( int i = m_parentPath.length() - 3; i >= 0; i--)
        {
          char c = m_parentPath.charAt( i);
          if( c == separatorChar())
          {
            iIdx = i + 1;
            break;
          }
        }
      }
      if( iIdx == -1)
        m_parentPath = null;
      else
        m_parentPath = m_parentPath.substring( 0, iIdx);
    }
    m_blArchive = super.isArchive();
    m_blDirectory = super.isDirectory();
    m_blFile = super.isFile();
    m_blHidden = super.isHidden();
    m_lModified = super.lastModified();
    m_lLength = super.length();
  }

  /**
   * Creates a new CgiTypeFile instance.
   * 
   * @param strCurPath: current path
   */
  public CgiTypeFile( String strCgiPath, String strParentPath, FileInterfaceConfiguration fic)
  {
    super( strCgiPath, fic);
    logon();
    m_intPath = strCgiPath;
    // init data
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)fic).getConnectionManager();
    cm.resetPostParams();
    cm.addPostParam( ACTION, "init");
    cm.addPostParam( PATH, strCgiPath);

    // Send data.
    cm.openConnection();
    cm.getHttpURLConnection().setUseCaches( false);
    cm.post();

    // Read lines from cgi-script.
    String line = null;
    while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    line = cm.readLine();
    m_strPath = line;
    line = cm.readLine();
    m_cSeparator = line.charAt( 0);
    line = cm.readLine();
    m_blRead = line.equals( "0") ? false : true;
    line = cm.readLine();
    m_blWrite = line.equals( "0") ? false : true;
    line = cm.readLine();
    m_blDirectory = line.equals( "0") ? false : true;
    line = cm.readLine();
    m_blArchive = false;
    m_blFile = line.equals( "0") ? false : true;
    line = cm.readLine();
    m_blHidden = line.equals( "0") ? false : true;
    line = cm.readLine();
    m_lLength = ( new Long( line)).longValue();
    line = cm.readLine();
    m_strName = line;
    line = cm.readLine();
    m_strAbsolutePath = line;
    line = cm.readLine();
    if( strParentPath == null)
      m_strParent = line;
    else
    {
      m_parentPath = strParentPath;
      m_strParent = strParentPath;
    }

    int idx = m_strPath.lastIndexOf( separatorChar());
    if( idx > 0 && idx == m_strPath.length() - 1)
      m_strPath = m_strPath.substring( 0, m_strPath.length() - 1);
    idx = m_strAbsolutePath.lastIndexOf( separatorChar());
    if( idx > 0 && idx == m_strAbsolutePath.length() - 1)
      m_strAbsolutePath = m_strAbsolutePath.substring( 0, m_strAbsolutePath.length() - 1);
    idx = m_strParent.lastIndexOf( separatorChar());
    if( idx > 0 && idx == m_strParent.length() - 1)
      m_strParent = m_strParent.substring( 0, m_strParent.length() - 1);

    line = cm.readLine();
    int iRootCount = ( new Integer( line)).intValue();
    m_fiRoots = new CgiTypeFile[iRootCount];
    for( int i = 0; i < iRootCount; i++)
    {
      line = cm.readLine();
      m_fiRoots[i] = new CgiTypeFile( line, true, separatorChar(), getFileInterfaceConfiguration());
    }

    line = cm.readLine();
    // TODO: Convert date from float to long (server)
    m_lModified = 100;
    // m_dateModified = new Date( (new Long( line)).longValue());

    cm.disconnect();

    idx = strCgiPath.lastIndexOf( separatorChar());
    if( idx > 0 && idx == strCgiPath.length() - 1)
      strCgiPath = strCgiPath.substring( 0, strCgiPath.length() - 1);
    m_intPath = strCgiPath;
  }

  public FileInterface duplicate()
  {
    CgiTypeFile ctf = new CgiTypeFile( getAbsolutePath(), isDirectory(), separatorChar(), getFileInterfaceConfiguration());
    return ctf;
  }

  protected String getEntry( String path)
  {
    String ge = null;
    if( !path.equals( ""))
      ge = path;
    return ge;
  }

  public String getId()
  {
    return "Cgi-" + m_strHost;
  }

  /**
   * Creates a new CgiTypeFile instance.
   * 
   * @param strCurPath: current path
   */
  public CgiTypeFile( String strCgiPath, FileInterfaceConfiguration fic) throws IOException
  {
    this( strCgiPath, null, fic);
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

  public void setRoots( CgiTypeFile[] fiRoots)
  {
    m_fiRoots = fiRoots;
  }

  public boolean setLastModified( long time)
  {
    m_lModified = time;
    return true;
  }

  public void setHost( String strHost)
  {
    m_strHost = strHost;
  }

  public BufferedInputStream getBufferedInputStream() throws FileNotFoundException, IOException
  {
    BufferedInputStream bis = null;
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    cm.resetPostParams();
    cm.addPostParam( ACTION, "copyfiletolocal");
    cm.addPostParam( PATH, getAbsolutePath());

    // Send data.
    cm.openConnection();
    cm.getHttpURLConnection().setUseCaches( false);
    cm.post();

    // Read lines from cgi-script.
//    byte buffer[] = new byte[BUFFERSIZE];
//    int iRest = 0;
//    int iRet = 0;
//    int iSum = 0;
//    int iOff = 0;
//    String strRest = "";
    bis = cm.getBufferedInputStream();
    return bis;
  }

  public BufferedOutputStream getBufferedOutputStream( String strNewPath) throws FileNotFoundException
  {
    if( strNewPath.indexOf( getParent()) == -1)
    {
      m_bos = new BufferedOutputStream( new FileOutputStream( strNewPath));
    }
    else
    {
      ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
      cm.resetPostParams();
      cm.addPostParam( ACTION, "setpath");
      cm.addPostParam( PATH, getAbsolutePath());
      cm.addPostParam( PARAM, strNewPath);

      // Send data.
      cm.openConnection();
      cm.getHttpURLConnection().setUseCaches( false);
      cm.post();

      // Read lines from cgi-script.
      String line;
      while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
        ;
      line = cm.readLine();
      boolean blRet = line.equals( "0") ? false : true;
      cm.disconnect();

      if( blRet)
      {
        cm.resetPostParams();
        cm.addPostParam( ACTION, "copytoremote");
        cm.addPostParam( PATH, getAbsolutePath());
        cm.addPostParam( PARAM, strNewPath);

        // Send data.
        cm.openConnection();
        cm.getHttpURLConnection().setUseCaches( false);
        cm.getHttpURLConnection().setRequestProperty( "Content-type", ConnectionManager.BINDATA_CONTENTTYPE);
        m_bos = cm.getBufferedOutputStream();
        /*
         * cm.post( bi);
         * 
         * while( !(line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF)) ; line =
         * cm.readLine(); blRet = line.equals( "0") ? false : true; bi.close(); cm.disconnect();
         */
      }
    }
    return m_bos;
  }

  /**
   * copy file
   * 
   * @param bo: BufferedOutputStream to destination
   */
  public boolean copyFile( BufferedOutputStream bo) throws IOException
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
    boolean blRet = false;
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    cm.post( bi);

    String line;
    while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
    {
      if( cpd != null)
        cpd.getDetailProgressbar().setValue( cpd.getDetailProgressbar().getValue() + line.length());
    }
    line = cm.readLine();
    blRet = line.equals( "0") ? false : true;
    bi.close();
    cm.disconnect();

    return blRet;
  }

  public FileInterface[] _listRoots()
  {
    return m_fiRoots;
  }

  public char separatorChar()
  {
    return m_cSeparator;
  }

  public char pathSeparatorChar()
  {
    // TODO extend remote interface
    return ';';
  }

  public boolean canRead()
  {
    return m_blRead;
  }

  public boolean canWrite()
  {
    return m_blWrite;
  }

  public boolean isDirectory()
  {
    return m_blDirectory;
  }

  public boolean isArchive()
  {
    return m_blArchive;
  }

  public boolean isFile()
  {
    return m_blFile;
  }

  public boolean isHidden()
  {
    return m_blHidden;
  }

  public URL toURL()
  {
    URL url = null;
    url = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager().getURL();
    return url;
  }

  public long length()
  {
    return m_lLength;
  }

  public String getName()
  {
    if( m_strName == null)
      // nessecary for the initialisation
      return super.getName();
    else
      return m_strName;
  }

  public String getAbsolutePath()
  {
    return m_strAbsolutePath;
  }

  public FileInterface getCanonicalFile() throws IOException
  {
    return new CgiTypeFile( m_curCgiEntry, m_parentPath, getFileInterfaceConfiguration());
  }

  public String getPath()
  {
    return m_strPath;
  }

  public FileInterface getDirectAccessFileInterface()
  {
    FileInterface fi = this;
    String strPath = getAbsolutePath();

    int iStat;
    iStat = JOptionPane.showConfirmDialog( GlobalApplicationContext.instance().getRootComponent(),
        "Extract to temp and execute?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if( iStat == JOptionPane.NO_OPTION)
    {
      GlobalApplicationContext.instance().getOutputPrintStream().println( "...cancelled");
      // System.out.println( "...cancelled" );
      return null;
    }
    strPath = extractToTempDir();
    if( strPath != null)
      fi = FileConnector.createFileInterface( strPath, null, false, getFileInterfaceConfiguration());
    return fi;
  }

  public String getParent()
  {
    return m_strParent;
  }

  public FileInterface getParentFileInterface()
  {
    boolean blExtract = false;
    if( m_intPath != null && !m_intPath.equals( "") && m_intPath.indexOf( separatorChar()) != -1)
      blExtract = true;
    FileInterface fi = FileConnector.createFileInterface( getParent(), null, blExtract, getFileInterfaceConfiguration());
    return fi;
  }

  public long lastModified()
  {
    return m_lModified;
  }

  public String[] list()
  {
    if( m_strList != null)
      return m_strList;
    m_strList = new String[0];
    if( isDirectory() || ( m_curCgiEntry == null && isArchive()))
    {
      if( m_fiList == null)
        _listFiles();
      m_strList = new String[Array.getLength( m_fiList)];
      for( int i = 0; i < Array.getLength( m_fiList); ++i)
      {
        m_strList[i] = m_fiList[i].getAbsolutePath();
      }
    }
    return m_strList;
  }

  public FileInterface[] _listFiles( FileInterfaceFilter filter)
  {
    // if( m_fiList != null)
    // return m_fiList;
    CgiTypeFile[] cgis = new CgiTypeFile[0];
    Vector vec = null;
    m_fiList = (FileInterface[])cgis;
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    if( isDirectory() || ( m_curCgiEntry == null && isArchive()))
    {
      cm.resetPostParams();
      cm.addPostParam( ACTION, "initFileInfoArray");
      cm.addPostParam( PATH, getAbsolutePath());

      // Send data.
      cm.openConnection();
      cm.getHttpURLConnection().setUseCaches( false);
      cm.post();

      // Read lines from cgi-script.
      String line = null;
      //int iIdx = -1;
      int iCount = 0;
      while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
        ;
      line = cm.readLine();
      iCount = ( new Integer( line)).intValue();
      if( filter == null)
        cgis = new CgiTypeFile[iCount];
      else
        vec = new Vector();
      for( int iCf = 0; iCf < iCount; iCf++)
      {
        line = cm.readLine();
        CgiTypeFile cgiFile = new CgiTypeFile( line, true, separatorChar(), getFileInterfaceConfiguration());
        cgiFile.setPath( line);
        line = cm.readLine();
        cgiFile.setSeperatorChar( line.charAt( 0));
        line = cm.readLine();
        cgiFile.setReadable( line.equals( "0") ? false : true);
        line = cm.readLine();
        cgiFile.setWriteable( line.equals( "0") ? false : true);
        line = cm.readLine();
        cgiFile.setDirectory( line.equals( "0") ? false : true);
        line = cm.readLine();
        cgiFile.setFile( line.equals( "0") ? false : true);
        cgiFile.setArchive( false);
        line = cm.readLine();
        cgiFile.setHidden( line.equals( "0") ? false : true);
        line = cm.readLine();
        cgiFile.setLength( ( new Long( line)).longValue());
        line = cm.readLine();
        cgiFile.setName( line);
        line = cm.readLine();
        cgiFile.setAbsolutePath( line);
        line = cm.readLine();
        cgiFile.setParent( line);
        cgiFile.setHost( cm.getURL().getHost());

        line = cm.readLine();
        int iRootCount = ( new Integer( line)).intValue();
        CgiTypeFile[] fiRoots = new CgiTypeFile[iRootCount];
        for( int i = 0; i < iRootCount; i++)
        {
          line = cm.readLine();
          fiRoots[i] = new CgiTypeFile( line, true, separatorChar(), getFileInterfaceConfiguration());
        }
        cgiFile.setRoots( fiRoots);

        line = cm.readLine();
        // TODO: Convert date from float to long (server)
        cgiFile.setLastModified( 100);
        // cgiFile.setLastModified( new Date( (new Long( line)).longValue()));
        if( filter == null)
          cgis[iCf] = cgiFile;
        else
        {
          if( filter.accept( cgiFile))
            vec.add( cgiFile);
        }
      }

      cm.disconnect();
    }
    if( filter != null)
    {
      if( vec != null && vec.size() > 0)
      {
        cgis = new CgiTypeFile[vec.size()];
        for( int j = 0; j < vec.size(); ++j)
          cgis[j] = (CgiTypeFile)vec.elementAt( j);
      }
    }
    m_fiList = (FileInterface[])cgis;
    return m_fiList;
  }

  public FileInterface[] _listFiles()
  {
    if( m_fiList != null)
      return m_fiList;
    CgiTypeFile[] cgis = new CgiTypeFile[0];
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    if( isDirectory() || ( m_curCgiEntry == null && isArchive()))
    {
      cm.resetPostParams();
      cm.addPostParam( ACTION, "initFileInfoArray");
      cm.addPostParam( PATH, getAbsolutePath());

      // Send data.
      cm.openConnection();
      cm.getHttpURLConnection().setUseCaches( false);
      cm.post();

      // Read lines from cgi-script.
      String line = null;
      //int iIdx = -1;
      int iCount = 0;
      while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
        ;
      line = cm.readLine();
      iCount = ( new Integer( line)).intValue();
      cgis = new CgiTypeFile[iCount];
      for( int iCf = 0; iCf < iCount; iCf++)
      {
        line = cm.readLine();
        cgis[iCf] = new CgiTypeFile( line, true, separatorChar(), getFileInterfaceConfiguration());
        cgis[iCf].setPath( line);
        line = cm.readLine();
        cgis[iCf].setSeperatorChar( line.charAt( 0));
        line = cm.readLine();
        cgis[iCf].setReadable( line.equals( "0") ? false : true);
        line = cm.readLine();
        cgis[iCf].setWriteable( line.equals( "0") ? false : true);
        line = cm.readLine();
        cgis[iCf].setDirectory( line.equals( "0") ? false : true);
        line = cm.readLine();
        cgis[iCf].setFile( line.equals( "0") ? false : true);
        cgis[iCf].setArchive( false);
        line = cm.readLine();
        cgis[iCf].setHidden( line.equals( "0") ? false : true);
        line = cm.readLine();
        cgis[iCf].setLength( ( new Long( line)).longValue());
        line = cm.readLine();
        cgis[iCf].setName( line);
        line = cm.readLine();
        cgis[iCf].setAbsolutePath( line);
        line = cm.readLine();
        cgis[iCf].setParent( line);
        cgis[iCf].setHost( cm.getURL().getHost());

        line = cm.readLine();
        int iRootCount = ( new Integer( line)).intValue();
        CgiTypeFile[] fiRoots = new CgiTypeFile[iRootCount];
        for( int i = 0; i < iRootCount; i++)
        {
          line = cm.readLine();
          fiRoots[i] = new CgiTypeFile( line, true, separatorChar(), getFileInterfaceConfiguration());
        }
        cgis[iCf].setRoots( fiRoots);

        line = cm.readLine();
        // TODO: Convert date from float to long (server)
        cgis[iCf].setLastModified( 100);
        // cgis[iCf].setLastModified( new Date( (new Long( line)).longValue()));
      }

      cm.disconnect();
    }
    m_fiList = (FileInterface[])cgis;
    return m_fiList;
  }

  public boolean renameTo( FileInterface file)
  {
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    cm.resetPostParams();
    cm.addPostParam( ACTION, "renameTo");
    cm.addPostParam( PATH, getAbsolutePath());
    cm.addPostParam( PARAM, file.getAbsolutePath());

    // Send data.
    cm.openConnection();
    cm.getHttpURLConnection().setUseCaches( false);
    cm.post();

    // Read lines from cgi-script.
    String line = null;
    while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    line = cm.readLine();
    boolean blRet = line.equals( "0") ? false : true;
    if( blRet)
    {
      line = cm.readLine();
      m_strPath = line;
      line = cm.readLine();
      m_strName = line;
      line = cm.readLine();
      // TODO: Convert date from float to long (server)
      m_lModified = 100;
      // m_dateModified = new Date( (new Long( line)).longValue());
    }
    cm.disconnect();
    return blRet;
  }

  public boolean delete()
  {
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    cm.resetPostParams();
    cm.addPostParam( ACTION, "deletedir");
    cm.addPostParam( PATH, getAbsolutePath());

    // Send data.
    cm.openConnection();
    cm.getHttpURLConnection().setUseCaches( false);
    cm.post();

    // Read lines from cgi-script.
    String line = null;
    while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    line = cm.readLine();
    boolean blRet = line.equals( "0") ? false : true;
    cm.disconnect();
    return blRet;
  }

  public boolean mkdir()
  {
    m_fiList = null;
    m_strList = null;
    m_blArchive = false;
    m_blDirectory = true;
    m_blFile = false;
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    cm.resetPostParams();
    cm.addPostParam( ACTION, "mkdir");
    cm.addPostParam( PATH, getParent());
    cm.addPostParam( PARAM, getName());

    // Send data.
    cm.openConnection();
    cm.getHttpURLConnection().setUseCaches( false);
    cm.post();

    // Read lines from cgi-script.
    String line = null;
    while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    line = cm.readLine();
    boolean blRet = line.equals( "0") ? false : true;
    if( blRet)
    {
      line = cm.readLine();
      m_strPath = line;
    }
    cm.disconnect();
    return blRet;
  }

  public boolean exists()
  {
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    cm.resetPostParams();
    cm.addPostParam( ACTION, "exists");
    cm.addPostParam( PATH, getParent());
    cm.addPostParam( PARAM, getName());

    // Send data.
    cm.openConnection();
    cm.getHttpURLConnection().setUseCaches( false);
    cm.post();

    // Read lines from cgi-script.
    String line = null;
    while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    line = cm.readLine();
    boolean blRet = line.equals( "0") ? false : true;
    cm.disconnect();
    return blRet;
  }

  public void logon()
  {
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    if( getFileInterfaceConfiguration().getAppProperties().getProperty( GlobalApplicationContext.LOGONUSER) == null
        || getFileInterfaceConfiguration().getAppProperties().getProperty( GlobalApplicationContext.LOGONUSER).equals( ""))
    {
      m_blLogon = false;
      return;
    }
    else
      m_blLogon = true;
    cm.resetPostParams();
    cm.addPostParam( CgiTypeFile.ACTION, "logon");
    String strPass = getFileInterfaceConfiguration().getAppProperties().getProperty( GlobalApplicationContext.LOGONPASSWORD);
    if( strPass == null)
    {
      AuthorizationDialog authDlg = new AuthorizationDialog( null, "Authorization", true, getFileInterfaceConfiguration().getAppProperties(),
          AuthorizationDialog.CGIAUTH, getFileInterfaceConfiguration().getAppName());
      authDlg.setRequestText( new String( "CGI authorization for ") + cm.getURL().getHost());
      authDlg.setVisible( true);
      strPass = getFileInterfaceConfiguration().getAppProperties().getProperty( GlobalApplicationContext.LOGONPASSWORD);
    }

    if( getFileInterfaceConfiguration().getAppProperties().getProperty( GlobalApplicationContext.LOGONUSER) == null
        || getFileInterfaceConfiguration().getAppProperties().getProperty( GlobalApplicationContext.LOGONUSER).equals( ""))
    {
      m_blLogon = false;
      return;
    }
    else
      m_blLogon = true;
    cm.addPostParam( CgiTypeFile.PARAM, getFileInterfaceConfiguration().getAppProperties().getProperty( GlobalApplicationContext.LOGONUSER));
    cm.addPostParam( CgiTypeFile.PATH, strPass);

    // Send data.
    cm.openConnection();
    try
    {
      cm.getHttpURLConnection().setRequestMethod( "POST");
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    cm.getHttpURLConnection().setUseCaches( false);
    cm.post();

    // Read lines from cgi-script.
    readToStdOut( cm, getFileInterfaceConfiguration().getAppName());
    cm.disconnect();
    return;
  }

  public void logoff()
  {
    if( !m_blLogon)
      return;
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    cm.resetPostParams();
    cm.addPostParam( ACTION, "logoff");

    // Send data.
    cm.openConnection();
    try
    {
      cm.getHttpURLConnection().setRequestMethod( "POST");
    }
    catch( ProtocolException pex)
    {
      pex.printStackTrace();
    }
    cm.getHttpURLConnection().setUseCaches( false);
    cm.post();

    // Read lines from cgi-script.
    readToStdOut( cm, getFileInterfaceConfiguration().getAppName());
    cm.disconnect();
    // dispose treminal instance
    if( m_dlgTerm != null)
    {
      m_dlgTerm.dispose();
      m_dlgTerm = null;
    }
    return;
  }

  /**
   * read remote std out (temp output file) and print it to system out
   * 
   * @param cm ConnectionManager to read from
   */
  private static void readToStdOut( ConnectionManager cm, String strAppName)
  {
    String line = null;
    int iIdx = -1;
    while( !( line = cm.readLine()).equalsIgnoreCase( ConnectionManager.SOF))
      ;
    while( ( line = cm.readLine()) != null)
    {
      if( ( iIdx = line.indexOf( "<EOF>")) != -1)
      {
        line = line.substring( 0, iIdx);
        GlobalApplicationContext.instance().getOutputPrintStream().println( line);
        break;
      }
      GlobalApplicationContext.instance().getOutputPrintStream().println( line);
      // System.out.println( line);
    }
  }

  public void startTerminal()
  {
    if( m_lrsTerm == null)
    {
      m_lrsTerm = new LRShell( getFileInterfaceConfiguration().getAppProperties());
      m_lrsTerm.init();
    }
    if( m_dlgTerm == null)
    {
      m_dlgTerm = new JExDialog( null, "LRShell - " + getId(), false, getAppName());
      m_dlgTerm.getContentPane().add( "Center", m_lrsTerm);
      m_lrsTerm.setTopComponent( m_dlgTerm);
      m_dlgTerm.pack();
    }
    if( !m_dlgTerm.isVisible())
      m_dlgTerm.setVisible( true);
  }

  /**
   * ececute command
   * 
   * @param strCgiPath traget path
   * @param cmd CommandClass
   */
  public int exec( String strCgiPath, CommandClass cmd, OutputStream osOut, OutputStream osErr, InputStream is)
  {
    //int iStat;
    int iRet = -1;
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    if( !cmd.getExecLocal())
    {
      String strCmd = cmd.getCompleteCommand( this, getParent(), strCgiPath);
      cm.resetPostParams();
      cm.addPostParam( ACTION, "exec");
      cm.addPostParam( PATH, getParent());
      cm.addPostParam( PARAM, strCmd);

      // Send data.
      cm.openConnection();
      cm.getHttpURLConnection().setUseCaches( false);
      cm.post();

      // Read lines from cgi-script.
      readToStdOut( cm, getFileInterfaceConfiguration().getAppName());
      cm.disconnect();
      iRet = 0;
    }
    else
    {
      FileInterface fi = this;
      fi = getDirectAccessFileInterface();
      try
      {
        iRet = FileConnector.exec( fi, cmd.getCompleteCommand( fi, fi.getAbsolutePath(), strCgiPath), strCgiPath, cmd, osOut, osErr, is);
      }
      catch( IOException ex)
      {
        ex.printStackTrace();
      }
    }
    return iRet;
  }

  public Object getConnObj()
  {
    return ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
  }
}
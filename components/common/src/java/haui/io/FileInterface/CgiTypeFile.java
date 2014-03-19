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
import java.util.ArrayList;
import java.util.List;
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
  String parentPath;
  List<String> cgiEntries = new ArrayList<>();
  String curCgiEntry;
  LRShell lrsTerm = null;
  JDialog dlgTerm = null;
  boolean logon = false;

  BufferedOutputStream m_bos = null;

  /**
   * Creates a new CgiTypeFile instance.
   * 
   * @param strCurPath: current path
   */
  private CgiTypeFile( String strPath, boolean blDirectory, char cSeparator, FileInterfaceConfiguration fic)
  {
    super( strPath, blDirectory, cSeparator, fic);
    curCgiEntry = super.getName();
    read = super.canRead();
    write = super.canWrite();
    absolutePath = super.getAbsolutePath();
    host = super.getHost();
    strPath = super.getAbsolutePath();
    name = super.getName();
    parent = super.getParent();
    if( parentPath == null)
    {
      parentPath = getAbsolutePath();
      int iIdx = -1;
      if( parentPath.length() > 2)
      {
        for( int i = parentPath.length() - 3; i >= 0; i--)
        {
          char c = parentPath.charAt( i);
          if( c == separatorChar())
          {
            iIdx = i + 1;
            break;
          }
        }
      }
      if( iIdx == -1)
        parentPath = null;
      else
        parentPath = parentPath.substring( 0, iIdx);
    }
    archive = super.isArchive();
    blDirectory = super.isDirectory();
    fileType = super.isFile();
    hidden = super.isHidden();
    modified = super.lastModified();
    length = super.length();
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
    intPath = strCgiPath;
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
    path = line;
    line = cm.readLine();
    separator = line.charAt( 0);
    line = cm.readLine();
    read = line.equals( "0") ? false : true;
    line = cm.readLine();
    write = line.equals( "0") ? false : true;
    line = cm.readLine();
    directory = line.equals( "0") ? false : true;
    line = cm.readLine();
    archive = false;
    fileType = line.equals( "0") ? false : true;
    line = cm.readLine();
    hidden = line.equals( "0") ? false : true;
    line = cm.readLine();
    length = ( new Long( line)).longValue();
    line = cm.readLine();
    name = line;
    line = cm.readLine();
    absolutePath = line;
    line = cm.readLine();
    if( strParentPath == null)
      parent = line;
    else
    {
      parentPath = strParentPath;
      parent = strParentPath;
    }

    int idx = path.lastIndexOf( separatorChar());
    if( idx > 0 && idx == path.length() - 1)
      path = path.substring( 0, path.length() - 1);
    idx = absolutePath.lastIndexOf( separatorChar());
    if( idx > 0 && idx == absolutePath.length() - 1)
      absolutePath = absolutePath.substring( 0, absolutePath.length() - 1);
    idx = parent.lastIndexOf( separatorChar());
    if( idx > 0 && idx == parent.length() - 1)
      parent = parent.substring( 0, parent.length() - 1);

    line = cm.readLine();
    int iRootCount = ( new Integer( line)).intValue();
    fileInterfaceRoots = new CgiTypeFile[iRootCount];
    for( int i = 0; i < iRootCount; i++)
    {
      line = cm.readLine();
      fileInterfaceRoots[i] = new CgiTypeFile( line, true, separatorChar(), getFileInterfaceConfiguration());
    }

    line = cm.readLine();
    // TODO: Convert date from float to long (server)
    modified = 100;
    // m_dateModified = new Date( (new Long( line)).longValue());

    cm.disconnect();

    idx = strCgiPath.lastIndexOf( separatorChar());
    if( idx > 0 && idx == strCgiPath.length() - 1)
      strCgiPath = strCgiPath.substring( 0, strCgiPath.length() - 1);
    intPath = strCgiPath;
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
    return "Cgi-" + host;
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
    separator = cSeperator;
  }

  public void setReadable( boolean blRead)
  {
    blRead = blRead;
  }

  public void setWriteable( boolean blWrite)
  {
    blWrite = blWrite;
  }

  public void setDirectory( boolean blDirectory)
  {
    blDirectory = blDirectory;
  }

  public void setArchive( boolean blArchive)
  {
    blArchive = blArchive;
  }

  public void setFile( boolean blFile)
  {
    blFile = blFile;
  }

  public void setHidden( boolean blHidden)
  {
    blHidden = blHidden;
  }

  public void setLength( long lLength)
  {
    length = lLength;
  }

  public void setName( String strName)
  {
    strName = strName;
  }

  public void setParent( String strParent)
  {
    strParent = strParent;
  }

  public void setPath( String strPath)
  {
    strPath = strPath;
  }

  public void setAbsolutePath( String strAbsolutePath)
  {
    strAbsolutePath = strAbsolutePath;
  }

  public void setRoots( CgiTypeFile[] fiRoots)
  {
    fileInterfaceRoots = fiRoots;
  }

  public boolean setLastModified( long time)
  {
    modified = time;
    return true;
  }

  public void setHost( String strHost)
  {
    host = strHost;
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
    return fileInterfaceRoots;
  }

  public char separatorChar()
  {
    return separator;
  }

  public char pathSeparatorChar()
  {
    // TODO extend remote interface
    return ';';
  }

  public boolean canRead()
  {
    return read;
  }

  public boolean canWrite()
  {
    return write;
  }

  public boolean isDirectory()
  {
    return directory;
  }

  public boolean isArchive()
  {
    return archive;
  }

  public boolean isFile()
  {
    return fileType;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  public URL toURL()
  {
    URL url = null;
    url = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager().getURL();
    return url;
  }

  public long length()
  {
    return length;
  }

  public String getName()
  {
    if( name == null)
      // nessecary for the initialisation
      return super.getName();
    else
      return name;
  }

  public String getAbsolutePath()
  {
    return absolutePath;
  }

  public FileInterface getCanonicalFile() throws IOException
  {
    return new CgiTypeFile( curCgiEntry, parentPath, getFileInterfaceConfiguration());
  }

  public String getPath()
  {
    return path;
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
    return parent;
  }

  public FileInterface getParentFileInterface()
  {
    boolean blExtract = false;
    if( intPath != null && !intPath.equals( "") && intPath.indexOf( separatorChar()) != -1)
      blExtract = true;
    FileInterface fi = FileConnector.createFileInterface( getParent(), null, blExtract, getFileInterfaceConfiguration());
    return fi;
  }

  public long lastModified()
  {
    return modified;
  }

  public String[] list()
  {
    if( list != null)
      return list;
    list = new String[0];
    if( isDirectory() || ( curCgiEntry == null && isArchive()))
    {
      if( fileInterfaces == null)
        _listFiles();
      list = new String[Array.getLength( fileInterfaces)];
      for( int i = 0; i < Array.getLength( fileInterfaces); ++i)
      {
        list[i] = fileInterfaces[i].getAbsolutePath();
      }
    }
    return list;
  }

  public FileInterface[] _listFiles( FileInterfaceFilter filter)
  {
    // if( m_fiList != null)
    // return m_fiList;
    CgiTypeFile[] cgis = new CgiTypeFile[0];
    Vector vec = null;
    fileInterfaces = (FileInterface[])cgis;
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    if( isDirectory() || ( curCgiEntry == null && isArchive()))
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
    fileInterfaces = (FileInterface[])cgis;
    return fileInterfaces;
  }

  public FileInterface[] _listFiles()
  {
    if( fileInterfaces != null)
      return fileInterfaces;
    CgiTypeFile[] cgis = new CgiTypeFile[0];
    ConnectionManager cm = ( (CgiTypeFileInterfaceConfiguration)getFileInterfaceConfiguration()).getConnectionManager();
    if( isDirectory() || ( curCgiEntry == null && isArchive()))
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
    fileInterfaces = (FileInterface[])cgis;
    return fileInterfaces;
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
      path = line;
      line = cm.readLine();
      name = line;
      line = cm.readLine();
      // TODO: Convert date from float to long (server)
      modified = 100;
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
    fileInterfaces = null;
    list = null;
    archive = false;
    directory = true;
    fileType = false;
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
      path = line;
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
      logon = false;
      return;
    }
    else
      logon = true;
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
      logon = false;
      return;
    }
    else
      logon = true;
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
    if( !logon)
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
    if( dlgTerm != null)
    {
      dlgTerm.dispose();
      dlgTerm = null;
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
    if( lrsTerm == null)
    {
      lrsTerm = new LRShell( getFileInterfaceConfiguration().getAppProperties());
      lrsTerm.init();
    }
    if( dlgTerm == null)
    {
      dlgTerm = new JExDialog( null, "LRShell - " + getId(), false, getAppName());
      dlgTerm.getContentPane().add( "Center", lrsTerm);
      lrsTerm.setTopComponent( dlgTerm);
      dlgTerm.pack();
    }
    if( !dlgTerm.isVisible())
      dlgTerm.setVisible( true);
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
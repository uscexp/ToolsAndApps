package haui.io.FileInterface;

import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.remote.RemoteRequestObject;
import haui.io.FileInterface.remote.RemoteResponseObject;
import haui.util.AppProperties;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.JOptionPane;

/**
 * Module:      ClientTypeFile.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\ClientTypeFile.java,v $
 *<p>
 * Description: abstract base class for ClientTypeFiles.<br>
 *</p><p>
 * Created:	    19.05.2004  by AE
 *</p><p>
 * @history     19.05.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: ClientTypeFile.java,v $
 * Revision 1.1  2004-08-31 16:03:19+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:54+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\ClientTypeFile.java,v 1.1 2004-08-31 16:03:19+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public abstract class ClientTypeFile
  extends BaseTypeFile
{
  boolean m_blInit = false;

  // cache variables
  char m_cSeparator = ' ';
  char m_cPathSeparator = ' ';

  public ClientTypeFile( FileInterfaceConfiguration fic)
  {
    super( fic);
    super._init();
  }

  protected void initUpDummy( String strName, String strParent, char cSeparator, char cPathSeperator)
  {
    m_strName = strName;
    m_strParent = strParent;
    m_strAbsolutePath = m_strParent + cSeparator + strName;
    m_blArchive = false;
    m_blDirectory = true;
    m_blFile = false;
    m_blRead = true;
    m_blWrite = true;
    m_cSeparator = cSeparator;
    m_cPathSeparator = cPathSeperator;
    m_blInit = true;
  }

  public abstract void connect();

  public abstract void disconnect();

  public abstract void closeTransfer();

  public abstract RemoteResponseObject readResponseObject();

  public abstract void sendRequestObject( RemoteRequestObject rro);

  public boolean init( String strPath)
  {
    if( strPath == null)
      return false;
    RemoteRequestObject request = new RemoteRequestObject( "init");
    request.addParam( strPath);
    sendRequestObject( request);
    RemoteResponseObject response = readResponseObject();
    Boolean blRet = null;

    if( response != null)
      blRet = (Boolean)response.getObject();
    if( blRet.booleanValue())
    {
      m_strAbsolutePath = getAbsolutePath();
      m_strPath = getPath();
      m_strParent = getParent();
      m_strName = getName();
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

    return blRet.booleanValue();
  }

  public boolean resetPath()
  {
    if( m_strAbsolutePath == null)
      return false;
    RemoteRequestObject request = new RemoteRequestObject( "init");
    request.addParam( m_strAbsolutePath);
    sendRequestObject( request);
    RemoteResponseObject response = readResponseObject();
    Boolean blRet = null;

    if( response != null)
      blRet = (Boolean)response.getObject();

    return blRet.booleanValue();
  }

  public AppProperties getAppProperties()
  {
    return getFileInterfaceConfiguration().getAppProperties();
  }

  public String getTempDirectory()
  {
    /*
    sendRequestObject( new RemoteRequestObject( "getTempDirectory"));
    RemoteResponseObject response = readResponseObject();
    String strRet = null;

    if( response != null)
      strRet = (String)response.getObject();

    return strRet;
    */
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

  public BufferedInputStream getBufferedInputStream()
    throws FileNotFoundException, IOException
  {
    resetPath();
    closeTransfer();
    BufferedInputStream bisRet = null;

    sendRequestObject( new RemoteRequestObject( "getBufferedInputStream"));
    RemoteResponseObject response = readResponseObject();
    Boolean bl = null;

    if( response != null)
      bl = (Boolean)response.getObject();
    if( bl != null && bl.booleanValue())
    {
      bisRet = getRealInputStream();
    }

    return bisRet;
  }

  protected abstract BufferedInputStream getRealInputStream();

  public BufferedOutputStream getBufferedOutputStream(String strNewPath)
    throws FileNotFoundException
  {
    resetPath();
    closeTransfer();
    BufferedOutputStream bosRet = null;

    RemoteRequestObject request = new RemoteRequestObject( "getBufferedOutputStream");
    request.addParam( strNewPath);
    sendRequestObject( request);
    RemoteResponseObject response = readResponseObject();
    Boolean bl = null;

    if( response != null)
      bl = (Boolean)response.getObject();
    if( bl != null && bl.booleanValue())
    {
      bosRet = getRealOutputStream();
    }

    return bosRet;
  }

  protected abstract BufferedOutputStream getRealOutputStream();

  public abstract FileInterface duplicate();

  public FileInterface[] _listRoots()
  {
    if( !isCached() || m_fiRoots == null) // only because of the performance!!
    {
      resetPath();
      sendRequestObject( new RemoteRequestObject( "_listRoots" ) );
      RemoteResponseObject response = readResponseObject();

      if( response != null )
        m_fiRoots = (haui.io.FileInterface.FileInterface[] )response.getObject();
    }

    return m_fiRoots;
  }

  public boolean isCached()
  {
    return getFileInterfaceConfiguration().isCached();
  }

  public char separatorChar()
  {
    Character cRet = null;
    if( !isCached() || m_cSeparator == ' ')
    {
      sendRequestObject( new RemoteRequestObject( "separatorChar" ) );
      RemoteResponseObject response = readResponseObject();

      if( response != null )
        cRet = ( Character )response.getObject();
    }
    else
      return m_cSeparator;

    return cRet.charValue();
  }

  public char pathSeparatorChar()
  {
    Character cRet = null;
    if( !isCached() || m_cPathSeparator == ' ')
    {
      sendRequestObject( new RemoteRequestObject( "pathSeparatorChar" ) );
      RemoteResponseObject response = readResponseObject();

      if( response != null )
        cRet = ( Character )response.getObject();
    }
    else
      return m_cPathSeparator;

    return cRet.charValue();
  }

  public boolean canRead()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "canRead" ) );
      RemoteResponseObject response = readResponseObject();
      Boolean blRet = null;

      if( response != null )
        blRet = ( Boolean )response.getObject();

      return blRet.booleanValue();
    }
    else
      return m_blRead;
  }

  public boolean canWrite()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "canWrite" ) );
      RemoteResponseObject response = readResponseObject();
      Boolean blRet = null;

      if( response != null )
        blRet = ( Boolean )response.getObject();

      return blRet.booleanValue();
    }
    else
      return m_blWrite;
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

  public boolean isDirectory()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "isDirectory" ) );
      RemoteResponseObject response = readResponseObject();
      Boolean blRet = null;

      if( response != null )
        blRet = ( Boolean )response.getObject();

      return blRet.booleanValue();
    }
    else
      return m_blDirectory;
  }

  public boolean isArchive()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "isArchive" ) );
      RemoteResponseObject response = readResponseObject();
      Boolean blRet = null;

      if( response != null )
        blRet = ( Boolean )response.getObject();

      return blRet.booleanValue();
    }
    else
      return m_blArchive;
  }

  public boolean isFile()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "isFile" ) );
      RemoteResponseObject response = readResponseObject();
      Boolean blRet = null;

      if( response != null )
        blRet = ( Boolean )response.getObject();

      return blRet.booleanValue();
    }
    else
      return m_blFile;
  }

  public boolean isHidden()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "isHidden" ) );
      RemoteResponseObject response = readResponseObject();
      Boolean blRet = null;

      if( response != null )
        blRet = ( Boolean )response.getObject();

      return blRet.booleanValue();
    }
    else
      return m_blHidden;
  }

  public URL toURL()
  {
    resetPath();
    sendRequestObject( new RemoteRequestObject( "toURL"));
    RemoteResponseObject response = readResponseObject();
    URL urlRet = null;

    if( response != null)
      urlRet = (URL)response.getObject();

    return urlRet;
  }

  public long length()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "length" ) );
      RemoteResponseObject response = readResponseObject();
      Long lRet = null;

      if( response != null )
        lRet = ( Long )response.getObject();

      return lRet.longValue();
    }
    else
      return m_lLength;
  }

  public abstract String getId();

  public abstract String getHost();

  public String getName()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "getName" ) );
      RemoteResponseObject response = readResponseObject();
      String strRet = null;

      if( response != null )
        strRet = ( String )response.getObject();

      return strRet;
    }
    else
      return m_strName;
  }

  public void setName( String strName)
  {
    m_strName = strName;
  }

  public String getAbsolutePath()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "getAbsolutePath" ) );
      RemoteResponseObject response = readResponseObject();
      String strRet = null;

      if( response != null )
        strRet = ( String )response.getObject();

      return strRet;
    }
    else
      return m_strAbsolutePath;
  }

  public String getPath()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "getPath" ) );
      RemoteResponseObject response = readResponseObject();
      String strRet = null;

      if( response != null )
        strRet = ( String )response.getObject();

      return strRet;
    }
    else
      return m_strPath;
  }

  public String getInternalPath()
  {
    resetPath();
    sendRequestObject( new RemoteRequestObject( "getInternalPath" ) );
    RemoteResponseObject response = readResponseObject();
    String strRet = null;

    if( response != null )
      strRet = ( String )response.getObject();

    return strRet;
  }

  public FileInterface getDirectAccessFileInterface()
  {
    FileInterface fi = this;
    String strPath = getAbsolutePath();

    if( getInternalPath() != null && !getInternalPath().equals( ""))
    {
      int iStat;
      iStat = JOptionPane.showConfirmDialog( GlobalApplicationContext.instance().getRootComponent(),
                                             "Extract to temp and execute?", "Confirmation",
                                             JOptionPane.YES_NO_OPTION,
                                             JOptionPane.QUESTION_MESSAGE );
      if( iStat == JOptionPane.NO_OPTION )
      {
        GlobalApplicationContext.instance().getOutputPrintStream().println( "...cancelled");
        //System.out.println( "...cancelled" );
        return null;
      }
      strPath = extractToTempDir();
      if( strPath != null)
        fi = FileConnector.createFileInterface( strPath, null, false, getFileInterfaceConfiguration());
    }
    return fi;
  }

  public String getParent()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "getParent" ) );
      RemoteResponseObject response = readResponseObject();
      String strRet = null;

      if( response != null )
        strRet = ( String )response.getObject();

      return strRet;
    }
    else
      return m_strParent;
  }

  public FileInterface getParentFileInterface()
  {
    resetPath();
    sendRequestObject( new RemoteRequestObject( "getParentFileInterface"));
    RemoteResponseObject response = readResponseObject();
    FileInterface fiRet = null;

    if( response != null)
      fiRet = (FileInterface)response.getObject();

    return fiRet;
  }

  public long lastModified()
  {
    if( !m_blInit)
    {
      sendRequestObject( new RemoteRequestObject( "lastModified" ) );
      RemoteResponseObject response = readResponseObject();
      Long lRet = null;

      if( response != null )
        lRet = ( Long )response.getObject();

      return lRet.longValue();
    }
    else
      return m_lModified;
  }

  public boolean setLastModified( long time)
  {
    if( !m_blInit)
    {
      RemoteRequestObject request = new RemoteRequestObject( "setLastModified" );
      request.addParam( new Long( time));
      sendRequestObject(  request);
      RemoteResponseObject response = readResponseObject();
      Boolean lRet = null;

      if( response != null )
        lRet = ( Boolean )response.getObject();

      return lRet.booleanValue();
    }
    return false;
  }

  public String[] list()
  {
    if( !isCached() || m_strList == null)
    {
      resetPath();
      sendRequestObject( new RemoteRequestObject( "list" ) );
      RemoteResponseObject response = readResponseObject();

      if( response != null )
        m_strList = ( String[] )response.getObject();
    }
    return m_strList;
  }

  public FileInterface[] _listFiles(FileInterfaceFilter filter)
  {
    if( filter == null)
    {
      return _listFiles();
    }
    if( !isCached() || m_fiList == null)
    {
      resetPath();
      RemoteRequestObject request = new RemoteRequestObject( "_listFiles" );
      request.addParam( filter );
      sendRequestObject( request );

      RemoteResponseObject response = readResponseObject();

      if( response != null )
        m_fiList = (haui.io.FileInterface.FileInterface[] )response.getObject();
    }
    return m_fiList;
  }

  public FileInterface[] _listFiles()
  {
    if( !isCached() || m_fiList == null)
    {
      resetPath();
      sendRequestObject( new RemoteRequestObject( "_listFiles" ) );
      RemoteResponseObject response = readResponseObject();

      if( response != null )
        m_fiList = (haui.io.FileInterface.FileInterface[] )response.getObject();
    }
    return m_fiList;
  }

  public boolean renameTo(FileInterface file)
  {
    resetPath();
    RemoteRequestObject request = new RemoteRequestObject( "renameTo");
    request.addParam( file);
    sendRequestObject( request);

    RemoteResponseObject response = readResponseObject();
    Boolean blRet = null;

    if( response != null)
      blRet = (Boolean)response.getObject();

    return blRet.booleanValue();
  }

  public boolean delete()
  {
    resetPath();
    sendRequestObject( new RemoteRequestObject( "delete"));
    RemoteResponseObject response = readResponseObject();
    Boolean blRet = null;

    if( response != null)
      blRet = (Boolean)response.getObject();

    return blRet.booleanValue();
  }

  public boolean mkdir()
  {
    m_fiList = null;
    m_strList = null;
    m_blArchive = false;
    m_blDirectory = true;
    m_blFile = false;
    resetPath();
    sendRequestObject( new RemoteRequestObject( "mkdir"));
    RemoteResponseObject response = readResponseObject();
    Boolean blRet = null;

    if( response != null)
      blRet = (Boolean)response.getObject();

    return blRet.booleanValue();
  }

  public boolean exists()
  {
    resetPath();
    sendRequestObject( new RemoteRequestObject( "exists"));
    RemoteResponseObject response = readResponseObject();
    Boolean blRet = null;

    if( response != null)
      blRet = (Boolean)response.getObject();

    return blRet.booleanValue();
  }

  public void logon()
  {
    RemoteRequestObject request = new RemoteRequestObject( "logon");
    request.addParam( getFileInterfaceConfiguration().getAppProperties());
    sendRequestObject( request);
    readResponseObject();
  }

  public void logoff()
  {
    sendRequestObject( new RemoteRequestObject( "logoff"));
    //RemoteResponseObject response = readResponseObject();
    disconnect();
  }

  public abstract void startTerminal();

  public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut, OutputStream osErr, InputStream is)
  {
    int iRet = -1;
    if( !cmd.getExecLocal())
    {
      resetPath();
      RemoteRequestObject request = new RemoteRequestObject( "exec");
      request.addParam( strTargetPath);
      request.addParam( cmd);
      //request.addParam( osOut);
      //request.addParam( osErr);
      //request.addParam( is);
      sendRequestObject( request);

      RemoteResponseObject response = readResponseObject();
      Integer intRet = null;

      if( response != null)
        intRet = (Integer)response.getObject();


      iRet = intRet.intValue();
    }
    else
    {
      FileInterface fi = getDirectAccessFileInterface();
      try
      {
        iRet = FileConnector.exec( fi, cmd.getCompleteCommand( fi, fi.getAbsolutePath(), strTargetPath ), strTargetPath,
                            cmd, osOut, osErr, is );
      }
      catch( IOException ex )
      {
        ex.printStackTrace();
      }
    }
    return iRet;
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

  public abstract Object getConnObj();

  private void writeObject( java.io.ObjectOutputStream out )
    throws IOException
  {
    out.defaultWriteObject();

    out.writeObject( getFileInterfaceConfiguration());
    //out.writeBoolean( m_blArchive);
    out.writeBoolean( m_blDirectory);
    out.writeBoolean( m_blFile);
    out.writeBoolean( m_blHidden);
    out.writeBoolean( m_blInit);
    out.writeBoolean( m_blRead);
    out.writeBoolean( m_blWrite);
    out.writeObject( m_fiList );
    out.writeObject( m_fiRoots );
    out.writeLong( m_lLength);
    out.writeLong( m_lModified);
    out.writeObject( m_strAbsolutePath);
    out.writeObject( m_strList);
    out.writeObject( m_strName);
    out.writeObject( m_strParent);
    out.writeObject( m_strPath);
  }

  private void readObject( java.io.ObjectInputStream in )
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();

    setFileInterfaceConfiguration( (FileInterfaceConfiguration)in.readObject());
    m_blArchive = false;
    m_blDirectory = in.readBoolean();
    m_blFile = in.readBoolean();
    m_blHidden = in.readBoolean();
    m_blInit = in.readBoolean();
    m_blRead = in.readBoolean();
    m_blWrite = in.readBoolean();
    m_fiList = (FileInterface[])in.readObject();
    m_fiRoots = (FileInterface[])in.readObject();
    m_lLength = in.readLong();
    m_lModified = in.readLong();
    m_strAbsolutePath = (String)in.readObject();
    m_strList = (String[])in.readObject();
    m_strName = (String)in.readObject();
    m_strParent = (String)in.readObject();
    m_strPath = (String)in.readObject();
  }
}
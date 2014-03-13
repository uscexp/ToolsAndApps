package haui.io.FileInterface;

import haui.components.JExDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.tool.shell.JShellPanel;
import haui.tool.shell.engine.JShellEngine;
import haui.util.CommandClass;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

/**
 * Module:      NormalFile.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\NormalFile.java,v $ <p> Description: Cache for file information.<br> </p><p> Created:     11.06.2002 by AE </p><p>
 * @history      11.06.2002 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: NormalFile.java,v $  Revision 1.2  2004-08-31 16:03:21+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.1  2004-06-22 14:08:51+02  t026843  bigger changes  Revision 1.0  2003-06-06 10:05:37+02  t026843  Initial revision  Revision 1.2  2003-06-04 15:35:54+02  t026843  bugfixes  Revision 1.1  2003-05-28 14:19:52+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:55+02  t026843  Initial revision  Revision 1.5  2002-09-18 11:16:20+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.4  2002-09-03 17:08:00+02  t026843  - CgiTypeFile is now full functional.  - Migrated to the extended filemanager.pl script.  Revision 1.3  2002-08-07 15:25:26+02  t026843  Ftp support via filetype added.  Some bugfixes.  Revision 1.2  2002-06-21 11:00:18+02  t026843  Added gzip and bzip2 file support  Revision 1.1  2002-06-19 16:13:52+02  t026843  Zip file support; writing doesn't work yet!  Revision 1.0  2002-06-17 17:21:19+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2002; $Revision: 1.2 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\NormalFile.java,v 1.2 2004-08-31 16:03:21+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.2  </p>
 */
public class NormalFile
  extends BaseTypeFile
{
  // constants

  // member variables
  private File m_file;

  /**
   * Creates a new NormalFile instance.
   *
   * @param strCurPath: current path
   */
  public NormalFile( String strCurPath, String strParentPath, FileInterfaceConfiguration fic)
  {
    super( fic);
    m_file = new File( strCurPath);
    m_strParent = strParentPath;
    if( m_strParent == null)
    {
      if( m_file.getParentFile() != null)
        m_strParent = m_file.getParentFile().getAbsolutePath();
      else
      {
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
      }
    }
    super._init();
  }

  public FileInterface duplicate()
  {
    NormalFile nf = new NormalFile( getAbsolutePath(), getParent(), getFileInterfaceConfiguration());
    return nf;
  }

  public BufferedInputStream getBufferedInputStream()
    throws FileNotFoundException, IOException
  {
    return new BufferedInputStream( new FileInputStream( getAbsolutePath()));
  }

  public BufferedOutputStream getBufferedOutputStream( String strNewPath)
    throws FileNotFoundException
  {
    return new BufferedOutputStream( new FileOutputStream( strNewPath));
  }

  public void closeOutputStream()
    throws IOException
  {
  }

  public FileInterface[] _listRoots()
  {
    if( !isCached() || m_fiRoots == null)
    {
      File f[] = File.listRoots();
      if( f == null )
        return null;

      m_fiRoots = new NormalFile[f.length];
      for( int i = 0; i < f.length; ++i )
      {
        m_fiRoots[i] = new NormalFile( f[i].getAbsolutePath(), f[i].getParent(), getFileInterfaceConfiguration());
      }
    }
    return (FileInterface[])m_fiRoots;
  }

  public char separatorChar()
  {
    return File.separatorChar;
  }

  public char pathSeparatorChar()
  {
    return File.pathSeparatorChar;
  }

  public boolean canRead()
  {
    if( !isCached())
      m_blRead = m_file.canRead();
    return m_blRead;
  }

  public boolean canWrite()
  {
    if( !isCached())
      m_blWrite = m_file.canWrite();
    return m_blWrite;
  }

  public boolean isDirectory()
  {
    if( !isCached())
      m_blDirectory = m_file.isDirectory();
    return m_blDirectory;
  }

  public boolean isFile()
  {
    if( !isCached())
      m_blFile = m_file.isFile();
    return m_blFile;
  }

  public boolean isHidden()
  {
    if( !isCached())
      m_blHidden = m_file.isHidden();
    return m_blHidden;
  }

  public URL toURL()
  {
    URL url = null;
    try
    {
      url = new URL( "file", null, getAbsolutePath() );
    }
    catch( MalformedURLException ex )
    {
      ex.printStackTrace();
    }
    return url;
  }

  public long length()
  {
    if( !isCached())
      m_lLength = m_file.length();
    return m_lLength;
  }

  public String getId()
  {
    return m_strHost;
  }

  public String getName()
  {
    if( !isCached() || m_strName == null)
      m_strName = m_file.getName();
    return m_strName;
  }

  public String getAbsolutePath()
  {
    if( !isCached() || m_strAbsolutePath == null)
      m_strAbsolutePath = m_file.getAbsolutePath();
    return m_strAbsolutePath;
  }

  public FileInterface getCanonicalFile() throws IOException
  {
    return new NormalFile( m_file.getCanonicalPath(), getParent(), getFileInterfaceConfiguration());
  }

  public String getPath()
  {
    if( !isCached() || m_strPath == null)
      m_strPath = m_file.getPath();
    return m_strPath;
  }

  public String getInternalPath()
  {
    return null;
  }

  public FileInterface getDirectAccessFileInterface()
  {
    return this;
  }

  public String getParent()
  {
    if( m_strParent != null && !m_strParent.equals( ""))
      return m_strParent;
    return m_file.getParent();
  }

  public FileInterface getParentFileInterface()
  {
    boolean blExtract = false;
    FileInterface fi = FileConnector.createFileInterface( getParent(), null, blExtract, getFileInterfaceConfiguration());
    return fi;
  }

  public long lastModified()
  {
    if( !isCached())
      m_lModified = m_file.lastModified();
    return m_lModified;
  }

  public boolean setLastModified( long time)
  {
    return m_file.setLastModified( time);
  }

  public String[] list()
  {
    if( !isCached() || m_strList == null)
      m_strList = m_file.list();
    return m_strList;
  }

  public FileInterface[] _listFiles( FileInterfaceFilter filter)
  {
    if( !isCached() || m_fiList == null)
    {
      File[] f;
      List<FileInterface> files = new ArrayList<FileInterface>();
      if( filter == null )
      {
        f = m_file.listFiles();
        for( int i = 0; i < f.length; ++i )
        {
          files.add(new NormalFile( f[i].getAbsolutePath(), f[i].getParent(), getFileInterfaceConfiguration()));
        }
      }
      else
      {
        f = m_file.listFiles();
        for( int i = 0; i < f.length; ++i )
        {
          FileInterface file = new NormalFile( f[i].getAbsolutePath(), f[i].getParent(), getFileInterfaceConfiguration());
          if((filter == null) || filter.accept(file))
            files.add(file);
        }
      }

      if( f == null )
        return null;

      m_fiList = (FileInterface[])files.toArray(new FileInterface[files.size()]);
    }
    return (FileInterface[])m_fiList;
  }

  public FileInterface[] _listFiles()
  {
    return _listFiles( null);
  }

  public boolean renameTo( FileInterface file)
  {
    boolean blRet = m_file.renameTo( (File)file);
    if( blRet && isCached())
    {
      m_strAbsolutePath = file.getAbsolutePath();
      m_strPath = file.getPath();
      m_strName = file.getName();
    }
    return blRet;
  }

  public boolean delete()
  {
    return m_file.delete();
  }

  public boolean mkdir()
  {
    m_fiList = null;
    m_strList = null;
    m_blArchive = false;
    m_blDirectory = true;
    m_blFile = false;
    return m_file.mkdir();
  }

  public boolean exists()
  {
    return m_file.exists();
  }

  public void logon()
  {
    return;
  }

  public void logoff()
  {
    return;
  }

  public boolean createNewFile() throws IOException
  {
    return m_file.createNewFile();
  }

  public void startTerminal()
  {
    final JShellPanel sp = new JShellPanel( getFileInterfaceConfiguration().getAppName());
    final JExDialog dlg = new JExDialog( null, "JShell - " + getId(), false, getAppName());
    dlg.addWindowListener( new WindowAdapter()
    {
      public void windowClosing( WindowEvent event )
      {
        Object object = event.getSource();
        if( object == dlg )
        {
          sp.stop();
          super.windowClosing( event);
          dlg.dispose();
        }
      }
    } );
    dlg.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE);
    dlg.getContentPane().add("Center", sp);
    dlg.pack();
    dlg.setVisible( true);
    Thread th = new Thread()
    {
      public void run()
      {
        try
        {
          StringBuffer strbufCmd = new StringBuffer( "cd \"");
          strbufCmd.append( getAbsolutePath());
          strbufCmd.append( "\"");
          sp.getShell().getShellEnv().setFileInterface( duplicate() );
          JShellEngine.processCommands( strbufCmd.toString(), sp.getShell().getShellEnv(), false);
          sp.start();
          dlg.setVisible( false );
          dlg.dispose();
        }
        catch( Exception ex)
        {
          ex.printStackTrace();
          dlg.setVisible( false );
        }
      }
    };
    th.start();
  }

  /**
   * ececute command
   *
   * @param strTargetPath traget path
   * @param cmd CommandClass
   */
  public int exec( String strTargetPath, CommandClass cmd,
                    OutputStream osOut, OutputStream osErr, InputStream is )
  {
    int iRet = -1;
    try
    {
      iRet = FileConnector.exec( this, cmd.getCompleteCommand( this, strTargetPath ), strTargetPath,
                          cmd, osOut, osErr, is );
    }
    catch( IOException ex )
    {
      ex.printStackTrace();
    }
    return iRet;
  }

  public Object getConnObj()
  {
    return null;
  }

  public String extractToTempDir()
  {
    return getParent();
  }

  public void deleteOnExit()
  {
    m_file.deleteOnExit();
  }

  public boolean setReadOnly()
  {
    return m_file.setReadOnly();
  }
}
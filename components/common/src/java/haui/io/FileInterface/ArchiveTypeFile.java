
package haui.io.FileInterface;

import haui.components.JExDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.tool.shell.JShellPanel;
import haui.tool.shell.engine.JShellEngine;
import haui.util.CommandClass;
import haui.util.GlobalApplicationContext;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Module:      ArchiveTypeFile<br> $Source: $ <p> Description: base FileInterface class for archive type files.<br> </p><p> Created:     21.12.2004 by AE </p><p>
 * @history      21.12.2004 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.2  </p>
 */
public abstract class ArchiveTypeFile
  extends BaseTypeFile
{
  // member variables
  protected String m_archPath;
  protected String m_intPath;
  protected File m_file;

  // cache variables

  public ArchiveTypeFile( String strArchPath, String strIntPath, String strParentPath, FileInterfaceConfiguration fic)
  {
    super( fic);
    m_file = new File( strArchPath);

    int idx = strArchPath.lastIndexOf( separatorChar());
    if( idx > 0 &&  idx == strArchPath.length()-1)
      strArchPath = strArchPath.substring( 0, strArchPath.length()-1);
    m_archPath = strArchPath;
    idx = strIntPath.indexOf( separatorChar());
    if( idx == 0)
      strIntPath = strIntPath.substring( 1, strIntPath.length());
    idx = strIntPath.lastIndexOf( separatorChar());
    if( idx > 0 &&  idx == strIntPath.length()-1)
      strIntPath = strIntPath.substring( 0, strIntPath.length()-1);
    m_intPath = strIntPath;
    if( strParentPath == null)
    {
      String path = m_archPath + separatorChar() + m_intPath;
      idx = path.lastIndexOf( separatorChar());
      strParentPath = path.substring( 0, idx);
    }
    m_strParent = strParentPath;
    idx = strIntPath.lastIndexOf( separatorChar());
    m_strName = m_intPath.substring( idx+1, m_intPath.length());
  }

  public FileInterface[] _listFiles( FileInterfaceFilter filter)
  {
    if( !isCached() || m_fiList == null)
    {
      FileInterface[] fis = FileConnector.allocateFileInterfaceArray( this, 0);
      Vector vec = null;
      m_fiList = fis;
      if( isDirectory() || isArchive() )
      {
        String[] str = list();
        if( str != null)
        {
          if( str.length > 0 )
          {
            if( filter == null )
              fis = FileConnector.allocateFileInterfaceArray( this, str.length);
            else
              vec = new Vector();
            for( int j = 0; j < str.length; ++j )
            {
              String path = "";
              int idx = m_archPath.length() + 1;
              if( idx > 0 && idx < str[j].length() )
                path = str[j].substring( idx, str[j].length() );
              FileInterface f = FileConnector.createFileInterface( this.getClass(), null, m_archPath, path,
                getAbsolutePath(), getFileInterfaceConfiguration());
              if( filter == null )
                fis[j] = f;
              else
              {
                if( filter.accept( f ) )
                  vec.add( f );
              }
            }
            if( filter != null )
            {
              fis = FileConnector.allocateFileInterfaceArray( this, vec.size());
              for( int j = 0; j < vec.size(); ++j )
                fis[j] = ( FileInterface )vec.elementAt( j );
            }
            m_fiList = fis;
          }
        }
      }
    }
    return m_fiList;
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

  public boolean isFile()
  {
    return m_blFile;
  }

  public boolean isHidden()
  {
    return m_blHidden;
  }

  public long length()
  {
    return m_lLength;
  }

  public String getId()
  {
    return m_strHost;
  }

  public String getName()
  {
    return m_strName;
  }

  public String getAbsolutePath()
  {
    return m_strAbsolutePath;
  }

  public String getPath()
  {
    return m_strPath;
  }

  public String getInternalPath()
  {
    return m_intPath;
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

  public void logon()
  {
    return;
  }

  public void logoff()
  {
    return;
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
    FileInterface fi = this;
    int iRet = -1;
    fi = getDirectAccessFileInterface();
    try
    {
      iRet = FileConnector.exec( fi, cmd.getCompleteCommand( fi, fi.getAbsolutePath(), strTargetPath ), strTargetPath,
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
}

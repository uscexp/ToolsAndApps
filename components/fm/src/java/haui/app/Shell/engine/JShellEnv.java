package haui.app.Shell.engine;

import haui.app.Shell.util.*;
import haui.app.Shell.command.JShellCommandInterface;
import haui.components.*;
import haui.io.*;
import haui.util.AppProperties;

import cz.dhl.ftp.Ftp;

import org.javagroup.process.ProcessManager;
import org.javagroup.process.ProcessManagerHolder;
import org.javagroup.process.JProcess;

import java.io.*;
import java.util.*;
import java.text.Collator;
import java.net.URL;
import java.net.MalformedURLException;

/**
 *    Module:       JShellEnv.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellEnv.java,v $
 *<p>
 *    Description:  Java shell environment.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: JShellEnv.java,v $
 *    Revision 1.1  2004-08-31 16:03:02+02  t026843
 *    Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *    Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *    Revision 1.0  2004-06-22 14:06:51+02  t026843
 *    Initial revision
 *
 *</p><p>
 *    @author       Andreas Eisenhauer
 *</p><p>
 *    @version      v1.0, 2004; $Revision: 1.1 $<br>
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\engine\\JShellEnv.java,v 1.1 2004-08-31 16:03:02+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class JShellEnv
{
  // constants
  public static final boolean ISDEBUG = false;
  public static final String APPNAME = "JShell";
  public static final String COMMANDFILE = "JShellCmd.pps";
  //public static final String HISTORYFILE = "JShellHist.pps";
  public static final String APPVERSION = "0.1";
  public static final String COPYRIGHT = "Copyright(C) 2004 A.E.";

  public static final int EXITVALUE = -999;
  public static final int DEFAULTERRORVALUE = -1;
  public static final int DEFAULTERRORVALUEFORINTCOMMAND = -2;
  public static final int OKVALUE = 0;

  // member variables
  private String m_strAppName;
  private AppProperties m_appProps = new AppProperties();
  private ShellTextArea.StreamContainer m_sc = null;
  private InputStream m_is = null;
  private PrintStream m_psOut = null;
  private PrintStream m_psErr = null;
  private Collator m_collator = null;

  private String m_strOriginalLine = null;
  private String m_strStartLine = null;
  private FileInterface m_fi = null;
  private FileInterface m_fiBase = null;
  private String m_strTargetPath = null;
  private JShellCommandList m_commandList = null;
  private JShellCommandList m_subCommandList = null;
  private Vector m_vecProcesses = new Vector();
  private ProcessManager m_pm;
  private ShellTextArea m_sta = null;
  private JShellCommandInterface m_jsci = null;
  private JProcess m_proc = null;
  private boolean m_blSubEnv = false;
  private boolean m_blExecLocal = true;

  public JShellEnv( String strAppName)
  {
    this( (ShellTextArea)null, strAppName);
  }

  public JShellEnv( ShellTextArea sta, String strAppName)
  {
    constructor( sta, strAppName);
  }

  public JShellEnv( ShellTextArea.StreamContainer sc, ShellTextArea sta, String strAppName)
  {
    setStreamContainer( sc);
    constructor( sta, strAppName);
  }

  public JShellEnv( InputStream is, PrintStream psOut, PrintStream psErr, ShellTextArea sta, String strAppName)
  {
    setIn( is);
    setOut( psOut);
    setErr( psErr);
    constructor( sta, strAppName);
  }


  /**
   * JShellEnv constructor for a subcommand processor
   *
   * @param jseParent parent JShellEnv
   */
  public JShellEnv( JShellEnv jseParent, FileInterface fi, String strAppName)
  {
    setIn( jseParent.getIn());
    setOut( jseParent.getOut());
    setErr( jseParent.getErr());
    constructor( jseParent.getShellTextArea(), strAppName);
    m_fiBase = jseParent.getFileInterface();
    setFileInterface( fi);
    m_blSubEnv = true;
  }

  public void setExecLocal( boolean bl)
  {
    m_blExecLocal = bl;
  }

  public boolean execLocal()
  {
    return m_blExecLocal;
  }

  public void setSubCommandList( JShellCommandList jsccl)
  {
    m_subCommandList = jsccl;
  }

  private void constructor( ShellTextArea sta, String strAppName)
  {
    m_strAppName = strAppName;
    m_sta = sta;
    setClasspathToDefaultClasspath();
    if( !m_blSubEnv)
    {
      init();
      m_commandList = new JShellCommandList( this );
    }
  }

  public void init()
  {
    File f = new File( ".");
    String strPath = ".";
    strPath = f.getAbsolutePath();
    int iIdx = -1;
    if( (iIdx = strPath.lastIndexOf( f.separatorChar)) != -1 )
    {
      strPath = strPath.substring( 0, iIdx );
    }
    m_fi = FileConnector.createFileInterface( strPath, null, false, null, APPNAME, m_appProps, true);
    initFileInterfaceOutputStream();
  }

  public boolean isSubEnv()
  {
    return m_blSubEnv;
  }

  public void setPasswordInputMode()
  {
    if( m_sta != null)
      m_sta.setPasswordInputMode( true);
  }

  public boolean isInPasswordInputMode()
  {
    if( m_sta != null)
      return m_sta.isInPasswordInputMode();
    return false;
  }

  public void setInputMode()
  {
    if( m_sta != null)
      m_sta.setInputMode( true);
  }

  public boolean isInInputMode()
  {
    if( m_sta != null)
      return m_sta.isInInputMode();
    return false;
  }

  public void initFileInterfaceOutputStream()
  {
    if( m_fi != null)
    {
      if( m_fi.getConnObj() instanceof Ftp)
        ((Ftp)m_fi.getConnObj()).setContextOutputStream( getOut());
    }
  }

  public void setTargetPath( String strTargetPath)
  {
    m_strTargetPath = strTargetPath;
  }

  public String getTargetPath()
  {
    if( m_strTargetPath == null || m_strTargetPath.equals( ""))
      return m_fi.getAbsolutePath();
    return m_strTargetPath;
  }

  public void setStartLine( String strStartLine)
  {
    m_strStartLine = strStartLine;
  }

  public String getStartLine()
  {
    return m_strStartLine;
  }

  public static void setClasspathToDefaultClasspath()
  {
    String strPath = System.getProperty( "java.class.path");
    strPath = convertClasspathToUrlString( strPath);
    System.setProperty( "default.classpath", strPath);
  }

  public static String convertClasspathToUrlString( String strClasspath)
  {
    StringBuffer strbufPath = new StringBuffer();
    URL[] urls = convertClasspathToUrlArray( strClasspath);

    if( urls == null)
      return null;

    for( int i = 0; i < urls.length; ++i)
    {
      //if( i > 0)
        //strbufPath.append( ",");
      strbufPath.append( urls[i].toString());
      strbufPath.append( ",");
    }
    return strbufPath.toString();
  }

  public static URL[] convertClasspathToUrlArray( String strClasspath)
  {
    URL[] urls = null;
    StringTokenizer st = new StringTokenizer( strClasspath, ";", false);
    int iCount = st.countTokens();
    urls = new URL[iCount];

    int i = 0;
    while( st.hasMoreTokens())
    {
      try
      {
        String str = st.nextToken();
        if( str.toLowerCase().endsWith( ".jar") || str.toLowerCase().endsWith( ".zip"))
          urls[i] = new URL( "file", null, str );
        else
        {
          if( !str.endsWith( File.separator))
            str += File.separator;
          urls[i] = new URL( "file", null, str );
        }
      }
      catch( MalformedURLException ex )
      {
        ex.printStackTrace();
      }
      ++i;
    }
    return urls;
  }

  public void setCurrentShellCommand( JShellCommandInterface jsci)
  {
    m_jsci = jsci;
  }

  public JShellCommandInterface getCurrentShellCommand()
  {
    return m_jsci;
  }

  public void setProcess( JProcess proc)
  {
    m_proc = proc;
  }

  public void killForegoundProcess()
  {
    if( m_proc != null)
    {
      m_proc.kill();
      m_proc = null;
    }
  }

  public ProcessManager getProcessManager()
  {
    if( m_pm == null)
      m_pm = ProcessManagerHolder.getProcessManager();
    return m_pm;
  }

  public int clearScreen()
  {
    int iStatus = DEFAULTERRORVALUE;
    if( m_sta != null)
    {
      m_sta.clear();
      iStatus = OKVALUE;
    }
    return iStatus;
  }

  public void addProcess( BackgroundThread bt)
  {
    m_vecProcesses.add( bt);
  }

  public void clearInactivProcesses()
  {
    for( int i = m_vecProcesses.size()-1; i >= 0; --i)
    {
      BackgroundThread bt = (BackgroundThread)m_vecProcesses.elementAt( i);
      if( !bt.isAlive())
        removeProcess( i);
    }
  }

  public BackgroundThread getProcess( int iIdx)
  {
    if( iIdx >= m_vecProcesses.size() && iIdx >= 0)
      return null;
    return (BackgroundThread)m_vecProcesses.elementAt( iIdx);
  }

  public BackgroundThread removeProcess( int iIdx)
  {
    if( iIdx >= m_vecProcesses.size() && iIdx >= 0)
      return null;
    return (BackgroundThread)m_vecProcesses.remove( iIdx);
  }

  public Vector getBackgroundProcessVector()
  {
    return m_vecProcesses;
  }

  public void setFileInterface( FileInterface fi)
  {
    m_fi = fi;
    initFileInterfaceOutputStream();
  }

  public String getAppName()
  {
    return m_strAppName;
  }

  public AppProperties getAppPropperties()
  {
    return m_appProps;
  }

  public JShellCommandList getCommandList()
  {
    return m_commandList;
  }

  public JShellCommandList getSubCommandList()
  {
    return m_subCommandList;
  }

  public void save()
  {
  }

  /**
   * The standardization of the comparison.
   */
  public final Collator getCollatorInstance()
  {
    if( m_collator == null )
    {
      m_collator = Collator.getInstance();
      m_collator.setStrength( Collator.PRIMARY );
      m_collator.setDecomposition( Collator.FULL_DECOMPOSITION );
    }
    return m_collator;
  }

  public final void clearLine()
  {
    m_strOriginalLine = null;
  }

  public final String getOriginalReadLine()
  {
    return m_strOriginalLine;
  }

  public final String getNextLine()
  {
    if( m_strStartLine != null)
    {
      String str = m_strStartLine;
      m_strStartLine = null;
      return str;
    }
    String strRead = StringUtil.readLine( getIn() );
    m_strOriginalLine = strRead;
    return strRead;
  }

  public void setShellTextArea( ShellTextArea sta)
  {
    m_sta = sta;
  }

  public ShellTextArea getShellTextArea()
  {
    return m_sta;
  }

  public void setStreamContainer( ShellTextArea.StreamContainer sc )
  {
    m_is = null;
    m_psOut = null;
    m_psErr = null;
    m_sc = sc;
  }

  public void setIn( InputStream is )
  {
    m_is = is;
  }

  public InputStream getIn()
  {
    InputStream is = m_is;
    if( is == null )
      is = m_sc.getInputStream();
    return is;
  }

  public void setOut( PrintStream ps )
  {
    m_psOut = ps;
  }

  public PrintStream getOut()
  {
    PrintStream ps = m_psOut;
    if( ps == null )
      ps = m_sc.getOutputPrintStream();
    return ps;
  }

  public void setErr( PrintStream ps )
  {
    m_psErr = ps;
  }

  public PrintStream getErr()
  {
    PrintStream ps = m_psErr;
    if( ps == null )
      ps = m_sc.getOutputPrintStream();
    return ps;
  }

  public FileInterface getFileInterface()
  {
    return m_fi;
  }

  public void setBaseFileInterface( FileInterface fi)
  {
    m_fiBase = fi;
  }

  public FileInterface getBaseFileInterface()
  {
    return m_fiBase;
  }
}
package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.FileInterface;
import haui.io.FileConnector;

import org.javagroup.process.ProcessManagerHolder;
import org.javagroup.process.ProcessManager;
import org.javagroup.process.JProcess;
import org.javagroup.util.URLClassLoader;
import org.javagroup.util.StandardIO;

import java.util.*;
import java.io.File;
import java.util.jar.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.MalformedURLException;
import java.security.SecureClassLoader;
//import haui.net.URLClassLoader;

/**
 * Module:      JShellCommandJexec.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandJexec.java,v $
 *<p>
 * Description: execute java programs.<br>
 *</p><p>
 * Created:     06.04.2004  by AE
 *</p><p>
 * @history     06.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandJexec.java,v $
 * Revision 1.1  2004-08-31 16:03:00+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:46+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandJexec.java,v 1.1 2004-08-31 16:03:00+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandJexec
  extends JShellCommandDefault
{
  private BackgroundThread m_bt = null;

  public JShellCommandJexec( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "jexec", jse));
    setDisplayString("execute java programs");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " [-cp] <classpath or jarfile>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n -cp\tadditional classpath");

    jse.getOut().println( strbufUsage.toString());
  }

  public void setBackgroundThread( BackgroundThread bt)
  {
    m_bt = bt;
  }

  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp)
    throws haui.app.Shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    if( !fi.isDirectory() && !fi.isArchive())
      fi = fi.getParentFileInterface();
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    String strArg = null;
    if( strArgs != null && strArgs.length > 0)
      strArg = strArgs[ 0];
    String strDir = null;
    String strName = null;
    String strOldPath = System.getProperty( "java.class.path");
    if( strArg != null && !strArg.equals( ""))
    {
      String strMainClass = strArg;

      StringBuffer strbufPath = new StringBuffer( fi.getAbsolutePath());
      strbufPath.append( ";");
      strbufPath.append( strOldPath);

      String strPath = (String)hmOptions.get( "-cp");
      if( strPath != null)
      {
        StringTokenizer stPath = new StringTokenizer( strPath, ";", false);

        while( stPath.hasMoreTokens())
        {
          strbufPath.append( ";");
          String str = stPath.nextToken().trim();
          if( !str.equals( "."))
          {
            FileInterface fiTmp = FileConnector.createFileInterface( str, null, false,
              fi.getConnObj(), jse.getAppName(), jse.getAppPropperties() );
            if( !fiTmp.exists() )
            {
              StringBuffer stTmp = new StringBuffer( fi.getAbsolutePath() );
              stTmp.append( fi.separatorChar() );
              stTmp.append( str );
              fiTmp = FileConnector.createFileInterface( stTmp.toString(), null, false,
                fi.getConnObj(), jse.getAppName(), jse.getAppPropperties() );
              if( fiTmp.exists() )
                strbufPath.append( fiTmp.getAbsolutePath() );
              else
                strbufPath.append( str );
            }
          }
        }
        //strbufPath.append( strPath);
      }
      if( strArg.endsWith( ".jar"))
      {
        try
        {
          File file = new File( strArg);
          if( !file.exists())
          {
            file = new File( jse.getFileInterface().getAbsolutePath() + jse.getFileInterface().separatorChar()
                             + strArg);
            if( !file.exists())
              return iStatus;
          }
          strbufPath.append( ";");
          strbufPath.append( file.getAbsolutePath());
          JarFile jf = new JarFile( file );
          Manifest ma = jf.getManifest();
          if( ma != null)
          {
            Attributes attr = ma.getMainAttributes();
            if( attr != null)
            {
              strMainClass = (String)attr.getValue( "Main-Class");
            }
          }
        }
        catch( IOException ioex )
        {
          ioex.printStackTrace();
        }
      }
      try
      {
        ProcessManager pm = jse.getProcessManager();
        boolean blFound = false;
        int iStart = 0;
        int ii = 0;
        String[] strProcArgs = null;
        strArgs = jscp.getArgumentArray();
        for( int i = 0; i < strArgs.length; ++i)
        {
          if( !blFound)
          {
            if( strArg.equals( strArgs[i]))
            {
              blFound = true;
              iStart = i+1;
              ii = 0;
              strProcArgs = new String[ strArgs.length-iStart];
            }
          }
          else
          {
            if( strProcArgs != null)
              strProcArgs[ii++] = strArgs[i];
          }
        }
        //System.setProperty( "java.class.path", strbufPath.toString());
        jse.setClasspathToDefaultClasspath();
        strPath = jse.convertClasspathToUrlString( strbufPath.toString());
        String strFilePath = System.getProperty( "user.dir");
        System.setProperty( "user.dir", fi.getAbsolutePath());
        JProcess proc = pm.createProcess( strMainClass, strProcArgs, URLClassLoader.decodePathString( strPath) );
        if( m_bt != null)
          m_bt.setProcess( proc);
        else
          jse.setProcess( proc);
        proc.launch();

        Thread.sleep( 1000);
        while( proc.getState() != JProcess.RUNNING)
          Thread.sleep( 1000);
        if( proc.getState() == JProcess.RUNNING)
          Thread.sleep( 1000);
        System.setProperty( "user.dir", strFilePath);

        proc.waitFor();
        iStatus = proc.getExitCode();
      }
      catch( Exception ex )
      {
        ex.printStackTrace();
      }
      //iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
  /*
  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp)
    throws haui.app.Shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    String strArg = null;
    if( strArgs != null && strArgs.length > 0)
      strArg = strArgs[ 0];
    String strDir = null;
    String strName = null;
    String strPath = System.getProperty( "java.class.path");
    if( strArg != null && !strArg.equals( ""))
    {
      String strMainClass = strArg;
      StringBuffer strbufPath = new StringBuffer();
      if( strPath != null && strPath.length() > 0)
        strbufPath.append( strPath);

      if( strPath != null && !strPath.endsWith( ";"))
        strbufPath.append( ";");
      strbufPath.append( fi.getAbsolutePath());

      strPath = (String)hmOptions.get( "-cp");
      if( strPath != null)
      {
        strbufPath.append( ";");
        strbufPath.append( strPath);
      }
      if( strArg.endsWith( ".jar"))
      {
        try
        {
          File file = new File( strArg);
          if( !file.exists())
          {
            file = new File( jse.getFileInterface().getAbsolutePath() + jse.getFileInterface().separatorChar()
                             + strArg);
            if( !file.exists())
              return iStatus;
          }
          strbufPath.append( ";");
          strbufPath.append( file.getAbsolutePath());
          JarFile jf = new JarFile( file );
          Manifest ma = jf.getManifest();
          if( ma != null)
          {
            Attributes attr = ma.getMainAttributes();
            if( attr != null)
            {
              strMainClass = (String)attr.getValue( "Main-Class");
            }
          }
        }
        catch( IOException ioex )
        {
          ioex.printStackTrace();
        }
      }
      try
      {
        //System.setProperty( "java.class.path", strbufPath.toString());
        //Class[] caArr = (new String[100]);
        URL[] urlClassPath = jse.convertClasspathToUrlArray( strbufPath.toString());
        Class ca = Class.forName( strMainClass );
        Method[] meths = ca.getMethods();
        Method meth = null;
        for( int i = 0; i < meths.length; ++i)
        {
          if( meths[i].getName().equals( "main"))
          {
            meth = meths[i];
            break;
          }
        }
        if( meth != null)
        {
          boolean blFound = false;
          int iStart = 0;
          int ii = 0;
          String[] strProcArgs = null;
          strArgs = jscp.getArgumentArray();
          for( int i = 0; i < strArgs.length; ++i)
          {
            if( !blFound)
            {
              if( strArg.equals( strArgs[i]))
              {
                blFound = true;
                iStart = i;
                ii = 0;
                strProcArgs = new String[ strArgs.length-iStart];
              }
              else
              {
                if( strProcArgs != null)
                  strProcArgs[ii] = strArgs[i];
              }

            }
          }
          //meth.invoke( null, strProcArgs );
          ThreadGroup tg = new ThreadGroup( strMainClass);
          ProcessThread thProc = new ProcessThread( tg, meth, strProcArgs);
          URLClassLoader ucl = new URLClassLoader( urlClassPath);
          thProc.setContextClassLoader( ucl);
          thProc.start();
          while( tg.activeCount() > 0)
            thProc.sleep( 1000);
          iStatus = thProc.getExitValue();
        }
      }
      catch( Exception ex )
      {
        ex.printStackTrace();
      }
      //iStatus = JShellEnv.OKVALUE;
    }
    //System.setProperty( "java.class.path", strPath);

    return iStatus;
  }

  class ProcessThread
    extends Thread
  {
    private int m_iState = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;
    private Method m_meth;
    private String[] m_strArgs;

    public ProcessThread( ThreadGroup tg, Method method, String[] strArgs)
    {
      super( tg, (Runnable)null);
      m_meth = method;
      m_strArgs = strArgs;
    }

    public void run()
    {
      try
      {
        Object obj = m_meth.invoke( null, m_strArgs );
        if( obj != null && obj instanceof Integer )
        {
          m_iState = ( ( Integer )obj ).intValue();
        }
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }

    public void waitFor()
    {
      try
      {
        while( isAlive() )
          sleep( 1000 );
      }
      catch( InterruptedException ex )
      {
        ex.printStackTrace();
      }
    }

    public int getExitValue()
    {
      return m_iState;
    }
  }

  public class DefaultClassLoader
    extends SecureClassLoader
  {
    public DefaultClassLoader()
    {
      super();
    }
  }
*/
}
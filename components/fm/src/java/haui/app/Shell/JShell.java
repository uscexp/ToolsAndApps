package haui.app.Shell;

import haui.components.*;
import haui.util.ConfigPathUtil;
import haui.app.Shell.engine.*;
import haui.util.AppProperties;

import java.io.*;
import java.lang.reflect.Method;

/**
 *    Module:       JShell.java<br>
 *                  $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\JShell.java,v $
 *<p>
 *    Description:  Java shell implementation.<br>
 *</p><p>
 *    Created:	    23.03.2004  by AE
 *</p><p>
 *    @history      23.03.2004  by AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: JShell.java,v $
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
 *                  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\JShell.java,v 1.1 2004-08-31 16:03:02+02 t026843 Exp t026843 $
 *</p><p>
 *    @since        JDK1.3
 *</p>
 */
public class JShell
{
  // member variables
  JShellEnv m_jsenv;
  JShellEngine m_jseng;

  public JShell( String strAppName)
  {
    if( strAppName.equals( JShellEnv.APPNAME))
      ConfigPathUtil.init( strAppName);
    m_jsenv = new JShellEnv( JShellEnv.APPNAME);
    m_jseng = new JShellEngine( m_jsenv, false);
  }

  public JShellEnv getShellEnv()
  {
    return m_jsenv;
  }

  public JShellEngine getShellEngine()
  {
    return m_jseng;
  }

  public static final void main(String[] args)
  {
    JShell js = new JShell( JShellEnv.APPNAME);
    setMultiAppMode( true);

    js.init();

    int iRet = js.process();

    setMultiAppMode( false);
    js.exit(iRet);
  }

  public int start()
  {
    int iRet=process();

    return iRet;
  }

  public void stop()
  {
    if( m_jseng != null)
      m_jseng.stop();
  }

  public void init()
  {
    m_jsenv.setIn(System.in);
    m_jsenv.setOut(System.out);
    m_jsenv.setErr(System.err);
  }

  /**
   * This function takes care of the call to the modified System class <BR>
   * It checks first if the System class is in use or the modified one, to remain compatible with the old one.<BR>
   * This way it's still working even if the user forgot the -xbootclasspath stuff.
   * @see System
   */
  public static void setMultiAppMode( boolean mode )
  {
    // Call by reflexion API
    Class params[] = new Class[1];
    params[0] = boolean.class;
    Class classe = System.class;
    try
    {
      Method toCall = classe.getDeclaredMethod( "setMultiAppMode", params );
      Object args[] = new Object[1];
      args[0] = new Boolean( mode );
      toCall.invoke( null, args );
    }
    catch( Exception e )
    {
      // Could not make the call
      AppProperties.getPrintStreamOutput( JShellEnv.APPNAME).println( "Warning : Standard system class used.\nJShell not fully functional, please use -Xbootclasspath option");
      //System.err.println( "Warning : Standard system class used.\nJShell not fully functional, please use -Xbootclasspath option" );
    }
  }

  private void exit(int iRet)
  {
    m_jsenv.save();
    System.exit(iRet);
  }

  /**
   * Entry point for shell instance.
   */
  public int process()
  {
    if( m_jsenv == null)
      return JShellEnv.DEFAULTERRORVALUE;
    int iRet = m_jseng.process();

    return iRet;
  }

  public void setShellTextArea( ShellTextArea sta)
  {
    m_jsenv.setShellTextArea( sta);
  }

  public void setStreamContainer( ShellTextArea.StreamContainer sc)
  {
    m_jsenv.setStreamContainer( sc);
  }

  public void setIn(InputStream streamIn)
  {
    m_jsenv.setIn(streamIn);
  }

  public InputStream getIn()
  {
    return m_jsenv.getIn();
  }

  public void setOut(PrintStream printArg)
  {
    m_jsenv.setOut(printArg);
  }

  public PrintStream getOut()
  {
    return m_jsenv.getOut();
  }

  public void setErr(PrintStream printArg)
  {
    m_jsenv.setErr(printArg);
  }

  public PrintStream getErr()
  {
    return m_jsenv.getErr();
  }

  // static initializer for setting look & feel
  static
  {
    try
    {
      AppProperties.addAppClass( JShellEnv.APPNAME, Class.forName( "haui.app.Shell.JShell" ) );
    }
    catch( Exception e )
    {}
  }
}
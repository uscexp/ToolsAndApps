package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.*;
import haui.app.Shell.command.remote.JShellRemoteCommandList;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.*;

/**
 * Module:      JShellCommandDlsocket.java<br>
 *              $Source: $
 *<p>
 * Description: direct link connection with ip socket.<br>
 *</p><p>
 * Created:     08.06.2004  by AE
 *</p><p>
 * @history     08.06.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandDlsocket
  extends JShellCommandDefault
{
  String m_strHost;
  int m_iPort = 0;

  public JShellCommandDlsocket( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "dlsocket", jse));
    setDisplayString("direct link ip socket connection client");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " -host=<HOST> -port=<PORT> [-exec=<command>]");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n -host=<HOST>\thostname or hostip");
    strbufUsage.append( "\n -port=<PORT>\tconnetion port");
    strbufUsage.append( "\n -exec=<command>\texecute this commandline in this direct link socket connection subshell");

    jse.getOut().println( strbufUsage.toString());
  }

  public int processCommand(JShellEnv jse, JShellCommandProcessor jscp)
    throws haui.app.Shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    FileInterface fi = jse.getFileInterface();
    HashMap hmOptions = jscp.getOptions();
    String[] strArgs = jscp.getArgumentsWithoutOptions();
    if( strArgs == null || strArgs.length == 0)
    {
      String strPort = null;
      if( m_strHost == null &&  (m_strHost = (String)hmOptions.get( "-host")) == null)
      {
        jse.getOut().print( "host? ");
        m_strHost = jse.getNextLine();
      }

      if( strPort == null && (strPort = (String)hmOptions.get( "-port")) == null)
      {
        jse.getOut().print( "port? ");
        strPort = jse.getNextLine();
      }
      if( strPort != null)
      {
        try
        {
          m_iPort = Integer.parseInt( strPort);
        }
        catch( NumberFormatException nfex)
        {
          nfex.printStackTrace();
        }
      }

      SocketConnection sc = new SocketConnection( m_strHost, m_iPort);
      try
      { /* connect & login to host */
        FileInterface fiSc = FileConnector.createFileInterface( ".", null, false, sc, jse.getAppName(),
          jse.getAppPropperties(), true);
        JShellEnv jseSub = new JShellEnv( jse, fiSc, jse.getAppName());
        JShellRemoteCommandList jsfcl = new JShellRemoteCommandList( jseSub);
        jseSub.setSubCommandList( jsfcl);
        JShellEngine jsengSub = new JShellEngine( jseSub, true);
        String strExec = (String)hmOptions.get( "-exec");
        if( strExec != null)
        {
          if( strExec.startsWith( "\""))
            strExec = strExec.substring( 1);
          if( strExec.endsWith( "\""))
            strExec = strExec.substring( 0, strExec.length()-1);
          jseSub.setStartLine( strExec);
        }
        iStatus = jsengSub.process();
      }
      catch ( Exception ex)
      {
        ex.printStackTrace();
      }
      m_strHost = null;
      m_iPort = 0;
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
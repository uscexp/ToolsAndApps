package haui.tool.shell.command;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.remote.SocketConnection;
import haui.tool.shell.command.remote.JShellRemoteCommandList;
import haui.tool.shell.engine.JShellCommandProcessor;
import haui.tool.shell.engine.JShellEngine;
import haui.tool.shell.engine.JShellEnv;
import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

import java.io.IOException;
import java.util.HashMap;

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
    setCommand( (new JShellCommandProcessor( jse, "dlsocket")).getCommandLineContainer());
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

  public int processCommand(JShellEnv jse, CommandLineContainer clc)
    throws haui.tool.shell.engine.JShellException
  {
    int iStatus = JShellEnv.DEFAULTERRORVALUEFORINTCOMMAND;

    //FileInterface fi = jse.getFileInterface();
    HashMap hmOptions = clc.getOptions();
    String[] strArgs = clc.getArgumentsWithoutOptionsAsString();
    if( strArgs == null || strArgs.length == 0)
    {
      String strPort = null;
      try
      {
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
      }
      catch( IOException ex)
      {
        ex.printStackTrace();
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
        FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, sc, 0, 0,
            jse.getAppName(), jse.getAppPropperties(), true);
        FileInterface fiSc = FileConnector.createFileInterface( ".", null, false, fic);
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
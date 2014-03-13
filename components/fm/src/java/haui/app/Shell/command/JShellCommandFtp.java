package haui.app.Shell.command;

import haui.app.Shell.engine.*;
import haui.io.FileInterface;
import haui.io.FileConnector;
import haui.app.Shell.command.remote.JShellRemoteCommandList;

import cz.dhl.ftp.*;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.*;

/**
 * Module:      JShellCommandFtp.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandFtp.java,v $
 *<p>
 * Description: ftp command.<br>
 *</p><p>
 * Created:     11.04.2004  by AE
 *</p><p>
 * @history     11.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellCommandFtp.java,v $
 * Revision 1.1  2004-08-31 16:03:23+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:45+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\command\\JShellCommandFtp.java,v 1.1 2004-08-31 16:03:23+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellCommandFtp
  extends JShellCommandDefault
{
  String m_strUrl = null;
  String m_strUser = null;
  String m_strPwd = null;

  public JShellCommandFtp( JShellEnv jse)
  {
    setCommand( new JShellCommandProcessor( "ftp", jse));
    setDisplayString("console ftp client");
  }

  public void usage(JShellEnv jse)
  {
    StringBuffer strbufUsage = new StringBuffer();
    strbufUsage.append( "\nUsage: ");
    strbufUsage.append( getCommand().getCommand());
    strbufUsage.append( " [-user=<USER>] [-pwd=<PWD>] [-exec=<command>] <host>");
    strbufUsage.append( "\nDescription: ");
    strbufUsage.append( getDisplayString());
    strbufUsage.append( "\n -user=<USER>\tusername");
    strbufUsage.append( "\n -pwd=<PWD>\tpassword");
    strbufUsage.append( "\n -exec=<command>\texecute this commandline in this ftp subshell");
    strbufUsage.append( "\n host\tformat: ftp://myhost.mydomain.de:21/mypath or");
    strbufUsage.append( "\n     \t        ftp://user:pwd@myhost.mydomain.de:21/mypath");

    jse.getOut().println( strbufUsage.toString());
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
    if( strArgs.length == 1)
    {
      m_strUrl = strArgs[0];

      if( !m_strUrl.startsWith( "ftp://"))
        return iStatus;
      int iIdx = -1;
      int iStart = "ftp://".length();
      iIdx = m_strUrl.indexOf( "@");
      String strUsrPwd = null;
      if( iIdx != -1 && iIdx > iStart)
      {
        strUsrPwd = m_strUrl.substring( iStart, iIdx );

        StringTokenizer st = new StringTokenizer( strUsrPwd, ":", false);

        if( st.countTokens() < 3)
        {
          if( st.hasMoreTokens())
            m_strUser = st.nextToken();
          if( st.hasMoreTokens())
            m_strPwd = st.nextToken();
        }
        m_strUrl = "ftp://" + m_strUrl.substring( iIdx+1);
      }

      if( m_strUser == null &&  (m_strUser = (String)hmOptions.get( "-user")) == null)
      {
        jse.getOut().print( "user? ");
        m_strUser = jse.getNextLine();
      }

      if( m_strPwd == null && (m_strPwd = (String)hmOptions.get( "-pwd")) == null)
      {
        jse.getOut().print( "password? ");
        jse.setPasswordInputMode();
        m_strPwd = jse.getNextLine();
      }

      String[] args;
      if( m_strUser != null)
      {
        args = new String[3];
        args[0] = m_strUrl;
        args[1] = "-user";
        args[2] = m_strUser;
      }
      else
      {
        args = new String[1];
        args[0] = m_strUrl;
      }
      FtpConnect fc = FtpConnect.newConnect( args);
      if( m_strPwd != null)
      {
        fc.setPassWord( m_strPwd);
      }
      Ftp f = new Ftp();
      try
      { /* connect & login to host */
        f.setContextOutputStream( jse.getOut());
        f.connect( fc );
        FileInterface fiFtp = FileConnector.createFileInterface( f.pwd(), null, false, f, jse.getAppName(),
          jse.getAppPropperties(), true);
        JShellEnv jseSub = new JShellEnv( jse, fiFtp, jse.getAppName());
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
      catch (IOException ioex)
      {
        ioex.printStackTrace();
      }
      m_strUser = null;
      m_strPwd = null;
      iStatus = JShellEnv.OKVALUE;
    }

    return iStatus;
  }
}
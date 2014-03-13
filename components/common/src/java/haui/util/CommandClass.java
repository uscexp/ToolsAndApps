package haui.util;

import haui.app.fm.FileInfo;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 * Module:					CommandClass.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\CommandClass.java,v $ <p> Description:    Hadels commands with its parameters.<br> </p><p> Created:				06.11.2000	by	AE </p><p>
 * @history  				06.11.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: CommandClass.java,v $  Revision 1.2  2004-08-31 16:03:22+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.1  2004-06-22 14:08:51+02  t026843  bigger changes  Revision 1.0  2003-06-06 10:05:38+02  t026843  Initial revision  Revision 1.0  2003-05-21 16:25:38+02  t026843  Initial revision  Revision 1.4  2002-09-03 17:07:57+02  t026843  - CgiTypeFile is now full functional.  - Migrated to the extended filemanager.pl script.  Revision 1.3  2002-08-07 15:25:23+02  t026843  Ftp support via filetype added.  Some bugfixes.  Revision 1.2  2002-06-17 17:19:18+02  t026843  Zip and Jar filetype read only support.  Revision 1.1  2002-05-29 11:18:17+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.0  2001-07-20 16:32:49+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.2 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\CommandClass.java,v 1.2 2004-08-31 16:03:22+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class CommandClass
  extends Object
  implements Serializable
{
  private static final long serialVersionUID = -2910569121564593662L;
  
  // property constants
  public final static String IDENTIFIER = "Identifier";
  public final static String STARTDIR = "StartDir";
  public final static String COMMAND = "Command";
  public final static String PARAMETER = "Params";
  public final static String MNEMONIC = "Mnemonic";
  public final static String MOUSEBUTTON = "MouseButton";
  public final static String EXECLOCAL = "ExecLocal";

  // constants
  public final static String EXEPATH = "%e";
  public final static String SOURCEPATH = "%p";
  public final static String SOURCENAME = "%n";
  public final static String DESTPATH = "%t";
  public final static String PARAMDLG = "%?";
  public final static String PERCENT = "%%";
  public final static int NO = 0;
  public final static int LEFT = 1;
  public final static int MIDDLE = 2;
  public final static int RIGHT = 3;

  // member variables
  FileInterfaceConfiguration m_fic;
  AppProperties m_appProps;
  String m_strAppName;
  String m_strIdentifier;
  String m_strStartDir;
  String m_strCommand;
  String m_strParams;
  int m_iMnemonic = -1;
  int m_iMouseButton = NO;
  Component m_comp;
  boolean m_blExecLocal = true;
  Object m_connObj;

  public CommandClass( String strAppName)
  {
    this( null, null, null, null, -1, NO, true, null, null);
  }

  public CommandClass( String strIdentifier, String strStartDir, String strCommand, String strParams, int iMnemonic,
    int iMouseButton, boolean blExecLocal, Component comp, FileInterfaceConfiguration fic)
  {
    m_fic = fic;
    m_strIdentifier = strIdentifier;
    m_strStartDir = strStartDir;
    m_strCommand = strCommand;
    m_strParams = strParams;
    m_iMnemonic = iMnemonic;
    m_iMouseButton = iMouseButton;
    m_blExecLocal = blExecLocal;
    m_comp = comp;
  }

  public FileInterfaceConfiguration getFileInterfaceConfiguration()
  {
    return m_fic;
  }

  public void setFileInterfaceConfiguration( FileInterfaceConfiguration fic)
  {
    this.m_fic = fic;
  }

  public void setExecLocal( boolean blExecLocal)
  {
    m_blExecLocal = blExecLocal;
  }

  public boolean getExecLocal()
  {
    return m_blExecLocal;
  }

  public void setComponent( Component comp)
  {
    m_comp = comp;
  }

  public Component getComponent()
  {
    return m_comp;
  }

  public void setConnectionObject( Object connObj)
  {
    m_connObj = connObj;
  }

  public Object getConnectionObject()
  {
    return m_connObj;
  }

  public void setMnemonic( int iMnemonic)
  {
    m_iMnemonic = iMnemonic;
  }

  public int getMnemonic()
  {
    return m_iMnemonic;
  }

  public void setMouseButton( int iMouseButton)
  {
    m_iMouseButton = iMouseButton;
  }

  public int getMouseButton()
  {
    return m_iMouseButton;
  }

  static public String getMouseButtonText( int iMouseButton)
  {
    String strMouseButton = "";
    switch( iMouseButton)
    {
      case NO:
        strMouseButton = "";
        break;

      case LEFT:
        strMouseButton = "Left";
        break;

      case MIDDLE:
        strMouseButton = "Middle";
        break;

      case RIGHT:
        strMouseButton = "Right";
        break;
    }
    return strMouseButton;
  }

  static public int getMouseButtonInt( String strMouseButton)
  {
    int iMouseButton = NO;

    if( strMouseButton.equalsIgnoreCase( ""))
      iMouseButton = NO;
    else if( strMouseButton.equalsIgnoreCase( "left"))
      iMouseButton = LEFT;
    else if( strMouseButton.equalsIgnoreCase( "middle"))
      iMouseButton = MIDDLE;
    else if( strMouseButton.equalsIgnoreCase( "right"))
      iMouseButton = RIGHT;

    return iMouseButton;
  }

  public void setIdentifier( String strIdentifier)
  {
    m_strIdentifier = strIdentifier;
  }

  public String getIdentifier()
  {
    return m_strIdentifier;
  }

  public void setStartDir( String strStartDir)
  {
    m_strStartDir = strStartDir;
  }

  public String getStartDir()
  {
    return m_strStartDir;
  }

  public String getCompleteStartDir( FileInfo fiSource, String strDest)
  {
    FileInterface fi = null;
    if( fiSource != null)
      fi = fiSource.getFileInterface();
    return getCompleteStartDir( fi, strDest);
  }

  public String getCompleteStartDir( FileInterface fiSource, String strDest)
  {
    String strPar = null;
    if( fiSource != null)
      strPar = fiSource.getParent();
    return getCompleteStartDir( fiSource, strPar, strDest);
  }

  public String getCompleteStartDir( FileInterface fiSource, String strParent, String strDest)
  {
    String strCompSd = "";
    strCompSd = parse( m_strStartDir, fiSource, strParent, strDest);
    return strCompSd;
  }

  public void setCommand( String strCommand)
  {
    m_strCommand = strCommand;
  }

  public String getCommand()
  {
    return m_strCommand;
  }

  public String getCommandWithPath( FileInfo fiSource, String strDest)
  {
    FileInterface fi = null;
    if( fiSource != null)
      fi = fiSource.getFileInterface();
    return getCommandWithPath( fi, strDest);
  }

  public String getCommandWithPath( FileInterface fiSource, String strDest)
  {
    String strPar = null;
    if( fiSource != null)
      strPar = fiSource.getParent();
    return getCommandWithPath( fiSource, strPar, strDest);
  }

  public String getCommandWithPath( FileInterface fiSource, String strParent, String strDest)
  {
    String strCmdPath = "";
    strCmdPath = parse( m_strCommand, fiSource, strParent, strDest);
    return strCmdPath;
  }

  public void setParams( String strParams)
  {
    m_strParams = strParams;
  }

  public String getParams()
  {
    return m_strParams;
  }

  static public CommandClass searchCommand( Vector vCmds, String strName, int iMouseButton)
  {
    int i;
    CommandClass cmd = null;
    boolean blFound = false;

    for( i = 0; i < vCmds.size(); i++)
    {
      cmd = (CommandClass)vCmds.elementAt( i);
      StringTokenizer st = new StringTokenizer( cmd.getIdentifier().toLowerCase(), ",", false);

      while( st.hasMoreTokens())
      {
        String strPart = st.nextToken().trim();
        if( strName.toLowerCase().endsWith( strPart) && cmd.getMouseButton() == iMouseButton )
        {
          blFound = true;
          break;
        }
      }
      if( blFound)
        break;
      else
        cmd = null;
    }
    return cmd;
  }

  public String getCompleteCommand( FileInfo fiSource, String strDest)
  {
    FileInterface fi = null;
    if( fiSource != null)
      fi = fiSource.getFileInterface();
    return getCompleteCommand( fi, strDest);
  }

  public String getCompleteCommand( FileInterface fiSource, String strDest)
  {
    String strPar = null;
    if( fiSource != null)
      strPar = fiSource.getParent();
    return getCompleteCommand( fiSource, strPar, strDest);
  }

  public String getCompleteCommand( FileInterface fiSource, String strParent, String strDest)
  {
    String strCompCmd = "";
    String strParam = parse( m_strParams, fiSource, strParent, strDest);
    if( strParam == null)
      strParam = "";
    else
      strParam = " " + strParam;
    strCompCmd = getCommandWithPath( fiSource, strParent, strDest) + strParam;
    return strCompCmd;
  }

  private String parse( String strToParse, FileInterface fiSource, String strParent, String strDest)
  {
    String strRet = strToParse;
    int iIdx = -1;
    char sepChar = '/';
    if( strParent != null && strParent.indexOf( '\\') != -1)
      sepChar = '\\';

    if( strRet != null)
    {
      while( ( iIdx = strRet.indexOf( SOURCEPATH ) ) != -1 )
      {
        if( fiSource == null )
          strRet = strRet.substring( 0, iIdx ) + "" + strRet.substring( iIdx + 2 );
        else
        {
          String str = strRet.substring( 0, iIdx ) + strParent;
          if( str.charAt( str.length() - 1 ) == sepChar )
            strRet = strRet.substring( 0, iIdx ) + strParent + strRet.substring( iIdx + 2 );
          else
            strRet = strRet.substring( 0, iIdx ) + strParent + sepChar + strRet.substring( iIdx + 2 );
        }
      }
      while( ( iIdx = strRet.indexOf( SOURCENAME ) ) != -1 )
      {
        if( fiSource == null )
          strRet = strRet.substring( 0, iIdx ) + "" + strRet.substring( iIdx + 2 );
        else
          strRet = strRet.substring( 0, iIdx ) + fiSource.getName() + strRet.substring( iIdx + 2 );
      }
      while( ( iIdx = strRet.indexOf( DESTPATH ) ) != -1 )
      {
        if( strDest == null )
          strRet = strRet.substring( 0, iIdx ) + "" + strRet.substring( iIdx + 2 );
        else
          strRet = strRet.substring( 0, iIdx ) + strDest + strRet.substring( iIdx + 2 );
      }
      while( ( iIdx = strRet.indexOf( EXEPATH ) ) != -1 )
      {
        String strPath = null;
        FileInterface fi = FileConnector.createFileInterface( ".", null, false, getFileInterfaceConfiguration());
        strPath = FileConnector.findCommandPathInExtendedPath( fi, strRet, getFileInterfaceConfiguration());
        if( strPath == null )
          strRet = strRet.substring( 0, iIdx ) + "" + strRet.substring( iIdx + 2 );
        else
          strRet = strRet.substring( 0, iIdx ) + strPath + strRet.substring( iIdx + 2 );
      }
      while( ( iIdx = strRet.indexOf( PARAMDLG ) ) != -1 )
      {
        String strCmd = strRet + " - parameter?";
        String str = JOptionPane.showInputDialog( m_comp, strCmd, "Parameter input",
                                                  JOptionPane.QUESTION_MESSAGE );
        if( str == null )
          str = "";
        strRet = strRet.substring( 0, iIdx ) + str + strRet.substring( iIdx + 2 );
      }
      while( ( iIdx = strRet.indexOf( PERCENT ) ) != -1 )
      {
        strRet = strRet.substring( 0, iIdx ) + "%" + strRet.substring( iIdx + 2 );
      }
    }
    return strRet;
  }

  public boolean equals( Object obj)
  {
    return m_strIdentifier.equals( ((CommandClass)obj).getIdentifier()) && m_iMouseButton == ((CommandClass)obj).getMouseButton();
  }

  public String toString()
  {
    String strMouseButton = "";
    String strMnemonic = "";
    String strStartDir = "";

    if( m_iMnemonic != -1)
    {
      strMnemonic = String.valueOf( (char)m_iMnemonic);
    }
    if( m_iMouseButton > NO)
    {
      strMouseButton = ", " + getMouseButtonText( m_iMouseButton);
    }
    if( m_strStartDir != null)
      strStartDir = m_strStartDir;
    return m_strIdentifier + ", " + strStartDir + ", " + m_strCommand + " " + m_strParams + strMnemonic
          + strMouseButton + ", " + String.valueOf( m_blExecLocal);
  }

  public Object clone()
  {
    CommandClass cmd = new CommandClass( m_strIdentifier, m_strStartDir, m_strCommand, m_strParams, m_iMnemonic,
        m_iMouseButton, m_blExecLocal, m_comp, getFileInterfaceConfiguration());
    return cmd;
  }

  static public void eraseCommands( String strPrefix, AppProperties appProps)
  {
    for( int i = 0; appProps.getProperty( strPrefix + String.valueOf( i) + "." + IDENTIFIER) != null; i++)
    {
      appProps.remove( strPrefix + String.valueOf( i) + "." + IDENTIFIER);
      appProps.remove( strPrefix + String.valueOf( i) + "." + STARTDIR);
      appProps.remove( strPrefix + String.valueOf( i) + "." + COMMAND);
      appProps.remove( strPrefix + String.valueOf( i) + "." + PARAMETER);
      appProps.remove( strPrefix + String.valueOf( i) + "." + MNEMONIC);
      appProps.remove( strPrefix + String.valueOf( i) + "." + MOUSEBUTTON);
      appProps.remove( strPrefix + String.valueOf( i) + "." + EXECLOCAL);
    }
  }

  static public Vector initCommandVector( String strPrefix, String strAppName, AppProperties appProps)
  {
    int i = 0;
    CommandClass cmd;
    String strId;
    String strSd;
    String strCom;
    String strPar;
    Integer iMnemonic;
    Integer iMouseButton;
    boolean blExecLocal;
    Vector vecCommands = new Vector();

    for( i = 0; (strId = appProps.getProperty( strPrefix + String.valueOf( i) + "." + IDENTIFIER)) != null
      && (strCom = appProps.getProperty( strPrefix + String.valueOf( i) + "." + COMMAND)) != null
      && (strPar = appProps.getProperty( strPrefix + String.valueOf( i) + "." + PARAMETER)) != null;
      i++)
    {
      cmd = new CommandClass( strAppName);
      cmd.setIdentifier( strId);
      strSd = strId = appProps.getProperty( strPrefix + String.valueOf( i) + "." + STARTDIR);
      cmd.setStartDir( strSd);
      cmd.setCommand( strCom);
      cmd.setParams( strPar);
      iMnemonic = appProps.getIntegerProperty( strPrefix + String.valueOf( i) + "." + MNEMONIC);
      if( iMnemonic == null)
        iMnemonic = new Integer( -1);
      cmd.setMnemonic( iMnemonic.intValue());
      iMouseButton = appProps.getIntegerProperty( strPrefix + String.valueOf( i) + "." + MOUSEBUTTON);
      if( iMouseButton == null)
        iMouseButton = new Integer( CommandClass.NO);
      cmd.setMouseButton( iMouseButton.intValue());
      blExecLocal = appProps.getBooleanProperty( strPrefix + String.valueOf( i) + "." + EXECLOCAL);
      cmd.setExecLocal( blExecLocal);
      vecCommands.add( cmd);
    }
    return vecCommands;
  }

  static public void storeCommands( String strPrefix, Vector vecCommands, AppProperties appProps)
  {
    for( int i = 0; i < vecCommands.size(); i++)
    {
      CommandClass cmd = (CommandClass)vecCommands.elementAt( i);
      appProps.setProperty( strPrefix + String.valueOf( i) + "." + IDENTIFIER,
        cmd.getIdentifier());
      appProps.setProperty( strPrefix + String.valueOf( i) + "." + STARTDIR,
        (cmd.getStartDir() == null ? "" : cmd.getStartDir()));
      appProps.setProperty( strPrefix + String.valueOf( i) + "." + COMMAND,
        cmd.getCommand());
      appProps.setProperty( strPrefix + String.valueOf( i) + "." + PARAMETER,
        cmd.getParams());
      appProps.setIntegerProperty( strPrefix + String.valueOf( i) + "." + MNEMONIC,
        new Integer( cmd.getMnemonic()));
      appProps.setIntegerProperty( strPrefix + String.valueOf( i) + "." + MOUSEBUTTON,
        new Integer( cmd.getMouseButton()));
      appProps.setBooleanProperty( strPrefix + String.valueOf( i) + "." + EXECLOCAL,
        cmd.getExecLocal());
    }
  }

  public void save( BufferedWriter bw)
  throws IOException
  {
    bw.write( getIdentifier());
    bw.newLine();
    bw.write( getStartDir());
    bw.newLine();
    bw.write( getCommand());
    bw.newLine();
    bw.write( getParams());
    bw.newLine();
    bw.write( String.valueOf( getMnemonic()));
    bw.newLine();
    bw.write( String.valueOf( getMouseButton()));
    bw.newLine();
  }

  public boolean read( BufferedReader br)
  throws IOException
  {
    String str = br.readLine();
    if( str == null)
      return false;
    setIdentifier( str);
    str = br.readLine();
    if( str == null)
      return false;
    setStartDir( str);
    str = br.readLine();
    if( str == null)
      return false;
    setCommand( br.readLine());
    str = br.readLine();
    if( str == null)
      return false;
    setParams( br.readLine());
    str = br.readLine();
    if( str == null)
      return false;
    setMnemonic( Integer.parseInt( str));
    str = br.readLine();
    if( str == null)
      return false;
    setMouseButton( Integer.parseInt( str));
    return true;
  }

  /**
   * Attention! This function does not serialize m_comp nor m_connObj
   * @param out
   * @throws IOException
   */
  private void writeObject( java.io.ObjectOutputStream out )
    throws IOException
  {
    out.defaultWriteObject();

    out.writeObject( m_appProps );
    out.writeObject( getIdentifier());
    out.writeObject( getStartDir());
    out.writeObject( getCommand());
    out.writeObject( getParams());
    out.writeInt( getMnemonic());
    out.writeInt( getMouseButton());
    out.writeBoolean( getExecLocal());
  }

  /**
   * Attention! This function does not deserialize m_comp nor m_connObj
   * @param in
   * @throws IOException
   * @throws java.lang.ClassNotFoundException
   */
  private void readObject( java.io.ObjectInputStream in )
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();

    m_appProps = (AppProperties)in.readObject();
    setIdentifier( (String)in.readObject());
    setStartDir( (String)in.readObject());
    setCommand( (String)in.readObject());
    setParams( (String)in.readObject());
    setMnemonic( in.readInt());
    setMouseButton( in.readInt());
    setExecLocal( in.readBoolean());
  }
}
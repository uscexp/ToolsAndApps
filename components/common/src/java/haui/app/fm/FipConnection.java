package haui.app.fm;

import haui.util.AppProperties;
import haui.util.CommandClass;

import java.util.List;

/**
 * Module:					FipConnection.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FipConnection.java,v $ <p> Description:    Contains the connection configuration.<br> </p><p> Created:				17.11.2000	by	AE </p><p>
 * @history  				17.11.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FipConnection.java,v $  Revision 1.4  2004-08-31 16:03:21+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.3  2004-06-22 14:08:55+02  t026843  bigger changes  Revision 1.2  2003-06-06 10:03:59+02  t026843  modifications because of the moving the 'TypeFile's to haui.io package  Revision 1.1  2003-05-28 14:19:50+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:50+02  t026843  Initial revision  Revision 1.4  2002-09-03 17:07:56+02  t026843  - CgiTypeFile is now full functional.  - Migrated to the extended filemanager.pl script.  Revision 1.3  2002-06-26 12:06:46+02  t026843  History extended, simple bookmark system added.  Revision 1.2  2002-06-11 09:32:51+02  t026843  bugfixes  Revision 1.1  2002-05-29 11:18:15+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.0  2001-07-20 16:34:25+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.4 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FipConnection.java,v 1.4 2004-08-31 16:03:21+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FipConnection
extends Object
{
  // constants
  public final static String FMCONN = "FipConnection.";
  public final static String BUTCMD = "ButCmd";
  public final static String EXTCMD = "ExtCmd";

  //member variables
  AppProperties m_appProps;
  String m_strAppName;
  String m_strConnName;
  List<CommandClass> m_vecButCmd;
  List<CommandClass> m_vecFileExtCmd;

  public FipConnection( String strName, String strAppName, AppProperties appProps)
  {
    m_appProps = appProps;
    m_strAppName = strAppName;
    setName( strName);
  }

  public void _init()
  {
    String strPrefix = FMCONN + m_strConnName + ".";
    m_vecButCmd = CommandClass.initCommandVector( strPrefix + BUTCMD, m_strAppName, m_appProps);
    m_vecFileExtCmd = CommandClass.initCommandVector( strPrefix + EXTCMD, m_strAppName, m_appProps);
  }

  public void _save()
  {
    _erase();
    String strPrefix = FMCONN + m_strConnName + ".";

    CommandClass.storeCommands( strPrefix + BUTCMD, m_vecButCmd, m_appProps);
    CommandClass.storeCommands( strPrefix + EXTCMD, m_vecFileExtCmd, m_appProps);
  }

  public void _erase()
  {
    String strPrefix = FMCONN + m_strConnName + ".";
    CommandClass.eraseCommands( strPrefix + BUTCMD, m_appProps);
    CommandClass.eraseCommands( strPrefix + EXTCMD, m_appProps);
  }

  public void _eraseBut()
  {
    String strPrefix = FMCONN + m_strConnName + ".";
    CommandClass.eraseCommands( strPrefix + BUTCMD, m_appProps);
  }

  public void _eraseExt()
  {
    String strPrefix = FMCONN + m_strConnName + ".";
    CommandClass.eraseCommands( strPrefix + EXTCMD, m_appProps);
  }

  public String getName()
  {
    return m_strConnName;
  }

  private void setName( String strName)
  {
    m_strConnName = strName;
  }

  public List<CommandClass> getButtonCommands()
  {
    return m_vecButCmd;
  }

  public void setButtonCommands( List<CommandClass> vecButCmd)
  {
    m_vecButCmd = vecButCmd;
  }

  public List<CommandClass> getFileExtCommands()
  {
    return m_vecFileExtCmd;
  }

  public void setFileExtCommands( List<CommandClass> vecFileExtCmd)
  {
    m_vecFileExtCmd = vecFileExtCmd;
  }

  public AppProperties getAppProperties()
  {
    return m_appProps;
  }
}
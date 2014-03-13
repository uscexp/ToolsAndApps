package haui.app.dbtreetableview;

import haui.util.AppProperties;
import haui.util.ConfigPathUtil;
import haui.util.GlobalAppProperties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Vector;

/**
 *		Module:					DBAlias.java<br>
 *										$Source: $
 *<p>
 *		Description:    DBAlias definitions.<br>
 *</p><p>
 *		Created:				23.09.2002	by	AE
 *</p><p>
 *		@history				23.09.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DBAlias
{
  // Constants
  public static final String DBD = "DbAlias";
  public static final String PROPSFILE = "dbalias.ppr";
  public static final String PROPSHEADER = "DbTreeTableView db alias properties";
  public static final String DBALIASNAME = "DBAliasName";
  public static final String DBDRIVERCLASS = "DBDriverClass";
  public static final String DBDRIVER = "DBDriver";
  public static final String DBURL = "DBUrl";
  public static final String DBUSER = "DBUser";
  public static final String SQLFILEEXT = "sqf";
  public static final String HISTFILEEXT = "sqh";

  // member variables
  String m_strDBAliasName;
  String m_strDBDriver;
  String m_strDBDriverClass;
  String m_strDBUrl;
  String m_strDBUser;
  private ListElemListModel m_defSqlLM = new ListElemListModel();
  private ListElemListModel m_defHistLM = new ListElemListModel();
  Vector m_vecLists = new Vector();


  public DBAlias( String strDBAliasName, String strDBDriver, String strDBDriverClass, String strDBUrl, String strDBUser)
  {
    m_strDBAliasName = strDBAliasName;
    m_strDBDriver = strDBDriver;
    m_strDBDriverClass = strDBDriverClass;
    m_strDBUrl = strDBUrl;
    m_strDBUser = strDBUser;
  }

  public DBAlias()
  {
    this( null, null, null, null, null);
  }

  public String getAliasName()
  {
    return m_strDBAliasName;
  }

  public void setAliasName( String strDBAliasName)
  {
    m_strDBAliasName = strDBAliasName;
  }

  public String getDBDriver()
  {
    return m_strDBDriver;
  }

  public void setDBDriver( String strDBDriver)
  {
    m_strDBDriver = strDBDriver;
  }

  public String getDBDriverClass()
  {
    return m_strDBDriverClass;
  }

  public void setDBDriverClass( String strDBDriverClass)
  {
    m_strDBDriverClass = strDBDriverClass;
  }

  public String getFullDBUrl()
  {
    String strPrefix = "jdbc:";
    return strPrefix + m_strDBUrl;
  }

  public String getDBUrl()
  {
    return m_strDBUrl;
  }

  public void setDBUrl( String strDBUrl)
  {
    m_strDBUrl = strDBUrl;
  }

  public String getDBUser()
  {
    return m_strDBUser;
  }

  public void setDBUser( String strDBUser)
  {
    m_strDBUser = strDBUser;
  }

  public String toString()
  {
    return m_strDBAliasName;
  }

  public Object clone()
  {
    return new DBAlias( m_strDBAliasName, m_strDBDriver, m_strDBDriverClass, m_strDBUrl, m_strDBUser);
  }

  public boolean equals( Object obj)
  {
    if( obj != null && obj instanceof DBAlias)
    {
      DBAlias dbd = (DBAlias)obj;
      if( dbd.getAliasName().equals( getAliasName()))
        return true;
    }
    return false;
  }

  public ListElemListModel getSqlListModel()
  {
    if( m_defSqlLM.size() == 0)
      readSqlListElems( null, false);
    return m_defSqlLM;
  }

  public ListElemListModel getHistListModel()
  {
    if( m_defHistLM.size() == 0)
      readHistListElems( null, false);
    return m_defHistLM;
  }

  /**
   * Read SQL statements from file.
   *
   * @return		Error code.
   */
  public int readSqlListElems( String strName, boolean blAppend)
  {
    int iRet = GlobalAppProperties.instance().ERRORFILE;
    BufferedReader brSQL;
    String strImp = "";
    String strFile;
    if( strName != null && !strName.equals( ""))
      strFile = strName;
    else
    {
      strFile = getAliasName() + "." + SQLFILEEXT;
      strFile = ConfigPathUtil.getCurrentSavePath( DbTtvDesktop.DBTTVDT, strFile);
    }

    try
    {
      brSQL = new BufferedReader( new FileReader( strFile));
      int i = 0;
      ListElem elem = null;
      String strSql = "";
      if( !blAppend)
        m_defSqlLM.removeAllElements();
      while((strImp = brSQL.readLine()) != null)
      {
        if( i == 0)
        {
          if( strImp.equals( "<ESQL>"))
          {
            elem = new ListElem();
            elem.setSqlStatement( strSql.substring( 0, strSql.length()-1));
            i++;
            strSql = "";
          }
          else
          {
            if( strImp != null && !strImp.equals( "\n"))
              strSql += (strImp + "\n");
          }
        }
        else
        {
          if( strImp.equals( "<ETAB>"))
          {
            elem.setTableNames( strSql);
            m_defSqlLM.addElement( elem);
            i++;
            strSql = "";
          }
          else
          {
            if( strImp != null && !strImp.equals( "\n"))
              strSql += strImp;
          }
        }
        if( i > 1)
          i = 0;
      }
      brSQL.close();
      iRet = GlobalAppProperties.instance().OK;
    }
    catch(FileNotFoundException e)
    {
      GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT).println( strFile + ": file not found!");
      //System.out.println( strFile + ": file not found!");
    }
    catch(IOException e)
    {
      GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT).println( strFile + ": file read error!");
      //System.out.println( strFile + ": file read error!");
    }

    return iRet;
  }

  /**
   * Read history SQL statements from file.
   *
   * @return		Error code.
   */
  public int readHistListElems( String strName, boolean blAppend)
  {
    int iRet = GlobalAppProperties.instance().ERRORFILE;
    BufferedReader brSQL;
    String strImp = "";
    String strFile;
    if( strName != null && !strName.equals( ""))
      strFile = strName;
    else
    {
      strFile = getAliasName() + "." + HISTFILEEXT;
      strFile = ConfigPathUtil.getCurrentSavePath( DbTtvDesktop.DBTTVDT, strFile);
    }

    try
    {
      brSQL = new BufferedReader( new FileReader( strFile));
      int i = 0;
      ListElem elem = null;
      String strSql = "";
      if( !blAppend)
        m_defHistLM.removeAllElements();
      while((strImp = brSQL.readLine()) != null)
      {
        if( i == 0)
        {
          if( strImp.equals( "<ESQL>"))
          {
            elem = new ListElem();
            elem.setSqlStatement( strSql.substring( 0, strSql.length()-1));
            i++;
            strSql = "";
          }
          else
          {
            if( strImp != null && !strImp.equals( "\n"))
              strSql += (strImp + "\n");
          }
        }
        else
        {
          if( strImp.equals( "<ETAB>"))
          {
            elem.setTableNames( strSql);
            m_defHistLM.addElement( elem);
            i++;
            strSql = "";
          }
          else
          {
            if( strImp != null && !strImp.equals( "\n"))
              strSql += strImp;
          }
        }
        if( i > 1)
          i = 0;
      }
      brSQL.close();
      iRet = GlobalAppProperties.instance().OK;
    }
    catch(FileNotFoundException e)
    {
      //System.out.println( strFile + ": file not found!");
    }
    catch(IOException e)
    {
      GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT).println( strFile + ": file read error!");
      //System.out.println( strFile + ": file read error!");
    }

    return iRet;
  }

  /**
   * Save SQL statements to file.
   *
   * @return		Error code.
   */
  public int saveSqlListElems()
  {
    int i;
    int iRet = GlobalAppProperties.instance().ERRORFILE;
    BufferedWriter bwSQL;
    String strBuf = null;
    String strFile = getAliasName() + "." + SQLFILEEXT;

    try
    {
      bwSQL = new BufferedWriter( new FileWriter( ConfigPathUtil.getCurrentSavePath( DbTtvDesktop.DBTTVDT, strFile)));
      for( i = 0; i < m_defSqlLM.size(); i++)
      {
        ListElem elem = (ListElem)m_defSqlLM.get( i);
        strBuf = elem.getSqlStatement();
        if( strBuf != null)
          bwSQL.write( strBuf);
        else
          bwSQL.write( "");
        bwSQL.newLine();
        bwSQL.write( "<ESQL>");
        bwSQL.newLine();
        strBuf = elem.getTableNames();
        if( strBuf != null)
          bwSQL.write( strBuf);
        else
          bwSQL.write( "");
        bwSQL.newLine();
        bwSQL.write( "<ETAB>");
        bwSQL.newLine();
      }
      bwSQL.close();
      iRet = GlobalAppProperties.instance().OK;
    }
    catch(IOException e)
    {
      GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT).println( strFile + ": file write error!");
      //System.out.println( strFile + ": file write error!");
    }

    return iRet;
  }

  /**
   * Save history SQL statements to file.
   *
   * @return		Error code.
   */
  public int saveHistListElems()
  {
    int i;
    int iRet = GlobalAppProperties.instance().ERRORFILE;
    BufferedWriter bwSQL;
    String strBuf = null;
    String strFile = getAliasName() + "." + HISTFILEEXT;

    try
    {
      bwSQL = new BufferedWriter( new FileWriter( ConfigPathUtil.getCurrentSavePath( DbTtvDesktop.DBTTVDT, strFile)));
      for( i = 0; i < m_defHistLM.size(); i++)
      {
        ListElem elem = (ListElem)m_defHistLM.get( i);
        strBuf = elem.getSqlStatement();
        if( strBuf != null)
          bwSQL.write( strBuf);
        else
          bwSQL.write( "");
        bwSQL.newLine();
        bwSQL.write( "<ESQL>");
        bwSQL.newLine();
        strBuf = elem.getTableNames();
        if( strBuf != null)
          bwSQL.write( strBuf);
        else
          bwSQL.write( "");
        bwSQL.newLine();
        bwSQL.write( "<ETAB>");
        bwSQL.newLine();
      }
      bwSQL.close();
      iRet = GlobalAppProperties.instance().OK;
    }
    catch(IOException e)
    {
      GlobalAppProperties.instance().getPrintStreamOutput( DbTtvDesktop.DBTTVDT).println( strFile + ": file write error!");
      //System.out.println( strFile + ": file write error!");
    }

    return iRet;
  }

  static public DBAlias[] loadDBAliases()
  {
    AppProperties appProps = new AppProperties();
    Vector vecAlias = new Vector();
    appProps.load( ConfigPathUtil.getCurrentReadPath( DbTtvDesktop.DBTTVDT, PROPSFILE));
    // DB driver information
    String strDBAliasName;
    String strDBDriver;
    String strDBDriverClass;
    String strDBUrl;
    String strDBUser;
    int i = 0;
    String strAliasName = DBALIASNAME + (new Integer( i)).toString();
    String strDriver = DBDRIVER + (new Integer( i)).toString();
    String strDriverClass = DBDRIVERCLASS + (new Integer( i)).toString();
    String strUrl = DBURL + (new Integer( i)).toString();
    String strUser = DBUSER + (new Integer( i)).toString();
    while( (strDBAliasName = appProps.getProperty( strAliasName)) != null)
    {
      strDBDriver = appProps.getProperty( strDriver);
      strDBDriverClass = appProps.getProperty( strDriverClass);
      strDBUrl = appProps.getProperty( strUrl);
      strDBUser = appProps.getProperty( strUser);
      DBAlias dbd = new DBAlias( strDBAliasName, strDBDriver, strDBDriverClass, strDBUrl, strDBUser);
      vecAlias.add( dbd);
      i++;
      strAliasName = DBALIASNAME + (new Integer( i)).toString();
      strDriver = DBDRIVER + (new Integer( i)).toString();
      strDriverClass = DBDRIVERCLASS + (new Integer( i)).toString();
      strUrl = DBURL + (new Integer( i)).toString();
      strUser = DBUSER + (new Integer( i)).toString();
    }
    int iLen = vecAlias.size();
    DBAlias dbds[] = new DBAlias[ iLen];
    for( i = 0; i < iLen; ++i)
    {
      dbds[i] = (DBAlias)vecAlias.elementAt( i);
    }
    return dbds;
  }

  static public void saveDBAliases( DBAlias[] dbaliases)
  {
    AppProperties appProps = new AppProperties();
    String strAliasName;
    String strDriver;
    String strDriverClass;
    String strUrl;
    String strUser;
    // DB driver information
    for( int i = 0; i < Array.getLength( dbaliases); i++)
    {
      strAliasName = DBALIASNAME + (new Integer( i)).toString();
      strDriver = DBDRIVER + (new Integer( i)).toString();
      strDriverClass = DBDRIVERCLASS + (new Integer( i)).toString();
      strUrl = DBURL + (new Integer( i)).toString();
      strUser = DBUSER + (new Integer( i)).toString();
      if( dbaliases[i].getAliasName() != null)
        appProps.setProperty( strAliasName, dbaliases[i].getAliasName());
      if( dbaliases[i].getDBDriver() != null)
        appProps.setProperty( strDriver, dbaliases[i].getDBDriver());
      if( dbaliases[i].getDBDriverClass() != null)
        appProps.setProperty( strDriverClass, dbaliases[i].getDBDriverClass());
      if( dbaliases[i].getDBUrl() != null)
        appProps.setProperty( strUrl, dbaliases[i].getDBUrl());
      if( dbaliases[i].getDBUser() != null)
        appProps.setProperty( strUser, dbaliases[i].getDBUser());
    }
    appProps.store( ConfigPathUtil.getCurrentSavePath( DbTtvDesktop.DBTTVDT, PROPSFILE), PROPSHEADER);
  }
}
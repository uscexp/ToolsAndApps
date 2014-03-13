package haui.app.dbtreetableview;

import haui.util.AppProperties;
import haui.util.ConfigPathUtil;

import java.util.Vector;
import java.lang.reflect.Array;

/**
 *		Module:					DBDriver.java<br>
 *										$Source: $
 *<p>
 *		Description:    DBDriver definitions.<br>
 *</p><p>
 *		Created:				20.09.2002	by	AE
 *</p><p>
 *		@history				20.09.2002	by	AE: Created.<br>
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
public class DBDriver
{
  // Constants
  public static final String DBD = "DbDriver";
  public static final String PROPSFILE = "dbdriver.ppr";
  public static final String PROPSHEADER = "DbTreeTableView db driver properties";
  public static final String DBDRIVERNAME = "DBDriverName";
  public static final String DBDRIVERCLASS = "DBDriverClass";
  public static final String DBDRIVEREXAMPLE = "DBDriverExample";

  // member variables
  String m_strDBDriverName;
  String m_strDBDriverClass;
  String m_strDBDriverExample;


  public DBDriver( String strDBDriverName, String strDBDriverClass, String strDBDriverExample)
  {
    m_strDBDriverName = strDBDriverName;
    m_strDBDriverClass = strDBDriverClass;
    m_strDBDriverExample = strDBDriverExample;
  }

  public DBDriver()
  {
    this( null, null, null);
  }

  public String getDriverName()
  {
    return m_strDBDriverName;
  }

  public void setDriverName( String strDriverName)
  {
    m_strDBDriverName = strDriverName;
  }

  public String getDriverClass()
  {
    return m_strDBDriverClass;
  }

  public void setDriverClass( String strDriverClass)
  {
    m_strDBDriverClass = strDriverClass;
  }

  public String getDriverExample()
  {
    return m_strDBDriverExample;
  }

  public void setDriverExample( String strDriverExample)
  {
    m_strDBDriverExample = strDriverExample;
  }

  public String toString()
  {
    return m_strDBDriverName;
  }

  public Object clone()
  {
    return new DBDriver( m_strDBDriverName, m_strDBDriverClass, m_strDBDriverExample);
  }

  public boolean equals( Object obj)
  {
    if( obj != null && obj instanceof DBDriver)
    {
      DBDriver dbd = (DBDriver)obj;
      if( dbd.getDriverName().equals( getDriverName()) && dbd.getDriverClass().equals( getDriverClass()))
        return true;
    }
    return false;
  }

  static public DBDriver[] loadDBDrivers()
  {
    AppProperties appProps = new AppProperties();
    Vector vecDrv = new Vector();
    appProps.load( ConfigPathUtil.getCurrentReadPath( DbTtvDesktop.DBTTVDT, PROPSFILE));
    // DB driver information
    String strDBDriverName;
    String strDBDriverClass;
    String strDBDriverExample;
    int i = 0;
    String strDriverName = DBDRIVERNAME + (new Integer( i)).toString();
    String strDriverClass = DBDRIVERCLASS + (new Integer( i)).toString();
    String strDriverExample = DBDRIVEREXAMPLE + (new Integer( i)).toString();
    while( (strDBDriverName = appProps.getProperty( strDriverName)) != null)
    {
      strDBDriverClass = appProps.getProperty( strDriverClass);
      strDBDriverExample = appProps.getProperty( strDriverExample);
      DBDriver dbd = new DBDriver( strDBDriverName, strDBDriverClass, strDBDriverExample);
      vecDrv.add( dbd);
      i++;
      strDriverName = DBDRIVERNAME + (new Integer( i)).toString();
      strDriverClass = DBDRIVERCLASS + (new Integer( i)).toString();
      strDriverExample = DBDRIVEREXAMPLE + (new Integer( i)).toString();
    }
    int iLen = vecDrv.size();
    DBDriver dbds[] = new DBDriver[ iLen];
    for( i = 0; i < iLen; ++i)
    {
      dbds[i] = (DBDriver)vecDrv.elementAt( i);
    }
    return dbds;
  }

  static public void saveDBDrivers( DBDriver[] dbdrivers)
  {
    AppProperties appProps = new AppProperties();
    String strDriverName;
    String strDriverClass;
    String strDriverExample;
    // DB driver information
    for( int i = 0; i < Array.getLength( dbdrivers); i++)
    {
      strDriverName = DBDRIVERNAME + (new Integer( i)).toString();
      strDriverClass = DBDRIVERCLASS + (new Integer( i)).toString();
      strDriverExample = DBDRIVEREXAMPLE + (new Integer( i)).toString();
      if( dbdrivers[i].getDriverName() != null)
        appProps.setProperty( strDriverName, dbdrivers[i].getDriverName());
      if( dbdrivers[i].getDriverClass() != null)
        appProps.setProperty( strDriverClass, dbdrivers[i].getDriverClass());
      if( dbdrivers[i].getDriverExample() != null)
        appProps.setProperty( strDriverExample, dbdrivers[i].getDriverExample());
    }
    appProps.store( ConfigPathUtil.getCurrentSavePath( DbTtvDesktop.DBTTVDT, PROPSFILE), PROPSHEADER);
  }
}
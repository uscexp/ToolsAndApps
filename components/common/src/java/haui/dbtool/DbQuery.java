
package haui.dbtool;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 *		Module:					DbQuery.java
 *<p>
 *		Description:		Database control class.
 *										Read, write, explore databases.
 *</p><p>
 *		Created:				03.03.1998	by	AE
 *</p><p>
 *		Last Modified:	08.09.2000	by	AE
 *</p><p>
 *		history					08.09.1998	by	AE:	Function getNextId added<br>
 *										15.09.1998	by	AE:	Error correction in function getNextId added<br>
 *										16.09.1998	by	AE:	GetSqlValues expanded with a better date representation<br>
 *																				GetDateFormat & setDateFormat functions added<br>
 *																				CutText function added<br>
 *										25.09.1998	by	AE:	SetExtDebugMode function added<br>
 *										14.10.1998	by	AE:	SetColType functions added<br>
 *										15.10.1998	by	AE:	setNrOfRs function added<br>
 *										16.10.1998	by	AE:	Fix error in getNextId function<br>
 *										19.10.1998	by	AE:	SetColNonEditable functions added<br>
 *										29.10.1998	by	AE:	isColNullable function added<br>
 *										15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *										15.04.1999 - 16.04.1999	by	AE:	Port to JDBC 2.0<br>
 *										05.05.1999	by	AE:	Date SQL representation changed to ISO standard escape sequence<br>
 *										25.05.1999	by	AE:	SQL Statement error in Delete-, Insert-, UpdateRecord (if first column = null) fixed.<br>
 *										08.09.2000	by	AE: Limit rows of result set added.<br>
 *										02.08.2001	by	AE: filterText added, to filter \n in a query string.<br>
 *										13.08.2001	by	AE: setAutoCommit, getAutoCommit, commit, rollback added.<br>
 *										03.09.2001	by	AE: getColumns, getTables, getSchemas,getcatalogs added.<br>
 *</p><p>
 *		todo						testing of the new added features according to JDBC 2.0 specification.<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999,2000
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DbQuery
  extends Object
  implements Constant
{
  boolean m_blDbg;
  boolean m_blExDbg;
  boolean m_blLog;
  boolean m_blLogTable;
  String m_strUid;
  String m_strPwd;
  String m_strUrl;
  String m_strDriver;
  Connection m_con;
  Statement m_stmt[];
  String m_strQuery[];
  Vector m_vecTabs;
  int m_iTabCount;
  Vector m_vecSchemas;
  int m_iSchemaCount;
  int m_iMaxTabs;
  String m_strCols[][];
  String m_strColTabNames[][];
  int m_iColCount[];
  int m_iColTypes[][];
  int m_iColNullable[][];
  Vector m_vecColData;
  ResultSet m_rs[];
  ResultSet m_rsLog;
  boolean m_blWritable[][];
  OutputStream m_writerOut;
  OutputStream m_writerErr;
  int m_iRsCount;
  String m_strDateFormat;
  int m_iMaxRows[];
  boolean m_blAutoCommit = true;
  boolean m_blTransPend = false;
  int[] m_iRowsTreated;

  /**
  Constructor for the class DbQuery.
  */
  public DbQuery()
  {
    super();
    constructor(1);
  }

  /**
  Constructor for the class DbQuery.

  @param iRsCount:			number of result sets
  */
  public DbQuery(int iRsCount)
  {
    super();
    constructor(iRsCount);
  }

  /**
  Constructor for the class DbQuery.

  @param iRsCount:			number of result sets
  */
  public void constructor(int iRsCount)
  {
    m_blDbg = false;
    m_blExDbg = false;
    m_blLog = false;
    m_blLogTable = false;
    m_iRsCount = iRsCount+1;
    m_strUid = "";
    m_strPwd = "";
    m_strUrl = "";
    m_strDriver = "sun.jdbc.odbc.JdbcOdbcDriver";
    m_con = null;
    m_iTabCount = 0;
    m_iMaxTabs = 20;
    m_iColCount = new int[m_iRsCount];
    m_strQuery = new String[m_iRsCount];
    m_strCols = new String[m_iRsCount][];
    m_strColTabNames = new String[m_iRsCount][];
    m_iColCount = new int[m_iRsCount];
    m_iColTypes = new int[m_iRsCount][];
    m_iColNullable = new int[m_iRsCount][];
    m_iMaxRows = new int[m_iRsCount];
    m_writerOut = System.out;
    m_writerErr = System.out;
    m_stmt = new Statement[m_iRsCount];
    m_rs = new ResultSet[m_iRsCount];
    m_rsLog = null;
    m_strDateFormat = null;
    m_blWritable = new boolean[m_iRsCount][];
    m_vecColData = new Vector();
    for(int j = 0; j < m_iRsCount; j++)
      m_vecColData.addElement(new Vector());
  }

  /**
  Set number of result sets.

  @param iRsCount:		Number of result sets to use.
  */
  public void setNrOfRs(int iRsCount)
  {
    try
    {
      for(int i = 0; i < m_iRsCount; i++)
        close(i);

      m_con.close();
      return;
    }
    catch(SQLException sqlexception)
    {
      throwSqlExeption(sqlexception);
    }
    m_iRsCount = iRsCount;
    m_con = null;
    m_iTabCount = 0;
    m_iMaxTabs = 20;
    m_iColCount = new int[m_iRsCount];
    m_strQuery = new String[m_iRsCount];
    m_strCols = new String[m_iRsCount][];
    m_strColTabNames = new String[m_iRsCount][];
    m_iColCount = new int[m_iRsCount];
    m_iColTypes = new int[m_iRsCount][];
    m_iColNullable = new int[m_iRsCount][];
    m_stmt = new Statement[m_iRsCount];
    m_rs = new ResultSet[m_iRsCount];
    m_blWritable = new boolean[m_iRsCount][];
    m_vecColData = new Vector();
    for(int j = 0; j < m_iRsCount; j++)
      m_vecColData.addElement(new Vector());
  }

  /**
  Get number of result sets.

  @return		Count of result sets in use.
  */
  public int getNrOfRs()
  {
    return m_iRsCount;
  }

  /**
  Set debug mode.

  @param blDbg:				Debug mode true or false
  */
  public void setDebugMode(boolean blDbg)
  {
    m_blDbg = blDbg;
  }

  /**
  Set extended debug mode.

  @param blExDbg:				Extended debug mode true or false
  */
  public void setExtDebugMode(boolean blExDbg)
  {
    if( blExDbg)
      m_blDbg = true;
    m_blExDbg = blExDbg;
  }

  /**
  Set log mode.

  @param blLog:				Log mode true or false
  */
  public void setLogMode(boolean blLog)
  {
    m_blLog = blLog;
  }

  /**
  Set OutputStream.

  @param stream:			OutputStream to set.
  */
  public void setOutStream(OutputStream stream)
  {
    if(stream != null)
    {
      m_writerOut = stream;
    }
  }

  /**
  Set error OutputStream.

  @param stream:			Error OutputStream to set.
  */
  public void setErrStream(OutputStream stream)
  {
    if(stream != null)
      m_writerErr = stream;
  }

  /**
  Get OutputStream.

  @return				OutputStream.
  */
  public OutputStream getOutStream()
  {
    return m_writerOut;
  }

  /**
  Get error OutputStream.

  @return				Error OutputStream.
  */
  public OutputStream getErrStream()
  {
    return m_writerErr;
  }

  /**
  Set auto commit mode.

  @param blLog:				Auto commit mode true or false
  */
  public void setAutoCommit( boolean blAutoCommit)
  {
    try
    {
      if( m_con != null && m_con.getAutoCommit() != blAutoCommit)
        m_con.setAutoCommit( blAutoCommit);
      m_blAutoCommit = blAutoCommit;
    }
    catch( SQLException sqlex)
    {
      sqlex.printStackTrace();
    }
  }

  /**
  Get auto commit mode.

  @return				Auto commit mode
  */
  public boolean getAutoCommit()
  {
    try
    {
      if( m_con != null)
        return m_con.getAutoCommit();
      else
        return false;
    }
    catch( SQLException sqlex)
    {
      sqlex.printStackTrace();
    }
    return false;
  }

  /**
  Get transaction status.

  @return				true if a transaction is pending
  */
  public boolean isTransactionPending()
  {
    return m_blTransPend;
  }

  /**
  Execute commit on DB.
  */
  public void commit()
    throws SQLException
  {
    m_con.commit();
    m_blTransPend = false;
  }

  /**
  Execute rollback on DB.
  */
  public void rollback()
    throws SQLException
  {
    m_con.rollback();
    m_blTransPend = false;
  }

  /**
  Set max number of rows in result sets. If 0 result set is not limited

  @param iMaxRows:		Limit to max number of rows in result set.
  @param iIdx:				Index of the result set
  */
  public void setMaxRows( int iMaxRows, int iIdx)
  {
    m_iMaxRows[iIdx] = iMaxRows;
  }

  /**
  Get max number of rows in result sets. If 0 result set is not limited

  @param iIdx:				Index of the result set

  @return		Max number of rows in result sets.
  */
  public int getMaxRows( int iIdx)
  {
    return m_iMaxRows[iIdx];
  }

  /**
  Set date datatype format.

  @param strDateFormat:		Format of the date datatype (default: null).
  */
  public void setDateFormat(String strDateFormat)
  {
    m_strDateFormat = strDateFormat;
  }

  /**
  Get date datatype format.
  */
  public String getDateFormat()
  {
    return m_strDateFormat;
  }

  /**
  Destructor, closes result sets and connection.
  */
  public void finalize()
    throws java.lang.Throwable
  {
    try
    {
      for(int i = 0; i < m_iRsCount; i++)
        close(i);

      m_con.close();
      return;
    }
    catch(SQLException sqlexception)
    {
      throwSqlExeption(sqlexception);
    }
    super.finalize();
  }

  /**
  Get the type of the columns in the result set.

  @param iCIdx:				Index of the column
  @param iIdx:				Index of result set

  @return type of column if index exists else Constant.IDXERR.
  */
  public int getColType(int iCIdx, int iIdx)
  {
    if(iIdx >= m_iRsCount || iIdx <= IDXERR || iCIdx >= m_iColCount[iIdx] || iCIdx <= IDXERR)
      return IDXERR;
    else
      return m_iColTypes[iIdx][iCIdx];
  }

  /**
  Get the type of the columns in the result set.

  @param strColName:	name of the column
  @param iIdx:				Index of result set

  @return type of column if index exists else Constant.IDXERR.
  */
  public int getColType( String strColName, int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return IDXERR;
    }

    int iCIdx = findColIdx( strColName, iIdx);
    return getColType(iCIdx, iIdx);
  }

  /**
  Set the type of a column in the result set.

  @param iType:				Type of the column
  @param iCIdx:				Index of the column
  @param iIdx:				Index of result set

  @return type of column if index exists else Constant.IDXERR.
  */
  public int setColType( int iType, int iCIdx, int iIdx)
  {
    if( iIdx >= m_iRsCount || iIdx <= IDXERR || iCIdx >= m_iColCount[iIdx] || iCIdx <= IDXERR)
      return IDXERR;

    m_iColTypes[iIdx][iCIdx] = iType;
    return OK;
  }

  /**
  Set the type of the columns in the result set.

  @param iType:				Type of the column
  @param strColName:	name of the column
  @param iIdx:				Index of result set

  @return type of column if index exists else Constant.IDXERR.
  */
  public int setColType( int iType, String strColName, int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return IDXERR;
    }

    int iCIdx = findColIdx( strColName, iIdx);
    return setColType( iType, iCIdx, iIdx);
  }

  /**
  Can you put a NULL in the secified column?

  @param iCIdx:				Index of the column
  @param iIdx:				Index of result set

  @return type of column if index exists else Constant.IDXERR.
  */
  public int isColNullable(int iCIdx, int iIdx)
  {
    if( iIdx >= m_iRsCount || iIdx <= IDXERR || iCIdx >= m_iColCount[iIdx] || iCIdx <= IDXERR)
      return IDXERR;
    else
      return m_iColNullable[iIdx][iCIdx];
  }

  /**
  Can you put a NULL in the secified column?

  @param strColName:	name of the column
  @param iIdx:				Index of result set

  @return type of column if index exists else Constant.IDXERR.
  */
  public int isColNullable( String strColName, int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return IDXERR;
    }

    int iCIdx = findColIdx( strColName, iIdx);
    return isColNullable(iCIdx, iIdx);
  }

  /**
  Checks if the columns in the result set is editable.

  @param iCIdx:				Index of the column
  @param iIdx:				Index of result set

  @return true if column is editable else false.
  */
  public boolean isColEditable( int iCIdx, int iIdx)
  {
    if( iIdx >= m_iRsCount || iIdx <= IDXERR || iCIdx >= m_iColCount[iIdx] || iCIdx <= IDXERR)
    {
      return false;
    }

    return m_blWritable[iIdx][iCIdx];
  }

  /**
  Checks if the columns in the result set is editable.

  @param strColName:	name of the column
  @param iIdx:				Index of result set

  @return true if column is editable else false.
  */
  public boolean isColEditable( String strColName, int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return false;
    }

    int iCIdx = findColIdx( strColName, iIdx);
    return isColEditable( iCIdx, iIdx);
  }

  /**
  Sets a column in the result set to non editable.

  @param iCIdx:				Index of the column
  @param iIdx:				Index of result set

  @return Error code ( Success = Constant.OK).
  */
  public int setColNonEditable( int iCIdx, int iIdx)
  {
    if( iIdx >= m_iRsCount || iIdx <= IDXERR || iCIdx >= m_iColCount[iIdx] || iCIdx <= IDXERR)
    {
      return IDXERR;
    }

    m_blWritable[iIdx][iCIdx] = false;
    return OK;
  }

  /**
  Sets a column in the result set to non editable. Must be set after a Query().

  @param strColName:	name of the column
  @param iIdx:				Index of result set

  @return Error code ( Success = Constant.OK).
  */
  public int setColNonEditable( String strColName, int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return IDXERR;
    }

    int iCIdx = findColIdx( strColName, iIdx);
    return setColNonEditable( iCIdx, iIdx);
  }

  /**
  Get the count of the columns in the result set.

  @param iIdx:				Index of result set

  @return Count of columns if index exists else Constant.IDXERR.
  */
  public int getColCount( int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return IDXERR;
    }

    return m_iColCount[iIdx];
  }

  /**
  Get the name of the table with corresponding index.

  @param iIdx:			Index of the table

  @return Name of the table with corresponding index if index exists else null.
  */
  public String getTabName( int iIdx)
  {
    if( iIdx < m_iTabCount)
      return (String)m_vecTabs.elementAt( iIdx);
    else
      return null;
  }

  /**
  Get the count of the tables in DB.

  @return Count of tables.
  */
  public int getTabCount()
  {
    return m_iTabCount;
  }

  /**
  Get the number of rows treated by 'INSERT', 'UPDATE', 'DELETE' statement

  @return Number of rows.
  */
  public int getTreatedRows()
  {
    if( m_iRowsTreated != null && Array.getLength( m_iRowsTreated) > 0)
      return m_iRowsTreated[0];
    return 0;
  }

  /**
  Get the array of number of rows treated by batch execution

  @return Array of number of rows.
  */
  public int[] getArrayTreatedRows()
  {
    return m_iRowsTreated;
  }

  /**
  Get the name of the shema with corresponding index.

  @param iIdx:			Index of the table

  @return Name of the schema with corresponding index if index exists else null.
  */
  public String getSchemaName( int iIdx)
  {
    if( iIdx < getSchemaCount())
      return (String)m_vecSchemas.elementAt( iIdx);
    else
      return null;
  }

  /**
  Get the count of the schemas in DB.

  @return Count of schemas.
  */
  public int getSchemaCount()
  {
    if( m_vecSchemas == null)
      getSchemas();
    return m_iSchemaCount;
  }

  /**
  Get next free id of that table.

  @param strTable:		Table name
  @param strColName:	name of the primary key column

  @return Next free id if primary key column exist else null.
  */
  public BigDecimal getNextId( String strTable, String strColName)
  {
    int iIdx = m_iRsCount-1;
    BigDecimal bdIdx = null;
    int i = 1;

    query( "SELECT " + strColName + " FROM " + strTable + " ORDER BY " + strColName, iIdx);
    int iPrimKey = findColIdx( strColName, iIdx);
    if( iPrimKey == IDXERR)
      return null;
    if( next( iIdx))
    {
      BigDecimal bd = (BigDecimal)getColValue( iPrimKey, iIdx);
      while( bd.equals( new BigDecimal( i)))
      {
        i++;
        if( next( iIdx))
          bd = (BigDecimal)getColValue( iPrimKey, iIdx);
        else
          break;
      }
    }
    bdIdx = new BigDecimal( i);
    return bdIdx;
  }

  /**
  Check if DB connection is closed.

  @return true if connection is closed else false.
  */
  public boolean isClosed()
  {
    boolean blRet = false;

    try
    {
      blRet =  m_con.isClosed();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
    }
    return blRet;
  }

  /**
  Close the result set and statement.

  @param iIdx:				Index of result set
  */
  public void close( int iIdx)
  {
    try
    {
      // Close ResultSet
      if( m_rs[iIdx] !=null)
        m_rs[iIdx].close();
      // Close the statement
      if( m_stmt[iIdx] !=null)
        m_stmt[iIdx].close();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
    }
  }

  /**
  Connect to the database.

  @param strDriver:		DB Driver e.g.: "sun.jdbc.odbc.JdbcOdbcDriver"
  @param strUrl:				DB URL e.g.: "jdbc:odbc:SBV"
  @param strUid:				User id to connect the DB.
  @param strPwd:				Password to connect the DB.

  @return Connection object if connection was successful else null.
  */
  public Connection dbConnect( String strDriver, String strUrl, String strUid, String strPwd)
  {
    if( strUid != "")
      m_strUid = strUid;
    if( strPwd != "")
      m_strPwd = strPwd;
    if( strUrl != "")
      m_strUrl = strUrl;
    if( strDriver != "")
      m_strDriver = strDriver;

    try
    {
      // Load the jdbc driver
      Class.forName ( m_strDriver);

      if( m_blDbg)
        DriverManager.setLogWriter( new PrintWriter( m_writerOut));
      else
        DriverManager.setLogWriter( null);

      // Attempt to connect to a driver.  Each one
      // of the registered drivers will be loaded until
      // one is found that can process this URL
      m_con = DriverManager.getConnection( m_strUrl, m_strUid, m_strPwd);

      m_con.setAutoCommit( m_blAutoCommit);

      // Check for, and display and warnings generated
      // by the connect.
      checkForWarning( m_con.getWarnings());
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
    }
    return m_con;
  }

  /**
  Close connection to database.
  */
  public void closeConnection()
  {
    try
    {
      if( m_con != null)
      {
        if( !m_con.getAutoCommit())
          rollback();

        closeLog();
        while( !m_con.isClosed())
        {
          if( m_blDbg)
          {
            println( "\nDB Connection - Close()\n");
          }
          m_con.close();
        }
      }
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
    }
  }

  /**
  Get all the names of the tables and views in the DB (no system tables).

  @return Error code (0 = success (OK)).
  */
  public int getTabs()
  {
    int iRet = OK;
    ResultSet rsTab;

    try
    {
      String strTypes[];
      //int i;

      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();

      strTypes = new String[2];
      strTypes[0] = "TABLE";
      strTypes[1] = "VIEW";

      // Get Table definition RS
      rsTab = dma.getTables( null, null, null, strTypes);

      m_vecTabs = new Vector();
      // Get Tab names
      for( m_iTabCount = 0; rsTab.next(); m_iTabCount++)
      {
        m_vecTabs.addElement( rsTab.getString("TABLE_NAME"));
        if( m_blDbg)
        {
          if( m_blExDbg)
            println( "\n" + "Tab: " + m_vecTabs.elementAt( m_iTabCount));
        }
      }
      rsTab.close();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Get all the names of the tables and views in the DB (no system tables).

  @param strTypes:		Array of table types
  @param strSchema:		Table schema

  @return Error code (0 = success (OK)).
  */
  public int getTabs( String strTypes[], String strSchema)
  {
    int iRet = OK;
    ResultSet rsTab;

    try
    {
      //int i;

      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();

      // Get Table definition RS
      rsTab = dma.getTables( null, strSchema, null, strTypes);

      m_vecTabs = new Vector();
      // Get Tab names
      for( m_iTabCount = 0; rsTab.next(); m_iTabCount++)
      {
        m_vecTabs.addElement( rsTab.getString("TABLE_NAME"));
        if( m_blDbg)
        {
          if( m_blExDbg)
            println( "\n" + "Tab: " + m_vecTabs.elementAt( m_iTabCount));
        }
      }
      rsTab.close();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Get all the names of the schemas in the DB.

  @return Error code (0 = success (OK)).
  */
  public int getSchemas()
  {
    int iRet = OK;
    ResultSet rsSchema;

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();

      // Get Schema definition RS
      rsSchema = dma.getSchemas();

      m_vecSchemas = new Vector();
      // Get Tab names
      for( m_iSchemaCount = 0; rsSchema.next(); m_iSchemaCount++)
      {
        m_vecSchemas.addElement( rsSchema.getString("TABLE_SCHEM"));
        if( m_blDbg)
        {
          if( m_blExDbg)
            println( "\n" + "Schema: " + m_vecSchemas.elementAt( m_iSchemaCount));
        }
      }
      if( m_iSchemaCount == 0)
        iRet = ERR;
      rsSchema.close();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  public static String filterText( String s)
  {
    String str = new String( s);
    str.replace( '\n', ' ');
    return str;
  }

  /**
  Execute a DB query and creates a result set.

  @param strQuery:		DB query string e.g.: "SELECT * FROM tablename"
  @param iIdx:				Index of result set

  @return Error code (0 = success (OK)).
  */
  public int query( String strQuery, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      int i;
      strQuery = getFirstSqlStatement( strQuery);
      m_strQuery[iIdx] = strQuery;

      if( m_blDbg)
      {
        println( "\n" + strQuery);
      }

      // Create a Statement object so we can submit
      // SQL statements to the driver
      //if( m_stmt[iIdx] == null)
      if( m_stmt[iIdx] !=null)
        m_stmt[iIdx].close();

      m_stmt[iIdx] = m_con.createStatement();

      // Limit result set to max rows
      m_stmt[iIdx].setMaxRows( getMaxRows( iIdx));

      // Submit a query, creating a ResultSet object
      boolean blState = m_stmt[iIdx].execute( filterText( m_strQuery[iIdx]));

      SQLWarning sqlw = m_stmt[iIdx].getWarnings();

      while( sqlw != null)
      {
        SQLException sqlex = sqlw.getNextException();
        errPrintln( sqlex.toString());
        //sqlex.printStackTrace();
        sqlw = sqlw.getNextWarning();
      }

      if( blState)
        m_rs[iIdx] = m_stmt[iIdx].getResultSet();
      else
        return OKNORSET;

      // Get ResulSetMetaData object
      ResultSetMetaData rsma = m_rs[iIdx].getMetaData();
      m_iColCount[iIdx] = rsma.getColumnCount();
      m_strCols[iIdx] = new String[m_iColCount[iIdx]];
      m_strColTabNames[iIdx] = null;
      m_iColTypes[iIdx] = new int[m_iColCount[iIdx]];
      m_iColNullable[iIdx] = new int[m_iColCount[iIdx]];
      m_blWritable[iIdx] = new boolean[m_iColCount[iIdx]];
      //m_vecColData.setElementAt( new Vector(), iIdx);
      Vector vec = (Vector)m_vecColData.elementAt( iIdx);
      vec.removeAllElements();
      vec.setSize( m_iColCount[iIdx]);
      for( i = 0; i < m_iColCount[iIdx]; i++)
      {
        m_strCols[iIdx][i] = rsma.getColumnName(i+1);
        m_iColTypes[iIdx][i] = rsma.getColumnType(i+1);
        m_iColNullable[iIdx][i] = rsma.isNullable(i+1);
        m_blWritable[iIdx][i] = rsma.isWritable(i+1);
        if( m_blDbg)
        {
          if( m_blExDbg)
          {
            println( "ColumnName: " + m_strCols[iIdx][i]);
            println( "ColumnType: " + m_iColTypes[iIdx][i]);
            println( "ColumnIsNullable: " + m_iColNullable[iIdx][i]);
            println( "isWritable: " + m_blWritable[iIdx][i]);
          }
        }
      }

      // Display all columns and rows from the result set
      // dispResultSet( m_rs);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Execute a DB query without result set.

  @param strQuery:		DB query string e.g.: "USE tablename"
  @param iIdx:				Index of result set

  @return Error code (0 = success (OK)).
  */
  public int queryNoReturn( String strQuery, int iIdx)
  {
    int iRet = OKNORSET;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      //int i;

      if( m_blDbg)
      {
        println( "\n" + strQuery);
      }

      strQuery = getFirstSqlStatement( strQuery);

      // Create a Statement object so we can submit
      // SQL statements to the driver
      //if( m_stmt[iIdx] == null)
      if( m_stmt[iIdx] !=null)
        m_stmt[iIdx].close();
      m_stmt[iIdx] = m_con.createStatement();

      // Submit a query, creating a ResultSet object
      m_iRowsTreated = new int[1];
      m_iRowsTreated[0] = m_stmt[iIdx].executeUpdate( filterText( strQuery));

      SQLWarning sqlw = m_stmt[iIdx].getWarnings();

      while( sqlw != null)
      {
        SQLException sqlex = sqlw.getNextException();
        errPrintln( sqlex.toString());
        //sqlex.printStackTrace();
        sqlw = sqlw.getNextWarning();
      }

      if( !getAutoCommit())
        m_blTransPend = true;
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    writeLog( strQuery, iRet);
    return iRet;
  }

  /**
  Execute a DB query batch without result set.

  @param strQuery:		DB query string
  @param iIdx:				Index of result set

  @return Error code (0 = success (OK)).
  */
  public int queryBatch( String strQuery, int iIdx)
  {
    int iRet = OKNORSET;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      //int i;

      if( m_blDbg)
      {
        println( "\n" + strQuery);
      }

      StringTokenizer strtok = getSqlStatements( strQuery);

      // Create a Statement object so we can submit
      // SQL statements to the driver
      //if( m_stmt[iIdx] == null)
      if( m_stmt[iIdx] !=null)
        m_stmt[iIdx].close();
      m_stmt[iIdx] = m_con.createStatement();

      while( strtok.hasMoreTokens())
      {
        String strTmp = strtok.nextToken();
        if( strTmp != null)
          m_stmt[iIdx].addBatch( filterText( strTmp));
      }

      // Submit a query, creating a ResultSet object
      m_iRowsTreated = m_stmt[iIdx].executeBatch();

      SQLWarning sqlw = m_stmt[iIdx].getWarnings();

      while( sqlw != null)
      {
        SQLException sqlex = sqlw.getNextException();
        errPrintln( sqlex.toString());
        //sqlex.printStackTrace();
        sqlw = sqlw.getNextWarning();
      }

      if( !getAutoCommit())
        m_blTransPend = true;
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    writeLog( strQuery, iRet);
    return iRet;
  }

  /**
  Gets the schema names available in this database.
  The results are ordered by schema name.

  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getSchemas( int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      //int i;
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getSchemas();

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets the catalog names available in this database.
  The results are ordered by catalog name.

  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getCatalogs( int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getCatalogs();

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Retrive ResultSetMetaData from ResultSet

  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int postGetResultSet( int iIdx)
  throws SQLException
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    // Get ResulSetMetaData object
    ResultSetMetaData rsma = m_rs[iIdx].getMetaData();
    m_iColCount[iIdx] = rsma.getColumnCount();
    m_strCols[iIdx] = new String[m_iColCount[iIdx]];
    m_strColTabNames[iIdx] = null;
    m_iColTypes[iIdx] = new int[m_iColCount[iIdx]];
    m_iColNullable[iIdx] = new int[m_iColCount[iIdx]];
    m_blWritable[iIdx] = new boolean[m_iColCount[iIdx]];
    //m_vecColData.setElementAt( new Vector(), iIdx);
    Vector vec = (Vector)m_vecColData.elementAt( iIdx);
    vec.removeAllElements();
    vec.setSize( m_iColCount[iIdx]);
    for( int i = 0; i < m_iColCount[iIdx]; i++)
    {
      m_strCols[iIdx][i] = rsma.getColumnName(i+1);
      m_iColTypes[iIdx][i] = rsma.getColumnType(i+1);
      m_iColNullable[iIdx][i] = rsma.isNullable(i+1);
      m_blWritable[iIdx][i] = rsma.isWritable(i+1);
      if( m_blDbg)
      {
        if( m_blExDbg)
        {
          println( "ColumnName: " + m_strCols[iIdx][i]);
          println( "ColumnType: " + m_iColTypes[iIdx][i]);
          println( "ColumnIsNullable: " + m_iColNullable[iIdx][i]);
          println( "isWritable: " + m_blWritable[iIdx][i]);
        }
      }
    }
    return iRet;
  }

  /**
  Gets a description of table columns available in the specified catalog.
  Only column descriptions matching the catalog, schema, table and column name criteria are returned.
  They are ordered by TABLE_SCHEM, TABLE_NAME and ORDINAL_POSITION.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param column:    a column name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getColumns( String catalog, String schema, String table, String column, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strTab = null;
      String strCol = null;
      if( catalog != null)
      {
        strCat = catalog.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      else
        strSchema = "%";
      if( table != null)
      {
        strTab = table.trim();
        strTab = table.toUpperCase();
      }
      else
        strTab = "%";
      if( column != null)
      {
        strCol = column.trim();
        strCol = column.toUpperCase();
      }
      else
        strCol = "%";
      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getColumns( strCat, strSchema, strTab, strCol);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets a description of the access rights for a table's columns.
  Only privileges matching the column name criteria are returned. They are ordered by COLUMN_NAME and PRIVILEGE.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name
  @param column:    a column name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getColumnPrivileges( String catalog, String schema, String table, String column, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strTab = null;
      String strCol = null;
      if( catalog != null)
      {
        strCat = catalog.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      else
        strSchema = "%";
      if( table != null)
      {
        strTab = table.trim();
        strTab = table.toUpperCase();
      }
      else
        strTab = "%";
      if( column != null)
      {
        strCol = column.trim();
        strCol = column.toUpperCase();
      }
      else
        strCol = "%";
      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getColumnPrivileges( strCat, strSchema, strTab, strCol);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets a description of the stored procedures available in a catalog.
  Only procedure descriptions matching the schema and procedure name criteria are returned.
  They are ordered by PROCEDURE_SCHEM, and PROCEDURE_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param procedure: a procedure name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getProcedures( String catalog, String schema, String procedure, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strProc = null;
       if( catalog != null)
      {
        strCat = strCat.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      else
        strSchema = "%";
      if( procedure != null)
      {
        strProc = procedure.trim();
        strProc = procedure.toUpperCase();
      }
      else
        strProc = "%";

      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getProcedures( strCat, strSchema, strProc);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets a description of a catalog's stored procedure parameters and result columns.
  Only descriptions matching the schema, procedure and parameter name criteria are returned.
  They are ordered by PROCEDURE_SCHEM and PROCEDURE_NAME.
  Within this, the return value, if any, is first. Next are the parameter descriptions in call order.
  The column descriptions follow in column number order.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param procedure: a procedure name pattern
  @param column:    a column name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getProcedureColumns( String catalog, String schema, String procedure, String column, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strProc = null;
      String strCol = null;
       if( catalog != null)
      {
        strCat = strCat.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      else
        strSchema = "%";
      if( procedure != null)
      {
        strProc = procedure.trim();
        strProc = procedure.toUpperCase();
      }
      else
        strProc = "%";
      if( column != null)
      {
        strCol = column.trim();
        strCol = column.toUpperCase();
      }
      else
        strCol = "%";

      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getProcedureColumns( strCat, strSchema, strProc, strCol);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Only table descriptions matching the catalog, schema, table name and type criteria are returned.
  They are ordered by TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getTables( String catalog, String schema, String table, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strTab = null;
      //String strCol = null;
      if( catalog != null)
      {
        strCat = strCat.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      if( table != null)
      {
        strTab = table.trim();
        strTab = table.toUpperCase();
      }

      String strTypes[] = new String[7];
      strTypes[0] = "TABLE";
      strTypes[1] = "VIEW";
      strTypes[2] = "SYSTEM TABLE";
      strTypes[3] = "GLOBAL TEMPORARY";
      strTypes[4] = "LOCAL TEMPORARY";
      strTypes[5] = "ALIAS";
      strTypes[6] = "SYNONYM";

      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getTables( strCat, strSchema, strTab, strTypes);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets a description of the access rights for each table available in a catalog.
  Note that a table privilege applies to one or more columns in the table.
  It would be wrong to assume that this priviledge applies to all columns
  (this may be true for some systems but is not true for all.)
  Only privileges matching the schema and table name criteria are returned.
  They are ordered by TABLE_SCHEM, TABLE_NAME, and PRIVILEGE.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getTablePrivileges( String catalog, String schema, String table, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strTab = null;
      //String strCol = null;
      if( catalog != null)
      {
        strCat = strCat.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      if( table != null)
      {
        strTab = table.trim();
        strTab = table.toUpperCase();
      }

      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getTablePrivileges( strCat, strSchema, strTab);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets a description of a table's primary key columns. They are ordered by COLUMN_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getPrimaryKeys( String catalog, String schema, String table, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strTab = null;
      //String strCol = null;
      if( catalog != null)
      {
        strCat = strCat.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      if( table != null)
      {
        strTab = table.trim();
        strTab = table.toUpperCase();
      }

      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getPrimaryKeys( strCat, strSchema, strTab);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets a description of the primary key columns that are referenced by a table's foreign key columns
  (the primary keys imported by a table).
  They are ordered by PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getImportedKeys( String catalog, String schema, String table, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strTab = null;
      //String strCol = null;
      if( catalog != null)
      {
        strCat = strCat.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      if( table != null)
      {
        strTab = table.trim();
        strTab = table.toUpperCase();
      }

      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getImportedKeys( strCat, strSchema, strTab);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets a description of the foreign key columns that reference a table's primary key columns
  (the foreign keys exported by a table).
  They are ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and KEY_SEQ.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getExportedKeys( String catalog, String schema, String table, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strTab = null;
      //String strCol = null;
      if( catalog != null)
      {
        strCat = strCat.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      if( table != null)
      {
        strTab = table.trim();
        strTab = table.toUpperCase();
      }

      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getExportedKeys( strCat, strSchema, strTab);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets a description of a table's indices and statistics.
  They are ordered by NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param unique:    when true, return only indices for unique values;
                    when false, return indices regardless of whether unique or not
  @param approximate: when true, result is allowed to reflect approximate or out of data values;
                    when false, results are requested to be accurate
  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getIndexInfo( String catalog, String schema, String table, boolean unique, boolean approximate, int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      // Get the DatabaseMetaData object
      DatabaseMetaData dma = m_con.getMetaData();
      String strCat = null;
      String strSchema = null;
      String strTab = null;
      //String strCol = null;
      if( catalog != null)
      {
        strCat = strCat.trim();
        strCat = catalog.toUpperCase();
      }
      if( schema != null)
      {
        strSchema = schema.trim();
        strSchema = schema.toUpperCase();
      }
      if( table != null)
      {
        strTab = table.trim();
        strTab = table.toUpperCase();
      }

      if( m_rs[iIdx] != null)
        m_rs[iIdx].close();
      m_rs[iIdx] = dma.getIndexInfo( strCat, strSchema, strTab, unique, approximate);

      iRet = postGetResultSet( iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    return iRet;
  }

  /**
  Gets the name of the SQL cursor used by this ResultSet.
  <p>
  In SQL, a result table is retrieved through a cursor that is named. The current row of a result can be updated or deleted using a positioned update/delete statement that references the cursor name. To insure that the cursor has the proper isolation level to support update, the cursor's select statement should be of the form 'select for update'. If the 'for update' clause is omitted the positioned updates may fail.
  </p><p>
  JDBC supports this SQL feature by providing the name of the SQL cursor used by a ResultSet. The current row of a ResultSet is also the current row of this SQL cursor.
  </p>
  @param iIdx:				Index of the result set

  @return the ResultSet's SQL cursor name
  */
  public String getCursorName( int iIdx)
  {
    String strRet = null;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      return strRet;
    }

    try
    {
      m_rs[iIdx].getCursorName();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      strRet = null;
    }

    return strRet;
  }

  /**
  Get the RS of given index

  @param iIdx:				Index of the result set

  @return the RS
  */
  public ResultSet getResultSet( int iIdx)
  {
    return m_rs[iIdx];
  }

  /**
  JDBC 2.0 Retrieves the current row number. The first row is number 1, the second number 2, and so on.

  @param iIdx:				Index of the result set

  @return the current row number; 0 if there is no current row; IDXERR on wrong result set index; ERR on other error
  */
  public int getRow( int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      iRet = m_rs[iIdx].getRow();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }

    return iRet;
  }

  /**
  Moves the cursor a row in the result set.

  @param iIdx:				Index of result set
  @param iMode:				Move mode for result set
  @param iRow:				Row for the absolute move

  @return Error code (true if the new current row is valid; false if there are no more rows).
  */
  private boolean move( int iIdx, int iMode, int iRow)
  {
    boolean blRet=false;
    int i;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      blRet = false;
      return blRet;
    }

    try
    {
      switch( iMode)
      {
      case FIRST:
        blRet = m_rs[iIdx].first();
        break;

      case NEXT:
        blRet = m_rs[iIdx].next();
        break;

      case PREV:
        blRet = m_rs[iIdx].previous();
        break;

      case LAST:
        blRet = m_rs[iIdx].last();
        break;

      case ABSOLUTE:
        blRet = m_rs[iIdx].absolute( iRow);
        break;

      case RELATIVE:
        blRet = m_rs[iIdx].relative( iRow);
        break;
      }
      if( blRet == false)
        return blRet;

      Vector vec = (Vector)m_vecColData.elementAt( iIdx);
      for( i = 1; i <= m_iColCount[iIdx]; i++)
      {
        Object obj = m_rs[iIdx].getObject(i);
        if( i <= vec.size())
          vec.setElementAt( obj, i-1);
        else
          vec.addElement( obj);

        if( m_blDbg && m_blExDbg)
        {
          println( i + ": " + vec.elementAt( i-1));
        }
      }
      if( m_iColCount[iIdx] == 0)
        blRet = false;
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      blRet = false;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      blRet = false;
    }

    return blRet;
  }

  /**
  JDBC 2.0 Moves the cursor to the front of the result set, just before the first row. Has no effect if the result set contains no rows.

  @param iIdx:				Index of result set

  @return true if the cursor could be positioned; false otherwise
  */
  public boolean beforeFirst( int iIdx)
  {
    boolean blRet = true;

    try
    {
      m_rs[iIdx].beforeFirst();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      blRet = false;
    }
    return blRet;
  }

  /**
  JDBC 2.0 Moves the cursor to the first row in the result set.

  @param iIdx:				Index of result set

  @return true if the cursor is on a row; false otherwise
  */
  public boolean first( int iIdx)
  {
    return move( iIdx, FIRST, 0);
  }

  /**
  Step throu a result set.

  @param iIdx:				Index of result set

  @return true if the cursor is on a row; false otherwise
  */
  public boolean next( int iIdx)
  {
    return move( iIdx, NEXT, 0);
  }

  /**
  JDBC 2.0
  <p>
  Moves the cursor to the previous row in the result set.
  </p><p>
  Note: previous() is not the same as relative(-1) because it makes sense to callprevious() when there is no current row.
  </p>
  @param iIdx:				Index of result set

  @return true if the cursor is on a row; false otherwise
  */
  public boolean previous( int iIdx)
  {
    return move( iIdx, PREV, 0);
  }

  /**
  JDBC 2.0 Moves the cursor to the last row in the result set.

  @param iIdx:				Index of result set

  @return true if the cursor is on a row; false otherwise
  */
  public boolean last( int iIdx)
  {
    return move( iIdx, LAST, 0);
  }

  /**
  JDBC 2.0 Moves the cursor to the end of the result set, just after the last row. Has no effect if the result set contains no rows.

  @param iIdx:				Index of result set
  */
  public boolean afterLast( int iIdx)
  {
    boolean blRet = true;

    try
    {
      m_rs[iIdx].afterLast();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      blRet = false;
    }
    return blRet;
  }

  /**
  JDBC 2.0 Indicates whether the cursor is before the first row in the result set.

  @param iIdx:				Index of the result set

  @return true if the cursor is before the first row, false otherwise. Returns false when the result set contains no rows or the result set index is wrong.

  @return true if the cursor could be positioned; false otherwise
  */
  public boolean isBeforeFirst( int iIdx)
  {
    boolean blRet = true;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      blRet = false;
      return blRet;
    }
    try
    {
      m_rs[iIdx].isBeforeFirst();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      blRet = false;
    }

    return blRet;
  }

  /**
  JDBC 2.0 Indicates whether the cursor is after the last row in the result set.

  @param iIdx:				Index of the result set

  @return true if the cursor is after the last row, false otherwise. Returns false when the result set contains no rows or the result set index is wrong.
  */
  public boolean isAfterLast( int iIdx)
  {
    boolean blRet = true;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      blRet = false;
      return blRet;
    }
    try
    {
      m_rs[iIdx].isAfterLast();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      blRet = false;
    }

    return blRet;
  }

  /**
  JDBC 2.0 Indicates whether the cursor is on the first row of the result set.

  @param iIdx:				Index of the result set

  @return true if the cursor is on the first row, false otherwise.
  */
  public boolean isFirst( int iIdx)
  {
    boolean blRet = true;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      blRet = false;
      return blRet;
    }
    try
    {
      m_rs[iIdx].isFirst();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      blRet = false;
    }

    return blRet;
  }

  /**
  JDBC 2.0 Indicates whether the cursor is on the last row of the result set. Note: Calling the method isLast may be expensive because the JDBC driver might need to fetch ahead one row in order to determine whether the current row is the last row in the result set.

  @param iIdx:				Index of the result set

  @return true if the cursor is on the last row, false otherwise.
  */
  public boolean isLast( int iIdx)
  {
    boolean blRet = true;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      blRet = false;
      return blRet;
    }
    try
    {
      m_rs[iIdx].isLast();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      blRet = false;
    }

    return blRet;
  }

  /**
  JDBC 2.0
  <p>
  Moves the cursor to the given row number in the result set.
  </p><p>
  If the row number is positive, the cursor moves to the given row number with respect to the beginning of the result set. The first row is row 1, the second is row 2, and so on.
  </p><p>
  If the given row number is negative, the cursor moves to an absolute row position with respect to the end of the result set. For example, calling absolute(-1) positions the cursor on the last row, absolute(-2) indicates the next-to-last row, and so on.
  </p><p>
  An attempt to position the cursor beyond the first/last row in the result set leaves the cursor before/after the first/last row, respectively.
  </p><p>
  Note: Calling absolute(1) is the same as calling first(). Calling absolute(-1) is the same as calling last().
  </p>
  @param iIdx:				Index of result set
  @param iRow:				Row to move to in the result set

  @return true if the cursor is on a row; false otherwise
  */
  public boolean absolute( int iIdx, int iRow)
  {
    return move( iIdx, ABSOLUTE, iRow);
  }

  /**
  JDBC 2.0
  <p>
  Moves the cursor a relative number of rows, either positive or negative. Attempting to move beyond the first/last row in the result set positions the cursor before/after the the first/last row. Calling relative(0) is valid, but does not change the cursor position.
  </p><p>
  Note: Calling relative(1) is different from calling next() because is makes sense to call next() when there is no current row, for example, when the cursor is positioned before the first row or after the last row of the result set.
  </p>
  @param iIdx:				Index of result set
  @param iRow:				Rows to move relative to in the result set

  @return true if the cursor is on a row; false otherwise
  */
  public boolean relative( int iIdx, int iRow)
  {
    return move( iIdx, RELATIVE, iRow);
  }

  /**
  JDBC 2.0 Inserts the contents of the insert row into the result set and the database. Must be on the insert row when this method is called.

  @param iIdx:				Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int insertRow( int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      iRet = IDXERR;
      return iRet;
    }
    try
    {
      for( int i = 0; i < m_iColCount[iIdx]; i++)
      {
        m_rs[iIdx].updateObject( i, getColValue( i, iIdx));
      }
      m_rs[iIdx].insertRow();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }

    return iRet;
  }

  /**
  Inserts a new Row in the database.

  <BR><BR>Comment:<BR>
  Only for result sets within one table.

  @param strTable:		Name of the table
  @param iIdx:				Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int insertRecord( String strTable, int iIdx)
  {
    int iRet = OK;
    boolean blFirst = true;
    String strUpdate = null;

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      int i;

      strUpdate = "INSERT INTO " + strTable + " ( ";
      for( i = 0; i < m_iColCount[iIdx]; i++)
      {
        if( getColValue( i, iIdx) != null)
        {
          if( blFirst)
          {
            strUpdate += getColName( i, iIdx);
            blFirst = false;
          }
          else
            strUpdate += ", " + getColName( i, iIdx);
        }
      }
      strUpdate += ")";
      blFirst = true;
      strUpdate += " values( ";
      for( i = 0; i < m_iColCount[iIdx]; i++)
      {
        if( getColValue( i, iIdx) != null)
        {
          if( blFirst)
          {
            strUpdate += getSqlValue( i, iIdx);
            blFirst = false;
          }
          else
            strUpdate += ", " + getSqlValue( i, iIdx);
        }
      }
      strUpdate += " )";

      if( m_blDbg)
      {
        println( "\n" + strUpdate);
      }

      // Create a Statement object so we can submit
      // SQL statements to the driver
      //if( m_stmt[iIdx] == null)
      if( m_stmt[iIdx] !=null)
        m_stmt[iIdx].close();
      m_stmt[iIdx] = m_con.createStatement();

      // Submit a query, creating a ResultSet object
      m_stmt[iIdx].executeUpdate( strUpdate);
      if( !getAutoCommit())
        m_blTransPend = true;
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    writeLog( strUpdate, iRet);
    return iRet;
  }

  /**
  JDBC 2.0 Deletes the current row from the result set and the underlying database. Cannot be called when on the insert row.

  <BR><BR>Comment:<BR>
  Only for result sets within one table.

  @param iIdx:				Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int deleteRow( int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      iRet = IDXERR;
      return iRet;
    }
    try
    {
      m_rs[iIdx].deleteRow();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }

    return iRet;
  }

  /**
  Deletes a Row in the database.

  <BR><BR>Comment:<BR>
  Only for result sets within one table.

  @param strTable:		Name of the table
  @param iIdx:				Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int deleteRecord( String strTable, int iIdx)
  {
    int iRet = OK;
    String strUpdate = "";

    if( iIdx >= m_iRsCount)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      int i;
      boolean blFirst = true;

      // Get ResulSetMetaData object
      //ResultSetMetaData rsma = m_rs[iIdx].getMetaData();
      strUpdate = "DELETE FROM " + strTable;

      strUpdate += " WHERE ";
      for( i = 0; i < m_iColCount[iIdx] && getColValue( i, iIdx) != null; i++)
      {
        if( blFirst)
        {
          strUpdate += getColName( i, iIdx) + " = " + getSqlValue( i, iIdx);
          blFirst = false;
        }
        else
          strUpdate += " and " + getColName( i, iIdx) + " = " + getSqlValue( i, iIdx);
      }

      if( m_blDbg)
      {
        println( "\n" + strUpdate);
      }

      // Create a Statement object so we can submit
      // SQL statements to the driver
      Statement stmt = m_con.createStatement();

      // Submit a query
      stmt.executeUpdate( strUpdate);

      // Close the statement
      stmt.close();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    writeLog( strUpdate, iRet);
    return iRet;
  }

  /**
   * Split semicolon seperated sql queries in tokens without senicilon
   *
   * @param strQuery: semicolon seperated sql queries
   */
  public StringTokenizer getSqlStatements( String strQuery)
  {
    StringTokenizer strtok = new StringTokenizer( strQuery, ";", false);
    return strtok;
  }

  /**
   * Split semicolon seperated sql queries in tokens without senicilon
   *
   * @param strQuery: semicolon seperated sql queries
   */
  public String getFirstSqlStatement( String strQuery)
  {
    StringTokenizer strtok = getSqlStatements( strQuery);
    String strRet = "";

    if( strtok.hasMoreTokens())
      strRet = strtok.nextToken();

    return strRet;
  }

  /**
  Get the sql-string representation of the value of the column with corresponding index.

  @param iCIdx:				Index of the column
  @param iIdx:					Index of the result set

  @return Sql-string which represents the value of the column with corresponding index if index exists else "".
  */
  public String getSqlValue( int iCIdx, int iIdx)
  {
    Object obj;
    String strRet = "";

    if( iIdx >= m_iRsCount)
    {
      strRet = null;
      return strRet;
    }

    Vector vec = (Vector)m_vecColData.elementAt( iIdx);
    if( iCIdx < m_iColCount[iIdx])
      obj = vec.elementAt( iCIdx);
    else
      return strRet;

    if( obj != null)
    {
/*
      if( getColType( iCIdx, iIdx) == Types.DATE || getColType( iCIdx, iIdx) == Types.TIMESTAMP || getColType( iCIdx, iIdx) == Types.TIME)
      {
        if( m_strDriver.indexOf( "OracleDriver") != -1)
        {
          if( m_strDateFormat != null)
            strRet = "TO_DATE('" + obj.toString() + "', '" + m_strDateFormat + "')";
          else
            strRet = "TO_DATE('" + obj.toString() + "')";
        }
        else
          strRet = "'" + ConvertSpecialChars( obj.toString()) + "'";
      }
*/
      if( getColType( iCIdx, iIdx) == Types.DATE)
      {
        strRet = "{d '" + convertSpecialChars( obj.toString()) + "'}";
      }
      else
      {
        if( getColType( iCIdx, iIdx) == Types.TIME)
        {
          strRet = "{t '" + convertSpecialChars( obj.toString()) + "'}";
        }
        else
        {
          if( getColType( iCIdx, iIdx) == Types.TIMESTAMP)
          {
            strRet = "{ts '" + convertSpecialChars( obj.toString()) + "'}";
          }
          else
          {
            if( obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
              strRet = "'" + convertSpecialChars( obj.toString()) + "'";
            else
            {
              strRet = convertSpecialChars( obj.toString());
              if( strRet.equalsIgnoreCase( "true"))
                strRet = "1";
              else
              {
                if( strRet.equalsIgnoreCase( "false"))
                  strRet = "0";
              }
            }
          }
        }
      }
    }
    else
      strRet = "NULL";

    return strRet;
  }

  /**
  Get the value of the column with corresponding index.

  @param iCIdx:				Index of the column
  @param iIdx:					Index of the result set

  @return Object which represents the value of the column with corresponding index if index exists else null.
  */
  public Object getColValue( int iCIdx, int iIdx)
  {
    if( iIdx < m_iRsCount && iIdx > IDXERR && iCIdx < m_iColCount[iIdx] && iCIdx > IDXERR)
    {
      Vector vec = (Vector)m_vecColData.elementAt( iIdx);
      return vec.elementAt( iCIdx);
    }
    else
      return null;
  }

  /**
  Get the value of the column with corresponding name.

  @param strColName:		name of the column
  @param iIdx:					Index of the result set

  @return Object which represents the value of the column with corresponding name if index exists else null.
  */
  public Object getColValue( String strColName, int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return null;
    }

    int iCIdx = findColIdx( strColName, iIdx);
    return getColValue( iCIdx, iIdx);
  }

  /**
  Get the name of the column with corresponding index.

  @param iCIdx:				Index of the column
  @param iIdx:				Index of the result set

  @return Name of the column with corresponding index if index exists else null.
  */
  public String getColName( int iCIdx, int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return null;
    }

    if( iCIdx < m_iColCount[iIdx])
      return m_strCols[iIdx][iCIdx];
    else
      return null;
  }

  /**
  Get the table name of the column with corresponding index.

  @param iCIdx:				Index of the column
  @param iIdx:				Index of the result set

  @return Table name of the column with corresponding index if index exists else null.
  */
  public String getColTabName( int iCIdx, int iIdx)
  {
    String strRet = null;

    if( iIdx >= m_iRsCount || iCIdx >= m_iColCount[iIdx])
    {
      return strRet;
    }

    try
    {
      if( m_strColTabNames[iIdx] == null)
      {
        // Get ResulSetMetaData object
        ResultSetMetaData rsma = m_rs[iIdx].getMetaData();
        m_strColTabNames[iIdx] = new String[m_iColCount[iIdx]];
        for( int i = 0; i < m_iColCount[iIdx]; i++)
        {
          m_strColTabNames[iIdx][i] = rsma.getTableName(i+1);
        }
      }
      strRet = m_strColTabNames[iIdx][iCIdx];
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      strRet = null;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      strRet = null;
    }

    return strRet;
  }

  /**
  Get the table name of the column with corresponding index.

  @param strColName:		name of the column
  @param iIdx:					Index of the result set

  @return Table name of the column with corresponding index if index exists else null.
  */
  public String getColTabName( String strColName, int iIdx)
  {
    if( iIdx >= m_iRsCount)
    {
      return null;
    }

    int iCIdx = findColIdx( strColName, iIdx);

    if( iCIdx == IDXERR)
    {
      return null;
    }
    return getColTabName( iCIdx, iIdx);
  }

  /**
  Get the index of the column with corresponding name.

  @param strColName:		name of the column
  @param iIdx:					Index of the result set

  @return Index of the column with corresponding name if name exists else -1.
  */
  public int findColIdx( String strColName, int iIdx)
  {
    int iRet = IDXERR;

    if( iIdx >= m_iRsCount)
    {
      return iRet;
    }

    for( int i = 0; i < m_iColCount[iIdx]; i++)
    {
      if( strColName.equalsIgnoreCase( m_strCols[iIdx][i]))
      {
        iRet = i;
        break;
      }
    }
    /*
    try
    {
    iRet = m_rs[iIdx].findColumn( strColName);
    }
    catch( SQLException sqlex)
    {
    throwSqlExeption( sqlex);
    iRet = IDXERR;
    }
    */
    return iRet;
  }

  /**
  Set the value of the column with corresponding name, without updating the DB.

  <BR><BR>Comment:<BR>
  Only for result sets within one table.

  @param obj:					Value to set in column
  @param strColName:		name of the column
  @param iIdx:					Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int setColValue( Object obj, String strColName, int iIdx)
  {
    int iCIdx = findColIdx( strColName, iIdx);
    return setColValue( obj, iCIdx, iIdx);
  }

  /**
  Set the value of the column with corresponding index, without updating the DB.

  <BR><BR>Comment:<BR>
  Only for result sets within one table.

  @param obj:					Value to set in column
  @param iCIdx:				Index of the column
  @param iIdx:				Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int setColValue( Object obj, int iCIdx, int iIdx)
  {
    int iRet = OK;

    if( iIdx < m_iRsCount && iIdx > IDXERR && iCIdx < m_iColCount[iIdx] && iCIdx > IDXERR)
    {
      Vector vec = (Vector)m_vecColData.elementAt( iIdx);
      vec.setElementAt( obj, iCIdx);
    }
    else
      iRet = IDXERR;

    return iRet;
  }

  /**
  Set the value of the column with corresponding index, with updating the DB.

  <BR><BR>Comment:<BR>
  Only for result sets within one table.

  @param obj:					Value to set in column
  @param strTable:		Name of the table
  @param iCIdx:				Index of the column
  @param iIdx:				Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int updateColValue( Object obj, String strTable, int iCIdx, int iIdx)
  {
    int iRet = OK;
    String strUpdate = null;

    if( obj == null)
    {
      iRet = NULLERR;
      return iRet;
    }

    if( iIdx >= m_iRsCount || iIdx <= IDXERR || iCIdx >= m_iColCount[iIdx] || iCIdx <= IDXERR)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      int i;
      String strCN;
      String strColName = getColName( iCIdx, iIdx);

      strUpdate = "Update " + strTable + " SET "
        + strColName + " = ";
      if( obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
        strUpdate += "'" + convertSpecialChars( obj.toString()) + "'";
      else
        strUpdate += convertSpecialChars( obj.toString());

      strUpdate += " WHERE ";
      boolean blFirst = true;
      for( i = 0; i < m_iColCount[iIdx]; i++)
      {
        strCN = getColName( i, iIdx);
        if( !strCN.equalsIgnoreCase( strColName) && getColValue( i, iIdx) != null)
        {
          if( blFirst)
          {
            strUpdate += strCN + " = " + getSqlValue( i, iIdx);
            blFirst = false;
          }
          else
            strUpdate += " AND " + strCN + " = " + getSqlValue( i, iIdx);
        }
      }

      if( m_blDbg)
      {
        println( "\n" + strUpdate);
      }

      // Create a Statement object so we can submit
      // SQL statements to the driver
      //if( m_stmt[iIdx] == null)
      if( m_stmt[iIdx] !=null)
        m_stmt[iIdx].close();
      m_stmt[iIdx] = m_con.createStatement();

      // Submit a query, creating a ResultSet object
      m_stmt[iIdx].executeUpdate( strUpdate);

      setColValue( obj, iCIdx, iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    writeLog( strUpdate, iRet);
    return iRet;
  }

  /**
  Set the value of the column with corresponding name, with updating the DB.

  <BR><BR>Comment:<BR>
  Only for result sets within one table.

  @param obj:						Value to set in column
  @param strTable:			Name of the table
  @param strColName:		name of the column
  @param iIdx:					Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int updateColValue( Object obj, String strTable, String strColName, int iIdx)
  {
    int iRet = OK;
    String strUpdate = null;

    if( obj == null)
    {
      iRet = NULLERR;
      return iRet;
    }

    int iCIdx = findColIdx( strColName, iIdx);

    if( iCIdx == IDXERR)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      int i;
      String strCN;

      strUpdate = "Update " + strTable + " SET "
        + strColName + " = ";
      if( obj.getClass().getName().equalsIgnoreCase("java.lang.String"))
        strUpdate += "'" + convertSpecialChars( obj.toString()) + "'";
      else
        strUpdate += convertSpecialChars( obj.toString());

      strUpdate += " WHERE ";
      boolean blFirst = true;
      for( i = 0; i < m_iColCount[iIdx]; i++)
      {
        strCN = getColName( i, iIdx);
        if( !strCN.equalsIgnoreCase( strColName) && getColValue( i, iIdx) != null)
        {
          if( blFirst)
          {
            strUpdate += strCN + " = " + getSqlValue( i, iIdx);
            blFirst = false;
          }
          else
            strUpdate += " AND " + strCN + " = " + getSqlValue( i, iIdx);
        }
      }

      if( m_blDbg)
      {
        println( "\n" + strUpdate);
      }

      // Create a Statement object so we can submit
      // SQL statements to the driver
      //if( m_stmt[iIdx] == null)
      if( m_stmt[iIdx] !=null)
        m_stmt[iIdx].close();
      m_stmt[iIdx] = m_con.createStatement();

      // Submit a query, creating a ResultSet object
      m_stmt[iIdx].executeUpdate( strUpdate);

      setColValue( obj, iCIdx, iIdx);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    writeLog( strUpdate, iRet);
    return iRet;
  }

  /**
  JDBC 2.0 Updates the underlying database with the new contents of the current row. Cannot be called when on the insert row.

  @param iIdx:				Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int updateRow( int iIdx)
  {
    int iRet = OK;

    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      iRet = IDXERR;
      return iRet;
    }
    try
    {
      for( int i = 0; i < m_iColCount[iIdx]; i++)
      {
        m_rs[iIdx].updateObject( i, getColValue( i, iIdx));
      }
      m_rs[iIdx].updateRow();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }

    return iRet;
  }

  /**
  Updating the row current row of the result set with the corresponding primary key.

  <BR><BR>Comment:<BR>
  Only for result sets within one table.

  @param strTable:			Name of the table
  @param strPkColName:	Name of the primary key column
  @param iIdx:					Index of the result set

  @return Error code (0 = success (OK)).
  */
  public int updateRecord( String strTable, String strPkColName, int iIdx)
  {
    int iRet = OK;
    String strUpdate = null;

    int iCIdx = findColIdx( strPkColName, iIdx);

    if( iCIdx == IDXERR)
    {
      iRet = IDXERR;
      return iRet;
    }

    try
    {
      int i;
      String strCN;

      strUpdate = "UPDATE " + strTable + " SET ";
      boolean blFirst = true;
      for( i = 0; i < m_iColCount[iIdx]; i++)
      {
        strCN = getColName( i, iIdx);
        if( !strCN.equalsIgnoreCase( strPkColName))
        {
          if( blFirst)
          {
            strUpdate += strCN + " = " + getSqlValue( i, iIdx);
            blFirst = false;
          }
          else
            strUpdate += ", " + strCN + " = " + getSqlValue( i, iIdx);
        }
      }

      strUpdate += " WHERE " + strPkColName + " = " + getSqlValue( iCIdx, iIdx);

      if( m_blDbg)
      {
        println( "\n" + strUpdate);
      }

      // Create a Statement object so we can submit
      // SQL statements to the driver
      //if( m_stmt[iIdx] == null)
      if( m_stmt[iIdx] !=null)
        m_stmt[iIdx].close();
      m_stmt[iIdx] = m_con.createStatement();

      // Submit a query, creating a ResultSet object
      m_stmt[iIdx].executeUpdate( strUpdate);
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
      iRet = ERR;
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
      iRet = ERR;
    }
    writeLog( strUpdate, iRet);
    return iRet;
  }

  /**
  Gives the values in string representation

  @param iIdx:					Index of the result set

  @return string which represents the row values as string.
  */
  public String toString( int iIdx)
  {
    if( iIdx >= m_iRsCount || iIdx <= IDXERR)
    {
      return null;
    }

    String str = null;
    String strTmp = null;
    Vector vec = (Vector)m_vecColData.elementAt( iIdx);

    for( int i = 0; i < vec.size(); i++)
    {
      strTmp = String.valueOf( vec.elementAt( i));
      if( i == 0)
        str = strTmp;
      else
        str += "\t" + strTmp;
    }
    return str;
  }

  /**
  Convert special caracters.

  @param strSource:			String to convert

  @return Converted string
  */
  static public String convertSpecialChars( String strSource)
  {
    String strRet = "";
    int i;

    if( strSource == null)
    {
      strRet = null;
      return strRet;
    }

    char cArr[] = strSource.toCharArray();

    for( i = 0; i < strSource.length(); i++)
    {
      switch( cArr[i])
      {
      case '\'':
        strRet += '\'';
        strRet += '\'';
        break;

      case '\"':
        strRet += '\"';
        strRet += '\"';
        break;

      default:
        strRet += cArr[i];
        break;
      }
    }
    return strRet;
  }

  /**
  Cut a string to a defined length.

  @param str:			String to cut
  @param iMax:		max length of the string

  @return		string with max length of iMax
  */
  static public String cutText( String str, int iMax)
  {
    String strRet = null;

    if( str != null)
      strRet = str.substring( 0, java.lang.Math.min( str.length(), iMax));

    return strRet;
  }

  String getTables(String s, int i)
  {
    if(s == null)
    {
      //Object obj = null;
      boolean flag = true;
      for(int j = 0; j < m_iColCount[i]; j++)
      {
        String s1 = getColTabName(j, i);
        if(s1 != null && null != null && !s1.equals(null))
          if(flag)
          {
            s = s1;
            flag = false;
          }
          else
          {
            s = s + "," + s1;
          }
        }

    }
    return s;
  }

  /**
  Throws the sql exeption.

  @param sqlexception:			SQLException
  */
  void throwSqlExeption(SQLException sqlexception)
  {
    errPrintln("\n*** SQLException caught ***\n");
    while(sqlexception != null)
    {
      errPrintln("SQLState: " + sqlexception.getSQLState());
      errPrintln("Message:  " + sqlexception.getMessage());
      errPrintln("Vendor:   " + sqlexception.getErrorCode());
      sqlexception = sqlexception.getNextException();
      errPrintln("");
    }

  }

  /**
  Writes a string to the Log table.

  @param strCom:			String to write
  @param iError:			Error code of the command to write into the log.

  @return Error code (0 = success (OK)).
  */
  private int writeLog( String strCom, int iError)
  {
    int iRet = ERR;

    if( m_blLog)
    {
      if( m_blLogTable || checkLogTable() == OK)
      {
        if( m_rsLog != null)
        {
          try
          {
            if( strCom == null)
              strCom = "";
            String str = DbQuery.convertSpecialChars( strCom);
            String strUpdate = "INSERT INTO " + LOGTAB + " ( Usr,Command,Error ) values ( '"
              + m_strUid + "', '" + str.substring( 0, java.lang.Math.min( str.length(), 255)) + "', " + iError + " )";

            if( m_blDbg)
              println( "\n" + strUpdate);

            // Create a Statement object so we can submit
            // SQL statements to the driver
            Statement stmt = m_con.createStatement();

            // Submit a query, creating a ResultSet object
            stmt.executeUpdate( strUpdate);
            iRet = OK;
          }
          catch( SQLException sqlex)
          {
            throwSqlExeption( sqlex);
            iRet = ERR;
          }
          catch( java.lang.Exception ex)
          {
            // Got some other type of exception.  Dump it.
            ex.printStackTrace ();
            iRet = ERR;
          }
        }
      }
    }
    return iRet;
  }

  /**
  Checks if there are a Log table, if m_blLog == true. If not exists will create one.

  @return Error code (0 = success (OK)).
  */
  private int checkLogTable()
  {
    int iRet = ERR;

    if( m_blLog)
    {
      String strTab = null;

      try
      {
        if( m_iTabCount == 0)
        {
          if( getTabs() != OK)
            m_blLog = false;
        }
        for( int i = 0; i < m_iTabCount; i++)
        {
          if( (strTab = getTabName( i)) != null && strTab.equalsIgnoreCase( LOGTAB))
          {
            iRet = OK;
            m_blLogTable = true;
            break;
          }
        }
        if( iRet == OK)
        {
          String strQuery = "SELECT * from " + LOGTAB;

          if( m_blDbg)
            println( "\n" + strQuery);

          // Create a Statement object so we can submit
          // SQL statements to the driver
          Statement stmt = m_con.createStatement();

          // Submit a query, creating a ResultSet object
          m_rsLog = stmt.executeQuery( strQuery);
        }
      }
      catch( SQLException sqlex)
      {
        throwSqlExeption( sqlex);
        m_blLog = false;
        iRet = ERR;
      }
      catch( java.lang.Exception ex)
      {
        // Got some other type of exception.  Dump it.
        ex.printStackTrace ();
        iRet = ERR;
      }
    }
    return iRet;
  }

  /**
  Close LogTable result set.
  */
  private void closeLog()
  {
    try
    {
      // Close ResultSet
      if( m_rsLog != null)
        m_rsLog.close();
    }
    catch( SQLException sqlex)
    {
      throwSqlExeption( sqlex);
    }
    catch( java.lang.Exception ex)
    {
      // Got some other type of exception.  Dump it.
      ex.printStackTrace ();
    }
  }

  //-------------------------------------------------------------------
  // checkForWarning
  // Checks for and displays warnings.  Returns true if a warning
  // existed
  //-------------------------------------------------------------------
  private boolean checkForWarning(SQLWarning sqlwarning)
    throws SQLException
  {
    boolean flag = false;
    if(sqlwarning != null && m_blDbg)
    {
      println("\n *** Warning ***\n");
      flag = true;
      for(; sqlwarning != null; sqlwarning = sqlwarning.getNextWarning())
      {
        println("SQLState: " + sqlwarning.getSQLState());
        println("Message:  " + sqlwarning.getMessage());
        println("Vendor:   " + sqlwarning.getErrorCode());
        println("");
      }

    }
    return flag;
  }

  //-------------------------------------------------------------------
  // dispResultSet
  // Displays all columns and rows in the given result set
  //-------------------------------------------------------------------
//  private void dispResultSet(ResultSet resultset)
//    throws SQLException
//  {
//    ResultSetMetaData resultsetmetadata = resultset.getMetaData();
//    int k = resultsetmetadata.getColumnCount();
//    for(int i = 1; i <= k; i++)
//    {
//      if(i > 1)
//        print(",");
//      print(resultsetmetadata.getColumnLabel(i));
//    }
//
//    print("\n");
//    for(boolean flag = resultset.next(); flag; flag = resultset.next())
//    {
//      for(int j = 1; j <= k; j++)
//      {
//        if(j > 1)
//          print(",");
//        String s = resultset.getString(j);
//        if(!resultset.wasNull())
//          print(s);
//      }
//
//      print("");
//    }
//
//  }

//  private void errPrint( String str)
//  {
//    errWrite( str);
//  }

//  private void print( String str)
//  {
//    write( str);
//  }

  private void errPrintln( String str)
  {
    errWrite( str + "\n");
  }

  private void println( String str)
  {
    write( str + "\n");
  }

  private void errWrite( String str)
  {
    try
    {
      for( int i = 0; i < str.length(); i++)
      {
        m_writerErr.write( str.charAt( i));
      }
    }
    catch( java.io.IOException ex)
    {
      ex.printStackTrace();
    }
  }

  private void write( String str)
  {
    try
    {
      for( int i = 0; i < str.length(); i++)
      {
        m_writerOut.write( str.charAt( i));
      }
    }
    catch( java.io.IOException ex)
    {
      ex.printStackTrace();
    }
  }

  /**
  Get the Class represented by the type of the column with corresponding index.

  @param iCIdx:				Index of the column
  @param iIdx:				Index of the result set

  @return Class represented by the type of the column with corresponding index if index exists else null.
  */
  public Class getColClass( int iCIdx, int iIdx)
  {
    int type;

    if( iIdx >= m_iRsCount || iCIdx >= m_iColCount[iIdx])
    {
      return null;
    }

    type = m_iColTypes[iIdx][iCIdx];

    switch(type)
    {
    case Types.CHAR:
    case Types.VARCHAR:
    case Types.LONGVARCHAR:
      return String.class;

    case Types.BIT:
      return Boolean.class;

    case Types.TINYINT:
    case Types.SMALLINT:
    case Types.INTEGER:
      return Integer.class;

    case Types.BIGINT:
      return Long.class;

    case Types.FLOAT:
    case Types.DOUBLE:
      return Double.class;

    case Types.DATE:
      return java.sql.Date.class;

    case Types.TIME:
      return java.sql.Time.class;

    case Types.TIMESTAMP:
      return java.sql.Timestamp.class;

    default:
      return Object.class;
    }
  }
}

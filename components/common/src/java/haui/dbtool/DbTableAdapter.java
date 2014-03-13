
package haui.dbtool;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * Module:					DBTableAdapter.java <p> Description:		An adaptor, transforming the JDBC interface to the TableModel interface. </p><p> Created:				23.03.1998	by	AE </p><p> Last Modified:	08.09.2000	by	AE </p><p> history					16.09.1998	by	AE:	dbRepresentation expanded with a better date representation<br> GetDateFormat & setDateFormat functions added<br> 25.09.1998	by	AE:	SetExtDebugMode function added<br> 14.10.1998	by	AE:	SetColType + GetColType functions added<br> 19.10.1998	by	AE:	Inserted special treatment for the BIT datatype in the getValueAt function<br> 19.10.1998	by	AE:	setColNonEditable functions added<br> 29.10.1998	by	AE:	Superclass changed in DafaultTableModel<br> 29.10.1998	by	AE:	isColNullable function added<br> 30.10.1998	by	AE:	getDbQuery function added<br> 09.11.1998	by	AE:	fix error in getRowCount function<br> 10.11.1998	by	AE:	getUsePrimKey and setUsePrimKey function added<br> 10.11.1998	by	AE:	setValueAt function with additional strPrimKey added<br> 18.11.1998	by	AE:	error ArrayOutOfBoundException in getValueAt function fixed<br> 15.04.1999	by	AE:	Converted to JDK v1.2<br> 05.05.1999	by	AE:	Date SQL representation changed to ISO standard escape sequence<br> 20.05.1999	by	AE:	Bugfix in setValueAt( Object, int, int);<br> 08.09.2000	by	AE: Limit rows of result set added.<br> 13.08.2001	by	AE: setAutoCommit, getAutoCommit, commit, rollback added.<br> 03.09.2001	by	AE: getColumns, getTables, getSchemas,getcatalogs added.<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999,2000  </p><p>
 * @since  					JDK1.2  </p>
 */
public class DbTableAdapter extends DefaultTableModel
  implements Constant
{
  private static final long serialVersionUID = -6624161085395040146L;
  
  Connection m_con;
  boolean m_blDbg;
  boolean m_blExDbg;
  boolean m_blEditable;
  Class m_classColTypes[];
  Vector m_vecRows;
  DbQuery m_dbqueryTab;
  String m_strTable;
  OutputStream m_printsOut;
  OutputStream m_printsErr;
  String m_strPrimKey;
  boolean m_blUsePrimKey;
  boolean m_blAutoCommit = true;

  /**
    * Constructor for the class DbTableAdapter.
    */
  public DbTableAdapter()
  {
    m_blDbg = false;
    m_blExDbg = false;
    m_blEditable = true;
    m_blUsePrimKey = false;
    m_classColTypes = new Class[0];
  }

  /**
    * Set primary key to use (true) or not (false) with the setValueAt function.
    *
    * @param objPrimKey:	Column name of the primary key.
    * @param blUsePrimKey:	true to use the primary key.
    */
  public void setUsePrimKey( String strPrimKey, boolean blUsePrimKey)
  {
    m_strPrimKey = strPrimKey;
    m_blUsePrimKey = blUsePrimKey;
  }

  /**
    * Get if the primary key is in use (true) or not (false) with the setValueAt function.
    */
  public boolean getUsePrimKey()
  {
    return m_blUsePrimKey;
  }

  /**
    * Set OutputStream for the output of the class DbTableAdapter.
    *
    * @param stream:			OutputStream.
    */
  public void setOutStream(OutputStream stream)
  {
    m_printsOut = stream;
  }

  /**
    * Set OutputStream for the error output of the class DbTableAdapter.
    *
    * @param stream:			OutputStream.
    */
  public void setErrStream(OutputStream stream)
  {
    m_printsErr = stream;
  }

  /**
    * Get OutputStream of the output of the class DbTableAdapter.
    */
  public OutputStream getOutStream()
  {
    if(m_dbqueryTab != null)
      return m_dbqueryTab.getOutStream();
    else
      return null;
  }

  /**
    * Get OutputStream of the error output of the class DbTableAdapter.
    */
  public OutputStream getErrStream()
  {
    if(m_dbqueryTab != null)
      return m_dbqueryTab.getErrStream();
    else
      return null;
  }

  /**
    * Get DbQuery class in class DbTableAdapter.
    */
  public DbQuery getDbQuery()
  {
    return m_dbqueryTab;
  }

  /**
    * Get DbQuery class in class DbTableAdapter.
    */
  public void setDbQuery( DbQuery dbquery)
  {
    m_dbqueryTab = dbquery;
  }

  /**
    * Set auto commit mode.
    *
    * @param blLog:				Auto commit mode true or false
    */
  public void setAutoCommit( boolean blAutoCommit)
  {
    if( m_dbqueryTab != null)
      m_dbqueryTab.setAutoCommit( blAutoCommit);
    m_blAutoCommit = blAutoCommit;
  }

  /**
    * Get auto commit mode.
    *
    * @return				Auto commit mode
    */
  public boolean getAutoCommit()
  {
    if( m_dbqueryTab != null)
      return m_dbqueryTab.getAutoCommit();
    else
      return m_blAutoCommit;
  }

  /**
   * Get transaction status.
   *
   * @return				true if a transaction is pending
   */
  public boolean isTransactionPending()
  {
    if( m_dbqueryTab != null)
      return m_dbqueryTab.isTransactionPending();
    else
      return false;
  }

  /**
    * Execute commit on DB.
    */
  public void commit()
    throws SQLException
  {
    if( m_dbqueryTab != null)
      m_dbqueryTab.commit();
  }

  /**
    * Execute rollback on DB.
    */
  public void rollback()
    throws SQLException
  {
    if( m_dbqueryTab != null)
      m_dbqueryTab.rollback();
  }

  /**
  Set max number of rows in result sets. If 0 result set is not limited

  @param iMaxRows:		Limit to max number of rows in result set.
  */
  public void setMaxRows( int iMaxRows)
  {
    if( m_dbqueryTab != null)
      m_dbqueryTab.setMaxRows( iMaxRows, 0);
  }

  /**
  Get max number of rows in result sets. If 0 result set is not limited

  @return		Max number of rows in result sets.
  */
  public int getMaxRows()
  {
    int iMaxRows = 0;

    if( m_dbqueryTab != null)
      iMaxRows = m_dbqueryTab.getMaxRows( 0);

    return iMaxRows;
  }

  /**
    * Set date datatype format.
    *
    * @param strDateFormat:		Format of the date datatype (default: "DD.MM.YYYY").
    */
  public void setDateFormat(String strDateFormat)
  {
    if( strDateFormat != null)
      m_dbqueryTab.setDateFormat( strDateFormat);
  }

  /**
    * Get date datatype format.
    */
  public String getDateFormat()
  {
    return m_dbqueryTab.getDateFormat();
  }

  /**
    * Connect to the database.
    *
    * @param strDriver:		DB Driver e.g.: "sun.jdbc.odbc.JdbcOdbcDriver"
    * @param strUrl:			DB URL e.g.: "jdbc:odbc:SBV"
    * @param strUid:			User id to connect the DB.
    * @param strPwd:			Password to connect the DB.
    *
    * @return Connection object if connection was successful else null.
    */
  public Connection dbConnect(String strDriver, String strUrl, String strUid, String strPwd)
  {
    m_dbqueryTab = new DbQuery(1);
    if( m_printsOut != null)
      m_dbqueryTab.setOutStream(m_printsOut);
    if( m_printsErr != null)
      m_dbqueryTab.setErrStream(m_printsErr);
    m_dbqueryTab.setAutoCommit( m_blAutoCommit);
    try
    {
      Class.forName(strDriver);
      m_con = m_dbqueryTab.dbConnect(strDriver, strUrl, strUid, strPwd);
    }
    catch(ClassNotFoundException classnotfoundexception)
    {
      errPrintln("Cannot find the database driver classes.");
      errPrintln(classnotfoundexception.toString());
    }
    return m_con;
  }

  /**
    * Set table name(s) necessary for the update and delete SQL statements.
    *
    * @param s:			Table name(s) e.g.: "table11,table2"
    */
  public void setTableName(String s)
  {
    m_strTable = s;
  }

  /**
    * Set log mode.
    *
    * @param blLog:		Log mode true or false
    */
  public void setLogMode(boolean blLog)
  {
    m_dbqueryTab.setLogMode(blLog);
  }

  /**
    * Set debug output mode.
    *
    * @param flag:		If true sets the debug output to on.
    */
  public void setDebugMode(boolean flag)
  {
    m_dbqueryTab.setDebugMode(flag);
    m_blDbg = flag;
  }

  /**
    * Set extended debug output mode.
    *
    * @param flag:		If true sets the extended debug output to on.
    */
  public void setExtDebugMode(boolean flag)
  {
    m_dbqueryTab.setExtDebugMode(flag);
    m_blExDbg = flag;
  }

  /**
    * Set table editable.
    *
    * @param flag:		If true sets the table to editable.
    */
  public void setEditable(boolean flag)
  {
    m_blEditable = flag;
  }

  /**
    * Execute a DB query and creates a result set.
    *
    * @param strQuery:		DB query string e.g.: "SELECT * FROM tablename"
    *
    * @return Error code (0 = success (OK)).
    */
  public int query(String strQuery)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    String strQueryUpper = strQuery.toUpperCase();
    strQueryUpper = strQueryUpper.trim();
    if( strQueryUpper != null &&
      (strQueryUpper.startsWith( "INSERT") || strQueryUpper.startsWith( "UPDATE") || strQueryUpper.startsWith( "DELETE")))
    {
      i = m_dbqueryTab.queryNoReturn( strQuery, 0);
    }
    else
    {
      if((i = m_dbqueryTab.query(strQuery, 0)) == OK)
      {
        postGetResultSet();
      }
    }
    return i;
  }

  /**
    * Execute a DB query and without result set.
    *
    * @param strQuery:		DB query string e.g.: "USE tablename"
    *
    * @return Error code (0 = success (OK)).
    */
  public int queryNoReturn(String strQuery)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    else
    {
      int j = m_dbqueryTab.queryNoReturn(strQuery, 0);
      return j;
    }
  }

  /**
   * Execute a DB query batch without result set.
   *
   * @param strQuery:		DB query string
   *
   * @return Error code (0 = success (OK)).
   */
  public int queryBatch( String strQuery)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    else
    {
      int j = m_dbqueryTab.queryBatch( strQuery, 0);
      return j;
    }
  }

  /**
    * Close the result set, statementent and DB connection.
    */
  public void close()
  {
    m_dbqueryTab.close(0);
    m_dbqueryTab.closeConnection();
  }

  /**
    * Destructor, closes result sets and connection.
    */
  protected void finalize()
    throws java.lang.Throwable
  {
    close();
    super.finalize();
  }

  /**
    * Deletes a Row in the database.
    *
    * <BR><BR>Comment:<BR>
    * Only for result sets within one table.
    *
    * @param iIdx:			index of the row
    * @param strPrim:		Primary Key field name of the table (can be empty).
    *
    * @return Error code (0 = success (OK)).
    */
  public int deleteRecord(int iIdx, String strPrim)
  {
    int j = ERR;
    String s1 = null;
    if(m_strTable != null)
      s1 = m_strTable;
    else
      s1 = m_dbqueryTab.getColTabName(0, 0);
    String s2 = "delete from " + s1 + " where ";
    if(strPrim != null && !strPrim.equals(""))
    {
      int k = getColumnIndex(strPrim);
      s2 = s2 + strPrim + " = " + dbRepresentation(k, getValueAt(iIdx, k));
    }
    else
    {
      for(int l = 0; l < getColumnCount(); l++)
      {
        String s3 = getColumnName(l);
        if(!s3.equals("") && getValueAt(iIdx, l) != null)
        {
          if(l != 0)
            s2 = s2 + " and ";
          s2 = s2 + s3 + " = " + dbRepresentation(l, getValueAt(iIdx, l));
        }
      }

    }
    j = m_dbqueryTab.queryNoReturn(s2, 0);
    if(j == OKNORSET)
    {
      m_vecRows.removeElementAt(iIdx);
      fireTableChanged(null);
    }
    return j;
  }

  /**
    * Get the RS of given index
    *
    * @param iIdx:				Index of the result set
    *
    * @return the RS
    */
  public ResultSet getResultSet( int iIdx)
  {
    if( m_dbqueryTab != null)
      return m_dbqueryTab.getResultSet( iIdx);
    return null;
  }

  /**
    * Get the vector of a row.
    *
    * @param iX:	index of the row
    *
    * @return vector of the row.
    */
  public Vector getRow(int iX)
    throws ArrayIndexOutOfBoundsException
  {
    return (Vector)m_vecRows.elementAt(iX);
  }

  /**
    * Find a row in the database table view.
    *
    * @param obj:			Object to be found.
    * @param iIdx:		Index of the column in the table view to be search in
    *
    * @return Number of the found row or Constant.IDXERR if nothing was found.
    */
  public int findRow(Object obj, int iIdx)
  {
    int j = IDXERR;
    //Object obj1 = null;
    //Object obj2 = null;
    for(int k = 0; k < getRowCount(); k++)
    {
      Vector vector = (Vector)m_vecRows.elementAt(k);
      Object obj3 = vector.elementAt(iIdx);
      if(obj3 == null || !obj3.equals(obj))
        continue;
      j = k;
      break;
    }

    return j;
  }

  /**
    * Find a column in the database table view.
    *
    * @param obj:			Object to be found.
    * @param iIdx:		Index of the row in the table view to be search in
    *
    * @return Number of the found column or Constant.IDXERR if nothing was found.
    */
  public int findColumn(Object obj, int iIdx)
  {
    int j = IDXERR;
    Vector vector = null;
    //Object obj1 = null;
    vector = (Vector)m_vecRows.elementAt(iIdx);
    for(int k = 0; k < getColumnCount(); k++)
    {
      Object obj2 = vector.elementAt(k);
      if(obj2 == null || !obj2.equals(obj))
        continue;
      j = k;
      break;
    }

    return j;
  }

  /**
    * Get the type of the columns in the result set.
    *
    * @param iCIdx:				Index of the column
    * @param iIdx:				Index of result set
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int getColType( int iCIdx, int iIdx)
  {
    return m_dbqueryTab.getColType( iCIdx, iIdx);
  }

  /**
    * Get the type of the columns in the result set.
    *
    * @param strColName:	name of the column
    * @param iIdx:				Index of result set
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int getColType( String strColName, int iIdx)
  {
    return m_dbqueryTab.getColType( strColName, iIdx);
  }

  /**
    * Get the type of the columns in the result set.
    *
    * @param iType:				Type of the column
    * @param iCIdx:				Index of the column
    * @param iIdx:				Index of result set
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int setColType( int iType, int iCIdx, int iIdx)
  {
    return m_dbqueryTab.setColType( iType, iCIdx, iIdx);
  }

  /**
    * Set the type of the columns in the result set.
    *
    * @param iType:				Type of the column
    * @param strColName:	name of the column
    * @param iIdx:				Index of result set
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int setColType( int iType, String strColName, int iIdx)
  {
    return m_dbqueryTab.setColType( iType, strColName, iIdx);
  }

  /**
    * Can you put a NULL in the secified column?
    *
    * @param iCIdx:				Index of the column
    * @param iIdx:				Index of result set
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int isColNullable(int iCIdx, int iIdx)
  {
    return m_dbqueryTab.isColNullable( iCIdx, iIdx);
  }

  /**
    * Can you put a NULL in the secified column?
    *
    * @param strColName:	name of the column
    * @param iIdx:				Index of result set
    *
    * @return type of column if index exists else Constant.IDXERR.
    */
  public int isColNullable( String strColName, int iIdx)
  {
    return m_dbqueryTab.isColNullable( strColName, iIdx);
  }

  /**
    * Get the index of a column.
    *
    * @param strColName:	name of the column
    *
    * @return Index of the found column or Constant.IDXERR if nothing was found.
    */
  public int getColumnIndex(String strColName)
  {
    int i = IDXERR;
    for(int j = 0; j < getColumnCount(); j++)
    {
      if(!getColumnName(j).equalsIgnoreCase(strColName))
        continue;
      i = j;
      break;
    }

    return i;
  }

  /**
    * Get the name of a column index.
    *
    * @param iIdx:	index of the column
    *
    * @return Name of the found column or null if nothing was found.
    */
  public String getColumnName(int iIdx)
  {
    return m_dbqueryTab.getColName(iIdx, 0);
  }

  /**
    * Get the name of the table of a column index.
    *
    * @param strCol:	column name
    *
    * @return Name of the table of the found column or null if nothing was found.
    */
  public String getColumnTableName( String strCol)
  {
    return m_dbqueryTab.getColTabName( strCol, 0);
  }

  /**
    * Get the name of the shema.
    *
    * @return Name of the shema.
    */
  public String getSchemaName()
  {
    return m_dbqueryTab.getSchemaName( 0);
  }

  /**
  Prepare ResultSet data for the jTable

  @return Error code (0 = success (OK)).
  */
  void postGetResultSet()
  {
    m_vecRows = new Vector();
    Vector vector;
    for(; m_dbqueryTab.next(0); m_vecRows.addElement(vector))
    {
      vector = new Vector();
      for(int j = 0; j < m_dbqueryTab.getColCount(0); j++)
        vector.addElement(m_dbqueryTab.getColValue(j, 0));
    }
    fireTableChanged(null);
  }

  /**
  Gets a description of table columns available in the specified catalog.
  Only column descriptions matching the catalog, schema, table and column name criteria are returned.
  They are ordered by TABLE_SCHEM, TABLE_NAME and ORDINAL_POSITION.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern
  @param column:    a column name pattern

  @return Error code (0 = success (OK)).
  */
  public int getColumns( String catalog, String schema, String table, String column)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getColumns( catalog, schema, table, column, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
  Gets a description of the access rights for a table's columns.
  Only privileges matching the column name criteria are returned. They are ordered by COLUMN_NAME and PRIVILEGE.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name
  @param column:    a column name pattern

  @return Error code (0 = success (OK)).
  */
  public int getColumnPrivileges( String catalog, String schema, String table, String column)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getColumnPrivileges( catalog, schema, table, column, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
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

  @return Error code (0 = success (OK)).
  */
  public int getProcedureColumns( String catalog, String schema, String procedure, String column)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getProcedureColumns( catalog, schema, procedure, column, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
  Gets a description of the stored procedures available in a catalog.
  Only procedure descriptions matching the schema and procedure name criteria are returned.
  They are ordered by PROCEDURE_SCHEM, and PROCEDURE_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param procedure: a procedure name pattern

  @return Error code (0 = success (OK)).
  */
  public int getProcedures( String catalog, String schema, String procedure)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getProcedures( catalog, schema, procedure, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
  Only table descriptions matching the catalog, schema, table name and type criteria are returned.
  They are ordered by TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getTables( String catalog, String schema, String table)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getTables( catalog, schema, table, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
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

  @return Error code (0 = success (OK)).
  */
  public int getTablePrivileges( String catalog, String schema, String table)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getTablePrivileges( catalog, schema, table, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
  Gets a description of a table's primary key columns. They are ordered by COLUMN_NAME.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getPrimaryKeys( String catalog, String schema, String table)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getPrimaryKeys( catalog, schema, table, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
  Gets a description of the primary key columns that are referenced by a table's foreign key columns
  (the primary keys imported by a table).
  They are ordered by PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getImportedKeys( String catalog, String schema, String table)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getImportedKeys( catalog, schema, table, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
  Gets a description of the foreign key columns that reference a table's primary key columns
  (the foreign keys exported by a table).
  They are ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and KEY_SEQ.

  @param catalog:   a catalog name; "" retrieves those without a catalog; null means drop catalog name from the selection criteria
  @param schema:    a schema name pattern; "" retrieves those without a schema
  @param table:     a table name pattern

  @return Error code (0 = success (OK)).
  */
  public int getExportedKeys( String catalog, String schema, String table)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getExportedKeys( catalog, schema, table, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
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

  @return Error code (0 = success (OK)).
  */
  public int getIndexInfo( String catalog, String schema, String table, boolean unique, boolean approximate)
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getIndexInfo( catalog, schema, table, unique, approximate, 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
  Gets the schema names available in this database.
  The results are ordered by schema name.

  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getSchemas()
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getSchemas( 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
  Gets the catalog names available in this database.
  The results are ordered by catalog name.

  @param iIdx:			Index of result set

  @return Error code (0 = success (OK)).
  */
  public int getCatalogs()
  {
    int i = ERR;
    if(m_dbqueryTab == null)
    {
      errPrintln("There is no database to execute the query.");
      return i;
    }
    m_vecRows = new Vector();
    if((i = m_dbqueryTab.getCatalogs( 0)) == 0)
    {
      postGetResultSet();
    }
    return i;
  }

  /**
    * Get cell editable status.
    *
    * @param iX:	index of the row
    * @param iY:	index of the column
    *
    * @return true if the cell is editable.
    */
  public boolean isCellEditable(int iX, int iY)
  {
    if(m_blEditable)
      return m_dbqueryTab.isColEditable(iY, 0);
    else
      return false;
  }

  /**
    * Sets a column in the result set to non editable. Must be set after a Query().
    *
    * @param strColName:	name of the column
    *
    * @return Error code ( Success = Constant.OK).
    */
  public int setColNonEditable( String strColName)
  {
    return m_dbqueryTab.setColNonEditable( strColName, 0);
  }

  /**
    * Sets a column in the result set to non editable. Must be set after a Query().
    *
    * @param iIdx:	index of the column
    *
    * @return Error code ( Success = Constant.OK).
    */
  public int setColNonEditable( int iIdx)
  {
    return m_dbqueryTab.setColNonEditable( iIdx, 0);
  }

  /**
    * Get the column count in the table view.
    *
    * @return Number of columns.
    */
  public int getColumnCount()
  {
    return m_dbqueryTab.getColCount(0);
  }

  /**
    * Get the row count in the table view.
    *
    * @return Number of rows.
    */
  public int getRowCount()
  {
    if( m_vecRows != null)
      return m_vecRows.size();
    else
      return 0;
  }

  /**
    * Get the number of rows treted by 'INSERT', 'UPDATE', 'DELETE' statement
    *
    * @return Number of rows.
    */
  public int getTreatedRows()
  {
    if( m_dbqueryTab != null)
      return m_dbqueryTab.getTreatedRows();
    return 0;
  }

  /**
    * Get the array of number of rows treated by batch execution
    *
    * @return Array of number of rows.
    */
  public int[] getArrayTreatedRows()
  {
    if( m_dbqueryTab != null)
      return m_dbqueryTab.getArrayTreatedRows();
    return null;
  }

  /**
    * Gives the values in string representation
    *
    * @param iX:	index of the row
    *
    * @return string which represents the row values as string.
    */
  public String toString( int iX)
  {
    String str = null;
    String strTmp = null;
    if( iX <= getRowCount())
    {
      Vector vector = (Vector)m_vecRows.elementAt(iX);
      for( int i = 0; i < vector.size(); i++)
      {
        strTmp = String.valueOf( vector.elementAt( i));
        if( i == 0)
          str = strTmp;
        else
          str += "\t" + strTmp;
      }
      //str = vector.toString();
    }
    return str;
  }

  /**
    * Get the object of a cell.
    *
    * @param iX:	index of the row
    * @param iY:	index of the column
    *
    * @return object of the cell, or null if the cell doesn't exist.
    */
  public Object getValueAt(int iX, int iY)
    throws ArrayIndexOutOfBoundsException
  {
    Object obj = null;
    try
    {
      if( iX <= getRowCount())
      {
        if( iX >= m_vecRows.size())
          return obj;
        Vector vector = (Vector)m_vecRows.elementAt(iX);
        if( iY <= getColumnCount())
        {
          if( iY >= vector.size())
            return obj;
          obj = vector.elementAt(iY);

          if( obj != null && getColType( iY, 0) == Types.BIT)
          {
            if( obj.getClass() != Class.forName( "java.lang.Boolean"))
            {
              if( ((Number)obj).intValue() == 0)
                return new Boolean( false);
              else
                return new Boolean( true);
            }
          }
        }
        else
          obj = null;
      }
      else
        obj = null;
    }
    catch( java.lang.ClassNotFoundException ex)
    {
      errPrintln( ex.getMessage());
      ex.printStackTrace();
    }
    return obj;
  }

  /**
    * Set the object of a cell.
    *
    * @param obj:	Object to be set
    * @param iX:	index of the row
    * @param iY:	index of the column
    */
  public void setValueAt(Object obj, int iX, int iY)
  {
    boolean blFirst = true;

    if( m_blUsePrimKey && m_strPrimKey != null)
    {
      setValueAt( obj, iX, iY, m_strPrimKey);
      return;
    }

    String s = m_dbqueryTab.getColTabName(iY, 0);
    if(s == null || s != null && s.equals(""))
      s = m_strTable;
    String s1 = getColumnName(iY);
    String s2 = "update " + s + " set " + s1 + " = " + dbRepresentation(iY, obj) + " where ";
    for(int k = 0; k < getColumnCount(); k++)
    {
      String s3 = getColumnName(k);
      if(!s3.equals("") && getValueAt(iX, k) != null)
      {
        if(!blFirst)
          s2 = s2 + " and ";
        s2 = s2 + s3 + " = " + dbRepresentation(k, getValueAt(iX, k));
        blFirst = false;
      }
    }

    if(m_blDbg)
      println( s2);
    m_dbqueryTab.queryNoReturn(s2, 0);
    Vector vector = (Vector)m_vecRows.elementAt(iX);
    vector.setElementAt(obj, iY);
  }

  /**
    * Set the object of a cell.
    *
    * @param obj:	Object to be set
    * @param iX:	index of the row
    * @param iY:	index of the column
    * @param strPrimKey: Primary key of the table
    */
  public void setValueAt(Object obj, int iX, int iY, String strPrimKey)
  {
    if( strPrimKey == null)
    {
      setValueAt( obj, iX, iY);
      return;
    }
    String s = m_dbqueryTab.getColTabName(iY, 0);
    if(s == null || s != null && s.equals(""))
      s = m_strTable;
    String s1 = getColumnName(iY);
    String s2 = "update " + s + " set " + s1 + " = " + dbRepresentation(iY, obj) + " where " + strPrimKey + " = ";
    int iPos = getColumnIndex( strPrimKey);

    if( iPos != Constant.IDXERR)
    {
      Object object = getValueAt(iX, iPos);
      if( object != null)
        s2 += object.toString();
    }

    if(m_blDbg)
      println(s2);
    m_dbqueryTab.queryNoReturn(s2, 0);
    Vector vector = (Vector)m_vecRows.elementAt(iX);
    vector.setElementAt(obj, iY);
  }

    /**
     *  Add a row to the end of the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
  public void addRow(Vector rowData)
  {
    for( int i = 0; i < getColumnCount(); i++)
    {
      if( rowData == null)
        m_dbqueryTab.setColValue( null, i, 0);
      else
        m_dbqueryTab.setColValue( rowData.elementAt(i), i, 0);
    }

    m_dbqueryTab.insertRecord( m_strTable, 0);
    //super.addRow( rowData);
  }

    /**
     *  Add a row to the end of the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
  public void addRow(Object[] rowData)
  {
    super.addRow( rowData);
  }

    /**
     *  Insert a row at <i>row</i> in the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   row             the row index of the row to be inserted
     * @param   rowData         optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
  public void insertRow(int row, Vector rowData)
  {
    for( int i = 0; i < getColumnCount(); i++)
    {
      if( rowData == null)
        m_dbqueryTab.setColValue( null, i, 0);
      else
        m_dbqueryTab.setColValue( rowData.elementAt(i), i, 0);
    }

    m_dbqueryTab.insertRecord( m_strTable, 0);
    //if( super.getRowCount() > row)
      //super.insertRow( row, rowData);
  }

    /**
     *  Insert a row at <i>row</i> in the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   row      the row index of the row to be inserted
     * @param   rowData          optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
  public void insertRow(int row, Object[] rowData)
  {
    super.insertRow( row, rowData);
  }

    /**
     *  Remove the row at <i>row</i> from the model and database.  Notification
     *  of the row being removed will be sent to all the listeners.
     *
     * @param   row      the row index of the row to be removed
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
  public void removeRow(int row)
  {
    //super.removeRow( row);
    deleteRecord( row, "");
  }

  /**
    * Get the class representation of a column.
    *
    * @param iY:	index of the column
    *
    * @return Class representation of a column.
    */
  public Class getColumnClass(int iY)
  {
    return m_dbqueryTab.getColClass(iY, 0);
  }

  /**
    * Get the string representation of a column value.
    *
    * @param column:	index of the column
    * @param value:		object of the column
    *
    * @return String representation of the column values.
    */
  public String dbRepresentation(int column, Object value)
  {
    int type;

    if( value == null || value.toString().equals( "") )
    {
      return "null";
    }

    type = m_dbqueryTab.getColType(column, 0);

    switch(type)
    {
    case Types.INTEGER:
    case Types.DOUBLE:
    case Types.FLOAT:
      return value.toString();

    case Types.BIT:
      return ((Boolean)value).booleanValue() ? "1" : "0";

    case Types.TIME:
      return "{t \'"+DbQuery.convertSpecialChars( value.toString())+"\'}";

    case Types.TIMESTAMP:
      return "{ts \'"+DbQuery.convertSpecialChars( value.toString())+"\'}";

    case Types.DATE:
      return "{d \'"+DbQuery.convertSpecialChars( value.toString())+"\'}";
/*
      if( m_dbqueryTab.m_strDriver.indexOf( "OracleDriver") != -1)
      {
        if( getDateFormat() != null)
          return "TO_DATE('" + value.toString() + "', '" + getDateFormat() + "')"; // This will need some conversion.
        else
          return "TO_DATE('" + value.toString() + "')"; // This will need some conversion.
      }
      else
        return "\'"+m_dbqueryTab.ConvertSpecialChars( value.toString())+"\'";
*/

    case Types.CHAR:
    case Types.VARCHAR:
    case Types.LONGVARCHAR:
      return "\'"+DbQuery.convertSpecialChars( value.toString())+"\'";

    default:
      return value.toString();
    }
  }

//  private void errPrint( String str)
//  {
//    errWrite( str);
//  }
//
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
        m_printsErr.write( str.charAt( i));
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
        m_printsOut.write( str.charAt( i));
      }
    }
    catch( java.io.IOException ex)
    {
      ex.printStackTrace();
    }
  }
}

package haui.app.dbtreetableview;
/**
 *
 *		Module:					ListElem.java
 *<p>
 *		Description:
 *</p><p>
 *		Created:				21.08.2000	by	AE
 *</p><p>
 *		Last Modified:	21.08.2000	by	AE
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000
 *</p><p>
 *		@since					JDK1.1
 *</p>
 */
public class ListElem
{
    String tableNames;
    String sqlStat;

    public ListElem()
    {
    }

    public ListElem( String sql, String tabNames)
    {
      sqlStat = sql;
      tableNames = tabNames;
    }

    public String getTableNames()
    {
      return tableNames;
    }

    public void setTableNames( String tabNames)
    {
      tableNames = tabNames;
    }

    public String getSqlStatement()
    {
      return sqlStat;
    }

    public void setSqlStatement( String sql)
    {
      sqlStat = sql;
    }

    public String toString()
    {
      return sqlStat;
    }

    public boolean equals(Object obj)
    {
      return sqlStat.equals( obj);
    }
}


package haui.dbtool;

import java.awt.Component;
import java.util.Vector;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * Module:					ComboCellEditor.java <p> Description:		Table view CellRenderer class. </p><p> Created:				21.10.1998	by	AE </p><p> Last Modified:	15.04.1999	by	AE </p><p> history					21.10.1998 - 03.11.1998	by	AE:	Create ComboCellEditor basic functionality<br> 04.12.1998	by	AE:	Automatic insert functionality added<br> 15.04.1999	by	AE:	Converted to JDK v1.2<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ComboCellEditor extends DefaultCellEditor
{
  private static final long serialVersionUID = 6360330154557577980L;
  
  javax.swing.JComboBox m_jComboBox;
	Object value;
	Vector m_vecUse;
	DbQuery m_dbquery;
	String m_strQuery;
	String m_strTable;
	String m_strUseColumn;

	/**
		* Constructor of the ComboCellEditor
		*
		* @param strTable:			DB Table
		* @param strUseColumn:	Column to use in ComboBox
		* @param strDriver:			JDBC Driver for the DB connection
		* @param strUrl:				JDBC Url for the DB connection
		* @param strDid:				JDBC login for the DB connection
		* @param strPwd:				JDBC pssword for the DB connection
		*/
	public ComboCellEditor( String strTable, String strUseColumn, boolean blEditable,
		String strDriver, String strUrl, String strUid, String strPwd)
	{
		super(new JComboBox());

		m_strTable = strTable;
		m_strUseColumn = strUseColumn;
		m_strQuery = "SELECT DISTINCT " + strUseColumn + " FROM " + strTable + " ORDER BY " + strUseColumn;
		m_vecUse = new Vector();
		m_dbquery = new DbQuery();
		m_dbquery.dbConnect( strDriver, strUrl, strUid, strPwd);

		setClickCountToStart(2);
		//{{INIT_CONTROLS
		//}}
		m_jComboBox = new javax.swing.JComboBox();
		m_jComboBox.setEditable( blEditable);
		if( m_dbquery.query( m_strQuery, 0) == Constant.OK)
		{
			for( int i = 0; m_dbquery.next(0); i++)
			{
				m_vecUse.addElement( m_dbquery.getColValue( strUseColumn, 0));
				m_jComboBox.addItem( m_vecUse.elementAt( i));
			}
		}

		m_jComboBox.setOpaque(true); //MUST do this for background to show up.
	}

	public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected,
		int row, int column)
	{
		if( value == null)
			value = "";
		int iIdx = m_vecUse.indexOf( value);

		if( iIdx != -1)
		{
			m_jComboBox.setSelectedItem( m_vecUse.elementAt( iIdx));
			this.value = m_vecUse.elementAt( iIdx);
		}

		return m_jComboBox;
	}

	/**
		* Destructor, closes result sets and connection.
		*/
	public void finalize()
  	throws java.lang.Throwable
	{
		m_dbquery.close( 0);
		m_dbquery.closeConnection();
	}

	public Object getCellEditorValue()
	{
		ComboBoxEditor cbeditor = m_jComboBox.getEditor();
		Object obj = cbeditor.getItem();
		int iIdx = m_vecUse.indexOf( obj);

		if( iIdx != -1)
		{
			this.value = m_vecUse.elementAt( iIdx);
			return m_vecUse.elementAt( iIdx).toString();
		}
		else
		{
			this.value = obj;
			if( obj != null)
			{
    			String strVal = null;
    			if( obj.getClass() == String.class)
    				strVal = "'" + DbQuery.convertSpecialChars( obj.toString()) + "'";
    			else
    				strVal = obj.toString();
    			if( !strVal.equals( "''"))
    			{
            		if( m_dbquery.queryNoReturn( "INSERT INTO " + m_strTable + " ( " + m_strUseColumn + " ) VALUES( " + strVal + " )", 0) == Constant.OK)
            		{
            			m_vecUse.addElement( obj);
            			m_jComboBox.addItem( obj);
            		}
            	}
        	}
			return this.value.toString();
		}
	}

	public JComboBox getComboBox()
	{
		return m_jComboBox;
	}

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
	protected void fireEditingStopped()
	{
		super.fireEditingStopped();
	}

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
	protected void fireEditingCanceled()
	{
		super.fireEditingCanceled();
	}
}


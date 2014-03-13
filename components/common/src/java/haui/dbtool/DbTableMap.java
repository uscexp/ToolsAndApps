
package haui.dbtool;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 *		Module:					DbTableMap.java
 *<p>
 *		Description:		In a chain of data manipulators some behaviour is common. TableMap
 *										provides most of this behavour and can be subclassed by filters
 *										that only need to override a handful of specific methods. TableMap 
 *										implements TableModel by routing all requests to its model, and
 *										TableModelListener by routing all events to its listeners. Inserting 
 *										a TableMap which has not been subclassed into a chain of table filters 
 *										should have no effect.
 *</p><p>
 *		Created:				23.03.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DbTableMap extends AbstractTableModel
    implements TableModelListener
{

    public DbTableMap()
    {
    }

    public TableModel getModel()
    {
        return m_model;
    }

    public void setModel(TableModel tablemodel)
    {
        m_model = tablemodel;
        m_model.addTableModelListener(this);
    }

    public Object getValueAt(int i, int j)
		throws ArrayIndexOutOfBoundsException
    {
        return m_model.getValueAt(i, j);
    }

    public void setValueAt(Object obj, int i, int j)
    {
        m_model.setValueAt(obj, i, j);
    }

    public int getRowCount()
    {
        if(m_model == null)
            return 0;
        else
            return m_model.getRowCount();
    }

    public int getColumnCount()
    {
        if(m_model == null)
            return 0;
        else
            return m_model.getColumnCount();
    }

    public String getColumnName(int i)
    {
        return m_model.getColumnName(i);
    }

    public Class getColumnClass(int i)
    {
        return m_model.getColumnClass(i);
    }

    public boolean isCellEditable(int i, int j)
    {
        return m_model.isCellEditable(i, j);
    }

    public void tableChanged(TableModelEvent tablemodelevent)
    {
        fireTableChanged(tablemodelevent);
    }

    static final long serialVersionUID = -8993532701117933615L;
    protected TableModel m_model;
}

package haui.app.filesync;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * Module:      FileSyncTableModel.java<br> $Source: $ <p> Description: FileSyncTableModel.<br> </p><p> Created:     30.09.2004  by AE </p><p>
 * @history      30.09.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.3  </p>
 */
public class FileSyncTableModel
  extends DefaultTableModel
  implements TableModelListener
{
  private static final long serialVersionUID = -5946598066637642748L;
  
  // constants
  public final static String GE = ">";
  public final static String EQ = "=";
  public final static String NOTEQ = "!=";
  public final static String LE = "<";
  public final static String EMPTY = null;

  // member variables
  String m_strAppName;
  Vector m_vecDataContainer = new Vector();
  DateFormat m_df = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT);
  FileSyncTable m_fst;

  public FileSyncTableModel( String strAppName)
  {
    super();
    m_strAppName = strAppName;
  }

  public void setFileSyncTable( FileSyncTable fst)
  {
    m_fst = fst;
  }

  public void addDataContainerToVector( FsDataContainer dc)
  {
    m_vecDataContainer.insertElementAt( dc, 0);
  }

  public void removeDataContainerVectorElements()
  {
    m_vecDataContainer.removeAllElements();
  }

  public void addDataContainerVector( Vector vecDc)
  {
    m_vecDataContainer = vecDc;
  }

  public void addDataContainerArray( FsDataContainer[] dcArray)
  {
    if( dcArray == null)
      return;
    for( int i = 0; i < dcArray.length; i++)
      m_vecDataContainer.add( dcArray[i]);
  }

  public void setDataContainerVector( FsDataContainer[] dcArray)
  {
    m_vecDataContainer.removeAllElements();
    if( dcArray == null)
      return;
    for( int i = 0; i < dcArray.length; i++)
      m_vecDataContainer.add( dcArray[i]);
  }

  public void setDataContainerVector( Vector vecDataContainer)
  {
    m_vecDataContainer = vecDataContainer;
  }

  public FsDataContainer getDataContainerAt( int row)
  {
    return (FsDataContainer)m_vecDataContainer.elementAt( row);
  }

  public FsDataContainer[] getDataContainerArray()
  {
    FsDataContainer dc[] = new FsDataContainer[m_vecDataContainer.size()];
    m_vecDataContainer.toArray( dc);
    return dc;
  }

  public String getColumnName( int column)
  {
    String strName = super.getColumnName( column);

    switch( column)
    {
      case 0:
        strName = "Name";
        break;

      case 1:
        strName = "Size";
        break;

      case 2:
        strName = "Date";
        break;

      case 3:
        strName = "<=>";
        break;

      case 4:
        strName = "Date";
        break;

      case 5:
        strName = "Size";
        break;

      case 6:
        strName = "Name";
        break;
    }
    return strName;
  }

  public int getColumnCount()
  {
    return 7;
  }

  public int getRowCount()
  {
    int iRows = 0;

    if( m_vecDataContainer != null)
      iRows = m_vecDataContainer.size();

    return iRows;
  }


  public void tableChanged( TableModelEvent tablemodelevent )
  {
    fireTableChanged( tablemodelevent );
  }

  public void addRow( FsDataContainer dc)
  {
    //int row = m_vecDataContainer.size();
    m_vecDataContainer.add( dc);
    int iIdx = m_vecDataContainer.indexOf( dc);
    TableModelEvent tme = new TableModelEvent( this, iIdx, iIdx, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
    fireTableChanged( tme);
  }

  public void removeRow( int row)
  {
    m_vecDataContainer.remove( row);
    TableModelEvent tme = new TableModelEvent( this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
    fireTableChanged( tme);
  }

  public void removeRow( FsDataContainer dc)
  {
    m_vecDataContainer.remove( dc);
    forceRedraw();
  }

  public void forceRedraw()
  {
    TableModelEvent tme = new TableModelEvent( this);
    fireTableChanged( tme);
  }

  public boolean isCellEditable(int row, int column)
  {
    boolean blRet = false;

    switch( column)
    {
      case 1:
        blRet = super.isCellEditable( row, column);
        break;

      default:
        blRet = false;
        break;
    }
    return blRet;
  }

  public Object getValueAt( int row, int column)
  {
    if( m_vecDataContainer.size() == 0)
      return null;

    Object objRet = null;
    FsDataContainer dc = (FsDataContainer)m_vecDataContainer.elementAt( row);
    //String strLabel = "";

    switch( column)
    {
      case 0:
        objRet = dc.fiLeft.getName();
        break;

      case 1:
        //objRet = NumberFormat.getInstance().format( fiPair[0].length());
        objRet = new Long( dc.fiLeft.length());
        break;

      case 2:
        objRet = m_df.format( new Date( dc.fiLeft.lastModified()));
        break;

      case 3:
        objRet = dc.iconCompare;
        break;

      case 4:
        objRet = m_df.format( new Date( dc.fiRight.lastModified()));
        break;

      case 5:
        //objRet = NumberFormat.getInstance().format( dc.fiRight.length());
        objRet = new Long( dc.fiRight.length());
        break;

      case 6:
        objRet = dc.fiRight.getName();
        break;
    }
    return objRet;
  }

  public Object getRealValueAt( int row, int column)
  {
    if( m_vecDataContainer.size() == 0)
      return null;

    Object objRet = null;
    FsDataContainer dc = (FsDataContainer)m_vecDataContainer.elementAt( row);

    switch( column)
    {
      case 0:
        objRet = dc.fiLeft.getAbsolutePath();
        break;

      case 1:
      case 5:
        objRet = getValueAt( row, column);
        break;

      case 6:
        objRet = dc.fiRight.getAbsolutePath();
        break;

      case 3:
        objRet = dc.strCompare;
        break;

      case 2:
        objRet = new Date( dc.fiLeft.lastModified());
        break;

      case 4:
        objRet = new Date( dc.fiRight.lastModified());
        break;
    }
    return objRet;
  }

  public void setValueAt( Object aValue, int row, int column)
  {
    if( m_vecDataContainer.size() == 0)
      return;

    FsDataContainer dc = (FsDataContainer)m_vecDataContainer.elementAt( row);

    switch( column)
    {
      case 3:
        dc.strCompare = (String)aValue;
        break;
    }
  }
}

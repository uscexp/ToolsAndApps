package haui.app.fm;

import haui.io.*;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.resource.ResouceManager;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;

/**
 * Module:					FileInfoTableModel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoTableModel.java,v $ <p> Description:    TableModel for a FileInfo table.<br> </p><p> Created:				25.10.2000	by	AE </p><p>
 * @history  				25.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileInfoTableModel.java,v $  Revision 1.3  2004-08-31 16:03:04+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2004-06-22 14:08:55+02  t026843  bigger changes  Revision 1.1  2003-05-28 14:19:45+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:48+02  t026843  Initial revision  Revision 1.2  2002-06-27 15:51:42+02  t026843  sorting expanded and added as 'allways aktivated'  Revision 1.1  2002-06-17 17:19:20+02  t026843  Zip and Jar filetype read only support.  Revision 1.0  2001-07-20 16:34:24+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.3 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoTableModel.java,v 1.3 2004-08-31 16:03:04+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FileInfoTableModel extends DefaultTableModel
{
  private static final long serialVersionUID = -8847850415133990124L;
  
  // constants
  public final static String DRAWER = "drawer.gif";
  public final static String ARCHIVE = "archive.gif";
  public final static String FILE = "file.gif";

  // member variables
  Vector m_vecFileInterface = new Vector();
  ImageIcon m_icoDrawer = ResouceManager.getCommonImageIcon( FileManager.APPNAME, DRAWER);
  ImageIcon m_icoArchive = ResouceManager.getCommonImageIcon( FileManager.APPNAME, ARCHIVE);
  ImageIcon m_icoFile = ResouceManager.getCommonImageIcon( FileManager.APPNAME, FILE);
  DateFormat m_df = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT);
  int m_iSortColumn = -1;
  boolean m_blAsc = true;
  FileInfoTable m_fit;
  int m_colClickCount = 0;
  String m_strNameAdd = "";

  public FileInfoTableModel()
  {
    super();
  }

  public void setFileInfoTable( FileInfoTable fit)
  {
    m_fit = fit;
  }

  public void addFileInterfaceToVector( FileInterface fi)
  {
    m_vecFileInterface.insertElementAt( fi, 0);
  }

  public void removeFileInterfaceVectorElements()
  {
    m_vecFileInterface.removeAllElements();
  }

  public void addFileInterfaceVector( FileInterface[] fileInfoArray)
  {
    if( fileInfoArray == null)
      return;
    for( int i = 0; i < Array.getLength( fileInfoArray); i++)
      m_vecFileInterface.add( fileInfoArray[i]);
  }

  public void setFileInterfaceVector( FileInterface[] fileInfoArray)
  {
    m_vecFileInterface.removeAllElements();
    if( fileInfoArray == null)
      return;
    for( int i = 0; i < Array.getLength( fileInfoArray); i++)
      m_vecFileInterface.add( fileInfoArray[i]);
  }

  public void setFileInterfaceVector( Vector vecFileInterface)
  {
    m_vecFileInterface = vecFileInterface;
  }

  public FileInterface getFileInterfaceAt( int row)
  {
    return (FileInterface)m_vecFileInterface.elementAt( row);
  }

  public FileInterface[] getFileInterfaceArray()
  {
    FileInterface fi[] = new FileInterface[m_vecFileInterface.size()];
    m_vecFileInterface.toArray( fi);
    return fi;
  }

  public String getColumnName( int column)
  {
    String strName = super.getColumnName( column);

    switch( column)
    {
      case 0:
        strName = "";
        break;

      case 1:
        strName = "Name";
        break;

      case 2:
        strName = "Ext";
        break;

      case 3:
        strName = "Size";
        break;

      case 4:
        strName = "Date";
        break;

      case 5:
        strName = "Attr";
        break;
    }
    strName += m_strNameAdd;

    return strName;
  }

  public void sort( int column)
  {
    boolean blChange = true;
    if( column < 0)
    {
      column = m_iSortColumn;
      --m_colClickCount;
      blChange = false;
    }
    int iOldColumn = m_iSortColumn;
    m_iSortColumn = column;
    if( m_iSortColumn < 0)
    {
      sortDirFirst();
      return;
    }
    if( m_iSortColumn > 5)
      return;
    if( m_iSortColumn == 0)
      m_iSortColumn = 1;

    if( iOldColumn == m_iSortColumn)
    {
      ++m_colClickCount;
      if( m_colClickCount > 2)
      {
        m_colClickCount = 0;
        m_strNameAdd = "";
        if( m_fit != null && m_iSortColumn != -1)
          m_fit.getColumnModel().getColumn( m_iSortColumn).setHeaderValue( getColumnName( m_iSortColumn));
        m_iSortColumn = -1;
        sortDirFirst();
        return;
      }
      if( blChange)
        m_blAsc = m_blAsc ? false : true;
      if( m_blAsc)
        m_strNameAdd = " asc";
      else
        m_strNameAdd = " desc";
    }
    else
    {
      m_colClickCount = 1;
      m_blAsc = true;
      m_strNameAdd = "";
      if( m_fit != null && iOldColumn != -1)
        m_fit.getColumnModel().getColumn( iOldColumn).setHeaderValue( getColumnName( iOldColumn));
      m_strNameAdd = " asc";
    }

    if( m_fit != null && m_iSortColumn != -1)
      m_fit.getColumnModel().getColumn( m_iSortColumn).setHeaderValue( getColumnName( m_iSortColumn));
    for( int iEx = 0; iEx < getRowCount(); iEx++)
    {
      int iRow1 = iEx;
      int iRow2 = iEx+1;
      int iLargest = iRow1;

      for( int iIn = iEx; iIn < getRowCount(); iIn++)
      {
        iRow2 = iIn;
        boolean blComp = compareRowsByColumn( iRow2, iLargest, column);
        if( m_blAsc)
        {
          if( blComp)
            iLargest = iRow2;
        }
        else
        {
          if( !blComp)
            iLargest = iRow2;
        }
      }
      FileInterface finfo = (FileInterface)m_vecFileInterface.elementAt( iLargest);
      m_vecFileInterface.remove( iLargest);
      m_vecFileInterface.insertElementAt( finfo, 0);
      if( m_fit != null)
        m_fit.getColumnModel().getColumn( column).setHeaderValue( getColumnName( column));
      //m_fit.getTableHeader().repaint();
      //m_fit.repaint();
    }
    sortDirFirst();
  }

  public void sortDirFirst()
  {
    int iIdx = -1;

    for( int i = 0; i < getRowCount(); i++)
    {
      if( ((FileInterface)m_vecFileInterface.elementAt( i)).isDirectory())
      {
        iIdx = i;
        break;
      }
      else
        break;
    }
    iIdx++;
    for( int i = iIdx; i < getRowCount(); i++)
    {
      if( ((FileInterface)m_vecFileInterface.elementAt( i)).isDirectory())
      {
        FileInterface finfo = (FileInterface)m_vecFileInterface.elementAt( i);
        m_vecFileInterface.remove( i);
        m_vecFileInterface.insertElementAt( finfo, iIdx);
        iIdx++;
      }
      //m_fit.repaint();
    }
  }

  public int getColumnCount()
  {
    return 6;
  }

  public int getRowCount()
  {
    int iRows = 0;

    if( m_vecFileInterface != null)
      iRows = m_vecFileInterface.size();

    return iRows;
  }

  public void addRow( FileInterface finfo)
  {
    //int row = m_vecFileInterface.size();
    m_vecFileInterface.add( finfo);
    int iIdx = m_vecFileInterface.indexOf( finfo);
    TableModelEvent tme = new TableModelEvent( this, iIdx, iIdx, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
    fireTableChanged( tme);
  }

  public void removeRow( int row)
  {
    m_vecFileInterface.remove( row);
    TableModelEvent tme = new TableModelEvent( this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
    fireTableChanged( tme);
  }

  public void forceRedraw()
  {
    TableModelEvent tme = new TableModelEvent( this);
    fireTableChanged( tme);
  }

  /**
   * delete file
   *
   * @param row: row to delete
   *
   * @return true if the file or directory is successfully deleted; false otherwise
   */
  public boolean delete( int row)
  {
    FileInterface fi = (FileInterface)m_vecFileInterface.elementAt( row);
    boolean blRet = FileConnector.deleteRecusively( fi);
    if( blRet)
    {
      removeRow( row);
    }
    return blRet;
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

  public String getName( int row)
  {
    FileInterface finfo = (FileInterface)m_vecFileInterface.elementAt( row);
    return finfo.getName();
  }

  public Object getValueAt( int row, int column)
  {
    if( m_vecFileInterface.size() == 0)
      return null;

    Object objRet = null;
    FileInterface finfo = (FileInterface)m_vecFileInterface.elementAt( row);
    String strLabel = "";

    switch( column)
    {
      case 0:
        if( finfo.isDirectory())
          objRet = m_icoDrawer;
        else if( finfo.isArchive())
          objRet = m_icoArchive;
        else
          objRet = m_icoFile;
        break;

      case 1:
        if( finfo.getName() != null)
        {
          if( finfo.getName().equals( ".") || finfo.getName().equals( ".."))
            objRet = finfo.getName();
          else
          {
            int iIdx = finfo.getName().lastIndexOf( '.');
            if( iIdx == 0)
              objRet = finfo.getName();
            else
            {
              if( iIdx != -1)
                objRet = finfo.getName().substring( 0, iIdx);
              else
                objRet = finfo.getName();
            }
          }
        }
        break;

      case 2:
        if( finfo.getName() != null)
        {
          if( finfo.getName().equals( ".") || finfo.getName().equals( ".."))
            objRet = "";
          else
          {
            int iIdx = finfo.getName().lastIndexOf( '.');
            if( iIdx == 0)
              objRet = "";
            else
            {
              if( iIdx != -1)
                objRet = finfo.getName().substring( iIdx);
            }
          }
        }
        break;

      case 3:
        //objRet = NumberFormat.getInstance().format( finfo.length());
        objRet = new Long( finfo.length());
        break;

      case 4:
        objRet = m_df.format( new Date( finfo.lastModified()));
        break;

      case 5:
        if( finfo.isHidden())
          strLabel += "h";
        else
          strLabel += "-";
        if( finfo.canRead())
          strLabel += "r";
        else
          strLabel += "-";
        if( finfo.canWrite())
          strLabel += "w";
        else
          strLabel += "-";
        objRet = strLabel;
        break;
    }
    return objRet;
  }

  public Object getRealValueAt( int row, int column)
  {
    if( m_vecFileInterface.size() == 0)
      return null;

    Object objRet = null;
    FileInterface finfo = (FileInterface)m_vecFileInterface.elementAt( row);

    switch( column)
    {
      case 0:
        objRet = getValueAt( row, column);
        break;

      case 1:
        objRet = getValueAt( row, column);
        break;

      case 2:
        objRet = getValueAt( row, column);
        break;

      case 3:
        objRet = getValueAt( row, column);
        break;

      case 4:
        objRet = new Date( finfo.lastModified());
        break;

      case 5:
        objRet = getValueAt( row, column);
        break;
    }
    return objRet;
  }

  public void setValueAt( Object aValue, int row, int column)
  {
    if( m_vecFileInterface.size() == 0)
      return;

    FileInterface fi = (FileInterface)m_vecFileInterface.elementAt( row);

    switch( column)
    {
      case 1:
        StringBuffer strbufNewPath = new StringBuffer( fi.getParent());
        if( strbufNewPath.charAt( strbufNewPath.length()-1) != fi.separatorChar())
            strbufNewPath.append( fi.separatorChar());
        strbufNewPath.append( (String)aValue);
        FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
            fi.getConnObj(), 0, 0, FileManager.APPNAME, fi.getAppProperties(), true);
        FileInterface fiNew = FileConnector.createFileInterface( strbufNewPath.toString(), null, false, fic);
        fi.renameTo( fiNew);
        break;
    }
  }

  public boolean compareRowsByColumn(int row1, int row2, int column)
  {
    FileInfoTableModel data = this;

    // Check for nulls

    Object o1 = data.getRealValueAt(row1, column);
    Object o2 = data.getRealValueAt(row2, column);

    if( o1 == null)
      return true;
    else if( o2 == null)
      return false;
    Class type = o1.getClass();

    // If both values are null return 0
    if (o1 == null && o2 == null)
    {
      return false;
    }
    else if (o1 == null)
    { // Define null less than everything.
      return false;
    }
    else if (o2 == null)
    {
      return true;
    }

    /* We copy all returned values from the getValue call in case
    an optimised model is reusing one object to return many values.
    The Number subclasses in the JDK are immutable and so will not be used in
    this way but other subclasses of Number might want to do this to save
    space and avoid unnecessary heap allocation.
    */
    if (type.getSuperclass() == java.lang.Number.class)
    {
      Number n1 = (Number)data.getRealValueAt(row1, column);
      double d1 = n1.doubleValue();
      Number n2 = (Number)data.getRealValueAt(row2, column);
      double d2 = n2.doubleValue();

      if(d1 > d2)
        return true;
      else
        return false;
    }
    else if (type == java.util.Date.class)
    {
      Date d1 = (Date)data.getRealValueAt(row1, column);
      long n1 = d1.getTime();
      Date d2 = (Date)data.getRealValueAt(row2, column);
      long n2 = d2.getTime();

      if (n1 > n2)
        return true;
      else
        return false;
    }
    else if (type == String.class)
    {
      String s1 = (String)data.getRealValueAt(row1, column);
      String s2    = (String)data.getRealValueAt(row2, column);
      if( column == 1)
      {
        if( data.getRealValueAt( row1, 2 ) != null)
          s1 = s1.toString() + data.getRealValueAt( row1, 2 ).toString();
        if( data.getRealValueAt( row2, 2 ) != null)
          s2 = s2.toString() + data.getRealValueAt( row2, 2 ).toString();
      }

      int result = s1.compareTo(s2);

      if (result > 0)
        return true;
      else
        return false;
    }
    else if (type == Boolean.class)
    {
      Boolean bool1 = (Boolean)data.getRealValueAt(row1, column);
      boolean b1 = bool1.booleanValue();
      //Boolean bool2 = (Boolean)data.getRealValueAt(row2, column);
      //boolean b2 = bool2.booleanValue();

      if (b1) // Define false < true
        return true;
      else
        return false;
    }
    else
    {
      Object v1 = data.getRealValueAt(row1, column);
      String s1 = v1.toString();
      Object v2 = data.getRealValueAt(row2, column);
      String s2 = v2.toString();
      int result = s1.compareTo(s2);

      if (result > 0)
        return true;
      else
        return false;
    }
  }
}
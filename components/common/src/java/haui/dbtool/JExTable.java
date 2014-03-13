package haui.dbtool;

import java.awt.Dimension;
import java.awt.Point;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 *		Module:					JExTable.java
 *<p>
 *		Description:
 *</p><p>
 *		Created:				31.08.2000	by	AE
 *</p><p>
 *		Last Modified:	07.09.2000	by	AE
 *</p><p>
 *		@history				31.08.2000	by	AE: New defaultrenderers and -editors for (Date), TimeStamp and Time.<br>
 *										04.09.2000	by	AE: Tableheader popup functionality added.<br>
 *										07.09.2000	by	AE: Chances made for printing functionality in TablePanel.<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class JExTable extends JTable
{
  private static final long serialVersionUID = -1479423840820343309L;
  
  JPopupMenu m_popup;
  String m_column;
  boolean m_blPrinting = false;

  public JExTable()
  {
    super();
  }

  public JExTable(int numRows, int numColumns)
  {
    super( numRows, numColumns);
  }

  public JExTable(final Object[][] rowData, final Object[] columnNames)
  {
    super( rowData, columnNames);
  }

  public JExTable(TableModel dm)
  {
    super( dm);
  }

  public JExTable(TableModel dm, TableColumnModel cm)
  {
    super( dm, cm);
  }

  public JExTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
  {
    super( dm, cm, sm);
  }

  public JExTable(final Vector rowData, final Vector columnNames)
  {
    super( rowData, columnNames);
  }

  /**
    * Set by JTable print()
    *
    * @param blPrinting:	If printing - true, else - false.
    */
  public void setPrinting( boolean blPrinting)
  {
    m_blPrinting = blPrinting;
  }

  /**
    * Is true while JTable is printing.
    */
  public boolean isPrinting()
  {
    return m_blPrinting;
  }

  protected void createDefaultRenderers()
  {
    super.createDefaultRenderers();

    // Objects
    DefaultTableCellRenderer label = new DefaultTableCellRenderer()
    {
      public void setValue(Object value)
      {
        if( (value == null || value.toString().equals( "")) && isPrinting()) // necessary for printing
          setText( " ");
        else
          setText((value == null) ? "" : value.toString());
      }
    };
    setDefaultRenderer(Object.class, label);

    // Numbers
    DefaultTableCellRenderer numberRenderer = new DefaultTableCellRenderer()
    {
      NumberFormat formatter = NumberFormat.getInstance();
      public void setValue(Object value)
      {
        if( (value == null || value.toString().equals( "")) && isPrinting()) // necessary for printing
          setText( " ");
        else
          setText((value == null) ? "" : formatter.format(value));
      }
    };
    numberRenderer.setHorizontalAlignment(JLabel.RIGHT);
    setDefaultRenderer(Number.class, numberRenderer);

    // Dates
    DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer()
    {
      DateFormat formatter = DateFormat.getDateInstance();
      public void setValue(Object value)
      {
        if( (value == null || value.toString().equals( "")) && isPrinting()) // necessary for printing
          setText( " ");
        else
          setText((value == null) ? "" : formatter.format(value));
      }
    };
    dateRenderer.setHorizontalAlignment(JLabel.RIGHT);
    setDefaultRenderer(Date.class, dateRenderer);

    // Timestamp
    DefaultTableCellRenderer dateTimeRenderer = new DefaultTableCellRenderer()
    {
      DateFormat formatter = DateFormat.getDateTimeInstance();
      public void setValue(Object value)
      {
        if( (value == null || value.toString().equals( "")) && isPrinting()) // necessary for printing
          setText( " ");
        else
          setText((value == null) ? "" : formatter.format(value));
      }
    };
    dateTimeRenderer.setHorizontalAlignment(JLabel.RIGHT);
    setDefaultRenderer(Timestamp.class, dateTimeRenderer);

    // Time
    DefaultTableCellRenderer timeRenderer = new DefaultTableCellRenderer()
    {
      DateFormat formatter = DateFormat.getTimeInstance();
      public void setValue(Object value)
      {
        if( (value == null || value.toString().equals( "")) && isPrinting()) // necessary for printing
          setText( " ");
        else
          setText((value == null) ? "" : formatter.format(value));
      }
    };
    timeRenderer.setHorizontalAlignment(JLabel.RIGHT);
    setDefaultRenderer(Time.class, timeRenderer);
  }

  protected void createDefaultEditors()
  {
    super.createDefaultEditors();
    // Date
    setDefaultEditor(Date.class, new DateTimeCellEditor());

    // Timestamp
    setDefaultEditor(Timestamp.class, new DateTimeCellEditor());

    // Time
    setDefaultEditor(Time.class, new DateTimeCellEditor());
  }

  protected JTableHeader createDefaultTableHeader()
  {
    return new JExTableHeader(columnModel);
  }

  public void setPopup( JPopupMenu popup)
  {
    m_popup = popup;
  }

  public JPopupMenu getPopup()
  {
    return m_popup;
  }

  public void showPopup( int x, int y)
  {
    if( m_popup != null)
    {
      m_column = null;
      m_popup.show( getTableHeader(), x, y);
      m_column = getColumnName( getTableHeader().columnAtPoint(new Point( x, y)));
      m_popup.setVisible( true);
      Dimension screenDim = getToolkit().getScreenSize();
      int maxX = screenDim.width;
      int maxY = screenDim.height;
      x = (int)m_popup.getLocationOnScreen().getX();
      y = (int)m_popup.getLocationOnScreen().getY();
      int x1 = x + m_popup.getWidth();
      int y1 = y + m_popup.getHeight();
      boolean blChanged = false;
      if( x < 0)
      {
        blChanged = true;
        x = 0;
      }
      else if( x1 > maxX)
      {
        blChanged = true;
        x -= (x1-maxX);
      }
      if( y < 0)
      {
        blChanged = true;
        y = 0;
      }
      else if( y1 > maxY)
      {
        blChanged = true;
        y -= (y1-maxY);
      }
      if( blChanged)
        m_popup.setLocation( x, y);
    }
  }

  /**
    * Get the name of the column selected by an tableheader popup.
    *
    * @return Name of column or null if nothing was selected.
    */
  public String getSelColumnName()
  {
    return m_column;
  }

/* Workaround for a bug in JDK 1.2
  protected void paintComponent(Graphics g)
  {
    if(getParent() != null)
    {
      Rectangle clip = g.getClipBounds();
      g.setColor(getParent().getBackground());
      g.fillRect(clip.x, clip.y, clip.width, clip.height);
    }
    super.paintComponent(g);
  }
*/
}

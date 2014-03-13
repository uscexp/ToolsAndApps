
package haui.dbtool;

import haui.components.JExDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Types;
import javax.swing.BoxLayout;

/**
 * Module:					DbTableDialog.java <p> Description:		Dialog which implements TablePanel </p><p> Created:				27.10.1998	by	AE </p><p> Last Modified:	15.04.1999	by	AE </p><p> history					27.10.1998 - 11.11.1998	by	AE:	Create DbTableDialog basic functionality<br> 15.04.1999	by	AE:	Converted to JDK v1.2<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999  </p><p>
 * @since  					JDK1.2  </p>
 */
public class DbTableDialog
  extends JExDialog
{
  private static final long serialVersionUID = 5668253378700458405L;
  
  haui.dbtool.TablePanel m_tablePanel;
  javax.swing.JPanel jPanelBut;
  javax.swing.JButton jButtonNew;
  javax.swing.JButton jButtonDelete;
  javax.swing.JButton jButtonOk;
  javax.swing.JButton jButtonClear;
  javax.swing.JButton jButtonCancel;

  // Used for addNotify check.
  boolean fComponentsAdjusted = false;

  String m_strTable;
  String m_strResultColumn;
  String m_strDriver;
  String m_strUrl;
  String m_strUsr;
  String m_strPwd;
  Object m_objValue;
  Object m_objResultValue;
  boolean m_blEditable = true;
  boolean m_blDbg = false;
  boolean m_blExDbg = false;
  OutputStream m_printsOut;
  OutputStream m_printsErr;
  DbTableCellEditor m_tce;

  public DbTableDialog( String strAppName)
  {
    this( null, strAppName);
  }

  public DbTableDialog(String sTitle, String strAppName)
  {
    this( sTitle, null, null, null, true, true, true, true, true, null, null, null, null, strAppName);
  }

  public DbTableDialog( String strDlgTitle, String strQuery, String strTable, String strResultColumn, boolean blOk, boolean blClear, boolean blCancel, boolean blNew, boolean blDelete,
    String strDriver, String strUrl, String strUid, String strPwd, String strAppName)
  {
    super( strAppName);
    Constructor( strDlgTitle, strQuery, strTable, strResultColumn, blOk, blClear, blCancel, blNew, blDelete,
                 strDriver, strUrl, strUid, strPwd, strAppName);
  }

  public void Constructor( String strDlgTitle, String strQuery, String strTable, String strResultColumn, boolean blOk, boolean blClear, boolean blCancel, boolean blNew, boolean blDelete,
    String strDriver, String strUrl, String strUid, String strPwd, String strAppName)
  {
    //{{INIT_CONTROLS
    //}}
    getContentPane().setLayout(new BorderLayout(0,0));
    setVisible(false);
    setSize(405,305);
    m_tablePanel = new haui.dbtool.TablePanel( strAppName);
    //m_tablePanel.setLayout(new BorderLayout(0,0));
    m_tablePanel.setBounds(0,0,1065,712);
    m_tablePanel.setFont(new Font("Dialog", Font.PLAIN, 12));
    m_tablePanel.setForeground(new Color(0));
    m_tablePanel.setBackground(new Color(-3355444));
    getContentPane().add("Center", m_tablePanel);
    jPanelBut = new javax.swing.JPanel();
    jPanelBut.setAlignmentY(0.0F);
    jPanelBut.setLayout(new BoxLayout(jPanelBut,BoxLayout.X_AXIS));
    jPanelBut.setBounds(0,280,405,25);
    jPanelBut.setFont(new Font("Dialog", Font.PLAIN, 12));
    jPanelBut.setForeground(new Color(0));
    jPanelBut.setBackground(new Color(-3355444));
    getContentPane().add("South", jPanelBut);
    jButtonNew = new javax.swing.JButton();
    jButtonNew.setText("New");
    jButtonNew.setActionCommand("New");
    jButtonNew.setBounds(0,0,59,25);
    jButtonNew.setFont(new Font("Dialog", Font.BOLD, 12));
    jButtonNew.setForeground(new Color(0));
    jButtonNew.setBackground(new Color(-3355444));
    jPanelBut.add(jButtonNew);
    jButtonDelete = new javax.swing.JButton();
    jButtonDelete.setText("Delete");
    jButtonDelete.setActionCommand("Delete");
    jButtonDelete.setBounds(59,0,71,25);
    jButtonDelete.setFont(new Font("Dialog", Font.BOLD, 12));
    jButtonDelete.setForeground(new Color(0));
    jButtonDelete.setBackground(new Color(-3355444));
    jPanelBut.add(jButtonDelete);
    jButtonOk = new javax.swing.JButton();
    jButtonOk.setText("Ok");
    jButtonOk.setActionCommand("Ok");
    jButtonOk.setBounds(130,0,51,25);
    jButtonOk.setFont(new Font("Dialog", Font.BOLD, 12));
    jButtonOk.setForeground(new Color(0));
    jButtonOk.setBackground(new Color(-3355444));
    jPanelBut.add(jButtonOk);
    jButtonClear = new javax.swing.JButton();
    jButtonClear.setText("Clear");
    jButtonClear.setActionCommand("Clear");
    jButtonClear.setBounds(181,0,73,25);
    jButtonClear.setFont(new Font("Dialog", Font.BOLD, 12));
    jButtonClear.setForeground(new Color(0));
    jButtonClear.setBackground(new Color(-3355444));
    jPanelBut.add(jButtonClear);
    jButtonCancel = new javax.swing.JButton();
    jButtonCancel.setText("Cancel");
    jButtonCancel.setActionCommand("Cancel");
    jButtonCancel.setBounds(181,0,73,25);
    jButtonCancel.setFont(new Font("Dialog", Font.BOLD, 12));
    jButtonCancel.setForeground(new Color(0));
    jButtonCancel.setBackground(new Color(-3355444));
    jPanelBut.add(jButtonCancel);
    //End INIT_CONTROLS

    jButtonNew.setVisible( blNew);
    jButtonDelete.setVisible( blDelete);
    jButtonOk.setVisible( blOk);
    jButtonClear.setVisible( blClear);
    jButtonCancel.setVisible( blCancel);
    if( strDlgTitle == null)
      setTitle("DbTableEditor");
    else
      setTitle(strDlgTitle);
    m_strTable = strTable;
    m_tablePanel.setTableName( m_strTable);
    m_strResultColumn = strResultColumn;
    setDriver( strDriver);
    setUrl( strUrl);
    setUsr( strUid);
    setPwd( strPwd);
    setQuery( strQuery);

    //{{REGISTER_LISTENERS
    SymWindow aSymWindow = new SymWindow();
    this.addWindowListener(aSymWindow);
    SymAction lSymAction = new SymAction();
    jButtonNew.addActionListener(lSymAction);
    jButtonDelete.addActionListener(lSymAction);
    jButtonOk.addActionListener(lSymAction);
    jButtonClear.addActionListener(lSymAction);
    jButtonCancel.addActionListener(lSymAction);
    //}}
  }

  public void setTableCellEditor( DbTableCellEditor tce)
  {
    m_tce = tce;
  }

  public DbTableCellEditor getTableCellEditor()
  {
    return m_tce;
  }

  public TablePanel getTablePanel()
  {
    return m_tablePanel;
  }

  public boolean getEditable()
  {
    return m_blEditable;
  }

  /**
    * Set table editable.
    *
    * @param flag:		If true sets the table to editable.
    */
  public void setEditable(boolean flag)
  {
    m_blEditable = flag;
    m_tablePanel.setEditable( m_blEditable);
  }

  public String getUsr()
  {
    if( m_tablePanel != null)
      return m_tablePanel.getUsr();
    else
      return null;
  }

  public void setUsr(String strUsr)
  {
    if( m_tablePanel != null)
      m_tablePanel.setUsr( strUsr);
    m_strUsr = strUsr;
  }

  public String getPwd()
  {
    if( m_tablePanel != null)
      return m_tablePanel.getPwd();
    else
      return null;
  }

  public void setPwd(String strPwd)
  {
    if( m_tablePanel != null)
      m_tablePanel.setPwd( strPwd);
    m_strPwd = strPwd;
  }

  public String getDriver()
  {
    if( m_tablePanel != null)
      return m_tablePanel.getDriver();
    else
      return null;
  }

  public void setDriver(String strDriver)
  {
    if( m_tablePanel != null)
      m_tablePanel.setDriver( strDriver);
    m_strDriver = strDriver;
  }

  public String getUrl()
  {
    if( m_tablePanel != null)
      return m_tablePanel.getUrl();
    else
      return null;
  }

  public void setUrl(String strUrl)
  {
    if( m_tablePanel != null)
      m_tablePanel.setUrl( strUrl);
    m_strUrl = strUrl;
  }

  /**
    * Set debug output mode.
    *
    * @param flag:		If true sets the debug output to on.
    */
  public void setDbg(boolean flag)
  {
    if( m_tablePanel != null)
      m_tablePanel.setDbg(flag);
    m_blDbg = flag;
  }

  /**
    * Set extended debug output mode.
    *
    * @param flag:		If true sets the extended debug output to on.
    */
  public void setExtDbg(boolean flag)
  {
    if( m_tablePanel != null)
      m_tablePanel.setExtDbg(flag);
    m_blExDbg = flag;
  }

  /**
    * Set OutputStream for the output of the class DbTableAdapter.
    *
    * @param stream:			OutputStream.
    */
  public void setOutStream(OutputStream stream)
  {
    if( m_tablePanel != null)
      m_tablePanel.setOutStream(stream);
    m_printsOut = stream;
  }

  /**
    * Set OutputStream for the error output of the class DbTableAdapter.
    *
    * @param stream:			OutputStream.
    */
  public void setErrStream(OutputStream stream)
  {
    if( m_tablePanel != null)
      m_tablePanel.setErrStream(stream);
    m_printsErr = stream;
  }

  /**
    * Get OutputStream of the output of the class DbTableAdapter.
    */
  public OutputStream getOutStream()
  {
    if( m_tablePanel != null)
      return m_tablePanel.getOutStream();
    else
      return null;
  }

  /**
    * Get OutputStream of the error output of the class DbTableAdapter.
    */
  public OutputStream getErrStream()
  {
    if( m_tablePanel != null)
      return m_tablePanel.getErrStream();
    else
      return null;
  }

  public void setQuery( String strQuery)
  {
    if( m_tablePanel != null)
      m_tablePanel.setQuery( strQuery);
  }

  public void setResultColumn( String strResultColumn)
  {
    m_strResultColumn = strResultColumn;
  }

  public String getResultColumn()
  {
    return m_strResultColumn;
  }

  /**
    * Set table name(s) necessary for the update and delete SQL statements.
    *
    * @param strTable:	Table name(s) e.g.: "table11,table2"
    */
  public void setTableName( String strTable)
  {
    m_strTable = strTable;
    m_tablePanel.setTableName( strTable);
  }

  public void setValue( Object objValue)
  {
    m_objValue = objValue;
  }

  public void setResultValue( Object obj)
  {
    m_objResultValue = obj;
  }

  public Object getResultValue()
  {
    return m_objResultValue;
  }

  public int Query()
  {
    int iRet = Constant.ERR;
    boolean blEdt = m_blEditable;

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    setEditable( false);
    setDriver( m_strDriver);
    setUrl( m_strUrl);
    setUsr( m_strUsr);
    setPwd( m_strPwd);
    iRet = m_tablePanel.query();
    setTableName( m_strTable);
    setEditable( blEdt);
    setDbg( m_blDbg);
    setExtDbg( m_blExDbg);
    if( m_printsOut != null)
      setOutStream( m_printsOut);
    if( m_printsErr != null)
      setErrStream( m_printsErr);
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    return iRet;
  }

  public void setOkVisible( boolean bl)
  {
    jButtonOk.setVisible(bl);
  }

  public void setClearVisible( boolean bl)
  {
    jButtonClear.setVisible(bl);
  }

  public void setCancelVisible( boolean bl)
  {
    jButtonCancel.setVisible(bl);
  }

  public void setNewVisible( boolean bl)
  {
    jButtonNew.setVisible(bl);
  }

  public void setDeleteVisible( boolean bl)
  {
    jButtonDelete.setVisible(bl);
  }

  public void setOkText( String str)
  {
    jButtonOk.setText(str);
  }

  public void setClearText( String str)
  {
    jButtonClear.setText(str);
  }

  public void setCancelText( String str)
  {
    jButtonCancel.setText(str);
  }

  public void setNewText( String str)
  {
    jButtonNew.setText(str);
  }

  public void setDeleteText( String str)
  {
    jButtonDelete.setText(str);
  }

  public void setVisible(boolean b)
  {
    super.setVisible(b);
  }

  static public void main(String args[])
  {
    (new DbTableDialog( "DbTableDialog")).setVisible(true);
  }

  public void addNotify()
  {
    // Record the size of the window prior to calling parents addNotify.
    Dimension d = getSize();

    super.addNotify();

    if (fComponentsAdjusted)
      return;
    // Adjust components according to the getInsets
    setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
    Component components[] = getContentPane().getComponents();
    for (int i = 0; i < components.length; i++)
    {
      Point p = components[i].getLocation();
      p.translate(getInsets().left, getInsets().top);
      components[i].setLocation(p);
    }
    fComponentsAdjusted = true;
  }

  class SymWindow extends java.awt.event.WindowAdapter
  {
    public void windowClosing(java.awt.event.WindowEvent event)
    {
      Object object = event.getSource();
      if (object == DbTableDialog.this)
        JDialog1_WindowClosing(event);
    }
  }

  void JDialog1_WindowClosing(java.awt.event.WindowEvent event)
  {
    setVisible(false); // hide the Frame
    //dispose();	     // free the system resources
    //System.exit(0);    // close the application
  }

  class SymAction implements java.awt.event.ActionListener
  {
    public void actionPerformed(java.awt.event.ActionEvent event)
    {
      Object object = event.getSource();
      if (object == jButtonNew)
        jButtonNew_actionPerformed(event);
      else if (object == jButtonDelete)
        jButtonDelete_actionPerformed(event);
      else if (object == jButtonOk)
        jButtonOk_actionPerformed(event);
      else if (object == jButtonClear)
        jButtonClear_actionPerformed(event);
      else if (object == jButtonCancel)
        jButtonCancel_actionPerformed(event);
    }
  }

  void jButtonNew_actionPerformed(java.awt.event.ActionEvent event)
  {
    Object objValues[] = null;
    int iColCount = m_tablePanel.getColumnCount();

    if( iColCount != Constant.IDXERR)
      objValues = new Object[iColCount];
    else
      return;

    for( int i = 0; i < iColCount; i++)
    {
      if( m_tablePanel.isColNullable( i) != Constant.columnNullable)
      {
        switch( m_tablePanel.getColType( i))
        {
          case Types.CHAR:
          case Types.VARCHAR:
          case Types.LONGVARCHAR:
            objValues[i] = new String("Please, edit here!");
            break;

          case Types.BIT:
            objValues[i] = new Boolean( false);
            break;

          case Types.TINYINT:
          case Types.SMALLINT:
          case Types.INTEGER:
            objValues[i] = new Integer( 1);
            break;

          case Types.BIGINT:
            objValues[i] = new BigInteger( "1");
            break;

          case Types.DECIMAL:
          case Types.NUMERIC:
            objValues[i] = new BigDecimal( "1");
            break;

          case Types.FLOAT:
          case Types.DOUBLE:
            objValues[i] = new Double( 0.0);
            break;

          case Types.DATE:
          case Types.TIME:
          case Types.TIMESTAMP:
            objValues[i] = new Date( 0);
            break;

          default:
            objValues[i] = new String("Please, edit here!");
            break;
        }
      }
    }
    BigDecimal bdId = m_tablePanel.getTableAdapter().getDbQuery().getNextId( m_strTable, m_strResultColumn);
    int iPrimKey = m_tablePanel.getColumnIndex( m_strResultColumn);

    if( iPrimKey != Constant.IDXERR && bdId != null)
    {
      objValues[iPrimKey] = bdId;
    }
    else
      return;
    boolean bl = m_blEditable;
    setEditable( false);
    m_tablePanel.insertRow( 0, objValues);
    setTableName( m_strTable);
    setEditable( bl);
    if( iPrimKey != Constant.IDXERR && bdId != null)
    {
      int iRow = m_tablePanel.findRow( objValues[iPrimKey], iPrimKey);
      if( iRow != Constant.IDXERR)
      {
        m_tablePanel.setRowSelectionInterval( iRow, iRow);
        m_tablePanel.getScrollPane().getVerticalScrollBar().setValue(
          (iRow*m_tablePanel.getScrollPane().getVerticalScrollBar().getMaximum())/m_tablePanel.getRowCount());
      }
    }
  }

  void jButtonDelete_actionPerformed(java.awt.event.ActionEvent event)
  {
    int iRow = m_tablePanel.getSelectedRow();

    if( iRow != Constant.IDXERR)
    {
      if( m_tablePanel.questionMsgDlg( "Really delete this row?"))
        m_tablePanel.deleteRecord( iRow, m_strResultColumn);
    }
  }

  void jButtonOk_actionPerformed(java.awt.event.ActionEvent event)
  {
    int iRow = m_tablePanel.getSelectedRow();
    int iCol = m_tablePanel.getColumnIndex( m_strResultColumn);

    if( iRow == Constant.IDXERR || iCol == Constant.IDXERR)
    {
      m_objResultValue = null;
    }
    else
    {
      m_objResultValue = m_tablePanel.getValueAt( iRow, iCol);
    }
    setVisible(false); // hide the Frame
  }

  void jButtonClear_actionPerformed(java.awt.event.ActionEvent event)
  {
    m_objResultValue = null;
    setVisible(false); // hide the Frame
  }

  void jButtonCancel_actionPerformed(java.awt.event.ActionEvent event)
  {
    setVisible(false); // hide the Frame
  }
}

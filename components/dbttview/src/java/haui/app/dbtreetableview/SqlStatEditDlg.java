package haui.app.dbtreetableview;

//import haui.components.JSyntaxEditPanel;
import haui.sqlpanel.SQLPanel;
import haui.components.JExDialog;
import haui.resource.ResouceManager;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 *		Module:					SqlStatEditDlg.java
 *<p>
 *		Description:
 *</p><p>
 *		Created:				21.08.2000	by	AE
 *</p><p>
 *		Last Modified:	22.08.2000	by	AE
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2000
 *</p><p>
 *		@since					JDK1.1
 *</p>
 */
public class SqlStatEditDlg
  extends JExDialog
  implements ActionListener
{
  static int MAXUNDO = 20;

  JMenuBar jMenuBarMain = new JMenuBar();
  JMenu jMenuEdit = new JMenu();
  JMenuItem jMenuItemUndo = new JMenuItem();
  JMenuItem jMenuItemRedo = new JMenuItem();
  JSeparator jSeparatorUndo = new JSeparator();
  JMenuItem jMenuItemCut = new JMenuItem();
  JMenuItem jMenuItemCopy = new JMenuItem();
  JMenuItem jMenuItemPaste = new JMenuItem();
  JPanel SqlStatEditDlg = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanelSqlTables = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel jLabelSqlTables = new JLabel();
  JTextField jTextFieldSqlTables = new JTextField();
  JPanel jPanelSqlStatEdit = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JLabel jLabelSqlStatEdit = new JLabel();
  //JSyntaxEditPanel jTextAreaSqlStatEdit = new JSyntaxEditPanel( MAXUNDO);
  JScrollPane jScrollPaneSqlStatEdit = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  SQLPanel jTextAreaSqlStatEdit = new SQLPanel( DbTtvDesktop.DBTTVDT, MAXUNDO);
  JPanel jPanelSqlStatPrefs = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  JLabel jLabelDummy1 = new JLabel();
  JLabel jLabelDummy2 = new JLabel();
  JLabel jLabelDummy3 = new JLabel();
  JPanel jPanelButtons = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton jButtonOk = new JButton();
  JButton jButtonCancel = new JButton();

  ListElem m_listelem;

  public SqlStatEditDlg(Frame frame, String title, boolean modal)
  {
    super(frame, modal, DbTtvDesktop.DBTTVDT);
    try
    {
      setTitle( title);
      jbInit();
      pack();
      getRootPane().setDefaultButton( jButtonOk);

      //jTextAreaSqlStatEdit.setStyle( JSyntaxEditPanel.SQL_STYLE);

      jButtonOk.addActionListener(this);
      jButtonCancel.addActionListener(this);

      SymAction lSymAction = new SymAction();
      jMenuItemUndo.addActionListener(lSymAction);
      jMenuItemRedo.addActionListener(lSymAction);
      jMenuItemCut.addActionListener(lSymAction);
      jMenuItemCopy.addActionListener(lSymAction);
      jMenuItemPaste.addActionListener(lSymAction);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public SqlStatEditDlg()
  {
    this(null, "Edit SQL statement", true);
  }

  void jbInit() throws Exception
  {
    SqlStatEditDlg.setLayout(borderLayout1);
    jPanelSqlTables.setLayout(borderLayout2);
    jLabelSqlTables.setToolTipText("");
    jLabelSqlTables.setText("Tablename(s) for the SQL statement:");
    jTextFieldSqlTables.setToolTipText("Tablename(s)");
    jPanelSqlStatEdit.setLayout(borderLayout3);
    jLabelSqlStatEdit.setText("SQL Statement: ( ? = input variable! )");
    jTextAreaSqlStatEdit.setToolTipText("SQL Statement");
    jScrollPaneSqlStatEdit.setViewportView( jTextAreaSqlStatEdit);
    jScrollPaneSqlStatEdit.setOpaque(true);
    jPanelSqlStatPrefs.setLayout(borderLayout4);
    jLabelDummy1.setText("  ");
    jLabelDummy2.setText("  ");
    jLabelDummy2.setText("  ");
    jLabelDummy3.setText("  ");
    jLabelDummy3.setText("  ");
    jPanelButtons.setLayout(flowLayout1);
    jButtonOk.setText("Ok");
    jButtonCancel.setText("Cancel");
    SqlStatEditDlg.setPreferredSize(new Dimension(640, 400));
    getContentPane().add(SqlStatEditDlg);
    SqlStatEditDlg.add(jPanelSqlStatPrefs, BorderLayout.CENTER);
    jPanelSqlStatPrefs.add(jPanelSqlTables, BorderLayout.NORTH);
    jPanelSqlTables.add(jLabelSqlTables, BorderLayout.NORTH);
    jPanelSqlTables.add(jTextFieldSqlTables, BorderLayout.CENTER);
    jPanelSqlStatPrefs.add(jPanelSqlStatEdit, BorderLayout.CENTER);
    jPanelSqlStatEdit.add(jLabelSqlStatEdit, BorderLayout.NORTH);
    jPanelSqlStatEdit.add(jScrollPaneSqlStatEdit, BorderLayout.CENTER);
    SqlStatEditDlg.add(jLabelDummy1, BorderLayout.NORTH);
    SqlStatEditDlg.add(jLabelDummy2, BorderLayout.WEST);
    SqlStatEditDlg.add(jLabelDummy3, BorderLayout.EAST);
    SqlStatEditDlg.add(jPanelButtons, BorderLayout.SOUTH);
    jPanelButtons.add(jButtonOk, null);
    jPanelButtons.add(jButtonCancel, null);
    setJMenuBar(jMenuBarMain);
    jMenuEdit.setText("Edit");
    jMenuEdit.setActionCommand("edit");
    jMenuEdit.setMnemonic((int)'E');
    jMenuBarMain.add(jMenuEdit);
    jMenuItemUndo.setText("Undo");
    jMenuItemUndo.setActionCommand("undo");
    jMenuItemUndo.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, Event.CTRL_MASK));
    jMenuItemUndo.setMnemonic((int)'U');
    jMenuItemUndo.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Undo16.gif"));
    jMenuEdit.add(jMenuItemUndo);
    jMenuItemRedo.setText("Redo");
    jMenuItemRedo.setActionCommand("redo");
    jMenuItemRedo.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, Event.ALT_MASK));
    jMenuItemRedo.setMnemonic((int)'R');
    jMenuItemRedo.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Redo16.gif"));
    jMenuEdit.add(jMenuItemRedo);
    jMenuEdit.add(jSeparatorUndo);
    jMenuItemCut.setText("Cut");
    jMenuItemCut.setActionCommand("cut");
    jMenuItemCut.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, Event.CTRL_MASK));
    jMenuItemCut.setMnemonic((int)'u');
    jMenuEdit.add(jMenuItemCut);
    jMenuItemCopy.setText("Copy");
    jMenuItemCopy.setActionCommand("copy");
    jMenuItemCopy.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, Event.CTRL_MASK));
    jMenuItemCopy.setMnemonic((int)'C');
    jMenuEdit.add(jMenuItemCopy);
    jMenuItemPaste.setText("Paste");
    jMenuItemPaste.setActionCommand("paste");
    jMenuItemPaste.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, Event.CTRL_MASK));
    jMenuItemPaste.setMnemonic((int)'P');
    jMenuEdit.add(jMenuItemPaste);
  }

  class SymAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "cut")
        cut_actionPerformed(event);
      else if (cmd == "copy")
        copy_actionPerformed(event);
      else if (cmd == "paste")
        paste_actionPerformed(event);
      else if (cmd == "undo")
        undo_actionPerformed(event);
      else if (cmd == "redo")
        redo_actionPerformed(event);
    }
  }

  public void setListElem( ListElem elem)
  {
    m_listelem = elem;
    jTextFieldSqlTables.setText( elem.getTableNames());
    jTextAreaSqlStatEdit.setText( elem.getSqlStatement());
  }

  public ListElem getListElem()
  {
    return m_listelem;
  }

  public void actionPerformed( ActionEvent event)
  {
    String cmd = event.getActionCommand();
    if (cmd == "Ok")
      ok_actionPerformed(event);
    else if( cmd == "Cancel")
      cancel_actionPerformed(event);
  }

  void ok_actionPerformed( ActionEvent event)
  {
    //{{CONNECTION
    {
      m_listelem.setTableNames( jTextFieldSqlTables.getText());
      m_listelem.setSqlStatement( jTextAreaSqlStatEdit.getText());
      setVisible(false);
      // dispose();
    }
    //}}
  }

  void cancel_actionPerformed( ActionEvent event)
  {
    //{{CONNECTION
    {
      setVisible(false);
      // dispose();
    }
    //}}
  }

  private void undo_actionPerformed( ActionEvent event)
  {
    //if( jTextAreaSqlStatEdit.hasFocus())
    {
      jTextAreaSqlStatEdit.undo();
    }
  }

  private void redo_actionPerformed( ActionEvent event)
  {
    //if( jTextAreaSqlStatEdit.hasFocus())
    {
      jTextAreaSqlStatEdit.redo();
    }
  }

  void cut_actionPerformed( ActionEvent event)
  {
    //if( jTextAreaSqlStatEdit.hasFocus())
    {
      jTextAreaSqlStatEdit.cut();
    }
  }

  void copy_actionPerformed( ActionEvent event)
  {
    //if( jTextAreaSqlStatEdit.hasFocus())
    {
      jTextAreaSqlStatEdit.copy();
    }
  }

  void paste_actionPerformed( ActionEvent event)
  {
    //if( jTextAreaSqlStatEdit.hasFocus())
    {
      jTextAreaSqlStatEdit.paste();
    }
  }
}

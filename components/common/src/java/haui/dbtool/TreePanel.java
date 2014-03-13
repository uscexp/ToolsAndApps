package haui.dbtool;

import haui.components.JExDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.StringTokenizer;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Module:					TreePanel.java <p> Description:		Database tree view class. </p><p> Created:				06.07.1998	by	AE </p><p> Last Modified:	04.08.2000	by	AE </p><p> history					15.04.1999	by	AE:	Converted to JDK v1.2<br> 08.03.2000	by	AE:	Added DB schemas to tree and select statements<br> 04.08.2000	by	AE:	Added m_blConnect to control auto connection dialog<br> 04.08.2000	by	AE:	Bugfix in clearTree()<br> 03.08.2001	by	AE:	Formats select statement<br> 03.09.2001	by	AE: call to TablePanel getColumns, getTables, getSchemas,getcatalogs per popup added.<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999,2000  </p><p>
 * @since  					JDK1.2  </p>
 */
public class TreePanel extends javax.swing.JPanel
  implements Constant
{
  private static final long serialVersionUID = -3583603918233177559L;

  // constants
  public static final String NOTSUPPORTED = "Schema not supported";

  String m_strAppName;
  JScrollPane TreeScrollPane;
  JTree DbTree;
  JToolBar ConToolBar;
  JButton Connect;
  JButton About;
  JPopupMenu m_popup;
  JMenuItem m_jMenuItemColumnInfo;
  JMenuItem m_jMenuItemColumnPrivsInfo;
  JMenuItem m_jMenuItemTableInfo;
  JMenuItem m_jMenuItemTablePrivsInfo;
  JMenuItem m_jMenuItemPrimKeyInfo;
  JMenuItem m_jMenuItemImKeyInfo;
  JMenuItem m_jMenuItemExKeyInfo;
  JMenuItem m_jMenuItemIdxTrueInfo;
  JMenuItem m_jMenuItemIdxFalseInfo;
  JMenuItem m_jMenuItemProcInfo;
  JMenuItem m_jMenuItemProcColInfo;
  JMenuItem m_jMenuItemSchemaInfo;
  JMenuItem m_jMenuItemCatalogInfo;
  TablePanel m_tablePanel;
  //End DECLARE_CONTROLS

  ConnectDlg m_connectDlg;
  AboutDlg m_aboutDlg;
  ComboDlg m_schemaDlg;
  DbQuery m_dbquery;
  DefaultMutableTreeNode m_dmtreenode;
  DbTreeModel m_dbtreemodel;
  public int m_iAddCount;
  public int m_iInsertCount;
  boolean m_blTabs;
  String m_strTable;
  String m_strTypes[];

  // Extras Gui members
  boolean m_blDbg;
  boolean m_blExDbg;
  boolean m_blConnect = true;
  String m_strQuery;
  static String m_strUsr = "";
  static String m_strPwd = "";
  static String m_strDriver = "";
  static String m_strUrl = "";
  JTextField m_textUsr;
  JPasswordField m_passwordPwd;
  JDialog m_dlgLogin;
  OutputStream m_printsOut;
  OutputStream m_printsErr;

  public TreePanel( String strAppName)
  {
    super();
    m_strAppName = strAppName;
    //{{INIT_CONTROLS
    //}}
    super.setLayout(new BorderLayout(0,0));
    setSize(300,578);
    TreeScrollPane = new javax.swing.JScrollPane();
    //TreeScrollPane.setOpaque(true);
    add("Center", TreeScrollPane);
    DbTree = new javax.swing.JTree();
    DbTree.setShowsRootHandles(true);
    DbTree.setToolTipText("Database Tree View");
    TreeScrollPane.getViewport().add(DbTree);
    ConToolBar = new javax.swing.JToolBar();
    ConToolBar.setMargin(new java.awt.Insets(0,0,0,0));
    add("North", ConToolBar);
    Connect = new javax.swing.JButton();
    Connect.setText("Connect");
    Connect.setActionCommand("Connect");
    Connect.setToolTipText("Connect to Database");
    ConToolBar.add(Connect);
    About = new javax.swing.JButton();
    About.setText("About");
    About.setActionCommand("About");
    About.setToolTipText("About DbTreeView");
    ConToolBar.add(About);
    m_popup = new JPopupMenu();
    m_jMenuItemCatalogInfo = new JMenuItem();
    m_jMenuItemCatalogInfo.setText("Catalog info");
    m_jMenuItemCatalogInfo.setActionCommand("catinfo");
    m_jMenuItemCatalogInfo.setMnemonic((int)'g');
    m_popup.add(m_jMenuItemCatalogInfo);
    m_jMenuItemSchemaInfo = new JMenuItem();
    m_jMenuItemSchemaInfo.setText("Schema info");
    m_jMenuItemSchemaInfo.setActionCommand("schemainfo");
    m_jMenuItemSchemaInfo.setMnemonic((int)'S');
    m_popup.add(m_jMenuItemSchemaInfo);
    m_jMenuItemProcInfo = new JMenuItem();
    m_jMenuItemProcInfo.setText("Procedures info");
    m_jMenuItemProcInfo.setActionCommand("procinfo");
    m_popup.add(m_jMenuItemProcInfo);
    m_jMenuItemProcColInfo = new JMenuItem();
    m_jMenuItemProcColInfo.setText("Procedure columns info");
    m_jMenuItemProcColInfo.setActionCommand("proccolinfo");
    m_popup.add(m_jMenuItemProcColInfo);
    m_jMenuItemTableInfo = new JMenuItem();
    m_jMenuItemTableInfo.setText("Table info");
    m_jMenuItemTableInfo.setActionCommand("tabinfo");
    m_jMenuItemTableInfo.setMnemonic((int)'T');
    m_popup.add(m_jMenuItemTableInfo);
    m_jMenuItemTablePrivsInfo = new JMenuItem();
    m_jMenuItemTablePrivsInfo.setText("Table privs info");
    m_jMenuItemTablePrivsInfo.setActionCommand("tabprivinfo");
    m_popup.add(m_jMenuItemTablePrivsInfo);
    m_jMenuItemPrimKeyInfo = new JMenuItem();
    m_jMenuItemPrimKeyInfo.setText("Table prim keys");
    m_jMenuItemPrimKeyInfo.setActionCommand("primkeyinfo");
    m_popup.add(m_jMenuItemPrimKeyInfo);
    m_jMenuItemImKeyInfo = new JMenuItem();
    m_jMenuItemImKeyInfo.setText("Table imported keys");
    m_jMenuItemImKeyInfo.setActionCommand("imkeyinfo");
    m_popup.add(m_jMenuItemImKeyInfo);
    m_jMenuItemExKeyInfo = new JMenuItem();
    m_jMenuItemExKeyInfo.setText("Table exported keys");
    m_jMenuItemExKeyInfo.setActionCommand("exkeyinfo");
    m_popup.add(m_jMenuItemExKeyInfo);
    m_jMenuItemIdxTrueInfo = new JMenuItem();
    m_jMenuItemIdxTrueInfo.setText("Table unique indices");
    m_jMenuItemIdxTrueInfo.setActionCommand("idxtrueinfo");
    m_popup.add(m_jMenuItemIdxTrueInfo);
    m_jMenuItemIdxFalseInfo = new JMenuItem();
    m_jMenuItemIdxFalseInfo.setText("Table not unique indices");
    m_jMenuItemIdxFalseInfo.setActionCommand("idxfalseinfo");
    m_popup.add(m_jMenuItemIdxFalseInfo);
    m_jMenuItemColumnInfo = new JMenuItem();
    m_jMenuItemColumnInfo.setText("Column info");
    m_jMenuItemColumnInfo.setActionCommand("colinfo");
    m_jMenuItemColumnInfo.setMnemonic((int)'C');
    m_popup.add(m_jMenuItemColumnInfo);
    m_jMenuItemColumnPrivsInfo = new JMenuItem();
    m_jMenuItemColumnPrivsInfo.setText("Column privs info");
    m_jMenuItemColumnPrivsInfo.setActionCommand("colprivinfo");
    m_popup.add(m_jMenuItemColumnPrivsInfo);
    //End INIT_CONTROLS

    //{{REGISTER_LISTENERS
    SymMouse aSymMouse = new SymMouse();
    DbTree.addMouseListener(aSymMouse);
    SymAction lSymAction = new SymAction();
    Connect.addActionListener(lSymAction);
    About.addActionListener(lSymAction);
    m_jMenuItemColumnPrivsInfo.addActionListener( lSymAction);
    m_jMenuItemColumnInfo.addActionListener( lSymAction);
    m_jMenuItemTablePrivsInfo.addActionListener( lSymAction);
    m_jMenuItemPrimKeyInfo.addActionListener( lSymAction);
    m_jMenuItemImKeyInfo.addActionListener( lSymAction);
    m_jMenuItemExKeyInfo.addActionListener( lSymAction);
    m_jMenuItemIdxTrueInfo.addActionListener( lSymAction);
    m_jMenuItemIdxFalseInfo.addActionListener( lSymAction);
    m_jMenuItemTableInfo.addActionListener( lSymAction);
    m_jMenuItemProcInfo.addActionListener( lSymAction);
    m_jMenuItemProcColInfo.addActionListener( lSymAction);
    m_jMenuItemSchemaInfo.addActionListener( lSymAction);
    m_jMenuItemCatalogInfo.addActionListener( lSymAction);
    //}}
    m_dmtreenode = createNewNode("Database");
    m_dbtreemodel = new DbTreeModel(m_dmtreenode);
    DbTree.setModel(m_dbtreemodel);

      /* Enable tool tips for the tree, without this tool tips will not be picked up. */
    ToolTipManager.sharedInstance().registerComponent(DbTree);

      /* Make the tree use an instance of SampleTreeCellRenderer for drawing. */
    DbTree.setCellRenderer(new DbTreeCellRenderer());

      /* Make tree ask for the height of each row. */
    DbTree.setRowHeight(-1);
  }
/*
  static public void main(String args[])
  {
    class DriverFrame extends java.awt.Frame {
      public DriverFrame() {
        addWindowListener(new java.awt.event.WindowAdapter() {
          public void windowClosing(java.awt.event.WindowEvent event)
          {
            dispose();	  // free the system resources
            System.exit(0); // close the application
          }
        });
        this.setLayout(new java.awt.BorderLayout());
        this.setSize(300,578);
        this.add(new TreePanel());
      }
    }

    new DriverFrame().show();
  }
*/
  public void setLayout(LayoutManager mgr)
  {
    return;
  }

  public TablePanel getTablePanel()
  {
    return m_tablePanel;
  }

  public void setTablePanel( TablePanel tablePanel)
  {
    m_tablePanel = tablePanel;
  }

  /**
    * Selects the node at the specified row in the display.
    *
    * @param row  the row to select, where 0 is the first row in
    *             the display
    */
  public void setSelectionRow(int row)
  {
    DbTree.setSelectionRow( row);
  }

  /**
    * Returns the paths of all selected values.
    *
    * @return an array of TreePath objects indicating the selected
    *         nodes, or null if nothing is currently selected.
    */
  public TreePath[] getSelectionPaths()
  {
    return DbTree.getSelectionPaths();
  }

  public boolean hasFocus()
  {
    return DbTree.hasFocus() || TreeScrollPane.hasFocus();
  }

  /**
    * Returns the TreeNode instance that is selected in the tree.
    * If nothing is selected, null is returned.
    */
  protected DefaultMutableTreeNode getSelectedNode()
  {
    TreePath   tpSelPath = DbTree.getSelectionPath();

    if(tpSelPath != null)
      return (DefaultMutableTreeNode)tpSelPath.getLastPathComponent();
    return null;
  }

  protected DefaultMutableTreeNode createNewNode(String strItem)
  {
    return new DefaultMutableTreeNode(new DbData(null, Color.black, strItem));
  }

  protected DefaultMutableTreeNode createNewNode(String strItem, boolean blChilds)
  {
    return new DefaultMutableTreeNode(new DbData(null, Color.black, strItem), blChilds);
  }

  public void addTmpView()
  {
    TreePath tp[] = getSelectionPaths();

    setSelectionRow( 0);

    int i = 0;
    int ii = 1;
    String oldStr = "";
    while( i < Array.getLength( tp))
    {
      if( ii < tp[i].getPathCount())
      {
        setSelectionRow( ii-1);
        if( ii != tp[i].getPathCount()-1)
        {
          String str = null;
          if( ii == 1)
            str = tp[i].getPathComponent(ii) + "_TmpView";
          else
            str = tp[i].getPathComponent(ii).toString();
          if( i > 0 && !str.equals(oldStr))
            addItem( str, true);
          else
          {
            if( i == 0)
              addItem( str, true);
          }
          oldStr = str;
        }
        else
          addItem( tp[i].getPathComponent(ii).toString());
      }

      i++;
      if( i == Array.getLength( tp))
      {
        TreePath tmpTp = DbTree.getSelectionPath();
        DbTree.expandPath( tmpTp);
        ii++;
        if( ii < tp[0].getPathCount())
          i = 0;
      }
    }
  }

  public void addItem(String strItem, boolean blChilds)
  {
    int               newIndex;
    DefaultMutableTreeNode          lastItem = getSelectedNode();
    DefaultMutableTreeNode          parent;

      /* Determine where to create the new node. */
    if(lastItem != null)
      parent = (DefaultMutableTreeNode)lastItem;
    else
      parent = (DefaultMutableTreeNode)m_dbtreemodel.getRoot();
    if(lastItem == null)
      newIndex = m_dbtreemodel.getChildCount(parent);
    else
      newIndex = parent.getIndex(lastItem) + 1;

      /* Let the treemodel know. */
    m_dbtreemodel.insertNodeInto(createNewNode(strItem, blChilds), parent, newIndex);
    m_iAddCount++;
  }

  public void addItem(String strItem)
  {
    addItem( strItem, false);
  }

  protected void insertItem(String strItem)
  {
    int               newIndex;
    DefaultMutableTreeNode          lastItem = getSelectedNode();
    DefaultMutableTreeNode          parent;

      /* Determine where to create the new node. */
    if(lastItem != null)
      parent = (DefaultMutableTreeNode)lastItem.getParent();
    else
      parent = (DefaultMutableTreeNode)m_dbtreemodel.getRoot();
    if(lastItem == null)
      newIndex = m_dbtreemodel.getChildCount(parent);
    else
      newIndex = parent.getIndex(lastItem);

      /* Let the treemodel know. */
    m_dbtreemodel.insertNodeInto(createNewNode(strItem), parent, newIndex);
    m_iInsertCount++;
  }

  protected void removeItem(String strItem)
  {
    DefaultMutableTreeNode          lastItem = getSelectedNode();

    if(lastItem != null && lastItem != (DefaultMutableTreeNode)m_dbtreemodel.getRoot())
    {
      m_dbtreemodel.removeNodeFromParent(lastItem);
    }
  }

  class SymMouse extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      Object object = event.getSource();
      if( object == DbTree && event.getModifiers() == InputEvent.BUTTON3_MASK)
        rightMousePressed( event);
      else if( object == DbTree)
        DbTree_mouseClicked(event);
    }

    void rightMousePressed( MouseEvent event)
    {
      showPopup( event.getX(), event.getY());
    }
  }

  void DbTree_mouseClicked( MouseEvent event)
  {
    //{{CONNECTION
    // singleClick... doubleClick
    {
      int iSelRow = DbTree.getRowForLocation(event.getX(), event.getY());
      TreePath tpSelPath = DbTree.getPathForLocation(event.getX(), event.getY());
      if(iSelRow != -1)
      {
        if(event.getClickCount() == 1)
        {
                    //SingleClick(iSelRow, tpSelPath);
        }
        else if(event.getClickCount() == 2)
        {
          DoubleClick(iSelRow, tpSelPath);
        }
      }
    }
    //}}
  }

  void SingleClick(int iSelRow, TreePath tpSelPath)
  {
    println( "single click: Row: " + iSelRow + ", Path count: " + tpSelPath.getPathCount());
    for( int i = 0; i < tpSelPath.getPathCount(); i++)
    {
      if( i > 0)
        print( ".");
      print( tpSelPath.getPath()[i].toString());
    }
    println( "");
    getSelectStatement();
  }

  void DoubleClick(int iSelRow, TreePath tpSelPath)
  {
    int iPathCount = tpSelPath.getPathCount();
    String str = null;

    switch( iPathCount)
    {
    case 1:
    case 2:
      if( m_dbquery == null)
      {
        Connect();
        DbTree.expandPath( tpSelPath);
      }
      else
      {
        if( DbTree.isExpanded( tpSelPath) && !DbTree.isCollapsed( tpSelPath))
          DbTree.collapsePath( tpSelPath);
        else
          DbTree.expandPath( tpSelPath);
      }
      break;

    case 3:
      if( m_dbtreemodel.getChildCount( tpSelPath.getLastPathComponent()) == 0)
      {
        int i = 0;

        String strTypes[] = new String[7];
        strTypes[6] = "TABLE";
        strTypes[5] = "VIEW";
        strTypes[4] = "ALIAS";
        strTypes[3] = "SYNONYM";
        strTypes[2] = "SYSTEM TABLE";
        strTypes[1] = "GLOBAL TEMPORARY";
        strTypes[0] = "LOCAL TEMPORARY";
        int iCount = Array.getLength( strTypes);

        getRootPane().getParent().setCursor(new Cursor( Cursor.WAIT_CURSOR));
        for( i = 0; i < iCount; ++i)
        {
          addItem( strTypes[i], true);
        }
        getRootPane().getParent().setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
        DbTree.expandPath( tpSelPath);
      }
      else
      {
        if( DbTree.isExpanded( tpSelPath) && !DbTree.isCollapsed( tpSelPath))
          DbTree.collapsePath( tpSelPath);
        else
          DbTree.expandPath( tpSelPath);
      }
      break;

    case 4:
      if( m_dbtreemodel.getChildCount( tpSelPath.getLastPathComponent()) == 0)
      {
        int i = 0;

        String strSchemaType[] = new String[1];
        strSchemaType[0] = DbTree.getLastSelectedPathComponent().toString();
        String strSchema = tpSelPath.getPathComponent(2).toString();

        getRootPane().getParent().setCursor(new Cursor( Cursor.WAIT_CURSOR));
        if( m_dbquery.getTabs( strSchemaType, strSchema) == OK)
        {
          for( i = 0; ( str = m_dbquery.getTabName( i)) != null; i++)
          {
            addItem( str, true);
          }
        }
        getRootPane().getParent().setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
        DbTree.expandPath( tpSelPath);
      }
      else
      {
        if( DbTree.isExpanded( tpSelPath) && !DbTree.isCollapsed( tpSelPath))
          DbTree.collapsePath( tpSelPath);
        else
          DbTree.expandPath( tpSelPath);
      }
      break;

    case 5:
      if( m_dbtreemodel.getChildCount( tpSelPath.getLastPathComponent()) == 0)
      {
        //println( "SELECT * FROM " + tpSelPath.getPathComponent(2).toString());
        getRootPane().getParent().setCursor(new Cursor( Cursor.WAIT_CURSOR));
        String strQuery = null;
        if( tpSelPath.getPathComponent(2).toString().equals( NOTSUPPORTED))
          strQuery = "SELECT * FROM " + DbTree.getLastSelectedPathComponent().toString();
        else
          strQuery = "SELECT * FROM " + tpSelPath.getPathComponent( 2 ).toString()
                     + "." + DbTree.getLastSelectedPathComponent().toString();
        if( m_dbquery.query( strQuery, 0) == OK)
        {
          for( int i = 0; ( str = m_dbquery.getColName( i, 0)) != null; i++)
          {
            addItem( str, true);
          }
        }
        getRootPane().getParent().setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
        DbTree.expandPath( tpSelPath);
      }
      else
      {
        if( DbTree.isExpanded( tpSelPath) && !DbTree.isCollapsed( tpSelPath))
          DbTree.collapsePath( tpSelPath);
        else
          DbTree.expandPath( tpSelPath);
      }
      break;
    }
  }

  public JPopupMenu getDbInfoMenu()
  {
    return m_popup;
  }

  public void showPopup( int x, int y)
  {
    showPopup( x, y, true);
  }

  public void showPopup( int x, int y, boolean blSel)
  {
    if( m_popup != null)
    {
      TreePath tpSelPath = DbTree.getPathForLocation( x, y);
      if( tpSelPath == null)
        return;
      int xOff = 0;
      int yOff = 0;
      if( blSel)
      {
        DbTree.setSelectionPath( tpSelPath);
        xOff = TreeScrollPane.getHorizontalScrollBar().getValue();
        yOff = TreeScrollPane.getVerticalScrollBar().getValue();
      }
      int iPathCount = tpSelPath.getPathCount();

      switch( iPathCount)
      {
        case 2:
        case 3:
        case 4:
          m_jMenuItemPrimKeyInfo.setEnabled( false);
          m_jMenuItemImKeyInfo.setEnabled( false);
          m_jMenuItemExKeyInfo.setEnabled( false);
          m_jMenuItemIdxTrueInfo.setEnabled( false);
          m_jMenuItemIdxFalseInfo.setEnabled( false);
          m_popup.show( this, x - xOff, y - yOff);
          break;

        case 5:
        case 6:
          m_jMenuItemPrimKeyInfo.setEnabled( true);
          m_jMenuItemImKeyInfo.setEnabled( true);
          m_jMenuItemExKeyInfo.setEnabled( true);
          m_jMenuItemIdxTrueInfo.setEnabled( true);
          m_jMenuItemIdxFalseInfo.setEnabled( true);
          m_popup.show( this, x - xOff, y - yOff);
          break;
      }
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

  public String getTableName()
  {
    if( m_strTable != null)
      return m_strTable;
    else
      return "";
  }

  /**
    * Get DbQuery class in class DbTableAdapter.
    */
  public DbQuery getDbQuery()
  {
    return m_dbquery;
  }

  /**
    * Get DbQuery class in class DbTableAdapter.
    */
  public void setDbQuery( DbQuery dbquery)
  {
    m_dbquery = dbquery;
  }

  public String getSelectStatement()
  {
    int iSelCount = DbTree.getSelectionCount();
    TreePath tpSelPath[] = DbTree.getSelectionPaths();
    String strQuery = null;
    m_strTable = "";

    if( iSelCount > 0)
      strQuery = "SELECT ";

    String str = null;
    String strTmp = null;
    StringTokenizer strtok = null;
    int i = 0;
    int j = 0;
    for( j = 0; j < iSelCount; j++)
    {
      str = tpSelPath[j].toString();
      strtok = new StringTokenizer( str, ",}]", false);

      if( strtok.countTokens() >= 5)
      {
        i = 1;
      }
      if( strtok.countTokens() < 6)
      {
        break;
      }
      for( int iI = 0; iI < 5; iI++)
      {
        if( iI < 3)
        {
          strTmp = strtok.nextToken();
          strTmp = strTmp.substring( 1);
        }
        else if( iI == 3)
        {
          strtok.nextToken();
        }
        else
        {
          if( strTmp.equals( NOTSUPPORTED))
          {
            strTmp = " " + strtok.nextToken().substring( 1 );
          }
          else
            strTmp += "." + strtok.nextToken().substring( 1 );
        }
      }
      strTmp += "." + strtok.nextToken().substring( 1);
      int iIdx = -1;
      iIdx = strQuery.indexOf( strTmp);
      char c = ' ';
      if( iIdx != -1 && strQuery.length() >= iIdx + strTmp.length())
      {
        c = strQuery.charAt( iIdx + strTmp.length());
      }
      if( iIdx == -1 || c != ' ' || c != ',')
      {
        if( j == 0)
        {
          strQuery += " " + strTmp;
        }
        else
        {
          strQuery += "," + strTmp;
        }
      }
    }
    if( j == 0)
      strQuery += "*\nFROM ";
    else
      if( i == 1)
        strQuery += "\nFROM ";

    String strCom = "";
    for( i = 0; i < iSelCount; i++)
    {
      str = tpSelPath[i].toString();
      strtok = new StringTokenizer( str, ",}]", false);

      if( strtok.countTokens() < 4)
      {
        strQuery = null;
        break;
      }
      for( int iI = 0; iI < 5; iI++)
      {
        if( iI < 3)
          strTmp = strtok.nextToken();
        else if( iI == 3)
          strtok.nextToken();
        else if( strtok.hasMoreTokens())
        {
          if( strTmp.equals( " " + NOTSUPPORTED))
          {
            strTmp = " " + strtok.nextToken().substring( 1 );
          }
          else
            strTmp += "." + strtok.nextToken().substring( 1 );
        }
        else
          return null;
      }
      strCom = strQuery.substring( strQuery.indexOf( "FROM"));
      if( strCom.indexOf( strTmp) == -1)
      {
        if( i == 0)
        {
          strQuery += strTmp;
          m_strTable += strTmp;
        }
        else
        {
          strQuery += "," + strTmp;
          m_strTable += "," + strTmp;
        }
      }
    }
    //if( strQuery != null)
      //println( "\n" + strQuery);

    return strQuery;
  }

  public void setToolBarVisible( boolean blVis)
  {
    ConToolBar.setVisible( blVis);
  }

  public JButton addToToolBar( Action a)
  {
    return ConToolBar.add( a);
  }

  public void setAboutVisible( boolean blVis)
  {
    About.setVisible( blVis);
  }

  public void setConnectVisible( boolean blVis)
  {
    Connect.setVisible( blVis);
  }

  public void clearTree()
  {
    TreePath tpSelPath;
    DbTree.setSelectionRow( 0);
    tpSelPath = DbTree.getSelectionPath();
    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)m_dbtreemodel.getRoot();
    if( m_dbtreemodel.getChildCount( parent) == 0)
      return;
    DbTree.expandPath( tpSelPath);
    DbTree.setSelectionRow( 1);
    tpSelPath = DbTree.getSelectionPath();
    DbTree.collapsePath( tpSelPath);
    removeItem( DbTree.getLastSelectedPathComponent().toString());
    //DbTree.removeSelectionPath( tpSelPath);
    DbTree.setSelectionRow( 0);
  }

  public void Close()
  {
    if( m_dbquery != null)
      m_dbquery.closeConnection();
    m_dbquery = null;
  }

  public boolean Connect()
  {
    boolean blRet = false;
    getRootPane().getParent().setCursor(new Cursor( Cursor.WAIT_CURSOR));
    if( m_blConnect &&( m_strDriver == null || m_strDriver.equals( "")) || ( m_strUrl == null || m_strUrl.equals( "")))
    {
      if( m_dbquery != null)
      {
        Close();
      }
      Reset();
      if( m_connectDlg == null)
      {
        m_connectDlg = new ConnectDlg( m_strAppName);
      }
      else
      {
        m_connectDlg.setVisible( true);
        clearTree();
      }
      setDriver( m_connectDlg.getDriver());
      setUrl( m_connectDlg.getUrl());
    }
    if( m_blConnect & (getUsr() == null || ( getUsr() != null && getUsr().equals(""))))
      LoginDlg();
//    if(getUsr() != null && !getUsr().equals(""))
    {
      m_dbquery = new DbQuery( 1);
      if( m_printsOut != null)
        m_dbquery.setOutStream(m_printsOut);
      if( m_printsErr != null)
        m_dbquery.setErrStream(m_printsErr);
      if(m_dbquery.dbConnect(getDriver(), getUrl(), getUsr(), getPwd()) != null)
      {
        blRet = true;
        m_dbquery.setDebugMode(m_blDbg);
        m_dbquery.setExtDebugMode(m_blExDbg);
        if( m_printsOut != null)
          m_dbquery.setOutStream(m_printsOut);
        if( m_printsErr != null)
          m_dbquery.setErrStream(m_printsErr);
      }
      else
      {
        blRet = false;
        ErrorMsgDlg("Could not connect to database!");
        Reset();
        m_dbquery = null;
        return blRet;
      }
    }
/*    else
    {
      ErrorMsgDlg("Wrong login name!");
      Reset();
      m_dbquery = null;
      return;
    }
*/
    String strItem = "";
    StringTokenizer st = new StringTokenizer( m_strUrl, ":/");
    while(st.hasMoreTokens())
    {
      strItem = st.nextToken();
    }
    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)m_dbtreemodel.getRoot();
    m_dbtreemodel.insertNodeInto(createNewNode(strItem), parent, m_dbtreemodel.getChildCount(parent));

    DbTree.setSelectionRow( 0);
    TreePath tpSelPath = DbTree.getLeadSelectionPath();
    DbTree.expandPath( tpSelPath);
    DbTree.setSelectionRow( 1);

    int i = 0;
    //String strTypes[] = new String[2];
    String str;

    if( m_dbquery.getSchemas() == OK)
    {
      //String strSchemas[] = new String[ m_dbquery.getSchemaCount()];
      for( i = 0; ( str = m_dbquery.getSchemaName( i)) != null; i++)
      {
        addItem( str, true);
      }
    }
    else
    {
      addItem( NOTSUPPORTED, true);
    }
    getRootPane().getParent().setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
    DbTree.expandPath( tpSelPath);
    return blRet;
  }

  class SymAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      Object object = event.getSource();
      String str = event.getActionCommand();
      if (object == Connect)
        Connect_actionPerformed(event);
      else if (object == About)
        About_actionPerformed(event);
      else if (str == "colprivinfo")
        colprivinfo_actionPerformed(event);
      else if (str == "colinfo")
        colinfo_actionPerformed(event);
      else if (str == "idxfalseinfo")
        idxfalseinfo_actionPerformed(event);
      else if (str == "idxtrueinfo")
        idxtrueinfo_actionPerformed(event);
      else if (str == "exkeyinfo")
        exkeyinfo_actionPerformed(event);
      else if (str == "imkeyinfo")
        imkeyinfo_actionPerformed(event);
      else if (str == "primkeyinfo")
        primkeyinfo_actionPerformed(event);
      else if (str == "tabprivinfo")
        tabprivinfo_actionPerformed(event);
      else if (str == "tabinfo")
        tabinfo_actionPerformed(event);
      else if (str == "schemainfo")
        schemainfo_actionPerformed(event);
      else if (str == "procinfo")
        procinfo_actionPerformed(event);
      else if (str == "proccolinfo")
        proccolinfo_actionPerformed(event);
      else if (str == "catinfo")
        catinfo_actionPerformed(event);
    }
  }

  void catinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    if( getTablePanel() != null)
      getTablePanel().getCatalogs();
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void schemainfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    if( getTablePanel() != null)
      getTablePanel().getSchemas();
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void tabinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getTables( null, strSchema, strTable);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void tabprivinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getTablePrivileges( null, strSchema, strTable);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void primkeyinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getPrimaryKeys( null, strSchema, strTable);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void imkeyinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getImportedKeys( null, strSchema, strTable);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void exkeyinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getExportedKeys( null, strSchema, strTable);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void idxtrueinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getIndexInfo( null, strSchema, strTable, true, false);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void idxfalseinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getIndexInfo( null, strSchema, strTable, false, false);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void proccolinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strProc = "%";
    String strColumn = "%";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getProcedureColumns( null, strSchema, strProc, strColumn);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void procinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strProc = "%";
    //String strColumn = "%";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getProcedures( null, strSchema, strProc);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void colinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getColumns( null, strSchema, strTable, strColumn);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void colprivinfo_actionPerformed( ActionEvent event)
  {
    m_popup.setVisible( false);
    getRootPane().setCursor( new Cursor( Cursor.WAIT_CURSOR));
    TreePath tpSelPath = DbTree.getSelectionPath();
    //if( tpSelPath == null || getTablePanel() == null)
    if( tpSelPath == null)
      return;

    String str = tpSelPath.toString();
    StringTokenizer strtok = new StringTokenizer( str, ",}]", false);
    String strCat = "";
    String strSchema = "";
    String strTable = "";
    String strColumn = "";

    if( strtok.countTokens() >= 2)
    {
      strtok.nextToken();
      strCat = strtok.nextToken();
      while( Character.isWhitespace( strCat.charAt(0)))
        strCat = strCat.substring( 1);
      strSchema = null;
      strTable = null;
      strColumn = "%";
    }
    if( strtok.hasMoreTokens())
    {
      strSchema = strtok.nextToken();
      while( Character.isWhitespace( strSchema.charAt(0)))
        strSchema = strSchema.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strtok.nextToken();
    }
    if( strtok.hasMoreTokens())
    {
      strTable = strtok.nextToken();
      while( Character.isWhitespace( strTable.charAt(0)))
        strTable = strTable.substring( 1);
    }
    if( strtok.hasMoreTokens())
    {
      strColumn = strtok.nextToken();
      while( Character.isWhitespace( strColumn.charAt(0)))
        strColumn = strColumn.substring( 1);
    }
    if( getTablePanel() != null)
      getTablePanel().getColumnPrivileges( null, strSchema, strTable, strColumn);
    getRootPane().setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  }

  void Connect_actionPerformed( ActionEvent event)
  {
    //{{CONNECTION
    // Connect to DB
    {
      Connect();
    }
    //}}
  }

  void About_actionPerformed( ActionEvent event)
  {
    // to do: code goes here.

    //{{CONNECTION
    // Create and show as modal
    {
      if( m_aboutDlg == null)
      {
        m_aboutDlg = new AboutDlg( m_strAppName);
      }
      else
      {
        m_aboutDlg.setVisible( true);
      }
    }
    //}}
  }

  // ExtrasGui

  public boolean getDbg()
  {
    return m_blDbg;
  }

  public boolean getExtDbg()
  {
    return m_blExDbg;
  }

  public void setDbg(boolean flag)
  {
    if( m_dbquery != null)
      m_dbquery.setDebugMode(flag);
    m_blDbg = flag;
  }

  public void setExtDbg(boolean flag)
  {
    if( m_dbquery != null)
      m_dbquery.setExtDebugMode(flag);
    m_blExDbg = flag;
  }

  public void setOutStream(OutputStream stream)
  {
    if( m_dbquery != null)
      m_dbquery.setOutStream(stream);
    m_printsOut = stream;
  }

  public void setErrStream(OutputStream stream)
  {
    if( m_dbquery != null)
      m_dbquery.setErrStream(stream);
    m_printsErr = stream;
  }

  public OutputStream getOutStream()
  {
    if(m_dbquery != null)
      return m_dbquery.getOutStream();
    else
      return null;
  }

  public OutputStream getErrStream()
  {
    if(m_dbquery != null)
      return m_dbquery.getErrStream();
    else
      return null;
  }

  public String getUsr()
  {
    return m_strUsr;
  }

  public void setUsr(String s)
  {
    m_strUsr = s;
  }

  public String getPwd()
  {
    return m_strPwd;
  }

  public void setPwd(String s)
  {
    m_strPwd = s;
  }

  public String getDriver()
  {
    return m_strDriver;
  }

  public void setDriver(String s)
  {
    m_strDriver = s;
  }

  public String getUrl()
  {
    return m_strUrl;
  }

  public void setUrl(String s)
  {
    m_strUrl = s;
  }

  public String[] getTypes()
  {
    return m_strTypes;
  }

  public void setTypes(String[] s)
  {
    m_strTypes = s;
  }

  public void Reset()
  {
    m_strUsr = null;
    m_strPwd = null;
  }

  public void LoginDlg()
  {
    if(m_dlgLogin == null)
    {
      m_dlgLogin = new JExDialog( m_strAppName);
      m_dlgLogin.setModal( true);
      m_dlgLogin.setTitle( "Database login:");
      m_dlgLogin.setBackground(Color.lightGray);
      m_dlgLogin.getContentPane().setLayout(new BorderLayout());
      m_dlgLogin.setResizable(false);
      Point point = getParent().getLocation();
      m_dlgLogin.setLocation(point.x + 30, point.y + 30);
      Font font = new Font("Arial", 1, 12);
      m_textUsr = new JTextField(15);
      m_passwordPwd = new JPasswordField(15);
      JPanel jpanel = new JPanel();
      jpanel.setLayout(new GridLayout(4, 1));
      JLabel jlabel = new JLabel("Login:");
      jlabel.setFont(font);
      jlabel.setForeground(new Color(0, 0, 150));
      JLabel jlabel1 = new JLabel("Password:");
      jlabel1.setFont(font);
      jlabel1.setForeground(new Color(0, 0, 150));
      jpanel.add(jlabel);
      jpanel.add(m_textUsr);
      jpanel.add(jlabel1);
      jpanel.add(m_passwordPwd);
      m_dlgLogin.getContentPane().add("Center", jpanel);
      JPanel jpanel1 = new JPanel();
      jpanel1.setLayout(new FlowLayout(1));
      JButton jbutton = new JButton("Ok");
      jbutton.setVerticalTextPosition(0);
      jbutton.setHorizontalTextPosition(0);
      jbutton.setMnemonic('O');
      jbutton.setActionCommand("Ok");
      jbutton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent actionevent)
        {
          String s = actionevent.getActionCommand();
          if(s.equals("Ok"))
          {
            m_strUsr = m_textUsr.getText();
            m_strPwd = String.valueOf( m_passwordPwd.getPassword());
            m_dlgLogin.setVisible(false);
          }
        }

      });
      jpanel1.add(jbutton);
      m_dlgLogin.getContentPane().add("South", jpanel1);
      m_dlgLogin.pack();
      if( m_strUsr != null)
        m_textUsr.setText( m_strUsr);
      if( m_strPwd != null)
        m_passwordPwd.setText( m_strPwd);
      m_dlgLogin.setVisible(true);
      getRootPane().setDefaultButton( jbutton);
      if( m_strUsr != null)
        m_passwordPwd.requestFocus();
      else
        m_textUsr.requestFocus();
      return;
    }
    else
    {
      if( m_strUsr == null || m_strUsr.equals( ""))
        m_textUsr.setText( "");
      if( m_strPwd == null || m_strPwd.equals( ""))
        m_passwordPwd.setText( "");
      m_dlgLogin.setVisible(true);
      if( m_strUsr != null)
        m_passwordPwd.requestFocus();
      else
        m_textUsr.requestFocus();
      return;
    }
  }

  public boolean getAutoConnect()
  {
    return m_blConnect;
  }

  public void setAutoConnect( boolean blConnect)
  {
    m_blConnect = blConnect;
  }

  public void ErrorMsgDlg(String s)
  {
    JOptionPane.showConfirmDialog(getParent(), s, "Error!", -1, 0);
  }

  public boolean QuestionMsgDlg(String s)
  {
    return JOptionPane.showConfirmDialog(getParent(), s, "Question!", 0, 3) == 0;
  }

  public void InfoMsgDlg(String s)
  {
    JOptionPane.showConfirmDialog(getParent(), s, "Info!", -1, 1);
  }

  public boolean WarnMsgDlg(String s)
  {
    return JOptionPane.showConfirmDialog(getParent(), s, "Warning!", 2, 2) == 0;
  }

  public String InputMsgDlg(String s)
  {
    String s1 = null;
    s1 = JOptionPane.showInputDialog(getParent(), s, "Input!", 1);
    return s1;
  }

  public void BeginWait()
  {
    setCursor(new Cursor(3));
  }

  public void EndWait()
  {
    setCursor(new Cursor(0));
  }

//  private void errPrint( String str)
//  {
//    errWrite( str);
//  }

  private void print( String str)
  {
    write( str);
  }

//  private void errPrintln( String str)
//  {
//    errWrite( str + "\n");
//  }

  private void println( String str)
  {
    write( str + "\n");
  }

//  private void errWrite( String str)
//  {
//    try
//    {
//      for( int i = 0; i < str.length(); i++)
//      {
//        m_printsErr.write( str.charAt( i));
//      }
//    }
//    catch( java.io.IOException ex)
//    {
//      ex.printStackTrace();
//    }
//  }

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

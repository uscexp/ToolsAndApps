
package haui.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Module:					jPanelTree.java <p> Description:		tree view class. </p><p> Created:				08.10.1998	by	AE </p><p> Last Modified:	15.04.1999	by	AE </p><p> history					08.10.1998 - 13.10.1998	by	AE:	Create jPanelTree basic functionality<br> 15.04.1999	by	AE:	Converted to JDK v1.2<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v0.1, 1998,1999  </p><p>
 * @since  					JDK1.2  </p>
 */
public class jPanelTree extends JTree
	implements Constant
{
  private static final long serialVersionUID = 8867104674986534339L;
  
  JScrollPane TreeScrollPane;
  DefaultMutableTreeNode m_dmtreenode;
  jTreeModel m_jtreemodel;
	public int m_iAddCount;
	public int m_iInsertCount;
	TreePath   m_tpRootPath;
	
  String m_strError = "Error!";
  String m_strQuestion = "Question!";
  String m_strInfo = "Info!";
  String m_strWarning = "Warning!";
  String m_strInput = "Input!";
	
	public jPanelTree()
	{
		super();
		try 
		{
			jbInit();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
  	/**
     * Construct jPanelTree.
     */
	private void jbInit() throws Exception
	{
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setForeground(new Color(0));
		setBackground(new Color(-3355444));
		setShowsRootHandles(true);
		setToolTipText("Database Tree View");
		setBounds(0,0,77,40);
		setBackground(new Color(16777215));

		//{{REGISTER_LISTENERS
		//SymMouse aSymMouse = new SymMouse();
		//addMouseListener(aSymMouse);
		//}}
		m_dmtreenode = createNewNode("Root");
		m_jtreemodel = new jTreeModel(m_dmtreenode);
		setModel(m_jtreemodel);
    setSelectionRow( 0);
		m_tpRootPath = getSelectionPath();
		
	    /* Enable tool tips for the tree, without this tool tips will not be picked up. */
		ToolTipManager.sharedInstance().registerComponent(this);
		
	    /* Make the tree use an instance of jTreeCellRenderer for drawing. */
		setCellRenderer(new jTreeCellRenderer());
		
    	/* Make tree ask for the height of each row. */
		setRowHeight(-1);
	}
	
    /**
     * Set the root label in the tree
     *
     * @param str String for the new root label.
     */
  public void setRootLabel( String str)
  {
  	TreePath treepath[] = getSelectionPaths();
		
    setSelectionRow( 0);
    Data data = (Data)getUserObject();
    data.setString( str);
		
    setSelectionPaths( treepath);
  }
	
    /**
     * Set a label in the tree
     *
     * @param str String for the new label.
     */
  public void setLabel( String str)
  {
		if( getSelectedNode() != null)
    {
    	setSelectionRow( 0);
    	Data data = (Data)getUserObject();
    	data.setString( str);
    }
  }
	
    /**
     * Returns this node's user object.
     *
     * @return	the Object stored at this node by the user
     * @see	#setUserObject
     * @see	#toString
     */
  public Object getUserObject()
  {
		DefaultMutableTreeNode dmtnSel =  getSelectedNode();
		
    if( dmtnSel != null)
    	return dmtnSel.getUserObject();
    else
    	return null;
  }
	
    /**
      * Returns the user object path, from the root, to get to this node.
      * If some of the TreeNodes in the path have null user objects, the
      * returned path will contain nulls.
      */
  public Object[] getUserObjectPath()
  {
		DefaultMutableTreeNode dmtnSel =  getSelectedNode();

    if( dmtnSel != null)
    	return dmtnSel.getUserObjectPath();
    else
    	return null;
  }

	/**
	  * Returns the TreeNode instance that is selected in the tree.
	  * If nothing is selected, null is returned.
	  */
	public DefaultMutableTreeNode getSelectedNode()
	{
		TreePath   tpSelPath = getSelectionPath();

		if(tpSelPath != null)
			return (DefaultMutableTreeNode)tpSelPath.getLastPathComponent();
		return null;
	}

    /**
     * Create a new TreeNode
     *
     * @param strItem String for the new TreeNode.
     */
	public DefaultMutableTreeNode createNewNode(String strItem)
	{
		return new DefaultMutableTreeNode(new Data(null, Color.black, strItem));
	}
	
    /**
     * Create a new TreeNode which accept or not accept childs
     *
     * @param strItem String for the new TreeNode.
     * @param blChilds Accept childs if true.
     */
	public DefaultMutableTreeNode createNewNode(String strItem, boolean blChilds)
	{
		return new DefaultMutableTreeNode(new Data(null, Color.black, strItem), blChilds);
	}
	
    /**
     * Add a new TreeNode to the tree at the end at the selected position.
     *
     * @param strItem String for the new TreeNode.
     * @param blChilds Accept childs if true.
     */
	public void addItem(String strItem, boolean blChilds)
	{
		int               newIndex;
		DefaultMutableTreeNode          lastItem = getSelectedNode();
		DefaultMutableTreeNode          parent;
		
	    /* Determine where to create the new node. */
		if(lastItem != null)
			parent = (DefaultMutableTreeNode)lastItem;
		else
			parent = (DefaultMutableTreeNode)m_jtreemodel.getRoot();
		if(lastItem == null)
			newIndex = m_jtreemodel.getChildCount(parent);
		else
			newIndex = parent.getIndex(lastItem) + 1;
		
	    /* Let the treemodel know. */
		m_jtreemodel.insertNodeInto(createNewNode(strItem, blChilds), parent, newIndex);
		m_iAddCount++;
	}
	
    /**
     * Add a new TreeNode to the tree at the end at the selected position. Do not accept childs.
     *
     * @param strItem String for the new TreeNode.
     * @param blChilds Accept childs if true.
     */
	public void addItem(String strItem)
	{
		addItem( strItem, false);
	}
	
    /**
     * Insert a new TreeNode to the tree at the selected position.
     *
     * @param strItem String for the new TreeNode.
     */
	public void insertItem(String strItem)
	{
		int               newIndex;
		DefaultMutableTreeNode          lastItem = getSelectedNode();
		DefaultMutableTreeNode          parent;
		
	    /* Determine where to create the new node. */
		if(lastItem != null)
			parent = (DefaultMutableTreeNode)lastItem.getParent();
		else
			parent = (DefaultMutableTreeNode)m_jtreemodel.getRoot();
		if(lastItem == null)
			newIndex = m_jtreemodel.getChildCount(parent);
		else
			newIndex = parent.getIndex(lastItem);
		
	    /* Let the treemodel know. */
		m_jtreemodel.insertNodeInto(createNewNode(strItem), parent, newIndex);
		m_iInsertCount++;
	}
	
    /**
     * Remove selected TreeNode from the tree.
     *
     * @param strItem String for the new TreeNode.
     */
	public void removeItem(String strItem)
	{
		DefaultMutableTreeNode          lastItem = getSelectedNode();
		
		if(lastItem != null && lastItem != (DefaultMutableTreeNode)m_jtreemodel.getRoot())
		{
			m_jtreemodel.removeNodeFromParent(lastItem);
		}
	}

    /**
     * Remove all TreeNodes form the tree.
     */
	public void clearTree()
	{
		TreePath tpSelPath;
		setSelectionRow( 1);
		tpSelPath = getSelectionPath();
		collapsePath( tpSelPath);
		removeItem( getLastSelectedPathComponent().toString());
    //removeSelectionPath( tpSelPath);
		setSelectionRow( 0);
	}
	
	// ExtrasGui
	
    /**
     * Set error message dialog label.
     *
     * @param s Error message dialog label.
     */
	public void strErrorMsgDlgLabel(String s)
	{
  	if( s != null)
			m_strError = s;
	}
	
    /**
     * PopUp an error message dialog.
     *
     * @param s Error message.
     */
	public void ErrorMsgDlg(String s)
	{
		JOptionPane.showConfirmDialog(getParent(), s, m_strError, -1, 0);
	}
	
    /**
     * Set question message dialog label.
     *
     * @param s Question message dialog label.
     */
	public void strQuestionMsgDlgLabel(String s)
	{
  	if( s != null)
			m_strError = s;
	}
	
    /**
     * PopUp an question message dialog.
     *
     * @param s Error message.
     * @return true if the yes button was pressed, else false.
     */
	public boolean QuestionMsgDlg(String s)
	{
		return JOptionPane.showConfirmDialog(getParent(), s, m_strQuestion, 0, 3) == 0;
	}
	
    /**
     * Set info message dialog label.
     *
     * @param s Info message dialog label.
     */
	public void strInfoMsgDlgLabel(String s)
	{
  	if( s != null)
			m_strError = s;
	}
	
    /**
     * PopUp an info message dialog.
     *
     * @param s Info message.
     */
	public void InfoMsgDlg(String s)
	{
		JOptionPane.showConfirmDialog(getParent(), s, m_strInfo, -1, 1);
	}
	
    /**
     * Set warning message dialog label.
     *
     * @param s warning message dialog label.
     */
	public void strWarnMsgDlgLabel(String s)
	{
  	if( s != null)
			m_strError = s;
	}
	
    /**
     * PopUp an wanrning message dialog.
     *
     * @param s Warning message.
     * @return true if the ok button was pressed, else false.
     */
	public boolean WarnMsgDlg(String s)
	{
		return JOptionPane.showConfirmDialog(getParent(), s, m_strWarning, 2, 2) == 0;
	}
	
    /**
     * Set input message dialog label.
     *
     * @param s Input message dialog label.
     */
	public void strInputMsgDlgLabel(String s)
	{
  	if( s != null)
			m_strError = s;
	}
	
    /**
     * PopUp an input message dialog.
     *
     * @param s Input message.
     * @return Inserted string from the TextField. null, if cancel was pressed.
     */
	public String InputMsgDlg(String s)
	{
		String s1 = null;
		s1 = JOptionPane.showInputDialog(getParent(), s, m_strInput, 1);
		return s1;
	}

    /**
     * Set wait cursor.
     */
	public void BeginWait()
	{
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

    /**
     * End wait cursor.
     */
	public void EndWait()
	{
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

/*
	public static void main(String[] args)
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
				jPanelTree jpt = new jPanelTree();
				jpt.setRootLabel( "NewLabel");
				jpt.addItem( "item1", true);
				jpt.addItem( "item2", true);
				String path[] = { "NewLabel", "item1"};
				jpt.expandRow( 0);
				jpt.setSelectionRow( 2);
				jpt.addItem( "level2", true);
				this.add("Center", jpt);
			}
		}

		new DriverFrame().show();
	}
*/
}



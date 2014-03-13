package haui.dbtool;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *		Module:					DbTreeModel.java
 *<p>
 *		Description:		Database TreeModel class.
 *</p><p>
 *		Created:				07.07.1998	by	AE
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
public class DbTreeModel extends DefaultTreeModel
{
    private static final long serialVersionUID = 540291227773306541L;

    /**
      * Creates a new instance of SampleTreeModel with newRoot set
      * to the root of this model.
      */
    public DbTreeModel(TreeNode newRoot)
    {
    	super(newRoot);
    }

    /**
      * Subclassed to message setString() to the changed path item.
      */
    public void valueForPathChanged(TreePath path, Object newValue)
    {
    	/* Update the user object. */
    	DefaultMutableTreeNode      aNode = (DefaultMutableTreeNode)path.getLastPathComponent();
    	DbData    dbData = (DbData)aNode.getUserObject();

    	dbData.setString((String)newValue);
    	//dbData.setColor(Color.green);

    	/* Since we've changed how the data is to be displayed, message nodeChanged. */
    	nodeChanged(aNode);
    }
}

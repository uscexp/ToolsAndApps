package haui.components;

import haui.io.FileFilters;
import haui.util.FileUtilities;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Module:      FileSystemTreeModel<br> <p> Description: FileSystemTreeModel<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
 * @history      08.04.2008 by AE: Created.<br>  </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
 * @version      v0.1, 2008; %version: %<br>  </p><p>
 * @since        JDK1.4  </p>
 */
public class FileSystemTreeModel
  implements TreeModel
{
  private FileNode m_Root;
  public static FileNode m_LocalFs;
  private static FileNode m_UserHome;
  private FileFilter m_Filter;
  private Comparator m_Comparator;
  protected EventListenerList listenerList = new EventListenerList();

  public FileSystemTreeModel( FileNode root, FileFilter filter, Comparator comparator )
  {
    m_Root = root;
    root.m_Model = this;
    m_Filter = filter;
    m_Comparator = comparator;
  }

  public FileSystemTreeModel( FileNode root, FileFilter filter )
  {
    this( root, filter, new DefaultComparator() );
  }

  public FileSystemTreeModel( FileNode root )
  {
    this( root, FileFilters.ALL );
  }

  public FileSystemTreeModel( File root, FileFilter filter, Comparator comparator )
  {
    this( new DefaultFileNode( root, null ), filter, comparator );
  }

  public FileSystemTreeModel( File root, FileFilter filter )
  {
    this( new DefaultFileNode( root, null ), filter );
  }

  public FileSystemTreeModel( File root )
  {
    this( new DefaultFileNode( root, null ) );
  }

  public FileSystemTreeModel( FileFilter filter, Comparator comparator, boolean blUserHome )
  {
    this( new DefaultRootFileNode( blUserHome ), filter, comparator );
  }

  public FileSystemTreeModel( FileFilter filter, boolean blUserHome )
  {
    this( new DefaultRootFileNode( blUserHome ), filter );
  }

  public FileSystemTreeModel( boolean blUserHome )
  {
    this( new DefaultRootFileNode( blUserHome ) );
  }

  public static FileNode createLocalFileSystemFileNode( FileNode parent, String sName )
  {
    File[] roots = File.listRoots();

    if( roots.length == 1 )
    {
      if( m_LocalFs == null )
        m_LocalFs = new DefaultFileNode( roots[0], parent, sName );
      return m_LocalFs;
    }
    if( m_LocalFs == null )
      m_LocalFs = new LocalFileSystemFileNode( parent, sName );
    return m_LocalFs;
  }

  public static FileNode createLocalFileSystemFileNode( FileNode parent )
  {
    return createLocalFileSystemFileNode( parent, "Local file system" );
  }

  /**
   * Returns the root of the tree.  Returns null only if the tree has
   * no nodes.
   *
   * @return  the root of the tree
   */
  public Object getRoot()
  {
    return m_Root;
  }

  /**
   * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
   * child array.  <I>parent</I> must be a node previously obtained from
   * this data source. This should not return null if <i>index</i>
   * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
   * <i>index</i> < getChildCount(<i>parent</i>)).
   *
   * @param   parent  a node in the tree, obtained from this data source
   * @return  the child of <I>parent</I> at index <I>index</I>
   */
  public Object getChild( Object parent, int index )
  {
    return( ( FileNode )parent ).getChild( index );
  }

  /**
   * Returns the number of children of <I>parent</I>.  Returns 0 if the node
   * is a leaf or if it has no children.  <I>parent</I> must be a node
   * previously obtained from this data source.
   *
   * @param   parent  a node in the tree, obtained from this data source
   * @return  the number of children of the node <I>parent</I>
   */
  public int getChildCount( Object parent )
  {
    return( ( FileNode )parent ).getChildCount();
  }

  /**
   * Returns true if <I>node</I> is a leaf.  It is possible for this method
   * to return false even if <I>node</I> has no children.  A directory in a
   * filesystem, for example, may contain no files; the node representing
   * the directory is not a leaf, but it also has no children.
   *
   * @param   node    a node in the tree, obtained from this data source
   * @return  true if <I>node</I> is a leaf
   */
  public boolean isLeaf( Object node )
  {
    return( ( FileNode )node ).isLeaf();
  }

  /**
   * Messaged when the user has altered the value for the item identified
   * by <I>path</I> to <I>newValue</I>.  If <I>newValue</I> signifies
   * a truly new value the model should post a treeNodesChanged
   * event.
   *
   * @param path path to the node that the user has altered.
   * @param newValue the new value from the TreeCellEditor.
   */
  public void valueForPathChanged( TreePath path, Object newValue )
  {

  }

  /**
   * Returns the index of child in parent.
   */
  public int getIndexOfChild( Object parent, Object child )
  {
    return( ( FileNode )parent ).getIndexOfChild( ( FileNode )child );
  }

  /**
   * Adds a listener for the TreeModelEvent posted after the tree changes.
   *
   * @see     #removeTreeModelListener
   * @param   l       the listener to add
   */
  public void addTreeModelListener( TreeModelListener l )
  {
    listenerList.add( TreeModelListener.class, l );
  }

  /**
   * Removes a listener previously added with <B>addTreeModelListener()</B>.
   *
   * @see     #addTreeModelListener
   * @param   l       the listener to remove
   */
  public void removeTreeModelListener( TreeModelListener l )
  {
    listenerList.remove( TreeModelListener.class, l );
  }

  /**
   * Notify all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created using the parameters passed into
   * the fire method.
   * @see EventListenerList
   */
  protected void fireTreeNodesChanged( Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children )
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if( listeners[i] == TreeModelListener.class )
      {
        // Lazily create the event:
        if( e == null )
          e = new TreeModelEvent( source, path,
                                  childIndices, children );
        ( ( TreeModelListener )listeners[i + 1] ).treeNodesChanged( e );
      }
    }
  }

  /**
   * Notify all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created using the parameters passed into
   * the fire method.
   * @see EventListenerList
   */
  protected void fireTreeNodesInserted( Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children )
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if( listeners[i] == TreeModelListener.class )
      {
        // Lazily create the event:
        if( e == null )
          e = new TreeModelEvent( source, path,
                                  childIndices, children );
        ( ( TreeModelListener )listeners[i + 1] ).treeNodesInserted( e );
      }
    }
  }

  /**
   * Notify all listeners that have registered interest for
   * notification on this event type.  The event instance
   * is lazily created using the parameters passed into
   * the fire method.
   * @see EventListenerList
   */
  protected void fireTreeNodesRemoved( Object source, Object[] path,
                                       int[] childIndices,
                                       Object[] children )
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if( listeners[i] == TreeModelListener.class )
      {
        // Lazily create the event:
        if( e == null )
          e = new TreeModelEvent( source, path,
                                  childIndices, children );
        ( ( TreeModelListener )listeners[i + 1] ).treeNodesRemoved( e );
      }
    }
  }

  /**
   * Module:      FileSystemTreeModel$FileNode<br> <p> Description: FileSystemTreeModel$FileNode<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
   * @history      08.04.2008 by AE: Created.<br>  </p><p>
   * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
   * @version      v0.1, 2008; %version: %<br>  </p><p>
   * @since        JDK1.4  </p>
   */
  public static abstract class FileNode
  {
    private File m_File;
    private FileNode m_Parent;
    private String m_sName;
    private FileSystemTreeModel m_Model;

    public FileNode( File file, FileNode parent, String sName )
    {
      m_File = file;
      m_Parent = parent;
      m_sName = sName;
    }

    public FileNode( File file, FileNode parent )
    {
      this( file, parent, file.getName() );
    }

    public FileSystemTreeModel getModel()
    {
      FileNode node = this;

      while( node.getParent() != null )
        node = node.getParent();

      return node.m_Model;
    }

    /** Gets the path to this file node. */

    public FileNode[] getPath()
    {
      FileNode node = this;
      int nDepth = 0;

      while( node != null )
      {
        nDepth++;
        node = node.getParent();
      }

      FileNode[] path = new FileNode[nDepth];
      node = this;

      for( int i = nDepth - 1; node != null; i-- )
      {
        path[i] = node;
        node = node.getParent();
      }

      return path;
    }

    /** Gets the path to the specified file or null if the file is not below this file node. */

    public FileNode[] getPathToFile( File file )
    {
      if( file == null )
        return null;

      if( file.equals( m_File ) )
        return getPath();

      FileNode[] shortestPath = null;

      if( m_File == null || ( m_File.isDirectory() && FileUtilities.isParentOf( m_File, file ) ) )
      {
        int nCount = getChildCount();

        for( int i = 0; i < nCount; i++ )
        {
          FileNode child = getChild( i );
          FileNode[] path = child.getPathToFile( file );

          if( path != null && ( shortestPath == null || path.length < shortestPath.length ) )
            shortestPath = path;
        }
      }

      return shortestPath;
    }

    public String toString()
    {
      return m_sName;
    }

    public File getFile()
    {
      return m_File;
    }

    public FileNode getParent()
    {
      return m_Parent;
    }

    public boolean isLeaf()
    {
      if( m_File == null )
        return false;

      if( getParent() == m_LocalFs )
        return false;

      return!m_File.isDirectory();
    }

    /*
    public boolean isFloppyDrive()
    {
      if( m_File == null )
        return false;

      return FileSystemView.getFileSystemView().isFloppyDrive( m_File );
    }
    */

    public abstract int getChildCount();

    public abstract FileNode getChild( int nIndex );

    public abstract int getIndexOfChild( FileNode child );

    public void reloadChildren()
    {}
  }

    /**
     * The default file node. This is used to represent a file or directory and can update its contents through the reloadChildren method.
     */

  public static class DefaultFileNode
    extends FileNode
  {
    /**
     * @uml.property  name="m_Children"
     * @uml.associationEnd  multiplicity="(0 -1)"
     */
    private FileNode[] m_Children;

    public DefaultFileNode( File file, FileNode parent, String sName )
    {
      super( file, parent, sName );
    }

    public DefaultFileNode( File file, FileNode parent )
    {
      this( file, parent, file.getName() );
    }

    public boolean equals( Object o )
    {
      if( o == this )
        return true;

      if( ! ( o instanceof FileNode ) )
        return false;

      File file = ( ( FileNode )o ).getFile();
      if( file == null )
        return false;

      return file.equals( getFile() );
    }

    public int getChildCount()
    {
      if( m_Children == null )
        reloadChildren();

      return m_Children.length;
    }

    public FileNode getChild( int nIndex )
    {
      if( m_Children == null )
        reloadChildren();

      return m_Children[nIndex];
    }

    public int getIndexOfChild( FileNode child )
    {
      if( m_Children == null )
        reloadChildren();

      for( int i = 0; i < m_Children.length; i++ )
      {
        if( m_Children[i].equals( child ) )
          return i;
      }

      return -1;
    }

    protected File[] listFiles()
    {
      File[] files = getFile().listFiles( getModel().m_Filter );

      if( files == null )
        return null;

      return files;
    }

    public void reloadChildren()
    {
      FileNode[] oldChildren = m_Children;

      // List children files and sort them

      File[] files = listFiles();

      if( files == null )
      {
        files = new File[0];
        m_Children = new FileNode[0];
      }

      // Create FileNode [] of children

      else
        m_Children = new FileNode[files.length];

      for( int i = 0; i < files.length; i++ )
      {
        String strName = "";
        File file;
        /* jdk14
        if( FileSystemView.getFileSystemView().isFileSystemRoot(  files[i]))
          file = FileSystemView.getFileSystemView().createFileObject( files[i].getAbsolutePath());
        else
          file = files[i];
        if( FileSystemView.getFileSystemView().isFloppyDrive( file ) )
          strName = file.getName();
        else
          strName = FileSystemView.getFileSystemView().getSystemDisplayName( file );
        */
        // jdk13
        file = files[i];
        strName = file.getName();
        // end jdk13
        if( strName.equals( "" ) )
          strName = file.getPath();
        m_Children[i] = new DefaultFileNode( file, this, strName );
      }

      final FileSystemTreeModel model = getModel();

      if( model == null )
        return;

      //Comparator comparator = model.m_Comparator;

      Arrays.sort( m_Children, model.m_Comparator );

      // Fire tree model events to notify listeners of inserted/removed children

      if( oldChildren != null )
      {
        // Determine path to this node

        final Object[] path = getPath();

        // Determine which ones were removed

        int nNumRemoved = 0;

        for( int i = 0; i < oldChildren.length; i++ )
        {
          if( Arrays.binarySearch( m_Children, oldChildren[i], model.m_Comparator ) < 0 )
            nNumRemoved++;
        }

        if( nNumRemoved > 0 )
        {
          final int[] childIndices = new int[nNumRemoved];
          final FileNode[] children = new FileNode[nNumRemoved];
          int n = 0;

          for( int i = 0; i < oldChildren.length; i++ )
          {
            if( Arrays.binarySearch( m_Children, oldChildren[i], model.m_Comparator ) < 0 )
            {
              childIndices[n] = i;
              children[n] = oldChildren[i];
              n++;
            }
          }

          if( !SwingUtilities.isEventDispatchThread() )
          {
            SwingUtilities.invokeLater( new Runnable()
            {
              public void run()
              {
                model.fireTreeNodesRemoved( this, path, childIndices, children );
              }
            } );
          }

          else
            model.fireTreeNodesRemoved( this, path, childIndices, children );
        }

        // Determine which ones were added

        int nNumAdded = 0;

        for( int i = 0; i < m_Children.length; i++ )
        {
          if( Arrays.binarySearch( oldChildren, m_Children[i], model.m_Comparator ) < 0 )
            nNumAdded++;
        }

        if( nNumAdded > 0 )
        {
          final int[] childIndices = new int[nNumAdded];
          final FileNode[] children = new FileNode[nNumAdded];
          int n = 0;

          for( int i = 0; i < m_Children.length; i++ )
          {
            if( Arrays.binarySearch( oldChildren, m_Children[i], model.m_Comparator ) < 0 )
            {
              childIndices[n] = i;
              children[n] = m_Children[i];
              n++;
            }
          }

          if( !SwingUtilities.isEventDispatchThread() )
          {
            SwingUtilities.invokeLater( new Runnable()
            {
              public void run()
              {
                model.fireTreeNodesInserted( this, path, childIndices, children );
              }
            } );
          }

          else
            model.fireTreeNodesInserted( this, path, childIndices, children );
        }

        // Determine which ones were changed

        int nNumChanged = 0;

        for( int i = 0; i < m_Children.length; i++ )
        {
          if( !oldChildren[i].toString().equals( m_Children[i].toString()))
            nNumChanged++;
        }

        if( nNumChanged > 0 )
        {
          final int[] childIndices = new int[nNumChanged];
          final FileNode[] children = new FileNode[nNumChanged];
          int n = 0;

          for( int i = 0; i < m_Children.length; i++ )
          {
            if( !oldChildren[i].toString().equals( m_Children[i].toString()))
            {
              childIndices[n] = i;
              children[n] = m_Children[i];
              n++;
            }
          }

          if( !SwingUtilities.isEventDispatchThread() )
          {
            SwingUtilities.invokeLater( new Runnable()
            {
              public void run()
              {
                model.fireTreeNodesChanged( this, path, childIndices, children );
              }
            } );
          }

          else
            model.fireTreeNodesChanged( this, path, childIndices, children );
        }
      }
    }
  }

  /** A file node that allows a group of files to be listed in it. */

  public static class GroupFileNode
    extends FileNode
  {
    public GroupFileNode( FileNode parent, String sName )
    {
      super( null, parent, sName );
    }

    public int getChildCount()
    {
      return m_Children.size();
    }

    public FileNode getChild( int nIndex )
    {
      return( FileNode )m_Children.get( nIndex );
    }

    public int getIndexOfChild( FileNode child )
    {
      return m_Children.indexOf( child );
    }

    public void addChild( FileNode node )
    {
      m_Children.add( node );

      FileSystemTreeModel model = getModel();

      if( model != null )
        model.fireTreeNodesInserted( this, getPath(), new int[]
                                     {m_Children.size() - 1}
                                     , new FileNode[]
                                     {node} );
    }

    public void removeChild( FileNode node )
    {
      int nIndex = m_Children.indexOf( node );

      if( nIndex != -1 )
      {
        m_Children.remove( nIndex );

        FileSystemTreeModel model = getModel();

        if( model != null )
          model.fireTreeNodesRemoved( this, getPath(), new int[]
                                      {nIndex}
                                      , new FileNode[]
                                      {node} );
      }
    }

    private List m_Children = new ArrayList( 4 );
  }

  /** A file node that represents the roots of the file system. */

  public static class LocalFileSystemFileNode
    extends DefaultFileNode
  {
    public LocalFileSystemFileNode( FileNode parent, String sName )
    {
      super( null, parent, sName );
    }

    protected File[] listFiles()
    {
      return File.listRoots();
    }
  }

    /** The default root node. This node has a local file system node and a user home directory node. */

  public static class DefaultRootFileNode
    extends GroupFileNode
  {
    public DefaultRootFileNode( FileNode parent, String sName, boolean blUserHome )
    {
      super( parent, sName );
      addChild( createLocalFileSystemFileNode( this ) );
      if( blUserHome )
      {
        String strUserHome = System.getProperty( "user.home" );
        if( strUserHome != null && !strUserHome.equals( "" ) )
        {
          if( m_UserHome == null )
            m_UserHome = new DefaultFileNode( new File( System.getProperty( "user.home" ) ), this,
                                              "User home" );
          addChild( m_UserHome );
        }
      }
    }

    public DefaultRootFileNode( FileNode parent, boolean blUserHome )
    {
      this( parent, "Root", blUserHome );
    }

    public DefaultRootFileNode( boolean blUserHome )
    {
      this( null, blUserHome );
    }
  }

  public static class DefaultComparator
    implements Comparator
  {
    public int compare( Object o1, Object o2 )
    {
      FileNode fn1 = ( FileNode )o1;
      FileNode fn2 = ( FileNode )o2;
      File f1 = fn1.getFile();
      File f2 = fn2.getFile();

      if( fn1.getParent() == m_LocalFs && fn2.getParent() == m_LocalFs )
        return f1.getName().compareTo( f2.getName() );

      if( f1.isDirectory() && !f2.isDirectory() )
        return -1;

      if( !f1.isDirectory() && f2.isDirectory() )
        return 1;

      return f1.getName().compareTo( f2.getName() );
    }
  }
}
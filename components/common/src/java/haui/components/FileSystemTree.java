package haui.components;

import haui.resource.ResouceManager;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


public class FileSystemTree
  extends JTree
{
  private static final long serialVersionUID = 2807995966287609716L;

  public FileSystemTree( FileSystemTreeModel model )
  {
    super( model );
    setCellRenderer( createCellRenderer() );
  }

  public FileSystemTree( boolean blUserHome)
  {
    this( new FileSystemTreeModel( blUserHome) );
  }


  public FileSystemTree()
  {
    this( new FileSystemTreeModel( false) );
  }

  protected TreeCellRenderer createCellRenderer()
  {
    return new FileCellRenderer();
  }

  /** Updates the tree view so that any expanded nodes relect any newly added/removed files.
      This method is thread safe. */

  public void updateFileSystem()
  {
    FileSystemTreeModel.FileNode fn = (FileSystemTreeModel.FileNode)( ( FileSystemTreeModel)getModel()).getRoot();

    fn.reloadChildren();

    // Get the list of expanded paths

    Enumeration expandedPathsEnum = null;

    if( SwingUtilities.isEventDispatchThread() )
      expandedPathsEnum = getExpandedDescendants( new TreePath( getModel().getRoot() ) );

    else // Make sure we execute on the event dispatch thread
    {
      final Enumeration[] enums = new Enumeration[1];

      try
      {
        SwingUtilities.invokeAndWait( new Runnable()
        {
          public void run()
          {
            enums[0] = getExpandedDescendants( new TreePath( getModel().getRoot() ) );
          }
        } );
      }

      catch( java.lang.reflect.InvocationTargetException e )
      {
        return;
      }
      catch( InterruptedException e )
      {
        return;
      }

      expandedPathsEnum = enums[0];
    }

    List expandedPaths = new ArrayList( 10 );

    while( expandedPathsEnum.hasMoreElements() )
    {
      TreePath path = ( TreePath )expandedPathsEnum.nextElement();

      expandedPaths.add( path );
    }

    // Sort so most deeply nested are first

    Collections.sort( expandedPaths, new Comparator()
    {
      public int compare( Object o1, Object o2 )
      {
        return( ( TreePath )o2 ).getPathCount() - ( ( TreePath )o1 ).getPathCount();
      }
    } );

    // Ask each expanded path to reload its children

    Iterator i = expandedPaths.iterator();

    while( i.hasNext() )
    {
      TreePath path = ( TreePath )i.next();

      ( ( FileSystemTreeModel.FileNode )path.getLastPathComponent() ).reloadChildren();
    }
  }

  public void setModel( TreeModel model )
  {
    if( ! ( model instanceof FileSystemTreeModel ) )
      throw new IllegalArgumentException( "TreeModel must be a FileSystemTreeModel" );

    super.setModel( model );
  }

  public File getFile( TreePath path )
  {
    return( ( FileSystemTreeModel.FileNode )path.getLastPathComponent() ).getFile();
  }

  public TreePath getTreePath( File file )
  {
    // Try looking under the currently selected path from the furthest node to the root node

    Object[] path = ( ( FileSystemTreeModel.FileNode )getModel().getRoot() ).getPathToFile( file );

    if( path == null )
      return null;

    return new TreePath( path );
  }

  public File getSelectedFile()
  {
    TreePath path = getSelectionPath();

    if( path == null )
      return null;

    return getFile( path );
  }

  public void setSelectedFile( File file )
  {
    File selected = getSelectedFile();

    if( selected != null && selected.equals( file ) )
      return;

    TreePath path = getTreePath( file );

    if( path == null )
      clearSelection();

    else
    {
      setSelectionPath( path );
      scrollPathToVisible( path );
    }
  }

  public File[] getSelectedFiles()
  {
    TreePath[] paths = getSelectionPaths();

    if( paths == null )
      return null;

    File[] files = new File[paths.length];

    for( int i = 0; i < paths.length; i++ )
      files[i] = getFile( paths[i] );

    return files;
  }

  public static class FileCellRenderer
    extends DefaultTreeCellRenderer
  {
    private static final long serialVersionUID = 2807995966287609715L;
    
    /** Icon to use for roots. */
    static protected ImageIcon rootsIcon;

    public FileCellRenderer()
    {
      setOpenIcon( ResouceManager.getCommonImageIcon( null, "openFolder.gif" ) ) ;
      setClosedIcon( ResouceManager.getCommonImageIcon( null, "folder.gif" ) ) ;
      setLeafIcon( ResouceManager.getCommonImageIcon( null, "file.gif" ) );
      rootsIcon = ResouceManager.getCommonImageIcon( null, "drive16.gif" );
    }

    /**
     * This is messaged from JTree whenever it needs to get the size
     * of the component or it wants to draw it.
     * This attempts to set the font based on value, which will be
     * a TreeNode.
     */
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected,
      boolean expanded, boolean leaf, int row, boolean hasFocus )
    {
      super.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus);

      if( value instanceof FileSystemTreeModel.FileNode)
      {
        FileSystemTreeModel.FileNode fn = (FileSystemTreeModel.FileNode)value;
        if( !tree.isEnabled() )
        {
          if( FileSystemTreeModel.m_LocalFs != null)
          {
            if( fn == FileSystemTreeModel.m_LocalFs )
              setDisabledIcon( null );
            else if( fn.getParent() == FileSystemTreeModel.m_LocalFs )
              setDisabledIcon( rootsIcon );
          }
        }
        else
        {
          if( FileSystemTreeModel.m_LocalFs != null)
          {
            if( fn == FileSystemTreeModel.m_LocalFs )
              setIcon( null );
            else if( fn.getParent() == FileSystemTreeModel.m_LocalFs )
            setIcon( rootsIcon );
          }
        }
      }

      return this;
    }
  }

  /*public static void main (String args[])
   throws Exception
    {
   JFrame frame = new JFrame ("Testing");
   final FileSystemTree tree = new FileSystemTree (new FileSystemTreeModel(new FileFilter ()
                     {
                      public boolean accept (File file)
                      {
                       if (file.isDirectory())
                       {
                        if (file.getName().equals("CVS"))
                         return false;
                        return true;
                       }
                       return false;
                      }
                     }));
   tree.setRootVisible(false);
   frame.getContentPane().add(new JScrollPane (tree));
   final JButton reload = new JButton ("Reload");
   reload.addActionListener(new java.awt.event.ActionListener()
          {
           public void actionPerformed (java.awt.event.ActionEvent e)
           {
            reload.setEnabled(false);
            new Thread ()
            {
            public void run ()
             {
              tree.updateFileSystem();
              reload.setEnabled(true);
             }
            }.start();
           }
          });
   frame.getContentPane().add(reload, java.awt.BorderLayout.SOUTH);
   frame.pack();
   frame.setVisible(true);
   }*/
}
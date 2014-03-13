/* *****************************************************************
 * Project: common
 * File:    GenericFileAdapterSystemView.java
 * 
 * Creation:     30.10.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.components;

import haui.awt.shell.ShellFolder;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import haui.util.CommandClass;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;

/**
 * GenericFileInterfaceSystemView
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class GenericFileInterfaceSystemView
{

  static GenericFileInterfaceSystemView genericFileSystemView = null;
  static boolean useSystemExtensionsHiding = false;
  private FileInterfaceConfiguration fic;
  
  public static GenericFileInterfaceSystemView getFileSystemView(FileInterfaceConfiguration fic)
  {
    useSystemExtensionsHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
    UIManager.addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent e)
      {
        if(e.getPropertyName().equals("lookAndFeel"))
        {
          useSystemExtensionsHiding = UIManager.getDefaults().getBoolean("FileChooser.useSystemExtensionHiding");
        }
      }
    });

    return new GenericFileInterfaceSystemView(fic);
  }

  protected GenericFileInterfaceSystemView()
  {
    super();
  }

  public GenericFileInterfaceSystemView(FileInterfaceConfiguration fic)
  {
    super();
    this.fic = fic;
  }

  public static GenericFileInterfaceSystemView getGenericFileSystemView()
  {
    return genericFileSystemView;
  }

  /**
   * On Windows, a file can appear in multiple folders, other than its
   * parent directory in the filesystem. Folder could for example be the
   * "Desktop" folder which is not the same as file.getParentFile().
   *
   * @param folder a <code>File</code> object repesenting a directory or special folder
   * @param file a <code>File</code> object
   * @return <code>true</code> if <code>folder</code> is a directory or special folder and contains <code>file</code>.
   */
  public boolean isParent(FileInterface folder, FileInterface file)
  {
    if(folder == null || file == null)
    {
      return false;
    }
    else if(folder instanceof ShellFolder)
    {
      FileInterface parent = file.getParentFileInterface();
      if(parent != null && parent.equals(folder))
      {
        return true;
      }
      FileInterface[] children = getFiles(folder, false);
      for(int i = 0; i < children.length; i++)
      {
        if(file.equals(children[i]))
        {
          return true;
        }
      }
      return false;
    }
    else
    {
      return folder.equals(file.getParentFileInterface());
    }
  }

  /**
   * Gets the list of shown (i.e. not hidden) files.
   */
  public FileInterface[] getFiles(FileInterface dir, boolean useFileHiding)
  {
    Vector files = new Vector();

    // add all files in dir
    FileInterface[] names;

    names = dir._listFiles(useFileHiding);
    FileInterface f;

    int nameCount = (names == null) ? 0 : names.length;
    for(int i = 0; i < nameCount; i++)
    {
      if(Thread.currentThread().isInterrupted())
      {
        break;
      }
      f = names[i];
      if(!(f instanceof ShellFolder))
      {
        if(isFileSystemRoot(f))
        {
          f = createFileSystemRoot(f);
        }
        if(!f.exists())
          // Not a valid file (wouldn't show in native file chooser)
          // Example: C:\pagefile.sys
          continue;
      }
      if(!useFileHiding || !f.isHidden())
      {
        files.addElement(f);
      }
    }

    return (FileInterface[])files.toArray(new FileInterface[files.size()]);
  }

  /**
   * Returns a File object constructed in dir from the given filename.
   */
  public FileInterface createFileObject(FileInterface dir, String filename)
  {
    if(dir == null)
    {
      return FileConnector.createFileInterface(dir, filename, dir.getFileInterfaceConfiguration());
    }
    else
    {
      return FileConnector.createFileInterface(dir, filename, dir.getFileInterfaceConfiguration());
    }
  }

  /**
   * Returns a File object constructed from the given path string.
   */
  public FileInterface createFileObject(String path, FileInterfaceConfiguration fic)
  {
    FileInterface f = FileConnector.createFileInterface(path, null, false, fic);
    if(isFileSystemRoot(f))
    {
      f = createFileSystemRoot(f);
    }
    return f;
  }

  /* (non-Javadoc)
   * @see javax.swing.filechooser.FileSystemView#createNewFolder(haui.io.File)
   */
  public FileInterface createNewFolder(FileInterface containingDir) throws IOException
  {
    return FileConnector.createFileInterface(containingDir, "New Folder", containingDir.getFileInterfaceConfiguration());
  }

  /**
   * Creates a new <code>FileInterface</code> object for <code>f</code> with correct
   * behavior for a file system root directory.
   *
   * @param f a <code>FileInterface</code> object representing a file system root
   *    directory, for example "/" on Unix or "C:\" on Windows.
   * @return a new <code>FileInterface</code> object
   */
  protected FileInterface createFileSystemRoot(FileInterface f)
  {
    return new FileSystemRoot(f);
  }

  /**
   * Return the user's default starting directory for the file chooser.
   *
   * @return a <code>FileInterface</code> object representing the default
   *         starting folder
   */
  public FileInterface getDefaultDirectory()
  {
    FileInterface f = (FileInterface)ShellFolder.get("fileChooserDefaultFolder", fic);
    if(isFileSystemRoot(f))
    {
      f = createFileSystemRoot(f);
    }
    return f;
  }

  /**
   * Returns the parent directory of <code>dir</code>.
   * @param dir the <code>FileInterface</code> being queried
   * @return the parent directory of <code>dir</code>, or
   *   <code>null</code> if <code>dir</code> is <code>null</code>
   */
  public FileInterface getParentDirectory(FileInterface dir)
  {
    if(dir != null && dir.exists())
    {
      ShellFolder sf = getShellFolder(dir);
      FileInterface psf = sf.getParentFile();
      if(psf != null)
      {
        if(isFileSystem(psf))
        {
          FileInterface f = psf;
          if(f != null && !f.exists())
          {
            // This could be a node under "Network Neighborhood".
            FileInterface ppsf = psf.getParentFileInterface();
            if(ppsf == null || !isFileSystem(ppsf))
            {
              // We're mostly after the exists() override for windows below.
              f = createFileSystemRoot(f);
            }
          }
          return f;
        }
        else
        {
          return psf;
        }
      }
    }
    return null;
  }

  // Providing default implementations for the remaining methods
  // because most OS file systems will likely be able to use this
  // code. If a given OS can't, override these methods in its
  // implementation.

  public FileInterface getHomeDirectory(FileInterfaceConfiguration fic)
  {
    return createFileObject(System.getProperty("user.home"), fic);
  }

  ShellFolder getShellFolder(FileInterface f)
  {
    if(!(f instanceof ShellFolder) && !(f instanceof FileSystemRoot) && isFileSystemRoot(f))
    {

      f = createFileSystemRoot(f);
    }
    try
    {
      return ShellFolder.getShellFolder(f);
    }
    catch(FileNotFoundException e)
    {
      System.err.println("FileSystemView.getShellFolder: f=" + f);
      e.printStackTrace();
      return null;
    }
    catch(InternalError e)
    {
      System.err.println("FileSystemView.getShellFolder: f=" + f);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Is dir the root of a tree in the file system, such as a drive
   * or partition. Example: Returns true for "C:\" on Windows 98.
   * 
   * @param f a <code>FileInterface</code> object representing a directory
   * @return <code>true</code> if <code>f</code> is a root of a filesystem
   * @see #isRoot
   */
  public boolean isFileSystemRoot(FileInterface dir)
  {
    return ShellFolder.isFileSystemRoot(dir);
  }

  /**
   * Determines if the given file is a root in the navigatable tree(s).
   * Examples: Windows 98 has one root, the Desktop folder. DOS has one root
   * per drive letter, <code>C:\</code>, <code>D:\</code>, etc. Unix has one root,
   * the <code>"/"</code> directory.
   *
   * The default implementation gets information from the <code>ShellFolder</code> class.
   *
   * @param f a <code>File</code> object representing a directory
   * @return <code>true</code> if <code>f</code> is a root in the navigatable tree.
   * @see #isFileSystemRoot
   */
  public boolean isRoot(FileInterface f)
  {
    if(f == null || !f.isAbsolute())
    {
      return false;
    }

    FileInterface[] roots = getRoots();
    for (int i = 0; i < roots.length; i++) {
        if (roots[i].equals(f)) {
      return true;
        }
    }
    return false;
  }

  /**
   * Returns all root partitions on this system. For example, on
   * Windows, this would be the "Desktop" folder, while on DOS this
   * would be the A: through Z: drives.
   */
  public FileInterface[] getRoots()
  {
    // Don't cache this array, because filesystem might change
    FileInterface[] roots = (FileInterface[])ShellFolder.get("roots", fic);
    List<FileInterface> list = new ArrayList<FileInterface>();

    for(int i = 0; i < roots.length; i++)
    {
      if(isFileSystemRoot(roots[i]))
      {
        list.add(createFileSystemRoot(roots[i]));
      }
    }
    return (FileInterface[])list.toArray(new FileInterface[list.size()]);
  }

  /**
   * Checks if <code>f</code> represents a real directory or file as opposed to a
   * special folder such as <code>"Desktop"</code>. Used by UI classes to decide if
   * a folder is selectable when doing directory choosing.
   *
   * @param f a <code>FileInterface</code> object
   * @return <code>true</code> if <code>f</code> is a real file or directory.
   */
  public boolean isFileSystem(FileInterface f)
  {
    if(f instanceof ShellFolder)
    {
      ShellFolder sf = (ShellFolder)f;
      // Shortcuts to directories are treated as not being file system objects,
      // so that they are never returned by JFileChooser.
      return sf.isFileSystem() && !(sf.isLink() && sf.isDirectory());
    }
    else
    {
      return true;
    }
  }

  /**
   * Returns true if the file (directory) can be visited.
   * Returns false if the directory cannot be traversed.
   *
   * @param f the <code>FileInterface</code>
   * @return <code>true</code> if the file/directory can be traversed, otherwise <code>false</code>
   * @see JFileInterfaceChooser#isTraversable
   * @see FileView#isTraversable
   */
  public Boolean isTraversable(FileInterface f)
  {
    return Boolean.valueOf(f.isDirectory());
  }

  /**
  *
  * @param parent a <code>FileInterface</code> object repesenting a directory or special folder
  * @param fileName a name of a file or folder which exists in <code>parent</code>
  * @return a FileInterface object. When parent and child are both
  * special folders, the <code>FileInterface</code> is a wrapper containing
  * a <code>ShellFolder</code> object.
  */
 public FileInterface getChild(FileInterface parent, String fileName)
  {
    if(parent instanceof ShellFolder)
    {
      FileInterface[] children = getFiles(parent, false);
      for(int i = 0; i < children.length; i++)
      {
        if(children[i].getName().equals(fileName))
        {
          return children[i];
        }
      }
    }
    return createFileObject(parent, fileName);
  }

 /**
  * Name of a file, directory, or folder as it would be displayed in
  * a system file browser. Example from Windows: the "M:\" directory
  * displays as "CD-ROM (M:)"
  *
  * The default implementation gets information from the ShellFolder class.
  *
  * @param f a <code>FileInterface</code> object
  * @return the file name as it would be displayed by a native file chooser
  * @see JFileInterfaceChooser#getName
  */
  public String getSystemDisplayName(FileInterface f)
  {
    String name = null;
    if(f != null)
    {
      name = f.getName();
      if(!name.equals("..") && !name.equals(".") && (useSystemExtensionsHiding || !isFileSystem(f) || isFileSystemRoot(f))
          && ((f instanceof ShellFolder) || f.exists()))
      {

        name = getShellFolder(f).getDisplayName();
        if(name == null || name.length() == 0)
        {
          name = f.getPath(); // e.g. "/"
        }
      }
    }
    return name;
  }

  /**
   * Icon for a file, directory, or folder as it would be displayed in a system file browser.
   * Example from Windows: the "M:\" directory displays a CD-ROM icon.
   * 
   * The default implementation gets information from the ShellFolder class.
   * 
   * @param f a <code>FileInterface</code> object
   * @return an icon as it would be displayed by a native file chooser
   * @see JFileChooser#getIcon
   */
  public Icon getSystemIcon(FileInterface f)
  {
    if(f != null)
    {
      ShellFolder sf = getShellFolder(f);
      Image img = sf.getIcon(false);
      if(img != null)
      {
        return new ImageIcon(img, sf.getFolderType());
      }
      else
      {
        return UIManager.getIcon(f.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
      }
    }
    else
    {
      return null;
    }
  }

  /**
   * Type description for a file, directory, or folder as it would be displayed in
   * a system file browser. Example from Windows: the "Desktop" folder
   * is desribed as "Desktop".
   *
   * Override for platforms with native ShellFolder implementations.
   *
   * @param f a <code>FileInterface</code> object
   * @return the file type description as it would be displayed by a native file chooser
   * or null if no native information is available.
   * @see JFileInterfaceChooser#getTypeDescription
   */
  public String getSystemTypeDescription(FileInterface f)
  {
    return null;
  }

  /**
   * Used by UI classes to decide whether to display a special icon for drives or partitions, e.g. a
   * "hard disk" icon.
   * 
   * The default implementation has no way of knowing, so always returns false.
   * 
   * @param dir a directory
   * @return <code>false</code> always
   */
  public boolean isDrive(FileInterface dir)
  {
    return false;
  }

  /**
   * Used by UI classes to decide whether to display a special icon for a floppy disk. Implies
   * isDrive(dir).
   * 
   * The default implementation has no way of knowing, so always returns false.
   * 
   * @param dir a directory
   * @return <code>false</code> always
   */
  public boolean isFloppyDrive(FileInterface dir)
  {
    return false;
  }

  /**
   * Used by UI classes to decide whether to display a special icon for a computer node, e.g.
   * "My Computer" or a network server.
   * 
   * The default implementation has no way of knowing, so always returns false.
   * 
   * @param dir a directory
   * @return <code>false</code> always
   */
  public boolean isComputerNode(FileInterface dir)
  {
    return ShellFolder.isComputerNode(dir);
  }
  
  class FileSystemRoot implements FileInterface
  {
    FileInterface file = null;
    
    public FileSystemRoot(FileInterface f)
    {
      file = f;
    }

    public FileSystemRoot(String s, FileInterfaceConfiguration fic)
    {
    }

    public boolean isDirectory()
    {
      return true;
    }

    public String getName()
    {
      return getPath();
    }

    public FileInterface[] _listFiles(FileInterfaceFilter filter)
    {
      return file._listFiles(filter);
    }

    public boolean canRead()
    {
      return file.canRead();
    }

    public boolean canWrite()
    {
      return file.canWrite();
    }

    public boolean delete()
    {
      return file.delete();
    }

    public FileInterface duplicate()
    {
      return file.duplicate();
    }

    public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut, OutputStream osErr, InputStream is)
    {
      return file.exec(strTargetPath, cmd, osOut, osErr, is);
    }

    public boolean exists()
    {
      return file.exists();
    }

    public String getAbsolutePath()
    {
      return file.getAbsolutePath();
    }

    public BufferedInputStream getBufferedInputStream() throws FileNotFoundException, IOException
    {
      return file.getBufferedInputStream();
    }

    public BufferedOutputStream getBufferedOutputStream(String strNewPath) throws FileNotFoundException
    {
      return file.getBufferedOutputStream(strNewPath);
    }

    public FileInterface getCanonicalFile() throws IOException
    {
      return file.getCanonicalFile();
    }

    public Object getConnObj()
    {
      return file.getConnObj();
    }

    public FileInterface getDirectAccessFileInterface()
    {
      return file.getDirectAccessFileInterface();
    }

    public String getId()
    {
      return file.getId();
    }

    public String getInternalPath()
    {
      return file.getInternalPath();
    }

    public String getParent()
    {
      return file.getParent();
    }

    public FileInterface getParentFileInterface()
    {
      return file.getParentFileInterface();
    }

    public String getPath()
    {
      return file.getPath();
    }

    public boolean isFile()
    {
      return file.isFile();
    }

    public boolean isHidden()
    {
      return file.isHidden();
    }

    public long lastModified()
    {
      return file.lastModified();
    }

    public long length()
    {
      return file.length();
    }

    public String[] list()
    {
      return file.list();
    }

    public void logoff()
    {
      file.logoff();
    }

    public void logon()
    {
      file.logon();
    }

    public boolean mkdir()
    {
      return file.mkdir();
    }

    public char pathSeparatorChar()
    {
      return file.pathSeparatorChar();
    }

    public boolean renameTo(FileInterface file)
    {
      return file.renameTo(file);
    }

    public char separatorChar()
    {
      return file.separatorChar();
    }

    public boolean setLastModified(long time)
    {
      return file.setLastModified(time);
    }

    public void startTerminal()
    {
      file.startTerminal();
    }

    public URL toURL()
    {
      return file.toURL();
    }

    public FileInterface[] _listFiles()
    {
      return file._listFiles();
    }

    public FileInterface[] _listRoots()
    {
      return null;
    }

    public boolean copyFile(BufferedOutputStream bo) throws IOException
    {
      return file.copyFile(bo);
    }

    public boolean copyFile(BufferedOutputStream bo, CancelProgressDialog cpd) throws IOException
    {
      return file.copyFile(bo, cpd);
    }

    public String extractToTempDir()
    {
      return file.extractToTempDir();
    }

    public String getAppName()
    {
      return file.getAppName();
    }

    public AppProperties getAppProperties()
    {
      return file.getAppProperties();
    }

    public String getCanonicalPath() throws IOException
    {
      return file.getCanonicalPath();
    }

    public Process getCurrentProcess()
    {
      return file.getCurrentProcess();
    }

    public FileInterfaceConfiguration getFileInterfaceConfiguration()
    {
      return file.getFileInterfaceConfiguration();
    }

    public String getHost()
    {
      return file.getHost();
    }

    public FileInterface getRootFileInterface()
    {
      return file.getRootFileInterface();
    }

    public String getTempDirectory()
    {
      return file.getTempDirectory();
    }

    public boolean isAbsolute()
    {
      return file.isAbsolute();
    }

    public boolean isAbsolutePath(String strPath)
    {
      return file.isAbsolutePath(strPath);
    }

    public boolean isArchive()
    {
      return file.isArchive();
    }

    public boolean isCached()
    {
      return file.isCached();
    }

    public void killCurrentProcess()
    {
      file.killCurrentProcess();
    }

    public boolean mkdirs()
    {
      return file.mkdirs();
    }

    public void setCurrentProcess(Process proc)
    {
      file.setCurrentProcess(proc);
    }

    public void setFileInterfaceConfiguration(FileInterfaceConfiguration fic)
    {
      file.setFileInterfaceConfiguration(fic);
    }

    public FileInterface[] _listFiles(boolean dontShowHidden)
    {
      return file._listFiles(dontShowHidden);
    }

    public FileInterface[] _listFiles(FileInterfaceFilter filter, boolean dontShowHidden)
    {
      return file._listFiles(filter, dontShowHidden);
    }

    public boolean isRoot()
    {
      return file.isRoot();
    }

    public boolean createNewFile() throws IOException
    {
      return file.createNewFile();
    }

    public void deleteOnExit()
    {
      file.deleteOnExit();
    }

    public boolean setReadOnly()
    {
      return file.setReadOnly();
    }

    public int compareTo(Object o)
    {
      return file.compareTo(o);
    }
  }
}

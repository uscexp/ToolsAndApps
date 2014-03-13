/* *****************************************************************
 * Project: common
 * File:    ShellFolder.java
 * 
 * Creation:     05.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.awt.shell;

import haui.components.CancelProgressDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import haui.util.CommandClass;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

/**
 * ShellFolder
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class ShellFolder implements FileInterface
{
  protected ShellFolder parent;
  protected FileInterface current;
  protected FileInterfaceConfiguration fic;

  /**
   * Create a file system shell folder from a file
   */
  ShellFolder(ShellFolder parent, String pathname, FileInterfaceConfiguration fic)
  {
    String parentPath = null;
    this.fic = fic;
    if(parent != null)
    {
      parentPath = parent.getAbsolutePath();
      if(fic == null)
        fic = parent.getFileInterfaceConfiguration();
    }
    current = FileConnector.createFileInterface((pathname != null) ? pathname : "ShellFolder",
        parentPath, false, fic);
    this.parent = parent;
  }

  /**
   * @return Whether this is a file system shell folder
   */
  public boolean isFileSystem()
  {
    return (!getPath().startsWith("ShellFolder"));
  }

  /**
   * This method must be implemented to make sure that no instances of <code>ShellFolder</code> are
   * ever serialized. If <code>isFileSystem()</code> returns <code>true</code>, then the object
   * should be representable with an instance of <code>java.io.FileInterface</code> instead. If not, then the
   * object is most likely depending on some internal (native) state and cannot be serialized.
   * 
   * @returns a <code>java.io.FileInterface</code> replacement object, or <code>null</code> if no suitable
   *          replacement can be found.
   */
  protected abstract Object writeReplace() throws java.io.ObjectStreamException;

  /**
   * Returns the path for this object's parent, or <code>null</code> if this object does not name a
   * parent folder.
   * 
   * @return the path as a String for this object's parent, or <code>null</code> if this object does
   *         not name a parent folder
   * 
   * @see java.io.FileInterface#getParent()
   * @since 1.4
   */
  public String getParent()
  {
    if(parent == null && isFileSystem())
    {
      return current.getParent();
    }
    if(parent != null)
    {
      return (parent.getPath());
    }
    else
    {
      return null;
    }
  }

  /**
   * Returns a FileInterface object representing this object's parent, or <code>null</code> if this object
   * does not name a parent folder.
   * 
   * @return a FileInterface object representing this object's parent, or <code>null</code> if this object
   *         does not name a parent folder
   * 
   * @see java.io.FileInterface#getParentFile()
   * @since 1.4
   */
  public FileInterface getParentFile()
  {
    if(parent != null)
    {
      return parent;
    }
    else if(isFileSystem())
    {
      return current.getParentFileInterface();
    }
    else
    {
      return null;
    }
  }

  public FileInterface[] listFiles()
  {
    return listFiles(true);
  }

  public FileInterface[] listFiles(boolean includeHiddenFiles)
  {
    FileInterface[] files = current._listFiles(!includeHiddenFiles);

    return files;
  }

  /**
   * @return Whether this shell folder is a link
   */
  public abstract boolean isLink();

  /**
   * @return The shell folder linked to by this shell folder, or null if this shell folder is not a
   *         link
   */
  public abstract ShellFolder getLinkLocation() throws FileNotFoundException;

  /**
   * @return The name used to display this shell folder
   */
  public abstract String getDisplayName();

  /**
   * @return The type of shell folder as a string
   */
  public abstract String getFolderType();

  /**
   * @return The executable type as a string
   */
  public abstract String getExecutableType();

  /**
   * Compares this ShellFolder with the specified ShellFolder for order.
   * 
   * @see #compareTo(Object)
   */
  public int compareTo(FileInterface file2)
  {
    if(file2 == null || !(file2 instanceof ShellFolder) || ((file2 instanceof ShellFolder) && ((ShellFolder)file2).isFileSystem()))
    {

      if(isFileSystem())
      {
        return current.compareTo(file2);
      }
      else
      {
        return -1;
      }
    }
    else
    {
      if(isFileSystem())
      {
        return 1;
      }
      else
      {
        return getName().compareTo(file2.getName());
      }
    }
  }

  /**
   * @param getLargeIcon whether to return large icon (ignored in base implementation)
   * @return The icon used to display this shell folder
   */
  public Image getIcon(boolean getLargeIcon)
  {
    return null;
  }

  // Static

  private static ShellFolderManager shellFolderManager;

  static
  {
//    Class managerClass = (Class)Toolkit.getDefaultToolkit().getDesktopProperty("Shell.shellFolderManager");
    Class managerClass = null;
    if(managerClass == null)
    {
      managerClass = ShellFolderManager.class;
    }
    try
    {
      shellFolderManager = (ShellFolderManager)managerClass.newInstance();
    }
    catch(InstantiationException e)
    {
      throw new Error("Could not instantiate Shell Folder Manager: " + managerClass.getName());
    }
    catch(IllegalAccessException e)
    {
      throw new Error("Could not access Shell Folder Manager: " + managerClass.getName());
    }
  }

  /**
   * Return a shell folder from a file object
   * 
   * @exception FileNotFoundException if file does not exist
   */
  public static ShellFolder getShellFolder(FileInterface file) throws FileNotFoundException
  {
    if(file instanceof ShellFolder)
    {
      return (ShellFolder)file;
    }
    if(!file.exists())
    {
      throw new FileNotFoundException();
    }
    return shellFolderManager.createShellFolder(file);
  }

  /**
   * @param key a <code>String</code>
   * @return An Object matching the string <code>key</code>.
   * @see ShellFolderManager#get(String)
   */
  public static Object get(String key, FileInterfaceConfiguration fic)
  {
    return shellFolderManager.get(key, fic);
  }

  /**
   * Does <code>dir</code> represent a "computer" such as a node on the network, or "My Computer" on
   * the desktop.
   */
  public static boolean isComputerNode(FileInterface dir)
  {
    return shellFolderManager.isComputerNode(dir);
  }

  /**
   * @return Whether this is a file system root directory
   */
  public static boolean isFileSystemRoot(FileInterface dir)
  {
    return shellFolderManager.isFileSystemRoot(dir);
  }

  /**
   * Canonicalizes files that don't have symbolic links in their path. Normalizes files that do,
   * preserving symbolic links from being resolved.
   */
  public static FileInterface getNormalizedFile(FileInterface f) throws IOException
  {
    FileInterface canonical = f.getCanonicalFile();
    // FIXME return correct FileInterface
    return canonical;
//    if(f.equals(canonical))
//    {
//      // path of f doesn't contain symbolic links
//      return canonical;
//    }
//
//    // preserve symbolic links from being resolved
//    return new FileInterface(f.toURI().normalize());
  }

  // Override FileInterface methods

  public static void sortFiles(List<FileInterface> files)
  {
    shellFolderManager.sortFiles(files);
  }

  public boolean isAbsolute()
  {
    return (!isFileSystem() || current.isAbsolute());
  }

  public FileInterface getAbsoluteFile()
  {
    return (isFileSystem() ? current : this);
  }

  public boolean canRead()
  {
    return (isFileSystem() ? current.canRead() : true); // ((Fix?))
  }

  /**
   * Returns true if folder allows creation of children. True for the "Desktop" folder, but false
   * for the "My Computer" folder.
   */
  public boolean canWrite()
  {
    return (isFileSystem() ? current.canWrite() : false); // ((Fix?))
  }

  public boolean exists()
  {
    // Assume top-level drives exist, because state is uncertain for
    // removable drives.
    return (!isFileSystem() || isFileSystemRoot(this) || current.exists());
  }

  public boolean isDirectory()
  {
    return (isFileSystem() ? current.isDirectory() : true); // ((Fix?))
  }

  public boolean isFile()
  {
    return (isFileSystem() ? current.isFile() : !isDirectory()); // ((Fix?))
  }

  public long lastModified()
  {
    return (isFileSystem() ? current.lastModified() : 0L); // ((Fix?))
  }

  public long length()
  {
    return (isFileSystem() ? current.length() : 0L); // ((Fix?))
  }

  public boolean createNewFile() throws IOException
  {
    return (isFileSystem() ? current.createNewFile() : false);
  }

  public boolean delete()
  {
    return (isFileSystem() ? current.delete() : false); // ((Fix?))
  }

  public void deleteOnExit()
  {
    if(isFileSystem())
    {
      current.deleteOnExit();
    }
    else
    {
      // Do nothing // ((Fix?))
    }
  }

  public boolean mkdir()
  {
    return (isFileSystem() ? current.mkdir() : false);
  }

  public boolean mkdirs()
  {
    return (isFileSystem() ? current.mkdirs() : false);
  }

  public boolean renameTo(FileInterface dest)
  {
    return (isFileSystem() ? current.renameTo(dest) : false); // ((Fix?))
  }

  public boolean setLastModified(long time)
  {
    return (isFileSystem() ? current.setLastModified(time) : false); // ((Fix?))
  }

  public boolean setReadOnly()
  {
    return (isFileSystem() ? current.setReadOnly() : false); // ((Fix?))
  }

  public String toString()
  {
    return (isFileSystem() ? current.toString() : getDisplayName());
  }

  public static ShellFolderColumnInfo[] getFolderColumns(FileInterface dir)
  {
    return shellFolderManager.getFolderColumns(dir);
  }

  public static Object getFolderColumnValue(FileInterface file, int column)
  {
    return shellFolderManager.getFolderColumnValue(file, column);
  }

  public ShellFolderColumnInfo[] getFolderColumns()
  {
    return null;
  }

  public Object getFolderColumnValue(int column)
  {
    return null;
  }
  
  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#_listFiles(haui.io.FileInterface.filter.FileInterfaceFilter)
   */
  public FileInterface[] _listFiles(FileInterfaceFilter filter)
  {
    return current._listFiles(filter);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#_listFiles()
   */
  public FileInterface[] _listFiles()
  {
    return current._listFiles();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#_listFiles(boolean)
   */
  public FileInterface[] _listFiles(boolean dontShowHidden)
  {
    return current._listFiles(dontShowHidden);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#_listFiles(haui.io.FileInterface.filter.FileInterfaceFilter, boolean)
   */
  public FileInterface[] _listFiles(FileInterfaceFilter filter, boolean dontShowHidden)
  {
    return current._listFiles(filter, dontShowHidden);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#_listRoots()
   */
  public FileInterface[] _listRoots()
  {
    return current._listRoots();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#copyFile(java.io.BufferedOutputStream)
   */
  public boolean copyFile(BufferedOutputStream bo) throws IOException
  {
    return current.copyFile(bo);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#copyFile(java.io.BufferedOutputStream, haui.components.CancelProgressDialog)
   */
  public boolean copyFile(BufferedOutputStream bo, CancelProgressDialog cpd) throws IOException
  {
    return current.copyFile(bo, cpd);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#duplicate()
   */
  public FileInterface duplicate()
  {
    return current.duplicate();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#exec(java.lang.String, haui.util.CommandClass, java.io.OutputStream, java.io.OutputStream, java.io.InputStream)
   */
  public int exec(String strTargetPath, CommandClass cmd, OutputStream osOut, OutputStream osErr, InputStream is)
  {
    return current.exec(strTargetPath, cmd, osOut, osErr, is);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#extractToTempDir()
   */
  public String extractToTempDir()
  {
    return current.extractToTempDir();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getAbsolutePath()
   */
  public String getAbsolutePath()
  {
    return current.getAbsolutePath();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getAppName()
   */
  public String getAppName()
  {
    return current.getAppName();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getAppProperties()
   */
  public AppProperties getAppProperties()
  {
    return current.getAppProperties();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getBufferedInputStream()
   */
  public BufferedInputStream getBufferedInputStream() throws FileNotFoundException, IOException
  {
    return current.getBufferedInputStream();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getBufferedOutputStream(java.lang.String)
   */
  public BufferedOutputStream getBufferedOutputStream(String strNewPath) throws FileNotFoundException
  {
    return current.getBufferedOutputStream(strNewPath);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getCanonicalFile()
   */
  public FileInterface getCanonicalFile() throws IOException
  {
    return current.getCanonicalFile();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getCanonicalPath()
   */
  public String getCanonicalPath() throws IOException
  {
    return current.getCanonicalPath();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getConnObj()
   */
  public Object getConnObj()
  {
    return current.getConnObj();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getCurrentProcess()
   */
  public Process getCurrentProcess()
  {
    return current.getCurrentProcess();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getDirectAccessFileInterface()
   */
  public FileInterface getDirectAccessFileInterface()
  {
    return current.getDirectAccessFileInterface();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getFileInterfaceConfiguration()
   */
  public FileInterfaceConfiguration getFileInterfaceConfiguration()
  {
    return current.getFileInterfaceConfiguration();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getHost()
   */
  public String getHost()
  {
    return current.getHost();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getId()
   */
  public String getId()
  {
    return current.getId();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getInternalPath()
   */
  public String getInternalPath()
  {
    return current.getInternalPath();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getName()
   */
  public String getName()
  {
    return current.getName();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getParentFileInterface()
   */
  public FileInterface getParentFileInterface()
  {
    return current.getParentFileInterface();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getPath()
   */
  public String getPath()
  {
    return current.getPath();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getRootFileInterface()
   */
  public FileInterface getRootFileInterface()
  {
    return current.getRootFileInterface();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#getTempDirectory()
   */
  public String getTempDirectory()
  {
    return current.getTempDirectory();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#isAbsolutePath(java.lang.String)
   */
  public boolean isAbsolutePath(String strPath)
  {
    return current.isAbsolutePath(strPath);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#isArchive()
   */
  public boolean isArchive()
  {
    return current.isArchive();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#isCached()
   */
  public boolean isCached()
  {
    return current.isCached();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#isHidden()
   */
  public boolean isHidden()
  {
    return current.isHidden();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#isRoot()
   */
  public boolean isRoot()
  {
    return current.isRoot();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#killCurrentProcess()
   */
  public void killCurrentProcess()
  {
    current.killCurrentProcess();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#list()
   */
  public String[] list()
  {
    return current.list();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#logoff()
   */
  public void logoff()
  {
    current.logoff();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#logon()
   */
  public void logon()
  {
    current.logon();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#pathSeparatorChar()
   */
  public char pathSeparatorChar()
  {
    return current.pathSeparatorChar();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#separatorChar()
   */
  public char separatorChar()
  {
    return current.separatorChar();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#setCurrentProcess(java.lang.Process)
   */
  public void setCurrentProcess(Process proc)
  {
    current.setCurrentProcess(proc);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#setFileInterfaceConfiguration(haui.io.FileInterface.configuration.FileInterfaceConfiguration)
   */
  public void setFileInterfaceConfiguration(FileInterfaceConfiguration fic)
  {
    current.setFileInterfaceConfiguration(fic);
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#startTerminal()
   */
  public void startTerminal()
  {
    current.startTerminal();
  }

  /* (non-Javadoc)
   * @see haui.io.FileInterface.FileInterface#toURL()
   */
  public URL toURL()
  {
    return current.toURL();
  }

  public int compareTo(Object o)
  {
    return current.compareTo(o);
  }

}

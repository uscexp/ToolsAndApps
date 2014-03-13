/* *****************************************************************
 * Project: common
 * File:    ShellFolderManager.java
 * 
 * Creation:     05.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.awt.shell;

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.SwingConstants;

/**
 * ShellFolderManager
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ShellFolderManager
{
  private static final String COLUMN_NAME = "FileChooser.fileNameHeaderText";
  private static final String COLUMN_SIZE = "FileChooser.fileSizeHeaderText";
  private static final String COLUMN_DATE = "FileChooser.fileDateHeaderText";

  /**
   * Create a shell folder from a file. Override to return machine-dependent behavior.
   */
  public ShellFolder createShellFolder(FileInterface file) throws FileNotFoundException
  {
    return new DefaultShellFolder(null, file);
  }

  /**
   * @param key a <code>String</code> "fileChooserDefaultFolder": Returns a <code>FileInterface</code> - the
   *          default shellfolder for a new filechooser "roots": Returns a <code>FileInterface[]</code> -
   *          containing the root(s) of the displayable hieararchy "fileChooserComboBoxFolders":
   *          Returns a <code>FileInterface[]</code> - an array of shellfolders representing the list to show
   *          by default in the file chooser's combobox "fileChooserShortcutPanelFolders": Returns a
   *          <code>FileInterface[]</code> - an array of shellfolders representing well-known folders, such
   *          as Desktop, Documents, History, Network, Home, etc. This is used in the shortcut panel
   *          of the filechooser on Windows 2000 and Windows Me. "fileChooserIcon nn": Returns an
   *          <code>Image</code> - icon nn from resource 124 in comctl32.dll (Windows only).
   * 
   * @return An Object matching the key string.
   */
  public Object get(String key, FileInterfaceConfiguration fic)
  {
    if(key.equals("fileChooserDefaultFolder"))
    {
      // Return the default shellfolder for a new filechooser
      FileInterface homeDir = FileConnector.createFileInterface(System.getProperty("user.home"), null, false, fic);
      if(!homeDir.exists()) {
        homeDir = FileConnector.createFileInterface(".", null, false, fic);
      }
      try
      {
        return createShellFolder(homeDir);
      }
      catch(FileNotFoundException e)
      {
        return homeDir;
      }
    }
    else if(key.equals("roots"))
    {
      // The root(s) of the displayable hieararchy
      return FileConnector.createFileInterface(".", null, false, fic)._listRoots();
    }
    else if(key.equals("fileChooserComboBoxFolders"))
    {
      // Return an array of ShellFolders representing the list to
      // show by default in the file chooser's combobox
      return get("roots", fic);
    }
    else if(key.equals("fileChooserShortcutPanelFolders"))
    {
      // Return an array of ShellFolders representing well-known
      // folders, such as Desktop, Documents, History, Network, Home, etc.
      // This is used in the shortcut panel of the filechooser on Windows 2000
      // and Windows Me
      return new FileInterface[] { (FileInterface)get("fileChooserDefaultFolder", fic) };
    }
    return null;
  }

  /**
   * Does <code>dir</code> represent a "computer" such as a node on the network, or "My Computer" on
   * the desktop.
   */
  public boolean isComputerNode(FileInterface dir)
  {
    return false;
  }

  public boolean isFileSystemRoot(FileInterface dir)
  {
    if(dir instanceof ShellFolder && !((ShellFolder)dir).isFileSystem())
    {
      return false;
    }
    return (dir.getParentFileInterface() == null);
  }

  public void sortFiles(List<FileInterface> files)
  {
    Collections.sort(files, fileComparator);
  }

  private Comparator fileComparator = new Comparator()
  {
    public int compare(Object a, Object b)
    {
      return compare((FileInterface)a, (FileInterface)b);
    }

    public int compare(FileInterface f1, FileInterface f2)
    {
      ShellFolder sf1 = null;
      ShellFolder sf2 = null;

      if(f1 instanceof ShellFolder)
      {
        sf1 = (ShellFolder)f1;
        if(sf1.isFileSystem())
        {
          sf1 = null;
        }
      }
      if(f2 instanceof ShellFolder)
      {
        sf2 = (ShellFolder)f2;
        if(sf2.isFileSystem())
        {
          sf2 = null;
        }
      }

      if(sf1 != null && sf2 != null)
      {
        return sf1.compareTo(sf2);
      }
      else if(sf1 != null)
      {
        return -1; // Non-file shellfolders sort before files
      }
      else if(sf2 != null)
      {
        return 1;
      }
      else
      {
        String name1 = f1.getName();
        String name2 = f2.getName();

        // First ignore case when comparing
        int diff = name1.toLowerCase().compareTo(name2.toLowerCase());
        if(diff != 0)
        {
          return diff;
        }
        else
        {
          // May differ in case (e.g. "mail" vs. "Mail")
          // We need this test for consistent sorting
          return name1.compareTo(name2);
        }
      }
    }
  };

  public ShellFolderColumnInfo[] getFolderColumns(FileInterface dir)
  {
    ShellFolderColumnInfo[] columns = null;

    if(dir instanceof ShellFolder)
    {
      columns = ((ShellFolder)dir).getFolderColumns();
    }

    if(columns == null)
    {
      columns = new ShellFolderColumnInfo[] { new ShellFolderColumnInfo(COLUMN_NAME, 150, SwingConstants.LEADING, true, null, fileComparator),
          new ShellFolderColumnInfo(COLUMN_SIZE, 75, SwingConstants.RIGHT, true, null, ComparableComparator.getInstance(), true),
          new ShellFolderColumnInfo(COLUMN_DATE, 130, SwingConstants.LEADING, true, null, ComparableComparator.getInstance(), true) };
    }

    return columns;
  }

  public Object getFolderColumnValue(FileInterface file, int column)
  {
    if(file instanceof ShellFolder)
    {
      Object value = ((ShellFolder)file).getFolderColumnValue(column);
      if(value != null)
      {
        return value;
      }
    }

    if(file == null || !file.exists())
    {
      return null;
    }

    switch(column)
    {
      case 0:
        // By default, file name will be rendered using getSystemDisplayName()
        return file;

      case 1: // size
        return file.isDirectory() ? null : new Long(file.length());

      case 2: // date
        if(isFileSystemRoot(file))
        {
          return null;
        }
        long time = file.lastModified();
        return (time == 0L) ? null : new Date(time);

      default:
        return null;
    }
  }

  /**
   * This class provides a default comparator for the default column set
   */
  private static class ComparableComparator implements Comparator
  {
    private static Comparator instance;

    public static Comparator getInstance()
    {
      if(instance == null)
      {
        instance = new ComparableComparator();
      }
      return instance;
    }

    public int compare(Object o1, Object o2)
    {
      int gt;

      if(o1 == null && o2 == null)
      {
        gt = 0;
      }
      else if(o1 != null && o2 == null)
      {
        gt = 1;
      }
      else if(o1 == null && o2 != null)
      {
        gt = -1;
      }
      else if(o1 instanceof Comparable)
      {
        gt = ((Comparable)o1).compareTo(o2);
      }
      else
      {
        gt = 0;
      }

      return gt;
    }
  }
}

/* *****************************************************************
 * Project: common
 * File:    FileTransferable.java
 * 
 * Creation:     05.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package javax.swing.plaf.basic;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;

/**
 * FileTransferable
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class FileTransferable extends BasicTransferable
{
  Object[] fileData;

  public FileTransferable(String plainData, String htmlData, Object[] fileData)
  {
    super(plainData, htmlData);
    this.fileData = fileData;
  }

  /**
   * Best format of the file chooser is DataFlavor.javaFileListFlavor.
   */
  protected DataFlavor[] getRicherFlavors()
  {
    DataFlavor[] flavors = new DataFlavor[1];
    flavors[0] = DataFlavor.javaFileListFlavor;
    return flavors;
  }

  /**
   * The only richer format supported is the file list flavor
   */
  protected Object getRicherData(DataFlavor flavor)
  {
    if(DataFlavor.javaFileListFlavor.equals(flavor))
    {
      ArrayList files = new ArrayList();
      for(int i = 0; i < fileData.length; i++)
      {
        files.add(fileData[i]);
      }
      return files;
    }
    return null;
  }

}

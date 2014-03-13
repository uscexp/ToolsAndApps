/* *****************************************************************
 * Project: common
 * File:    FileView.java
 * 
 * Creation:     05.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.swing.filechooser;

import haui.io.FileInterface.FileInterface;

import javax.swing.Icon;

/**
 * FileView
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class FileView
{
  /**
   * The name of the file. Normally this would be simply <code>f.getName()</code>.
   */
  public String getName(FileInterface f)
  {
    return null;
  };

  /**
   * A human readable description of the file. For example, a file named <i>jag.jpg</i> might have a
   * description that read: "A JPEG image file of James Gosling's face".
   */
  public String getDescription(FileInterface f)
  {
    return null;
  }

  /**
   * A human readable description of the type of the file. For example, a <code>jpg</code> file
   * might have a type description of: "A JPEG Compressed Image File"
   */
  public String getTypeDescription(FileInterface f)
  {
    return null;
  }

  /**
   * The icon that represents this file in the <code>JFileChooser</code>.
   */
  public Icon getIcon(FileInterface f)
  {
    return null;
  }

  /**
   * Whether the directory is traversable or not. This might be useful, for example, if you want a
   * directory to represent a compound document and don't want the user to descend into it.
   */
  public Boolean isTraversable(FileInterface f)
  {
    return null;
  }

}

/* *****************************************************************
 * Project: common
 * File:    FileChooserUI.java
 * 
 * Creation:     05.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.swing.plaf;

import haui.components.JFileInterfaceChooser;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.swing.filechooser.FileView;

import javax.swing.plaf.ComponentUI;

/**
 * FileChooserUI
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class FileInterfaceChooserUI extends ComponentUI
{
  public abstract FileInterfaceFilter getAcceptAllFileFilter(JFileInterfaceChooser fc);
  public abstract FileView getFileView(JFileInterfaceChooser fc);

  public abstract String getApproveButtonText(JFileInterfaceChooser fc);
  public abstract String getDialogTitle(JFileInterfaceChooser fc);

  public abstract void rescanCurrentDirectory(JFileInterfaceChooser fc);
  public abstract void ensureFileIsVisible(JFileInterfaceChooser fc, FileInterface f);
}

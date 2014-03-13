/* *****************************************************************
 * Project: common
 * FileInterface:    WindowsPlacesBar.java
 * 
 * Creation:     06.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.swing;

import haui.awt.shell.ShellFolder;
import haui.components.GenericFileInterfaceSystemView;
import haui.components.JFileInterfaceChooser;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

/**
 * WindowsPlacesBar
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public class WindowsPlacesBar extends JToolBar implements ActionListener, PropertyChangeListener
{

  public WindowsPlacesBar(JFileInterfaceChooser jfilechooser, boolean flag)
  {
    super(1);
    fc = jfilechooser;
    setFloatable(false);
    putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    boolean flag1 = System.getProperty("os.name").startsWith("Windows") && System.getProperty("os.version").compareTo("5.1") >= 0;
    if(flag)
    {
      buttonSize = new Dimension(83, 69);
      putClientProperty("XPStyle.subAppName", "placesbar");
      setBorder(new EmptyBorder(1, 1, 1, 1));
    }
    else
    {
      buttonSize = new Dimension(83, flag1 ? 65 : 54);
      setBorder(new BevelBorder(1, UIManager.getColor("ToolBar.highlight"), UIManager.getColor("ToolBar.background"), UIManager.getColor("ToolBar.darkShadow"),
          UIManager.getColor("ToolBar.shadow")));
    }
    Color color = new Color(UIManager.getColor("ToolBar.shadow").getRGB());
    setBackground(color);
    GenericFileInterfaceSystemView filesystemview = jfilechooser.getFileSystemView();
    files = (FileInterface[])(FileInterface[])ShellFolder.get("fileChooserShortcutPanelFolders",
        fc.getFileInterfaceConfiguration());
    buttons = new JToggleButton[files.length];
    buttonGroup = new ButtonGroup();
    for(int i = 0; i < files.length; i++)
    {
      if(filesystemview.isFileSystemRoot(files[i]))
        files[i] = filesystemview.createFileObject(files[i].getAbsolutePath(), fc.getFileInterfaceConfiguration());
      String s = filesystemview.getSystemDisplayName(files[i]);
      FileInterface fiTpl = FileConnector.createTemplateFileInterface(fc.getFileInterfaceConfiguration());
      int j = s.lastIndexOf(fiTpl.separatorChar());
      if(j >= 0 && j < s.length() - 1)
        s = s.substring(j + 1);
      Object obj = null;
      if(files[i] instanceof ShellFolder)
      {
        ShellFolder shellfolder = (ShellFolder)files[i];
        obj = new ImageIcon(shellfolder.getIcon(true), shellfolder.getFolderType());
      }
      else
      {
        obj = filesystemview.getSystemIcon(files[i]);
      }
      buttons[i] = new JToggleButton(s, ((javax.swing.Icon)(obj)));
      if(flag1)
      {
        buttons[i].setIconTextGap(2);
        buttons[i].setMargin(new Insets(2, 2, 2, 2));
        buttons[i].setText((new StringBuilder()).append("<html><center>").append(s).append("</center></html>").toString());
      }
      if(flag)
      {
        buttons[i].putClientProperty("XPStyle.subAppName", "placesbar");
      }
      else
      {
        Color color1 = new Color(UIManager.getColor("List.selectionForeground").getRGB());
        buttons[i].setBackground(color);
        buttons[i].setForeground(color1);
      }
      buttons[i].setHorizontalTextPosition(0);
      buttons[i].setVerticalTextPosition(3);
      buttons[i].setAlignmentX(0.5F);
      buttons[i].setPreferredSize(buttonSize);
      buttons[i].setMaximumSize(buttonSize);
      buttons[i].addActionListener(this);
      add(buttons[i]);
      if(i < files.length - 1 && flag)
        add(Box.createRigidArea(new Dimension(1, 1)));
      buttonGroup.add(buttons[i]);
    }

    doDirectoryChanged(jfilechooser.getCurrentDirectory());
  }

  protected void doDirectoryChanged(FileInterface file)
  {
    for(int i = 0; i < buttons.length; i++)
    {
      JToggleButton jtogglebutton = buttons[i];
      if(files[i].equals(file))
      {
        jtogglebutton.setSelected(true);
        break;
      }
      if(jtogglebutton.isSelected())
      {
        buttonGroup.remove(jtogglebutton);
        jtogglebutton.setSelected(false);
        buttonGroup.add(jtogglebutton);
      }
    }

  }

  public void propertyChange(PropertyChangeEvent propertychangeevent)
  {
    String s = propertychangeevent.getPropertyName();
    if(s == "directoryChanged")
      doDirectoryChanged(fc.getCurrentDirectory());
  }

  public void actionPerformed(ActionEvent actionevent)
  {
    JToggleButton jtogglebutton = (JToggleButton)actionevent.getSource();
    int i = 0;
    do
    {
      if(i >= buttons.length)
        break;
      if(jtogglebutton == buttons[i])
      {
        fc.setCurrentDirectory(files[i]);
        break;
      }
      i++;
    } while(true);
  }

  public Dimension getPreferredSize()
  {
    Dimension dimension = super.getMinimumSize();
    Dimension dimension1 = super.getPreferredSize();
    if(dimension.height > dimension1.height)
      dimension1 = new Dimension(dimension1.width, dimension.height);
    return dimension1;
  }

  JFileInterfaceChooser fc;
  JToggleButton buttons[];
  ButtonGroup buttonGroup;
  FileInterface files[];
  final Dimension buttonSize;
}

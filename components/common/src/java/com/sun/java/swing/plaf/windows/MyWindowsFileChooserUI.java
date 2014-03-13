/* *****************************************************************
 * Project: common
 * FileInterface:    WindowsFileChooserUI.java
 * 
 * Creation:     06.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package com.sun.java.swing.plaf.windows;

import haui.awt.shell.ShellFolder;
import haui.components.GenericFileInterfaceSystemView;
import haui.components.JFileInterfaceChooser;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.swing.FilePane;
import haui.swing.WindowsPlacesBar;
import haui.swing.filechooser.FileView;
import haui.swing.plaf.basic.BasicDirectoryModel;
import haui.swing.plaf.basic.BasicFileChooserUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;

/**
 * WindowsFileChooserUI
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public class MyWindowsFileChooserUI extends BasicFileChooserUI
{

  // The following are private because the implementation of the
  // Windows FileChooser L&F is not complete yet.

  private static final String OS_VERSION = System.getProperty("os.version");

  private JPanel centerPanel;

  private JLabel lookInLabel;
  private JComboBox directoryComboBox;
  private DirectoryComboBoxModel directoryComboBoxModel;
  private ActionListener directoryComboBoxAction = new DirectoryComboBoxAction();

  private FilterComboBoxModel filterComboBoxModel;

  private JTextField filenameTextField;
  private JToggleButton listViewButton;
  private JToggleButton detailsViewButton;
  private FilePane filePane;
  private WindowsPlacesBar placesBar;
  private boolean useShellFolder;

  private JButton approveButton;
  private JButton cancelButton;

  private JPanel buttonPanel;
  private JPanel bottomPanel;

  private JComboBox filterComboBox;

  private static final Dimension hstrut10 = new Dimension(10, 1);

  private static final Dimension vstrut4 = new Dimension(1, 4);
  private static final Dimension vstrut6 = new Dimension(1, 6);
  private static final Dimension vstrut8 = new Dimension(1, 8);

  private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);

  // Preferred and Minimum sizes for the dialog box
  private static int PREF_WIDTH = 425;
  private static int PREF_HEIGHT = 245;
  private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);

  private static int MIN_WIDTH = 425;
  private static int MIN_HEIGHT = 245;
  private static Dimension MIN_SIZE = new Dimension(MIN_WIDTH, MIN_HEIGHT);

  private static int LIST_PREF_WIDTH = 444;
  private static int LIST_PREF_HEIGHT = 138;
  private static Dimension LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);

  // Labels, mnemonics, and tooltips (oh my!)
  private int lookInLabelMnemonic = 0;
  private String lookInLabelText = null;
  private String saveInLabelText = null;

  private int fileNameLabelMnemonic = 0;
  private String fileNameLabelText = null;

  private int filesOfTypeLabelMnemonic = 0;
  private String filesOfTypeLabelText = null;

  private String upFolderToolTipText = null;
  private String upFolderAccessibleName = null;

  private String homeFolderToolTipText = null;
  private String homeFolderAccessibleName = null;

  private String newFolderToolTipText = null;
  private String newFolderAccessibleName = null;

  private String listViewButtonToolTipText = null;
  private String listViewButtonAccessibleName = null;

  private String detailsViewButtonToolTipText = null;
  private String detailsViewButtonAccessibleName = null;

  private BasicFileView fileView = new WindowsFileView();

  //
  // ComponentUI Interface Implementation methods
  //
  public static ComponentUI createUI(JComponent c)
  {
    return new MyWindowsFileChooserUI((JFileInterfaceChooser)c);
  }

  public MyWindowsFileChooserUI(JFileInterfaceChooser filechooser)
  {
    super(filechooser);
  }

  public void installUI(JComponent c)
  {
    super.installUI(c);
  }

  public void uninstallComponents(JFileInterfaceChooser fc)
  {
    fc.removeAll();
  }

  private class WindowsFileChooserUIAccessor implements FilePane.FileChooserUIAccessor
  {
    public JFileInterfaceChooser getFileChooser()
    {
      return MyWindowsFileChooserUI.this.getFileChooser();
    }

    public BasicDirectoryModel getModel()
    {
      return MyWindowsFileChooserUI.this.getModel();
    }

    public JPanel createList()
    {
      return MyWindowsFileChooserUI.this.createList(getFileChooser());
    }

    public JPanel createDetailsView()
    {
      return MyWindowsFileChooserUI.this.createDetailsView(getFileChooser());
    }

    public boolean isDirectorySelected()
    {
      return MyWindowsFileChooserUI.this.isDirectorySelected();
    }

    public FileInterface getDirectory()
    {
      return MyWindowsFileChooserUI.this.getDirectory();
    }

    public Action getChangeToParentDirectoryAction()
    {
      return MyWindowsFileChooserUI.this.getChangeToParentDirectoryAction();
    }

    public Action getApproveSelectionAction()
    {
      return MyWindowsFileChooserUI.this.getApproveSelectionAction();
    }

    public Action getNewFolderAction()
    {
      return MyWindowsFileChooserUI.this.getNewFolderAction();
    }

    public MouseListener createDoubleClickListener(JList list)
    {
      return MyWindowsFileChooserUI.this.createDoubleClickListener(getFileChooser(), list);
    }

    public ListSelectionListener createListSelectionListener()
    {
      return MyWindowsFileChooserUI.this.createListSelectionListener(getFileChooser());
    }
  }

  public void installComponents(JFileInterfaceChooser fc)
  {
    filePane = new FilePane(new WindowsFileChooserUIAccessor());
    fc.addPropertyChangeListener(filePane);

    GenericFileInterfaceSystemView fsv = fc.getFileSystemView();

    fc.setBorder(new EmptyBorder(4, 10, 10, 10));
    fc.setLayout(new BorderLayout(8, 8));

    updateUseShellFolder();

    // ********************************* //
    // **** Construct the top panel **** //
    // ********************************* //

    // Directory manipulation buttons
    JToolBar topPanel = new JToolBar();
    topPanel.setFloatable(false);
    if(OS_VERSION.compareTo("4.9") >= 0)
    { // Windows Me/2000 and later (4.90/5.0)
      topPanel.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }

    // Add the top panel to the fileChooser
    fc.add(topPanel, BorderLayout.NORTH);

    // ComboBox Label
    lookInLabel = new JLabel(lookInLabelText, JLabel.TRAILING)
    {
      public Dimension getPreferredSize()
      {
        return getMinimumSize();
      }

      public Dimension getMinimumSize()
      {
        Dimension d = super.getPreferredSize();
        if(placesBar != null)
        {
          d.width = Math.max(d.width, placesBar.getWidth());
        }
        return d;
      }
    };
    lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
    lookInLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    lookInLabel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    topPanel.add(lookInLabel);
    topPanel.add(Box.createRigidArea(new Dimension(8, 0)));

    // CurrentDir ComboBox
    directoryComboBox = new JComboBox()
    {
      public Dimension getMinimumSize()
      {
        Dimension d = super.getMinimumSize();
        d.width = 60;
        return d;
      }

      public Dimension getPreferredSize()
      {
        Dimension d = super.getPreferredSize();
        // Must be small enough to not affect total width.
        d.width = 150;
        return d;
      }
    };
    directoryComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
    lookInLabel.setLabelFor(directoryComboBox);
    directoryComboBoxModel = createDirectoryComboBoxModel(fc);
    directoryComboBox.setModel(directoryComboBoxModel);
    directoryComboBox.addActionListener(directoryComboBoxAction);
    directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(fc));
    directoryComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    directoryComboBox.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    directoryComboBox.setMaximumRowCount(8);

    topPanel.add(directoryComboBox);
    topPanel.add(Box.createRigidArea(hstrut10));

    // Up Button
    JButton upFolderButton = new JButton(getChangeToParentDirectoryAction());
    upFolderButton.setText(null);
    upFolderButton.setIcon(upFolderIcon);
    upFolderButton.setToolTipText(upFolderToolTipText);
    upFolderButton.getAccessibleContext().setAccessibleName(upFolderAccessibleName);
    upFolderButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    upFolderButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    upFolderButton.setMargin(shrinkwrap);
    upFolderButton.setFocusPainted(false);
    topPanel.add(upFolderButton);
    if(OS_VERSION.compareTo("4.9") < 0)
    { // Before Windows Me/2000 (4.90/5.0)
      topPanel.add(Box.createRigidArea(hstrut10));
    }

    JButton b;

    if(OS_VERSION.startsWith("4.1"))
    { // Windows 98 (4.10)
      // Desktop Button
      FileInterface homeDir = fsv.getHomeDirectory(getFileChooser().getFileInterfaceConfiguration());
      String toolTipText = homeFolderToolTipText;
      if(fsv.isRoot(homeDir))
      {
        toolTipText = getFileView(fc).getName(homeDir); // Probably "Desktop".
      }
      b = new JButton(getFileView(fc).getIcon(homeDir));
      b.setToolTipText(toolTipText);
      b.getAccessibleContext().setAccessibleName(toolTipText);
      b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
      b.setMargin(shrinkwrap);
      b.setFocusPainted(false);
      b.addActionListener(getGoHomeAction());
      topPanel.add(b);
      topPanel.add(Box.createRigidArea(hstrut10));
    }

    // New Directory Button
    if(!UIManager.getBoolean("FileChooser.readOnly"))
    {
      b = new JButton(filePane.getNewFolderAction());
      b.setText(null);
      b.setIcon(newFolderIcon);
      b.setToolTipText(newFolderToolTipText);
      b.getAccessibleContext().setAccessibleName(newFolderAccessibleName);
      b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
      b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
      b.setMargin(shrinkwrap);
      b.setFocusPainted(false);
      topPanel.add(b);
    }
    if(OS_VERSION.compareTo("4.9") < 0)
    { // Before Windows Me/2000 (4.90/5.0)
      topPanel.add(Box.createRigidArea(hstrut10));
    }

    // View button group
    ButtonGroup viewButtonGroup = new ButtonGroup();

    // List Button
    listViewButton = new JToggleButton(listViewIcon);
    listViewButton.setToolTipText(listViewButtonToolTipText);
    listViewButton.getAccessibleContext().setAccessibleName(listViewButtonAccessibleName);
    listViewButton.setFocusPainted(false);
    listViewButton.setSelected(true);
    listViewButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    listViewButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    listViewButton.setMargin(shrinkwrap);
    listViewButton.addActionListener(filePane.getViewTypeAction(FilePane.VIEWTYPE_LIST));
    topPanel.add(listViewButton);
    viewButtonGroup.add(listViewButton);

    // Details Button
    detailsViewButton = new JToggleButton(detailsViewIcon);
    detailsViewButton.setToolTipText(detailsViewButtonToolTipText);
    detailsViewButton.getAccessibleContext().setAccessibleName(detailsViewButtonAccessibleName);
    detailsViewButton.setFocusPainted(false);
    detailsViewButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    detailsViewButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    detailsViewButton.setMargin(shrinkwrap);
    detailsViewButton.addActionListener(filePane.getViewTypeAction(FilePane.VIEWTYPE_DETAILS));
    topPanel.add(detailsViewButton);
    viewButtonGroup.add(detailsViewButton);

    topPanel.add(Box.createRigidArea(new Dimension(60, 0)));

    filePane.addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent e)
      {
        if("viewType".equals(e.getPropertyName()))
        {
          int viewType = filePane.getViewType();
          switch(viewType)
          {
            case FilePane.VIEWTYPE_LIST:
              listViewButton.setSelected(true);
              break;

            case FilePane.VIEWTYPE_DETAILS:
              detailsViewButton.setSelected(true);
              break;
          }
        }
      }
    });

    // ************************************** //
    // ******* Add the directory pane ******* //
    // ************************************** //
    centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(getAccessoryPanel(), BorderLayout.AFTER_LINE_ENDS);
    JComponent accessory = fc.getAccessory();
    if(accessory != null)
    {
      getAccessoryPanel().add(accessory);
    }
    filePane.setPreferredSize(LIST_PREF_SIZE);
    centerPanel.add(filePane, BorderLayout.CENTER);
    fc.add(centerPanel, BorderLayout.CENTER);

    // ********************************** //
    // **** Construct the bottom panel ** //
    // ********************************** //
    getBottomPanel().setLayout(new BoxLayout(getBottomPanel(), BoxLayout.LINE_AXIS));

    // Add the bottom panel to file chooser
    centerPanel.add(getBottomPanel(), BorderLayout.SOUTH);

    // labels
    JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
    labelPanel.add(Box.createRigidArea(vstrut4));

    JLabel fnl = new JLabel(fileNameLabelText);
    fnl.setDisplayedMnemonic(fileNameLabelMnemonic);
    fnl.setAlignmentY(0);
    labelPanel.add(fnl);

    labelPanel.add(Box.createRigidArea(new Dimension(1, 12)));

    JLabel ftl = new JLabel(filesOfTypeLabelText);
    ftl.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
    labelPanel.add(ftl);

    getBottomPanel().add(labelPanel);
    getBottomPanel().add(Box.createRigidArea(new Dimension(15, 0)));

    // file entry and filters
    JPanel fileAndFilterPanel = new JPanel();
    fileAndFilterPanel.add(Box.createRigidArea(vstrut8));
    fileAndFilterPanel.setLayout(new BoxLayout(fileAndFilterPanel, BoxLayout.Y_AXIS));

    filenameTextField = new JTextField(35)
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
      }
    };

    fnl.setLabelFor(filenameTextField);
    filenameTextField.addFocusListener(new FocusAdapter()
    {
      public void focusGained(FocusEvent e)
      {
        if(!getFileChooser().isMultiSelectionEnabled())
        {
          filePane.clearSelection();
        }
      }
    });

    if(fc.isMultiSelectionEnabled())
    {
      setFileName(fileNameString(fc.getSelectedFiles()));
    }
    else
    {
      setFileName(fileNameString(fc.getSelectedFile()));
    }

    fileAndFilterPanel.add(filenameTextField);
    fileAndFilterPanel.add(Box.createRigidArea(vstrut8));

    filterComboBoxModel = createFilterComboBoxModel();
    fc.addPropertyChangeListener(filterComboBoxModel);
    filterComboBox = new JComboBox(filterComboBoxModel);
    ftl.setLabelFor(filterComboBox);
    filterComboBox.setRenderer(createFilterComboBoxRenderer());
    fileAndFilterPanel.add(filterComboBox);

    getBottomPanel().add(fileAndFilterPanel);
    getBottomPanel().add(Box.createRigidArea(new Dimension(30, 0)));

    // buttons
    getButtonPanel().setLayout(new BoxLayout(getButtonPanel(), BoxLayout.Y_AXIS));

    approveButton = new JButton(getApproveButtonText(fc))
    {
      public Dimension getMaximumSize()
      {
        return approveButton.getPreferredSize().width > cancelButton.getPreferredSize().width ? approveButton.getPreferredSize() : cancelButton
            .getPreferredSize();
      }
    };
    Insets buttonMargin = approveButton.getMargin();
    buttonMargin = new InsetsUIResource(buttonMargin.top, buttonMargin.left + 5, buttonMargin.bottom, buttonMargin.right + 5);
    approveButton.setMargin(buttonMargin);
    approveButton.setMnemonic(getApproveButtonMnemonic(fc));
    approveButton.addActionListener(getApproveSelectionAction());
    approveButton.setToolTipText(getApproveButtonToolTipText(fc));
    getButtonPanel().add(Box.createRigidArea(vstrut6));
    getButtonPanel().add(approveButton);
    getButtonPanel().add(Box.createRigidArea(vstrut4));

    cancelButton = new JButton(cancelButtonText)
    {
      public Dimension getMaximumSize()
      {
        return approveButton.getPreferredSize().width > cancelButton.getPreferredSize().width ? approveButton.getPreferredSize() : cancelButton
            .getPreferredSize();
      }
    };
    cancelButton.setMargin(buttonMargin);
    cancelButton.setToolTipText(cancelButtonToolTipText);
    cancelButton.addActionListener(getCancelSelectionAction());
    getButtonPanel().add(cancelButton);

    if(fc.getControlButtonsAreShown())
    {
      addControlButtons();
    }
  }

  private void updateUseShellFolder()
  {
    // Decide whether to use the ShellFolder class to populate shortcut
    // panel and combobox.
    JFileInterfaceChooser fc = getFileChooser();
    Boolean prop = (Boolean)fc.getClientProperty("FileChooser.useShellFolder");
    if(prop != null)
    {
      useShellFolder = prop.booleanValue();
    }
    else
    {
      // See if FileSystemView.getRoots() returns the desktop folder,
      // i.e. the normal Windows hierarchy.
      useShellFolder = false;
      FileInterface[] roots = fc.getFileSystemView().getRoots();
      if(roots != null && roots.length == 1)
      {
        FileInterface[] cbFolders = (FileInterface[])ShellFolder.get("fileChooserComboBoxFolders",
            getFileChooser().getFileInterfaceConfiguration());
        if(cbFolders != null && cbFolders.length > 0 && roots[0] == cbFolders[0])
        {
          useShellFolder = true;
        }
      }
    }
    if(OS_VERSION.compareTo("4.9") >= 0)
    { // Windows Me/2000 and later (4.90/5.0)
      if(useShellFolder)
      {
        if(placesBar == null)
        {
          placesBar = new WindowsPlacesBar(fc, XPStyle.getXP() != null);
          fc.add(placesBar, BorderLayout.BEFORE_LINE_BEGINS);
          fc.addPropertyChangeListener(placesBar);
        }
      }
      else
      {
        if(placesBar != null)
        {
          fc.remove(placesBar);
          fc.removePropertyChangeListener(placesBar);
          placesBar = null;
        }
      }
    }
  }
  
  public static XPStyle getXP()
  {
    return XPStyle.getXP();
  }

  protected JPanel getButtonPanel()
  {
    if(buttonPanel == null)
    {
      buttonPanel = new JPanel();
    }
    return buttonPanel;
  }

  protected JPanel getBottomPanel()
  {
    if(bottomPanel == null)
    {
      bottomPanel = new JPanel();
    }
    return bottomPanel;
  }

  protected void installStrings(JFileInterfaceChooser fc)
  {
    super.installStrings(fc);

    Locale l = fc.getLocale();

    lookInLabelMnemonic = UIManager.getInt("FileChooser.lookInLabelMnemonic");
    lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", l);
    saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", l);

    fileNameLabelMnemonic = UIManager.getInt("FileChooser.fileNameLabelMnemonic");
    fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", l);

    filesOfTypeLabelMnemonic = UIManager.getInt("FileChooser.filesOfTypeLabelMnemonic");
    filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", l);

    upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", l);
    upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", l);

    homeFolderToolTipText = UIManager.getString("FileChooser.homeFolderToolTipText", l);
    homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName", l);

    newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", l);
    newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", l);

    listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText", l);
    listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName", l);

    detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText", l);
    detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName", l);
  }

  protected void installListeners(JFileInterfaceChooser fc)
  {
    super.installListeners(fc);
    ActionMap actionMap = getActionMap();
    SwingUtilities.replaceUIActionMap(fc, actionMap);
  }

  protected ActionMap getActionMap()
  {
    return createActionMap();
  }

  protected ActionMap createActionMap()
  {
    ActionMap map = new ActionMapUIResource();
    FilePane.addActionsToMap(map, filePane.getActions());
    return map;
  }

  protected JPanel createList(JFileInterfaceChooser fc)
  {
    return filePane.createList();
  }

  protected JPanel createDetailsView(JFileInterfaceChooser fc)
  {
    return filePane.createDetailsView();
  }

  /**
   * Creates a selection listener for the list of files and directories.
   * 
   * @param fc a <code>JFileAdapterChooser</code>
   * @return a <code>ListSelectionListener</code>
   */
  public ListSelectionListener createListSelectionListener(JFileInterfaceChooser fc)
  {
    return super.createListSelectionListener(fc);
  }

  // Obsolete class, not used in this version.
  protected class WindowsNewFolderAction extends NewFolderAction
  {}

  // Obsolete class, not used in this version.
  protected class SingleClickListener extends MouseAdapter
  {}

  // Obsolete class, not used in this version.
  protected class FileRenderer extends DefaultListCellRenderer
  {}

  public void uninstallUI(JComponent c)
  {
    // Remove listeners
    c.removePropertyChangeListener(filterComboBoxModel);
    c.removePropertyChangeListener(filePane);
    if(placesBar != null)
    {
      c.removePropertyChangeListener(placesBar);
    }
    cancelButton.removeActionListener(getCancelSelectionAction());
    approveButton.removeActionListener(getApproveSelectionAction());
    filenameTextField.removeActionListener(getApproveSelectionAction());

    super.uninstallUI(c);
  }

  /**
   * Returns the preferred size of the specified <code>JFileAdapterChooser</code>. The preferred size is at
   * least as large, in both height and width, as the preferred size recommended by the file
   * chooser's layout manager.
   * 
   * @param c a <code>JFileAdapterChooser</code>
   * @return a <code>Dimension</code> specifying the preferred width and height of the file chooser
   */
  public Dimension getPreferredSize(JComponent c)
  {
    int prefWidth = PREF_SIZE.width;
    Dimension d = c.getLayout().preferredLayoutSize(c);
    if(d != null)
    {
      return new Dimension(d.width < prefWidth ? prefWidth : d.width, d.height < PREF_SIZE.height ? PREF_SIZE.height : d.height);
    }
    else
    {
      return new Dimension(prefWidth, PREF_SIZE.height);
    }
  }

  /**
   * Returns the minimum size of the <code>JFileAdapterChooser</code>.
   * 
   * @param c a <code>JFileAdapterChooser</code>
   * @return a <code>Dimension</code> specifying the minimum width and height of the file chooser
   */
  public Dimension getMinimumSize(JComponent c)
  {
    return MIN_SIZE;
  }

  /**
   * Returns the maximum size of the <code>JFileAdapterChooser</code>.
   * 
   * @param c a <code>JFileAdapterChooser</code>
   * @return a <code>Dimension</code> specifying the maximum width and height of the file chooser
   */
  public Dimension getMaximumSize(JComponent c)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  private String fileNameString(FileInterface file)
  {
    if(file == null)
    {
      return null;
    }
    else
    {
      JFileInterfaceChooser fc = getFileChooser();
      if(fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled())
      {
        return file.getPath();
      }
      else
      {
        return file.getName();
      }
    }
  }

  private String fileNameString(FileInterface[] files)
  {
    StringBuffer buf = new StringBuffer();
    for(int i = 0; files != null && i < files.length; i++)
    {
      if(i > 0)
      {
        buf.append(" ");
      }
      if(files.length > 1)
      {
        buf.append("\"");
      }
      buf.append(fileNameString(files[i]));
      if(files.length > 1)
      {
        buf.append("\"");
      }
    }
    return buf.toString();
  }

  /* The following methods are used by the PropertyChange Listener */

  private void doSelectedFileChanged(PropertyChangeEvent e)
  {
    FileInterface f = (FileInterface)e.getNewValue();
    JFileInterfaceChooser fc = getFileChooser();
    if(f != null && ((fc.isFileSelectionEnabled() && !f.isDirectory()) || (f.isDirectory() && fc.isDirectorySelectionEnabled())))
    {

      setFileName(fileNameString(f));
    }
  }

  private void doSelectedFilesChanged(PropertyChangeEvent e)
  {
    FileInterface[] files = (FileInterface[])e.getNewValue();
    JFileInterfaceChooser fc = getFileChooser();
    if(files != null && files.length > 0 && (files.length > 1 || fc.isDirectorySelectionEnabled() || !files[0].isDirectory()))
    {
      setFileName(fileNameString(files));
    }
  }

  private void doDirectoryChanged(PropertyChangeEvent e)
  {
    JFileInterfaceChooser fc = getFileChooser();
    GenericFileInterfaceSystemView fsv = fc.getFileSystemView();

    clearIconCache();
    FileInterface currentDirectory = fc.getCurrentDirectory();
    if(currentDirectory != null)
    {
      directoryComboBoxModel.addItem(currentDirectory);

      if(fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled())
      {
        if(fsv.isFileSystem(currentDirectory))
        {
          setFileName(currentDirectory.getPath());
        }
        else
        {
          setFileName(null);
        }
      }
    }
  }

  private void doFilterChanged(PropertyChangeEvent e)
  {
    clearIconCache();
  }

  private void doFileSelectionModeChanged(PropertyChangeEvent e)
  {
    clearIconCache();

    JFileInterfaceChooser fc = getFileChooser();
    FileInterface currentDirectory = fc.getCurrentDirectory();
    if(currentDirectory != null && fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled() && fc.getFileSystemView().isFileSystem(currentDirectory))
    {

      setFileName(currentDirectory.getPath());
    }
    else
    {
      setFileName(null);
    }
  }

  private void doAccessoryChanged(PropertyChangeEvent e)
  {
    if(getAccessoryPanel() != null)
    {
      if(e.getOldValue() != null)
      {
        getAccessoryPanel().remove((JComponent)e.getOldValue());
      }
      JComponent accessory = (JComponent)e.getNewValue();
      if(accessory != null)
      {
        getAccessoryPanel().add(accessory, BorderLayout.CENTER);
      }
    }
  }

  private void doApproveButtonTextChanged(PropertyChangeEvent e)
  {
    JFileInterfaceChooser chooser = getFileChooser();
    approveButton.setText(getApproveButtonText(chooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
    approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
  }

  private void doDialogTypeChanged(PropertyChangeEvent e)
  {
    JFileInterfaceChooser chooser = getFileChooser();
    approveButton.setText(getApproveButtonText(chooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
    approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
    if(chooser.getDialogType() == JFileInterfaceChooser.SAVE_DIALOG)
    {
      lookInLabel.setText(saveInLabelText);
    }
    else
    {
      lookInLabel.setText(lookInLabelText);
    }
  }

  private void doApproveButtonMnemonicChanged(PropertyChangeEvent e)
  {
    approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser()));
  }

  private void doControlButtonsChanged(PropertyChangeEvent e)
  {
    if(getFileChooser().getControlButtonsAreShown())
    {
      addControlButtons();
    }
    else
    {
      removeControlButtons();
    }
  }

  /*
   * Listen for filechooser property changes, such as the selected file changing, or the type of the
   * dialog changing.
   */
  public PropertyChangeListener createPropertyChangeListener(JFileInterfaceChooser fc)
  {
    return new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent e)
      {
        String s = e.getPropertyName();
        if(s.equals(JFileInterfaceChooser.SELECTED_FILE_CHANGED_PROPERTY))
        {
          doSelectedFileChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.SELECTED_FILES_CHANGED_PROPERTY))
        {
          doSelectedFilesChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.DIRECTORY_CHANGED_PROPERTY))
        {
          doDirectoryChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.FILE_FILTER_CHANGED_PROPERTY))
        {
          doFilterChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY))
        {
          doFileSelectionModeChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.ACCESSORY_CHANGED_PROPERTY))
        {
          doAccessoryChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY) || s.equals(JFileInterfaceChooser.APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY))
        {
          doApproveButtonTextChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.DIALOG_TYPE_CHANGED_PROPERTY))
        {
          doDialogTypeChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY))
        {
          doApproveButtonMnemonicChanged(e);
        }
        else if(s.equals(JFileInterfaceChooser.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY))
        {
          doControlButtonsChanged(e);
        }
        else if(s == "FileChooser.useShellFolder")
        {
          updateUseShellFolder();
          doDirectoryChanged(e);
        }
        else if(s.equals("componentOrientation"))
        {
          ComponentOrientation o = (ComponentOrientation)e.getNewValue();
          JFileInterfaceChooser cc = (JFileInterfaceChooser)e.getSource();
          if(o != (ComponentOrientation)e.getOldValue())
          {
            cc.applyComponentOrientation(o);
          }
        }
        else if(s.equals("ancestor"))
        {
          if(e.getOldValue() == null && e.getNewValue() != null)
          {
            // Ancestor was added, set initial focus
            filenameTextField.selectAll();
            filenameTextField.requestFocus();
          }
        }
      }
    };
  }

  protected void removeControlButtons()
  {
    getBottomPanel().remove(getButtonPanel());
  }

  protected void addControlButtons()
  {
    getBottomPanel().add(getButtonPanel());
  }

  public void ensureFileIsVisible(JFileInterfaceChooser fc, FileInterface f)
  {
    filePane.ensureFileIsVisible(fc, f);
  }

  public void rescanCurrentDirectory(JFileInterfaceChooser fc)
  {
    filePane.rescanCurrentDirectory();
  }

  public String getFileName()
  {
    if(filenameTextField != null)
    {
      return filenameTextField.getText();
    }
    else
    {
      return null;
    }
  }

  public void setFileName(String filename)
  {
    if(filenameTextField != null)
    {
      filenameTextField.setText(filename);
    }
  }

  /**
   * Property to remember whether a directory is currently selected in the UI. This is normally
   * called by the UI on a selection event.
   * 
   * @param directorySelected if a directory is currently selected.
   * @since 1.4
   */
  protected void setDirectorySelected(boolean directorySelected)
  {
    super.setDirectorySelected(directorySelected);
    JFileInterfaceChooser chooser = getFileChooser();
    if(directorySelected)
    {
      approveButton.setText(directoryOpenButtonText);
      approveButton.setToolTipText(directoryOpenButtonToolTipText);
      approveButton.setMnemonic(directoryOpenButtonMnemonic);
    }
    else
    {
      approveButton.setText(getApproveButtonText(chooser));
      approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
      approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
    }
  }

  public String getDirectoryName()
  {
    // PENDING(jeff) - get the name from the directory combobox
    return null;
  }

  public void setDirectoryName(String dirname)
  {
    // PENDING(jeff) - set the name in the directory combobox
  }

  protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileInterfaceChooser fc)
  {
    return new DirectoryComboBoxRenderer();
  }

  //
  // Renderer for DirectoryComboBox
  //
  class DirectoryComboBoxRenderer extends DefaultListCellRenderer
  {
    IndentIcon ii = new IndentIcon();

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {

      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      if(value == null)
      {
        setText("");
        return this;
      }
      FileInterface directory = (FileInterface)value;
      setText(getFileChooser().getName(directory));
      Icon icon = getFileChooser().getIcon(directory);
      ii.icon = icon;
      ii.depth = directoryComboBoxModel.getDepth(index);
      setIcon(ii);

      return this;
    }
  }

  final static int space = 10;

  class IndentIcon implements Icon
  {

    Icon icon = null;
    int depth = 0;

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
      if(c.getComponentOrientation().isLeftToRight())
      {
        icon.paintIcon(c, g, x + depth * space, y);
      }
      else
      {
        icon.paintIcon(c, g, x, y);
      }
    }

    public int getIconWidth()
    {
      return icon.getIconWidth() + depth * space;
    }

    public int getIconHeight()
    {
      return icon.getIconHeight();
    }

  }

  //
  // DataModel for DirectoryComboxbox
  //
  protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileInterfaceChooser fc)
  {
    return new DirectoryComboBoxModel();
  }

  /**
   * Data model for a type-face selection combo-box.
   */
  protected class DirectoryComboBoxModel extends AbstractListModel implements ComboBoxModel
  {
    Vector directories = new Vector();
    int[] depths = null;
    FileInterface selectedDirectory = null;
    JFileInterfaceChooser chooser = getFileChooser();
    GenericFileInterfaceSystemView fsv = chooser.getFileSystemView();

    public DirectoryComboBoxModel()
    {
      // Add the current directory to the model, and make it the
      // selectedDirectory
      FileInterface dir = getFileChooser().getCurrentDirectory();
      if(dir != null)
      {
        addItem(dir);
      }
    }

    /**
     * Adds the directory to the model and sets it to be selected, additionally clears out the
     * previous selected directory and the paths leading up to it, if any.
     */
    private void addItem(FileInterface directory)
    {

      if(directory == null)
      {
        return;
      }

      directories.clear();

      FileInterface[] baseFolders;
      if(useShellFolder)
      {
        baseFolders = (FileInterface[])ShellFolder.get("fileChooserComboBoxFolders",
            getFileChooser().getFileInterfaceConfiguration());
      }
      else
      {
        baseFolders = fsv.getRoots();
      }
      directories.addAll(Arrays.asList(baseFolders));

      // Get the canonical (full) path. This has the side
      // benefit of removing extraneous chars from the path,
      // for example /foo/bar/ becomes /foo/bar
      FileInterface canonical = null;
      try
      {
        canonical = directory.getCanonicalFile();
      }
      catch(IOException e)
      {
        // Maybe drive is not ready. Can't abort here.
        canonical = directory;
      }

      // create FileInterface instances of each directory leading up to the top
      try
      {
        FileInterface sf = useShellFolder ? ShellFolder.getShellFolder(canonical) : canonical;
        FileInterface f = sf;
        Vector path = new Vector(10);
        do
        {
          path.addElement(f);
        } while((f = f.getParentFileInterface()) != null);

        int pathCount = path.size();
        // Insert chain at appropriate place in vector
        for(int i = 0; i < pathCount; i++)
        {
          f = (FileInterface)path.get(i);
          if(directories.contains(f))
          {
            int topIndex = directories.indexOf(f);
            for(int j = i - 1; j >= 0; j--)
            {
              directories.insertElementAt(path.get(j), topIndex + i - j);
            }
            break;
          }
        }
        calculateDepths();
        setSelectedItem(sf);
      }
      catch(FileNotFoundException ex)
      {
        calculateDepths();
      }
    }

    private void calculateDepths()
    {
      depths = new int[directories.size()];
      for(int i = 0; i < depths.length; i++)
      {
        FileInterface dir = (FileInterface)directories.get(i);
        FileInterface parent = dir.getParentFileInterface();
        depths[i] = 0;
        if(parent != null)
        {
          for(int j = i - 1; j >= 0; j--)
          {
            if(parent.equals((FileInterface)directories.get(j)))
            {
              depths[i] = depths[j] + 1;
              break;
            }
          }
        }
      }
    }

    public int getDepth(int i)
    {
      return (depths != null && i >= 0 && i < depths.length) ? depths[i] : 0;
    }

    public void setSelectedItem(Object selectedDirectory)
    {
      this.selectedDirectory = (FileInterface)selectedDirectory;
      fireContentsChanged(this, -1, -1);
    }

    public Object getSelectedItem()
    {
      return selectedDirectory;
    }

    public int getSize()
    {
      return directories.size();
    }

    public Object getElementAt(int index)
    {
      return directories.elementAt(index);
    }
  }

  //
  // Renderer for Types ComboBox
  //
  protected FilterComboBoxRenderer createFilterComboBoxRenderer()
  {
    return new FilterComboBoxRenderer();
  }

  /**
   * Render different type sizes and styles.
   */
  public class FilterComboBoxRenderer extends DefaultListCellRenderer
  {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {

      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      if(value != null && value instanceof FileInterfaceFilter)
      {
        setText(((FileInterfaceFilter)value).getDescription());
      }

      return this;
    }
  }

  //
  // DataModel for Types Comboxbox
  //
  protected FilterComboBoxModel createFilterComboBoxModel()
  {
    return new FilterComboBoxModel();
  }

  /**
   * Data model for a type-face selection combo-box.
   */
  protected class FilterComboBoxModel extends AbstractListModel implements ComboBoxModel, PropertyChangeListener
  {
    protected FileInterfaceFilter[] filters;

    protected FilterComboBoxModel()
    {
      super();
      filters = getFileChooser().getChoosableFileFilters();
    }

    public void propertyChange(PropertyChangeEvent e)
    {
      String prop = e.getPropertyName();
      if(prop == JFileInterfaceChooser.CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY)
      {
        filters = (FileInterfaceFilter[])e.getNewValue();
        fireContentsChanged(this, -1, -1);
      }
      else if(prop == JFileInterfaceChooser.FILE_FILTER_CHANGED_PROPERTY)
      {
        fireContentsChanged(this, -1, -1);
      }
    }

    public void setSelectedItem(Object filter)
    {
      if(filter != null)
      {
        getFileChooser().setFileFilter((FileInterfaceFilter)filter);
        setFileName(null);
        fireContentsChanged(this, -1, -1);
      }
    }

    public Object getSelectedItem()
    {
      // Ensure that the current filter is in the list.
      // NOTE: we shouldnt' have to do this, since JFileAdapterChooser adds
      // the filter to the choosable filters list when the filter
      // is set. Lets be paranoid just in case someone overrides
      // setFileFilter in JFileAdapterChooser.
      FileInterfaceFilter currentFilter = getFileChooser().getFileFilter();
      boolean found = false;
      if(currentFilter != null)
      {
        for(int i = 0; i < filters.length; i++)
        {
          if(filters[i] == currentFilter)
          {
            found = true;
          }
        }
        if(found == false)
        {
          getFileChooser().addChoosableFileFilter(currentFilter);
        }
      }
      return getFileChooser().getFileFilter();
    }

    public int getSize()
    {
      if(filters != null)
      {
        return filters.length;
      }
      else
      {
        return 0;
      }
    }

    public Object getElementAt(int index)
    {
      if(index > getSize() - 1)
      {
        // This shouldn't happen. Try to recover gracefully.
        return getFileChooser().getFileFilter();
      }
      if(filters != null)
      {
        return filters[index];
      }
      else
      {
        return null;
      }
    }
  }

  public void valueChanged(ListSelectionEvent e)
  {
    JFileInterfaceChooser fc = getFileChooser();
    FileInterface f = fc.getSelectedFile();
    if(!e.getValueIsAdjusting() && f != null && !getFileChooser().isTraversable(f))
    {
      setFileName(fileNameString(f));
    }
  }

  /**
   * Acts when DirectoryComboBox has changed the selected item.
   */
  protected class DirectoryComboBoxAction implements ActionListener
  {

    public void actionPerformed(ActionEvent e)
    {
      FileInterface f = (FileInterface)directoryComboBox.getSelectedItem();
      getFileChooser().setCurrentDirectory(f);
    }
  }

  protected JButton getApproveButton(JFileInterfaceChooser fc)
  {
    return approveButton;
  }

  public FileView getFileView(JFileInterfaceChooser fc)
  {
    return fileView;
  }

  // ***********************
  // * FileView operations *
  // ***********************
  protected class WindowsFileView extends BasicFileView
  {
    /* FileView type descriptions */

    public Icon getIcon(FileInterface f)
    {
      Icon icon = getCachedIcon(f);
      if(icon != null)
      {
        return icon;
      }
      if(f != null)
      {
        icon = getFileChooser().getFileSystemView().getSystemIcon(f);
      }
      if(icon == null)
      {
        icon = super.getIcon(f);
      }
      cacheIcon(f, icon);
      return icon;
    }
  }
}

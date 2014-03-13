/* *****************************************************************
 * Project: common
 * File:    MetalFileChooserUI.java
 * 
 * Creation:     05.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.swing.plaf.metal;

import haui.awt.shell.ShellFolder;
import haui.components.GenericFileInterfaceSystemView;
import haui.components.JFileInterfaceChooser;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.swing.FilePane;
import haui.swing.plaf.basic.BasicDirectoryModel;
import haui.swing.plaf.basic.BasicFileChooserUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
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

import javax.swing.AbstractAction;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;

/**
 * MetalFileChooserUI
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class MetalFileChooserUI extends BasicFileChooserUI
{

  // Much of the Metal UI for JFilechooser is just a copy of
  // the windows implementation, but using Metal themed buttons, lists,
  // icons, etc. We are planning a complete rewrite, and hence we've
  // made most things in this class private.

  private JLabel lookInLabel;
  private JComboBox directoryComboBox;
  private DirectoryComboBoxModel directoryComboBoxModel;
  private Action directoryComboBoxAction = new DirectoryComboBoxAction();

  private FilterComboBoxModel filterComboBoxModel;

  private JTextField fileNameTextField;

  private FilePane filePane;
  private JToggleButton listViewButton;
  private JToggleButton detailsViewButton;

  private boolean useShellFolder;

  private JButton approveButton;
  private JButton cancelButton;

  private JPanel buttonPanel;
  private JPanel bottomPanel;

  private JComboBox filterComboBox;

  private static final Dimension hstrut5 = new Dimension(5, 1);
  private static final Dimension hstrut11 = new Dimension(11, 1);

  private static final Dimension vstrut5 = new Dimension(1, 5);

  private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);

  // Preferred and Minimum sizes for the dialog box
  private static int PREF_WIDTH = 500;
  private static int PREF_HEIGHT = 326;
  private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);

  private static int MIN_WIDTH = 500;
  private static int MIN_HEIGHT = 326;
  private static Dimension MIN_SIZE = new Dimension(MIN_WIDTH, MIN_HEIGHT);

  private static int LIST_PREF_WIDTH = 405;
  private static int LIST_PREF_HEIGHT = 135;
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

  //
  // ComponentUI Interface Implementation methods
  //
  public static ComponentUI createUI(JComponent c)
  {
    return new MetalFileChooserUI((JFileInterfaceChooser)c);
  }

  public MetalFileChooserUI(JFileInterfaceChooser filechooser)
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
    bottomPanel = null;
    buttonPanel = null;
  }

  private class MetalFileChooserUIAccessor implements FilePane.FileChooserUIAccessor
  {
    public JFileInterfaceChooser getFileChooser()
    {
      return MetalFileChooserUI.this.getFileChooser();
    }

    public haui.swing.plaf.basic.BasicDirectoryModel getModel()
    {
      return MetalFileChooserUI.this.getModel();
    }

    public JPanel createList()
    {
      return MetalFileChooserUI.this.createList(getFileChooser());
    }

    public JPanel createDetailsView()
    {
      return MetalFileChooserUI.this.createDetailsView(getFileChooser());
    }

    public boolean isDirectorySelected()
    {
      return MetalFileChooserUI.this.isDirectorySelected();
    }

    public FileInterface getDirectory()
    {
      return MetalFileChooserUI.this.getDirectory();
    }

    public Action getChangeToParentDirectoryAction()
    {
      return MetalFileChooserUI.this.getChangeToParentDirectoryAction();
    }

    public Action getApproveSelectionAction()
    {
      return MetalFileChooserUI.this.getApproveSelectionAction();
    }

    public Action getNewFolderAction()
    {
      return MetalFileChooserUI.this.getNewFolderAction();
    }

    public MouseListener createDoubleClickListener(JList list)
    {
      return MetalFileChooserUI.this.createDoubleClickListener(getFileChooser(), list);
    }

    public ListSelectionListener createListSelectionListener()
    {
      return MetalFileChooserUI.this.createListSelectionListener(getFileChooser());
    }
  }

  public void installComponents(JFileInterfaceChooser fc)
  {
    GenericFileInterfaceSystemView fsv = fc.getFileSystemView();

    fc.setBorder(new EmptyBorder(12, 12, 11, 11));
    fc.setLayout(new BorderLayout(0, 11));

    filePane = new FilePane(new MetalFileChooserUIAccessor());
    fc.addPropertyChangeListener(filePane);

    updateUseShellFolder();

    // ********************************* //
    // **** Construct the top panel **** //
    // ********************************* //

    // Directory manipulation buttons
    JPanel topPanel = new JPanel(new BorderLayout(11, 0));
    JPanel topButtonPanel = new JPanel();
    topButtonPanel.setLayout(new BoxLayout(topButtonPanel, BoxLayout.LINE_AXIS));
    topPanel.add(topButtonPanel, BorderLayout.AFTER_LINE_ENDS);

    // Add the top panel to the fileChooser
    fc.add(topPanel, BorderLayout.NORTH);

    // ComboBox Label
    lookInLabel = new JLabel(lookInLabelText);
    lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
    topPanel.add(lookInLabel, BorderLayout.BEFORE_LINE_BEGINS);

    // CurrentDir ComboBox
    directoryComboBox = new JComboBox()
    {
      public Dimension getPreferredSize()
      {
        Dimension d = super.getPreferredSize();
        // Must be small enough to not affect total width.
        d.width = 150;
        return d;
      }
    };
    directoryComboBox.getAccessibleContext().setAccessibleDescription(lookInLabelText);
    directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
    lookInLabel.setLabelFor(directoryComboBox);
    directoryComboBoxModel = createDirectoryComboBoxModel(fc);
    directoryComboBox.setModel(directoryComboBoxModel);
    directoryComboBox.addActionListener(directoryComboBoxAction);
    directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(fc));
    directoryComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    directoryComboBox.setAlignmentY(JComponent.TOP_ALIGNMENT);
    directoryComboBox.setMaximumRowCount(8);

    topPanel.add(directoryComboBox, BorderLayout.CENTER);

    // Up Button
    JButton upFolderButton = new JButton(getChangeToParentDirectoryAction());
    upFolderButton.setText(null);
    upFolderButton.setIcon(upFolderIcon);
    upFolderButton.setToolTipText(upFolderToolTipText);
    upFolderButton.getAccessibleContext().setAccessibleName(upFolderAccessibleName);
    upFolderButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    upFolderButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    upFolderButton.setMargin(shrinkwrap);

    topButtonPanel.add(upFolderButton);
    topButtonPanel.add(Box.createRigidArea(hstrut5));

    // Home Button
    FileInterface homeDir = fsv.getHomeDirectory(fc.getFileInterfaceConfiguration());
    String toolTipText = homeFolderToolTipText;
    if(fsv.isRoot(homeDir))
    {
      toolTipText = getFileView(fc).getName(homeDir); // Probably "Desktop".
    }

    JButton b = new JButton(homeFolderIcon);
    b.setToolTipText(toolTipText);
    b.getAccessibleContext().setAccessibleName(homeFolderAccessibleName);
    b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    b.setMargin(shrinkwrap);

    b.addActionListener(getGoHomeAction());
    topButtonPanel.add(b);
    topButtonPanel.add(Box.createRigidArea(hstrut5));

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
    }
    topButtonPanel.add(b);
    topButtonPanel.add(Box.createRigidArea(hstrut5));

    // View button group
    ButtonGroup viewButtonGroup = new ButtonGroup();

    // List Button
    listViewButton = new JToggleButton(listViewIcon);
    listViewButton.setToolTipText(listViewButtonToolTipText);
    listViewButton.getAccessibleContext().setAccessibleName(listViewButtonAccessibleName);
    listViewButton.setSelected(true);
    listViewButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    listViewButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    listViewButton.setMargin(shrinkwrap);
    listViewButton.addActionListener(filePane.getViewTypeAction(FilePane.VIEWTYPE_LIST));
    topButtonPanel.add(listViewButton);
    viewButtonGroup.add(listViewButton);

    // Details Button
    detailsViewButton = new JToggleButton(detailsViewIcon);
    detailsViewButton.setToolTipText(detailsViewButtonToolTipText);
    detailsViewButton.getAccessibleContext().setAccessibleName(detailsViewButtonAccessibleName);
    detailsViewButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    detailsViewButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    detailsViewButton.setMargin(shrinkwrap);
    detailsViewButton.addActionListener(filePane.getViewTypeAction(FilePane.VIEWTYPE_DETAILS));
    topButtonPanel.add(detailsViewButton);
    viewButtonGroup.add(detailsViewButton);

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
    fc.add(getAccessoryPanel(), BorderLayout.AFTER_LINE_ENDS);
    JComponent accessory = fc.getAccessory();
    if(accessory != null)
    {
      getAccessoryPanel().add(accessory);
    }
    filePane.setPreferredSize(LIST_PREF_SIZE);
    fc.add(filePane, BorderLayout.CENTER);

    // ********************************** //
    // **** Construct the bottom panel ** //
    // ********************************** //
    JPanel bottomPanel = getBottomPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
    fc.add(bottomPanel, BorderLayout.SOUTH);

    // FileName label and textfield
    JPanel fileNamePanel = new JPanel();
    fileNamePanel.setLayout(new BoxLayout(fileNamePanel, BoxLayout.LINE_AXIS));
    bottomPanel.add(fileNamePanel);
    bottomPanel.add(Box.createRigidArea(vstrut5));

    AlignedLabel fileNameLabel = new AlignedLabel(fileNameLabelText);
    fileNameLabel.setDisplayedMnemonic(fileNameLabelMnemonic);
    fileNamePanel.add(fileNameLabel);

    fileNameTextField = new JTextField(35)
    {
      public Dimension getMaximumSize()
      {
        return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
      }
    };
    fileNamePanel.add(fileNameTextField);
    fileNameLabel.setLabelFor(fileNameTextField);
    fileNameTextField.addFocusListener(new FocusAdapter()
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

    // Filetype label and combobox
    JPanel filesOfTypePanel = new JPanel();
    filesOfTypePanel.setLayout(new BoxLayout(filesOfTypePanel, BoxLayout.LINE_AXIS));
    bottomPanel.add(filesOfTypePanel);

    AlignedLabel filesOfTypeLabel = new AlignedLabel(filesOfTypeLabelText);
    filesOfTypeLabel.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
    filesOfTypePanel.add(filesOfTypeLabel);

    filterComboBoxModel = createFilterComboBoxModel();
    fc.addPropertyChangeListener(filterComboBoxModel);
    filterComboBox = new JComboBox(filterComboBoxModel);
    filterComboBox.getAccessibleContext().setAccessibleDescription(filesOfTypeLabelText);
    filesOfTypeLabel.setLabelFor(filterComboBox);
    filterComboBox.setRenderer(createFilterComboBoxRenderer());
    filesOfTypePanel.add(filterComboBox);

    // buttons
    getButtonPanel().setLayout(new ButtonAreaLayout());

    approveButton = new JButton(getApproveButtonText(fc));
    // Note: Metal does not use mnemonics for approve and cancel
    approveButton.addActionListener(getApproveSelectionAction());
    approveButton.setToolTipText(getApproveButtonToolTipText(fc));
    getButtonPanel().add(approveButton);

    cancelButton = new JButton(cancelButtonText);
    cancelButton.setToolTipText(cancelButtonToolTipText);
    cancelButton.addActionListener(getCancelSelectionAction());
    getButtonPanel().add(cancelButton);

    if(fc.getControlButtonsAreShown())
    {
      addControlButtons();
    }

    groupLabels(new AlignedLabel[] { fileNameLabel, filesOfTypeLabel });
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
            fc.getFileInterfaceConfiguration());
        if(cbFolders != null && cbFolders.length > 0 && roots[0] == cbFolders[0])
        {
          useShellFolder = true;
        }
      }
    }
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
  protected class SingleClickListener extends MouseAdapter
  {
    public SingleClickListener(JList list)
    {
    }
  }

  // Obsolete class, not used in this version.
  protected class FileRenderer extends DefaultListCellRenderer
  {}

  public void uninstallUI(JComponent c)
  {
    // Remove listeners
    c.removePropertyChangeListener(filterComboBoxModel);
    c.removePropertyChangeListener(filePane);
    cancelButton.removeActionListener(getCancelSelectionAction());
    approveButton.removeActionListener(getApproveSelectionAction());
    fileNameTextField.removeActionListener(getApproveSelectionAction());

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
  }

  private void doDialogTypeChanged(PropertyChangeEvent e)
  {
    JFileInterfaceChooser chooser = getFileChooser();
    approveButton.setText(getApproveButtonText(chooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
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
    // Note: Metal does not use mnemonics for approve and cancel
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
        else if(s.equals("componentOrientation"))
        {
          ComponentOrientation o = (ComponentOrientation)e.getNewValue();
          JFileInterfaceChooser cc = (JFileInterfaceChooser)e.getSource();
          if(o != (ComponentOrientation)e.getOldValue())
          {
            cc.applyComponentOrientation(o);
          }
        }
        else if(s == "FileChooser.useShellFolder")
        {
          updateUseShellFolder();
          doDirectoryChanged(e);
        }
        else if(s.equals("ancestor"))
        {
          if(e.getOldValue() == null && e.getNewValue() != null)
          {
            // Ancestor was added, set initial focus
            fileNameTextField.selectAll();
            fileNameTextField.requestFocus();
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
    if(fileNameTextField != null)
    {
      return fileNameTextField.getText();
    }
    else
    {
      return null;
    }
  }

  public void setFileName(String filename)
  {
    if(fileNameTextField != null)
    {
      fileNameTextField.setText(filename);
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
      if(approveButton != null)
      {
        approveButton.setText(directoryOpenButtonText);
        approveButton.setToolTipText(directoryOpenButtonToolTipText);
      }
    }
    else
    {
      if(approveButton != null)
      {
        approveButton.setText(getApproveButtonText(chooser));
        approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
      }
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
  protected class DirectoryComboBoxAction extends AbstractAction
  {
    protected DirectoryComboBoxAction()
    {
      super("DirectoryComboBoxAction");
    }

    public void actionPerformed(ActionEvent e)
    {
      directoryComboBox.hidePopup();
      FileInterface f = (FileInterface)directoryComboBox.getSelectedItem();
      if(!getFileChooser().getCurrentDirectory().equals(f))
      {
        getFileChooser().setCurrentDirectory(f);
      }
    }
  }

  protected JButton getApproveButton(JFileInterfaceChooser fc)
  {
    return approveButton;
  }

  /**
   * <code>ButtonAreaLayout</code> behaves in a similar manner to <code>FlowLayout</code>. It lays
   * out all components from left to right, flushed right. The widths of all components will be set
   * to the largest preferred size width.
   */
  private static class ButtonAreaLayout implements LayoutManager
  {
    private int hGap = 5;
    private int topMargin = 17;

    public void addLayoutComponent(String string, Component comp)
    {
    }

    public void layoutContainer(Container container)
    {
      Component[] children = container.getComponents();

      if(children != null && children.length > 0)
      {
        int numChildren = children.length;
        Dimension[] sizes = new Dimension[numChildren];
        Insets insets = container.getInsets();
        int yLocation = insets.top + topMargin;
        int maxWidth = 0;

        for(int counter = 0; counter < numChildren; counter++)
        {
          sizes[counter] = children[counter].getPreferredSize();
          maxWidth = Math.max(maxWidth, sizes[counter].width);
        }
        int xLocation, xOffset;
        if(container.getComponentOrientation().isLeftToRight())
        {
          xLocation = container.getSize().width - insets.left - maxWidth;
          xOffset = hGap + maxWidth;
        }
        else
        {
          xLocation = insets.left;
          xOffset = -(hGap + maxWidth);
        }
        for(int counter = numChildren - 1; counter >= 0; counter--)
        {
          children[counter].setBounds(xLocation, yLocation, maxWidth, sizes[counter].height);
          xLocation -= xOffset;
        }
      }
    }

    public Dimension minimumLayoutSize(Container c)
    {
      if(c != null)
      {
        Component[] children = c.getComponents();

        if(children != null && children.length > 0)
        {
          int numChildren = children.length;
          int height = 0;
          Insets cInsets = c.getInsets();
          int extraHeight = topMargin + cInsets.top + cInsets.bottom;
          int extraWidth = cInsets.left + cInsets.right;
          int maxWidth = 0;

          for(int counter = 0; counter < numChildren; counter++)
          {
            Dimension aSize = children[counter].getPreferredSize();
            height = Math.max(height, aSize.height);
            maxWidth = Math.max(maxWidth, aSize.width);
          }
          return new Dimension(extraWidth + numChildren * maxWidth + (numChildren - 1) * hGap, extraHeight + height);
        }
      }
      return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container c)
    {
      return minimumLayoutSize(c);
    }

    public void removeLayoutComponent(Component c)
    {
    }
  }

  private static void groupLabels(AlignedLabel[] group)
  {
    for(int i = 0; i < group.length; i++)
    {
      group[i].group = group;
    }
  }

  private class AlignedLabel extends JLabel
  {
    private AlignedLabel[] group;
    private int maxWidth = 0;

    AlignedLabel(String text)
    {
      super(text);
      setAlignmentX(JComponent.LEFT_ALIGNMENT);
    }

    public Dimension getPreferredSize()
    {
      Dimension d = super.getPreferredSize();
      // Align the width with all other labels in group.
      return new Dimension(getMaxWidth() + 11, d.height);
    }

    private int getMaxWidth()
    {
      if(maxWidth == 0 && group != null)
      {
        int max = 0;
        for(int i = 0; i < group.length; i++)
        {
          max = Math.max(group[i].getSuperPreferredWidth(), max);
        }
        for(int i = 0; i < group.length; i++)
        {
          group[i].maxWidth = max;
        }
      }
      return maxWidth;
    }

    private int getSuperPreferredWidth()
    {
      return super.getPreferredSize().width;
    }
  }
}

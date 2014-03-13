package haui.swing.plaf.basic;

import haui.awt.shell.ShellFolder;
import haui.components.GenericFileInterfaceSystemView;
import haui.components.JFileInterfaceChooser;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.swing.filechooser.FileView;
import haui.swing.plaf.FileInterfaceChooserUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.FileTransferable;

import sun.swing.DefaultLookup;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;


public class BasicFileChooserUI extends FileInterfaceChooserUI
{

  /* FileView icons */
  protected Icon directoryIcon = null;
  protected Icon fileIcon = null;
  protected Icon computerIcon = null;
  protected Icon hardDriveIcon = null;
  protected Icon floppyDriveIcon = null;

  protected Icon newFolderIcon = null;
  protected Icon upFolderIcon = null;
  protected Icon homeFolderIcon = null;
  protected Icon listViewIcon = null;
  protected Icon detailsViewIcon = null;

  protected int saveButtonMnemonic = 0;
  protected int openButtonMnemonic = 0;
  protected int cancelButtonMnemonic = 0;
  protected int updateButtonMnemonic = 0;
  protected int helpButtonMnemonic = 0;

  /**
   * The mnemonic keycode used for the approve button when a directory is selected and the current
   * selection mode is not DIRECTORIES_ONLY.
   * 
   * @since 1.4
   */
  protected int directoryOpenButtonMnemonic = 0;

  protected String saveButtonText = null;
  protected String openButtonText = null;
  protected String cancelButtonText = null;
  protected String updateButtonText = null;
  protected String helpButtonText = null;

  /**
   * The label text displayed on the approve button when a directory is selected and the current
   * selection mode is not DIRECTORIES_ONLY.
   * 
   * @since 1.4
   */
  protected String directoryOpenButtonText = null;

  private String openDialogTitleText = null;
  private String saveDialogTitleText = null;

  protected String saveButtonToolTipText = null;
  protected String openButtonToolTipText = null;
  protected String cancelButtonToolTipText = null;
  protected String updateButtonToolTipText = null;
  protected String helpButtonToolTipText = null;

  /**
   * The tooltip text displayed on the approve button when a directory is selected and the current
   * selection mode is not DIRECTORIES_ONLY.
   * 
   * @since 1.4
   */
  protected String directoryOpenButtonToolTipText = null;

  // Some generic FileChooser functions
  private Action approveSelectionAction = new ApproveSelectionAction();
  private Action cancelSelectionAction = new CancelSelectionAction();
  private Action updateAction = new UpdateAction();
  private Action newFolderAction;
  private Action goHomeAction = new GoHomeAction();
  private Action changeToParentDirectoryAction = new ChangeToParentDirectoryAction();

  private String newFolderErrorSeparator = null;
  private String newFolderErrorText = null;
  private String fileDescriptionText = null;
  private String directoryDescriptionText = null;

  private JFileInterfaceChooser filechooser = null;

  private boolean directorySelected = false;
  private FileInterface directory = null;

  private PropertyChangeListener propertyChangeListener = null;
  private AcceptAllFileFilter acceptAllFileFilter = new AcceptAllFileFilter();
  private FileInterfaceFilter actualFileFilter = null;
  private GlobFilter globFilter = null;
  private BasicDirectoryModel model = null;
  private BasicFileView fileView = new BasicFileView();
  private boolean usesSingleFilePane;
  private boolean readOnly;

  // The accessoryPanel is a container to place the JFileChooser accessory component
  private JPanel accessoryPanel = null;
  private Handler handler;

  public BasicFileChooserUI(JFileInterfaceChooser b)
  {
  }

  public void installUI(JComponent c)
  {
    accessoryPanel = new JPanel(new BorderLayout());
    filechooser = (JFileInterfaceChooser)c;

    createModel();

    clearIconCache();

    installDefaults(filechooser);
    installComponents(filechooser);
    installListeners(filechooser);
    filechooser.applyComponentOrientation(filechooser.getComponentOrientation());
  }

  public void uninstallUI(JComponent c)
  {
    uninstallListeners((JFileInterfaceChooser)filechooser);
    uninstallComponents((JFileInterfaceChooser)filechooser);
    uninstallDefaults((JFileInterfaceChooser)filechooser);

    if(accessoryPanel != null)
    {
      accessoryPanel.removeAll();
    }

    accessoryPanel = null;
    getFileChooser().removeAll();

    handler = null;
  }

  public void installComponents(JFileInterfaceChooser fc)
  {
  }

  public void uninstallComponents(JFileInterfaceChooser fc)
  {
  }

  protected void installListeners(JFileInterfaceChooser fc)
  {
    propertyChangeListener = createPropertyChangeListener(fc);
    if(propertyChangeListener != null)
    {
      fc.addPropertyChangeListener(propertyChangeListener);
    }
    fc.addPropertyChangeListener(getModel());

    InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    SwingUtilities.replaceUIInputMap(fc, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
    ActionMap actionMap = getActionMap();
    SwingUtilities.replaceUIActionMap(fc, actionMap);
  }

  InputMap getInputMap(int condition)
  {
    if(condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    {
      return (InputMap)DefaultLookup.get(getFileChooser(), this, "FileChooser.ancestorInputMap");
    }
    return null;
  }

  ActionMap getActionMap()
  {
    return createActionMap();
  }

  ActionMap createActionMap()
  {
    ActionMap map = new ActionMapUIResource();

    Action refreshAction = new UIAction(FilePane.ACTION_REFRESH)
    {
      public void actionPerformed(ActionEvent evt)
      {
        getFileChooser().rescanCurrentDirectory();
      }
    };

    map.put(FilePane.ACTION_APPROVE_SELECTION, getApproveSelectionAction());
    map.put(FilePane.ACTION_CANCEL, getCancelSelectionAction());
    map.put(FilePane.ACTION_REFRESH, refreshAction);
    map.put(FilePane.ACTION_CHANGE_TO_PARENT_DIRECTORY, getChangeToParentDirectoryAction());
    return map;
  }

  protected void uninstallListeners(JFileInterfaceChooser fc)
  {
    if(propertyChangeListener != null)
    {
      fc.removePropertyChangeListener(propertyChangeListener);
    }
    fc.removePropertyChangeListener(getModel());
    SwingUtilities.replaceUIInputMap(fc, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
    SwingUtilities.replaceUIActionMap(fc, null);
  }

  protected void installDefaults(JFileInterfaceChooser fc)
  {
    installIcons(fc);
    installStrings(fc);
    usesSingleFilePane = UIManager.getBoolean("FileChooser.usesSingleFilePane");
    readOnly = UIManager.getBoolean("FileChooser.readOnly");
    TransferHandler th = fc.getTransferHandler();
    if(th == null || th instanceof UIResource)
    {
      fc.setTransferHandler(defaultTransferHandler);
    }
    LookAndFeel.installProperty(fc, "opaque", Boolean.FALSE);
  }

  protected void installIcons(JFileInterfaceChooser fc)
  {
    directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    fileIcon = UIManager.getIcon("FileView.fileIcon");
    computerIcon = UIManager.getIcon("FileView.computerIcon");
    hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");
    floppyDriveIcon = UIManager.getIcon("FileView.floppyDriveIcon");

    newFolderIcon = UIManager.getIcon("FileChooser.newFolderIcon");
    upFolderIcon = UIManager.getIcon("FileChooser.upFolderIcon");
    homeFolderIcon = UIManager.getIcon("FileChooser.homeFolderIcon");
    detailsViewIcon = UIManager.getIcon("FileChooser.detailsViewIcon");
    listViewIcon = UIManager.getIcon("FileChooser.listViewIcon");
  }

  protected void installStrings(JFileInterfaceChooser fc)
  {

    Locale l = fc.getLocale();
    newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText", l);
    newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator", l);

    fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText", l);
    directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText", l);

    saveButtonText = UIManager.getString("FileChooser.saveButtonText", l);
    openButtonText = UIManager.getString("FileChooser.openButtonText", l);
    saveDialogTitleText = UIManager.getString("FileChooser.saveDialogTitleText", l);
    openDialogTitleText = UIManager.getString("FileChooser.openDialogTitleText", l);
    cancelButtonText = UIManager.getString("FileChooser.cancelButtonText", l);
    updateButtonText = UIManager.getString("FileChooser.updateButtonText", l);
    helpButtonText = UIManager.getString("FileChooser.helpButtonText", l);
    directoryOpenButtonText = UIManager.getString("FileChooser.directoryOpenButtonText", l);

    saveButtonMnemonic = getMnemonic("FileChooser.saveButtonMnemonic", l);
    openButtonMnemonic = getMnemonic("FileChooser.openButtonMnemonic", l);
    cancelButtonMnemonic = getMnemonic("FileChooser.cancelButtonMnemonic", l);
    updateButtonMnemonic = getMnemonic("FileChooser.updateButtonMnemonic", l);
    helpButtonMnemonic = getMnemonic("FileChooser.helpButtonMnemonic", l);
    directoryOpenButtonMnemonic = getMnemonic("FileChooser.directoryOpenButtonMnemonic", l);

    saveButtonToolTipText = UIManager.getString("FileChooser.saveButtonToolTipText", l);
    openButtonToolTipText = UIManager.getString("FileChooser.openButtonToolTipText", l);
    cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText", l);
    updateButtonToolTipText = UIManager.getString("FileChooser.updateButtonToolTipText", l);
    helpButtonToolTipText = UIManager.getString("FileChooser.helpButtonToolTipText", l);
    directoryOpenButtonToolTipText = UIManager.getString("FileChooser.directoryOpenButtonToolTipText", l);
  }

  protected void uninstallDefaults(JFileInterfaceChooser fc)
  {
    uninstallIcons(fc);
    uninstallStrings(fc);
    if(fc.getTransferHandler() instanceof UIResource)
    {
      fc.setTransferHandler(null);
    }
  }

  protected void uninstallIcons(JFileInterfaceChooser fc)
  {
    directoryIcon = null;
    fileIcon = null;
    computerIcon = null;
    hardDriveIcon = null;
    floppyDriveIcon = null;

    newFolderIcon = null;
    upFolderIcon = null;
    homeFolderIcon = null;
    detailsViewIcon = null;
    listViewIcon = null;
  }

  protected void uninstallStrings(JFileInterfaceChooser fc)
  {
    saveButtonText = null;
    openButtonText = null;
    cancelButtonText = null;
    updateButtonText = null;
    helpButtonText = null;
    directoryOpenButtonText = null;

    saveButtonToolTipText = null;
    openButtonToolTipText = null;
    cancelButtonToolTipText = null;
    updateButtonToolTipText = null;
    helpButtonToolTipText = null;
    directoryOpenButtonToolTipText = null;
  }

  protected void createModel()
  {
    model = new BasicDirectoryModel(getFileChooser());
    if(model != null)
    {
      model.invalidateFileCache();
    }
  }

  public BasicDirectoryModel getModel()
  {
    return model;
  }

  public PropertyChangeListener createPropertyChangeListener(JFileInterfaceChooser fc)
  {
    return null;
  }

  public String getFileName()
  {
    return null;
  }

  public String getDirectoryName()
  {
    return null;
  }

  public void setFileName(String filename)
  {
  }

  public void setDirectoryName(String dirname)
  {
  }

  public void rescanCurrentDirectory(JFileInterfaceChooser fc)
  {
  }

  public void ensureFileIsVisible(JFileInterfaceChooser fc, FileInterface f)
  {
  }

  public JFileInterfaceChooser getFileChooser()
  {
    return filechooser;
  }

  public JPanel getAccessoryPanel()
  {
    return accessoryPanel;
  }

  protected JButton getApproveButton(JFileInterfaceChooser fc)
  {
    return null;
  }

  public String getApproveButtonToolTipText(JFileInterfaceChooser fc)
  {
    String tooltipText = fc.getApproveButtonToolTipText();
    if(tooltipText != null)
    {
      return tooltipText;
    }

    if(fc.getDialogType() == JFileInterfaceChooser.OPEN_DIALOG)
    {
      return openButtonToolTipText;
    }
    else if(fc.getDialogType() == JFileInterfaceChooser.SAVE_DIALOG)
    {
      return saveButtonToolTipText;
    }
    return null;
  }

  public void clearIconCache()
  {
    fileView.clearIconCache();
  }

  // ********************************************
  // ************ Create Listeners **************
  // ********************************************

  private Handler getHandler()
  {
    if(handler == null)
    {
      handler = new Handler();
    }
    return handler;
  }

  protected MouseListener createDoubleClickListener(JFileInterfaceChooser fc, JList list)
  {
    return new Handler(list);
  }

  public ListSelectionListener createListSelectionListener(JFileInterfaceChooser fc)
  {
    return getHandler();
  }

  private class Handler implements MouseListener, ListSelectionListener
  {
    JList list;

    Handler()
    {
    }

    Handler(JList list)
    {
      this.list = list;
    }

    public void mouseClicked(MouseEvent evt)
    {
      // Note: we can't depend on evt.getSource() because of backward
      // compatability
      if(list != null && SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2)
      {

        int index = SwingUtilities2.loc2IndexFileList(list, evt.getPoint());
        if(index >= 0)
        {
          FileInterface f = (FileInterface)list.getModel().getElementAt(index);
          try
          {
            // Strip trailing ".."
            f = f.getCanonicalFile();
          }
          catch(IOException ex)
          {
            // That's ok, we'll use f as is
          }
          if(getFileChooser().isTraversable(f))
          {
            list.clearSelection();
            changeDirectory(f);
            if(getFileChooser().getFileSelectionMode() == JFileInterfaceChooser.FILES_AND_DIRECTORIES && getFileChooser().getFileSystemView().isFileSystem(f))
            {
              setFileName(f.toString());
            }
          }
          else
          {
            getFileChooser().approveSelection();
          }
        }
      }
    }

    public void mouseEntered(MouseEvent evt)
    {
      if(list != null)
      {
        TransferHandler th1 = getFileChooser().getTransferHandler();
        TransferHandler th2 = list.getTransferHandler();
        if(th1 != th2)
        {
          list.setTransferHandler(th1);
        }
        if(getFileChooser().getDragEnabled() != list.getDragEnabled())
        {
          list.setDragEnabled(getFileChooser().getDragEnabled());
        }
      }
    }

    public void mouseExited(MouseEvent evt)
    {
    }

    public void mousePressed(MouseEvent evt)
    {
    }

    public void mouseReleased(MouseEvent evt)
    {
    }

    public void valueChanged(ListSelectionEvent evt)
    {
      if(!evt.getValueIsAdjusting())
      {
        JFileInterfaceChooser chooser = getFileChooser();
        GenericFileInterfaceSystemView fsv = chooser.getFileSystemView();
        JList list = (JList)evt.getSource();

        int fsm = chooser.getFileSelectionMode();
        boolean useSetDirectory = usesSingleFilePane ? (fsm == JFileInterfaceChooser.FILES_ONLY) : (fsm != JFileInterfaceChooser.DIRECTORIES_ONLY);

        if(chooser.isMultiSelectionEnabled())
        {
          FileInterface[] files = null;
          Object[] objects = list.getSelectedValues();
          if(objects != null)
          {
            if(objects.length == 1 && ((FileInterface)objects[0]).isDirectory() && chooser.isTraversable(((FileInterface)objects[0]))
                && (useSetDirectory || !fsv.isFileSystem(((FileInterface)objects[0]))))
            {
              setDirectorySelected(true);
              setDirectory(((FileInterface)objects[0]));
            }
            else
            {
              ArrayList fList = new ArrayList(objects.length);
              for(int i = 0; i < objects.length; i++)
              {
                FileInterface f = (FileInterface)objects[i];
                boolean isDir = f.isDirectory();
                if((chooser.isFileSelectionEnabled() && !isDir) || (chooser.isDirectorySelectionEnabled() && fsv.isFileSystem(f) && isDir))
                {
                  fList.add(f);
                }
              }
              if(fList.size() > 0)
              {
                files = (FileInterface[])fList.toArray(new FileInterface[fList.size()]);
              }
              setDirectorySelected(false);
            }
          }
          chooser.setSelectedFiles(files);
        }
        else
        {
          FileInterface file = (FileInterface)list.getSelectedValue();
          if(file != null && file.isDirectory() && chooser.isTraversable(file) && (useSetDirectory || !fsv.isFileSystem(file)))
          {

            setDirectorySelected(true);
            setDirectory(file);
            if(usesSingleFilePane)
            {
              chooser.setSelectedFile(null);
            }
          }
          else
          {
            setDirectorySelected(false);
            if(file != null)
            {
              chooser.setSelectedFile(file);
            }
          }
        }
      }
    }
  }

  protected class DoubleClickListener extends MouseAdapter
  {
    // NOTE: This class exists only for backward compatability. All
    // its functionality has been moved into Handler. If you need to add
    // new functionality add it to the Handler, but make sure this
    // class calls into the Handler.
    Handler handler;

    public DoubleClickListener(JList list)
    {
      handler = new Handler(list);
    }

    /**
     * The JList used for representing the files is created by subclasses, but the selection is
     * monitored in this class. The TransferHandler installed in the JFileAdapterChooser is also installed
     * in the file list as it is used as the actual transfer source. The list is updated on a mouse
     * enter to reflect the current data transfer state of the file chooser.
     */
    public void mouseEntered(MouseEvent e)
    {
      handler.mouseEntered(e);
    }

    public void mouseClicked(MouseEvent e)
    {
      handler.mouseClicked(e);
    }
  }

  protected class SelectionListener implements ListSelectionListener
  {
    // NOTE: This class exists only for backward compatability. All
    // its functionality has been moved into Handler. If you need to add
    // new functionality add it to the Handler, but make sure this
    // class calls into the Handler.
    public void valueChanged(ListSelectionEvent e)
    {
      getHandler().valueChanged(e);
    }
  }

  /**
   * Property to remember whether a directory is currently selected in the UI.
   * 
   * @return <code>true</code> iff a directory is currently selected.
   * @since 1.4
   */
  protected boolean isDirectorySelected()
  {
    return directorySelected;
  }

  /**
   * Property to remember whether a directory is currently selected in the UI. This is normally
   * called by the UI on a selection event.
   * 
   * @param b iff a directory is currently selected.
   * @since 1.4
   */
  protected void setDirectorySelected(boolean b)
  {
    directorySelected = b;
  }

  /**
   * Property to remember the directory that is currently selected in the UI.
   * 
   * @return the value of the <code>directory</code> property
   * @see #setDirectory
   * @since 1.4
   */
  protected FileInterface getDirectory()
  {
    return directory;
  }

  /**
   * Property to remember the directory that is currently selected in the UI. This is normally
   * called by the UI on a selection event.
   * 
   * @param f the <code>FileInterface</code> object representing the directory that is currently selected
   * @since 1.4
   */
  protected void setDirectory(FileInterface f)
  {
    directory = f;
  }

  /**
   * Returns the mnemonic for the given key.
   */
  private int getMnemonic(String key, Locale l)
  {
    Object value = UIManager.get(key, l);

    if(value instanceof Integer)
    {
      return (Integer)value;
    }
    if(value instanceof String)
    {
      try
      {
        return Integer.parseInt((String)value);
      }
      catch(NumberFormatException nfe)
      {}
    }
    return 0;
  }

  // *******************************************************
  // ************ FileChooser UI PLAF methods **************
  // *******************************************************

  /**
   * Returns the default accept all file filter
   */
  public FileInterfaceFilter getAcceptAllFileFilter(JFileInterfaceChooser fc)
  {
    return acceptAllFileFilter;
  }

  public FileView getFileView(JFileInterfaceChooser fc)
  {
    return fileView;
  }

  /**
   * Returns the title of this dialog
   */
  public String getDialogTitle(JFileInterfaceChooser fc)
  {
    String dialogTitle = fc.getDialogTitle();
    if(dialogTitle != null)
    {
      return dialogTitle;
    }
    else if(fc.getDialogType() == JFileInterfaceChooser.OPEN_DIALOG)
    {
      return openDialogTitleText;
    }
    else if(fc.getDialogType() == JFileInterfaceChooser.SAVE_DIALOG)
    {
      return saveDialogTitleText;
    }
    else
    {
      return getApproveButtonText(fc);
    }
  }

  public int getApproveButtonMnemonic(JFileInterfaceChooser fc)
  {
    int mnemonic = fc.getApproveButtonMnemonic();
    if(mnemonic > 0)
    {
      return mnemonic;
    }
    else if(fc.getDialogType() == JFileInterfaceChooser.OPEN_DIALOG)
    {
      return openButtonMnemonic;
    }
    else if(fc.getDialogType() == JFileInterfaceChooser.SAVE_DIALOG)
    {
      return saveButtonMnemonic;
    }
    else
    {
      return mnemonic;
    }
  }

  public String getApproveButtonText(JFileInterfaceChooser fc)
  {
    String buttonText = fc.getApproveButtonText();
    if(buttonText != null)
    {
      return buttonText;
    }
    else if(fc.getDialogType() == JFileInterfaceChooser.OPEN_DIALOG)
    {
      return openButtonText;
    }
    else if(fc.getDialogType() == JFileInterfaceChooser.SAVE_DIALOG)
    {
      return saveButtonText;
    }
    else
    {
      return null;
    }
  }

  // *****************************
  // ***** Directory Actions *****
  // *****************************

  public Action getNewFolderAction()
  {
    if(newFolderAction == null)
    {
      newFolderAction = new NewFolderAction();
      // Note: Don't return null for readOnly, it might
      // break older apps.
      if(readOnly)
      {
        newFolderAction.setEnabled(false);
      }
    }
    return newFolderAction;
  }

  public Action getGoHomeAction()
  {
    return goHomeAction;
  }

  public Action getChangeToParentDirectoryAction()
  {
    return changeToParentDirectoryAction;
  }

  public Action getApproveSelectionAction()
  {
    return approveSelectionAction;
  }

  public Action getCancelSelectionAction()
  {
    return cancelSelectionAction;
  }

  public Action getUpdateAction()
  {
    return updateAction;
  }

  /**
   * Creates a new folder.
   */
  protected class NewFolderAction extends AbstractAction
  {
    protected NewFolderAction()
    {
      super(FilePane.ACTION_NEW_FOLDER);
    }

    public void actionPerformed(ActionEvent e)
    {
      if(readOnly)
      {
        return;
      }
      JFileInterfaceChooser fc = getFileChooser();
      FileInterface currentDirectory = fc.getCurrentDirectory();
      FileInterface newFolder = null;
      try
      {
        newFolder = fc.getFileSystemView().createNewFolder(currentDirectory);
        if(fc.isMultiSelectionEnabled())
        {
          fc.setSelectedFiles(new FileInterface[] { newFolder });
        }
        else
        {
          fc.setSelectedFile(newFolder);
        }
      }
      catch(IOException exc)
      {
        JOptionPane.showMessageDialog(fc, newFolderErrorText + newFolderErrorSeparator + exc, newFolderErrorText, JOptionPane.ERROR_MESSAGE);
        return;
      }

      fc.rescanCurrentDirectory();
    }
  }

  /**
   * Acts on the "home" key event or equivalent event.
   */
  protected class GoHomeAction extends AbstractAction
  {
    protected GoHomeAction()
    {
      super("Go Home");
    }

    public void actionPerformed(ActionEvent e)
    {
      JFileInterfaceChooser fc = getFileChooser();
      FileInterfaceConfiguration fic = fc.getFileInterfaceConfiguration();
      changeDirectory(fc.getFileSystemView().getHomeDirectory(fic));
    }
  }

  protected class ChangeToParentDirectoryAction extends AbstractAction
  {
    protected ChangeToParentDirectoryAction()
    {
      super("Go Up");
      putValue(Action.ACTION_COMMAND_KEY, FilePane.ACTION_CHANGE_TO_PARENT_DIRECTORY);
    }

    public void actionPerformed(ActionEvent e)
    {
      Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      if(focusOwner == null || !(focusOwner instanceof javax.swing.text.JTextComponent))
      {
        getFileChooser().changeToParentDirectory();
      }
    }
  }

  /**
   * Responds to an Open or Save request
   */
  protected class ApproveSelectionAction extends AbstractAction
  {
    protected ApproveSelectionAction()
    {
      super(FilePane.ACTION_APPROVE_SELECTION);
    }

    public void actionPerformed(ActionEvent e)
    {
      if(isDirectorySelected())
      {
        FileInterface dir = getDirectory();
        if(dir != null)
        {
          try
          {
            // Strip trailing ".."
            dir = dir.getCanonicalFile();
          }
          catch(IOException ex)
          {
            // Ok, use f as is
          }
          changeDirectory(dir);
          return;
        }
      }

      JFileInterfaceChooser chooser = getFileChooser();
      
      FileInterface fiTpl = FileConnector.createTemplateFileInterface(chooser.getFileInterfaceConfiguration());

      String filename = getFileName();
      GenericFileInterfaceSystemView fs = chooser.getFileSystemView();
      FileInterface dir = chooser.getCurrentDirectory();

      if(filename != null)
      {
        // Remove whitespace from beginning and end of filename
        filename = filename.trim();
      }

      if(filename == null || filename.equals(""))
      {
        // no file selected, multiple selection off, therefore cancel the approve action
        resetGlobFilter();
        return;
      }

      FileInterface selectedFile = null;
      FileInterface[] selectedFiles = null;

      if(filename != null && !filename.equals(""))
      {
        // Unix: Resolve '~' to user's home directory
        if(fiTpl.separatorChar() == '/')
        {
          if(filename.startsWith("~/"))
          {
            filename = System.getProperty("user.home") + filename.substring(1);
          }
          else if(filename.equals("~"))
          {
            filename = System.getProperty("user.home");
          }
        }

        if(chooser.isMultiSelectionEnabled() && filename.startsWith("\""))
        {
          ArrayList fList = new ArrayList();

          filename = filename.substring(1);
          if(filename.endsWith("\""))
          {
            filename = filename.substring(0, filename.length() - 1);
          }
          FileInterface[] children = null;
          int childIndex = 0;
          do
          {
            String str;
            int i = filename.indexOf("\" \"");
            if(i > 0)
            {
              str = filename.substring(0, i);
              filename = filename.substring(i + 3);
            }
            else
            {
              str = filename;
              filename = "";
            }
            FileInterface file = fs.createFileObject(str, chooser.getFileInterfaceConfiguration());
            if(!file.isAbsolute())
            {
              if(children == null)
              {
                children = fs.getFiles(dir, false);
                Arrays.sort(children);
              }
              for(int k = 0; k < children.length; k++)
              {
                int l = (childIndex + k) % children.length;
                if(children[l].getName().equals(str))
                {
                  file = children[l];
                  childIndex = l + 1;
                  break;
                }
              }
            }
            fList.add(file);
          } while(filename.length() > 0);
          if(fList.size() > 0)
          {
            selectedFiles = (FileInterface[])fList.toArray(new FileInterface[fList.size()]);
          }
          resetGlobFilter();
        }
        else
        {
          selectedFile = fs.createFileObject(filename, chooser.getFileInterfaceConfiguration());
          if(!selectedFile.isAbsolute())
          {
            selectedFile = fs.getChild(dir, filename);
          }
          // check for wildcard pattern
          FileInterfaceFilter currentFilter = chooser.getFileFilter();
          if(!selectedFile.exists() && isGlobPattern(filename, chooser.getFileInterfaceConfiguration()))
          {
            if(globFilter == null)
            {
              globFilter = new GlobFilter(chooser.getFileInterfaceConfiguration());
            }
            try
            {
              globFilter.setPattern(filename);
              if(!(currentFilter instanceof GlobFilter))
              {
                actualFileFilter = currentFilter;
              }
              chooser.setFileFilter(null);
              chooser.setFileFilter(globFilter);
              return;
            }
            catch(PatternSyntaxException pse)
            {
              // Not a valid glob pattern. Abandon filter.
            }
          }

          resetGlobFilter();

          // Check for directory change action
          boolean isDir = (selectedFile != null && selectedFile.isDirectory());
          boolean isTrav = (selectedFile != null && chooser.isTraversable(selectedFile));
          boolean isDirSelEnabled = chooser.isDirectorySelectionEnabled();
          boolean isFileSelEnabled = chooser.isFileSelectionEnabled();

          if(isDir && isTrav && !isDirSelEnabled)
          {
            changeDirectory(selectedFile);
            return;
          }
          else if((isDir || !isFileSelEnabled) && (!isDir || !isDirSelEnabled) && (!isDirSelEnabled || selectedFile.exists()))
          {
            selectedFile = null;
          }
        }
      }
      if(selectedFiles != null || selectedFile != null)
      {
        if(selectedFiles != null || chooser.isMultiSelectionEnabled())
        {
          if(selectedFiles == null)
          {
            selectedFiles = new FileInterface[] { selectedFile };
          }
          chooser.setSelectedFiles(selectedFiles);
          // Do it again. This is a fix for bug 4949273 to force the
          // selected value in case the ListSelectionModel clears it
          // for non-existing file names.
          chooser.setSelectedFiles(selectedFiles);
        }
        else
        {
          chooser.setSelectedFile(selectedFile);
        }
        chooser.approveSelection();
      }
      else
      {
        if(chooser.isMultiSelectionEnabled())
        {
          chooser.setSelectedFiles(null);
        }
        else
        {
          chooser.setSelectedFile(null);
        }
        chooser.cancelSelection();
      }
    }
  }

  private void resetGlobFilter()
  {
    if(actualFileFilter != null)
    {
      JFileInterfaceChooser chooser = getFileChooser();
      FileInterfaceFilter currentFilter = chooser.getFileFilter();
      if(currentFilter != null && currentFilter.equals(globFilter))
      {
        chooser.setFileFilter(actualFileFilter);
        chooser.removeChoosableFileFilter(globFilter);
      }
      actualFileFilter = null;
    }
  }

  private static boolean isGlobPattern(String filename, FileInterfaceConfiguration fic)
  {
    FileInterface fiTpl = FileConnector.createTemplateFileInterface(fic);
    return ((fiTpl.separatorChar() == '\\' && (filename.indexOf('*') >= 0 || filename.indexOf('?') >= 0)) || (fiTpl.separatorChar() == '/' && (filename.indexOf('*') >= 0
        || filename.indexOf('?') >= 0 || filename.indexOf('[') >= 0)));
  }

  /*
   * A file filter which accepts file patterns containing the special wildcards? on Windows and?[]
   * on Unix.
   */
  class GlobFilter implements FileInterfaceFilter
  {
    Pattern pattern;
    String globPattern;
    FileInterface fiTpl;

    public GlobFilter(FileInterfaceConfiguration fic)
    {
      super();
      this.fiTpl = FileConnector.createTemplateFileInterface(fic);
    }

    public void setPattern(String globPattern)
    {
      char[] gPat = globPattern.toCharArray();
      char[] rPat = new char[gPat.length * 2];
      boolean isWin32 = (fiTpl.separatorChar() == '\\');
      boolean inBrackets = false;
      StringBuffer buf = new StringBuffer();
      int j = 0;

      this.globPattern = globPattern;

      if(isWin32)
      {
        // On windows, a pattern ending with *.* is equal to ending with *
        int len = gPat.length;
        if(globPattern.endsWith("*.*"))
        {
          len -= 2;
        }
        for(int i = 0; i < len; i++)
        {
          switch(gPat[i])
          {
            case '*':
              rPat[j++] = '.';
              rPat[j++] = '*';
              break;

            case '?':
              rPat[j++] = '.';
              break;

            case '\\':
              rPat[j++] = '\\';
              rPat[j++] = '\\';
              break;

            default:
              if("+()^$.{}[]".indexOf(gPat[i]) >= 0)
              {
                rPat[j++] = '\\';
              }
              rPat[j++] = gPat[i];
              break;
          }
        }
      }
      else
      {
        for(int i = 0; i < gPat.length; i++)
        {
          switch(gPat[i])
          {
            case '*':
              if(!inBrackets)
              {
                rPat[j++] = '.';
              }
              rPat[j++] = '*';
              break;

            case '?':
              rPat[j++] = inBrackets ? '?' : '.';
              break;

            case '[':
              inBrackets = true;
              rPat[j++] = gPat[i];

              if(i < gPat.length - 1)
              {
                switch(gPat[i + 1])
                {
                  case '!':
                  case '^':
                    rPat[j++] = '^';
                    i++;
                    break;

                  case ']':
                    rPat[j++] = gPat[++i];
                    break;
                }
              }
              break;

            case ']':
              rPat[j++] = gPat[i];
              inBrackets = false;
              break;

            case '\\':
              if(i == 0 && gPat.length > 1 && gPat[1] == '~')
              {
                rPat[j++] = gPat[++i];
              }
              else
              {
                rPat[j++] = '\\';
                if(i < gPat.length - 1 && "*?[]".indexOf(gPat[i + 1]) >= 0)
                {
                  rPat[j++] = gPat[++i];
                }
                else
                {
                  rPat[j++] = '\\';
                }
              }
              break;

            default:
              // if ("+()|^$.{}<>".indexOf(gPat[i]) >= 0) {
              if(!Character.isLetterOrDigit(gPat[i]))
              {
                rPat[j++] = '\\';
              }
              rPat[j++] = gPat[i];
              break;
          }
        }
      }
      this.pattern = Pattern.compile(new String(rPat, 0, j), Pattern.CASE_INSENSITIVE);
    }

    public boolean accept(FileInterface f)
    {
      if(f == null)
      {
        return false;
      }
      if(f.isDirectory())
      {
        return true;
      }
      return pattern.matcher(f.getName()).matches();
    }

    public String getDescription()
    {
      return globPattern;
    }

    public boolean accept(File f)
    {
      if(f == null)
      {
        return false;
      }
      if(f.isDirectory())
      {
        return true;
      }
      return pattern.matcher(f.getName()).matches();
    }
  }

  /**
   * Responds to a cancel request.
   */
  protected class CancelSelectionAction extends AbstractAction
  {
    public void actionPerformed(ActionEvent e)
    {
      getFileChooser().cancelSelection();
    }
  }

  /**
   * Rescans the files in the current directory
   */
  protected class UpdateAction extends AbstractAction
  {
    public void actionPerformed(ActionEvent e)
    {
      JFileInterfaceChooser fc = getFileChooser();
      fc.setCurrentDirectory(fc.getFileSystemView().createFileObject(getDirectoryName(),
          fc.getFileInterfaceConfiguration()));
      fc.rescanCurrentDirectory();
    }
  }

  private void changeDirectory(FileInterface dir)
  {
    JFileInterfaceChooser fc = getFileChooser();
    // Traverse shortcuts on Windows
    if(dir != null && dir.separatorChar() == '\\' && dir.getPath().endsWith(".lnk"))
    {
      try
      {
        FileInterface linkedTo = ShellFolder.getShellFolder(dir).getLinkLocation();
        if(linkedTo != null && fc.isTraversable(linkedTo))
        {
          dir = linkedTo;
        }
        else
        {
          return;
        }
      }
      catch(FileNotFoundException ex)
      {
        return;
      }
    }
    fc.setCurrentDirectory(dir);
  }

  // *****************************************
  // ***** default AcceptAll file filter *****
  // *****************************************
  protected class AcceptAllFileFilter implements FileInterfaceFilter
  {

    public AcceptAllFileFilter()
    {
    }

    public boolean accept(FileInterface f)
    {
      return true;
    }

    public String getDescription()
    {
      return UIManager.getString("FileChooser.acceptAllFileFilterText");
    }

    public boolean accept(File pathname)
    {
      return true;
    }
  }

  // ***********************
  // * FileView operations *
  // ***********************
  protected class BasicFileView extends FileView
  {
    /* FileView type descriptions */
    // PENDING(jeff) - pass in the icon cache size
    protected Hashtable<FileInterface, Icon> iconCache = new Hashtable<FileInterface, Icon>();

    public BasicFileView()
    {
    }

    public void clearIconCache()
    {
      iconCache = new Hashtable<FileInterface, Icon>();
    }

    public String getName(FileInterface f)
    {
      // Note: Returns display name rather than file name
      String fileName = null;
      if(f != null)
      {
        fileName = getFileChooser().getFileSystemView().getSystemDisplayName(f);
      }
      return fileName;
    }

    public String getDescription(FileInterface f)
    {
      return f.getName();
    }

    public String getTypeDescription(FileInterface f)
    {
      String type = getFileChooser().getFileSystemView().getSystemTypeDescription(f);
      if(type == null)
      {
        if(f.isDirectory())
        {
          type = directoryDescriptionText;
        }
        else
        {
          type = fileDescriptionText;
        }
      }
      return type;
    }

    public Icon getCachedIcon(FileInterface f)
    {
      return (Icon)iconCache.get(f);
    }

    public void cacheIcon(FileInterface f, Icon i)
    {
      if(f == null || i == null)
      {
        return;
      }
      iconCache.put(f, i);
    }

    public Icon getIcon(FileInterface f)
    {
      Icon icon = getCachedIcon(f);
      if(icon != null)
      {
        return icon;
      }
      icon = fileIcon;
      if(f != null)
      {
        GenericFileInterfaceSystemView fsv = getFileChooser().getFileSystemView();

        if(fsv.isFloppyDrive(f))
        {
          icon = floppyDriveIcon;
        }
        else if(fsv.isDrive(f))
        {
          icon = hardDriveIcon;
        }
        else if(fsv.isComputerNode(f))
        {
          icon = computerIcon;
        }
        else if(f.isDirectory())
        {
          icon = directoryIcon;
        }
      }
      cacheIcon(f, icon);
      return icon;
    }

    public Boolean isHidden(FileInterface f)
    {
      String name = f.getName();
      if(name != null && name.charAt(0) == '.')
      {
        return Boolean.TRUE;
      }
      else
      {
        return Boolean.FALSE;
      }
    }
  }

  private static final TransferHandler defaultTransferHandler = new FileTransferHandler();

  /**
   * Data transfer support for the file chooser. Since files are currently presented as a list, the
   * list support is reused with the added flavor of DataFlavor.javaFileListFlavor
   */
  static class FileTransferHandler extends TransferHandler implements UIResource
  {

    /**
     * Create a Transferable to use as the source for a data transfer.
     * 
     * @param c The component holding the data to be transfered. This argument is provided to enable
     *          sharing of TransferHandlers by multiple components.
     * @return The representation of the data to be transfered.
     * 
     */
    protected Transferable createTransferable(JComponent c)
    {
      Object[] values = null;
      if(c instanceof JList)
      {
        values = ((JList)c).getSelectedValues();
      }
      else if(c instanceof JTable)
      {
        JTable table = (JTable)c;
        int[] rows = table.getSelectedRows();
        if(rows != null)
        {
          values = new Object[rows.length];
          for(int i = 0; i < rows.length; i++)
          {
            values[i] = table.getValueAt(rows[i], 0);
          }
        }
      }
      if(values == null || values.length == 0)
      {
        return null;
      }

      StringBuffer plainBuf = new StringBuffer();
      StringBuffer htmlBuf = new StringBuffer();

      htmlBuf.append("<html>\n<body>\n<ul>\n");

      for(int i = 0; i < values.length; i++)
      {
        Object obj = values[i];
        String val = ((obj == null) ? "" : obj.toString());
        plainBuf.append(val + "\n");
        htmlBuf.append("  <li>" + val + "\n");
      }

      // remove the last newline
      plainBuf.deleteCharAt(plainBuf.length() - 1);
      htmlBuf.append("</ul>\n</body>\n</html>");

      return new FileTransferable(plainBuf.toString(), htmlBuf.toString(), values);
    }

    public int getSourceActions(JComponent c)
    {
      return COPY;
    }
  }
}

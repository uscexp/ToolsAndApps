/* *****************************************************************
 * Project: common
 * File:    JFileInterfaceChooser.java
 * 
 * Creation:     08.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.components;

import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.swing.filechooser.FileView;
import haui.swing.plaf.FileInterfaceChooserUI;
import haui.util.ClassUtil;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.Vector;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;

import sun.awt.shell.ShellFolder;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/**
 * Module:      JFileInterfaceChooser<br>
 * <p>
 * Description: JFileInterfaceChooser<br>
 * </p><p>
 * Created:     08.03.2006 by Andreas Eisenhauer </p><p>
 * @history      08.03.2006 by AE: Created.<br>
 * </p><p>
 * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 * </p><p>
 * @version      v0.1, 2006; %version: %<br>
 * </p><p>
 * @since        JDK1.4
 * </p>
 */
public class JFileInterfaceChooser extends JComponent implements Accessible
{
  private static final long serialVersionUID = 3406977150507909231L;

  /**
   * @see #getUIClassID
   * @see #readObject
   */
  private static final String uiClassID = "FileInterfaceChooserUI";

  // ************************
  // ***** Dialog Types *****
  // ************************

  /**
   * Type value indicating that the <code>JFileAdapterChooser</code> supports an 
   * "Open" file operation.
   */
  public static final int OPEN_DIALOG = 0;

  /**
   * Type value indicating that the <code>JFileAdapterChooser</code> supports a
   * "Save" file operation.
   */
  public static final int SAVE_DIALOG = 1;

  /**
   * Type value indicating that the <code>JFileAdapterChooser</code> supports a
   * developer-specified file operation.
   */
  public static final int CUSTOM_DIALOG = 2;


  // ********************************
  // ***** Dialog Return Values *****
  // ********************************

  /**
   * Return value if cancel is chosen.
   */
  public static final int CANCEL_OPTION = 1;

  /**
   * Return value if approve (yes, ok) is chosen.
   */
  public static final int APPROVE_OPTION = 0;

  /**
   * Return value if an error occured.
   */
  public static final int ERROR_OPTION = -1;


  // **********************************
  // ***** JFileAdapterChooser properties *****
  // **********************************


  /** Instruction to display only files. */
  public static final int FILES_ONLY = 0;

  /** Instruction to display only directories. */
  public static final int DIRECTORIES_ONLY = 1;

  /** Instruction to display both files and directories. */
  public static final int FILES_AND_DIRECTORIES = 2;

  /** Instruction to cancel the current selection. */
  public static final String CANCEL_SELECTION = "CancelSelection";

  /**
   * Instruction to approve the current selection
   * (same as pressing yes or ok).
   */
  public static final String APPROVE_SELECTION = "ApproveSelection";

  /** Identifies change in the text on the approve (yes, ok) button. */
  public static final String APPROVE_BUTTON_TEXT_CHANGED_PROPERTY = "ApproveButtonTextChangedProperty";

  /**
   * Identifies change in the tooltip text for the approve (yes, ok)
   * button.  
   */
  public static final String APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY = "ApproveButtonToolTipTextChangedProperty";

  /** Identifies change in the mnemonic for the approve (yes, ok) button. */
  public static final String APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY = "ApproveButtonMnemonicChangedProperty";

  /** Instruction to display the control buttons. */
  public static final String CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY = "ControlButtonsAreShownChangedProperty";

  /** Identifies user's directory change. */
  public static final String DIRECTORY_CHANGED_PROPERTY = "directoryChanged";

  /** Identifies change in user's single-file selection. */
  public static final String SELECTED_FILE_CHANGED_PROPERTY = "SelectedFileChangedProperty";

  /** Identifies change in user's multiple-file selection. */
  public static final String SELECTED_FILES_CHANGED_PROPERTY = "SelectedFilesChangedProperty";

  /** Enables multiple-file selections. */
  public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";

  /**
   * Says that a different object is being used to find available drives
   * on the system. 
   */
  public static final String FILE_SYSTEM_VIEW_CHANGED_PROPERTY = "FileSystemViewChanged";

  /**
   * Says that a different object is being used to retrieve file
   * information. 
   */
  public static final String FILE_VIEW_CHANGED_PROPERTY = "fileViewChanged";

  /** Identifies a change in the display-hidden-files property. */
  public static final String FILE_HIDING_CHANGED_PROPERTY = "FileHidingChanged";

  /** User changed the kind of files to display. */
  public static final String FILE_FILTER_CHANGED_PROPERTY = "fileFilterChanged";

  /**
   * Identifies a change in the kind of selection (single,
   * multiple, etc.). 
   */
  public static final String FILE_SELECTION_MODE_CHANGED_PROPERTY = "fileSelectionChanged";

  /**
   * Says that a different accessory component is in use
   * (for example, to preview files). 
   */
  public static final String ACCESSORY_CHANGED_PROPERTY = "AccessoryChangedProperty";

  /**
   * Identifies whether a the AcceptAllFileFilter is used or not. 
   */
  public static final String ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY = "acceptAllFileFilterUsedChanged";

  /** Identifies a change in the dialog title. */
  public static final String DIALOG_TITLE_CHANGED_PROPERTY = "DialogTitleChangedProperty";

  /**
   * Identifies a change in the type of files displayed (files only,
   * directories only, or both files and directories). 
   */
  public static final String DIALOG_TYPE_CHANGED_PROPERTY = "DialogTypeChangedProperty";

  /** 
   * Identifies a change in the list of predefined file filters
   * the user can choose from.
   */
  public static final String CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY = "ChoosableFileFilterChangedProperty";

  // ******************************
  // ***** instance variables *****
  // ******************************

  private String dialogTitle = null;
  private String approveButtonText = null;
  private String approveButtonToolTipText = null;
  private int approveButtonMnemonic = 0;

  private ActionListener actionListener = null;

  private Vector filters = new Vector(5);
  private JDialog dialog = null;
  private int dialogType = OPEN_DIALOG;
  private int returnValue = ERROR_OPTION;
  private JComponent accessory = null;

  private FileView fileView = null;

  // uiFileView is not serialized, as it is initialized
  // by updateUI() after deserialization
  private transient FileView uiFileView = null;

  private boolean controlsShown = true;

  private boolean useFileHiding = true;
  private static final String SHOW_HIDDEN_PROP = "awt.file.showHiddenFiles";

  // Listens to changes in the native setting for showing hidden files.
  // The Listener is removed and the native setting is ignored if
  // setFileHidingEnabled() is ever called.
  private PropertyChangeListener showFilesListener = null;

  private int fileSelectionMode = FILES_ONLY;

  private boolean multiSelectionEnabled = false;

  private boolean useAcceptAllFileFilter = true;

  private boolean dragEnabled = false;

  private FileInterfaceFilter fileFilter = null;

  private GenericFileInterfaceSystemView fileSystemView = null;

  private FileInterface currentDirectory = null;
  private FileInterface selectedFile = null;
  private FileInterface[] selectedFiles;
  private FileInterfaceConfiguration fic;
  private LookAndFeel orgLookAndFeel = null;

  // *************************************
  // ***** JFileInterfaceChooser Constructors *****
  // *************************************

  /**
   * Constructs a <code>JFileInterfaceChooser</code> pointing to the user's
   * default directory. This default depends on the operating system.
   * It is typically the "My Documents" folder on Windows, and the
   * user's home directory on Unix.
   */
  public JFileInterfaceChooser(FileInterfaceConfiguration fic)
  {
    this((FileInterface)null, (GenericFileInterfaceSystemView)null, fic);
  }
  
  /**
   * Constructs a <code>JFileAdapterChooser</code> using the given path.
   * Passing in a <code>null</code>
   * string causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectoryPath  a <code>String</code> giving the path
   *        to a file or directory
   */
//  public JFileAdapterChooser(String currentDirectoryPath)
//  {
//    this(currentDirectoryPath, (GenericFileAdapterSystemView)null);
//  }

  /**
   * Constructs a <code>JFileAdapterChooser</code> using the given <code>FileInterface</code>
   * as the path. Passing in a <code>null</code> file
   * causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectory  a <code>FileInterface</code> object specifying
   *        the path to a file or directory
   */
  public JFileInterfaceChooser(FileInterface currentDirectory, FileInterfaceConfiguration fic)
  {
    this(currentDirectory, (GenericFileInterfaceSystemView)null, fic);
  }

  /**
   * Constructs a <code>JFileAdapterChooser</code> using the given
   * <code>FileSystemView</code>.
   */
  public JFileInterfaceChooser(GenericFileInterfaceSystemView fsv, FileInterfaceConfiguration fic)
  {
    this((FileInterface)null, fsv, fic);
  }


  /**
   * Constructs a <code>JFileAdapterChooser</code> using the given current directory
   * and <code>FileSystemView</code>.
   */
  public JFileInterfaceChooser(FileInterface currentDirectory, GenericFileInterfaceSystemView fsv,
      FileInterfaceConfiguration fic)
  {
    this.fic = fic;
    setup(fsv);
    setCurrentDirectory(currentDirectory);
  }

  /**
   * Constructs a <code>JFileAdapterChooser</code> using the given current directory
   * path and <code>FileSystemView</code>.
   */
//  public JFileAdapterChooser(String currentDirectoryPath, GenericFileAdapterSystemView fsv)
//  {
//    setup(fsv);
//    if(currentDirectoryPath == null)
//    {
//      setCurrentDirectory(null);
//    }
//    else
//    {
//      setCurrentDirectory(fileSystemView.createFileObject(currentDirectoryPath));
//    }
//  }

  public FileInterfaceConfiguration getFileInterfaceConfiguration()
  {
    return fic;
  }

  /**
   * Performs common constructor initialization and setup.
   */
  protected void setup(GenericFileInterfaceSystemView view)
  {
    // Track native setting for showing hidden files
    Toolkit tk = Toolkit.getDefaultToolkit();
    Object showHiddenProperty = tk.getDesktopProperty(SHOW_HIDDEN_PROP);
    if(showHiddenProperty instanceof Boolean)
    {
      useFileHiding = !((Boolean)showHiddenProperty).booleanValue();
      showFilesListener = new WeakPCL(this);
      tk.addPropertyChangeListener(SHOW_HIDDEN_PROP, showFilesListener);
    }

    if(view == null)
    {
      view = GenericFileInterfaceSystemView.getFileSystemView(fic);
    }
    setFileSystemView(view);
    updateUI();
    if(isAcceptAllFileFilterUsed())
    {
      setFileFilter(getAcceptAllFileFilter());
    }
  }

  /**
   * Sets the <code>dragEnabled</code> property, which must be <code>true</code> to enable automatic
   * drag handling (the first part of drag and drop) on this component. The
   * <code>transferHandler</code> property needs to be set to a non-<code>null</code> value for the
   * drag to do anything. The default value of the <code>dragEnabled</code> property is
   * <code>false</code>.
   * 
   * <p>
   * 
   * When automatic drag handling is enabled, most look and feels begin a drag-and-drop operation
   * whenever the user presses the mouse button over a selection and then moves the mouse a few
   * pixels. Setting this property to <code>true</code> can therefore have a subtle effect on how
   * selections behave.
   * 
   * <p>
   * 
   * Some look and feels might not support automatic drag and drop; they will ignore this property.
   * You can work around such look and feels by modifying the component to directly call the
   * <code>exportAsDrag</code> method of a <code>TransferHandler</code>.
   * 
   * @param b the value to set the <code>dragEnabled</code> property to
   * @exception HeadlessException if <code>b</code> is <code>true</code> and
   *              <code>GraphicsEnvironment.isHeadless()</code> returns <code>true</code>
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see #getDragEnabled
   * @see #setTransferHandler
   * @see TransferHandler
   * @since 1.4
   * 
   * @beaninfo description: determines whether automatic drag handling is enabled bound: false
   */
  public void setDragEnabled(boolean b)
  {
    if(b && GraphicsEnvironment.isHeadless())
    {
      throw new HeadlessException();
    }
    dragEnabled = b;
  }

  /**
   * Gets the value of the <code>dragEnabled</code> property.
   * 
   * @return the value of the <code>dragEnabled</code> property
   * @see #setDragEnabled
   * @since 1.4
   */
  public boolean getDragEnabled()
  {
    return dragEnabled;
  }

  // *****************************
  // ****** FileInterface Operations ******
  // *****************************

  /**
   * Returns the selected file. This can be set either by the programmer via <code>setFile</code> or
   * by a user action, such as either typing the filename into the UI or selecting the file from a
   * list in the UI.
   * 
   * @see #setSelectedFile
   * @return the selected file
   */
  public FileInterface getSelectedFile()
  {
    return selectedFile;
  }

  /**
   * Sets the selected file. If the file's parent directory is not the current directory, changes
   * the current directory to be the file's parent directory.
   * 
   * @beaninfo preferred: true bound: true
   * 
   * @see #getSelectedFile
   * 
   * @param file the selected file
   */
  public void setSelectedFile(FileInterface file)
  {
    FileInterface oldValue = selectedFile;
    selectedFile = file;
    if(selectedFile != null)
    {
      if(file.isAbsolute() && !getFileSystemView().isParent(getCurrentDirectory(), selectedFile))
      {
        setCurrentDirectory(selectedFile.getParentFileInterface());
      }
      if(!isMultiSelectionEnabled() || selectedFiles == null || selectedFiles.length == 1)
      {
        ensureFileIsVisible(selectedFile);
      }
    }
    firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, oldValue, selectedFile);
  }

  /**
   * Returns a list of selected files if the file chooser is set to allow multiple selection.
   */
  public FileInterface[] getSelectedFiles()
  {
    if(selectedFiles == null)
    {
      return new FileInterface[0];
    }
    else
    {
      return (FileInterface[])selectedFiles.clone();
    }
  }

  /**
   * Sets the list of selected files if the file chooser is set to allow multiple selection.
   * 
   * @beaninfo bound: true description: The list of selected files if the chooser is in multiple
   *           selection mode.
   */
  public void setSelectedFiles(FileInterface[] selectedFiles)
  {
    FileInterface[] oldValue = this.selectedFiles;
    if(selectedFiles != null && selectedFiles.length == 0)
    {
      selectedFiles = null;
    }
    this.selectedFiles = selectedFiles;
    setSelectedFile((selectedFiles != null) ? selectedFiles[0] : null);
    firePropertyChange(SELECTED_FILES_CHANGED_PROPERTY, oldValue, this.selectedFiles);
  }

  /**
   * Returns the current directory.
   * 
   * @return the current directory
   * @see #setCurrentDirectory
   */
  public FileInterface getCurrentDirectory()
  {
    return currentDirectory;
  }

  /**
   * Sets the current directory. Passing in <code>null</code> sets the file chooser to point to the
   * user's default directory. This default depends on the operating system. It is typically the
   * "My Documents" folder on Windows, and the user's home directory on Unix.
   * 
   * If the file passed in as <code>currentDirectory</code> is not a directory, the parent of the
   * file will be used as the currentDirectory. If the parent is not traversable, then it will walk
   * up the parent tree until it finds a traversable directory, or hits the root of the file system.
   * 
   * @beaninfo preferred: true bound: true description: The directory that the JFileInterfaceChooser is
   *           showing files of.
   * 
   * @param dir the current directory to point to
   * @see #getCurrentDirectory
   */
  public void setCurrentDirectory(FileInterface dir)
  {
    FileInterface oldValue = currentDirectory;

    if(dir != null && !dir.exists())
    {
      dir = currentDirectory;
    }
    if(dir == null)
    {
      dir = getFileSystemView().getDefaultDirectory();
    }
    if(currentDirectory != null)
    {
      /* Verify the toString of object */
      if(this.currentDirectory.equals(dir))
      {
        return;
      }
    }

    FileInterface prev = null;
    while(!isTraversable(dir) && prev != dir)
    {
      prev = dir;
      dir = getFileSystemView().getParentDirectory(dir);
    }
    currentDirectory = dir;

    firePropertyChange(DIRECTORY_CHANGED_PROPERTY, oldValue, currentDirectory);
  }

  /**
   * Changes the directory to be set to the parent of the current directory.
   * 
   * @see #getCurrentDirectory
   */
  public void changeToParentDirectory()
  {
    selectedFile = null;
    FileInterface oldValue = getCurrentDirectory();
    setCurrentDirectory(getFileSystemView().getParentDirectory(oldValue));
  }

  /**
   * Tells the UI to rescan its files list from the current directory.
   */
  public void rescanCurrentDirectory()
  {
    getUI().rescanCurrentDirectory(this);
  }

  /**
   * Makes sure that the specified file is viewable, and not hidden.
   * 
   * @param f a FileInterface object
   */
  public void ensureFileIsVisible(FileInterface f)
  {
    getUI().ensureFileIsVisible(this, f);
  }

  // **************************************
  // ***** JFileInterfaceChooser Dialog methods *****
  // **************************************

  /**
   * Pops up an "Open FileInterface" file chooser dialog. Note that the text that appears in the approve
   * button is determined by the L&F.
   * 
   * @param parent the parent component of the dialog, can be <code>null</code>; see
   *          <code>showDialog</code> for details
   * @return the return state of the file chooser on popdown:
   *         <ul>
   *         <li>JFileInterfaceChooser.CANCEL_OPTION <li>JFileInterfaceChooser.APPROVE_OPTION <li>
   *         JFileCHooser.ERROR_OPTION if an error occurs or the dialog is dismissed
   *         </ul>
   * @exception HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see #showDialog
   */
  public int showOpenDialog(Component parent) throws HeadlessException
  {
    setDialogType(OPEN_DIALOG);
    return showDialog(parent, null);
  }

  /**
   * Pops up a "Save FileInterface" file chooser dialog. Note that the text that appears in the approve
   * button is determined by the L&F.
   * 
   * @param parent the parent component of the dialog, can be <code>null</code>; see
   *          <code>showDialog</code> for details
   * @return the return state of the file chooser on popdown:
   *         <ul>
   *         <li>JFileInterfaceChooser.CANCEL_OPTION <li>JFileInterfaceChooser.APPROVE_OPTION <li>
   *         JFileCHooser.ERROR_OPTION if an error occurs or the dialog is dismissed
   *         </ul>
   * @exception HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see #showDialog
   */
  public int showSaveDialog(Component parent) throws HeadlessException
  {
    setDialogType(SAVE_DIALOG);
    return showDialog(parent, null);
  }

  /**
   * Pops a custom file chooser dialog with a custom approve button. For example, the following code
   * pops up a file chooser with a "Run Application" button (instead of the normal "Save" or "Open"
   * button):
   * 
   * <pre>
   * filechooser.showDialog(parentFrame, &quot;Run Application&quot;);
   * </pre>
   * 
   * Alternatively, the following code does the same thing:
   * 
   * <pre>
   * JFileInterfaceChooser chooser = new JFileInterfaceChooser(null);
   * chooser.setApproveButtonText(&quot;Run Application&quot;);
   * chooser.showDialog(parentFrame, null);
   * </pre>
   * 
   * <!--PENDING(jeff) - the following method should be added to the api: showDialog(Component
   * parent);--> <!--PENDING(kwalrath) - should specify modality and what "depends" means.-->
   * 
   * <p>
   * 
   * The <code>parent</code> argument determines two things: the frame on which the open dialog
   * depends and the component whose position the look and feel should consider when placing the
   * dialog. If the parent is a <code>Frame</code> object (such as a <code>JFrame</code>) then the
   * dialog depends on the frame and the look and feel positions the dialog relative to the frame
   * (for example, centered over the frame). If the parent is a component, then the dialog depends
   * on the frame containing the component, and is positioned relative to the component (for
   * example, centered over the component). If the parent is <code>null</code>, then the dialog
   * depends on no visible window, and it's placed in a look-and-feel-dependent position such as the
   * center of the screen.
   * 
   * @param parent the parent component of the dialog; can be <code>null</code>
   * @param approveButtonText the text of the <code>ApproveButton</code>
   * @return the return state of the file chooser on popdown:
   *         <ul>
   *         <li>JFileInterfaceChooser.CANCEL_OPTION <li>JFileInterfaceChooser.APPROVE_OPTION <li>
   *         JFileCHooser.ERROR_OPTION if an error occurs or the dialog is dismissed
   *         </ul>
   * @exception HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  public int showDialog(Component parent, String approveButtonText) throws HeadlessException
  {
    if(approveButtonText != null)
    {
      setApproveButtonText(approveButtonText);
      setDialogType(CUSTOM_DIALOG);
    }
    dialog = createDialog(parent);
    dialog.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        returnValue = CANCEL_OPTION;
      }
    });
    returnValue = ERROR_OPTION;
    rescanCurrentDirectory();

    dialog.show();
    firePropertyChange("JFileChooserDialogIsClosingProperty", dialog, null);
    dialog.removeAll();
    dialog.dispose();
    dialog = null;
    return returnValue;
  }

  /**
   * Creates and returns a new <code>JDialog</code> wrapping <code>this</code> centered on the
   * <code>parent</code> in the <code>parent</code>'s frame. This method can be overriden to further
   * manipulate the dialog, to disable resizing, set the location, etc. Example:
   * 
   * <pre>
   * class MyFileChooser extends JFileInterfaceChooser
   * {
   *   protected JDialog createDialog(Component parent) throws HeadlessException
   *   {
   *     JDialog dialog = super.createDialog(parent);
   *     dialog.setLocation(300, 200);
   *     dialog.setResizable(false);
   *     return dialog;
   *   }
   * }
   * </pre>
   * 
   * @param parent the parent component of the dialog; can be <code>null</code>
   * @return a new <code>JDialog</code> containing this instance
   * @exception HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @since 1.4
   */
  protected JDialog createDialog(Component parent) throws HeadlessException
  {
    String title = getUI().getDialogTitle(this);
    getAccessibleContext().setAccessibleDescription(title);

    JDialog dialog;
    Window window = null;
    try
    {
      window = (Window)ClassUtil.invokeStaticMethod(JOptionPane.class, "getWindowForComponent",
          (new Class[]{Component.class}), (new Object[]{parent}));
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    if(window instanceof Frame)
    {
      dialog = new JDialog((Frame)window, title, true);
    }
    else
    {
      dialog = new JDialog((Dialog)window, title, true);
    }
    dialog.setComponentOrientation(this.getComponentOrientation());

    Container contentPane = dialog.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(this, BorderLayout.CENTER);

    if(JDialog.isDefaultLookAndFeelDecorated())
    {
      boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if(supportsWindowDecorations)
      {
        dialog.getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
      }
    }
    dialog.pack();
    dialog.setLocationRelativeTo(parent);

    return dialog;
  }

  // **************************
  // ***** Dialog Options *****
  // **************************

  /**
   * Returns the value of the <code>controlButtonsAreShown</code> property.
   * 
   * @return the value of the <code>controlButtonsAreShown</code> property
   * 
   * @see #setControlButtonsAreShown
   * @since 1.3
   */
  public boolean getControlButtonsAreShown()
  {
    return controlsShown;
  }

  /**
   * Sets the property that indicates whether the <i>approve</i> and <i>cancel</i> buttons are shown
   * in the file chooser. This property is <code>true</code> by default. Look and feels that always
   * show these buttons will ignore the value of this property. This method fires a property-changed
   * event, using the string value of <code>CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY</code> as the
   * name of the property.
   * 
   * @param b <code>false</code> if control buttons should not be shown; otherwise,
   *          <code>true</code>
   * 
   * @beaninfo preferred: true bound: true description: Sets whether the approve & cancel buttons
   *           are shown.
   * 
   * @see #getControlButtonsAreShown
   * @see #CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY
   * @since 1.3
   */
  public void setControlButtonsAreShown(boolean b)
  {
    if(controlsShown == b)
    {
      return;
    }
    boolean oldValue = controlsShown;
    controlsShown = b;
    firePropertyChange(CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY, oldValue, controlsShown);
  }

  /**
   * Returns the type of this dialog. The default is <code>JFileInterfaceChooser.OPEN_DIALOG</code>.
   * 
   * @return the type of dialog to be displayed:
   *         <ul>
   *         <li>JFileInterfaceChooser.OPEN_DIALOG <li>JFileInterfaceChooser.SAVE_DIALOG <li>
   *         JFileInterfaceChooser.CUSTOM_DIALOG
   *         </ul>
   * 
   * @see #setDialogType
   */
  public int getDialogType()
  {
    return dialogType;
  }

  /**
   * Sets the type of this dialog. Use <code>OPEN_DIALOG</code> when you want to bring up a file
   * chooser that the user can use to open a file. Likewise, use <code>SAVE_DIALOG</code> for
   * letting the user choose a file for saving. Use <code>CUSTOM_DIALOG</code> when you want to use
   * the file chooser in a context other than "Open" or "Save". For instance, you might want to
   * bring up a file chooser that allows the user to choose a file to execute. Note that you
   * normally would not need to set the <code>JFileInterfaceChooser</code> to use <code>CUSTOM_DIALOG</code>
   * since a call to <code>setApproveButtonText</code> does this for you. The default dialog type is
   * <code>JFileInterfaceChooser.OPEN_DIALOG</code>.
   * 
   * @param dialogType the type of dialog to be displayed:
   *          <ul>
   *          <li>JFileInterfaceChooser.OPEN_DIALOG <li>JFileInterfaceChooser.SAVE_DIALOG <li>
   *          JFileInterfaceChooser.CUSTOM_DIALOG
   *          </ul>
   * 
   * @exception IllegalArgumentException if <code>dialogType</code> is not legal
   * @beaninfo preferred: true bound: true description: The type (open, save, custom) of the
   *           JFileInterfaceChooser. enum: OPEN_DIALOG JFileInterfaceChooser.OPEN_DIALOG SAVE_DIALOG
   *           JFileInterfaceChooser.SAVE_DIALOG CUSTOM_DIALOG JFileInterfaceChooser.CUSTOM_DIALOG
   * 
   * @see #getDialogType
   * @see #setApproveButtonText
   */
  // PENDING(jeff) - fire button text change property
  public void setDialogType(int dialogType)
  {
    if(this.dialogType == dialogType)
    {
      return;
    }
    if(!(dialogType == OPEN_DIALOG || dialogType == SAVE_DIALOG || dialogType == CUSTOM_DIALOG))
    {
      throw new IllegalArgumentException("Incorrect Dialog Type: " + dialogType);
    }
    int oldValue = this.dialogType;
    this.dialogType = dialogType;
    if(dialogType == OPEN_DIALOG || dialogType == SAVE_DIALOG)
    {
      setApproveButtonText(null);
    }
    firePropertyChange(DIALOG_TYPE_CHANGED_PROPERTY, oldValue, dialogType);
  }

  /**
   * Sets the string that goes in the <code>JFileInterfaceChooser</code> window's title bar.
   * 
   * @param dialogTitle the new <code>String</code> for the title bar
   * 
   * @beaninfo preferred: true bound: true description: The title of the JFileInterfaceChooser dialog window.
   * 
   * @see #getDialogTitle
   * 
   */
  public void setDialogTitle(String dialogTitle)
  {
    String oldValue = this.dialogTitle;
    this.dialogTitle = dialogTitle;
    if(dialog != null)
    {
      dialog.setTitle(dialogTitle);
    }
    firePropertyChange(DIALOG_TITLE_CHANGED_PROPERTY, oldValue, dialogTitle);
  }

  /**
   * Gets the string that goes in the <code>JFileInterfaceChooser</code>'s titlebar.
   * 
   * @see #setDialogTitle
   */
  public String getDialogTitle()
  {
    return dialogTitle;
  }

  // ************************************
  // ***** JFileInterfaceChooser View Options *****
  // ************************************

  /**
   * Sets the tooltip text used in the <code>ApproveButton</code>. If <code>null</code>, the UI
   * object will determine the button's text.
   * 
   * @beaninfo preferred: true bound: true description: The tooltip text for the ApproveButton.
   * 
   * @return the text used in the ApproveButton
   * 
   * @see #setApproveButtonText
   * @see #setDialogType
   * @see #showDialog
   */
  public void setApproveButtonToolTipText(String toolTipText)
  {
    if(approveButtonToolTipText == toolTipText)
    {
      return;
    }
    String oldValue = approveButtonToolTipText;
    approveButtonToolTipText = toolTipText;
    firePropertyChange(APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY, oldValue, approveButtonToolTipText);
  }

  /**
   * Returns the tooltip text used in the <code>ApproveButton</code>. If <code>null</code>, the UI
   * object will determine the button's text.
   * 
   * @return the text used in the <code>ApproveButton</code>
   * 
   * @see #setApproveButtonText
   * @see #setDialogType
   * @see #showDialog
   */
  public String getApproveButtonToolTipText()
  {
    return approveButtonToolTipText;
  }

  /**
   * Returns the approve button's mnemonic.
   * 
   * @return an integer value for the mnemonic key
   * 
   * @see #setApproveButtonMnemonic
   */
  public int getApproveButtonMnemonic()
  {
    return approveButtonMnemonic;
  }

  /**
   * Sets the approve button's mnemonic using a numeric keycode.
   * 
   * @param mnemonic an integer value for the mnemonic key
   * 
   * @beaninfo preferred: true bound: true description: The mnemonic key accelerator for the
   *           ApproveButton.
   * 
   * @see #getApproveButtonMnemonic
   */
  public void setApproveButtonMnemonic(int mnemonic)
  {
    if(approveButtonMnemonic == mnemonic)
    {
      return;
    }
    int oldValue = approveButtonMnemonic;
    approveButtonMnemonic = mnemonic;
    firePropertyChange(APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY, oldValue, approveButtonMnemonic);
  }

  /**
   * Sets the approve button's mnemonic using a character.
   * 
   * @param mnemonic a character value for the mnemonic key
   * 
   * @see #getApproveButtonMnemonic
   */
  public void setApproveButtonMnemonic(char mnemonic)
  {
    int vk = (int)mnemonic;
    if(vk >= 'a' && vk <= 'z')
    {
      vk -= ('a' - 'A');
    }
    setApproveButtonMnemonic(vk);
  }

  /**
   * Sets the text used in the <code>ApproveButton</code> in the <code>FileInterfaceChooserUI</code>.
   * 
   * @beaninfo preferred: true bound: true description: The text that goes in the ApproveButton.
   * 
   * @param approveButtonText the text used in the <code>ApproveButton</code>
   * 
   * @see #getApproveButtonText
   * @see #setDialogType
   * @see #showDialog
   */
  // PENDING(jeff) - have ui set this on dialog type change
  public void setApproveButtonText(String approveButtonText)
  {
    if(this.approveButtonText == approveButtonText)
    {
      return;
    }
    String oldValue = this.approveButtonText;
    this.approveButtonText = approveButtonText;
    firePropertyChange(APPROVE_BUTTON_TEXT_CHANGED_PROPERTY, oldValue, approveButtonText);
  }

  /**
   * Returns the text used in the <code>ApproveButton</code> in the <code>FileInterfaceChooserUI</code>. If
   * <code>null</code>, the UI object will determine the button's text.
   * 
   * Typically, this would be "Open" or "Save".
   * 
   * @return the text used in the <code>ApproveButton</code>
   * 
   * @see #setApproveButtonText
   * @see #setDialogType
   * @see #showDialog
   */
  public String getApproveButtonText()
  {
    return approveButtonText;
  }

  /**
   * Gets the list of user choosable file filters.
   * 
   * @return a <code>FileInterfaceFilter</code> array containing all the choosable file filters
   * 
   * @see #addChoosableFileFilter
   * @see #removeChoosableFileFilter
   * @see #resetChoosableFileFilters
   */
  public FileInterfaceFilter[] getChoosableFileFilters()
  {
    FileInterfaceFilter[] filterArray = new FileInterfaceFilter[filters.size()];
    filters.copyInto(filterArray);
    return filterArray;
  }

  /**
   * Adds a filter to the list of user choosable file filters. For information on setting the file
   * selection mode, see {@link #setFileSelectionMode setFileSelectionMode}.
   * 
   * @param filter the <code>FileInterfaceFilter</code> to add to the choosable file filter list
   * 
   * @beaninfo preferred: true bound: true description: Adds a filter to the list of user choosable
   *           file filters.
   * 
   * @see #getChoosableFileFilters
   * @see #removeChoosableFileFilter
   * @see #resetChoosableFileFilters
   * @see #setFileSelectionMode
   */
  public void addChoosableFileFilter(FileInterfaceFilter filter)
  {
    if(filter != null && !filters.contains(filter))
    {
      FileInterfaceFilter[] oldValue = getChoosableFileFilters();
      filters.addElement(filter);
      firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY, oldValue, getChoosableFileFilters());
    }
    setFileFilter(filter);
  }

  /**
   * Removes a filter from the list of user choosable file filters. Returns true if the file filter
   * was removed.
   * 
   * @see #addChoosableFileFilter
   * @see #getChoosableFileFilters
   * @see #resetChoosableFileFilters
   */
  public boolean removeChoosableFileFilter(FileInterfaceFilter f)
  {
    if(filters.contains(f))
    {
      if(getFileFilter() == f)
      {
        setFileFilter(null);
      }
      FileInterfaceFilter[] oldValue = getChoosableFileFilters();
      filters.removeElement(f);
      firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY, oldValue, getChoosableFileFilters());
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Resets the choosable file filter list to its starting state. Normally, this removes all added
   * file filters while leaving the <code>AcceptAll</code> file filter.
   * 
   * @see #addChoosableFileFilter
   * @see #getChoosableFileFilters
   * @see #removeChoosableFileFilter
   */
  public void resetChoosableFileFilters()
  {
    FileInterfaceFilter[] oldValue = getChoosableFileFilters();
    setFileFilter(null);
    filters.removeAllElements();
    if(isAcceptAllFileFilterUsed())
    {
      addChoosableFileFilter(getAcceptAllFileFilter());
    }
    firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY, oldValue, getChoosableFileFilters());
  }

  /**
   * Returns the <code>AcceptAll</code> file filter. For example, on Microsoft Windows this would be
   * All Files (*.*).
   */
  public FileInterfaceFilter getAcceptAllFileFilter()
  {
    FileInterfaceFilter filter = null;
    if(getUI() != null)
    {
      filter = getUI().getAcceptAllFileFilter(this);
    }
    return filter;
  }

  /**
   * Returns whether the <code>AcceptAll FileInterfaceFilter</code> is used.
   * 
   * @return true if the <code>AcceptAll FileInterfaceFilter</code> is used
   * @see #setAcceptAllFileFilterUsed
   * @since 1.3
   */
  public boolean isAcceptAllFileFilterUsed()
  {
    return useAcceptAllFileFilter;
  }

  /**
   * Determines whether the <code>AcceptAll FileInterfaceFilter</code> is used as an available choice in the
   * choosable filter list. If false, the <code>AcceptAll</code> file filter is removed from the
   * list of available file filters. If true, the <code>AcceptAll</code> file filter will become the
   * the actively used file filter.
   * 
   * @beaninfo preferred: true bound: true description: Sets whether the AcceptAll FileInterfaceFilter is
   *           used as an available choice in the choosable filter list.
   * 
   * @see #isAcceptAllFileFilterUsed
   * @see #getAcceptAllFileFilter
   * @see #setFileFilter
   * @since 1.3
   */
  public void setAcceptAllFileFilterUsed(boolean b)
  {
    boolean oldValue = useAcceptAllFileFilter;
    useAcceptAllFileFilter = b;
    if(!b)
    {
      removeChoosableFileFilter(getAcceptAllFileFilter());
    }
    else
    {
      removeChoosableFileFilter(getAcceptAllFileFilter());
      addChoosableFileFilter(getAcceptAllFileFilter());
    }
    firePropertyChange(ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY, oldValue, useAcceptAllFileFilter);
  }

  /**
   * Returns the accessory component.
   * 
   * @return this JFileInterfaceChooser's accessory component, or null
   * @see #setAccessory
   */
  public JComponent getAccessory()
  {
    return accessory;
  }

  /**
   * Sets the accessory component. An accessory is often used to show a preview image of the
   * selected file; however, it can be used for anything that the programmer wishes, such as extra
   * custom file chooser controls.
   * 
   * <p>
   * Note: if there was a previous accessory, you should unregister any listeners that the accessory
   * might have registered with the file chooser.
   * 
   * @beaninfo preferred: true bound: true description: Sets the accessory component on the
   *           JFileInterfaceChooser.
   */
  public void setAccessory(JComponent newAccessory)
  {
    JComponent oldValue = accessory;
    accessory = newAccessory;
    firePropertyChange(ACCESSORY_CHANGED_PROPERTY, oldValue, accessory);
  }

  /**
   * Sets the <code>JFileInterfaceChooser</code> to allow the user to just select files, just select
   * directories, or select both files and directories. The default is
   * <code>JFilesChooser.FILES_ONLY</code>.
   * 
   * @param mode the type of files to be displayed:
   *          <ul>
   *          <li>JFileInterfaceChooser.FILES_ONLY <li>JFileInterfaceChooser.DIRECTORIES_ONLY <li>
   *          JFileInterfaceChooser.FILES_AND_DIRECTORIES
   *          </ul>
   * 
   * @exception IllegalArgumentException if <code>mode</code> is an illegal file selection mode
   * @beaninfo preferred: true bound: true description: Sets the types of files that the
   *           JFileInterfaceChooser can choose. enum: FILES_ONLY JFileInterfaceChooser.FILES_ONLY DIRECTORIES_ONLY
   *           JFileInterfaceChooser.DIRECTORIES_ONLY FILES_AND_DIRECTORIES
   *           JFileInterfaceChooser.FILES_AND_DIRECTORIES
   * 
   * 
   * @see #getFileSelectionMode
   */
  public void setFileSelectionMode(int mode)
  {
    if(fileSelectionMode == mode)
    {
      return;
    }

    if((mode == FILES_ONLY) || (mode == DIRECTORIES_ONLY) || (mode == FILES_AND_DIRECTORIES))
    {
      int oldValue = fileSelectionMode;
      fileSelectionMode = mode;
      firePropertyChange(FILE_SELECTION_MODE_CHANGED_PROPERTY, oldValue, fileSelectionMode);
    }
    else
    {
      throw new IllegalArgumentException("Incorrect Mode for file selection: " + mode);
    }
  }

  /**
   * Returns the current file-selection mode. The default is <code>JFilesChooser.FILES_ONLY</code>.
   * 
   * @return the type of files to be displayed, one of the following:
   *         <ul>
   *         <li>JFileInterfaceChooser.FILES_ONLY <li>JFileInterfaceChooser.DIRECTORIES_ONLY <li>
   *         JFileInterfaceChooser.FILES_AND_DIRECTORIES
   *         </ul>
   * @see #setFileSelectionMode
   */
  public int getFileSelectionMode()
  {
    return fileSelectionMode;
  }

  /**
   * Convenience call that determines if files are selectable based on the current file selection
   * mode.
   * 
   * @see #setFileSelectionMode
   * @see #getFileSelectionMode
   */
  public boolean isFileSelectionEnabled()
  {
    return ((fileSelectionMode == FILES_ONLY) || (fileSelectionMode == FILES_AND_DIRECTORIES));
  }

  /**
   * Convenience call that determines if directories are selectable based on the current file
   * selection mode.
   * 
   * @see #setFileSelectionMode
   * @see #getFileSelectionMode
   */
  public boolean isDirectorySelectionEnabled()
  {
    return ((fileSelectionMode == DIRECTORIES_ONLY) || (fileSelectionMode == FILES_AND_DIRECTORIES));
  }

  /**
   * Sets the file chooser to allow multiple file selections.
   * 
   * @param b true if multiple files may be selected
   * @beaninfo bound: true description: Sets multiple file selection mode.
   * 
   * @see #isMultiSelectionEnabled
   */
  public void setMultiSelectionEnabled(boolean b)
  {
    if(multiSelectionEnabled == b)
    {
      return;
    }
    boolean oldValue = multiSelectionEnabled;
    multiSelectionEnabled = b;
    firePropertyChange(MULTI_SELECTION_ENABLED_CHANGED_PROPERTY, oldValue, multiSelectionEnabled);
  }

  /**
   * Returns true if multiple files can be selected.
   * 
   * @return true if multiple files can be selected
   * @see #setMultiSelectionEnabled
   */
  public boolean isMultiSelectionEnabled()
  {
    return multiSelectionEnabled;
  }

  /**
   * Returns true if hidden files are not shown in the file chooser; otherwise, returns false.
   * 
   * @return the status of the file hiding property
   * @see #setFileHidingEnabled
   */
  public boolean isFileHidingEnabled()
  {
    return useFileHiding;
  }

  /**
   * Sets file hiding on or off. If true, hidden files are not shown in the file chooser. The job of
   * determining which files are shown is done by the <code>FileView</code>.
   * 
   * @beaninfo preferred: true bound: true description: Sets file hiding on or off.
   * 
   * @param b the boolean value that determines whether file hiding is turned on
   * @see #isFileHidingEnabled
   */
  public void setFileHidingEnabled(boolean b)
  {
    // Dump showFilesListener since we'll ignore it from now on
    if(showFilesListener != null)
    {
      Toolkit.getDefaultToolkit().removePropertyChangeListener(SHOW_HIDDEN_PROP, showFilesListener);
      showFilesListener = null;
    }
    boolean oldValue = useFileHiding;
    useFileHiding = b;
    firePropertyChange(FILE_HIDING_CHANGED_PROPERTY, oldValue, useFileHiding);
  }

  /**
   * Sets the current file filter. The file filter is used by the file chooser to filter out files
   * from the user's view.
   * 
   * @beaninfo preferred: true bound: true description: Sets the FileInterface Filter used to filter out
   *           files of type.
   * 
   * @param filter the new current file filter to use
   * @see #getFileFilter
   */
  public void setFileFilter(FileInterfaceFilter filter)
  {
    FileInterfaceFilter oldValue = fileFilter;
    fileFilter = filter;
    if(filter != null)
    {
      if(isMultiSelectionEnabled() && selectedFiles != null && selectedFiles.length > 0)
      {
        Vector fList = new Vector();
        boolean failed = false;
        for(int i = 0; i < selectedFiles.length; i++)
        {
          if(filter.accept(selectedFiles[i]))
          {
            fList.add(selectedFiles[i]);
          }
          else
          {
            failed = true;
          }
        }
        if(failed)
        {
          setSelectedFiles((fList.size() == 0) ? null : (FileInterface[])fList.toArray(new FileInterface[fList.size()]));
        }
      }
      else if(selectedFile != null && !filter.accept(selectedFile))
      {
        setSelectedFile(null);
      }
    }
    firePropertyChange(FILE_FILTER_CHANGED_PROPERTY, oldValue, fileFilter);
  }

  /**
   * Returns the currently selected file filter.
   * 
   * @return the current file filter
   * @see #setFileFilter
   * @see #addChoosableFileFilter
   */
  public FileInterfaceFilter getFileFilter()
  {
    return fileFilter;
  }

  /**
   * Sets the file view to used to retrieve UI information, such as the icon that represents a file
   * or the type description of a file.
   * 
   * @beaninfo preferred: true bound: true description: Sets the FileInterface View used to get file type
   *           information.
   * 
   * @see #getFileView
   */
  public void setFileView(FileView fileView)
  {
    FileView oldValue = this.fileView;
    this.fileView = fileView;
    firePropertyChange(FILE_VIEW_CHANGED_PROPERTY, oldValue, fileView);
  }

  /**
   * Returns the current file view.
   * 
   * @see #setFileView
   */
  public FileView getFileView()
  {
    return fileView;
  }

  // ******************************
  // *****FileView delegation *****
  // ******************************

  // NOTE: all of the following methods attempt to delegate
  // first to the client set fileView, and if <code>null</code> is returned
  // (or there is now client defined fileView) then calls the
  // UI's default fileView.

  /**
   * Returns the filename.
   * 
   * @param f the <code>FileInterface</code>
   * @return the <code>String</code> containing the filename for <code>f</code>
   * @see FileView#getName
   */
  public String getName(FileInterface f)
  {
    String filename = null;
    if(f != null)
    {
      if(getFileView() != null)
      {
        filename = getFileView().getName(f);
      }
      if(filename == null && uiFileView != null)
      {
        filename = uiFileView.getName(f);
      }
    }
    return filename;
  }

  /**
   * Returns the file description.
   * 
   * @param f the <code>FileInterface</code>
   * @return the <code>String</code> containing the file description for <code>f</code>
   * @see FileView#getDescription
   */
  public String getDescription(FileInterface f)
  {
    String description = null;
    if(f != null)
    {
      if(getFileView() != null)
      {
        description = getFileView().getDescription(f);
      }
      if(description == null && uiFileView != null)
      {
        description = uiFileView.getDescription(f);
      }
    }
    return description;
  }

  /**
   * Returns the file type.
   * 
   * @param f the <code>FileInterface</code>
   * @return the <code>String</code> containing the file type description for <code>f</code>
   * @see FileView#getTypeDescription
   */
  public String getTypeDescription(FileInterface f)
  {
    String typeDescription = null;
    if(f != null)
    {
      if(getFileView() != null)
      {
        typeDescription = getFileView().getTypeDescription(f);
      }
      if(typeDescription == null && uiFileView != null)
      {
        typeDescription = uiFileView.getTypeDescription(f);
      }
    }
    return typeDescription;
  }

  /**
   * Returns the icon for this file or type of file, depending on the system.
   * 
   * @param f the <code>FileInterface</code>
   * @return the <code>Icon</code> for this file, or type of file
   * @see FileView#getIcon
   */
  public Icon getIcon(FileInterface f)
  {
    Icon icon = null;
    if(f != null)
    {
      if(getFileView() != null)
      {
        icon = getFileView().getIcon(f);
      }
      if(icon == null && uiFileView != null)
      {
        icon = uiFileView.getIcon(f);
      }
    }
    return icon;
  }

  /**
   * Returns true if the file (directory) can be visited. Returns false if the directory cannot be
   * traversed.
   * 
   * @param f the <code>FileInterface</code>
   * @return true if the file/directory can be traversed, otherwise false
   * @see FileView#isTraversable
   */
  public boolean isTraversable(FileInterface f)
  {
    Boolean traversable = null;
    if(f != null)
    {
      if(getFileView() != null)
      {
        traversable = getFileView().isTraversable(f);
      }
      if(traversable == null && uiFileView != null)
      {
        traversable = uiFileView.isTraversable(f);
      }
      if(traversable == null)
      {
        traversable = getFileSystemView().isTraversable(f);
      }
    }
    return (traversable != null && traversable.booleanValue());
  }

  /**
   * Returns true if the file should be displayed.
   * 
   * @param f the <code>FileInterface</code>
   * @return true if the file should be displayed, otherwise false
   * @see FileInterfaceFilter#accept
   */
  public boolean accept(FileInterface f)
  {
    boolean shown = true;
    if(f != null && fileFilter != null)
    {
      shown = fileFilter.accept(f);
    }
    return shown;
  }

  /**
   * Sets the file system view that the <code>JFileInterfaceChooser</code> uses for accessing and creating
   * file system resources, such as finding the floppy drive and getting a list of root drives.
   * 
   * @param fsv the new <code>FileSystemView</code>
   * 
   * @beaninfo expert: true bound: true description: Sets the FileSytemView used to get filesystem
   *           information.
   * 
   * @see FileSystemView
   */
  public void setFileSystemView(GenericFileInterfaceSystemView fsv)
  {
    GenericFileInterfaceSystemView oldValue = fileSystemView;
    fileSystemView = fsv;
    firePropertyChange(FILE_SYSTEM_VIEW_CHANGED_PROPERTY, oldValue, fileSystemView);
  }

  /**
   * Returns the file system view.
   * 
   * @return the <code>FileSystemView</code> object
   * @see #setFileSystemView
   */
  public GenericFileInterfaceSystemView getFileSystemView()
  {
    return fileSystemView;
  }

  // **************************
  // ***** Event Handling *****
  // **************************

  /**
   * Called by the UI when the user hits the Approve button (labeled "Open" or "Save", by default).
   * This can also be called by the programmer. This method causes an action event to fire with the
   * command string equal to <code>APPROVE_SELECTION</code>.
   * 
   * @see #APPROVE_SELECTION
   */
  public void approveSelection()
  {
    returnValue = APPROVE_OPTION;
    if(dialog != null)
    {
      dialog.setVisible(false);
    }
    fireActionPerformed(APPROVE_SELECTION);
  }

  /**
   * Called by the UI when the user chooses the Cancel button. This can also be called by the
   * programmer. This method causes an action event to fire with the command string equal to
   * <code>CANCEL_SELECTION</code>.
   * 
   * @see #CANCEL_SELECTION
   */
  public void cancelSelection()
  {
    returnValue = CANCEL_OPTION;
    if(dialog != null)
    {
      dialog.setVisible(false);
    }
    fireActionPerformed(CANCEL_SELECTION);
  }

  /**
   * Adds an <code>ActionListener</code> to the file chooser.
   * 
   * @param l the listener to be added
   * 
   * @see #approveSelection
   * @see #cancelSelection
   */
  public void addActionListener(ActionListener l)
  {
    listenerList.add(ActionListener.class, l);
  }

  /**
   * Removes an <code>ActionListener</code> from the file chooser.
   * 
   * @param l the listener to be removed
   * 
   * @see #addActionListener
   */
  public void removeActionListener(ActionListener l)
  {
    listenerList.remove(ActionListener.class, l);
  }

  /**
   * Returns an array of all the action listeners registered on this file chooser.
   * 
   * @return all of this file chooser's <code>ActionListener</code>s or an empty array if no action
   *         listeners are currently registered
   * 
   * @see #addActionListener
   * @see #removeActionListener
   * 
   * @since 1.4
   */
  public ActionListener[] getActionListeners()
  {
    return (ActionListener[])listenerList.getListeners(ActionListener.class);
  }

  /**
   * Notifies all listeners that have registered interest for notification on this event type. The
   * event instance is lazily created using the <code>command</code> parameter.
   * 
   * @see EventListenerList
   */
  protected void fireActionPerformed(String command)
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    long mostRecentEventTime = EventQueue.getMostRecentEventTime();
    int modifiers = 0;
    AWTEvent currentEvent = EventQueue.getCurrentEvent();
    if(currentEvent instanceof InputEvent)
    {
      modifiers = ((InputEvent)currentEvent).getModifiers();
    }
    else if(currentEvent instanceof ActionEvent)
    {
      modifiers = ((ActionEvent)currentEvent).getModifiers();
    }
    ActionEvent e = null;
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for(int i = listeners.length - 2; i >= 0; i -= 2)
    {
      if(listeners[i] == ActionListener.class)
      {
        // Lazily create the event:
        if(e == null)
        {
          e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command, mostRecentEventTime, modifiers);
        }
        ((ActionListener)listeners[i + 1]).actionPerformed(e);
      }
    }
  }

  private static class WeakPCL implements PropertyChangeListener
  {
    WeakReference<JFileInterfaceChooser> jfcRef;

    public WeakPCL(JFileInterfaceChooser jfc)
    {
      jfcRef = new WeakReference(jfc);
    }

    public void propertyChange(PropertyChangeEvent ev)
    {
      assert ev.getPropertyName().equals(SHOW_HIDDEN_PROP);
      JFileInterfaceChooser jfc = jfcRef.get();
      if(jfc == null)
      {
        // Our JFileAdapterChooser is no longer around, so we no longer need to
        // listen for PropertyChangeEvents.
        Toolkit.getDefaultToolkit().removePropertyChangeListener(SHOW_HIDDEN_PROP, this);
      }
      else
      {
        boolean oldValue = jfc.useFileHiding;
        jfc.useFileHiding = !((Boolean)ev.getNewValue()).booleanValue();
        jfc.firePropertyChange(FILE_HIDING_CHANGED_PROPERTY, oldValue, jfc.useFileHiding);
      }
    }
  }

  // *********************************
  // ***** Pluggable L&F methods *****
  // *********************************

  /**
   * Resets the UI property to a value from the current look and feel.
   * 
   * @see JComponent#updateUI
   */
  public void updateUI()
  {
    if(isAcceptAllFileFilterUsed())
    {
      removeChoosableFileFilter(getAcceptAllFileFilter());
    }
    modifyLookAndFeel();
    FileInterfaceChooserUI ui = ((FileInterfaceChooserUI)UIManager.getUI(this));
    if(fileSystemView == null)
    {
      // We were probably deserialized
      setFileSystemView(GenericFileInterfaceSystemView.getFileSystemView(fic));
    }
    setUI(ui);

    uiFileView = getUI().getFileView(this);
    if(isAcceptAllFileFilterUsed())
    {
      addChoosableFileFilter(getAcceptAllFileFilter());
    }
  }
  
  protected void modifyLookAndFeel()
  {
    if(orgLookAndFeel == null)
      orgLookAndFeel = UIManager.getLookAndFeel();
    LookAndFeel lookAndFeel = orgLookAndFeel;
    
    if(MetalLookAndFeel.class.isAssignableFrom(lookAndFeel.getClass()))
    {
      lookAndFeel = new MetalLookAndFeel() {

        @Override
        protected void initClassDefaults(UIDefaults table)
        {
          super.initClassDefaults(table);
          final String packageName = "haui.swing.plaf.metal.";

          Object[] uiDefaults = {
           "FileInterfaceChooserUI", packageName + "MetalFileChooserUI"
          };

          table.putDefaults(uiDefaults);
        }

        /* (non-Javadoc)
         * @see com.sun.java.swing.plaf.motif.MotifLookAndFeel#initComponentDefaults(javax.swing.UIDefaults)
         */
        @Override
        protected void initComponentDefaults(UIDefaults table)
        {
          super.initComponentDefaults(table);
          Object[] defaults = {
              // File Chooser
              "FileChooser.detailsViewIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserDetailViewIcon"),
              "FileChooser.homeFolderIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserHomeFolderIcon"),
              "FileChooser.listViewIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserListViewIcon"),
              "FileChooser.newFolderIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserNewFolderIcon"),
              "FileChooser.upFolderIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserUpFolderIcon"),

              "FileChooser.lookInLabelMnemonic",
              new Integer(KeyEvent.VK_I),
              "FileChooser.fileNameLabelMnemonic",
              new Integer(KeyEvent.VK_N),
              "FileChooser.filesOfTypeLabelMnemonic",
              new Integer(KeyEvent.VK_T),
              "FileChooser.usesSingleFilePane",
              Boolean.TRUE,
              "FileChooser.ancestorInputMap",
              new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up", "ENTER",
                  "approveSelection" }) };
          table.putDefaults(defaults);
        }
        
      };
    }
    else if(MotifLookAndFeel.class.isAssignableFrom(lookAndFeel.getClass()))
    {
      lookAndFeel = new MotifLookAndFeel() {

        @Override
        protected void initClassDefaults(UIDefaults table)
        {
          super.initClassDefaults(table);
          final String packageName = "haui.swing.plaf.motif.";

          Object[] uiDefaults = {
           "FileInterfaceChooserUI", packageName + "MotifFileChooserUI"
          };

          table.putDefaults(uiDefaults);
        }

        /* (non-Javadoc)
         * @see com.sun.java.swing.plaf.motif.MotifLookAndFeel#initComponentDefaults(javax.swing.UIDefaults)
         */
        @Override
        protected void initComponentDefaults(UIDefaults table)
        {
          super.initComponentDefaults(table);
          Object[] defaults = {
              "FileInterfaceChooser.pathLabelMnemonic", new Integer(KeyEvent.VK_P), // 'p'
              "FileInterfaceChooser.filterLabelMnemonic", new Integer (KeyEvent.VK_R), // 'r'
              "FileInterfaceChooser.foldersLabelMnemonic", new Integer (KeyEvent.VK_L), // 'l'
              "FileInterfaceChooser.filesLabelMnemonic", new Integer (KeyEvent.VK_I), // 'i'
              "FileInterfaceChooser.enterFileNameLabelMnemonic", new Integer (KeyEvent.VK_N), // 'n'
              "FileInterfaceChooser.ancestorInputMap", 
                 new UIDefaults.LazyInputMap(new Object[] {
                             "ESCAPE", "cancelSelection",
                             "ENTER", "approveSelection"
             })
          };
          table.putDefaults(defaults);
        }
        
      };
    }
    else if(WindowsClassicLookAndFeel.class.isAssignableFrom(lookAndFeel.getClass()))
    {
      lookAndFeel = new WindowsClassicLookAndFeel() {

        @Override
        protected void initClassDefaults(UIDefaults table)
        {
          super.initClassDefaults(table);
          final String packageName = "com.sun.java.swing.plaf.windows.";

          Object[] uiDefaults = {
           "FileInterfaceChooserUI", packageName + "MyWindowsFileChooserUI"
          };

          table.putDefaults(uiDefaults);
        }
        
        /* (non-Javadoc)
         * @see javax.swing.plaf.metal.MetalLookAndFeel#initComponentDefaults(javax.swing.UIDefaults)
         */
        @Override
        protected void initComponentDefaults(UIDefaults table)
        {
          super.initComponentDefaults(table);
          Integer fontPlain = new Integer(Font.PLAIN);
          Integer twelve = new Integer(12);
          Object dialogPlain12 = new SwingLazyValue(
              "javax.swing.plaf.FontUIResource",
              null,
              new Object[] {"Dialog", fontPlain, twelve});
          Object IconFont = dialogPlain12;
          Object[] defaults = {
                 "FileInterfaceChooser.homeFolderIcon",  new LazyWindowsIcon(null,
                                "icons/HomeFolder.gif"),
                 "FileInterfaceChooser.listFont", IconFont,
                 "FileInterfaceChooser.listViewIcon",    new LazyWindowsIcon("fileChooserIcon ListView",
                                "icons/ListView.gif"),
                 "FileInterfaceChooser.listViewWindowsStyle", Boolean.TRUE,
                 "FileInterfaceChooser.detailsViewIcon", new LazyWindowsIcon("fileChooserIcon DetailsView",
                                "icons/DetailsView.gif"),
                 "FileInterfaceChooser.upFolderIcon",    new LazyWindowsIcon("fileChooserIcon UpFolder",
                                "icons/UpFolder.gif"),
                 "FileInterfaceChooser.newFolderIcon",   new LazyWindowsIcon("fileChooserIcon NewFolder",
                                "icons/NewFolder.gif"),
                 "FileInterfaceChooser.useSystemExtensionHiding", Boolean.TRUE,

                       "FileInterfaceChooser.lookInLabelMnemonic", new Integer(KeyEvent.VK_I),
                       "FileInterfaceChooser.fileNameLabelMnemonic", new Integer(KeyEvent.VK_N),
                       "FileInterfaceChooser.filesOfTypeLabelMnemonic", new Integer(KeyEvent.VK_T),
                 "FileInterfaceChooser.usesSingleFilePane", Boolean.TRUE,
                 "FileInterfaceChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {
                     "ESCAPE", "cancelSelection",
                     "F2", "editFileName",
                     "F5", "refresh",
                     "BACK_SPACE", "Go Up",
                     "ENTER", "approveSelection"
                 })
          };
          table.putDefaults(defaults);
        }
      };
    }
    else if(WindowsLookAndFeel.class.isAssignableFrom(lookAndFeel.getClass()))
    {
      lookAndFeel = new WindowsLookAndFeel() {

        @Override
        protected void initClassDefaults(UIDefaults table)
        {
          super.initClassDefaults(table);
          final String packageName = "com.sun.java.swing.plaf.windows.";

          Object[] uiDefaults = {
           "FileInterfaceChooserUI", packageName + "MyWindowsFileChooserUI"
          };

          table.putDefaults(uiDefaults);
        }
        
        /* (non-Javadoc)
         * @see javax.swing.plaf.metal.MetalLookAndFeel#initComponentDefaults(javax.swing.UIDefaults)
         */
        @Override
        protected void initComponentDefaults(UIDefaults table)
        {
          super.initComponentDefaults(table);
          Integer fontPlain = new Integer(Font.PLAIN);
          Integer twelve = new Integer(12);
          Object dialogPlain12 = new SwingLazyValue(
              "javax.swing.plaf.FontUIResource",
              null,
              new Object[] {"Dialog", fontPlain, twelve});
          Object IconFont = dialogPlain12;
          Object[] defaults = {
                 "FileInterfaceChooser.homeFolderIcon",  new LazyWindowsIcon(null,
                                "icons/HomeFolder.gif"),
                 "FileInterfaceChooser.listFont", IconFont,
                 "FileInterfaceChooser.listViewIcon",    new LazyWindowsIcon("fileChooserIcon ListView",
                                "icons/ListView.gif"),
                 "FileInterfaceChooser.listViewWindowsStyle", Boolean.TRUE,
                 "FileInterfaceChooser.detailsViewIcon", new LazyWindowsIcon("fileChooserIcon DetailsView",
                                "icons/DetailsView.gif"),
                 "FileInterfaceChooser.upFolderIcon",    new LazyWindowsIcon("fileChooserIcon UpFolder",
                                "icons/UpFolder.gif"),
                 "FileInterfaceChooser.newFolderIcon",   new LazyWindowsIcon("fileChooserIcon NewFolder",
                                "icons/NewFolder.gif"),
                 "FileInterfaceChooser.useSystemExtensionHiding", Boolean.TRUE,

                       "FileInterfaceChooser.lookInLabelMnemonic", new Integer(KeyEvent.VK_I),
                       "FileInterfaceChooser.fileNameLabelMnemonic", new Integer(KeyEvent.VK_N),
                       "FileInterfaceChooser.filesOfTypeLabelMnemonic", new Integer(KeyEvent.VK_T),
                 "FileInterfaceChooser.usesSingleFilePane", Boolean.TRUE,
                 "FileInterfaceChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {
                     "ESCAPE", "cancelSelection",
                     "F2", "editFileName",
                     "F5", "refresh",
                     "BACK_SPACE", "Go Up",
                     "ENTER", "approveSelection"
                 })
          };
          table.putDefaults(defaults);
        }
      };
    }
    else if(SynthLookAndFeel.class.isAssignableFrom(lookAndFeel.getClass()))
    {
      lookAndFeel = new SynthLookAndFeel() {

        @Override
        protected void initClassDefaults(UIDefaults table)
        {
          super.initClassDefaults(table);
          final String packageName = "haui.swing.plaf.synth.";

          Object[] uiDefaults = {
           "FileInterfaceChooserUI", packageName + "SynthFileChooserUI"
          };

          table.putDefaults(uiDefaults);
        }
        
      };
    }
    else
    {
      lookAndFeel = new MetalLookAndFeel() {

        @Override
        protected void initClassDefaults(UIDefaults table)
        {
          super.initClassDefaults(table);
          final String packageName = "haui.swing.plaf.metal.";

          Object[] uiDefaults = {
           "FileInterfaceChooserUI", packageName + "MetalFileChooserUI"
          };

          table.putDefaults(uiDefaults);
        }
        
        /* (non-Javadoc)
         * @see com.sun.java.swing.plaf.motif.MotifLookAndFeel#initComponentDefaults(javax.swing.UIDefaults)
         */
        @Override
        protected void initComponentDefaults(UIDefaults table)
        {
          super.initComponentDefaults(table);
          Object[] defaults = {
              // File Chooser
              "FileChooser.detailsViewIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserDetailViewIcon"),
              "FileChooser.homeFolderIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserHomeFolderIcon"),
              "FileChooser.listViewIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserListViewIcon"),
              "FileChooser.newFolderIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserNewFolderIcon"),
              "FileChooser.upFolderIcon",
              new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserUpFolderIcon"),

              "FileChooser.lookInLabelMnemonic",
              new Integer(KeyEvent.VK_I),
              "FileChooser.fileNameLabelMnemonic",
              new Integer(KeyEvent.VK_N),
              "FileChooser.filesOfTypeLabelMnemonic",
              new Integer(KeyEvent.VK_T),
              "FileChooser.usesSingleFilePane",
              Boolean.TRUE,
              "FileChooser.ancestorInputMap",
              new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up", "ENTER",
                  "approveSelection" }) };
          table.putDefaults(defaults);
        }
      };
    }
    
    try {
      UIManager.setLookAndFeel(lookAndFeel);
//      SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception e) {
      // ignore
    }
  }

  /* (non-Javadoc)
   * @see javax.swing.JComponent#setVisible(boolean)
   */
  @Override
  public void setVisible(boolean flag)
  {
    if(!flag) {
      try {
        UIManager.setLookAndFeel(orgLookAndFeel);
        // SwingUtilities.updateComponentTreeUI(this);
      } catch (Exception e) {
        // ignore
      }
    }
    super.setVisible(flag);
  }

  /**
   * Returns a string that specifies the name of the L&F class that renders this component.
   * 
   * @return the string "FileInterfaceChooserUI"
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   * @beaninfo expert: true description: A string that specifies the name of the L&F class.
   */
  public String getUIClassID()
  {
    return uiClassID;
  }

  /**
   * Gets the UI object which implements the L&F for this component.
   * 
   * @return the FileInterfaceChooserUI object that implements the FileInterfaceChooserUI L&F
   */
  public FileInterfaceChooserUI getUI()
  {
    return (FileInterfaceChooserUI)ui;
  }

  /**
   * See <code>readObject</code> and <code>writeObject</code> in <code>JComponent</code> for more
   * information about serialization in Swing.
   */
  private void writeObject(ObjectOutputStream s) throws IOException
  {
    GenericFileInterfaceSystemView fsv = null;

    if(isAcceptAllFileFilterUsed())
    {
      // The AcceptAllFileFilter is UI specific, it will be reset by
      // updateUI() after deserialization
      removeChoosableFileFilter(getAcceptAllFileFilter());
    }
    if(fileSystemView.equals(GenericFileInterfaceSystemView.getFileSystemView(fic)))
    {
      // The default FileSystemView is platform specific, it will be
      // reset by updateUI() after deserialization
      fsv = fileSystemView;
      fileSystemView = null;
    }
    s.defaultWriteObject();
    if(fsv != null)
    {
      fileSystemView = fsv;
    }
    if(isAcceptAllFileFilterUsed())
    {
      addChoosableFileFilter(getAcceptAllFileFilter());
    }
    if(getUIClassID().equals(uiClassID))
    {
      byte count = 0;
      try
      {
        Byte co = (Byte)ClassUtil.invokeStaticMethod(JComponent.class, "getWriteObjCounter",
            null, new Object[]{this});
        count = co.byteValue();
        ClassUtil.invokeStaticMethod(JComponent.class, "setWriteObjCounter",
            null, new Object[]{this, new Byte(--count)});
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      if(count == 0 && ui != null)
      {
        ui.installUI(this);
      }
    }
  }

  /**
   * Returns a string representation of this <code>JFileAdapterChooser</code>. This method is intended to
   * be used only for debugging purposes, and the content and format of the returned string may vary
   * between implementations. The returned string may be empty but may not be <code>null</code>.
   * 
   * @return a string representation of this <code>JFileAdapterChooser</code>
   */
  protected String paramString()
  {
    String approveButtonTextString = (approveButtonText != null ? approveButtonText : "");
    String dialogTitleString = (dialogTitle != null ? dialogTitle : "");
    String dialogTypeString;
    if(dialogType == OPEN_DIALOG)
    {
      dialogTypeString = "OPEN_DIALOG";
    }
    else if(dialogType == SAVE_DIALOG)
    {
      dialogTypeString = "SAVE_DIALOG";
    }
    else if(dialogType == CUSTOM_DIALOG)
    {
      dialogTypeString = "CUSTOM_DIALOG";
    }
    else
      dialogTypeString = "";
    String returnValueString;
    if(returnValue == CANCEL_OPTION)
    {
      returnValueString = "CANCEL_OPTION";
    }
    else if(returnValue == APPROVE_OPTION)
    {
      returnValueString = "APPROVE_OPTION";
    }
    else if(returnValue == ERROR_OPTION)
    {
      returnValueString = "ERROR_OPTION";
    }
    else
      returnValueString = "";
    String useFileHidingString = (useFileHiding ? "true" : "false");
    String fileSelectionModeString;
    if(fileSelectionMode == FILES_ONLY)
    {
      fileSelectionModeString = "FILES_ONLY";
    }
    else if(fileSelectionMode == DIRECTORIES_ONLY)
    {
      fileSelectionModeString = "DIRECTORIES_ONLY";
    }
    else if(fileSelectionMode == FILES_AND_DIRECTORIES)
    {
      fileSelectionModeString = "FILES_AND_DIRECTORIES";
    }
    else
      fileSelectionModeString = "";
    String currentDirectoryString = (currentDirectory != null ? currentDirectory.toString() : "");
    String selectedFileString = (selectedFile != null ? selectedFile.toString() : "");

    return super.paramString() + ",approveButtonText=" + approveButtonTextString + ",currentDirectory=" + currentDirectoryString + ",dialogTitle="
        + dialogTitleString + ",dialogType=" + dialogTypeString + ",fileSelectionMode=" + fileSelectionModeString + ",returnValue=" + returnValueString
        + ",selectedFile=" + selectedFileString + ",useFileHiding=" + useFileHidingString;
  }

  // ///////////////
  // Accessibility support
  // //////////////

  protected AccessibleContext accessibleContext = null;

  /**
   * Gets the AccessibleContext associated with this JFileAdapterChooser. For file choosers, the
   * AccessibleContext takes the form of an AccessibleJFileAdapterChooser. A new AccessibleJFileAdapterChooser
   * instance is created if necessary.
   * 
   * @return an AccessibleJFileAdapterChooser that serves as the AccessibleContext of this JFileAdapterChooser
   */
  public AccessibleContext getAccessibleContext()
  {
    if(accessibleContext == null)
    {
      accessibleContext = new AccessibleJFileAdapterChooser();
    }
    return accessibleContext;
  }

  /**
   * This class implements accessibility support for the <code>JFileAdapterChooser</code> class. It
   * provides an implementation of the Java Accessibility API appropriate to file chooser
   * user-interface elements.
   */
  protected class AccessibleJFileAdapterChooser extends AccessibleJComponent
  {

    /**
     * Gets the role of this object.
     * 
     * @return an instance of AccessibleRole describing the role of the object
     * @see AccessibleRole
     */
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.FILE_CHOOSER;
    }

  } // inner class AccessibleJFileAdapterChooser

  /**
   * Gets an <code>Icon</code> from the native libraries if available,
   * otherwise gets it from an image resource file.
   */
  private static class LazyWindowsIcon implements UIDefaults.LazyValue
  {
    private String nativeImage;
    private String resource;

    LazyWindowsIcon(String nativeImage, String resource)
    {
      this.nativeImage = nativeImage;
      this.resource = resource;
    }

    public Object createValue(UIDefaults table)
    {
      if(nativeImage != null)
      {
        Image image = (Image)ShellFolder.get(nativeImage);
        if(image != null)
        {
          return new ImageIcon(image);
        }
      }
      return SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, resource);
    }
  }

}

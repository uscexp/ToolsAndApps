/* *****************************************************************
 * Project: common
 * FileInterface:    FilePane.java
 * 
 * Creation:     05.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.swing;

import haui.components.GenericFileInterfaceSystemView;
import haui.components.JFileInterfaceChooser;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.swing.plaf.basic.BasicDirectoryModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessControlException;
import java.security.AccessController;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;

/**
 * FilePane
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public class FilePane extends JPanel implements PropertyChangeListener
{
  private boolean smallIconsView;
  private Border listViewBorder;
  private Color listViewBackground;
  private boolean listViewWindowsStyle;
  private boolean readOnly;
  private ListSelectionModel listSelectionModel;
  private JList list;
  private JTable detailsTable;
  private static final int COLUMN_FILENAME = 0;
  private static final int COLUMN_FILESIZE = 1;
  private static final int COLUMN_FILETYPE = 2;
  private static final int COLUMN_FILEDATE = 3;
  private static final int COLUMN_FILEATTR = 4;
  private static final int COLUMN_COLCOUNT = 5;
  private int COLUMN_WIDTHS[] = { 150, 75, 130, 130, 40 };
  private String fileNameHeaderText;
  private String fileSizeHeaderText;
  private String fileTypeHeaderText;
  private String fileDateHeaderText;
  private String fileAttrHeaderText;
  private FileInterface newFolderFile;
  private FileChooserUIAccessor fileChooserUIAccessor;
  int lastIndex;
  FileInterface editFile;
  int editX;
  JTextField editCell;
  protected Action newFolderAction;
  private Handler handler;
  public static final String ACTION_APPROVE_SELECTION = "approveSelection";
  public static final String ACTION_CANCEL = "cancelSelection";
  public static final String ACTION_EDIT_FILE_NAME = "editFileName";
  public static final String ACTION_REFRESH = "refresh";
  public static final String ACTION_CHANGE_TO_PARENT_DIRECTORY = "Go Up";
  public static final String ACTION_NEW_FOLDER = "New Folder";
  public static final String ACTION_VIEW_LIST = "viewTypeList";
  public static final String ACTION_VIEW_DETAILS = "viewTypeDetails";
  private Action actions[];
  public static final int VIEWTYPE_LIST = 0;
  public static final int VIEWTYPE_DETAILS = 1;
  private static final int VIEWTYPE_COUNT = 2;
  private int viewType;
  private JPanel viewPanels[];
  private JPanel currentViewPanel;
  private String viewTypeActionNames[];
  private JPopupMenu contextMenu;
  private JMenu viewMenu;
  private String viewMenuLabelText;
  private String refreshActionLabelText;
  private String newFolderActionLabelText;
  private FocusListener editorFocusListener;
  private static FocusListener repaintListener = new FocusListener()
  {

    public void focusGained(FocusEvent focusevent)
    {
      repaintSelection(focusevent.getSource());
    }

    public void focusLost(FocusEvent focusevent)
    {
      repaintSelection(focusevent.getSource());
    }

    private void repaintSelection(Object obj)
    {
      if(obj instanceof JList)
        repaintListSelection((JList)obj);
      else if(obj instanceof JTable)
        repaintTableSelection((JTable)obj);
    }

    private void repaintListSelection(JList jlist)
    {
      int ai[] = jlist.getSelectedIndices();
      int ai1[] = ai;
      int i = ai1.length;
      for(int j = 0; j < i; j++)
      {
        int k = ai1[j];
        Rectangle rectangle = jlist.getCellBounds(k, k);
        jlist.repaint(rectangle);
      }

    }

    private void repaintTableSelection(JTable jtable)
    {
      int i = jtable.getSelectionModel().getMinSelectionIndex();
      int j = jtable.getSelectionModel().getMaxSelectionIndex();
      if(i == -1 || j == -1)
      {
        return;
      }
      else
      {
        int k = jtable.convertColumnIndexToView(0);
        Rectangle rectangle = jtable.getCellRect(i, k, false);
        Rectangle rectangle1 = jtable.getCellRect(j, k, false);
        Rectangle rectangle2 = rectangle.union(rectangle1);
        jtable.repaint(rectangle2);
        return;
      }
    }

  };
  
  private class DelayedSelectionUpdater implements Runnable
  {

    public void run()
    {
      setFileSelected();
      if(editFile != null)
      {
        editFileName(getModel().indexOf(editFile));
        editFile = null;
      }
    }

    FileInterface editFile;

    DelayedSelectionUpdater()
    {
      this(null);
    }

    DelayedSelectionUpdater(FileInterface file)
    {
      super();
      editFile = file;
      if(isShowing())
        SwingUtilities.invokeLater(this);
    }
  }

  class DetailsTableCellRenderer extends DefaultTableCellRenderer
  {

    public void setBounds(int i, int j, int k, int l)
    {
      if(getHorizontalAlignment() == 10)
        k = Math.min(k, getPreferredSize().width + 4);
      else
        i -= 4;
      super.setBounds(i, j, k, l);
    }

    public Insets getInsets(Insets insets)
    {
      insets = super.getInsets(insets);
      insets.left += 4;
      insets.right += 4;
      return insets;
    }

    public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean flag, boolean flag1, int i, int j)
    {
      if(jtable.convertColumnIndexToModel(j) != 0 || listViewWindowsStyle && !jtable.isFocusOwner())
        flag = false;
      return super.getTableCellRendererComponent(jtable, obj, flag, flag1, i, j);
    }

    public void setValue(Object obj)
    {
      setIcon(null);
      if(obj instanceof FileInterface)
      {
        FileInterface file = (FileInterface)obj;
        String s = chooser.getName(file);
        setText(s);
        Icon icon = chooser.getIcon(file);
        setIcon(icon);
      }
      else if(obj instanceof Date)
        setText(obj != null ? df.format((Date)obj) : "");
      else
        super.setValue(obj);
    }

    JFileInterfaceChooser chooser;
    DateFormat df;

    DetailsTableCellRenderer(JFileInterfaceChooser jfilechooser)
    {
      super();
      chooser = jfilechooser;
      df = DateFormat.getDateTimeInstance(3, 3, jfilechooser.getLocale());
    }
  }

  class DetailsTableModel extends AbstractTableModel implements ListDataListener
  {

    public int getRowCount()
    {
      return listModel.getSize();
    }

    public int getColumnCount()
    {
      return 5;
    }

    public String getColumnName(int i)
    {
      return columnNames[i];
    }

    public Class getColumnClass(int i)
    {
      switch(i)
      {
        case 0: // '\0'
          return FileInterface.class;

        case 3: // '\003'
          return Date.class;
      }
      return super.getColumnClass(i);
    }

    public Object getValueAt(int i, int j)
    {
      FileInterface file = (FileInterface)listModel.getElementAt(i);
      switch(j)
      {
        case 0: // '\0'
          return file;

        case 1: // '\001'
          if(!file.exists() || file.isDirectory())
            return null;
          if(listViewWindowsStyle)
            return (new StringBuilder()).append(file.length() / 1024L + 1L).append("KB").toString();
          long l = file.length() / 1024L;
          if(l < 1024L)
            return (new StringBuilder()).append(l != 0L ? l : 1L).append(" KB").toString();
          l /= 1024L;
          if(l < 1024L)
          {
            return (new StringBuilder()).append(l).append(" MB").toString();
          }
          else
          {
            l /= 1024L;
            return (new StringBuilder()).append(l).append(" GB").toString();
          }

        case 2: // '\002'
          if(!file.exists())
            return null;
          else
            return chooser.getFileSystemView().getSystemTypeDescription(file);

        case 3: // '\003'
          if(!file.exists() || chooser.getFileSystemView().isFileSystemRoot(file))
          {
            return null;
          }
          else
          {
            long l1 = file.lastModified();
            return l1 != 0L ? new Date(l1) : null;
          }

        case 4: // '\004'
          if(!file.exists() || chooser.getFileSystemView().isFileSystemRoot(file))
            return null;
          String s = "";
          try
          {
            if(!file.canWrite())
              s = (new StringBuilder()).append(s).append("R").toString();
          }
          catch(AccessControlException accesscontrolexception)
          {}
          if(file.isHidden())
            s = (new StringBuilder()).append(s).append("H").toString();
          return s;
      }
      return null;
    }

    public void setValueAt(Object obj, int i, int j)
    {
      if(j == 0)
      {
        JFileInterfaceChooser jfilechooser = getFileChooser();
        FileInterface file = (FileInterface)getValueAt(i, j);
        if(file != null)
        {
          String s = jfilechooser.getName(file);
          String s1 = file.getName();
          String s2 = ((String)obj).trim();
          if(!s2.equals(s))
          {
            String s3 = s2;
            int k = s1.length();
            int l = s.length();
            if(k > l && s1.charAt(l) == '.')
              s3 = (new StringBuilder()).append(s2).append(s1.substring(l)).toString();
            GenericFileInterfaceSystemView filesystemview = jfilechooser.getFileSystemView();
            FileInterface file1 = filesystemview.createFileObject(file.getParentFileInterface(), s3);
            if(!file1.exists() && getModel().renameFile(file, file1) && filesystemview.isParent(jfilechooser.getCurrentDirectory(), file1))
              if(jfilechooser.isMultiSelectionEnabled())
                jfilechooser.setSelectedFiles(new FileInterface[] { file1 });
              else
                jfilechooser.setSelectedFile(file1);
          }
        }
      }
    }

    public boolean isCellEditable(int i, int j)
    {
      FileInterface file = getFileChooser().getCurrentDirectory();
      return !readOnly && j == 0 && FilePane.canWrite(file);
    }

    public void contentsChanged(ListDataEvent listdataevent)
    {
      fireTableDataChanged();
    }

    public void intervalAdded(ListDataEvent listdataevent)
    {
      fireTableDataChanged();
    }

    public void intervalRemoved(ListDataEvent listdataevent)
    {
      fireTableDataChanged();
    }

    String columnNames[];
    JFileInterfaceChooser chooser;
    ListModel listModel;

    DetailsTableModel(JFileInterfaceChooser jfilechooser)
    {
      super();
      columnNames = (new String[] { fileNameHeaderText, fileSizeHeaderText, fileTypeHeaderText, fileDateHeaderText, fileAttrHeaderText });
      chooser = jfilechooser;
      listModel = getModel();
      listModel.addListDataListener(this);
    }
  }

  class EditActionListener implements ActionListener
  {

    public void actionPerformed(ActionEvent actionevent)
    {
      applyEdit();
    }

    EditActionListener()
    {
      super();
    }
  }

  public static interface FileChooserUIAccessor
  {

    public abstract JFileInterfaceChooser getFileChooser();

    public abstract BasicDirectoryModel getModel();

    public abstract JPanel createList();

    public abstract JPanel createDetailsView();

    public abstract boolean isDirectorySelected();

    public abstract FileInterface getDirectory();

    public abstract Action getApproveSelectionAction();

    public abstract Action getChangeToParentDirectoryAction();

    public abstract Action getNewFolderAction();

    public abstract MouseListener createDoubleClickListener(JList jlist);

    public abstract ListSelectionListener createListSelectionListener();
  }

  protected class FileRenderer extends DefaultListCellRenderer
  {

    public Component getListCellRendererComponent(JList jlist, Object obj, int i, boolean flag, boolean flag1)
    {
      if(listViewWindowsStyle && !jlist.isFocusOwner())
        flag = false;
      super.getListCellRendererComponent(jlist, obj, i, flag, flag1);
      FileInterface file = (FileInterface)obj;
      String s = getFileChooser().getName(file);
      setText(s);
      setFont(jlist.getFont());
      Icon icon = getFileChooser().getIcon(file);
      if(icon != null)
      {
        setIcon(icon);
        if(flag)
          editX = icon.getIconWidth() + 4;
      }
      else if(getFileChooser().getFileSystemView().isTraversable(file).booleanValue())
        setText((new StringBuilder()).append(s).append(FileConnector.createTemplateFileInterface(
            getFileChooser().getFileInterfaceConfiguration()).separatorChar()).toString());
      return this;
    }

    protected FileRenderer()
    {
      super();
    }
  }

  private class Handler implements MouseListener
  {

    public void mouseClicked(MouseEvent mouseevent)
    {
      JComponent jcomponent = (JComponent)mouseevent.getSource();
      int i;
      if(jcomponent instanceof JList)
        i = SwingUtilities2.loc2IndexFileList(list, mouseevent.getPoint());
      else if(jcomponent instanceof JTable)
      {
        JTable jtable = (JTable)jcomponent;
        java.awt.Point point = mouseevent.getPoint();
        i = jtable.rowAtPoint(point);
        if(SwingUtilities2.pointOutsidePrefSize(jtable, i, jtable.columnAtPoint(point), point))
          return;
        if(i >= 0 && list != null && listSelectionModel.isSelectedIndex(i))
        {
          Rectangle rectangle = list.getCellBounds(i, i);
          mouseevent = new MouseEvent(list, mouseevent.getID(), mouseevent.getWhen(), mouseevent.getModifiers(), rectangle.x + 1, rectangle.y
              + rectangle.height / 2, mouseevent.getClickCount(), mouseevent.isPopupTrigger(), mouseevent.getButton());
        }
      }
      else
      {
        return;
      }
      if(i >= 0 && SwingUtilities.isLeftMouseButton(mouseevent))
      {
        JFileInterfaceChooser jfilechooser = getFileChooser();
        if(mouseevent.getClickCount() == 1 && (jcomponent instanceof JList))
        {
          if((!jfilechooser.isMultiSelectionEnabled() || jfilechooser.getSelectedFiles().length <= 1) && i >= 0 && listSelectionModel.isSelectedIndex(i)
              && getEditIndex() == i && editFile == null)
            editFileName(i);
          else if(i >= 0)
            setEditIndex(i);
          else
            resetEditIndex();
        }
        else if(mouseevent.getClickCount() == 2)
          resetEditIndex();
      }
      if(getDoubleClickListener() != null)
        getDoubleClickListener().mouseClicked(mouseevent);
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
      JComponent jcomponent = (JComponent)mouseevent.getSource();
      if(jcomponent instanceof JTable)
      {
        JTable jtable = (JTable)mouseevent.getSource();
        javax.swing.TransferHandler transferhandler = getFileChooser().getTransferHandler();
        javax.swing.TransferHandler transferhandler1 = jtable.getTransferHandler();
        if(transferhandler != transferhandler1)
          jtable.setTransferHandler(transferhandler);
        boolean flag = getFileChooser().getDragEnabled();
        if(flag != jtable.getDragEnabled())
          jtable.setDragEnabled(flag);
      }
      else if((jcomponent instanceof JList) && getDoubleClickListener() != null)
        getDoubleClickListener().mouseEntered(mouseevent);
    }

    public void mouseExited(MouseEvent mouseevent)
    {
      if((mouseevent.getSource() instanceof JList) && getDoubleClickListener() != null)
        getDoubleClickListener().mouseExited(mouseevent);
    }

    public void mousePressed(MouseEvent mouseevent)
    {
      if((mouseevent.getSource() instanceof JList) && getDoubleClickListener() != null)
        getDoubleClickListener().mousePressed(mouseevent);
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
      if((mouseevent.getSource() instanceof JList) && getDoubleClickListener() != null)
        getDoubleClickListener().mouseReleased(mouseevent);
    }

    private MouseListener getDoubleClickListener()
    {
      if(doubleClickListener == null && list != null)
        doubleClickListener = fileChooserUIAccessor.createDoubleClickListener(list);
      return doubleClickListener;
    }

    private MouseListener doubleClickListener;

    private Handler()
    {
      super();
    }

  }

  class ViewTypeAction extends AbstractAction
  {

    public void actionPerformed(ActionEvent actionevent)
    {
      setViewType(viewType);
    }

    private int viewType;

    ViewTypeAction(int i)
    {
      super(viewTypeActionNames[i]);
      viewType = i;
      String s;
      switch(i)
      {
        case 0: // '\0'
          s = "viewTypeList";
          break;

        case 1: // '\001'
          s = "viewTypeDetails";
          break;

        default:
          s = (String)getValue("Name");
          break;
      }
      putValue("ActionCommandKey", s);
    }
  }

  public FilePane(FileChooserUIAccessor filechooseruiaccessor)
  {
    super(new BorderLayout());
    viewType = -1;
    viewPanels = new JPanel[2];
    editorFocusListener = new FocusAdapter()
    {

      public void focusLost(FocusEvent focusevent)
      {
        if(!focusevent.isTemporary())
          applyEdit();
      }
    };
    smallIconsView = false;
    fileNameHeaderText = null;
    fileSizeHeaderText = null;
    fileTypeHeaderText = null;
    fileDateHeaderText = null;
    fileAttrHeaderText = null;
    lastIndex = -1;
    editFile = null;
    editX = 20;
    editCell = null;
    fileChooserUIAccessor = filechooseruiaccessor;
    installDefaults();
    createActionMap();
  }

  protected JFileInterfaceChooser getFileChooser()
  {
    return fileChooserUIAccessor.getFileChooser();
  }

  protected BasicDirectoryModel getModel()
  {
    return fileChooserUIAccessor.getModel();
  }

  public int getViewType()
  {
    return viewType;
  }

  public void setViewType(int i)
  {
    int j = viewType;
    if(i == j)
      return;
    viewType = i;
    switch(i)
    {
      default:
        break;

      case 0: // '\0'
        if(viewPanels[i] == null)
        {
          JPanel jpanel = fileChooserUIAccessor.createList();
          if(jpanel == null)
            jpanel = createList();
          setViewPanel(i, jpanel);
        }
        list.setLayoutOrientation(1);
        break;

      case 1: // '\001'
        if(viewPanels[i] != null)
          break;
        JPanel jpanel1 = fileChooserUIAccessor.createDetailsView();
        if(jpanel1 == null)
          jpanel1 = createDetailsView();
        setViewPanel(i, jpanel1);
        break;
    }
    JPanel jpanel2 = currentViewPanel;
    currentViewPanel = viewPanels[i];
    if(currentViewPanel != jpanel2)
    {
      if(jpanel2 != null)
        remove(jpanel2);
      add(currentViewPanel, "Center");
      revalidate();
      repaint();
    }
    updateViewMenu();
    firePropertyChange("viewType", j, i);
  }

  public Action getViewTypeAction(int i)
  {
    return new ViewTypeAction(i);
  }

  private static void recursivelySetInheritsPopupMenu(Container container, boolean flag)
  {
    if(container instanceof JComponent)
      ((JComponent)container).setInheritsPopupMenu(flag);
    int i = container.getComponentCount();
    for(int j = 0; j < i; j++)
      recursivelySetInheritsPopupMenu((Container)container.getComponent(j), flag);

  }

  public void setViewPanel(int i, JPanel jpanel)
  {
    viewPanels[i] = jpanel;
    recursivelySetInheritsPopupMenu(jpanel, true);
    switch(i)
    {
      case 0: // '\0'
        list = (JList)findChildComponent(viewPanels[i], JList.class);
        if(listSelectionModel == null)
        {
          listSelectionModel = list.getSelectionModel();
          if(detailsTable != null)
            detailsTable.setSelectionModel(listSelectionModel);
        }
        else
        {
          list.setSelectionModel(listSelectionModel);
        }
        break;

      case 1: // '\001'
        detailsTable = (JTable)findChildComponent(viewPanels[i], JTable.class);
        detailsTable.setRowHeight(Math.max(detailsTable.getFont().getSize() + 4, 17));
        if(listSelectionModel != null)
          detailsTable.setSelectionModel(listSelectionModel);
        break;
    }
    if(viewType == i)
    {
      if(currentViewPanel != null)
        remove(currentViewPanel);
      currentViewPanel = jpanel;
      add(currentViewPanel, "Center");
      revalidate();
      repaint();
    }
  }

  protected void installDefaults()
  {
    java.util.Locale locale = getFileChooser().getLocale();
    listViewBorder = UIManager.getBorder("FileChooser.listViewBorder");
    listViewBackground = UIManager.getColor("FileChooser.listViewBackground");
    listViewWindowsStyle = UIManager.getBoolean("FileChooser.listViewWindowsStyle");
    readOnly = UIManager.getBoolean("FileChooser.readOnly");
    viewMenuLabelText = UIManager.getString("FileChooser.viewMenuLabelText", locale);
    refreshActionLabelText = UIManager.getString("FileChooser.refreshActionLabelText", locale);
    newFolderActionLabelText = UIManager.getString("FileChooser.newFolderActionLabelText", locale);
    viewTypeActionNames = new String[2];
    viewTypeActionNames[0] = UIManager.getString("FileChooser.listViewActionLabelText", locale);
    viewTypeActionNames[1] = UIManager.getString("FileChooser.detailsViewActionLabelText", locale);
  }

  public Action[] getActions()
  {
    if(actions == null)
    {
      ArrayList arraylist = new ArrayList(8);
      class _cls1FilePaneAction extends AbstractAction
      {

        public void actionPerformed(ActionEvent actionevent)
        {
          String s = (String)getValue("ActionCommandKey");
          if(s == "cancelSelection")
          {
            if(editFile != null)
              cancelEdit();
            else
              getFileChooser().cancelSelection();
          }
          else if(s == "editFileName")
          {
            JFileInterfaceChooser jfilechooser = getFileChooser();
            int i = listSelectionModel.getMinSelectionIndex();
            if(i >= 0 && editFile == null && (!jfilechooser.isMultiSelectionEnabled() || jfilechooser.getSelectedFiles().length <= 1))
              editFileName(i);
          }
          else if(s == "refresh")
            getFileChooser().rescanCurrentDirectory();
        }

        public boolean isEnabled()
        {
          String s = (String)getValue("ActionCommandKey");
          if(s == "cancelSelection")
            return getFileChooser().isEnabled();
          if(s == "editFileName")
            return !readOnly && getFileChooser().isEnabled();
          else
            return true;
        }

        _cls1FilePaneAction(String s)
        {
          this(s, s);
        }

        _cls1FilePaneAction(String s, String s1)
        {
          super(s);
          putValue("ActionCommandKey", s1);
        }
      }

      arraylist.add(new _cls1FilePaneAction("cancelSelection"));
      arraylist.add(new _cls1FilePaneAction("editFileName"));
      arraylist.add(new _cls1FilePaneAction(refreshActionLabelText, "refresh"));
      Action action = fileChooserUIAccessor.getApproveSelectionAction();
      if(action != null)
        arraylist.add(action);
      action = fileChooserUIAccessor.getChangeToParentDirectoryAction();
      if(action != null)
        arraylist.add(action);
      action = getNewFolderAction();
      if(action != null)
        arraylist.add(action);
      action = getViewTypeAction(0);
      if(action != null)
        arraylist.add(action);
      action = getViewTypeAction(1);
      if(action != null)
        arraylist.add(action);
      actions = (Action[])arraylist.toArray(new Action[arraylist.size()]);
    }
    return actions;
  }

  protected void createActionMap()
  {
    addActionsToMap(super.getActionMap(), getActions());
  }

  public static void addActionsToMap(ActionMap actionmap, Action aaction[])
  {
    if(actionmap != null && aaction != null)
    {
      for(int i = 0; i < aaction.length; i++)
      {
        Action action = aaction[i];
        String s = (String)action.getValue("ActionCommandKey");
        if(s == null)
          s = (String)action.getValue("Name");
        actionmap.put(s, action);
      }

    }
  }

  private void updateListRowCount(JList jlist)
  {
    if(smallIconsView)
      jlist.setVisibleRowCount(getModel().getSize() / 3);
    else
      jlist.setVisibleRowCount(-1);
  }

  public JPanel createList()
  {
    JPanel jpanel = new JPanel(new BorderLayout());
    final JFileInterfaceChooser fileChooser = getFileChooser();
    final JList list = new JList()
    {

      public int getNextMatch(String s, int i, javax.swing.text.Position.Bias bias)
      {
        ListModel listmodel = getModel();
        int j = listmodel.getSize();
        if(s == null || i < 0 || i >= j)
          throw new IllegalArgumentException();
        boolean flag = bias == javax.swing.text.Position.Bias.Backward;
        int k = i;
        while(flag ? k < 0 : k >= j)
        {
          String s1 = fileChooser.getName((FileInterface)listmodel.getElementAt(k));
          if(s1.regionMatches(true, 0, s, 0, s.length()))
            return k;
          k += flag ? -1 : 1;
        }
        return -1;
      }
    };
    list.setCellRenderer(new FileRenderer());
    list.setLayoutOrientation(1);
    list.putClientProperty("List.isFileList", Boolean.TRUE);
    if(listViewWindowsStyle)
      list.addFocusListener(repaintListener);
    updateListRowCount(list);
    getModel().addListDataListener(new ListDataListener()
    {

      public void intervalAdded(ListDataEvent listdataevent)
      {
        updateListRowCount(list);
      }

      public void intervalRemoved(ListDataEvent listdataevent)
      {
        updateListRowCount(list);
      }

      public void contentsChanged(ListDataEvent listdataevent)
      {
        if(isShowing())
          clearSelection();
        updateListRowCount(list);
      }
    });
    if(fileChooser.isMultiSelectionEnabled())
      list.setSelectionMode(2);
    else
      list.setSelectionMode(0);
    list.setModel(getModel());
    list.addListSelectionListener(createListSelectionListener());
    list.addMouseListener(getMouseHandler());
    getModel().addListDataListener(new ListDataListener()
    {

      public void contentsChanged(ListDataEvent listdataevent)
      {
        new DelayedSelectionUpdater();
      }

      public void intervalAdded(ListDataEvent listdataevent)
      {
        int i = listdataevent.getIndex0();
        int j = listdataevent.getIndex1();
        if(i == j)
        {
          FileInterface file = (FileInterface)getModel().getElementAt(i);
          if(file.equals(newFolderFile))
          {
            new DelayedSelectionUpdater(file);
            newFolderFile = null;
          }
        }
      }

      public void intervalRemoved(ListDataEvent listdataevent)
      {
      }

    });
    JScrollPane jscrollpane = new JScrollPane(list);
    if(listViewBackground != null)
      list.setBackground(listViewBackground);
    if(listViewBorder != null)
      jscrollpane.setBorder(listViewBorder);
    jpanel.add(jscrollpane, "Center");
    return jpanel;
  }

  public JPanel createDetailsView()
  {
    final JFileInterfaceChooser jfilechooser = getFileChooser();
    java.util.Locale locale = jfilechooser.getLocale();
    fileNameHeaderText = UIManager.getString("FileChooser.fileNameHeaderText", locale);
    fileSizeHeaderText = UIManager.getString("FileChooser.fileSizeHeaderText", locale);
    fileTypeHeaderText = UIManager.getString("FileChooser.fileTypeHeaderText", locale);
    fileDateHeaderText = UIManager.getString("FileChooser.fileDateHeaderText", locale);
    fileAttrHeaderText = UIManager.getString("FileChooser.fileAttrHeaderText", locale);
    JPanel jpanel = new JPanel(new BorderLayout());
    final DetailsTableModel final_tablemodel = new DetailsTableModel(jfilechooser);
    final JTable detailsTable = new JTable(final_tablemodel)
    {
      protected boolean processKeyBinding(KeyStroke keystroke, KeyEvent keyevent, int j, boolean flag)
      {
        if(keyevent.getKeyCode() == 27 && getCellEditor() == null)
        {
          jfilechooser.dispatchEvent(keyevent);
          return true;
        }
        else
        {
          return super.processKeyBinding(keystroke, keyevent, j, flag);
        }
      }

      public Component prepareRenderer(TableCellRenderer tablecellrenderer, int j, int k)
      {
        Component component = super.prepareRenderer(tablecellrenderer, j, k);
        if(component instanceof JLabel)
          if(convertColumnIndexToModel(k) == 1)
            ((JLabel)component).setHorizontalAlignment(4);
          else
            ((JLabel)component).setHorizontalAlignment(10);
        return component;
      }
    };
    detailsTable.setComponentOrientation(jfilechooser.getComponentOrientation());
    detailsTable.setAutoResizeMode(0);
    detailsTable.setShowGrid(false);
    detailsTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
    Font font = list.getFont();
    detailsTable.setFont(font);
    detailsTable.setIntercellSpacing(new Dimension(0, 0));
    TableColumnModel tablecolumnmodel = detailsTable.getColumnModel();
    TableColumn atablecolumn[] = new TableColumn[5];
    for(int i = 0; i < 5; i++)
    {
      atablecolumn[i] = tablecolumnmodel.getColumn(i);
      atablecolumn[i].setPreferredWidth(COLUMN_WIDTHS[i]);
    }

    String s = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
    if(s == null || !s.startsWith("Windows"))
    {
      tablecolumnmodel.removeColumn(atablecolumn[2]);
      tablecolumnmodel.removeColumn(atablecolumn[4]);
    }
    DetailsTableCellRenderer detailstablecellrenderer = new DetailsTableCellRenderer(jfilechooser);
    detailsTable.setDefaultRenderer(FileInterface.class, detailstablecellrenderer);
    detailsTable.setDefaultRenderer(Date.class, detailstablecellrenderer);
    detailsTable.setDefaultRenderer(Object.class, detailstablecellrenderer);
    detailsTable.getColumnModel().getSelectionModel().setSelectionMode(0);
    if(!readOnly)
    {
      final JTextField final_jtextfield = new JTextField();
      final_jtextfield.addFocusListener(editorFocusListener);
      atablecolumn[0].setCellEditor(new DefaultCellEditor(final_jtextfield)
      {

        public boolean isCellEditable(EventObject eventobject)
        {
          if(/*!SwingUtilities2.DRAG_FIX &&*/ (eventobject instanceof MouseEvent))
          {
            MouseEvent mouseevent = (MouseEvent)eventobject;
            int j = detailsTable.rowAtPoint(mouseevent.getPoint());
            return mouseevent.getClickCount() == 1 && detailsTable.isRowSelected(j);
          }
          else
          {
            return super.isCellEditable(eventobject);
          }
        }

        public Component getTableCellEditorComponent(JTable jtable, Object obj, boolean flag, int j, int k)
        {
          Component component = super.getTableCellEditorComponent(jtable, obj, flag, j, k);
          if(obj instanceof FileInterface)
          {
            final_jtextfield.setText(jfilechooser.getName((FileInterface)obj));
            if(/*!SwingUtilities2.DRAG_FIX*/ true)
              final_jtextfield.requestFocus();
            final_jtextfield.selectAll();
          }
          return component;
        }
      });
    }
    detailsTable.addMouseListener(getMouseHandler());
    detailsTable.putClientProperty("Table.isFileList", Boolean.TRUE);
    if(listViewWindowsStyle)
      detailsTable.addFocusListener(repaintListener);
    ActionMap actionmap = SwingUtilities.getUIActionMap(detailsTable);
    actionmap.remove("selectNextRowCell");
    actionmap.remove("selectPreviousRowCell");
    actionmap.remove("selectNextColumnCell");
    actionmap.remove("selectPreviousColumnCell");
    detailsTable.setFocusTraversalKeys(0, null);
    detailsTable.setFocusTraversalKeys(1, null);
    JScrollPane jscrollpane = new JScrollPane(detailsTable);
    jscrollpane.setComponentOrientation(jfilechooser.getComponentOrientation());
    LookAndFeel.installColors(jscrollpane.getViewport(), "Table.background", "Table.foreground");
    jscrollpane.addComponentListener(new ComponentAdapter()
    {

      public void componentResized(ComponentEvent componentevent)
      {
        JScrollPane jscrollpane1 = (JScrollPane)componentevent.getComponent();
        fixNameColumnWidth(jscrollpane1.getViewport().getSize().width);
        jscrollpane1.removeComponentListener(this);
      }

    });
    jscrollpane.addMouseListener(new MouseAdapter()
    {

      public void mousePressed(MouseEvent mouseevent)
      {
        JScrollPane jscrollpane1 = (JScrollPane)mouseevent.getComponent();
        JTable jtable = (JTable)jscrollpane1.getViewport().getView();
        if(!mouseevent.isShiftDown() || jtable.getSelectionModel().getSelectionMode() == 0)
        {
          clearSelection();
          TableCellEditor tablecelleditor = jtable.getCellEditor();
          if(tablecelleditor != null)
            tablecelleditor.stopCellEditing();
        }
      }

    });
    if(listViewBackground != null)
      detailsTable.setBackground(listViewBackground);
    if(listViewBorder != null)
      jscrollpane.setBorder(listViewBorder);
    jpanel.add(jscrollpane, "Center");
    return jpanel;
  }

  private void fixNameColumnWidth(int i)
  {
    TableColumn tablecolumn = detailsTable.getColumnModel().getColumn(0);
    int j = detailsTable.getPreferredSize().width;
    if(j < i)
      tablecolumn.setPreferredWidth((tablecolumn.getPreferredWidth() + i) - j);
  }

  public ListSelectionListener createListSelectionListener()
  {
    return fileChooserUIAccessor.createListSelectionListener();
  }

  private int getEditIndex()
  {
    return lastIndex;
  }

  private void setEditIndex(int i)
  {
    lastIndex = i;
  }

  private void resetEditIndex()
  {
    lastIndex = -1;
  }

  private void cancelEdit()
  {
    if(editFile != null)
    {
      editFile = null;
      list.remove(editCell);
      repaint();
    }
    else if(detailsTable != null && detailsTable.isEditing())
      detailsTable.getCellEditor().cancelCellEditing();
  }

  private void editFileName(int i)
  {
    FileInterface file = getFileChooser().getCurrentDirectory();
    if(readOnly || !canWrite(file))
      return;
    ensureIndexIsVisible(i);
    switch(viewType)
    {
      case 0: // '\0'
        editFile = (FileInterface)getModel().getElementAt(i);
        Rectangle rectangle = list.getCellBounds(i, i);
        if(editCell == null)
        {
          editCell = new JTextField();
          editCell.addActionListener(new EditActionListener());
          editCell.addFocusListener(editorFocusListener);
          editCell.setNextFocusableComponent(list);
        }
        list.add(editCell);
        editCell.setText(getFileChooser().getName(editFile));
        ComponentOrientation componentorientation = list.getComponentOrientation();
        editCell.setComponentOrientation(componentorientation);
        if(componentorientation.isLeftToRight())
          editCell.setBounds(editX + rectangle.x, rectangle.y, rectangle.width - editX, rectangle.height);
        else
          editCell.setBounds(rectangle.x, rectangle.y, rectangle.width - editX, rectangle.height);
        editCell.requestFocus();
        editCell.selectAll();
        break;

      case 1: // '\001'
        detailsTable.editCellAt(i, 0);
        break;
    }
  }

  private void applyEdit()
  {
    if(editFile != null && editFile.exists())
    {
      JFileInterfaceChooser jfilechooser = getFileChooser();
      String s = jfilechooser.getName(editFile);
      String s1 = editFile.getName();
      String s2 = editCell.getText().trim();
      if(!s2.equals(s))
      {
        String s3 = s2;
        int i = s1.length();
        int j = s.length();
        if(i > j && s1.charAt(j) == '.')
          s3 = (new StringBuilder()).append(s2).append(s1.substring(j)).toString();
        GenericFileInterfaceSystemView filesystemview = jfilechooser.getFileSystemView();
        FileInterface file = filesystemview.createFileObject(editFile.getParentFileInterface(), s3);
        if(!file.exists() && getModel().renameFile(editFile, file) && filesystemview.isParent(jfilechooser.getCurrentDirectory(), file))
          if(jfilechooser.isMultiSelectionEnabled())
            jfilechooser.setSelectedFiles(new FileInterface[] { file });
          else
            jfilechooser.setSelectedFile(file);
      }
    }
    if(detailsTable != null && detailsTable.isEditing())
      detailsTable.getCellEditor().stopCellEditing();
    cancelEdit();
  }

  public Action getNewFolderAction()
  {
    if(!readOnly && newFolderAction == null)
      newFolderAction = new AbstractAction(newFolderActionLabelText)
      {

        public void actionPerformed(ActionEvent actionevent)
        {
          if(basicNewFolderAction == null)
            basicNewFolderAction = fileChooserUIAccessor.getNewFolderAction();
          JFileInterfaceChooser jfilechooser = getFileChooser();
          FileInterface file = jfilechooser.getSelectedFile();
          basicNewFolderAction.actionPerformed(actionevent);
          FileInterface file1 = jfilechooser.getSelectedFile();
          if(file1 != null && !file1.equals(file) && file1.isDirectory())
            newFolderFile = file1;
        }

        private Action basicNewFolderAction;

        {
          putValue("ActionCommandKey", "New Folder");
          FileInterface file = getFileChooser().getCurrentDirectory();
          if(file != null)
            setEnabled(FilePane.canWrite(file));
        }
      };
    return newFolderAction;
  }

  void setFileSelected()
  {
    if(getFileChooser().isMultiSelectionEnabled() && !isDirectorySelected())
    {
      FileInterface[] files = getFileChooser().getSelectedFiles(); // Should be selected
      Object[] selectedObjects = list.getSelectedValues(); // Are actually selected

      listSelectionModel.setValueIsAdjusting(true);
      try
      {
        int lead = listSelectionModel.getLeadSelectionIndex();
        int anchor = listSelectionModel.getAnchorSelectionIndex();

        Arrays.sort(files);
        Arrays.sort(selectedObjects);

        int shouldIndex = 0;
        int actuallyIndex = 0;

        // Remove files that shouldn't be selected and add files which should be selected
        // Note: Assume files are already sorted in compareTo order.
        while(shouldIndex < files.length && actuallyIndex < selectedObjects.length)
        {
          int comparison = files[shouldIndex].compareTo((FileInterface)selectedObjects[actuallyIndex]);
          if(comparison < 0)
          {
            doSelectFile(files[shouldIndex++]);
          }
          else if(comparison > 0)
          {
            doDeselectFile(selectedObjects[actuallyIndex++]);
          }
          else
          {
            // Do nothing
            shouldIndex++;
            actuallyIndex++;
          }

        }

        while(shouldIndex < files.length)
        {
          doSelectFile(files[shouldIndex++]);
        }

        while(actuallyIndex < selectedObjects.length)
        {
          doDeselectFile(selectedObjects[actuallyIndex++]);
        }

        // restore the anchor and lead
        if(listSelectionModel instanceof DefaultListSelectionModel)
        {
          ((DefaultListSelectionModel)listSelectionModel).moveLeadSelectionIndex(lead);
          listSelectionModel.setAnchorSelectionIndex(anchor);
        }
      }
      finally
      {
        listSelectionModel.setValueIsAdjusting(false);
      }
    }
    else
    {
      JFileInterfaceChooser chooser = getFileChooser();
      FileInterface f;
      if(isDirectorySelected())
      {
        f = getDirectory();
      }
      else
      {
        f = chooser.getSelectedFile();
      }
      int i;
      if(f != null && (i = getModel().indexOf(f)) >= 0)
      {
        listSelectionModel.setSelectionInterval(i, i);
        ensureIndexIsVisible(i);
      }
      else
      {
        clearSelection();
      }
    }
  }

  private void doSelectFile(FileInterface fileToSelect)
  {
    int index = getModel().indexOf(fileToSelect);
    // could be missed in the current directory if it changed
    if(index >= 0)
    {
      listSelectionModel.addSelectionInterval(index, index);
    }
  }

  private void doDeselectFile(Object fileToDeselect)
  {
    int index = getModel().indexOf(fileToDeselect);
    listSelectionModel.removeSelectionInterval(index, index);
  }

  private void doSelectedFileChanged(PropertyChangeEvent propertychangeevent)
  {
    applyEdit();
    FileInterface file = (FileInterface)propertychangeevent.getNewValue();
    JFileInterfaceChooser jfilechooser = getFileChooser();
    if(file != null && (jfilechooser.isFileSelectionEnabled() && !file.isDirectory() || file.isDirectory() && jfilechooser.isDirectorySelectionEnabled()))
      setFileSelected();
  }

  private void doSelectedFilesChanged(PropertyChangeEvent propertychangeevent)
  {
    applyEdit();
    FileInterface afile[] = (FileInterface[])(FileInterface[])propertychangeevent.getNewValue();
    JFileInterfaceChooser jfilechooser = getFileChooser();
    if(afile != null && afile.length > 0 && (afile.length > 1 || jfilechooser.isDirectorySelectionEnabled() || !afile[0].isDirectory()))
      setFileSelected();
  }

  private void doDirectoryChanged(PropertyChangeEvent propertychangeevent)
  {
    JFileInterfaceChooser jfilechooser = getFileChooser();
    GenericFileInterfaceSystemView filesystemview = jfilechooser.getFileSystemView();
    applyEdit();
    resetEditIndex();
    ensureIndexIsVisible(0);
    FileInterface file = jfilechooser.getCurrentDirectory();
    if(file != null)
    {
      if(!readOnly)
        getNewFolderAction().setEnabled(canWrite(file));
      fileChooserUIAccessor.getChangeToParentDirectoryAction().setEnabled(!filesystemview.isRoot(file));
    }
  }

  private void doFilterChanged(PropertyChangeEvent propertychangeevent)
  {
    applyEdit();
    resetEditIndex();
    clearSelection();
  }

  private void doFileSelectionModeChanged(PropertyChangeEvent propertychangeevent)
  {
    applyEdit();
    resetEditIndex();
    clearSelection();
  }

  private void doMultiSelectionChanged(PropertyChangeEvent propertychangeevent)
  {
    if(getFileChooser().isMultiSelectionEnabled())
    {
      listSelectionModel.setSelectionMode(2);
    }
    else
    {
      listSelectionModel.setSelectionMode(0);
      clearSelection();
      getFileChooser().setSelectedFiles(null);
    }
  }

  public void propertyChange(PropertyChangeEvent propertychangeevent)
  {
    if(viewType == -1)
      setViewType(0);
    String s = propertychangeevent.getPropertyName();
    if(s.equals("SelectedFileChangedProperty"))
      doSelectedFileChanged(propertychangeevent);
    else if(s.equals("SelectedFilesChangedProperty"))
      doSelectedFilesChanged(propertychangeevent);
    else if(s.equals("directoryChanged"))
      doDirectoryChanged(propertychangeevent);
    else if(s.equals("fileFilterChanged"))
      doFilterChanged(propertychangeevent);
    else if(s.equals("fileSelectionChanged"))
      doFileSelectionModeChanged(propertychangeevent);
    else if(s.equals("MultiSelectionEnabledChangedProperty"))
      doMultiSelectionChanged(propertychangeevent);
    else if(s.equals("CancelSelection"))
      applyEdit();
    else if(s.equals("componentOrientation"))
    {
      ComponentOrientation componentorientation = (ComponentOrientation)propertychangeevent.getNewValue();
      JFileInterfaceChooser jfilechooser = (JFileInterfaceChooser)propertychangeevent.getSource();
      if(componentorientation != (ComponentOrientation)propertychangeevent.getOldValue())
        jfilechooser.applyComponentOrientation(componentorientation);
      if(detailsTable != null)
      {
        detailsTable.setComponentOrientation(componentorientation);
        detailsTable.getParent().getParent().setComponentOrientation(componentorientation);
      }
    }
  }

  private void ensureIndexIsVisible(int i)
  {
    if(i >= 0)
    {
      if(list != null)
        list.ensureIndexIsVisible(i);
      if(detailsTable != null)
        detailsTable.scrollRectToVisible(detailsTable.getCellRect(i, 0, true));
    }
  }

  public void ensureFileIsVisible(JFileInterfaceChooser jfilechooser, FileInterface file)
  {
    ensureIndexIsVisible(getModel().indexOf(file));
  }

  public void rescanCurrentDirectory()
  {
    getModel().validateFileCache();
  }

  public void clearSelection()
  {
    if(listSelectionModel != null)
    {
      listSelectionModel.clearSelection();
      if(listSelectionModel instanceof DefaultListSelectionModel)
      {
        ((DefaultListSelectionModel)listSelectionModel).moveLeadSelectionIndex(0);
        ((DefaultListSelectionModel)listSelectionModel).setAnchorSelectionIndex(0);
      }
    }
  }

  public JMenu getViewMenu()
  {
    if(viewMenu == null)
    {
      viewMenu = new JMenu(viewMenuLabelText);
      ButtonGroup buttongroup = new ButtonGroup();
      for(int i = 0; i < 2; i++)
      {
        JRadioButtonMenuItem jradiobuttonmenuitem = new JRadioButtonMenuItem(new ViewTypeAction(i));
        buttongroup.add(jradiobuttonmenuitem);
        viewMenu.add(jradiobuttonmenuitem);
      }

      updateViewMenu();
    }
    return viewMenu;
  }

  private void updateViewMenu()
  {
    if(viewMenu != null)
    {
      Component acomponent[] = viewMenu.getMenuComponents();
      for(int i = 0; i < acomponent.length; i++)
      {
        if(!(acomponent[i] instanceof JRadioButtonMenuItem))
          continue;
        JRadioButtonMenuItem jradiobuttonmenuitem = (JRadioButtonMenuItem)acomponent[i];
        if(((ViewTypeAction)jradiobuttonmenuitem.getAction()).viewType == viewType)
          jradiobuttonmenuitem.setSelected(true);
      }

    }
  }

  public JPopupMenu getComponentPopupMenu()
  {
    JPopupMenu jpopupmenu = getFileChooser().getComponentPopupMenu();
    if(jpopupmenu != null)
      return jpopupmenu;
    JMenu jmenu = getViewMenu();
    if(contextMenu == null)
    {
      contextMenu = new JPopupMenu();
      if(jmenu != null)
      {
        contextMenu.add(jmenu);
        if(listViewWindowsStyle)
          contextMenu.addSeparator();
      }
      ActionMap actionmap = getActionMap();
      Action action = actionmap.get("refresh");
      Action action1 = actionmap.get("New Folder");
      if(action != null)
      {
        contextMenu.add(action);
        if(listViewWindowsStyle && action1 != null)
          contextMenu.addSeparator();
      }
      if(action1 != null)
        contextMenu.add(action1);
    }
    if(jmenu != null)
      jmenu.getPopupMenu().setInvoker(jmenu);
    return contextMenu;
  }

  protected Handler getMouseHandler()
  {
    if(handler == null)
      handler = new Handler();
    return handler;
  }

  protected boolean isDirectorySelected()
  {
    return fileChooserUIAccessor.isDirectorySelected();
  }

  protected FileInterface getDirectory()
  {
    return fileChooserUIAccessor.getDirectory();
  }

  private Component findChildComponent(Container container, Class class1)
  {
    int i = container.getComponentCount();
    for(int j = 0; j < i; j++)
    {
      Component component = container.getComponent(j);
      if(class1.isInstance(component))
        return component;
      if(!(component instanceof Container))
        continue;
      Component component1 = findChildComponent((Container)component, class1);
      if(component1 != null)
        return component1;
    }

    return null;
  }

  public static boolean canWrite(FileInterface file)
  {
    boolean flag = false;
    if(file != null)
      try
      {
        flag = file.canWrite();
      }
      catch(AccessControlException accesscontrolexception)
      {
        flag = false;
      }
    return flag;
  }

}

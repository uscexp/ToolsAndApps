/* *****************************************************************
 * Project: common
 * FileInterface:    SynthFileChooserUIImpl.java
 * 
 * Creation:     06.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.swing.plaf.synth;

import haui.awt.shell.ShellFolder;
import haui.components.GenericFileInterfaceSystemView;
import haui.components.JFileInterfaceChooser;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.swing.FilePane;
import haui.swing.plaf.basic.BasicDirectoryModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;

/**
 * SynthFileChooserUIImpl
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public class SynthFileChooserUIImpl extends SynthFileChooserUI
{
  private class AlignedLabel extends JLabel
  {

    public Dimension getPreferredSize()
    {
      Dimension dimension = super.getPreferredSize();
      return new Dimension(getMaxWidth() + 11, dimension.height);
    }

    private int getMaxWidth()
    {
      if(maxWidth == 0 && group != null)
      {
        int i = 0;
        for(int j = 0; j < group.length; j++)
          i = Math.max(group[j].getSuperPreferredWidth(), i);

        for(int k = 0; k < group.length; k++)
          group[k].maxWidth = i;

      }
      return maxWidth;
    }

    private int getSuperPreferredWidth()
    {
      return super.getPreferredSize().width;
    }

    private AlignedLabel group[];
    private int maxWidth;

    AlignedLabel(String s)
    {
      super(s);
      maxWidth = 0;
      setAlignmentX(0.0F);
    }
  }

  private static class ButtonAreaLayout implements LayoutManager
  {

    public void addLayoutComponent(String s, Component component)
    {
    }

    public void layoutContainer(Container container)
    {
      Component acomponent[] = container.getComponents();
      if(acomponent != null && acomponent.length > 0)
      {
        int i = acomponent.length;
        Dimension adimension[] = new Dimension[i];
        Insets insets = container.getInsets();
        int j = insets.top + topMargin;
        int k = 0;
        for(int l = 0; l < i; l++)
        {
          adimension[l] = acomponent[l].getPreferredSize();
          k = Math.max(k, adimension[l].width);
        }

        int i1;
        int j1;
        if(container.getComponentOrientation().isLeftToRight())
        {
          i1 = container.getSize().width - insets.left - k;
          j1 = hGap + k;
        }
        else
        {
          i1 = insets.left;
          j1 = -(hGap + k);
        }
        for(int k1 = i - 1; k1 >= 0; k1--)
        {
          acomponent[k1].setBounds(i1, j, k, adimension[k1].height);
          i1 -= j1;
        }

      }
    }

    public Dimension minimumLayoutSize(Container container)
    {
      if(container != null)
      {
        Component acomponent[] = container.getComponents();
        if(acomponent != null && acomponent.length > 0)
        {
          int i = acomponent.length;
          int j = 0;
          Insets insets = container.getInsets();
          int k = topMargin + insets.top + insets.bottom;
          int l = insets.left + insets.right;
          int i1 = 0;
          for(int j1 = 0; j1 < i; j1++)
          {
            Dimension dimension = acomponent[j1].getPreferredSize();
            j = Math.max(j, dimension.height);
            i1 = Math.max(i1, dimension.width);
          }

          return new Dimension(l + i * i1 + (i - 1) * hGap, k + j);
        }
      }
      return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container container)
    {
      return minimumLayoutSize(container);
    }

    public void removeLayoutComponent(Component component)
    {
    }

    private int hGap;
    private int topMargin;

    private ButtonAreaLayout()
    {
      hGap = 5;
      topMargin = 17;
    }

  }

  protected class DirectoryComboBoxAction extends AbstractAction
  {

    public void actionPerformed(ActionEvent actionevent)
    {
      directoryComboBox.hidePopup();
      JComponent jcomponent = getDirectoryComboBox();
      if(jcomponent instanceof JComboBox)
      {
        FileInterface file = (FileInterface)((JComboBox)jcomponent).getSelectedItem();
        getFileChooser().setCurrentDirectory(file);
      }
    }

    protected DirectoryComboBoxAction()
    {
      super("DirectoryComboBoxAction");
    }
  }

  protected class DirectoryComboBoxModel extends AbstractListModel implements ComboBoxModel
  {

    public void addItem(FileInterface file)
    {
      if(file == null)
        return;
      int i = directories.size();
      directories.clear();
      if(i > 0)
        fireIntervalRemoved(this, 0, i);
      FileInterface afile[];
      if(useShellFolder)
        afile = (FileInterface[])(FileInterface[])ShellFolder.get("fileChooserComboBoxFolders", getFileChooser().getFileInterfaceConfiguration());
      else
        afile = fsv.getRoots();
      directories.addAll(Arrays.asList(afile));
      FileInterface file1 = null;
      try
      {
        file1 = file.getCanonicalFile();
      }
      catch(IOException ioexception)
      {
        file1 = file;
      }
      try
      {
        Object obj = useShellFolder ? ((Object)(ShellFolder.getShellFolder(file1))) : ((Object)(file1));
        Object obj1 = obj;
        Vector vector = new Vector(10);
        do
          vector.addElement(obj1);
        while((obj1 = ((FileInterface)(obj1)).getParentFileInterface()) != null);
        int j = vector.size();
        int k = 0;
        do
        {
          if(k >= j)
            break;
          FileInterface file2 = (FileInterface)vector.get(k);
          if(directories.contains(file2))
          {
            int l = directories.indexOf(file2);
            for(int i1 = k - 1; i1 >= 0; i1--)
              directories.insertElementAt(vector.get(i1), (l + k) - i1);

            break;
          }
          k++;
        } while(true);
        calculateDepths();
        setSelectedItem(obj);
      }
      catch(FileNotFoundException filenotfoundexception)
      {
        calculateDepths();
      }
    }

    private void calculateDepths()
    {
      depths = new int[directories.size()];
      label0: for(int i = 0; i < depths.length; i++)
      {
        FileInterface file = (FileInterface)directories.get(i);
        FileInterface file1 = file.getParentFileInterface();
        depths[i] = 0;
        if(file1 == null)
          continue;
        int j = i - 1;
        do
        {
          if(j < 0)
            continue label0;
          if(file1.equals((FileInterface)directories.get(j)))
          {
            depths[i] = depths[j] + 1;
            continue label0;
          }
          j--;
        } while(true);
      }

    }

    public int getDepth(int i)
    {
      return depths == null || i < 0 || i >= depths.length ? 0 : depths[i];
    }

    public void setSelectedItem(Object obj)
    {
      selectedDirectory = (FileInterface)obj;
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

    public Object getElementAt(int i)
    {
      return directories.elementAt(i);
    }

    Vector directories;
    int depths[];
    FileInterface selectedDirectory;
    JFileInterfaceChooser chooser;
    GenericFileInterfaceSystemView fsv;

    public DirectoryComboBoxModel()
    {
      super();
      directories = new Vector();
      depths = null;
      selectedDirectory = null;
      chooser = getFileChooser();
      fsv = chooser.getFileSystemView();
      FileInterface file = getFileChooser().getCurrentDirectory();
      if(file != null)
        addItem(file);
    }
  }

  class DirectoryComboBoxRenderer extends DefaultListCellRenderer
  {

    public Component getListCellRendererComponent(JList jlist, Object obj, int i, boolean flag, boolean flag1)
    {
      super.getListCellRendererComponent(jlist, obj, i, flag, flag1);
      if(obj == null)
      {
        setText("");
        return this;
      }
      else
      {
        FileInterface file = (FileInterface)obj;
        setText(getFileChooser().getName(file));
        Icon icon = getFileChooser().getIcon(file);
        ii.icon = icon;
        ii.depth = directoryComboBoxModel.getDepth(i);
        setIcon(ii);
        return this;
      }
    }

    IndentIcon ii;

    DirectoryComboBoxRenderer()
    {
      super();
      ii = new IndentIcon();
    }
  }

  protected class FilterComboBoxModel extends AbstractListModel implements ComboBoxModel, PropertyChangeListener
  {

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
      String s = propertychangeevent.getPropertyName();
      if(s == "ChoosableFileFilterChangedProperty")
      {
        filters = (FileInterfaceFilter[])(FileInterfaceFilter[])propertychangeevent.getNewValue();
        fireContentsChanged(this, -1, -1);
      }
      else if(s == "fileFilterChanged")
        fireContentsChanged(this, -1, -1);
    }

    public void setSelectedItem(Object obj)
    {
      if(obj != null)
      {
        getFileChooser().setFileFilter((FileInterfaceFilter)obj);
        setFileName(null);
        fireContentsChanged(this, -1, -1);
      }
    }

    public Object getSelectedItem()
    {
      FileInterfaceFilter filefilter = getFileChooser().getFileFilter();
      boolean flag = false;
      if(filefilter != null)
      {
        for(int i = 0; i < filters.length; i++)
          if(filters[i] == filefilter)
            flag = true;

        if(!flag)
          getFileChooser().addChoosableFileFilter(filefilter);
      }
      return getFileChooser().getFileFilter();
    }

    public int getSize()
    {
      if(filters != null)
        return filters.length;
      else
        return 0;
    }

    public Object getElementAt(int i)
    {
      if(i > getSize() - 1)
        return getFileChooser().getFileFilter();
      if(filters != null)
        return filters[i];
      else
        return null;
    }

    protected FileInterfaceFilter filters[];

    protected FilterComboBoxModel()
    {
      super();
      filters = getFileChooser().getChoosableFileFilters();
    }
  }

  public class FilterComboBoxRenderer extends DefaultListCellRenderer
  {

    public Component getListCellRendererComponent(JList jlist, Object obj, int i, boolean flag, boolean flag1)
    {
      super.getListCellRendererComponent(jlist, obj, i, flag, flag1);
      if(obj != null && (obj instanceof FileInterfaceFilter))
        setText(((FileInterfaceFilter)obj).getDescription());
      return this;
    }

    public FilterComboBoxRenderer()
    {
      super();
    }
  }

  class IndentIcon implements Icon
  {

    public void paintIcon(Component component, Graphics g, int i, int j)
    {
      if(icon != null)
        if(component.getComponentOrientation().isLeftToRight())
          icon.paintIcon(component, g, i + depth * 10, j);
        else
          icon.paintIcon(component, g, i, j);
    }

    public int getIconWidth()
    {
      return (icon == null ? 0 : icon.getIconWidth()) + depth * 10;
    }

    public int getIconHeight()
    {
      return icon == null ? 0 : icon.getIconHeight();
    }

    Icon icon;
    int depth;

    IndentIcon()
    {
      super();
      icon = null;
      depth = 0;
    }
  }

  private class SynthFileChooserUIAccessor implements haui.swing.FilePane.FileChooserUIAccessor
  {

    public JFileInterfaceChooser getFileChooser()
    {
      return SynthFileChooserUIImpl.this.getFileChooser();
    }

    public BasicDirectoryModel getModel()
    {
      return SynthFileChooserUIImpl.this.getModel();
    }

    public JPanel createList()
    {
      return null;
    }

    public JPanel createDetailsView()
    {
      return null;
    }

    public boolean isDirectorySelected()
    {
      return SynthFileChooserUIImpl.this.isDirectorySelected();
    }

    public FileInterface getDirectory()
    {
      return SynthFileChooserUIImpl.this.getDirectory();
    }

    public Action getChangeToParentDirectoryAction()
    {
      return SynthFileChooserUIImpl.this.getChangeToParentDirectoryAction();
    }

    public Action getApproveSelectionAction()
    {
      return SynthFileChooserUIImpl.this.getApproveSelectionAction();
    }

    public Action getNewFolderAction()
    {
      return SynthFileChooserUIImpl.this.getNewFolderAction();
    }

    public MouseListener createDoubleClickListener(JList jlist)
    {
      return SynthFileChooserUIImpl.this.createDoubleClickListener(getFileChooser(), jlist);
    }

    public ListSelectionListener createListSelectionListener()
    {
      return SynthFileChooserUIImpl.this.createListSelectionListener(getFileChooser());
    }

    private SynthFileChooserUIAccessor()
    {
      super();
    }

  }

  public SynthFileChooserUIImpl(JFileInterfaceChooser jfilechooser)
  {
    super(jfilechooser);
    directoryComboBoxAction = new DirectoryComboBoxAction();
    lookInLabelMnemonic = 0;
    lookInLabelText = null;
    saveInLabelText = null;
    fileNameLabelMnemonic = 0;
    fileNameLabelText = null;
    filesOfTypeLabelMnemonic = 0;
    filesOfTypeLabelText = null;
  }

  public void installComponents(JFileInterfaceChooser jfilechooser)
  {
    super.installComponents(jfilechooser);
    javax.swing.plaf.synth.SynthContext synthcontext = getContext(jfilechooser, 1);
    updateUseShellFolder();
    jfilechooser.setLayout(new BorderLayout(0, 11));
    JPanel jpanel = new JPanel(new BorderLayout(11, 0));
    jfilechooser.add(jpanel, "North");
    lookInLabel = new JLabel(lookInLabelText);
    lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
    jpanel.add(lookInLabel, "Before");
    directoryComboBox = new JComboBox();
    directoryComboBox.getAccessibleContext().setAccessibleDescription(lookInLabelText);
    directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
    lookInLabel.setLabelFor(directoryComboBox);
    directoryComboBoxModel = createDirectoryComboBoxModel(jfilechooser);
    directoryComboBox.setModel(directoryComboBoxModel);
    directoryComboBox.addActionListener(directoryComboBoxAction);
    directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(jfilechooser));
    directoryComboBox.setAlignmentX(0.0F);
    directoryComboBox.setAlignmentY(0.0F);
    directoryComboBox.setMaximumRowCount(8);
    jpanel.add(directoryComboBox, "Center");
    filePane = new FilePane(new SynthFileChooserUIAccessor());
    jfilechooser.addPropertyChangeListener(filePane);
    JPopupMenu jpopupmenu = filePane.getComponentPopupMenu();
    if(jpopupmenu != null)
    {
      jpopupmenu.insert(getChangeToParentDirectoryAction(), 0);
      if(FileConnector.createTemplateFileInterface(jfilechooser.getFileInterfaceConfiguration()).separatorChar() == '/')
        jpopupmenu.insert(getGoHomeAction(), 1);
    }
    jfilechooser.add(getAccessoryPanel(), "After");
    JComponent jcomponent = jfilechooser.getAccessory();
    if(jcomponent != null)
    {
      getAccessoryPanel().add(jcomponent);
      System.out.println("added accessory");
    }
    filePane.setPreferredSize(LIST_PREF_SIZE);
    jfilechooser.add(filePane, "Center");
    bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, 1));
    jfilechooser.add(bottomPanel, "South");
    JPanel jpanel1 = new JPanel();
    jpanel1.setLayout(new BoxLayout(jpanel1, 2));
    bottomPanel.add(jpanel1);
    bottomPanel.add(Box.createRigidArea(new Dimension(1, 5)));
    AlignedLabel alignedlabel = new AlignedLabel(fileNameLabelText);
    alignedlabel.setDisplayedMnemonic(fileNameLabelMnemonic);
    jpanel1.add(alignedlabel);
    fileNameTextField = new JTextField(35)
    {

      public Dimension getMaximumSize()
      {
        return new Dimension(32767, super.getPreferredSize().height);
      }

    };
    jpanel1.add(fileNameTextField);
    alignedlabel.setLabelFor(fileNameTextField);
    fileNameTextField.addFocusListener(new FocusAdapter()
    {

      public void focusGained(FocusEvent focusevent)
      {
        if(!getFileChooser().isMultiSelectionEnabled())
          filePane.clearSelection();
      }

    });
    if(jfilechooser.isMultiSelectionEnabled())
      setFileName(fileNameString(jfilechooser.getSelectedFiles()));
    else
      setFileName(fileNameString(jfilechooser.getSelectedFile()));
    JPanel jpanel2 = new JPanel();
    jpanel2.setLayout(new BoxLayout(jpanel2, 2));
    bottomPanel.add(jpanel2);
    AlignedLabel alignedlabel1 = new AlignedLabel(filesOfTypeLabelText);
    alignedlabel1.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
    jpanel2.add(alignedlabel1);
    filterComboBoxModel = createFilterComboBoxModel();
    jfilechooser.addPropertyChangeListener(filterComboBoxModel);
    filterComboBox = new JComboBox(filterComboBoxModel);
    filterComboBox.getAccessibleContext().setAccessibleDescription(filesOfTypeLabelText);
    alignedlabel1.setLabelFor(filterComboBox);
    filterComboBox.setRenderer(createFilterComboBoxRenderer());
    jpanel2.add(filterComboBox);
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new ButtonAreaLayout());
    buttonPanel.add(getApproveButton(jfilechooser));
    buttonPanel.add(getCancelButton(jfilechooser));
    if(jfilechooser.getControlButtonsAreShown())
      addControlButtons();
    groupLabels(new AlignedLabel[] { alignedlabel, alignedlabel1 });
  }

  private void updateUseShellFolder()
  {
    JFileInterfaceChooser jfilechooser = getFileChooser();
    Boolean boolean1 = (Boolean)jfilechooser.getClientProperty("FileChooser.useShellFolder");
    if(boolean1 != null)
    {
      useShellFolder = boolean1.booleanValue();
    }
    else
    {
      useShellFolder = false;
      FileInterface afile[] = jfilechooser.getFileSystemView().getRoots();
      if(afile != null && afile.length == 1)
      {
        FileInterface afile1[] = (FileInterface[])(FileInterface[])ShellFolder.get("fileChooserComboBoxFolders", jfilechooser.getFileInterfaceConfiguration());
        if(afile1 != null && afile1.length > 0 && afile[0] == afile1[0])
          useShellFolder = true;
      }
    }
  }

  private String fileNameString(FileInterface file)
  {
    if(file == null)
      return null;
    JFileInterfaceChooser jfilechooser = getFileChooser();
    if(jfilechooser.isDirectorySelectionEnabled() && !jfilechooser.isFileSelectionEnabled())
      return file.getPath();
    else
      return file.getName();
  }

  private String fileNameString(FileInterface afile[])
  {
    StringBuffer stringbuffer = new StringBuffer();
    for(int i = 0; afile != null && i < afile.length; i++)
    {
      if(i > 0)
        stringbuffer.append(" ");
      if(afile.length > 1)
        stringbuffer.append("\"");
      stringbuffer.append(fileNameString(afile[i]));
      if(afile.length > 1)
        stringbuffer.append("\"");
    }

    return stringbuffer.toString();
  }

  public void uninstallUI(JComponent jcomponent)
  {
    jcomponent.removePropertyChangeListener(filterComboBoxModel);
    jcomponent.removePropertyChangeListener(filePane);
    super.uninstallUI(jcomponent);
  }

  protected void installStrings(JFileInterfaceChooser jfilechooser)
  {
    super.installStrings(jfilechooser);
    Locale locale = jfilechooser.getLocale();
    lookInLabelMnemonic = getMnemonic("FileChooser.lookInLabelMnemonic", locale);
    lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", locale);
    saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", locale);
    fileNameLabelMnemonic = getMnemonic("FileChooser.fileNameLabelMnemonic", locale);
    fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", locale);
    filesOfTypeLabelMnemonic = getMnemonic("FileChooser.filesOfTypeLabelMnemonic", locale);
    filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", locale);
  }

  private int getMnemonic(String s, Locale locale)
  {
    String s1 = (String)UIManager.get(s, locale);
    if(s1 != null)
      try
      {
        return Integer.parseInt(s1);
      }
      catch(NumberFormatException numberformatexception)
      {}
    return 0;
  }

  public String getFileName()
  {
    if(fileNameTextField != null)
      return fileNameTextField.getText();
    else
      return null;
  }

  public void setFileName(String s)
  {
    if(fileNameTextField != null)
      fileNameTextField.setText(s);
  }

  protected void doSelectedFileChanged(PropertyChangeEvent propertychangeevent)
  {
    super.doSelectedFileChanged(propertychangeevent);
    FileInterface file = (FileInterface)propertychangeevent.getNewValue();
    JFileInterfaceChooser jfilechooser = getFileChooser();
    if(file != null && (jfilechooser.isFileSelectionEnabled() && !file.isDirectory() || file.isDirectory() && jfilechooser.isDirectorySelectionEnabled()))
      setFileName(fileNameString(file));
  }

  protected void doSelectedFilesChanged(PropertyChangeEvent propertychangeevent)
  {
    super.doSelectedFilesChanged(propertychangeevent);
    FileInterface afile[] = (FileInterface[])(FileInterface[])propertychangeevent.getNewValue();
    JFileInterfaceChooser jfilechooser = getFileChooser();
    if(afile != null && afile.length > 0 && (afile.length > 1 || jfilechooser.isDirectorySelectionEnabled() || !afile[0].isDirectory()))
      setFileName(fileNameString(afile));
  }

  protected void doDirectoryChanged(PropertyChangeEvent propertychangeevent)
  {
    super.doDirectoryChanged(propertychangeevent);
    JFileInterfaceChooser jfilechooser = getFileChooser();
    GenericFileInterfaceSystemView filesystemview = jfilechooser.getFileSystemView();
    FileInterface file = getFileChooser().getCurrentDirectory();
    if(file != null)
    {
      JComponent jcomponent = getDirectoryComboBox();
      if(jcomponent instanceof JComboBox)
      {
        ComboBoxModel comboboxmodel = ((JComboBox)jcomponent).getModel();
        if(comboboxmodel instanceof DirectoryComboBoxModel)
          ((DirectoryComboBoxModel)comboboxmodel).addItem(file);
      }
      if(jfilechooser.isDirectorySelectionEnabled() && !jfilechooser.isFileSelectionEnabled())
        if(filesystemview.isFileSystem(file))
          setFileName(file.getPath());
        else
          setFileName(null);
    }
  }

  protected void doFileSelectionModeChanged(PropertyChangeEvent propertychangeevent)
  {
    super.doFileSelectionModeChanged(propertychangeevent);
    JFileInterfaceChooser jfilechooser = getFileChooser();
    FileInterface file = jfilechooser.getCurrentDirectory();
    if(file != null && jfilechooser.isDirectorySelectionEnabled() && !jfilechooser.isFileSelectionEnabled()
        && jfilechooser.getFileSystemView().isFileSystem(file))
      setFileName(file.getPath());
    else
      setFileName(null);
  }

  protected void doAccessoryChanged(PropertyChangeEvent propertychangeevent)
  {
    if(getAccessoryPanel() != null)
    {
      if(propertychangeevent.getOldValue() != null)
        getAccessoryPanel().remove((JComponent)propertychangeevent.getOldValue());
      JComponent jcomponent = (JComponent)propertychangeevent.getNewValue();
      if(jcomponent != null)
        getAccessoryPanel().add(jcomponent, "Center");
    }
  }

  protected void doControlButtonsChanged(PropertyChangeEvent propertychangeevent)
  {
    super.doControlButtonsChanged(propertychangeevent);
    if(getFileChooser().getControlButtonsAreShown())
      addControlButtons();
    else
      removeControlButtons();
  }

  protected void addControlButtons()
  {
    if(bottomPanel != null)
      bottomPanel.add(buttonPanel);
  }

  protected void removeControlButtons()
  {
    if(bottomPanel != null)
      bottomPanel.remove(buttonPanel);
  }

  protected JComponent getDirectoryComboBox()
  {
    return directoryComboBox;
  }

  protected Action getDirectoryComboBoxAction()
  {
    return directoryComboBoxAction;
  }

  protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileInterfaceChooser jfilechooser)
  {
    return new DirectoryComboBoxRenderer();
  }

  protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileInterfaceChooser jfilechooser)
  {
    return new DirectoryComboBoxModel();
  }

  protected FilterComboBoxRenderer createFilterComboBoxRenderer()
  {
    return new FilterComboBoxRenderer();
  }

  protected FilterComboBoxModel createFilterComboBoxModel()
  {
    return new FilterComboBoxModel();
  }

  private static void groupLabels(AlignedLabel aalignedlabel[])
  {
    for(int i = 0; i < aalignedlabel.length; i++)
      aalignedlabel[i].group = aalignedlabel;

  }

  private JLabel lookInLabel;
  private JComboBox directoryComboBox;
  private DirectoryComboBoxModel directoryComboBoxModel;
  private Action directoryComboBoxAction;
  private FilterComboBoxModel filterComboBoxModel;
  private JTextField fileNameTextField;
  private FilePane filePane;
  private boolean useShellFolder;
  private JPanel buttonPanel;
  private JPanel bottomPanel;
  private JComboBox filterComboBox;
  private static final Dimension hstrut5 = new Dimension(5, 1);
  private static final Dimension vstrut5 = new Dimension(1, 5);
  private static Dimension LIST_PREF_SIZE = new Dimension(405, 135);
  private int lookInLabelMnemonic;
  private String lookInLabelText;
  private String saveInLabelText;
  private int fileNameLabelMnemonic;
  private String fileNameLabelText;
  private int filesOfTypeLabelMnemonic;
  private String filesOfTypeLabelText;
  static final int space = 10;
}

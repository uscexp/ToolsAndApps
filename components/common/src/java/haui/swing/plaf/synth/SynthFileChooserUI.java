/* *****************************************************************
 * Project: common
 * FileInterface:    SynthFileChooserUI.java
 * 
 * Creation:     06.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.swing.plaf.synth;

import haui.components.JFileInterfaceChooser;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.swing.FilePane;
import haui.swing.filechooser.FileView;
import haui.swing.plaf.basic.BasicFileChooserUI;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;


/**
 * SynthFileChooserUI
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public abstract class SynthFileChooserUI extends BasicFileChooserUI implements SynthUI
{
  private class DelayedSelectionUpdater implements Runnable
  {

    public void run()
    {
      updateFileNameCompletion();
    }

    DelayedSelectionUpdater()
    {
      super();
      SwingUtilities.invokeLater(this);
    }
  }

  private class FileNameCompletionAction extends AbstractAction
  {

    public void actionPerformed(ActionEvent actionevent)
    {
      JFileInterfaceChooser jfilechooser = getFileChooser();
      String s = getFileName();
      if(s != null)
        s = s.trim();
      resetGlobFilter();
      if(s == null || s.equals("") || jfilechooser.isMultiSelectionEnabled() && s.startsWith("\""))
        return;
      FileInterfaceFilter filefilter = jfilechooser.getFileFilter();
      if(globFilter == null)
        globFilter = new GlobFilter(jfilechooser.getFileInterfaceConfiguration());
      try
      {
        globFilter.setPattern(SynthFileChooserUI.isGlobPattern(s, jfilechooser.getFileInterfaceConfiguration()) ? s : (new StringBuilder()).append(s).append(
            "*").toString());
        if(!(filefilter instanceof GlobFilter))
          actualFileFilter = filefilter;
        jfilechooser.setFileFilter(null);
        jfilechooser.setFileFilter(globFilter);
        fileNameCompletionString = s;
      }
      catch(PatternSyntaxException patternsyntaxexception)
      {}
    }

    protected FileNameCompletionAction()
    {
      super("fileNameCompletion");
    }
  }

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

    public void setPattern(String s)
    {
      char ac[] = s.toCharArray();
      char ac1[] = new char[ac.length * 2];
      boolean flag = fiTpl.separatorChar() == '\\';
      boolean flag1 = false;
      StringBuffer stringbuffer = new StringBuffer();
      int i = 0;
      globPattern = s;
      if(flag)
      {
        int j = ac.length;
        if(s.endsWith("*.*"))
          j -= 2;
        for(int l = 0; l < j; l++)
        {
          if(ac[l] == '*')
            ac1[i++] = '.';
          ac1[i++] = ac[l];
        }

      }
      else
      {
        for(int k = 0; k < ac.length; k++)
          switch(ac[k])
          {
            case 42: // '*'
              if(!flag1)
                ac1[i++] = '.';
              ac1[i++] = '*';
              break;

            case 63: // '?'
              ac1[i++] = flag1 ? '?' : '.';
              break;

            case 91: // '['
              flag1 = true;
              ac1[i++] = ac[k];
              if(k >= ac.length - 1)
                break;
              switch(ac[k + 1])
              {
                case 33: // '!'
                case 94: // '^'
                  ac1[i++] = '^';
                  k++;
                  break;

                case 93: // ']'
                  ac1[i++] = ac[++k];
                  break;
              }
              break;

            case 93: // ']'
              ac1[i++] = ac[k];
              flag1 = false;
              break;

            case 92: // '\\'
              if(k == 0 && ac.length > 1 && ac[1] == '~')
              {
                ac1[i++] = ac[++k];
                break;
              }
              ac1[i++] = '\\';
              if(k < ac.length - 1 && "*?[]".indexOf(ac[k + 1]) >= 0)
                ac1[i++] = ac[++k];
              else
                ac1[i++] = '\\';
              break;

            default:
              if(!Character.isLetterOrDigit(ac[k]))
                ac1[i++] = '\\';
              ac1[i++] = ac[k];
              break;
          }

      }
      pattern = Pattern.compile(new String(ac1, 0, i), 2);
    }

    public boolean accept(FileInterface file)
    {
      if(file == null)
        return false;
      if(file.isDirectory())
        return true;
      else
        return pattern.matcher(file.getName()).matches();
    }

    public String getDescription()
    {
      return globPattern;
    }

    public boolean accept(File file)
    {
      if(file == null)
        return false;
      if(file.isDirectory())
        return true;
      else
        return pattern.matcher(file.getName()).matches();
    }

  }

  private class SynthFCPropertyChangeListener implements PropertyChangeListener
  {

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
      String s = propertychangeevent.getPropertyName();
      if(s.equals("fileSelectionChanged"))
        doFileSelectionModeChanged(propertychangeevent);
      else if(s.equals("SelectedFileChangedProperty"))
        doSelectedFileChanged(propertychangeevent);
      else if(s.equals("SelectedFilesChangedProperty"))
        doSelectedFilesChanged(propertychangeevent);
      else if(s.equals("directoryChanged"))
        doDirectoryChanged(propertychangeevent);
      else if(s == "MultiSelectionEnabledChangedProperty")
        doMultiSelectionChanged(propertychangeevent);
      else if(s == "AccessoryChangedProperty")
        doAccessoryChanged(propertychangeevent);
      else if(s == "ApproveButtonTextChangedProperty" || s == "ApproveButtonToolTipTextChangedProperty" || s == "DialogTypeChangedProperty"
          || s == "ControlButtonsAreShownChangedProperty")
        doControlButtonsChanged(propertychangeevent);
      else if(s.equals("componentOrientation"))
      {
        ComponentOrientation componentorientation = (ComponentOrientation)propertychangeevent.getNewValue();
        JFileInterfaceChooser jfilechooser = (JFileInterfaceChooser)propertychangeevent.getSource();
        if(componentorientation != (ComponentOrientation)propertychangeevent.getOldValue())
          jfilechooser.applyComponentOrientation(componentorientation);
      }
      else if(s.equals("ancestor"))
        doAncestorChanged(propertychangeevent);
    }

    private SynthFCPropertyChangeListener()
    {
      super();
    }

  }

  private class SynthFileView extends haui.swing.plaf.basic.BasicFileChooserUI.BasicFileView
  {

    public void clearIconCache()
    {
    }

    public Icon getCachedIcon(FileInterface file)
    {
      return null;
    }

    public void cacheIcon(FileInterface file, Icon icon)
    {
    }

    public Icon getIcon(FileInterface file)
    {
      return null;
    }

    public SynthFileView()
    {
      super();
      iconCache = null;
    }
  }

  private class UIBorder extends AbstractBorder implements UIResource
  {

    public void paintBorder(Component component, Graphics g, int i, int j, int k, int l)
    {
      JComponent jcomponent = (JComponent)component;
      SynthContext synthcontext = getContext(jcomponent);
      SynthStyle synthstyle = synthcontext.getStyle();
      if(synthstyle != null)
        synthstyle.getPainter(synthcontext).paintFileChooserBorder(synthcontext, g, i, j, k, l);
    }

    public Insets getBorderInsets(Component component)
    {
      return getBorderInsets(component, null);
    }

    public Insets getBorderInsets(Component component, Insets insets)
    {
      if(insets == null)
        insets = new Insets(0, 0, 0, 0);
      if(_insets != null)
      {
        insets.top = _insets.top;
        insets.bottom = _insets.bottom;
        insets.left = _insets.left;
        insets.right = _insets.right;
      }
      else
      {
        insets.top = insets.bottom = insets.right = insets.left = 0;
      }
      return insets;
    }

    public boolean isBorderOpaque()
    {
      return false;
    }

    private Insets _insets;

    UIBorder(Insets insets)
    {
      super();
      if(insets != null)
        _insets = new Insets(insets.top, insets.left, insets.bottom, insets.right);
      else
        _insets = null;
    }
  }

  public static ComponentUI createUI(JComponent jcomponent)
  {
    return new SynthFileChooserUIImpl((JFileInterfaceChooser)jcomponent);
  }

  public SynthFileChooserUI(JFileInterfaceChooser jfilechooser)
  {
    super(jfilechooser);
    fileView = new SynthFileView();
    cancelButtonText = null;
    cancelButtonToolTipText = null;
    cancelButtonMnemonic = 0;
    fileNameCompletionAction = new FileNameCompletionAction();
    actualFileFilter = null;
    globFilter = null;
  }

  public SynthContext getContext(JComponent jcomponent)
  {
    return new SynthContext(jcomponent, Region.FILE_CHOOSER, style, getComponentState(jcomponent));
  }

  protected SynthContext getContext(JComponent jcomponent, int i)
  {
    Region region = SynthLookAndFeel.getRegion(jcomponent);
    return new SynthContext(jcomponent, Region.FILE_CHOOSER, style, i);
  }

  private Region getRegion(JComponent jcomponent)
  {
    return SynthLookAndFeel.getRegion(jcomponent);
  }

  private int getComponentState(JComponent jcomponent)
  {
    if(jcomponent.isEnabled())
      return !jcomponent.isFocusOwner() ? 1 : 257;
    else
      return 8;
  }

  private void updateStyle(JComponent jcomponent)
  {
    SynthStyle synthstyle = SynthLookAndFeel.getStyleFactory().getStyle(jcomponent, Region.FILE_CHOOSER);
    if(synthstyle != style)
    {
      if(style != null)
        style.uninstallDefaults(getContext(jcomponent, 1));
      style = synthstyle;
      SynthContext synthcontext = getContext(jcomponent, 1);
      style.installDefaults(synthcontext);
      javax.swing.border.Border border = jcomponent.getBorder();
      if(border == null || (border instanceof UIResource))
        jcomponent.setBorder(new UIBorder(style.getInsets(synthcontext, null)));
    }
  }

  public FileView getFileView(JFileInterfaceChooser jfilechooser)
  {
    return fileView;
  }

  public void installUI(JComponent jcomponent)
  {
    super.installUI(jcomponent);
    SwingUtilities.replaceUIActionMap(jcomponent, createActionMap());
  }

  public void installComponents(JFileInterfaceChooser jfilechooser)
  {
    SynthContext synthcontext = getContext(jfilechooser, 1);
    cancelButtonText = UIManager.getString("FileChooser.cancelButtonText", jfilechooser.getLocale());
    cancelButtonMnemonic = UIManager.getInt("FileChooser.cancelButtonMnemonic");
    cancelButton = new JButton(cancelButtonText);
    cancelButton.setName("SynthFileChooser.cancelButton");
    cancelButton.setIcon(synthcontext.getStyle().getIcon(synthcontext, "FileChooser.cancelIcon"));
    cancelButton.setMnemonic(cancelButtonMnemonic);
    cancelButton.setToolTipText(cancelButtonToolTipText);
    cancelButton.addActionListener(getCancelSelectionAction());
    approveButton = new JButton(getApproveButtonText(jfilechooser));
    approveButton.setName("SynthFileChooser.approveButton");
    approveButton.setIcon(synthcontext.getStyle().getIcon(synthcontext, "FileChooser.okIcon"));
    approveButton.setMnemonic(getApproveButtonMnemonic(jfilechooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(jfilechooser));
    approveButton.addActionListener(getApproveSelectionAction());
  }

  public void uninstallComponents(JFileInterfaceChooser jfilechooser)
  {
    jfilechooser.removeAll();
  }

  protected void installListeners(JFileInterfaceChooser jfilechooser)
  {
    super.installListeners(jfilechooser);
    getModel().addListDataListener(new ListDataListener()
    {

      public void contentsChanged(ListDataEvent listdataevent)
      {
        new DelayedSelectionUpdater();
      }

      public void intervalAdded(ListDataEvent listdataevent)
      {
        new DelayedSelectionUpdater();
      }

      public void intervalRemoved(ListDataEvent listdataevent)
      {
      }
    });
  }

  ActionMap createActionMap()
  {
    ActionMapUIResource actionmapuiresource = new ActionMapUIResource();
    actionmapuiresource.put("approveSelection", getApproveSelectionAction());
    actionmapuiresource.put("cancelSelection", getCancelSelectionAction());
    actionmapuiresource.put("Go Up", getChangeToParentDirectoryAction());
    actionmapuiresource.put("fileNameCompletion", getFileNameCompletionAction());
    return actionmapuiresource;
  }

  protected void installDefaults(JFileInterfaceChooser jfilechooser)
  {
    super.installDefaults(jfilechooser);
    updateStyle(jfilechooser);
    readOnly = UIManager.getBoolean("FileChooser.readOnly");
  }

  protected void uninstallDefaults(JFileInterfaceChooser jfilechooser)
  {
    super.uninstallDefaults(jfilechooser);
    SynthContext synthcontext = getContext(getFileChooser(), 1);
    style.uninstallDefaults(synthcontext);
    style = null;
  }

  protected void installIcons(JFileInterfaceChooser jfilechooser)
  {
  }

  protected void uninstallIcons(JFileInterfaceChooser jfilechooser)
  {
  }

  public void update(Graphics g, JComponent jcomponent)
  {
    SynthContext synthcontext = getContext(jcomponent);
    if(jcomponent.isOpaque())
    {
      g.setColor(style.getColor(synthcontext, ColorType.BACKGROUND));
      g.fillRect(0, 0, jcomponent.getWidth(), jcomponent.getHeight());
    }
    style.getPainter(synthcontext).paintFileChooserBackground(synthcontext, g, 0, 0, jcomponent.getWidth(), jcomponent.getHeight());
    paint(synthcontext, g);
  }

  public void paintBorder(SynthContext synthcontext, Graphics g, int i, int j, int k, int l)
  {
  }

  public void paint(Graphics g, JComponent jcomponent)
  {
    SynthContext synthcontext = getContext(jcomponent);
    paint(synthcontext, g);
  }

  protected void paint(SynthContext synthcontext, Graphics g)
  {
  }

  public abstract void setFileName(String s);

  public abstract String getFileName();

  protected void doSelectedFileChanged(PropertyChangeEvent propertychangeevent)
  {
  }

  protected void doSelectedFilesChanged(PropertyChangeEvent propertychangeevent)
  {
  }

  protected void doDirectoryChanged(PropertyChangeEvent propertychangeevent)
  {
    FileInterface file = getFileChooser().getCurrentDirectory();
    if(!readOnly && file != null)
      getNewFolderAction().setEnabled(FilePane.canWrite(file));
  }

  protected void doAccessoryChanged(PropertyChangeEvent propertychangeevent)
  {
  }

  protected void doFileSelectionModeChanged(PropertyChangeEvent propertychangeevent)
  {
  }

  protected void doMultiSelectionChanged(PropertyChangeEvent propertychangeevent)
  {
    if(!getFileChooser().isMultiSelectionEnabled())
      getFileChooser().setSelectedFiles(null);
  }

  protected void doControlButtonsChanged(PropertyChangeEvent propertychangeevent)
  {
    if(getFileChooser().getControlButtonsAreShown())
    {
      approveButton.setText(getApproveButtonText(getFileChooser()));
      approveButton.setToolTipText(getApproveButtonToolTipText(getFileChooser()));
    }
  }

  protected void doAncestorChanged(PropertyChangeEvent propertychangeevent)
  {
  }

  public PropertyChangeListener createPropertyChangeListener(JFileInterfaceChooser jfilechooser)
  {
    return new SynthFCPropertyChangeListener();
  }

  private void updateFileNameCompletion()
  {
    if(fileNameCompletionString != null && fileNameCompletionString.equals(getFileName()))
    {
      FileInterface afile[] = (FileInterface[])(FileInterface[])getModel().getFiles().toArray(new FileInterface[0]);
      String s = getCommonStartString(afile);
      if(s != null && s.startsWith(fileNameCompletionString))
        setFileName(s);
      fileNameCompletionString = null;
    }
  }

  private String getCommonStartString(FileInterface afile[])
  {
    String s = null;
    String s1 = null;
    int i = 0;
    if(afile.length == 0)
      return null;
    do
    {
      for(int j = 0; j < afile.length; j++)
      {
        String s2 = afile[j].getName();
        if(j == 0)
        {
          if(s2.length() == i)
            return s;
          s1 = s2.substring(0, i + 1);
        }
        if(!s2.startsWith(s1))
          return s;
      }

      s = s1;
      i++;
    } while(true);
  }

  private void resetGlobFilter()
  {
    if(actualFileFilter != null)
    {
      JFileInterfaceChooser jfilechooser = getFileChooser();
      FileInterfaceFilter filefilter = jfilechooser.getFileFilter();
      if(filefilter != null && filefilter.equals(globFilter))
      {
        jfilechooser.setFileFilter(actualFileFilter);
        jfilechooser.removeChoosableFileFilter(globFilter);
      }
      actualFileFilter = null;
    }
  }

  private static boolean isGlobPattern(String s, FileInterfaceConfiguration fic)
  {
    FileInterface fiTpl = FileConnector.createTemplateFileInterface(fic);
    return fiTpl.separatorChar() == '\\' && s.indexOf('*') >= 0 || fiTpl.separatorChar() == '/'
        && (s.indexOf('*') >= 0 || s.indexOf('?') >= 0 || s.indexOf('[') >= 0);
  }

  public Action getFileNameCompletionAction()
  {
    return fileNameCompletionAction;
  }

  protected JButton getApproveButton(JFileInterfaceChooser jfilechooser)
  {
    return approveButton;
  }

  protected JButton getCancelButton(JFileInterfaceChooser jfilechooser)
  {
    return cancelButton;
  }

  public void clearIconCache()
  {
  }

  private JButton approveButton;
  private JButton cancelButton;
  private SynthStyle style;
  private SynthFileView fileView;
  private String cancelButtonText;
  private String cancelButtonToolTipText;
  private int cancelButtonMnemonic;
  private Action fileNameCompletionAction;
  private FileInterfaceFilter actualFileFilter;
  private GlobFilter globFilter;
  private boolean readOnly;
  private String fileNameCompletionString;
}

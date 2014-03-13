package haui.app.filesync;

import haui.app.fm.FileFilterPanel;
import haui.app.fm.FileInfoPanel;
import haui.components.JExDialog;
import haui.components.JFileInterfaceChooser;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.WildcardFileInterfaceFilter;
import haui.resource.ResouceManager;
import haui.util.AppProperties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

/**
 * Module:      FileSyncPanel.java<br> $Source: $ <p> Description: FileSyncPanel.<br> </p><p> Created:     30.09.2004  by AE </p><p>
 * @history      30.09.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.3  </p>
 */
public class FileSyncPanel
  extends JPanel
{
  private static final long serialVersionUID = 8486183360326058526L;
  
  // member variables
  private AppProperties m_appProps;
  private String m_strAppName;
  private FileInfoPanel m_fipLeft;
  private FileInfoPanel m_fipRight;
  private FileInterface m_fiLeft;
  private FileInterface m_fiRight;
  private boolean m_blToLeft = false;
  private boolean m_blToRight = false;
  private boolean m_blEqual = false;
  private boolean m_blNotEqual = false;
  private boolean m_blDuplicates = false;
  private boolean m_blSingles = false;
  private boolean m_blSelected = false;
  private boolean m_blSubdirs = false;
  private boolean m_blContent = false;
  private boolean m_blIgnDate = false;
  private String m_strFilter = null;
  private String m_strSel = "*";
  private Thread m_th = null;

  // GUI
  private LisAction m_actionlistener;
  private BorderLayout m_borderLayoutBase = new BorderLayout();
  private JPanel m_jPanelTop = new JPanel();
  private GridBagLayout m_gridBagLayoutTop = new GridBagLayout();
  private JTextField m_jTextFieldLeft = new JTextField();
  private JTextField m_jTextFieldRight = new JTextField();
  private JButton m_jButtonLeft = new JButton();
  private JButton m_jButtonRight = new JButton();
  private JComboBox m_jComboBoxFilter = new JComboBox();
  private JButton m_jButtonCompare = new JButton();
  private JCheckBox m_jCheckBoxSelected = new JCheckBox();
  private JCheckBox m_jCheckBoxSubdirs = new JCheckBox();
  private JCheckBox m_jCheckBoxContent = new JCheckBox();
  private JCheckBox m_jCheckBoxIngnDate = new JCheckBox();
  private JPanel m_jPanelButtons = new JPanel();
  private GridLayout m_gridLayoutButtons = new GridLayout();
  private JButton m_jButtonSync = new JButton();
  private JButton m_jButtonClose = new JButton();
  private JScrollPane m_jScrollPaneSyncTable = new JScrollPane();
  private FileSyncTable m_fileSyncTable = new FileSyncTable();
  private JToggleButton m_jToggleButtonToRight = new JToggleButton();
  private JToggleButton m_jToggleButtonEqual = new JToggleButton();
  private JToggleButton m_jToggleButtonNotEqual = new JToggleButton();
  private JToggleButton m_jToggleButtonToLeft = new JToggleButton();
  private JToggleButton m_jToggleButtonDuplicates = new JToggleButton();
  private JToggleButton m_jToggleButtonSingles = new JToggleButton();
  private JExDialog m_jDialogMain = null;
  private JProgressBar m_jProgressBarAdv = new JProgressBar();
  JButton m_jButtonCancel = new JButton();

  protected FileSyncPanel( FileInfoPanel fipLeft, FileInfoPanel fipRight, FileInterface fiLeft,
                        FileInterface fiRight, String strAppName, AppProperties appProps)
  {
    try
    {
      m_fipLeft = fipLeft;
      m_fipRight = fipRight;
      m_fiLeft = fiLeft;
      m_fiRight = fiRight;
      m_appProps = appProps;
      m_strAppName = strAppName;
      m_fileSyncTable = new FileSyncTable( strAppName, appProps);
      jbInit();
      initFilter();

      if( m_fipLeft != null)
      {
        if( fiLeft == null)
          m_fiLeft = fipLeft.getFileInfoTable().getFileInterface();
        m_jTextFieldLeft.setText( m_fipLeft.getFileInfoTable().getAbsolutePath());
      }
      else if( m_fiLeft != null)
      {
        m_fiLeft.getFileInterfaceConfiguration().setCached( false);
        m_jTextFieldLeft.setText( m_fiLeft.getAbsolutePath());
      }
      if( m_fipRight != null)
      {
        if( fiRight == null)
          m_fiRight = fipRight.getFileInfoTable().getFileInterface();
        m_jTextFieldRight.setText( m_fipRight.getFileInfoTable().getAbsolutePath());
      }
      else if( m_fiRight != null)
      {
        m_fiRight.getFileInterfaceConfiguration().setCached( false);
        m_jTextFieldRight.setText( m_fiRight.getAbsolutePath());
      }

      m_actionlistener = new LisAction();
      m_jButtonClose.addActionListener( m_actionlistener);
      m_jButtonLeft.addActionListener( m_actionlistener);
      m_jButtonRight.addActionListener( m_actionlistener);
      m_jButtonCompare.addActionListener( m_actionlistener);
      m_jButtonSync.addActionListener( m_actionlistener);
      m_jButtonCancel.addActionListener( m_actionlistener);
      m_jToggleButtonToRight.addActionListener( m_actionlistener);
      m_jToggleButtonEqual.addActionListener( m_actionlistener);
      m_jToggleButtonNotEqual.addActionListener( m_actionlistener);
      m_jToggleButtonToLeft.addActionListener( m_actionlistener);
      m_jToggleButtonDuplicates.addActionListener( m_actionlistener);
      m_jToggleButtonSingles.addActionListener( m_actionlistener);

      setStates();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  protected FileSyncPanel( FileInfoPanel fipLeft, FileInfoPanel fipRight, String strAppName, AppProperties appProps)
  {
    this( fipLeft, fipRight, null, null, strAppName, appProps);
  }

  public FileSyncPanel( FileInterface fiLeft, FileInterface fiRight, String strAppName, AppProperties appProps)
  {
    this( null, null, fiLeft, fiRight, strAppName, appProps);
  }

  public FileSyncPanel( String strAppName, AppProperties appProps)
  {
    this( null, null, null, null, strAppName, appProps);
  }

  private void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jPanelTop.setLayout(m_gridBagLayoutTop);
    m_jButtonLeft.setActionCommand("browseleft");
    m_jButtonLeft.setText(">>");
    m_jButtonRight.setActionCommand("browseright");
    m_jButtonRight.setText(">>");
    m_jComboBoxFilter.setEditable(true);
    m_jButtonCompare.setActionCommand("compare");
    m_jButtonCompare.setText("Compare");
    m_jCheckBoxSelected.setActionCommand("selected");
    m_jCheckBoxSelected.setText("Only selected");
    m_jCheckBoxSelected.setEnabled( false);
    m_jCheckBoxSubdirs.setActionCommand("subdirs");
    m_jCheckBoxSubdirs.setSelected(true);
    m_jCheckBoxSubdirs.setText("Subdirectories");
    m_jCheckBoxContent.setActionCommand("content");
    m_jCheckBoxContent.setText("Due to content");
    m_jCheckBoxIngnDate.setActionCommand("ignoredate");
    m_jCheckBoxIngnDate.setText("Ignore date");
    m_jPanelButtons.setLayout(m_gridLayoutButtons);
    m_gridLayoutButtons.setColumns(4);
    m_gridLayoutButtons.setHgap(0);
    m_jButtonSync.setActionCommand("sync");
    m_jButtonSync.setText("Synchronize");
    m_jPanelButtons.setBorder(BorderFactory.createEtchedBorder());
    m_jButtonClose.setActionCommand("close");
    m_jButtonClose.setText("Close");
    m_jToggleButtonToRight.setActionCommand("toright");
    m_jToggleButtonToRight.setSelected(true);
    m_jToggleButtonToRight.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "toright.gif" ));
    m_jToggleButtonEqual.setActionCommand("equal");
    m_jToggleButtonEqual.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "equal.gif" ));
    m_jToggleButtonNotEqual.setActionCommand("notequal");
    m_jToggleButtonNotEqual.setSelected(true);
    m_jToggleButtonNotEqual.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "notequal.gif" ));
    m_jToggleButtonToLeft.setActionCommand("toleft");
    m_jToggleButtonToLeft.setSelected(true);
    m_jToggleButtonToLeft.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "toleft.gif" ));
    m_jToggleButtonDuplicates.setActionCommand("duplicates");
    m_jToggleButtonDuplicates.setSelected(true);
    m_jToggleButtonDuplicates.setText("Duplicates");
    m_jToggleButtonSingles.setActionCommand("singles");
    m_jToggleButtonSingles.setSelected(true);
    m_jToggleButtonSingles.setText("Singles");
    m_jButtonCancel.setActionCommand("cancel");
    m_jButtonCancel.setText("Cancel");
    this.add( m_jPanelTop, BorderLayout.NORTH);
    m_jPanelTop.add( m_jTextFieldLeft, new GridBagConstraints( 0, 0, 2, 1, 0.3, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jTextFieldRight, new GridBagConstraints( 4, 0, 2, 1, 0.7, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jButtonRight, new GridBagConstraints( 6, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jComboBoxFilter, new GridBagConstraints( 3, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jButtonCompare, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jButtonLeft, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jCheckBoxSelected, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jCheckBoxSubdirs, new GridBagConstraints( 1, 1, 2, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jCheckBoxContent, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jCheckBoxIngnDate, new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelTop.add( m_jPanelButtons, new GridBagConstraints( 2, 1, 2, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0), 0, 0));
    m_jPanelButtons.add(m_jToggleButtonToRight, null);
    m_jPanelButtons.add(m_jToggleButtonEqual, null);
    m_jPanelButtons.add(m_jToggleButtonNotEqual, null);
    m_jPanelButtons.add(m_jToggleButtonToLeft, null);
    m_jPanelTop.add(m_jButtonSync,     new GridBagConstraints(5, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    m_jPanelTop.add(m_jButtonClose,    new GridBagConstraints(5, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(m_jScrollPaneSyncTable,  BorderLayout.CENTER);
    m_jScrollPaneSyncTable.getViewport().add(m_fileSyncTable, null);
    m_jPanelTop.add(m_jToggleButtonDuplicates,  new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    m_jPanelTop.add(m_jToggleButtonSingles,  new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    m_jPanelTop.add(m_jProgressBarAdv,   new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    m_jPanelTop.add(m_jButtonCancel,   new GridBagConstraints(2, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }

  protected void setStates()
  {
    m_blDuplicates = m_jToggleButtonDuplicates.isSelected();
    m_blSingles = m_jToggleButtonSingles.isSelected();
    m_blEqual = m_jToggleButtonEqual.isSelected();
    m_blNotEqual = m_jToggleButtonNotEqual.isSelected();
    m_blToLeft = m_jToggleButtonToLeft.isSelected();
    m_blToRight = m_jToggleButtonToRight.isSelected();
    m_blSelected = m_jCheckBoxSelected.isSelected();
    m_blSubdirs = m_jCheckBoxSubdirs.isSelected();
    m_blContent = m_jCheckBoxContent.isSelected();
    m_blIgnDate = m_jCheckBoxIngnDate.isSelected();
    m_strFilter = (String)m_jComboBoxFilter.getSelectedItem();
  }

  public static JExDialog createSyncDialog( Component parent, FileInfoPanel fipLeft, FileInfoPanel fipRight, String strAppName, AppProperties appProps)
  {
    JExDialog jDialogMain = new JExDialog( parent, "Synchronize", true, strAppName);
    BorderLayout borderLayoutMain = new BorderLayout();
    jDialogMain.getContentPane().setLayout( borderLayoutMain);
    FileSyncPanel fsp = new FileSyncPanel( fipLeft, fipRight, strAppName, appProps);
    jDialogMain.getContentPane().add( fsp);
    fsp.setDialog( jDialogMain);
    jDialogMain.pack();

    return jDialogMain;
  }

  protected void setDialog( JExDialog dlg)
  {
    m_jDialogMain = dlg;
  }

  public void hideCloseButton()
  {
    m_jButtonClose.setVisible( false);
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event )
    {
      getRootPane().getParent().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
      String cmd = event.getActionCommand();
      if( cmd.equals( "close" ) )
      {
        if( m_jDialogMain != null)
          m_jDialogMain.setVisible( false);
          m_jDialogMain.dispose();
      }
      else if( cmd.equals( "compare" ) )
      {
        onCompare();
      }
      else if( cmd.equals( "cancel" ) )
      {
        if( m_th != null && m_th.isAlive())
        {
          try
          {
            m_th.interrupt();
            if( m_th.isAlive())
            {
              Thread.sleep( 300 );
              if( m_th.isAlive())
                m_th.stop();
            }
          }
          catch( InterruptedException ex )
          {
            ex.printStackTrace();
          }
        }
        m_fileSyncTable.interrupt();
      }
      else if( cmd.equals( "browseleft" ) )
      {
        FileInterface fiValid = getValidFileInterface( m_fiLeft);
        JFileInterfaceChooser chooser = new JFileInterfaceChooser( fiValid, fiValid.getFileInterfaceConfiguration());
        chooser.setFileSelectionMode( JFileInterfaceChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog( getParent());
        if(returnVal == JFileInterfaceChooser.APPROVE_OPTION)
        {
          if( chooser.getSelectedFile() != null && chooser.getSelectedFile().getAbsolutePath() != null)
          {
            FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, null, 0, 0,
                m_strAppName, m_appProps, true);
            FileInterface fi = FileConnector.createFileInterface( chooser.getSelectedFile().getAbsolutePath(),
                null, false, fic);
            if( fi != null)
            {
              m_fiLeft = fi;
              m_fipLeft = null;
              m_jTextFieldLeft.setText( m_fiLeft.getAbsolutePath());
            }
          }
        }
        chooser.setVisible( false);
      }
      else if( cmd.equals( "browseright" ) )
      {
        FileInterface fiValid = getValidFileInterface( m_fiRight);
        JFileInterfaceChooser chooser = new JFileInterfaceChooser( fiValid, fiValid.getFileInterfaceConfiguration());
        chooser.setFileSelectionMode( JFileInterfaceChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog( getParent());
        if(returnVal == JFileInterfaceChooser.APPROVE_OPTION)
        {
          if( chooser.getSelectedFile() != null && chooser.getSelectedFile().getAbsolutePath() != null)
          {
            FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, null, 0, 0,
                m_strAppName, m_appProps, true);
            FileInterface fi = FileConnector.createFileInterface( chooser.getSelectedFile().getAbsolutePath(),
                null, false, fic);
            if( fi != null)
            {
              m_fiRight = fi;
              m_fipRight = null;
              m_jTextFieldRight.setText( m_fiRight.getAbsolutePath());
            }
          }
        }
        chooser.setVisible( false);
      }
      else if( cmd.equals( "toright") || cmd.equals( "equal") || cmd.equals( "notequal")
          || cmd.equals( "toleft") || cmd.equals( "duplicates") || cmd.equals( "singles"))
      {
        setStates();
        m_fileSyncTable.showResult( m_blToLeft, m_blToRight, m_blEqual, m_blNotEqual, m_blDuplicates, m_blSingles);
      }
      else if( cmd.equals( "sync" ) )
      {
        onSynchronize();
      }

      getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
    }
  }
  
  public FileInterface getValidFileInterface( FileInterface fi)
  {
    FileInterface fileInterface = fi;
    if( fi != null && !fi.exists())
    {
      fileInterface = fi.getParentFileInterface();
      if( !fileInterface.exists())
        fileInterface = getValidFileInterface( fileInterface);
    }
    return fileInterface;
  }

  protected void onCompare()
  {
    setStates();
    final WildcardFileInterfaceFilter wfif = new WildcardFileInterfaceFilter( m_strFilter, m_strAppName);
    addFilter( m_strFilter);
    m_fileSyncTable.clear();
    m_th = new Thread()
    {
      public void run()
      {
        m_fileSyncTable.setLocked( true);
        m_fileSyncTable.compare( m_jProgressBarAdv, m_fiLeft, m_fiRight, wfif, m_blSubdirs, m_blIgnDate, m_blContent, m_blSelected,
                                 m_blToLeft, m_blToRight, m_blEqual, m_blNotEqual, m_blDuplicates, m_blSingles);
        m_fileSyncTable.setLocked( false);
      }
    };
    m_th.start();
    /*
    CancelDlg cdlg = new CancelDlg( this, "Executing ...", m_th, m_strAppName);
    cdlg.start();
    if( !cdlg.isCanceled())
      m_fileSyncTable.showResult( m_blToLeft, m_blToRight, m_blEqual, m_blNotEqual, m_blDuplicates, m_blSingles);
    */
  }

  protected void onSynchronize()
  {
    setStates();
    m_fileSyncTable.synchronize( m_jProgressBarAdv, m_blToLeft, m_blToRight, m_blEqual, m_blNotEqual,
                                 m_blDuplicates, m_blSingles);
  }

  // ComboBoxFilter management
  public void initFilter()
  {
    Vector vecFilterStrings = new Vector();
    String str;

    m_jComboBoxFilter.removeAllItems();
    for( int i = 0; ( str = m_appProps.getProperty( FileFilterPanel.FILTERDEF + String.valueOf(i))) != null; ++i)
    {
      m_jComboBoxFilter.addItem( str);
    }
    if( m_jComboBoxFilter.getItemCount() == 0)
    {
      m_jComboBoxFilter.addItem( m_strSel );
      updateProperties();
    }
    m_jComboBoxFilter.setSelectedItem( m_strSel);
  }

  public void addFilter( String strFilter)
  {
    String str;
    int iIdx = -1;
    int iCompRes;

    for( int i = 0; i < m_jComboBoxFilter.getItemCount(); ++i)
    {
      str = (String)m_jComboBoxFilter.getItemAt( i);
      if( (iCompRes = strFilter.compareTo( str)) == 0)
        return;
      else if( iCompRes < 0 && iIdx == -1)
        iIdx = i;
    }
    if( !strFilter.equals( ""))
    {
      clearProperties();
      if( iIdx == -1 )
        m_jComboBoxFilter.addItem( strFilter );
      else
        m_jComboBoxFilter.insertItemAt( strFilter, iIdx );
      updateProperties();
    }
  }

  public void removeFilter( String strFilter)
  {
    clearProperties();
    for( int i = 0; i < m_jComboBoxFilter.getItemCount(); ++i)
    {
      String str = (String)m_jComboBoxFilter.getItemAt( i);
      if( strFilter.equals( str))
      {
        m_jComboBoxFilter.removeItemAt( i);
        m_jComboBoxFilter.setSelectedItem( "");
      }
    }
    updateProperties();
  }

  private void clearProperties()
  {
    for( int i = 0; i < m_jComboBoxFilter.getItemCount(); ++i)
    {
      m_appProps.remove( FileFilterPanel.FILTERDEF + String.valueOf( i));
    }
  }

  private void updateProperties()
  {
    for( int i = 0; i < m_jComboBoxFilter.getItemCount(); ++i)
    {
      String str = (String)m_jComboBoxFilter.getItemAt( i);
      m_appProps.setProperty( FileFilterPanel.FILTERDEF + String.valueOf( i), str);
    }
  }

  // main for testing purposes
  public static void main(String argv[])
  {
    FileSyncPanel demo = new FileSyncPanel( "FileSyncPanel", new AppProperties());

    JFrame f = new JFrame("FileSyncPanel");
    f.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {System.exit(0);}
    });
    f.getContentPane().add("Center", demo);
    f.pack();
    //f.setSize(new Dimension(640,480));
    f.setVisible( true);
  }
}
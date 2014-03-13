package haui.app.cdarchive;

import haui.components.FileSystemTree;
import haui.components.VerticalFlowLayout;
import haui.components.desktop.JExInternalFrame;
import haui.resource.ResouceManager;
import haui.tool.InternalCancelDlg;
import haui.util.AppProperties;
import haui.util.GlobalAppProperties;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Module:      CdArchiveFrame.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchiveFrame.java,v $
 *<p>
 * Description: CdArchiveFrame.<br>
 *</p><p>
 * Created:     02.05.2003	by	AE
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     02.05.2003
 *</p><p>
 * Modification:<br>
 * $Log: CdArchiveFrame.java,v $
 * Revision 1.4  2004-08-31 16:03:03+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.3  2004-06-22 14:08:51+02  t026843
 * bigger changes
 *
 * Revision 1.2  2004-02-17 16:05:51+01  t026843
 * <>
 *
 * Revision 1.1  2003-05-28 14:19:47+02  t026843
 * reorganisations
 *
 * Revision 1.0  2003-05-21 16:24:39+02  t026843
 * Initial revision
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.4 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchiveFrame.java,v 1.4 2004-08-31 16:03:03+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class CdArchiveFrame
  extends JExInternalFrame
{
  // Constants
  public static final String CDARCH = "CdArchive Dokument";

  // member variables
  protected AppAction m_appAction = new AppAction();
  private AppProperties m_appProps;
  private CdObject m_cdObj;
  private boolean m_blNew = false;
  protected Vector m_vecFilters;
  protected Vector m_vecCategories;
  protected CdArchiveDesktop m_dp;

  // GUI member variales
  BorderLayout m_borderLayoutMain = new BorderLayout();
  JMenuBar m_jMenuBar = new JMenuBar();
  private JMenu m_jMenuFile = new JMenu();
  private JMenuItem m_jMenuItemNew = new JMenuItem();
  private JMenuItem m_jMenuItemRead = new JMenuItem();
  private JMenuItem m_jMenuItemReload = new JMenuItem();
  private JMenuItem m_jMenuItemPrint = new JMenuItem();
  private JMenu m_jMenuOptions = new JMenu();
  private JMenuItem m_jMenuItemProperties = new JMenuItem();
  private JMenu m_jMenuEdit = new JMenu();
  private JMenuItem m_jMenuItemUndo = new JMenuItem();
  private JMenuItem m_jMenuItemRedo = new JMenuItem();
  private JSeparator JSeparatorUndo = new JSeparator();
  private JMenuItem m_jMenuItemCut = new JMenuItem();
  private JMenuItem m_jMenuItemCopy = new JMenuItem();
  private JMenuItem m_jMenuItemPaste = new JMenuItem();
  private JSeparator JSeparatorFind = new JSeparator();
  private JMenuItem m_jMenuItemFind = new JMenuItem();
  private JSeparator JSeparatorExEdit = new JSeparator();
  private JMenuItem m_jMenuItemInsLabel = new JMenuItem();
  private JToolBar m_jToolBarMain = new JToolBar();
  private JButton m_jButtonNew = new JButton();
  private JButton m_jButtonRead = new JButton();
  private JButton m_jButtonReload = new JButton();
  private JButton m_jButtonPrint = new JButton();
  private JButton m_jButtonProperties = new JButton();
  private JSeparator m_jSeparatorNew = new JSeparator();
  private JSeparator m_jSeparatorPrint = new JSeparator();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonApply = new JButton();
  JButton m_jButtonCancel = new JButton();
  JSplitPane m_jSplitPaneMain = new JSplitPane();
  JPanel m_jPanelRight = new JPanel();
  BorderLayout m_borderLayoutRight = new BorderLayout();
  BorderLayout m_borderLayoutLeft = new BorderLayout();
  JLabel m_jLabelCategories = new JLabel();
  JComboBox m_jComboBoxCategories = new JComboBox();
  JTextField m_jTextFieldId = new JTextField();
  JPanel m_jPanelTopRight = new JPanel();
  JPanel m_jPanelLeft = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutTopRight = new VerticalFlowLayout();
  JLabel m_jLabelLabel = new JLabel();
  JLabel m_jLabelId = new JLabel();
  JTextField m_jTextFieldLabel = new JTextField();
  JLabel m_jLabelContent = new JLabel();
  ContentTextArea m_contentTextArea;
  JScrollPane m_jScrollPaneContent = new JScrollPane();
  JScrollPane m_jScrollPaneFileTree = new JScrollPane();
  FileSystemTree m_fileSystemTree = new FileSystemTree();

  public CdArchiveFrame( CdArchiveDesktop dp, CdObject cdObj, AppProperties appProps, boolean blNew)
  {
    super( dp, CdArchiveDesktop.CDARCHDT, CDARCH, true, true, true, true);
    m_dp = dp;
    setCursor(new Cursor( Cursor.WAIT_CURSOR));
    m_appProps = appProps;
    m_cdObj = cdObj;
    m_blNew = blNew;
    Integer iPa = m_appProps.getIntegerProperty( CdArchiveDesktop.PRINTERADJUST);
    int iPrinterAdjust = 0;
    if( iPa != null)
      iPrinterAdjust = iPa.intValue();
    m_contentTextArea = new ContentTextArea( m_dp, this, 25, iPrinterAdjust, CDARCH);
    m_vecFilters = (Vector)appProps.getObjectProperty( CdArchiveDesktop.FILTERS);
    m_vecCategories = CdArchiveDesktop.getCategories();
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    initCategories();

    m_jMenuItemNew.addActionListener( m_appAction);
    m_jMenuItemRead.addActionListener( m_appAction);
    m_jMenuItemReload.addActionListener( m_appAction);
    m_jMenuItemPrint.addActionListener( m_appAction);
    m_jMenuItemProperties.addActionListener( m_appAction);
    m_jMenuItemUndo.addActionListener(m_appAction);
    m_jMenuItemRedo.addActionListener(m_appAction);
    m_jMenuItemCut.addActionListener(m_appAction);
    m_jMenuItemCopy.addActionListener(m_appAction);
    m_jMenuItemPaste.addActionListener(m_appAction);
    m_jMenuItemFind.addActionListener(m_appAction);
    m_jMenuItemInsLabel.addActionListener(m_appAction);
    m_jButtonOk.addActionListener( m_appAction);
    m_jButtonApply.addActionListener( m_appAction);
    m_jButtonCancel.addActionListener( m_appAction);
    m_jButtonNew.addActionListener( m_appAction);
    m_jButtonRead.addActionListener( m_appAction);
    m_jButtonReload.addActionListener( m_appAction);
    m_jButtonPrint.addActionListener( m_appAction);
    m_jButtonProperties.addActionListener( m_appAction);

    m_fileSystemTree.expandRow( 0);

    if( !m_blNew)
    {
      updateFields();
      m_jMenuItemRead.setEnabled( false);
      m_jButtonRead.setEnabled( false);
    }
    Thread th = new Thread()
                {
                  public void run()
                  {
                    m_contentTextArea.requestFocus();
                  }
                };
    SwingUtilities.invokeLater( th);
    setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
  }

  private void jbInit() throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutMain);
    this.setJMenuBar(m_jMenuBar);
    m_jMenuFile.setText("File");
    m_jMenuFile.setActionCommand("file");
    m_jMenuFile.setMnemonic((int)'F');
    m_jMenuBar.add(m_jMenuFile);
    m_jMenuItemNew.setText("New");
    m_jMenuItemNew.setActionCommand("new");
    m_jMenuItemNew.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, Event.ALT_MASK));
    m_jMenuItemNew.setMnemonic((int)'N');
    m_jMenuItemNew.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "new.gif"));
    m_jMenuItemRead.setText("Read");
    m_jMenuItemRead.setActionCommand("read");
    m_jMenuItemRead.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, Event.ALT_MASK));
    m_jMenuItemRead.setMnemonic((int)'R');
    m_jMenuItemRead.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "read.gif"));
    m_jMenuItemReload.setText("Reread file tree");
    m_jMenuItemReload.setActionCommand("reload");
    m_jMenuItemReload.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F, Event.ALT_MASK));
    m_jMenuItemReload.setMnemonic((int)'f');
    m_jMenuItemReload.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "reload.gif"));
    m_jMenuItemPrint.setText("Print");
    m_jMenuItemPrint.setActionCommand("print");
    m_jMenuItemPrint.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, Event.ALT_MASK));
    m_jMenuItemPrint.setMnemonic((int)'P');
    m_jMenuItemPrint.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "print.gif"));
    m_jMenuFile.add(m_jMenuItemNew);
    m_jMenuFile.add(m_jSeparatorNew);
    m_jMenuFile.add(m_jMenuItemRead);
    m_jMenuFile.add(m_jMenuItemReload);
    m_jMenuFile.add(m_jSeparatorPrint);
    m_jMenuFile.add(m_jMenuItemPrint);
    m_jMenuEdit.setText("Edit");
    m_jMenuEdit.setActionCommand("edit");
    m_jMenuEdit.setMnemonic((int)'E');
    m_jMenuBar.add(m_jMenuEdit);
    m_jMenuItemUndo.setText("Undo");
    m_jMenuItemUndo.setActionCommand("undo");
    m_jMenuItemUndo.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, Event.CTRL_MASK));
    m_jMenuItemUndo.setMnemonic((int)'U');
    m_jMenuItemUndo.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "Undo16.gif"));
    m_jMenuEdit.add(m_jMenuItemUndo);
    m_jMenuItemRedo.setText("Redo");
    m_jMenuItemRedo.setActionCommand("redo");
    m_jMenuItemRedo.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, Event.ALT_MASK));
    m_jMenuItemRedo.setMnemonic((int)'R');
    m_jMenuItemRedo.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "Redo16.gif"));
    m_jMenuEdit.add(m_jMenuItemRedo);
    m_jMenuEdit.add(JSeparatorUndo);
    m_jMenuItemCut.setText("Cut");
    m_jMenuItemCut.setActionCommand("cut");
    m_jMenuItemCut.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, Event.CTRL_MASK));
    m_jMenuItemCut.setMnemonic((int)'t');
    m_jMenuItemCut.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "Cut16.gif"));
    m_jMenuEdit.add(m_jMenuItemCut);
    m_jMenuItemCopy.setText("Copy");
    m_jMenuItemCopy.setActionCommand("copy");
    m_jMenuItemCopy.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, Event.CTRL_MASK));
    m_jMenuItemCopy.setMnemonic((int)'C');
    m_jMenuItemCopy.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "Copy16.gif"));
    m_jMenuEdit.add(m_jMenuItemCopy);
    m_jMenuItemPaste.setText("Paste");
    m_jMenuItemPaste.setActionCommand("paste");
    m_jMenuItemPaste.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, Event.CTRL_MASK));
    m_jMenuItemPaste.setMnemonic((int)'P');
    m_jMenuItemPaste.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "Paste16.gif"));
    m_jMenuEdit.add(m_jMenuItemPaste);
    m_jMenuEdit.add(JSeparatorFind);
    m_jMenuItemFind.setText("Find");
    m_jMenuItemFind.setActionCommand("find");
    m_jMenuItemFind.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F, Event.CTRL_MASK));
    m_jMenuItemFind.setMnemonic((int)'F');
    m_jMenuItemFind.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "search.gif"));
    m_jMenuEdit.add(m_jMenuItemFind);
    m_jMenuEdit.add(JSeparatorExEdit);
    m_jMenuItemInsLabel.setText("Insert label");
    m_jMenuItemInsLabel.setActionCommand("inslabel");
    m_jMenuItemInsLabel.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_I, Event.CTRL_MASK));
    m_jMenuItemInsLabel.setMnemonic((int)'I');
    //m_jMenuItemInsLabel.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "search.gif"));
    m_jMenuEdit.add(m_jMenuItemInsLabel);
    m_jMenuOptions.setText("Options");
    m_jMenuOptions.setActionCommand("options");
    m_jMenuOptions.setMnemonic((int)'O');
    m_jMenuBar.add(m_jMenuOptions);
    m_jMenuItemProperties.setText("Properties");
    m_jMenuItemProperties.setActionCommand("properties");
    m_jMenuItemProperties.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, Event.ALT_MASK));
    m_jMenuItemProperties.setMnemonic((int)'P');
    m_jMenuItemProperties.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "preferences16.gif"));
    m_jMenuOptions.add(m_jMenuItemProperties);
    m_jButtonNew.setMaximumSize(new Dimension(18, 18));
    m_jButtonNew.setMinimumSize(new Dimension(18, 18));
    m_jButtonNew.setPreferredSize(new Dimension(18, 18));
    m_jButtonNew.setToolTipText("New");
    m_jButtonNew.setActionCommand("new");
    m_jButtonNew.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "new.gif"));
    m_jButtonRead.setMaximumSize(new Dimension(18, 18));
    m_jButtonRead.setMinimumSize(new Dimension(18, 18));
    m_jButtonRead.setPreferredSize(new Dimension(18, 18));
    m_jButtonRead.setToolTipText("Read");
    m_jButtonRead.setActionCommand("read");
    m_jButtonRead.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "read.gif"));
    m_jButtonReload.setMaximumSize(new Dimension(18, 18));
    m_jButtonReload.setMinimumSize(new Dimension(18, 18));
    m_jButtonReload.setPreferredSize(new Dimension(18, 18));
    m_jButtonReload.setToolTipText("Reread file tree");
    m_jButtonReload.setActionCommand("reload");
    m_jButtonReload.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "reload.gif"));
    m_jButtonPrint.setMaximumSize(new Dimension(18, 18));
    m_jButtonPrint.setMinimumSize(new Dimension(18, 18));
    m_jButtonPrint.setPreferredSize(new Dimension(18, 18));
    m_jButtonPrint.setToolTipText("Print");
    m_jButtonPrint.setActionCommand("print");
    m_jButtonPrint.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "print.gif"));
    m_jButtonProperties.setMaximumSize(new Dimension(18, 18));
    m_jButtonProperties.setMinimumSize(new Dimension(18, 18));
    m_jButtonProperties.setPreferredSize(new Dimension(18, 18));
    m_jButtonProperties.setToolTipText("Properties");
    m_jButtonProperties.setActionCommand("properties");
    m_jButtonProperties.setIcon( ResouceManager.getCommonImageIcon( CdArchiveDesktop.CDARCHDT, "preferences16.gif"));
    m_jToolBarMain.add(m_jButtonNew, null);
    m_jToolBarMain.addSeparator();
    m_jToolBarMain.add(m_jButtonRead, null);
    m_jToolBarMain.add(m_jButtonReload, null);
    m_jToolBarMain.addSeparator();
    m_jToolBarMain.add(m_jButtonPrint, null);
    m_jToolBarMain.addSeparator();
    m_jToolBarMain.add(m_jButtonProperties, null);
    this.getContentPane().add( m_jToolBarMain, BorderLayout.NORTH);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    this.getContentPane().add(m_jPanelButton,  BorderLayout.SOUTH);
    m_jButtonOk.setActionCommand("ok");
    m_jButtonOk.setText("Ok");
    m_jButtonApply.setActionCommand("apply");
    m_jButtonApply.setText("Apply");
    m_jButtonCancel.setActionCommand("cancel");
    m_jButtonCancel.setText("Cancel");
    m_jPanelButton.add(m_jButtonOk, null);
    m_jPanelButton.add(m_jButtonApply, null);
    m_jPanelButton.add(m_jButtonCancel, null);
    m_jSplitPaneMain.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    m_jPanelRight.setLayout(m_borderLayoutRight);
    m_jTextFieldId.setText("");
    m_jTextFieldId.setEditable(false);
    m_jPanelTopRight.setLayout(m_verticalFlowLayoutTopRight);
    m_jLabelCategories.setText("Category:");
    m_jLabelLabel.setText("Label:");
    m_jLabelId.setText("Id:");
    m_jTextFieldLabel.setText("");
    m_jLabelContent.setText("Content:");
    this.getContentPane().add(m_jSplitPaneMain,  BorderLayout.CENTER);
    m_jComboBoxCategories.setEditable( true);
    m_jPanelTopRight.add(m_jLabelId, null);
    m_jPanelTopRight.add(m_jTextFieldId, null);
    m_jPanelTopRight.add(m_jLabelCategories, null);
    m_jPanelTopRight.add(m_jComboBoxCategories, null);
    m_jPanelTopRight.add(m_jLabelLabel, null);
    m_jPanelTopRight.add(m_jTextFieldLabel, null);
    m_jPanelTopRight.add(m_jLabelContent, null);
    m_jPanelRight.add(m_jPanelTopRight, BorderLayout.NORTH);
    m_jPanelRight.add(m_jScrollPaneContent,  BorderLayout.CENTER);
    m_jScrollPaneContent.setViewportView( m_contentTextArea);
    m_jSplitPaneMain.add( m_jPanelRight, JSplitPane.RIGHT);
    m_jPanelLeft.setLayout(m_borderLayoutLeft);
    m_jPanelLeft.add(m_jScrollPaneFileTree,  BorderLayout.CENTER);
    m_fileSystemTree.setRootVisible( false);
    m_jScrollPaneFileTree.setViewportView( m_fileSystemTree);
    m_jSplitPaneMain.add( m_jPanelLeft, JSplitPane.LEFT);
  }

  protected void initCategories()
  {
    if( m_vecCategories != null)
    {
      m_jComboBoxCategories.removeAllItems();
      for( int i = 0; i < m_vecCategories.size(); i++ )
      {
        String str = ( String )m_vecCategories.elementAt( i );
        m_jComboBoxCategories.addItem( str );
      }
    }
  }

  protected boolean checkCategory( String strCat)
  {
    boolean blRet = false;
    if( strCat != null && !strCat.equals( ""))
    {
      if( m_vecCategories.contains( strCat))
        blRet = true;
      else
      {
        if( ((CdArchiveDesktop)m_df).addCategory( strCat))
        {
          initCategories();
          m_jComboBoxCategories.setSelectedItem( strCat );
          blRet = true;
        }
      }
    }
    return blRet;
  }

  public void setSearchString( String strSearch)
  {
    m_contentTextArea.setSearchString( strSearch);
  }

  public String getSearchString()
  {
    return m_contentTextArea.getSearchString();
  }

  public void adjustComponent()
  {
    m_jSplitPaneMain.setDividerLocation( 0.25);
  }

  private void updateFields()
  {
    if( m_cdObj.getId() > -1)
      m_jTextFieldId.setText( String.valueOf( m_cdObj.getId()));
    if( m_cdObj.getCategory() == null || m_cdObj.getCategory().equals( ""))
    {
      if( m_vecCategories.size() > 0)
        m_jComboBoxCategories.setSelectedIndex( 0);
    }
    else
      m_jComboBoxCategories.setSelectedItem( m_cdObj.getCategory());
    m_jTextFieldLabel.setText( m_cdObj.getLabel());
    m_contentTextArea.setText( m_cdObj.getContent());
  }

  private boolean checkFieldsUpdated()
  {
    boolean blRet = false;
    blRet = !m_cdObj.getCategory().equals( m_jComboBoxCategories.getSelectedItem());
    if( !blRet)
    {
      blRet = !m_cdObj.getLabel().equals( m_jTextFieldLabel.getText());
    }
    if( !blRet)
    {
      blRet = !m_cdObj.getContent().equals( m_contentTextArea.getText());
    }

    return blRet;
  }

  class AppAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event )
    {
      String cmd = event.getActionCommand();
      if( cmd == "exit" )
        onExit();
      else if( cmd == "new" )
        onNew();
      else if( cmd == "read" )
        onRead();
      else if( cmd == "reload" )
        onReload();
      else if( cmd == "properties" )
        onProperties();
      else if (cmd == "undo")
        onUndo();
      else if (cmd == "redo")
        onRedo();
      else if (cmd == "cut")
        onCut();
      else if (cmd == "copy")
        onCopy();
      else if (cmd == "paste")
        onPaste();
      else if (cmd == "find")
        onFind( true);
      else if (cmd == "inslabel")
        onInsertLabel();
      else if( cmd == "ok" )
        onOk();
      else if( cmd == "apply" )
        onApply();
      else if( cmd == "cancel" )
        onCancel();
      else if( cmd == "print" )
        onPrint();
    }
  }

  private void onUndo()
  {
    m_contentTextArea.undo();
  }

  private void onRedo()
  {
    m_contentTextArea.redo();
  }

  private void onCut()
  {
    m_contentTextArea.cut();
  }

  private void onCopy()
  {
    m_contentTextArea.copy();
  }

  private void onPaste()
  {
    m_contentTextArea.paste();
  }

  private void onFind( boolean blNew)
  {
    m_contentTextArea.find( blNew);
  }

  private void onInsertLabel()
  {
    String str = m_jTextFieldLabel.getText();
    String strContent = m_contentTextArea.getText();
    m_contentTextArea.setCaretPosition( 0 );
    if( str != null && !str.equals( "") && strContent != null)
    {
      if( strContent.startsWith( "{"))
      {
        int iIdx = strContent.indexOf( '}');
        if( iIdx > 0)
        {
          m_contentTextArea.setSelectionStart( 0 );
          m_contentTextArea.setSelectionEnd( iIdx+1);
        }
        str = "{" + str + "}";
      }
      else if( strContent.startsWith( "\n"))
        str = "{" + str + "}";
      else
        str = "{" + str + "}\n";
      m_contentTextArea.replaceSelection( str);
    }
  }

  private void onPrint()
  {
    PrinterJob pj = PrinterJob.getPrinterJob();
    pj.setPrintable( m_contentTextArea);
    pj.printDialog();
    try
    {
      setCursor(new Cursor( Cursor.WAIT_CURSOR));
      pj.print();
    }
    catch( PrinterException pex)
    {
      pex.printStackTrace();
    }
    setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
  }

  protected void onNew()
  {
    if( checkFieldsUpdated())
    {
      int iRet = JOptionPane.showConfirmDialog( GlobalAppProperties.instance().getRootComponent( CDARCH),
                                                "The entry was changed, dischard anyway?"
                                                , "Alert", JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE );
      if( iRet == JOptionPane.NO_OPTION )
      {
        GlobalAppProperties.instance().getPrintStreamOutput( CdArchiveDesktop.CDARCHDT).println( "...cancelled");
        //System.out.println( "...cancelled" );
        return;
      }
    }
    m_cdObj = new CdObject();
    m_blNew = true;
    m_jTextFieldId.setText( "");
    m_jTextFieldLabel.setText( "");
    m_contentTextArea.setText( "");
    m_contentTextArea.setSearchString( ((CdArchiveDesktop)getDesktopFrame()).getSearchString());
    initCategories();
    m_jMenuItemRead.setEnabled( true);
    m_jButtonRead.setEnabled( true);
  }

  protected void onRead()
  {
    //setCursor(new Cursor( Cursor.WAIT_CURSOR));
    final InternalCancelDlg cdlg = new InternalCancelDlg( this, CDARCH, "Executing ...", null);
    Runnable runner = new Runnable()
    {
      public void run()
      {
        File file = m_fileSystemTree.getSelectedFile();
        if( file == null || !file.isDirectory())
        {
          JOptionPane.showMessageDialog( GlobalAppProperties.instance().getRootComponent( CDARCH), "You have to select a drive or directory!"
                                        , "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        /* jdk14
        if( FileSystemView.getFileSystemView().isFileSystemRoot( file))
        {
          String str = FileSystemView.getFileSystemView().getSystemDisplayName( file);
          int iIdx = str.indexOf( " (");
          str = str.substring( 0, iIdx);
          if( str == null || str.equals( ""))
          {
            str = JOptionPane.showInputDialog( AppProperties.m_rootComp, "Label text?"
                                               , "Label input", JOptionPane.QUESTION_MESSAGE );
          }
          m_cdObj.setLabel( str);
        }
        else
        {
          m_cdObj.setLabel( file.getName());
        }
        */
        // jdk13
        String str = file.getName();
        if( str.endsWith( ":\\" ))
          str = "";
        if( str == null || str.equals( "" ) )
        {
         str = JOptionPane.showInputDialog( GlobalAppProperties.instance().getRootComponent( CDARCH), "Label text?"
                                            , "Label input", JOptionPane.QUESTION_MESSAGE );
        }
        m_cdObj.setLabel( str );
        //end jdk13

        String strText = m_contentTextArea.parseFileStructure( file, m_cdObj.getLabel(), m_vecFilters);
        m_cdObj.setContent( strText);
        updateFields();
      }
    };
    cdlg.setRunner( runner);
    cdlg.start();
    if( cdlg.isCanceled())
      ;
    //setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
  }

  protected void onReload()
  {
    m_fileSystemTree.updateFileSystem();
  }

  protected void onProperties()
  {
    CdArchFramePropertiesDialog dlg = new CdArchFramePropertiesDialog( (Frame)null, "Properties", true, m_vecFilters);
    dlg.setVisible( true);
    if( !dlg.isCancelled())
      m_vecFilters = dlg.getFilters();
    dlg.dispose();
  }

  protected void onOk()
  {
    onApply();
    onExit();
  }

  protected void onApply()
  {
    if( m_jTextFieldLabel.getText() == null || m_jTextFieldLabel.getText().equals( ""))
    {
      JOptionPane.showMessageDialog( GlobalAppProperties.instance().getRootComponent( CDARCH), "You have to add a label!"
                                    , "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if( m_contentTextArea.getText() == null || m_contentTextArea.getText().equals( ""))
    {
      JOptionPane.showMessageDialog( GlobalAppProperties.instance().getRootComponent( CDARCH), "You have to add a content!"
                                    , "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    m_cdObj.setLabel( m_jTextFieldLabel.getText());
    m_cdObj.setContent( m_contentTextArea.getText());
    String strCat = (String)m_jComboBoxCategories.getSelectedItem();
    if( strCat == null || strCat.equals( ""))
    {
      JOptionPane.showMessageDialog( GlobalAppProperties.instance().getRootComponent( CDARCH), "You have to add a category!"
                                    , "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if( m_blNew || ( !m_blNew && !IdManager.instance().isUsedId( m_cdObj.getId())))
    {
      if( m_cdObj.insert( strCat))
      {
        updateFields();
        initCategories();
        m_jComboBoxCategories.setSelectedItem( strCat );
        m_blNew = false;
        m_jMenuItemRead.setEnabled( false);
        m_jButtonRead.setEnabled( false);
      }
    }
    else
    {
      m_cdObj.update( strCat);
    }
  }

  protected void onCancel()
  {
    onExit();
  }

  protected void onExit()
  {
    super.onExit();
  }

}
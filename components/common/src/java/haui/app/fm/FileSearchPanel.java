package haui.app.fm;

import haui.components.VerticalFlowLayout;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.filter.MultiFileInterfaceFilter;
import haui.io.FileInterface.filter.WildcardFileInterfaceFilter;
import haui.tool.shell.engine.JShellEnv;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Module:      FileSearchPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileSearchPanel.java,v $ <p> Description: file search panel.<br> </p><p> Created:     19.08.2004 by AE </p><p>
 * @history      19.08.2004 AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileSearchPanel.java,v $  Revision 1.0  2004-08-31 15:57:36+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: 1.0 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileSearchPanel.java,v 1.0 2004-08-31 15:57:36+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.2  </p>
 */
public class FileSearchPanel
  extends JPanel
{
  private static final long serialVersionUID = -6147607936685099577L;
  
  // property constants
  public static short MAXCOMBOHIST = 20;
  public final static String FSP = "FileSearchPanel.";
  public final static String FILETXT = "FileText";
  public final static String SEARCHTEXT = "SearchText";
  public final static String SEARCHPATH = "SearchPath";
  public final static String FOUNDTEXT = " File(s) found";

  // member variables
  protected SearchThread m_sth = null;
  protected FileInfoPanel m_fip = null;
  protected AppProperties m_appProps;
  protected String m_strAppName;
  protected String m_strFileText;
  protected String m_strSearchPath;
  /**
   * @uml.property  name="m_fiMarked"
   * @uml.associationEnd  multiplicity="(0 -1)"
   */
  protected FileInterface[] m_fiMarked;
  protected boolean m_blMarked = false;
  protected boolean m_blSearchArchives = false;
  protected boolean m_blTextSearch = false;
  protected String m_strSearchText;
  protected boolean m_blWholeWord = false;
  protected boolean m_blMatchCase = false;
  protected boolean m_blNotTextSearch = false;
  protected Vector m_vecResult = null;

  // GUI member variables
  protected LisAction m_actionlistener;
  protected MouseHandler m_handMouse;
  protected BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelResults = new JPanel();
  BorderLayout m_borderLayoutResults = new BorderLayout();
  JScrollPane m_jScrollPaneFound = new JScrollPane();
  JList m_jListFound = new JList();
  DefaultListModel m_defLM = new DefaultListModel();
  JTextField m_jTextFieldResult = new JTextField();
  JPanel m_jPanelMain = new JPanel();
  BorderLayout m_borderLayoutMain = new BorderLayout();
  VerticalFlowLayout m_verticalFlowLayoutButtons = new VerticalFlowLayout();
  JButton m_jButtonCancel = new JButton();
  JPanel m_jPanelButtons = new JPanel();
  JButton m_jButtonSearch = new JButton();
  JLabel m_jLabelDummy = new JLabel();
  JPanel m_jPanelRightTop = new JPanel();
  BorderLayout m_borderLayoutGeneral = new BorderLayout();
  JCheckBox m_jCheckBoxMatchCase = new JCheckBox();
  JPanel m_jPanelLeftTop = new JPanel();
  JCheckBox m_jCheckBoxTextSearch = new JCheckBox();
  JCheckBox m_jCheckBoxNot = new JCheckBox();
  JComboBox m_jComboBoxPath = new JComboBox();
  JComboBox m_jComboBoxTextSearch = new JComboBox();
  JLabel m_jLabelPath = new JLabel();
  JCheckBox m_jCheckBoxWholeWord = new JCheckBox();
  JPanel m_jPanelButtom = new JPanel();
  JLabel m_jLabelDummy2 = new JLabel();
  GridLayout m_gridLayoutLeftTop = new GridLayout();
  JCheckBox m_jCheckBoxMarked = new JCheckBox();
  GridLayout m_gridLayoutRightBottom = new GridLayout();
  JTabbedPane m_jTabbedPaneSearch = new JTabbedPane();
  BorderLayout m_borderLayoutPath = new BorderLayout();
  JPanel m_jPanelPath = new JPanel();
  BorderLayout m_borderLayoutBottom = new BorderLayout();
  JPanel m_jPanelGeneral = new JPanel();
  protected JComboBox m_jComboBoxSearch = new JComboBox();
  JLabel m_jLabelSearch = new JLabel();
  JButton m_jButtonBrowse = new JButton();
  GridLayout m_gridLayoutLeftBottom = new GridLayout();
  JPanel m_jPanelLeftBottom = new JPanel();
  JCheckBox m_jCheckBoxArchive = new JCheckBox();
  JPanel m_jPanelTop = new JPanel();
  JPanel m_jPanelRightBottom = new JPanel();
  GridLayout m_gridLayoutRightTop = new GridLayout();
  JLabel m_jLabelDummy1 = new JLabel();
  BorderLayout m_borderLayoutTop = new BorderLayout();
  JProgressBar m_jProgressBarResults = new JProgressBar();

  public FileSearchPanel( FileInfoPanel fip, AppProperties appProps, String strAppName)
  {
    m_fip = fip;
    m_appProps = appProps;
    m_strAppName = strAppName;
    try
    {
      jbInit();
      m_jListFound.setModel( m_defLM);

      m_handMouse = new MouseHandler();
      m_jListFound.addMouseListener( m_handMouse);
      m_actionlistener = new LisAction();
      m_jButtonSearch.addActionListener( m_actionlistener);
      m_jButtonCancel.addActionListener( m_actionlistener);
      m_jButtonBrowse.addActionListener( m_actionlistener);
      m_jCheckBoxTextSearch.addActionListener( m_actionlistener);

      enableTextSearch( m_jCheckBoxTextSearch.isSelected());
      initComboHist( m_jComboBoxSearch);
      initComboHist( m_jComboBoxPath);
      initComboHist( m_jComboBoxTextSearch);

      if( fip != null)
      {
        addItem( m_jComboBoxPath, m_fip.getFileInfoTable().getAbsolutePath(), false);
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    m_jLabelDummy1.setText(" ");
    m_gridLayoutRightTop.setVgap(0);
    m_gridLayoutRightTop.setRows(4);
    m_gridLayoutRightTop.setColumns(1);
    m_jPanelRightBottom.setLayout(m_gridLayoutRightBottom);
    m_jPanelTop.setLayout(m_borderLayoutTop);
    m_jPanelTop.setBorder(BorderFactory.createEtchedBorder());
    m_jCheckBoxArchive.setText("Search archives");
    m_jPanelLeftBottom.setLayout(m_gridLayoutLeftBottom);
    m_gridLayoutLeftBottom.setRows(4);
    m_gridLayoutLeftBottom.setColumns(1);
    m_jButtonBrowse.setActionCommand("browse");
    m_jButtonBrowse.setText("Browse");
    m_jLabelSearch.setText("Search for:");
    m_jComboBoxSearch.setEditable(true);
    m_jPanelGeneral.setLayout(m_borderLayoutGeneral);
    m_jPanelPath.setLayout(m_borderLayoutPath);
    m_gridLayoutRightBottom.setVgap(0);
    m_gridLayoutRightBottom.setRows(4);
    m_gridLayoutRightBottom.setColumns(1);
    m_jCheckBoxMarked.setText("Only search marked Files");
    m_gridLayoutLeftTop.setRows(4);
    m_gridLayoutLeftTop.setColumns(1);
    m_jLabelDummy2.setText(" ");
    m_jPanelButtom.setLayout(m_borderLayoutBottom);
    m_jPanelButtom.setBorder(BorderFactory.createEtchedBorder());
    m_jCheckBoxWholeWord.setEnabled(true);
    m_jCheckBoxWholeWord.setText("Whole word only");
    m_jLabelPath.setText("Search in:");
    m_jComboBoxTextSearch.setEnabled(true);
    m_jComboBoxTextSearch.setEditable(true);
    m_jComboBoxPath.setEditable(true);
    m_jCheckBoxNot.setText("Find files without this text");
    m_jCheckBoxTextSearch.setText("Search text:");
    m_jCheckBoxTextSearch.setActionCommand("textsearch");
    m_jPanelLeftTop.setLayout(m_gridLayoutLeftTop);
    m_jCheckBoxMatchCase.setEnabled(true);
    m_jCheckBoxMatchCase.setDoubleBuffered(false);
    m_jCheckBoxMatchCase.setText("Match case");
    m_jPanelRightTop.setLayout(m_gridLayoutRightTop);
    m_jLabelDummy.setText(" ");
    m_jButtonSearch.setText("Search");
    m_jButtonSearch.setActionCommand("search");
    m_jPanelButtons.setLayout(m_verticalFlowLayoutButtons);
    m_jButtonCancel.setText("Cancel");
    m_jButtonCancel.setActionCommand("cancel");
    this.setLayout(m_borderLayoutBase);
    m_jPanelResults.setLayout(m_borderLayoutResults);
    m_jTextFieldResult.setEditable(false);
    m_jTextFieldResult.setText("0" + FOUNDTEXT);
    m_jPanelMain.setLayout(m_borderLayoutMain);
    m_jScrollPaneFound.setPreferredSize(new Dimension(200, 150));
    this.add(m_jPanelResults,  BorderLayout.CENTER);
    m_jPanelResults.add(m_jScrollPaneFound,  BorderLayout.CENTER);
    m_jScrollPaneFound.setViewportView( m_jListFound);
    m_jPanelResults.add(m_jTextFieldResult,  BorderLayout.SOUTH);
    m_jPanelResults.add(m_jProgressBarResults, BorderLayout.NORTH);
    this.add(m_jPanelMain, BorderLayout.NORTH);
    m_jPanelButtons.add(m_jLabelDummy, null);
    m_jPanelButtons.add(m_jButtonSearch, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelMain.add(m_jTabbedPaneSearch,  BorderLayout.CENTER);
    m_jTabbedPaneSearch.add(m_jPanelGeneral, "General");
    m_jPanelButtom.add(m_jPanelLeftBottom, BorderLayout.WEST);
    m_jPanelLeftBottom.add(m_jCheckBoxTextSearch, null);
    m_jPanelButtom.add(m_jPanelRightBottom, BorderLayout.CENTER);
    m_jPanelRightBottom.add(m_jComboBoxTextSearch, null);
    m_jPanelRightBottom.add(m_jCheckBoxWholeWord, null);
    m_jPanelRightBottom.add(m_jCheckBoxMatchCase, null);
    m_jPanelRightBottom.add(m_jCheckBoxNot, null);
    m_jPanelGeneral.add(m_jPanelTop, BorderLayout.CENTER);
    m_jPanelGeneral.add(m_jPanelButtom, BorderLayout.SOUTH);
    m_jPanelLeftTop.add(m_jLabelSearch, null);
    m_jPanelLeftTop.add(m_jLabelPath, null);
    m_jPanelLeftTop.add(m_jLabelDummy1, null);
    m_jPanelLeftTop.add(m_jLabelDummy2, null);
    m_jPanelTop.add(m_jPanelRightTop, BorderLayout.CENTER);
    m_jPanelTop.add(m_jPanelLeftTop, BorderLayout.WEST);
    m_jPanelRightTop.add(m_jComboBoxSearch, null);
    m_jPanelRightTop.add(m_jPanelPath, null);
    m_jPanelPath.add(m_jComboBoxPath, BorderLayout.CENTER);
    m_jPanelPath.add(m_jButtonBrowse, BorderLayout.EAST);
    m_jPanelRightTop.add(m_jCheckBoxMarked, null);
    m_jPanelRightTop.add(m_jCheckBoxArchive, null);
    m_jPanelMain.add(m_jPanelButtons,  BorderLayout.EAST);
  }

  protected void addItem( JComboBox jcb, Object obj, boolean blInit)
  {
    boolean blFound = false;
    for( int i = 0; i < jcb.getItemCount(); ++i)
    {
      if( jcb.getItemAt( i).equals( obj))
      {
        blFound = true;
        jcb.setSelectedItem( obj );
        break;
      }
    }
    if( !blFound)
    {
      if( jcb.getItemCount() == 0)
        jcb.addItem( obj);
      else
        jcb.insertItemAt( obj, 0);
      jcb.setSelectedItem( obj );
      if( jcb.getItemCount() > MAXCOMBOHIST)
        jcb.removeItemAt( jcb.getItemCount()-1);
    }
    if( !blInit)
    {
      for( int i = 0; i < jcb.getItemCount(); ++i )
      {
        eraseComboHist( jcb );
        storeComboHist( jcb );
      }
    }
  }

  protected JButton getDeaultButton()
  {
    return m_jButtonSearch;
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event )
    {
      String cmd = event.getActionCommand();
      if( cmd.equals( "search"))
      {
        search();
      }
      else if( cmd.equals( "cancel"))
      {
        cancel();
      }
      else if( cmd.equals( "textsearch"))
      {
        enableTextSearch( m_jCheckBoxTextSearch.isSelected());
      }
      else if (cmd.equals( "browse"))
      {
        browse();
      }
    }
  }

  public void browse()
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
    int returnVal = chooser.showOpenDialog( getParent());
    if(returnVal == JFileChooser.APPROVE_OPTION)
    {
      if( chooser.getSelectedFile() != null && chooser.getSelectedFile().getAbsolutePath() != null)
      {
        String str = chooser.getSelectedFile().getAbsolutePath();
        addItem( m_jComboBoxPath, str, false);
      }
    }
  }

  public void updateStatus()
  {
    m_jTextFieldResult.setText( String.valueOf( m_defLM.size()) + FOUNDTEXT);
  }

  public void updateStatusSearching()
  {
    m_jTextFieldResult.setText( String.valueOf( "Searching... " + m_defLM.size()) + FOUNDTEXT);
  }

  public void search()
  {
    if( m_sth != null && m_sth.isAlive())
    {
      cancel();
        return;
    }
    m_defLM.removeAllElements();
    updateStatusSearching();
    m_strFileText = (String)m_jComboBoxSearch.getSelectedItem();
    if( m_strFileText == null || m_strFileText.equals( ""))
      m_strFileText = "*";
    else
      addItem( m_jComboBoxSearch, m_strFileText, false);
    m_strSearchPath = (String)m_jComboBoxPath.getSelectedItem();
    if( m_strSearchPath == null || m_strSearchPath.equals( ""))
    {
      if( m_fip.getFileInfoTable() != null && m_fip.getFileInfoTable().getFileInterface() != null
          && m_fip.getFileInfoTable().getFileInterface().getRootFileInterface() != null)
      {
        m_strSearchPath = m_fip.getFileInfoTable().getFileInterface().getRootFileInterface().getAbsolutePath();
        addItem( m_jComboBoxPath, m_strSearchPath, false);
      }
    }
    else
      addItem( m_jComboBoxPath, m_strSearchPath, false);
    m_blSearchArchives = m_jCheckBoxArchive.isSelected();
    m_blMarked = m_jCheckBoxMarked.isSelected();
    m_blTextSearch = m_jCheckBoxTextSearch.isSelected();

    if( m_blTextSearch)
    {
      m_strSearchText = ( String )m_jComboBoxTextSearch.getSelectedItem();
      if( m_strSearchText == null)
        m_strSearchText = "";
      else
        addItem( m_jComboBoxTextSearch, m_strSearchText, false);
      m_blMatchCase = m_jCheckBoxMatchCase.isSelected();
      m_blWholeWord = m_jCheckBoxWholeWord.isSelected();
      m_blNotTextSearch = m_jCheckBoxNot.isSelected();
    }

    m_sth = new SearchThread();
    m_sth.start();
  }

  public boolean cancel()
  {
    boolean blRet = false;
    if( m_sth != null && m_sth.isAlive())
    {
      blRet = true;
      int iRet = JOptionPane.showConfirmDialog( this, "Cancel search?", "Cancel",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if( iRet == JOptionPane.YES_OPTION)
      {
        m_sth.stopIt();
        updateStatus();
      }
    }
    return blRet;
  }

  public boolean isAlive()
  {
    return m_sth != null && m_sth.isAlive();
  }

  protected void enableTextSearch( boolean bl)
  {
    m_jComboBoxTextSearch.setEnabled( bl);
    m_jCheckBoxMatchCase.setEnabled( bl);
    m_jCheckBoxWholeWord.setEnabled( bl);
    m_jCheckBoxNot.setEnabled( bl);
  }

  protected void eraseComboHist( JComboBox jcb)
  {
    String strPrefix = getPrefix( jcb);
    for( int i = 0; m_appProps.getProperty( strPrefix + String.valueOf( i)) != null; i++)
    {
      m_appProps.remove( strPrefix + String.valueOf( i));
    }
  }

  protected void storeComboHist( JComboBox jcb)
  {
    String strPrefix = getPrefix( jcb);
    for( int i = 0; i < jcb.getItemCount(); i++)
    {
      String str = (String)jcb.getItemAt( i);
      m_appProps.setProperty( strPrefix + String.valueOf( i), str);
    }
  }

  protected void initComboHist( JComboBox jcb)
  {
    String strPrefix = getPrefix( jcb);
    String str;

    for( int i = 0; (str = m_appProps.getProperty( strPrefix + String.valueOf( i))) != null; i++)
    {
      addItem( jcb, str, true);
    }
  }

  private String getPrefix( JComboBox jcb)
  {
    String strPrefix = FSP;
    if( jcb == m_jComboBoxSearch)
      strPrefix += FILETXT;
    else if( jcb == m_jComboBoxPath)
      strPrefix += SEARCHPATH;
    else if( jcb == m_jComboBoxTextSearch)
      strPrefix += SEARCHTEXT;
    return strPrefix;
  }

  class MouseHandler
    extends MouseAdapter
  {
    public void mouseClicked( MouseEvent event )
    {
      //Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 2 )
        rightMouseDoublePressed( event );
      else if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 1 )
        rightMousePressed( event );
      else if( event.getModifiers() == InputEvent.BUTTON2_MASK && event.getClickCount() == 2 )
        middleMouseDoublePressed( event );
      else if( event.getModifiers() == InputEvent.BUTTON2_MASK && event.getClickCount() == 1 )
        middleMousePressed( event );
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2 )
        leftMouseDoublePressed( event );
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 1 )
        leftMousePressed( event );
    }
  }

  void leftMousePressed( MouseEvent event)
  {
  }

  void leftMouseDoublePressed( MouseEvent event)
  {
    String str = (String)m_jListFound.getSelectedValue();
    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
        m_fip.getFileInfoTable().getConnectionObject(), 0, 0, FileManager.APPNAME,
        m_fip.getFileInfoTable().getFileInterface().getAppProperties(), true);
    FileInterface fi = FileConnector.createFileInterface( str, null, true, fic);
    if( fi.isDirectory())
    {
      m_fip.init( fi);
    }
    else
    {
      m_fip.init( fi.getParent(), null,
                  m_fip.getFileInfoTable().getConnectionObject(), true);
      int iIdx = m_fip.getFileInfoTable().findFileInterfaceIndex( fi.getAbsolutePath());
      if( iIdx != -1)
        m_fip.getFileInfoTable().setRowSelectionInterval( iIdx, iIdx);
    }
  }

  void middleMousePressed( MouseEvent event)
  {
  }

  void middleMouseDoublePressed( MouseEvent event)
  {
  }

  void rightMousePressed( MouseEvent event)
  {
  }

  void rightMouseDoublePressed( MouseEvent event)
  {
  }

  class SearchThread
    extends Thread
  {
    private boolean m_blRun = false;
    private char[] m_cWordSeperators = { ' ', ',', '.', ':', ';', '\'', '"', '\n', '\t', '+',
                                           '-', '*', '/', '\\', '(', ')', '[', ']', '{', '}',
                                           '!', '?', '´', '`', '^', '~', '&', '%', '|', '¦',
                                           '@', '#', '€', '<', '>', '§', '°' };

    public SearchThread()
    {
      super( "SearchThread");
    }

    public void stopIt()
    {
      m_blRun = false;
      try
      {
        sleep( 200);
        if( this.isAlive())
        {
          interrupt();
          sleep( 200);
          if( this.isAlive())
          {
            stop();
          }
        }
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }

    public void run()
    {
      m_blRun = true;
      try
      {
        m_vecResult = getFileInterfaces( m_fip.getFileInfoTable().getFileInterface(),
                                      m_fip.getShell().getShellEnv(), m_strSearchPath, m_strFileText,
                                      m_blMarked, true);
        updateStatus();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
      m_blRun = false;
    }

    public Vector getFileInterfaces( FileInterface fi, JShellEnv jse, String strPath, String strFileText,
                                     boolean blMarked, boolean blStart)
    {
      //FileInterface fiSource = null;
      WildcardFileInterfaceFilter wfif = new WildcardFileInterfaceFilter( strFileText, FileManager.APPNAME );
      MultiFileInterfaceFilter mfif = new MultiFileInterfaceFilter( FileConnector.SUPPORTEDFILES, FileManager.APPNAME);
      Vector vec = new Vector();

      if( blMarked)
      {
        FileInterface[] fis = m_fip.getFileInfoTable().getSelectedFileInterfaces();
        if( blStart)
        {
          m_jProgressBarResults.setMinimum( 0);
          m_jProgressBarResults.setMaximum( fis.length);
          m_jProgressBarResults.setValue( 0);
        }

        for( int i = 0; i < fis.length && m_blRun; ++i)
        {
          FileInterface fint = fis[i];
          if( wfif.accept( fint ) && accept( fint) )
          {
            m_defLM.addElement( fint.getAbsolutePath() );
            updateStatusSearching();
            vec.add( fint );
          }
          if( fint.isDirectory() || ( fint.isArchive() && m_blSearchArchives ) )
          {
            Vector vecSub = getFileInterfaces( fi, jse,
                                               fint.getAbsolutePath(),
                                               strFileText, false, false );
            vec.addAll( vecSub );
          }
          if( blStart)
            m_jProgressBarResults.setValue( i+1);
        }
      }
      else
      {
        vec = getSourceFileInterfaces( fi, jse, strPath, wfif, true, true );
        Vector vecDirs = getSourceFileInterfaces( fi, jse, strPath, mfif, false, true );

        if( blStart)
        {
          m_jProgressBarResults.setMinimum( 0);
          m_jProgressBarResults.setMaximum( vecDirs.size());
          m_jProgressBarResults.setValue( 0);
        }

        for( int i = 0; i < vecDirs.size() && m_blRun; ++i )
        {
          FileInterface fiTmp = ( FileInterface )vecDirs.elementAt( i );
          if( fiTmp.isDirectory() || ( fiTmp.isArchive() && m_blSearchArchives ) )
          {
            Vector vecSub = getFileInterfaces( fi, jse,
                                               fiTmp.getAbsolutePath(),
                                               strFileText, false, false );
            vec.addAll( vecSub );
          }
          if( blStart)
            m_jProgressBarResults.setValue( i+1);
        }
      }
      return vec;
    }

    public Vector getSourceFileInterfaces( FileInterface fi, JShellEnv jse, String strCurPath,
                                                  FileInterfaceFilter fif, boolean blAdd, boolean blExtract)
    {
      Vector vecFi = new Vector();
      FileInterface fiRet[] = null;

      if( m_blTextSearch && !m_blMatchCase)
        m_strSearchText = m_strSearchText.toLowerCase();
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
          fi.getConnObj(), 0, 0, FileManager.APPNAME,
          jse.getAppPropperties(), true);
      FileInterface fiDir = FileConnector.createFileInterface( strCurPath, null, blExtract, fic);
      if( fiDir != null /* && fiDir.exists() //because FtpFile always returns false */
          && ( fiDir.isDirectory()
               || ( m_blSearchArchives && fiDir.isArchive() ) ) )
      {
        fiRet = fiDir._listFiles( fif );
        if( fiRet != null )
        {
          for( int i = 0; i < fiRet.length; ++i )
          {
            if( blAdd )
            {
              if( accept( fiRet[i]))
              {
                m_defLM.addElement( fiRet[i].getAbsolutePath() );
                updateStatusSearching();
              }
            }
            vecFi.add( fiRet[i] );
          }
        }
      }
      return vecFi;
    }

    private boolean accept( FileInterface fi)
    {
      boolean blFound = false;

      if( m_blTextSearch)
      {
        try
        {
          BufferedReader br = new BufferedReader( new InputStreamReader( fi.getBufferedInputStream() ) );
          String str = null;
          int iIdx = -1;
          int iLen = m_strSearchText.length();

          while( (str = br.readLine()) != null && !blFound)
          {
            if( !m_blMatchCase)
              str = str.toLowerCase();
            if( ( iIdx = str.indexOf( m_strSearchText)) != -1)
            {
              blFound = true;
              if( m_blWholeWord)
              {
                boolean blOk = false;
                do
                {
                  blOk = false;
                  if( iIdx > 0)
                  {
                    char c = str.charAt( iIdx-1);
                    for( int ii = 0; ii < m_cWordSeperators.length; ++ii)
                    {
                      if( c == m_cWordSeperators[ii])
                      {
                        blOk = true;
                        break;
                      }
                    }
                  }
                  else if(iIdx == 0)
                    blOk = true;
                  if( blOk)
                  {
                    blOk = false;
                    if( iIdx+iLen < str.length())
                    {
                      char c = str.charAt( iIdx+iLen);
                      for( int ii = 0; ii < m_cWordSeperators.length; ++ii)
                      {
                        if( c == m_cWordSeperators[ii])
                        {
                          blOk = true;
                          break;
                        }
                      }
                    }
                    else if( iIdx+iLen == str.length())
                      blOk = true;
                  }
                  if( blOk)
                    blFound = true;
                  else
                    blFound = false;
                  if( iIdx+iLen < str.length())
                    str = str.substring( iIdx+iLen);
                  else
                    str = "";
                }while( ( iIdx = str.indexOf( m_strSearchText)) != -1);
              }
            }
          }
        }
        catch( IOException ex )
        {
          GlobalApplicationContext.instance().getOutputPrintStream().println( ex.toString());
        }
      }
      else
        blFound = true;
      if( m_blNotTextSearch)
      {
        if( blFound)
          blFound = false;
        else
          blFound = true;
      }
      return blFound;
    }
  }
}
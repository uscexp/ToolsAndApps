package haui.app.cdarchive;

import haui.components.VerticalFlowLayout;
import haui.components.desktop.JExInternalDialog;
import haui.util.GlobalAppProperties;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * Module:      CdArchSearchDialog.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchSearchDialog.java,v $
 *<p>
 * Description: CdArchDtPropertiesDialog.<br>
 *</p><p>
 * Created:     09.05.2003 by AE
 *</p><p>
 * @history	    09.05.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: CdArchSearchDialog.java,v $
 * Revision 1.2  2004-08-31 16:03:05+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.1  2004-02-17 16:06:38+01  t026843
 * <>
 *
 * Revision 1.0  2003-05-21 16:24:39+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.2 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchSearchDialog.java,v 1.2 2004-08-31 16:03:05+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class CdArchSearchDialog
  extends JExInternalDialog
{
  // constants

  // member variables
  protected CdObject m_cdObj;
  protected CdArchiveDesktop m_dt;
  protected Vector m_vecCategories;

  // GUI member variables
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JLabel m_jLabelLeft = new JLabel();
  JLabel m_jLabelRight = new JLabel();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonDelete = new JButton();
  JButton m_jButtonCancel = new JButton();
  ButtonGroup m_buttonGroupOpen = new ButtonGroup();
  JLabel m_jLabelSearch = new JLabel();
  JPanel m_jPanelSearch = new JPanel();
  JPanel jPanelGeneral = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutOpen = new VerticalFlowLayout();
  VerticalFlowLayout m_verticalFlowLayoutGeneral = new VerticalFlowLayout();
  JPanel m_jPanelRadioButtons = new JPanel();
  FlowLayout m_flowLayoutRadioButtons = new FlowLayout();
  JLabel m_jLabelCategories = new JLabel();
  JComboBox m_jComboBoxCategories = new JComboBox();
  JRadioButton m_jRadioButtonId = new JRadioButton();
  JRadioButton m_jRadioButtonLabel = new JRadioButton();
  JRadioButton m_jRadioButtonContent = new JRadioButton();
  JTextField m_jTextFieldSearch = new JTextField();
  JScrollPane m_jScrollPaneResult = new JScrollPane();
  JList m_jListResult = new JList();
  JPanel m_jPanelSearchButton = new JPanel();
  FlowLayout m_flowLayoutSearchButton = new FlowLayout();
  JButton m_jButtonSearch = new JButton();

  public CdArchSearchDialog(CdArchiveDesktop frame, String title, boolean modal, Vector vecCategories)
  {
    //super( frame, title, false, false, false, false);
    super( frame, null, CdArchiveDesktop.CDARCHDT, title, false);
    m_dt = frame;
    m_vecCategories = vecCategories;
    //m_dt.getDesktopPane().add( this);
    try
    {
      jbInit();
      pack();
      m_jRadioButtonId.setSelected( true);
      m_jButtonOk.requestFocus();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    m_buttonGroupOpen.add( m_jRadioButtonContent);
    m_buttonGroupOpen.add( m_jRadioButtonId);
    m_buttonGroupOpen.add( m_jRadioButtonLabel);
    _init();
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonDelete.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
    m_jButtonSearch.addActionListener( actionlis);

    MouseHandler mh = new MouseHandler();
    m_jListResult.addMouseListener( mh);
    m_jListResult.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
  }

  public CdArchSearchDialog( CdArchiveDesktop dt, Vector vecCategories)
  {
    this(dt, "search", false, vecCategories);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jLabelLeft.setText(" ");
    m_jLabelRight.setText(" ");
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonDelete.setText("Delete");
    m_jButtonCancel.setText("Cancel");
    m_jLabelSearch.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelSearch.setText("Search entry:");
    m_jPanelSearch.setLayout(m_verticalFlowLayoutOpen);
    m_jPanelSearch.setBorder(BorderFactory.createEtchedBorder());
    jPanelGeneral.setLayout(m_verticalFlowLayoutGeneral);
    m_jLabelCategories.setText("Category:");
    m_jPanelRadioButtons.setLayout(m_flowLayoutRadioButtons);
    m_jRadioButtonId.setText("Id");
    m_jRadioButtonId.setActionCommand("openmaxtoop");
    m_jRadioButtonLabel.setActionCommand("openmax");
    m_jRadioButtonLabel.setText("Label");
    m_jRadioButtonContent.setText("Content");
    m_jRadioButtonContent.setActionCommand("opencascade");
    m_jPanelSearchButton.setLayout(m_flowLayoutSearchButton);
    m_jButtonSearch.setActionCommand("search");
    m_jButtonSearch.setText("Search");
    m_jPanelSearchButton.add(m_jButtonSearch, null);
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelBase.add(m_jLabelLeft, BorderLayout.WEST);
    m_jPanelBase.add(m_jLabelRight, BorderLayout.EAST);
    m_jPanelBase.add(jPanelGeneral,  BorderLayout.NORTH);
    jPanelGeneral.add(m_jPanelSearch, null);
    m_jPanelSearch.add(m_jLabelSearch, null);
    m_jPanelSearch.add(m_jLabelCategories, null);
    m_jPanelSearch.add(m_jComboBoxCategories, null);
    m_jPanelSearch.add(m_jPanelRadioButtons, null);
    m_jPanelSearch.add(m_jTextFieldSearch, null);
    m_jPanelSearch.add(m_jPanelSearchButton, null);
    m_jPanelBase.add(m_jScrollPaneResult, BorderLayout.CENTER);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonDelete, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelRadioButtons.add(m_jRadioButtonId, null);
    m_jPanelRadioButtons.add(m_jRadioButtonLabel, null);
    m_jPanelRadioButtons.add(m_jRadioButtonContent, null);
    m_jScrollPaneResult.getViewport().add(m_jListResult, null);
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

  public void _init()
  {
    initCategories();
  }

  public void _save()
  {
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if( cmd == "Ok" )
        onOk();
      else if( cmd == "Delete" )
        onDelete();
      else if( cmd == "Cancel" )
        onExit();
      else if( cmd == "search" )
        onSearch();
    }
  }

  class MouseHandler
    extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 2)
        rightMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 1)
        rightMousePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON2_MASK && event.getClickCount() == 2)
        middleMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON2_MASK && event.getClickCount() == 1)
        middleMousePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2)
        leftMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 1)
        leftMousePressed( event);
    }

    public void mouseReleased(MouseEvent event)
    {
      Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON1_MASK)
        leftMouseReleased( event);
      else if( (event.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && ((event.getModifiers() & InputEvent.SHIFT_MASK) != 0
              || (event.getModifiers() & InputEvent.CTRL_MASK) != 0))
        leftMouseReleased( event);
    }
  }

  void leftMousePressed( MouseEvent event)
  {
  }

  void leftMouseDoublePressed( MouseEvent event)
  {
    showInternalFrame();
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

  void leftMouseReleased( MouseEvent event)
  {
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
    }
    super.setVisible( b);
  }

  protected void showInternalFrame()
  {
    setCursor(new Cursor( Cursor.WAIT_CURSOR));
    int iIdx = m_jListResult.getSelectedIndex();
    if( iIdx > -1)
    {
      CdObject cdObj = (CdObject)m_jListResult.getSelectedValue();
      m_dt.createFrame( cdObj, false);
    }
    setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
  }

  protected void onOk()
  {
    _save();
    showInternalFrame();
    onExit();
  }

  protected void onDelete()
  {
    int iIdx;
    if( ( iIdx = m_jListResult.getSelectedIndex()) > -1)
    {
      int iRet = JOptionPane.showConfirmDialog( GlobalAppProperties.instance().getRootComponent( CdArchiveDesktop.CDARCHDT), "Really delete this entry?"
                                                , "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if( iRet == JOptionPane.NO_OPTION)
      {
        GlobalAppProperties.instance().getPrintStreamOutput( CdArchiveDesktop.CDARCHDT).println( "...cancelled");
        //System.out.println( "...cancelled");
        return;
      }
      CdObject cdObj = (CdObject)m_jListResult.getSelectedValue();
      ((DefaultListModel)m_jListResult.getModel()).removeElement( cdObj);
      cdObj.delete();
      initCategories();
    }
  }

  protected void onExit()
  {
    setVisible( false);
    //dispose();
  }

  protected void onSearch()
  {
    setCursor(new Cursor( Cursor.WAIT_CURSOR));
    String str = m_jTextFieldSearch.getText();
    m_dt.setSearchString( str);
    String strCat = (String)m_jComboBoxCategories.getSelectedItem();
    if( strCat == null || strCat.equals( ""))
      strCat = null;
    m_cdObj = new CdObject();
    boolean blResult = false;

    if( m_jRadioButtonId.isSelected())
    {
      try
      {
        if( str != null && !str.equals( ""))
        {
          int iId = Integer.parseInt( str );

          blResult = m_cdObj.findWithId( iId );
        }
      }
      catch( NumberFormatException nfex)
      {
        //nfex.printStackTrace();
      }
    }
    else if( m_jRadioButtonLabel.isSelected())
    {
      blResult = m_cdObj.findWithLabelNoCase( strCat, str);
    }
    else if( m_jRadioButtonContent.isSelected())
    {
      blResult = m_cdObj.findInContent( strCat, str);
    }

    DefaultListModel dlm = new DefaultListModel();
    if( blResult)
    {
      Vector vec = m_cdObj.getResult();

      for( int i = 0; i < vec.size(); ++i)
      {
        CdObject co = (CdObject)vec.elementAt(i);
        dlm.addElement( co);
      }
      /*
      for( int i = 0; i < vecPs.size(); ++i)
      {
        CdObject cdObj = new CdObject();
        PropertyStruct ps = (PropertyStruct)vecPs.elementAt( i);
        cdObj.setId( ps.intValue( CdObject.ID));
        cdObj.setCategory( CdObject.getCategory( ps));
        cdObj.setLabel( ps.stringValue( CdObject.LABEL));
        cdObj.setContent( ps.stringValue( CdObject.CONTENT));
        cdObj._init();
        boolean blInserted = false;
        for( int j = 0; j < dlm.size(); ++j)
        {
          str = ((CdObject)dlm.elementAt( j)).getLabel();
          if( str.compareTo( cdObj.getLabel()) > 0)
          {
            dlm.insertElementAt( cdObj, j );
            blInserted = true;
            break;
          }
        }
        if( !blInserted)
          dlm.addElement( cdObj);
      }
      */
      m_jListResult.setModel( dlm);

      if( m_jListResult.getModel().getSize() > 0)
        m_jListResult.setSelectedIndex( 0);
    }
    else
      m_jListResult.setModel( dlm);

    setCursor(new Cursor( Cursor.DEFAULT_CURSOR));
  }
}

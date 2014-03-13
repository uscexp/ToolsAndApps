package haui.app.cdarchive;

import haui.components.JExDialog;
import haui.components.VerticalFlowLayout;
import haui.util.AppProperties;
import haui.util.GlobalAppProperties;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Module:      CdArchDtPropertiesDialog.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchDtPropertiesDialog.java,v $
 *<p>
 * Description: CdArchDtPropertiesDialog.<br>
 *</p><p>
 * Created:     23.04.2003 by AE
 *</p><p>
 * @history	    23.04.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: CdArchDtPropertiesDialog.java,v $
 * Revision 1.2  2004-08-31 16:03:06+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.1  2004-02-17 16:32:20+01  t026843
 * <>
 *
 * Revision 1.0  2003-05-21 16:24:37+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.2 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchDtPropertiesDialog.java,v 1.2 2004-08-31 16:03:06+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class CdArchDtPropertiesDialog
  extends JExDialog
{
  // constants

  // member variables
  AppProperties m_appProps;
  Vector m_vecCategories;

  // GUI member variables
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JLabel m_jLabelLeft = new JLabel();
  JLabel m_jLabelRight = new JLabel();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  JTabbedPane m_jTabbedPaneCenter = new JTabbedPane();
  JPanel m_jPanelCdArchive = new JPanel();
  ButtonGroup m_buttonGroupOpen = new ButtonGroup();
  JPanel jPanelGeneral = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutGeneral = new VerticalFlowLayout();
  JLabel m_jLabelCategories = new JLabel();
  JRadioButton m_jRadioButtonOpenMax = new JRadioButton();
  JRadioButton m_jRadioButtonOpenMaxToOp = new JRadioButton();
  JPanel m_jPanelOpen = new JPanel();
  JRadioButton m_jRadioButtonOpenCascade = new JRadioButton();
  VerticalFlowLayout m_verticalFlowLayoutOpen = new VerticalFlowLayout();
  JLabel m_jLabelOpen = new JLabel();
  VerticalFlowLayout m_verticalFlowLayoutCdArchive = new VerticalFlowLayout();
  JPanel m_jPanelCdArchiveProps = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutCdArchiveProps = new VerticalFlowLayout();
  JLabel m_jLabelCdArchiveProps = new JLabel();
  JLabel m_jLabelFilter = new JLabel();
  JTextField m_jTextFieldFilter = new JTextField();
  JPanel m_jPanelCategories = new JPanel();
  FlowLayout m_flowLayoutCategories = new FlowLayout();
  JComboBox m_jComboBoxCategories = new JComboBox();
  JButton m_jButtonRename = new JButton();
  JButton m_jButtonDelete = new JButton();

  public CdArchDtPropertiesDialog(Frame frame, String title, boolean modal, AppProperties appProps, Vector vecCategories)
  {
    super(frame, title, modal, CdArchiveDesktop.CDARCHDT);
    m_appProps = appProps;
    m_vecCategories = vecCategories;
    try
    {
      jbInit();
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    m_buttonGroupOpen.add( m_jRadioButtonOpenCascade);
    m_buttonGroupOpen.add( m_jRadioButtonOpenMaxToOp);
    m_buttonGroupOpen.add( m_jRadioButtonOpenMax);
    _init();
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
    m_jButtonRename.addActionListener( actionlis);
    m_jButtonDelete.addActionListener( actionlis);
  }

  public CdArchDtPropertiesDialog( Vector vecCategories)
  {
    this(null, "", false, null, vecCategories);
  }

  void jbInit() throws Exception
  {
    m_jRadioButtonOpenCascade.setText("Cascaded.");
    m_jRadioButtonOpenCascade.setActionCommand("opencascade");
    m_jPanelOpen.setLayout(m_verticalFlowLayoutOpen);
    m_jRadioButtonOpenMaxToOp.setText("Maximized to output.");
    m_jRadioButtonOpenMaxToOp.setActionCommand("openmaxtoop");
    m_jRadioButtonOpenMax.setActionCommand("openmax");
    m_jRadioButtonOpenMax.setText("Maximized.");
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jLabelLeft.setText(" ");
    m_jLabelRight.setText(" ");
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    jPanelGeneral.setLayout(m_verticalFlowLayoutGeneral);
    m_jPanelOpen.setBorder(BorderFactory.createEtchedBorder());
    m_jLabelOpen.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelOpen.setText("Open Windows:");
    m_jPanelCdArchive.setLayout(m_verticalFlowLayoutCdArchive);
    m_jPanelCdArchiveProps.setLayout(m_verticalFlowLayoutCdArchiveProps);
    m_jPanelCdArchiveProps.setBorder(BorderFactory.createEtchedBorder());
    m_jLabelCdArchiveProps.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelCdArchiveProps.setText("Default properties");
    m_jLabelFilter.setText("Filter extensions:");
    m_jLabelCategories.setText("Category:");
    m_jPanelCategories.setLayout(m_flowLayoutCategories);
    m_jComboBoxCategories.setEditable(true);
    m_flowLayoutCategories.setAlignment(FlowLayout.LEFT);
    m_jButtonRename.setActionCommand("rename");
    m_jButtonRename.setText("Rename");
    m_jButtonDelete.setActionCommand("delete");
    m_jButtonDelete.setText("Delete");
    m_jPanelCategories.add(m_jComboBoxCategories, null);
    m_jPanelCategories.add(m_jButtonRename, null);
    m_jPanelCategories.add(m_jButtonDelete, null);
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelBase.add(m_jLabelLeft, BorderLayout.WEST);
    m_jPanelBase.add(m_jLabelRight, BorderLayout.EAST);
    m_jPanelBase.add(m_jTabbedPaneCenter, BorderLayout.CENTER);
    m_jTabbedPaneCenter.add(jPanelGeneral, "General");
    jPanelGeneral.add(m_jPanelOpen, null);
    m_jPanelOpen.add(m_jLabelOpen, null);
    m_jPanelOpen.add(m_jRadioButtonOpenCascade, null);
    m_jPanelOpen.add(m_jRadioButtonOpenMaxToOp, null);
    m_jPanelOpen.add(m_jRadioButtonOpenMax, null);
    m_jTabbedPaneCenter.add(m_jPanelCdArchive, "CD Archive");
    m_jPanelCdArchive.add(m_jPanelCdArchiveProps, null);
    m_jPanelCdArchiveProps.add(m_jLabelCdArchiveProps, null);
    m_jPanelCdArchiveProps.add(m_jLabelFilter, null);
    m_jPanelCdArchiveProps.add(m_jTextFieldFilter, null);
    m_jPanelCdArchiveProps.add(m_jLabelCategories, null);
    m_jPanelCdArchiveProps.add(m_jPanelCategories, null);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
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
    if( m_vecCategories.size() > 0)
      m_jComboBoxCategories.setSelectedIndex( 0);
  }

  public void _init()
  {
    boolean bl = m_appProps.getBooleanProperty( CdArchiveDesktop.WINCASCADED);
    m_jRadioButtonOpenCascade.setSelected( bl);
    bl = m_appProps.getBooleanProperty( CdArchiveDesktop.WINMAXTOOP);
    m_jRadioButtonOpenMaxToOp.setSelected( bl);
    bl = m_appProps.getBooleanProperty( CdArchiveDesktop.WINMAX);
    m_jRadioButtonOpenMax.setSelected( bl);
    Vector vecFilters = (Vector)m_appProps.getObjectProperty( CdArchiveDesktop.FILTERS);
    if( vecFilters != null)
    {
      String strText = "";
      for( int i = 0; i < vecFilters.size(); ++i)
      {
        String str = (String)vecFilters.elementAt( i);
        if( i == 0)
          strText = str;
        else
          strText += ", " + str;
      }
      m_jTextFieldFilter.setText( strText);
    }
    initCategories();
  }

  public void _save()
  {
    boolean bl = m_jRadioButtonOpenCascade.isSelected();
    m_appProps.setBooleanProperty( CdArchiveDesktop.WINCASCADED, bl);
    bl = m_jRadioButtonOpenMaxToOp.isSelected();
    m_appProps.setBooleanProperty( CdArchiveDesktop.WINMAXTOOP, bl);
    bl = m_jRadioButtonOpenMax.isSelected();
    m_appProps.setBooleanProperty( CdArchiveDesktop.WINMAX, bl);
    String strText = m_jTextFieldFilter.getText();
    if( strText != null && !strText.equals(""))
    {
      StringTokenizer st = new StringTokenizer( strText, ",;", false);
      String str;
      Vector vecFilters = new Vector();

      while( st.hasMoreTokens())
      {
        str = st.nextToken().trim();
        vecFilters.add( str);
      }
      if( vecFilters.size() > 0)
      {
        m_appProps.setObjectProperty( CdArchiveDesktop.FILTERS, vecFilters);
      }
    }
    CdArchiveDesktop.addCategory( (String)m_jComboBoxCategories.getSelectedItem());
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        onOk();
      else if (cmd == "Cancel")
        setVisible( false);
      else if (cmd == "rename")
        onRename();
      else if (cmd == "delete")
        onDelete();
    }
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
    }
    super.setVisible( b);
  }

  protected void onOk()
  {
    _save();
    setVisible( false);
  }

  protected void onRename()
  {
    String strOldCategory = (String)m_jComboBoxCategories.getSelectedItem();

    String strNewCategory = JOptionPane.showInputDialog( GlobalAppProperties.instance().getRootComponent( CdArchiveDesktop.CDARCHDT) , "New category name?"
                                                         , "Rename category", JOptionPane.QUESTION_MESSAGE );

    if( strOldCategory == null || strOldCategory.equals( "") || strNewCategory == null || strNewCategory.equals( ""))
      return;
    int iRet = JOptionPane.showConfirmDialog( GlobalAppProperties.instance().getRootComponent( CdArchiveDesktop.CDARCHDT)
                                              , "Rename category and move all entries to new category?"
                                              , "Alert", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if( iRet == JOptionPane.NO_OPTION)
    {
      GlobalAppProperties.instance().getPrintStreamOutput( CdArchiveDesktop.CDARCHDT).println( "...cancelled");
      //System.out.println( "...cancelled");
      return;
    }
    CdArchiveDesktop.renameCategory( strOldCategory, strNewCategory);
    initCategories();
    m_jComboBoxCategories.setSelectedItem( strNewCategory);
  }

  protected void onDelete()
  {
    String strCategory = (String)m_jComboBoxCategories.getSelectedItem();

    if( strCategory == null || strCategory.equals( ""))
      return;
    int iRet = JOptionPane.showConfirmDialog( GlobalAppProperties.instance().getRootComponent( CdArchiveDesktop.CDARCHDT), "Delete all entries within this category?"
                                              , "Alert", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if( iRet == JOptionPane.NO_OPTION)
    {
      GlobalAppProperties.instance().getPrintStreamOutput( CdArchiveDesktop.CDARCHDT).println( "...cancelled");
      //System.out.println( "...cancelled");
      return;
    }
    CdArchiveDesktop.removeCategory( strCategory);
    initCategories();
  }
}

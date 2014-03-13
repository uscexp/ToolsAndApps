package haui.app.dbtreetableview;

import haui.components.VerticalFlowLayout;
import haui.dbtool.TablePanel;
import haui.resource.ResouceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Module:      RecordDetailPanel.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\RecordDetailPanel.java,v $
 *<p>
 * Description: Shows dynamically record details.<br>
 *</p><p>
 * Created:     29.07.2004 by AE
 *</p><p>
 * @history     29.07.2004 AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: RecordDetailPanel.java,v $
 * Revision 1.0  2004-08-31 15:57:17+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\RecordDetailPanel.java,v 1.0 2004-08-31 15:57:17+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class RecordDetailPanel
  extends JPanel
{
  // member variables
  TablePanel m_dbTable;
  Vector m_vecColumNames = new Vector();
  Vector m_vecLabels = new Vector();
  Vector m_vecTextFields = new Vector();
  int m_iIdx = 0;

  // GUI member variables
  private BorderLayout m_borderLayoutMain = new BorderLayout();
  private JPanel m_jPanelLabels = new JPanel();
  private VerticalFlowLayout m_verticalFlowLayoutLabels = new VerticalFlowLayout();
  private JPanel m_jPanelTextFields = new JPanel();
  private VerticalFlowLayout m_verticalFlowLayoutTextFields = new VerticalFlowLayout();
  JPanel m_jPanelNav = new JPanel();
  FlowLayout m_flowLayoutNav = new FlowLayout();
  JButton m_jButtonPrev = new JButton();
  JButton m_jButtonNext = new JButton();

  public RecordDetailPanel( TablePanel dbTable)
  {
    m_dbTable = dbTable;
    try
    {
      jbInit();
      if( m_dbTable.getSelectedRowCount() != 0)
      {
        int idx[] = m_dbTable.getSelectedRows();
        m_iIdx = idx[0];
        for( int i = 0; i < m_dbTable.getColumnModel().getColumnCount(); ++i)
        {
          m_vecColumNames.add( m_dbTable.getColumnModel().getColumn( i).getHeaderValue());
        }
        LisAction actionlistener = new LisAction();
        m_jButtonPrev.addActionListener( actionlistener);
        m_jButtonNext.addActionListener( actionlistener);
        createPanel( m_vecColumNames, m_dbTable.getTableAdapter().getRow( m_iIdx));
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
    this.setLayout(m_borderLayoutMain);
    m_jPanelLabels.setLayout(m_verticalFlowLayoutLabels);
    m_jPanelTextFields.setLayout(m_verticalFlowLayoutTextFields);
    m_jPanelNav.setLayout(m_flowLayoutNav);
    m_flowLayoutNav.setAlignment(FlowLayout.LEFT);
    m_jButtonPrev.setActionCommand("prev");
    m_jButtonPrev.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "lbutakt.gif"));
    m_jButtonNext.setActionCommand("next");
    m_jButtonNext.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "rbutakt.gif"));
    this.add(m_jPanelLabels,  BorderLayout.WEST);
    this.add(m_jPanelTextFields,  BorderLayout.CENTER);
    this.add(m_jPanelNav, BorderLayout.NORTH);
    m_jPanelNav.add(m_jButtonPrev, null);
    m_jPanelNav.add(m_jButtonNext, null);
  }

  public void addPair( Object objHeader, Object objValue)
  {
    JLabel label = new JLabel( String.valueOf( objHeader));
    JTextField tf = new JTextField( String.valueOf( objValue));
    tf.setEditable( false);
    m_vecLabels.add( label);
    m_vecTextFields.add( tf);
    m_jPanelLabels.add( label);
    m_jPanelTextFields.add( tf);
    label.setPreferredSize( new Dimension( 200, 20));
    tf.setPreferredSize( new Dimension( 300, 20));
  }

  public void createPanel( Vector vecHeader, Vector vecData)
  {
    if( vecHeader.size() != vecData.size())
      return;

    for( int i = 0; i < vecHeader.size(); ++i)
    {
      addPair( vecHeader.elementAt( i), vecData.elementAt( i));
    }
  }

  public void changePanel( Vector vecHeader, Vector vecData)
  {
    if( vecHeader.size() != vecData.size())
      return;

    for( int i = 0; i < vecHeader.size(); ++i)
    {
      ((JLabel)m_vecLabels.elementAt( i)).setText( String.valueOf( vecHeader.elementAt( i)));
      ((JTextField)m_vecTextFields.elementAt( i)).setText( String.valueOf( vecData.elementAt( i)));
    }
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd.equals( "prev"))
      {
        if( m_iIdx > 0)
        {
          changePanel( m_vecColumNames, m_dbTable.getTableAdapter().getRow( --m_iIdx ) );
        }
      }
      else if( cmd.equals( "next"))
      {
        if( m_iIdx < m_dbTable.getRowCount()-1)
        {
          changePanel( m_vecColumNames, m_dbTable.getTableAdapter().getRow( ++m_iIdx ) );
        }
      }
    }
  }
}
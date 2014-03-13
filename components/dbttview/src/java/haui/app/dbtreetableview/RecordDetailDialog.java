package haui.app.dbtreetableview;

import haui.dbtool.TablePanel;
import haui.components.*;
import haui.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;

/**
 *
 * Module:      RecordDetailDialog.java<br>
 *              $Source: $
 *<p>
 * Description: Shows dynamically record details.<br>
 *</p><p>
 * Created:     29.07.2004 by AE
 *</p><p>
 * @history     29.07.2004 AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class RecordDetailDialog
  extends JExDialog
{
  AppProperties m_appProps;
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  RecordDetailPanel m_recDetailPanel = null;
  JScrollPane m_jScrollPaneDetail = new JScrollPane();

  public RecordDetailDialog(Component frame, String title, boolean modal, AppProperties appProps, TablePanel dbTable)
  {
    super( (Frame)null, title, modal, DbTtvDesktop.DBTTVDT);
    setFrame( frame);
    m_appProps = appProps;
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE);

    try
    {
      m_recDetailPanel = new RecordDetailPanel( dbTable);
      jbInit();
      m_jScrollPaneDetail.setPreferredSize( new Dimension( 450, 600));
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jScrollPaneDetail.setViewportView( m_recDetailPanel);
    m_jPanelBase.add(m_jScrollPaneDetail,  BorderLayout.CENTER);
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        m_jButtonOk_actionPerformed( event);
    }
  }

  public void setVisible( boolean b)
  {
    m_jButtonOk.requestFocus();
    super.setVisible( b);
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    setVisible( false);
  }
}


package haui.app.cdarchive;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.*;
import java.util.*;
import haui.components.*;

/**
 * Module:      CdArchFramePropertiesDialog.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchFramePropertiesDialog.java,v $
 *<p>
 * Description: CdArchFramePropertiesDialog.<br>
 *</p><p>
 * Created:     08.05.2003 by AE
 *</p><p>
 * @history	    08.05.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: CdArchFramePropertiesDialog.java,v $
 * Revision 1.1  2004-08-31 16:03:06+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2003-05-21 16:24:38+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\cdarchive\\CdArchFramePropertiesDialog.java,v 1.1 2004-08-31 16:03:06+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class CdArchFramePropertiesDialog
  extends JExDialog
{
  // constants

  // member variables
  Vector m_vecFilters;
  boolean m_blCancelled = true;
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  ButtonGroup m_buttonGroupOpen = new ButtonGroup();
  VerticalFlowLayout m_verticalFlowLayoutCdArchive = new VerticalFlowLayout();
  JPanel m_jPanelCdArchiveProps = new JPanel();
  JPanel m_jPanelCdArchive = new JPanel();
  VerticalFlowLayout m_verticalFlowLayoutCdArchiveProps = new VerticalFlowLayout();
  JTextField m_jTextFieldFilter = new JTextField();
  JLabel m_jLabelFilter = new JLabel();
  JLabel m_jLabelCdArchiveProps = new JLabel();

  public CdArchFramePropertiesDialog(Frame frame, String title, boolean modal, Vector vecFilters)
  {
    super(frame, title, modal, CdArchiveDesktop.CDARCHDT);
    m_vecFilters = vecFilters;
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
    _init();
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  public CdArchFramePropertiesDialog()
  {
    this(null, "", false, null);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jPanelCdArchiveProps.setLayout(m_verticalFlowLayoutCdArchiveProps);
    m_jPanelCdArchiveProps.setBorder(BorderFactory.createEtchedBorder());
    m_jPanelCdArchive.setLayout(m_verticalFlowLayoutCdArchive);
    m_jLabelFilter.setText("Filter extensions:");
    m_jLabelCdArchiveProps.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelCdArchiveProps.setText("Properties");
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelBase.add(m_jPanelCdArchive,  BorderLayout.CENTER);
    m_jPanelCdArchive.add(m_jPanelCdArchiveProps, null);
    m_jPanelCdArchiveProps.add(m_jLabelCdArchiveProps, null);
    m_jPanelCdArchiveProps.add(m_jLabelFilter, null);
    m_jPanelCdArchiveProps.add(m_jTextFieldFilter, null);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
  }

  public void _init()
  {
    if( m_vecFilters != null)
    {
      String strText = "";
      for( int i = 0; i < m_vecFilters.size(); ++i)
      {
        String str = (String)m_vecFilters.elementAt( i);
        if( i == 0)
          strText = str;
        else
          strText += ", " + str;
      }
      m_jTextFieldFilter.setText( strText);
    }
  }

  public void _save()
  {
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
      m_vecFilters = vecFilters;
    }
  }

  public Vector getFilters()
  {
    return m_vecFilters;
  }

  public boolean isCancelled()
  {
    return m_blCancelled;
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
      {
        m_blCancelled = false;
        m_jButtonOk_actionPerformed( event );
      }
      else if (cmd == "Cancel")
      {
        m_blCancelled = true;
        setVisible( false );
      }
    }
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
    }
    super.setVisible( b);
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    _save();
    setVisible( false);
  }
}

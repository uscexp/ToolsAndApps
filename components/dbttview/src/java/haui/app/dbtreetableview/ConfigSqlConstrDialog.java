package haui.app.dbtreetableview;

import haui.util.GlobalAppProperties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *		Module:					ConfigSqlConstrDialog.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\ConfigSqlConstrDialog.java,v $
 *<p>
 *		Description:    Dialog to config popup Sql parts.<br>
 *</p><p>
 *		Created:				22.08.2001	by	AE
 *</p><p>
 *		@history				22.08.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: ConfigSqlConstrDialog.java,v $
 *		Revision 1.2  2004-08-31 16:03:24+02  t026843
 *		Large redesign for application dependent outputstreams, mainframes, AppProperties!
 *		Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 *		Revision 1.1  2003-05-28 14:19:58+02  t026843
 *		reorganisations
 *
 *		Revision 1.0  2003-05-21 16:25:21+02  t026843
 *		Initial revision
 *
 *		Revision 1.0  2002-09-27 15:47:29+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2001; $Revision: 1.2 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\ConfigSqlConstrDialog.java,v 1.2 2004-08-31 16:03:24+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ConfigSqlConstrDialog extends JDialog
{
  Component m_frame = null;
  DefaultListModel m_dlm = new DefaultListModel();
  JPanel m_panelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  ConfigSqlConstrPanel m_configSqlConstrPanel = new ConfigSqlConstrPanel();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  JLabel m_jLabelDummyLeft = new JLabel();
  JLabel m_jLabelDummyRight = new JLabel();

  public ConfigSqlConstrDialog(Component frame, String title, boolean modal, Vector sql)
  {
    super((Frame)null, title, modal);
    m_frame = frame;
    m_configSqlConstrPanel.setFrame( m_frame);
    m_configSqlConstrPanel.setSqlVector( sql);
    try
    {
      jbInit();
      pack();
      this.getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  public ConfigSqlConstrDialog( Component frame, Vector sql)
  {
    this( frame, "Sql parts configuration", true, sql);
  }

  public ConfigSqlConstrDialog()
  {
    this( null, null);
  }

  void jbInit() throws Exception
  {
    m_panelBase.setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jLabelDummyLeft.setText(" ");
    m_jLabelDummyRight.setText(" ");
    m_configSqlConstrPanel.setMinimumSize(new Dimension(350, 350));
    m_configSqlConstrPanel.setPreferredSize(new Dimension(350, 350));
    getContentPane().add(m_panelBase);
    m_panelBase.add(m_configSqlConstrPanel, BorderLayout.CENTER);
    m_panelBase.add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonOk, null);
    m_jPanelButton.add(m_jButtonCancel, null);
    m_panelBase.add(m_jLabelDummyLeft, BorderLayout.WEST);
    m_panelBase.add(m_jLabelDummyRight, BorderLayout.EAST);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        m_jButtonOk_actionPerformed( event);
      else if (cmd == "Cancel")
        setVisible( false);
    }
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    m_configSqlConstrPanel.m_jButtonSet_actionPerformed(e);
    setVisible( false);
  }

  public Vector getSql()
  {
    return m_configSqlConstrPanel.getSql();
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      if( m_frame != null)
      {
        Component root = GlobalAppProperties.instance().getRootComponent( DbTtvDesktop.DBTTVDT);
        Point rootloc;
        if( root != null)
          rootloc = root.getLocation();
        else
          rootloc = getRootPane().getLocation();
        Point parloc = m_frame.getLocation();
        int x = rootloc.x + parloc.x + m_frame.getWidth()/2 - getWidth()/2;
        int y = rootloc.y + m_frame.getHeight()/2 - getHeight()/2;
        if( x < 0)
          x = 0;
        if( y < 0)
          y = 0;
        setLocation( x, y);
      }
      else
      {
        Component root = GlobalAppProperties.instance().getRootComponent( DbTtvDesktop.DBTTVDT);
        Point rootloc;
        int x;
        int y;
        if( root != null)
        {
          rootloc = root.getLocation();
          x = rootloc.x + root.getWidth()/2 - getWidth()/2;
          y = rootloc.y + root.getHeight()/2 - getHeight()/2;
          if( x < 0)
            x = 0;
          if( y < 0)
            y = 0;
        }
        else
        {
          rootloc = getRootPane().getLocation();
          x = rootloc.x + 50 + getRootPane().getWidth()/2 - getWidth()/2;
          y = rootloc.y + getRootPane().getHeight()/2 - getHeight()/2;
          if( x < 0)
            x = 0;
          if( y < 0)
            y = 0;
        }
        setLocation( x, y);
      }
    }
    m_configSqlConstrPanel.requestFocus();
    super.setVisible( b);
  }
}

package haui.app.dbtreetableview;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;

/**
 *		Module:					ConfigSqlConstrPanel.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\ConfigSqlConstrPanel.java,v $
 *<p>
 *		Description:    Panel to config popup Sql parts.<br>
 *</p><p>
 *		Created:				22.08.2001	by	AE
 *</p><p>
 *		@history				22.08.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: ConfigSqlConstrPanel.java,v $
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
 *		@version				v1.0, 2001; $Revision: 1.0 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\ConfigSqlConstrPanel.java,v 1.0 2003-05-21 16:25:21+02 t026843 Exp $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ConfigSqlConstrPanel extends JPanel
{
  Component m_frame = null;
  Vector m_sql;
  DefaultListModel m_dlm = new DefaultListModel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  ConfigSqlPanel m_configSqlPanel = new ConfigSqlPanel();
  BorderLayout m_borderLayoutList = new BorderLayout();
  FlowLayout m_flowLayoutListButton = new FlowLayout();
  JButton m_jButtonDelete = new JButton();
  JPanel m_jPanelList = new JPanel();
  JScrollPane m_jScrollPaneList = new JScrollPane();
  JButton m_jButtonSet = new JButton();
  JPanel m_jPanelListButton = new JPanel();
  JList m_jListFileExtCommands = new JList();
  JButton m_jButtonUp = new JButton();
  JButton m_jButtonDown = new JButton();

  public ConfigSqlConstrPanel( Component frame, Vector sql)
  {
    m_frame = frame;
    try
    {
      jbInit();
      m_jListFileExtCommands.setModel( m_dlm);
      setSqlVector( sql);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonSet.addActionListener( actionlis);
    m_jButtonDelete.addActionListener( actionlis);
    m_jButtonUp.addActionListener( actionlis);
    m_jButtonDown.addActionListener( actionlis);

    LisListSelection listenerListSel = new LisListSelection();
    m_jListFileExtCommands.addListSelectionListener( listenerListSel);
  }

  public ConfigSqlConstrPanel()
  {
    this( null, null);
  }

  public void setFrame( Component frame)
  {
    m_frame = frame;
  }

  public void setSqlVector( Vector sql)
  {
    m_sql = sql;

    if( m_sql != null && m_sql.size() > 0)
    {
      m_configSqlPanel.setSql( (SqlClass)m_sql.elementAt(0));
      m_configSqlPanel._init();
      for( int i = 0; i < m_sql.size(); i++)
      {
        m_dlm.addElement( (SqlClass)m_sql.elementAt(i));
        m_jListFileExtCommands.setSelectedIndex( 0);
      }
    }
  }

  void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jButtonDelete.setText("Delete");
    m_jPanelList.setLayout(m_borderLayoutList);
    m_jButtonSet.setText("Set");
    m_jPanelListButton.setLayout(m_flowLayoutListButton);
    this.setBorder(BorderFactory.createEtchedBorder());
    m_jButtonUp.setActionCommand("up");
    m_jButtonUp.setText("Up");
    m_jButtonDown.setActionCommand("down");
    m_jButtonDown.setText("Down");
    this.add(m_configSqlPanel, BorderLayout.SOUTH);
    this.add(m_jPanelList, BorderLayout.CENTER);
    m_jPanelList.add(m_jScrollPaneList, BorderLayout.CENTER);
    m_jPanelList.add(m_jPanelListButton, BorderLayout.SOUTH);
    m_jPanelListButton.add(m_jButtonSet, null);
    m_jPanelListButton.add(m_jButtonDelete, null);
    m_jPanelListButton.add(m_jButtonUp, null);
    m_jPanelListButton.add(m_jButtonDown, null);
    m_jScrollPaneList.getViewport().add(m_jListFileExtCommands, null);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Set")
        m_jButtonSet_actionPerformed( event);
      else if (cmd == "Delete")
        m_jButtonDelete_actionPerformed( event);
      else if (cmd == "up")
        up_actionPerformed( event);
      else if (cmd == "down")
        down_actionPerformed( event);
    }
  }

  void m_jButtonSet_actionPerformed(ActionEvent e)
  {
    m_configSqlPanel._save();
    SqlClass sql = m_configSqlPanel.getSql();
    if( sql == null)
      return;
    if( sql.getComponent() == null && m_frame != null)
      sql.setComponent( m_frame);
    int iIdx = -1;
    if( m_sql.size() > 0)
      iIdx = m_sql.indexOf( sql);
    if( iIdx != -1)
    {
      int i = m_dlm.indexOf( sql);
      m_sql.set( iIdx, sql.clone());
      m_dlm.set( i, sql.clone());
    }
    else
    {
      m_sql.add( sql.clone());
      m_dlm.addElement( sql.clone());
      if( m_dlm.size() > 1)
        m_jListFileExtCommands.setSelectedValue( sql, true);
      else
        m_jListFileExtCommands.setSelectedIndex( 0);
    }
  }

  void m_jButtonDelete_actionPerformed(ActionEvent e)
  {
    SqlClass sql = (SqlClass)m_jListFileExtCommands.getSelectedValue();

    m_jListFileExtCommands.clearSelection();
    m_dlm.removeElement( sql);
    m_sql.remove( sql);
    if( m_dlm.size() > 0)
      m_jListFileExtCommands.setSelectedIndex( 0);
  }

  void up_actionPerformed(ActionEvent e)
  {
    int idx = m_jListFileExtCommands.getSelectedIndex();
    if( idx == -1)
      return;
    SqlClass sql = (SqlClass)m_jListFileExtCommands.getSelectedValue();

    if( idx > 0)
    {
      SqlClass sqlprev = (SqlClass)m_sql.elementAt( idx-1);
      m_sql.setElementAt( sqlprev, idx);
      m_sql.setElementAt( sql, idx-1);
      m_dlm.setElementAt( sqlprev, idx);
      m_dlm.setElementAt( sql, idx-1);
      m_jListFileExtCommands.setSelectedIndex( idx-1);
    }
  }

  void down_actionPerformed(ActionEvent e)
  {
    int idx = m_jListFileExtCommands.getSelectedIndex();
    if( idx == -1)
      return;
    SqlClass sql = (SqlClass)m_jListFileExtCommands.getSelectedValue();

    if( idx+1 < m_sql.size())
    {
      SqlClass sqlnext = (SqlClass)m_sql.elementAt( idx+1);
      m_sql.setElementAt( sqlnext, idx);
      m_sql.setElementAt( sql, idx+1);
      m_dlm.setElementAt( sqlnext, idx);
      m_dlm.setElementAt( sql, idx+1);
      m_jListFileExtCommands.setSelectedIndex( idx+1);
    }
  }

  public Vector getSql()
  {
    return m_sql;
  }

  class LisListSelection implements ListSelectionListener
  {
    public void valueChanged(ListSelectionEvent event)
    {
      JList object = (JList)event.getSource();

      int iIdx = object.getSelectedIndex();
      m_configSqlPanel.setSql( (SqlClass)object.getSelectedValue());
      m_configSqlPanel._init();
    }
  }

  public void requestFocus()
  {
    m_configSqlPanel.requestFocus();
  }
}


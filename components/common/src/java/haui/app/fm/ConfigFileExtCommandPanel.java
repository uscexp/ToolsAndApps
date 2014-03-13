package haui.app.fm;

import haui.util.CommandClass;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Module:					ConfigFileExtCommandPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConfigFileExtCommandPanel.java,v $ <p> Description:    Dialog to config CommandClass file extensions.<br> </p><p> Created:				26.06.2001	by	AE </p><p>
 * @history  				26.06.2001	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ConfigFileExtCommandPanel.java,v $  Revision 1.3  2004-08-31 16:03:20+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2004-06-22 14:08:56+02  t026843  bigger changes  Revision 1.1  2003-06-06 10:04:00+02  t026843  modifications because of the moving the 'TypeFile's to haui.io package  Revision 1.0  2003-05-21 16:25:40+02  t026843  Initial revision  Revision 1.3  2002-06-26 12:06:45+02  t026843  History extended, simple bookmark system added.  Revision 1.2  2002-05-29 11:18:16+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.1  2001-08-14 16:48:40+02  t026843  default button added  Revision 1.0  2001-07-20 16:32:51+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.3 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConfigFileExtCommandPanel.java,v 1.3 2004-08-31 16:03:20+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ConfigFileExtCommandPanel extends JPanel
{
  private static final long serialVersionUID = -8856871448170972650L;
  
  Component m_frame = null;
  Vector m_cmd;
  DefaultListModel m_dlm = new DefaultListModel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  ConfigCommandPanel m_configCommandPanel = new ConfigCommandPanel( FileManager.APPNAME);
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

  public ConfigFileExtCommandPanel( Component frame, Vector cmd)
  {
    m_frame = frame;
    try
    {
      jbInit();
      m_jListFileExtCommands.setModel( m_dlm);
      setCommandVector( cmd);
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

  public ConfigFileExtCommandPanel()
  {
    this( null, null);
  }

  public void setFrame( Component frame)
  {
    m_frame = frame;
  }

  public void setCommandVector( Vector cmd)
  {
    m_cmd = cmd;

    m_dlm.removeAllElements();
    m_configCommandPanel._clear();
    if( m_cmd != null && m_cmd.size() > 0)
    {
      m_configCommandPanel.setCommand( (CommandClass)m_cmd.elementAt(0));
      m_configCommandPanel._init();
      for( int i = 0; i < m_cmd.size(); i++)
      {
        m_dlm.addElement( (CommandClass)m_cmd.elementAt(i));
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
    this.add(m_configCommandPanel, BorderLayout.SOUTH);
    this.add(m_jPanelList, BorderLayout.CENTER);
    m_jPanelList.add(m_jScrollPaneList, BorderLayout.CENTER);
    m_jPanelList.add(m_jPanelListButton, BorderLayout.SOUTH);
    m_jPanelListButton.add(m_jButtonSet, null);
    m_jPanelListButton.add(m_jButtonDelete, null);
    m_jPanelListButton.add(m_jButtonUp, null);
    m_jPanelListButton.add(m_jButtonDown, null);
    m_jScrollPaneList.getViewport().add(m_jListFileExtCommands, null);
  }

  public void hideForStarter()
  {
    m_configCommandPanel.hideForStarter();
  }

  public void showAfterStarter()
  {
    m_configCommandPanel.showAfterStarter();
  }

  public void hideForFileEx()
  {
    m_configCommandPanel.hideForFileEx();
  }

  public void showAfterFileEx()
  {
    m_configCommandPanel.showAfterFileEx();
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

  void up_actionPerformed(ActionEvent e)
  {
    int idx = m_jListFileExtCommands.getSelectedIndex();
    if( idx == -1)
      return;
    CommandClass cmd = (CommandClass)m_jListFileExtCommands.getSelectedValue();

    if( idx > 0)
    {
      CommandClass cmdprev = (CommandClass)m_cmd.elementAt( idx-1);
      m_cmd.setElementAt( cmdprev, idx);
      m_cmd.setElementAt( cmd, idx-1);
      m_dlm.setElementAt( cmdprev, idx);
      m_dlm.setElementAt( cmd, idx-1);
      m_jListFileExtCommands.setSelectedIndex( idx-1);
    }
  }

  void down_actionPerformed(ActionEvent e)
  {
    int idx = m_jListFileExtCommands.getSelectedIndex();
    if( idx == -1)
      return;
    CommandClass cmd = (CommandClass)m_jListFileExtCommands.getSelectedValue();

    if( idx+1 < m_cmd.size())
    {
      CommandClass cmdnext = (CommandClass)m_cmd.elementAt( idx+1);
      m_cmd.setElementAt( cmdnext, idx);
      m_cmd.setElementAt( cmd, idx+1);
      m_dlm.setElementAt( cmdnext, idx);
      m_dlm.setElementAt( cmd, idx+1);
      m_jListFileExtCommands.setSelectedIndex( idx+1);
    }
  }

  void m_jButtonSet_actionPerformed(ActionEvent e)
  {
    m_configCommandPanel._save();
    CommandClass cmd = m_configCommandPanel.getCommand();
    if( cmd == null)
      return;
    if( cmd.getComponent() == null && m_frame != null)
      cmd.setComponent( m_frame);
    int iIdx = -1;
    if( m_cmd.size() > 0)
      iIdx = m_cmd.indexOf( cmd);
    if( iIdx != -1)
    {
      int i = m_dlm.indexOf( cmd);
      m_cmd.set( iIdx, cmd.clone());
      m_dlm.set( i, cmd.clone());
    }
    else
    {
      m_cmd.add( cmd.clone());
      m_dlm.addElement( cmd.clone());
      if( m_dlm.size() > 1)
        m_jListFileExtCommands.setSelectedValue( cmd, true);
      else
        m_jListFileExtCommands.setSelectedIndex( 0);
    }
  }

  void m_jButtonDelete_actionPerformed(ActionEvent e)
  {
    CommandClass cmd = (CommandClass)m_jListFileExtCommands.getSelectedValue();

    m_jListFileExtCommands.clearSelection();
    m_dlm.removeElement( cmd);
    m_cmd.remove( cmd);
    if( m_dlm.size() > 0)
      m_jListFileExtCommands.setSelectedIndex( 0);
  }

  public Vector getCommand()
  {
    return m_cmd;
  }

  class LisListSelection implements ListSelectionListener
  {
    public void valueChanged(ListSelectionEvent event)
    {
      JList object = (JList)event.getSource();

      //int iIdx = object.getSelectedIndex();
      m_configCommandPanel.setCommand( (CommandClass)object.getSelectedValue());
      m_configCommandPanel._init();
    }
  }

  public void requestFocus()
  {
    m_configCommandPanel.requestFocus();
  }
}


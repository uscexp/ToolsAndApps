
package haui.app.dbtreetableview;

import haui.resource.ResouceManager;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.Array;

/**
 *		Module:					BaseToolFrame.java<br>
 *										$Source: $
 *<p>
 *		Description:     Base for tool frames.<br>
 *</p><p>
 *		Created:				20.09.2002	by	AE
 *</p><p>
 *		@history				20.09.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class BaseToolFrame
  extends JInternalFrame
{
  // member variables
  AppAction m_appAction;

  // GUI member variables
  BorderLayout m_borderLayoutBase = new BorderLayout();
  DefaultListModel m_defLM = new DefaultListModel();
  JList m_jList = new JList(m_defLM);
  JToolBar m_jToolBarMain = new JToolBar();
  JButton m_jButtonConnect = new JButton();
  JButton m_jButtonAdd = new JButton();
  JButton m_jButtonEdit = new JButton();
  JButton m_jButtonCopy = new JButton();
  JButton m_jButtonDelete = new JButton();

  public BaseToolFrame( String strTitle)
  {
    super( strTitle, false, false, false, false);
    setFrameIcon( (new ImageIcon( getClass().getResource(DbTtvDesktop.APPICON))));
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    // add mouse handler to list
    MouseHandler mh = new MouseHandler();
    m_jList.addMouseListener( mh);

    // add action handler
    m_appAction = new AppAction();
    m_jButtonConnect.addActionListener( m_appAction);
    m_jButtonAdd.addActionListener( m_appAction);
    m_jButtonEdit.addActionListener( m_appAction);
    m_jButtonDelete.addActionListener( m_appAction);
    m_jButtonCopy.addActionListener( m_appAction);

    setSize( 200, 200);
    setLocation( 0, 0);
  }

  private void jbInit()
    throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutBase);
    m_jButtonConnect.setMaximumSize(new Dimension(18, 18));
    m_jButtonConnect.setMinimumSize(new Dimension(18, 18));
    m_jButtonConnect.setPreferredSize(new Dimension(18, 18));
    m_jButtonConnect.setToolTipText("Connect");
    m_jButtonConnect.setActionCommand("connect");
    m_jButtonConnect.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "connect16.gif"));
    m_jButtonAdd.setMaximumSize(new Dimension(18, 18));
    m_jButtonAdd.setMinimumSize(new Dimension(18, 18));
    m_jButtonAdd.setPreferredSize(new Dimension(18, 18));
    m_jButtonAdd.setToolTipText("Add");
    m_jButtonAdd.setActionCommand("add");
    m_jButtonAdd.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Add16.gif"));
    m_jButtonEdit.setMaximumSize(new Dimension(18, 18));
    m_jButtonEdit.setMinimumSize(new Dimension(18, 18));
    m_jButtonEdit.setPreferredSize(new Dimension(18, 18));
    m_jButtonEdit.setToolTipText("Edit");
    m_jButtonEdit.setActionCommand("edit");
    m_jButtonEdit.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Edit16.gif"));
    m_jButtonDelete.setMaximumSize(new Dimension(18, 18));
    m_jButtonDelete.setMinimumSize(new Dimension(18, 18));
    m_jButtonDelete.setPreferredSize(new Dimension(18, 18));
    m_jButtonDelete.setToolTipText("Delete");
    m_jButtonDelete.setActionCommand("delete");
    m_jButtonDelete.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Delete16.gif"));
    m_jButtonCopy.setMaximumSize(new Dimension(18, 18));
    m_jButtonCopy.setMinimumSize(new Dimension(18, 18));
    m_jButtonCopy.setPreferredSize(new Dimension(18, 18));
    m_jButtonCopy.setToolTipText("Copy");
    m_jButtonCopy.setActionCommand("copy");
    m_jButtonCopy.setIcon( ResouceManager.getCommonImageIcon( DbTtvDesktop.DBTTVDT, "Copy16.gif"));
    m_jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.getContentPane().add(m_jList, BorderLayout.CENTER);
    this.getContentPane().add(m_jToolBarMain, BorderLayout.NORTH);
    m_jToolBarMain.add(m_jButtonConnect, null);
    m_jToolBarMain.addSeparator();
    m_jToolBarMain.add(m_jButtonAdd, null);
    m_jToolBarMain.add(m_jButtonEdit, null);
    m_jToolBarMain.add(m_jButtonCopy, null);
    m_jToolBarMain.add(m_jButtonDelete, null);
  }

  class AppAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if( cmd == "add")
        onAdd();
      else if( cmd == "edit")
        onEdit();
      else if( cmd == "delete")
        onDelete();
      else if( cmd == "copy")
        onCopy();
      else if( cmd == "connect")
        onConnect();
    }
  }

  public void setArray( Object[] objs)
  {
    int iLen = Array.getLength( objs);
    for( int i = 0; i < iLen; ++i)
    {
      addElement( objs[i]);
    }
  }

  public int listSize()
  {
    return m_defLM.size();
  }

  public int getSelectedIndex()
  {
    return m_jList.getSelectedIndex();
  }

  public void setSelectedIndex( int idx)
  {
    m_jList.setSelectedIndex( idx);
  }

  public void addElement( Object obj)
  {
    m_defLM.addElement( obj);
  }

  public Object elementAt( int iIdx)
  {
    return m_defLM.elementAt( iIdx);
  }

  public void setElementAt( Object obj, int iIdx)
  {
    m_defLM.setElementAt( obj, iIdx);
  }

  public void removeElementAt( int iIdx)
  {
    m_defLM.removeElementAt( iIdx);
  }

   public void removeAllElements()
  {
    m_defLM.removeAllElements();
  }

  public void onConnect()
  {
  }

  public Object onAdd()
  {
    return null;
  }

  public void onEdit()
  {
  }

  public void onDelete()
  {
  }

  public void onCopy()
  {
  }

  public void onExit()
  {
    setVisible(false); // hide the Frame
    dispose();	     // free the system resources
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2)
        onLeftMouseDoublePressed( event);
    }
  }

  void onLeftMouseDoublePressed( MouseEvent event)
  {
  }
}

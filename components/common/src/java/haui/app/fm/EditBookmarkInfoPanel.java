package haui.app.fm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *		Module:					EditBookmarkInfoPanel.java<br>
 *										$Source: $
 *<p>
 *		Description:    Dialog to edit BookmakInfos.<br>
 *</p><p>
 *		Created:				26.06.2002	by	AE
 *</p><p>
 *		@history				26.06.2002	by	AE: Created.<br>
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
public class EditBookmarkInfoPanel extends JPanel
{
  private static final long serialVersionUID = -2046551931257845877L;
  
  Component m_frame = null;
  Vector m_bookmarks;
  DefaultListModel m_dlm = new DefaultListModel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  BorderLayout m_borderLayoutList = new BorderLayout();
  FlowLayout m_flowLayoutListButton = new FlowLayout();
  JButton m_jButtonDelete = new JButton();
  JPanel m_jPanelList = new JPanel();
  JScrollPane m_jScrollPaneList = new JScrollPane();
  JPanel m_jPanelListButton = new JPanel();
  JList m_jListBookmarks = new JList();
  JButton m_jButtonUp = new JButton();
  JButton m_jButtonDown = new JButton();

  public EditBookmarkInfoPanel( Component frame, Vector bookmarks)
  {
    m_frame = frame;
    try
    {
      jbInit();
      m_jListBookmarks.setModel( m_dlm);
      setBookmarkVector( bookmarks);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonDelete.addActionListener( actionlis);
    m_jButtonUp.addActionListener( actionlis);
    m_jButtonDown.addActionListener( actionlis);
  }

  public EditBookmarkInfoPanel()
  {
    this( null, null);
  }

  public void setFrame( Component frame)
  {
    m_frame = frame;
  }

  public void setBookmarkVector( Vector bookmarks)
  {
    m_bookmarks = bookmarks;

    if( m_bookmarks != null && m_bookmarks.size() > 0)
    {
      for( int i = 0; i < m_bookmarks.size(); i++)
      {
        m_dlm.addElement( (BookmarkInfo)m_bookmarks.elementAt(i));
        m_jListBookmarks.setSelectedIndex( 0);
      }
    }
  }

  void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jButtonDelete.setText("Delete");
    m_jPanelList.setLayout(m_borderLayoutList);
    m_jPanelListButton.setLayout(m_flowLayoutListButton);
    this.setBorder(BorderFactory.createEtchedBorder());
    m_jButtonUp.setActionCommand("up");
    m_jButtonUp.setText("Up");
    m_jButtonDown.setActionCommand("down");
    m_jButtonDown.setText("Down");
    this.add(m_jPanelList, BorderLayout.CENTER);
    m_jPanelList.add(m_jScrollPaneList, BorderLayout.CENTER);
    m_jPanelList.add(m_jPanelListButton, BorderLayout.SOUTH);
    m_jPanelListButton.add(m_jButtonDelete, null);
    m_jPanelListButton.add(m_jButtonUp, null);
    m_jPanelListButton.add(m_jButtonDown, null);
    m_jScrollPaneList.getViewport().add(m_jListBookmarks, null);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Delete")
        m_jButtonDelete_actionPerformed( event);
      else if (cmd == "up")
        up_actionPerformed( event);
      else if (cmd == "down")
        down_actionPerformed( event);
    }
  }

  void up_actionPerformed(ActionEvent e)
  {
    int idx = m_jListBookmarks.getSelectedIndex();
    if( idx == -1)
      return;
    BookmarkInfo bookmark = (BookmarkInfo)m_jListBookmarks.getSelectedValue();

    if( idx > 0)
    {
      BookmarkInfo bookmarkprev = (BookmarkInfo)m_bookmarks.elementAt( idx-1);
      m_bookmarks.setElementAt( bookmarkprev, idx);
      m_bookmarks.setElementAt( bookmark, idx-1);
      m_dlm.setElementAt( bookmarkprev, idx);
      m_dlm.setElementAt( bookmark, idx-1);
      m_jListBookmarks.setSelectedIndex( idx-1);
    }
  }

  void down_actionPerformed(ActionEvent e)
  {
    int idx = m_jListBookmarks.getSelectedIndex();
    if( idx == -1)
      return;
    BookmarkInfo bookmark = (BookmarkInfo)m_jListBookmarks.getSelectedValue();

    if( idx+1 < m_bookmarks.size())
    {
      BookmarkInfo bookmarknext = (BookmarkInfo)m_bookmarks.elementAt( idx+1);
      m_bookmarks.setElementAt( bookmarknext, idx);
      m_bookmarks.setElementAt( bookmark, idx+1);
      m_dlm.setElementAt( bookmarknext, idx);
      m_dlm.setElementAt( bookmark, idx+1);
      m_jListBookmarks.setSelectedIndex( idx+1);
    }
  }

  void m_jButtonDelete_actionPerformed(ActionEvent e)
  {
    BookmarkInfo bookmark = (BookmarkInfo)m_jListBookmarks.getSelectedValue();

    m_jListBookmarks.clearSelection();
    m_dlm.removeElement( bookmark);
    m_bookmarks.remove( bookmark);
    if( m_dlm.size() > 0)
      m_jListBookmarks.setSelectedIndex( 0);
  }

  public Vector getBookmark()
  {
    return m_bookmarks;
  }

  public void requestFocus()
  {
    m_jButtonDelete.requestFocus();
  }
}

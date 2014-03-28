package haui.app.fm;

import haui.components.JExDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Module:					EditBookmarkInfoDialog.java<br> $Source: $ <p> Description:    Dialog to edit BookmarkInfos.<br> </p><p> Created:				26.06.2002	by	AE </p><p>
 * @history  				26.06.2002	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2002; $Revision: $<br>  $Header: $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class EditBookmarkInfoDialog
  extends JExDialog
{
  private static final long serialVersionUID = -6268856486570197745L;
  
  DefaultListModel<BookmarkInfo> m_dlm = new DefaultListModel<>();
  JPanel m_panelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  EditBookmarkInfoPanel m_configBookmarkInfoPanel = new EditBookmarkInfoPanel();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  JLabel m_jLabelDummyLeft = new JLabel();
  JLabel m_jLabelDummyRight = new JLabel();
  boolean m_ok = false;

  public EditBookmarkInfoDialog(Component frame, String title, boolean modal, List<BookmarkInfo> bookmarks)
  {
    super((Frame)null, title, modal, FileManager.APPNAME);
    setFrame( frame);
    m_configBookmarkInfoPanel.setFrame( frame);
    m_configBookmarkInfoPanel.setBookmarkVector( bookmarks);
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

  public EditBookmarkInfoDialog( Component frame, List<BookmarkInfo> bookmarks)
  {
    this( frame, "Edit Bookmarks", true, bookmarks);
  }

  public EditBookmarkInfoDialog()
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
    getContentPane().add(m_panelBase);
    m_panelBase.add(m_configBookmarkInfoPanel, BorderLayout.CENTER);
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
    m_ok = true;
    setVisible( false);
  }

  public boolean isOk()
  {
    return m_ok;
  }

  public List<BookmarkInfo> getBookmark()
  {
    return m_configBookmarkInfoPanel.getBookmark();
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_ok = false;
    }
    super.setVisible( b);
    m_configBookmarkInfoPanel.requestFocus();
  }
}

package haui.app.fm;

import haui.components.JExDialog;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Module:      FileFilterDialog.java<br> $Source: $ <p> Description: FileInterfaceFilter selection dialog.<br> </p><p> Created:     22.07.2004 by AE </p><p>
 * @history      22.07.2004 AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.2  </p>
 */
public class FileFilterDialog
  extends JExDialog
{
  private static final long serialVersionUID = 8172767717279699017L;
  
  AppProperties m_appProps;
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  FileFilterPanel m_FileFilterPanel = null;
  boolean m_blCanceled = false;

  public FileFilterDialog(Component frame, String title, boolean modal, AppProperties appProps)
  {
    super( (Frame)null, title, modal, FileManager.APPNAME);
    setFrame( frame);
    m_appProps = appProps;
    m_FileFilterPanel = new FileFilterPanel( FileManager.APPNAME, appProps);
    m_blCanceled = false;

    try
    {
      jbInit();
      m_FileFilterPanel._init();
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    //setSize( 600, getHeight());
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_FileFilterPanel.setBorder(BorderFactory.createEtchedBorder());
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelBase.add(m_FileFilterPanel, BorderLayout.CENTER);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        m_jButtonOk_actionPerformed( event);
      else if (cmd == "Cancel")
      {
        m_blCanceled = true;
        setVisible( false);
      }
    }
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_blCanceled = false;
      m_FileFilterPanel._init();
    }
    super.setVisible( b);
    m_jButtonOk.requestFocus();
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    m_FileFilterPanel._save();
    setVisible( false);
  }

  public boolean isCanceled()
  {
    return m_blCanceled;
  }

  public FileInterfaceFilter getSelection()
  {
    if( !m_blCanceled)
      return m_FileFilterPanel.getSelection();
    return null;
  }
}

package haui.components;

import haui.io.FileInterface.FileTypeServer;
import haui.io.FileInterface.configuration.FileTypeServerFileInterfaceConfiguration;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Module:      FileTypeServerDialog.java<br> $Source: $ <p> Description: dialog to log FileTypeServer output.<br> </p><p> Created:	27.05.2004  by AE </p><p>
 * @history      27.05.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.3  </p>
 */
public class FileTypeServerDialog
  extends JExDialog
{
  private static final long serialVersionUID = 8581993854262144900L;

  // member variables
  FileTypeServer m_fts = null;

  // GUI member variables
  BorderLayout m_borderLayoutMain = new BorderLayout();
  JScrollPane m_jScrollPaneMain = new JScrollPane();
  JTextArea m_jTextAreaMain = new JTextArea( 25, 50);
  JPanel m_jPanelControl = new JPanel();
  JPanel m_jPanelExt = new JPanel();
  BorderLayout m_borderLayoutExt = new BorderLayout();
  GridLayout m_gridLayoutControl = new GridLayout();
  JPanel m_jPanelButtons = new JPanel();
  JButton m_jButtonClose = new JButton();
  JButton m_jButtonStart = new JButton();
  FlowLayout m_flowLayoutButtons = new FlowLayout();

  public FileTypeServerDialog( Component frame, String title, boolean modal, FileTypeServer fts, String strAppName)
  {
    super( (Frame)null, title, modal, strAppName);
    setFrame( frame);
    m_fts = fts;
    if( m_fts != null && m_fts.getFileInterfaceConfiguration() != null)
      ((FileTypeServerFileInterfaceConfiguration)m_fts.getFileInterfaceConfiguration()).setDialog( this);
    try
    {
      jbInit();
      pack();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    LisAction actionlis = new LisAction();
    m_jButtonStart.addActionListener( actionlis);
    m_jButtonClose.addActionListener( actionlis);
  }

  private void jbInit()
    throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutMain);
    m_jTextAreaMain.setEditable(false);
    m_jTextAreaMain.setText("");
    m_jPanelControl.setLayout(m_gridLayoutControl);
    m_jPanelExt.setLayout(m_borderLayoutExt);
    m_gridLayoutControl.setColumns(2);
    m_jButtonClose.setActionCommand("close");
    m_jButtonClose.setText("Close");
    m_jButtonStart.setToolTipText("start server");
    m_jButtonStart.setActionCommand("start");
    m_jButtonStart.setText("Start");
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    this.getContentPane().add(m_jScrollPaneMain,  BorderLayout.CENTER);
    m_jScrollPaneMain.getViewport().add(m_jTextAreaMain, null);
    this.getContentPane().add(m_jPanelControl,  BorderLayout.SOUTH);
    m_jPanelControl.add(m_jPanelExt, null);
    m_jPanelControl.add(m_jPanelButtons, null);
    m_jPanelButtons.add(m_jButtonStart, null);
    m_jPanelButtons.add(m_jButtonClose, null);
  }

  public void append( String strText)
  {
    if( strText != null && !strText.equals( ""))
    {
      m_jTextAreaMain.append( strText);
      m_jTextAreaMain.setCaretPosition( m_jTextAreaMain.getText().length()-1);
    }
  }

  public void addComponent( Component co)
  {
    m_jPanelExt.add( co, BorderLayout.CENTER);
    pack();
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
    }
    else
    {
      stop();
    }
    super.setVisible( b);
    m_jButtonStart.requestFocus();
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "start")
        start();
      if (cmd == "stop")
        stop();
      else if (cmd == "close")
      {
        stop();
        setVisible( false );
      }
    }
  }

  public void start()
  {
    if( m_fts != null)
    {
      Thread th = new Thread()
      {
        public void run()
        {
          m_fts.listen();
          FileTypeServerDialog.this.stop();
        }
      };
      th.start();
      m_jButtonStart.setActionCommand( "stop");
      m_jButtonStart.setText( "Stop");
      m_jButtonStart.setToolTipText( "stop server");
    }
  }

  public void stop()
  {
    if( m_fts != null)
    {
      if( m_fts.isConnected())
        m_fts.disconnect();
      m_jButtonStart.setToolTipText("start server");
      m_jButtonStart.setActionCommand("start");
      m_jButtonStart.setText("Start");
    }
  }
}
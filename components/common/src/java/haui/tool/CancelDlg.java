
package haui.tool;

import haui.components.JExDialog;
import haui.resource.ResouceManager;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Module:					CancelDlg.java <p> Description:		Dialog which controls a given Runnable. </p><p> Created:				19.09.2000	by	AE </p><p> Last Modified:	21.09.2000	by	AE </p><p> history					19.09.2000 - 21.09.2000	by	AE:	Create CancelDlg basic functionality<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000  </p><p>
 * @since  					JDK1.2  </p>
 */
public class CancelDlg
extends JExDialog
implements ActionListener
{
  private static final long serialVersionUID = -6150302517469655377L;
  
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel panel1 = new JPanel();
  AniGifPanel m_aniGifPanel = null;
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel m_jLabelMsg = new JLabel();
  JButton m_jButtonCancel = new JButton();

  Thread m_th;
  String m_msg;
  boolean m_blCancel = false;
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();

  public CancelDlg(Component frame, String msg, Runnable r, String strAppName)
  {
    super( frame, "Waiting ...", true, strAppName);
    m_aniGifPanel = new AniGifPanel( ResouceManager.getCommonImageIcon( strAppName, "a_cons23.gif"));
    try
    {
      jbInit();
      pack();
      getRootPane().setDefaultButton( m_jButtonCancel);

      m_msg = msg;
      m_th = new Thread( r)
      {
        public void run()
        {
          super.run();
          setVisible( false);
        }
      };
      m_jLabelMsg.setText( m_msg);
      m_jButtonCancel.addActionListener( this);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception
  {
    panel1.setLayout(borderLayout1);
    m_jButtonCancel.setText("Cancel");
    jPanel1.setLayout(flowLayout1);
    jPanel2.setLayout(flowLayout2);
    m_jLabelMsg.setText("Searching ...");
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(m_jButtonCancel, null);
    panel1.add(jPanel2, BorderLayout.NORTH);
    jPanel2.add(m_aniGifPanel, null);
    jPanel2.add(m_jLabelMsg, null);
  }

  public void setRunner( Runnable r)
  {
    if( m_th != null)
      //m_th.interrupt();
      m_th.stop();
    m_th = new Thread( r)
    {
      public void run()
      {
        super.run();
        setVisible( false);
      }
    };
  }

  public void start()
  {
    try
    {
      m_th.setPriority( m_th.getPriority() - 1);
      m_th.start();
      setVisible( true);
    }
    catch( Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public boolean isChildAlive()
  {
    return m_th.isAlive();
  }

  public void interrupt()
  {
    if( m_th != null)
      //m_th.interrupt();
      m_th.stop();
    setVisible( false);
  }

  public boolean isCanceled()
  {
    return m_blCancel;
  }

  public void actionPerformed(java.awt.event.ActionEvent event)
  {
    String cmd = event.getActionCommand();
    if (cmd == "Cancel")
    {
      m_blCancel = true;
    }
    this.interrupt();
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_blCancel = false;
    }
    super.setVisible( b);
  }
}


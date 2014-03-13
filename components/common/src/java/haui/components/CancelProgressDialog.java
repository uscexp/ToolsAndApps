package haui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Module:      CancelProgressDialog.java<br> $Source: $ <p> Description: CancelProgressDialog.<br> </p><p> Created:     06.09.2004  by AE </p><p>
 * @history      06.09.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.3  </p>
 */
public class CancelProgressDialog
  extends JExDialog
{
  private static final long serialVersionUID = -2091133395383106990L;
  
  Thread m_th;
  String m_msg;
  boolean m_blCancel = false;

  JPanel m_jPanelButton = new JPanel();
  JPanel m_jPanelProgress = new JPanel();
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JLabel m_jLabelMsg = new JLabel();
  JButton m_jButtonCancel = new JButton();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  VerticalFlowLayout m_verticalFlowLayoutProgress = new VerticalFlowLayout();
  JProgressBar m_jProgressBarMain = new JProgressBar();
  JProgressBar m_jProgressBarDetail = new JProgressBar();
  JLabel m_jLabelFrom = new JLabel();
  JLabel m_jLabelTo = new JLabel();

  public CancelProgressDialog(Component frame, String msg, Runnable r, boolean blModal, String strAppName)
  {
    super( frame, "Working ...", blModal, strAppName);
    try
    {
      jbInit();
      pack();
      getRootPane().setDefaultButton( m_jButtonCancel);

      m_msg = msg;
      if( r != null)
        setRunnable( r);
      m_jLabelMsg.setText( m_msg);
      LisAction listener = new LisAction();
      m_jButtonCancel.addActionListener( listener);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public void setRunnable( Runnable r)
  {
    m_th = new Thread( r)
    {
      public void run()
      {
        super.run();
        setVisible( false);
      }
    };
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jButtonCancel.setText("Cancel");
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jPanelProgress.setLayout(m_verticalFlowLayoutProgress);
    m_jLabelMsg.setText("...");
    m_jLabelFrom.setText("from: ");
    m_jPanelBase.setPreferredSize(new Dimension(300, 150));
    m_jLabelTo.setText("to: ");
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonCancel, null);
    m_jPanelBase.add(m_jPanelProgress,  BorderLayout.CENTER);
    m_jPanelProgress.add(m_jLabelMsg, null);
    m_jPanelProgress.add(m_jProgressBarMain, null);
    m_jPanelProgress.add(m_jLabelFrom, null);
    m_jPanelProgress.add(m_jLabelTo, null);
    m_jPanelProgress.add(m_jProgressBarDetail, null);
  }

  public JProgressBar getMainProgressbar()
  {
    return m_jProgressBarMain;
  }

  public JProgressBar getDetailProgressbar()
  {
    return m_jProgressBarDetail;
  }

  public void setFromLabelText( String strFrom)
  {
    m_jLabelFrom.setText( strFrom);
  }

  public void setToLabelText( String strTo)
  {
    m_jLabelTo.setText( strTo);
  }

  public void showToLabel()
  {
    m_jLabelTo.setVisible( true);
  }

  public void hideToLabel()
  {
    m_jLabelTo.setVisible( false);
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

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_blCancel = false;
    }
    super.setVisible( b);
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event )
    {
      String cmd = event.getActionCommand();
      if( cmd.equals( "Cancel" ))
      {
        m_blCancel = true;
      }
      CancelProgressDialog.this.interrupt();
    }
  }
}

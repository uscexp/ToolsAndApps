package haui.components.desktop;

import haui.components.PrintStreamOutputThread;
import haui.io.TextAreaPrintStream;
import haui.resource.ResouceManager;
import haui.util.GlobalApplicationContext;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

/**
 * Module:      OutputFrame.java<br> $Source: $ <p> Description: OutputFrame.<br> </p><p> Created:     23.04.2003 by AE </p><p>
 * @history  	    23.04.2003 by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2003; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.2  </p>
 */
public class OutputFrame
  extends JInternalFrame
{
  private static final long serialVersionUID = 741358106654825875L;
  
  // member variables
  String m_strAppName;
  AppAction m_appAction;
  PrintStreamOutputThread m_ot;
  JDesktopFrame m_df;

  // GUI member variables
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JToolBar m_jToolBarMain = new JToolBar();
  JButton m_jButtonClear = new JButton();
  JScrollPane m_jScrollPaneOutput = new JScrollPane();
  JTextArea m_jTextAreaOutput = new JTextArea();

  public OutputFrame( String strAppName, String strTitle, JDesktopFrame jDeskFrame)
  {
    super( strTitle, true, false, true, true);
    m_strAppName = strAppName;
    m_df = jDeskFrame;
    setFrameIcon( (new ImageIcon( m_df.getClass().getResource( JDesktopFrame.APPICON))) );
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    // add action handler
    m_appAction = new AppAction();
    m_jButtonClear.addActionListener( m_appAction);

    // set size and pos
    if( jDeskFrame != null)
    {
      onFixWindow( jDeskFrame.getDesktopPane());
    }
    else
    {
      setSize( 200, 200);
      setLocation( 200, 0);
    }
    GlobalApplicationContext.instance().setOutputPrintStream(new TextAreaPrintStream( m_jTextAreaOutput));
    // start output thread
    //m_ot = new PrintStreamOutputThread( m_strAppName, m_jTextAreaOutput);
    //m_ot.start();
  }

  private void jbInit()
    throws Exception
  {
    this.getContentPane().setLayout(m_borderLayoutBase);
    m_jButtonClear.setMaximumSize(new Dimension(20, 18));
    m_jButtonClear.setMinimumSize(new Dimension(20, 18));
    m_jButtonClear.setPreferredSize(new Dimension(20, 18));
    m_jButtonClear.setToolTipText("Clear output");
    m_jButtonClear.setActionCommand("clear");
    m_jButtonClear.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "clearout.gif"));
    m_jTextAreaOutput.setEditable(false);
    this.getContentPane().add(m_jToolBarMain, BorderLayout.NORTH);
    this.getContentPane().add(m_jScrollPaneOutput, BorderLayout.CENTER);
    m_jScrollPaneOutput.getViewport().add(m_jTextAreaOutput, null);
    m_jToolBarMain.add(m_jButtonClear, null);
  }

  public void onFixWindow( JDesktopPane dp)
  {
    int iDpWidth = dp.getWidth();
    int iDpHeight = dp.getHeight();
    int iOfHeight = (iDpHeight < 150) ? iDpHeight : 150;
    int iOfPosY = iDpHeight - (iOfHeight);

    setSize( iDpWidth, iOfHeight);
    setLocation( 0, iOfPosY);
  }

  class AppAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if( cmd == "clear")
        onClear();
    }
  }

  public void onClear()
  {
    m_jTextAreaOutput.setText( "");
  }

  public void onExit()
  {
    setVisible(false); // hide the Frame
    dispose();	     // free the system resources
  }
}

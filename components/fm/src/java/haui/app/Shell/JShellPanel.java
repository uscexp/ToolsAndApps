package haui.app.Shell;

import haui.components.*;
import haui.app.Shell.engine.JShellEnv;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.*;

/**
 * Module:      JShellPanel.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\JShellPanel.java,v $
 *<p>
 * Description: JPanel for JShell.<br>
 *</p><p>
 * Created:     08.04.2004  by AE
 *</p><p>
 * @history     08.04.2004  by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: JShellPanel.java,v $
 * Revision 1.1  2004-08-31 16:03:17+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.0  2004-06-22 14:06:52+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.1 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\Shell\\JShellPanel.java,v 1.1 2004-08-31 16:03:17+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class JShellPanel
  extends JPanel
{
  // member variables
  private JShell m_shell = null;

  // GUI member variables
  private BorderLayout m_borderLayoutMain = new BorderLayout();
  private JScrollPane m_jScrollPaneMain = new JScrollPane();
  private ShellTextArea m_shellTextAreaMain = null;

  public JShellPanel( String strAppName)
  {
    this( null, strAppName);
  }

  public JShellPanel( ShellTextArea sta,  String strAppName)
  {
    m_shell = new JShell( strAppName);
    m_shellTextAreaMain = new ShellTextArea( m_shell.getShellEnv().getAppName(), 25, 80);
    try
    {
      if( sta != null)
        m_shellTextAreaMain = sta;
      jbInit();
      m_shell.setShellTextArea( m_shellTextAreaMain);
      m_shell.setStreamContainer( m_shellTextAreaMain.getStreamContainer());
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutMain);
    this.add(m_jScrollPaneMain, BorderLayout.CENTER);
    m_jScrollPaneMain.setViewportView( m_shellTextAreaMain);
  }

  public void start()
  {
    m_shell.start();
  }

  public void stop()
  {
    m_shell.stop();
    m_shellTextAreaMain.getStreamContainer().close();
  }

  public JShell getShell()
  {
    return m_shell;
  }

  public ShellTextArea getShellTextArea()
  {
    return m_shellTextAreaMain;
  }

  //Main-Methode
  public static void main(String[] args)
  {
    final JShellPanel sp = new JShellPanel( JShellEnv.APPNAME);
    final JFrame frame = new JFrame( "JShell");
    frame.addWindowListener( new WindowAdapter()
    {
      public void windowClosing( WindowEvent event )
      {
        Object object = event.getSource();
        if( object == frame )
        {
          sp.stop();
          super.windowClosing( event);
          frame.dispose();
          System.exit( 0);
        }
      }
    } );
    frame.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE);
    frame.getContentPane().add("Center", sp);
    frame.pack();
    frame.setVisible( true);
    Thread th = new Thread()
    {
      public void run()
      {
        try
        {
          sp.start();
          frame.setVisible( false );
          frame.dispose();
          System.exit( 0);
        }
        catch( Exception ex)
        {
          ex.printStackTrace();
          frame.setVisible( false );
        }
      }
    };
    th.start();
  }
}
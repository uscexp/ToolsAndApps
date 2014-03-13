
package haui.components;

import haui.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Module:					AuthorizationDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\AuthorizationDialog.java,v $ <p> Description:    URL Authorization dialog.<br> </p><p> Created:				05.10.2000	by	AE </p><p>
 * @history  				05.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: AuthorizationDialog.java,v $  Revision 1.8  2004-08-31 16:03:14+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.7  2003-05-28 14:20:00+02  t026843  reorganisations  Revision 1.6  2002-09-27 15:29:42+02  t026843  Dialogs extended from JExDialog  Revision 1.5  2002-09-18 11:16:22+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.4  2002-08-28 14:23:41+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.3  2001-08-14 16:49:13+02  t026843  default button added  Revision 1.2  2001-07-20 16:29:03+02  t026843  FileManager changes  Revision 1.1  2000-10-13 09:09:44+02  t026843  bugfixes + changes  Revision 1.0  2000-10-05 14:48:34+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.8 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\AuthorizationDialog.java,v 1.8 2004-08-31 16:03:14+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class AuthorizationDialog extends JExDialog
{
  private static final long serialVersionUID = 6977898052241153690L;
  
  // constants
  public final static int URLAUTH = 1;
  public final static int PROXYAUTH = 2;
  public final static int CGIAUTH = 3;

  // member variables
  AppProperties m_appProps;
  int  m_iMode;
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  URLAuthorizationPanel m_jPanelURLAuthorization = new URLAuthorizationPanel();
  ProxyAuthorizationPanel m_jPanelProxyAuthorization = new ProxyAuthorizationPanel();
  CGIAuthorizationPanel m_jPanelCGIAuthorization = new CGIAuthorizationPanel();
  JLabel m_jLabelLeft = new JLabel();
  JLabel m_jLabelRight = new JLabel();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();

  public AuthorizationDialog(Frame frame, String title, boolean modal, AppProperties appProps, int iMode, String strAppName)
  {
    super(frame, title, modal, strAppName);
    m_appProps = appProps;
    m_iMode = iMode;
    switch( m_iMode)
    {
      case 1:
        m_jPanelURLAuthorization = new URLAuthorizationPanel( appProps);
        break;

      case 2:
        m_jPanelProxyAuthorization = new ProxyAuthorizationPanel( appProps);
        break;

      case 3:
        m_jPanelCGIAuthorization = new CGIAuthorizationPanel( appProps);
        break;

      default:
        m_jPanelURLAuthorization = new URLAuthorizationPanel( appProps);
        break;
    }
    try
    {
      jbInit();
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    setResizable( false);
    switch( m_iMode)
    {
      case 1:
        m_jPanelURLAuthorization._init();
        break;

      case 2:
        m_jPanelProxyAuthorization._init();
        break;

      case 3:
        m_jPanelCGIAuthorization._init();
        break;

      default:
        m_jPanelURLAuthorization._init();
        break;
    }
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
  }

  public AuthorizationDialog( String strAppName)
  {
    this(null, "", false, null, URLAUTH, strAppName);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jLabelLeft.setText(" ");
    m_jLabelRight.setText(" ");
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelBase.add(m_jLabelLeft, BorderLayout.WEST);
    m_jPanelBase.add(m_jLabelRight, BorderLayout.EAST);
    switch( m_iMode)
    {
      case 1:
        m_jPanelBase.add(m_jPanelURLAuthorization, BorderLayout.CENTER);
        break;

      case 2:
        m_jPanelBase.add(m_jPanelProxyAuthorization, BorderLayout.CENTER);
        break;

      case 3:
        m_jPanelBase.add(m_jPanelCGIAuthorization, BorderLayout.CENTER);
        break;

      default:
        m_jPanelBase.add(m_jPanelURLAuthorization, BorderLayout.CENTER);
        break;
    }
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
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

  public void setRequestText( String str)
  {
    switch( m_iMode)
    {
      case 1:
        m_jPanelURLAuthorization.setRequestText( str);
        break;

      case 2:
        m_jPanelProxyAuthorization.setRequestText( str);
        break;

      case 3:
        m_jPanelCGIAuthorization.setRequestText( str);
        break;

      default:
        m_jPanelURLAuthorization.setRequestText( str);
        break;
    }
    pack();
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      switch( m_iMode)
      {
        case 1:
          m_jPanelURLAuthorization._init();
          break;

        case 2:
          m_jPanelProxyAuthorization._init();
          break;

        case 3:
          m_jPanelCGIAuthorization._init();
          break;

        default:
          m_jPanelURLAuthorization._init();
          break;
      }
    }
    super.setVisible( b);
    switch( m_iMode)
    {
      case 1:
        m_jPanelURLAuthorization.requestFocus();
        break;

      case 2:
        m_jPanelProxyAuthorization.requestFocus();
        break;

      case 3:
        m_jPanelCGIAuthorization.requestFocus();
        break;

      default:
        m_jPanelURLAuthorization.requestFocus();
        break;
    }
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    switch( m_iMode)
    {
      case 1:
        m_jPanelURLAuthorization._save();
        break;

      case 2:
        m_jPanelProxyAuthorization._save();
        break;

      case 3:
        m_jPanelCGIAuthorization._save();
        break;

      default:
        m_jPanelURLAuthorization._save();
        break;
    }
    setVisible( false);
  }
}

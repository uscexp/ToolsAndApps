package haui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

/**
 *
 * Module:      SplashWindow.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\SplashWindow.java,v $
 *<p>
 * Description: SplashWindow.<br>
 *</p><p>
 * Created:     20.05.2003 by AE
 *</p><p>
 * @history	    20.05.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: SplashWindow.java,v $
 * Revision 1.0  2003-05-21 16:26:10+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\SplashWindow.java,v 1.0 2003-05-21 16:26:10+02 t026843 Exp $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class SplashWindow
  extends JWindow
{
  private static final long serialVersionUID = -8443560896219641456L;
  
  JPanel m_jPanelWin = new JPanel();
  BorderLayout m_borderLayoutWin = new BorderLayout();
  MatteBorder m_border = new MatteBorder(1,1,1,1,Color.black);
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JLabel m_jLabelIcon = new JLabel();
  JLabel m_jLabelText = new JLabel();

  public SplashWindow( String strHtml, ImageIcon image)
  {
    constructor( strHtml, image);
  }

  public SplashWindow( String strHeader, String strAppDesc, String m_strVersion, ImageIcon image)
  {
    if( strHeader == null || strHeader.equals( ""))
      strHeader = "Application";
    if( strAppDesc == null || strAppDesc.equals( ""))
      strAppDesc = "Application description";
    if( m_strVersion == null || m_strVersion.equals( ""))
      m_strVersion = "Version";
    String strText =
      "<HTML><P ALIGN=\"CENTER\">" +
      "<H2>" + strHeader + "</H2><B>" +
      strAppDesc + "</B><BR><BR>" +
      m_strVersion + "<BR>" +
      "</P></HTML>";
    constructor( strText, image);
  }

  private void constructor( String strHtml, ImageIcon image)
  {
    try
    {
      jbInit();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    String strText;
    if( strHtml != null && !strHtml.equals( ""))
      strText = strHtml;
    else
    {
      strText =
        "<HTML><P ALIGN=\"CENTER\">" +
        "<H2>Application</H2><B>" +
        "Application description</B><BR><BR>" +
        "Version<BR>" +
        "</p></HTML>";
    }
    if( image != null)
      m_jLabelIcon.setIcon( image);
    m_jLabelText.setText( strText);
    Toolkit to = getToolkit();
    Dimension dim = to.getScreenSize();
    setLocation(dim.width/3,dim.height/3);
    pack();
  }

  void jbInit()
    throws Exception
  {
    m_jPanelWin.setLayout( m_borderLayoutWin );
    m_borderLayoutWin.setHgap( 10 );
    m_borderLayoutWin.setVgap( 10 );
    m_jPanelWin.setBorder( m_border);
    setContentPane(m_jPanelWin);
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    m_jPanelBase.setLayout( m_borderLayoutBase );
    m_borderLayoutBase.setHgap( 10 );
    m_borderLayoutBase.setVgap( 10 );
    m_jLabelText.setAlignmentX( ( float )0.5 );
    m_jLabelText.setHorizontalAlignment( SwingConstants.LEADING );
    m_jLabelText.setHorizontalTextPosition( SwingConstants.TRAILING );
    getContentPane().add( m_jPanelBase, BorderLayout.CENTER);
    m_jPanelBase.add( m_jLabelText, BorderLayout.CENTER );
    m_jPanelBase.add( m_jLabelIcon, BorderLayout.WEST );
  }

  public void close()
  {
    setVisible( false);
    dispose();
  }
}
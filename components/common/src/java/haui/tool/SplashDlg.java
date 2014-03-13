
package haui.tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.MatteBorder;

/**
 *
 *		Module:					SplashDlg.java
 *<p>
 *		Description:		Splash window.
 *</p><p>
 *		Created:				16.11.1998	by	AE
 *</p><p>
 *		Last Modified:	16.10.2002	by	AE
 *</p><p>
 *		history					16.11.1998 - 20.11.1998	by	AE:	Create SplashDlg basic functionality<br>
 *										15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *										16.10.2002	by	AE:	Changed constructor to accept an ImageIcon<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999,2002
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class SplashDlg extends JWindow
{
  private static final long serialVersionUID = -3785444823493584626L;
  
  javax.swing.JLabel m_jLabelNorth;
  javax.swing.JLabel m_jLabelSouth;
  javax.swing.JLabel m_jLabelIcon;
  javax.swing.JPanel m_jPanelText;
  javax.swing.JLabel m_jLabelHead;
  javax.swing.JTextArea m_jTextArea;

  public SplashDlg()
  {
    Constructor( null, null, null);
  }

  public SplashDlg( String strHead, String strText, ImageIcon image)
  {
    Constructor( strHead, strText, image);
  }

  void Constructor( String strHead, String strText, ImageIcon image)
  {
    JPanel splash = new JPanel(new BorderLayout(10,10));
    splash.setBorder(new MatteBorder(1,1,1,1,Color.black));
    setContentPane(splash);
    getContentPane().setLayout(new BorderLayout(10,10));
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    m_jLabelNorth = new javax.swing.JLabel();
    getContentPane().add("North", m_jLabelNorth);
    m_jLabelSouth = new javax.swing.JLabel();
    getContentPane().add("South", m_jLabelSouth);
    m_jLabelIcon = new javax.swing.JLabel();
    getContentPane().add("West", m_jLabelIcon);
    m_jPanelText = new javax.swing.JPanel();
    m_jPanelText.setLayout(new BorderLayout(10,10));
    getContentPane().add("Center", m_jPanelText);
    m_jLabelHead = new javax.swing.JLabel();
    m_jPanelText.add("North", m_jLabelHead);
    m_jTextArea = new javax.swing.JTextArea();
    m_jTextArea.setEditable(false);
    m_jTextArea.setBackground( m_jLabelHead.getBackground());
    m_jTextArea.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    m_jPanelText.add("Center", m_jTextArea);

    Toolkit to = getToolkit();
    Dimension dim = to.getScreenSize();
    setLocation(dim.width/3,dim.height/3);
    if( strHead != null)
      setHead( strHead);
    if( strText != null)
      setText( strText);
    if( image != null)
      setIcon( image);
    pack();
    setVisible(false);
  }

  public void setHead( String strHead)
  {
    m_jLabelHead.setText( strHead + " ");
    //m_jLabelHead.setSize( 480,320);
    //pack();
  }

  public void setText( String strText)
  {
    m_jTextArea.setText( strText + " ");
    //m_jTextArea.setSize( 480,320);
    //pack();
  }

  public void setIcon( ImageIcon image)
  {
    m_jLabelIcon.setIcon( image);
    //m_jLabelIcon.setSize( 480,320);
    //pack();
  }

  public void setHeadFont(Font f)
  {
    m_jLabelHead.setFont(f);
    //m_jLabelHead.setSize( 480,320);
    //pack();
  }

  public void setTextFont(Font f)
  {
    m_jTextArea.setFont(f);
    //m_jTextArea.setSize( 480,320);
    //pack();
  }

  public void close()
  {
    setVisible( false);
    dispose();
  }
}


package haui.components;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.*;

/**
 *
 * Module:      AboutDialog.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\AboutDialog.java,v $
 *<p>
 * Description: About dialog.<br>
 *</p><p>
 * Created:     23.04.2003 by AE
 *</p><p>
 * @history	    23.04.2003 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: AboutDialog.java,v $
 * Revision 1.2  2004-08-31 16:03:12+02  t026843
 * Large redesign for application dependent outputstreams, mainframes, AppProperties!
 * Bugfixes to DbTreeTableView, additional features for jDirWork.
 *
 * Revision 1.1  2003-05-28 14:19:48+02  t026843
 * reorganisations
 *
 * Revision 1.0  2003-05-21 16:26:02+02  t026843
 * Initial revision
 *
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.2 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\AboutDialog.java,v 1.2 2004-08-31 16:03:12+02 t026843 Exp t026843 $
 *</p><p>
 * @since       JDK1.2
 *</p>
 */
public class AboutDialog
  extends JExDialog
  implements ActionListener
{
  private static final long serialVersionUID = -2645696040569765452L;
  
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JLabel m_jLabelIcon = new JLabel();
  JLabel m_jLabelText = new JLabel();
  JPanel m_jPanelButton = new JPanel();
  FlowLayout m_flowLayoutButton = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  Component m_frame;
  Border m_border;

  public AboutDialog( Component frame, String title, boolean modal, String strHtml, ImageIcon icon, String strAppName)
  {
    super( (Frame)null, title, modal, strAppName);
    constructor( frame, title, modal, strHtml, icon);
  }

  public AboutDialog( Component frame, String title, boolean modal, String strHeader, String strAppDesc, String m_strVersion, ImageIcon icon, String strAppName)
  {
    super( (Frame)null, title, modal, strAppName);
    if( strHeader == null || strHeader.equals( ""))
      strHeader = "Application";
    if( strAppDesc == null || strAppDesc.equals( ""))
      strAppDesc = "Application description";
    if( m_strVersion == null || m_strVersion.equals( ""))
      m_strVersion = "Version";
    String strText =
      "<HTML><P ALIGN=\"CENTER\">" +
      "<H3>" + strHeader + "</H3>" +
      strAppDesc + "<BR>" +
      m_strVersion + "<BR>" +
      "</p></HTML>";

    constructor( frame, title, modal, strText, icon);
  }

  public AboutDialog( String strAppName)
  {
    this(null, "About", true, null, null, strAppName);
  }

  public void constructor( Component frame, String title, boolean modal, String strHtml, ImageIcon icon)
  {
    setFrame( frame);
    m_frame = frame;
    try
    {
      jbInit();
      if( icon != null)
        m_jLabelIcon.setIcon( icon);
      m_jPanelBase.add( m_jLabelIcon, BorderLayout.WEST );
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
        "<H3>Application</H3>" +
        "Application description<BR>" +
        "Version<BR>" +
        "</p></HTML>";
    }
    m_jLabelText.setText( strText);
    pack();
    m_jButtonOk.addActionListener(this);
  }

  void jbInit() throws Exception
  {
    m_border = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(Color.white,new Color(134, 134, 134)),BorderFactory.createEmptyBorder(5,5,5,5));
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelButton.setLayout(m_flowLayoutButton);
    m_jButtonOk.setText("Ok");
    this.setResizable(false);
    m_borderLayoutBase.setHgap(5);
    m_borderLayoutBase.setVgap(5);
    m_jLabelText.setAlignmentX((float) 0.5);
    m_jLabelText.setBorder(m_border);
    m_jLabelText.setHorizontalAlignment(SwingConstants.LEADING);
    m_jLabelText.setHorizontalTextPosition(SwingConstants.TRAILING);
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jLabelText, BorderLayout.CENTER);
    m_jPanelBase.add(m_jPanelButton, BorderLayout.SOUTH);
    m_jPanelButton.add(m_jButtonOk, null);
  }

  public void actionPerformed(ActionEvent event)
  {
    setVisible(false);
    // dispose();
  }
}

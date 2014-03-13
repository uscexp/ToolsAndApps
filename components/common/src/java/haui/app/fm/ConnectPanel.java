package haui.app.fm;

import haui.util.AppProperties;
import java.awt.BorderLayout;
import java.io.File;
import java.lang.reflect.Array;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Module:					ConnectPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConnectPanel.java,v $ <p> Description:    Connection selection panel.<br> </p><p> Created:				25.06.2001	by	AE </p><p>
 * @history  				25.06.2001 - 26.06.2001	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ConnectPanel.java,v $  Revision 1.2  2004-06-22 14:08:56+02  t026843  bigger changes  Revision 1.1  2003-05-28 14:19:57+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:41+02  t026843  Initial revision  Revision 1.1  2001-08-14 16:48:42+02  t026843  default button added  Revision 1.0  2001-07-20 16:32:52+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConnectPanel.java,v 1.2 2004-06-22 14:08:56+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ConnectPanel extends JPanel
{
  private static final long serialVersionUID = -6297051692846983616L;
  
  // member variables
  AppProperties m_appProps;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JComboBox m_jComboBoxConnect = new JComboBox();
  JLabel m_jLabelConnect = new JLabel();
  String m_strSel = FileInfoPanel.NEWLOC;

  public ConnectPanel()
  {
    this( null);
  }

  public ConnectPanel( AppProperties appProps)
  {
    if( appProps != null)
      m_appProps = appProps;
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  void jbInit() throws Exception
  {
    this.setLayout(m_borderLayoutBase);
    m_jLabelConnect.setText("Connect to: ");
    this.add(m_jComboBoxConnect, BorderLayout.CENTER);
    this.add(m_jLabelConnect, BorderLayout.WEST);
  }

  public void _init()
  {
    File fi = new File( ".");
    String fiNames[] = fi.list();

    m_jComboBoxConnect.removeAllItems();
    //m_jComboBoxConnect.addItem( FileInfoPanel.NEWLOC);

    for( int i = 0; i < Array.getLength( fiNames); i++)
    {
      if( fiNames[i].toLowerCase().endsWith( FileInfoPanel.FILEEXT))
      {
        String str = fiNames[i].substring( 0, fiNames[i].length() - FileInfoPanel.FILEEXT.length());
        m_jComboBoxConnect.addItem( str);
      }
    }
  }

  public void _save()
  {
    m_strSel = (String)m_jComboBoxConnect.getSelectedItem();
  }

  public void requestFocus()
  {
    m_jComboBoxConnect.requestFocus();
  }

  public String getSelection()
  {
    return m_strSel;
  }
}

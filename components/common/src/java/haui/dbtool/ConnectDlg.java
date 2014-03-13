
package haui.dbtool;

import haui.components.JExDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;

/**
 * Module:					ConnectDlg.java <p> Description:		Dialog to enter DB URL and Driver </p><p> Created:				26.06.1998	by	AE </p><p> Last Modified:	15.04.1999	by	AE </p><p> history					15.04.1999	by	AE:	Converted to JDK v1.2<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999  </p><p>
 * @since  					JDK1.2  </p>
 */
class ConnectDlg
extends JExDialog
implements ActionListener
{
  private static final long serialVersionUID = -2436929917963645088L;
  
  javax.swing.JPanel EditTopPannel;
  javax.swing.JPanel EditRightPannel;
  javax.swing.JPanel EditLeftPannel;
  javax.swing.JLabel driver;
  javax.swing.JTextField driverField;
  javax.swing.JLabel url;
  javax.swing.JTextField urlField;
  javax.swing.JLabel dummy;
  javax.swing.JCheckBox debug;
  javax.swing.JPanel ButPanel;
  javax.swing.JButton ok;

  String m_strDriver = "oracle.jdbc.driver.OracleDriver";
  String m_strUrl = "jdbc:oracle:thin:@m-info.sbcs.swissbank.com:1521:fidb";
  boolean m_blDbg = false;

  public ConnectDlg(String strAppName)
  {
    super( strAppName);
    //{{INIT_CONTROLS
    //}}
    setTitle("Jdbc Parameter");
    setResizable(false);
    setModal(true);
    getContentPane().setLayout(new BorderLayout(0,0));
    EditTopPannel = new javax.swing.JPanel();
    EditTopPannel.setLayout(new BorderLayout(0,0));
    EditTopPannel.setBounds(0,0,20,40);
    EditTopPannel.setFont(new Font("Dialog", Font.PLAIN, 12));
    EditTopPannel.setForeground(new Color(0));
    EditTopPannel.setBackground(new Color(-3355444));
    EditRightPannel = new javax.swing.JPanel();
    EditRightPannel.setLayout(new GridLayout(3,1,0,0));
    EditRightPannel.setBounds(0,0,20,40);
    EditRightPannel.setFont(new Font("Dialog", Font.PLAIN, 12));
    EditRightPannel.setForeground(new Color(0));
    EditRightPannel.setBackground(new Color(-3355444));
    EditLeftPannel = new javax.swing.JPanel();
    EditLeftPannel.setLayout(new GridLayout(3,1,0,0));
    EditLeftPannel.setBounds(0,0,20,40);
    EditLeftPannel.setFont(new Font("Dialog", Font.PLAIN, 12));
    EditLeftPannel.setForeground(new Color(0));
    EditLeftPannel.setBackground(new Color(-3355444));
    getContentPane().add("Center", EditTopPannel);
    driver = new javax.swing.JLabel();
    driver.setText("Jdbc Driver:");
    driver.setBounds(0,0,20,40);
    driver.setFont(new Font("Dialog", Font.BOLD, 12));
    driver.setForeground(new Color(-10066279));
    driver.setBackground(new Color(-3355444));
    EditRightPannel.add(driver);
    driverField = new javax.swing.JTextField();
    driverField.setBounds(0,0,20,40);
    driverField.setFont(new Font("SansSerif", Font.PLAIN, 12));
    driverField.setForeground(new Color(0));
    driverField.setBackground(new Color(16777215));
    EditLeftPannel.add(driverField);
    url = new javax.swing.JLabel();
    url.setText("Jdbc Url:");
    url.setBounds(0,0,20,40);
    url.setFont(new Font("Dialog", Font.BOLD, 12));
    url.setForeground(new Color(-10066279));
    url.setBackground(new Color(-3355444));
    EditRightPannel.add(url);
    urlField = new javax.swing.JTextField();
    urlField.setBounds(0,0,20,40);
    urlField.setFont(new Font("SansSerif", Font.PLAIN, 12));
    urlField.setForeground(new Color(0));
    urlField.setBackground(new Color(16777215));
    EditLeftPannel.add(urlField);
    dummy = new javax.swing.JLabel();
    dummy.setBounds(0,0,20,40);
    dummy.setFont(new Font("Dialog", Font.BOLD, 12));
    dummy.setForeground(new Color(-10066279));
    dummy.setBackground(new Color(-3355444));
    EditRightPannel.add(dummy);
    debug = new javax.swing.JCheckBox();
    debug.setText("Database Debug Mode");
    debug.setBounds(0,0,23,40);
    debug.setFont(new Font("Dialog", Font.BOLD, 12));
    debug.setForeground(new Color(0));
    debug.setBackground(new Color(-3355444));
    EditLeftPannel.add(debug);
    EditTopPannel.add("West", EditRightPannel);
    EditTopPannel.add("Center", EditLeftPannel);
    ButPanel = new javax.swing.JPanel();
    ButPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
    ButPanel.setBounds(0,0,20,40);
    ButPanel.setFont(new Font("Dialog", Font.PLAIN, 12));
    ButPanel.setForeground(new Color(0));
    ButPanel.setBackground(new Color(-3355444));
    getContentPane().add("South", ButPanel);
    ok = new javax.swing.JButton();
    ok.setText("Ok");
    ok.setActionCommand("Ok");
    ok.setBounds(0,0,35,40);
    ok.setFont(new Font("Dialog", Font.BOLD, 12));
    ok.setForeground(new Color(0));
    ok.setBackground(new Color(-3355444));
    ButPanel.add(ok);
    //End INIT_CONTROLS
    ok.addActionListener(this);
    driverField.setText( m_strDriver);
    urlField.setText( m_strUrl);
    Point parloc = getParent().getLocation();
      setLocation(parloc.x + 30, parloc.y + 30);
    pack();
    setVisible( true);

    //{{REGISTER_LISTENERS
    //SymAction lSymAction = new SymAction();
    //ok.addActionListener(this);
    //}}
  }

    /**
     * @return  Returns the driver.
     * @uml.property  name="driver"
     */
    public String getDriver()
    {
        return m_strDriver;
    }

    /**
     * @return  Returns the url.
     * @uml.property  name="url"
     */
    public String getUrl()
    {
        return m_strUrl;
    }

    public boolean getDbg()
    {
        return m_blDbg;
    }

/*	class SymAction implements java.awt.event.ActionListener
  {*/
    public void actionPerformed(java.awt.event.ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        ok_actionPerformed(event);
    }
//	}

  void ok_actionPerformed(java.awt.event.ActionEvent event)
  {
    //{{CONNECTION
    {
        m_strDriver = driverField.getText();
        m_strUrl = urlField.getText();
        m_blDbg = debug.isSelected();
        setVisible(false);
        // dispose();
    }
    //}}
  }
}

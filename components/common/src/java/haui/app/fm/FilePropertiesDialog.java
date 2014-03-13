package haui.app.fm;

import haui.components.JExDialog;
import haui.io.FileInterface.FileInterface;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Module:					FilePropertiesDialog.java<br> $Source: $ <p> Description:    connection dialog.<br> </p><p> Created:				03.07.2002	by	AE </p><p>
 * @history  				03.07.2002 - 03.07.2002	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				$<br>  $Header: $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FilePropertiesDialog
  extends JExDialog
{
  private static final long serialVersionUID = -8797315897683533482L;
  
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JPanel m_jPanelProps = new JPanel();
  GridLayout m_gridLayoutProps = new GridLayout();
  JLabel m_jLabelType = new JLabel();
  JTextField m_jTextFieldTypeVal = new JTextField();
  JLabel m_jLabelSize = new JLabel();
  JTextField m_jTextFieldSizeVal = new JTextField();
  JLabel m_jLabelName = new JLabel();
  JTextField m_jTextFieldNameVal = new JTextField();
  JLabel m_jLabelPath = new JLabel();
  JTextField m_jTextFieldPathVal = new JTextField();
  JLabel m_jLabelDate = new JLabel();
  JTextField m_jTextFieldDateVal = new JTextField();
  TitledBorder m_titledBorderProps;
  FileInterface m_fi;
  JPanel m_jPanelAttrib = new JPanel();
  GridLayout m_gridLayoutAttrib = new GridLayout();
  TitledBorder titledBorder1;
  JLabel m_jLabelAttrib = new JLabel();
  JCheckBox m_jCheckBoxRead = new JCheckBox();
  JLabel m_jLabelDum1 = new JLabel();
  JCheckBox m_jCheckBoxWrite = new JCheckBox();
  JLabel m_jLabelDum2 = new JLabel();
  JCheckBox m_jCheckBoxHidden = new JCheckBox();
  //JLabel m_jLabelDirVolSize = new JLabel();
  //JTextField m_jTextFieldDirVolSize = new JTextField();

  public FilePropertiesDialog(FileInterface fi, Component frame, String title, boolean modal)
  {
    super( (Frame)null, title, modal, FileManager.APPNAME);
    setFrame( frame);
    m_fi = fi;

    try
    {
      jbInit();
      _init();
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    //setSize( 600, getHeight());
    setResizable( false);
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
  }

  void jbInit() throws Exception
  {
    m_titledBorderProps = new TitledBorder("");
    titledBorder1 = new TitledBorder("");
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jPanelProps.setLayout(m_gridLayoutProps);
    m_gridLayoutProps.setRows(6);
    m_gridLayoutProps.setColumns(2);
    m_jLabelType.setText("Type:");
    m_jLabelSize.setText("Size:");
    m_jLabelName.setText("Name:");
    m_jLabelPath.setText("Path:");
    m_jLabelDate.setText("Date:");
    m_jPanelProps.setBorder(m_titledBorderProps);
    m_jPanelAttrib.setLayout(m_gridLayoutAttrib);
    m_gridLayoutAttrib.setRows(3);
    m_gridLayoutAttrib.setColumns(2);
    m_jPanelAttrib.setBorder(titledBorder1);
    m_jLabelAttrib.setText("Attributes:");
    m_jCheckBoxRead.setEnabled(false);
    m_jCheckBoxRead.setText("Read");
    m_jCheckBoxWrite.setEnabled(false);
    m_jCheckBoxWrite.setText("Write");
    m_jCheckBoxHidden.setEnabled(false);
    m_jCheckBoxHidden.setText("Hidden");
    m_jTextFieldTypeVal.setEditable(false);
    m_jTextFieldTypeVal.setText("");
    m_jTextFieldSizeVal.setEditable(false);
    m_jTextFieldSizeVal.setText("");
    m_jTextFieldNameVal.setEditable(false);
    m_jTextFieldPathVal.setEditable(false);
    m_jTextFieldDateVal.setEditable(false);
    //m_jLabelDirVolSize.setText("Dir. vol. size:");
    //m_jTextFieldDirVolSize.setEditable(false);
    //m_jTextFieldDirVolSize.setText("jTextField1");
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelBase.add(m_jPanelProps, BorderLayout.NORTH);
    m_jPanelProps.add(m_jLabelName, null);
    m_jPanelProps.add(m_jTextFieldNameVal, null);
    m_jPanelProps.add(m_jLabelPath, null);
    m_jPanelProps.add(m_jTextFieldPathVal, null);
    m_jPanelProps.add(m_jLabelType, null);
    m_jPanelProps.add(m_jTextFieldTypeVal, null);
    m_jPanelProps.add(m_jLabelSize, null);
    m_jPanelProps.add(m_jTextFieldSizeVal, null);
    m_jPanelProps.add(m_jLabelDate, null);
    m_jPanelProps.add(m_jTextFieldDateVal, null);
    //m_jPanelProps.add(m_jLabelDirVolSize, null);
    //m_jPanelProps.add(m_jTextFieldDirVolSize, null);
    m_jPanelBase.add(m_jPanelAttrib, BorderLayout.CENTER);
    m_jPanelAttrib.add(m_jLabelAttrib, null);
    m_jPanelAttrib.add(m_jCheckBoxRead, null);
    m_jPanelAttrib.add(m_jLabelDum1, null);
    m_jPanelAttrib.add(m_jCheckBoxWrite, null);
    m_jPanelAttrib.add(m_jLabelDum2, null);
    m_jPanelAttrib.add(m_jCheckBoxHidden, null);
  }

  public void _init()
  {
    m_jTextFieldNameVal.setText( m_fi.getName());
    m_jTextFieldPathVal.setText( m_fi.getAbsolutePath());
    //m_jLabelDirVolSize.setVisible( false);
    //m_jTextFieldDirVolSize.setVisible( false);
    if( m_fi.isDirectory())
    {
      m_jTextFieldTypeVal.setText( "Directory" );
      //m_jLabelDirVolSize.setVisible( true);
      //m_jTextFieldDirVolSize.setVisible( true);
      //m_jTextFieldDirVolSize.setText( String.valueOf( FileConnector.lengthAndCountOfFilesOfDir( m_fi, null, true)[0]));
    }
    else if( m_fi.isArchive())
      m_jTextFieldTypeVal.setText( "Archive");
    else if( m_fi.isFile())
      m_jTextFieldTypeVal.setText( "File");
    m_jTextFieldSizeVal.setText( String.valueOf( m_fi.length()));
    Date date = new Date( m_fi.lastModified());
    m_jTextFieldDateVal.setText( date.toString());
    m_jCheckBoxRead.setSelected( m_fi.canRead());
    m_jCheckBoxWrite.setSelected( m_fi.canWrite());
    m_jCheckBoxHidden.setSelected( m_fi.isHidden());
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd == "Ok")
        m_jButtonOk_actionPerformed( event);
    }
  }

  public void setVisible( boolean b)
  {
    super.setVisible( b);
    m_jButtonOk.requestFocus();
  }

  void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    setVisible( false);
    dispose();
  }
}

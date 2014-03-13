package haui.app.fm;

import haui.components.VerticalFlowLayout;
import haui.util.CommandClass;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * Module:					ConfigCommandPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConfigCommandPanel.java,v $ <p> Description:    JPanel to config CommandClass(es).<br> </p><p> Created:				07.11.2000	by	AE </p><p>
 * @history  				07.11.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: ConfigCommandPanel.java,v $  Revision 1.3  2004-08-31 16:03:01+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2004-06-22 14:08:55+02  t026843  bigger changes  Revision 1.1  2003-06-06 10:04:01+02  t026843  modifications because of the moving the 'TypeFile's to haui.io package  Revision 1.0  2003-05-21 16:25:39+02  t026843  Initial revision  Revision 1.5  2002-09-03 17:07:57+02  t026843  - CgiTypeFile is now full functional.  - Migrated to the extended filemanager.pl script.  Revision 1.4  2002-07-01 16:42:07+02  t026843  FileCooser, to select commands, added  Revision 1.3  2002-06-06 16:06:07+02  t026843  bugfix with starter menu  Revision 1.2  2002-05-29 11:18:14+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.1  2001-08-14 16:48:41+02  t026843  default button added  Revision 1.0  2001-07-20 16:32:51+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.3 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\ConfigCommandPanel.java,v 1.3 2004-08-31 16:03:01+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class ConfigCommandPanel extends JPanel
{
  private static final long serialVersionUID = 32469375747325496L;
  
  CommandClass m_cmd;
  String m_strAppName;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelCenter = new JPanel();
  BorderLayout m_borderLayoutCenter = new BorderLayout();
  JPanel m_jPanelFields = new JPanel();
  JPanel m_jPanelHelp = new JPanel();
  JLabel m_jLabelP = new JLabel();
  JLabel m_jLabelPercent = new JLabel();
  JLabel m_jLabelQest = new JLabel();
  JLabel m_jLabelT = new JLabel();
  JLabel m_jLabelN = new JLabel();
  JLabel m_jLabelHead = new JLabel();
  JLabel m_jLabelTitle = new JLabel();
  TitledBorder titledBorder1;
  VerticalFlowLayout m_verticalFlowLayoutHelp = new VerticalFlowLayout();
  VerticalFlowLayout m_verticalFlowLayoutFields = new VerticalFlowLayout();
  JPanel m_jPanelIdent = new JPanel();
  JLabel m_jLabelIdentifier = new JLabel();
  JTextField m_jTextFieldIdentifier = new JTextField();
  BorderLayout m_borderLayoutIdent = new BorderLayout();
  JPanel m_jPanelCommand = new JPanel();
  BorderLayout m_borderLayoutCommand = new BorderLayout();
  JLabel m_jLabelCommando = new JLabel();
  JTextField m_jTextFieldCommando = new JTextField();
  JPanel m_jPanelParam = new JPanel();
  BorderLayout m_borderLayoutParam = new BorderLayout();
  JLabel m_jLabelParams = new JLabel();
  JTextField m_jTextFieldParameter = new JTextField();
  JPanel m_jPanelMnemonic = new JPanel();
  BorderLayout m_borderLayoutMnemonic = new BorderLayout();
  JLabel m_jLabelMnemonic = new JLabel();
  JTextField m_jTextFieldMnemonic = new JTextField();
  JPanel m_jPanelMouseBut = new JPanel();
  BorderLayout m_borderLayoutMouseBut = new BorderLayout();
  JLabel m_jLabelMouseButton = new JLabel();
  JComboBox m_jComboBoxMouseButton = new JComboBox();
  JButton m_jButtonBrowse = new JButton();
  JCheckBox m_jCheckBoxExecLocal = new JCheckBox();
  JPanel m_jPanelStartDir = new JPanel();
  JLabel m_jLabelStartDir = new JLabel();
  BorderLayout m_borderLayoutStartDir = new BorderLayout();
  JButton m_jButtonBrowseDir = new JButton();
  JTextField m_jTextFieldStartDir = new JTextField();
  JLabel m_jLabelHead1 = new JLabel();
  JLabel m_jLabelHead2 = new JLabel();
  JLabel m_jLabelHeadPath = new JLabel();
  JLabel m_jLabelExtPath = new JLabel();

  public ConfigCommandPanel( String strAppName)
  {
    this( null, strAppName);
  }

  public ConfigCommandPanel( CommandClass cmd, String strAppName)
  {
    try
    {
      m_strAppName = strAppName;
      jbInit();
      m_jComboBoxMouseButton.removeAllItems();
      m_jComboBoxMouseButton.addItem( CommandClass.getMouseButtonText( CommandClass.LEFT));
      m_jComboBoxMouseButton.addItem( CommandClass.getMouseButtonText( CommandClass.MIDDLE));
      m_jComboBoxMouseButton.addItem( CommandClass.getMouseButtonText( CommandClass.RIGHT));
      m_jComboBoxMouseButton.setSelectedIndex(0);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    LisAction actionlistener = new LisAction();
    m_jButtonBrowse.addActionListener( actionlistener);
    m_jButtonBrowseDir.addActionListener( actionlistener);

    if( cmd != null)
      m_cmd = (CommandClass)cmd.clone();
    // Mnemonic disabled
    hideForFileEx();
  }

  void jbInit() throws Exception
  {
    titledBorder1 = new TitledBorder("");
    this.setLayout(m_borderLayoutBase);
    m_jPanelCenter.setLayout(m_borderLayoutCenter);
    m_jPanelFields.setLayout(m_verticalFlowLayoutFields);
    m_jPanelHelp.setLayout(m_verticalFlowLayoutHelp);
    m_jLabelP.setText("%p: adds source path");
    m_jLabelPercent.setText("%%: % character");
    m_jLabelQest.setText("%?: opens a dialog for input");
    m_jLabelT.setText("%t: adds destination path (if it is the same location)");
    m_jLabelN.setText("%n: adds selected name");
    m_jLabelHead.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelHead.setText("Parameter help:");
    m_jLabelTitle.setText("Command settings:");
    m_jLabelTitle.setFont(new java.awt.Font("Dialog", 1, 12));
    m_verticalFlowLayoutHelp.setVgap(1);
    m_verticalFlowLayoutFields.setHgap(1);
    m_verticalFlowLayoutFields.setVgap(1);
    m_jPanelIdent.setLayout(m_borderLayoutIdent);
    m_jLabelIdentifier.setText("Identifier:          ");
    m_jTextFieldIdentifier.setPreferredSize(new Dimension(140, 21));
    m_jPanelCommand.setLayout(m_borderLayoutCommand);
    m_jLabelCommando.setText("Commando:      ");
    m_jPanelParam.setLayout(m_borderLayoutParam);
    m_jLabelParams.setText("Parameter:       ");
    m_jPanelMnemonic.setLayout(m_borderLayoutMnemonic);
    m_jLabelMnemonic.setText("Mnemonic:       ");
    m_jPanelMouseBut.setLayout(m_borderLayoutMouseBut);
    m_jLabelMouseButton.setText("Mouse Button:  ");
    m_jComboBoxMouseButton.setActionCommand("CBoxMouseButtonChanged");
    m_jButtonBrowse.setActionCommand("browse");
    m_jButtonBrowse.setText("Browse");
    m_jCheckBoxExecLocal.setSelected(true);
    m_jCheckBoxExecLocal.setText("Execute local: ");
    m_jCheckBoxExecLocal.setHorizontalTextPosition(SwingConstants.LEADING);
    m_jPanelStartDir.setLayout(m_borderLayoutStartDir);
    m_jLabelStartDir.setText("Start Directory:");
    m_jButtonBrowseDir.setActionCommand("browsedir");
    m_jButtonBrowseDir.setText("Browse");
    m_jLabelHead1.setText("Parameter help:");
    m_jLabelHead1.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelHead2.setText("Parameter help:");
    m_jLabelHead2.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelHeadPath.setText("Start dir and command dir help:");
    m_jLabelHeadPath.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelExtPath.setText("%e: adds command path found in ext. path");
    this.add(m_jPanelCenter, BorderLayout.CENTER);
    m_jPanelCenter.add(m_jPanelFields, BorderLayout.CENTER);
    m_jPanelMouseBut.add(m_jLabelMouseButton, BorderLayout.WEST);
    m_jPanelMouseBut.add(m_jComboBoxMouseButton, BorderLayout.CENTER);
    m_jPanelMnemonic.add(m_jLabelMnemonic, BorderLayout.WEST);
    m_jPanelMnemonic.add(m_jTextFieldMnemonic, BorderLayout.CENTER);
    m_jPanelParam.add(m_jLabelParams, BorderLayout.WEST);
    m_jPanelParam.add(m_jTextFieldParameter, BorderLayout.CENTER);
    m_jPanelCommand.add(m_jLabelCommando, BorderLayout.WEST);
    m_jPanelCommand.add(m_jTextFieldCommando, BorderLayout.CENTER);
    m_jPanelCommand.add(m_jButtonBrowse, BorderLayout.EAST);
    m_jPanelIdent.add(m_jLabelIdentifier, BorderLayout.WEST);
    m_jPanelIdent.add(m_jTextFieldIdentifier, BorderLayout.CENTER);
    m_jPanelStartDir.add(m_jLabelStartDir, BorderLayout.WEST);
    m_jPanelStartDir.add(m_jTextFieldStartDir, BorderLayout.CENTER);
    m_jPanelStartDir.add(m_jButtonBrowseDir, BorderLayout.EAST);
    m_jPanelFields.add(m_jPanelIdent, null);
    m_jPanelFields.add(m_jPanelStartDir, null);
    m_jPanelFields.add(m_jPanelCommand, null);
    m_jPanelFields.add(m_jPanelParam, null);
    m_jPanelFields.add(m_jPanelMnemonic, null);
    m_jPanelFields.add(m_jPanelMouseBut, null);
    m_jPanelFields.add(m_jCheckBoxExecLocal, null);
    this.add(m_jPanelHelp, BorderLayout.SOUTH);
    m_jPanelHelp.add(m_jLabelHeadPath, null);
    m_jPanelHelp.add(m_jLabelExtPath, null);
    m_jPanelHelp.add(m_jLabelHead, null);
    m_jPanelHelp.add(m_jLabelT, null);
    m_jPanelHelp.add(m_jLabelP, null);
    m_jPanelHelp.add(m_jLabelN, null);
    m_jPanelHelp.add(m_jLabelQest, null);
    m_jPanelHelp.add(m_jLabelPercent, null);
    this.add(m_jLabelTitle, BorderLayout.NORTH);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if (cmd.equals( "browse"))
      {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog( getParent());
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
          if( chooser.getSelectedFile() != null && chooser.getSelectedFile().getAbsolutePath() != null)
            m_jTextFieldCommando.setText( chooser.getSelectedFile().getAbsolutePath());
        }
      }
      else if (cmd.equals( "browsedir"))
      {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog( getParent());
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
          if( chooser.getSelectedFile() != null && chooser.getSelectedFile().getAbsolutePath() != null)
            m_jTextFieldStartDir.setText( chooser.getSelectedFile().getAbsolutePath());
        }
      }
    }
  }

  public void setCommand( CommandClass cmd)
  {
    if( cmd != null)
      m_cmd = (CommandClass)cmd.clone();
  }

  public CommandClass getCommand()
  {
    return m_cmd;
  }

  public void hideMouseButtonCombo()
  {
    m_jPanelMouseBut.setVisible( false);
  }

  public void showMouseButtonCombo()
  {
    m_jPanelMouseBut.setVisible( true);
  }

  public void hideForFileEx()
  {
    m_jPanelMnemonic.setVisible( false);
  }

  public void showAfterFileEx()
  {
    // disabled
    m_jPanelMnemonic.setVisible( false);
  }

  public void hideForStarter()
  {
    m_jPanelMouseBut.setVisible( false);
    //m_jLabelP.setVisible( false);
    //m_jLabelN.setVisible( false);
    //m_jLabelT.setVisible( false);
    m_jPanelMnemonic.setVisible( false);
    //m_jCheckBoxExecLocal.setVisible( false);
  }

  public void showAfterStarter()
  {
    m_jPanelMouseBut.setVisible( true);
    m_jLabelP.setVisible( true);
    m_jLabelN.setVisible( true);
    m_jLabelT.setVisible( true);
    m_jPanelMnemonic.setVisible( true);
    //m_jCheckBoxExecLocal.setVisible( true);
  }

  public void _init()
  {
    if( m_cmd == null)
      return;
    m_jTextFieldIdentifier.setText( m_cmd.getIdentifier());
    m_jTextFieldStartDir.setText( m_cmd.getStartDir());
    m_jTextFieldCommando.setText( m_cmd.getCommand());
    m_jTextFieldParameter.setText( m_cmd.getParams());
    int iM = m_cmd.getMnemonic();
    if( iM != -1)
      m_jTextFieldMnemonic.setText( String.valueOf( (char)iM));
    int i = m_cmd.getMouseButton();
    if( i > 0)
      i -= 1;
    m_jComboBoxMouseButton.setSelectedIndex( i);
    m_jCheckBoxExecLocal.setSelected( m_cmd.getExecLocal());
  }

  public void _clear()
  {
    m_jTextFieldIdentifier.setText( "");
    m_jTextFieldStartDir.setText( "");
    m_jTextFieldCommando.setText( "");
    m_jTextFieldParameter.setText( "");
  }

  public void _save()
  {
    if( m_jTextFieldIdentifier.getText() == null || m_jTextFieldIdentifier.getText().equals(""))
    {
      m_cmd = null;
      return;
    }
    if( m_cmd == null)
      m_cmd = new CommandClass( m_strAppName);
    m_cmd.setIdentifier( m_jTextFieldIdentifier.getText());
    m_cmd.setStartDir( m_jTextFieldStartDir.getText());
    m_cmd.setCommand( m_jTextFieldCommando.getText());
    m_cmd.setParams( m_jTextFieldParameter.getText());
    if( m_jPanelMnemonic.isVisible() && m_jTextFieldMnemonic.getText() != null && m_jTextFieldMnemonic.getText() != "")
      m_cmd.setMnemonic( (int)(m_jTextFieldMnemonic.getText().charAt(0)));
    else
      m_cmd.setMnemonic( -1);
    if( m_jPanelMouseBut.isVisible())
      m_cmd.setMouseButton( m_jComboBoxMouseButton.getSelectedIndex()+1);
    else
      m_cmd.setMouseButton( CommandClass.NO);
    m_cmd.setExecLocal( m_jCheckBoxExecLocal.isSelected());
  }

  public void requestFocus()
  {
    m_jTextFieldIdentifier.requestFocus();
  }
}
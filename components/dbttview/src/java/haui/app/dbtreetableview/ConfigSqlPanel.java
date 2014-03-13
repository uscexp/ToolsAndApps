package haui.app.dbtreetableview;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

/**
 *		Module:					ConfigSqlPanel.java<br>
 *										$Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\ConfigSqlPanel.java,v $
 *<p>
 *		Description:    JPanel to config SqlClass(es).<br>
 *</p><p>
 *		Created:				22.08.2001	by	AE
 *</p><p>
 *		@history				22.08.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: ConfigSqlPanel.java,v $
 *		Revision 1.2  2004-06-22 14:08:56+02  t026843
 *		bigger changes
 *
 *		Revision 1.1  2004-02-17 15:57:58+01  t026843
 *		handles now submenus
 *
 *		Revision 1.0  2003-05-21 16:25:21+02  t026843
 *		Initial revision
 *
 *		Revision 1.0  2002-09-27 15:47:30+02  t026843
 *		Initial revision
 *
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2001; $Revision: 1.2 $<br>
 *										$Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\dbtreetableview\\ConfigSqlPanel.java,v 1.2 2004-06-22 14:08:56+02 t026843 Exp t026843 $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class ConfigSqlPanel extends JPanel
{
  SqlClass m_sql;
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelFieldLabels = new JPanel();
  GridLayout m_gridLayoutFieldLabels = new GridLayout();
  JLabel m_jLabelSql = new JLabel();
  JPanel m_jPanelCenter = new JPanel();
  BorderLayout m_borderLayoutCenter = new BorderLayout();
  JPanel m_jPanelFields = new JPanel();
  GridLayout m_gridLayoutFields = new GridLayout();
  JTextField m_jTextFieldSql = new JTextField();
  JPanel m_jPanelHelp = new JPanel();
  GridLayout m_gridLayoutHelp = new GridLayout();
  JLabel m_jLabelC = new JLabel();
  JLabel m_jLabelPercent = new JLabel();
  JLabel m_jLabelQest = new JLabel();
  JLabel m_jLabelHead = new JLabel();
  JLabel m_jLabelTitle = new JLabel();
  TitledBorder titledBorder1;
  JLabel m_jLabelIdentifier = new JLabel();
  JTextField m_jTextFieldIdentifier = new JTextField();
  JLabel m_jLabelNewline = new JLabel();
  JLabel m_jLabelTab = new JLabel();
  JLabel m_jLabelT = new JLabel();
  JLabel m_jLabelS = new JLabel();
  JTextField m_jTextFieldSubMenu = new JTextField();
  JLabel m_jLabelSubMenu = new JLabel();

  public ConfigSqlPanel()
  {
    this( null);
  }

  public ConfigSqlPanel( SqlClass cmd)
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    if( cmd != null)
      m_sql = (SqlClass)cmd.clone();
  }

  void jbInit() throws Exception
  {
    titledBorder1 = new TitledBorder("");
    this.setLayout(m_borderLayoutBase);
    m_jPanelFieldLabels.setLayout(m_gridLayoutFieldLabels);
    m_gridLayoutFieldLabels.setRows(3);
    m_gridLayoutFieldLabels.setColumns(1);
    m_jLabelSql.setText("Sql part:");
    m_jPanelCenter.setLayout(m_borderLayoutCenter);
    m_jPanelFields.setLayout(m_gridLayoutFields);
    m_gridLayoutFields.setRows(3);
    m_gridLayoutFields.setColumns(1);
    m_jPanelHelp.setLayout(m_gridLayoutHelp);
    m_gridLayoutHelp.setColumns(1);
    m_gridLayoutHelp.setRows(8);
    m_jLabelC.setText("%c: adds column name");
    m_jLabelPercent.setText("%%: % character");
    m_jLabelQest.setText("%?: opens a dialog for input");
    m_jLabelHead.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelHead.setText("Help:");
    m_jLabelTitle.setText("Sql part settings:");
    m_jLabelTitle.setFont(new java.awt.Font("Dialog", 1, 12));
    m_jLabelIdentifier.setText("Identifier:");
    m_jLabelNewline.setText("\\n: adds a newline");
    m_jLabelTab.setText("\\t: adds a tab");
    m_jLabelT.setText("%t: adds table name");
    m_jLabelS.setText("%s: adds schema name");
    m_jLabelSubMenu.setText("SubMenu:");
    this.add(m_jPanelCenter, BorderLayout.CENTER);
    m_jPanelCenter.add(m_jPanelFields, BorderLayout.CENTER);
    m_jPanelFields.add(m_jTextFieldIdentifier, null);
    m_jPanelFields.add(m_jTextFieldSql, null);
    m_jPanelFields.add(m_jTextFieldSubMenu, null);
    m_jPanelCenter.add(m_jPanelFieldLabels, BorderLayout.WEST);
    m_jPanelFieldLabels.add(m_jLabelIdentifier, null);
    m_jPanelFieldLabels.add(m_jLabelSql, null);
    this.add(m_jPanelHelp, BorderLayout.SOUTH);
    m_jPanelHelp.add(m_jLabelHead, null);
    m_jPanelHelp.add(m_jLabelC, null);
    m_jPanelHelp.add(m_jLabelT, null);
    m_jPanelHelp.add(m_jLabelS, null);
    m_jPanelHelp.add(m_jLabelQest, null);
    m_jPanelHelp.add(m_jLabelPercent, null);
    m_jPanelHelp.add(m_jLabelNewline, null);
    m_jPanelHelp.add(m_jLabelTab, null);
    this.add(m_jLabelTitle, BorderLayout.NORTH);
    m_jPanelFieldLabels.add(m_jLabelSubMenu, null);
  }

  public void setSql( SqlClass sql)
  {
    if( sql != null)
      m_sql = (SqlClass)sql.clone();
  }

  public SqlClass getSql()
  {
    return m_sql;
  }

  public void _init()
  {
    if( m_sql == null)
      return;
    m_jTextFieldIdentifier.setText( m_sql.getIdentifier());
    m_jTextFieldSubMenu.setText( m_sql.getSubMenu());
    m_jTextFieldSql.setText( m_sql.getSql());
  }

  public void _save()
  {
    if( m_jTextFieldIdentifier.getText() == null || m_jTextFieldIdentifier.getText().equals(""))
    {
      m_sql = null;
      return;
    }
    if( m_sql == null)
      m_sql = new SqlClass();
    m_sql.setIdentifier( m_jTextFieldIdentifier.getText());
    m_sql.setSubMenu( m_jTextFieldSubMenu.getText());
    m_sql.setSql( m_jTextFieldSql.getText());
  }

  public void requestFocus()
  {
    m_jTextFieldIdentifier.requestFocus();
  }
}
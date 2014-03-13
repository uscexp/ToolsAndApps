package haui.app.fm;

import haui.io.FileInterface.filter.DirectoryFileInterfaceFilter;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.io.FileInterface.filter.OrFileInterfaceFilter;
import haui.io.FileInterface.filter.WildcardFileInterfaceFilter;
import haui.util.AppProperties;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Module:      FileFilterPanel.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileFilterPanel.java,v $ <p> Description: FileInterfaceFilter selection panel.<br> </p><p> Created:     22.07.2004 by AE </p><p>
 * @history      22.07.2004 AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileFilterPanel.java,v $  Revision 1.0  2004-08-31 15:57:36+02  t026843  Initial revision  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: 1.0 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileFilterPanel.java,v 1.0 2004-08-31 15:57:36+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.2  </p>
 */
public class FileFilterPanel
  extends JPanel
{
  private static final long serialVersionUID = -8242634117810625497L;

  // Constants
  final public static String FILTERDEF = "FilterDef";

  // member variables
  AppProperties m_appProps;
  String m_strAppName;
  String m_strSel = "*";
  LisAction m_actionlistener;
  JPanel m_jPanelMain = new JPanel();
  FlowLayout m_flowLayoutMain = new FlowLayout();
  JButton m_jButtonRemove = new JButton();
  JButton m_jButtonAdd = new JButton();
  JComboBox m_jComboBoxFilter = new JComboBox();
  JLabel m_jLabelFilter = new JLabel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JCheckBox m_jCheckBoxDir = new JCheckBox();

  public FileFilterPanel( String strAppName, AppProperties appProps)
  {
    m_appProps = appProps;
    m_strAppName = strAppName;
    try
    {
      jbInit();
      m_actionlistener = new LisAction();
      m_jButtonAdd.addActionListener( m_actionlistener);
      m_jButtonRemove.addActionListener( m_actionlistener);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  void jbInit()
    throws Exception
  {
    m_jPanelMain.setLayout(m_flowLayoutMain);
    this.setLayout(m_borderLayoutBase);
    m_jButtonRemove.setActionCommand("remove");
    m_jButtonRemove.setText("Remove");
    m_jButtonAdd.setActionCommand("add");
    m_jButtonAdd.setText("Add");
    m_jComboBoxFilter.setEditable(true);
    m_jLabelFilter.setText("Filter: ");
    m_jCheckBoxDir.setText("Accept all directories");
    m_flowLayoutMain.setAlignment(FlowLayout.LEFT);
    this.add(m_jPanelMain,  BorderLayout.CENTER);
    m_jPanelMain.add(m_jLabelFilter, null);
    m_jPanelMain.add(m_jComboBoxFilter, null);
    m_jPanelMain.add(m_jButtonAdd, null);
    m_jPanelMain.add(m_jButtonRemove, null);
    m_jCheckBoxDir.setSelected( true);
    this.add(m_jCheckBoxDir, BorderLayout.SOUTH);
  }

  public void _init()
  {
    //Vector vecFilterStrings = new Vector();
    String str;

    m_jComboBoxFilter.removeAllItems();
    for( int i = 0; ( str = m_appProps.getProperty( FILTERDEF + String.valueOf(i))) != null; ++i)
    {
      m_jComboBoxFilter.addItem( str);
    }
    if( m_jComboBoxFilter.getItemCount() == 0)
    {
      m_jComboBoxFilter.addItem( m_strSel );
      updateProperties();
    }
    m_jComboBoxFilter.setSelectedItem( m_strSel);
  }

  public void _save()
  {
    m_strSel = (String)m_jComboBoxFilter.getSelectedItem();
  }

  public void addFilter( String strFilter)
  {
    String str;
    int iIdx = -1;
    int iCompRes;

    for( int i = 0; i < m_jComboBoxFilter.getItemCount(); ++i)
    {
      str = (String)m_jComboBoxFilter.getItemAt( i);
      if( (iCompRes = strFilter.compareTo( str)) == 0)
        return;
      else if( iCompRes < 0 && iIdx == -1)
        iIdx = i;
    }
    if( !strFilter.equals( ""))
    {
      clearProperties();
      if( iIdx == -1 )
        m_jComboBoxFilter.addItem( strFilter );
      else
        m_jComboBoxFilter.insertItemAt( strFilter, iIdx );
      updateProperties();
    }
  }

  public void removeFilter( String strFilter)
  {
    clearProperties();
    for( int i = 0; i < m_jComboBoxFilter.getItemCount(); ++i)
    {
      String str = (String)m_jComboBoxFilter.getItemAt( i);
      if( strFilter.equals( str))
      {
        m_jComboBoxFilter.removeItemAt( i);
        m_jComboBoxFilter.setSelectedItem( "");
      }
    }
    updateProperties();
  }

  private void clearProperties()
  {
    for( int i = 0; i < m_jComboBoxFilter.getItemCount(); ++i)
    {
      m_appProps.remove( FILTERDEF + String.valueOf( i));
    }
  }

  private void updateProperties()
  {
    for( int i = 0; i < m_jComboBoxFilter.getItemCount(); ++i)
    {
      String str = (String)m_jComboBoxFilter.getItemAt( i);
      m_appProps.setProperty( FILTERDEF + String.valueOf( i), str);
    }
  }

  public void requestFocus()
  {
    m_jComboBoxFilter.requestFocus();
  }

  public FileInterfaceFilter getSelection()
  {
    String str = (String)m_jComboBoxFilter.getSelectedItem();
    OrFileInterfaceFilter fif = new OrFileInterfaceFilter( m_strAppName);
    if( m_jCheckBoxDir.isSelected())
      fif.addFilter( new DirectoryFileInterfaceFilter( m_strAppName));

    if( str != null)
    {
      Vector vecFilters = WildcardFileInterfaceFilter.createFileInterfaceFilters( str, m_strAppName );
      fif.addFilter( vecFilters );
    }
    return fif;
  }

  class LisAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event )
    {
      String cmd = event.getActionCommand();
      if( cmd.equals( "add"))
        addFilter( (String)m_jComboBoxFilter.getSelectedItem());
      else if( cmd.equals( "remove"))
        removeFilter( (String)m_jComboBoxFilter.getSelectedItem());
    }
  }
}


package haui.app.fm;

import haui.components.JExDialog;
import haui.components.TextInputDialog;
import haui.io.FileInterface.FileInterface;
import haui.resource.ResouceManager;
import haui.util.AppProperties;
import haui.util.CommandClass;
import haui.util.ConfigPathUtil;
import haui.util.GlobalApplicationContext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

/**
 * Module:					FipAllPropertiesDialog.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FipAllPropertiesDialog.java,v $ <p> Description:    FileInfoPanel properties dialog.<br> </p><p> Created:				27.02.2004	by	AE </p><p>
 * @history  				27.02.2004	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FipAllPropertiesDialog.java,v $  Revision 1.1  2004-08-31 16:03:04+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.0  2004-06-22 14:06:43+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2004; $Revision: 1.1 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FipAllPropertiesDialog.java,v 1.1 2004-08-31 16:03:04+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.3  </p>
 */
public class FipAllPropertiesDialog
  extends JExDialog
{
  private static final long serialVersionUID = 1184651298733629030L;
  
  // member variables
  AppProperties m_appProps;
  boolean m_blCanceled = false;
  List<CommandClass> m_vecFileCmd = new ArrayList<>();
  FipConnection m_fipc;
  List<FipConnection> m_vecFipConns = new ArrayList<>();

  // GUI member variables
  JPanel m_jPanelBase = new JPanel();
  BorderLayout m_borderLayoutBase = new BorderLayout();
  JPanel m_jPanelComponents = new JPanel();
  JPanel m_jPanelButtons = new JPanel();
  FlowLayout m_flowLayoutButtons = new FlowLayout();
  JButton m_jButtonOk = new JButton();
  JButton m_jButtonCancel = new JButton();
  BorderLayout m_borderLayoutComponents = new BorderLayout();
  JSplitPane m_jSplitPaneCenter = new JSplitPane();
  ConfigFileExtCommandPanel m_jPanelCoCmd = new ConfigFileExtCommandPanel();
  BorderLayout m_borderLayoutLeft = new BorderLayout();
  JPanel m_jPanelLeft = new JPanel();
  FipPropertiesPanel m_jPanelFip = new FipPropertiesPanel();
  JScrollPane m_jScrollPaneCmd = new JScrollPane();
  JList<String> m_jListCmd = new JList<>();
  DefaultListModel<String> m_dlm = new DefaultListModel<>();
  JToolBar m_jToolBarFip = new JToolBar();
  JButton m_jButtonRename = new JButton();
  JButton m_jButtonDelete = new JButton();
  JButton m_jButtonCopy = new JButton();
  JButton m_jButtonNew = new JButton();

  public FipAllPropertiesDialog( Component frame, String title, boolean modal, String strConnId)
  {
    super( frame, title, modal, FileManager.APPNAME);
    m_appProps = new AppProperties();
    m_jPanelFip = new FipPropertiesPanel( this, m_appProps);
    try
    {
      String strFileName = null;
      jbInit();
      m_jListCmd.setModel( m_dlm);
      m_jListCmd.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
      _init();
      if( strConnId != null && strConnId.equalsIgnoreCase( FileInterface.LOCAL))
        strConnId = null;
      if( strConnId != null)
      {
        strFileName = strConnId + FileInfoPanel.FILEEXT;
        m_jListCmd.setSelectedValue( strFileName, true);
      }
      else
      {
        if( m_dlm.size() > 0)
        {
          m_jListCmd.setSelectedIndex(0);
          m_jScrollPaneCmd.getVerticalScrollBar().setValue( 0);
          strFileName = (String)m_jListCmd.getSelectedValue();
          if( strFileName == null)
          {
            if( m_dlm.size() > 0)
            {
              strFileName = (String)m_dlm.getElementAt( 0);
            }
            else
              return;
          }
        }
      }
      updateData( strFileName);
      pack();
      getRootPane().setDefaultButton( m_jButtonOk);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    setSize( 600, getHeight());
    setResizable( false);
    m_jPanelFip._init();
    LisAction actionlis = new LisAction();
    m_jButtonOk.addActionListener( actionlis);
    m_jButtonCancel.addActionListener( actionlis);
    m_jButtonRename.addActionListener( actionlis);
    m_jButtonDelete.addActionListener( actionlis);
    m_jButtonCopy.addActionListener( actionlis);
    m_jButtonNew.addActionListener( actionlis);

    MouseHandler mouseHandler = new MouseHandler();
    m_jListCmd.addMouseListener( mouseHandler);
  }

  public FipAllPropertiesDialog(Component frame, String title, boolean modal)
  {
    this( frame, title, modal, null);
  }

  void jbInit() throws Exception
  {
    m_jPanelBase.setLayout(m_borderLayoutBase);
    m_jPanelComponents.setLayout(m_borderLayoutComponents);
    m_jPanelButtons.setLayout(m_flowLayoutButtons);
    m_jButtonOk.setText("Ok");
    m_jButtonCancel.setText("Cancel");
    m_jPanelLeft.setLayout(m_borderLayoutLeft);
    m_jPanelFip.setMinimumSize(new Dimension(300, 265));
    m_jPanelFip.setPreferredSize(new Dimension(300, 265));
    m_jButtonNew.setMaximumSize(new Dimension(18, 18));
    m_jButtonNew.setMinimumSize(new Dimension(18, 18));
    m_jButtonNew.setPreferredSize(new Dimension(18, 18));
    m_jButtonNew.setToolTipText("New");
    m_jButtonNew.setActionCommand("new");
    m_jButtonNew.setIcon( ResouceManager.getCommonImageIcon( FileManager.APPNAME, "Add16.gif"));
    m_jButtonRename.setMaximumSize(new Dimension(18, 18));
    m_jButtonRename.setMinimumSize(new Dimension(18, 18));
    m_jButtonRename.setPreferredSize(new Dimension(18, 18));
    m_jButtonRename.setToolTipText("Rename");
    m_jButtonRename.setActionCommand("rename");
    m_jButtonRename.setIcon( ResouceManager.getCommonImageIcon( FileManager.APPNAME, "Edit16.gif"));
    m_jButtonDelete.setMaximumSize(new Dimension(18, 18));
    m_jButtonDelete.setMinimumSize(new Dimension(18, 18));
    m_jButtonDelete.setPreferredSize(new Dimension(18, 18));
    m_jButtonDelete.setToolTipText("Delete");
    m_jButtonDelete.setActionCommand("delete");
    m_jButtonDelete.setIcon( ResouceManager.getCommonImageIcon( FileManager.APPNAME, "Delete16.gif"));
    m_jButtonCopy.setMaximumSize(new Dimension(18, 18));
    m_jButtonCopy.setMinimumSize(new Dimension(18, 18));
    m_jButtonCopy.setPreferredSize(new Dimension(18, 18));
    m_jButtonCopy.setToolTipText("Copy");
    m_jButtonCopy.setActionCommand("copy");
    m_jButtonCopy.setIcon( ResouceManager.getCommonImageIcon( FileManager.APPNAME, "Copy16.gif"));
    getContentPane().add(m_jPanelBase);
    m_jPanelBase.add(m_jPanelComponents, BorderLayout.CENTER);
    m_jPanelComponents.add(m_jSplitPaneCenter, BorderLayout.CENTER);
    m_jSplitPaneCenter.add(m_jPanelCoCmd, JSplitPane.RIGHT);
    m_jSplitPaneCenter.add(m_jPanelLeft, JSplitPane.LEFT);
    m_jPanelLeft.add(m_jPanelFip, BorderLayout.NORTH);
    m_jPanelBase.add(m_jPanelButtons, BorderLayout.SOUTH);
    m_jPanelButtons.add(m_jButtonOk, null);
    m_jPanelButtons.add(m_jButtonCancel, null);
    m_jPanelBase.add(m_jScrollPaneCmd,  BorderLayout.WEST);
    m_jPanelBase.add(m_jToolBarFip, BorderLayout.NORTH);
    m_jScrollPaneCmd.getViewport().add(m_jListCmd, null);
    m_jToolBarFip.add(m_jButtonNew, null);
    m_jToolBarFip.add(m_jButtonRename, null);
    m_jToolBarFip.add(m_jButtonCopy, null);
    m_jToolBarFip.addSeparator();
    m_jToolBarFip.add(m_jButtonDelete, null);
  }

  class LisAction implements ActionListener
  {
    public void actionPerformed( ActionEvent event)
    {
      String cmd = event.getActionCommand();
      if(cmd == "Ok")
        m_jButtonOk_actionPerformed( event);
      else if(cmd == "Cancel")
      {
        setVisible( false );
        m_blCanceled = true;
      }
      else if(cmd == "new")
      {
        onNew();
      }
      else if(cmd == "rename")
      {
        onRename();
      }
      else if(cmd == "copy")
      {
        onCopy();
      }
      else if(cmd == "delete")
      {
        onDelete();
      }
    }
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      //Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 1)
        rightMousePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2)
        leftMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 1)
        leftMousePressed( event);
    }
  }

  protected void leftMousePressed( MouseEvent event)
  {
    String strFileName = (String)m_jListCmd.getSelectedValue();
    if( strFileName != null)
      updateData( strFileName);
  }

  protected void leftMouseDoublePressed( MouseEvent event)
  {
  }

  protected void rightMousePressed( MouseEvent event)
  {
    //setSelectionAtMousePos( event.getX(), event.getY());
  }

  protected void setSelectionAtMousePos( int x, int y)
  {
    int idx = m_jListCmd.locationToIndex( new Point( x, y));
    if( idx != -1 && idx < m_dlm.size())
    {
      m_jListCmd.setSelectedIndex( idx);
    }
  }

  protected void updateData( String strFileName)
  {
    if( m_dlm.size() > 0)
    {
      if( strFileName == null)
        return;
      readCommands( strFileName);
      setCommandVector( m_vecFileCmd);
      m_jPanelFip.update( m_appProps);
      if( strFileName.equalsIgnoreCase( FileInterface.LOCAL + FileInfoPanel.FILEEXT ) )
        m_jPanelFip.setVisible( false );
      else
      {
        m_jPanelFip.setVisible( true );
        //Dimension dimWidth = m_jPanelFip.getMinimumSize();
        //m_jSplitPaneCenter.setDividerLocation( 0.5);
      }
      m_jPanelCoCmd.setVisible( false);
      //m_jPanelCoCmd.hideForFileEx();
    }
  }

  public void _init()
  {
    File fi = new File( ".");
    String fiNames[] = fi.list();

    m_dlm.removeAllElements();

    for( int i = 0; i < fiNames.length; i++)
    {
      if( fiNames[i].toLowerCase().endsWith( FileInfoPanel.FILEEXT)
          && !fiNames[i].equalsIgnoreCase( FileInterface.LOCAL + FileInfoPanel.FILEEXT))
      {
        //String str = fiNames[i].substring( 0, fiNames[i].length() - FileInfoPanel.FILEEXT.length());
        m_dlm.addElement( fiNames[i]);
      }
    }
  }

  public void _save()
  {
    String strHeader = "Connection, FileExt and Button command properties";
    for( int i = 0; i < m_vecFipConns.size(); ++i)
    {
      FipConnection fip = m_vecFipConns.get( i);
      fip._save();
      AppProperties fipProps = fip.getAppProperties();
      fipProps.remove( GlobalApplicationContext.LOGONPASSWORD);
      fipProps.remove( FileInfoPanel.FTPPASSWORD);
      // remove keys for terminal execution
      fipProps.remove( GlobalApplicationContext.PROXYHOST);
      fipProps.remove( GlobalApplicationContext.PROXYNOPROXYLIST);
      fipProps.remove( GlobalApplicationContext.PROXYUSER);
      fipProps.remove( GlobalApplicationContext.PROXYPASSWORD);
      fipProps.remove( GlobalApplicationContext.PROXYENABLE);
      fipProps.remove( GlobalApplicationContext.PROXYPORT);

      fipProps.store( ConfigPathUtil.getCurrentSavePath( FileManager.APPNAME, fip.m_strConnName), strHeader);
    }
  }

  protected void readCommands( String strConnId)
  {
    //BufferedReader br;
    String strFileName = null;
    //CommandClass cmd;
    //Vector buttonCmd = new Vector();
    //Vector fileCmd = new Vector();

    strFileName = (String)m_jListCmd.getSelectedValue();
    if( strFileName == null)
    {
      if( strConnId != null)
        strFileName = strConnId;
      else
      {
        if( m_dlm.size() > 0)
        {
          strFileName = (String)m_dlm.getElementAt( 0);
        }
        else
          return;
      }
    }
    int iIdx = strFileName.lastIndexOf( FileInfoPanel.FILEEXT);
    if( iIdx == -1)
    {
      strFileName += FileInfoPanel.FILEEXT;
    }

    if( m_fipc != null)
    {
      m_jPanelFip._save();
      m_fipc._save();
    }
    for( int i = 0; i < m_vecFipConns.size(); ++i)
    {
      FipConnection fip = m_vecFipConns.get( i);
      if( fip.getName().equals( strFileName))
      {
        m_fipc = fip;
        m_fipc._init();
        m_vecFileCmd = m_fipc.getFileExtCommands();
        m_appProps = m_fipc.getAppProperties();
        return;
      }
    }

    m_appProps = new AppProperties();
    if( m_appProps.load( ConfigPathUtil.getCurrentReadPath( FileManager.APPNAME, strFileName)))
    {
      m_fipc = new FipConnection( strFileName, FileManager.APPNAME, m_appProps);
      m_fipc._init();
      m_vecFileCmd = m_fipc.getFileExtCommands();
      if( m_fipc != null)
      {
        boolean blFound = false;
        for( int i = 0; i < m_vecFipConns.size(); ++i)
        {
          FipConnection fipcTmp = m_vecFipConns.get( i);
          if( fipcTmp.getName().equals( m_fipc.getName()))
          {
            m_vecFipConns.set(i, m_fipc);
            blFound = true;
            break;
          }
        }
        if( !blFound)
          m_vecFipConns.add( m_fipc);
      }
    }
  }

  public boolean isCanceled()
  {
    return m_blCanceled;
  }

  public void hideFipPanel()
  {
    m_jPanelFip.setVisible( false);
  }

  public void showFipPanel()
  {
    m_jPanelFip.setVisible( true);
  }

  public void hideForFileEx()
  {
    m_jPanelCoCmd.hideForFileEx();
  }

  public void showAfterFileEx()
  {
    m_jPanelCoCmd.showAfterFileEx();
  }

  public void setCommandVector( List<CommandClass> cmd)
  {
    m_jPanelCoCmd.setCommandVector( cmd);
  }

  public List<CommandClass> getCommand()
  {
    return m_jPanelCoCmd.getCommand();
  }

  public void setVisible( boolean b)
  {
    if( b)
    {
      m_jPanelFip._init();
    }
    super.setVisible( b);
    m_jPanelCoCmd.requestFocus();
    m_jSplitPaneCenter.setDividerLocation( 0.5);
  }

  protected void m_jButtonOk_actionPerformed(ActionEvent e)
  {
    m_jPanelFip._save();
    m_jPanelCoCmd.m_jButtonSet_actionPerformed( e);
    m_blCanceled = false;
    setVisible( false);
  }

  protected void onNew()
  {
    String strFileName = null;
    TextInputDialog dlg = new TextInputDialog( this, "New", true, FileManager.APPNAME);
    dlg.setMessageString( "Name of the new connection?");
    dlg.setVisible( true);
    if( !dlg.isCanceled())
    {
      strFileName = dlg.getInputString();
      if( strFileName != null && !strFileName.endsWith( FileInfoPanel.FILEEXT))
      {
        strFileName += FileInfoPanel.FILEEXT;
      }
      if( strFileName != null)
      {
        m_appProps = new AppProperties();
        m_fipc = new FipConnection( strFileName, FileManager.APPNAME, m_appProps);
        m_fipc._init();
        m_vecFileCmd = m_fipc.getFileExtCommands();
        m_vecFipConns.add( m_fipc);
        File fi = new File( strFileName);
        try
        {
          fi.createNewFile();
        }
        catch( IOException ioex)
        {
          ioex.printStackTrace();
        }
        m_dlm.addElement( strFileName);
        m_jListCmd.setSelectedValue( strFileName, true);
        updateData( strFileName);
        _save();
      }
    }
  }

  protected void onRename()
  {
    String strFileName = (String)m_jListCmd.getSelectedValue();
    String strOldName = strFileName;
    TextInputDialog dlg = new TextInputDialog( this, "Rename", true, FileManager.APPNAME);
    dlg.setMessageString( "Rename connection?");
    dlg.setInputString( strFileName);
    dlg.setVisible( true);
    if( !dlg.isCanceled())
    {
      strFileName = dlg.getInputString();
      if( strFileName != null && !strFileName.endsWith( FileInfoPanel.FILEEXT))
      {
        strFileName += FileInfoPanel.FILEEXT;
      }
      if( strFileName != null)
      {
        //boolean blFound = false;
        for( int i = 0; i < m_vecFipConns.size(); ++i)
        {
          FipConnection fipcTmp = m_vecFipConns.get( i);
          if( fipcTmp.getName().equals( strOldName))
          {
            int iIdx = i;
            for( i = 0; i < m_vecFipConns.size(); ++i)
            {
              fipcTmp = m_vecFipConns.get( i );
              if( fipcTmp.getName().equals( strFileName ) )
              {
                int iRet;
                iRet = JOptionPane.showConfirmDialog( this, "Overwrite connection configuration?",
                  "Confirmation", JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE );
                if( iRet == JOptionPane.YES_OPTION )
                {
                  File fi = new File( strFileName);
                  fi.delete();
                  m_dlm.removeElement( strFileName);
                }
                else
                {
                  return;
                }
                //blFound = true;
                break;
              }
            }
            m_fipc.m_strConnName = strFileName;
            m_fipc._init();
            m_vecFipConns.set(iIdx, m_fipc);
            break;
          }
        }
        File fiOld = new File( strOldName);
        File fiNew = new File( strFileName);
        fiOld.renameTo( fiNew);
        int iIdx = m_dlm.indexOf( strOldName);
        if( iIdx != -1)
          m_dlm.setElementAt( strFileName, iIdx);
        m_jListCmd.setSelectedValue( strFileName, true);
        updateData( strFileName);
      }
    }
  }

  protected void onCopy()
  {
    String strFileName = (String)m_jListCmd.getSelectedValue();
    String strOldName = strFileName;
    TextInputDialog dlg = new TextInputDialog( this, "Copy", true, FileManager.APPNAME);
    dlg.setMessageString( "Name of the connection copy?");
    dlg.setInputString( "Copy of " + strFileName);
    dlg.setVisible( true);
    if( !dlg.isCanceled())
    {
      strFileName = dlg.getInputString();
      if( strFileName != null && !strFileName.endsWith( FileInfoPanel.FILEEXT))
      {
        strFileName += FileInfoPanel.FILEEXT;
      }
      if( strFileName != null && !strFileName.equalsIgnoreCase( strOldName))
      {
        FipConnection fip = m_fipc;
        AppProperties appProps = m_appProps;
        m_appProps = (AppProperties)m_appProps.clone();
        m_fipc = new FipConnection( strFileName, FileManager.APPNAME, m_appProps);
        m_fipc._init();
        boolean blFound = false;
        for( int i = 0; i < m_vecFipConns.size(); ++i)
        {
          FipConnection fipcTmp = m_vecFipConns.get( i);
          if( fipcTmp.getName().equals( strFileName))
          {
            int iRet;
            iRet = JOptionPane.showConfirmDialog( this, "Overwrite connection configuration?", "Confirmation", JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE);
            if( iRet == JOptionPane.YES_OPTION)
            {
              m_vecFipConns.set(i, m_fipc);
            }
            else
            {
              m_fipc = fip;
              m_appProps = appProps;
              return;
            }
            blFound = true;
            break;
          }
        }
        if( !blFound)
          m_vecFipConns.add( m_fipc);
        File fiNew = new File( strFileName);
        try
        {
          fiNew.createNewFile();
        }
        catch( IOException ioex)
        {
          ioex.printStackTrace();
        }
        if( !blFound)
          m_dlm.addElement( strFileName);
        m_jListCmd.setSelectedValue( strFileName, true);
        updateData( strFileName);
        _save();
      }
    }
  }

  protected void onDelete()
  {
    int iRet;
    iRet = JOptionPane.showConfirmDialog( this, "Delete this connection configuration?", "Confirmation", JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE);
    if( iRet == JOptionPane.YES_OPTION)
    {
      String strFileName = (String)m_jListCmd.getSelectedValue();
      if( strFileName != null)
      {
        int iIdx = m_jListCmd.getSelectedIndex();
        m_dlm.removeElement( strFileName);
        if( iIdx > 0 && iIdx-1 < m_dlm.size())
        {
          m_jListCmd.setSelectedIndex( --iIdx);
        }
        else if( m_dlm.size() > 0)
          m_jListCmd.setSelectedIndex( 0);

        File file = new File( strFileName);
        file.delete();
        for( int i = 0; i < m_vecFipConns.size(); ++i)
        {
          FipConnection fip = m_vecFipConns.get( i );
          if( fip.getName().equalsIgnoreCase( strFileName))
          {
            m_vecFipConns.remove( i);
          }
        }
        strFileName = (String)m_jListCmd.getSelectedValue();
        if( strFileName != null)
        {
          for( int i = 0; i < m_vecFipConns.size(); ++i)
          {
            FipConnection fip = m_vecFipConns.get( i );
            if( fip.getName().equalsIgnoreCase( strFileName))
            {
              m_appProps = fip.getAppProperties();
              m_vecFileCmd = fip.getFileExtCommands();
            }
          }
        }
        updateData( strFileName);
      }
    }
  }
}

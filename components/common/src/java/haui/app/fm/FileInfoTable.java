package haui.app.fm;

import haui.components.ConnectionManager;
import haui.io.FileConnector;
import haui.io.FileInterface.CgiTypeFile;
import haui.io.FileInterface.ClientTypeFile;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;

/**
 * Module:					FileInfoTable.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoTable.java,v $ <p> Description:    FileInfo table.<br> </p><p> Created:				25.10.2000	by	AE </p><p>
 * @history  				25.10.2000	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: FileInfoTable.java,v $  Revision 1.3  2004-08-31 16:03:01+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.2  2004-06-22 14:08:55+02  t026843  bigger changes  Revision 1.1  2003-05-28 14:19:53+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:25:47+02  t026843  Initial revision  Revision 1.8  2002-09-18 11:16:21+02  t026843  - changes to fit extended filemanager.pl  - logon and logoff moved to 'TypeFile's  - startTerminal() added to 'TypeFile's, but only CgiTypeFile (until now) starts the LRShell as terminal  - LRShell changed to work with filemanager.pl  Revision 1.7  2002-08-28 14:22:43+02  t026843  - filmanager.pl upload added.  - first preparations for the CgiTypeFile, which will repleace the FileInfo, cgi part.  Revision 1.6  2002-08-07 15:25:24+02  t026843  Ftp support via filetype added.  Some bugfixes.  Revision 1.5  2002-07-03 14:14:45+02  t026843  File context menu added  Revision 1.4  2002-06-27 15:51:42+02  t026843  sorting expanded and added as 'allways aktivated'  Revision 1.3  2002-06-26 12:06:47+02  t026843  History extended, simple bookmark system added.  Revision 1.2  2002-06-17 17:19:19+02  t026843  Zip and Jar filetype read only support.  Revision 1.1  2002-05-29 11:18:19+02  t026843  Added:  - starter menu  - config starter dialog  - file extensions configurable for right, middle and left mouse button  Changed:  - icons minimized  - changed layout of file ex. and button cmd config dialog  - output area hideable  - other minor changes  bugfixes:  - some minor bugfixes  Revision 1.0  2001-07-20 16:34:23+02  t026843  Initial revision  </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 2000; $Revision: 1.3 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\app\\fm\\FileInfoTable.java,v 1.3 2004-08-31 16:03:01+02 t026843 Exp t026843 $  </p><p>
 * @since  					JDK1.2  </p>
 */
public class FileInfoTable
  extends JTable
{
  private static final long serialVersionUID = 316987383784032227L;
  
  FileInfoTableModel m_fitm;
  FileInterface m_fi;
  FileInfoIconCellRenderer m_fiicr = new FileInfoIconCellRenderer();
  FileInfoNameCellEditor m_fince = new FileInfoNameCellEditor();
  FileInfoNumberFormatCellRenderer m_finfcr = new FileInfoNumberFormatCellRenderer();
  JPopupMenu m_popup;
  JPopupMenu m_headerPopup;
  String m_column;
  FinfoHistory m_fHist = new FinfoHistory();
  AppProperties m_appProps;
  HashMap m_hmRootHist = new HashMap();
  FileInterfaceFilter m_fifOld = null;
  FileInterfaceFilter m_fifCurrent = null;

  public FileInfoTable( AppProperties appProps)
  {
    m_appProps = appProps;
    m_fitm = new FileInfoTableModel();
    m_fitm.setFileInfoTable( this);
    setModel( m_fitm);
    setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN);
    AdpComponent comadapter = new AdpComponent();
    addComponentListener( comadapter);

    AdpKey keyadp = new AdpKey();
    addKeyListener( keyadp);

    MouseHandler mouseHandler = new MouseHandler();
    addMouseListener( mouseHandler);
  }

  public FileInterface getUpFileInterface( String strPath, String strParentPath, Object connObj)
  {
    FileInterface fiRet = null;
    try
    {
      if( m_fi instanceof ClientTypeFile )
      {
        Class ca = m_fi.getClass();
        String strP = "..";
        String strPP = strParentPath;
        if( strPP == null)
        {
          strPP = strPath;
          int iIdx = -1;
          if( strPP.length() > 2 )
          {
            for( int i = strPP.length() - 3; i >= 0; i-- )
            {
              char c = strPP.charAt( i );
              if( c == m_fi.separatorChar() )
              {
                iIdx = i + 1;
                break;
              }
            }
          }
          if( iIdx == -1 )
            strPP = null;
          else
            strPP = strPP.substring( 0, iIdx );
        }
        Character c = new Character( m_fi.separatorChar() );
        Object objConn = connObj;
        String strAppName = FileManager.APPNAME;
        AppProperties appProps = m_fi.getAppProperties();
        Boolean blCache = new Boolean( true );
        Boolean blUpDummy = new Boolean( true );
        Class[] caParams = {strP.getClass(), strPP.getClass(), c.getClass(), objConn.getClass(),
                           strAppName.getClass(), appProps.getClass(), blCache.getClass(),
                           blUpDummy.getClass()};
        Object[] objArr = {strP, strPP, c, objConn, strAppName, appProps, blCache, blUpDummy};
        Constructor con = ca.getConstructor( caParams );
        ClientTypeFile ctf = ( ClientTypeFile )con.newInstance( objArr );
        fiRet = ctf;
      }
      else
      {
        FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
            connObj, 0, 0, FileManager.APPNAME, m_appProps, true);
        fiRet = FileConnector.createFileInterface( "..", strParentPath, true, fic);
      }
    }
    catch( Exception ex )
    {
      ex.printStackTrace();
    }
    return fiRet;
  }

  public void init( String strPath, String strParentPath, Object connObj, boolean blUpdate)
  {
    try
    {
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
          connObj, 0, 0, FileManager.APPNAME, m_appProps, true);
      m_fi = FileConnector.createFileInterface( strPath, strParentPath, true, fic);
      //fi.setFileInterfaceFilter( getFileInterfaceFilter());
      m_fitm.removeFileInterfaceVectorElements();
      FileInterface fiRoot;
      fiRoot = getFileInterface().getRootFileInterface();
      if( !getAbsolutePath().equals( fiRoot.getAbsolutePath() ) )
      {
        if( strParentPath == null )
        {
          strParentPath = getParentPath();
        }
        FileInterface fiUp = getUpFileInterface( strPath, strParentPath, connObj );
        m_fitm.addFileInterfaceToVector( fiUp );
      }
      m_fitm.addFileInterfaceVector( m_fi._listFiles( getFileInterfaceFilter() ) );
      getColumnModel().getColumn( 0 ).setCellRenderer( m_fiicr );
      getColumnModel().getColumn( 1 ).setCellEditor( m_fince );
      getColumnModel().getColumn( 3 ).setCellRenderer( m_finfcr );
      setToolTipText( "FIView" );
      m_fitm.sort( -1 );
      onResize();
      TableModelEvent tme = new TableModelEvent( m_fitm, 0, 0, 0, TableModelEvent.INSERT );
      if( blUpdate )
        m_fHist.update( getFileInterface() );
      else
        m_fHist.add( getFileInterface() );
      m_fitm.fireTableChanged( tme );
      clearSelection();
      if( getRowCount() > 0 )
        setRowSelectionInterval( 0, 0 );
      m_hmRootHist.put( getFileInterface().getRootFileInterface().getAbsolutePath(),
                        getAbsolutePath() );
    }
    catch( Exception ex)
    {
      ex.printStackTrace( );
    }
  }

  public void init( FileInterface fi)
  {
    m_fi = fi;
    m_fitm.removeFileInterfaceVectorElements();
    if( !getAbsolutePath().equals( getFileInterface().getRootFileInterface().getAbsolutePath()))
    {
      try
      {
        FileInterface fiUp = getUpFileInterface( m_fi.getAbsolutePath(), m_fi.getParent(), m_fi.getConnObj());
        m_fitm.addFileInterfaceToVector(  fiUp);
      }
      catch( Exception ex )
      {
        ex.printStackTrace();
      }
    }
    m_fitm.addFileInterfaceVector( m_fi._listFiles( getFileInterfaceFilter()));
    getColumnModel().getColumn( 0).setCellRenderer( m_fiicr);
    getColumnModel().getColumn( 1).setCellEditor( m_fince);
    getColumnModel().getColumn( 3).setCellRenderer( m_finfcr);
    setToolTipText( "FIView");
    m_fitm.sort(-1);
    onResize();
    TableModelEvent tme = new TableModelEvent( m_fitm, 0, 0, 0, TableModelEvent.INSERT);
    m_fitm.fireTableChanged( tme);
    clearSelection();
    if( getRowCount() > 0)
      setRowSelectionInterval( 0, 0);
    m_hmRootHist.put( getFileInterface().getRootFileInterface().getAbsolutePath(),
                     getAbsolutePath());
  }

  public void init( FileInterface[] fi)
  {
    if( fi == null || fi.length == 0)
      return;
    //FileInterfaceFilter fif = getFileInterfaceFilter();
    m_fi = fi[0].getParentFileInterface();
      //new FileInterface( ".", null, fi[0].isLocal(), fi[0].getConnectionObject(), FileManager.APPNAME, fi[0].getAppProperties());
    //m_fi.setFileInterfaceFilter( fif);
    m_fitm.setFileInterfaceVector( fi);
    getColumnModel().getColumn( 0).setCellRenderer( m_fiicr);
    getColumnModel().getColumn( 1).setCellEditor( m_fince);
    getColumnModel().getColumn( 3).setCellRenderer( m_finfcr);
    setToolTipText( "FIView");
    m_fitm.sort(-1);
    onResize();
    TableModelEvent tme = new TableModelEvent( m_fitm, 0, 0, 0, TableModelEvent.INSERT);
    m_fitm.fireTableChanged( tme);
    clearSelection();
    if( getRowCount() > 0)
      setRowSelectionInterval( 0, 0);
    m_hmRootHist.put( getFileInterface().getRootFileInterface().getAbsolutePath(),
                     getAbsolutePath());
  }

  public void putRootHist( String strRoot, String strCurrentPath)
  {
    m_hmRootHist.put( strRoot, strCurrentPath);
  }

  public void clearRootHist()
  {
    m_hmRootHist.clear();
  }

  public String getRootHist( String strRoot)
  {
    return (String)m_hmRootHist.get( strRoot);
  }

  public void setAppProperties( AppProperties appProps)
  {
    m_appProps = appProps;
  }

  public int findFileInterfaceIndex( String strAbsolutePath)
  {
    int iRet = -1;
    FileInterface[] fis = m_fitm.getFileInterfaceArray();
    if( fis == null)
      return iRet;
    int iLen = fis.length;
    for( int i = 0; i < iLen; ++i)
    {
      if( strAbsolutePath.equals( fis[i].getAbsolutePath()))
      {
        iRet = i;
        break;
      }
    }
    return iRet;
  }

  public FinfoHistory getFinfoHistory()
  {
    return m_fHist;
  }

  /**
    * Get the name of a column index.
    *
    * @param iIdx:	index of the column
    *
    * @return Name of the found column or null if nothing was found.
    */
  public String getColumnName(int iIdx)
  {
    return m_fitm.getColumnName( iIdx);
  }

  public void setFileInterface( FileInterface fi)
  {
    m_fi = fi;
  }

  public FileInterfaceFilter getFileInterfaceFilter()
  {
    return m_fifCurrent;
  }

  public void setFileInterfaceFilter( FileInterfaceFilter fif)
  {
    m_fifOld = m_fifCurrent;
    m_fifCurrent = fif;
  }

  public FileInfoNumberFormatCellRenderer getFINFCR()
  {
    return m_finfcr;
  }

  public FileInfoNameCellEditor getFINCE()
  {
    return m_fince;
  }

  public FileInfoIconCellRenderer getFIICR()
  {
    return m_fiicr;
  }

  public void setVisible( boolean b)
  {
    super.setVisible( b);
    onResize();
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      //Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 2)
        rightMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON3_MASK && event.getClickCount() == 1)
        rightMousePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON2_MASK && event.getClickCount() == 2)
        middleMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON2_MASK && event.getClickCount() == 1)
        middleMousePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 2)
        leftMouseDoublePressed( event);
      else if( event.getModifiers() == InputEvent.BUTTON1_MASK && event.getClickCount() == 1)
        leftMousePressed( event);
    }

    public void mouseReleased(MouseEvent event)
    {
      //Object object = event.getSource();
      if( event.getModifiers() == InputEvent.BUTTON1_MASK)
        leftMouseReleased( event);
      else if( (event.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && ((event.getModifiers() & InputEvent.SHIFT_MASK) != 0
              || (event.getModifiers() & InputEvent.CTRL_MASK) != 0))
        leftMouseReleased( event);
    }
  }

  void leftMousePressed( MouseEvent event)
  {
  }

  void leftMouseDoublePressed( MouseEvent event)
  {
    FileInterface fi = getSelectedFileInterface();

    if( fi.isDirectory() || fi.isArchive())
    {
      init( fi.getAbsolutePath(), fi.getParent(), fi.getConnObj(), false);
      //repaint();
    }
  }

  void middleMousePressed( MouseEvent event)
  {
    setSelectionAtMousePos( event.getX(), event.getY());
  }

  void middleMouseDoublePressed( MouseEvent event)
  {
    setSelectionAtMousePos( event.getX(), event.getY());
  }

  void rightMousePressed( MouseEvent event)
  {
    if( getSelectedRowCount() <= 1)
      setSelectionAtMousePos( event.getX(), event.getY());
    showPopup( event.getX()+1, event.getY()+1);
  }

  void rightMouseDoublePressed( MouseEvent event)
  {
    setSelectionAtMousePos( event.getX(), event.getY());
  }

  void leftMouseReleased( MouseEvent event)
  {
  }

  protected void setSelectionAtMousePos( int x, int y)
  {
    int idx = this.rowAtPoint( new Point( x, y));
    if( idx != -1 && idx < getRowCount())
    {
      setRowSelectionInterval( idx, idx);
    }
  }

  class AdpKey
    extends KeyAdapter
  {
    public void keyPressed( KeyEvent e)
    {
      switch( e.getKeyCode())
      {
        case KeyEvent.VK_ENTER:
          leftMouseDoublePressed( null);
          e.consume();
          break;

        case KeyEvent.VK_HOME:
          if( m_fitm != null && m_fitm.getFileInterfaceArray() != null)
          {
            int iLen = m_fitm.getFileInterfaceArray().length;
            if( iLen > 0)
              setRowSelectionInterval( 0, 0);
          }
          e.consume();
          break;

        case KeyEvent.VK_END:
          if( m_fitm != null && m_fitm.getFileInterfaceArray() != null)
          {
            int iLen = m_fitm.getFileInterfaceArray().length;
            if( iLen > 0)
              setRowSelectionInterval( iLen-1, iLen-1);
          }
          e.consume();
          break;

        case KeyEvent.VK_U:
          if( e.getModifiers() == KeyEvent.ALT_MASK)
          {
            parent();
            e.consume();
          }
          break;

        case KeyEvent.VK_R:
          if( e.getModifiers() == KeyEvent.ALT_MASK)
          {
            root();
            e.consume();
          }
          break;

        case KeyEvent.VK_LEFT:
          if( e.getModifiers() == KeyEvent.ALT_MASK)
          {
            back();
            e.consume();
          }
          break;

        case KeyEvent.VK_RIGHT:
          if( e.getModifiers() == KeyEvent.ALT_MASK)
          {
            next();
            e.consume();
          }
          break;

        default:
          super.keyPressed( e);
          break;
      }
    }
  }

  /**
   * root button handler
   */
  public void root()
  {
  }

  /**
   * back button handler
   */
  public void back()
  {
    String strPath = getAbsolutePath();
    FileInterface fi = m_fHist.previous();
    if( fi == null)
      return;
    init( fi);
    //repaint();
    int idx = findFileInterfaceIndex( strPath);
    if( idx != -1)
    {
      clearSelection();
      setRowSelectionInterval( idx, idx);
    }
  }

  /**
   * next button handler
   */
  public void next()
  {
    FileInterface fi = m_fHist.next();
    if( fi == null)
      return;
    init( fi);
    //repaint();
  }

  /**
   * parent button handler
   */
  public void parent()
  {
    if( getParentPath() != null)
    {
      String strPath = getAbsolutePath();
      init( getParentPath(), null, getConnectionObject(), false);
      //repaint();
      int idx = findFileInterfaceIndex( strPath);
      if( idx != -1)
      {
        clearSelection();
        setRowSelectionInterval( idx, idx);
      }
    }
  }

  class AdpComponent extends ComponentAdapter
  {
    public void componentResized( ComponentEvent event)
    {
      Object object = event.getSource();
      if (object == FileInfoTable.this)
      {
        onResize();
      }
    }
  }

  public void onResize()
  {
    FileInfoTable fiTab = this;
    int iWidth = fiTab.getWidth();

    if( iWidth == 0)
      iWidth = (int)fiTab.getPreferredSize().getWidth();

    getColumnModel().getColumn( 0).setPreferredWidth( (int)(iWidth*0.05));
    getColumnModel().getColumn( 1).setPreferredWidth( (int)(iWidth*0.475));
    getColumnModel().getColumn( 2).setPreferredWidth( (int)(iWidth*0.1));
    getColumnModel().getColumn( 3).setPreferredWidth( (int)(iWidth*0.1));
    getColumnModel().getColumn( 4).setPreferredWidth( (int)(iWidth*0.2));
    getColumnModel().getColumn( 5).setPreferredWidth( (int)(iWidth*0.075));
  }

  public void sort( Point p)
  {
    FileInterface[] fis = getSelectedFileInterfaces();
    m_fitm.sort( columnAtPoint( p));
    if( fis != null && fis.length > 0)
    {
      clearSelection();
      for( int i = 0; i < fis.length; ++i)
      {
        FileInterface fi = fis[i];
        int iIdx = findFileInterfaceIndex( fi.getAbsolutePath());
        if( iIdx != -1)
          addRowSelectionInterval( iIdx, iIdx);
      }
    }
  }

  public FileInterface getFileInterfaceAt( int iIdx)
  {
    return m_fitm.getFileInterfaceAt( iIdx);
  }

  public FileInterface getFileInterface()
  {
    return m_fi;
  }

  public FileInterface getSelectedFileInterface()
  {
    if( getSelectedRow() < 0)
      return null;
    return getFileInterfaceAt( getSelectedRow());
  }

  public FileInterface[] getSelectedFileInterfaces()
  {
    int iIdx[] = getSelectedRows();
    FileInterface fi[] = new FileInterface[ iIdx.length];

    for( int i = 0; i < iIdx.length; i++)
      fi[i] = getFileInterfaceAt( iIdx[i]);

    return fi;
  }

  /**
   * make directory
   *
   * @return true if directory is successfully created; false otherwise
   */
  public boolean mkdir()
  {
    boolean blRet = true;
    FileInterface fi = null;
    String str = JOptionPane.showInputDialog( this, "Name of new folder?", "New folder", JOptionPane.QUESTION_MESSAGE);
    if( str != null && !str.equals( ""))
    {
      //boolean blFound = false;
      String strNewPath = getAbsolutePath();
      // check if the last char in path is the seperator
      if( strNewPath.charAt( strNewPath.length()-1) != getFileInterface().separatorChar())
        strNewPath += getFileInterface().separatorChar();
      strNewPath += str;
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null,
          m_fi.getConnObj(), 0, 0, FileManager.APPNAME, m_fi.getAppProperties(), true);
      fi = FileConnector.createFileInterface( strNewPath, null, false, fic);
      blRet = fi.mkdir();
      if( fi != null && blRet)
        m_fitm.addRow( fi);
    }
    return blRet;
  }

  /**
   * delete file
   *
   * @return true if all the files or directories are successfully deleted; false otherwise
   */
  public boolean delete( boolean blForce)
  {
    boolean blRet = true;
    int iIdx[] = getSelectedRows();

    if( iIdx != null)
    {
      for( int i = iIdx.length-1; i >= 0; --i)
      {
        if( getFileInterfaceAt( iIdx[i]).isDirectory() && !blForce)
        {
          int iRet = JOptionPane.NO_OPTION;
          if( iIdx.length > 1)
          {
            String strName = "Delete ALL directorys and all it's contents?";
            iRet = JOptionPane.showConfirmDialog( this.getParent(), strName, "Delete dir",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
            if( iRet == JOptionPane.YES_OPTION)
              blForce = true;
          }
          else
          {
            String strName = "Delete directory " + getFileInterfaceAt( iIdx[i] ).getName()
                             + " and all it's contents?";
            iRet = JOptionPane.showConfirmDialog( this.getParent(), strName, "Delete dir",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
          }
          if( iRet == JOptionPane.NO_OPTION)
            continue;
        }
        if( !m_fitm.delete( iIdx[i]) && blRet)
          blRet = false;
      }
    }
    else
      blRet = false;
    return blRet;
  }

  /**
   * get path
   *
   * @return path
   */
  public String getPath()
  {
    return m_fi.getPath();
  }

  /**
   * get absolute path
   *
   * @return absolute path
   */
  public String getAbsolutePath()
  {
    return m_fi.getAbsolutePath();
  }

  /**
   * get parent path
   *
   * @return parent path
   */
  public String getParentPath()
  {
    return m_fi.getParent();
  }

  public ConnectionManager getConnectionManager()
  {
    ConnectionManager cm = null;
    if( m_fi instanceof CgiTypeFile)
      cm = (ConnectionManager)((CgiTypeFile)m_fi).getConnObj();
    return cm;
  }

  public Object getConnectionObject()
  {
    return m_fi.getConnObj();
  }

  public String getToolTipText(MouseEvent event)
  {
    int row = rowAtPoint( event.getPoint());
    int column = columnAtPoint( event.getPoint());
    String strTTText = null;

    if( row != -1)
    {
      switch( column)
      {
        case 1:
        case 2:
          strTTText = m_fitm.getFileInterfaceAt( row).getName();
          break;

        case 3:
        case 4:
        case 5:
          strTTText = getValueAt( row, column).toString();
          break;
      }
    }
    return strTTText;
  }

  public boolean isCellEditable(int row, int column)
  {
    if( getSelectedRow() == row)
      return m_fitm.isCellEditable( row, column);
    else
      return false;
  }

  public String getName( int row)
  {
    return m_fitm.getName( row);
  }

  public Object getValueAt( int row, int column)
  {
    Object objRet = null;

    if( m_fitm != null)
    {
      objRet = m_fitm.getValueAt( row, column);
    }
    return objRet;
  }

  public void setValueAt( Object aValue, int row, int column)
  {
    if( m_fitm != null)
    {
      m_fitm.setValueAt( aValue, row, column);
    }
  }

  protected JTableHeader createDefaultTableHeader()
  {
    return new FileInfoTableHeader(columnModel);
  }

  public void setPopup( JPopupMenu popup)
  {
    m_popup = popup;
  }

  public JPopupMenu getPopup()
  {
    return m_popup;
  }

  public void setHeaderPopup( JPopupMenu popup)
  {
    m_headerPopup = popup;
  }

  public JPopupMenu getHeaderPopup()
  {
    return m_headerPopup;
  }

  public void showHeaderPopup( int x, int y)
  {
    if( m_headerPopup != null)
    {
      m_column = null;
      m_headerPopup.show( getTableHeader(), x, y);
      m_column = getColumnName( getTableHeader().columnAtPoint(new Point( x, y)));
      m_headerPopup.setVisible( true);
    }
  }

  public void showPopup( int x, int y)
  {
    if( m_popup != null)
    {
      m_popup.show( getComponentAt( x, y), x, y);
      if( getSelectedRowCount() == 1)
      {
        for( int i = 0; i < m_popup.getComponentCount(); ++i)
        {
          Component co = m_popup.getComponent( i);
          if( co instanceof JMenuItem)
          {
            String str = ((JMenuItem)co).getActionCommand();
            if( str != null && str.equalsIgnoreCase( "split"))
            {
              FileInterface fi = getSelectedFileInterface();
              if( fi.isFile() || fi.isArchive())
                ((JMenuItem)co).setEnabled( true );
              else
                ((JMenuItem)co).setEnabled( false );
            }
            if( str != null && str.equalsIgnoreCase( "concat"))
              ((JMenuItem)co).setEnabled( false);
          }
        }
      }
      else if( getSelectedRowCount() > 1)
      {
        for( int i = 0; i < m_popup.getComponentCount(); ++i)
        {
          Component co = m_popup.getComponent( i);
          if( co instanceof JMenuItem)
          {
            String str = ((JMenuItem)co).getActionCommand();
            if( str != null && str.equalsIgnoreCase( "split"))
              ((JMenuItem)co).setEnabled( false);
            if( str != null && str.equalsIgnoreCase( "concat"))
            {
              FileInterface fis[] = getSelectedFileInterfaces();
              boolean blFiles = true;
              for( int ii = 0; ii < fis.length; ++ii)
              {
                FileInterface fi = fis[ii];
                if( fi.isDirectory())
                {
                  blFiles = false;
                  break;
                }
              }
              if( blFiles)
                ((JMenuItem)co).setEnabled( true );
              else
                ((JMenuItem)co).setEnabled( false );
            }
          }
        }
      }
      m_popup.setVisible( true);
    }
  }

  /**
    * Get the name of the column selected by an tableheader popup.
    *
    * @return Name of column or null if nothing was selected.
    */
  public String getSelColumnName()
  {
    return m_column;
  }

  // main for testing purposes
  public static void main(String argv[])
  {
    FileInfoTable demo = new FileInfoTable(null);
    demo.init( ".", null, null, false);
    JScrollPane scrollpane = new JScrollPane(demo);
    JFrame f = new JFrame("FileInfoTable");
    f.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {System.exit(0);}
    });
    f.getContentPane().add("Center", scrollpane);
    f.pack();
    //f.setSize(new Dimension(640,480));
    f.setVisible( true);
  }
}

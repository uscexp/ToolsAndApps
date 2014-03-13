package haui.app.filesync;

import haui.app.fm.CopyFileDialog;
import haui.app.fm.FileInfoIconCellRenderer;
import haui.app.fm.FileInfoNumberFormatCellRenderer;
import haui.dbtool.JExTable;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.io.FileInterface.filter.FileInterfaceFilter;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.Cursor;
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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Module:      FileSyncTable.java<br> $Source: $ <p> Description: FileSyncTable.<br> </p><p> Created:     30.09.2004  by AE </p><p>
 * @history      30.09.2004  by AE: Created.<br>  </p><p>  Modification:<br>  $Log: $  </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @version      v1.0, 2004; $Revision: $<br>  $Header: $  </p><p>
 * @since        JDK1.3  </p>
 */
public class FileSyncTable
  extends JExTable
{
  private static final long serialVersionUID = 2292387007557559881L;
  
  private Thread m_th = null;
  private boolean m_blLocked = false;
  private boolean m_blRecursiveInit = false;
  private boolean m_blOverwriteAll = false;
  private Vector m_vecDataContainer = new Vector();
  private LinkedList m_listAddedPaths = new LinkedList();
  private FileSyncTableModel m_fstm;
  private FileInfoNumberFormatCellRenderer m_finfcr = new FileInfoNumberFormatCellRenderer();
  private FileInfoIconCellRenderer m_fiicr = new FileInfoIconCellRenderer();
  private JPopupMenu m_popup;
  private JPopupMenu m_headerPopup;
  private String m_column;
  private AppProperties m_appProps;
  private String m_strAppName;
  private HashMap m_hmRootHist = new HashMap();
  private FileInterfaceFilter m_fifOld = null;
  private FileInterfaceFilter m_fifCurrent = null;

  public FileSyncTable()
  {
    this( null, null);
  }

  public FileSyncTable( String strAppName, AppProperties appProps)
  {
    m_appProps = appProps;
    m_strAppName = strAppName;
    m_fstm = new FileSyncTableModel( m_strAppName);
    m_fstm.setFileSyncTable( this);
    setModel( m_fstm);
    setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN);
    getColumnModel().getColumn( 1).setCellRenderer( m_finfcr);
    getColumnModel().getColumn( 3).setCellRenderer( m_fiicr);
    getColumnModel().getColumn( 5).setCellRenderer( m_finfcr);
    AdpComponent comadapter = new AdpComponent();
    addComponentListener( comadapter);

    AdpKey keyadp = new AdpKey();
    addKeyListener( keyadp);

    MouseHandler mouseHandler = new MouseHandler();
    addMouseListener( mouseHandler);
  }

  public void add( FsDataContainer dc)
  {
    if( (dc.fiLeft != null && !m_listAddedPaths.contains( dc.fiLeft.getAbsolutePath()))
        || (dc.fiRight != null && !m_listAddedPaths.contains( dc.fiRight.getAbsolutePath())))
    {
      m_vecDataContainer.add( dc );
      if(dc.fiLeft != null && !m_listAddedPaths.contains( dc.fiLeft.getAbsolutePath()))
        m_listAddedPaths.add( dc.fiLeft.getAbsolutePath());
      if(dc.fiRight != null && !m_listAddedPaths.contains( dc.fiRight.getAbsolutePath()))
        m_listAddedPaths.add( dc.fiRight.getAbsolutePath());
    }
  }

  public void setAppProperties( AppProperties appProps)
  {
    m_appProps = appProps;
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

  public void setVisible( boolean b)
  {
    super.setVisible( b);
    onResize();
  }

  public void clear()
  {
    m_listAddedPaths.clear();
    m_vecDataContainer.removeAllElements();
    m_blRecursiveInit = false;
  }

  public boolean isLocked()
  {
    return m_blLocked;
  }

  public void setLocked( boolean bl)
  {
    m_blLocked = bl;
  }

  public void compare( JProgressBar jProgressBar, FileInterface fiLeftDir, FileInterface fiRightDir,
                       FileInterfaceFilter fif,
                       boolean blSubDirs, boolean blIgnDate, boolean blContent, boolean blSelected,
                       boolean blToLeft, boolean blToRight, boolean blEqual, boolean blNotEqual,
                       boolean blDuplicates, boolean blSingles)
  {
    if( fiLeftDir == null || fiRightDir == null)
      return;

    FileInterface[] fiLeftFiles = fiLeftDir._listFiles( fif);
    FileInterface[] fiRightFiles = fiRightDir._listFiles( fif);
    String strPathLeft = fiLeftDir.getAbsolutePath();
    String strPathRight = fiRightDir.getAbsolutePath();
    int iLenLeft = strPathLeft.length();
    int iLenRight = strPathRight.length();
    if( !m_blRecursiveInit)
    {
      getRootPane().getParent().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
      long[] lFiCombi = FileConnector.lengthAndCountOfFiles( fiLeftFiles, fif, true);
      long[] lFiCombi1 = FileConnector.lengthAndCountOfFiles( fiRightFiles, fif, true);
      jProgressBar.setMinimum( 0 );
      jProgressBar.setMaximum( ( int )lFiCombi[1] + (int)lFiCombi1[1] );
      jProgressBar.setValue( 0 );
      m_blRecursiveInit = true;
      getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
    }

    for( int i = 0; i < fiLeftFiles.length; ++i)
    {
      FileInterface fiLeft = fiLeftFiles[i];
      String strDiff = fiLeft.getAbsolutePath().substring( iLenLeft);
      String strPath = strPathRight + strDiff;
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, fiRightDir.getConnObj(), 0, 0,
          m_strAppName, fiRightDir.getAppProperties(), false);
      FileInterface fiRight = FileConnector.createFileInterface( strPath, null, false, fic);

      jProgressBar.setValue( jProgressBar.getValue()+1);

      if( fiLeft.isDirectory() && fiRight.exists())
      {
        if( blSubDirs)
          compare( jProgressBar, fiLeft, fiRight, fif, blSubDirs, blIgnDate, blContent, blSelected, blToLeft, blToRight,
                   blEqual, blNotEqual, blDuplicates, blSingles);
      }
      else if( fiLeft.isDirectory())
      {
        FsDataContainer dc = new FsDataContainer( fiLeft, fiRight, FileSyncTableModel.GE, m_strAppName);
        dc.blDuplicate = false;
        add( dc);
      }
      else if( fiLeft.isFile() && fiRight.exists())
      {
        String strCompare = FileSyncTableModel.EQ;

        if( blContent)
        {
          strCompare = compareByContent( fiLeft, fiRight);

          if( !blIgnDate && !strCompare.equals( FileSyncTableModel.EQ))
          {
            String str = compareByDate( fiLeft, fiRight);
            if( !str.equals( FileSyncTableModel.EQ))
              strCompare = str;
          }
        }
        else if( blIgnDate)
        {
          if( fiLeft.length() == fiRight.length())
          {
            strCompare =  FileSyncTableModel.EQ;
          }
          else
          {
            strCompare =  FileSyncTableModel.NOTEQ;
          }
        }
        else
        {
          strCompare = compareByDate( fiLeft, fiRight);
        }
        FsDataContainer dc = new FsDataContainer( fiLeft, fiRight, strCompare, m_strAppName);
        dc.blDuplicate = false;
        add( dc );
      }
      else if( fiLeft.isFile())
      {
        FsDataContainer dc = new FsDataContainer( fiLeft, fiRight, FileSyncTableModel.GE, m_strAppName);
        dc.blDuplicate = false;
        add( dc );
      }
    }

    for( int i = 0; i < fiRightFiles.length; ++i)
    {
      FileInterface fiRight = fiRightFiles[i];
      jProgressBar.setValue( jProgressBar.getValue()+1);
      if( m_listAddedPaths.contains( fiRight.getAbsolutePath()))
        continue;
      String strDiff = fiRight.getAbsolutePath().substring( iLenRight);
      String strPath = strPathLeft + strDiff;
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, fiLeftDir.getConnObj(), 0, 0,
          m_strAppName, fiLeftDir.getAppProperties(), false);
      FileInterface fiLeft = FileConnector.createFileInterface( strPath, null, false, fic);


      if( fiRight.isDirectory() && fiLeft.exists())
      {
        if( blSubDirs)
          compare( jProgressBar, fiLeft, fiRight, fif, blSubDirs, blIgnDate, blContent, blSelected, blToLeft, blToRight,
                   blEqual, blNotEqual, blDuplicates, blSingles);
      }
      else if( fiRight.isDirectory())
      {
        FsDataContainer dc = new FsDataContainer( fiLeft, fiRight, FileSyncTableModel.GE, m_strAppName);
        dc.blDuplicate = false;
        add( dc);
      }
      else if( fiRight.isFile() && fiLeft.exists())
      {
        String strCompare = FileSyncTableModel.EQ;

        if( blContent)
        {
          strCompare = compareByContent( fiLeft, fiRight);

          if( !blIgnDate && !strCompare.equals( FileSyncTableModel.EQ))
          {
            String str = compareByDate( fiLeft, fiRight);
            if( !str.equals( FileSyncTableModel.EQ))
              strCompare = str;
          }
        }
        else if( blIgnDate)
        {
          if( fiLeft.length() == fiRight.length())
          {
            strCompare =  FileSyncTableModel.EQ;
          }
          else
          {
            strCompare =  FileSyncTableModel.NOTEQ;
          }
        }
        else
        {
          strCompare = compareByDate( fiLeft, fiRight);
        }
        FsDataContainer dc = new FsDataContainer( fiLeft, fiRight, strCompare, m_strAppName);
        dc.blDuplicate = false;
        add( dc );
      }
      else if( fiRight.isFile())
      {
        FsDataContainer dc = new FsDataContainer( fiLeft, fiRight, FileSyncTableModel.LE, m_strAppName);
        dc.blDuplicate = false;
        add( dc );
      }
    }
    showResult( blToLeft, blToRight, blEqual, blNotEqual, blDuplicates, blSingles);
  }

  protected String compareByDate( FileInterface fiLeft, FileInterface fiRight)
  {
    String strCompare = FileSyncTableModel.EQ;
    long lLeft = fiLeft.lastModified();
    long lRight = fiRight.lastModified();
    lLeft = lLeft/1000;
    lRight = lRight/1000;

    if( (lLeft > lRight) && ((lLeft+1) > lRight) && ((lLeft-1) > lRight))
    {
      strCompare =  FileSyncTableModel.GE;
    }
    else if( (lLeft < lRight) && ((lLeft+1) < lRight) && ((lLeft-1) < lRight))
    {
      strCompare =  FileSyncTableModel.LE;
    }
    else if( (lLeft == lRight) || ((lLeft+1) == lRight) || ((lLeft-1) == lRight))
    {
      strCompare =  FileSyncTableModel.EQ;
    }
    else
    {
      strCompare =  FileSyncTableModel.NOTEQ;
    }
    return strCompare;
  }

  protected String compareByContent( FileInterface fiLeft, FileInterface fiRight)
  {
    String strCompare = FileSyncTableModel.EQ;

    if( fiLeft.length() != fiRight.length())
    {
      strCompare = FileSyncTableModel.NOTEQ;
    }
    else
    {
      try
      {
        BufferedInputStream bisLeft = fiLeft.getBufferedInputStream();
        BufferedInputStream bisRight = fiRight.getBufferedInputStream();
        int iRead = -1;
        byte[] bLeft = new byte[1];
        byte[] bRight = new byte[1];

        while( ( iRead = bisLeft.read( bLeft, 0, 1)) > 0)
        {
          if( ( iRead = bisRight.read( bRight, 0, 1)) > 0)
          {
            if( bLeft[0] != bRight[0])
            {
              strCompare = FileSyncTableModel.NOTEQ;
              break;
            }
          }
        }
        bisLeft.close();
        bisRight.close();
      }
      catch( FileNotFoundException ex)
      {
        ex.printStackTrace( GlobalApplicationContext.instance().getErrorPrintStream());
      }
      catch( IOException ex)
      {
        ex.printStackTrace( GlobalApplicationContext.instance().getErrorPrintStream());
      }
    }
    return strCompare;
  }

  public void showResult( boolean blToLeft, boolean blToRight, boolean blEqual, boolean blNotEqual,
                          boolean blDuplicates, boolean blSingles)
  {
    Vector vecRes = new Vector();

    getRootPane().getParent().setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
    for( int i = 0; i < m_vecDataContainer.size(); ++i)
    {
      FsDataContainer dc = (FsDataContainer)m_vecDataContainer.elementAt( i);

      if( blDuplicates && dc.blDuplicate)
      {
        if( blToLeft && dc.strCompare == FileSyncTableModel.LE )
          vecRes.add( dc );
        else if( blToRight && dc.strCompare == FileSyncTableModel.GE )
          vecRes.add( dc );
        else if( blEqual && dc.strCompare == FileSyncTableModel.EQ )
          vecRes.add( dc );
        else if( blNotEqual && dc.strCompare == FileSyncTableModel.NOTEQ )
          vecRes.add( dc );
      }
      else if( blSingles && !dc.blDuplicate)
      {
        if( blToLeft && dc.strCompare == FileSyncTableModel.LE )
          vecRes.add( dc );
        else if( blToRight && dc.strCompare == FileSyncTableModel.GE )
          vecRes.add( dc );
        else if( blEqual && dc.strCompare == FileSyncTableModel.EQ )
          vecRes.add( dc );
        else if( blNotEqual && dc.strCompare == FileSyncTableModel.NOTEQ )
          vecRes.add( dc );
      }
    }
    m_fstm.addDataContainerVector( vecRes);
    m_fstm.forceRedraw();
    getRootPane().getParent().setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
  }

  public void synchronize( JProgressBar jProgressBar, boolean blToLeft, boolean blToRight, boolean blEqual,
                           boolean blNotEqual, boolean blDuplicates, boolean blSingles)
  {
    final FsDataContainer[] dcs = m_fstm.getDataContainerArray();
    final boolean isToLeft = blToLeft;
    final boolean isToRight = blToRight;
    final boolean isEqual = blEqual;
    final boolean isNotEqual = blNotEqual;
    final boolean isDuplicates = blDuplicates;
    final boolean isSingles = blSingles;

    final JProgressBar pb = jProgressBar;
    if( dcs != null)
    {
      //long[] lFiCombi = FileConnector.lengthAndCountOfFiles( dcs);
      jProgressBar.setMinimum( 0 );
      jProgressBar.setMaximum( dcs.length);
      jProgressBar.setValue( 0 );
      m_blOverwriteAll = false;
      m_th = new Thread( "copythread" )
      {
        public void run()
        {
          for( int i = 0; i < dcs.length; ++i )
          {
            FsDataContainer dc = dcs[i];
            FileInterface fiSource = null;
            FileInterface fiTarget = null;

            if( dc.strCompare.equals( FileSyncTableModel.GE ))
            {
              fiSource = dc.fiLeft;
              fiTarget = dc.fiRight;
            }
            else if( dc.strCompare.equals( FileSyncTableModel.LE ))
            {
              fiSource = dc.fiRight;
              fiTarget = dc.fiLeft;
            }

            if( dc.strCompare.equals( FileSyncTableModel.GE ) ||
                dc.strCompare.equals( FileSyncTableModel.LE ))
            {
              if( !fiSource.exists())
              {
                int iRet = JOptionPane.showConfirmDialog( FileSyncTable.this, "Delete file " + fiTarget.getName() + "?",
                                                        "Confirmation", JOptionPane.YES_NO_OPTION,
                                                        JOptionPane.QUESTION_MESSAGE );
                if( iRet == JOptionPane.YES_OPTION )
                {
                  if( fiTarget.delete())
                    m_fstm.removeRow( dc );
                }
                pb.setValue( pb.getValue()+1);
              }
              else
              {
                int iSuccess = copy( fiSource, fiTarget, pb );
                if( iSuccess == CopyFileDialog.YES_OPTION )
                {
                  dc.setEqual();
                  dc.fiLeft.getFileInterfaceConfiguration().setCached( false);
                  dc.fiLeft.lastModified();
                  dc.fiLeft.length();
                  dc.fiRight.getFileInterfaceConfiguration().setCached( false);
                  dc.fiRight.lastModified();
                  dc.fiRight.length();
                  if( !isEqual)
                    m_fstm.removeRow( dc );
                  m_fstm.forceRedraw();
                }
                else if( iSuccess == CopyFileDialog.CANCEL_OPTION )
                  break;
              }
            }
            else
              pb.setValue( pb.getValue()+1);
          }
        }
      };
      m_th.start();
    }
  }

  /**
   * copy files
   *
   * @return true if the file or directory is successfully copied; false otherwise
   */
  int copy( FileInterface fiOrg, FileInterface fiDest, JProgressBar jProgressBar)
  {
    int iRet = CopyFileDialog.CANCEL_OPTION;
    boolean blRet = false;
    // do not copy spezal dirs . and ..
    if( fiOrg.getName().equals( ".") || fiOrg.getName().equals( ".."))
      return CopyFileDialog.YES_OPTION;
    final String strDest = fiDest.getAbsolutePath();
    if( fiOrg.isFile() || fiOrg.isArchive())
    { // copy a file
      if( !m_blOverwriteAll & fiDest.exists())
      {
        // file exists allready -> ask to overwrite it
        CopyFileDialog dlg = new CopyFileDialog( this, "Confirmation", true, "Overwrite file " + fiOrg.getName() + "?");
        dlg.setVisible( true);
        int iRes = dlg.getValue();
        if( iRes == CopyFileDialog.CANCEL_OPTION)
        {
          jProgressBar.setValue( jProgressBar.getValue()+1);
          return iRet;
        }
        else if( iRes == CopyFileDialog.OVALL_OPTION)
          m_blOverwriteAll = true;
        else if( iRes == CopyFileDialog.NO_OPTION)
        {
          jProgressBar.setValue( jProgressBar.getValue()+1);
          return iRet;
        }
      }
      try
      {
        BufferedOutputStream bos = fiDest.getBufferedOutputStream( fiDest.getAbsolutePath() );
        blRet = fiOrg.copyFile( bos);
        bos.flush();
        bos.close();
        fiDest.setLastModified( fiOrg.lastModified());
        jProgressBar.setValue( jProgressBar.getValue()+1);
      }
      catch( Exception ex )
      {
        ex.printStackTrace( GlobalApplicationContext.instance().getErrorPrintStream());
      }
    }
    else if( fiOrg.isDirectory())
    { // copy dir recursively
      blRet = fiDest.exists();
      if( !blRet ) // create the copied dir if not exists on dest
      {
        /*
        FileInterface fiNewDir = FileConnector.createFileInterface( fiDest.getAbsolutePath(), null, false,
          fiDest.getConnObj(),
          m_strAppName,
          fiDest.getAppProperties(), true );
        */
        blRet = fiDest.mkdir();
        fiDest.setLastModified( fiOrg.lastModified());
      }
    }
    if( blRet)
      iRet = CopyFileDialog.YES_OPTION;
    return iRet;
  }

  public void interrupt()
  {
    if( m_th != null && m_th.isAlive())
    {
      try
      {
        m_th.interrupt();
        if( m_th.isAlive())
        {
          m_th.sleep( 300 );
          if( m_th.isAlive())
            m_th.stop();
        }
      }
      catch( InterruptedException ex )
      {
        ex.printStackTrace( GlobalApplicationContext.instance().getErrorPrintStream());
      }
    }
  }

  class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent event)
    {
      Object object = event.getSource();
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
      Object object = event.getSource();
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
    Point p = event.getPoint();

    if( !m_blLocked && columnAtPoint( p) == 3)
    {
      FsDataContainer dc = getSelectedDataContainer();
      dc.change();
      m_fstm.forceRedraw();
    }
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
          if( m_fstm != null && m_fstm.getDataContainerArray() != null)
          {
            int iLen = m_fstm.getDataContainerArray().length;
            if( iLen > 0)
              setRowSelectionInterval( 0, 0);
          }
          e.consume();
          break;

        case KeyEvent.VK_END:
          if( m_fstm != null && m_fstm.getDataContainerArray() != null)
          {
            int iLen = m_fstm.getDataContainerArray().length;
            if( iLen > 0)
              setRowSelectionInterval( iLen-1, iLen-1);
          }
          e.consume();
          break;

        default:
          super.keyPressed( e);
          break;
      }
    }
  }

  class AdpComponent
    extends ComponentAdapter
  {
    public void componentResized( ComponentEvent event)
    {
      Object object = event.getSource();
      if (object == FileSyncTable.this)
      {
        onResize();
      }
    }
  }

  public void onResize()
  {
    FileSyncTable fiTab = this;
    int iWidth = fiTab.getWidth();

    if( iWidth == 0)
      iWidth = (int)fiTab.getPreferredSize().getWidth();

    getColumnModel().getColumn( 0).setPreferredWidth( (int)(iWidth*0.3));
    getColumnModel().getColumn( 1).setPreferredWidth( (int)(iWidth*0.06));
    getColumnModel().getColumn( 2).setPreferredWidth( (int)(iWidth*0.11));
    getColumnModel().getColumn( 3).setPreferredWidth( (int)(iWidth*0.06));
    getColumnModel().getColumn( 4).setPreferredWidth( (int)(iWidth*0.11));
    getColumnModel().getColumn( 5).setPreferredWidth( (int)(iWidth*0.06));
    getColumnModel().getColumn( 6).setPreferredWidth( (int)(iWidth*0.3));
  }

  public FsDataContainer getDataContainerAt( int iIdx)
  {
    return m_fstm.getDataContainerAt( iIdx);
  }

  public FsDataContainer getSelectedDataContainer()
  {
    if( getSelectedRow() < 0)
      return null;
    return getDataContainerAt( getSelectedRow());
  }

  public FsDataContainer[] getSelectedDataContainers()
  {
    int iIdx[] = getSelectedRows();
    FsDataContainer dc[] = new FsDataContainer[ iIdx.length];

    for( int i = 0; i < iIdx.length; i++)
      dc[i] = getDataContainerAt( iIdx[i]);

    return dc;
  }

  public String getToolTipText(MouseEvent event)
  {
    int row = rowAtPoint( event.getPoint());
    int column = columnAtPoint( event.getPoint());
    String strTTText = null;
    Object obj = null;

    if( row != -1)
    {
      switch( column)
      {
        case 0:
        case 3:
        case 6:
          obj = getRealValueAt( row, column);
          if( obj != null)
            strTTText = obj.toString();
          break;

        case 1:
        case 2:
        case 4:
        case 5:
          obj = getValueAt( row, column);
          if( obj != null)
            strTTText = obj.toString();
          break;
      }
    }
    return strTTText;
  }

  public boolean isCellEditable(int row, int column)
  {
    if( getSelectedRow() == row)
      return m_fstm.isCellEditable( row, column);
    else
      return false;
  }

  public Object getValueAt( int row, int column)
  {
    Object objRet = null;

    if( m_fstm != null)
    {
      objRet = m_fstm.getValueAt( row, column);
    }
    return objRet;
  }

  public Object getRealValueAt( int row, int column)
  {
    Object objRet = null;

    if( m_fstm != null)
    {
      objRet = m_fstm.getRealValueAt( row, column);
    }
    return objRet;
  }

  public void setValueAt( Object aValue, int row, int column)
  {
    if( m_fstm != null)
    {
      m_fstm.setValueAt( aValue, row, column);
    }
  }

/*
  protected JTableHeader createDefaultTableHeader()
  {
    return new FileSyncTableHeader(columnModel);
  }
*/

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
    FileSyncTable demo = new FileSyncTable( "FileSyncTable", new AppProperties());

    JScrollPane scrollpane = new JScrollPane(demo);
    JFrame f = new JFrame("FileSyncTable");
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

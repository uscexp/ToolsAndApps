package haui.components.desktop;

import haui.resource.ResouceManager;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.Array;
import javax.swing.DefaultFocusManager;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;

/**
 * Module:      JDesktopFrame.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\desktop\\JDesktopFrame.java,v $ <p> Description: JDesktopFrame.<br> </p><p> Created:     23.04.2003	by	AE </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @created      23. April 2003
 * @history      23.04.2003	by	AE: Created.<br>  </p><p>  Modification:<br>  $Log: JDesktopFrame.java,v $  Revision 1.2  2004-08-31 16:03:03+02  t026843  Large redesign for application dependent outputstreams, mainframes, AppProperties!  Bugfixes to DbTreeTableView, additional features for jDirWork.  Revision 1.1  2003-05-28 14:19:48+02  t026843  reorganisations  Revision 1.0  2003-05-21 16:26:04+02  t026843  Initial revision  </p><p>
 * @version      v1.0, 2003; $Revision: 1.2 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\components\\desktop\\JDesktopFrame.java,v 1.2 2004-08-31 16:03:03+02 t026843 Exp t026843 $  </p><p>
 * @since        JDK1.3  </p>
 */
public class JDesktopFrame
  extends JFrame
{
  private static final long serialVersionUID = 7836225472915076635L;
  
  // Constants
  public static String APPICON = "Splash.gif";
  public static final String WLOCX = "WinLocationX";
  public static final String WLOCY = "WinLocationY";
  public static final String WWIDTH = "WinWidth";
  public static final String WHEIGHT = "WinHeight";
  public static final String OFRAMELOCX = "OutFrameLocationX";
  public static final String OFRAMELOCY = "OutFrameLocationY";
  public static final String OFRAMEWIDTH = "OutFrameWidth";
  public static final String OFRAMEHEIGHT = "OutFrameHeight";
  public static final String DOCPATH = "DocPath";
  public static final String DOCINDEX = "DocIndex";

  // Default properties constants
  public static final String WINCASCADED = "WinCascaded";
  public static final String WINMAXTOOP = "WinMaxToOp";
  public static final String WINMAX = "WinMax";

  // GUI member variables
  protected JDesktopPane m_dp;
  protected ExDesktopManager m_dpManager;
  protected OutputFrame m_ofOutput;
  protected JMenu m_jMenuWindowDesktop = null;
  protected JMenuItem m_jMenuItemClear;
  protected JMenuItem m_jMenuItemSave;
  protected JSeparator m_jSeparatorSave;
  protected JMenuItem m_jMenuItemWOP;
  protected JMenuItem m_jMenuItemWSD;
  protected JSeparator m_jSeparatorFixWin;
  protected JMenuItem m_jMenuItemCascade;
  protected JMenuItem m_jMenuItemTile;
  protected JMenuItem m_jMenuItemMax;
  protected JMenuItem m_jMenuItemMaxToOp;
  protected JMenuItem m_jMenuItemFitDp;
  protected JMenuItem m_jMenuItemFOP;
  protected JMenuItem m_jMenuItemDeicon;
  protected JSeparator m_jSeparatorSizeWin;
  protected JButton m_jButtonClear;
  protected WindowSelectDialog m_wsDlg;

  // member variables
  protected ActionListener m_actListener = null;
  protected static final int m_xOffset = 25 ;
  protected static final int m_yOffset = 25 ;
  protected AppProperties m_appProps = new AppProperties();
  protected String m_strDocPath;
  protected String m_strDocIndex;
  protected String m_strAppName;

  public JDesktopFrame( String title, String strAppName)
    //throws HeadlessException
  {
    super( title);
    m_strAppName = strAppName;
    GlobalApplicationContext.instance().addRootComponent(this);
    String strL_F = UIManager.getSystemLookAndFeelClassName();
    //String strL_F = UIManager.getCrossPlatformLookAndFeelClassName();
    try
    {
      Class.forName(strL_F);
      UIManager.setLookAndFeel( strL_F);
    }
    catch( Exception ex)
    {
      //m_cmd.ErrorMsgDlg( this, strL_F + " konnte nicht eingestellt werden!");
    }

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    //Quit this app when the big window closes.
    AdpWindow adpWin = new AdpWindow();
    addWindowListener( adpWin);

    //Make dragging faster:
    m_dp.putClientProperty( "JDesktopPane.dragMode", "outline");

    // set focus manager to handle tabs
    FocusManager.setCurrentManager( new SpecialFocusManager());
  }

  //Initialisierung der Komponenten
  private void jbInit()
    throws Exception
  {
    m_dp = new JDesktopPane();
    m_dpManager = new ExDesktopManager( m_dp);
  }

  public JDesktopPane getDesktopPane()
  {
    return m_dp;
  }

  public AppProperties getAppProperties()
  {
    return m_appProps;
  }

  public void createFrame( JInternalFrame jIntFrame)
  {
    boolean bl = m_appProps.getBooleanProperty( WINMAX);
    if( bl)
    {
      try
      {
        jIntFrame.setMaximum( true);
      }
      catch( PropertyVetoException e)
      {}
    }
    bl = m_appProps.getBooleanProperty( WINMAXTOOP);
    if( bl)
    {
      onFixOp();
      int iWidth = m_dp.getWidth();
      int iHeight = m_dp.getHeight()-m_ofOutput.getHeight()+m_yOffset;
      jIntFrame.setSize( iWidth, iHeight);
      jIntFrame.setLocation( 0, 0);
    }
    try
    {
      jIntFrame.setSelected(true);
    }
    catch( PropertyVetoException e)
    {}
  }

  protected void createFileMenuEntries( ActionListener actionListener, JMenu jMenuFile)
  {
    m_actListener = actionListener;
    m_jMenuItemClear = new JMenuItem();
    m_jSeparatorSave = new JSeparator();
    m_jMenuItemSave = new JMenuItem();
    m_jMenuItemClear.setText( "Clear output");
    m_jMenuItemClear.setActionCommand( "clear");
    m_jMenuItemClear.setMnemonic( (int)'C');
    m_jMenuItemClear.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "clearout.gif"));
    jMenuFile.add( m_jMenuItemClear);
    jMenuFile.add( m_jSeparatorSave);
    m_jMenuItemSave.setText( "Save prefs.");
    m_jMenuItemSave.setActionCommand( "save");
    m_jMenuItemSave.setMnemonic( (int)'S');
    jMenuFile.add( m_jMenuItemSave);

    m_jMenuItemClear.addActionListener( m_actListener);
    m_jMenuItemSave.addActionListener( m_actListener);
  }

  protected void createWindowMenuEntries( ActionListener actionListener, JMenu jMenuWindow)
  {
    m_actListener = actionListener;
    m_jMenuWindowDesktop = jMenuWindow;
    m_jMenuItemWOP = new JMenuItem();
    m_jMenuItemWSD = new JMenuItem();
    m_jSeparatorFixWin = new JSeparator();
    m_jMenuItemCascade = new JMenuItem();
    m_jMenuItemTile = new JMenuItem();
    m_jMenuItemMax = new JMenuItem();
    m_jMenuItemMaxToOp = new JMenuItem();
    m_jMenuItemFitDp = new JMenuItem();
    m_jMenuItemFOP = new JMenuItem();
    m_jMenuItemDeicon = new JMenuItem();
    m_jSeparatorSizeWin = new JSeparator();
    m_jMenuItemCascade.setText( "Cascade windows");
    m_jMenuItemCascade.setActionCommand( "cascadewin");
    m_jMenuItemCascade.setMnemonic( (int)'C');
    m_jMenuItemCascade.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "cascadewindows16.gif"));
    jMenuWindow.add( m_jMenuItemCascade);
    m_jMenuItemTile.setText( "Tile windows");
    m_jMenuItemTile.setActionCommand( "tilewin");
    m_jMenuItemTile.setMnemonic( (int)'T');
    m_jMenuItemTile.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "tilevertical16.gif"));
    jMenuWindow.add( m_jMenuItemTile);
    m_jMenuItemMax.setText( "Maximize windows");
    m_jMenuItemMax.setActionCommand( "maxwin");
    m_jMenuItemMax.setMnemonic( (int)'M');
    m_jMenuItemMax.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "maximizewindows16.gif"));
    jMenuWindow.add( m_jMenuItemMax);
    m_jMenuItemMaxToOp.setText( "Maximize to output");
    m_jMenuItemMaxToOp.setActionCommand( "maxtoopwin");
    m_jMenuItemMaxToOp.setMnemonic( (int)'o');
    jMenuWindow.add( m_jMenuItemMaxToOp);
    m_jMenuItemFitDp.setText( "Fit desktop to view");
    m_jMenuItemFitDp.setActionCommand( "fitdptoview");
    m_jMenuItemFitDp.setMnemonic( (int)'v');
    jMenuWindow.add( m_jMenuItemFitDp);
    m_jMenuItemFOP.setText( "Fix Output");
    m_jMenuItemFOP.setActionCommand( "fixop");
    m_jMenuItemFOP.setMnemonic( (int)'F');
    jMenuWindow.add( m_jMenuItemFOP);
    m_jMenuItemDeicon.setText( "Deiconify all");
    m_jMenuItemDeicon.setActionCommand( "deiconwin");
    m_jMenuItemDeicon.setMnemonic( (int)'D');
    jMenuWindow.add( m_jMenuItemDeicon);
    jMenuWindow.add( m_jSeparatorSizeWin);
    m_jMenuItemWOP.setText( "View Output");
    m_jMenuItemWOP.setActionCommand( "viewop");
    jMenuWindow.add( m_jMenuItemWOP);
    m_jMenuItemWSD.setText( "Window selection");
    m_jMenuItemWSD.setActionCommand( "seldlg");
    m_jMenuItemWSD.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_TAB, Event.CTRL_MASK));
    m_jMenuItemWSD.setMnemonic( (int)'W');
    jMenuWindow.add( m_jMenuItemWSD);
    jMenuWindow.add( m_jSeparatorFixWin);

    m_jMenuItemWOP.addActionListener( m_actListener);
    m_jMenuItemWSD.addActionListener( m_actListener);
    m_jMenuItemCascade.addActionListener( m_actListener);
    m_jMenuItemTile.addActionListener( m_actListener);
    m_jMenuItemMax.addActionListener( m_actListener);
    m_jMenuItemMaxToOp.addActionListener( m_actListener);
    m_jMenuItemFitDp.addActionListener( m_actListener);
    m_jMenuItemFOP.addActionListener( m_actListener);
    m_jMenuItemDeicon.addActionListener( m_actListener);
  }

  protected void createToolbarEntries( ActionListener actionListener, JToolBar jToolBar)
  {
    m_actListener = actionListener;
    m_jButtonClear = new JButton();
    m_jButtonClear.setMaximumSize(new Dimension(20, 18));
    m_jButtonClear.setMinimumSize(new Dimension(20, 18));
    m_jButtonClear.setPreferredSize(new Dimension(20, 18));
    m_jButtonClear.setToolTipText("Clear output");
    m_jButtonClear.setActionCommand("clear");
    m_jButtonClear.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "clearout.gif"));

    jToolBar.add(m_jButtonClear, null);

    m_jButtonClear.addActionListener( m_actListener);
  }

  protected void createToolFrames()
  {
    m_ofOutput = new OutputFrame( m_strAppName, "Output:", this);
    m_ofOutput.setVisible(true); //necessary as of kestrel
    m_dp.add( m_ofOutput);
    // Set stored dimension to output frame
    Integer ofx = m_appProps.getIntegerProperty( OFRAMELOCX);
    Integer ofy = m_appProps.getIntegerProperty( OFRAMELOCY);
    Integer ofwidth = m_appProps.getIntegerProperty( OFRAMEWIDTH);
    Integer ofheight = m_appProps.getIntegerProperty( OFRAMEHEIGHT);

    if( ofx != null && ofy != null)
      m_ofOutput.setLocation( ofx.intValue(), ofy.intValue());

    if( ofwidth != null && ofheight != null)
      m_ofOutput.setSize( ofwidth.intValue(), ofheight.intValue());
    m_dpManager.resizeDesktop( m_ofOutput);
  }

  /**
   * Read properties from file.
   */
  protected void readProperties( String strFile)
  {
    m_appProps.load( strFile);
    // DB url information
    m_strDocPath = m_appProps.getProperty( DOCPATH);
    m_strDocIndex = m_appProps.getProperty( DOCINDEX);

    if( m_strDocPath == null)
      m_strDocPath = "doc";
    if( m_strDocIndex == null)
      m_strDocIndex = "index.html";

    //Make the big window be indented 50 pixels from each edge of the screen.
    // Default dimension
    int inset = 50;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds(inset, inset, screenSize.width - inset*2, screenSize.height-inset*2);

    // Set stored dimension
    Integer wx = m_appProps.getIntegerProperty( WLOCX);
    Integer wy = m_appProps.getIntegerProperty( WLOCY);
    Integer wwidth = m_appProps.getIntegerProperty( WWIDTH);
    Integer wheight = m_appProps.getIntegerProperty( WHEIGHT);

    if( wx != null && wy != null)
      setLocation( wx.intValue(), wy.intValue());

    if( wwidth != null && wheight != null)
      setSize( wwidth.intValue(), wheight.intValue());
  }

  /**
   * Save properties from file.
   */
  protected void saveProperties()
  {
    m_appProps.remove( DOCPATH);
    m_appProps.remove( DOCINDEX);

    m_appProps.setProperty( DOCPATH, m_strDocPath);
    m_appProps.setProperty( DOCINDEX, m_strDocIndex);

    // Window location and dimension
    m_appProps.setIntegerProperty( WLOCX, new Integer( getX()));
    m_appProps.setIntegerProperty( WLOCY, new Integer( getY()));
    m_appProps.setIntegerProperty( WWIDTH, new Integer( getWidth()));
    m_appProps.setIntegerProperty( WHEIGHT, new Integer( getHeight()));

    // Output frame location and dimension
    m_appProps.setIntegerProperty( OFRAMELOCX, new Integer( m_ofOutput.getX()));
    m_appProps.setIntegerProperty( OFRAMELOCY, new Integer( m_ofOutput.getY()));
    m_appProps.setIntegerProperty( OFRAMEWIDTH, new Integer( m_ofOutput.getWidth()));
    m_appProps.setIntegerProperty( OFRAMEHEIGHT, new Integer( m_ofOutput.getHeight()));
  }

  public JInternalFrame[] getRelevantInternalFrames()
  {
    int iWin = 0;
    JInternalFrame[] intFrame = null;
    JInternalFrame[] ifArray = m_dp.getAllFrames();
    int iCount = Array.getLength( ifArray);
    int iIntFrameCount = iCount-1;
    for( int i = 0; i < iCount; ++i)
    {
      if( !ifArray[i].isMaximizable())
      {
        --iIntFrameCount;
      }
    }
    if( iIntFrameCount > 0)
    {
      intFrame = new JInternalFrame[iIntFrameCount];
      iWin = 0;
      for( int i = 0; i < iCount; ++i)
      {
        if( !m_ofOutput.equals( ifArray[i]) && ifArray[i].isMaximizable())
        {
          intFrame[iWin] = ifArray[i];
          ++iWin;
        }
      }
    }
    else
      intFrame = new JInternalFrame[0];
    return intFrame;
  }

  public void menuCanceled( MenuEvent event)
  {
  }

  public void menuDeselected( MenuEvent event)
  {
  }

  public void menuSelected( MenuEvent event, int iMenuItemCount)
  {
    if( iMenuItemCount == -1)
      iMenuItemCount = 11;
    JInternalFrame[] ifArray = getRelevantInternalFrames();
    Object object = event.getSource();
    int i = 0;
    if( m_jMenuWindowDesktop != null && object == m_jMenuWindowDesktop)
    {
      int iCount = m_jMenuWindowDesktop.getItemCount();
      if( iCount > iMenuItemCount)
      {
        for( i = iCount-1; i >= iMenuItemCount; --i)
        {
          m_jMenuWindowDesktop.getItem( i).removeActionListener( m_actListener);
          m_jMenuWindowDesktop.remove( i);
        }
      }
      //int iWin = -1;
      for( i = 0; i < Array.getLength( ifArray); ++i)
      {
        JInternalFrame intFrame = ifArray[i];
        JMenuItem mi = new JMenuItem( intFrame.getTitle());
        mi.setActionCommand( String.valueOf( i));
        m_jMenuWindowDesktop.add( mi);
        mi.addActionListener( m_actListener);
      }
    }
  }

  protected boolean actionPerformed( ActionEvent event, String cmd)
  {
    boolean blRet = false;
    if( cmd == "windows")
    {
      onViewOp();
      blRet = true;
    }
    else if( cmd == "viewop")
    {
      onViewOp();
      blRet = true;
    }
    else if( cmd == "seldlg")
    {
      onWindowSelectDialog();
      blRet = true;
    }
    else if( cmd == "fixop")
    {
      onFixOp();
      blRet = true;
    }
    else if( cmd == "cascadewin")
    {
      onCascadeWin();
      blRet = true;
    }
    else if( cmd == "tilewin")
    {
      onTileWin();
      blRet = true;
    }
    else if( cmd == "maxwin")
    {
      onMaxWin();
      blRet = true;
    }
    else if( cmd == "maxtoopwin")
    {
      onMaxToOpWin();
      blRet = true;
    }
    else if( cmd == "fitdptoview")
    {
      fitDesktopToView();
      blRet = true;
    }
    else if( cmd == "deiconwin")
    {
      onDeiconWin();
      blRet = true;
    }
    else if( cmd == "save")
    {
      onSave();
      blRet = true;
    }
    else if( cmd == "clear")
    {
      onClear();
      blRet = true;
    }
    return blRet;
  }

  protected void fitDesktopToView()
  {
    int iWidth = (int)m_dp.getVisibleRect().getWidth();
    int iHeight = (int)m_dp.getVisibleRect().getHeight();
    m_dp.setPreferredSize( new Dimension( iWidth, iHeight));
    m_dp.setSize( iWidth, iHeight);
    m_dp.revalidate();
  }

  protected void onClear()
  {
    m_ofOutput.onClear();
  }

  protected void onViewOp()
  {
    if( m_ofOutput.isIcon())
    {
      try
      {
        m_ofOutput.setIcon( false);
      }
      catch( PropertyVetoException e)
      {}
    }
    m_ofOutput.toFront();
    try
    {
      m_ofOutput.setSelected(true);
    }
    catch( PropertyVetoException e)
    {}
  }

  protected void onFixOp()
  {
    m_ofOutput.onFixWindow( m_dp);
  }

  protected void onCascadeWin()
  {
    JInternalFrame[] ifArray = getRelevantInternalFrames();
    int iWidth = (int)(m_dp.getWidth()*0.8d);
    int iHeight = (int)(m_dp.getHeight()*0.8d);
    int iWin = 0;

    int iCount = Array.getLength( ifArray);
    if( iCount > 0)
    {
      JInternalFrame intFrames[] = new JInternalFrame[iCount];
      for( int i = 0; i < iCount; ++i)
      {
        JInternalFrame intFramesTmp = (JInternalFrame)ifArray[i];
        if( !intFramesTmp.isClosed() && !intFramesTmp.isIcon())
        {
          intFrames[iWin] = (JInternalFrame)ifArray[i];
          ++iWin;
        }
      }
      iCount = iWin;
      iWin = 0;
      for( int i = 0; i < iCount; ++i)
      {
        JInternalFrame intFramesTmp = intFrames[i];
        if( !intFramesTmp.isClosed() && !intFramesTmp.isIcon())
        {
          if( intFramesTmp.isIcon())
          {
            try
            {
              intFramesTmp.setIcon( false);
            }
            catch( PropertyVetoException e)
            {}
          }
          else if( intFramesTmp.isMaximum())
          {
            try
            {
              intFramesTmp.setMaximum( false);
            }
            catch( PropertyVetoException e)
            {}
          }
          intFramesTmp.setSize( iWidth, iHeight);
          intFramesTmp.setLocation( m_xOffset*(iCount-iWin-1), m_yOffset*(iCount-iWin-1));
          ++iWin;
        }
      }
    }
  }

  protected void onTileWin()
  {
    JInternalFrame[] ifArray = getRelevantInternalFrames();
    int iCount = Array.getLength( ifArray);
    if( iCount > 0)
    {
      int iWin = 0;
      JInternalFrame intFrames[] = new JInternalFrame[iCount];
      for( int i = 0; i < iCount; ++i)
      {
        JInternalFrame intFramesTmp = (JInternalFrame)ifArray[i];
        if( !intFramesTmp.isClosed() && !intFramesTmp.isIcon())
        {
          intFrames[iWin] = (JInternalFrame)ifArray[i];
          ++iWin;
        }
      }
      iCount = iWin;
      // Determine size of grid to tile into. e.g 3X3 for 9 cells.
      int iRows = (int)Math.sqrt( iCount);
      int iCols = iRows;
      while( iRows * iCols < iCount)
      {
        ++iRows;
        if( iRows * iCols < iCount)
        {
          ++iCols;
        }
      }

      int iWidth = m_dp.getWidth()/iCols;
      int iHeight = m_dp.getHeight()/iRows;
      int iPosX = 0;
      int iPosY = 0;
      for( int y = 0; y < iRows; ++y)
      {
        for( int x = 0; x < iCols; ++x)
        {
          int idx = y + (x * iRows);
          if( idx >= iCount)
          {
            break;
          }
          JInternalFrame frame = intFrames[idx];
          if( !frame.isClosed() && !frame.isIcon())
          {
            if( frame.isIcon())
            {
              try
              {
                  frame.setIcon(false);
              }
              catch( PropertyVetoException e)
              {}
            }
            else if( frame.isMaximum())
            {
              try
              {
                frame.setMaximum( false);
              }
              catch( PropertyVetoException e)
              {}
            }
            frame.reshape( iPosX, iPosY, iWidth, iHeight);
              iPosX += iWidth;
          }
        }
        iPosX = 0;
        iPosY += iHeight;
      }
    }
  }

  protected void onMaxWin()
  {
    JInternalFrame[] ifArray = getRelevantInternalFrames();
    int iCount = Array.getLength( ifArray);
    for( int i = 0; i < iCount; ++i)
    {
      JInternalFrame intFrames = (JInternalFrame)ifArray[i];
      if( !intFrames.isClosed() && !intFrames.isIcon())
      {
        if( intFrames.isIcon())
        {
          try
          {
            intFrames.setIcon( false);
          }
          catch( PropertyVetoException e)
          {}
        }
        try
        {
          intFrames.setMaximum( true);
        }
        catch( PropertyVetoException e)
        {}
      }
    }
  }

  protected void onMaxToOpWin()
  {
    JInternalFrame[] ifArray = getRelevantInternalFrames();
    onFixOp();
    int iWidth = m_dp.getWidth();
    int iHeight = m_dp.getHeight()-m_ofOutput.getHeight()+m_yOffset;
    int iWin = 0;
    int iCount = Array.getLength( ifArray);
    if( iCount > 0)
    {
      JInternalFrame intFrames[] = new JInternalFrame[iCount];
      for( int i = 0; i < iCount; ++i)
      {
        JInternalFrame intFramesTmp = (JInternalFrame)ifArray[i];
        if( !intFramesTmp.isClosed() && !intFramesTmp.isIcon())
        {
          intFrames[iWin] = (JInternalFrame)ifArray[i];
          ++iWin;
        }
      }
      iCount = iWin;
      for( int i = 0; i < iCount; ++i)
      {
        JInternalFrame intFramesTmp = intFrames[i];
        if( !intFramesTmp.isClosed() && !intFramesTmp.isIcon())
        {
          if( intFramesTmp.isIcon())
          {
            try
            {
              intFramesTmp.setIcon( false);
            }
            catch( PropertyVetoException e)
            {}
          }
          else if( intFramesTmp.isMaximum())
          {
            try
            {
              intFramesTmp.setMaximum( false);
            }
            catch( PropertyVetoException e)
            {}
          }
          intFramesTmp.setSize( iWidth, iHeight);
          intFramesTmp.setLocation( 0, 0);
        }
      }
    }
  }

  protected void onDeiconWin()
  {
    JInternalFrame[] ifArray = getRelevantInternalFrames();
    int iCount = Array.getLength( ifArray);
    if( m_ofOutput.isIcon())
    {
      try
      {
        m_ofOutput.setIcon( false);
      }
      catch( PropertyVetoException e)
      {}
    }
    for( int i = 0; i < iCount; ++i)
    {
      JInternalFrame intFrames = (JInternalFrame)ifArray[i];
      if( !intFrames.isClosed())
      {
        if( intFrames.isIcon())
        {
          try
          {
            intFrames.setIcon( false);
          }
          catch( PropertyVetoException e)
          {}
        }
      }
    }
  }

  protected void onWindowSelectDialog()
  {
    m_wsDlg = new WindowSelectDialog( m_dp, m_dp, m_strAppName);
    m_wsDlg.setVisible( true);
    m_wsDlg = null;
  }

  protected void onExit()
  {
    if( m_ofOutput != null)
    {
      m_ofOutput.onExit();
    }
  }

  protected void onSave()
  {
  }

  public void addNotify()
  {
    super.addNotify();
  }

  public class SpecialFocusManager
    extends DefaultFocusManager
  {
    int m_iCount = 0;

    public void processKeyEvent( Component focusedComponent, KeyEvent e)
    {
      if( e.getKeyCode() == KeyEvent.VK_TAB )
      {
        if( e.getModifiers() == KeyEvent.CTRL_MASK)
        {
          if( m_wsDlg == null || !m_wsDlg.isVisible())
          {
            onWindowSelectDialog();
            m_iCount = 0;
          }
          else
          {
            if( m_iCount == 0)
            {
              m_wsDlg.nextSelection();
              ++m_iCount;
            }
            else
              m_iCount = 0; // skip second event
          }
          e.consume();
          return;
        }
      }
      super.processKeyEvent( focusedComponent, e);
    }
  }

  class AdpWindow extends WindowAdapter
  {
    public void windowClosing( WindowEvent event)
    {
      Object object = event.getSource();
      if( object == JDesktopFrame.this)
      {
        onExit();
      }
    }
  }
}
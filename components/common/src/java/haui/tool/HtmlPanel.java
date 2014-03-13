
package haui.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 * Module:					HtmlPanel.java <p> Description:		Html viewer with minimized functionality. </p><p> Created:				19.11.1998	by	AE </p><p> Last Modified:	21.09.2000	by	AE </p><p> history					19.11.1998 - 19.11.1998	by	AE:	Create HtmlPanel basic functionality<br> 03.12.1998	by	AE:	syncronizing PageLoader and extending PageLoader from Thread<br> 15.04.1999	by	AE:	Converted to JDK v1.2<br> 16.04.1999 - 22.04.1999	by	AE:	Implement HtmlFrame functionality<br> 12.09.2000	by	AE:	Workaround für next - and prev botton bug<br> 15.09.2000	by	AE:	Searchbar added<br> 21.09.2000	by	AE:	Cancel search dialog added<br> 22.09.2000	by	AE:	Search result output (HTML) added<br> 30.04.2003	by	AE:	Compatibility changes for JDK v1.4<br> </p><p> todo						Find foreward button bug.<br> todo						Find back and home button instability.<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999,2000,2001,2002,2003  </p><p>
 * @since  					JDK1.2  </p>
 */
public class HtmlPanel
  extends JPanel
{
  private static final long serialVersionUID = -5367544417831113479L;
  
  String m_strAppName;
  int m_iCurHist;
  Vector m_vectorHist;
  PageLoader m_pl;
  String m_strSearchDir = ".";
  AppAction m_appAction = new AppAction();
  HyperlinkAction m_hypAction = new HyperlinkAction();
  JScrollPane m_jScrollPane = new JScrollPane();
  JEditorPane m_jEditorPane = new JEditorPane();
  JToolBar m_jToolBar = new JToolBar();
  JButton m_jButtonBack = new JButton();
  JButton m_jButtonNext = new JButton();
  JButton m_jButtonHome = new JButton();
  JPanel m_jPanelTop = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JTextField m_jTextFieldSearch = new JTextField();
  JPanel m_jPanelSearch = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton m_jButtonSearch = new JButton();
  JCheckBox m_jCheckBoxCase = new JCheckBox();

  public HtmlPanel( String strAppName)
  {
    Constructor( null, strAppName);
  }

  public HtmlPanel( URL url, String strAppName)
  {
    Constructor( url, strAppName);
  }

  public HtmlPanel( String str, String strAppName)
  {
    URL url = null;
    try
    {
      url = new URL(str);
    }
    catch (MalformedURLException e)
    {
      System.out.println("Malformed URL: " + e);
    }

    Constructor( url, strAppName);
  }

  public void Constructor( URL url, String strAppName)
  {
    m_strAppName = strAppName;
    getAccessibleContext().setAccessibleName("HTML panel");
    getAccessibleContext().setAccessibleDescription("panel to view HTML documents");
    try
    {
      jbInit();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    m_jEditorPane.addHyperlinkListener(m_hypAction);
    m_iCurHist = -1;
    m_vectorHist = new Vector();
    if( url != null)
    {
      m_iCurHist = 0;
      setPage( url, null);
    }

    m_jButtonHome.addActionListener(m_appAction);
    m_jButtonBack.addActionListener(m_appAction);
    m_jButtonNext.addActionListener(m_appAction);
    m_jButtonSearch.addActionListener(m_appAction);
  }

  void jbInit() throws Exception
  {
    setLayout(new BorderLayout(0,0));
    m_jScrollPane.setOpaque(true);
    m_jEditorPane.setEditable(false);
    m_jButtonBack.setActionCommand("Back");
    m_jButtonBack.setToolTipText("Go to previous page");
    m_jButtonNext.setActionCommand("Next");
    m_jButtonNext.setToolTipText("Go to next page");
    m_jButtonHome.setActionCommand("Home");
    m_jButtonHome.setToolTipText("Go to Home page");
    m_jPanelTop.setLayout(borderLayout1);
    m_jPanelSearch.setLayout(borderLayout2);

    m_jButtonSearch.setActionCommand("search");
    m_jButtonSearch.setText("search");
    m_jTextFieldSearch.setMinimumSize(new Dimension(80, 21));
    m_jTextFieldSearch.setPreferredSize(new Dimension(140, 21));

    m_jCheckBoxCase.setText("Match case");
    this.add(m_jScrollPane, BorderLayout.CENTER);
    this.add(m_jPanelTop, BorderLayout.NORTH);
    m_jPanelTop.add(m_jToolBar, BorderLayout.CENTER);
    m_jToolBar.add(m_jButtonBack);
    m_jToolBar.add(m_jButtonNext);
    m_jToolBar.add(m_jButtonHome);
    m_jPanelTop.add(m_jPanelSearch, BorderLayout.EAST);
    m_jPanelSearch.add(m_jTextFieldSearch, BorderLayout.CENTER);
    m_jPanelSearch.add(m_jButtonSearch, BorderLayout.WEST);
    m_jPanelSearch.add(m_jCheckBoxCase, BorderLayout.EAST);
    m_jScrollPane.getViewport().add(m_jEditorPane);

    m_jButtonBack.setIcon( new ImageIcon( Left.bytes));
    m_jButtonBack.setPreferredSize( new Dimension( 31,25));
    m_jButtonBack.setMaximumSize( new Dimension( 31,25));
    m_jButtonBack.setMinimumSize( new Dimension( 31,25));
    m_jButtonNext.setIcon( new ImageIcon( Right.bytes));
    m_jButtonNext.setPreferredSize( new Dimension( 31,25));
    m_jButtonNext.setMaximumSize( new Dimension( 31,25));
    m_jButtonNext.setMinimumSize( new Dimension( 31,25));
    m_jButtonHome.setIcon( new ImageIcon( Home.bytes));
    m_jButtonHome.setPreferredSize( new Dimension( 31,25));
    m_jButtonHome.setMaximumSize( new Dimension( 31,25));
    m_jButtonHome.setMinimumSize( new Dimension( 31,25));
    m_jScrollPane.getViewport().setScrollMode( JViewport.BACKINGSTORE_SCROLL_MODE );
  }

  public void setSearchDir( String strSearchDir)
  {
    m_strSearchDir = strSearchDir;
  }

  public String getSearchDir()
  {
    return m_strSearchDir;
  }

  public void setToolbarVisible( boolean bl)
  {
    m_jToolBar.setVisible( bl);
  }

  public void setSearchbarVisible( boolean bl)
  {
    m_jPanelSearch.setVisible( bl);
  }

  public void setHomeToolTipText( String str)
  {
    m_jButtonHome.setToolTipText(str);
  }

  public void setBackToolTipText( String str)
  {
    m_jButtonBack.setToolTipText(str);
  }

  public void setNextToolTipText( String str)
  {
    m_jButtonNext.setToolTipText(str);
  }

  /**
   * Sets the current url being displayed.  The content type of the
   * pane is set, and if the editor kit for the pane is non-null, then
   * a new default document is created and the URL is read into it.
   *
   * @param page the URL of the page
   * @exception IOException for a null or invalid page specification
   */
  public void setPage(URL page, HyperlinkEvent hev)
  {
    try
    {
      if( hev != null && hev instanceof HTMLFrameHyperlinkEvent)
      {
        JEditorPane pane = (JEditorPane)hev.getSource();
        HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)hev;
        HTMLDocument hdoc = (HTMLDocument)pane.getDocument();
        hdoc.processHTMLFrameHyperlinkEvent(evt);
      }
      else
      {
        if( page == null && hev != null)
          page = hev.getURL();
        m_jEditorPane.setPage( page);
      }
      m_iCurHist++;
      History hist = new History( page, hev);
      hist.setHistory();
    }
    catch (IOException e)
    {
      System.out.println("IOException: " + e);
    }
  }

  /**
   * Sets the current url being displayed.  The content type of the
   * pane is set, and if the editor kit for the pane is non-null, then
   * a new default document is created and the URL is read into it.
   *
   * @param page String representation of the URL of the page
   */
  public void setPage(String page)
  {
    URL url = null;
    try
    {
      url = new URL(page);
    }
    catch (MalformedURLException e)
    {
      System.out.println("Malformed URL: " + e);
    }
    setPage( url, null);
  }

  /**
   * Gets the current url being displayed.  If a URL was
   * not specified in the creation of the document, this
   * will return null, and relative URL's will not be
   * resolved.
   *
   * @return the URL
   */
  public URL getPage()
  {
    return m_jEditorPane.getPage();
  }

  protected void onSearch()
  {
    CancelDlg cdlg = new CancelDlg( null, "Searching ...", null, m_strAppName);
    if( m_strSearchDir != null && !m_strSearchDir.equals( ""))
    {
      Runnable runner = new Runnable()
      {
        public void run()
        {
          Vector vecFiles;
          String strSearch = m_jTextFieldSearch.getText();
          MultiSearch ms = new MultiSearch( strSearch);
          boolean blCase = m_jCheckBoxCase.isSelected();
          try
          {
            BufferedWriter bw = new BufferedWriter( new FileWriter( m_strSearchDir + "/searchResult.html"));

            if( blCase)
              vecFiles = ms.checkInDirAndSubDir( m_strSearchDir);
            else
              vecFiles = ms.checkInDirAndSubDirIgnoreCase( m_strSearchDir);
            bw.write( "<html><head><title>Search Result</title></head>");
            bw.newLine();
            bw.write( "<body><h2>Search: " + strSearch + "</h2>");
            bw.newLine();
            for( int i = 0; i < vecFiles.size(); i++)
            {
              File fi = new File( (String)vecFiles.elementAt( i));
              URL urlFile = fi.toURL();
              bw.write( "<a href=" + urlFile.toString() + ">" + fi.getName() + "</a><br>");
              bw.newLine();
            }
            bw.write( "</body>");
            bw.newLine();
            bw.write( "</html>");
            bw.newLine();
            bw.close();
            setPage( new URL( new URL( "file:"), m_strSearchDir + "/searchResult.html"), null);
          }
          catch( IOException ioex)
          {
            ioex.printStackTrace();
          }
        }
      };
      cdlg.setRunner( runner);
      cdlg.start();
      cdlg.dispose();
    }
  }

  protected void onBack()
  {
    if( m_iCurHist <= 0)
      return;
    Cursor c = m_jEditorPane.getCursor();
    Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    m_iCurHist--;
    History hist = null;
    if( m_iCurHist < m_vectorHist.size())
    {
      hist = (History)m_vectorHist.elementAt( m_iCurHist);
      m_iCurHist--;
    }
    m_jEditorPane = new javax.swing.JEditorPane();
    m_jEditorPane.setEditable(false);
    m_jScrollPane.getViewport().add(m_jEditorPane);
    m_jEditorPane.addHyperlinkListener(m_hypAction);
    /* excluded because of a bug
    if( hist.getHev() == null)
    {
      m_jEditorPane = new javax.swing.JEditorPane();
      m_jEditorPane.setEditable(false);
      m_jScrollPane.getViewport().add(m_jEditorPane);
      m_jEditorPane.addHyperlinkListener(this);
    }
    else
    {
      m_jEditorPane = (JEditorPane)hist.getHev().getSource();
    }
    */
    m_jEditorPane.setCursor(waitCursor);
    if( m_pl == null)
      m_pl = new PageLoader(hist, c);
    else
    {
      m_pl.stopThread();
      m_pl = new PageLoader(hist, c);
    }
    SwingUtilities.invokeLater(m_pl);
  }

  protected void onNext()
  {
    if( m_iCurHist+1 >= m_vectorHist.size())
      return;
    Cursor c = m_jEditorPane.getCursor();
    Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    History hist = null;
    if( (m_iCurHist+1) < m_vectorHist.size())
    {
      hist = (History)m_vectorHist.elementAt( m_iCurHist+1);
    }
    m_jEditorPane = new javax.swing.JEditorPane();
    m_jEditorPane.setEditable(false);
    m_jScrollPane.getViewport().add(m_jEditorPane);
    m_jEditorPane.addHyperlinkListener(m_hypAction);
    /* excluded because of a bug
    if( hist.getHev() == null)
    {
      m_jEditorPane = new javax.swing.JEditorPane();
      m_jEditorPane.setEditable(false);
      m_jScrollPane.getViewport().add(m_jEditorPane);
      m_jEditorPane.addHyperlinkListener(this);
    }
    else
    {
      m_jEditorPane = (JEditorPane)hist.getHev().getSource();
    }
    */
    m_jEditorPane.setCursor(waitCursor);
    if( m_pl == null)
      m_pl = new PageLoader(hist, c);
    else
    {
      m_pl.stopThread();
      m_pl = new PageLoader(hist, c);
    }
    SwingUtilities.invokeLater(m_pl);
  }

  protected void onHome()
  {
    if( m_iCurHist == 0)
      return;
    Cursor c = m_jEditorPane.getCursor();
    Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    m_iCurHist=0;
    History hist = null;
    if( m_iCurHist < m_vectorHist.size())
    {
      hist = (History)m_vectorHist.elementAt( m_iCurHist);
      m_iCurHist--;
    }
    if( hist.getHev() == null)
    {
      m_jEditorPane = new javax.swing.JEditorPane();
      m_jEditorPane.setEditable(false);
      m_jScrollPane.getViewport().add(m_jEditorPane);
      m_jEditorPane.addHyperlinkListener(m_hypAction);
    }
    else
    {
      m_jEditorPane = (JEditorPane)hist.getHev().getSource();
    }
    m_jEditorPane.setCursor(waitCursor);
    if( m_pl == null)
      m_pl = new PageLoader(hist, c);
    else
    {
      m_pl.stopThread();
      m_pl = new PageLoader(hist, c);
    }
    SwingUtilities.invokeLater(m_pl);
  }

  class HyperlinkAction
    implements HyperlinkListener
  {
    /**
     * Notification of a change relative to a
     * hyperlink.
     */
    public void hyperlinkUpdate( HyperlinkEvent e )
    {
      if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
      {
        linkActivated( e );
      }
    }

    /**
     * Follows the reference in an
     * link.  The given url is the requested reference.
     * By default this calls <a href="#setPage">setPage</a>,
     * and if an exception is thrown the original previous
     * document is restored and a beep sounded.  If an
     * attempt was made to follow a link, but it represented
     * a malformed url, this method will be called with a
     * null argument.
     *
     * @param u the URL to follow
     */
    protected void linkActivated( HyperlinkEvent e )
    {
      Cursor c = m_jEditorPane.getCursor();
      Cursor waitCursor = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
      m_jEditorPane.setCursor( waitCursor );
      if( m_pl == null )
        m_pl = new PageLoader( null, e, c );
      else
      {
        m_pl.stopThread();
        m_pl = new PageLoader( null, e, c );
      }
      SwingUtilities.invokeLater( m_pl );
    }
  }

  class AppAction
    implements ActionListener
  {
    public void actionPerformed( ActionEvent event )
    {
      String object = event.getActionCommand();
      if( object.equals( "Back" ) )
      {
        onBack();
      }
      else if( object.equals( "Next" ) )
      {
        onNext();
      }
      else if( object.equals( "Home" ) )
      {
        onHome();
      }
      else if( object.equals( "search" ) )
      {
        onSearch();
      }
    }
  }

  /**
   * History class
   */
  class History extends Object
  {
    URL m_url;
    HyperlinkEvent m_hev;

    public History()
    {
      m_url = null;
      m_hev = null;
    }

    public History( URL url, HyperlinkEvent hev)
    {
      m_url = url;
      m_hev = hev;
    }

    public URL getURL()
    {
      return m_url;
    }

    public HyperlinkEvent getHev()
    {
      return m_hev;
    }

    public void setHistory()
    {
      if( m_iCurHist < m_vectorHist.size())
      {
        m_vectorHist.setElementAt( this, m_iCurHist);
      }
      else
      {
        if( m_iCurHist > -1)
          m_vectorHist.addElement( this);
      }
    }
  }

  /**
   * temporary class that loads synchronously (although
   * later than the request so that a cursor change
   * can be done).
   */
  class PageLoader extends Thread
  {
    URL m_url;
    HyperlinkEvent m_HEv;
    Cursor m_cursor;
    boolean m_blStop = false;

    PageLoader( URL u, HyperlinkEvent e, Cursor c)
    {
      m_url = u;
      m_HEv = e;
      m_cursor = c;
    }

    public void setParams(URL u, HyperlinkEvent e, Cursor c)
    {
      m_url = u;
      m_HEv = e;
      m_cursor = c;
    }

    PageLoader( History hist, Cursor c)
    {
      m_url = hist.m_url;
      m_HEv = hist.m_hev;
      m_cursor = c;
    }

    public void setParams( History hist, Cursor c)
    {
      m_url = hist.m_url;
      m_HEv = hist.m_hev;
      m_cursor = c;
    }

    public void start()
    {
      m_blStop = false;
    }

    public void stopThread()
    {
      m_blStop = true;
    }

    public void run()
    {
      synchronized (getClass())
      {
        if( !m_blStop)
        {
          if (m_url == null && m_HEv == null)
          {
            // restore the original cursor
            m_jEditorPane.setCursor(m_cursor);

            // PENDING(prinz) remove this hack when
            // automatic validation is activated.
            Container parent = m_jEditorPane.getParent();
            if( parent != null)
              parent.repaint();
          }
          else
          {
            //Document doc = m_jEditorPane.getDocument();
            try
            {
              setPage(m_url, m_HEv);
            }
            /*						catch (IOException ioe)
            {
            m_jEditorPane.setDocument(doc);
            getToolkit().beep();
            }*/
            finally
            {
              // schedule the cursor to revert after
              // the paint has happended.
              m_url = null;
              m_HEv = null;
              SwingUtilities.invokeLater(this);
            }
          }
        }
      }
    }
  }

/*
  static public void main(String args[])
  {
    HtmlPanel htmlPanel = new HtmlPanel();
    final JFrame jFrame = new JFrame();

    jFrame.getContentPane().setLayout(new BorderLayout());
    jFrame.setTitle( "HtmlBrowser");
    jFrame.getContentPane().add("Center",htmlPanel);
    jFrame.setSize( 600, 800);

    //Window-Listener
    jFrame.addWindowListener(
      new java.awt.event.WindowAdapter()
      {
        public void windowClosing(java.awt.event.WindowEvent event)
        {
          jFrame.setVisible(false);
          jFrame.dispose();
          System.exit(0);
        }
      }
    );
    htmlPanel.setPage("file:///D:/Dev/source/Bean/ToolKit2/doc/index.html");
    jFrame.setVisible( true);
  }
*/
}

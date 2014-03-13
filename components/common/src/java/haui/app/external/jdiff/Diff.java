/*
 Copyright (C) 2002  Hatem El-Kazak
 This software is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This software is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 For any questions regarding this software contact hbelkazak@yahoo.com.
 Hatem El-Kazak, 6 April 2002
 */

package haui.app.external.jdiff;

import haui.app.external.jdiff.JLibDiff.Hunk;
import haui.app.external.jdiff.JLibDiff.diff;
import haui.components.JExDialog;
import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.util.AppProperties;
import haui.util.GlobalApplicationContext;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

/**
 * The diff application, responsible for differing 2 files, does the computations
 */
public class Diff
  extends JExDialog
{
  private static final long serialVersionUID = -3648628858396694335L;
  
  protected String m_strAppName;
  protected AppProperties m_appProps;

  private DiffToolBar toolBar = null;
  private DiffView view = new DiffView();
  private int[] diffs = null;
  private int currentDiff = 0;
  private int diffCount = 0;

  public Diff( Component parent, FileInterface serverFile, FileInterface localFile, boolean modal, String strAppName, AppProperties appProps )
  {
    super( parent, strAppName, modal, strAppName);
    m_strAppName = strAppName;
    m_appProps = appProps;
    //this.setTitle( "JDiff" );
    this.setJMenuBar( new DiffMenu( this, m_strAppName, m_appProps ) );
    toolBar = new DiffToolBar( this, m_strAppName, m_appProps );
    getContentPane().add( this.view, BorderLayout.CENTER );
    addWindowListener( new WindowAdapter()
    {
      public void windowClosing( WindowEvent we )
      {
        setVisible( false);
      }
    } );
    compareFiles( serverFile, localFile );

    // define status bar
    JPanel statusBar = new JPanel();
    statusBar.setLayout( new FlowLayout( FlowLayout.RIGHT, 0, 2 ) );

    JLabel added = new JLabel( " added " );
    added.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
    added.setForeground( CodePane.ADDED_LINE_COLOR );
    JLabel deleted = new JLabel( " deleted " );
    deleted.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
    deleted.setForeground( CodePane.DELETED_LINE_COLOR );
    JLabel modified = new JLabel( " modified " );
    modified.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
    modified.setForeground( CodePane.MODIFIED_LINE_COLOR );

    statusBar.add( added );
    statusBar.add( modified );
    statusBar.add( deleted );

    getContentPane().add( toolBar, BorderLayout.NORTH );
    getContentPane().add( statusBar, BorderLayout.SOUTH );
  }

  /**
   * compares the two given files, displays them in a DiffView and mark the
   * differences.
   */
  public void compareFiles( FileInterface serverFile, FileInterface localFile )
  {
    this.view.displayFiles( serverFile, localFile );
    markDifferences( diffFiles( serverFile, localFile ) );
    // init tool bar, must be done after files are compared.
    initToolBar();
  }

  /**
   * differentiates two files and returns a Vector containing differences (Hunks).
   * @return a Vector containing differences.
   */
  private Vector diffFiles( FileInterface serverFile, FileInterface localFile )
  {
    Vector v = null;
    try
    {
      diff d = new diff( serverFile, localFile);
      v = d.getHunk();

      diffCount = v.size();
      // diffs are based on the server panel count.
      diffs = new int[diffCount];
      Enumeration e = v.elements();
      int addedLines = 0;
      for( int i = 0; e.hasMoreElements(); i++ )
      {
        Hunk h = ( Hunk )e.nextElement();

        /*
            lines are computed according to their position on the screen
            not according to thier original place in the file they were
            read from.
         */
        switch( h.getType() )
        {
          case Hunk.ADDITION:
            diffs[i] = h.lowLine( 0 ) + addedLines + 1;
            addedLines += h.highLine( 1 ) - h.lowLine( 1 ) + 1;
            break;
          case Hunk.CHANGE:
            diffs[i] = h.lowLine( 0 ) + addedLines;
            int serverDiff = h.highLine( 0 ) - h.lowLine( 0 );
            int localDiff = h.highLine( 1 ) - h.lowLine( 1 );
            int totalDiff = localDiff - serverDiff;
            if( totalDiff > 0 )
              addedLines += totalDiff;
            break;
          case Hunk.DELETION:
            diffs[i] = h.lowLine( 0 ) + addedLines;
            break;
          default:
            System.out.println( "unknown Hunk type" );
        }
      }
    }
    catch( IOException ioe )
    {
      ioe.printStackTrace();
    }
    return v;
  }

  /**
   * compares the two text files and marks the differences on the DiffView's
   * panels.
   * @param differences a Vector containing differences between the two files.
   */
  private void markDifferences( Vector differences )
  {
    for( Enumeration e = differences.elements(); e.hasMoreElements(); )
    {
      Hunk h = ( Hunk )e.nextElement();
      switch( h.getType() )
      {
        case Hunk.ADDITION:
          view.markAdded( DiffView.LOCAL_VERSION_PANE, h.lowLine( 1 ), h.highLine( 1 ) );
          view.insertEmptyLines( DiffView.SERVER_VERSION_PANE, h.lowLine( 0 ),
                                 ( h.highLine( 1 ) - h.lowLine( 1 ) ) + 1 );
          break;
        case Hunk.CHANGE:
          view.markModified( DiffView.SERVER_VERSION_PANE, h.lowLine( 0 ), h.highLine( 0 ) );
          view.markModified( DiffView.LOCAL_VERSION_PANE, h.lowLine( 1 ), h.highLine( 1 ) );
          int serverDiff = h.highLine( 0 ) - h.lowLine( 0 );
          int localDiff = h.highLine( 1 ) - h.lowLine( 1 );
          if( serverDiff > localDiff )
          {
            view.insertEmptyLines( DiffView.LOCAL_VERSION_PANE, h.highLine( 1 ),
                                   serverDiff - localDiff );
          }
          else if( localDiff > serverDiff )
          {
            view.insertEmptyLines( DiffView.SERVER_VERSION_PANE, h.highLine( 0 ),
                                   localDiff - serverDiff );
          }
          break;
        case Hunk.DELETION:
          view.markDeleted( DiffView.SERVER_VERSION_PANE, h.lowLine( 0 ), h.highLine( 0 ) );
          view.insertEmptyLines( DiffView.LOCAL_VERSION_PANE, h.lowLine( 1 ),
                                 ( h.highLine( 0 ) - h.lowLine( 0 ) ) + 1 );
          break;
        default:
          System.out.println( "unknown Hunk type" );
      }
    }
  }

  /**
   *
   */
  public void markFirstDiff()
  {
    if( diffCount > 0 && currentDiff > 0 )
    {
      currentDiff = 0;
      view.markDiff( diffs[currentDiff] );
    }
    toolBar.firstDiff.setEnabled( false );
    toolBar.prevDiff.setEnabled( false );
    if( diffCount > 1 )
    {
      toolBar.nextDiff.setEnabled( true );
      toolBar.lastDiff.setEnabled( true );
    }
  }

  /**
   *
   */
  public void markPrevDiff()
  {
    if( diffCount > 0 && currentDiff > 0 )
      view.markDiff( diffs[--currentDiff] );
    if( currentDiff == 0 )
    {
      toolBar.firstDiff.setEnabled( false );
      toolBar.prevDiff.setEnabled( false );
    }
    if( diffCount > 1 )
    {
      toolBar.nextDiff.setEnabled( true );
      toolBar.lastDiff.setEnabled( true );
    }
  }

  /**
   *
   */
  public void gotoCurrentDiff()
  {
    if( diffCount > 0 )
      view.markDiff( diffs[currentDiff] );
  }

  /**
   *
   */
  public void markNextDiff()
  {
    if( diffCount > 0 && currentDiff < diffCount - 1 )
      view.markDiff( diffs[++currentDiff] );
    if( currentDiff == diffCount - 1 )
    {
      toolBar.nextDiff.setEnabled( false );
      toolBar.lastDiff.setEnabled( false );
    }
    if( diffCount > 1 )
    {
      toolBar.firstDiff.setEnabled( true );
      toolBar.prevDiff.setEnabled( true );
    }
  }

  /**
   *
   */
  public void markLastDiff()
  {
    if( diffCount > 0 && currentDiff < diffCount - 1 )
    {
      currentDiff = diffCount - 1;
      view.markDiff( diffs[currentDiff] );
    }
    toolBar.nextDiff.setEnabled( false );
    toolBar.lastDiff.setEnabled( false );
    if( diffCount > 1 )
    {
      toolBar.firstDiff.setEnabled( true );
      toolBar.prevDiff.setEnabled( true );
    }
  }

  private void initToolBar()
  {
    if( diffCount > 1 )
    {
      toolBar.firstDiff.setEnabled( false );
      toolBar.prevDiff.setEnabled( false );
    }
    else
    {
      toolBar.firstDiff.setEnabled( false );
      toolBar.prevDiff.setEnabled( false );
      toolBar.nextDiff.setEnabled( false );
      toolBar.lastDiff.setEnabled( false );

      if( diffCount != 1 )
        toolBar.currentDiff.setEnabled( false );

    }
  }

  /**
   * overrides same method in JFrame to allow the pointer to be set at
   * the first difference when the frame is initially shown.
   */
  public void setVisible( boolean flag )
  {
    super.setVisible( flag );
    if( currentDiff > 0)
      view.markDiff( diffs[currentDiff] );
  }

  /*
      private boolean firstTime = true;
      public void paint(java.awt.Graphics g)
      {
          super.paint(g);
          if(firstTime)
          {
              view.markDiff(diffs[currentDiff]);
              firstTime = false;
          }
      }
   */
  /**
   * overrides same method in JFrame to allow the pointer to be set at
   * the first difference when the frame is initially shown.
   */
  public void show()
  {
    super.show();
    if( currentDiff > 0)
      view.markDiff( diffs[currentDiff] );
  }

  /**
   * app entry point.
   */
  public static void main( String[] args )
  {
    /*
    try
    {
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    */

    String file1, file2;
    String strAppName = "jDiff";
    AppProperties appProps = new AppProperties();
    JFrame frame = new JFrame();
    GlobalApplicationContext.instance().addRootComponent(frame);
    if( args.length != 2 )
    {
      CompareDialog dlg = new CompareDialog( null, strAppName, appProps );
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = dlg.getSize();
      if( frameSize.height > screenSize.height )
      {
        frameSize.height = screenSize.height;
      }
      if( frameSize.width > screenSize.width )
      {
        frameSize.width = screenSize.width;
      }
      dlg.setLocation( ( screenSize.width - frameSize.width ) / 2,
                       ( screenSize.height - frameSize.height ) / 2 );
      dlg.show();

      if( dlg.getStatus() == CompareDialog.CANCEL )
        System.exit( 0 );

      file1 = dlg.getFile1();
      file2 = dlg.getFile2();

    }
    else
    {
      file1 = args[0];
      file2 = args[1];
    }

    FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, null, 0, 0,
        strAppName, appProps, true);
    FileInterface fi1 = FileConnector.createFileInterface( file1, null, false, fic);
    FileInterface fi2 = FileConnector.createFileInterface( file2, null, false, fic);

    Diff diff = new Diff( frame, fi1, fi2, true, strAppName, appProps );
    diff.setSize( 700, 500 );
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = diff.getSize();
    if( frameSize.height > screenSize.height )
    {
      frameSize.height = screenSize.height;
    }
    if( frameSize.width > screenSize.width )
    {
      frameSize.width = screenSize.width;
    }
    diff.setLocation( ( screenSize.width - frameSize.width ) / 2,
                      ( screenSize.height - frameSize.height ) / 2 );
    //diff.pack();
    diff.show();
    System.exit( 0 );
  }

  // static initializer for setting look & feel
  static
  {
    try
    {
      GlobalApplicationContext.instance().addApplicationClass(Class.forName( "haui.app.jdiff.Diff"));

      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch (Exception e) {}
  }
}
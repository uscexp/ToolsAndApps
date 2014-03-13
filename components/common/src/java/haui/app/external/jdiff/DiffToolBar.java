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

import haui.io.FileConnector;
import haui.io.FileInterface.FileInterface;
import haui.io.FileInterface.configuration.FileInterfaceConfiguration;
import haui.resource.ResouceManager;
import haui.util.AppProperties;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

/**
 * JDiff's tool bar.
 */
public class DiffToolBar
  extends JToolBar
{
  private static final long serialVersionUID = 7498601780688755898L;
  
  protected String m_strAppName;
  protected AppProperties m_appProps;

  private Diff parent = null;
  private JButton compare = new JButton();
  JButton nextDiff = new JButton();
  JButton lastDiff = new JButton();
  JButton firstDiff = new JButton();
  JButton prevDiff = new JButton();
  JButton currentDiff = new JButton();
  private JButton help = new JButton();

  public DiffToolBar( Diff theParent, String strAppName, AppProperties appProps )
  {
    this.parent = theParent;
    m_strAppName = strAppName;
    m_appProps = appProps;
    setFloatable( false );

    compare.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "compare.gif") );
    compare.setToolTipText( "Compare");
    compare.setFocusPainted( false );
    compare.setMaximumSize( new java.awt.Dimension( 18, 18 ) );
    compare.setMinimumSize( new java.awt.Dimension( 18, 18 ) );
    compare.setPreferredSize( new java.awt.Dimension( 18, 18 ) );
    compare.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        CompareDialog dlg = new CompareDialog( parent, m_strAppName, m_appProps );
        centerWindow( dlg );
        dlg.show();
        if( dlg.getStatus() == CompareDialog.OK )
        {
          String file1 = dlg.getFile1();
          String file2 = dlg.getFile2();
          FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, null, 0, 0,
              m_strAppName, m_appProps, true);
          FileInterface fi1 = FileConnector.createFileInterface( file1, null, false, fic);
          FileInterface fi2 = FileConnector.createFileInterface( file2, null, false, fic);
          parent.compareFiles( fi1, fi2 );
        }
        // repaint the area behind the closed dialog.
        parent.repaint();
      }
    } );

    firstDiff.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "first.gif" ) );
    firstDiff.setToolTipText( "First diff");
    firstDiff.setFocusPainted( false );
    firstDiff.setMaximumSize( new java.awt.Dimension( 18, 18 ) );
    firstDiff.setMinimumSize( new java.awt.Dimension( 18, 18 ) );
    firstDiff.setPreferredSize( new java.awt.Dimension( 18, 18 ) );
    firstDiff.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        parent.markFirstDiff();
      }
    } );

    prevDiff.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "prev.gif" ) );
    prevDiff.setToolTipText( "Previous diff");
    prevDiff.setFocusPainted( false );
    prevDiff.setMaximumSize( new java.awt.Dimension( 18, 18 ) );
    prevDiff.setMinimumSize( new java.awt.Dimension( 18, 18 ) );
    prevDiff.setPreferredSize( new java.awt.Dimension( 18, 18 ) );
    prevDiff.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        parent.markPrevDiff();
      }
    } );

    currentDiff.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "current.gif" ) );
    currentDiff.setToolTipText( "Current diff");
    currentDiff.setFocusPainted( false );
    currentDiff.setMaximumSize( new java.awt.Dimension( 18, 18 ) );
    currentDiff.setMinimumSize( new java.awt.Dimension( 18, 18 ) );
    currentDiff.setPreferredSize( new java.awt.Dimension( 18, 18 ) );
    currentDiff.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        parent.gotoCurrentDiff();
      }
    } );

    nextDiff.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "next.gif" ) );
    nextDiff.setToolTipText( "Next diff");
    nextDiff.setFocusPainted( false );
    nextDiff.setMaximumSize( new java.awt.Dimension( 18, 18 ) );
    nextDiff.setMinimumSize( new java.awt.Dimension( 18, 18 ) );
    nextDiff.setPreferredSize( new java.awt.Dimension( 18, 18 ) );
    nextDiff.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        parent.markNextDiff();
      }
    } );

    lastDiff.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "last.gif" ) );
    lastDiff.setToolTipText( "Last diff");
    lastDiff.setFocusPainted( false );
    lastDiff.setMaximumSize( new java.awt.Dimension( 18, 18 ) );
    lastDiff.setMinimumSize( new java.awt.Dimension( 18, 18 ) );
    lastDiff.setPreferredSize( new java.awt.Dimension( 18, 18 ) );
    lastDiff.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        parent.markLastDiff();
      }
    } );

    help.setIcon( ResouceManager.getCommonImageIcon( m_strAppName, "help.gif" ) );
    help.setToolTipText( "Help");
    help.setFocusPainted( false );
    help.setMaximumSize( new java.awt.Dimension( 18, 18 ) );
    help.setMinimumSize( new java.awt.Dimension( 18, 18 ) );
    help.setPreferredSize( new java.awt.Dimension( 18, 18 ) );
    help.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        centerWindow( new AboutJDiff( parent, m_strAppName ) ).show();
        // repaint the area behind the closed dialog.
        parent.repaint();
      }
    } );

    add( compare );
    addSeparator();
    add( firstDiff );
    add( prevDiff );
    add( currentDiff );
    add( nextDiff );
    add( lastDiff );
    addSeparator();
    add( help );
  }

  /**
   * centers the given window with respect to the screen.
   */
  private Window centerWindow( Window window )
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = window.getSize();
    if( frameSize.height > screenSize.height )
    {
      frameSize.height = screenSize.height;
    }
    if( frameSize.width > screenSize.width )
    {
      frameSize.width = screenSize.width;
    }
    window.setLocation( ( screenSize.width - frameSize.width ) / 2,
                        ( screenSize.height - frameSize.height ) / 2 );
    return window;
  }

  public static void main( String[] args )
  {
    String strAppName = "jDiff";
    AppProperties appProps = new AppProperties();
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.getContentPane().add( new DiffToolBar( null, strAppName, appProps ), BorderLayout.NORTH );
    frame.setSize( 400, 300 );
    frame.setVisible( true );
  }
}
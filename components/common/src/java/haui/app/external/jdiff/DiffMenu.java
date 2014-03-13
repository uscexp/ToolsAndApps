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
import haui.util.AppProperties;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * JDiff menu bar.
 */
public class DiffMenu
  extends JMenuBar
{
  private static final long serialVersionUID = 5344795516429163056L;
  
  protected String m_strAppName;
  protected AppProperties m_appProps;

  private Diff parent = null;

  private DiffMenuHandler menuHandler = new DiffMenuHandler();
  private JMenu FileMenu = new JMenu();
  private JMenu HelpMenu = new JMenu();
  private JMenuItem menuItem2 = new JMenuItem();
  private JMenuItem menuItem5 = new JMenuItem();
  private JMenuItem menuItem6 = new JMenuItem();
  private JMenuItem menuItem7 = new JMenuItem();
  private JMenuItem menuItem16 = new JMenuItem();
  private JMenuItem menuItem17 = new JMenuItem();
  private JMenuItem menuItem18 = new JMenuItem();
  private JMenuItem menuItem19 = new JMenuItem();

  /**
   * constructor.
   */
  public DiffMenu( Diff parent, String strAppName, AppProperties appProps )
  {
    this.parent = parent;
    m_strAppName = strAppName;
    m_appProps = appProps;
    try
    {
      jbInit();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  /**
   * initializes this instance of DiffMenu
   */
  private void jbInit()
    throws Exception
  {
    FileMenu.setText( "File" );
    HelpMenu.setText( "Help" );

    this.add( FileMenu );
    this.add( HelpMenu );

    setupMenuItem( menuItem2, "Compare...", "FILE_COMPARE", menuHandler );
    setupMenuItem( menuItem5, "Print", "FILE_PRINT", menuHandler );
    setupMenuItem( menuItem6, "Print Setup...", "FILE_PRINTSETUP", menuHandler );
    setupMenuItem( menuItem7, "Exit", "FILE_EXIT", menuHandler );
    setupMenuItem( menuItem16, "Contents", "HELP_CONTENTS", menuHandler );
    setupMenuItem( menuItem17, "Search for help on", "HELP_SEARCHFORHELPON", menuHandler );
    setupMenuItem( menuItem18, "How to use help", "HELP_HOWTOUSEHELP", menuHandler );
    setupMenuItem( menuItem19, "About", "HELP_ABOUT", menuHandler );

    FileMenu.add( menuItem2 );
    FileMenu.addSeparator();
    FileMenu.add( menuItem5 );
    FileMenu.add( menuItem6 );
    FileMenu.addSeparator();
    FileMenu.add( menuItem7 );
    HelpMenu.add( menuItem16 );
    HelpMenu.add( menuItem17 );
    HelpMenu.add( menuItem18 );
    HelpMenu.add( menuItem19 );

    menuItem5.setEnabled( false );
    menuItem6.setEnabled( false );
    menuItem16.setEnabled( false );
    menuItem17.setEnabled( false );
    menuItem18.setEnabled( false );
  }

  /**
   * setups a menu item. ie: puts the display string, action command,
   */
  private void setupMenuItem( JMenuItem menuItem, String display, String actionCommand,
                              ActionListener aListener )
  {
    // add menu handler to menu items.
    menuItem.setText( display );
    menuItem.setActionCommand( actionCommand );
    menuItem.addActionListener( aListener );
  }

  /**
   * contains the actions to be performed for each menu item.
   */
  private class DiffMenuHandler
    implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      String actionCommand = ae.getActionCommand();
      if( actionCommand == null )
        return;

      if( actionCommand.equals( "FILE_COMPARE" ) )
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
      else if( actionCommand.equals( "FILE_EXIT" ) )
      {
        parent.setVisible( false);
      }
      else if( actionCommand.equals( "HELP_ABOUT" ) )
      {
        centerWindow( new AboutJDiff( parent, m_strAppName ) ).show();
        // repaint the area behind the closed dialog.
        parent.repaint();
      }
    }
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
}
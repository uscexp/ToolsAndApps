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

import haui.io.FileInterface.FileInterface;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * the main diff UI that will show the source code for both local and server's revisions and thier differences.
 */
public class DiffView
  extends JPanel
{
  private static final long serialVersionUID = 5825431403843619145L;
  
  public static final int SERVER_VERSION_PANE = 0;
  public static final int LOCAL_VERSION_PANE = 1;
  private CodePane serverVersion = new CodePane();
  private CodePane localVersion = new CodePane();
  private JPanel serverPanel = new JPanel( new BorderLayout() );
  private JPanel localPanel = new JPanel( new BorderLayout() );
  private JLabel serverFileNameLabel = new JLabel( "server" );
  private JLabel localFileNameLabel = new JLabel( "local" );
  private JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true )
  {
    private static final long serialVersionUID = 5825431403843619144L;
    
    // fixing new behaviour with JDK 1.4 (divider isnot positioned at the appropriate position)
    protected void paintChildren( Graphics g )
    {
      super.paintChildren( g );
      if( getLastDividerLocation() == -1 )
        revalidate();
    }
  };

  /**
   * default constructor
   */
  public DiffView()
  {
    serverPanel.add( serverFileNameLabel, BorderLayout.NORTH );
    serverPanel.add( serverVersion, BorderLayout.CENTER );
    localPanel.add( localFileNameLabel, BorderLayout.NORTH );
    localPanel.add( localVersion, BorderLayout.CENTER );
    splitPane.setLeftComponent( serverPanel );
    splitPane.setRightComponent( localPanel );

    setLayout( new GridLayout() );
    add( splitPane );
    splitPane.setOneTouchExpandable( false );
    splitPane.setDividerLocation( 0.5 );
    splitPane.setResizeWeight( 0.5 );
    localVersion.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
    localVersion.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
    serverVersion.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
    serverVersion.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );

    tieScrolls();
  }

  /**
   * a convinience constructor that take the paths of the two files to be compared.
   * @param serverCopy the path of the file containing the server copy.
   * @param localCopy the path of the file containing the local copy.
   */
  public DiffView( FileInterface serverFile, FileInterface localFile )
  {
    this();
    displayFiles( serverFile, localFile );
  }

  /**
   * diplays the files in both text panes.
   */
  public void displayFiles( FileInterface serverFile, FileInterface localFile )
  {
    try
    {
      serverVersion.displayFile( serverFile );
      serverFileNameLabel.setText( " " + serverFile );
      localVersion.displayFile( localFile );
      localFileNameLabel.setText( " " + localFile );
    }
    catch( IOException ioe )
    {
      ioe.printStackTrace();
    }
  }

  /**
   * ties the scrolls of both CodePanes, so that they move together.
   */
  private void tieScrolls()
  {
    // tie vertical ScrollBars
    localVersion.getVerticalScrollBar().getModel().addChangeListener( new ChangeListener()
    {
      public void stateChanged( ChangeEvent ce )
      {
        int val = localVersion.getVerticalScrollBar().getModel().getValue();
        serverVersion.getVerticalScrollBar().getModel().setValue( val );
      }
    } );
    serverVersion.getVerticalScrollBar().getModel().addChangeListener( new ChangeListener()
    {
      public void stateChanged( ChangeEvent ce )
      {
        int val = serverVersion.getVerticalScrollBar().getModel().getValue();
        localVersion.getVerticalScrollBar().getModel().setValue( val );
      }
    } );
    // tie horizontal ScrollBars
    localVersion.getHorizontalScrollBar().getModel().addChangeListener( new ChangeListener()
    {
      public void stateChanged( ChangeEvent ce )
      {
        int val = localVersion.getHorizontalScrollBar().getModel().getValue();
        serverVersion.getHorizontalScrollBar().getModel().setValue( val );
      }
    } );
    serverVersion.getHorizontalScrollBar().getModel().addChangeListener( new ChangeListener()
    {
      public void stateChanged( ChangeEvent ce )
      {
        int val = serverVersion.getHorizontalScrollBar().getModel().getValue();
        localVersion.getHorizontalScrollBar().getModel().setValue( val );
      }
    } );
  }

  /**
   * marks the lines starting from startLine to endLine (inclusive) as deleted.
   * Line numbers are starts from 1.
   * @param pane the desired pane to be marked.
   * @param startLine the line from which code will be marked.
   * @param endLine the line to which code will be marked.
   */
  public void markDeleted( int pane, int startLine, int endLine )
  {
    switch( pane )
    {
      case DiffView.SERVER_VERSION_PANE:
        serverVersion.markDeleted( startLine, endLine );
        break;
      case DiffView.LOCAL_VERSION_PANE:
        localVersion.markDeleted( startLine, endLine );
        break;
      default:
        System.out.println( "illegal value for parameter pane" );
    }
  }

  /**
   * marks the lines starting from startLine to endLine (inclusive) as added.
   * Line numbers are starts from 1.
   * @param pane the desired pane to be marked.
   * @param startLine the line from which code will be marked.
   * @param endLine the line to which code will be marked.
   */
  public void markAdded( int pane, int startLine, int endLine )
  {
    switch( pane )
    {
      case DiffView.SERVER_VERSION_PANE:
        serverVersion.markAdded( startLine, endLine );
        break;
      case DiffView.LOCAL_VERSION_PANE:
        localVersion.markAdded( startLine, endLine );
        break;
      default:
        System.out.println( "illegal value for parameter pane" );
    }
  }

  /**
   * marks the lines starting from startLine to endLine (inclusive) as modified.
   * Line numbers are starts from 1.
   * @param pane the desired pane to be marked.
   * @param startLine the line from which code will be marked.
   * @param endLine the line to which code will be marked.
   */
  public void markModified( int pane, int startLine, int endLine )
  {
    switch( pane )
    {
      case DiffView.SERVER_VERSION_PANE:
        serverVersion.markModified( startLine, endLine );
        break;
      case DiffView.LOCAL_VERSION_PANE:
        localVersion.markModified( startLine, endLine );
        break;
      default:
        System.out.println( "illegal value for parameter pane" );
    }
  }

  /**
   * inserts an empty line after the specified line.
   * Line numbers are starts from 1, line number of zero is used to add an
   * empty line at the begining of the document.
   * @param pane the desired pane to be marked.
   * @param lineNumber the line after which an empty line will be inserted.
   * @param numberOfLines the number of lines to insert.
   */
  public void insertEmptyLines( int pane, int lineNumber, int numberOfLines )
  {
    switch( pane )
    {
      case DiffView.SERVER_VERSION_PANE:
        serverVersion.insertEmptyLines( lineNumber, numberOfLines );
        break;
      case DiffView.LOCAL_VERSION_PANE:
        localVersion.insertEmptyLines( lineNumber, numberOfLines );
        break;
      default:
        System.out.println( "illegal value for parameter pane" );
    }
  }

  /**
   *
   */
  public void markDiff( int lineNumber )
  {
    // only one CodePane needs to be scrolled, the other is tied to it,
    // and is automatically scrolled to the same postion.
    serverVersion.markDiff( lineNumber );
  }
}
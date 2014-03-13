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
import haui.util.GlobalApplicationContext;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * a pane that displays the code in a certain file and can annotate specific lines.
 */
public class CodePane
  extends JScrollPane
{
  private static final long serialVersionUID = 9166889639300863073L;

  /**
   * forground colors for added lines.
   */
  public static Color ADDED_LINE_COLOR = new Color( 0, 130, 0 );

  /**
   * forground colors for deleted lines.
   */
  public static Color DELETED_LINE_COLOR = new Color( 255, 0, 0 );

  /**
   * forground colors for modified lines.
   */
  public static Color MODIFIED_LINE_COLOR = new Color( 0, 0, 255 );

  private JTextPane textPane = new JTextPane()
  {
    private static final long serialVersionUID = 9166889639300863072L;

    public EditorKit getEditorKitForContentType( String type )
    {
      return createDefaultEditorKit();
    }
  };

  /**
   * this panel is used to fix the wrapping problem with JTextPane, don't know
   * how though. sun should really work on this strange behaviour.
   */
  private JPanel antiWrapPanel = new JPanel();

  /**
   * first element is the startoffset for each line, and the second one is the
   * endoffset for the same line.
   */
  private int[][] lines = null;

  /**
   *
   */
  private LineNumberBorder numberBorder = null;

  /**
   * responsible for highlighting lines.
   */
  private DefaultHighlighter lineHighlighter = new DefaultHighlighter()
  {
    public void paint( Graphics g )
    {
      JTextComponent component = textPane;
      Rectangle visibleArea = component.getVisibleRect();

      // draw all highlights that exist within the JTextComponent's visible area
      Highlighter.Highlight[] highlights = lineHighlighter.getHighlights();
      for( int i = 0; i < highlights.length; i++ )
      {
        int p0 = highlights[i].getStartOffset();
        int p1 = highlights[i].getEndOffset();

        try
        {
          Rectangle bounds = component.modelToView( p0 );
          bounds.width = visibleArea.width;

          // if inside visible area
          if( ( bounds.y + bounds.height ) > visibleArea.y
              && bounds.y < visibleArea.y + visibleArea.height )
            highlights[i].getPainter().paint( g, p0, p1, bounds, component );
        }
        catch( BadLocationException e )
        {
          e.printStackTrace();
        }
      }
    }
  };
  private FullLineHighlightPainter painter = new FullLineHighlightPainter( Color.lightGray );

  /**
   * default constructor.
   */
  public CodePane()
  {
    textPane.setEditable( false );
    antiWrapPanel.setLayout( new GridLayout() );
    antiWrapPanel.add( textPane );
    setViewportView( antiWrapPanel );
    getVerticalScrollBar().setUnitIncrement( 10 );
    getHorizontalScrollBar().setUnitIncrement( 10 );

    // repaint component when vertical scroll bar moves, to update border
    getVerticalScrollBar().addAdjustmentListener( new AdjustmentListener()
    {
      public void adjustmentValueChanged( AdjustmentEvent e )
      {
        repaint( getVisibleRect() );
      }
    } );

    // fix selection disorders
    textPane.addMouseMotionListener( new MouseMotionAdapter()
    {
      public void mouseDragged( MouseEvent e )
      {
        textPane.repaint( textPane.getVisibleRect() );
      }
    } );

    textPane.addMouseListener( new MouseAdapter()
    {
      public void mousePressed( MouseEvent e )
      {
        textPane.repaint( textPane.getVisibleRect() );
      }
    } );

    // set line highlighter.
//        lineHighlighter.setDrawsLayeredHighlights(false);
    textPane.setHighlighter( lineHighlighter );
  }

  /**
   * marks a code range as added from this panel, line numbers are inclusive.
   * First line is 1.
   */
  public void markAdded( int startLine, int endLine )
  {
    // modify to fit 0 based Vector.
    startLine--;
    endLine--;

    // the existance of this loop is necessary as not to color empty lines in the way
    for( int i = startLine; i <= endLine; i++ )
    {
      MutableAttributeSet attrs = new SimpleAttributeSet();
      StyleConstants.setForeground( attrs, CodePane.ADDED_LINE_COLOR );
      textPane.getStyledDocument().setCharacterAttributes( lines[i][0], lines[i][1] - lines[i][0],
        attrs, true );
      highlightLine( lines[i][0], 1 );
    }
  }

  /**
   * marks a code range as deleted from this panel, line numbers are inclusive.
   * First line is 1.
   */
  public void markDeleted( int startLine, int endLine )
  {
    // modify to fit 0 based Vector.
    startLine--;
    endLine--;

    for( int i = startLine; i <= endLine; i++ )
    {
      MutableAttributeSet attrs = new SimpleAttributeSet();
      StyleConstants.setForeground( attrs, CodePane.DELETED_LINE_COLOR );
      textPane.getStyledDocument().setCharacterAttributes( lines[i][0], lines[i][1] - lines[i][0],
        attrs, true );
      highlightLine( lines[i][0], 1 );
    }
  }

  /**
   * marks a code range as modified in this panel, line numbers are inclusive.
   * First line is 1.
   */
  public void markModified( int startLine, int endLine )
  {
    // modify to fit 0 based Vector.
    startLine--;
    endLine--;

    for( int i = startLine; i <= endLine; i++ )
    {
      SimpleAttributeSet attrs = new SimpleAttributeSet();
      StyleConstants.setForeground( attrs, CodePane.MODIFIED_LINE_COLOR );
      textPane.getStyledDocument().setCharacterAttributes( lines[i][0], lines[i][1] - lines[i][0],
        attrs, true );
      highlightLine( lines[i][0], 1 );
    }
  }

  /**
   * inserts an empty line.
   * use zero to insert a line at the beginning of a document.
   * @param lineNumber the line number in the original text file to insert
   * an empty line after.
   * @param numberOfLines the number of lines to insert.
   */
  public void insertEmptyLines( int lineNumber, int numberOfLines )
  {
    // modify to match with a zero based array index.
    int index = lineNumber - 1;

    StyledDocument styledDoc = textPane.getStyledDocument();

    String breaks = "";
    try
    {
      for( int i = 0; i < numberOfLines; i++ )
        breaks += "\n";

        // see if empty lines are to be inserted before first text line.
      if( lineNumber == 0 )
      {
        styledDoc.insertString( 0, breaks, null );
        highlightLine( 0, numberOfLines );
      }
      // see if it is last line.
      else if( lineNumber == lines.length )
      {
        styledDoc.insertString( lines[index][1] - 1, breaks, null );
        styledDoc.setCharacterAttributes( styledDoc.getLength(), 1, new SimpleAttributeSet(), true );
        highlightLine( lines[index][1] - 1, numberOfLines );
      }
      // if not last line.
      else
      {
        styledDoc.insertString( lines[index][1], breaks, null );
        highlightLine( lines[index][1], numberOfLines );
      }

      // shift lines
      // lineNumber is used without modifications because we need to shift
      // locations for the following lines but not the current one (after all
      // an empty is inserted after the given lineNumber not before it).
      for( int j = lineNumber; j < lines.length; j++ )
      {
        lines[j][0] += numberOfLines;
        lines[j][1] += numberOfLines;
      }
    }
    catch( BadLocationException ble )
    {
      ble.printStackTrace();
    }
  }

  /**
   * highlights the line that have the given startingOffset.
   */
  private void highlightLine( int startingOffset, int numberOfLines )
  {
    try
    {
      for( int i = startingOffset; i < startingOffset + numberOfLines; i++ )
        lineHighlighter.addHighlight( i, i + 1, painter );
    }
    catch( BadLocationException ble )
    {
      ble.printStackTrace();
    }
  }

  /**
   * displays a file in this panel
   * @param fileName the name of a local file to be displayed.
   */
  public void displayFile( FileInterface file )
    throws IOException
  {
    this.lineHighlighter.removeAllHighlights();
    // border is set here so its width is calculated with each new displayed file.
    numberBorder = new LineNumberBorder( getBackground() );
    this.setBorder( numberBorder );
    //textPane.read( file.getBufferedInputStream(), "text/plain");
    textPane.read( file.getBufferedInputStream(), null);

    DefaultStyledDocument styledDoc = ( DefaultStyledDocument )textPane.getStyledDocument();
    Element rootElem = styledDoc.getDefaultRootElement();
    int elementCount = rootElem.getElementCount();
    Vector intermidiateLines = new Vector();

    for( int i = 0; i < elementCount; i++ )
    {
      Element elm = rootElem.getElement( i );
      if( !elm.isLeaf() )
      {
        Element e = elm.getElement( 0 );
        int[] offsets =
                        {e.getStartOffset(), e.getEndOffset()};
        intermidiateLines.add( offsets );
      }
    }
    lines = new int[intermidiateLines.size()][2];
    intermidiateLines.toArray( lines );
  }

  /**
   *
   */
  public void markDiff( int lineNumber )
  {
    int textPaneHeight = textPane.getHeight();
    int viewPortHeight = ( int )getViewport().getExtentSize().getHeight();

    Graphics g = textPane.getGraphics();
    FontMetrics fm = g.getFontMetrics();
    int lineHeight = fm.getLeading() + fm.getMaxAscent() + fm.getMaxDescent();
    int y = lineNumber * lineHeight - lineHeight;

    if( ( textPaneHeight - y ) > viewPortHeight )
      getViewport().setViewPosition( new Point( 0, y ) );
    else
      getViewport().setViewPosition( new Point( 0, textPaneHeight - viewPortHeight ) );

    if( numberBorder != null )
      numberBorder.setArrowPosition( lineNumber );
  }

  /**
   * A border that displays numbers for the line in the associated JTextPane numbers are displayed to the original line number in the text file.
   */
  private class LineNumberBorder
    extends MatteBorder
  {
    private static final long serialVersionUID = 9166889639300863071L;

    private int arrowPosition = 0;

    /**
     * constructor
     * @param borderColor the color of the border.
     */
    LineNumberBorder( Color borderColor )
    {
      super( 0, 0, 0, 0, borderColor );
    }

    /**
     * paints the border when ever it needs painting, line numbers are added in the process.
     * lines have numbers according to their arrangement in the text file they
     * were read from.
     */
    public void paintBorder( Component c, Graphics g, int x, int y, int width, int height )
    {
      // calculate border size
      if( left == 0 )
      {
        left = SwingUtilities.computeStringWidth( g.getFontMetrics(),
                                                  Integer.toString( lines.length ) ) + 15; // for decorations and spaces from both sides
        if( left < 30 )
          left = 30;
      }

      // paint border and customize it.
      super.paintBorder( c, g, x, y, width, height );
      g.setColor( Color.white );
      g.fillRect( left - 3, 0, 3, height );
      g.drawLine( left - 5, 0, left - 5, height );

      // prepare for drawing line numbers.
      StyledDocument styledDoc = textPane.getStyledDocument();
      Rectangle viewRectangle = getViewport().getViewRect();
      g.setFont( textPane.getGraphics().getFont() );
      FontMetrics fm = g.getFontMetrics();
      int leading = fm.getLeading();
      int maxAscent = fm.getMaxAscent();

      int startLineIndex = getLineNumber( textPane.viewToModel( new Point( viewRectangle.x,
        viewRectangle.y ) ) );
      int endLineIndex = getLineNumber( textPane.viewToModel( new Point( viewRectangle.x,
        viewRectangle.y + viewRectangle.height ) ) );

      for( int i = startLineIndex - 1; i <= endLineIndex && i < lines.length; i++ )
      {
        Element e = styledDoc.getCharacterElement( lines[i][0] );
        Color color = StyleConstants.getForeground( e.getAttributes() );

        Rectangle r = null;
        try
        {
          r = textPane.modelToView( lines[i][0] );
        }
        catch( BadLocationException ble )
        {
          ble.printStackTrace();
        }

        int ly = r.y + leading + maxAscent - viewRectangle.y;

        // draw line numbers
        g.setColor( color );
        g.drawString( Integer.toString( i + 1 ), 5, ly );
      }
      if( arrowPosition > 0 )
      {
        int maxDescent = fm.getMaxDescent();
        int lineHeight = leading + maxAscent + maxDescent;
        int y2 = lineHeight * arrowPosition - lineHeight - viewRectangle.y;
        g.clearRect( 0, y2, left - 5, lineHeight );
        g.setColor( Color.blue );
        g.fillRect( left - 25, y2 + 8, 12, 7 );
        int[] xPoints =
                        {left + 3 - 16, left + 3 - 16, left + 3 - 8};
        int[] yPoints =
                        {y2 + 0 + 3, y2 + 16 + 3, y2 + 8 + 3};
        g.fillPolygon( xPoints, yPoints, 3 );
      }
    }

    /**
     * gets the line number that should appear beside each line.
     * @return the line number that contains the given offset, or the first
     * line before it if the given offset doen't belong to a text line.
     */
    private int getLineNumber( int offset )
    {
      // if  offset >= last line and it is not an empty line, return
      // number of last line.
      if( offset >= lines[lines.length - 1][1] )
        return lines.length;

      // if offset lies within a normal text line.
      for( int i = 0; i < lines.length; i++ )
        if( offset >= lines[i][0] && offset <= lines[i][1] )
          return i + 1;

      // if offset lies in an emty line, return the number of the last
      // text line before this empty line.
      for( int j = lines.length - 1; j >= 0; j-- )
        if( offset >= lines[j][0] )
          return j;

      // if this part is reached it means that the line is an empty line
      // inserted before the first text line, so one is returned because
      // it is the first text line.
      return 1;
    }

    /**
     * @param position  the line number to put the arrow at, line numbers  start from 1.
     * @uml.property  name="arrowPosition"
     */
    public void setArrowPosition( int position )
    {
      this.arrowPosition = position;
    }
  }

  /**
   * for testing purposes.
   */
  public static void main( String[] args )
  {
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout( new BorderLayout() );
    frame.addWindowListener( new WindowAdapter()
    {
      public void windowClosing( WindowEvent we )
      {
        System.exit( 0 );
      }
    } );

    CodePane code = new CodePane();
    frame.getContentPane().add( code, BorderLayout.CENTER );
    try
    {
      AppProperties appProps = new AppProperties();
      FileInterfaceConfiguration fic = FileConnector.createFileInterfaceConfiguration( null, 0, null, null, 0, 0,
          "CodePane", appProps, true);
      FileInterface fi = FileConnector.createFileInterface( "d:/work/xvcs/classes/2.txt", null, false, fic);
      GlobalApplicationContext.instance().addRootComponent(frame);
      code.displayFile( fi);
    }
    catch( IOException ioe )
    {
      ioe.printStackTrace();
    }

    code.insertEmptyLines( 1, 1 );
    code.markModified( 25, 26 );
    code.markAdded( 10, 12 );
    code.markDeleted( 15, 18 );

    frame.setSize( 1000, 400 );
    frame.show();
  }
}
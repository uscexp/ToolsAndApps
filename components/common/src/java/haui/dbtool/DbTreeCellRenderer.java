package haui.dbtool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 *		Module:					DbTreeCellRenderer.java
 *<p>
 *		Description:		Database TreeCellRenderer class.
 *</p><p>
 *		Created:				07.07.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DbTreeCellRenderer extends JLabel implements TreeCellRenderer
{
  private static final long serialVersionUID = -7479741799879624982L;
  
    /** Font used if the string to be displayed isn't a font. */
  static protected Font             defaultFont;
    /** Icon to use when the item is collapsed. */
  static protected ImageIcon        collapsedIcon;
    /** Icon to use when the item is expanded. */
  static protected ImageIcon        expandedIcon;

    /** Color to use for the background when selected. */
  static protected final Color SelectedBackgroundColor = Color.blue;

  static
  {
    try
    {
      defaultFont = new Font("SansSerif", 0, 12);
    }
    catch (Exception e)
    {
    }
    try
    {
      collapsedIcon = new ImageIcon("collapsed.gif");
      expandedIcon = new ImageIcon("expanded.gif");
    }
    catch (Exception e)
    {
      System.err.println("Couldn't load images: " + e);
    }
  }

    /** Whether or not the item that was last configured is selected. */
  protected boolean            selected;

    /**
      * This is messaged from JTree whenever it needs to get the size
      * of the component or it wants to draw it.
      * This attempts to set the font based on value, which will be
      * a TreeNode.
      */
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
    boolean leaf, int row, boolean hasFocus)
  {
    //Font            font;
    String          stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);

      /* Set the text. */
    setText(stringValue);
      /* Tooltips used by the tree. */
    setToolTipText(stringValue);

      /* Set the image. */
    if(expanded)
      setIcon(expandedIcon);
    else
      if(!leaf)
        setIcon(collapsedIcon);
    else
      setIcon(null);

      /* Set the color and the font based on the DbData userObject. */
    DbData         userObject = (DbData)((DefaultMutableTreeNode)value).getUserObject();
    if(hasFocus)
      setForeground(Color.white);
    else
      setForeground(userObject.getColor());

    if(userObject.getFont() == null)
      setFont(defaultFont);
    else
      setFont(userObject.getFont());

      /* Update the selected flag for the next paint. */
    this.selected = selected;

    return this;
  }

    /**
      * paint is subclassed to draw the background correctly.  JLabel
      * currently does not allow backgrounds other than white, and it
      * will also fill behind the icon.  Something that isn't desirable.
      */
  public void paint(Graphics g)
  {
    Color            bColor;
    Icon             currentI = getIcon();

    if(selected)
      bColor = SelectedBackgroundColor;
    else
      if(getParent() != null)
              /* Pick background color up from parent (which will come from
                 the JTree we're contained in). */
        bColor = getParent().getBackground();
    else
      bColor = getBackground();

    g.setColor(bColor);
    if(currentI != null && getText() != null)
    {
      int          offset = (currentI.getIconWidth() + getIconTextGap());

      g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
    }
    else
      g.fillRect(0, 0, getWidth()-1, getHeight()-1);
    super.paint(g);
  }
}

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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.JTextComponent;
import javax.swing.text.View;

/**
 * A custom Highlighter that highlights a whole line, not just the text in the line.
 */
public class FullLineHighlightPainter extends javax.swing.text.DefaultHighlighter.DefaultHighlightPainter
{
    /**
     * constructor
     * @param c color to use for highlighting.
     */
    public FullLineHighlightPainter(Color c)
    {
        super(c);
    }

    /**
     *
     */
    public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c)
    {
      Color color = getColor();
      if (color == null)
        {
            g.setColor(c.getSelectionColor());
      }
      else
        {
            g.setColor(color);
      }

        // Contained in view, can just use bounds.
        Rectangle alloc;
        if (bounds instanceof Rectangle)
        {
            alloc = (Rectangle)bounds;
        }
        else
        {
            alloc = bounds.getBounds();
        }
        Rectangle area = c.getBounds();
        g.fillRect(alloc.x, alloc.y, area.width, alloc.height);
    }

    /**
     *
     */
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view)
    {
        return bounds;
    }
}
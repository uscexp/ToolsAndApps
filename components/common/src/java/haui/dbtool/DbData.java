package haui.dbtool;

import java.awt.Color;
import java.awt.Font;

/**
 * Module:					DbData.java <p> Description:		Database Object data class. </p><p> Created:				07.07.1998	by	AE </p><p> Last Modified:	15.04.1999	by	AE </p><p> history					15.04.1999	by	AE:	Converted to JDK v1.2<br> </p><p>
 * @author  					Andreas Eisenhauer  </p><p>
 * @version  				v1.0, 1998,1999  </p><p>
 * @since  					JDK1.2  </p>
 */
public class DbData extends Object
{
    /** Font used for drawing. */
	protected Font          font;

    /** Color used for text. */
	protected Color         color;

    /** Value to display. */
	protected String        string;


    /**
      * Constructs a new instance of DbData with the passed in
      * arguments.
      */
	public DbData(Font newFont, Color newColor, String newString)
	{
		font = newFont;
		color = newColor;
		string = newString;
	}

    /**
     * Sets the font that is used to represent this object.
     * @uml.property  name="font"
     */
	public void setFont(Font newFont)
	{
		font = newFont;
	}

    /**
     * Returns the Font used to represent this object.
     * @uml.property  name="font"
     */
	public Font getFont()
	{
		return font;
	}

    /**
     * Sets the color used to draw the text.
     * @uml.property  name="color"
     */
	public void setColor(Color newColor)
	{
		color = newColor;
	}

    /**
     * Returns the color used to draw the text.
     * @uml.property  name="color"
     */
	public Color getColor()
	{
		return color;
	}

    /**
     * Sets the string to display for this object.
     * @uml.property  name="string"
     */
	public void setString(String newString)
	{
		string = newString;
	}

    /**
      * Returnes the string to display for this object.
      */
	public String string()
	{
		return string;
	}

	public String toString()
	{
		return string;
	}
}

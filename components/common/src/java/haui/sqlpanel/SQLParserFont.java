package haui.sqlpanel;

import java.io.Serializable;

/**
 *  Description of the Class
 *
 * @author     t026843
 * @created    12. Februar 2003
 */
public class SQLParserFont extends Object implements Serializable
{
  private static final long serialVersionUID = 4749276705129848609L;
  
  /**
   *  Description of the Field
   */
  public String name;
  /**
   *  Description of the Field
   */
  public int size;
  /**
   *  Description of the Field
   */
  public String font;
  /**
   *  Description of the Field
   */
  public String color;
  /**
   *  Description of the Field
   */
  public boolean italic;
  /**
   *  Description of the Field
   */
  public boolean underline;
  /**
   *  Description of the Field
   */
  public boolean bold;


//    private static final long serialVersionUID = 7523967970078585855L;

  /**
   *Constructor for the SQLParserFont object
   *
   * @param  name       Description of Parameter
   * @param  size       Description of Parameter
   * @param  font       Description of Parameter
   * @param  color      Description of Parameter
   * @param  italic     Description of Parameter
   * @param  underline  Description of Parameter
   * @param  bold       Description of Parameter
   */
  public SQLParserFont( String name,
      int size,
      String font,
      String color,
      boolean italic,
      boolean underline,
      boolean bold )
  {
    super();

    this.name = name;
    this.size = size;
    this.font = font;
    this.color = color;
    this.italic = italic;
    this.underline = underline;
    this.bold = bold;
  }
  /*
    private synchronized void readObject(java.io.ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        name = (String)stream.readObject();
            System.out.println(name);
        size = stream.readInt();
        font = (String)stream.readObject();
            System.out.println(font);
        color = (Color)stream.readObject();
        italic = stream.readBoolean();
        underline = stream.readBoolean();
        bold = stream.readBoolean();
    }
    private synchronized void writeObject(java.io.ObjectOutputStream stream)
        throws IOException {
        stream.defaultWriteObject();
        stream.writeChars(name);
        stream.writeInt(size);
        stream.writeChars(font);
        stream.writeObject(color);
        stream.writeBoolean(italic);
        stream.writeBoolean(underline);
        stream.writeBoolean(bold);
        System.out.println("Font: " + name);
    }
*/
}

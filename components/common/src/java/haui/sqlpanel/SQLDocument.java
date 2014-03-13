package haui.sqlpanel;

import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

/**
 * Description of the Class
 * @author      A.E.
 * @created     12. Februar 2003
 */
public class SQLDocument
  extends DefaultStyledDocument
{
  private static final long serialVersionUID = 2000472080134248734L;
  
  SQLParser owner;


  /**
   *Constructor for the SQLDocument object
   *
   * @param  context  Description of Parameter
   * @param  owner    Description of Parameter
   */
  public SQLDocument( StyleContext context, SQLParser owner )
  {
    super( context );

    this.owner = owner;

    setLogicalStyle( 0, getStyle( "DEFAULT" ) );
  }


  /**
   *Constructor for the SQLDocument object
   *
   * @param  content  Description of Parameter
   * @param  context  Description of Parameter
   * @param  owner    Description of Parameter
   */
  public SQLDocument( AbstractDocument.Content content, StyleContext context, SQLParser owner )
  {
    super( content, context );

    this.owner = owner;

    setLogicalStyle( 0, getStyle( "DEFAULT" ) );
  }


  /**
   *  Gets the TheContent attribute of the SQLDocument object
   *
   * @return    The TheContent value
   */
  public AbstractDocument.Content getTheContent()
  {
    return getContent();
  }


  /**
   *  Gets the Writer attribute of the SQLDocument object
   *
   * @return    The Writer value
   */
  public String getWriter()
  {
    return getCurrentWriter().toString();
  }


  /**
   *  Description of the Method
   */
  public void lock()
  {
    writeLock();
  }


  /**
   *  Description of the Method
   */
  public void unlock()
  {
    writeUnlock();
  }


  /**
   *  Description of the Method
   *
   * @return    Description of the Returned Value
   */
  public boolean canWrite()
  {
    return getCurrentWriter() == null;
  }

}

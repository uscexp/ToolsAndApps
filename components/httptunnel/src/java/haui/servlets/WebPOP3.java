
//package moe99;
package haui.servlets;

import java.io.*;
import java.net.*;
import java.text.*;
import javax.mail.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 *    Module:         WebPop3.java<br>
 *                    $Source: $
 *<p>
 *    Description:    Servlet to send and receive emails.<br>
 *</p><p>
 *    Created:        25.10.2001	by	AE
 *</p><p>
 *
 * @author     Andreas Eisenhauer
 *</p><p>
 * @created    30. Oktober 2001
 * @history    30.10.2001	by	AE: Created.<br>
 *</p><p>
 *    Modification:<br>
 *    $Log: $
 *
 *</p><p>
 * @version    v0.1, 2001; $Revision: $<br>
 *                         $Header: $
 *</p><p>
 * @since      JDK1.2
 *</p>
 */
public class WebPOP3
     extends HttpServlet
{

  /**
   *Description of the Field
   *
   * @since
   */
  protected final static String loginForm =
      "<HTML><HEAD><TITLE>WebPOP3</TITLE></HEAD>" +
      "<FORM ACTION=\"{0}\" METHOD=\"POST\">" +
      "Host: <INPUT NAME=\"host\"><BR>" +
      "User: <INPUT NAME=\"user\"><BR>" +
      "Password: <INPUT TYPE=\"PASSWORD\"" +
      " NAME=\"password\"><BR>" +
      "<INPUT TYPE=\"HIDDEN\" NAME=\"action\"" +
      " VALUE=\"list\">" +
      "<INPUT TYPE=\"SUBMIT\" NAME=\"Login\">" +
      "</FORM></BODY></HTML>";

  /**
   *Description of the Field
   *
   * @since
   */
  protected final static String listMsgHeader =
      "<HTML><HEAD><TITLE>WebPOP3</TITLE></HEAD>" +
      "<BODY><P><A HREF=\"{0}?action=list\">[ Inbox ]" +
      "</A> <A HREF=\"{0}?action=logout\">[ Logout ]" +
      "</A>";
  /**
   *Description of the Field
   *
   * @since
   */
  protected final static String listMsgFooter =
      "</BODY></HTML>";
  /**
   *Description of the Field
   *
   * @since
   */
  protected final static String linkList =
      "<P><FORM ACTION=\"{0}\" METHOD=\"POST\">" +
      "<A HREF=\"{0}?action=retrieve&msg={1}\">" +
      "Message: {2}</A> " +
      "<INPUT TYPE=\"HIDDEN\" NAME=\"action\"" +
      " VALUE=\"delete\"><INPUT TYPE=\"HIDDEN\"" +
      " NAME=\"msg\" VALUE=\"{1}\">" +
      "<INPUT TYPE=\"SUBMIT\" VALUE=\"Delete\"></FORM>";
  /**
   *Description of the Field
   *
   * @since
   */
  protected final static String attachmentLink =
      "<A HREF=\"{0}?action=retrieve&msg={1}\">{2}</A>";


  /**
   *Gets the Msg attribute of the WebPOP3 object
   *
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @return                         The Msg value
   * @exception  IOException         Description of Exception
   * @exception  ServletException    Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected Message getMsg( HttpServletRequest request, HttpServletResponse response )
    throws IOException, ServletException, MessagingException
  {
    String msgst = request.getParameter( "msg" );
    if( msgst != null )
    {
      int msgnr = Integer.parseInt( msgst );
      Folder inbox = getInbox( request, response );
      if( inbox == null )
        return null;
      else
        return inbox.getMessage( msgnr );
    }
    else
      return null;
  }


  /**
   *Gets the Inbox attribute of the WebPOP3 object
   *
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @return                         The Inbox value
   * @exception  IOException         Description of Exception
   * @exception  ServletException    Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected Folder getInbox( HttpServletRequest request, HttpServletResponse response )
    throws IOException, ServletException, MessagingException
  {
    HttpSession session = request.getSession( true );
    InboxManager inboxManager;
    if( session.isNew() )
    {
      String host = request.getParameter( "host" );
      String user = request.getParameter( "user" );
      String password = request.getParameter( "password" );
      if( host == null || user == null || password == null )
      {
        doLogin( request, response );
        return null;
      }
      URLName url = new URLName( "pop3", host, 110, "", user, password );
      inboxManager = new InboxManager( url );
      session.setAttribute( "inboxmanager", inboxManager );
    }
    else
      inboxManager = (InboxManager)session.getAttribute( "inboxmanager" );
    return inboxManager.getInbox();
  }


  /**
   *Description of the Method
   *
   * @param  request          Description of Parameter
   * @param  response         Description of Parameter
   * @exception  IOException  Description of Exception
   */
  protected void doGet( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    doGetPost( request, response );
  }


  /**
   *Description of the Method
   *
   * @param  request          Description of Parameter
   * @param  response         Description of Parameter
   * @exception  IOException  Description of Exception
   */
  protected void doPost( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    doGetPost( request, response );
  }


  /**
   *Description of the Method
   *
   * @param  request          Description of Parameter
   * @param  response         Description of Parameter
   * @exception  IOException  Description of Exception
   */
  protected void doGetPost( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    try
    {
      String action = request.getParameter( "action" );
      if( action == null )
        doLogin( request, response );
      else if( action.equals( "logout" ) )
        doLogout( request, response );
      else if( action.equals( "list" ) )
        doList( request, response );
      else if( action.equals( "retrieve" ) )
        doRetrieve( request, response );
      else if( action.equals( "delete" ) )
        doRemove( request, response );
    }
    catch( Exception e )
    {
      PrintWriter writer = response.getWriter();
      writer.println( "Oops!" );
      e.printStackTrace( writer );
      writer.println( "" );
    }
  }


  /**
   *Description of the Method
   *
   * @param  request          Description of Parameter
   * @param  response         Description of Parameter
   * @exception  IOException  Description of Exception
   */
  protected void doLogin( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    printForm( loginForm, request, response );
  }


  /**
   *Description of the Method
   *
   * @param  form             Description of Parameter
   * @param  request          Description of Parameter
   * @param  response         Description of Parameter
   * @exception  IOException  Description of Exception
   */
  protected void printForm( String form, HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    response.setContentType("text/html");
    PrintWriter writer = response.getWriter();
    form = MessageFormat.format( form, new Object[]{request.getServletPath()} );
    writer.print( form );
    writer.flush();
  }


  /**
   *Description of the Method
   *
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @exception  ServletException    Description of Exception
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected void doList( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException, MessagingException
  {
    printForm( listMsgHeader, request, response );
    Folder inbox = getInbox( request, response );
    if( inbox == null )
      return;
    int count = inbox.getMessageCount();
    for( int i = 1; i <= count; i++ )
    {
      Message msg = inbox.getMessage( i );
      if( !msg.isSet( Flags.Flag.DELETED ) )
        printHyperlink( linkList, request, response, String.valueOf( i ), msg.getSubject() );
    }
    printForm( listMsgFooter, request, response );
  }


  /**
   *Description of the Method
   *
   * @param  form             Description of Parameter
   * @param  request          Description of Parameter
   * @param  response         Description of Parameter
   * @param  parameters       Description of Parameter
   * @param  text             Description of Parameter
   * @exception  IOException  Description of Exception
   */
  protected void printHyperlink( String form, HttpServletRequest request, HttpServletResponse response,
      String parameters, String text )
    throws IOException
  {
    PrintWriter writer = response.getWriter();
    form = MessageFormat.format( form, new Object[]{request.getServletPath(), parameters, text} );
    writer.print( form );
    writer.flush();
  }


  /**
   *Description of the Method
   *
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @exception  ServletException    Description of Exception
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected void doRemove( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException, MessagingException
  {
    Message msg = getMsg( request, response );
    if( msg == null )
      return;
    msg.setFlag( Flags.Flag.DELETED, true );
    doList( request, response );
  }


  /**
   *Description of the Method
   *
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @exception  ServletException    Description of Exception
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected void doRetrieve( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException, MessagingException
  {
    Message msg = getMsg( request, response );
    if( msg == null )
      return;
    if( null == request.getParameter( "part" ) )
      writeMessage( msg, request, response );
    else
      writePart( msg, request, response );
  }


  /**
   *Description of the Method
   *
   * @param  msg                     Description of Parameter
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected void writeEnveloppe( Message msg, HttpServletRequest request, HttpServletResponse response )
    throws IOException, MessagingException
  {
    printForm( listMsgHeader, request, response );
    PrintWriter writer = response.getWriter();
    writer.println( "From: " );
    writeAddresses( msg.getFrom(), writer );
    writer.println( "To: " );
    writeAddresses( msg.getRecipients( Message.RecipientType.TO ), writer );
    writer.println( "Cc: " );
    writeAddresses( msg.getRecipients( Message.RecipientType.CC ), writer );
    writer.println( "Subject: " );
    writer.println( msg.getSubject() + "" );
    writer.flush();
  }


  /**
   *Description of the Method
   *
   * @param  addresses  Description of Parameter
   * @param  writer     Description of Parameter
   */
  protected void writeAddresses( Address[] addresses, PrintWriter writer )
  {
    if( addresses != null )
      for( int i = 0; i < addresses.length; i++ )
        writer.println( addresses[i] + "" );
    else
      writer.println( "" );
  }


  /**
   *Description of the Method
   *
   * @param  msg                     Description of Parameter
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @exception  ServletException    Description of Exception
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected void writeMessage( Message msg, HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException, MessagingException
  {
    response.setContentType("text/html");
    PrintWriter writer = response.getWriter();
    writeEnveloppe( msg, request, response );
    if( msg.isMimeType( "multipart/*" ) )
    {
      Multipart multipart = (Multipart)msg.getContent();
      for( int i = 0; i < multipart.getCount(); i++ )
      {
        Part p = multipart.getBodyPart( i );
        if( p.isMimeType( "text/plain" ) )
        {
          writer.print( "" );
          writer.print( p.getContent() );
          writer.print( "" );
        }
        else
        {
          String filename = p.getFileName();
          printHyperlink( attachmentLink, request, response, msg.getMessageNumber() + "\u0081=" + i, "attachement" );
        }
      }
    }
    else if( msg.isMimeType( "text/plain" ) )
    {
      writer.print( "" );
      writer.print( msg.getContent() );
      writer.print( "" );
    }
    else
      printHyperlink( attachmentLink, request, response, msg.getMessageNumber() + "\u0081=-1", "open content" );
    writer.flush();
  }


  /**
   *Description of the Method
   *
   * @param  msg                     Description of Parameter
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected void writePart( Message msg, HttpServletRequest request, HttpServletResponse response )
    throws IOException, MessagingException
  {
    int partnr = Integer.parseInt( request.getParameter( "part" ) );
    Part p;
    if( partnr < 0 )
      p = msg;
    else
    {
      Multipart multipart = (Multipart)msg.getContent();
      p = multipart.getBodyPart( partnr );
    }
    response.setContentType( p.getContentType() );
    if( p.getFileName() != null )
      response.setHeader( "Content-Disposition", "attachment; filename=\"" + p.getFileName() + "\"" );
    OutputStream out = response.getOutputStream();
    InputStream in = p.getInputStream();
    int c = in.read();
    while( c != -1 )
    {
      out.write( c );
      c = in.read();
    }
  }


  /**
   *Description of the Method
   *
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @exception  ServletException    Description of Exception
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  protected void doLogout( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException, MessagingException
  {
    HttpSession session = request.getSession( false );
    if( null != session )
      session.invalidate();
    doLogin( request, response );
  }
}

/**
 *Description of the Class
 *
 * @author     t026843
 * @created    30. Oktober 2001
 */
class InboxManager
     implements HttpSessionBindingListener
{


  private Store store;
  private Folder inbox;
  private static Session session = Session.getDefaultInstance( System.getProperties(), null );


  /**
   *Constructor for the InboxManager object
   *
   * @param  url                          Description of Parameter
   * @exception  NoSuchProviderException  Description of Exception
   * @exception  MessagingException       Description of Exception
   */
  public InboxManager( URLName url )
    throws NoSuchProviderException, MessagingException
  {
    store = session.getStore( url );
    store.connect();
    inbox = store.getFolder( "INBOX" );
    inbox.open( Folder.READ_WRITE );
  }


  /**
   *Gets the Inbox attribute of the InboxManager object
   *
   * @return    The Inbox value
   */
  public Folder getInbox()
  {
    return inbox;
  }


  /**
   *Description of the Method
   *
   * @param  event  Description of Parameter
   */
  public void valueBound( HttpSessionBindingEvent event )
  {
  }


  /**
   *Description of the Method
   *
   * @param  event  Description of Parameter
   */
  public void valueUnbound( HttpSessionBindingEvent event )
  {
    try
    {
      inbox.close( true );
      store.close();
    }
    catch( MessagingException e )
    {
    }
  }
}



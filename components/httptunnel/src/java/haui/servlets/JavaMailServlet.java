
//package moe99;
package haui.servlets;

import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.reflect.Array;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 *    Module:         JavaMailServlet.java<br>
 *                    $Source: $
 *<p>
 *    Description:    Servlet to send and receive emails.<br>
 *</p><p>
 *    Created:        25.10.2001	by	AE
 *</p><p>
 *
 * @author     Andreas Eisenhauer
 *</p><p>
 * @created    25. Oktober 2001
 * @history    25.10.2001	by	AE: Created.<br>
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
public class JavaMailServlet extends HttpServlet implements SingleThreadModel
{
  String protocol = "imap";
  String mbox = "INBOX";

  // constants
  // parameter names
  public static String ACTION = "action";
  public static String OUTPUT = "output";
  public static String ATTACHMENT = "attachment0";
  public static String ATTACHMENTBASE = "attachment";
  public static String HOSTNAME = "hostname";
  public static String SMTP = "smtp";
  public static String USERNAME = "username";
  public static String PASSWORD = "password";
  public static String FOLDER = "folder";
  public static String MESSAGE = "message";
  public static String PART = "part";
  public static String FROM = "from";
  public static String REPLYTO = "replyto";
  public static String TO = "to";
  public static String CC = "cc";
  public static String BCC = "bcc";
  public static String SUBJECT = "subject";
  public static String TEXT = "text";

  // parameter values
  public static String SEND = "send";
  public static String REPLY = "reply";
  public static String REPLYALL = "replyall";
  public static String FORWARD = "forward";
  public static String REPLYFORM = "reply to sender";
  public static String REPLYALLFORM = "reply to all";
  public static String FORWARDFORM = "forward message";
  public static String LOGIN = "login";
  public static String LOGOUT = "logout";
  public static String LIST = "list";
  public static String LOAD = "load";
  public static String READ = "read";
  public static String COMPOSE = "compose";
  public static String DELETE = "delete";
  public static String HTML = "html";
  public static String NOHTML = "nohtml";

  /**
   *Gets the ServletInfo attribute of the JavaMailServlet object
   *
   * @return    The ServletInfo value
   */
  public String getServletInfo()
  {
    return "A mail servlet";
  }


  /**
   * This method handles the "POST" submission.
   *
   * @param  req                   Request from client
   * @param  res                   Response to client
   * @exception  ServletException  Thrown on servlet error
   * @exception  IOException       Thrown on io error
   */
  public void doPost( HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
  {
    executeAction( req, res );
  }


  /**
   * This method handles the GET requests for the client.
   *
   * @param  req                   Request from client
   * @param  res                   Response to client
   * @exception  ServletException  Thrown on servlet error
   * @exception  IOException       Thrown on io error
   */
  public void doGet( HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
  {
    executeAction( req, res );
  }


  /**
   * This method handles the GET and POST requests for the client.
   *
   * @param  req                   Request from client
   * @param  res                   Response to client
   * @exception  ServletException  Thrown on servlet error
   * @exception  IOException       Thrown on io error
   */
  private void executeAction( HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException
  {
    String action;
    String output;
    String host;
    String smtp;
    String user;
    String passwd;
    String foldername;

    ServletOutputStream out = res.getOutputStream();

    MailUserData mud = null;
    HttpSession ssn = null;

    try
    {
      String content = req.getContentType();
      if( content != null)
      {
        if( content.startsWith( "multipart/form-data" ))
        {
          doUpload( req, res);
          return;
        }
      }
      action = req.getParameter( ACTION );
      output = req.getParameter( OUTPUT );
      host = req.getParameter( HOSTNAME );
      smtp = req.getParameter( SMTP );
      user = req.getParameter( USERNAME );
      passwd = req.getParameter( PASSWORD );
      foldername = req.getParameter( FOLDER );

      // get the session
      if( action != null )
      {
        if( action.equalsIgnoreCase( LOGIN ) )
          ssn = req.getSession( true );
        else
        {
          ssn = req.getSession( false );
          mud = getMUD( ssn );

          if( mud == null )
            throw new MessagingException( "Please Login (no session)" );

          if( !mud.getStore().isConnected() )
            throw new MessagingException( "Not Connected To Store" );
        }
      }

      if( action != null && ssn != null )
      {
        if( action.equalsIgnoreCase( SEND ) )
        {
          // process message sending
          if( output != null && output.equalsIgnoreCase( HTML ) )
            ;
        }
        else if( action.equalsIgnoreCase( REPLY ) || action.equalsIgnoreCase( REPLYALL ) || action.equalsIgnoreCase( FORWARD ) )
        {
          // process message sending
          if( output != null && output.equalsIgnoreCase( HTML ) )
            ;
        }
        else if( action.equalsIgnoreCase( READ ) )
        {
          // get url parameters
          String msgStr = req.getParameter( MESSAGE );
          String part = req.getParameter( PART );
          int msgNum = -1;
          int partNum = -1;

          // process url params
          if( msgStr != null )
          {
            // operate on message "msgStr"
            msgNum = Integer.parseInt( msgStr );

            if( part == null )
            {
              // display message "msgStr"
              if( output != null && output.equalsIgnoreCase( HTML ) )
              {
              }
            }
            else if( part != null )
            {
              // display part "part" in message "msgStr"
              partNum = Integer.parseInt( part );
              output = null;
              displayPart( mud, msgNum, partNum, out, res );
            }
          }
        }
        else if( action.equalsIgnoreCase( COMPOSE ) )
        {
          // display compose form
          if( output != null && output.equalsIgnoreCase( HTML ) )
            ;
        }
        else if( action.equalsIgnoreCase( REPLYFORM ) || action.equalsIgnoreCase( REPLYALLFORM )
            || action.equalsIgnoreCase( FORWARDFORM ))
        {
          // display compose form
          if( output != null && output.equalsIgnoreCase( HTML ) )
            ;
        }
        else if( action.equalsIgnoreCase( LOGOUT ) )
        {
          this.getServletContext().log( "logout entered\n" );
          mud.getFolder().close( true );
          mud.getStore().close();
          ssn.invalidate();
        }
        else if( action.equalsIgnoreCase( LIST ) )
        {
          Folder folder = mud.getFolder();
          int totalMessages = folder.getMessageCount();
          Message[] msgs = folder.getMessages();
          FetchProfile fp = new FetchProfile();
          fp.add( FetchProfile.Item.ENVELOPE );
          folder.fetch( msgs, fp );
        }
        else if( action.equalsIgnoreCase( LOAD ) )
        {
          // load folder

          URLName url = mud.getURLName();

          host = url.getHost();
          user = url.getUsername();
          passwd = url.getPassword();
          smtp = mud.getSmtpHost();

          mud = doLogin( protocol, host, smtp, foldername, user, passwd, mud, ssn, req);
        }
        else if( action.equalsIgnoreCase( LOGIN ) )
        {
          // initial login
          mud = doLogin( protocol, host, smtp, mbox, user, passwd, mud, ssn, req);
        }
        if( output != null && output.equalsIgnoreCase( HTML ) )
        {
          this.getServletContext().log( "html output entered\n" );
          doHtmlOutput( action, req, res, mud );
        }
        else
        {
        }
      }
    }
    catch( Exception ex )
    {
      //out.println( ex.toString() );
      ex.printStackTrace( new PrintStream( out));
    }
    finally
    {
      out.close();
    }
  }


  /**
   * Sends the output in html form
   *
   * @param  protocol         Mail protocol (IMAP, POP3)
   * @param  host             IMAP/POP3 host
   * @param  smtp             SMTP host
   * @param  foldername       Folder to connect to
   * @param  user             User
   * @param  passwd           Password
   * @param  mud              Session user data
   * @param  ssn              HttpSession
   * @param  req              Request from client
   * @return                  MultiUserData
   * @exception  MessagingException  Thrown on messaging error
   * @exception  IOException  Thrown on io error
   */
  private MailUserData doLogin( String protocol, String host, String smtp, String foldername, String user, String passwd, MailUserData mud,
      HttpSession ssn, HttpServletRequest req)
    throws MessagingException, IOException
  {
    URLName url = null;

    if( foldername != null)
      url = new URLName( protocol, host, -1, foldername, user, passwd );

    if( url == null )
      throw new IOException( "No folder name" );

    // close old session
    if( mud != null)
    {
      mud.getFolder().close( true );
      mud.getStore().close();
      ssn.invalidate();
    }

    // create
    ssn = req.getSession( true );
    mud = new MailUserData( url );
    ssn.setAttribute( "javamailservlet", mud );
    ssn.setMaxInactiveInterval( 3600);

    Properties props = System.getProperties();
    if( smtp == null )
      smtp = "mycgiserver.com";

    props.put( "mail.smtp.host", smtp );
    if( protocol.equalsIgnoreCase( "pop3"))
      props.put( "mail.pop3.host", host );
    else if( protocol.equalsIgnoreCase( "imap"))
      props.put( "mail.imap.host", host );
    Session session = Session.getDefaultInstance( props, null );
    session.setDebug( false );
    Store store = session.getStore( url );
    store.connect();
    Folder folder = store.getDefaultFolder();
    if( folder == null )
      throw new MessagingException( "No default folder" );

    Folder rootSubFolders[] = folder.list();

    folder = folder.getFolder( foldername );
    if( folder == null )
      throw new MessagingException( "Invalid folder" );

    folder.open( Folder.READ_WRITE );
    Folder currentSubFolders[] = folder.list();
    /*
    int totalMessages = folder.getMessageCount();
    Message[] msgs = folder.getMessages();
    FetchProfile fp = new FetchProfile();
    fp.add( FetchProfile.Item.ENVELOPE );
    folder.fetch( msgs, fp );
    */

    // save stuff into MUD
    mud.setSession( session );
    mud.setSmtpHost( smtp);
    mud.setStore( store );
    mud.setFolder( folder );
    mud.setRootSubFolders( rootSubFolders);
    mud.setCurrentSubFolders( currentSubFolders);

    return mud;
  }


  /**
   * Sends the output in html form
   *
   * @param  action           Output according to the action
   * @param  req              Request from client
   * @param  res              Output stream
   * @param  mud              Session user data
   * @exception  IOException  Thrown on io error
   */
  private void doHtmlOutput( String action, HttpServletRequest req, HttpServletResponse res, MailUserData mud )
    throws MessagingException, IOException
  {
    ServletOutputStream out = res.getOutputStream();
    res.setContentType( "text/html" );
    out.println( "<html><body bgcolor=\"#CCCCFF\">" );

    // splash
    out.print( "<center>" );
    out.print( "<font face=\"Arial,Helvetica\" font size=\"5\">" );
    out.println( "<b>M.o.E Mail</b></font></center><p>" );

    if( action.equalsIgnoreCase( LOGIN ) || action.equalsIgnoreCase( LOAD ))
    {
      // folder table
      out.println( "<table width=\"50%\" border=0 align=center>" );
      // folder name column header
      out.print( "<tr><td width=\"75%\" bgcolor=\"#ffffcc\">" );
      out.print( "<font face=\"Arial,Helvetica\" font size=\"3\">" );
      out.println( "<b>FolderName</b></font></td><br>" );
      // msg count column header
      out.print( "<td width=\"25%\" bgcolor=\"#ffffcc\">" );
      out.print( "<font face=\"Arial,Helvetica\" font size=\"3\">" );
      out.println( "<b>Messages</b></font></td><br>" );
      out.println( "</tr>" );
      // folder name
      Folder rootSubFolders[] = mud.getRootSubFolders();
      Folder folder = null;
      for( int i = 0; i < Array.getLength( rootSubFolders); i++)
      {
        out.print( "<tr><td width=\"75%\" bgcolor=\"#ffffff\">" );
        folder = rootSubFolders[i];
        if( folder != mud.getFolder())
          folder.open( Folder.READ_ONLY);
        out.print( "<a href=\"" + req.getRequestURL() + "?" + ACTION + "=" + LIST + "&" + FOLDER
        + "=" + folder.getName() + "&" + OUTPUT + "=" + HTML + "\">" + folder.getName() + "</a></td><br>" );
        // msg count
        out.println( "<td width=\"25%\" bgcolor=\"#ffffff\">" + (folder.getMessageCount()) + "</td>" );
        out.println( "</tr>" );
        if( folder != mud.getFolder())
          folder.close( false);
      }
      out.println( "</table>" );
    }
    else if( action.equalsIgnoreCase( LIST ) )
    {
      // display headers
      displayHeadersHtml( mud, req, out );
    }
    else if( action.equalsIgnoreCase( LOGOUT ) )
    {
      this.getServletContext().log( "writing log out text\n" );
      out.println( "Logged out OK" );
    }
    else if( action.equalsIgnoreCase( SEND ) || action.equalsIgnoreCase( REPLY ) || action.equalsIgnoreCase( REPLYALL )
        || action.equalsIgnoreCase( FORWARD ))
    {
      // sending html message
      sendHtml( req, out );
    }
    else if( action.equalsIgnoreCase( READ ) )
    {
      // get url parameters
      String msgStr = req.getParameter( MESSAGE );
      String part = req.getParameter( PART );
      int msgNum = -1;
      int partNum = -1;

      // process url params
      if( msgStr != null )
      {
        // operate on message "msgStr"
        msgNum = Integer.parseInt( msgStr );

        if( part == null )
        {
          // display message "msgStr"
          displayMessageHtml( mud, req, out, msgNum );
        }
      }
    }
    else if( action.equalsIgnoreCase( COMPOSE ) )
    {
      // display compose form
      composeForm( mud, res, out );
    }
    else if( action.equalsIgnoreCase( REPLYFORM ) )
    {
      String msgStr = req.getParameter( MESSAGE );
      int msgNum = -1;
      if( msgStr != null )
      {
        // operate on message "msgStr"
        msgNum = Integer.parseInt( msgStr );
        // display reply form
        replyForm( mud, res, out, msgNum );
      }
    }
    else if( action.equalsIgnoreCase( REPLYALLFORM ) )
    {
      String msgStr = req.getParameter( MESSAGE );
      int msgNum = -1;
      if( msgStr != null )
      {
        // operate on message "msgStr"
        msgNum = Integer.parseInt( msgStr );
        // display reply form
        replyAllForm( mud, res, out, msgNum );
      }
    }
    else if( action.equalsIgnoreCase( FORWARDFORM ) )
    {
      String msgStr = req.getParameter( MESSAGE );
      int msgNum = -1;
      if( msgStr != null )
      {
        // operate on message "msgStr"
        msgNum = Integer.parseInt( msgStr );
        // display reply form
        forwardForm( mud, res, out, msgNum );
      }
    }
    out.println( "</body></html>" );
    out.flush();
  }


  /**
   *Description of the Method
   *
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  private boolean doUpload( HttpServletRequest request, HttpServletResponse response )
    throws IOException, MessagingException
  {
/*
    boolean blRet = false;
    String boundary = request.getHeader( "Content-Type" );
    int pos = boundary.indexOf( '=' );
    boundary = boundary.substring( pos + 1 );
    ServletInputStream in = request.getInputStream();

    HttpMultiPartParser hmpp = new HttpMultiPartParser();
    Dictionary fields = hmpp.parseData( in, boundary);

    blRet = doSendMessage( request, response, fields, null );

    return blRet;
*/
    boolean blRet = false;
    String boundary = request.getHeader( "Content-Type" );
    int pos = boundary.indexOf( '=' );
    boundary = boundary.substring( pos + 1 );
    boundary = "--" + boundary;
    ServletInputStream in = request.getInputStream();
    byte[] bytes = new byte[512];
    int state = 0;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    String name = null;
    String value = null;
    String filename = null;
    String contentType = null;
    Dictionary fields = new Hashtable();
    int i = in.readLine( bytes, 0, 512 );
    while( -1 != i )
    {
      String st = new String( bytes, 0, i );
      if( st.startsWith( boundary ) )
      {
        state = 0;
        if( null != name )
        {
          if( value != null )
            fields.put( name, value.substring( 0, value.length() - 2 ) );
            // -2 to remove CR/LF
          else if( buffer.size() > 2 )
          {
            InternetHeaders headers = new InternetHeaders();
            MimeBodyPart bodyPart = new MimeBodyPart();
            DataSource ds = new ByteArrayDataSource( buffer.toByteArray(), contentType, filename );
            bodyPart.setDataHandler( new DataHandler( ds ) );
            bodyPart.setDisposition( "attachment; filename=\"" + filename + "\"" );
            bodyPart.setFileName( filename );
            fields.put( name, bodyPart );
          }
          name = null;
          value = null;
          filename = null;
          contentType = null;
          buffer = new ByteArrayOutputStream();
        }
      }
      else if( st.startsWith(
          "Content-Disposition: form-data" ) && state == 0 )
      {
        StringTokenizer tokenizer = new StringTokenizer( st, ";=\"" );
        while( tokenizer.hasMoreTokens() )
        {
          String token = tokenizer.nextToken();
          if( token.startsWith( " name" ) )
          {
            name = tokenizer.nextToken();
            state = 2;
          }
          else if( token.startsWith( " filename" ) )
          {
            filename = tokenizer.nextToken();
            StringTokenizer ftokenizer = new StringTokenizer( filename, "\\/:" );
            filename = ftokenizer.nextToken();
            while( ftokenizer.hasMoreTokens() )
              filename = ftokenizer.nextToken();
            state = 1;
            break;
          }
        }
      }
      else if( st.startsWith( "Content-Type" ) && state == 1 )
      {
        pos = st.indexOf( ":" );
        // + 2 to remove the space
        // - 2 to remove CR/LF
        contentType = st.substring( pos + 2, st.length() - 2 );
      }
      else if( st.equals( "\r\n" ) && state == 1 )
        state = 3;
      else if( st.equals( "\r\n" ) && state == 2 )
        state = 4;
      else if( state == 4 )
        value = value == null ? st : value + st;
      else if( state == 3 )
        buffer.write( bytes, 0, i );
      i = in.readLine( bytes, 0, 512 );
    }
    blRet = doSendMessage( request, response, fields, null );

    return blRet;
  }


  /**
   *Description of the Method
   *
   * @param  request                 Description of Parameter
   * @param  response                Description of Parameter
   * @param  fields                  Description of Parameter
   * @exception  IOException         Description of Exception
   * @exception  MessagingException  Description of Exception
   */
  private boolean doSendMessage( HttpServletRequest request, HttpServletResponse response, Dictionary fields, MailUserData mud )
    throws IOException, MessagingException
  {
    String output = (String)fields.get( OUTPUT );
    String action = (String)fields.get( ACTION );
    String msgStr = (String)fields.get( MESSAGE );
    String from = (String)fields.get( FROM );
    String replyto = (String)fields.get( REPLYTO );
    String to = (String)fields.get( TO );
    String cc = (String)fields.get( CC );
    String bcc = (String)fields.get( BCC );
    String subj = (String)fields.get( SUBJECT );
    String text = (String)fields.get( TEXT );

    /*
    FileInfo fi = null;
    try
    {
      fi = (FileInfo)fields.get( ATTACHMENT);
    }
    catch( Exception ex)
    {
      fi = null;
    }
    */
    boolean blRet = false;
    HttpSession ssn = null;

    if( mud == null )
    {
      ssn = request.getSession( false );
      mud = getMUD( ssn );

      if( mud == null )
        throw new MessagingException( "Please Login (no session)" );

      if( !mud.getStore().isConnected() )
        throw new MessagingException( "Not Connected To Store" );
    }
    MimeMessage msg = null;
    MimeMessage fwdmsg = null;

    if( action.equalsIgnoreCase( SEND ))
      msg = new MimeMessage( mud.getSession() );
    else if( action.equalsIgnoreCase( REPLY ) )
    {
      int msgNum = -1;
      if( msgStr != null )
      {
        // operate on message "msgStr"
        msgNum = Integer.parseInt( msgStr );
        msg = (MimeMessage)mud.getFolder().getMessage( msgNum );
        msg = (MimeMessage)msg.reply( false);
      }
      else
        msg = new MimeMessage( mud.getSession() );
    }
    else if( action.equalsIgnoreCase( REPLYALL ) )
    {
      int msgNum = -1;
      if( msgStr != null )
      {
        // operate on message "msgStr"
        msgNum = Integer.parseInt( msgStr );
        msg = (MimeMessage)mud.getFolder().getMessage( msgNum );
        msg = (MimeMessage)msg.reply( true);
      }
      else
        msg = new MimeMessage( mud.getSession() );
    }
    else if( action.equalsIgnoreCase( FORWARD ) )
    {
      int msgNum = -1;
      if( msgStr != null )
      {
        // operate on message "msgStr"
        msgNum = Integer.parseInt( msgStr );
        fwdmsg = (MimeMessage)mud.getFolder().getMessage( msgNum );
      }
      msg = new MimeMessage( mud.getSession() );
    }
    InternetAddress[] toAddrs = null;
    InternetAddress[] ccAddrs = null;
    InternetAddress[] bccAddrs = null;
    InternetAddress[] replyAddrs = null;

    if( to != null )
    {
      toAddrs = InternetAddress.parse( to, false );
      if( action.equalsIgnoreCase( FORWARD ) )
      {
        msg.addRecipients( Message.RecipientType.TO, toAddrs );
      }
      else
        msg.setRecipients( Message.RecipientType.TO, toAddrs );
    }
    else
      throw new MessagingException( "No \"To\" address specified" );

    if( cc != null )
    {
      ccAddrs = InternetAddress.parse( cc, false );
      msg.setRecipients( Message.RecipientType.CC, ccAddrs );
    }

    if( bcc != null )
    {
      bccAddrs = InternetAddress.parse( bcc, false );
      msg.setRecipients( Message.RecipientType.BCC, bccAddrs );
    }

    if( !action.equalsIgnoreCase( REPLY ) || msgStr == null)
    {
      if( subj != null )
        msg.setSubject( subj );
    }

    URLName u = mud.getURLName();
    if( from != null )
      msg.setFrom( new InternetAddress( from ) );
    else
      msg.setFrom( new InternetAddress( u.getUsername() + "@" + u.getHost() ) );

    if( replyto != null )
    {
      replyAddrs = InternetAddress.parse( replyto, false );
      msg.setReplyTo( replyAddrs);
    }

    msg.setSentDate( new Date() );

    //if( null == fi)
    if( fields.get( ATTACHMENT) != null || (action.equalsIgnoreCase( FORWARD ) && fwdmsg != null))
    {
      BodyPart body = new MimeBodyPart();
      MimeBodyPart attachment = null;
      MimeBodyPart fwdbody = null;
      MimeMultipart multipart = new MimeMultipart();

      if( action.equalsIgnoreCase( FORWARD ) && fwdmsg != null)
      {
        if( text != null)
          text += "\n\nOriginal message:\n\n";
        else
          text = "Original message:\n\n";
      }
      if( text != null)
        body.setText( text );
      multipart.addBodyPart(body);
      if( action.equalsIgnoreCase( FORWARD ) && fwdmsg != null)
      {
        if( fwdmsg.isMimeType( "multipart/*" ) )
        {
          Multipart mp = (Multipart)fwdmsg.getContent();
          int cnt = mp.getCount();
          for( int i = 0; i < cnt; i++ )
          {
            multipart.addBodyPart( mp.getBodyPart( i ));
          }
        }
        else
        {
          fwdbody = new MimeBodyPart();
          fwdbody.setDataHandler( fwdmsg.getDataHandler());
          multipart.addBodyPart(fwdbody);
        }
      }
      if( null != fields.get( ATTACHMENT))
      {
        for( int i = 0; (attachment = (MimeBodyPart)fields.get( ATTACHMENTBASE + i)) != null; i++)
        {
          //attachment = (MimeBodyPart)fields.get( ATTACHMENT);
          multipart.addBodyPart(attachment);
        }
      }
      msg.setContent(multipart);
      /*
      InternetHeaders headers = new InternetHeaders();
      MimeBodyPart attachment = new MimeBodyPart();
      String filename = fi.getClientFileName();
      DataSource ds = new ByteArrayDataSource( fi.getFileContents(), fi.getFileContentType(), filename );
      attachment.setDataHandler( new DataHandler( ds ) );
      attachment.setDisposition( "attachment; filename=\"" + filename + "\"" );
      attachment.setDescription( fi.toString());
      attachment.setFileName( filename );

      BodyPart body = new MimeBodyPart();
      if( text != null)
        body.setText( text );
      MimeMultipart multipart = new MimeMultipart();
      multipart.addBodyPart( body );
      multipart.addBodyPart( attachment );
      msg.setContent( multipart );
      */
    }
    else
    {
      if( text != null)
        msg.setText( text );
    }
    Transport.send( msg );
    if( output != null && output.equalsIgnoreCase( HTML ) )
    {
      //response.getOutputStream().println( fields.toString());
      doHtmlOutput( action, request, response, mud);
    }
    blRet = true;

    return blRet;
  }


  // utility method; returns a string suitable for msg header display
  /**
   *Gets the DisplayAddress attribute of the JavaMailServlet object
   *
   * @param  a  Description of Parameter
   * @return    The DisplayAddress value
   */
  private String getDisplayAddress( Address a )
  {
    String pers = null;
    String addr = null;
    if( a instanceof InternetAddress &&
        ( ( pers = ( (InternetAddress)a ).getPersonal() ) != null ) )
    {

      addr = pers + "  " + "&lt;" + ( (InternetAddress)a ).getAddress() + "&gt;";
    }
    else
      addr = a.toString();

    return addr;
  }


  // utility method; retrieve the MailUserData
  // from the HttpSession and return it
  /**
   *Gets the MUD attribute of the JavaMailServlet object
   *
   * @param  ses              Description of Parameter
   * @return                  The MUD value
   * @exception  IOException  Description of Exception
   */
  private MailUserData getMUD( HttpSession ses )
    throws IOException
  {
    MailUserData mud = null;

    if( ses == null )
    {
      return null;
    }
    else
    {
      if( ( mud = (MailUserData)ses.getAttribute( "javamailservlet" ) ) == null )
      {
        return null;
      }
    }
    return mud;
  }


  /* main method to display messages */
  /**
   *Description of the Method
   *
   * @param  mud              Description of Parameter
   * @param  req              Description of Parameter
   * @param  out              Description of Parameter
   * @param  msgNum           Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void displayMessageHtml( MailUserData mud, HttpServletRequest req,
      ServletOutputStream out, int msgNum )
    throws IOException
  {

    //out.println( "<html>" );
    //out.println( "<HEAD><TITLE>JavaMail Servlet</TITLE></HEAD>" );
    //out.println( "<BODY bgcolor=\"#ccccff\">" );
    out.print( "<center><font face=\"Arial,Helvetica\" " );
    out.println( "font size=\"3\"><b>" );
    out.println( "Message " + ( msgNum + 1 ) + " in folder "
         + mud.getStore().getURLName() + "/" + mbox + "</b></font></center><p>" );

    out.println( "<FORM ACTION=\"/servlet/moe99.JavaMailServlet\" METHOD=\"POST\" ENCTYPE=\"application/x-www-form-urlencoded\">" );
    out.println( "<input type=\"hidden\" name=\"output\" value=\"" + HTML + "\">" );
    out.println( "<input type=\"hidden\" name=\"message\" value=\"" + msgNum + "\">" );
    out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"action\" VALUE=\"" + LOGOUT + "\">" );
    out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"action\" VALUE=\"" + LIST + "\">" );
    out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"action\" VALUE=\"" + COMPOSE + "\">" );
    out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"action\" VALUE=\"" + REPLYFORM + "\">" );
    out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"action\" VALUE=\"" + REPLYALLFORM + "\">" );
    out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"action\" VALUE=\"" + FORWARDFORM + "\"></FORM>" );
    out.println( "<hr>" );

    try
    {
      Message msg = mud.getFolder().getMessage( msgNum );

      // first, display this message's headers
      displayMessageHeadersHtml( mud, msg, out );

      // and now, handle the content
      Object o = msg.getContent();

      //if (o instanceof String) {
      if( msg.isMimeType( "text/plain" ) )
      {
        out.println( "<pre>" );
        out.println( (String)o );
        out.println( "</pre>" );
        //} else if (o instanceof Multipart){
      }
      else if( msg.isMimeType( "multipart/*" ) )
      {
        Multipart mp = (Multipart)o;
        int cnt = mp.getCount();
        for( int i = 0; i < cnt; i++ )
        {
          displayPartHtml( mud, msgNum, mp.getBodyPart( i ), i, req, out );
        }
      }
      else
      {
        out.println( msg.getContentType() );
      }

    }
    catch( MessagingException mex )
    {
      out.println( mex.toString() );
    }

    //out.println( "</BODY></html>" );
    //out.close();
  }


  /**
   * This method displays a message part. <code>text/plain</code>
   * content parts are displayed inline. For all other parts,
   * a URL is generated and displayed; clicking on the URL
   * brings up the part in a separate page.
   *
   * @param  mud              Description of Parameter
   * @param  msgNum           Description of Parameter
   * @param  part             Description of Parameter
   * @param  partNum          Description of Parameter
   * @param  req              Description of Parameter
   * @param  out              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void displayPartHtml( MailUserData mud, int msgNum, Part part, int partNum, HttpServletRequest req, ServletOutputStream out )
    throws IOException
  {

    if( partNum != 0 )
      out.println( "<p><hr>" );

    try
    {

      String sct = part.getContentType();
      if( sct == null )
      {
        out.println( "invalid part" );
        return;
      }
      ContentType ct = new ContentType( sct );

      if( partNum != 0 )
        out.println( "<b>Attachment Type:</b> " +
            ct.getBaseType() + "<br>" );

      if( ct.match( "text/plain" ) )
      {
        // display text/plain inline
        out.println( "<pre>" );
        out.println( (String)part.getContent() );
        out.println( "</pre>" );

      }
      else
      {
        // generate a url for this part
        String s;
        if( ( s = part.getFileName() ) != null )
          out.println( "<b>Filename:</b> " + s + "<br>" );
        s = null;
        if( ( s = part.getDescription() ) != null )
          out.println( "<b>Description:</b> " + s + "<br>" );

        out.println( "<a href=\"" + req.getRequestURL() + "?" + ACTION + "=" + READ + "&" + OUTPUT + "=" + HTML
        + "&" + MESSAGE + "=" + msgNum + "&" + PART + "=" + partNum + "\">Display Attachment</a>" );
        /*
        out.println( "<FORM ACTION=\"/servlet/moe99.JavaMailServlet\" METHOD=\"POST\" ENCTYPE=\"application/x-www-form-urlencoded\">" );
        out.println( "<input type=\"hidden\" name=\"output\" value=\"html\">" );
        out.println( "<input type=\"hidden\" name=\"action\" value=\"read\">" );
        out.println( "<input type=\"hidden\" name=\"message\" value=\"" + msgNum + "\">" );
        out.println( "<input type=\"hidden\" name=\"part\" value=\"" + partNum + "\">" );
        out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"show\" VALUE=\"show attachement\"></FORM>" );
        */
      }
    }
    catch( MessagingException mex )
    {
      out.println( mex.toString() );
    }
  }


  /**
   * This method gets the stream from for a given msg part and
   * pushes it out to the browser with the correct content type.
   * Used to display attachments and relies on the browser's
   * content handling capabilities.
   *
   * @param  mud              Description of Parameter
   * @param  msgNum           Description of Parameter
   * @param  partNum          Description of Parameter
   * @param  out              Description of Parameter
   * @param  res              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void displayPart( MailUserData mud, int msgNum, int partNum, ServletOutputStream out, HttpServletResponse res )
    throws IOException
  {

    Part part = null;

    try
    {
      Message msg = mud.getFolder().getMessage( msgNum );

      Multipart mp = (Multipart)msg.getContent();
      part = mp.getBodyPart( partNum );

      String sct = part.getContentType();
      if( sct == null )
      {
        out.println( "invalid part" );
        return;
      }
      ContentType ct = new ContentType( sct );

      res.setContentType( ct.getBaseType() );
      InputStream is = part.getInputStream();
      int i;
      while( ( i = is.read() ) != -1 )
        out.write( i );
      out.flush();
      //out.close();
    }
    catch( MessagingException mex )
    {
      out.println( mex.toString() );
    }
  }


  /**
   * This is a utility message that pretty-prints the message
   * headers for message that is being displayed.
   *
   * @param  mud              Description of Parameter
   * @param  msg              Description of Parameter
   * @param  out              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void displayMessageHeadersHtml( MailUserData mud, Message msg, ServletOutputStream out )
    throws IOException
  {
    try
    {
      out.println( "<b>Date:</b> " + msg.getSentDate() + "<br>" );

      Address[] fr = msg.getFrom();
      if( fr != null )
      {
        boolean tf = true;
        out.print( "<b>From:</b> " );
        for( int i = 0; i < fr.length; i++ )
        {
          out.print( ( ( tf ) ? " " : ", " ) + getDisplayAddress( fr[i] ) );
          tf = false;
        }
        out.println( "<br>" );
      }

      Address[] to = msg.getRecipients( Message.RecipientType.TO );
      if( to != null )
      {
        boolean tf = true;
        out.print( "<b>To:</b> " );
        for( int i = 0; i < to.length; i++ )
        {
          out.print( ( ( tf ) ? " " : ", " ) + getDisplayAddress( to[i] ) );
          tf = false;
        }
        out.println( "<br>" );
      }

      Address[] cc = msg.getRecipients( Message.RecipientType.CC );
      if( cc != null )
      {
        boolean cf = true;
        out.print( "<b>CC:</b> " );
        for( int i = 0; i < cc.length; i++ )
        {
          out.print( ( ( cf ) ? " " : ", " ) + getDisplayAddress( cc[i] ) );
          cf = false;
        }
        out.println( "<br>" );
      }

      out.print( "<b>Subject:</b> " +
          ( ( msg.getSubject() != null ) ? msg.getSubject() : "" ) +
          "<br>" );

    }
    catch( MessagingException mex )
    {
      out.println( msg.toString() );
    }
  }


  /**
   * This method displays the URL's for the available commands and the
   * INBOX headerlist
   *
   * @param  mud              Description of Parameter
   * @param  req              Description of Parameter
   * @param  out              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void displayHeadersHtml( MailUserData mud, HttpServletRequest req, ServletOutputStream out )
    throws IOException
  {

    SimpleDateFormat df = new SimpleDateFormat( "EE M/d/yy" );

    //out.println( "<html>" );
    //out.println( "<HEAD><TITLE>JavaMail Servlet</TITLE></HEAD>" );
    //out.println( "<BODY bgcolor=\"#ccccff\"><hr>" );
    out.print( "<center><font face=\"Arial,Helvetica\" font size=\"3\">" );
    out.println( "<b>Folder " + mud.getStore().getURLName() + "/" + mud.getFolder().getName() + "</b></font></center><p>" );

    // URL's for the commands that are available
    /*
    out.println( "<font face=\"Arial,Helvetica\" font size=\"+3\"><b>" );
    out.println( "<a href=\"" +  req.getRequestURL() + "?action=logout&output=html\">Logout</a>" );
    out.println( "<a href=\"" +  req.getRequestURL() + "?action=compose&output=html\" target=\"compose\">Compose</a>" );
    out.println( "</b></font>" );
    */
    out.println( "<FORM ACTION=\"/servlet/moe99.JavaMailServlet\" METHOD=\"POST\" ENCTYPE=\"application/x-www-form-urlencoded\">" );
    out.println( "<input type=\"hidden\" name=\"output\" value=\"" + HTML + "\">" );
    out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"action\" VALUE=\"" + LOGOUT + "\">" );
    out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"action\" VALUE=\"" + COMPOSE + "\"></FORM>" );
    out.println( "<hr>" );

    // List headers in a table
    out.print( "<table cellpadding=1 cellspacing=1 " );
    // table
    out.println( "width=\"100%\" border=1>" );
    // settings

    /*
    // read button column header
    out.println( "<tr><td width=\"5%\" bgcolor=\"ffffcc\">" );
    out.println( "<font face=\"Arial,Helvetica\" font size=\"+1\">" );
    out.println( "<b>M</b></font></td>" );
    */
    // sender column header
    out.println( "<tr><td width=\"25%\" bgcolor=\"ffffcc\">" );
    out.println( "<font face=\"Arial,Helvetica\" font size=\"+1\">" );
    out.println( "<b>Sender</b></font></td>" );
    // date column header
    out.println( "<td width=\"15%\" bgcolor=\"ffffcc\">" );
    out.println( "<font face=\"Arial,Helvetica\" font size=\"+1\">" );
    out.println( "<b>Date</b></font></td>" );
    // subject column header
    out.println( "<td bgcolor=\"ffffcc\">" );
    out.println( "<font face=\"Arial,Helvetica\" font size=\"+1\">" );
    out.println( "<b>Subject</b></font></td></tr>" );

    try
    {
      Folder f = mud.getFolder();
      int msgCount = f.getMessageCount();
      Message m = null;

      //out.println( "<FORM ACTION=\"/servlet/moe99.JavaMailServlet\" METHOD=\"POST\" ENCTYPE=\"application/x-www-form-urlencoded\">" );
      //out.println( "<input type=\"hidden\" name=\"output\" value=\"html\">" );

      // for each message, show its headers
      for( int i = 1; i <= msgCount; i++ )
      {
        m = f.getMessage( i );

        // if message has the DELETED flag set, don't display it
        if( m.isSet( Flags.Flag.DELETED ) )
          continue;

        // from
        out.println( "<tr valigh=middle>" );
        out.print( "<td width=\"25%\" bgcolor=\"ffffff\">" );
        out.println( "<font face=\"Arial,Helvetica\">"
             + ( ( m.getFrom() != null ) ? m.getFrom()[0].toString() : "" ) + "</font></td>" );

        // date
        out.print( "<td nowrap width=\"15%\" bgcolor=\"ffffff\">" );
        out.println( "<font face=\"Arial,Helvetica\">"
             + df.format( ( m.getSentDate() != null ) ? m.getSentDate() :
            ( ( m.getReceivedDate() != null ) ? m.getReceivedDate() : new Date( 0 ) ) ) + "</font></td>" );

        // subject & link
        out.print( "<td bgcolor=\"ffffff\">" );
        out.println( "<font face=\"Arial,Helvetica\">"
             + "<a href=\"" + req.getRequestURL()
             + "?" + ACTION + "=" + READ + "&" + OUTPUT + "=" + HTML + "&" + MESSAGE + "=" + i + "\">"
             + ( ( m.getSubject() != null ) ? m.getSubject() : "<i>No Subject</i>" )
             + "</a>" + "</font></td>" );
        out.println( "</tr>" );
        /*
        // from
        out.println( "<tr valigh=middle>" );
        out.print( "<td width=\"5%\" bgcolor=\"ffffff\">" );
        out.println( "<INPUT TYPE=\"SUBMIT\" NAME=\"message\" VALUE=\"" + i + "\">" );
        out.print( "</td>" );
        out.print( "<td width=\"25%\" bgcolor=\"ffffff\">" );
        out.println( "<font face=\"Arial,Helvetica\">"
            + ( ( m.getFrom() != null ) ? m.getFrom()[0].toString() : "" ) + "</font></td>" );
        // date
        out.print( "<td nowrap width=\"15%\" bgcolor=\"ffffff\">" );
        out.println( "<font face=\"Arial,Helvetica\">"
            + df.format( ( m.getSentDate() != null ) ? m.getSentDate() :
            ( ( m.getReceivedDate() != null ) ? m.getReceivedDate() : new Date( 0) ) ) + "</font></td>" );
        // subject & link
        out.print( "<td bgcolor=\"ffffff\">" );
        out.println( "<font face=\"Arial,Helvetica\">"
            + ( ( m.getSubject() != null ) ? m.getSubject() : "<i>No Subject</i>" )
            + "</font></td>" );
        out.println( "</tr>" );
        */
      }
      //out.println( "<INPUT TYPE=\"HIDDEN\" NAME=\"action\" VALUE=\"read\">" );
      //out.println( "</FORM>" );
    }
    catch( MessagingException mex )
    {
      out.println( "<tr><td>" + mex.toString() + "</td></tr>" );
      mex.printStackTrace();
    }

    out.println( "</table>" );
    //out.println( "</BODY></html>" );
    //out.flush();
    //out.close();
  }


  /**
   * This method handles the request when the user hits the
   * <i>Compose</i> link. It send the compose form to the browser.
   *
   * @param  mud              Description of Parameter
   * @param  res              Description of Parameter
   * @param  out              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void composeForm( MailUserData mud, HttpServletResponse res, ServletOutputStream out )
    throws IOException
  {

    res.setContentType( "text/html" );
    String user = mud.getFromAddress();

    String composeForm = "<FORM ENCTYPE=\"multipart/form-data\" METHOD=\"POST\" ACTION=\"/servlet/moe99.JavaMailServlet\">"
       + "<input type=\"hidden\" name=\"action\" value=\"" + SEND + "\"><input type=\"hidden\" name=\"output\" value=\"" + HTML + "\">"
       + "<P ALIGN=\"CENTER\"><B><FONT SIZE=\"4\" FACE=\"Verdana, Arial, Helvetica\">JavaMail Compose Message</FONT></B>"
       + "<P><TABLE BORDER=\"0\" WIDTH=\"100%\"><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">From:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"from\" VALUE=\"" + user + "\" SIZE=\"30\">"
       + "<FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + "</FONT></TD></TR><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">To:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"to\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">CC:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"cc\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">BCC:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"bcc\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">Subject:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"subject\" SIZE=\"55\"></TD></TR><TR><TD WIDTH=\"16%\">&nbsp;</TD>"
       + "<TD WIDTH=\"84%\" WRAP=\"PHYSICAL\"><TEXTAREA NAME=\"text\" ROWS=\"15\" COLS=\"53\"></TEXTAREA></TD></TR>"
       + "<TR><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">Attachment:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"FILE\" NAME=\"" + ATTACHMENT + "\" SIZE=\"55\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + "</FONT></TD></TR><TD WIDTH=\"16%\" HEIGHT=\"32\">&nbsp;</TD><TD WIDTH=\"84%\" HEIGHT=\"32\">"
       + "<INPUT TYPE=\"SUBMIT\" NAME=\"Send\" VALUE=\"Send\"><INPUT TYPE=\"RESET\" NAME=\"Reset\" VALUE=\"Reset\"></TD>"
       + "</TR></TABLE></FORM>";

    out.println( composeForm );
    out.close();
  }


  /**
   * This method handles the request when the user hits the
   * <i>reply</i> link. It send the reply form to the browser.
   *
   * @param  mud              Description of Parameter
   * @param  res              Description of Parameter
   * @param  out              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void replyForm( MailUserData mud, HttpServletResponse res, ServletOutputStream out, int msgNum )
    throws MessagingException, IOException
  {
    int cnt = 0;
    res.setContentType( "text/html" );
    String user = mud.getFromAddress();

    Message msg = mud.getFolder().getMessage( msgNum );
    String to = (msg.getFrom())[0].toString();
    String subj = msg.getSubject();

    if( !subj.substring( 0, 2).equalsIgnoreCase( "re:") && !subj.substring( 0, 2).equalsIgnoreCase( "aw:"))
      subj = "Re: " + subj;

    // and now, handle the content
    Object o = msg.getContent();
    String text = "";

    if( msg.isMimeType( "multipart/*" ) )
    {
      Multipart mp = (Multipart)o;
      cnt = mp.getCount();
      o = mp.getBodyPart( 0 ).getContent();
    }

    StringTokenizer st = new StringTokenizer( (String)o, "\n");

    while( st.hasMoreTokens())
    {
      text += "> " + st.nextToken() + "\n";
    }

    String composeForm = "<FORM ENCTYPE=\"multipart/form-data\" METHOD=\"POST\" ACTION=\"/servlet/moe99.JavaMailServlet\">"
       + "<input type=\"hidden\" name=\"action\" value=\"" + REPLY + "\"><input type=\"hidden\" name=\"output\" value=\"" + HTML + "\">"
       + "<input type=\"hidden\" name=\"message\" value=\"" + msgNum + "\">"
       + "<P ALIGN=\"CENTER\"><B><FONT SIZE=\"4\" FACE=\"Verdana, Arial, Helvetica\">JavaMail Compose Message</FONT></B>"
       + "<P><TABLE BORDER=\"0\" WIDTH=\"100%\"><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">From:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"from\" VALUE=\"" + user + "\" SIZE=\"30\">"
       + "<FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + "</FONT></TD></TR><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">To:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"to\" VALUE=\"" + to + "\" SIZE=\"30\" READONLY> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">CC:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"cc\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">BCC:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"bcc\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">Subject:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"subject\" VALUE=\"" + subj + "\" SIZE=\"55\" READONLY></TD></TR><TR><TD WIDTH=\"16%\">&nbsp;</TD>"
       + "<TD WIDTH=\"84%\"><TEXTAREA NAME=\"text\" ROWS=\"15\" COLS=\"53\" WRAP=\"PHYSICAL\"> \n" + to + " wrote:\n\n"
       + text + "</TEXTAREA></TD></TR>"
       + "<TR><TR><TD WIDTH=\"18%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">";
       composeForm += "<B><FONT FACE=\"Verdana, Arial, Helvetica\">Attachment:</FONT></B></TD><TD WIDTH=\"82%\" HEIGHT=\"22\">"
         + "<INPUT TYPE=\"FILE\" NAME=\"" + ATTACHMENT + "\" SIZE=\"55\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
         + "</FONT></TD></TR>";
/*
     {
       composeForm += "<B><FONT FACE=\"Verdana, Arial, Helvetica\">Attachments: </FONT></B></TD><TD WIDTH=\"82%\" HEIGHT=\"22\">"
         + "<FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">" + (cnt-1)
         + "</FONT></TD></TR>";
     }
*/
     composeForm += "<TD WIDTH=\"16%\" HEIGHT=\"32\">&nbsp;</TD><TD WIDTH=\"84%\" HEIGHT=\"32\">"
       + "<INPUT TYPE=\"SUBMIT\" NAME=\"Send\" VALUE=\"Send\"><INPUT TYPE=\"RESET\" NAME=\"Reset\" VALUE=\"Reset\"></TD>"
       + "</TR></TABLE></FORM>";

    out.println( composeForm );
    out.close();
  }


  /**
   * This method handles the request when the user hits the
   * <i>replyall</i> link. It send the reply form to the browser.
   *
   * @param  mud              Description of Parameter
   * @param  res              Description of Parameter
   * @param  out              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void replyAllForm( MailUserData mud, HttpServletResponse res, ServletOutputStream out, int msgNum )
    throws MessagingException, IOException
  {
    int cnt = 0;
    res.setContentType( "text/html" );
    String user = mud.getFromAddress();

    Message msg = mud.getFolder().getMessage( msgNum );
    String to = (msg.getFrom())[0].toString();
    String tos = to;
    int adrCount = Array.getLength( msg.getRecipients( Message.RecipientType.TO));
    for( int i = 0; i < adrCount; i++)
    {
      tos += ", " + (msg.getRecipients( Message.RecipientType.TO))[i].toString();
    }
    adrCount = Array.getLength( msg.getRecipients( Message.RecipientType.CC));
    for( int i = 0; i < adrCount; i++)
    {
      tos += ", " + (msg.getRecipients( Message.RecipientType.CC))[i].toString();
    }
    String subj = msg.getSubject();

    if( !subj.substring( 0, 2).equalsIgnoreCase( "re:") && !subj.substring( 0, 2).equalsIgnoreCase( "aw:"))
      subj = "Re: " + subj;

    // and now, handle the content
    Object o = msg.getContent();
    String text = "";

    if( msg.isMimeType( "multipart/*" ) )
    {
      Multipart mp = (Multipart)o;
      cnt = mp.getCount();
      o = mp.getBodyPart( 0 ).getContent();
    }

    StringTokenizer st = new StringTokenizer( (String)o, "\n");

    while( st.hasMoreTokens())
    {
      text += "> " + st.nextToken() + "\n";
    }

    String composeForm = "<FORM ENCTYPE=\"multipart/form-data\" METHOD=\"POST\" ACTION=\"/servlet/moe99.JavaMailServlet\">"
       + "<input type=\"hidden\" name=\"action\" value=\""+ REPLYALLFORM + "\"><input type=\"hidden\" name=\"output\" value=\"" + HTML + "\">"
       + "<input type=\"hidden\" name=\"message\" value=\"" + msgNum + "\">"
       + "<P ALIGN=\"CENTER\"><B><FONT SIZE=\"4\" FACE=\"Verdana, Arial, Helvetica\">JavaMail Compose Message</FONT></B>"
       + "<P><TABLE BORDER=\"0\" WIDTH=\"100%\"><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">From:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"from\" VALUE=\"" + user + "\" SIZE=\"30\">"
       + "<FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + "</FONT></TD></TR><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">To:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"to\" VALUE=\"" + tos + "\" SIZE=\"30\" READONLY> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">CC:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"cc\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">BCC:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"bcc\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">Subject:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"subject\" VALUE=\"" + subj + "\" SIZE=\"55\" READONLY></TD></TR><TR><TD WIDTH=\"16%\">&nbsp;</TD>"
       + "<TD WIDTH=\"84%\"><TEXTAREA NAME=\"text\" ROWS=\"15\" COLS=\"53\" WRAP=\"PHYSICAL\"> \n" + to + " wrote:\n\n"
       + text + "</TEXTAREA></TD></TR>"
       + "<TR><TR><TD WIDTH=\"18%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">";
     composeForm += "<B><FONT FACE=\"Verdana, Arial, Helvetica\">Attachment:</FONT></B></TD><TD WIDTH=\"82%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"FILE\" NAME=\"" + ATTACHMENT + "\" SIZE=\"55\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + "</FONT></TD></TR>";
     composeForm += "<TD WIDTH=\"16%\" HEIGHT=\"32\">&nbsp;</TD><TD WIDTH=\"84%\" HEIGHT=\"32\">"
       + "<INPUT TYPE=\"SUBMIT\" NAME=\"Send\" VALUE=\"Send\"><INPUT TYPE=\"RESET\" NAME=\"Reset\" VALUE=\"Reset\"></TD>"
       + "</TR></TABLE></FORM>";

    out.println( composeForm );
    out.close();
  }


  /**
   * This method handles the request when the user hits the
   * <i>forward</i> link. It send the reply form to the browser.
   *
   * @param  mud              Description of Parameter
   * @param  res              Description of Parameter
   * @param  out              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void forwardForm( MailUserData mud, HttpServletResponse res, ServletOutputStream out, int msgNum )
    throws MessagingException, IOException
  {
    int cnt = 0;
    res.setContentType( "text/html" );
    String user = mud.getFromAddress();

    Message msg = mud.getFolder().getMessage( msgNum );
    String subj = msg.getSubject();

    if( !subj.substring( 0, 3).equalsIgnoreCase( "fwd:") && !subj.substring( 0, 2).equalsIgnoreCase( "fw:"))
      subj = "Fwd: " + subj;

    String composeForm = "<FORM ENCTYPE=\"multipart/form-data\" METHOD=\"POST\" ACTION=\"/servlet/moe99.JavaMailServlet\">"
       + "<input type=\"hidden\" name=\"action\" value=\"" + FORWARD + "\"><input type=\"hidden\" name=\"output\" value=\"" + HTML + "\">"
       + "<input type=\"hidden\" name=\"message\" value=\"" + msgNum + "\">"
       + "<P ALIGN=\"CENTER\"><B><FONT SIZE=\"4\" FACE=\"Verdana, Arial, Helvetica\">JavaMail Compose Message</FONT></B>"
       + "<P><TABLE BORDER=\"0\" WIDTH=\"100%\"><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">From:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"from\" VALUE=\"" + user + "\" SIZE=\"30\">"
       + "<FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + "</FONT></TD></TR><TR><TD WIDTH=\"16%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">To:</FONT></B></TD><TD WIDTH=\"84%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"to\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">CC:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"cc\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">BCC:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"bcc\" SIZE=\"30\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + " (separate addresses with commas)</FONT></TD></TR><TR><TD WIDTH=\"16%\"><P ALIGN=\"RIGHT\">"
       + "<B><FONT FACE=\"Verdana, Arial, Helvetica\">Subject:</FONT></B></TD><TD WIDTH=\"84%\">"
       + "<INPUT TYPE=\"TEXT\" NAME=\"subject\" VALUE=\"" + subj + "\" SIZE=\"55\"></TD></TR><TR><TD WIDTH=\"16%\">&nbsp;</TD>"
       + "<TD WIDTH=\"84%\"><TEXTAREA NAME=\"text\" ROWS=\"15\" COLS=\"53\" WRAP=\"PHYSICAL\"></TEXTAREA></TD></TR>";
     composeForm += "<TR><TD WIDTH=\"18%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\"><B><FONT FACE=\"Verdana, Arial, Helvetica\"> </FONT></B></TD><TD WIDTH=\"82%\" HEIGHT=\"22\">"
       + "<FONT SIZE=\"2\" FACE=\"Verdana, Arial, Helvetica\">The original message will be added!"
       + "</FONT></TD></TR>";
     composeForm += "<TR><TD WIDTH=\"18%\" HEIGHT=\"22\">	<P ALIGN=\"RIGHT\"><B><FONT FACE=\"Verdana, Arial, Helvetica\">Attachment:</FONT></B></TD><TD WIDTH=\"82%\" HEIGHT=\"22\">"
       + "<INPUT TYPE=\"FILE\" NAME=\"" + ATTACHMENT + "\" SIZE=\"55\"> <FONT SIZE=\"1\" FACE=\"Verdana, Arial, Helvetica\">"
       + "</FONT></TD></TR>";
     composeForm += "<TD WIDTH=\"16%\" HEIGHT=\"32\">&nbsp;</TD><TD WIDTH=\"84%\" HEIGHT=\"32\">"
       + "<INPUT TYPE=\"SUBMIT\" NAME=\"Send\" VALUE=\"Send\"><INPUT TYPE=\"RESET\" NAME=\"Reset\" VALUE=\"Reset\"></TD>"
       + "</TR></TABLE></FORM>";

    out.println( composeForm );
    out.close();
  }


  /**
   * This method processes the send request from the compose form
   *
   * @param  req              Description of Parameter
   * @param  res              Description of Parameter
   * @param  out              Description of Parameter
   * @param  mud              Description of Parameter
   * @exception  IOException  Description of Exception
   */
  private void sendHtml( HttpServletRequest req, ServletOutputStream out)
    throws IOException
  {
    try
    {
        out.println( "<h3>Message sent successfully</h3>" );
        out.println( "<font face=\"Arial,Helvetica\">"
             + "<a href=\"" + req.getRequestURL()
             + "?" + ACTION + "=" + SEND + "&" + OUTPUT + "=" + HTML + ">Mail list</a>"
             + "</font>" );
        //out.println( "<h1>Error sending message.</h1>" );

      //out.close();

    }
    catch( Exception mex )
    {
      out.println( "<h1>Error sending message.</h1>" );
      out.println( mex.toString() );
      out.println( "<br>" );
    }
  }


  /**
   * This method processes the send request from the compose form
   *
   * @param  req              Description of Parameter
   * @param  res              Description of Parameter
   * @param  out              Description of Parameter
   * @param  mud              Description of Parameter
   * @return                  Description of the Returned Value
   * @exception  IOException  Description of Exception
   */
  private boolean send( HttpServletRequest req, HttpServletResponse res, ServletOutputStream out, MailUserData mud )
    throws IOException
  {
    String from = req.getParameter( FROM );
    String replyto = req.getParameter( REPLYTO );
    String to = req.getParameter( TO );
    String cc = req.getParameter( CC );
    String bcc = req.getParameter( BCC );
    String subj = req.getParameter( SUBJECT );
    String text = req.getParameter( TEXT );
    boolean blRet = false;

    try
    {
      if( mud == null )
        throw new Exception( "trying to send, but not logged in" );

      Message msg = new MimeMessage( mud.getSession() );
      InternetAddress[] toAddrs = null;
      InternetAddress[] ccAddrs = null;
      InternetAddress[] bccAddrs = null;
      InternetAddress[] replyAddrs = null;

      if( to != null )
      {
        toAddrs = InternetAddress.parse( to, false );
        msg.setRecipients( Message.RecipientType.TO, toAddrs );
      }
      else
        throw new MessagingException( "No \"To\" address specified" );

      if( cc != null )
      {
        ccAddrs = InternetAddress.parse( cc, false );
        msg.setRecipients( Message.RecipientType.CC, ccAddrs );
      }

      if( bcc != null )
      {
        bccAddrs = InternetAddress.parse( bcc, false );
        msg.setRecipients( Message.RecipientType.BCC, bccAddrs );
      }

      if( subj != null )
        msg.setSubject( subj );

      URLName u = mud.getURLName();
      if( from != null )
        msg.setFrom( new InternetAddress( from ) );
      else
        msg.setFrom( new InternetAddress( u.getUsername() + "@" + u.getHost() ) );

      if( replyto != null )
      {
        replyAddrs = InternetAddress.parse( replyto, false );
        msg.setReplyTo( replyAddrs);
      }

      if( text != null )
        msg.setText( text );

      Transport.send( msg );

      blRet = true;
    }
    catch( Exception mex )
    {
      out.println( mex.toString() );
    }
    return blRet;
  }
}

/**
 * Holds the data sources
 *
 * @author     Andreas Eisenhauer
 * @created    30. Oktober 2001
 */
class ByteArrayDataSource
     implements DataSource
{
  byte[] m_bytes;
  String m_contentType, m_name;


  /**
   *Constructor for the ByteArrayDataSource object
   *
   * @param  bytes        Description of Parameter
   * @param  contentType  Description of Parameter
   * @param  name         Description of Parameter
   */
  ByteArrayDataSource( byte[] bytes, String contentType, String name )
  {
    m_bytes = bytes;
    if( contentType == null )
      m_contentType = "application/octet-stream";
    else
      m_contentType = contentType;
    m_name = name;
  }


  /**
   *Gets the ContentType attribute of the ByteArrayDataSource object
   *
   * @return    The ContentType value
   */
  public String getContentType()
  {
    return m_contentType;
  }


  /**
   *Gets the InputStream attribute of the ByteArrayDataSource object
   *
   * @return    The InputStream value
   */
  public InputStream getInputStream()
  {
    // remove the final CR/LF
    return new ByteArrayInputStream( m_bytes, 0, m_bytes.length - 2 );
  }


  /**
   *Gets the Name attribute of the ByteArrayDataSource object
   *
   * @return    The Name value
   */
  public String getName()
  {
    return m_name;
  }


  /**
   *Gets the OutputStream attribute of the ByteArrayDataSource object
   *
   * @return                  The OutputStream value
   * @exception  IOException  Description of Exception
   */
  public OutputStream getOutputStream()
    throws IOException
  {
    throw new FileNotFoundException();
  }
}

/**
 * This class is used to store session data for each user's session. It
 * is stored in the HttpSession.
 *
 * @author     Andreas Eisenhauer
 * @created    26. Oktober 2001
 */
class MailUserData
{
  URLName m_url;
  Session m_session;
  Store m_store;
  Folder m_folder;
  Folder m_rootSubFolders[];
  Folder m_currentSubFolders[];
  String m_smtpHost;


  /**
   *Constructor for the MailUserData object
   *
   * @param  urlname  Description of Parameter
   */
  public MailUserData( URLName urlname )
  {
    m_url = urlname;
  }


  /**
   *Sets the Session attribute of the MailUserData object
   *
   * @param  s  The new Session value
   */
  public void setSession( Session s )
  {
    m_session = s;
  }


  /**
   *Sets the smtpHost attribute of the MailUserData object
   *
   * @param  s  The new smtpHost value
   */
  public void setSmtpHost( String s )
  {
    m_smtpHost = s;
  }


  /**
   *Sets the Store attribute of the MailUserData object
   *
   * @param  s  The new Store value
   */
  public void setStore( Store s )
  {
    m_store = s;
  }


  /**
   *Sets the Folder attribute of the MailUserData object
   *
   * @param  f  The new Folder value
   */
  public void setFolder( Folder f )
  {
    m_folder = f;
  }


  /**
   *Sets the rootSubFolders attribute of the MailUserData object
   *
   * @param  f  The new rootSubFolders value
   */
  public void setRootSubFolders( Folder f[] )
  {
    m_rootSubFolders = f;
  }


  /**
   *Sets the currentSubFolders attribute of the MailUserData object
   *
   * @param  f  The new currentSubFolders value
   */
  public void setCurrentSubFolders( Folder f[] )
  {
    m_currentSubFolders = f;
  }


  /**
   *Gets the URLName attribute of the MailUserData object
   *
   * @return    The URLName value
   */
  public URLName getURLName()
  {
    return m_url;
  }


  /**
   *Gets the Session attribute of the MailUserData object
   *
   * @return    The Session value
   */
  public Session getSession()
  {
    return m_session;
  }


  /**
   *Gets the smtpHost attribute of the MailUserData object
   *
   * @return    The smtpHost value
   */
  public String getSmtpHost()
  {
    return m_smtpHost;
  }


  /**
   *Gets the Store attribute of the MailUserData object
   *
   * @return    The Store value
   */
  public Store getStore()
  {
    return m_store;
  }


  /**
   *Gets the Folder attribute of the MailUserData object
   *
   * @return    The Folder value
   */
  public Folder getFolder()
  {
    return m_folder;
  }


  /**
   *Gets the rootSubFolders attribute of the MailUserData object
   *
   * @return    The rootSubFolders value
   */
  public Folder[] getRootSubFolders()
  {
    return m_rootSubFolders;
  }


  /**
   *Gets the currentSubFolders attribute of the MailUserData object
   *
   * @return    The currentSubFolders value
   */
  public Folder[] getCurrentSubFolders()
  {
    return m_currentSubFolders;
  }


  /**
   * This method extract the from address
   *
   * @param  mud              Description of Parameter
   * @return                  from address
   */
  public String getFromAddress()
  {
    String userAddress;
    String user = m_url.getUsername();
    String host = m_url.getHost();
    host = host.toLowerCase();

    if( host.startsWith( "pop3.") || host.startsWith( "mail.") || host.startsWith( "imap."))
      host = host.substring( 5);
    else if( host.startsWith( "pop."))
      host = host.substring( 4);

    userAddress = user + "@" + host;
    return userAddress;
  }
}


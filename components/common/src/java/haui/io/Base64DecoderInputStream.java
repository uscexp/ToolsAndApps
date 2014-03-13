package haui.io;

import java.io.*;

import java.util.*;

/**
 * A filter input stream that decodes data <i>on the fly</i> from BASE64 format
 * as specified in the
 * <a href="http://www.cis.ohio-state.edu/cgi-bin/rfc/rfc1521.html">MIME
 * specification</a>.
 *
 * @version $Revision: 1.0 $
 */
public class Base64DecoderInputStream
  extends FilterInputStream
{

  private static final String CVSVERSION = "$Revision: 1.0 $";

  private static final int MIN_BUFFER_SIZE = 8;
  private byte[] buffer;
  private int[] readybuffer;
  private byte[] chunk;
  private boolean closed = false;
  private int ready = 0;
  private int curbyte;
  private int curlimit;
  private int buffer_size;

  //
  // costructors
  //

  /** Creates a new instance of Base64DecoderInputStream */
  public Base64DecoderInputStream( InputStream in )
  {
    super( in );
    this.buffer_size = 1024;
    this.newBuffers();
  }

  /** Creates a new instance of Base64DecoderInputStream */
  public Base64DecoderInputStream( InputStream in, int bufferSize )
  {
    super( in );
    this.buffer_size = ( bufferSize > MIN_BUFFER_SIZE ) ? bufferSize : MIN_BUFFER_SIZE;
    this.newBuffers();
  }

  //
  // public methods
  //

  /** returns buffer size currently used */
  public int getBufferSize()
  {
    return buffer_size;
  }

  public synchronized int read()
    throws IOException
  {
    if( !closed )
    {
      return readInternal();
    }
    else
    {
      throw new IOException( "stream closed" );
    }
  }

  public int read( byte b[] )
    throws IOException
  {
    return this.read( b, 0, b.length );
  }

  public synchronized int read( byte b[], int off, int len )
    throws IOException
  {

    if( ( off < 0 ) || ( len < 0 ) || ( ( off + len ) > b.length ) )
    {
      throw new IndexOutOfBoundsException(
        "both off and len have to be >= 0 and off+len has to be <= b.length" );
    }

    if( !closed )
    {

      int retVal = 0;
      try
      {
        int til = off + len;
        int c;
        for( int i = off; i < til; ++i )
        {
          c = this.readInternal();
          if( c == -1 )
          {
            return retVal;
          }
          retVal++;
          b[i] = ( byte )c;
        }
      }
      catch( IOException e )
      {
        if( retVal == 0 )
        {
          throw e;
        }
      }
      return retVal;
    }
    else
    {
      throw new IOException( "stream closed" );
    }
  }

  public synchronized void close()
    throws IOException
  {
    this.closed = true;
    this.buffer = null;
    this.readybuffer = null;
    this.chunk = null;
    super.close();
  }

  /**
   * Encodes and decodes Files in Base64 format. This program was originally
   * designed for testing purposes only, that's why it is not really very
   * convenient.
   *
   * <p>Usage:<br>
   * <code>Base64DecoderInputStream sourcefile targetfile (encode|decode)</code></p>
   *
   * <ul><li><code>sourcefile</code> - source file to read from, will not be modified.</li>
   *     <li><code>targetfile</code> - target file to write the result of the coding/decoding
   *                                    process into. If the specified file already exists, it
   *                                    will be overwritten.</li>
   *     <li><code>(encode|decode)</code> - switch telling the program whether it should
   *                                         <i>encode</i> source file to target file or
   *                                         <i>decode</i> it. Just plainly use the signal
   *                                         "encode" or "decode" as a 3rd command line
   *                                         argument.</li>
   * </ul>
   */
  public static void main( String[] args )
    throws Exception
  {

    System.out.println( "Base64DecoderInputStream  " + CVSVERSION );
    System.out.println( "Base64EncoderOutputStream " + Base64EncoderOutputStream.CVSVERSION );
    System.out.println( "Author: Christian Ey <cey@mgm-edv.de>" );
    System.out.println();
    if( args.length < 3 )
    {
      System.out.println( "usage: Base64DecoderInputStream sourcefile targetfile (encode|decode)" );
    }
    else
    {
      File source = new File( args[0] );
      File target = new File( args[1] );
      boolean code = args[2].equalsIgnoreCase( "encode" );
      System.out.println( "source file: " + args[0] );
      System.out.println( "target file: " + args[1] );
      System.out.print( ( ( code ) ? "en" : "de" ) + "coding..." );

      long timestamp = System.currentTimeMillis();
      InputStream in;
      OutputStream out;
      if( code )
      {
        in = new FileInputStream( source );
        out = new Base64EncoderOutputStream( new FileOutputStream( target ) );
      }
      else
      {
        in = new Base64DecoderInputStream( new FileInputStream( source ) );
        out = new FileOutputStream( target );
      }

      int count = 0;
      byte[] cbuf = new byte[4096];

      while( ( count = in.read( cbuf, 0, cbuf.length ) ) == cbuf.length )
      {
        out.write( cbuf, 0, count );
      }
      if( count != -1 )
      {
        out.write( cbuf, 0, count );
      }

      out.close();
      in.close();
      System.out.println( "processed in " + ( System.currentTimeMillis() - timestamp ) + " ms." );
    }
  }

  //
  // private methods
  //

  private void newBuffers()
  {
    this.buffer = new byte[this.buffer_size];
    this.readybuffer = new int[this.buffer_size];
    this.chunk = new byte[4];
  }

  private int get1( byte buf[], int off )
  {
    return( ( buf[off] & 0x3f ) << 2 ) | ( ( buf[off + 1] & 0x30 ) >>> 4 );
  }

  private int get2( byte buf[], int off )
  {
    return( ( buf[off + 1] & 0x0f ) << 4 ) | ( ( buf[off + 2] & 0x3c ) >>> 2 );
  }

  private int get3( byte buf[], int off )
  {
    return( ( buf[off + 2] & 0x03 ) << 6 ) | ( buf[off + 3] & 0x3f );
  }

  private int check( int ch )
  {
    if( ( ch >= 'A' ) && ( ch <= 'Z' ) )
    {
      return ch - 'A';
    }
    else if( ( ch >= 'a' ) && ( ch <= 'z' ) )
    {
      return ch - 'a' + 26;
    }
    else if( ( ch >= '0' ) && ( ch <= '9' ) )
    {
      return ch - '0' + 52;
    }
    else
    {
      switch( ch )
      {
        case '=':
          return 65;
        case '+':
          return 62;
        case '/':
          return 63;
        default:
          return -1;
      }
    }
  }

  /** Writes into internal buffer. */
  private void bwrite( int i )
  {
    this.readybuffer[this.curlimit++] = i;
  }

  private void fill()
    throws IOException
  {
    int got = -1;
    this.curlimit = 0;
    this.curbyte = 0;

    fill:
    {
      if( ( got = in.read( buffer ) ) > 0 )
      {
        int skiped = 0;
        int ch;
        while( skiped < got )
        {
          // Check for un-understood characters:
          while( ready < 4 )
          {
            if( skiped >= got )
            {
              break fill;
            }
            ch = check( buffer[skiped++] );
            if( ch >= 0 )
            {
              chunk[ready++] = ( byte )ch;
            }
          }
          if( chunk[2] == 65 )
          {
            bwrite( get1( chunk, 0 ) );
            return;
          }
          else if( chunk[3] == 65 )
          {
            bwrite( get1( chunk, 0 ) );
            bwrite( get2( chunk, 0 ) );
            return;
          }
          else
          {
            bwrite( get1( chunk, 0 ) );
            bwrite( get2( chunk, 0 ) );
            bwrite( get3( chunk, 0 ) );
          }
          ready = 0;
        }
      }
      else
      {
        // there's nothing to read anymore: inputStream empty
        bwrite( -1 );
        if( ready != 0 )
        {
          // not reliable, therefore not used:
          //throw new IOException( "invalid length detected while decoding Base64");
        }
      }
    }
  }

  private int readInternal()
    throws IOException
  {
    if( this.curbyte >= this.curlimit )
    {
      this.fill();
    }

    return this.readybuffer[this.curbyte++];
  }
}
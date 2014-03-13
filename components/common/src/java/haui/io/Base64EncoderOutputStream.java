package haui.io;

import java.io.*;

/**
 * A filter output stream that encodes data <i>on the fly</i> into BASE64
 * format as specified in the
 * <a href="http://www.cis.ohio-state.edu/cgi-bin/rfc/rfc1521.html">MIME
 * specification</a>.
 *
 * @version $Revision: 1.0 $
 */
public class Base64EncoderOutputStream
  extends FilterOutputStream
{

  protected static final String CVSVERSION = "$Revision: 1.0 $";

  private static final int MIN_BUFFER_SIZE = 8;
  private static final int DEFAULT_BUFFER_SIZE = 1024;
  private int buffer_size;
  private byte[] buffer = null;
  private int curbyte = 0;
  private int count = 0;
  private boolean closed = false;

  private static byte[] encoding =
                                   {
                                   ( byte )'A', ( byte )'B', ( byte )'C', ( byte )'D',
                                   ( byte )'E', ( byte )'F', ( byte )'G', ( byte )'H',
                                   ( byte )'I', ( byte )'J', ( byte )'K', ( byte )'L',
                                   ( byte )'M', ( byte )'N', ( byte )'O', ( byte )'P',
                                   ( byte )'Q', ( byte )'R', ( byte )'S', ( byte )'T',
                                   ( byte )'U', ( byte )'V', ( byte )'W', ( byte )'X',
                                   ( byte )'Y', ( byte )'Z', ( byte )'a', ( byte )'b',
                                   ( byte )'c', ( byte )'d', ( byte )'e', ( byte )'f',
                                   ( byte )'g', ( byte )'h', ( byte )'i', ( byte )'j',
                                   ( byte )'k', ( byte )'l', ( byte )'m', ( byte )'n',
                                   ( byte )'o', ( byte )'p', ( byte )'q', ( byte )'r',
                                   ( byte )'s', ( byte )'t', ( byte )'u', ( byte )'v',
                                   ( byte )'w', ( byte )'x', ( byte )'y', ( byte )'z',
                                   ( byte )'0', ( byte )'1', ( byte )'2', ( byte )'3',
                                   ( byte )'4', ( byte )'5', ( byte )'6', ( byte )'7',
                                   ( byte )'8', ( byte )'9', ( byte )'+', ( byte )'/',
                                   ( byte )'='
  };

  //
  // constructors
  //

  /** Creates a new instance of Base64EncoderOutputStream */
  public Base64EncoderOutputStream( OutputStream out )
  {
    super( new BufferedOutputStream( out, DEFAULT_BUFFER_SIZE ) );
    this.buffer_size = DEFAULT_BUFFER_SIZE;
    this.buffer = new byte[this.buffer_size];
  }

  /** Creates a new instance of Base64EncoderOutputStream */
  public Base64EncoderOutputStream( OutputStream out, int bufferSize )
  {
    super( new BufferedOutputStream( out,
                                     ( bufferSize > MIN_BUFFER_SIZE ) ? bufferSize
                                     : MIN_BUFFER_SIZE ) );
    this.buffer_size = ( bufferSize > MIN_BUFFER_SIZE ) ? bufferSize : MIN_BUFFER_SIZE;
    this.buffer = new byte[this.buffer_size];
  }

  //
  // public methods
  //

  /** returns buffer size currently used */
  public int getBufferSize()
  {
    return buffer_size;
  }

  public synchronized void write( int b )
    throws IOException
  {
    if( !this.closed )
    {
      this.writeInternal( ( byte )b );
    }
    else
    {
      throw new IOException( "stream closed" );
    }
  }

  public void write( byte b[] )
    throws IOException
  {
    this.write( b, 0, b.length );
  }

  public synchronized void write( byte b[], int off, int len )
    throws IOException
  {
    if( !this.closed )
    {
      for( int i = 0; i < len; i++ )
      {
        this.writeInternal( b[off + i] );
      }
    }
    else
    {
      throw new IOException( "stream closed" );
    }
  }

  public synchronized void flush()
    throws IOException
  {
    this.flushInternal();
    out.flush();
  }

  public synchronized void close()
    throws IOException
  {
    if( !this.closed )
    {
      this.closed = true;
      this.flushInternal();
      this.finish();
      super.close();
    }
  }

  //
  // private methods
  //

  private static int get1( byte[] buf, int off )
  {
    return( buf[off] & 0xfc ) >> 2;
  }

  private static int get2( byte[] buf, int off )
  {
    return( ( buf[off] & 0x3 ) << 4 ) | ( ( buf[off + 1] & 0xf0 ) >>> 4 );
  }

  private static int get3( byte[] buf, int off )
  {
    return( ( buf[off + 1] & 0x0f ) << 2 ) | ( ( buf[off + 2] & 0xc0 ) >>> 6 );
  }

  private static final int get4( byte[] buf, int off )
  {
    return buf[off + 2] & 0x3f;
  }

  private void writeInternal( byte b )
    throws IOException
  {
    if( curbyte >= buffer_size )
    {
      this.flushInternal();
    }
    this.buffer[curbyte++] = b;
  }

  private void flushInternal()
    throws IOException
  {
    int got = this.curbyte;
    int off = 0;
    if( got >= 3 )
    {
      while( off + 3 <= got )
      {
        int c1 = get1( buffer, off );
        int c2 = get2( buffer, off );
        int c3 = get3( buffer, off );
        int c4 = get4( buffer, off );
        if( count < 73 )
        {
          out.write( encoding[c1] );
          out.write( encoding[c2] );
          out.write( encoding[c3] );
          out.write( encoding[c4] );
          count += 4;
        }
        else
        {
          if( count == 76 )
          {
            out.write( '\n' );
            count = 4;
          }
          out.write( encoding[c1] );
          if( count == 75 )
          {
            out.write( '\n' );
            count = 3;
          }
          out.write( encoding[c2] );
          if( count == 74 )
          {
            out.write( '\n' );
            count = 2;
          }
          out.write( encoding[c3] );
          if( count == 73 )
          {
            out.write( '\n' );
            count = 1;
          }
          out.write( encoding[c4] );
        }
        off += 3;
      }
      // Copy remaining bytes to beginning of buffer:
      for( int i = 0; i < 3; i++ )
        buffer[i] = ( i < got - off ) ? buffer[off + i] : ( ( byte )0 );
      off = got - off;
    }
    else
    {
      // Total read amount is less then 3 bytes:
      off = got;
    }
    this.curbyte = off;
  }

  private void finish()
    throws IOException
  {
    // Manage the last bytes, from 0 to curbyte:
    switch( this.curbyte )
    {
      case 1:
        out.write( encoding[get1( buffer, 0 )] );
        out.write( encoding[get2( buffer, 0 )] );
        out.write( '=' );
        out.write( '=' );
        break;
      case 2:
        out.write( encoding[get1( buffer, 0 )] );
        out.write( encoding[get2( buffer, 0 )] );
        out.write( encoding[get3( buffer, 0 )] );
        out.write( '=' );
    }
  }
}
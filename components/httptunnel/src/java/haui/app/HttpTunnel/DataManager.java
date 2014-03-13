package haui.app.HttpTunnel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import xjava.security.Cipher;
import xjava.security.CipherInputStream;
import xjava.security.CipherOutputStream;

/**
 *		Module:					DataManager.java<br>
 *										$Source: $
 *<p>
 *		Description:    Manages headers and bodies of given data.<br>
 *                    Field 1 (1 byte):  Length of action field. (byte)<br>
 *                    Field 2 (10 byte):  Action field. (byte encoded String)<br>
 *                    Field 3 (1 byte):  Length of buffer size field. (byte)<br>
 *                    Field 4 (10 byte):  Buffer size field. (byte encoded String)<br>
 *                    Field 5 (1 byte):  Length of checksum field. (byte)<br>
 *                    Field 6 (10 byte):  Checksum field. (byte encoded String)<br>
 *                    Field 7 (1 byte):  Length of session id field. (byte)<br>
 *                    Field 8 (var bytes):  Session id field. (byte encoded String)<br>
 *</p><p>
 *		Created:				11.12.2001	by	AE
 *</p><p>
 *		@history				11.12.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2001; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class DataManager
{
  // constants
  public final static int HEADER_ACTIONLENGTH_LENGTH = 1;
  public final static int HEADER_BUFFERLENGTH_LENGTH = 1;
  public final static int HEADER_CHECKSUMLENGTH_LENGTH = 1;
  public final static int HEADER_COMPLENGTH_LENGTH = 1;
  public final static int HEADER_SESSIONIDLENGTH_LENGTH = 1;
  public final static int COMP = 1;
  public final static int CRYPT = 2;
  public final static int COMPCRYPT = 3;

  // member variables
  byte m_header[];
  int m_headerLen;
  byte[] m_inBuf;
  byte[] m_body;
  int m_bodyLen;
  byte[] m_finalBody;
  int m_finalBodyLen;
  int m_realInBufLen;
  int m_inBufLen;
  long m_lCheckSum;
  long m_lRealCheckSum;
  String m_action;
  String m_sessionId;
  String m_remoteHost;
  int m_remotePort;
  int m_iMode = 0;
  private Cipher m_inCipher;
  private Cipher m_outCipher;

  public DataManager( String action, byte[] buf, int bufLen, String remoteHost, int remotePort, String sessionId, int mode, Cipher outCipher)
    throws IOException
  {
    m_action = action;
    m_inBuf = buf;
    m_realInBufLen = bufLen;
    m_inBufLen = bufLen;
    m_remoteHost = remoteHost;
    m_remotePort = remotePort;
    m_sessionId = sessionId;
    MakeCheckSum();
    m_lCheckSum = m_lRealCheckSum;
    MakeHeader();
    MakeBody();
    m_iMode = mode;
    m_outCipher = outCipher;
    if( m_outCipher == null && m_iMode > 0 && m_iMode < 4)
    {
      m_iMode = m_iMode - 2;
      if( m_iMode < 0)
        m_iMode = 0;
    }
    switch( m_iMode)
    {
      case COMP:
      case CRYPT:
      case COMPCRYPT:
        deflate( m_outCipher);
        break;

      default:
        m_finalBody = null;
        m_finalBodyLen = -1;
        break;
    }
  }

  public DataManager( byte[] body, int bodyLen, Cipher inCipher)
    throws IOException
  {
    m_inCipher = inCipher;
    m_iMode = new Byte( body[0]).intValue();
    switch( m_iMode)
    {
      case COMP:
      case CRYPT:
      case COMPCRYPT:
        m_finalBody = body;
        m_finalBodyLen = bodyLen;
        inflate( body, inCipher);
        break;

      default:
        m_body = body;
        m_bodyLen = bodyLen;
        m_finalBody = null;
        m_finalBodyLen = -1;
        break;
    }
    ExtractHeader();
    ExtractBody();
    MakeCheckSum();
  }

  private void deflate( Cipher outCipher)
    throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // write mode flag (uncompressed and uncrypted)
    byte b[] = new byte[1];
    b[0] = (byte)m_iMode;
    baos.write( b, 0, 1);

    Deflater def = new Deflater( Deflater.BEST_COMPRESSION);
    OutputStream os = null;
    //System.out.println( "************* Deflate");
    //System.out.println( m_iMode);
    switch( m_iMode)
    {
      case COMP:
        os = new DeflaterOutputStream( baos, def);
        break;

      case CRYPT:
        os = new CipherOutputStream( baos, outCipher);
        break;

      case COMPCRYPT:
        os = new DeflaterOutputStream( new CipherOutputStream( baos, outCipher), def);
        break;
    }
    if( os == null)
      return;
    BufferedOutputStream bos = new BufferedOutputStream( os);
    //System.out.write( m_body);
    bos.write( m_body);
    bos.flush();
    //if( dos != null)
      //dos.finish();
    bos.close();
    m_finalBody = baos.toByteArray();
    m_finalBodyLen = baos.size();
    //System.out.println( "-----------------");
    //System.out.write( m_finalBody);
  }

  private void inflate( byte[] body, Cipher inCipher)
    throws IOException
  {
    int iLen = Array.getLength( body);
    iLen--;
    byte buf[] = new byte[512];
    byte bodyBuf[] = new byte[iLen];
    for( int i = 0; i < iLen; i++)
    {
      bodyBuf[i] = body[i+1];
    }
    // delete (uncompressed) compression flag
    //ByteArrayInputStream bais = new ByteArrayInputStream( body, 1, Array.getLength( body)-1);
    ByteArrayInputStream bais = new ByteArrayInputStream( bodyBuf);
    InputStream is = null;
    //System.out.println( "************* Inflate");
    //System.out.println( m_iMode);
    switch( m_iMode)
    {
      case COMP:
        is = new InflaterInputStream( bais);
        break;

      case CRYPT:
        is = new CipherInputStream( bais, inCipher);
        break;

      case COMPCRYPT:
        is = new InflaterInputStream( new CipherInputStream( bais, inCipher));
        break;
    }
    if( is == null)
      return;
    BufferedInputStream bis = new BufferedInputStream( is);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //System.out.write( bodyBuf, 0, iLen);
    //System.out.println( "xx");
    while( (iLen = bis.read( buf, 0, 512)) > 0)
    {
      //System.out.write( buf, 0, iLen);
      baos.write( buf, 0, iLen);
    }
    m_body = baos.toByteArray();
    m_bodyLen = baos.size();
    //System.out.write( bodyBuf);
    //System.out.println( "-----------------");
    //System.out.write( m_body);
    bis.close();
  }

  private void MakeCheckSum()
  {
    if( m_realInBufLen > 0)
    {
      for( int i = 0; i < m_realInBufLen; i++)
      {
        m_lRealCheckSum += new Byte( m_inBuf[i]).intValue();
      }
    }
  }

  private void ExtractHeader()
  {
    int i = 0;
    int j = 0;
    int iLen = (new Byte( m_body[0])).intValue();
    m_headerLen = iLen;
    iLen = (new Byte( m_body[1])).intValue();
    byte bText[] = new byte[iLen];

    // build header action part
    i = 2;
    for( j = 0; j < iLen; j++, i++)
    {
      bText[j] = m_body[i];
    }
    if( iLen > 0)
      m_action = new String( bText);

    // build header buffer length part
    iLen = (new Byte( m_body[i])).intValue();
    i++;
    byte bNum[] = new byte[iLen];
    for( j = 0; j < iLen; j++, i++)
    {
      bNum[j] = m_body[i];
    }
    if( iLen > 0)
    {
      String strLen = new String( bNum);
      m_inBufLen = Integer.parseInt( strLen);
    }

    // build header checksum part
    iLen = (new Byte( m_body[i])).intValue();
    i++;
    bNum = new byte[iLen];
    for( j = 0; j < iLen; j++, i++)
    {
      bNum[j] = m_body[i];
    }
    if( iLen > 0)
    {
      String strLen = new String( bNum);
      m_lCheckSum = Integer.parseInt( strLen);
    }

    // build header remote host part
    iLen = (new Byte( m_body[i])).intValue();
    i++;
    if( iLen > 0)
    {
      bText = new byte[iLen];
      for( j = 0; j < iLen; j++, i++)
      {
        bText[j] = m_body[i];
      }
      m_remoteHost = new String( bText);
    }

    // build header remote port part
    iLen = (new Byte( m_body[i])).intValue();
    i++;
    bNum = new byte[iLen];
    for( j = 0; j < iLen; j++, i++)
    {
      bNum[j] = m_body[i];
    }
    if( iLen > 0)
    {
      String strLen = new String( bNum);
      m_remotePort = Integer.parseInt( strLen);
    }

    // build header session id part
    iLen = (new Byte( m_body[i])).intValue();
    i++;
    if( iLen > 0)
    {
      bText = new byte[iLen];
      for( j = 0; j < iLen; j++, i++)
      {
        bText[j] = m_body[i];
      }
      m_sessionId = new String( bText);
    }
    m_headerLen = i;
    m_header = new byte[m_headerLen];
    for( i = 0; i < m_headerLen; i++)
    {
      m_header[i] = m_body[i];
    }
  }

  private void MakeHeader()
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int i = 0;
    int j = 0;
    int iLen = Array.getLength( m_action.getBytes());
    byte len[] = new byte[1];
    len[0] = 0;

    // reseve first byte for the header length
    baos.write( len, 0, 1);

    // build header action part
    len[0] = new Integer( iLen).byteValue();
    baos.write( len, 0, 1);
    i = iLen+2;
    baos.write( m_action.getBytes(), 0, iLen);

    // build header buffer length part
    String strLen = String.valueOf( m_realInBufLen);
    iLen = Array.getLength( strLen.getBytes());
    len[0] = new Integer( iLen).byteValue();
    baos.write( len, 0, 1);
    i += 1+iLen;
    baos.write( strLen.getBytes(), 0, iLen);

    // build header checksum part
    strLen = String.valueOf( m_lCheckSum);
    iLen = Array.getLength( strLen.getBytes());
    len[0] = new Integer( iLen).byteValue();
    baos.write( len, 0, 1);
    i += 1+iLen;
    baos.write( strLen.getBytes(), 0, iLen);

    // build header remote host part
    if( m_remoteHost == null)
      iLen = 0;
    else
      iLen = Array.getLength( m_remoteHost.getBytes());
    len[0] = new Integer( iLen).byteValue();
    baos.write( len, 0, 1);
    i += 1+iLen;
    if( m_remoteHost != null)
      baos.write( m_remoteHost.getBytes(), 0, iLen);

    // build header remote port part
    strLen = String.valueOf( m_remotePort);
    iLen = Array.getLength( strLen.getBytes());
    len[0] = new Integer( iLen).byteValue();
    baos.write( len, 0, 1);
    i += 1+iLen;
    baos.write( strLen.getBytes(), 0, iLen);

    // build header session id part
    if( m_sessionId == null)
      iLen = 0;
    else
      iLen = Array.getLength( m_sessionId.getBytes());
    len[0] = new Integer( iLen).byteValue();
    baos.write( len, 0, 1);
    i += 1+iLen;
    if( m_sessionId != null)
      baos.write( m_sessionId.getBytes(), 0, iLen);
    m_header = baos.toByteArray();
    m_headerLen = i;

    // write header length
    len[0] = new Integer( m_headerLen).byteValue();
    m_header[0] = len[0];
  }

  private void ExtractBody()
  {
    int i = 0;
    int j = 0;
    if( m_bodyLen-m_headerLen <= 0)
    {
      m_realInBufLen = 0;
      m_inBuf = null;
      return;
    }
    m_inBuf = new byte[m_bodyLen-m_headerLen];

    for( i = m_headerLen; i < m_bodyLen; i++, j++)
    {
      m_inBuf[j] = m_body[i];
    }
    m_realInBufLen = m_bodyLen-m_headerLen;
  }

  private void MakeBody()
  {
    int i = 0;
    m_body = new byte[m_headerLen+m_realInBufLen];
    long lCheckSum = 0;

    if( m_action == null)
      return;

    byte[] header = getHeader();;
    for( i = 0; i < m_headerLen; i++)
    {
      m_body[i] = header[i];
    }
    for( int j = 0; j < m_realInBufLen; j++, i++)
    {
      m_body[i] = m_inBuf[j];
    }
    if( m_body == null)
      m_bodyLen = 0;
    else
      m_bodyLen = Array.getLength( m_body);
  }

  public String getRemoteHost()
  {
    return m_remoteHost;
  }

  public int getRemotePort()
  {
    return m_remotePort;
  }

  public String getSessionId()
  {
    return m_sessionId;
  }

  public boolean isCheckSumCorrect()
  {
    return (m_lRealCheckSum == m_lCheckSum);
  }

  public boolean isBufferLengthCorrect()
  {
    return (m_realInBufLen == m_inBufLen);
  }

  public int getMode()
  {
    return m_iMode;
  }

  public byte[] getHeader()
  {
    return m_header;
  }

  public int getHeaderLength()
  {
    return m_headerLen;
  }

  public byte[] getBody()
  {
    if( m_finalBodyLen == -1)
      return m_body;
    else
      return m_finalBody;
  }

  public int getBodyLength()
  {
    if( m_finalBodyLen == -1)
      return m_bodyLen;
    else
      return m_finalBodyLen;
  }

  public int getInputBufferLength()
  {
    return m_inBufLen;
  }

  public int getRealInputBufferLength()
  {
    return m_realInBufLen;
  }

  public long getCheckSum()
  {
    return m_lCheckSum;
  }

  public long getRealCheckSum()
  {
    return m_lRealCheckSum;
  }

  public byte[] getInputBuffer()
  {
    return m_inBuf;
  }

  public String getAction()
  {
    return m_action;
  }
}

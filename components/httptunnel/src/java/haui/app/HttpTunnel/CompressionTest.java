package haui.app.HttpTunnel;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Array;
import javax.swing.JTextArea;
import java.util.zip.*;

/**
 *		Module:					CompressionTest.java<br>
 *										$Source: $
 *<p>
 *		Description:    Reads the input stream of a socket connection and saves the data in a buffer.<br>
 *</p><p>
 *		Created:				22.03.2001	by	AE
 *</p><p>
 *		@history				22.03.2001	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public class CompressionTest
{
  public CompressionTest()
  {
  }

  public void write()
  {
  }

  public static void main(String[] args)
    throws Exception
  {
    byte buf[] = new byte[8];
    CompressionTest compressionTest1 = new CompressionTest();
    String str = "Das ist der zu kompremierende String, welcher natürlich eine gewisse Länge vorweisen muss um das Kompremieren und Dekompremieren entsprechend testen zu können!";
    int iLen;

    Deflater def = new Deflater( Deflater.BEST_COMPRESSION);
    FileOutputStream fos = new FileOutputStream( "test.out");
    byte b[] = new byte[1];
    b[0] = 1;
    fos.write( b, 0, 1);
    DeflaterOutputStream dos = new DeflaterOutputStream( fos, def);
    BufferedOutputStream bos = new BufferedOutputStream( dos);
    bos.write( str.getBytes());
    bos.flush();
    dos.finish();
    bos.close();

    BufferedInputStream bis = new BufferedInputStream( new FileInputStream( "test.out"));
    if( (iLen = bis.read( buf, 0, 1)) == 1)
    {
      System.out.write( buf, 0, iLen);
    }
    InflaterInputStream iis = new InflaterInputStream(  bis);
    while( (iLen = iis.read( buf, 0, 8)) > 0)
    {
      System.out.write( buf, 0, iLen);
    }
    bis.close();
  }
}
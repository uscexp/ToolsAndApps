package haui.app.HttpTunnel;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Array;
import javax.swing.JTextArea;
import java.util.zip.*;
import java.security.*;
import java.security.interfaces.*;
import xjava.security.*;
import xjava.security.interfaces.*;
import cryptix.provider.rsa.*;

/**
 *		Module:					ObjectOutputStreamTest.java<br>
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
public class ObjectOutputStreamTest
{
  private static final SecureRandom prng = new SecureRandom();
  public ObjectOutputStreamTest()
  {
  }

  public void write()
  {
  }

  public static void main(String[] args)
    throws Exception
  {
    Cipher inCipher;
    Cipher outCipher;
    KeyPairGenerator keyPairGen;
    KeyPair keyPair;
    RawRSAPrivateKey privKey;
    RawRSAPublicKey pubKey;
    RawRSAPublicKey serverPubKey;

    Security.addProvider( new cryptix.provider.Cryptix());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream( baos);

    // generate Cipher objects for encoding and decoding
    inCipher = Cipher.getInstance( new RawRSACipher(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));
    outCipher = Cipher.getInstance( new RawRSACipher(), (Mode)null, PaddingScheme.getInstance( "PKCS7", "Cryptix"));

    keyPairGen = KeyPairGenerator.getInstance("RSA", "Cryptix");
    keyPairGen.initialize( 1024);
    keyPair = keyPairGen.generateKeyPair();
    privKey = (RawRSAPrivateKey)keyPair.getPrivate();
    pubKey = (RawRSAPublicKey)keyPair.getPublic();

    oos.writeObject( pubKey);
    oos.flush();
    oos.close();
    byte b[] = baos.toByteArray();

    ByteArrayInputStream bais = new ByteArrayInputStream( b);
    ObjectInputStream ois = new ObjectInputStream( bais);
    serverPubKey = (RawRSAPublicKey)ois.readObject();
    ois.close();

    if( pubKey.toString().equals( serverPubKey.toString()))
      System.out.println( "equal");
    else
      System.out.println( "not equal");

    outCipher.initEncrypt( serverPubKey);
    inCipher.initDecrypt( privKey);

    // *********************************************
    baos.reset();
    Deflater def = new Deflater( Deflater.BEST_COMPRESSION);
    CipherOutputStream cos = new CipherOutputStream( baos, outCipher);
    DeflaterOutputStream dos = new DeflaterOutputStream( cos, def);
    BufferedOutputStream bos = new BufferedOutputStream( dos);

    String text = "Das ist der Testtext!";

    bos.write( text.getBytes());
    bos.flush();
    bos.close();

    if( baos.size() > 0)
    {
      b = new byte[1024];
      int i = 0;
      bais = new ByteArrayInputStream( baos.toByteArray());
      CipherInputStream cis = new CipherInputStream( bais, inCipher);
      InflaterInputStream iis = new InflaterInputStream( cis);
      BufferedInputStream bis = new BufferedInputStream( iis);

      baos.reset();
      while( (i = iis.read( b)) > 0)
      {
        baos.write( b);
      }
      System.out.println( new String( baos.toByteArray()));
    }
  }
}
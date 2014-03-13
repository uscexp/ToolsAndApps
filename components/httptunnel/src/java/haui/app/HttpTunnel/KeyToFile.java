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

/**
 *		Module:					KeyToFile.java<br>
 *										$Source: $
 *<p>
 *		Description:    Reads the input stream of a socket connection and saves the data in a buffer.<br>
 *</p><p>
 *		Created:				19.04.2001	by	AE
 *</p><p>
 *		@history				19.04.2001	by	AE: Created.<br>
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
public class KeyToFile
{
  private static final SecureRandom prng = new SecureRandom();

  public KeyToFile()
  {
  }

  public void write()
  {
  }

  public static void main(String[] args)
    throws Exception
  {
    KeyGenerator keyGen;
    Key key;

    Security.addProvider( new cryptix.provider.Cryptix());
    PrintWriter pr = new PrintWriter( new OutputStreamWriter( new FileOutputStream( "KeyBytes.txt")));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream( baos);

    keyGen = KeyGenerator.getInstance( "Blowfish", "Cryptix");
    keyGen.initialize( new SecureRandom());
    key = keyGen.generateKey();

    oos.writeObject( key);
    oos.flush();
    oos.close();
    byte b[] = baos.toByteArray();
    int iSize = baos.size();

    pr.println( "public static final byte[] keyBytes = new byte[] {");
    int i = 0;
    while( i < iSize)
    {
      for( int j = 0; i < iSize && j < 16; ++i, ++j)
      {
        pr.print( b[i]);
        pr.print( ",");
      }
      pr.println();
    }
    pr.println( "};");
    pr.flush();
    pr.close();
  }
}
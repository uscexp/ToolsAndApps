package haui.app.HttpTunnel;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Array;

/**
 *		Module:					SimpleConnectionSpy.java<br>
 *										$Source: $
 *<p>
 *		Description:    Reads the input stream of a socket connection and writes it to another socket connection.<br>
 *                    Spying the datastream.
 *</p><p>
 *		Created:				10.01.2002	by	AE
 *</p><p>
 *		@history				10.01.2002	by	AE: Created.<br>
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
public class SimpleConnectionSpy
{
  public final static int BUFFERSIZE = 512;

  public SimpleConnectionSpy()
  {
  }

  public void spyConnection()
  {
    int connP = 21;
    int serverP = 21;
    String host = "svantwerpen.flur.zuerich.ubs.ch";
    byte buf[] = new byte[BUFFERSIZE];

    try
    {
      ServerSocket lis = new ServerSocket( connP);

      Socket sock = lis.accept();

      BufferedInputStream connBis = new BufferedInputStream( sock.getInputStream());
      BufferedOutputStream connBos = new BufferedOutputStream( sock.getOutputStream());

      Socket serverSock = new Socket( host, serverP);

      BufferedOutputStream serverBos = new BufferedOutputStream( serverSock.getOutputStream());
      BufferedInputStream serverBis = new BufferedInputStream( serverSock.getInputStream());

      InputReader connIr = new InputReader( connBis, null, false);
      connIr.start();

      InputReader serverIr = new InputReader( serverBis, null, false);
      serverIr.start();

      boolean blRead = false;
      while( connIr.isAlive() && serverIr.isAlive())
      {
        int iLen = 0;
        if( blRead)
        {
          iLen = connIr.read( buf, BUFFERSIZE);
          System.out.print( "Request: ");
          System.out.write( buf, 0, iLen);
          System.out.println();
          if( iLen > 0)
          {
            serverBos.write( buf, 0, iLen);
            serverBos.flush();
          }
          blRead = false;
        }

        //Thread.sleep( 500);

        iLen = serverIr.read( buf, BUFFERSIZE);
        System.out.print( "Response: ");
        System.out.write( buf, 0, iLen);
        System.out.println();
        if( iLen > 0)
        {
          blRead = true;
          connBos.write( buf, 0, iLen);
          connBos.flush();
        }
        Thread.sleep( 500);
      }

      serverBos.close();
      serverBis.close();
      serverSock.close();
      connBos.close();
      connBis.close();
      sock.close();
      lis.close();
    }
    catch( Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    SimpleConnectionSpy simpleConnectionSpy1 = new SimpleConnectionSpy();
    simpleConnectionSpy1.spyConnection();
  }
}
package haui.io;

import haui.util.AppProperties;

import javax.swing.JTextArea;
import java.io.*;

/**
 *
 * Module:      TextAreaPrintStream.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\TextAreaPrintStream.java,v $
 *<p>
 * Description: Reads output and/or error output in a thread and prints it to a JTextArea.<br>
 *</p><p>
 * Created:     17.08.2004 by AE
 *</p><p>
 * @history     17.08.2004 by AE: Created.<br>
 *</p><p>
 * Modification:<br>
 * $Log: TextAreaPrintStream.java,v $
 * Revision 1.0  2004-08-31 15:59:06+02  t026843
 * Initial revision
 *
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @version     v1.0, 2004; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\io\\TextAreaPrintStream.java,v 1.0 2004-08-31 15:59:06+02 t026843 Exp t026843 $
 *</p><p>
 * @since					JDK1.3
 *</p>
 */
public class TextAreaPrintStream
  extends PrintStream
{
  JTextArea m_jTextAreaOutput;

  public TextAreaPrintStream( JTextArea jTextAreaOutput)
  {
    super( System.out);
    m_jTextAreaOutput = jTextAreaOutput;
  }

  private void setCaretEndPos()
  {
    m_jTextAreaOutput.setCaretPosition( m_jTextAreaOutput.getText().length());
  }

  public void println( double d)
  {
    print( d);
    println();
  }

  public void print(float f)
  {
    m_jTextAreaOutput.append( String.valueOf( f));
    setCaretEndPos();
  }

  public void print(char c)
  {
    m_jTextAreaOutput.append( String.valueOf( c));
    setCaretEndPos();
  }

  protected void setError()
  {
    super.setError();
  }

  public void close()
  {
  }

  public void println(String str)
  {
    print( str);
    println();
  }

  public void println(boolean bl)
  {
    print( bl);
    println();
  }

  public boolean checkError()
  {
    return super.checkError();
  }

  public void println(long l)
  {
    print( l);
    println();
  }

  public void println(char c)
  {
    print( c);
    println();
  }

  public void println(float f)
  {
    print( f);
    println();
  }

  public void print(int i)
  {
    m_jTextAreaOutput.append( String.valueOf( i));
    setCaretEndPos();
  }

  public void write(byte[] buf, int off, int len)
  {
    byte[] buffer = new byte[ 512];
    int iLen = 0;
    ByteArrayInputStream baos = new ByteArrayInputStream( buf, off, len);
    while( (iLen = baos.read( buffer, 0, 512)) > 0)
    {
      String str = new String( buffer);
      m_jTextAreaOutput.append( str);
    }
    setCaretEndPos();
  }

  public void print(String str)
  {
    m_jTextAreaOutput.append( str);
    setCaretEndPos();
  }

  public void print(double d)
  {
    m_jTextAreaOutput.append( String.valueOf( d));
    setCaretEndPos();
  }

  public void print(long l)
  {
    m_jTextAreaOutput.append( String.valueOf( l));
    setCaretEndPos();
  }

  public void println(char[] data)
  {
    print( data);
    println();
  }

  public void print( Object obj)
  {
    m_jTextAreaOutput.append( String.valueOf( obj));
    setCaretEndPos();
  }

  public void print(char[] data)
  {
    m_jTextAreaOutput.append( String.valueOf( data));
    setCaretEndPos();
  }

  public void println(Object obj)
  {
    print( obj);
    println();
  }

  public void flush()
  {
  }

  public void write(int b)
  {
    print( (char)b);
  }

  public void print(boolean bl)
  {
    m_jTextAreaOutput.append( String.valueOf( bl));
    setCaretEndPos();
  }


  public void println()
  {
    m_jTextAreaOutput.append( "\n");
    setCaretEndPos();
  }

  public void write(byte[] buf)
    throws IOException
  {
    write( buf, 0, buf.length);
  }

  public void println(int i)
  {
    print( i);
    println();
  }
}
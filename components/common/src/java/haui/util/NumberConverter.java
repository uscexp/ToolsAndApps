package haui.util;

import java.math.BigInteger;

/**
 * Module:      NumberConverter.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\NumberConverter.java,v $
 *<p>
 * Description: NumberConverter.<br>
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     23.05.2003
 *</p><p>
 * Modification:<br>
 * $Log: NumberConverter.java,v $
 * Revision 1.0  2003-05-28 14:21:51+02  t026843
 * Initial revision
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\NumberConverter.java,v 1.0 2003-05-28 14:21:51+02 t026843 Exp $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class NumberConverter
{
  public NumberConverter()
  {
  }

  public static byte[] toByteArray(short foo)
  {
    return toByteArray(foo, new byte[2]);
  }

  public static byte[] toByteArray(int foo)
  {
    return toByteArray(foo, new byte[4]);
  }

  public static byte[] toByteArray(long foo)
  {
    return toByteArray(foo, new byte[8]);
  }

  private static byte[] toByteArray(long foo, byte[] array)
  {
    BigInteger bi = new BigInteger( String.valueOf( foo));
    byte[] b = bi.toByteArray();

    int i = 0;
    int j = b.length-1;
    for( i = 0; i < array.length; ++i)
    {
      if( i < b.length)
      {
        array[i] = b[j];
        --j;
      }
      else
        array[i] = 0;
    }
    return array;
  }
}
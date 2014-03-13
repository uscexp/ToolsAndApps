package haui.util;

/**
 * Interface:   PropertyConditionPair.java<br>
 *              $Source: $
 *<p>
 * Description: interface for a property condition.<br>
 *</p><p>
 * Created:     09.05.2003 by AE
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     09.05.2003
 *</p><p>
 * Modification:<br>
 * $Log: $
 *</p><p>
 * @version     v1.0, 2003; $Revision: $<br>
 *              $Header: $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public interface PropertyCondition
{
  // constants
  public static final short EQUAL = 1;
  public static final short NOT_EQUAL = 2;
  public static final short EQUAL_NOCASE = 3;
  public static final short NOT_EQUAL_NOCASE = 4;
  public static final short GREATER = 5;
  public static final short GREATER_EQUAL = 6;
  public static final short SMALLER = 7;
  public static final short SMALLER_EQUAL = 8;
  public static final short CONTAINS = 9;
  public static final short CONTAINS_NOCASE = 10;

  public void extractValueToBeChecked( PropertyStruct ps);

  /**
   * compares the two given values
   * <br>
   * EQUAL -> internal valueToBeChecked = internal value
   * <br>
   * NOT_EQUAL -> internal valueToBeChecked != internal value
   * <br>
   * EQUAL_NOCASE -> internal valueToBeChecked = value (only Strings, not case snsitive)
   * <br>
   * NOT_EQUAL_NOCASE -> internal valueToBeChecked != value (only Strings, not case snsitive)
   * <br>
   * GREATER -> internal valueToBeChecked > internal value
   * <br>
   * GREATER_EQUAL -> internal valueToBeChecked >= internal value
   * <br>
   * SMALLER -> internal valueToBeChecked < internal value
   * <br>
   * SMALLER_EQUAL -> internal valueToBeChecked <= internal value
   * <br>
   * CONTAINS -> internal valueToBeChecked included in value (only Strings, case snsitive)
   * <br>
   * CONTAINS_NOCASE -> internal valueToBeChecked included in value (only Strings, not case snsitive)
   *
   * @return         true if contition is true
   */
  public boolean compare();
}
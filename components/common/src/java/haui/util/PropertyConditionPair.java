package haui.util;


/**
 * Module:      PropertyConditionPair.java<br>
 *              $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\PropertyConditionPair.java,v $
 *<p>
 * Description: Key, value pair plus condition.<br>
 *</p><p>
 * Created:     09.05.2003 by AE
 *</p><p>
 * @author      Andreas Eisenhauer
 *</p><p>
 * @created     09.05.2003
 *</p><p>
 * Modification:<br>
 * $Log: PropertyConditionPair.java,v $
 * Revision 1.0  2003-05-21 16:26:20+02  t026843
 * Initial revision
 *
 *</p><p>
 * @version     v1.0, 2003; $Revision: 1.0 $<br>
 *              $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\PropertyConditionPair.java,v 1.0 2003-05-21 16:26:20+02 t026843 Exp $
 *</p><p>
 * @since       JDK1.3
 *</p>
 */
public class PropertyConditionPair
  implements PropertyCondition
{
  // member variables
  private String m_strKey;
  private Object m_objValue;
  private short m_sCondition;
  private Object m_objValueToBeChecked;

  /**
   * constructor for a PropertyConditionPair
   *
   * @param  strKey     key (variablename) of the value
   * @param  value      value of the variable
   * @param  condition  condition to be checked with a later given value
   */
  public PropertyConditionPair( String strKey, Object value, short condition)
  {
    this( strKey, value, null, condition);
  }

  /**
   * constructor for a PropertyConditionPair
   *
   * @param  strKey             key (variablename) of the value
   * @param  value              value of the variable
   * @param  valueToBeChecked   value to be checked later
   * @param  condition          condition to check the two values
   */
  public PropertyConditionPair( String strKey, Object value, Object valueToBeChecked, short condition)
  {
    m_strKey = strKey;
    m_objValue = value;
    m_objValueToBeChecked = valueToBeChecked;
    m_sCondition = condition;
  }

  public String getKey()
  {
    return m_strKey;
  }

  public Object getValue()
  {
    return m_objValue;
  }

  public Object getValueToBeChecked()
  {
    return m_objValueToBeChecked;
  }

  public void setValueToBeChecked( Object valueToBeChecked)
  {
    m_objValueToBeChecked = valueToBeChecked;
  }

  public void extractValueToBeChecked( PropertyStruct ps)
  {
    m_objValueToBeChecked = ps.value( m_strKey);
  }

  /**
   * compares the two given values
   * <br>
   * EQUAL -> value = internal value
   * <br>
   * NOT_EQUAL -> value != internal value
   * <br>
   * EQUAL_NOCASE -> value = internal value (only Strings, not case snsitive)
   * <br>
   * NOT_EQUAL_NOCASE -> value != internal value (only Strings, not case snsitive)
   * <br>
   * GREATER -> value > internal value
   * <br>
   * GREATER_EQUAL -> value >= internal value
   * <br>
   * SMALLER -> value < internal value
   * <br>
   * SMALLER_EQUAL -> value <= internal value
   * <br>
   * CONTAINS -> value included in internal value (only Strings, case snsitive)
   * <br>
   * CONTAINS_NOCASE -> value included in internal value (only Strings, not case snsitive)
   *
   * @param  value   value of the variable
   * @return         true if contition is true
   */
  public boolean compare( Object value)
  {
    boolean blRet = false;
    if( value.getClass() != m_objValue.getClass())
    {
      throw new ClassCastException( "Value class is not of the same type!");
    }
    switch( m_sCondition)
    {
      case EQUAL:
        blRet = value.equals( m_objValue);
        break;

      case NOT_EQUAL:
        blRet = !value.equals( m_objValue);
        break;

      case EQUAL_NOCASE:
        if( !(m_objValue instanceof String))
        {
          throw new RuntimeException( "This class can only be tested to EQUAL_NOCASE!");
        }
        blRet = ((String)value).toLowerCase().equals( ((String)m_objValue).toLowerCase());
        break;

      case NOT_EQUAL_NOCASE:
        if( !(m_objValue instanceof String))
        {
          throw new RuntimeException( "This class can only be tested to EQUAL_NOCASE!");
        }
        blRet = !((String)value).toLowerCase().equals( ((String)m_objValue).toLowerCase());
        break;

      case GREATER:
        if( !(m_objValue instanceof Comparable))
        {
          throw new RuntimeException( "This class can only be tested to EQUAL or NOT_EQUAL!");
        }
        blRet = ((Comparable)value).compareTo( m_objValue) < 0;
        break;

      case GREATER_EQUAL:
        if( !(m_objValue instanceof Comparable))
        {
          throw new RuntimeException( "This class can only be tested to EQUAL or NOT_EQUAL!");
        }
        blRet = ( ( Comparable ) value).compareTo( m_objValue) <= 0;
        break;

      case SMALLER:
        if( !(m_objValue instanceof Comparable))
        {
          throw new RuntimeException( "This class can only be tested to EQUAL or NOT_EQUAL!");
        }
        blRet = ( ( Comparable ) value).compareTo( m_objValue) > 0;
        break;

      case SMALLER_EQUAL:
        if( !(m_objValue instanceof Comparable))
        {
          throw new RuntimeException( "This class can only be tested to EQUAL or NOT_EQUAL!");
        }
        blRet = ( ( Comparable )value ).compareTo(  m_objValue) >= 0;
        break;

      case CONTAINS:
        if( !(m_objValue instanceof String))
        {
          throw new RuntimeException( "This class can only be tested to CONTAINS!");
        }
        blRet = ((String)value).indexOf( (String)m_objValue ) != -1;
        break;

      case CONTAINS_NOCASE:
        if( !(m_objValue instanceof String))
        {
          throw new RuntimeException( "This class can only be tested to CONTAINS_NOCASE!");
        }
        blRet = ((String)value).toLowerCase().indexOf( ((String)m_objValue).toLowerCase() ) != -1;
        break;
    }
    return blRet;
  }

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
  public boolean compare()
  {
    boolean blRet = false;
    blRet = compare( m_objValueToBeChecked);
    return blRet;
  }
}

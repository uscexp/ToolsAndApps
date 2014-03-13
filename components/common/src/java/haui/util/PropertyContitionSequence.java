package haui.util;

/**
 * Module:      PropertyContitionSequence.java<br> $Source: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\PropertyContitionSequence.java,v $ <p> Description: Logical condition sequence (AND, OR).<br> </p><p> Created:     09.05.2003 by AE </p><p>
 * @author       Andreas Eisenhauer  </p><p>
 * @created      09.05.2003  </p><p>  Modification:<br>  $Log: PropertyContitionSequence.java,v $  Revision 1.0  2003-05-21 16:26:20+02  t026843  Initial revision  </p><p>
 * @version      v1.0, 2003; $Revision: 1.0 $<br>  $Header: M:\\Dev\\source\\RCS\\M\\Dev\\source\\Bean\\ToolKit2\\haui\\util\\PropertyContitionSequence.java,v 1.0 2003-05-21 16:26:20+02 t026843 Exp $  </p><p>
 * @since        JDK1.3  </p>
 */
public class PropertyContitionSequence
  implements PropertyCondition
{
  // constants
  public static final short AND = 1;
  public static final short OR = 2;

  // member variables
  private PropertyCondition m_pc1;
  private PropertyCondition m_pc2;
  private short m_sLogicalOperator;

  /**
   * constructor for a PropertyContitionSequence
   *
   * @param  pc1              first PropertyCondition
   * @param  pc2              second PropertyCondition
   * @param  logicalOperator  logical operator
   */
  public PropertyContitionSequence( PropertyCondition pc1, PropertyCondition pc2, short logicalOperator)
  {
    m_pc1 = pc1;
    m_pc2 = pc2;
    m_sLogicalOperator = logicalOperator;
  }

  public void extractValueToBeChecked( PropertyStruct ps)
  {
    m_pc1.extractValueToBeChecked( ps);
    m_pc2.extractValueToBeChecked( ps);
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
    switch( m_sLogicalOperator)
    {
      case AND:
        blRet = m_pc1.compare() && m_pc2.compare();
        break;

      case OR:
        blRet = m_pc1.compare() || m_pc2.compare();
        break;
    }
    return blRet;
  }
}
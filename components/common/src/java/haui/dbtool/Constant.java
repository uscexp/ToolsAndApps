
package haui.dbtool;

import java.sql.ResultSetMetaData;

/**
 *
 *		Module:					Constant.java
 *<p>
 *		Description:		Constants for the haui.dbtool package
 *</p><p>
 *		Created:				03.03.1998	by	AE
 *</p><p>
 *		Last Modified:	15.04.1999	by	AE
 *</p><p>
 *		history					15.04.1999	by	AE:	Converted to JDK v1.2<br>
 *										15.04.1999	by	AE:	Resultset move modes added.<br>
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 1998,1999
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public interface Constant
{

  public static final String LOGTAB = "logtable";
  public static final int IDXERR = -1;
  public static final int OK = 0;
  public static final int ERR = 1;
  public static final int NULLERR = 2;
  public static final int OKNORSET = 3;

  /** Does not allow NULL values. */
  public static final int columnNoNulls = ResultSetMetaData.columnNoNulls;
  /** Allows NULL values. */
  public static final int columnNullable = ResultSetMetaData.columnNullable;
  /** Nullability unknown. */
  public static final int columnNullableUnknown = ResultSetMetaData.columnNullableUnknown;

  /** Resultset move mode beforeFirst() */
  public static final int BEFOREFIRST = 0;
  /** Resultset move mode first() */
  public static final int FIRST = 1;
  /** Resultset move mode next() */
  public static final int NEXT = 2;
  /** Resultset move mode previous() */
  public static final int PREV = 3;
  /** Resultset move mode last() */
  public static final int LAST = 4;
  /** Resultset move mode afterLast() */
  public static final int AFTERLAST = 5;
  /** Resultset move mode absolute() */
  public static final int ABSOLUTE = 6;
  /** Resultset move mode relative() */
  public static final int RELATIVE = 7;
}

/* *****************************************************************
 * Project: common
 * File:    SortOrder.java
 * 
 * Creation:     05.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.awt.shell;

/**
 * SortOrder
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public enum SortOrder
{
  /**
   * Enumeration value indicating the items are sorted in increasing order. For example, the set
   * <code>1, 4, 0</code> sorted in <code>ASCENDING</code> order is <code>0, 1, 4</code>.
   */
  ASCENDING,

  /**
   * Enumeration value indicating the items are sorted in decreasing order. For example, the set
   * <code>1, 4, 0</code> sorted in <code>DESCENDING</code> order is <code>4, 1, 0</code>.
   */
  DESCENDING,

  /**
   * Enumeration value indicating the items are unordered. For example, the set <code>1, 4, 0</code>
   * in <code>UNSORTED</code> order is <code>1, 4, 0</code>.
   */
  UNSORTED
}

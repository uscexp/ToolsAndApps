/* *****************************************************************
 * Project: common
 * File:    DataTransferObject.java
 * 
 * Creation:     14.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model;

import haui.common.id.STIDGenerator;
import haui.exception.AppSystemException;
import haui.util.AppReflectionToStringBuilder;
import haui.util.AppToStringStyle;
import haui.util.AppUtil;
import haui.util.SortUtil;

import java.io.Serializable;
import java.util.Comparator;

/**
 * DataTransferObject
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public class DataTransferObject implements Serializable, IStidable,
		Comparable<DataTransferObject> {

	private static final long serialVersionUID = 3715195957847555584L;

	// pn attributes
	public static final String PN_STID = "stid";

	// property members
	private String stid;

	/**
	 * compare an subclass of DataTransferObject with this subclass of
	 * DataTransferObject to add more sorter see package:
	 * com.ubs.arte.common.util.sort. In order to allow DTOs that are equal
	 * according to the sort criteria to be in the same sorted set, zero is
	 * never returned for two DTOs
	 * 
	 * @param o
	 *            subclass of DataTransferObject return less, equal or greater 0
	 */
	public int compareTo(DataTransferObject o) {
		try {
			Comparator<DataTransferObject> comparator = SortUtil.getComparator(
					this.getClass(), o.getClass(), false);
			return comparator.compare(this, o);
		} catch (Exception e) {
			throw new AppSystemException("Can't compare "
					+ this.getClass().getName() + " with "
					+ o.getClass().getName(), e);
		}
	}

	/**
	 * compare an subclass of DataTransferObject with this subclass of
	 * DataTransferObject to add more sorter see package:
	 * com.ubs.arte.common.util.sort. If the two DTOs are equal according to the
	 * sort criteria, zero is returned (this is necessary for comparisons from
	 * within the DTO comparator; if the objects are equal according to the
	 * first criterion, the second criterion has to be used etc.)
	 * 
	 * @param o
	 *            subclass of DataTransferObject return less, equal or greater 0
	 */

	public int compareToAllowZero(DataTransferObject o) {
		try {
			Comparator<DataTransferObject> comparator = SortUtil.getComparator(this.getClass(),
					o.getClass(), true);
			return comparator.compare(this, o);
		} catch (Exception e) {
			throw new AppSystemException("Can't compare "
					+ this.getClass().getName() + " with "
					+ o.getClass().getName(), e);
		}
	}

	/**
	 * Answers a human readable string representation of this object (i.e. to be
	 * used in error messages)
	 * 
	 * @return short, single lined human readable string representation
	 */
	public String getShortDescription() {
		return toString();
	}

	/**
	 * Implements a generic <code>toString()</code> method that uses reflection
	 * to determine the fields and values of the DTO instance.
	 */
	public String toString() {
		return new AppReflectionToStringBuilder(this,
				AppToStringStyle.DTO_STYLE).toString();
	}

	/**
	 * FundamentalEntity implements <code>Object.equals(java.lang.Object)</code>
	 * based on the stid.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		return AppUtil.equals(getStid(), ((IStidable) obj).getStid());
	}

	/**
	 * FundamentalEntity implements <code>Object.hashCode()</code> based on the
	 * stid.
	 */
	public int hashCode() {
		String stid = getStid();
		return stid == null ? System.identityHashCode(this) : stid.hashCode();
	}

	/**
	 * // TODO insert comment into xml for [Property stid]
	 * 
	 * @return the stid property
	 */
	public final String getStid() {
		if (this.stid == null)
			this.stid = STIDGenerator.generate();
		return this.stid;
	}

	/**
	 * // TODO insert comment into xml for [Property stid]
	 * 
	 */
	public final void setStid(String stid) {
		this.stid = stid;
	}
}

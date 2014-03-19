/* *****************************************************************
 * Project: common
 * File:    AppToStringStyle.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import haui.model.DataTransferObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AppToStringStyle
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public class AppToStringStyle implements Serializable {

	private static final long serialVersionUID = -6066254873118447668L;
	
	/**
	 * The default style used for formatting DTOs.
	 */
	public static final ToStringStyle DTO_STYLE = new DTOToStringStyle();

	/**
	 * This is an inner class to ensure its immutability.
	 */
	private static final class DTOToStringStyle extends ToStringStyle {

		/**
       * 
       */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs a ToStringStyle customized for ARTE DTOs.
		 * <p>
		 * Use the static constant rather than instantiating.
		 */
		private DTOToStringStyle() {
			super();
			this.setUseShortClassName(true);
			this.setUseIdentityHashCode(true);
			this.setDefaultFullDetail(true);
			this.setSummaryObjectStartText("<");
			this.setSummaryObjectEndText(">");
			this.setSizeStartText("[size=");
			this.setSizeEndText("]");
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
			this.setFieldSeparatorAtStart(true);
			this.setContentStart("[");
			this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
		}

		/**
		 * Append to the toString() an <code>Object</code> value, printing the
		 * a) toString() that would be produced by <code>Object</code> if a
		 * class did not override toString() itself <code>Object</code> or else
		 * b) the full detail of the <code>Object</code>.
		 * 
		 * @param buffer
		 *            the <code>StringBuffer</code> to populate
		 * @param fieldName
		 *            the field, typically not used as already appended
		 * @param value
		 *            the value to add to toString(), not <code>null</code>
		 */
		public void appendDetail(StringBuffer buffer, String fieldName,
				Object value) {
			// display custom summary for certain types

			if (value instanceof Date)
				appendSummary(buffer, (Date) value);
			else if (value instanceof haui.model.data.Enum)
				appendSummary(buffer, (haui.model.data.Enum) value);
			else if (value instanceof DataTransferObject)
				appendSummary(buffer, (DataTransferObject) value);
			else
				super.appendDetail(buffer, fieldName, value);
		}

		/**
		 * Append the run-time type and size of a <code>Collection</code> to the
		 * <code>toString()</code> .
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				Collection<?> coll) {
			appendSummarySet(buffer, coll.getClass(), coll.size());
		}

		/**
		 * Append the run-time type and size of a <code>Map</code> to the
		 * <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				Map<?, ?> map) {
			appendSummarySet(buffer, map.getClass(), map.size());
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				boolean[] array) {
			appendSummary(buffer, "boolean[]", array.length);
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				byte[] array) {
			appendSummary(buffer, "byte[]", array.length);
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				char[] array) {
			appendSummary(buffer, "char[]", array.length);
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				double[] array) {
			appendSummary(buffer, "double[]", array.length);
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				float[] array) {
			appendSummary(buffer, "float[]", array.length);
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				int[] array) {
			appendSummary(buffer, "int[]", array.length);
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				long[] array) {
			appendSummary(buffer, "long[]", array.length);
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				short[] array) {
			appendSummary(buffer, "short[]", array.length);
		}

		/**
		 * Append the type and size of the array to the <code>toString()</code>.
		 */
		protected void appendDetail(StringBuffer buffer, String fieldName,
				Object[] array) {
			appendSummary(buffer, "Object[]", array.length);
		}

		/**
		 * Append the class name and the size of e.g. a Collection, a Map, Array
		 * etc.
		 * 
		 * @param buffer
		 *            the <code>StringBuffer</code> to populate
		 * @param clazz
		 *            the class to append
		 * @param size
		 *            the size to append
		 */
		protected void appendSummarySet(StringBuffer buffer, Class<?> clazz,
				int size) {
			appendSummary(buffer, clazz.getName(), size);
		}

		/**
		 * Append the class name and the size of e.g. a Collection, a Map, Array
		 * etc.
		 * 
		 * @param buffer
		 *            the <code>StringBuffer</code> to populate
		 * @param name
		 *            the type name to append
		 * @param size
		 *            the size to append
		 */
		protected void appendSummary(StringBuffer buffer, String name, int size) {
			buffer.append(name);
			appendSummarySize(buffer, null, size); // no fieldname needed
		}

		/**
		 * Append the <code>AbstractCode</code> class name and the code
		 * attribute.
		 * 
		 * @param buffer
		 *            the <code>StringBuffer</code> to populate
		 * @param vsc
		 *            the <code>AbstractCode</code> object containing the code
		 */
		protected void appendSummary(StringBuffer buffer,
				haui.model.data.Enum vsc) {
			buffer.append(ClassUtils.getShortClassName(vsc, ""));
			buffer.append("[");
			buffer.append("code=");
			buffer.append(vsc.getCode());
			buffer.append("]");
		}

		/**
		 * Append the <code>DataTransferObject</code> class name.
		 * 
		 * @param buffer
		 *            the <code>StringBuffer</code> to populate
		 * @param fe
		 *            the <code>DataTransferObject</code>
		 */
		protected void appendSummary(StringBuffer buffer, DataTransferObject fe) {
			buffer.append(fe.getClass().getName());
			// buffer.append("[");
			// buffer.append("uuid=");
			// buffer.append(fe.getUuid());
			// buffer.append("]");
		}

		/**
		 * Append the date formatted using the date format defined in
		 * {@link DateUtil#DATE_FORMAT}.
		 * 
		 * @param buffer
		 *            the <code>StringBuffer</code> to populate
		 * @param date
		 *            the <code>Date</code> to be appended
		 */
		protected void appendSummary(StringBuffer buffer, Date date) {
			buffer.append(DateUtil.format(date, true));
		}

		/**
		 * Ensure <code>Singleton</code> after serialization.
		 * 
		 * @return the singleton
		 */
		private Object readResolve() {
			return AppToStringStyle.DTO_STYLE;
		}
	}
}

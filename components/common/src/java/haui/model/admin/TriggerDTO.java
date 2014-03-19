/* *****************************************************************
 * Project: common
 * File:    TriggerDTO.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.admin;

import haui.exception.AppSystemException;
import haui.model.DataTransferObject;
import haui.util.GlobalApplicationContext;
import haui.util.SchedulerUtil;

import java.util.Date;

/**
 * TriggerDTO
 * 
 * @author Andreas Eisenhauer $LastChangedRevision: $
 * @since 1.0
 */
public class TriggerDTO extends DataTransferObject implements
		Comparable<DataTransferObject> {
	static final long serialVersionUID = 4695860848672637311L;

	// pn attributes
	public static final String PN_ADMIN_JOB = "adminJob";
	public static final String PN_NEXT_EXECUTION_TIME = "nextExecutionTime";
	public static final String PN_EXECUTION_TIME = "executionTime";
	public static final String PN_FIRING_TOLERANCE = "firingTolerance";

	/**
	 * Property to set default firing tolerance
	 */
	public final static String TRIGGER_FIRIRING_TOLERANCE = "arte.trigger.firingTolerance";

	/**
	 * Default property value for firing tolerance
	 */
	public final static int DEFAULT_TRIGGER_FIRIRING_TOLERANCE = 2;

	// property members
	private AdminJobDTO adminJob;
	private Date nextExecutionTime;
	private Date executionTime;
	private Date firingTolerance;

	// **************************************************************
	// ***** Constructors *******************************************
	// **************************************************************

	// TODO auto incremented primary key
	// Default constructor
	protected TriggerDTO() {
	}

	/**
	 * If this constructor is used, firing tolerance is set to 2 minutes. The
	 * firing tolerance represents a range in which a job has at least ha
	 * 
	 * @param adminJob
	 * @param executionTime
	 */
	public TriggerDTO(final AdminJobDTO adminJob, Date executionTime) {
		setAdminJob(adminJob);
		setExecutionTime(executionTime);

		int firingTolerance = GlobalApplicationContext
				.instance()
				.getApplicationProperties()
				.getProperty(TRIGGER_FIRIRING_TOLERANCE,
						DEFAULT_TRIGGER_FIRIRING_TOLERANCE);
		setFiringTolerance(SchedulerUtil.createTime(0, firingTolerance));
	}

	/**
	 * Use this constructor to overwrite the default firing tolerance.
	 * 
	 * @param adminJob
	 * @param executionTime
	 */
	public TriggerDTO(final AdminJobDTO adminJob, Date executionTime,
			Date firingTolerance) {
		setAdminJob(adminJob);
		setExecutionTime(executionTime);
		setFiringTolerance(firingTolerance);
	}

	// accessor methods

	/**
	 * Returns the jobDetail.
	 * 
	 * @return the adminJob property
	 */
	public final AdminJobDTO getAdminJob() {
		return adminJob;
	}

	/**
	 * Returns the jobDetail.
	 * 
	 */
	public final void setAdminJob(AdminJobDTO adminJob) {
		this.adminJob = adminJob;
	}

	/**
	 * Returns the nextExecutionTime.
	 * 
	 * @return the nextExecutionTime property
	 */
	public final Date getNextExecutionTime() {
		return nextExecutionTime;
	}

	/**
	 * Returns the nextExecutionTime.
	 * 
	 */
	public final void setNextExecutionTime(Date nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

	/**
	 * // TODO insert comment into xml for [PersistentProperty executionTime]
	 * 
	 * @return the executionTime property
	 */
	public final Date getExecutionTime() {
		return executionTime;
	}

	/**
	 * // TODO insert comment into xml for [PersistentProperty executionTime]
	 * 
	 */
	public final void setExecutionTime(Date executionTime) {
		this.executionTime = executionTime;
	}

	/**
	 * Returns the firingRange.
	 * 
	 * @return the firingTolerance property
	 */
	public final Date getFiringTolerance() {
		return firingTolerance;
	}

	/**
	 * Returns the firingRange.
	 * 
	 */
	public final void setFiringTolerance(Date firingTolerance) {
		this.firingTolerance = firingTolerance;
	}

	public int compareTo(DataTransferObject o) {
		if (!(o instanceof TriggerDTO)) {
			throw new AppSystemException("A TriggerDTO object was expected!");
		}
		Date tmp = ((TriggerDTO) o).getExecutionTime();
		return this.getExecutionTime().compareTo(tmp);
	}

	public int compareToAllowZero(DataTransferObject object) {
		return compareTo(object);
	}
}

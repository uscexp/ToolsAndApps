/* *****************************************************************
 * Project: common
 * File:    FileInterfaceConfiguration.java
 * 
 * Creation:     16.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.util.AppProperties;

/**
 * Module: FileInterfaceConfiguration<br>
 * <p>
 * Description: FileInterfaceConfiguration<br>
 * </p>
 * <p>
 * Created: 16.03.2006 by Andreas Eisenhauer
 * </p>
 * <p>
 * 
 * @history 16.03.2006 by AE: Created.<br>
 *          </p>
 *          <p>
 * @author <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas
 *         Eisenhauer</a>
 *         </p>
 *         <p>
 * @version v0.1, 2006; %version: %<br>
 *          </p>
 *          <p>
 * @since JDK1.4
 *        </p>
 */
public abstract class FileInterfaceConfiguration {
	protected String appName;
	protected AppProperties appProps;

	private boolean local = true;
	private boolean cached = false;

	public FileInterfaceConfiguration(String appName, AppProperties props,
			boolean cached) {
		this(appName, props, true, cached);
	}

	public FileInterfaceConfiguration(String appName, AppProperties props,
			boolean local, boolean cached) {
		super();
		this.appName = appName;
		this.appProps = props;
		this.local = local;
		this.cached = cached;
	}

	public AppProperties getAppProperties() {
		return appProps;
	}

	public void setAppProperties(AppProperties props) {
		this.appProps = props;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isLocal() {
		return local;
	}

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}
}

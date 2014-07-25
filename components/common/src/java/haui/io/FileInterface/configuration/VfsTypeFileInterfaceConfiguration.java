/* *****************************************************************
 * Project: common
 * File:    FtpTypeFileInterfaceConfiguration.java
 * 
 * Creation:     20.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import org.apache.commons.vfs2.FileSystemOptions;

import haui.util.AppProperties;

/**
 * Configuration for Apache VFS type files.
 * 
 * @author ae
 * 
 */
public abstract class VfsTypeFileInterfaceConfiguration extends
		FileInterfaceConfiguration {

	protected FileSystemOptions fileSystemOptions;

	public VfsTypeFileInterfaceConfiguration(String appName,
			AppProperties props, boolean cached) {
		super(appName, props, false, cached);
		fileSystemOptions = new FileSystemOptions();
	}

	public abstract String getUri(String path);

	public FileSystemOptions getFileSystemOptions() {
		return fileSystemOptions;
	}
}

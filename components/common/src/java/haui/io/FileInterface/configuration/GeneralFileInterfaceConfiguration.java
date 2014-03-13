/* *****************************************************************
 * Project: common
 * File:    GeneralFileInterfaceConfiguration.java
 * 
 * Creation:     23.03.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.io.FileInterface.configuration;

import haui.util.AppProperties;

/**
 * Module:      GeneralFileInterfaceConfiguration<br>
 *<p>
 * Description: GeneralFileInterfaceConfiguration<br>
 *</p><p>
 * Created:     23.03.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     23.03.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class GeneralFileInterfaceConfiguration extends FileInterfaceConfiguration
{

  public GeneralFileInterfaceConfiguration( String appName, AppProperties props, boolean cached)
  {
    super( appName, props, cached);
  }
}

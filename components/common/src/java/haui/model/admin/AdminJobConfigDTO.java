/* *****************************************************************
 * Project: common
 * File:    AdminJobConfigDTO.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.model.admin;

import haui.model.DataTransferObject;

/**
 * AdminJobConfigDTO
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class AdminJobConfigDTO extends DataTransferObject
{
  // pn attributes
  public static final String PN_FILE_NAME = "fileName";
  public static final String PN_FILE = "file";

  // property members
  private String fileName;
  private byte[] file;

  public AdminJobConfigDTO()
  {
    super();
  }

  // accessor methods

  /**
   * // TODO insert comment into xml for [PersistentProperty fileName]
   * 
   * @return the fileName property
   */
  public final String getFileName()
  {
    return fileName;
  }

  /**
   * // TODO insert comment into xml for [PersistentProperty fileName]
   * 
   */
  public final void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  /**
   * Returns the arteJobDependencies.xml file.
   * 
   * @return the file property
   */
  public final byte[] getFile()
  {
    return file;
  }

  /**
   * Returns the arteJobDependencies.xml file.
   * 
   */
  public final void setFile(byte[] file)
  {
    this.file = file;
  }
}

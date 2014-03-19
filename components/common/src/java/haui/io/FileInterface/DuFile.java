package haui.io.FileInterface;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

/**
 *		Module:					DuFile.java<br>
 *										$Source: $
 *<p>
 *		Description:    Dummy file information.<br>
 *</p><p>
 *		Created:				22.08.2002	by	AE
 *</p><p>
 *		@history				22.08.2002	by	AE: Created.<br>
 *</p><p>
 *		Modification:<br>
 *		$Log: $
 *
 *</p><p>
 *		@author					Andreas Eisenhauer
 *</p><p>
 *		@version				v1.0, 2002; $Revision: $<br>
 *										$Header: $
 *</p><p>
 *		@since					JDK1.2
 *</p>
 */
public abstract class DuFile
  extends BaseTypeFile
{
  // member variables
  protected String intPath;

  char separator;

  /**
   * Creates a new DuFile instance.
   *
   * @param strCurPath: current path
   */
  public DuFile( String strPath, boolean blDirectory, char cSeparator, FileInterfaceConfiguration fic)
  {
    super( fic);
    this.path = strPath;
    this.absolutePath = strPath;
    this.separator = cSeparator;
    this.directory = blDirectory;
    this.fileType = !blDirectory;
    int idx = strPath.lastIndexOf( separatorChar());
    if( idx != -1)
    	this.name = strPath.substring( idx+1, strPath.length());
    else
    	this.name = strPath;
  }

  /**
   * Creates a new DuFile instance.
   *
   * @param strCurPath: current path
   */
  public DuFile(  String strPath, FileInterfaceConfiguration fic)
  {
    this( strPath, true, '/', fic);
  }

  public char separatorChar()
  {
    return separator;
  }

  public boolean canRead()
  {
    return read;
  }

  public boolean canWrite()
  {
    return write;
  }

  public boolean isDirectory()
  {
    return directory;
  }

  public boolean isFile()
  {
    return fileType;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  public long length()
  {
    return length;
  }

  public String getId()
  {
    return host;
  }

  public String getName()
  {
    return name;
  }

  public String getAbsolutePath()
  {
    return absolutePath;
  }

  public String getPath()
  {
    return path;
  }

  public String getInternalPath()
  {
    return intPath;
  }

  public String getParent()
  {
    return parent;
  }

  public long lastModified()
  {
    return modified;
  }
}
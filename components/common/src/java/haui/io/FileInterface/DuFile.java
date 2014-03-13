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
  protected String m_intPath;

  char m_cSeparator;

  /**
   * Creates a new DuFile instance.
   *
   * @param strCurPath: current path
   */
  public DuFile( String strPath, boolean blDirectory, char cSeparator, FileInterfaceConfiguration fic)
  {
    super( fic);
    m_strPath = strPath;
    m_strAbsolutePath = strPath;
    m_cSeparator = cSeparator;
    m_blDirectory = blDirectory;
    m_blFile = !blDirectory;
    int idx = strPath.lastIndexOf( separatorChar());
    if( idx != -1)
      m_strName = strPath.substring( idx+1, strPath.length());
    else
      m_strName = strPath;
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
    return m_cSeparator;
  }

  public boolean canRead()
  {
    return m_blRead;
  }

  public boolean canWrite()
  {
    return m_blWrite;
  }

  public boolean isDirectory()
  {
    return m_blDirectory;
  }

  public boolean isFile()
  {
    return m_blFile;
  }

  public boolean isHidden()
  {
    return m_blHidden;
  }

  public long length()
  {
    return m_lLength;
  }

  public String getId()
  {
    return m_strHost;
  }

  public String getName()
  {
    return m_strName;
  }

  public String getAbsolutePath()
  {
    return m_strAbsolutePath;
  }

  public String getPath()
  {
    return m_strPath;
  }

  public String getInternalPath()
  {
    return m_intPath;
  }

  public String getParent()
  {
    return m_strParent;
  }

  public long lastModified()
  {
    return m_lModified;
  }
}
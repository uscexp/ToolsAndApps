/**
 * 
 */
package haui.io.FileInterface.compress.archive;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;

/**
 * @author ae
 * 
 */
public class JarTypeFile extends
		ArchiveTypeFile<JarTypeFile, JarArchiveInputStream, JarArchiveEntry> {

	public final static String EXTENSION = ".jar";

	public JarTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}

/**
 * 
 */
package haui.io.FileInterface.compress.archive;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

/**
 * @author ae
 * 
 */
public class TarTypeFile extends
		ArchiveTypeFile<TarTypeFile, TarArchiveInputStream, TarArchiveEntry> {

	public final static String EXTENSION = ".tar";

	public TarTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}

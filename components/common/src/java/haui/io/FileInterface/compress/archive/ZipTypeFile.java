/**
 * 
 */
package haui.io.FileInterface.compress.archive;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

/**
 * @author ae
 * 
 */
public class ZipTypeFile extends
		ArchiveTypeFile<ZipTypeFile, ZipArchiveInputStream, ZipArchiveEntry> {

	public final static String EXTENSION = ".zip";

	public ZipTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}

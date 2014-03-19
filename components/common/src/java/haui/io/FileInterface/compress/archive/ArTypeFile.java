/**
 * 
 */
package haui.io.FileInterface.compress.archive;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

import org.apache.commons.compress.archivers.arj.ArjArchiveEntry;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;

/**
 * @author ae
 *
 */
public class ArTypeFile extends ArchiveTypeFile<ArTypeFile, ArjArchiveInputStream, ArjArchiveEntry> {

	public final static String EXTENSION = ".ar";

	public ArTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}

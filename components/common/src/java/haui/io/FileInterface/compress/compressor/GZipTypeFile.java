/**
 * 
 */
package haui.io.FileInterface.compress.compressor;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * @author ae
 *
 */
public class GZipTypeFile extends CompressorTypeFile<GZipTypeFile, GzipCompressorInputStream> {

	public final static String EXTENSION = ".gz";
	public final static String EXTENSION1 = ".tgz";

	public GZipTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}

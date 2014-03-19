/**
 * 
 */
package haui.io.FileInterface.compress.compressor;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

import haui.io.FileInterface.configuration.FileInterfaceConfiguration;

/**
 * @author ae
 *
 */
public class ZTypeFile extends CompressorTypeFile<ZTypeFile, ZCompressorInputStream> {

	public final static String EXTENSION = ".Z";

	public ZTypeFile(String strArchPath, String strIntPath,
			String strParentPath, FileInterfaceConfiguration fic) {
		super(strArchPath, strIntPath, strParentPath, fic);
	}
}

/**
 * 
 */
package haui.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import org.apache.commons.net.ftp.FTPClient;

/**
 * Observes an ftp {@link InputStream} to finalize the stream the correct way.
 * 
 * @author ae
 * 
 */
public class FtpInputStreamObserver extends AbstractInputStreamObserver {

	FTPClient ftpClient;
	OutputStream out;
	OutputStream err;

	public FtpInputStreamObserver(FTPClient ftpClient, OutputStream out,
			OutputStream err) {
		super();
		this.ftpClient = ftpClient;
		this.out = out;
		this.err = err;
	}

	@Override
	public void onClosePre() {
		super.onClosePre();
		try {
			boolean success = ftpClient.completePendingCommand();
			if (!success) {
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
						out);
				outputStreamWriter.write(String.format(
						"Error reading file from ftp server: %s!", ftpClient
								.getRemoteAddress().getHostName()));
			}
		} catch (IOException e) {
			PrintStream printStream = new PrintStream(err);
			e.printStackTrace(printStream);
		}
	}

}

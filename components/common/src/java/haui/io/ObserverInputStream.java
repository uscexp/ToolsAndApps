/**
 * 
 */
package haui.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class observes (decorates) a given {@link InputStream} and calls several
 * methods on the set {@link InputStreamObserver}.
 * 
 * @author ae
 * 
 */
public class ObserverInputStream extends InputStream {

	InputStream observedInputStream;
	InputStreamObserver inputStreamObserver;

	public ObserverInputStream(InputStream observedInputStream,
			InputStreamObserver inputStreamObserver) {
		super();
		this.observedInputStream = observedInputStream;
		this.inputStreamObserver = inputStreamObserver;
	}

	@Override
	public int available() throws IOException {
		inputStreamObserver.onAvailablePre();
		int result = observedInputStream.available();
		inputStreamObserver.onAvailablePost(result);
		return result;
	}

	@Override
	public void close() throws IOException {
		inputStreamObserver.onClosePre();
		observedInputStream.close();
		inputStreamObserver.onClosePost();
	}

	@Override
	public synchronized void mark(int readlimit) {
		inputStreamObserver.onMarkPre(readlimit);
		observedInputStream.mark(readlimit);
		inputStreamObserver.onMarkPost(readlimit);
	}

	@Override
	public boolean markSupported() {
		inputStreamObserver.onMarkSupportedPre();
		boolean result = observedInputStream.markSupported();
		inputStreamObserver.onMarkSupportedPost(result);
		return result;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		inputStreamObserver.onReadPre(b, off, len);
		int result = observedInputStream.read(b, off, len);
		inputStreamObserver.onReadPost(b, off, len, result);
		return result;
	}

	@Override
	public int read(byte[] b) throws IOException {
		inputStreamObserver.onReadPre(b);
		int result = observedInputStream.read(b);
		inputStreamObserver.onReadPost(b, result);
		return result;
	}

	@Override
	public synchronized void reset() throws IOException {
		inputStreamObserver.onResetPre();
		observedInputStream.reset();
		inputStreamObserver.onResetPost();
	}

	@Override
	public long skip(long n) throws IOException {
		inputStreamObserver.onSkipPre(n);
		long result = observedInputStream.skip(n);
		inputStreamObserver.onSkipPost(n, result);
		return result;
	}

	@Override
	public int read() throws IOException {
		inputStreamObserver.onReadPre();
		int result = observedInputStream.read();
		inputStreamObserver.onReadPost(result);
		return result;
	}

}

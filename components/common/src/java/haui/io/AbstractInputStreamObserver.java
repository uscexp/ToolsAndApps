/**
 * 
 */
package haui.io;

/**
 * Default (empty) implementation of interface {@link InputStreamObserver}.
 * 
 * @author ae
 *
 */
public abstract class AbstractInputStreamObserver implements InputStreamObserver {

	@Override
	public void onAvailablePre() {
	}

	@Override
	public void onClosePre() {
	}

	@Override
	public void onMarkPre(int readlimit) {
	}

	@Override
	public void onMarkSupportedPre() {
	}

	@Override
	public void onReadPre(byte[] b, int off, int len) {
	}

	@Override
	public void onReadPre(byte[] b) {
	}

	@Override
	public void onReadPre() {
	}

	@Override
	public void onResetPre() {
	}

	@Override
	public void onSkipPre(long n) {
	}

	@Override
	public void onAvailablePost(int available) {
	}

	@Override
	public void onClosePost() {
	}

	@Override
	public void onMarkPost(int readlimit) {
	}

	@Override
	public void onMarkSupportedPost(boolean markSupported) {
	}

	@Override
	public void onReadPost(byte[] b, int off, int len, int read) {
	}

	@Override
	public void onReadPost(byte[] b, int read) {
	}

	@Override
	public void onReadPost(int read) {
	}

	@Override
	public void onResetPost() {
	}

	@Override
	public void onSkipPost(long n, long skip) {
	}

}

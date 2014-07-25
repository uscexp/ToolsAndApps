/**
 * 
 */
package haui.io;


/**
 * An implementation of that interface will be called from the {@link ObserverInputStream}.
 * 
 * @author ae
 *
 */
public interface InputStreamObserver {

	void onAvailablePre();

	void onClosePre();

	void onMarkPre(int readlimit);

	void onMarkSupportedPre();
	
	void onReadPre(byte[] b, int off, int len);

	void onReadPre(byte[] b);

	void onReadPre();

	void onResetPre();

	void onSkipPre(long n);

	void onAvailablePost(int available);

	void onClosePost();

	void onMarkPost(int readlimit);

	void onMarkSupportedPost(boolean markSupported);
	
	void onReadPost(byte[] b, int off, int len, int read);

	void onReadPost(byte[] b, int read);

	void onReadPost(int read);

	void onResetPost();

	void onSkipPost(long n, long skip);
}

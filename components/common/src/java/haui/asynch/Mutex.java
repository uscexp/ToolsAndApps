package haui.asynch;

import haui.asynch.Semaphore;

//import haui.tools.Assert;			// import one or the other of
// import haui.tools.debug.Assert;	// these, but not both.

/** Implementation of a mutual-exclusion semaphore. It can be owned by
 *  only one thread at a time. The thread can acquire it multiple times,
 *  but there must be a release for every acquire.
 * <br>
 * <br>
 *
 */

public final class Mutex
  implements Semaphore
{
  private Thread m_threadOwner = null; // Owner of mutex, null if nobody
  private int m_iLockCount = 0;

  private final int m_iMyId = LockManager.newId();

  public int id()
  {
    return m_iMyId;
  }

  /**
   * Acquire the mutex. The mutex can be acquired multiple times
   * by the same thread, provided that it is released as many
   * times as it is acquired. The calling thread blocks until
   * it has acquired the mutex. (There is no timeout).
   *
   * @param timeout If 0, then the behavior of this function is
   *				  identical to {@link acquireWithoutBlocking}.
   *				  If <code>timeout</code> is nonzero, then the timeout
   *				  is the the maximum amount of time that you'll wait
   *				  (in milliseconds).
   *				  Use Semaphore.FOREVER to wait forever.
   *
   * @return false  if the timeout was 0 and you did not acquire the
   *				  lock. True otherwise.
   *
   * @throw InterruptedException if the waiting thread is interrupted
   *				  before the timeout expires.
   *
   * @throw Semaphore.TimedOut if the specified time elapses before the
   *				  mutex on which we are waiting is released.
   *
   * @see #release
   * @see #acquireWithoutBlocking
   */

  public synchronized boolean acquire( long timeout )
    throws InterruptedException, Semaphore.TimedOut
  {
    if( timeout == 0 )
    {
      return acquireWithoutBlocking();
    }
    else if( timeout == FOREVER ) // wait forever
    {
      while( !acquireWithoutBlocking() ) //#spinLock
        this.wait( FOREVER );
    }
    else // wait limited by timeout
    {
      long expiration = System.currentTimeMillis() + timeout;
      while( !acquireWithoutBlocking() )
      {
        long timeRemaining =
          expiration - System.currentTimeMillis();
        if( timeRemaining <= 0 )
          throw new Semaphore.TimedOut( "Timed out waiting for Mutex" );

        this.wait( timeRemaining );
      }
    }
    return true;
  }

  /** A convenience method, effectively waits forever. Actually
   *  waits for 0x7fffffffffffffff milliseconds
   *  (approx. 292,271,023 years), but that's close enough to
   *  forever for me.
   *  @return false if interrupted, otherwise returns true.
   */
  public boolean acquire()
  {
    try
    {
      acquire( FOREVER );
    }
    catch( Exception e )
    {
      return false;
    }
    return true;
  }

  /**
   * Attempts to acquire the mutex. Returns false (and does not
   * block) if it can't get it. This method does not need to
   * be synchronized because it's always called from a
   * synchronized method.
   *
   * @return true if you get the mutex, false otherwise.
   * @see #release
   * @see #acquire
   */

  private boolean acquireWithoutBlocking()
  {
    Thread current = Thread.currentThread();

    if( m_threadOwner == null )
    {
      m_threadOwner = current;
      m_iLockCount = 1;
    }
    else if( m_threadOwner == current )
    {
      ++m_iLockCount;
    }

    return m_threadOwner == current;
  }

  /**
   * Release the mutex. The mutex has to be released as many times
   * as it was acquired to actually unlock the resource. The mutex
   * must be released by the thread that acquired it.
   *
   * @throws Semaphore.Ownership (a <code>RuntimeException</code>) if a thread
   *		other than the current owner tries to release the mutex.
   *		Also thrown if somebody tries to release a mutex that's
   *		not owned by any thread.
   */

  public synchronized void release()
  {
    if( m_threadOwner != Thread.currentThread() )
      throw new Semaphore.Ownership();

    if( --m_iLockCount <= 0 )
    {
      m_threadOwner = null;
      this.notify();
    }
  }
}
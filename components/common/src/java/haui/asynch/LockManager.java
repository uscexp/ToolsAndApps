package haui.asynch;

import haui.asynch.Semaphore;
import java.util.*;

/** The LockManager class helps manage groups of locks. It provides
 *	a way to safely (without deadlock) acquire all of a set of locks.
 * <br>
 * <br>
 * <table border=1 cellspacing=0 cellpadding=0>
 * <tr><td>11/17/02</td><td>Fixed constructor bug reported by Ralph Schaer</td></tr>
 * </table
 */

public class LockManager
{
  private static Object m_objIdLock = new Object();
  private static int m_iIdPool = 0;

  private LockManager()
  {}; // Make sure it can't be instantiated

  /** Return a unique integer ID. Used by implementers of Semaphore
   *  to get a value to return from their <code>id()</code> method.
   */

  public static int newId() // I'm deliberately using a stand-
  { // alone lock rather than
    // synchronizing newId() because
    synchronized( m_objIdLock )
    { // I don't want to prevent a call
      return m_iIdPool++;
    } // to acquireMultiple() while
    // another thread is calling
    // newId().
  }

  /** The comparator used to sort arrays of locks into ID order.
   */

  private static final Comparator compareStrategy = new Comparator()
  {
    public int compare( Object a, Object b )
    {
      return( ( Semaphore )a ).id() - ( ( Semaphore )b ).id();
    }

    public boolean equals( Object obj )
    {
      return obj == this;
    }
  };

  /**
   *	This function returns once all of the locks in the incoming
   *  array have been successfully acquired. Locks are always
   *  acquired in ascending order of ID to attempt to avoid
   *  deadlock situations. If the acquire operation is interrupted,
   *	or if it times out, all the locks that have been acquired will
   *	be released.
   *
   *	@param <b>locks</b>	All of these locks must be acquired before
   *			acquireMultiple returns. <b>Warning:</b> It's your job
   *			to make sure that The <code>locks</code> array is not modified while
   *			<code>acquireMultiple()</code> is executing.
   *	@param <b>timeout</b> Maximum time to wait to acquire each
   *			lock (milliseconds). The total time for the multiple
   *			acquire operation could be (timeout * locks.length).
   **/

  public static void acquireMultiple( Semaphore[] locks, long timeout )
    throws InterruptedException, Semaphore.TimedOut
  {
    acquire( locks, timeout );
  }

  /** Just like {@link #acquireMultiple(Semaphore[],long)}, except
   *	that it takes a collection--rather than an array--argument.
   */

  public static void acquireMultiple( Collection semaphores, long timeout )
    throws InterruptedException, Semaphore.TimedOut
  {
    acquire( semaphores.toArray(), timeout );
  }

  /** Actually do the acquisition here. The main reason this work can't
   *	be done in acquireMultiple is that the <code>toArray()</code> method called
   *  in the Collection version returns an array of <code>Object</code>, and you
   *	can't cast an array of <code>Object</code> into an array of <code>Semaphore</code>.
   */

  private static void acquire( Object[] locks, long timeout )
    throws InterruptedException, Semaphore.TimedOut
  {
    int currentLock = 0;

    try
    {
      // It's potentially dangerous to work directly on the locks
      // array rather than on a copy. I didn't want to incur the
      // overhead of making a copy, however.

      long expiration = ( timeout == Semaphore.FOREVER )
                        ? Semaphore.FOREVER
                        : System.currentTimeMillis() + timeout;
      ;

      Arrays.sort( locks, compareStrategy ); //#sort
      for( ; currentLock < locks.length; ++currentLock )
      {
        long timeRemaining =
          expiration - System.currentTimeMillis();
        if( timeRemaining <= 0 )
          throw new Semaphore.TimedOut( "Timed out waiting to acquire multiple locks" );

        ( ( Semaphore )locks[currentLock] ).acquire( timeRemaining );
      }
    }
    catch( Exception exception )
    { // Release all locks up to (but not including)
      // locks[currentLock];

      while( --currentLock >= 0 )
        ( ( Semaphore )locks[currentLock] ).release();

      if( exception instanceof InterruptedException )
        throw( InterruptedException )exception;
      else if( exception instanceof Semaphore.TimedOut )
        throw( Semaphore.TimedOut )exception;
      else
        throw new Error( "Unexpected exception:" + exception );
    }
  }
}
package haui.asynch;

/**
 *	This class implements a simple "condition variable." The notion
 *	is that a thread waits for some condition to become true.
 *	If the condition is false, then no wait occurs.
 *
 *	Be very careful of nested-monitor-lockout here:
 * <pre>
 *	 class lockout
 *	 {	Condition godot = new Condition(false);
 *
 *	 	synchronized void f()
 *	 	{
 *	 		someCode();
 *	 		godot.waitForTrue();
 *	 	}
 *
 *	 	synchronized void set() // <b>Deadlock if another thread is in f()</b>
 *	 	{	godot.setTrue();
 *	 	}
 *	 }
 * </pre>
 *	You enter f(), locking the monitor, then block waiting for the
 *  condition to become true. Note that you have not released the
 *  monitor for the "lockout" object.
 *	[The only way to set <code>godot</code> true
 *  is to call <code>set()</code>, but you'll block on entry
 *  to <code>set()</code> because
 *	the original caller to <code>f()</code> has the monitor
 *	containing "lockout" object.]
 *	<p>Solve the problem by releasing the monitor before waiting:
 * <pre>
 *	 class okay
 *	 {	Condition godot = new Condition(false);
 *
 *	 	void f()
 *	 	{	synchronized( this )
 *	 		{	someCode();
 *	 		}
 *	 		godot.waitForTrue();	// <b>Move the wait outside the monitor</b>
 *	 	}
 *
 *	 	synchronized void set()
 *	 	{	godot.setTrue();
 *	 	}
 *	 }
 * </pre>
 * or by not synchronizing the <code>set()</code> method:
 * <pre>
 *	 class okay
 *	 {	Condition godot = new Condition(false);
 *
 *	 	synchronized void f()
 *	 	{	someCode();
 *	 		godot.waitForTrue();
 *	 	}
 *
 *	 	void set()				// <b>Remove the synchronized statement</b>
 *	 	{	godot.setTrue();
 *	 	}
 *	}
 * </pre>
 * The normal <code>wait()</code>/<code>notify()</code> mechanism
 * doesn't have this problem since
 * <code>wait()</code> releases the monitor,
 * but you can't always use <code>wait()</code>/<code>notify()</code>.
 * <br><br>
 *
 */

public class Condition
  implements Semaphore
{
  private boolean m_blTrue;

  /**	Create a new condition variable in a known state.
   */
  public Condition( boolean isTrue )
  {
    this.m_blTrue = isTrue;
  }

  /** Set the condition to false. Waiting threads are not affected.
   *	Setting an already false condition variable to false is a
   *  harmless no-op.
   */
  public synchronized void setFalse()
  {
    m_blTrue = false;
  }

  /** Set the condition to true, releasing any waiting threads.
   */
  public synchronized void setTrue()
  {
    m_blTrue = true;
    notifyAll();
  }

  /** For those who actually know what "set" and "reset" mean, I've
   *  provided versions of those as well. (Set and "set to true" mean
   *  the same thing. You "reset" to enter the false condition.)
   */

  public final void set()
  {
    setTrue();
  }

  public final void reset()
  {
    setFalse();
  }

  /** Returns true if the condition variable is in the "true" state.
   *  Can be dangerous to call this method if the condition can change.
   */

  public final boolean isTrue()
  {
    return m_blTrue;
  }

  /** Release all waiting threads without setting the condition true. This
   *	method is effectively a "stateless" or "pulsed" condition variable,
   *	as is implemented by Java's <code>wait()</code> and <code>notifY()</code>
   *  calls. Only those threads that are waiting are released and subsequent
   *	threads will block on this call. The main difference between raw Java
   *  and the use of this function is that {@link waitForTrue()}, unlike
   *	<code>wait()</code> indicates a timeout with an exception toss. In Java
   *	there is no way to distinguish whether <code>wait()</code> returned because of
   *	an expired time out or because the object was notified.
   */
  public final synchronized void releaseAll()
  {
    notifyAll();
  }

  /** Release one waiting thread without setting the condition true
   */
  public final synchronized void releaseOne()
  {
    notify();
  }

  /** Wait for the condition to become true.
   *  @param timeout Timeout, in milliseconds. If 0, method returns
   *			immediately.
   *  @return false if the timeout was 0 and the condition was
   *			false, true otherwise.
   */

  public final synchronized boolean waitForTrue( long timeout )
    throws InterruptedException, Semaphore.TimedOut
  {
    if( timeout == 0 || m_blTrue )
      return m_blTrue;

    if( timeout == Semaphore.FOREVER )
      return waitForTrue();

    long expiration = System.currentTimeMillis() + timeout;
    while( !m_blTrue ) //#spinLock
    {
      long timeRemaining = expiration - System.currentTimeMillis();
      if( timeRemaining <= 0 )
        throw new Semaphore.TimedOut( "Timed out waiting to acquire Condition Variable" );

      wait( timeRemaining );
    }

    if( !m_blTrue ) // assume we've timed out.
      throw new Semaphore.TimedOut();

    return true;
  }

  /** Wait (potentially forever) for the condition to become true.
   *  This call is a bit more efficient than
   *  <code>waitForTrue(Semaphore.FOREVER);</code>
   */
  public final synchronized boolean waitForTrue()
    throws InterruptedException
  {
    while( !m_blTrue )
      wait();
    return true;
  }

  //------------------------------------------------------------------
  // Support for the Semaphore interface:
  //------------------------------------------------------------------

  private final int m_iId = LockManager.newId();

  /** The <code>id()</code> method returns the unique integer
   *  identifier of the current condition
   *	variable. You can use this ID to sort an array of semaphores
   *  in order to acquire them in the same order, thereby avoiding
   *	a common deadlock scenario.
   */
  public int id()
  {
    return m_iId;
  }

  /** Identical to {@link waitFortrue(long)}
   */
  public boolean acquire( long timeout )
    throws InterruptedException, Semaphore.TimedOut
  {
    return waitForTrue( timeout );
  }

  /** Identical to {@link setTrue()}
   */

  public void release()
  {
    setTrue();
  }
}
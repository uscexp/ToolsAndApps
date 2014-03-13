package haui.asynch;

import java.util.*; // just for testing

import haui.asynch.Semaphore; // contains definition of timeout exception

/** A simple generic spin lock. Use it to wait for some condition to be
 *	true when a notify is received. For example, this code:
 *	<pre>
 *	boolean someCondition = false; // set true by another thread
 *
 *	SpinLock lock = new SpinLock();
 *	lock.acquire(	new SpinLock.Condition()
 * 				 	{	public boolean satisfied()
 * 						{	return someCondition == false;
 *						}
 *					},
 *					1000	// wait at most one second
 *				);
 *	//...
 *	lock.release();
 *	</pre>
 *	has roughly the same effect as:
 *	<pre>
 *	Object lock;
 *
 *	while( someCondition == false )
 *		lock.wait(1000);
 *	//...
 *	lock.notify();
 *	</pre>
 *	The timeout will be reliable if you use the SpinLock;
 *	it may not be reliable if you use `wait()`.
 * <br>
 */

public final class SpinLock
{
  /**	A gang-of-four Strategy object that tells the spin lock the condition
   *	for which we're waiting. Override <code>satisfied()</code> to return
   *	true when the condition for which we're waiting becomes true.
   */

  public interface Condition
  {
    boolean satisfied();
  }

  /** Block until the condition specified by <code>condition</code> becomes
   *	true <u>and</u> the current SpinLock is passed a {@link release()} message
   *	after the condition becomes true.
   *
   *	@throws Semaphore.TimedOut	if the timeout expires
   *	@throws InterruptedException if another thread interrupts the timeout
   */
  public synchronized void acquire( Condition condition, long timeout )
    throws Semaphore.TimedOut, InterruptedException
  {
    long expiration = System.currentTimeMillis() + timeout; //#here
    while( !condition.satisfied() )
    {
      timeout = expiration - System.currentTimeMillis();
      if( timeout <= 0 )
        throw new Semaphore.TimedOut( "Spin lock timed out." );
      wait( timeout ); //#there
    }
  }

  public synchronized void release()
  {
    notify();
  }

  /******************************************************************
   * Test class, prints "hello world" when executed.
   */

  public static final class Test
  {
    public static void main( String[] args )
      throws Exception
    {
      final Stack stack = new Stack();
      final SpinLock lock = new SpinLock();
      new Thread()
      {
        public void run()
        {
          try
          {
            lock.acquire( new SpinLock.Condition()
            {
              public boolean satisfied()
              {
                return!stack.isEmpty();
              }
            }
            , 4000
            );

            System.out.println( stack.pop().toString() );
          }
          catch( Exception e )
          {}
        }
      }

      .start();

      Thread.sleep( 500 ); // give the thread a
      // chance to get started.
      stack.push( "hello world" );
      lock.release();
    }
  }
}
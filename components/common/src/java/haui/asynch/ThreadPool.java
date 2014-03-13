package haui.asynch;

import haui.asynch.BlockingQueue;

/**
 * A generic implementation of a thread pool. Use it like this: <PRE> ThreadPool pool = new ThreadPool(); pool.execute (	new Runnable() {	public void run() {	// execute this function on an existing // thread from the pool. } } ); </PRE> <p>The size of the thread pool can expand automatically to accommodate requests for execution. That is, if a thread is available in the pool, it's used to execute the Runnable object, otherwise a new thread can be created (and added to the pool) to execute the request. A maximum count can be specified to limit the number of threads in the pool, however. <p>Each thread pool also forms a thread group (all threads in the pool are in the group). In practice this means that the security manager controls whether a thread in the pool can access threads in other groups, but it also gives you an easy mechanism to make the entire group a Daemon. Unlike a <code>ThreadGroup</code>, it's possible to do the equivalent of a <code>join()</code> operations simply by calling <code>join()</code> on the thread pool itself. The  {@link #join}  method is provided for this purpose. You can also <code>wait()</code> for a thread pool to become idle, but if it's idle when you start to wait, the wait isn't satisfied until the pool becomes active and then quiescent again. {@link join} , on the other hand, returns immediately if the pool is idle. You can remove a thread from the pool by interrupting it. Often the easiest way to do this is to enqueue a request that issues the interrupt: <PRE> somePool.execute (	new Runnable() {	public void run() {	Thread.currentThread().interrupt(); } } ); </PRE> The  {@link deflate}  method removes all threads that were created over and above the initial size, and is usually a better choice than an explicit interrupt, however. <br> <br> <p>History (modifications since book was published) <table> <tr>06/05/00<td></td>Cleaned up constructor code so that all the pooledThreads threads are created before constructor returns. Fixed a bug in the idle-thread counting mechanism. <tr>08/14/02<td></td>Added a toString() method that evaluates to a string that represents the pool's current state. Note that this method can use the replaceAll method of String, new to JDK 1.4. It's commented out right now so that the code will work on older versions of Java, but you might want to put it back in to make the output more readable. <tr>08/14/02<td></td>Removed the version of @{link execute} that took a Command argument (rather than a Runnable). It's just commented out, so you can put it back in if you need it. <tr>08/15/02<td></td>Modified  {@link #join}  to throw InterruptedException if it's interrupted. <tr>08/15/02<td></td>Modified  {@link #join()}  so that it will return immediately if the pool is idle when it's called. <tr>08/15/02<td></td>Eliminated "joiners" field. All threads that are waiting on the thread pool are simply notified whenever the pool becomes idle. This is a slight inefficiency, but simplifies the code. <tr>08/15/02<td></td>Modified code to permit the pool to have an initial size of zero. <tr>08/15/02<td></td>Fixed PooledThread constructor to eliminate a race condition (and also a possible deadlock) on pool startup. <tr>08/16/02<td></td>Fixed an (unrported) bug in execute() so that we're now guaranteed that newly created threads are running before execute returns, Otherwise the pool size and idle-thread count could be incorrect. <tr>09/17/02<td></td>Modified the join() logic so that the PooledThread doesn't notify the joiners until all threads are idle AND there are no requests waiting to be serviced. <td></td></tr> My thanks (in no particular order) to Scott Willy, Aaron Greenhouse, TK Samy, Patrick Hancke, Dave Yost, Greg Charles, Pavel Lisovin, Omi Traub, Anatoly Pidruchny, Michael Oswall, and Anders Lindell for sending in bug reports and suggestions. (If I've forgotten anybody, I'm sorry.) </table>
 */
public final class ThreadPool
  extends ThreadGroup
{
  private final BlockingQueue m_bqPool = new BlockingQueue();
  private final int m_iInitialSize;
  private final int m_iMaximumSize;
  private int m_iIdleThreads = 0;
  private int m_iNewThreads = 0;
  private int m_iPoolSize = 0;
  private volatile boolean m_blHasClosed = false;
  private boolean m_blDeflate = false;
  private Object m_objCreationLock = new Object();

  // The m_objPooledThreadsAllRunning lock assures that all of the
  // threads in the pool are running before the constructor
  // returns. The constructor waits on it, and the last thread
  // added to the pool issues the notify when it detects that
  // the pool is now full [m_iInitialSize == m_iPoolSize].
  //
  private Object m_objPooledThreadsAllRunning = new Object();

  private static int m_iGroupNumber = 0;
  private static int m_iThreadId = 0;

  /******************************************************************
   * Return a string that shows the pool's current state.
   */
  public synchronized String toString()
  {
    StringBuffer message = new StringBuffer();
    message.append( "pool:" );

    message.append( m_bqPool.toString() );
    // If you're running JDK 1.4, use the following:
    // 		message.append( m_bqPool.toString().replaceAll("\\n","\n\t") );

    message.append( "\ninitialSize = " + m_iInitialSize );
    message.append( "\nmaximumSize = " + m_iMaximumSize );
    message.append( "\nidleThreads = " + m_iIdleThreads );
    message.append( "\nnewThreads  = " + m_iNewThreads );
    message.append( "\npoolSize    = " + m_iPoolSize );
    message.append( "\nhasClosed   = " + m_blHasClosed );
    message.append( "\ndeflate      = " + m_blDeflate );
    message.append( "\n" );
    return message.toString();
  }

  /******************************************************************
   * These are the objects that wait to be activated. They are
   * typically blocked on an empty queue. You post a Runnable
   * object to the queue to release a thread, which will execute
   * the run() method from that object. All Pooled-thread objects
   * will be members of the thread group the comprises the tread
   * pool.
   */

  private final class PooledThread
    extends Thread
  {
    public PooledThread()
    {
      super( ThreadPool.this, "T" + m_iThreadId++ );
    }

    public void run()
    {
      // The following handshake is performed both
      // by the ThreadPool constructor (when it creates the
      // pool) and by the execute() method (when it increases
      // the pool size during lazy instantiation):
      //
      // The initial synchronized statement blocks
      // until the ThreadPool constructor [or execute()] has
      // started all of the threads in the pool. I'd much
      // rather synchronize on ThreadPool.this than the
      // m_objCreationLock, but synchronization on this
      // doesn't work in a constuctor.
      //
      // The notifyAll() is the second part of the handshake,
      // and notifies the constructor [or execute()] that all
      // of the threads // are not only started, but running,
      // and that the m_iIdleThreads and m_iPoolSize
      // fields are accurate.

      synchronized( m_objCreationLock )
      {
        ++m_iIdleThreads; // Thread has been started, and is idle
        ++m_iPoolSize;

        if( --m_iNewThreads <= 0 )
        {
          synchronized( m_objPooledThreadsAllRunning )
          {
            m_objPooledThreadsAllRunning.notifyAll();
          }
        }
      }

      try
      {
        // The ThreadPool constructor synchronizes on the
        // ThreadPool object that's being constructed until
        // that Thread-pool object is fully constructed. The
        // PooledThread objects are both created and started
        // from within that constructor. The following
        // "synchronized" statement forces the pooled threads
        // to block until initialization is complete and the
        // constructor gives up the lock. This synchronization
        // is essential on a multiple-CPU machine to make sure
        // that the various CPU caches are in synch with each
        // other.

        while( !isInterrupted() && !m_blHasClosed )
        {
          try
          {
            Runnable command = ( Runnable )m_bqPool.dequeue();

            synchronized( ThreadPool.this )
            {
              --m_iIdleThreads;
            }

            if( command == null ) // sentinal to kill thread.
              break;

            command.run();
          }
          catch( InterruptedException e ) // User-issued stop
          {
            break;
          }
          catch( BlockingQueue.Closed e )
          { // Ignore it. The thread pool is shutting down.
          }
          catch( Exception e )
          {
            System.err.println( "Ignoring exception thrown from "
                                + " user-supplied thread-pool action:\n\t"
                                + e.getMessage() );
          }
          finally
          {
            synchronized( ThreadPool.this )
            {
              ++m_iIdleThreads;

              // If none of the threads in the pool are
              // doing anything, notify any joined threads.

              if( allThreadsAreIdle() && m_bqPool.isEmpty() )
                ThreadPool.this.notifyAll();
            }
          }
        }
      }
      finally
      {
        synchronized( ThreadPool.this )
        {
          synchronized( m_objCreationLock )
          {
            --m_iIdleThreads;
            --m_iPoolSize;
          }
        }
      }
    }
  }

  /******************************************************************
   * Convenience method, aids readability.
   */
  private boolean allThreadsAreIdle()
  {
    return m_iIdleThreads == m_iPoolSize;
  }

  /******************************************************************
   *	Create a thread pool with initialThreadCount threads
   *	in it. The pool can expand to contain additional threads
   *  if they are needed.
   *
   *	@param <b>initialThreadCount</b>	The initial thread count.
   *			If the initial count is greater than the maximum, it is
   *			silently truncated to the maximum.
   *  @param <b>maximumThreadCount</b> specifies the maximum number
   *			of threads that can be in the pool. A maximum of 0
   *			indicates that the 	pool will be permitted to grow
   *			indefinitely.
   */

  public ThreadPool( int initialThreadCount, int maximumThreadCount )
  {
    super( "ThreadPool" + m_iGroupNumber++ );

    // I'm synchronizing, here, to prevent the PooledThread objects
    // that are created below from being able to access the current
    // object until it's fully initialized. They synchronize on
    // "m_objCreationLock" at the top of their run() methods. The wait call
    // prevents the contructor from returning until all the threads
    // are active. [The notify is in the PooledThreads run() method.]
    //
    // Note that you can't syncronize on "this" in a constructor
    // so I'm using two roll-your-own locks to implement
    // the required handshake. (In Hotspot, at least, synchronizing
    // on "this" in a constructor is effectively a no-op. It
    // doesn't actually grab the lock.)

    synchronized( m_objPooledThreadsAllRunning )
    {
      synchronized( m_objCreationLock )
      {
        m_iInitialSize = initialThreadCount;
        m_iMaximumSize = ( maximumThreadCount > 0 )
                      ? maximumThreadCount : Integer.MAX_VALUE;

        if( initialThreadCount > m_iMaximumSize )
          throw new IllegalArgumentException
            ( "ThreadPool: initialThreadCount > m_iMaximumSize" );

        // m_iNewThreads is used by the handshake with the newly
        // created threads. Take a look in the PooledThread
        // constructor to see how this works
        m_iNewThreads = initialThreadCount;

        for( int i = initialThreadCount; --i >= 0; )
          new PooledThread().start();
      }

      if( m_iNewThreads > 0 )
      {
        try
        {
          m_objPooledThreadsAllRunning.wait();
        }
        catch( InterruptedException e )
        {
          throw new Error(
            "Internal error: unexpected InterruptedException" );
        }
      }
    }
  }

  /******************************************************************
   *	An efficient way to say <code>ThreadPool(0,0)</code>.
   */

  public ThreadPool()
  {
    super( "ThreadPool" + m_iGroupNumber++ );
    this.m_iInitialSize = 0;
    this.m_iMaximumSize = Integer.MAX_VALUE;
  }

  /******************************************************************
   *  Execute the <code>run()</code> method of the Runnable object on a thread
   *	in the pool. A new thread is created if the pool is
   *	empty and the number of threads in the pool is not at the
   *  maximum.
   *
   * 	@throws ThreadPool.Closed if you try to execute an action
   *			on a pool to which a close() request has been sent.
   */

  public synchronized void execute( Runnable action )
    throws Closed
  {
    // You must synchronize on the pool because the PooledThread's
    // run method is asynchronously dequeueing elements. If we
    // didn't synchronize, it would be possible for the isEmpty()
    // query to return false, and then have a PooledThread sneak
    // in and put a thread on the queue (by doing a blocking dequeue).
    // In this scenario, the number of threads in the pool could
    // exceed the maximum specified in the constructor. You'll have
    // to look closely at the pooled-threads run() method to understand
    // why we need two locks. Run needs to get both of the locks
    // we acquire here before it can issue a notify(). It can't get
    // both locks until we both leave the synchronized(this) block and
    // also issue the wait(). This way, we're guaranteed that the
    // notify is issued by run() after the wait() is called here.
    //
    // The InterruptedException that comes from wait is ignored because
    // the only references to m_objPooledThreadsAllRunning are internal,
    // and an interrupt is never issued internally. Just to be on the
    // safe side, I throw an Error() should the impossible happen.
    //
    // What a mess.

    boolean addedThread = false;

    synchronized( m_objPooledThreadsAllRunning )
    {
      synchronized( m_objCreationLock )
      {
        if( m_blHasClosed )
          throw new Closed();

        m_bqPool.enqueue( ( Object )action ); // Attach action to the thread

        if( m_iPoolSize < m_iMaximumSize && m_bqPool.waitingThreads() == 0 )
        {
          addedThread = true;
          m_iNewThreads = 1;
          new PooledThread().start(); // Add thread to pool
        }
      }

      // Don't wait if we haven't added any threads, but the
      // m_objCreationLock must have been released before we can
      // wait, otherwise we'll deadlock.

      if( addedThread )
        try
        {
          m_objPooledThreadsAllRunning.wait();
        }
        catch( InterruptedException e )
        {
          throw new Error(
            "Internal error: unexpected InterruptedException" );
        }
    }
  }

//	/******************************************************************
//	 *  A convenience wrapper to execute a Command (rather than a
//	 *  Runnable) object. Just creates a Runnable that
//	 *  calls <code>action.fire(argument)</code> and passes that
//	 *  Runnable to {@link execute}
//	 */
//
//	final synchronized
//	public void execute( final Command action, final Object argument)
//														throws Closed
//	{	execute(	new Runnable()
//					{	public void run()
//						{	action.fire( argument );
//						}
//					}
//		       );
//	}
//
  /******************************************************************
   * Objects of class ThreadPool.Closed are thrown if you try to
   * execute an action on a closed ThreadPool.
   */

  public final class Closed
    extends RuntimeException
  {
    Closed()
    {
      super( "Tried to execute operation on a closed ThreadPool" );
    }
  }

  /******************************************************************
   *	Kill all the threads waiting in the thread pool, and arrange
   *	for all threads that came out of the pool, but which are working,
   *	to die natural deaths when they're finished with whatever they're
   *	doing. Actions that have been passed to execute() but which
   *	have not been assigned to a thread for execution are discarded.
   *	<p>
   *  No further operations are permitted on a closed pool, though
   *	closing a closed pool is a harmless no-op.
   */

  public synchronized void close()
  {
    m_blHasClosed = true;
    m_bqPool.close(); // release all waiting threads
  }

  /******************************************************************
   * Wait for the pool to become idle if it's active.
   * You can also <code>wait()</code> for a thread pool to
   * become idle, but if it's idle when you start to wait, the wait isn't
   * satisfied until the pool becomes active and then quiescent again.
   * The join() method, on the other hand, returns immediately if the
   * pool is idle.
   *
   * In retrospect, "join" is not a great name for this method
   * since the threads are not dead, just idle. I'm reluctant
   * to change the name because I don't want to break existing code.
   * I've * provided a {@link #waitForAllThreadsToBeIdle} method
   * if you'd like a more sensible name, however.
   *
   * @throws InterruptedException if the waiting thread is
   * 			interrupted.
   */

  public synchronized void join()
    throws InterruptedException
  {
    // If all the threads are currently idle and no actions
    // are queued up waiting for service, then the pool
    // really is idle.

    if( ! ( allThreadsAreIdle() && m_bqPool.isEmpty() ) )
      wait();
  }

  /** Same as {@link #join}. Better name.
   */
  public void waitForAllThreadsToBeIdle()
    throws InterruptedException
  {
    join();
  }

  /******************************************************************
   * If the argument is true,
   * discard threads as they finish their operations until the
   * initial thread count passed to the constructor. That is, the
   * number of threads in the pool will not go below the initial
   * count, but if the number of threads in the pool expanded, then
   * the pool size will shrink to the initial size as these extra
   * threads finish whatever they're doing. It's generally best to
   * set this flag before any threads in the pool are activated.
   * If the argument is false (the default behavior of the thread
   * pool), then treads created above the initial count remain
   * in the pool indefinitely.
   * <p>
   * Though you can call deflate at any time to deflate the
   * pool back to its original size, it's generally best to
   * call deflate immediately after creating the thread pool.
   * Otherwise, the deflation state might be surprising when
   * the first few threads run.
   */

  public synchronized void deflate( boolean doDeflate )
  {
    if( m_blDeflate = doDeflate )
    {
      int excessThreads = m_iPoolSize - m_iInitialSize;
      while( --excessThreads >= 0 )
        m_bqPool.enqueue( null );
    }
  }

  /* ============================================================== */

  /**
   * Module:      ThreadPool$Test<br> <p> Description: ThreadPool$Test<br> </p><p> Created:     08.04.2008 by Andreas Eisenhauer </p><p>
   * @history      08.04.2008 by AE: Created.<br>  </p><p>
   * @author       <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>  </p><p>
   * @version      v0.1, 2008; %version: %<br>  </p><p>
   * @since        JDK1.4  </p>
   */
  public static final class Test
  {
    private static ThreadPool m_tpPool = new ThreadPool( 10, 10 );

    public static void main( String[] args )
      throws Exception
    {
      Test testBed = new Test();

      System.out.println( "Thread pool is running" );

      testBed.fireRunnable( "hello", m_tpPool );
      System.out.println( "hello fired" );

      m_tpPool.join();
      System.out.println( "Pool is idle, closing" );

      m_tpPool.close();
      System.out.println( "Pool closed" );

      m_tpPool = new ThreadPool(); // test lazy instantiation.
      m_tpPool.deflate( true );

      System.out.println( "\n\nNew thread pool is running:" );
      System.out.println( m_tpPool.toString() );

      testBed.fireRunnable( "lazy instantiation", m_tpPool );

      m_tpPool.join();
      Thread.sleep( 250 ); // wait a bit for the
      // pool to decay back to 0 size

      testBed.fireRunnable( "lazy instantiation", m_tpPool );
      m_tpPool.join();
      m_tpPool.close();
    }

    // The argument must be final in order for it to be accessed
    // from the inner class.

    private void fireRunnable( final String id, final ThreadPool pool )
    {
      pool.execute
        ( new Runnable()
      {
        public void run()
        {
          System.out.println( "-------------Begin " + id );

          // System.out.println( "Pool state:\n" + pool );
          try
          {
            Thread.sleep( 250 );
          }
          catch( InterruptedException e )
          {}

          System.out.println( "-------------End " + id );
        }
      }
      );
    }
  }
}
package haui.asynch;

import java.util.*;

/***********************************************************************
 * This is a thread-safe queue that blocks automatically if you
 *	try to dequeue from an empty queue. It's based on a linked list,
 *  so will never fill. (You'll never block on a queue-full condition
 *	because there isn't one.)
 *
 *	<p>
 *	This class uses the <code>LinkedList</code> class, introduced into
 *	the JDK at version 1.2. It will not work with earlier releases.
 *
 * <br><br>
 *
 * <p>History (modifications since book was published)
 * <table>
 * <tr>08/15/02<td></td>Syncronized {@link #isEmtpy} and
 * 					 {@link #m_iWaitingThreads} to handle potential
 * 					 visibility problems in SMP environments.
 * <td></td></tr>
 * </table>
 *
 */
public final class BlockingQueue
{
  private LinkedList m_llistElements = new LinkedList();
  private boolean m_blClosed = false;
  private boolean m_blRejectEnqueueRequests = false;
  private int m_iWaitingThreads = 0;
  private int m_iMaximumSize = Integer.MAX_VALUE;

  public synchronized String toString()
  {
    StringBuffer message = new StringBuffer();
    message.append( "\nclosed                  = " + m_blClosed );
    message.append( "\nrejectEnqueueRequests = " + m_blRejectEnqueueRequests );
    message.append( "\nwaitingThreads         = " + m_iWaitingThreads );
    message.append( "\nmaximumSize            = " + m_iMaximumSize );
    message.append( "\n" + m_llistElements.size() + " elements: " + m_llistElements );
    message.append( "\n" );
    return message.toString();
  }

  /*******************************************************************
   * The BlockingQueue.Exception class is the base class of the
   * other exception classes, provided so that you can catch any
   * queue-related error with a single <code>catch</code> statement.
   */
  public class Exception
    extends RuntimeException
  {
    public Exception( String s )
    {
      super( s );
    }
  }

  /*******************************************************************
   * The Closed exception is thrown if you try to used an explicitly
   * closed queue. See {@link #close}.
   */

  public class Closed
    extends Exception
  {
    private Closed()
    {
      super( "Tried to access closed BlockingQueue" );
    }
  }

  /*******************************************************************
   * The full exception is thrown if you try to enqueue an item in
   * a size-limited queue that's full.
   */

  public class Full
    extends Exception
  {
    private Full()
    {
      super( "Attempt to enqueue item to full BlockingQueue." );
    }
  }

  /*******************************************************************
   * Convenience constructor, creates a queue with no effective upper
   * limit on the size.
   */

  public BlockingQueue()
  {}

  /*******************************************************************
   * Constructs a queue with the indicated maximum number of elements.
   */

  public BlockingQueue( int maximumSize )
  {
    this.m_iMaximumSize = maximumSize;
  }

  /*******************************************************************
   *	Enqueue an object that will remain in the queue until it is
   *  dequeued.
   **/
  public synchronized final void enqueue( Object newElement )
    throws Closed, Full
  {
    if( m_blClosed || m_blRejectEnqueueRequests )
      throw new Closed();

    // Detect a full queue. Queues of size 0 are allowed to grow
    // indefinitely.

    if( m_llistElements.size() >= m_iMaximumSize )
      throw new Full();

    m_llistElements.addLast( newElement );
    notify(); //#notify
  }

  /*******************************************************************
   *	Enqueue an object that will remain in the queue for at most
   *	"timeout" milliseconds. The <code>run()</code> method of the
   *	<code>onRemoval</code> object is called if the object is
   *	removed in this way.
   *
   *	If a given object is in the queue more than once, then the first
   *	occurrence of the object is removed.
   *
   * @param newElement	The object to enqueue
   * @param timeout		The maximum time that the object will spend
   *						in the queue (subject to the usual variations
   *						that can occur if a higher-priority thread
   *						happens to be running when the timeout occurs).
   * @param onRemoval	If non-null, the <code>run()</code> method
   *						is called if the object is removed due to
   *						a timeout. If <code>null</code>, nothing
   *						in particular is done when the object is
   *						removed.
   */

  //#enqueue.with.timeout
  public synchronized final void enqueue( final Object newElement, final long timeout, final Runnable onRemoval )
  {
    enqueue( newElement );

    new Thread()
    {
      {
        setDaemon( true );
      } // instance initializer, effectively

      // a constructor for this object.
      public void run()
      {
        try
        {
          boolean found;

          sleep( timeout );
          synchronized( BlockingQueue.this )
          {
            found = m_llistElements.remove( newElement );

            if( found && ( m_llistElements.size() == 0 )
                && m_blRejectEnqueueRequests )
            {
              close(); // Have just removed final item,
            }
          }

          if( found && onRemoval != null )
            onRemoval.run();
        }
        catch( InterruptedException e )
        { /* can't happen */}
      }
    }

    .start();
  }

  /*******************************************************************
   * Convenience method, calls {@link enqueue(Object,long,Runnable)} with
   * a null <code>onRemoval</code> reference.
   */

  public synchronized final void enqueue( final Object newElement, final long timeout )
  {
    enqueue( newElement, timeout, null );
  }

  /*******************************************************************
   * Enqueue an item, and thereafter reject any requests to enqueue
   * additional items. The queue is closed automatically when the
   * final item is dequeued.
   */

  public synchronized final void enqueueFinalItem( Object last )
    throws Closed
  {
    enqueue( last );
    m_blRejectEnqueueRequests = true;
  } //#final.end

  /*******************************************************************
   *	Dequeues an element; blocks if the queue is empty
   *	(until something is enqueued). Be careful of nested-monitor
   *  lockout if you call this function. You must ensure that
   *	there's a way to get something into the queue that does
   *  not involve calling a synchronized method of whatever
   *  class is blocked, waiting to dequeue something.
   *  You can {@link Thread#interrupt interrupt} the dequeueing thread
   *  to break it out of a blocked dequeue operation, however.
   *
   *	@param timeout	Time-out value in milliseconds. An
   *					<code>ArithmeticException</code> is thrown
   *					if this value is greater than a million years
   *					or so.
   *					Use {@link Semaphore#FOREVER} to wait forever.
   *
   *  @see #enqueue
   *  @see #drain
   *  @see #nonblockingDequeue
   *
   *	@return the dequeued object or null if the wait timed out and
   *			nothing was dequeued.
   *
   *  @throws InterruptedException	if interrupted while blocked
   *  @throws Semaphore.TimedOut		if timed out while blocked
   *  @throws BlockingQueue.Closed	on attempt to dequeue from a closed queue.
   */

  public synchronized final Object dequeue( long timeout )
    throws InterruptedException,
    Closed,
    Semaphore.TimedOut
  {
    if( m_blClosed )
      throw new Closed();
    try
    { // If the queue is empty, wait. I've put the spin lock
      // inside an "if" so that the waitingThreads count doesn't
      // jitter while inside the spin lock. A thread is not
      // considered to be done waiting until it's actually
      // acquired an element or the timeout is exceeded.
      //
      //
      long expiration = ( timeout == Semaphore.FOREVER )
                        ? Semaphore.FOREVER
                        : System.currentTimeMillis() + timeout;
      ;

      if( m_llistElements.size() <= 0 )
      {
        ++m_iWaitingThreads; //#waitingUp
        while( m_llistElements.size() <= 0 )
        {
          wait( timeout ); //#wait

          if( System.currentTimeMillis() > expiration )
          {
            --m_iWaitingThreads;
            throw new Semaphore.TimedOut( "Timed out waiting to dequeue " + "from BlockingQueue" );
          }

          if( m_blClosed )
          {
            --m_iWaitingThreads;
            throw new Closed();
          }
        }
        --m_iWaitingThreads; //#waitingDown
      }

      Object head = m_llistElements.removeFirst();

      if( m_llistElements.size() == 0 && m_blRejectEnqueueRequests )
        close(); // just removed final item, close the queue.

      return head;
    }
    catch( NoSuchElementException e ) // Shouldn't happen
    {
      throw new Error( "Internal error (haui.asynch.BlockingQueue)" );
    }
  }

  /*******************************************************************
   * Convenience method, calls {@link dequeue(long)} with a timeout of
   * Semaphore.FOREVER.
   */

  public synchronized final Object dequeue()
    throws InterruptedException,
    Closed,
    Semaphore.TimedOut
  {
    return dequeue( Semaphore.FOREVER );
  }

  /*******************************************************************
   *	The isEmpty() method is inherently unreliable in a
   *  multithreaded situation. In code like the following,
   *	it's possible for a thread to sneak in after the test but before
   *	the dequeue operation and steal the element you thought you
   *	were dequeueing.
   *	<PRE>
   *	BlockingQueue queue = new BlockingQueue();
   *	//...
   *	if( !someQueue.isEmpty() )
   *		someQueue.dequeue();
   *	</PRE>
   *	To do the foregoing reliably, you must synchronize on the
   *	queue as follows:
   *	<PRE>
   *	BlockingQueue queue = new BlockingQueue();
   *	//...
   *	synchronized( queue )
   *	{   if( !someQueue.isEmpty() )
   *			someQueue.dequeue();
   *	}
   *	</PRE>
   *	The same effect can be achieved if the test/dequeue operation
   *	is done inside a synchronized method, and the only way to
   *	add or remove queue elements is from other synchronized
   *	methods.
   */

  public synchronized final boolean isEmpty()
  {
    return m_llistElements.size() <= 0;
  }

  /****************************************************************
   * Return the number of threads waiting for a message on the
   * current queue. See {@link isEmpty} for warnings about
   * synchronization. This method is syncronized primarily to
   * assure visibility of a write in an SMP envrionrment.
   */

  public final synchronized int waitingThreads()
  {
    return m_iWaitingThreads;
  }

  /*******************************************************************
   * Close the blocking queue. All threads that are blocked
   * [waiting in dequeue() for items to be enqueued] are released.
   * The {@link dequeue()} call will throw a {@link BlockingQueue.Closed}
   * runtime
   * exception instead of returning normally in this case.
   * Once a queue is closed, any attempt to enqueue() an item will
   * also result in a BlockingQueue.Closed exception toss.
   *
   * The queue is emptied when it's closed, so if the only references
   * to a given object are those stored on the queue, the object will
   * become garbage collectible.
   */

  public synchronized void close()
  {
    m_blClosed = true;
    m_llistElements = null;
    notifyAll();
  }

  /**
   * Unit test for the BlockingQueue class.
   */
  public static final class Test
  {
    private static BlockingQueue queue = new BlockingQueue();
    private boolean timedOut = false;

    public static void main( String[] args )
      throws InterruptedException
    {
      new Test();
    }

    public Test()
      throws InterruptedException
    {
      // Test the enqueue timeout. Wait two seconds for a
      // dequeue operation that will never happen.
      queue.enqueue
        ( "Enqueue this String",
          2000, // two seconds
          new Runnable()
      {
        public void run()
        {
          System.out.println( "Enqueue timeout worked." );
          timedOut = true;
        }
      }
      );
      Thread.currentThread().sleep( 2500 );
      if( !timedOut )
        System.out.println( "Enqueue timeout failed." );

        // Create a thread that enqueues numbers and another
        // that dequeues them
      Thread enqueueing =
        new Thread()
      {
        public void run()
        {
          for( int i = 10; --i >= 0; )
            queue.enqueue( "" + i );

          queue.enqueueFinalItem( null );
        }
      };

      Thread dequeueing =
        new Thread()
      {
        public void run()
        {
          try
          {
            String s;
            while( ( s = ( String )queue.dequeue() ) != null )
              System.out.println( s );
          }
          catch( InterruptedException e )
          {
            System.out.println( "Unexpected InterruptedException" );
          }

          boolean closeHandledCorrectly = false;
          try
          {
            queue.enqueue( null );
          }
          catch( BlockingQueue.Closed e )
          {
            closeHandledCorrectly = true;
          }

          if( closeHandledCorrectly )
            System.out.println( "Close handled" );
          else
            System.out.println( "Error: Close failed" );
        }
      };

      dequeueing.start();
      enqueueing.start();
    }
  }
}
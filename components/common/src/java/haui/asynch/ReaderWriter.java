package haui.asynch;

import java.util.LinkedList;

/**
 *	This reader/writer lock prevents reads from occurring while writes
 *	are in progress and vice versa, and also prevents multiple writes from
 *	happening simultaneously. Multiple read operations can run in parallel,
 *  however. Reads take priority over writes, so any read operations
 *  that are pending while a write is in progress will execute before
 *  any subsequent writes execute. Writes are guaranteed to execute in
 *  the order that they were requested&#151;the oldest request is processed
 *	first.
 *	<p>
 *  You should use the lock as follows:
 *	<pre>
 *	public class DataStructureOrResource
 *	{
 *		ReaderWriter lock = new ReaderWriter();
 *
 *		public void access( ) throws InterruptedException
 *		{<b>
 *			lock.requestRead();
 *			try
 *			{</b>
 *				    // do the read/access operation here.<b>
 *			}
 *			finally
 *			{	lock.readAccomplished();
 *			}</b>
 *		}
 *
 *		public void modify( ) throws InterruptedException
 *		{<b>
 *			lock.requestWrite();
 *			try
 *			{</b>
 *				    // do the write/modify operation here.<b>
 *			}
 *			finally
 *			{	lock.writeAccomplished();
 *			}</b>
 *		}
 *	}
 *	</pre>
 *
 * <p>The current implementation of `ReaderWriter` doesn't support
 *		timeouts.
 *
 * <font size=-1>
 * This implementation is based on the one in Doug Lea's <i>Concurrent
 * Programming in Java</i> (Reading: Addison Wesley, 1997, pp. 300-303),
 * I've simplified the code (and cleaned it up) and added the nonblocking
 * acquisition methods.
 * I've also made the lock a stand-alone class rather than
 * a base class from which you have to derive. You might also want to
 * look at the very different implementation of the reader/writer lock
 * in Scott Oaks and Henry Wong's <i>Java Threads</i>
 * (Sebastopol [Calif.]: O'Reilly, 1997, pp. 180--187).
 * </font>
 *
 * <br>
 */

public final class ReaderWriter
{
  private int m_iActiveReaders; // = 0
  private int m_iWaitingReaders; // = 0
  private int m_iActiveWriters; // = 0

  /******************************************************************
   * I keep a linked list of writers waiting for access so that I
   * can release them in the order that the requests were received.
   * The size of this list is effectively the "waiting writers" count.
   *
   * Note that the monitor of the ReaderWriter object itself is
   * used to lock out readers while writes are in progress, thus
   * there's no need for a separate "readerLock."
   */
  private final LinkedList m_llistWriterLocks = new LinkedList();

  /******************************************************************
   * Request the read lock. Block until a read operation can be
   * performed safely.
   * This call must be followed by a call to <code>readAccomplished()</code>
   * when the read operation completes.
   */
  public synchronized void requestRead()
    throws InterruptedException
  {
    if( m_iActiveWriters == 0 && m_llistWriterLocks.size() == 0 )
      ++
      m_iActiveReaders;
    else
    {
      ++m_iWaitingReaders;
      wait(); //#readBlock

      // The waitingReaders count is decremented in
      // notifyReaders() when the waiting reader is
      // reactivated.
    }
  }

  /******************************************************************
   * This version of <code>read()</code> requests read access,
   * returns true if you get it. If it returns false, you may not
   * safely read from the guarded resource. If it returns true, you
   * should do the read, then call <code>readAccomplished</code> in
   * the normal way. Here's an example:
   *	<pre>
   *	public void read()
   *	{	if( lock.requestImmediateRead() )
   *		{	try
   *			{
   *				// do the read operation here
   *			}
   *			finally
   *			{	lock.readAccomplished();
   *			}
   *		}
   *		else
   *			// couldn't read safely.
   *	}
   *	</pre>
   */
  public synchronized boolean requestImmediateRead()
  {
    if( m_iActiveWriters == 0 && m_llistWriterLocks.size() == 0 )
    {
      ++m_iActiveReaders;
      return true;
    }
    return false;
  }

  /******************************************************************
   * Release the lock. You must call this method when you're done
   * with the read operation.
   */
  public synchronized void readAccomplished()
  { //assert m_iActiveReaders > 0 : "requestRead() not called";

    if( --m_iActiveReaders == 0 )
      notifyWriters();
  }

  /******************************************************************
   * Request the write lock. Block until a write operation can be
   * performed safely. Write requests are guaranteed to be
   * executed in the order received. Pending read requests take
   * precedence over all write requests.
   * This call must be followed by a call to <code>writeAccomplished()</code>
   * when the write operation completes.
   */
  public void requestWrite()
    throws InterruptedException
  {
    // If this method was synchronized, there'd be a nested-monitor
    // lockout problem: We have to acquire the lock for "this" in
    // order to modify the fields, but that lock must be released
    // before we start waiting for a safe time to do the writing.
    // If requestWrite() were synchronized, we'd be holding
    // the monitor on the ReaderWriter lock object while we were
    // waiting. Since the only way to be released from the wait is
    // for someone to call either readAccomplished()
    // or writeAccomplished() (both of which are synchronized),
    // there would be no way for the wait to terminate.

    Object lock = new Object(); //#lockCreate
    synchronized( lock )
    {
      synchronized( this )
      {
        boolean okayToWrite = m_llistWriterLocks.size() == 0 && m_iActiveReaders == 0 && m_iActiveWriters == 0;
        if( okayToWrite )
        {
          ++m_iActiveWriters;
          return; // the "return" jumps over the "wait" call
        }

        m_llistWriterLocks.addLast( lock ); // Note that the
        // activeWriters count
        // is incremented in
        // notifyWriters() when
        // this writer gets control
      }
      lock.wait();
    }
  }

  /******************************************************************
   * This version of the write request returns false immediately (without
   * blocking) if any read or write operations are in progress and
   * a write isn't safe; otherwise, it returns true and acquires the
   * resource. Use it like this:
   *	<pre>
   *	public void write()
   *	{	if( lock.requestImmediateWrite() )
   *		{	try
   *			{
   *				// do the write operation here
   *			}
   *			finally
   *			{	lock.writeAccomplished();
   *			}
   *		}
   *		else
   *			// couldn't write safely.
   *	}
   */
  synchronized public boolean requestImmediateWrite()
  {
    if( m_llistWriterLocks.size() == 0 && m_iActiveReaders == 0
        && m_iActiveWriters == 0 )
    {
      ++m_iActiveWriters;
      return true;
    }
    return false;
  }

  /******************************************************************
   * Release the lock. You must call this method when you're done
   * with the read operation.
   */
  public synchronized void writeAccomplished()
  {
    // The logic here is more complicated than it appears.
    // If readers have priority, you'll  notify them. As they
    // finish up, they'll call readAccomplished(), one at
    // a time. When they're all done, readAccomplished() will
    // notify the next writer. If no readers are waiting, then
    // just notify the writer directly.

    --m_iActiveWriters;
    if( m_iWaitingReaders > 0 ) // priority to waiting readers
      notifyReaders();
    else
      notifyWriters();
  }

  /******************************************************************
   * Notify all the threads that have been waiting to read.
   */
  private void notifyReaders() // must be accessed from a
  { //	synchronized method
    m_iActiveReaders += m_iWaitingReaders;
    m_iWaitingReaders = 0;
    notifyAll();
  }

  /******************************************************************
   * Notify the writing thread that has been waiting the longest.
   */
  private void notifyWriters() // must be accessed from a
  { //	synchronized method
    if( m_llistWriterLocks.size() > 0 )
    {
      Object oldest = m_llistWriterLocks.removeFirst();
      ++m_iActiveWriters;
      synchronized( oldest )
      {
        oldest.notify();
      }
    }
  }

  /**
   * The <code>Test</code> class is a unit test for the other code in the current file. Run the test with: <pre> java haui.asynch.ReaderWriter\$Test </pre> (the backslash isn't required with windows boxes), and don't include this class file in your final distribution. Though the output could vary in trivial ways depending on system timing. The read/write order should be exactly the same as in the following sample: <pre> Starting w/0 w/0 writing Starting r/1 Starting w/1 Starting w/2 Starting r/2 Starting r/3 w/0 done Stopping w/0 r/1 reading r/2 reading r/3 reading r/1 done Stopping r/1 r/2 done r/3 done Stopping r/2 Stopping r/3 w/1 writing w/1 done Stopping w/1 w/2 writing w/2 done Stopping w/2 </pre>
   */
  public static class Test
  {
    Resource resource = new Resource();

    /**
     * The Resource class simulates a simple locked resource. The read operation simply pauses for .1 seconds. The write operation (which is typically higher overhead) pauses for .5 seconds. Note that the use of <code>try...finally</code> is not critical in the current test, but it's good style to always release the lock in a <code>finally</code> block in real code.
     */
    static class Resource
    {
      ReaderWriter lock = new ReaderWriter();

      public void read( String reader )
      {
        try
        {
          lock.requestRead();
          System.out.println( "\t\t" + reader + " reading" );
          try
          {
            Thread.sleep( 100 );
          }
          catch( InterruptedException e )
          {}
          System.out.println( "\t\t" + reader + " done" );
        }
        catch( InterruptedException exception )
        {
          System.out.println( "Unexpected interrupt" );
        }
        finally
        {
          lock.readAccomplished();
        }
      }

      public void write( String writer )
      {
        try
        {
          lock.requestWrite();
          System.out.println( "\t\t" + writer + " writing" );
          try
          {
            Thread.sleep( 500 );
          }
          catch( InterruptedException e )
          {}
          System.out.println( "\t\t" + writer + " done" );
        }
        catch( InterruptedException exception )
        {
          System.out.println( "Unexpected interrupt" );
        }
        finally
        {
          lock.writeAccomplished();
        }
      }

      public boolean readIfPossible()
      {
        if( lock.requestImmediateRead() )
        {
          // in the real world, you'd do the read here
          lock.readAccomplished();
          return true;
        }
        return false;
      }

      public boolean writeIfPossible()
      {
        if( lock.requestImmediateWrite() )
        {
          // in the real world, you'd do the write here
          lock.writeAccomplished();
          return true;
        }
        return false;
      }
    }

    /**
     * A simple reader thread. Just reads from the resource, passing
     * it a unique string id.
     */
    class Reader
      extends Thread
    {
      private String name;
      Reader( String name )
      {
        this.name = name;
      }

      public void run()
      {
        System.out.println( "Starting " + name );
        resource.read( name );
        System.out.println( "Stopping " + name );
      }
    }

    /**
     * A simple writer thread. Just writes to the resource, passing
     * it a unique string id.
     */
    class Writer
      extends Thread
    {
      private String name;
      Writer( String name )
      {
        this.name = name;
      }

      public void run()
      {
        System.out.println( "Starting " + name );
        resource.write( name );
        System.out.println( "Stopping " + name );
      }
    }

    /**
     * Test by creating several readers and writers. The initial
     * write operation (w/0) should complete before the first read (r/1)
     * runs. Since readers have priority, r/2 and r/3 should run before
     * w/1, and r/1, r/2, and r/3 should all run in parallel.
     * When all three reads complete, w/1 and w/2 should execute
     * sequentially in that order.
     */

    public Test()
    {
      if( !resource.readIfPossible() )
        System.out.println( "Immediate read request failed" );
      if( !resource.writeIfPossible() )
        System.out.println( "Immediate write request failed" );

      new Writer( "w/0" ).start();
      new Reader( "r/1" ).start();
      new Writer( "w/1" ).start();
      new Writer( "w/2" ).start();
      new Reader( "r/2" ).start();
      new Reader( "r/3" ).start();
    }

    static public void main( String[] args )
    {
      Test t = new Test();
    }
  }
}
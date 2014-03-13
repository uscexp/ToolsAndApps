package haui.asynch;

import haui.asynch.LockManager;
import haui.asynch.Semaphore;
import java.util.*;

/**
 * A counting-semaphore. <table border=1 cellspacing=0 cellpadding=5><tr><td><font size=-1><i> <center>(c) 2000, Allen I. Holub.</center> <p> This code may not be distributed by yourself except in binary form, incorporated into a java .class file. You may use this code freely for personal purposes, but you may not incorporate it into any commercial product without the express written permission of Allen I. Holub. </font></i></td></tr></table>
 */

public final class CountingSemaphore
  implements Semaphore
{
  private int m_iAvailableSlots; // Currently available slots.
  private int m_iMaximumSlots; // Maximum   available slots.
  private Runnable m_runableNotifyOnEmpty;
  private boolean m_blEnforceOwnership;

  /*****************************************************************
   * The "owners" list keeps track of the number of slots
   * allocated by a given thread. It's used only if enforceOwnership
   * is true. The class keeps a hash table keyed by Thread object,
   * where the associated value number of slots allocated
   * by that thread. If a given thread isn't in the table, then it
   * has no slots allocated to it.
   */

  private final class OwnerList
  {
    private Map m_mapOwners = new HashMap();

    /** HashMap objects must contain Object values, not raw ints,
     *  so wrap the int in a class so that it can go into the table.
     */
    private class Count
    {
      public int count = 0;
    }

    /** Increment the slot count associated with the current thread
     */

    public void addSlotForCurrentThread()
    {
      Thread requester = Thread.currentThread();
      Count current = ( Count )m_mapOwners.get( requester );

      if( current == null ) // thread hasn't allocated any slots
        m_mapOwners.put( requester, current = new Count() );

      ++current.count;
    }

    /** Reduce the slot count associated with the current thread by
     *  numberOfSlots and throw an exception if the count goes
     *  negative.
     */

    public void removeSlotsForCurrentThread( int numberOfSlots )
      throws Semaphore.Ownership
    {
      Thread requester = Thread.currentThread();
      Count current = ( Count )m_mapOwners.get( requester );

      if( current == null ) // all slots associated with thread
      { // have been freed or thread never
        // had any slots to begin with.
        throw new Semaphore.Ownership();
      }

      if( ( current.count -= numberOfSlots ) == 0 )
        m_mapOwners.remove( requester );

      if( current.count < 0 ) // Too many slots were freed.
      {
        current.count += numberOfSlots;
        throw new Semaphore.Ownership();
      }
    }
  }

  OwnerList m_olistOwners = new OwnerList();

  /*****************************************************************
   *	Create a counting semaphore with the specified initial and
   *	maximum counts. The <code>release()</code> method,
   *	which increments the count, is not
   *  permitted to increment it past the maximum. If the initialCount
   *  is larger than the maximumSlots, it is silently truncated.
   *
   *	@param initialCount	 The number of elements initially in the
   *							 pool
   *	@param maximumSlots	 The maximum number of elements in the
   *							 pool.
   *	@param notifyOnEmpty	 The <code>run()</code> method of this
   *							 object when the count goes to zero.
   *							 You can use this facility to implement
   *							 pools that grow when all resources are
   *							 in use. (See {@link #increaseMaximumSlots()})
   *  @param enforceOwnership If true, then a given thread can release
   *							 only the number of slots that it has
   *							 previously acquired. One thread cannot
   *							 release a slot acquired by another
   *							 thread.
   *  @see release
   */

  public CountingSemaphore( int initialCount, int maximumSlots, boolean enforceOwnership, Runnable notifyOnEmpty )
  {
    this.m_runableNotifyOnEmpty = notifyOnEmpty;
    this.m_iMaximumSlots = maximumSlots;
    this.m_blEnforceOwnership = enforceOwnership;
    this.m_iAvailableSlots = ( initialCount > maximumSlots )
                             ? maximumSlots : initialCount;
  }

  /*****************************************************************
   *	Create a counting semaphore with a maximum count of
   *  Integer.MAX_VALUE. Strict ownership is enforced.
   */
  public CountingSemaphore( int initialCount )
  {
    this( initialCount, Integer.MAX_VALUE, true, null );
  }

  /*****************************************************************
   *	Required override of Semaphore.id(). Don't call this function.
   *  @see LockManager
   */

  public int id()
  {
    return m_iId;
  }

  private final int m_iId = LockManager.newId();

  /*****************************************************************
   *	Acquire the semaphore, decrementing the count of available slots.
   *	Block if the count goes to zero.
   *	<p>
   *	If this call acquires the last available slot, the <code>run()</code>
   *	method of the <code>Runnable</code> object passed to the
   *	constructor is called. You can use this method to increase the
   *	pool size on an as-needed basis.
   *	(See ({@link #increaseMaximumSlots()}.)
   *
   *  @throws InterruptedException if interrupted while waiting
   *			for the semaphore.
   *  @return false if timeout is 0 and we didn't get the slot.
   *			Otherwise, return true;
   */
  public synchronized boolean acquire( long timeout )
    throws InterruptedException, Semaphore.TimedOut
  {
    if( timeout == 0 && m_iAvailableSlots <= 0 )
      return false;

    long expiration = System.currentTimeMillis() + timeout;
    while( m_iAvailableSlots <= 0 )
    {
      long timeRemaining = expiration - System.currentTimeMillis();
      if( timeRemaining <= 0 )
        throw new Semaphore.TimedOut( "Timed out waiting to acquire Condition Variable" );

      wait( timeRemaining ); //#waiting
    }

    if( m_blEnforceOwnership )
      m_olistOwners.addSlotForCurrentThread();

    if( --m_iAvailableSlots == 0 && m_runableNotifyOnEmpty != null )
      m_runableNotifyOnEmpty.run();

    return true;
  }

  /*****************************************************************
   * Acquire the specified number of slots. If you need to acquire
   *		  multiple slots and some other semaphore as well, use
   *		  <code>LockManger.acquireMultiple()</code>, and put multiple references
   *		  to a single <code>CountingSemaphore</code> in the array
   *		  passed to <code>acquireMultiple()</code>.
   * @param timeout maximum time (milliseconds) to wait for any
   *		  <i>one</i> of the slots. The total time to wait
   *		  is (<i>timeout * numberOfSlots</i>).
   * @throw Semaphore.TimedOut if a timeout is encountered while
   *		  waiting for any of the slots.
   */

  public synchronized boolean acquireMultipleSlots( int slots, long timeout )
    throws InterruptedException, Semaphore.TimedOut
  {
    if( timeout == 0 )
    {
      if( m_iAvailableSlots < slots )
        return false;

      if( ( m_iAvailableSlots -= slots ) <= 0 && m_runableNotifyOnEmpty != null )
        m_runableNotifyOnEmpty.run();
    }
    else
    {
      while( --slots >= 0 )
        acquire( timeout );
    }

    return true;
  }

  /*****************************************************************
   *	Increase the pool size by <code>thisMany</code> slots.
   *	If the available-slots count is zero, then waiting threads
   *	are released (up to the maximum specified by
   *	the new size). That is, this call modifies the
   *	maximum-available-slots count and then effectively performs a
   *	<code>release(thisMany)</code> operation.
   *
   *	@param thisMany	Number of slots to add to the pool. An
   *						<code>IllegalArgumentException</code> is
   *						thrown if this number is negative.
   */

  public synchronized void increaseMaximumSlotsBy( int thisMany )
  {
    if( thisMany < 0 )
      throw new IllegalArgumentException( "Negative Argument" );

    m_iMaximumSlots += thisMany;
    release( thisMany );
  }

  /*****************************************************************
   *	Return the current pool size (the maximum count), as passed into
   *	the constructor or as modified by {@link #increaseMaximumSlots()}.
   */

  public synchronized int maximumSlots()
  {
    return m_iMaximumSlots;
  }

  // The foregoing method must be synchronized in order to
  // gurantee visiblity of maximumSlots in an SMP environment.
  // Simple volatility isn't sufficient.

  /*****************************************************************
   *	Release the semaphore and increment the count.
   *  This one is the generic release required by the Semaphore
   *  interface, so all it can do is throw an exception if
   *  there's an error.
   *	@throws CountingSemaphore.TooManyReleases (a RuntimeException)
   * 		if you try to release a semaphore whose count is already
   *		at the maximum value.
   */
  public synchronized void release()
  {
    release( 1 );
  }

  /*****************************************************************
   *	Release "increment" slots in the semaphore all at once.
   *	@param <b>increment</b> The amount to increment the count.
   *		If this	value is zero, the current count is returned and
   *		no threads are released. An {@link IllegalArgumentException}
   *		is thrown if <code>increment</code> is negative.
   *	@throws CountingSemaphore.TooManyReleases (a RuntimeException)
   * 		if the current value + count is greater than the maximum.
   *		The semaphore will not have been modified in this case.
   *	@throws Semaphore.Ownership (a RuntimeException) if a given
   *		thread tries to free up more slots than it has acquired
   *		(and enforceOwnership was specified in the constructor).
   *	@return the value of the count after the increment is added.
   */
  public synchronized int release( int increment )
  {
    if( increment < 0 )
      throw new IllegalArgumentException( "Negative Argument" );

    if( increment > 0 )
    {
      int originalCount = m_iAvailableSlots;
      int newCount = m_iAvailableSlots + increment;

      if( m_blEnforceOwnership )
        m_olistOwners.removeSlotsForCurrentThread( increment );

      if( newCount > m_iMaximumSlots )
      {
        throw new TooManyReleases();
      }

      m_iAvailableSlots = newCount;
      if( originalCount == 0 )
        notifyAll();
    }
    return m_iAvailableSlots;
  }

  /** Thrown if you try to release more than the maximum number
   * of slots.
   */
  public static final class TooManyReleases
    extends RuntimeException
  {
    private TooManyReleases()
    {
      super( "Released semaphore that was at capacity" );
    }
  }
}
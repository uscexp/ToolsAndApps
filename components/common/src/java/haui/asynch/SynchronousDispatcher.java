package haui.asynch;

import java.util.*;
import haui.asynch.Alarm;

/***********************************************************************
 *	<p>A synchronous notification dispatcher, executes a sequence of
 *  operations sequentially. Allows two sets of linked operations to
 *  be interspersed and effectively executed in paralelly, but without
 *	using multiple threads for this purpose.
 *
 *  <p>This class is built on the JDK 1.2x LinkedList class, which must
 *	be present in the system.
 *
 * <br>
 */

public class SynchronousDispatcher
{
	private LinkedList m_llistEvents = new LinkedList(); // Runnable objects

	/** Add a new handler to the <i>end</i> of the current list of
	 *  subscribers.
	 */

	public synchronized void addHandler( Runnable handler )
	{
    m_llistEvents.add( handler );
	}

	/** Add several listeners to the dispatcher, distributing them
	 *	as evenly as possible with respect to the current list.
	 */

	public synchronized void addHandler( Runnable[] handlers )
	{
		if( m_llistEvents.size() == 0 )
		{
      for( int i=0; i < handlers.length; )
				m_llistEvents.add( handlers[i++] );
		}
		else
		{
			Object[] larger  = m_llistEvents.toArray();
	       	Object[] smaller = handlers;

			if( larger.length < smaller.length )  // swap them
      {
        Object[] tmp = larger;
				larger  = smaller;
				smaller = tmp;
			}

			int distribution = larger.length / smaller.length;

			LinkedList newList = new LinkedList();

			int largeSource = 0;
			int smallSource = 0;

			// Could use the iterator's add() method instead of
			// building an array, but the current implementation
			// will work even for data structures whose iterators
			// don't support add().

			while( smallSource < smaller.length )
      {
        for( int skip = 0; skip < distribution; ++skip )
					newList.add( larger[largeSource++] );
        newList.add( smaller[smallSource++] );
			}

			m_llistEvents = newList;
		}
	}

	/*******************************************************************
	 * Remove all handlers from the current dispatcher.
	 */

	public synchronized void removeAllHandlers()
	{
    m_llistEvents.clear();
	}

	/**
	 *	Dispatch the actions "iterations" times. Use -1 for "forever."
	 *  This function is not synchronized so that the list of events
	 *  can be modified while the dispather is running. It makes a clone
	 *	of the event list and then executes from the clone on each
	 *	iteration through the list of subscribers. Events added to
	 *	the list will be executed starting with the next iteration.
	 */

	public void dispatch( int iterations )
	{
		// Dispatch operations. A simple copy-and-dispatch-from-copy
		// strategy is used, here. Eventually, I'll replace this code
		// with a <code>Multicaster</code>.


		if( m_llistEvents.size() > 0 )
			while( iterations==-1 || iterations-- > 0 )
			{
				Object[] snapshot;
				synchronized( this )				//#snapshot
				{
          snapshot = m_llistEvents.toArray();
				}

				for( int i = 0; i < snapshot.length; ++i )
				{
          ((Runnable)snapshot[i]).run();
					Thread.yield();
				}
			}
	}

	/** Dispatch actions "iterations" number of times, with an action
	 *	dispatched every "interval" milliseconds. Note that the
	 *  last action executed takes up the entire time slot, even
	 *  if the run() function itself doesn't take "interval"
	 *	milliseconds to execute. Also note that the timing will be
	 *  irregular if any run() method executes in more than "interval"
	 *	milliseconds.
	 *
	 *  If you want a time interval between iterations, but not
	 *  between the operations performed in a single iteration, just
	 *  insert a Runnable action that sleeps for a fixed number of
	 *	milliseconds.
	 *
	 * @param iterations`	number of times to loop throug the actions
	 *						exectuting them. Use -1 to mean "forever."
	 * #param interval		An action is exectuted every "interval"
	 *						milliseconds.
	 */

	public void meteredDispatch( int iterations, int interval )
	{
		Alarm timer = new Alarm(interval, Alarm.MULTISHOT, false);
		timer.start();

		while( iterations==-1 || --iterations >= 0 )
		{
			Object[] snapshot;
			synchronized( this )
			{
        snapshot = m_llistEvents.toArray();
			}

			for( int i = 0; i < snapshot.length; ++i )
			{
        ((Runnable)snapshot[i]).run();
				timer.await();
				timer.start();
			}
		}

		timer.stop();
	}

	static public final class Test
	{
		// Execute the test with:
		//	java "haui.asynch.SynchronousDispatcher\$Test"
		//

		public static void main( String[] args )
		{
			SynchronousDispatcher dispatcher = new SynchronousDispatcher();

			dispatcher.addHandler(
				new Runnable()
				{
          public void run()
					{
            System.out.print("hello");
					}
				}
			);

			dispatcher.addHandler(
				new Runnable()
				{
          public void run()
					{
            System.out.println(" world");
					}
				}
			);

			dispatcher.dispatch( 1 );
			dispatcher.meteredDispatch( 2, 1000 );

			//------------------------------------------------
			// Test two tasks, passed to the dispatcher as arrays
			// of chunks. Should print:
			//			Hello (Bonjour) world (monde)

			Runnable[] firstTask =
			{ 	new Runnable()
				{
          public void run()
          { System.out.print("Hello"); }
        },
				new Runnable()
				{
          public void run()
          { System.out.print(" World");}
        }
			};

			Runnable[] secondTask =
			{
        new Runnable()
				{
          public void run()
          { System.out.print(" Bonjour");}
        },
				new Runnable()
				{
          public void run()
          { System.out.print(" Monde\n");}
        }
			};

			dispatcher = new SynchronousDispatcher();
			dispatcher.addHandler( firstTask  );
			dispatcher.addHandler( secondTask );
			dispatcher.dispatch( 1 );
		}
	}
}

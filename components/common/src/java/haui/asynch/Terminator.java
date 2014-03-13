package haui.asynch;

/**
 *  Objects of the <code>Terminator</code> class terminate
 *	a thread by sending it an
 *  <code>interrupt()<code> message. One use is to terminate a
 *	wait operation in such a way that you can detect
 *	whether or not the wait timed out:
 *	<pre>
 *	new Thread()
 *	{	public void run()
 *		{	try
 *			{ 	new Terminator( this, 1000 );
 *				synchronized(this){ wait(); }
 *				System.out.println("Notified");
 *			}
 *			catch( InterruptedException e )
 *			{	System.out.println("Timed out");
 *			}
 *		}
 *	}.start();
 *	</pre>
 *	Note that all you have to do is create a new
 *	Terminator. You do not have to keep a reference
 *	to it.
 * <br>
 */

public final class Terminator
  extends Thread
{
  private final Thread m_threadVictim;
  private final long m_lTimeout;

  /** Creates an object that terminates the
   *  <code>victim</code> thread by sending it an
   *	interrupt message after <code>timeout</code>
   *  milliseconds have elapsed.
   */

  public Terminator( Thread victim, long timeout )
  {
    this.m_threadVictim = victim;
    this.m_lTimeout = timeout;
    setDaemon( true );
    setPriority( getThreadGroup().getMaxPriority() );
    start();
  }

  /** Do not call.
   */
  public void run()
  {
    try
    {
      sleep( m_lTimeout );
      m_threadVictim.interrupt();
    }
    catch( InterruptedException e )
    { /*ignore*/}
  }

  private static final class Test
  {
    public static void main( String[] args )
    {
      new Thread()
      {
        public void run()
        {
          try
          {
            new Terminator( this, 1000 );
            synchronized( this )
            {
              wait();
            }
            System.out.println( "Notified" );
          }
          catch( InterruptedException e )
          {
            System.out.println( "Timed out" );
          }
        }
      }
      .start();
    }
  }
}
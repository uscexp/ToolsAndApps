/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 *
 * JvFTP was developed by Bea Petrovicova <beapetrovicova@yahoo.com>.
 * The sources was donated to sourceforge.net under the terms
 * of GNU Lesser General Public License (LGPL). Redistribution of any
 * part of JvFTP or any derivative works must include this notice.
 */
package cz.dhl.ftp;

import cz.dhl.ui.CoConsole;
import java.io.PrintStream;

/**
 * Maintains FTP client settings & context.
 * @version  0.72 08/10/2003
 * @author  Bea Petrovicova <beapetrovicova@yahoo.com>
 * @see  Ftp
 */
public class FtpContext
  extends FtpSetting
{
  private PrintStream m_ps = System.out;

  private String textfilter[] =
                                {".TXT", ".CGI", ".SH", ".KSH", ".CSH",
                                ".HTM", ".HTML", ".SHTML", ".CSS", ".JS", ".PL", ".PHP",
                                ".H", ".C", ".HPP", ".CPP", ".JAVA",
                                ".SQL", ".4GL", ".BAT", ".SH", ".AWK"};

  private CoConsole console = new CoConsole()
  {
    public void print( String message )
    {
      m_ps.println( message );
    }
  };

  FtpContext()
  {
  }

  public void setOutputStream( PrintStream ps)
  {
    m_ps = ps;
  }

  /** Sets array of strings representing text-file extensions.
   * @param textfilter must be array of uppercase strings with a leading '.' sign;
   * example: { ".TXT", ".HTM", ".HTML", etc ... };
     default settings is quite flexible */
  public void setTextFilter( String textfilter[] )
  {
    this.textfilter = textfilter;
  }

  /** Sets array of strings representing text-file extensions.
   * @return array of uppercase strings with a leading '.' sign;
   * example: { ".TXT", ".HTM", ".HTML", etc ... } */
  public String[] getTextFilter()
  {
    return textfilter;
  }

  /**
   * Sets output console.
   * @see  cz.dhl.ui.CoConsole
   * @uml.property  name="console"
   */
  synchronized public void setConsole( CoConsole console )
  {
    this.console = console;
  }

  /**
   * Gets output console.
   * @see  cz.dhl.ui.CoConsole
   * @uml.property  name="console"
   */
  synchronized public CoConsole getConsole()
  {
    return console;
  }

  /** Prints message line to output console. */
  public synchronized void printlog( String message )
  {
    if( console != null )
      console.print( message );
  }

  /** Prints object to standard output. */
  public void printerr( Exception exception )
  {
    m_ps.println( "Thread: " + Thread.currentThread().getName() );
    m_ps.println( "Exception:" );
    exception.printStackTrace( m_ps);
  }
}
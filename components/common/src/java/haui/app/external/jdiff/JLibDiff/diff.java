package haui.app.external.jdiff.JLibDiff;

import haui.io.FileInterface.FileInterface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * The <code>diff</code> class  compares two files. it compares also two
 * BufferedReaders  or two strings.
 * after the comparison the vector will represents a list of hunk
 * corresponding with  blocks of difference.
 * <p>
 * to generate a file of difference, one can instanciate as follows the class
 * diff:
 * <p><blockquote><pre>
 *     diff d = new diff(file1,file2);
 * </pre></blockquote><p>
 * which is equivalent to:
 * <p><blockquote><pre>
 *     diff d = new diff();
 *     d.diffFile(file1,file2);
 * </pre></blockquote><p>
 * To compare two BufferedReaders or two String we have to instanciate
 * as follows:
 * <p><blockquote><pre>
 *     diff d = new diff();
 *     d.diffBuffer(BufferedReader1,BufferedReader2);
 * </pre></blockquote><p>
 * or:
 * <p><blockquote><pre>
 *     diff d = new diff();
 *     d.diffString(String1,String2);
 * </pre></blockquote><p>
 * The class <code>diff</code> includes methods for examining, printing
 * or saveing  blocks of difference: (Hunks).
 * Here are some more examples of how <code>diff</code> can be used:
 * <p><blockquote><pre>
 *	diff d=new diff(args[0],args[1]);
 *	d.print();
 *     d.save("diff.txt");
 * </pre></blockquote><p>
 * Example using BufferedReader and ED_format:
 * <p><blockquote><pre>
 *	BufferedReader in=new BufferedReader(new FileReader(args[0]));
 *	BufferedReader inn=new BufferedReader(new FileReader(args[1]));
 *	diff d = new diff();
 *	d.diffBuffer(in,inn);
 *	d.print_ED();
 *	d.save_ED("diff.txt");
 * </pre></blockquote><p>
 * To go throw the list of Hunks we can choose between an Enumeration
 * or a loop by spesifyng  index in the vector to get at each time
 * the corresponding Hunk.
 *
 * <p><blockquote><pre>
 *     Vector v=d.getHunk();
 *     for(Enumeration e=v.element();e.hasMoreElements(); )
 *       {
 *         System.out.print(((Hunk)e.nextElement()).convert());
 *       }
 * </pre></blockquote><p>
 *
 * or:
 * <p><blockquote><pre>
 *     diff d = new diff(file1,file2);
 *     for(int i=0; i<d.numberOfHunk(); i++){
 *        Object k=d.hunkAt(i);
 *        if(k instanceof Hunk)
 *          System.out.print(k.convert());
 *      }
 * </pre></blockquote><p>
 * @see diff.diff#getHunk()
 * @see diff.diff#hunkAt()
 * @see diff.diff#numberOfHunk()
 * @see diff.diff#print()
 * @see diff.diff#print_ED()
 * @see diff.diff#print_RCS()
 * @see diff.diff#save()
 * @see diff.diff#save_ED()
 * @see diff.diff#save_RCS()
 */

public class diff
  implements HunkVisitable
{

  Vector v = new Vector();
  private int MAXLINES;
  private int ORIGIN = MAXLINES;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  /**
   * Allocates a new <code>diff</code> containing no Hunks.
   */
  public diff()
  {
  }

  /**
   * Allocates a new <code>diff</code> which contains Hunks corresponding
   * to the difference between the two files passed in arguments.
   *
   * @param s1  first file to compare.
   * @param s2  second file to compare.
   */
  public diff( FileInterface serverFile, FileInterface localFile)
    throws IOException
  {
    diffFile( serverFile, localFile );
  }

  /**
   * Returns a vector containing Hunks.
   */
  public Vector getHunk()
  {
    return v;
  }

  /**
   * Returns the number of hunks in the vector.
   */
  public int numberOfHunk()
  {
    return v.size();
  }

  /**
   * Return the hunk at the specified index.
   *
   * @param i  index of the hunk that will be returned.
   */
  public Hunk hunkAt( int i )
  {
    return( Hunk )v.elementAt( i );
  }

  /**
   * Accept a visitor in order to visit the collection of hunks.
   *
   * @param visitor  the HunkVisitor.
   */
  public void accept( HunkVisitor visitor )
  {
    for( Enumeration e = v.elements(); e.hasMoreElements(); )
    {
      Hunk h = ( Hunk )e.nextElement();
      h.accept( visitor );
    }
  }

  /**
   * Compares two files and updates the vector of Hunks.
   *
   * @param s1  first file to compare.
   * @param s2  second file to compare.
   */
  protected void diffFile( FileInterface serverFile, FileInterface localFile )
    throws IOException
  {

    int f1Count, f2Count;
    f1Count = getFileLineCount( serverFile );
    f2Count = getFileLineCount( localFile );
    ORIGIN = MAXLINES = ( f1Count > f2Count ) ? f1Count : f2Count;

    BufferedReader in = new BufferedReader( new InputStreamReader( serverFile.getBufferedInputStream()) );
    BufferedReader inn = new BufferedReader( new InputStreamReader( localFile.getBufferedInputStream()) );
    diffBuffer( in, inn );
    in.close();
    inn.close();
  }

  /**
   * Compares two BufferedReaders and updates the vector of Hunks.
   *
   * @param in  first BufferedReader to compare.
   * @param inn second BufferedReader to compare.
   */
  private void diffBuffer( BufferedReader in, BufferedReader inn )
    throws IOException
  {

    int max_d = 2 * MAXLINES, m = 0, n = 0, lower, upper, d = 1, k, row, col;

    int last_d[] = new int[2 * MAXLINES + 1]; //row containing last d
    //pour chaque diag
    edit script[] = new edit[2 * MAXLINES + 1]; //correspond a l'edit script */
    String str = "";
    String A[] = new String[MAXLINES], B[] = new String[MAXLINES];
    while( ( str = in.readLine() ) != null )
    {
      A[m] = str;
      m++;
    }
    while( ( str = inn.readLine() ) != null )
    {
      B[n] = str;
      n++;
    }

    for( row = 0; row < m && row < n && A[row].equals( B[row] ); row++ )
      ;
    last_d[ORIGIN] = row;
    script[ORIGIN] = null;
    if( row == m )
      lower = ORIGIN + 1;
    else
      lower = ORIGIN - 1;
    if( row == n )
      upper = ORIGIN - 1;
    else
      upper = ORIGIN + 1;
    if( lower > upper )
      return;
    else
    {
      for( d = 1; d <= max_d; d++ )
      { //for each value of edit distance
        for( k = lower; k <= upper; k += 2 ) //for each relevant diagonal
        {
          edit e = new edit();
          if( e == null )
          {
            System.out.println( ";;;;exceed" + d );
            return;
          }
          if( k == ORIGIN - d || k != ORIGIN + d && last_d[k + 1] >= last_d[k - 1] )
          {
            row = last_d[k + 1] + 1;
            e.setnext( script[k + 1] );
            e.setop( DELETE );
          }
          else
          {
            row = last_d[k - 1];
            e.setnext( script[k - 1] );
            e.setop( INSERT );
          }
          e.setline1( row );
          col = row + k - ORIGIN;
          e.setline2( col );
          script[k] = e;
          while( row < m && col < n && A[row].equals( B[col] ) )
          {
            row++;
            col++;
          }
          last_d[k] = row;
          if( row == m && col == n )
          {
            v = getHunk( script[k], A, B );
            return;
          }
          if( row == m )
            lower = k + 2;
          if( col == n )
            upper = k - 2;
        }

        lower--;
        upper++;

      }
    }
  }

  /**
   * Compares two strings and updates the vector of Hunks.
   *
   * @param s1   first string to compare.
   * @param s2   second string to compare.
   */
  protected void diffString( String s1, String s2 )
    throws IOException
  {

    int f1Count, f2Count;
    f1Count = getStringLineCount( s1 );
    f2Count = getStringLineCount( s2 );
    ORIGIN = MAXLINES = ( f1Count > f2Count ) ? f1Count : f2Count;

    // should modify MAXLINES here, but how to compute its value.

    int max_d = 2 * MAXLINES,
                      m = 0, n = 0,
                                 lower, upper,
                                 d = 1, k, row, col;

    int last_d[] = new int[2 * MAXLINES + 1];
    edit script[] = new edit[2 * MAXLINES + 1];
    char A[] = new char[MAXLINES], B[] = new char[MAXLINES];

    s1.getChars( 0, s1.length(), A, 0 );
    s2.getChars( 0, s2.length(), B, 0 );
    m = s1.length();
    n = s2.length();
    for( row = 0; row < m && row < n && A[row] == B[row]; row++ )
      ;
    last_d[ORIGIN] = row;
    script[ORIGIN] = null;
    if( row == m )
      lower = ORIGIN + 1;
    else
      lower = ORIGIN - 1;
    if( row == n )
      upper = ORIGIN - 1;
    else
      upper = ORIGIN + 1;
    if( lower > upper )
      return;
    else
    {
      for( d = 1; d <= max_d; d++ )
      {
        for( k = lower; k <= upper; k += 2 )
        {
          edit e = new edit();
          if( e == null )
          {
            System.out.println( ";;;;exceed" + d );
            return;
          }
          if( k == ORIGIN - d || k != ORIGIN + d && last_d[k + 1] >= last_d[k - 1] )
          {
            row = last_d[k + 1] + 1;
            e.setnext( script[k + 1] );
            e.setop( DELETE );
          }
          else
          {
            row = last_d[k - 1];
            e.setnext( script[k - 1] );
            e.setop( INSERT );
          }
          e.setline1( row );
          col = row + k - ORIGIN;
          e.setline2( col );
          script[k] = e;
          while( row < m && col < n && A[row] == B[col] )
          {
            row++;
            col++;
          }
          last_d[k] = row;
          if( row == m && col == n )
          {
            v = getHunk( script[k], A, B );
            return;
          }
          if( row == m )
            lower = k + 2;
          if( col == n )
            upper = k - 2;
        }

        lower--;
        upper++;
      }
      System.out.println( ";;;;exceed" + d );
    }
  }

  /*
   * Generates Hunks and returns them in a vector.
   */
  private static Vector getHunk( edit start, String A[], String B[] )
    throws IOException
  {
    Vector v = new Vector();
    boolean change;
    int i;
    edit ep = new edit();
    edit behind = new edit();
    edit ahead = new edit();
    edit a = new edit();
    edit b = new edit();
    ahead = start;
    ahead = start;
    ep = null;
    while( ahead != null )
    {
      behind = ep;
      ep = ahead;
      ahead = ahead.next;
      ep.next = behind;
    }

    while( ep != null )
    {
      b = ep;
      if( ep.op == INSERT )
      {
        a = ep;
        behind = ep.next;
        while( behind != null && behind.op == INSERT && ep.line1 == behind.line1 )
        {
          a = behind;
          behind = behind.next;
        }
        HunkAdd add = new HunkAdd();
        add.ld1 = ep.line1;
        add.ld2 = ep.line2;
        add.lf2 = a.line2;
        do
        {
          add.b.addElement( B[ep.line2 - 1] + "\n" );
          ep = ep.next;
        }
        while( ep != null && ep.op == INSERT && ep.line1 == b.line1 );
        add.next = null;
        if( v.size() != 0 )
          ( ( Hunk )v.lastElement() ).next = add;
        v.addElement( add );
      }
      else
      {
        do
        {
          a = b;
          b = b.next;
        }
        while( b != null && b.op == DELETE && b.line1 == a.line1 + 1 );
        change = ( b != null && b.op == INSERT && b.line1 == a.line1 );
        if( change )
        {
          HunkChange cha = new HunkChange();
          cha.ld1 = ep.line1;
          cha.lf1 = a.line1;
          i = 0;
          behind = b;
          while( behind != null && behind.op == INSERT && behind.line1 == b.line1 )
          {
            i++;
            behind = behind.next;
          }
          cha.ld2 = b.line2;
          cha.lf2 = i - 1 + b.line2;
          do
          {
            cha.a.addElement( A[ep.line1 - 1] + "\n" );
            ep = ep.next;
          }
          while( ep != b );
          if( !change )
            continue;
          do
          {
            cha.b.addElement( B[ep.line2 - 1] + "\n" );
            ep = ep.next;
          }
          while( ep != null && ep.op == INSERT && ep.line1 == b.line1 );
          cha.next = null;
          if( v.size() != 0 )
            ( ( Hunk )v.lastElement() ).next = cha;
          v.addElement( cha );
        }
        else
        {
          HunkDel del = new HunkDel();
          del.ld1 = ep.line1;
          del.lf1 = a.line1;
          del.ld2 = ep.line2;

          do
          {
            del.a.addElement( A[ep.line1 - 1] + "\n" );
            ep = ep.next;
          }
          while( ep != b );
          del.next = null;
          if( v.size() != 0 )
            ( ( Hunk )v.lastElement() ).next = del;
          v.addElement( del );

        }
      }
    }
    return v;
  }

  /*
   * Generates Hunks and returns them in a vector.
   */
  private static Vector getHunk( edit start, char A[], char B[] )
    throws IOException
  {
    Vector v = new Vector();

    boolean change;
    int i;
    edit ep = new edit();
    edit behind = new edit();
    edit ahead = new edit();
    edit a = new edit();
    edit b = new edit();
    ahead = start;
    ep = null;
    while( ahead != null )
    {
      behind = ep;
      ep = ahead;
      ahead = ahead.next;
      ep.next = behind;
    }
    while( ep != null )
    {
      b = ep;
      if( ep.op == INSERT )
      {
        a = ep;
        behind = ep.next;
        while( behind != null && behind.op == INSERT && ep.line1 == behind.line1 )
        {
          a = behind;
          behind = behind.next;
        }
        HunkAdd add = new HunkAdd();
        add.ld1 = ep.line1;
        add.ld2 = ep.line2;
        add.lf2 = a.line2;
        String s = new String();
        do
        {
          s = s + B[ep.line2 - 1];
          ep = ep.next;
        }
        while( ep != null && ep.op == INSERT && ep.line1 == b.line1 );
        add.b.addElement( s + "\n" );
        v.addElement( add );
      }
      else
      {
        do
        {
          a = b;
          b = b.next;
        }
        while( b != null && b.op == DELETE && b.line1 == a.line1 + 1 );
        change = ( b != null && b.op == INSERT && b.line1 == a.line1 );
        if( change )
        {
          HunkChange cha = new HunkChange();
          cha.ld1 = ep.line1;
          cha.lf1 = a.line1;
          i = 0;
          behind = b;
          while( behind != null && behind.op == INSERT && behind.line1 == b.line1 )
          {
            i++;
            behind = behind.next;
          }
          cha.ld2 = b.line2;
          cha.lf2 = i - 1 + b.line2;
          String s = new String();
          do
          {
            s = s + A[ep.line1 - 1];
            ep = ep.next;
          }
          while( ep != b );
          cha.a.addElement( s + "\n" );
          s = new String();
          do
          {
            s = s + B[ep.line2 - 1];
            ep = ep.next;
          }
          while( ep != null && ep.op == INSERT && ep.line1 == b.line1 );
          cha.b.addElement( s + "\n" );
          v.addElement( cha );
        }
        else
        {
          HunkDel del = new HunkDel();
          del.ld1 = ep.line1;
          del.lf1 = a.line1;
          del.ld2 = ep.line2;
          String s = new String();
          do
          {
            s = s + A[ep.line1 - 1];
            ep = ep.next;
          }
          while( ep != b );
          del.a.addElement( s + "\n" );
          v.addElement( del );
        }

      }
    }
    return v;
  }

  /**
   * Print Hunks with normal format.
   */
  // use only in diff3 (to be deleted)
  public void print()
  {
    for( Enumeration e = v.elements(); e.hasMoreElements(); )
      System.out.print( ( ( Hunk )e.nextElement() ).convert() );
  }

  /**
   * Print Hunks with ED_script format.
   */
//     public void print_ED(){
// 	for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
// 	    System.out.print(((Hunk)e.nextElement()).convert_ED());
//     }

  /**
   * Print Hunks with RCS format.
   */
//     public void print_RCS(){
// 	for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
// 	    System.out.print(((Hunk)e.nextElement()).convert_RCS());
//     }

  /**
   * Save Hunks with normal format at the specified file.
   *
   * @param file   file in which Hunks will be saved.
   */
//     public void save(String file)throws IOException{
// 	PrintWriter out
// 	    = new PrintWriter(new BufferedWriter(new FileWriter(file)));
// 	for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
// 	    out.write(((Hunk)e.nextElement()).convert());
// 	out.flush();
// 	out.close();
//     }

  /**
   * Save Hunks with ED_script format at the specified file.
   *
   * @param file   file in which Hunks will be saved.
   */
//     public void save_ED(String file)throws IOException{
// 	PrintWriter out
// 	    = new PrintWriter(new BufferedWriter(new FileWriter(file)));
// 	for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
// 	    out.write(((Hunk)e.nextElement()).convert_ED());
// 	out.flush();
// 	out.close();
//     }

  /**
   * Save Hunks with RCS format at the specified file.
   *
   * @param file   file in which Hunks will be saved.
   */
//     public void save_RCS(String file)throws IOException{
// 	PrintWriter out
// 	    = new PrintWriter(new BufferedWriter(new FileWriter(file)));
// 	for (Enumeration e = v.elements() ; e.hasMoreElements() ;)
// 	    out.write(((Hunk)e.nextElement()).convert_RCS());
// 	out.flush();
// 	out.close();
//     }

  /**
   * get the number of lines in the given file.
   * @param file filename.
   * @return the number of lines in the file, -1 if the file couldn't be found.
   */
  protected int getFileLineCount( FileInterface file )
  {
    int count = 0;
    BufferedReader fileReader = null;
    try
    {
      fileReader = new BufferedReader( new InputStreamReader( file.getBufferedInputStream()) );
      while( fileReader.readLine() != null )
        count++;
    }
    catch( FileNotFoundException fnfe )
    {
      count = -1;
      fnfe.printStackTrace();
    }
    catch( IOException ex)
    {
      count = -1;
      ex.printStackTrace();
    }
    finally
    {
      try
      {
        fileReader.close();
      }
      catch( IOException ioe )
      {
        ioe.printStackTrace();
      }
    }
    return count;
  }

  /**
   * get the number of lines in the given BufferedReader.
   * @param reader a BufferedReader.
   * @return the number of lines in the BufferedReader, -1 if an Exception occured.
   */
  /*
      protected int getBufferLineCount(BufferedReader reader)
      {
          int count = 0;
          try
          {
            while(reader.readLine() != null)
                count++;
          }
          catch(IOException ioe)
          {
            count = -1;
            ioe.printStackTrace();
          }
          return count;
      }
   */

  /**
   * get the number of lines in the given String.
   * @param aString a String.
   * @return the number of lines in the string.
   */
  protected int getStringLineCount( String aString )
  {
    return( new StringTokenizer( aString, "\r\n" ) ).countTokens();
  }

  /**
   * only a trial method.
   */
  public static void main( String[] args )
  {
    try
    {
//        	long start = System.currentTimeMillis();
//            StringTokenizer x = new StringTokenizer();
//	      	long end = System.currentTimeMillis();
//			System.out.println("line count: "+diff.getStringLineCount("12\n3 4\r\n5 678\r90000"));
      /*
                  while(x.hasMoreTokens())
                  System.out.println(">"+x.nextToken());
//            System.out.println("took: "+(end-start)+" milliSec");
       /*
                   diff d=new diff("1.txt","2.txt");
//            d.print();
                   Vector v = d.getHunk();
                   System.out.println("v.size: "+v.size());
                   for(Enumeration e=v.elements();e.hasMoreElements(); )
                   {
                       Hunk h = (Hunk)e.nextElement();
                       System.out.println("--------------------------------------");
                       System.out.println(h.convert());
                       System.out.println("0 = h.lowLine(): "+h.lowLine(0));
                       System.out.println("0 = h.highLine(): "+h.highLine(0));
                       System.out.println("1 = h.lowLine(): "+h.lowLine(1));
                       System.out.println("1 = h.highLine(): "+h.highLine(1));
                   }
        */
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }
}
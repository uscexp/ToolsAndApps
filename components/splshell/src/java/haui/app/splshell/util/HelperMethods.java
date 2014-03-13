/* *****************************************************************
 * Project: splshell
 * File:    HelperMethods.java
 * 
 * Creation:     30.06.2006 by Andreas Eisenhauer
 * Modification: %date_modified: % %derived_by: %   
 * Version:      %version: %
 *
 * Copyright (C) 2006 Andreas Eisenhauer. All rights reserved! 
 * ****************************************************************/
package haui.app.splshell.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Module:      HelperMethods<br>
 *<p>
 * Description: HelperMethods<br>
 *</p><p>
 * Created:     30.06.2006 by Andreas Eisenhauer
 *</p><p>
 * @history     30.06.2006 by AE: Created.<br>
 *</p><p>
 * @author      <a href="mailto:andreas.eisenhauer@haui.cjb.net">Andreas Eisenhauer</a>
 *</p><p>
 * @version     v0.1, 2006; %version: %<br>
 *</p><p>
 * @since       JDK1.4
 *</p>
 */
public class HelperMethods
{
  
  public static final String EOF = "__EOF__";
  
  public static BufferedReader createBufferedReader( String fileName) throws FileNotFoundException
  {
    return new BufferedReader( new FileReader( fileName));
  }
  
  public static BufferedWriter createBufferedWriter( String fileName, boolean append) throws IOException
  {
    return new BufferedWriter( new FileWriter( fileName, append));
  }
  
  public static String readLine( BufferedReader br) throws IOException
  {
    String str = null;
    str = br.readLine();
    
    if( str == null)
      str = EOF;
    
    return str;
  }
  
  public static String isEOF()
  {
    return EOF;
  }

  public static Character isCharEOF()
  {
    return new Character( (char)-1);
  }

  public static int arrayLength( ArrayList ar)
  {
    return ar.size();
  }
}

/* *****************************************************************
 * Project: common
 * File:    MultiStringTokenizer.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class is an alternative impelementation of StringTokenizer which takes strings (as opposed to characters) as tokenizers. Although, spilt() method exists this class is significantly faster than
 * the regex based split.
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class MultiStringTokenizer
{

  private final boolean returnDelims;
  private final boolean allowEmptyTokens;
  private String orgstring;
  private String delim;
  private String string;

  public MultiStringTokenizer(String s, String delim) {
      this(s, delim, false, false);
  }

  public MultiStringTokenizer(String string, String delim, boolean returnDelims, boolean allowEmptyTokens) {

      if (string == null) {
          throw new IllegalArgumentException("String must not be null");
      }

      if (delim == null) {
          throw new IllegalArgumentException("Delimiter must not be null");
      }

      this.string = string;
      this.orgstring = string;
      this.delim = delim;
      this.returnDelims = returnDelims;
      this.allowEmptyTokens = allowEmptyTokens;
  }

  public int countTokens() {
      return countTokens(orgstring);
  }

  public int countRemainingTokens() {
      return countTokens(string);
  }

  private int countTokens(String s) {
      if (s.equals("")) return 0;
      if (delim.equals("")) return 1;
      int i = 0;
      int length = delim.length();
      for (int n = 0 - length; (n = s.indexOf(delim, n + length)) > -1; i++)
          ;
      return i + 1 + (returnDelims ? i : 0);
  }

  public boolean hasMoreTokens() {
      return !string.equals("");
  }

  public boolean hasNext() {
      return !string.equals("");
  }

  public boolean hasMoreElements() {
      return !string.equals("");
  }

  public Object next() {
      return nextToken();
  }

  public Object nextElement() {
      return nextToken();
  }

  public String nextToken() {
      return nextToken(delim);
  }

  public boolean hasToken(String token) {
      if (delim == null) {
          throw new IllegalArgumentException("Token must not be null");
      }
      // Create a new "inner" object which does not affect the original object
      MultiStringTokenizer str = new MultiStringTokenizer(orgstring, delim, returnDelims, allowEmptyTokens);
      while (str.hasMoreTokens()) {
          if (token.equals(str.nextToken())) return true;
      }
      return false;
  }

  public String nextToken(String delim) {
      if (delim == null) {
          throw new IllegalArgumentException("Delimiter must not be null");
      }
      String token = "";
      if (string.equals("")) throw new NoSuchElementException();
      if (delim.equals("")) {
          token = string;
          string = "";
          return token;
      }
      while (token.equals("")) {
          int index = string.indexOf(delim, 0);
          if (index == -1) {
              token = string;
              string = "";
              break;
          }
          if (returnDelims) {
              token = string.startsWith(delim) ? delim : string.substring(0, index);
              string = string.substring(token.length(), string.length());
          } else {
              token = string.substring(0, index);
              string = string.substring(token.length() + delim.length(), string.length());
          }
          if (allowEmptyTokens) break;
      }
      return token;
  }

  public String getDelimiter() {
      return delim;
  }

  public String getString() {
      return orgstring;
  }

  public boolean isReturnDelims() {
      return returnDelims;
  }

  public boolean isAllowEmptyTokens() {
      return allowEmptyTokens;
  }

  public void reset(String string) {
      if (string == null) {
          throw new IllegalArgumentException("String must not be null");
      }
      this.string = string;
      this.orgstring = string;
  }

  public void reset() {
      string = orgstring;
  }

  public void remove() {
      throw new UnsupportedOperationException("Method remove() in class " + MultiStringTokenizer.class.getName() + " not supported!");
  }

  public void setDelimiter(String delim) {
      if (delim == null) {
          throw new IllegalArgumentException("Delimiter must not be null");
      }
      this.delim = delim;
  }

  public List<String> toList() {
      // Create a new "inner" object which does not affect the original object
      MultiStringTokenizer str = new MultiStringTokenizer(orgstring, delim, returnDelims, allowEmptyTokens);
      // List list = new ArrayList(str.countTokens());
      List<String> list = new ArrayList<String>();
      while (str.hasMoreTokens()) {
          list.add(str.nextToken());
      }
      return list;
  }

  public String[] toArray() {
      List<String> list = toList();
      return list.toArray(new String[list.size()]);
  }
}

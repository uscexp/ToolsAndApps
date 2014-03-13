/* *****************************************************************
 * Project: common
 * File:    StringUtil.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.util;

import haui.exception.AppLogicException;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * StringUtil
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class StringUtil
{

  /**
   * Answers whether a given string is null or empty.
   * 
   * @param string
   * @return boolan
   */
  public static boolean isEmpty(String string)
  {
    return string == null || string.trim().length() == 0;
  }

  /**
   * Answers the number of entries of a specified pattern text with a given input string.
   * 
   * @param string
   * @param patternText
   * @return number of entries
   */
  public static int getNumberOfEntries(String string, String patternText)
  {
    if(isEmpty(string) || isEmpty(patternText))
      return 0;
    return new MultiStringTokenizer(string, patternText).countTokens();
  }

  /**
   * check whether tthe characters of the given string contains only character defined in
   * validCharacter
   * 
   * @param string
   * @param validChars
   * @return the character position in the string which is not valid
   */
  public static int isValid(String string, char[] validChars)
  {
    char[] stringChars = string.toCharArray();
    for(int n = 0; n < stringChars.length; n++)
    {
      char sc = stringChars[n];
      boolean valid = false;
      for(int i = 0; i < validChars.length; i++)
      {
        char vc = validChars[i];
        if(sc == vc)
        {
          valid = true;
          break;
        }
      }
      if(!valid)
      {
        return n;
      }
    }
    return -1;
  }

  /**
   * Answers whether the 2 input strings are the same, ignoring case. Answers true if both are null.
   * 
   * @param aString1
   * @param aString2
   * @return boolean
   */
  public static boolean areEqualIgnoreCase(String aString1, String aString2)
  {
    if(aString1 == null || aString2 == null)
      return aString1 == aString2;
    return aString1.equalsIgnoreCase(aString2);
  }

  /**
   * Converts a collection to a String. Each value of the collection will be set between '.
   * Delimiter is a comma.
   * 
   * @param aCollection
   * @return String
   */
  public static String convertValueCollectionToSQL(Collection<?> aCollection)
  {
    String result = null;
    StringBuffer buffer = new StringBuffer();
    Iterator<?> iterator = aCollection.iterator();
    while(iterator.hasNext())
    {
      buffer.append("'");
      buffer.append(iterator.next().toString());
      buffer.append("',");
    }
    result = buffer.toString();
    if(!AppUtil.isEmpty(result))
    {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  /**
   * Converts the input string by changing the first character to upper case.
   * 
   * @param string
   * @return String
   */
  public static String changeFirstCharToUpperCase(String s)
  {
    if(s == null || s.equals(""))
      return s;
    return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
  }

  /**
   * Converts the input string by changing the first character to lower case.
   * 
   * @param string
   * @return String
   */
  public static String changeFirstCharToLowerCase(String s)
  {
    if(s == null || s.equals(""))
      return s;
    return s.substring(0, 1).toLowerCase() + s.substring(1, s.length());

  }

  /**
   * Changes all first characters of the words in a sentence to UpperCase
   * 
   * @param s
   * @return
   */
  public static String changeAllFirstCharsToUpperCase(String s)
  {
    if(s == null || s.equals(""))
      return s;
    StringTokenizer str = new StringTokenizer(s, " \t\n\r\f", true);
    StringBuffer buffer = new StringBuffer(s.length());
    while(str.hasMoreTokens())
    {
      String token = str.nextToken();
      buffer.append(token.substring(0, 1).toUpperCase()).append(token.substring(1, token.length()));
    }
    return buffer.toString();
  }

  public static String replaceAll(String string, String search, String replace)
  {
    int start = 0;
    int slength = search.length();
    StringBuffer buffer = null;
    for(int pos = 0; (pos = string.indexOf(search, start)) > -1; start = pos + slength)
    {
      if(buffer == null)
      {
        buffer = replace.length() <= slength ? new StringBuffer(string.length()) : new StringBuffer(string.length() + 30);
      }
      buffer.append(string.substring(start, pos)).append(replace);
    }
    return buffer == null ? string : buffer.append(string.substring(start)).toString();
  }

  public static String replaceFirst(String string, String search, String replace)
  {
    int start = 0;
    int slength = search.length();
    StringBuffer buffer = null;
    for(int pos = 0; (pos = string.indexOf(search, start)) > -1;)
    {
      buffer = new StringBuffer();
      buffer.append(string.substring(start, pos)).append(replace);
      start = pos + slength;
      break;
    }
    return buffer == null ? string : buffer.append(string.substring(start)).toString();
  }

  public static int countPattern(String string, String pattern)
  {
    int count = 0;
    for(int n = 0; (n = string.indexOf(pattern, n)) > -1; n += pattern.length())
    {
      count++;
    }
    return count;
  }

  public static String toCSV(Collection<?> collection)
  {
    return StringUtil.toCSV(collection, ",");
  }

  public static String toCSV(Collection<?> collection, String tokenizer)
  {
    if(collection == null)
      return "";
    tokenizer = tokenizer == null ? "" : tokenizer;
    StringBuffer buffer = new StringBuffer();
    for(Iterator<?> it = collection.iterator(); it.hasNext();)
    {
      Object object = it.next();
      buffer.append(object == null ? "null" : object.toString()).append(tokenizer);
    }
    String s = buffer.toString();
    return s.endsWith(tokenizer) ? s.substring(0, s.length() - tokenizer.length()) : s;

  }

  public static String toCSV(Object[] objects)
  {
    return StringUtil.toCSV(objects, ",");
  }

  public static String toCSV(Object[] objects, String tokenizer)
  {
    if(objects == null)
      return "";
    tokenizer = tokenizer == null ? "" : tokenizer;
    StringBuffer buffer = new StringBuffer();
    for(int n = 0; n < objects.length; n++)
    {
      buffer.append(objects[n] == null ? "null" : objects[n].toString()).append(tokenizer);
    }
    String s = buffer.toString();
    return s.endsWith(tokenizer) ? s.substring(0, s.length() - tokenizer.length()) : s;
  }

  /**
   * Trims the string to the max size, if it is longer
   * 
   * @param string
   * @param length
   * @return Returns the string.
   */
  public static String trimToMaxSize(String string, int maxLength)
  {
    if(string == null)
      return null;
    if(maxLength <= 0)
      return "";
    if(maxLength > string.length())
      return string;
    return string.substring(0, maxLength);
  }

  public static String addFillersToSize(String string, int size, boolean append)
  {
    return addFillersToSize(string, " ", size, append);
  }

  public static String addFillersToSize(String string, String filler, int size, boolean append)
  {
    if(string == null)
      return null;
    if(string.length() > size || size < 0)
      return string;
    while(string.length() < size)
    {
      if(append)
      {
        string = string + filler;
      }
      else
      {
        string = filler + string;
      }
    }
    return string;
  }

  /**
   * Removes all charaters that are neither a digit, a letter or a withspace whereas the last can be
   * turned off
   * 
   * @param string
   * @param removeWithspaces
   * @return
   */
  public static String removeSpecialCharacters(String string, boolean removeWithspaces)
  {
    if(string == null)
      return "";
    StringBuffer buffer = new StringBuffer(string.length());
    for(int n = 0; n < string.length(); n++)
    {
      char c = string.charAt(n);
      if(!Character.isLetterOrDigit(c))
      {
        if(removeWithspaces)
          continue;
        if(!Character.isWhitespace(c))
          continue;
      }
      buffer.append(c);
    }
    return buffer.toString();
  }

  public static String removeWhiteSpaces(String string)
  {
    if(string == null)
      return null;
    StringBuffer buffer = new StringBuffer(string.length());
    for(int n = 0; n < string.length(); n++)
    {
      char c = string.charAt(n);
      if(Character.isWhitespace(c))
        continue;
      buffer.append(c);
    }
    return buffer.toString();
  }

  public static String checkTextLength(String text, int length) throws AppLogicException
  {
    if(text == null)
      return null;
    if(text.length() > length)
    {
      throw new AppLogicException("Max " + length + " characters allowed (Used: " + text.length() + ")");
    }
    return text;
  }

  /**
   * Creates a string from an integer value using the specified base and pads it with zeros at the
   * start to fill the supplied length. eg padZeros (5, 1, 10) returns "00001". If the value is
   * negative then the minus sign is included in the length. eg padZeros (5, -1, 10) returns
   * "-0001".
   * 
   * @param length the length of the returned string after padding with zeros
   * @param value the value to be padded with zeros.
   * @param base the base to use for the number when represented as a string.
   */

  public static String padZeros(int length, int value, int base)
  {
    if(value < 0)
      return "-" + padZeros(length - 1, java.lang.Math.abs(value), base);

    String s = Integer.toString(value, base);
    int padCount = length - s.length();
    StringBuffer buffer = new StringBuffer(length);

    for(int i = 0; i < padCount; i++)
      buffer.append('0');

    buffer.append(s);

    return buffer.toString();
  }

  /**
   * Creates a string from an integer value and pads it with zeros at the start to fill the supplied
   * length. eg padZeros (5, 1) returns "00001". If the value is negative then the minus sign is
   * included in the length. eg padZeros (5, -1) returns "-0001". The base used for the returned
   * string is base 10.
   * 
   * @param length the length of the returned string after padding with zeros
   * @param value the value to be padded with zeros.
   */

  public static String padZeros(int length, int value)
  {
    return padZeros(length, value, 10);
  }

  /**
   * Separates the supplied string into separate strings which are separated by the separator. The
   * separated strings can be automatically trimmed (remove white space from start and end. eg
   * separateString (" 1 , 2, 3,    4", ",", true) returns {"1","2","3","4"}.
   * 
   * @param string the string to be separated
   * @param separator the string used as the separator (e.g. ",")
   * @param trim true if white space should be trimmed from the separated strings or false if they
   *          shouldn't
   */

  public static String[] separateString(String string, String separator, boolean trim)
  {
    int nArraySize = 1;
    int i = 0;
    int nSeparatorLength = separator.length();

    // Calculate size of array to allocate

    while(i < string.length())
    {
      int index = string.indexOf(separator, i);

      if(index == -1)
        break;

      i = index + nSeparatorLength;
      nArraySize++;
    }

    // Create and fill array with separated strings

    String[] result = new String[nArraySize];
    int nPos = 0;
    i = 0;
    String sValue = null;

    while(i < string.length())
    {
      int index = string.indexOf(separator, i);

      if(index == -1)
        sValue = string.substring(i);

      else
        sValue = string.substring(i, index);

      if(trim)
        sValue = sValue.trim();

      result[nPos++] = sValue;

      if(index == -1)
        break;

      i = index + nSeparatorLength;
    }

    if(nPos < nArraySize)
      result[nPos] = "";

    return result;
  }

  public static boolean containsWhiteSpace(String s)
  {
    for(int i = 0; i < s.length(); i++)
    {
      char c = s.charAt(i);

      if(c == ' ' || c == '\t' || c == '\n' || c == '\r')
        return true;
    }

    return false;
  }

  /** Separates the string using the supplied separator without trimming the separted strings. */

  public static String[] separateString(String string, String separator)
  {
    return separateString(string, separator, false);
  }

  /** Seapartes the string using a comma with optional trimming of the separted strings. */

  public static String[] separateString(String string, boolean trim)
  {
    return separateString(string, ",", trim);
  }

  /** Seapartes the string using a comma without trimming of the separted strings. */

  public static String[] separateString(String string)
  {
    return separateString(string, false);
  }

  /**
   * Determines if a string matches a pattern with or without case sensitivity.
   * 
   * @param sString The string to match against the pattern.
   * @param sPattern The pattern to check against.
   * @param bCaseSensitive true if case sensitivity should be used when matching the pattern or
   *          false otherwise.
   * @return If the string matches the expression, true, else false.
   */

  public static boolean matchesPattern(String sString, String sPattern, boolean bCaseSensitive)
  {
    return matchesPattern(sString, sPattern, 0, 0, bCaseSensitive);
  }

  /**
   * Determines if a string matches a pattern with case sensitivity.
   * 
   * @param sString The string to match against the pattern.
   * @param sPattern The pattern to check against.
   * @return If the string matches the expression, true, else false.
   */

  public static boolean matchesPattern(String sString, String sPattern)
  {
    return matchesPattern(sString, sPattern, true);
  }

  /**
   * Expands the the suplied string and returns a new string where tabs have been expanded to
   * spaces.
   */

  public static String expandTabs(String sString, int nTabSize)
  {
    boolean bHasTabs = false;

    for(int i = 0; i < sString.length(); i++)
    {
      if(sString.charAt(i) == '\t')
      {
        bHasTabs = true;
        break;
      }
    }

    if(!bHasTabs)
      return sString;

    StringBuffer buffer = new StringBuffer(sString.length() + 20);

    for(int nChar = 0; nChar < sString.length(); nChar++)
    {
      char c = sString.charAt(nChar);

      if(c == '\t')
      {
        int nNumSpaces = (buffer.length() / nTabSize + 1) * nTabSize - buffer.length();

        for(int j = 0; j < nNumSpaces; j++)
          buffer.append(' ');
      }

      else
        buffer.append(c);
    }

    return buffer.toString();
  }

  /**
   * An internal routine to implement expression matching. This routine is based on a self-recursive
   * algorithm.
   * 
   * @param string The string to be compared.
   * @param pattern The expression to compare <em>string</em> to.
   * @param sIdx The index of where we are in <em>string</em>.
   * @param pIdx The index of where we are in <em>pattern</em>.
   * @return True if <em>string</em> matched pattern, else false.
   */
  private static boolean matchesPattern(String sString, String sPattern, int nStringIndex, int nPatternIndex, boolean bCaseSensitive)
  {
    int nPatternLength = sPattern.length();
    int nStringLength = sString.length();

    while(true)
    {
      if(nPatternIndex >= nPatternLength)
        return (nStringIndex >= nStringLength);

      if(nStringIndex >= nStringLength && sPattern.charAt(nPatternIndex) != '*')
        return false;

      // Check for a '*' as the next sPattern char.
      // This is handled by a recursive call for
      // each postfix of the name.

      if(sPattern.charAt(nPatternIndex) == '*')
      {
        if(++nPatternIndex >= nPatternLength)
          return true;

        while(true)
        {
          if(matchesPattern(sString, sPattern, nStringIndex, nPatternIndex, bCaseSensitive))
            return true;

          if(nStringIndex >= nStringLength)
            return false;

          ++nStringIndex;
        }
      }

      // Check for '?' as the next sPattern char.
      // This matches the current character.

      if(sPattern.charAt(nPatternIndex) == '?')
      {
        ++nPatternIndex;
        ++nStringIndex;
        continue;
      }

      // Check for '[' as the next sPattern char.
      // This is a list of acceptable characters,
      // which can include character ranges.

      if(sPattern.charAt(nPatternIndex) == '[')
      {
        for(++nPatternIndex;; ++nPatternIndex)
        {
          if(nPatternIndex >= nPatternLength || sPattern.charAt(nPatternIndex) == ']')
            return false;

          if(areCharsEqual(sPattern.charAt(nPatternIndex), sString.charAt(nStringIndex), bCaseSensitive))
            break;

          if(nPatternIndex < (nPatternLength - 1) && sPattern.charAt(nPatternIndex + 1) == '-')
          {
            if(nPatternIndex >= (nPatternLength - 2))
              return false;

            char c = sString.charAt(nStringIndex);
            char cRangeStart = sPattern.charAt(nPatternIndex);
            char cRangeEnd = sPattern.charAt(nPatternIndex + 2);

            if(isCharInRange(c, cRangeStart, cRangeEnd, bCaseSensitive))
              break;

            nPatternIndex += 2;
          }
        }

        for(; sPattern.charAt(nPatternIndex) != ']'; ++nPatternIndex)
        {
          if(nPatternIndex >= nPatternLength)
          {
            --nPatternIndex;
            break;
          }
        }

        ++nPatternIndex;
        ++nStringIndex;
        continue;
      }

      // Check for backslash escapes
      // We just skip over them to match the next char.

      if(sPattern.charAt(nPatternIndex) == '\\')
      {
        if(++nPatternIndex >= nPatternLength)
          return false;
      }

      if(nPatternIndex < nPatternLength && nStringIndex < nStringLength)
        if(!areCharsEqual(sPattern.charAt(nPatternIndex), sString.charAt(nStringIndex), bCaseSensitive))
          return false;

      ++nPatternIndex;
      ++nStringIndex;
    }
  }

  private static boolean areCharsEqual(char c1, char c2, boolean bCaseSensitive)
  {
    if(bCaseSensitive)
      return c1 == c2;

    return Character.toUpperCase(c1) == Character.toUpperCase(c2);
  }

  private static boolean isCharInRange(char c, char cRangeStart, char cRangeEnd, boolean bCaseSensitive)
  {
    if(!bCaseSensitive)
    {
      c = Character.toUpperCase(c);
      cRangeStart = Character.toUpperCase(cRangeStart);
      cRangeEnd = Character.toUpperCase(cRangeEnd);
    }

    if(cRangeEnd < cRangeStart)
    {
      char cTemp = cRangeStart;
      cRangeStart = cRangeEnd;
      cRangeEnd = cTemp;
    }

    return (c >= cRangeStart && c <= cRangeEnd);
  }
}

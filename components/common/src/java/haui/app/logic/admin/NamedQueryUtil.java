/* *****************************************************************
 * Project: common
 * File:    NamedQueryUtil.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin;

import haui.app.dao.db.AbstractDataSource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * NamedQueryUtil
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class NamedQueryUtil
{

  private static final Log LOG = LogFactory.getLog(NamedQueryUtil.class);

  public static String substituteParmsInQuery(String query, Map parms)
  {
    StringBuffer sb = new StringBuffer();

    int position = 0;
    int prevPosition = 0;
    int length = query.length();

    while(position != -1 && position < length)
    {
      position = query.indexOf("++", position);
      if(position != -1)
      {
        sb.append(query.substring(prevPosition, position));
        int end_position = query.indexOf("++", position + 2);
        if(end_position == -1)
        {
          throw new RuntimeException("invalid query at position " + position);
        }
        String arg = query.substring(position + 2, end_position);
        String argVal = null;
        if(parms != null)
        {
          argVal = (String)parms.get(arg);
        }
        if(argVal != null)
        {
          sb.append(argVal);
        }
        else
        {
          LOG.warn("The argVal for '" + arg + "' was null - " + "are you sure you supplied the correct parms for this query?");
        }
        position = end_position + 2;
        prevPosition = position;
      }
    }
    sb.append(query.substring(prevPosition, length));
    String transformedQuery = new String(sb);

    return transformedQuery;
  }

  public static String addSchemaPrefixToTablenames(String query)
  {
    StringBuffer sb = new StringBuffer();
    int idx = -1;
    String searchWord = "from";

    idx = query.toLowerCase().indexOf(searchWord);

    if(idx > 0)
    {
      idx += searchWord.length();
      sb.append(query.substring(0, idx));
      sb.append(" ");

      if(query.length() > idx)
      {
        StringTokenizer tokenizer = new StringTokenizer(query.substring(idx + 1), ",", false);

        int i = 0;
        boolean addPrefix = true;
        while(tokenizer.hasMoreTokens())
        {
          String tablename = tokenizer.nextToken().trim();

          StringTokenizer tmp = new StringTokenizer(tablename, " \n\t\r", false);
          if(addPrefix && tmp.countTokens() > 0)
          {
            if(i == 0)
            {
              sb.append(" ");
              sb.append(AbstractDataSource.instance().getSchemaPrefix());
              sb.append(tablename);
            }
            else
            {
              sb.append(", ");
              sb.append(AbstractDataSource.instance().getSchemaPrefix());
              sb.append(tablename);
            }
            if(tmp.countTokens() > 1)
            {
              addPrefix = false;
              sb.append(" ");
            }
          }
          else
          {
            addPrefix = false;
            sb.append(" ");
            sb.append(tablename);
          }
        }
      }
    }

    return sb.toString();
  }

  private static String substituteKeysAndValsForInsert(String query, String tableName, String rowId, Map keysAndVals)
  {
    String[] keysAndValsStrings = getKeysAndValsStringsFromMap(keysAndVals);
    Map newMap = new HashMap();
    newMap.put("tableName", tableName);
    newMap.put("rowId", rowId);
    newMap.put("keys", keysAndValsStrings[0]);
    newMap.put("vals", keysAndValsStrings[1]);
    return substituteParmsInQuery(query, newMap);
  }

  private static String[] getKeysAndValsStringsFromMap(Map keysAndVals)
  {
    String[] strings = new String[2];
    String key;
    String val;
    StringBuffer keys = new StringBuffer();
    StringBuffer vals = new StringBuffer();

    Set keySet = keysAndVals.keySet();
    Iterator iter = keySet.iterator();
    while(iter.hasNext())
    {
      key = (String)iter.next();
      val = (String)keysAndVals.get(key);
      keys.append(key);
      if(shouldNotQuote(val))
      {
        vals.append(val);
      }
      else
      {
        vals.append("'" + val + "'");
      }
      if(iter.hasNext())
      {
        keys.append(", ");
        vals.append(", ");
      }
    }
    strings[0] = keys.toString();
    strings[1] = vals.toString();
    return strings;
  }

  public static String substituteParmsInQuery(String queryWithParameterizedTableName, String tableName, String rowId, Map parms)
  {
    String transformedQuery = null;
    if(queryWithParameterizedTableName.indexOf("++tableName++") == -1 || queryWithParameterizedTableName.indexOf("++keys++") == -1
        || queryWithParameterizedTableName.indexOf("++vals++") == -1)
    {
      // the resulting query will not be valid SQL
      LOG.error("the named query is invalid");
    }
    else
    {
      // substitute the parms
      transformedQuery = substituteKeysAndValsForInsert(queryWithParameterizedTableName, tableName, rowId, parms);
    }
    return transformedQuery;
  }

  public static String substituteParmsInDeleteQuery(String query, String tableName, Map parms)
  {
    String s = "";
    if(query.indexOf("++keyValMap++") == -1)
    {
      LOG.error("the named query is invalid");
    }
    else
    {
      s = substituteKeysAndValsForDelete(query, tableName, parms);
      LOG.debug("s = >" + s + "<");
    }

    return s;
  }

  private static String substituteKeysAndValsForDelete(String query, String tableName, Map keysAndVals)
  {
    String keysAndValsString = (String)getKeysAndValsStringFromMap(keysAndVals);
    Map newMap = new HashMap();
    newMap.put("tableName", tableName);
    newMap.put("keyValMap", keysAndValsString);
    return substituteParmsInQuery(query, newMap);
  }

  public static String substituteParmsInUpdateQuery(String query, String tableName, List keys, Map parms)
  {
    Set s = parms.keySet();
    Iterator iter = s.iterator();
    StringBuffer setClause = new StringBuffer();
    StringBuffer whereClause = new StringBuffer();
    String key = "";
    String value = "";
    while(iter.hasNext())
    {
      key = (String)iter.next();
      LOG.debug("key = >" + key + "<");
      if(keys.contains(key))
      { // it is a business key and belongs in the where clause
        value = (String)parms.get(key);
        if(whereClause.length() != 0)
        { // it is not empty, so add the "and"
          whereClause.append(" and ");
        }
        if(shouldNotQuote(value))
        {
          // do not add the single quotes
          whereClause.append(key + " = " + value);
        }
        else
        {
          whereClause.append(key + " = '" + value + "'");
        }
      }
      else
      { // it belongs in the set clause
        value = (String)parms.get(key);
        if(setClause.length() != 0)
        { // it is not empty, so add the ", "
          setClause.append(", ");
        }
        if(shouldNotQuote(value))
        {
          // do not add the single quotes
          setClause.append(key + " = " + value);
        }
        else
        {
          setClause.append(key + " = '" + value + "'");
        }
      }
    }
    LOG.debug("setClause = >" + setClause.toString() + "<");
    LOG.debug("whereClause = >" + whereClause.toString() + "<");
    // now substitute the the values in the query
    Map myMap = new HashMap();
    myMap.put("tableName", tableName);
    myMap.put("paramMap", setClause.toString());
    myMap.put("keyMap", whereClause.toString());
    String resolvedQuery = substituteParmsInQuery(query, myMap);
    LOG.debug("resolvedQuery = >" + resolvedQuery + "<");
    return resolvedQuery;
  }

  /*
   * return true if this value should not have quotes around it in the SQL
   */
  private static boolean shouldNotQuote(String value)
  {
    if(value == null)
    {
      return true;
    }
    if(value.startsWith("to_date") && value.indexOf("DD-MON-YY HH24:MI:SS") != -1)
    {
      return true;
    }
    return false;
  }

  private static String getKeysAndValsStringFromMap(Map keysAndVals)
  {
    Set s = keysAndVals.keySet();
    Iterator iter = s.iterator();
    StringBuffer sb = new StringBuffer();
    String key = "";
    String value = "";
    while(iter.hasNext())
    {
      key = (String)iter.next();
      LOG.debug("key = >" + key + "<");
      value = (String)keysAndVals.get(key);
      sb.append(key + " = '" + value.replaceAll("'", "''") + "'"); // dev item 675 - double-up the
                                                                   // single quotes
      if(iter.hasNext())
      {
        sb.append(" and ");
      }
    }
    return sb.toString();
  }
}

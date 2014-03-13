/* *****************************************************************
 * Project: common
 * File:    Job.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import haui.app.execution.AppExecutable;
import haui.exception.AppSystemException;
import haui.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Job
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Job implements XmlElement
{

  private static final Log LOG = LogFactory.getLog(Job.class);

  public static final String TAG = "job";

  private String name;

  private int timeout = AppExecutable.NO_TIMEOUT;

  private Boolean persistent = null;

  private ConditionalExpression conditionalExpression;

  private List<Blockedjob> blocks = new ArrayList<Blockedjob>();

  public boolean init(Element element)
  {
    boolean result = true;

    String jobName = element.getAttribute("name");
    setName(jobName);
    String jobTimeout = element.getAttribute("timeout");
    if(jobTimeout != null && !jobTimeout.equals(""))
    {
      try
      {
        Integer integer = new Integer(jobTimeout);
        setTimeout(integer.intValue());
      }
      catch(Exception e)
      {
        LOG.error("Timeout parsing error", e);
      }
    }
    String jobPersistency = element.getAttribute("persistent");
    setPersistent(jobPersistency != null ? new Boolean(jobPersistency) : new Boolean(true));

    NodeList listOfSubElements = element.getElementsByTagName(Blockedjob.TAG);

    for(int i = 0; i < listOfSubElements.getLength(); i++)
    {
      Element subElement = (Element)listOfSubElements.item(i);

      Blockedjob blockedjob = new Blockedjob();
      blockedjob.init(subElement);
      if(blockedjob.getName() != null)
        addBlockedjob(blockedjob);
    }
    // add this job to the block list
    Blockedjob blockedjob = new Blockedjob();
    blockedjob.setName(jobName);
    addBlockedjob(blockedjob);

    listOfSubElements = element.getElementsByTagName("condition");

    if(listOfSubElements.getLength() > 1)
    {
      throw new AppSystemException("There is more than one conditional expression tag in " + jobName);
    }
    else if(listOfSubElements.getLength() == 1)
    {
      Element subElement = (Element)listOfSubElements.item(0);

      List<Element> condElements = XmlJobDependencyUtil.getConditionalExpressionElements(subElement);
      if(condElements.size() > 1)
      {
        throw new AppSystemException("There is more than one conditional expression tag in " + jobName);
      }
      else if(condElements.size() == 1)
      {
        subElement = (Element)condElements.get(0);

        setConditionalExpression(ExpressionFactory.createConditionalExpression(subElement));
      }
      else
      {
        setConditionalExpression(null);
      }
    }
    else
    {
      setConditionalExpression(null);
    }

    return result;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getTimeout()
  {
    return timeout;
  }

  public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }

  public Boolean getPersistent()
  {
    return persistent;
  }

  public void setPersistent(Boolean persistent)
  {
    this.persistent = persistent;
  }

  public ConditionalExpression getConditionalExpression()
  {
    return conditionalExpression;
  }

  public void setConditionalExpression(ConditionalExpression conditionalExpression)
  {
    this.conditionalExpression = conditionalExpression;
  }

  public List<Blockedjob> getBlocks()
  {
    return blocks;
  }

  public void setBlocks(List<Blockedjob> blocks)
  {
    this.blocks = blocks;
  }

  public boolean addBlockedjob(Blockedjob blockedjob)
  {
    return blocks.add(blockedjob);
  }

  public boolean removeBlockedjob(Blockedjob blockedjob)
  {
    return blocks.remove(blockedjob);
  }

  public boolean locks(String jobName)
  {
    boolean result = false;
    for(int i = 0; i < blocks.size() && !result; ++i)
    {
      Blockedjob blockedjob = blocks.get(i);
      result = AppUtil.equals(blockedjob.getName(), jobName);
    }
    return result;
  }

  public boolean evaluateCondition()
  {
    if(getConditionalExpression() == null)
      return true;
    return getConditionalExpression().evaluate();
  }
}

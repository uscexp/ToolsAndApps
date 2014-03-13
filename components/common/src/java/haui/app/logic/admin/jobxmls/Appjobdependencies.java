/* *****************************************************************
 * Project: common
 * File:    Artejobdependencies.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.admin.jobxmls;

import haui.exception.AppLogicException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Artejobdependencies
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class Appjobdependencies implements XmlElement
{

  private static final Log LOG = LogFactory.getLog(Appjobdependencies.class);

  public static final String TAG = "artejobdependencies";

  private Map<String, Job> jobs = new HashMap<String, Job>();

  public boolean init(Element root)
  {
    boolean result = true;

    NodeList listOfMasterJobs = root.getElementsByTagName(Job.TAG);

    for(int i = 0; i < listOfMasterJobs.getLength(); i++)
    {
      Element jobElement = (Element)listOfMasterJobs.item(i);

      String jobName = jobElement.getAttribute("name");
      try
      {
        Job job = new Job();
        job.init(jobElement);
        addJob(job);
      }
      catch(Exception e)
      {
        LOG.error("Job " + jobName + " could not be parsed in job dependency XML!", e);
      }
    }
    return result;
  }

  public String[] getMutualExclusionKeys(String jobName)
  {
    String[] keys = null;
    Job job = getJob(jobName);
    if(job == null)
    {
      keys = new String[0];
    }
    else
    {
      List<Blockedjob> jobs = job.getBlocks();
      keys = new String[jobs.size()];

      for(int i = 0; i < jobs.size(); ++i)
      {
        Blockedjob blockedjob = jobs.get(i);

        keys[i] = blockedjob.getName();
      }
    }
    return keys;
  }

  public Job[] getJobs()
  {
    jobs.values();
    return jobs.values().toArray(new Job[jobs.size()]);
  }

  public void setJobs(Collection<Job> jobs)
  {
    for(Iterator<Job> it = jobs.iterator(); it.hasNext();)
    {
      Job job = it.next();
      addJob(job);
    }
  }

  public Job addJob(Job job)
  {
    return jobs.put(job.getName(), job);
  }

  public Job removeJob(Job job)
  {
    return jobs.remove(job.getName());
  }

  public Job getJob(String jobName)
  {
    return jobs.get(jobName);
  }

  private Job getJobNullChecked(String jobName)
  {
    Job job = getJob(jobName);
    if(job == null)
    {
      throw new AppLogicException("No Job found with name: " + jobName);
    }
    return job;
  }

  public boolean locks(String runningJobName, String jobNameToCheck)
  {
    Job runningJob = getJobNullChecked(runningJobName);
    return runningJob.locks(jobNameToCheck);
  }

  public boolean isConditionFulfilled(String jobName)
  {
    Job job = getJobNullChecked(jobName);
    return job.evaluateCondition();
  }
}

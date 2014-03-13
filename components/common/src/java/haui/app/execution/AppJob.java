/* *****************************************************************
 * Project: common
 * File:    AppJob.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.execution;

import haui.exception.AppLogicException;
import haui.model.admin.AdminJobConfigDTO;
import haui.model.admin.AdminJobDTO;
import haui.util.GlobalApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import haui.app.dao.admin.AdminJobConfigDAO;
import haui.app.logic.admin.jobxmls.Appjobdependencies;
import haui.app.logic.admin.jobxmls.Blockedjob;
import haui.app.logic.admin.jobxmls.Job;

/**
 * AppJob
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public abstract class AppJob implements IJob
{

  private static final Log LOG = LogFactory.getLog(AppJob.class);

  public final static String APP_JOB_DEPENDENCIES_FILENAME = "appJobDependencies.xml";

  private final static String APP_JOB_DEPENDENCIES_XML_FILE = "app.job.dependency.file";

  private final static String APP_JOB_DEPENDENCIES_XML_DEFAULT = "/appJobDependencies.xml";

  private AdminJobDTO adminJob;

  private Appjobdependencies artejobdependencies;

  public AppJob()
  {
    initJobDescription();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.arte.app.execution.IIsPcJob#init(com.ubs.arte.app.execution.ExecutionContext)
   */
  public void init(ExecutionContext context) throws AppLogicException
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.arte.app.execution.IIsPcJob#getMutualExclusionKeys()
   */
  public final String[] getMutualExclusionKeys()
  {
    return artejobdependencies.getMutualExclusionKeys(this.getJobName());
  }

  public Appjobdependencies getAppjobdependencies()
  {
    return artejobdependencies;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.arte.app.execution.IIsPcJob#run(com.ubs.arte.app.execution.ExecutionContext)
   */
  public abstract void run(ExecutionContext context) throws AppLogicException;

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.arte.app.execution.IIsPcJob#getIdentifier()
   */
  public final String getIdentifier()
  {
    if(adminJob == null)
      return null;
    return adminJob.getExecutableIdentifier();
  }

  public String getJobName()
  {
    if(adminJob == null)
      return null;
    return adminJob.getCode();
  }

  public int getTimeout()
  {
    if(artejobdependencies.getJob(this.getJobName()) == null)
    {
      Job job = new Job();
      job.setName(this.getJobName());
      Blockedjob blockedjob = new Blockedjob();
      blockedjob.setName(this.getIdentifier());
      job.addBlockedjob(blockedjob);
      artejobdependencies.addJob(job);
    }
    return artejobdependencies.getJob(this.getJobName()).getTimeout();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ubs.arte.app.execution.IIsPcJob#getAdminJob()
   */
  public AdminJobDTO getAdminJob()
  {
    return adminJob;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.ubs.arte.app.execution.IIsPcJob#setAdminJob(com.ubs.arte.common.model.admin.AdminJobDTO)
   */
  public void setAdminJob(AdminJobDTO aAdminJob)
  {
    adminJob = aAdminJob;
  }

  protected void checkForSelfBlockingJob()
  {
    if(artejobdependencies.getJob(this.getJobName()) == null)
    {
      Job job = new Job();
      job.setName(this.getJobName());
      Blockedjob blockedjob = new Blockedjob();
      blockedjob.setName(this.getIdentifier());
      job.addBlockedjob(blockedjob);
      artejobdependencies.addJob(job);
    }
  }

  private void initJobDescription()
  {
    try
    {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

      AdminJobConfigDAO daoConfig = new AdminJobConfigDAO();
      // retrieve arte job dependency config file from DB
      AdminJobConfigDTO adminJobConfigDTO = daoConfig.findByName(APP_JOB_DEPENDENCIES_FILENAME);
      InputStream in = null;
      if(adminJobConfigDTO == null)
      {
        // if in DB no AdminJobConfigDTO does exist (read from initial default artejobdependency
        // file)
        LOG.error("Could not find ressource: " + APP_JOB_DEPENDENCIES_FILENAME + " in DB -> take initial file");
        String resource = GlobalApplicationContext.instance().getApplicationProperties().getProperty(APP_JOB_DEPENDENCIES_XML_FILE, APP_JOB_DEPENDENCIES_XML_DEFAULT);
        in = this.getClass().getResourceAsStream(resource);
      }
      else
      {
        byte[] jobDependenciesFileContent = adminJobConfigDTO.getFile();

        if(jobDependenciesFileContent == null)
        {
          LOG.error("AppJobDependencies File is missing in AdminJobConfigDTO");
          throw new AppLogicException("AppJobDependencies File is missing in AdminJobConfigDTO");
        }

        in = new ByteArrayInputStream(jobDependenciesFileContent);
      }

      Document doc = docBuilder.parse(in);

      if(doc.getNodeType() != Node.DOCUMENT_NODE)
      {
        LOG.warn("XML File is not valid document");
        return;
      }

      // normalize text representation
      doc.getDocumentElement().normalize();

      NodeList roots = doc.getElementsByTagName(Appjobdependencies.TAG);

      if(roots != null && roots.getLength() > 0)
      {
        Element root = (Element)roots.item(0);

        if(root != null)
        {
          artejobdependencies = new Appjobdependencies();
          artejobdependencies.init(root);
        }
        else
        {
          artejobdependencies = new Appjobdependencies();
          Job job = new Job();
          job.setName(this.getJobName());
          Blockedjob blockedjob = new Blockedjob();
          blockedjob.setName(this.getIdentifier());
          job.addBlockedjob(blockedjob);
          artejobdependencies.addJob(job);
        }
      }
    }
    catch(Exception e)
    {
      LOG.warn("Unable to process file", e);
    }
  }
}

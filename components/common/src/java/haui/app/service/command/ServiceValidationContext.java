/* *****************************************************************
 * Project: common
 * File:    ServiceValidationContext.java
 * 
 * Creation:     26.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.service.command;

/**
 * Holds information corresponding to a service invocation and its validation
 * requirements.
 * <p>
 * The instance of this class associated with the current service call can be
 * accessed trough <code>ServiceContext.getValidationContext()</code>.
 * <p>
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public class ServiceValidationContext
{

  private boolean alwaysForceValidation = false;
  private boolean validationRequired = false;
  private boolean validationDone = false;
  private Class serviceBOClass = null;
  private String serviceName = null;

  /**
   * 
   */
  public ServiceValidationContext(Class serviceBoClass, String serviceName, boolean validationRequired)
  {
    this.serviceBOClass = serviceBoClass;
    this.serviceName = serviceName;
    this.validationRequired = validationRequired;
  }

  /**
   * This method gets called by the ServiceSesssionBean subclasses after the bo method implementing
   * the service has been called. It verifies that validation has occurred if required (if the
   * service is transactional) and creates an 'error' log enty if no validation has occured.
   * 
   */
  public void verify()
  {

    if(isValidationRequired() && !isValidationDone())
    {
      // TODO Mirko enable if we know where we need it.
      // LOG.warn("Transactional service invoked but no validation took place!");
    }
  }

  /**
   * @return Returns the validationDone.
   */
  public boolean isValidationDone()
  {
    return validationDone;
  }

  /**
   * @param validationDone The validationDone to set.
   */
  public void setValidationDone(boolean validationDone)
  {
    this.validationDone = validationDone;
  }

  /**
   * @return Returns the validationRequired.
   */
  public boolean isValidationRequired()
  {
    return validationRequired;
  }

  /**
   * @param validationRequired The validationRequired to set.
   */
  public void setValidationRequired(boolean validationRequired)
  {
    this.validationRequired = validationRequired;
  }

  /**
   * @return Returns the serviceBOClass.
   */
  public Class getServiceBOClass()
  {
    return serviceBOClass;
  }

  /**
   * @return Returns the serviceName.
   */
  public String getServiceName()
  {
    return serviceName;
  }

  /**
   * @return Returns the alwaysForceValidation.
   */
  public boolean isAlwaysForceValidation()
  {
    return alwaysForceValidation;
  }

  /**
   * @param alwaysForceValidation The alwaysForceValidation to set.
   */
  public void setAlwaysForceValidation(boolean alwaysForceValidation)
  {
    this.alwaysForceValidation = alwaysForceValidation;
  }
}

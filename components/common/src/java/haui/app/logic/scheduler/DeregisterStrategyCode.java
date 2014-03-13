/* *****************************************************************
 * Project: common
 * File:    DeregisterStrategyCode.java
 * 
 * Creation:     02.12.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.logic.scheduler;

/**
 * DeregisterStrategyCode
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface DeregisterStrategyCode
{
  public static final DeregisterStrategyCode MANUALLY = new DeregisterStrategyCodeImpl("MANUALLY");
  public static final DeregisterStrategyCode NOT_DEREGISTRERABLE = new DeregisterStrategyCodeImpl("NOT_DEREGISTRERABLE");

  public static final DeregisterStrategyCode EVENT_NOT_ACTIVE = new DeregisterStrategyCodeImpl("EVENT_NOT_ACTIVE");
  public static final DeregisterStrategyCode AFTER_FIRST_RUN = new DeregisterStrategyCodeImpl("AFTER_FIRST_RUN");
  public static final DeregisterStrategyCode EXECUTION_FAILED = new DeregisterStrategyCodeImpl("EXECUTION_FAILED");

  public static final DeregisterStrategyCode TERMINATION_CODE_FAILED = new DeregisterStrategyCodeImpl("TERMINATION_CODE_FAILED");
  public static final DeregisterStrategyCode TERMINATION_CODE_SUCCESSFUL = new DeregisterStrategyCodeImpl("TERMINATION_CODE_SUCCESSFUL");
  public static final DeregisterStrategyCode TERMINATION_CODE_CANCEL = new DeregisterStrategyCodeImpl("TERMINATION_CODE_CANCEL");

  final class DeregisterStrategyCodeImpl implements DeregisterStrategyCode {
      private final String name;

      private DeregisterStrategyCodeImpl(String name) {
          this.name = name;
      }
      public String toString() {
          return name;
      }
  }
}

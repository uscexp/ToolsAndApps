/* *****************************************************************
 * Project: common
 * File:    Registry.java
 * 
 * Creation:     28.11.2008 by A.E.
 * Modification: $Date$ $Author$
 * Version:      $Revision$
 *
 * Copyright (C) 1998 - 2008 A.E. All rights reserved!
 * ****************************************************************/

package haui.app.threading.impl;

import java.io.IOException;
import java.io.Writer;

/**
 * Registry
 * 
 * @author Andreas Eisenhauer
 * $LastChangedRevision: $
 * @since 1.0
 */
public interface Registry
{
  String[] INDENTS = { "", "    ", "        ", "            " };

  void register(Registerable registerable);

  boolean unregister(Registerable registerable);

  String createInternalLabel(String label);

  void writeOverview(Writer wrt, int indent) throws IOException;
}

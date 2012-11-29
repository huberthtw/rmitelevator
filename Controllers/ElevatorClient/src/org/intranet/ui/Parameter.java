/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.ui;

import java.io.Serializable;

/**
 * @author Neil McKellar and Chris Dailey
 *
 * LATER: add a way to validate inputs
 */
public abstract class Parameter
  implements Serializable
{
  private String description;
  
  private Parameter()
  {
    super();
  }
  
  public Parameter(String desc)
  {
    super();
    description = desc;
  }
  
  public final String getDescription()
  {
    return description;
  }

  public String toString()
  {
    return getDescription();
  }
}

/*
 * Copyright 2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.ui;

import java.util.List;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public abstract class MultipleValueParameter
    extends Parameter
{
  public MultipleValueParameter(String desc)
  {
    super(desc);
    isMultiple = false;
  }

  public abstract Object getSingleValue();
  public abstract List getParameterList();
  protected boolean isMultiple;

  public void setMultiple(boolean multiple)
  { isMultiple = multiple; }

  public boolean isMultiple()
  { return isMultiple; }
}

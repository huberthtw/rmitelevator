/*
 * Copyright 2004-2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.intranet.elevator.model.operate.Building;
import org.intranet.elevator.view.BuildingView;
import org.intranet.sim.Model;
import org.intranet.sim.SimulationApplication;
import org.intranet.sim.ui.ApplicationUI;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class ElevatorSimulationApplication
  implements SimulationApplication
{
  private Image iconImage;
  public static void main(String[] args)
  {
    ElevatorSimulationApplication sc = new ElevatorSimulationApplication();
 
//    new SimulationSelection(sc);
    new ApplicationUI(sc);
  }

  public ElevatorSimulationApplication()
  {
    super();
  }

  public JComponent createView(Model m)
  {
    return new BuildingView((Building)m);
  }
  
  public List getSimulations()
  {
    List simulations = new ArrayList();
    simulations.add(new RandomElevatorSimulator());
    simulations.add(new MorningTrafficElevatorSimulator());
    simulations.add(new EveningTrafficElevatorSimulator());
    simulations.add(new ThreePersonBugSimulator());
    simulations.add(new ThreePersonElevatorSimulator());
    simulations.add(new UpToFourThenDownSimulator());
    simulations.add(new NoIdleElevatorCarSimulator());
    simulations.add(new ThreePersonTwoElevatorSimulator());
    return simulations;
  }

  public String getApplicationName()
  {
    return "Elevator Simulator";
  }

  public String getCopyright()
  {
    return "Copyright 2004-2005 Chris Dailey & Neil McKellar";
  }

  public String getVersion()
  {
    return "0.4";
  }

  public Image getImageIcon()
  {
    if (iconImage == null)
    {
      URL iconImageURL = ElevatorSimulationApplication.class.getResource("icon.gif");
      iconImage = Toolkit.getDefaultToolkit().createImage(iconImageURL);
    }
    return iconImage;
  }
}

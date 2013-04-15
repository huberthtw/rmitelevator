/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator;

import java.util.Random;

import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.Building;
import org.intranet.elevator.model.operate.Person;
import org.intranet.elevator.model.operate.controller.MetaController;
import org.intranet.sim.Model;
import org.intranet.sim.Simulator;
import org.intranet.sim.event.Event;
import org.intranet.ui.FloatParameter;
import org.intranet.ui.IntegerParameter;
import org.intranet.ui.LongParameter;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class EveningTrafficElevatorSimulator
  extends Simulator
{
  private IntegerParameter floorsParameter;
  private IntegerParameter carsParameter;
  private IntegerParameter ridersParameter;
  private FloatParameter durationParameter;
  private IntegerParameter stdDeviationParameter;
  private LongParameter seedParameter;
  
  private Building building;

  public EveningTrafficElevatorSimulator()
  {
    super();
    floorsParameter = new IntegerParameter("Number of Floors",10);
    parameters.add(floorsParameter);
    carsParameter = new IntegerParameter("Number of Elevators",3);
    parameters.add(carsParameter);
    ridersParameter = new IntegerParameter("Number of People per Floor", 20);
    parameters.add(ridersParameter);
    durationParameter = new FloatParameter("Rider insertion time (hours)",1.0f);
    parameters.add(durationParameter);
    stdDeviationParameter = new IntegerParameter("Standard Deviation", 1);
    parameters.add(stdDeviationParameter);
    seedParameter = new LongParameter("Random seed", 635359);
    parameters.add(seedParameter);
  }

  public void initializeModel()
  {
    int numFloors = floorsParameter.getIntegerValue();
    int numCars = carsParameter.getIntegerValue();
    int numRiders = ridersParameter.getIntegerValue();
    float duration = durationParameter.getFloatValue();
    int stdDeviation = stdDeviationParameter.getIntegerValue();
    long seed = seedParameter.getLongValue();

    building = new Building(getEventQueue(), numFloors, numCars,
        new MetaController());
    // destination floor is the ground floor
    final Floor destFloor = building.getFloor(0);
    
    Random rand = new Random(seed);

    for (int i = 1; i < numFloors; i++)
    {
      Floor startingFloor = building.getFloor(i);
      for (int j = 0; j < numRiders; j++)
      {
        final Person person = building.createPerson(startingFloor);
        // time to insert
        // Convert a gaussian[-1, 1] to a gaussian[0, 1]
        float gaussian = (getGaussian(rand, stdDeviation) + 1) / 2;
        // Apply gaussian value to the duration (in hours)
        // and convert to milliseconds
        long insertTime = (long)(gaussian * duration * 3600 * 1000);
        
        // insertion event for destination at time
        Event event = new Event(insertTime)
        {
          public void perform()
          {
            person.setDestination(destFloor);
          }
        };
        getEventQueue().addEvent(event);
      }
    }
  }

  public final Model getModel()
  {
    return building;
  }
  
  public String getDescription()
  {
    return "Evening Traffic Rider Insertion";
  }

  public Simulator duplicate()
  {
    return new EveningTrafficElevatorSimulator();
  }
  
  private static float getGaussian(Random rand, int stddev)
  {
    while (true)
    {
      float gaussian = (float)(rand.nextGaussian()/stddev);
      if (gaussian >= -1.0f && gaussian <= 1.0f) return gaussian;
    }
  }
}

Elevator Simulator 0.4
Copyright 2004-2005 Neil McKellar and Chris Dailey
Licensed under the LGPL (Lesser GNU Public License)

Written by Neil and Chris

The Elevator Simulator is an experiment in paired-programming.
Development is done entirely in Java. The finished product will
provide a framework for additional simulations and some ability
to test different elevator behaviours.

The application can be built with Ant using the build.xml file in
the components directory.  The current source was developed with
JDK 1.4.2.  There are no external library dependencies.  The
resulting jar file in build/jars/ should automatically execute
org.intranet.elevator.ElevatorSimulationApplication.

The project page is located at:
http://sourceforge.net/projects/elevatorsim

Report bugs and feature requests through the project page.

New Features in 0.4:
====================
Added an application icon.
Added the ability to have more than one type of car controller.
Added a time factor to the user interface to control the speed of the
real-time simulation.
Updated formatting of clock display.
Added a capacity setting for elevator cars.
Added animation of people getting on and off cars.
Added sensor behaviour for car doors.
Added an averaging feature for multiple simulations.  This makes the
random seed setting more useful.
Added additional error dialogs when the application encounters errors.
Many, many bug fixes.


New Features in 0.3:
====================
An ability to run blocks of simulations so results can be compared.
Basically, the application will run simulations outside of the
real-time clock.  The simulation increments each value by the
specified step value until every combination is tested.  A table of
results is produced that can show comparisons with two of the
ranges of parameters.
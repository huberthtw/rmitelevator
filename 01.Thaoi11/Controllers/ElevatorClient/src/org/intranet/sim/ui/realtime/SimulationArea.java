/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.sim.ui.realtime;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.intranet.sim.Model;
import org.intranet.sim.SimulationApplication;
import org.intranet.sim.Simulator;
import org.intranet.sim.Simulator.SimulatorListener;
import org.intranet.sim.clock.Clock;
import org.intranet.sim.clock.RealTimeClock;
import org.intranet.ui.InputPanel;
import org.intranet.ui.SingleValueInputPanel;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class SimulationArea
  extends JComponent
{
  private JButton startButton = new JButton();
  private Simulator sim;
  private JComponent bView;
  private Statistics statistics;
  private JComponent leftPane = new JPanel();
  private JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  private JPanel bottomPanel = new JPanel(new BorderLayout());
  ClockDisplay clockDisplay = new ClockDisplay();

  private EventQueueDisplay eventQueueDisplay;

  public SimulationArea(Simulator simulator, SimulationApplication simApp)
  {
    super();
    sim = simulator;
    
    setLayout(new BorderLayout());

    createRightPane();

    createBottomPanel();

    createLeftPane(simApp);

    sim.addListener(new SimulatorListener()
    {
      public void modelUpdate(long time)
      {
        // TODO : The model should be responsible for telling the view when it updates
        bView.repaint();
      }
    });
  }

  private void createLeftPane(final SimulationApplication simApp)
  {
    leftPane.setLayout(new BorderLayout());
    SingleValueInputPanel ip = new SingleValueInputPanel(sim.getParameters(),
        new InputPanel.Listener()
    {
      public void parametersApplied()
      {
        sim.initialize(new RealTimeClock.RealTimeClockFactory());
        reconfigureSimulation(simApp);
      }
    });
    leftPane.add(ip, BorderLayout.NORTH);
    statistics = new Statistics();
    leftPane.add(statistics, BorderLayout.CENTER);
    
    add(leftPane, BorderLayout.WEST);
  }

  private void createRightPane()
  {
    add(rightSplitPane, BorderLayout.CENTER);
    rightSplitPane.setResizeWeight(1.0);
    rightSplitPane.setDividerLocation(425);
    eventQueueDisplay = new EventQueueDisplay();
    rightSplitPane.setRightComponent(eventQueueDisplay);
  }

  private void createBottomPanel()
  {
    JPanel timeFactorPanel = new JPanel();

    JLabel timeFactorLabel = new JLabel("Time Factor");

    final SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(0, -20, 20, 1);
    JSpinner timeFactor = new JSpinner(spinnerNumberModel);
    spinnerNumberModel.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        if (!sim.isInitializied()) return;
        
        int factor = ((Number)spinnerNumberModel.getValue()).intValue();
        RealTimeClock rtClock = (RealTimeClock)sim.getClock();
        synchronized (rtClock)
        {
          rtClock.setTimeConversion(factor);
        }
      }
    });
    
    timeFactorPanel.add(timeFactorLabel);
    timeFactorPanel.add(timeFactor);
    bottomPanel.add(timeFactorPanel, BorderLayout.EAST);

    JPanel startButtonPanel = new JPanel();
    startButtonPanel.add(startButton);
    bottomPanel.add(startButtonPanel, BorderLayout.CENTER);
    startButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        Clock clock = sim.getClock();
        synchronized (clock)
        {
          if (clock.isRunning())
          {
            clock.pause();
          }
          else
          {
            int factor = ((Number)spinnerNumberModel.getValue()).intValue();
            ((RealTimeClock)clock).setTimeConversion(factor);
            clock.start();
          }
        }
      }
    });
    startButton.setEnabled(false);
    add(bottomPanel, BorderLayout.SOUTH);
    updateButtonText(false);
    bottomPanel.add(clockDisplay, BorderLayout.WEST);
  }

  private void updateButtonText(boolean running)
  {
    startButton.setText(running ? "Pause" : "Go, Dude!");
  }

  public void paint(Graphics g)
  {
    Model model = sim.getModel();
    if (model == null)
    {
      super.paint(g);
    }
    else
      synchronized(model)
      {
        super.paint(g); 
      }
  }

  private void reconfigureSimulation(final SimulationApplication simApp)
  {
    startButton.setEnabled(true);
    bView = simApp.createView(sim.getModel());
    rightSplitPane.setLeftComponent(bView);

    final Clock clock = sim.getClock();
    clock.addListener(new Clock.Listener()
    {
      public void timeUpdate(long time)
      { }
      public void stateUpdate(boolean running)
      {
        updateButtonText(running);
        if (!running)
          statistics.updateStatistics();
      }
    });

    clockDisplay.setClock(sim.getClock());
    eventQueueDisplay.initialize(sim.getEventQueue());

    statistics.setModel(sim.getModel());
    validate();
  }

  public void dispose()
  {
    // If the sim has not been initialized, we can't get a clock (and also don't
    // really need to worry about disposing of anything).  bView will be set
    // by this component shortly after calling initialize on the simulator,
    // so we can check its value.
    if (bView == null)
      return;
    Clock clock = sim.getClock();
    if (clock.isRunning())
      clock.pause();
  }
}

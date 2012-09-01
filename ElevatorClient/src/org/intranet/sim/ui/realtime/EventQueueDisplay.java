/*
 * Copyright 2004 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.sim.ui.realtime;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.intranet.sim.event.Event;
import org.intranet.sim.event.EventQueue;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class EventQueueDisplay extends JScrollPane
{
  private JList eventJList = new JList();
  private EventListModel eventListModel = new EventListModel();

  public EventQueueDisplay()
  {
    super();
    getViewport().setView(eventJList);
    eventJList.setModel(eventListModel);
  }
  
  public void initialize(EventQueue eQ)
  {
    eventListModel.setEventQueue(eQ);
  }

  private static class EventListModel extends AbstractListModel
                               implements EventQueue.Listener
  {
    private EventQueue eventQueue;
    private List list;
    
    public EventListModel()
    {
      super();
    }
    
    public void setEventQueue(EventQueue eQ)
    {
      if (eventQueue != null)
      {
        list = eventQueue.getEventList();
        final int oldSize = list.size() - 1;
        if (oldSize >= 0)
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              fireIntervalRemoved(EventListModel.this, 0, oldSize);
            }
          });
        }
      }
      eventQueue = eQ;
      eventQueue.addListener(this);
      list = eventQueue.getEventList();
      final int newSize = list.size() - 1;
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          fireIntervalAdded(EventListModel.this, 0, newSize);
        }
      });
    }

    public Object getElementAt(int index)
    {
      if (index >= list.size()) return null;
      return list.get(index);
    }

    public int getSize()
    {
      if (list == null)
        return 0;
      int size = list.size();
      return size;
    }

    public void eventAdded(Event e)
    {
      list = eventQueue.getEventList();
      final int index = list.indexOf(e);
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          fireIntervalAdded(EventListModel.this, index, index);
        }
      });
    }

    public void eventRemoved(Event e)
    {
      final int index = list.indexOf(e);
      list = eventQueue.getEventList();
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          fireIntervalRemoved(EventListModel.this, index, index);
        }
      });
    }

    public void eventError(Exception ex) {}
  }
}

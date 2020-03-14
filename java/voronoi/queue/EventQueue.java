package voronoi.queue;

import java.util.Collection;
import java.util.PriorityQueue;

/**
 * @author Willem Paul
 */
public class EventQueue extends PriorityQueue<Event>
{
	public EventQueue(Collection<? extends Event> sites)
	{
		super(sites);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		PriorityQueue<Event> events = new PriorityQueue<>(this);

		builder.append("Event Queue:\t[");
		while (!events.isEmpty())
		{
			if (events.size() > 1)
				builder.append(events.poll()).append(", ");
			else
				builder.append(events.poll());
		}
		builder.append(']');

		return builder.toString();
	}
}
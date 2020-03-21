package voronoi.queue;

import auxiliary.Point;

/**
 * @author Willem Paul
 */
public class Event implements Comparable<Event>
{
	private Point coordinates;

	public Event(Point coordinates)
	{
		this.coordinates = coordinates;
	}

	public Point getCoordinates()
	{
		return coordinates;
	}

	@Override
	public int compareTo(Event event)
	{
		return event.coordinates.compareTo(this.coordinates);
	}

	@Override
	public String toString()
	{
		return "S" + coordinates.toString();
	}
}
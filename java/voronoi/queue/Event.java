package voronoi.queue;

import auxiliary.Point;

import java.util.Objects;

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
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Event event = (Event) o;
		return coordinates.equals(event.coordinates);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(coordinates);
	}

	@Override
	public String toString()
	{
		return "S" + coordinates.toString();
	}
}
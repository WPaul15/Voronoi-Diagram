package voronoi.queue;

import voronoi.Point;
import voronoi.tree.Arc;

public class CircleEvent extends Event
{
	private Arc arc;

	public CircleEvent(Point coordinates, Arc arc)
	{
		super(coordinates);
		this.arc = arc;
	}

	public Arc getArc()
	{
		return arc;
	}

	public void setArc(Arc arc)
	{
		this.arc = arc;
	}

	@Override
	public String toString()
	{
		return "C" + getCoordinates().toString();
	}
}

package voronoi.queue;

import auxiliary.Point;
import voronoi.tree.ArcSegment;

public class CircleEvent extends Event
{
	private ArcSegment disappearingArcSegment;

	public CircleEvent(Point coordinates, ArcSegment disappearingArcSegment)
	{
		super(coordinates);
		this.disappearingArcSegment = disappearingArcSegment;
	}

	public ArcSegment getDisappearingArcSegment()
	{
		return disappearingArcSegment;
	}

	public void setDisappearingArcSegment(ArcSegment disappearingArcSegment)
	{
		this.disappearingArcSegment = disappearingArcSegment;
	}

	@Override
	public String toString()
	{
		return "C" + getCoordinates().toString();
	}
}

package voronoi.queue;

import auxiliary.Circle;
import voronoi.tree.ArcSegment;

public class CircleEvent extends Event
{
	private Circle circle;
	private ArcSegment disappearingArcSegment;

	public CircleEvent(Circle circle, ArcSegment disappearingArcSegment)
	{
		super(circle.getBottomPoint());
		this.circle = circle;
		this.disappearingArcSegment = disappearingArcSegment;
	}

	public Circle getCircle()
	{
		return circle;
	}

	public ArcSegment getDisappearingArcSegment()
	{
		return disappearingArcSegment;
	}

	@Override
	public String toString()
	{
		return "C" + getCoordinates().toString();
	}
}

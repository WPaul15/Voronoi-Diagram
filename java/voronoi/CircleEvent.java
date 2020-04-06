package voronoi;

import auxiliary.Circle;
import auxiliary.Point;
import voronoi.tree.ArcSegment;

public class CircleEvent extends Point
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
}

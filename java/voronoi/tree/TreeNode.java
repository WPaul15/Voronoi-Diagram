package voronoi.tree;

import voronoi.Point;
import voronoi.dcel.DCELEdge;
import voronoi.queue.CircleEvent;

public class TreeNode
{
	private Point arc;
	private Point leftArc, rightArc;
	private CircleEvent circleEvent;
	private DCELEdge tracedEdge;

	public TreeNode(Point arc)
	{
		this.arc = arc;
		this.circleEvent = null;
		this.leftArc = null;
		this.rightArc = null;
		this.tracedEdge = null;
	}

	public TreeNode(Point leftArc, Point rightArc)
	{
		this.arc = null;
		this.circleEvent = null; // TODO Generate reference to correct circle event
		this.leftArc = leftArc;
		this.rightArc = rightArc;
		this.tracedEdge = null; // TODO Generate reference to correct Voronoi edge
	}

	public double getBreakpointX(int sweepLinePos)
	{
		/* Calculate left parabola */
		int x = leftArc.getX();
		int y = leftArc.getY();
		double denominator = (2 * (y - sweepLinePos));

		double a1 = 1 / denominator;
		double b1 = (-2 * x) / denominator;
		double c1 = (x * x + y * y - sweepLinePos * sweepLinePos) / denominator;

		/* Calculate right parabola */
		x = rightArc.getX();
		y = rightArc.getY();
		denominator = (2 * (y - sweepLinePos));

		double a2 = 1 / denominator;
		double b2 = (-2 * x) / denominator;
		double c2 = (x * x + y * y - sweepLinePos * sweepLinePos) / denominator;

		/* Calculate intersection point */
		double a = a1 - a2;
		double b = b1 - b2;
		double c = c1 - c2;

		int sign = leftArc.getY() > rightArc.getY() ? -1 : 1;
		double discriminant = b * b - 4 * a * c;
		double breakpointX = 0;

		/* Correct small negative determinants */
		if (discriminant <= 0) breakpointX = -b / (2 * a);
		else breakpointX = (-b + sign * Math.sqrt(discriminant)) / (2 * a);

		return breakpointX;
	}

	public Point getArc()
	{
		return arc;
	}

	public Point getLeftArc()
	{
		return leftArc;
	}

	public Point getRightArc()
	{
		return rightArc;
	}

	public CircleEvent getCircleEvent()
	{
		return circleEvent;
	}

	public void setCircleEvent(CircleEvent circleEvent)
	{
		this.circleEvent = circleEvent;
	}

	public DCELEdge getTracedEdge()
	{
		return tracedEdge;
	}

	public void setTracedEdge(DCELEdge tracedEdge)
	{
		this.tracedEdge = tracedEdge;
	}

	@Override
	public String toString()
	{
		return arc.toString();
	}
}

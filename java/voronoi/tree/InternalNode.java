package voronoi.tree;

import voronoi.Point;
import voronoi.VoronoiDiagram;
import voronoi.dcel.DCELEdge;

/**
 * @author Willem Paul
 */
public class InternalNode extends TreeNode
{
	private Point leftArc, rightArc;
	private DCELEdge tracedEdge;
	private double cachedSweepLinePos;
	private Point cachedBreakpoint;

	public InternalNode(Point leftArc, Point rightArc, DCELEdge tracedEdge)
	{
		this.leftArc = leftArc;
		this.rightArc = rightArc;
		this.tracedEdge = tracedEdge;
	}

	@Override
	protected Point getPoint()
	{
		/* If the sweep line is at the same position, there's no need to recalculate the breakpoint */
		if (VoronoiDiagram.getSweepLinePos() == cachedSweepLinePos)
			return cachedBreakpoint;

		cachedSweepLinePos = VoronoiDiagram.getSweepLinePos();

		/* Calculate left parabola */
		double x = leftArc.getX();
		double y = leftArc.getY();
		double denominator = (2 * (y - cachedSweepLinePos));

		double a1 = 1 / denominator;
		double b1 = (-2 * x) / denominator;
		double c1 = (x * x + y * y - cachedSweepLinePos * cachedSweepLinePos) / denominator;

		/* Calculate right parabola */
		x = rightArc.getX();
		y = rightArc.getY();
		denominator = (2 * (y - cachedSweepLinePos));

		double a2 = 1 / denominator;
		double b2 = (-2 * x) / denominator;
		double c2 = (x * x + y * y - cachedSweepLinePos * cachedSweepLinePos) / denominator;

		/* Calculate intersection point */
		double a = a1 - a2;
		double b = b1 - b2;
		double c = c1 - c2;

		int sign = leftArc.getY() > rightArc.getY() ? -1 : 1;
		double discriminant = b * b - 4 * a * c;
		double breakpointX;

		/* Correct small negative discriminants */
		if (discriminant <= 0) breakpointX = -b / (2 * a);
		else breakpointX = (-b + sign * Math.sqrt(discriminant)) / (2 * a);

		cachedBreakpoint = new Point(breakpointX, 0);
		return cachedBreakpoint;
	}

	public Point getLeftArc()
	{
		return leftArc;
	}

	public Point getRightArc()
	{
		return rightArc;
	}

	public DCELEdge getTracedEdge()
	{
		return tracedEdge;
	}

	@Override
	public String toString()
	{
		return "[" + leftArc.toString() + ", " + rightArc.toString() + "]";
	}
}

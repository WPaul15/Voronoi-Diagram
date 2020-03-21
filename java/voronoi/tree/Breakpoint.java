package voronoi.tree;

import auxiliary.Point;
import voronoi.VoronoiDiagram;
import voronoi.dcel.DCELEdge;

/**
 * @author Willem Paul
 */
public class Breakpoint
{
	private Point leftArcSegment, rightArcSegment;
	private DCELEdge tracedEdge;
	private double cachedSweepLinePos;
	private Point cachedBreakpoint;

	public Breakpoint(Point leftArcSegment, Point rightArcSegment, DCELEdge tracedEdge)
	{
		this.leftArcSegment = leftArcSegment;
		this.rightArcSegment = rightArcSegment;
		this.tracedEdge = tracedEdge;
		cachedSweepLinePos = Double.MIN_VALUE;
		cachedBreakpoint = null;
	}

	/**
	 * Calculates the x-coordinate of the breakpoint. All {@code Point} objects returned by this method have a
	 * y-coordinate of 0, as the y-coordinate is irrelevant to searching the status tree.
	 *
	 * @return A {@code Point} representing the x-coordinate of the breakpoint.
	 */
	public Point getCoordinates()
	{
		/* If the sweep line is at the same position, there's no need to recalculate the breakpoint */
		double currentSweepLinePos = VoronoiDiagram.getSweepLinePos();
		if (currentSweepLinePos == cachedSweepLinePos && cachedBreakpoint != null)
			return cachedBreakpoint;

		cachedSweepLinePos = currentSweepLinePos;

		/* Handle new site point case (degenerate parabola) */
		if (leftArcSegment.getY() == currentSweepLinePos)
		{
			cachedBreakpoint = new Point(leftArcSegment.getX(), 0);
			return cachedBreakpoint;
		}
		else if (rightArcSegment.getY() == currentSweepLinePos)
		{
			cachedBreakpoint = new Point(rightArcSegment.getX(), 0);
			return cachedBreakpoint;
		}

		/* Calculate left parabola */
		double x = leftArcSegment.getX();
		double y = leftArcSegment.getY();
		double denominator = (2 * (y - currentSweepLinePos));

		double a1 = 1 / denominator;
		double b1 = (-2 * x) / denominator;
		double c1 = (x * x + y * y - currentSweepLinePos * currentSweepLinePos) / denominator;

		/* Calculate right parabola */
		x = rightArcSegment.getX();
		y = rightArcSegment.getY();
		denominator = (2 * (y - currentSweepLinePos));

		double a2 = 1 / denominator;
		double b2 = (-2 * x) / denominator;
		double c2 = (x * x + y * y - currentSweepLinePos * currentSweepLinePos) / denominator;

		/* Calculate intersection point */
		double a = a1 - a2;
		double b = b1 - b2;
		double c = c1 - c2;

		int sign = leftArcSegment.getY() > rightArcSegment.getY() ? -1 : 1;
		double discriminant = b * b - 4 * a * c;
		double breakpointX;

		/* Correct small negative discriminants */
		if (discriminant <= 0) breakpointX = -b / (2 * a);
		else breakpointX = (-b + sign * Math.sqrt(discriminant)) / (2 * a);

		cachedBreakpoint = new Point(breakpointX, 0);
		return cachedBreakpoint;
	}

	public Point getLeftArcSegment()
	{
		return leftArcSegment;
	}

	public Point getRightArcSegment()
	{
		return rightArcSegment;
	}

	public DCELEdge getTracedEdge()
	{
		return tracedEdge;
	}
}
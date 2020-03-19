package voronoi.tree;

import voronoi.Point;

/**
 * @author Willem Paul
 */
public class Arc implements Comparable<Arc>
{
	private Point site;
	private Breakpoint leftBreakpoint, rightBreakpoint;

	public Arc(Point site)
	{
		this.site = site;
		this.leftBreakpoint = null;
		this.rightBreakpoint = null;
	}

	public Arc(Point site, Breakpoint leftBreakpoint, Breakpoint rightBreakpoint)
	{
		if (leftBreakpoint == null && rightBreakpoint == null)
			throw new IllegalArgumentException("At least one breakpoint must be defined.");
		this.site = site;
		this.leftBreakpoint = leftBreakpoint;
		this.rightBreakpoint = rightBreakpoint;
	}

	public Point getSite()
	{
		return site;
	}

	public Breakpoint getLeftBreakpoint()
	{
		return leftBreakpoint;
	}

	public Breakpoint getRightBreakpoint()
	{
		return rightBreakpoint;
	}

	@Override
	public int compareTo(Arc compareTo)
	{
		if (this.getLeft().getX() == compareTo.getLeft().getX() && this.getRight().getX() == compareTo.getRight().getX())
			return 0;
		if (this.getLeft().getX() >= compareTo.getRight().getX())
			return 1;
		if (this.getRight().getX() <= compareTo.getLeft().getX())
			return -1;

		/* Handle the case when a site event appears directly below a breakpoint */
		return Point.midpoint(this.getLeft(), this.getRight()).compareTo(Point.midpoint(compareTo.getLeft(), compareTo.getRight()));
	}

	@Override
	public String toString()
	{
		return "[" + site.toString() + "]";
	}

	private Point getLeft()
	{
		if (leftBreakpoint == null)
			return new Point(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return leftBreakpoint.getCoordinates();
	}

	private Point getRight()
	{
		if (rightBreakpoint == null)
			return new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		return rightBreakpoint.getCoordinates();
	}
}
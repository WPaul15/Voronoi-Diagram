package voronoi.tree;

import auxiliary.MathOps;
import auxiliary.Point;
import voronoi.SiteEvent;

/**
 * @author Willem Paul
 */
public class ArcSegment implements Comparable<ArcSegment>
{
	private final SiteEvent site;
	private Breakpoint leftBreakpoint, rightBreakpoint;

	public ArcSegment(SiteEvent site)
	{
		this.site = site;
		this.leftBreakpoint = null;
		this.rightBreakpoint = null;
	}

	public ArcSegment(SiteEvent site, Breakpoint leftBreakpoint, Breakpoint rightBreakpoint)
	{
		if (leftBreakpoint == null && rightBreakpoint == null)
			throw new IllegalArgumentException("At least one breakpoint must be defined.");
		this.site = site;
		this.leftBreakpoint = leftBreakpoint;
		this.rightBreakpoint = rightBreakpoint;
	}

	public SiteEvent getSite()
	{
		return site;
	}

	public Breakpoint getLeftBreakpoint()
	{
		return leftBreakpoint;
	}

	public void setLeftBreakpoint(Breakpoint leftBreakpoint)
	{
		this.leftBreakpoint = leftBreakpoint;
	}

	public Breakpoint getRightBreakpoint()
	{
		return rightBreakpoint;
	}

	public void setRightBreakpoint(Breakpoint rightBreakpoint)
	{
		this.rightBreakpoint = rightBreakpoint;
	}

	@Override
	public int compareTo(ArcSegment compareTo)
	{
		Point thisLeft = this.getLeft();
		Point thisRight = this.getRight();
		Point compareToLeft = compareTo.getLeft();
		Point compareToRight = compareTo.getRight();

		/* Handle tree queries */
		if (this.getClass() == TreeQuery.class &&
				(thisLeft.getX() >= compareToLeft.getX() && thisRight.getX() <= compareToRight.getX()))
		{
			return 0;
		}

		if (thisLeft.getX() == compareToLeft.getX() && thisRight.getX() == compareToRight.getX())
			return 0;
		if (thisLeft.getX() >= compareToRight.getX())
			return 1;
		if (thisRight.getX() <= compareToLeft.getX())
			return -1;

		return MathOps.midpoint(thisLeft, thisRight).compareTo(MathOps.midpoint(compareToLeft, compareToRight));
	}

	@Override
	public String toString()
	{
		return "[" + site.toString() + "]";
	}

	protected Point getLeft()
	{
		if (leftBreakpoint == null)
			return new Point(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return leftBreakpoint.getCoordinates();
	}

	protected Point getRight()
	{
		if (rightBreakpoint == null)
			return new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		return rightBreakpoint.getCoordinates();
	}
}

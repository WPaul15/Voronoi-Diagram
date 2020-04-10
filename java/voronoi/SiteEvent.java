package voronoi;

import auxiliary.Point;

/**
 * @author Willem Paul
 */
public class SiteEvent extends Point
{
	private static int eventIndex = 0;

	private final int index;
	private final VoronoiCell cell;

	public SiteEvent(double x, double y)
	{
		super(x, y);
		this.index = ++eventIndex;
		this.cell = new VoronoiCell(this, this.index, null);
	}

	public int getIndex()
	{
		return index;
	}

	public VoronoiCell getCell()
	{
		return cell;
	}
}

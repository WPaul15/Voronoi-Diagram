package voronoi.tree;

import auxiliary.Point;

/**
 * @author Willem Paul
 */
public class TreeQuery extends ArcSegment
{
	private Point site;

	public TreeQuery(Point site)
	{
		super(site);
		this.site = site;
	}

	@Override
	protected Point getLeft()
	{
		return site;
	}

	@Override
	protected Point getRight()
	{
		return site;
	}
}
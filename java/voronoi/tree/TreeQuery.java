package voronoi.tree;

import voronoi.Point;

/**
 * @author Willem Paul
 */
public class TreeQuery extends Arc
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
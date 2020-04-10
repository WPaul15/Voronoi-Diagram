package voronoi.tree;

import auxiliary.Point;
import voronoi.SiteEvent;

/**
 * @author Willem Paul
 */
public class TreeQuery extends ArcSegment
{
	private final SiteEvent site;

	public TreeQuery(SiteEvent site)
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

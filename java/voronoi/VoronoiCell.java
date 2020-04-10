package voronoi;

import auxiliary.Point;
import dcel.DCELEdge;
import dcel.DCELFace;

/**
 * @author Willem Paul
 */
public class VoronoiCell extends DCELFace
{
	private final Point site;

	public VoronoiCell(Point site, int index, DCELEdge outerComponent, DCELEdge... innerComponents)
	{
		super(index, outerComponent, innerComponents);
		this.site = site;
	}

	public Point getSite()
	{
		return site;
	}

	@Override
	public String getName()
	{
		return getOuterComponent() == null ? "uf" : "c" + getIndex();
	}
}

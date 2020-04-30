package voronoi;

import auxiliary.Point;
import dcel.DCELFace;
import dcel.DCELVertex;

/**
 * @author Willem Paul
 */
public class SiteEvent extends Point
{
	private static int eventIndex = 0;

	private final DCELFace cell;

	public SiteEvent(double x, double y)
	{
		super(x, y);
		this.cell = new DCELFace(new DCELVertex(DCELVertex.VertexType.DELAUNAY_VERTEX, this), DCELFace.FaceType.VORONOI_CELL, ++eventIndex, null);
	}

	public DCELFace getCell()
	{
		return cell;
	}
}

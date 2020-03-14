package voronoi.dcel;

import voronoi.Point;

/**
 * @author Willem Paul
 */
public class DCELVertex
{
	public enum VertexType
	{
		VORONOI_VERTEX,
		BOUNDING_VERTEX
	}

	private int index;
	private VertexType type;
	private Point coordinates;
	private DCELEdge incidentEdge;

	public DCELVertex(int index, Point coordinates, DCELEdge incidentEdge)
	{
		this.index = index;
		this.type = VertexType.VORONOI_VERTEX;
		this.coordinates = coordinates;
		this.incidentEdge = incidentEdge;
	}

	public DCELVertex(int index, VertexType type, Point coordinates)
	{
		this.index = index;
		this.type = type;
		this.coordinates = coordinates;
		this.incidentEdge = null;
	}

	public String getName()
	{
		if (type == VertexType.BOUNDING_VERTEX) return "b" + index;
		else return "v" + index;
	}

	public int getIndex()
	{
		return index;
	}

	public Point getCoordinates()
	{
		return coordinates;
	}

	public DCELEdge getIncidentEdge()
	{
		return incidentEdge;
	}

	public void setIncidentEdge(DCELEdge incidentEdge)
	{
		this.incidentEdge = incidentEdge;
	}

	@Override
	public String toString()
	{
		return getName() + "  " + coordinates.toString() + "  " + incidentEdge.toString();
	}
}
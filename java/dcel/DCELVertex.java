package dcel;

import auxiliary.Point;

/**
 * @author Willem Paul
 */
public class DCELVertex
{
	private static int voronoiVertexIndex = 0;
	private static int delaunayVertexIndex = 0;

	public DCELVertex(Point coordinates, DCELEdge incidentEdge)
	{
		this.index = ++voronoiVertexIndex;
		this.type = VertexType.VORONOI_VERTEX;
		this.coordinates = coordinates;
		this.incidentEdge = incidentEdge;
	}

	private static int boundingVertexIndex = 0;

	private final int index;
	private final VertexType type;
	private final Point coordinates;
	private DCELEdge incidentEdge;

	public DCELVertex(VertexType type, Point coordinates)
	{
		if (type == VertexType.VORONOI_VERTEX)
			this.index = ++voronoiVertexIndex;
		else if (type == VertexType.DELAUNAY_VERTEX)
			this.index = ++delaunayVertexIndex;
		else
			this.index = ++boundingVertexIndex;
		this.type = type;
		this.coordinates = coordinates;
		this.incidentEdge = null;
	}

	public DCELVertex(VertexType type, Point coordinates, DCELEdge incidentEdge)
	{
		if (type == VertexType.VORONOI_VERTEX)
			this.index = ++voronoiVertexIndex;
		else if (type == VertexType.DELAUNAY_VERTEX)
			this.index = ++delaunayVertexIndex;
		else
			this.index = ++boundingVertexIndex;
		this.type = type;
		this.coordinates = coordinates;
		this.incidentEdge = incidentEdge;
	}

	public boolean isDelaunayVertex()
	{
		return type == VertexType.DELAUNAY_VERTEX;
	}

	public boolean isVoronoiVertex()
	{
		return type == VertexType.VORONOI_VERTEX;
	}

	public boolean isBoundingVertex()
	{
		return type == VertexType.BOUNDING_VERTEX;
	}

	public String getName()
	{
		if (isVoronoiVertex()) return "v" + index;
		else if (isDelaunayVertex()) return "p" + index;
		else return "b" + index;
	}

	public DCELEdge getLastIncomingEdge()
	{
		DCELEdge edge = incidentEdge.getTwin();

		if (edge.getNext() == null) return edge;

		while (edge.getNext() != incidentEdge)
		{
			edge = edge.getNext().getTwin();
		}
		return edge;
	}

	public int getIndex()
	{
		return index;
	}

	public VertexType getType()
	{
		return type;
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

	public DCELEdge getLastOutgoingEdge()
	{
		DCELEdge edge = incidentEdge;

		if (edge.getPrev() == null) return edge;

		while (edge.getPrev() != incidentEdge.getTwin())
		{
			edge = edge.getPrev().getTwin();
		}
		return edge;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append(getName()).append("  ").append(coordinates.toString());
		if (incidentEdge != null) builder.append("  ").append(incidentEdge.getName());

		return builder.toString();
	}

	public enum VertexType
	{
		VORONOI_VERTEX,
		DELAUNAY_VERTEX,
		BOUNDING_VERTEX
	}
}

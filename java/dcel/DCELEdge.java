package dcel;

/**
 * @author Willem Paul
 */
public class DCELEdge
{
	public enum EdgeType
	{
		VORONOI_EDGE,
		DELAUNAY_EDGE,
		BOUNDING_EDGE
	}

	private final String name;
	private final EdgeType type;
	private DCELVertex origin;
	private DCELFace incidentFace;
	private DCELEdge twin, next, prev;

	/* Store the vector representing the edge's direction for easier calculations later */
	private double[] direction;

	public DCELEdge(EdgeType type)
	{
		this.name = "";
		this.type = type;
		this.origin = null;
		this.twin = null;
		this.incidentFace = null;
		this.next = null;
		this.prev = null;
		this.direction = new double[2];
	}

	public DCELEdge(EdgeType type, DCELVertex origin)
	{
		this.name = "";
		this.type = type;
		this.origin = origin;
		this.twin = null;
		this.incidentFace = null;
		this.next = null;
		this.prev = null;
		this.direction = new double[2];
	}

	public DCELEdge(EdgeType type, DCELEdge twin)
	{
		this.name = "";
		this.type = type;
		this.origin = null;
		this.twin = twin;
		this.incidentFace = null;
		this.next = null;
		this.prev = null;
		this.direction = new double[2];

		twin.twin = this;
	}

	public DCELEdge(EdgeType type, DCELVertex origin, DCELEdge twin)
	{
		this.name = "";
		this.type = type;
		this.origin = origin;
		this.twin = twin;
		this.incidentFace = null;
		this.next = null;
		this.prev = null;
		this.direction = new double[2];

		twin.twin = this;
	}

	public boolean isVoronoiEdge()
	{
		return type == EdgeType.VORONOI_EDGE;
	}

	public boolean isDelaunayEdge()
	{
		return type == EdgeType.DELAUNAY_EDGE;
	}

	public boolean isBoundingEdge()
	{
		return type == EdgeType.BOUNDING_EDGE;
	}

	public boolean isDirectedRight()
	{
		return direction[0] > 0;
	}

	public boolean isDirectedStraightUp()
	{
		if (direction[0] == 0)
			return direction[1] > 0;

		return false;
	}

	public String getName()
	{
		if (name.equals(""))
		{
			StringBuilder builder = new StringBuilder();
			builder.append('e');

			if (origin != null)
			{
//				if (isVoronoiEdge()) builder.append("ve");
//				else if (isDelaunayEdge()) builder.append("de");
//				else builder.append("be");
				if (origin.isBoundingVertex()) builder.append('b');
				builder.append(origin.getIndex());
			}
			if (twin != null && twin.origin != null)
			{
				builder.append(',');
				if (twin.origin.isBoundingVertex()) builder.append('b');
				builder.append(twin.origin.getIndex());
			}

			return builder.toString();
		}

		return name;
	}

	public DCELVertex getOrigin()
	{
		return origin;
	}

	public void setOrigin(DCELVertex origin)
	{
		this.origin = origin;
	}

	public DCELEdge getTwin()
	{
		return twin;
	}

	public void setTwin(DCELEdge twin)
	{
		this.twin = twin;
	}

	public DCELFace getIncidentFace()
	{
		return incidentFace;
	}

	public void setIncidentFace(DCELFace incidentFace)
	{
		this.incidentFace = incidentFace;
	}

	public DCELEdge getNext()
	{
		return next;
	}

	public void setNext(DCELEdge next)
	{
		this.next = next;
	}

	public DCELEdge getPrev()
	{
		return prev;
	}

	public void setPrev(DCELEdge prev)
	{
		this.prev = prev;
	}

	public double[] getDirection()
	{
		return direction;
	}

	public void setDirection(double[] direction)
	{
		this.direction = direction;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append(getName());
		if (origin != null) builder.append("  ").append(origin.getName());
		if (twin != null) builder.append("  ").append(twin.getName());
		if (incidentFace != null) builder.append("  ").append(incidentFace.getName());
		if (next != null) builder.append("  ").append(next.getName());
		if (prev != null) builder.append("  ").append(prev.getName());

		return builder.toString();
	}
}

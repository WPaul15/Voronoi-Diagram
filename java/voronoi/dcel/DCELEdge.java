package voronoi.dcel;

import auxiliary.LineVector;

/**
 * @author Willem Paul
 */
public class DCELEdge
{
	private String name;
	private DCELVertex origin;
	private DCELFace incidentFace;
	private DCELEdge twin, next, prev;

	/* Store the line along which this edge lies for easier calculations later */
	private LineVector lineVector;

	/**
	 * Constructs a new DCEL edge.
	 */
	public DCELEdge()
	{
		this.name = "";
		this.origin = null;
		this.twin = null;
		this.incidentFace = null;
		this.next = null;
		this.prev = null;
	}

	public DCELEdge(DCELVertex origin)
	{
		this.name = "";
		this.origin = origin;
		this.twin = null;
		this.incidentFace = null;
		this.next = null;
		this.prev = null;

		origin.setIncidentEdge(this);
	}

	/**
	 * Constructs a new DCEL edge whose twin is the given DCEL edge and sets the twin reference of the given edge to
	 * this edge.
	 *
	 * @param twin The twin edge of this edge
	 */
	public DCELEdge(DCELEdge twin)
	{
		this.name = "";
		this.origin = null;
		this.twin = twin;
		this.incidentFace = null;
		this.next = null;
		this.prev = null;

		twin.twin = this;
	}

	public DCELEdge(DCELVertex origin, DCELEdge twin)
	{
		this.name = "";
		this.origin = origin;
		this.twin = twin;
		this.incidentFace = null;
		this.next = null;
		this.prev = null;

		twin.twin = this;
	}

	public String getName()
	{
		if (name.equals(""))
		{
			StringBuilder builder = new StringBuilder();
			//builder.append('e');

			// For testing purposes; helps differentiate between the edges of the diagram and the edges along the bounding box
			if (origin != null)
			{
				if (origin.getType() == DCELVertex.VertexType.VORONOI_VERTEX || twin.origin.getType() == DCELVertex.VertexType.VORONOI_VERTEX)
					builder.append("ve");
				else
					builder.append("be");
				builder.append(origin.getIndex());
			}
			if (twin != null && twin.origin != null) builder.append(',').append(twin.origin.getIndex());

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

	public LineVector getLineVector()
	{
		return lineVector;
	}

	public void setLineVector(LineVector lineVector)
	{
		this.lineVector = lineVector;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append(getName());
		if (origin != null) builder.append("  ").append(origin.getName());
		if (twin != null) builder.append("  T:").append(twin.getName());
		//if (incidentFace != null) builder.append("  F:").append(incidentFace.getName());
		if (next != null) builder.append("  N:").append(next.getName());
		if (prev != null) builder.append("  P:").append(prev.getName());

		return builder.toString();

//		return name + "  " + origin.getName() + "  " + twin.getName() + "  " + incidentFace.getName() + "  " +
//				next.getName() + "  " + prev.getName();
	}
}
package voronoi.dcel;

import auxiliary.Line;

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
	private Line line;

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

	public String getName()
	{
		if (name.equals(""))
		{
			StringBuilder builder = new StringBuilder();
			builder.append('e');

			if (origin != null) builder.append(origin.getIndex());
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

	public Line getLine()
	{
		return line;
	}

	public void setLine(Line line)
	{
		this.line = line;
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

//		return name + "  " + origin.getName() + "  " + twin.getName() + "  " + incidentFace.getName() + "  " +
//				next.getName() + "  " + prev.getName();
	}
}
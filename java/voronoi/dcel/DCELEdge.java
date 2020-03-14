package voronoi.dcel;

/**
 * @author Willem Paul
 */
public class DCELEdge
{
	private String name;
	private DCELVertex origin;
	private DCELEdge twin;
	private DCELFace incidentFace;
	private DCELEdge next;
	private DCELEdge prev;

	public DCELEdge(DCELVertex origin, DCELFace incidentFace)
	{
		this.origin = origin;
		this.twin = null;
		this.incidentFace = incidentFace;
		this.next = null;
		this.prev = null;
	}

	public String getName()
	{
		return "e" + origin.getIndex() + "," + twin.getOrigin().getIndex();
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

	@Override
	public String toString()
	{
		return name + "  " + origin.getName() + "  " + twin.getName() + "  " + incidentFace.getName() + "  " +
				next.getName() + "  " + prev.getName();
	}
}
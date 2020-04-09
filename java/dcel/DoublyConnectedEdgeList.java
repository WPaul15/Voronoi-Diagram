package dcel;

import auxiliary.MathOps;
import auxiliary.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Willem Paul
 */
public class DoublyConnectedEdgeList
{
	private final List<DCELVertex> vertices;
	private final List<DCELEdge> edges;
	private final List<DCELFace> faces;
	private BoundingBox boundingBox;

	public DoublyConnectedEdgeList()
	{
		this.vertices = new ArrayList<>();
		this.edges = new ArrayList<>();
		this.faces = new ArrayList<>();
	}

	protected void computeBoundingBox()
	{
		double minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		double minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

		for (DCELVertex v : vertices)
		{
			double x = v.getCoordinates().getX();
			double y = v.getCoordinates().getY();

			if (x < minX) minX = x;
			if (x > maxX) maxX = x;

			if (y < minY) minY = y;
			if (y > maxY) maxY = y;
		}

		int boundingBoxPadding = 20;
		DCELVertex b1 = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX,
		                               new Point(minX - boundingBoxPadding, minY - boundingBoxPadding)); //Lower left
		DCELVertex b2 = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX,
		                               new Point(maxX + boundingBoxPadding, minY - boundingBoxPadding)); //Lower right
		DCELVertex b3 = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX,
		                               new Point(maxX + boundingBoxPadding, maxY + boundingBoxPadding)); //Upper right
		DCELVertex b4 = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX,
		                               new Point(minX - boundingBoxPadding, maxY + boundingBoxPadding)); //Upper left

		vertices.add(b1);
		vertices.add(b2);
		vertices.add(b3);
		vertices.add(b4);

		DCELEdge bottomInner = new DCELEdge(b1);
		DCELEdge bottomOuter = new DCELEdge(b2, bottomInner);
		DCELEdge rightInner = new DCELEdge(b2);
		DCELEdge rightOuter = new DCELEdge(b3, rightInner);
		DCELEdge topInner = new DCELEdge(b3);
		DCELEdge topOuter = new DCELEdge(b4, topInner);
		DCELEdge leftInner = new DCELEdge(b4);
		DCELEdge leftOuter = new DCELEdge(b1, leftInner);

		b1.setIncidentEdge(leftOuter);
		b2.setIncidentEdge(bottomOuter);
		b3.setIncidentEdge(rightOuter);
		b4.setIncidentEdge(topOuter);

		edges.add(bottomInner);
		edges.add(bottomOuter);
		edges.add(rightInner);
		edges.add(rightOuter);
		edges.add(topInner);
		edges.add(topOuter);
		edges.add(leftInner);
		edges.add(leftOuter);

		// TODO Move to face calculation method
		DCELFace uf = new DCELFace(0, null, bottomInner);

		faces.add(uf);

		bottomOuter.setIncidentFace(uf);
		rightOuter.setIncidentFace(uf);
		topOuter.setIncidentFace(uf);
		leftOuter.setIncidentFace(uf);

		bottomInner.setNext(rightInner);
		rightInner.setNext(topInner);
		topInner.setNext(leftInner);
		leftInner.setNext(bottomInner);

		bottomInner.setPrev(leftInner);
		leftInner.setPrev(topInner);
		topInner.setPrev(rightInner);
		rightInner.setPrev(bottomInner);

		bottomOuter.setNext(leftOuter);
		leftOuter.setNext(topOuter);
		topOuter.setNext(rightOuter);
		rightOuter.setNext(bottomOuter);

		bottomOuter.setPrev(rightOuter);
		rightOuter.setPrev(topOuter);
		topOuter.setPrev(leftOuter);
		leftOuter.setPrev(bottomOuter);

		this.boundingBox = new BoundingBox(b1, b2, b3, b4);
	}

	public List<DCELVertex> getVertices()
	{
		return vertices;
	}

	public List<DCELEdge> getEdges()
	{
		return edges;
	}

	public List<DCELFace> getFaces()
	{
		return faces;
	}

	public BoundingBox getBoundingBox()
	{
		return boundingBox;
	}

	public List<Point> getVerticesForDisplay()
	{
		List<Point> points = new ArrayList<>();
		for (DCELVertex vertex : vertices)
		{
			if (vertex.isVoronoiVertex())
				points.add(new Point(vertex.getCoordinates()));
		}
		return points;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		if (!vertices.isEmpty())
		{
			for (DCELVertex v : vertices)
			{
				builder.append(v.toString()).append("\n");
			}

			builder.append("\n\n");
		}

		if (!faces.isEmpty())
		{
			for (DCELFace f : faces)
			{
				builder.append(f.toString()).append("\n");
			}

			builder.append("\n\n");
		}

		if (!edges.isEmpty())
		{
			for (DCELEdge e : edges)
			{
				builder.append(e.toString()).append("\n");
			}
		}

		return builder.toString();
	}

	public static class BoundingBox
	{
		private final DCELVertex lowerLeft, lowerRight, upperRight, upperLeft;

		public BoundingBox(DCELVertex lowerLeft, DCELVertex lowerRight, DCELVertex upperRight, DCELVertex upperLeft)
		{
			this.lowerLeft = lowerLeft;
			this.lowerRight = lowerRight;
			this.upperRight = upperRight;
			this.upperLeft = upperLeft;
		}

		public DCELEdge getIntersectedEdge(Point p)
		{
			double x = p.getX();
			double y = p.getY();

			/* Traverse the left boundary */
			if (MathOps.thresholdEquals(x, lowerLeft.getCoordinates().getX()))
			{
				DCELEdge edge = lowerLeft.getIncidentEdge();
				while (y > edge.getTwin().getOrigin().getCoordinates().getY())
				{
					edge = edge.getNext();
				}
				return edge;
			}
			/* Traverse the right boundary */
			else if (MathOps.thresholdEquals(x, upperRight.getCoordinates().getX()))
			{
				DCELEdge edge = upperRight.getIncidentEdge();
				while (y < edge.getTwin().getOrigin().getCoordinates().getY())
				{
					edge = edge.getNext();
				}
				return edge;
			}

			/* Traverse the lower boundary */
			if (MathOps.thresholdEquals(y, lowerLeft.getCoordinates().getY()))
			{
				DCELEdge edge = lowerRight.getIncidentEdge();
				while (x < edge.getTwin().getOrigin().getCoordinates().getY())
				{
					edge = edge.getNext();
				}
				return edge;
			}
			/* Traverse the upper boundary */
			else if (MathOps.thresholdEquals(y, upperRight.getCoordinates().getY()))
			{
				DCELEdge edge = upperLeft.getIncidentEdge();
				while (x > edge.getTwin().getOrigin().getCoordinates().getY())
				{
					edge = edge.getNext();
				}
				return edge;
			}

			return null;
		}

		public DCELVertex getLowerLeft()
		{
			return lowerLeft;
		}

		public DCELVertex getUpperRight()
		{
			return upperRight;
		}
	}
}
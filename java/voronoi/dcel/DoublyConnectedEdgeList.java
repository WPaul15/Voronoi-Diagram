package voronoi.dcel;

import auxiliary.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Willem Paul
 */
public class DoublyConnectedEdgeList
{
	private List<DCELVertex> vertices;
	private List<DCELEdge> edges;
	private List<DCELFace> faces;
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

		edges.add(bottomInner);
		edges.add(bottomOuter);
		edges.add(rightInner);
		edges.add(rightOuter);
		edges.add(topInner);
		edges.add(topOuter);
		edges.add(leftInner);
		edges.add(leftOuter);

		//b1.setIncidentEdge(bottomInner);
		//b2.setIncidentEdge(rightInner);
		//b3.setIncidentEdge(topInner);
		//b4.setIncidentEdge(leftInner);

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

		this.boundingBox = new BoundingBox(bottomOuter, leftOuter, rightOuter, topOuter, b1, b3);
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
		private DCELEdge bottomOuter, leftOuter, rightOuter, topOuter;
		private DCELVertex lowerLeft, upperRight;

		public BoundingBox(DCELEdge bottomOuter, DCELEdge leftOuter, DCELEdge rightOuter, DCELEdge topOuter, DCELVertex lowerLeft, DCELVertex upperRight)
		{
			this.bottomOuter = bottomOuter;
			this.leftOuter = leftOuter;
			this.rightOuter = rightOuter;
			this.topOuter = topOuter;
			this.lowerLeft = lowerLeft;
			this.upperRight = upperRight;
		}

		public DCELEdge getIntersectedEdge(Point p)
		{
			double x = p.getX();
			double y = p.getY();

			if (x == leftOuter.getOrigin().getCoordinates().getX()) return leftOuter;
			if (x == rightOuter.getOrigin().getCoordinates().getX()) return rightOuter;
			if (y == bottomOuter.getOrigin().getCoordinates().getY()) return bottomOuter;
			if (y == topOuter.getOrigin().getCoordinates().getY()) return topOuter;

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
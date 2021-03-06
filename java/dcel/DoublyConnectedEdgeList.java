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
	protected final List<DCELVertex> vertices;
	protected final List<DCELEdge> edges;
	protected List<DCELFace> faces;
	protected final DCELFace unboundedFace;
	private BoundingBox boundingBox;

	public DoublyConnectedEdgeList()
	{
		this.vertices = new ArrayList<>();
		this.edges = new ArrayList<>();
		this.faces = new ArrayList<>();
		this.unboundedFace = new DCELFace(DCELFace.FaceType.UNBOUNDED, 0, null);
		faces.add(unboundedFace);
	}

	protected void computeBoundingBox()
	{
		double minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		double minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

		if (!vertices.isEmpty())
		{
			for (DCELVertex v : vertices)
			{
				double x = v.getCoordinates().getX();
				double y = v.getCoordinates().getY();

				if (x < minX) minX = x;
				if (x > maxX) maxX = x;

				if (y < minY) minY = y;
				if (y > maxY) maxY = y;
			}
		}
		else
		{
			minX = -180;
			maxX = 180;
			minY = -180;
			maxY = 180;
		}

		int boundingBoxPadding = 20;
		DCELVertex lowerLeft = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX,
		                                      new Point(minX - boundingBoxPadding, minY - boundingBoxPadding));
		DCELVertex lowerRight = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX,
		                                       new Point(maxX + boundingBoxPadding, minY - boundingBoxPadding));
		DCELVertex upperRight = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX,
		                                       new Point(maxX + boundingBoxPadding, maxY + boundingBoxPadding));
		DCELVertex upperrLeft = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX,
		                                       new Point(minX - boundingBoxPadding, maxY + boundingBoxPadding));

		vertices.add(lowerLeft);
		vertices.add(lowerRight);
		vertices.add(upperRight);
		vertices.add(upperrLeft);

		DCELEdge bottomInner = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, lowerLeft);
		DCELEdge bottomOuter = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, lowerRight, bottomInner);
		DCELEdge rightInner = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, lowerRight);
		DCELEdge rightOuter = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, upperRight, rightInner);
		DCELEdge topInner = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, upperRight);
		DCELEdge topOuter = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, upperrLeft, topInner);
		DCELEdge leftInner = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, upperrLeft);
		DCELEdge leftOuter = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, lowerLeft, leftInner);

		edges.add(bottomInner);
		edges.add(bottomOuter);
		edges.add(rightInner);
		edges.add(rightOuter);
		edges.add(topInner);
		edges.add(topOuter);
		edges.add(leftInner);
		edges.add(leftOuter);

		lowerLeft.setIncidentEdge(leftOuter);
		lowerRight.setIncidentEdge(bottomOuter);
		upperRight.setIncidentEdge(rightOuter);
		upperrLeft.setIncidentEdge(topOuter);

		bottomOuter.setIncidentFace(unboundedFace);
		rightOuter.setIncidentFace(unboundedFace);
		topOuter.setIncidentFace(unboundedFace);
		leftOuter.setIncidentFace(unboundedFace);

		unboundedFace.setInnerComponents(bottomOuter);

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

		this.boundingBox = new BoundingBox(lowerLeft, lowerRight, upperRight, upperrLeft);
	}

	protected void computeFaces()
	{
		int index = 0;

		for (DCELEdge edge : edges)
		{
			if (edge.getIncidentFace() == null)
			{
				DCELFace face = new DCELFace(null, ++index, edge);
				faces.add(face);

				edge.setIncidentFace(face);

				DCELEdge e = edge.getNext();
				while (e != edge)
				{
					e.setIncidentFace(face);
					e = e.getNext();
				}
			}
		}
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
		}

		if (!faces.isEmpty())
		{
			builder.append("\n\n");

			for (DCELFace f : faces)
			{
				builder.append(f.toString()).append("\n");
			}
		}

		if (!edges.isEmpty())
		{
			builder.append("\n\n");

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

		public void connectEdge(DoublyConnectedEdgeList dcel, Point origin, DCELEdge edge)
		{
			Point intersection = getIntersection(origin, edge.getDirection());

			/* Check to make sure the edge isn't intersecting a corner of the bounding box. */
			if (intersection.equals(lowerLeft.getCoordinates()))
			{
				connectEdgeToCorner(lowerLeft, edge);
				return;
			}
			else if (intersection.equals(lowerRight.getCoordinates()))
			{
				connectEdgeToCorner(lowerRight, edge);
				return;
			}
			else if (intersection.equals(upperRight.getCoordinates()))
			{
				connectEdgeToCorner(upperRight, edge);
				return;
			}
			else if (intersection.equals(upperLeft.getCoordinates()))
			{
				connectEdgeToCorner(upperLeft, edge);
				return;
			}

			DCELVertex vertex = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX, intersection, edge.getTwin());
			dcel.vertices.add(vertex);

			DCELEdge outerBoundingEdge = getIntersectedEdge(intersection);
			DCELEdge innerBoundingEdge = outerBoundingEdge.getTwin();
			DCELEdge newOuterBoundingEdge = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, innerBoundingEdge);
			DCELEdge newInnerBoundingEdge = new DCELEdge(DCELEdge.EdgeType.BOUNDING_EDGE, outerBoundingEdge);

			dcel.edges.add(newOuterBoundingEdge);
			dcel.edges.add(newInnerBoundingEdge);

			edge.getTwin().setOrigin(vertex);
			newOuterBoundingEdge.setOrigin(vertex);
			newInnerBoundingEdge.setOrigin(vertex);

			newOuterBoundingEdge.setIncidentFace(outerBoundingEdge.getIncidentFace());

			newOuterBoundingEdge.setNext(outerBoundingEdge.getNext());
			outerBoundingEdge.setNext(newOuterBoundingEdge);
			newInnerBoundingEdge.setNext(innerBoundingEdge.getNext());
			innerBoundingEdge.setNext(edge.getTwin());
			edge.setNext(newInnerBoundingEdge);

			newOuterBoundingEdge.setPrev(outerBoundingEdge);
			newOuterBoundingEdge.getNext().setPrev(newOuterBoundingEdge);
			newInnerBoundingEdge.setPrev(edge);
			newInnerBoundingEdge.getNext().setPrev(newInnerBoundingEdge);
			edge.getTwin().setPrev(innerBoundingEdge);
		}

		private void connectEdgeToCorner(DCELVertex corner, DCELEdge edge)
		{
			/* This works on the assumption that the incident edges of each corner vertex are the edges facing the unbounded face. */
			DCELEdge innerBoundingEdge = corner.getIncidentEdge().getTwin();

			edge.getTwin().setOrigin(corner);

			edge.setNext(innerBoundingEdge.getNext());
			innerBoundingEdge.getNext().setPrev(edge);

			innerBoundingEdge.setNext(edge.getTwin());
			edge.getTwin().setPrev(innerBoundingEdge);
		}

		public Point getIntersection(Point origin, double[] direction)
		{
			/* Handle vertical and horizontal lines */
			if (direction[0] == 0)
				return direction[1] > 0 ? new Point(origin.getX(), upperRight.getCoordinates().getY()) : new Point(origin.getX(), lowerLeft.getCoordinates().getY());
			else if (direction[1] == 0)
				return direction[0] > 0 ? new Point(upperRight.getCoordinates().getX(), origin.getY()) : new Point(lowerLeft.getCoordinates().getX(), origin.getY());

			double t = 0, newT = 0;
			Point intersection;

			if (direction[0] > 0)
				t = (upperRight.getCoordinates().getX() - origin.getX()) / direction[0];
			else if (direction[0] < 0)
				t = (lowerLeft.getCoordinates().getX() - origin.getX()) / direction[0];

			intersection = new Point(origin.getX() + t * direction[0], origin.getY() + t * direction[1]);

			if (direction[1] > 0)
				newT = (upperRight.getCoordinates().getY() - origin.getY()) / direction[1];
			else if (direction[1] < 0)
				newT = (lowerLeft.getCoordinates().getY() - origin.getY()) / direction[1];

			if (newT < t)
			{
				intersection.setX(origin.getX() + newT * direction[0]);
				intersection.setY(origin.getY() + newT * direction[1]);
			}

			return intersection;
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
				while (x < edge.getTwin().getOrigin().getCoordinates().getX())
				{
					edge = edge.getNext();
				}
				return edge;
			}
			/* Traverse the upper boundary */
			else if (MathOps.thresholdEquals(y, upperRight.getCoordinates().getY()))
			{
				DCELEdge edge = upperLeft.getIncidentEdge();
				while (x > edge.getTwin().getOrigin().getCoordinates().getX())
				{
					edge = edge.getNext();
				}
				return edge;
			}

			return null;
		}

		public DCELEdge getInnerEdge()
		{
			/* This works on the assumption that the incident edges of each corner vertex are the edges facing the unbounded face. */
			return lowerLeft.getIncidentEdge().getTwin();
		}
	}
}

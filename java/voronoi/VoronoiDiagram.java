package voronoi;

import auxiliary.Circle;
import auxiliary.Line;
import auxiliary.MathOps;
import auxiliary.Point;
import dcel.DCELEdge;
import dcel.DCELFace;
import dcel.DCELVertex;
import dcel.DoublyConnectedEdgeList;
import voronoi.tree.ArcSegment;
import voronoi.tree.Breakpoint;
import voronoi.tree.TreeQuery;

import java.util.*;

/**
 * @author Willem Paul
 */
public class VoronoiDiagram extends DoublyConnectedEdgeList
{
	private static double sweepLinePos = Double.MIN_VALUE;
	private static double firstSiteSweepLinePos = Double.MIN_VALUE;

	private final PriorityQueue<Point> queue;
	private final TreeMap<ArcSegment, CircleEvent> status;

	/**
	 * Constructs a Voronoi diagram from the given set of sites using Steven Fortune's line sweep algorithm.
	 *
	 * @param sites the list of sites for which to construct a Voronoi diagram
	 */
	public VoronoiDiagram(Set<SiteEvent> sites)
	{
		super();

		this.queue = new PriorityQueue<>(sites);
		this.status = new TreeMap<>();

		for (SiteEvent site : sites)
		{
			faces.add(site.getCell());
		}

		createVoronoiDiagram();
	}

	/**
	 * Returns the current y-position of the sweep line.
	 *
	 * @return the current y-position of the sweep line
	 */
	public static double getSweepLinePos()
	{
		return sweepLinePos;
	}

	private void createVoronoiDiagram()
	{
		while (!queue.isEmpty())
		{
			Point event = queue.poll();
			sweepLinePos = event.getY();
			if (firstSiteSweepLinePos == Double.MIN_VALUE) firstSiteSweepLinePos = event.getY();
			if (event.getClass() == CircleEvent.class)
				handleCircleEvent((CircleEvent) event);
			else if (event.getClass() == SiteEvent.class)
				handleSiteEvent((SiteEvent) event);
			else
				throw new IllegalArgumentException("Non-event element in the queue");
		}

		// TODO Compute faces
		computeBoundingBox();
		connectInfiniteEdges();
		this.computeFaces();
	}

	private void handleSiteEvent(SiteEvent event)
	{
		if (status.isEmpty())
		{
			status.put(new ArcSegment(event), null);
			return;
		}

		Map.Entry<ArcSegment, CircleEvent> entryAbove = status.floorEntry(new TreeQuery(event));
		ArcSegment alpha = entryAbove.getKey();

		if (entryAbove.getValue() != null) queue.remove(entryAbove.getValue());

		status.remove(alpha);

		DCELEdge edge1 = new DCELEdge();
		DCELEdge edge2 = new DCELEdge(edge1);
		edges.add(edge1);
		edges.add(edge2);
		calculateLine(edge1, edge2, alpha.getSite(), event);

		/* Handle case in which multiple points have the same y-coordinate as the first */
		if (sweepLinePos == firstSiteSweepLinePos)
		{
			Breakpoint breakpoint;
			ArcSegment leftArcSegment, rightArcSegment;

			if (alpha.getSite().getX() < event.getX())
			{
				breakpoint = new Breakpoint(alpha.getSite(), event, edge1);
				leftArcSegment = new ArcSegment(alpha.getSite(), alpha.getLeftBreakpoint(), breakpoint);
				rightArcSegment = new ArcSegment(event, breakpoint, alpha.getRightBreakpoint());
			}
			else
			{
				breakpoint = new Breakpoint(event, alpha.getSite(), edge1);
				leftArcSegment = new ArcSegment(event, alpha.getLeftBreakpoint(), breakpoint);
				rightArcSegment = new ArcSegment(alpha.getSite(), breakpoint, alpha.getRightBreakpoint());
			}

			status.put(leftArcSegment, null);
			status.put(rightArcSegment, null);

			return;
		}

		Breakpoint newLeftBreakpoint = new Breakpoint(alpha.getSite(), event);
		Breakpoint newRightBreakpoint = new Breakpoint(event, alpha.getSite());

		if (newLeftBreakpoint.isMovingRight())
		{
			if (edge1.isDirectedRight())
			{
				newLeftBreakpoint.setTracedEdge(edge1);
				newRightBreakpoint.setTracedEdge(edge2);

				edge1.setIncidentFace(event.getCell());
				edge2.setIncidentFace(alpha.getSite().getCell());

				event.getCell().setOuterComponent(edge1);
				alpha.getSite().getCell().setOuterComponent(edge2);
			}
			else
			{
				newRightBreakpoint.setTracedEdge(edge1);
				newLeftBreakpoint.setTracedEdge(edge2);

				edge1.setIncidentFace(alpha.getSite().getCell());
				edge2.setIncidentFace(event.getCell());

				event.getCell().setOuterComponent(edge2);
				alpha.getSite().getCell().setOuterComponent(edge1);
			}
		}
		else
		{
			if (edge1.isDirectedRight())
			{
				newLeftBreakpoint.setTracedEdge(edge2);
				newRightBreakpoint.setTracedEdge(edge1);

				edge1.setIncidentFace(alpha.getSite().getCell());
				edge2.setIncidentFace(event.getCell());

				event.getCell().setOuterComponent(edge2);
				alpha.getSite().getCell().setOuterComponent(edge1);
			}
			else
			{
				newRightBreakpoint.setTracedEdge(edge2);
				newLeftBreakpoint.setTracedEdge(edge1);

				edge1.setIncidentFace(event.getCell());
				edge2.setIncidentFace(alpha.getSite().getCell());

				event.getCell().setOuterComponent(edge1);
				alpha.getSite().getCell().setOuterComponent(edge2);
			}
		}

//		if (event.getY() != alpha.getSite().getY())
//		{
//			if (event.getX() < alpha.getSite().getX()) // If it's to the left, the left breakpoint moves up and the right moves down
//			{
//				newLeftBreakpoint = new Breakpoint(alpha.getSite(), event, edge2);
//				newRightBreakpoint = new Breakpoint(event, alpha.getSite(), edge1);
//			}
//			else if (event.getX() > alpha.getSite().getX()) // If it's to the right, the left breakpoint moves down and the right moves up
//			{
//				newLeftBreakpoint = new Breakpoint(alpha.getSite(), event, edge1);
//				newRightBreakpoint = new Breakpoint(event, alpha.getSite(), edge2);
//			}
//			else // If it's below, the left breakpoint moves left and the right moves right
//			{
//				newLeftBreakpoint = new Breakpoint(alpha.getSite(), event, edge1);
//				newRightBreakpoint = new Breakpoint(event, alpha.getSite(), edge2);
//			}
//		}
//		else // If it's at the same y-level, pick one to to move down and the other to move up
//		{
//			newLeftBreakpoint = new Breakpoint(alpha.getSite(), event, edge1);
//			newRightBreakpoint = new Breakpoint(event, alpha.getSite(), edge2);
//		}

		//newLeftBreakpoint = new Breakpoint(alpha.getSite(), event, edge1);
		//newRightBreakpoint = new Breakpoint(event, alpha.getSite(), edge2);

//		VoronoiCell face1 = new VoronoiCell(alpha.getSite(), alpha.getSite().getIndex(), edge1);
//		VoronoiCell face2 = new VoronoiCell(event, event.getIndex(), edge2);
//		faces.add(face1);
//		faces.add(face2);
//
//		edge1.setIncidentFace(face1);
//		edge2.setIncidentFace(face2);

		ArcSegment leftArcSegment = new ArcSegment(alpha.getSite(), alpha.getLeftBreakpoint(), newLeftBreakpoint);
		ArcSegment centerArcSegment = new ArcSegment(event, newLeftBreakpoint, newRightBreakpoint);
		ArcSegment rightArcSegment = new ArcSegment(alpha.getSite(), newRightBreakpoint, alpha.getRightBreakpoint());

		status.put(leftArcSegment, null);
		status.put(centerArcSegment, null);
		status.put(rightArcSegment, null);

		checkForCircleEvent(leftArcSegment);
		checkForCircleEvent(rightArcSegment);
	}

	private void handleCircleEvent(CircleEvent event)
	{
		ArcSegment alpha = event.getDisappearingArcSegment();
		status.remove(alpha);

		Map.Entry<ArcSegment, CircleEvent> leftEntry = status.lowerEntry(alpha);
		Map.Entry<ArcSegment, CircleEvent> rightEntry = status.higherEntry(alpha);

		ArcSegment leftNeighbor = leftEntry.getKey();
		ArcSegment rightNeighbor = rightEntry.getKey();

		Breakpoint oldLeftBreakpoint = leftNeighbor.getRightBreakpoint();
		Breakpoint oldRightBreakpoint = rightNeighbor.getLeftBreakpoint();

		DCELEdge edge1 = new DCELEdge();
		DCELEdge edge2 = new DCELEdge(edge1);
		edges.add(edge1);
		edges.add(edge2);
		calculateLine(edge1, edge2, leftNeighbor.getSite(), rightNeighbor.getSite());

		Breakpoint newBreakpoint = new Breakpoint(leftNeighbor.getSite(), rightNeighbor.getSite(), edge1);

		leftNeighbor.setRightBreakpoint(newBreakpoint);
		rightNeighbor.setLeftBreakpoint(newBreakpoint);

		if (leftEntry.getValue() != null) queue.remove(leftEntry.getValue());
		if (rightEntry.getValue() != null) queue.remove(rightEntry.getValue());

		DCELVertex vertex = new DCELVertex(event.getCircle().getCenter(), edge1);
		vertices.add(vertex);

		oldRightBreakpoint.getTracedEdge().getTwin().setOrigin(vertex);
		oldLeftBreakpoint.getTracedEdge().getTwin().setOrigin(vertex);
		edge1.setOrigin(vertex);

		oldRightBreakpoint.getTracedEdge().getTwin().setPrev(oldLeftBreakpoint.getTracedEdge());
		oldLeftBreakpoint.getTracedEdge().getTwin().setPrev(edge2);
		edge1.setPrev(oldRightBreakpoint.getTracedEdge());

		oldRightBreakpoint.getTracedEdge().setNext(edge1);
		oldLeftBreakpoint.getTracedEdge().setNext(oldRightBreakpoint.getTracedEdge().getTwin());
		edge2.setNext(oldLeftBreakpoint.getTracedEdge().getTwin());

		checkForCircleEvent(leftNeighbor);
		checkForCircleEvent(rightNeighbor);
	}

	private void checkForCircleEvent(ArcSegment arcSegment)
	{
		/* If the arc has no left or right neighbors (i.e. if it is on the end of the beach line), there can be no
		circle event */
		if (arcSegment.getLeftBreakpoint() == null || arcSegment.getRightBreakpoint() == null) return;

		Point p1 = arcSegment.getLeftBreakpoint().getLeftArcSegment();
		Point p2 = arcSegment.getSite();
		Point p3 = arcSegment.getRightBreakpoint().getRightArcSegment();

		/* If the points make a clockwise turn, we have a circle */
		if (MathOps.crossProduct(p1, p2, p3) < 0)
		{
			Circle circle = MathOps.circle(p1, p2, p3);
			CircleEvent circleEvent = new CircleEvent(circle, arcSegment);

			/* Add the circle event to the queue and update the pointer in the status tree */
			queue.add(circleEvent);
			status.put(arcSegment, circleEvent);
		}
	}

	// TODO Handle collinear site points case
	// TODO Handle case where edge intersects a corner of the bounding box
	private void connectInfiniteEdges()
	{
		Point min = getBoundingBox().getLowerLeft().getCoordinates();
		Point max = getBoundingBox().getUpperRight().getCoordinates();

		ArrayList<DCELEdge> toAdd = new ArrayList<>();

		for (DCELEdge edge : edges)
		{
			if (edge.getOrigin() == null)
			{
				Point p = edge.getTwin().getOrigin().getCoordinates();
				Point intersection, maxIntersection, minIntersection;

				if (edge.getLine().isVertical())
				{
					maxIntersection = new Point(p.getX(), max.getY());
					minIntersection = new Point(p.getX(), min.getY());
				}
				else
				{
					double[] vector = edge.getDirection();

					double tx1 = (min.getX() - p.getX()) * (1 / vector[0]);
					double tx2 = (max.getX() - p.getX()) * (1 / vector[0]);

					double tMin = Math.min(tx1, tx2);
					double tMax = Math.max(tx1, tx2);

					double ty1 = (min.getY() - p.getY()) * (1 / vector[1]);
					double ty2 = (max.getY() - p.getY()) * (1 / vector[1]);

					tMin = Math.max(tMin, Math.min(ty1, ty2));
					tMax = Math.min(tMax, Math.max(ty1, ty2));

					maxIntersection = new Point((vector[0] * tMax) + p.getX(), (vector[1] * tMax) + p.getY());
					minIntersection = new Point((vector[0] * tMin) + p.getX(), (vector[1] * tMin) + p.getY());
				}

				// TODO Not always the case
				if (Point.distance(p, maxIntersection) < Point.distance(p, minIntersection))
					intersection = maxIntersection;
				else
					intersection = minIntersection;

				DCELVertex vertex = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX, intersection, edge);
				vertices.add(vertex);

				DCELEdge outerBoundingEdge = getBoundingBox().getIntersectedEdge(intersection);
				DCELEdge innerBoundingEdge = outerBoundingEdge.getTwin();
				DCELEdge newOuterBoundingEdge = new DCELEdge(innerBoundingEdge);
				DCELEdge newInnerBoundingEdge = new DCELEdge(outerBoundingEdge);

				toAdd.add(newOuterBoundingEdge);
				toAdd.add(newInnerBoundingEdge);

				edge.setOrigin(vertex);
				newOuterBoundingEdge.setOrigin(vertex);
				newInnerBoundingEdge.setOrigin(vertex);

				newOuterBoundingEdge.setIncidentFace(outerBoundingEdge.getIncidentFace());

				newOuterBoundingEdge.setNext(outerBoundingEdge.getNext());
				outerBoundingEdge.setNext(newOuterBoundingEdge);
				newInnerBoundingEdge.setNext(innerBoundingEdge.getNext());
				innerBoundingEdge.setNext(edge);
				edge.getTwin().setNext(newInnerBoundingEdge);

				newOuterBoundingEdge.setPrev(outerBoundingEdge);
				newOuterBoundingEdge.getNext().setPrev(newOuterBoundingEdge);
				newInnerBoundingEdge.setPrev(edge.getTwin());
				newInnerBoundingEdge.getNext().setPrev(newInnerBoundingEdge);
				edge.setPrev(innerBoundingEdge);
			}
		}

		edges.addAll(toAdd);
	}

	private void calculateLine(DCELEdge edge1, DCELEdge edge2, Point p1, Point p2)
	{
		Line perpendicularBisector = Line.perpendicularBisector(p1, p2);
		double vy = -(p1.getX() - p2.getX());
		double vx = p1.getY() - p2.getY();

		edge1.setLine(perpendicularBisector);
		edge2.setLine(perpendicularBisector);

		edge1.setDirection(new double[]{vx, vy});
		edge2.setDirection(new double[]{-vx, -vy});
	}

	@Override
	protected void computeFaces()
	{
		for (DCELEdge edge : edges)
		{
			if (edge.getIncidentFace() != null && edge.getIncidentFace().getOuterComponent() != null)
			{
				DCELEdge e = edge.getNext();
				DCELFace face = edge.getIncidentFace();

				while (e != edge)
				{
					e.setIncidentFace(face);
					e = e.getNext();
				}
			}
		}
	}
}

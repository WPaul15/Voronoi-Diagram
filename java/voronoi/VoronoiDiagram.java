package voronoi;

import auxiliary.Circle;
import auxiliary.LineVector;
import auxiliary.MathOps;
import auxiliary.Point;
import voronoi.dcel.DCELEdge;
import voronoi.dcel.DCELVertex;
import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.CircleEvent;
import voronoi.queue.Event;
import voronoi.tree.ArcSegment;
import voronoi.tree.Breakpoint;
import voronoi.tree.TreeQuery;

import java.util.*;

/**
 * @author Willem Paul
 */
public class VoronoiDiagram extends DoublyConnectedEdgeList
{
	private PriorityQueue<Event> queue;
	private TreeMap<ArcSegment, CircleEvent> status;

	private static double sweepLinePos = Double.MIN_VALUE;

	/**
	 * Constructs a Voronoi diagram from the given set of sites using Steven Fortune's line sweep algorithm.
	 *
	 * @param sites The list of sites for which to construct a Voronoi diagram
	 */
	public VoronoiDiagram(Set<Event> sites)
	{
		queue = new PriorityQueue<>(sites);
		status = new TreeMap<>();

		createVoronoiDiagram();
	}

	public static double getSweepLinePos()
	{
		return sweepLinePos;
	}

	private void createVoronoiDiagram()
	{
		while (!queue.isEmpty())
		{
			Event event = queue.poll();
			sweepLinePos = event.getCoordinates().getY();
			if (event.getClass() == Event.class)
				handleSiteEvent(event);
			else
				handleCircleEvent((CircleEvent) event);
		}

		// TODO Compute bounding box and update DCEL
		computeBoundingBox();
		connectInfiniteEdges();
	}

	private void handleSiteEvent(Event event)
	{
		/* If the status tree is empty, insert the arc given by the event */
		if (status.isEmpty())
		{
			status.put(new ArcSegment(event.getCoordinates()), null);
			return;
		}

		/* Retrieve the arc directly above the new event */
		Map.Entry<ArcSegment, CircleEvent> entryAbove = status.floorEntry(new TreeQuery(event.getCoordinates()));
		ArcSegment alpha = entryAbove.getKey();

		/* Remove false circle event if it exists */
		if (entryAbove.getValue() != null) queue.remove(entryAbove.getValue());

		/* Replace the old node in the status tree with a new subtree */
		status.remove(alpha);

		DCELEdge leftEdge = new DCELEdge();
		DCELEdge rightEdge = new DCELEdge(leftEdge);

		this.getEdges().add(leftEdge);
		this.getEdges().add(rightEdge);

		LineVector perpendicularBisector = LineVector.perpendicularBisector(alpha.getSite(), event.getCoordinates());

		leftEdge.setLineVector(perpendicularBisector);
		rightEdge.setLineVector(perpendicularBisector);

		Breakpoint newLeftBreakpoint = new Breakpoint(alpha.getSite(), event.getCoordinates(), leftEdge);
		Breakpoint newRightBreakpoint = new Breakpoint(event.getCoordinates(), alpha.getSite(), rightEdge);

		ArcSegment leftArcSegment = new ArcSegment(alpha.getSite(), alpha.getLeftBreakpoint(), newLeftBreakpoint);
		ArcSegment centerArcSegment = new ArcSegment(event.getCoordinates(), newLeftBreakpoint, newRightBreakpoint);
		ArcSegment rightArcSegment = new ArcSegment(alpha.getSite(), newRightBreakpoint, alpha.getRightBreakpoint());

		status.put(leftArcSegment, null);
		status.put(centerArcSegment, null);
		status.put(rightArcSegment, null);

		/* Check for possible circle events with the left and right neighbors */
		checkForCircleEvent(leftArcSegment);
		checkForCircleEvent(rightArcSegment);
	}

	private void handleCircleEvent(CircleEvent event)
	{
		ArcSegment alpha = event.getDisappearingArcSegment();

		/* Remove the arc segment from the status tree and update the breakpoints of the left and right neighbors */
		status.remove(alpha);

		Map.Entry<ArcSegment, CircleEvent> leftEntry = status.lowerEntry(alpha);
		Map.Entry<ArcSegment, CircleEvent> rightEntry = status.higherEntry(alpha);

		ArcSegment leftNeighbor = leftEntry.getKey();
		ArcSegment rightNeighbor = rightEntry.getKey();

		Breakpoint oldLeftBreakpoint = leftNeighbor.getRightBreakpoint();
		Breakpoint oldRightBreakpoint = rightNeighbor.getLeftBreakpoint();

		DCELEdge leftEdge = new DCELEdge();
		DCELEdge rightEdge = new DCELEdge(leftEdge);

		this.getEdges().add(leftEdge);
		this.getEdges().add(rightEdge);

		LineVector perpendicularBisector = LineVector.perpendicularBisector(leftNeighbor.getSite(), rightNeighbor.getSite());

		leftEdge.setLineVector(perpendicularBisector);
		rightEdge.setLineVector(perpendicularBisector);

		Breakpoint newBreakpoint = new Breakpoint(leftNeighbor.getSite(), rightNeighbor.getSite(), leftEdge);

		leftNeighbor.setRightBreakpoint(newBreakpoint);
		rightNeighbor.setLeftBreakpoint(newBreakpoint);

		/* Delete any other circle events involving alpha */
		if (leftEntry.getValue() != null) queue.remove(leftEntry.getValue());
		if (rightEntry.getValue() != null) queue.remove(rightEntry.getValue());

		/* Add a new Voronoi vertex and update the edges incident on it */
		DCELVertex vertex = new DCELVertex(event.getCircle().getCenter(), leftEdge);
		this.getVertices().add(vertex);

		oldRightBreakpoint.getTracedEdge().getTwin().setOrigin(vertex);
		oldLeftBreakpoint.getTracedEdge().getTwin().setOrigin(vertex);
		leftEdge.setOrigin(vertex);

		oldRightBreakpoint.getTracedEdge().getTwin().setPrev(oldLeftBreakpoint.getTracedEdge());
		oldLeftBreakpoint.getTracedEdge().getTwin().setPrev(rightEdge);
		leftEdge.setPrev(oldRightBreakpoint.getTracedEdge());

		oldRightBreakpoint.getTracedEdge().setNext(leftEdge);
		oldLeftBreakpoint.getTracedEdge().setNext(oldRightBreakpoint.getTracedEdge().getTwin());
		rightEdge.setNext(oldLeftBreakpoint.getTracedEdge().getTwin());

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

		for (DCELEdge edge : this.getEdges())
		{
			if (edge.getOrigin() == null)
			{
				Point p = edge.getTwin().getOrigin().getCoordinates();
				double[] lVector = edge.getLineVector().getVector();

				double tx1 = (min.getX() - p.getX()) * (1 / lVector[0]);
				double tx2 = (max.getX() - p.getX()) * (1 / lVector[0]);

				double tMin = Math.min(tx1, tx2);
				double tMax = Math.max(tx1, tx2);

				double ty1 = (min.getY() - p.getY()) * (1 / lVector[1]);
				double ty2 = (max.getY() - p.getY()) * (1 / lVector[1]);

				tMin = Math.max(tMin, Math.min(ty1, ty2));
				tMax = Math.min(tMax, Math.max(ty1, ty2));

				Point intersection;
				Point maxIntersection = new Point((lVector[0] * tMax) + p.getX(), (lVector[1] * tMax) + p.getY());
				Point minIntersection = new Point((lVector[0] * tMin) + p.getX(), (lVector[1] * tMin) + p.getY());

				if (Point.distance(p, maxIntersection) < Point.distance(p, minIntersection))
					intersection = maxIntersection;
				else
					intersection = minIntersection;

				DCELVertex vertex = new DCELVertex(DCELVertex.VertexType.BOUNDING_VERTEX, intersection, edge);
				this.getVertices().add(vertex);

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
				newOuterBoundingEdge.getNext().setPrev(outerBoundingEdge);
				newInnerBoundingEdge.setPrev(edge.getTwin());
				newInnerBoundingEdge.getNext().setPrev(newInnerBoundingEdge);
				edge.setPrev(innerBoundingEdge);
			}
		}

		getEdges().addAll(toAdd);
	}
}
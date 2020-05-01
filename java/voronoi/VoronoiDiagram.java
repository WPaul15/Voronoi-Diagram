package voronoi;

import auxiliary.Circle;
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
	private final HashSet<Breakpoint> breakpoints;

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
		this.breakpoints = new HashSet<>();

		/* If this is true, there are no site points and thus, there is nothing to be done. */
		if (sites.isEmpty()) return;

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

		computeBoundingBox();
		connectInfiniteEdges();
		this.computeFaces();
	}

	private void handleSiteEvent(SiteEvent event)
	{
		faces.add(event.getCell());

		if (status.isEmpty())
		{
			status.put(new ArcSegment(event), null);
			return;
		}

		Map.Entry<ArcSegment, CircleEvent> entryAbove = status.floorEntry(new TreeQuery(event));
		ArcSegment alpha = entryAbove.getKey();

		if (entryAbove.getValue() != null) queue.remove(entryAbove.getValue());

		status.remove(alpha);

		DCELEdge edge1 = new DCELEdge(DCELEdge.EdgeType.VORONOI_EDGE);
		DCELEdge edge2 = new DCELEdge(DCELEdge.EdgeType.VORONOI_EDGE, edge1);
		edges.add(edge1);
		edges.add(edge2);

		//DCELEdge.setTwinPair(edge1, edge2);
		calculateDirections(edge1, edge2, alpha.getSite(), event);

		/* Handle case in which multiple points have the same y-coordinate as the first */
		if (sweepLinePos == firstSiteSweepLinePos)
		{
			Breakpoint breakpoint;
			ArcSegment leftArcSegment, rightArcSegment;

			if (alpha.getSite().getX() < event.getX())
			{
				breakpoint = new Breakpoint(alpha.getSite(), event, null);

				if (!edge1.isDirectedStraightUp())
				{
					breakpoint.setTracedEdge(edge1);

					edge1.setIncidentFace(event.getCell());
					edge2.setIncidentFace(alpha.getSite().getCell());

					event.getCell().setOuterComponent(edge1);
					alpha.getSite().getCell().setOuterComponent(edge2);
				}
				else
				{
					breakpoint.setTracedEdge(edge2);

					edge1.setIncidentFace(alpha.getSite().getCell());
					edge2.setIncidentFace(event.getCell());

					alpha.getSite().getCell().setOuterComponent(edge1);
					event.getCell().setOuterComponent(edge2);
				}

				leftArcSegment = new ArcSegment(alpha.getSite(), alpha.getLeftBreakpoint(), breakpoint);
				rightArcSegment = new ArcSegment(event, breakpoint, alpha.getRightBreakpoint());
			}
			else
			{
				breakpoint = new Breakpoint(event, alpha.getSite(), null);

				if (!edge1.isDirectedStraightUp())
				{
					breakpoint.setTracedEdge(edge1);

					edge1.setIncidentFace(alpha.getSite().getCell());
					edge2.setIncidentFace(event.getCell());

					alpha.getSite().getCell().setOuterComponent(edge1);
					event.getCell().setOuterComponent(edge2);
				}
				else
				{
					breakpoint.setTracedEdge(edge2);

					edge1.setIncidentFace(event.getCell());
					edge2.setIncidentFace(alpha.getSite().getCell());

					event.getCell().setOuterComponent(edge1);
					alpha.getSite().getCell().setOuterComponent(edge2);
				}

				leftArcSegment = new ArcSegment(event, alpha.getLeftBreakpoint(), breakpoint);
				rightArcSegment = new ArcSegment(alpha.getSite(), breakpoint, alpha.getRightBreakpoint());
			}

			breakpoints.add(breakpoint);

			status.put(leftArcSegment, null);
			status.put(rightArcSegment, null);

			return;
		}

		Breakpoint newLeftBreakpoint = new Breakpoint(alpha.getSite(), event);
		Breakpoint newRightBreakpoint = new Breakpoint(event, alpha.getSite());

		if (newLeftBreakpoint.isMovingRight())
		{
			if (edge1.isDirectedRight() || edge1.isDirectedStraightUp())
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
			if (edge1.isDirectedRight() || edge1.isDirectedStraightUp())
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

		ArcSegment leftArcSegment = new ArcSegment(alpha.getSite(), alpha.getLeftBreakpoint(), newLeftBreakpoint);
		ArcSegment centerArcSegment = new ArcSegment(event, newLeftBreakpoint, newRightBreakpoint);
		ArcSegment rightArcSegment = new ArcSegment(alpha.getSite(), newRightBreakpoint, alpha.getRightBreakpoint());

		breakpoints.add(newLeftBreakpoint);
		breakpoints.add(newRightBreakpoint);

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

		ArcSegment leftArcSegment = leftEntry.getKey();
		ArcSegment rightArcSegment = rightEntry.getKey();

		Breakpoint oldLeftBreakpoint = leftArcSegment.getRightBreakpoint();
		Breakpoint oldRightBreakpoint = rightArcSegment.getLeftBreakpoint();

		if (!(oldLeftBreakpoint.getTracedEdge().getTwin().isDirectedStraightUp() &&
				oldLeftBreakpoint.getTracedEdge().getOrigin() == null))
			breakpoints.remove(oldLeftBreakpoint);

		if (!(oldRightBreakpoint.getTracedEdge().getTwin().isDirectedStraightUp() &&
				oldRightBreakpoint.getTracedEdge().getOrigin() == null))
			breakpoints.remove(oldRightBreakpoint);

		DCELEdge edge1 = new DCELEdge(DCELEdge.EdgeType.VORONOI_EDGE);
		DCELEdge edge2 = new DCELEdge(DCELEdge.EdgeType.VORONOI_EDGE, edge1);
		edges.add(edge1);
		edges.add(edge2);

		calculateDirections(edge1, edge2, leftArcSegment.getSite(), rightArcSegment.getSite());

		Breakpoint newBreakpoint = new Breakpoint(leftArcSegment.getSite(), rightArcSegment.getSite(), null);

		if (newBreakpoint.isMovingRight())
		{
			if (edge1.isDirectedRight() || edge1.isDirectedStraightUp()) newBreakpoint.setTracedEdge(edge1);
			else newBreakpoint.setTracedEdge(edge2);
		}
		else
		{
			if (edge1.isDirectedRight() || edge1.isDirectedStraightUp()) newBreakpoint.setTracedEdge(edge2);
			else newBreakpoint.setTracedEdge(edge1);
		}

		leftArcSegment.setRightBreakpoint(newBreakpoint);
		rightArcSegment.setLeftBreakpoint(newBreakpoint);

		breakpoints.add(newBreakpoint);

		if (leftEntry.getValue() != null) queue.remove(leftEntry.getValue());
		if (rightEntry.getValue() != null) queue.remove(rightEntry.getValue());

		DCELVertex vertex = new DCELVertex(event.getCircle().getCenter(), newBreakpoint.getTracedEdge());
		vertices.add(vertex);

		oldRightBreakpoint.getTracedEdge().getTwin().setOrigin(vertex);
		oldLeftBreakpoint.getTracedEdge().getTwin().setOrigin(vertex);
		newBreakpoint.getTracedEdge().setOrigin(vertex);

		oldRightBreakpoint.getTracedEdge().getTwin().setPrev(oldLeftBreakpoint.getTracedEdge());
		oldLeftBreakpoint.getTracedEdge().getTwin().setPrev(newBreakpoint.getTracedEdge().getTwin());
		newBreakpoint.getTracedEdge().setPrev(oldRightBreakpoint.getTracedEdge());

		oldRightBreakpoint.getTracedEdge().setNext(newBreakpoint.getTracedEdge());
		oldLeftBreakpoint.getTracedEdge().setNext(oldRightBreakpoint.getTracedEdge().getTwin());
		newBreakpoint.getTracedEdge().getTwin().setNext(oldLeftBreakpoint.getTracedEdge().getTwin());

		checkForCircleEvent(leftArcSegment);
		checkForCircleEvent(rightArcSegment);
	}

	private void checkForCircleEvent(ArcSegment arcSegment)
	{
		/* If the arc has no left or right neighbors (i.e. if it is on the end of the beach line), there can be no
		circle event. */
		if (arcSegment.getLeftBreakpoint() == null || arcSegment.getRightBreakpoint() == null) return;

		Point p1 = arcSegment.getLeftBreakpoint().getLeftArcSegment();
		Point p2 = arcSegment.getSite();
		Point p3 = arcSegment.getRightBreakpoint().getRightArcSegment();

		/* If the points make a clockwise turn, we have a circle. */
		if (MathOps.counterclockwise(p1, p2, p3))
		{
			Circle circle = MathOps.circle(p1, p2, p3);
			CircleEvent circleEvent = new CircleEvent(circle, arcSegment);

			/* Add the circle event to the queue and update the pointer in the status tree. */
			queue.add(circleEvent);
			status.put(arcSegment, circleEvent);
		}
	}

	private void connectInfiniteEdges()
	{
		/* If there are exactly four vertices, they are the corners of the bounding box and there are no Voronoi
		vertices. */
		boolean noVertices = vertices.size() == 4;

		/* If this is true, then there is only one face and thus, only one site point. */
		if (noVertices && faces.size() == 2)
		{
			DCELFace face = faces.get(0).isUnbounded() ? faces.get(1) : faces.get(0);
			getBoundingBox().getInnerEdge().setIncidentFace(face);
			face.setOuterComponent(getBoundingBox().getInnerEdge());
			return;
		}

		for (Breakpoint breakpoint : breakpoints)
		{
			DCELEdge edge = breakpoint.getTracedEdge();
			Point origin;

			if (noVertices)
			{
				/* Once we've encountered one unbounded half-edge, we update both that edge and the twin
				simultaneously. */
				if (edge.getOrigin() != null && edge.getTwin().getOrigin() != null) continue;

				/* We use this "origin" to calculate the bounding box intersection points. */
				origin = MathOps.midpoint(breakpoint.getLeftArcSegment(), breakpoint.getRightArcSegment());
				getBoundingBox().connectEdge(this, origin, edge);
				getBoundingBox().connectEdge(this, origin, edge.getTwin());
			}
			else
			{
				/* We want the half-edge directed out of the bounding box. */
				if (edge.getOrigin() != null)
					origin = edge.getOrigin().getCoordinates();
				else
				{
					// TODO Issue with more than three cocircular points where one pair of edges has no origin points
					edge = breakpoint.getTracedEdge().getTwin();
					origin = edge.getOrigin().getCoordinates();
				}

				getBoundingBox().connectEdge(this, origin, edge);
			}
		}
	}

	private void calculateDirections(DCELEdge edge1, DCELEdge edge2, Point p1, Point p2)
	{
		double vy = -(p1.getX() - p2.getX());
		double vx = p1.getY() - p2.getY();

		edge1.setDirection(new double[]{vx, vy});
		edge2.setDirection(new double[]{-vx, -vy});
	}

	@Override
	protected void computeFaces()
	{
		for (DCELEdge edge : edges)
		{
			if (edge.getIncidentFace() != null && !edge.getIncidentFace().isUnbounded())
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

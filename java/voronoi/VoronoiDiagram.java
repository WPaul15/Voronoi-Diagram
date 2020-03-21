package voronoi;

import auxiliary.Circle;
import auxiliary.MathOps;
import auxiliary.Point;
import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.CircleEvent;
import voronoi.queue.Event;
import voronoi.queue.EventQueue;
import voronoi.tree.ArcSegment;
import voronoi.tree.Breakpoint;
import voronoi.tree.StatusTree;
import voronoi.tree.TreeQuery;

import java.util.List;
import java.util.Map;

/**
 * @author Willem Paul
 */
public class VoronoiDiagram extends DoublyConnectedEdgeList
{
	private EventQueue queue;
	private StatusTree status;

	private static double sweepLinePos = Double.MIN_VALUE;

	/**
	 * Constructs a Voronoi diagram from the given set of sites using Steven Fortune's line sweep algorithm.
	 *
	 * @param sites The list of sites for which to construct a Voronoi diagram.
	 */
	public VoronoiDiagram(List<Event> sites)
	{
		queue = new EventQueue(sites);
		status = new StatusTree();

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
			System.out.println(queue);
			Event event = queue.poll();
			assert event != null;
			sweepLinePos = event.getCoordinates().getY();
			if (event.getClass() == Event.class)
				handleSiteEvent(event);
			else
				handleCircleEvent();
			System.out.println(status);
		}

		// TODO Compute bounding box and update DCEL
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
		CircleEvent circleEvent = entryAbove.getValue();

		/* Remove false circle event if it exists */
		if (circleEvent != null) queue.remove(circleEvent);

		System.out.println("Arc Above " + event.toString() + ":\t" + alpha.getSite().toString());

		/* Replace the leaf from the status tree with a new subtree */
		status.remove(alpha);

		// TODO Create DCEL edges
//		DCELEdge leftEdge = new DCELEdge();
//		DCELEdge rightEdge = new DCELEdge();

		Breakpoint newLeftBreakpoint = new Breakpoint(alpha.getSite(), event.getCoordinates(), null);
		Breakpoint newRightBreakpoint = new Breakpoint(event.getCoordinates(), alpha.getSite(), null);

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

	// TODO Implement
	private void handleCircleEvent()
	{

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
			CircleEvent circleEvent = new CircleEvent(circle.getBottomPoint(), arcSegment);

			/* Add the circle event to the queue and update the pointer in the status tree */
			queue.add(circleEvent);
			status.put(arcSegment, circleEvent);
		}
	}
}
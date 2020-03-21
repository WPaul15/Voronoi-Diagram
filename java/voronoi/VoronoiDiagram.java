package voronoi;

import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.CircleEvent;
import voronoi.queue.Event;
import voronoi.queue.EventQueue;
import voronoi.tree.Arc;
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

	// TODO Implement
	private void handleSiteEvent(Event event)
	{
		/* If the status tree is empty, insert the arc given by the event */
		if (status.isEmpty())
		{
			status.put(new Arc(event.getCoordinates()), null);
			return;
		}

		/* Retrieve the arc directly above the new event */
		Map.Entry<Arc, CircleEvent> entryAbove = status.floorEntry(new TreeQuery(event.getCoordinates()));
		Arc alpha = entryAbove.getKey();
		CircleEvent circleEvent = entryAbove.getValue();

		/* Remove false circle event if it exists */
		if (circleEvent != null) queue.remove(circleEvent);

		System.out.println("Arc Above " + event.toString() + ":\t" + alpha.getSite().toString());

		/* Replace the leaf from the status tree with a new subtree */
		status.remove(alpha);

		// TODO Create DCEL edges
		Breakpoint newLeftBreakpoint = new Breakpoint(alpha.getSite(), event.getCoordinates(), null);
		Breakpoint newRightBreakpoint = new Breakpoint(event.getCoordinates(), alpha.getSite(), null);

		Arc leftArc = new Arc(alpha.getSite(), alpha.getLeftBreakpoint(), newLeftBreakpoint);
		Arc centerArc = new Arc(event.getCoordinates(), newLeftBreakpoint, newRightBreakpoint);
		Arc rightArc = new Arc(alpha.getSite(), newRightBreakpoint, alpha.getRightBreakpoint());

		status.put(leftArc, null);
		status.put(centerArc, null);
		status.put(rightArc, null);

		/* Check for possible circle events */
		checkForCircleEvent(leftArc);
		checkForCircleEvent(rightArc);
	}

	// TODO Implement
	private void handleCircleEvent()
	{

	}

	private void checkForCircleEvent(Arc arc)
	{
		/* If the arc has no left or right neighbors, there can be no circle event */
		if (arc.getLeftBreakpoint() == null || arc.getRightBreakpoint() == null) return;

		Arc leftNeighbor = status.floorKey(arc);
		Arc rightNeighbor = status.ceilingKey(arc);
	}
}
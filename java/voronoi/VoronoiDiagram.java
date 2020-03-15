package voronoi;

import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.CircleEvent;
import voronoi.queue.Event;
import voronoi.queue.EventQueue;
import voronoi.tree.LeafNode;
import voronoi.tree.StatusTree;
import voronoi.tree.TreeNode;

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
			status.put(new LeafNode(event.getCoordinates()), null);
			return;
		}

		/* Retrieve the arc directly above the new event */
		Map.Entry<TreeNode, CircleEvent> entry = status.floorEntry(new LeafNode(event.getCoordinates()));
		TreeNode arc = entry.getKey();
		CircleEvent circleEvent = entry.getValue();

		/* Remove false circle event if it exists */
		if (circleEvent != null) queue.remove(circleEvent);

//		assert arc != null;
//		LeafNode l = (LeafNode) arc;
//		System.out.println("Arc Above " + event.toString() + ":\t" + l.getArc().toString());

		/* Replace the leaf from the status tree with a new subtree */
//		status.remove(entry.getKey());
	}

	// TODO Implement
	private void handleCircleEvent()
	{

	}
}
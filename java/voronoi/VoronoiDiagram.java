package voronoi;

import javafx.scene.Scene;
import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.Event;
import voronoi.queue.EventQueue;
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

	/**
	 * Constructs a Voronoi diagram from the given set of sites using Fortune's sweep line algorithm.
	 *
	 * @param sites The list of sites for which to construct a Voronoi diagram.
	 */
	public VoronoiDiagram(List<Event> sites)
	{
		queue = new EventQueue(sites);
		status = new StatusTree();

		//System.out.println(voronoi.queue);

		while (!queue.isEmpty())
		{
			System.out.println(queue);
			Event event = queue.poll();
			assert event != null;
			if (event.getClass() == Event.class)
				handleSiteEvent(event);// TODO Implement
			else
				handleCircleEvent();// TODO Implement
			System.out.println(status);
		}

		// TODO Compute bounding box and update DCEL
	}

	private void handleSiteEvent(Event event)
	{
		/* If the status voronoi.tree is empty, insert the arc given by the event */
		if (status.isEmpty())
		{
			Point arc = event.getCoordinates();
			status.put(arc.getX(), new TreeNode(arc));
			return;
		}

		/* Retrieve the arc directly above the new event */
		Map.Entry<Integer, TreeNode> entry = status.floorEntry(event.getCoordinates().getX());

		/* Remove false circle event if it exists */
		if (entry != null) queue.remove(entry.getValue().getCircleEvent());

		assert entry != null;
		TreeNode arc = entry.getValue();
		System.out.println("Arc Above " + event.toString() + ":\t" + arc.getArc().toString());

		/* Replace the leaf from the status voronoi.tree with a new subtree */
		//status.remove(entry.getKey());
	}

	private void handleCircleEvent()
	{

	}
}
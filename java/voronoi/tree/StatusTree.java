package voronoi.tree;

import voronoi.queue.CircleEvent;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Willem Paul
 */
public class StatusTree extends TreeMap<ArcSegment, CircleEvent>
{
	public StatusTree()
	{
		super();
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("Status Tree:\t[");
		for (Map.Entry<ArcSegment, CircleEvent> node : this.entrySet())
		{
			builder.append(node.getKey()).append(" => ").append(node.getValue());
			builder.append(", ");
		}
		builder.append(']');

		return builder.toString();
	}
}


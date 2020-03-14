package voronoi.tree;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Willem Paul
 */
public class StatusTree extends TreeMap<Integer, TreeNode>
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
		for (Map.Entry<Integer, TreeNode> node : this.entrySet())
		{
			builder.append(node.getKey()).append(" => ").append(node.getValue());
			builder.append(", ");
		}
		builder.append(']');

		return builder.toString();
	}
}


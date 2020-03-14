package voronoi.queue;

import voronoi.Point;
import voronoi.tree.TreeNode;

public class CircleEvent extends Event
{
	private TreeNode arc;

	public CircleEvent(Point coordinates, TreeNode arc)
	{
		super(coordinates);
		this.arc = arc;
	}

	public TreeNode getArc()
	{
		return arc;
	}

	public void setArc(TreeNode arc)
	{
		this.arc = arc;
	}

	@Override
	public String toString()
	{
		return "C" + getCoordinates().toString();
	}
}

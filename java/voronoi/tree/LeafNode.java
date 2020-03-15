package voronoi.tree;

import voronoi.Point;

/**
 * @author Willem Paul
 */
public class LeafNode extends TreeNode
{
	private Point arc;

	public LeafNode(Point arc)
	{
		this.arc = arc;
	}

	@Override
	protected Point getPoint()
	{
		return arc;
	}

	public Point getArc()
	{
		return arc;
	}

	@Override
	public String toString()
	{
		return arc.toString();
	}
}

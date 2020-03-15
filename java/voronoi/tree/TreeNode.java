package voronoi.tree;

import voronoi.Point;

/**
 * @author Willem Paul
 */
public abstract class TreeNode implements Comparable<TreeNode>
{
	protected abstract Point getPoint();

	@Override
	public int compareTo(TreeNode treeNode)
	{
		return Double.compare(this.getPoint().getX(), treeNode.getPoint().getX());
	}
}

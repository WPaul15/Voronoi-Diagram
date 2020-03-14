package voronoi;

/**
 * @author Willem Paul
 */
public class Point implements Comparable<Point>
{
	private int x, y;

	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public int compareTo(Point point)
	{
		int compareY = Integer.compare(this.y, point.y);
		return compareY == 0 ? Integer.compare(this.x, point.x) : compareY;
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
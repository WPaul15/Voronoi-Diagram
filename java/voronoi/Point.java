package voronoi;

/**
 * @author Willem Paul
 */
public class Point implements Comparable<Point>
{
	private double x, y;

	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	@Override
	public int compareTo(Point point)
	{
		int compareY = Double.compare(this.y, point.y);
		return compareY == 0 ? Double.compare(this.x, point.x) : compareY;
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
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

	public static Point midpoint(Point p1, Point p2)
	{
		double x = (p1.x + p2.x) / 2;
		double y = (p1.y + p2.y) / 2;
		return new Point(x, y);
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
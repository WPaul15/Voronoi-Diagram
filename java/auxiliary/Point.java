package auxiliary;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

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

	public static double distance(Point p1, Point p2)
	{
		double a = p1.getX() - p2.getX();
		double b = p1.getY() - p2.getY();
		return Math.sqrt((a * a) + (b * b));
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
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Point point = (Point) o;
		return Double.compare(point.x, x) == 0 &&
				Double.compare(point.y, y) == 0;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y);
	}

	@Override
	public String toString()
	{
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
		decimalFormat.applyPattern("###,###.####");
		return "(" + decimalFormat.format(x) + ", " + decimalFormat.format(y) + ")";
	}
}
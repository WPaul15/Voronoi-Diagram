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

	public Point(Point p)
	{
		this.x = p.x;
		this.y = p.y;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	@Override
	public int compareTo(Point point)
	{
		int compareY = Double.compare(point.y, this.y);
		return compareY == 0 ? Double.compare(point.x, this.x) : compareY;
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

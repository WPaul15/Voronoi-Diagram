package auxiliary;

/**
 * @author Willem Paul
 */
public class MathOps
{
	private static final double EPSILON = 0.00001;

	/**
	 * Calculates the midpoint of the segment whose endpoints are {@code p1} and {@code p2}.
	 *
	 * @param p1 The first endpoint of the segment.
	 * @param p2 The second endpoint of the segment.
	 * @return A {@code Point} whose coordinates are the midpoint of the segment defined by {@code p1} and {@code p2}.
	 */
	public static Point midpoint(Point p1, Point p2)
	{
		double x = (p1.getX() + p2.getX()) / 2;
		double y = (p1.getY() + p2.getY()) / 2;
		return new Point(x, y);
	}

	public static boolean counterclockwise(Point p1, Point p2, Point p3)
	{
		return crossProduct(p1, p2, p3) < 0;
	}

	/**
	 * Calculates the circle passing through {@code p1}, {@code p2}, and {@code p3}.
	 *
	 * @param p1 The first point.
	 * @param p2 The second point.
	 * @param p3 The third point.
	 * @return The {@code Circle} passing through {@code p1}, {@code p2}, and {@code p3}.
	 * @implNote This implementation is taken from GeoTools, distributed under the GNU LGPL 2.1. The source code can be
	 * found <a href="https://github.com/geotools/geotools/blob/master/modules/extension/xsd/xsd-gml3/src/main/java/org/geotools/gml3/Circle.java">here</a>.
	 */
	public static Circle circle(Point p1, Point p2, Point p3)
	{
		double x, y, radius;

		double a13 = 2 * (p1.getX() - p3.getX());
		double b13 = 2 * (p1.getY() - p3.getY());
		double c13 = (p1.getY() * p1.getY() - p3.getY() * p3.getY()) + (p1.getX() * p1.getX() - p3.getX() * p3.getX());
		double a23 = 2 * (p2.getX() - p3.getX());
		double b23 = 2 * (p2.getY() - p3.getY());
		double c23 = (p2.getY() * p2.getY() - p3.getY() * p3.getY()) + (p2.getX() * p2.getX() - p3.getX() * p3.getX());

		double smallNumber = 0.0000001;

		/* If the points are too close together, we won't be able to set a circle */
		if ((Math.abs(a13) < smallNumber && Math.abs(b13) < smallNumber) ||
				(Math.abs(a23) < smallNumber && Math.abs(b23) < smallNumber))
			throw new IllegalArgumentException("Points are too close together to calculate the circle");
		else
		{
			y = (a13 * c23 - a23 * c13) / (a13 * b23 - a23 * b13);

			if (Math.abs(a13) > Math.abs(a23))
				x = (c13 - b13 * y) / a13;
			else
				x = (c23 - b23 * y) / a23;

			radius = Math.sqrt((x - p1.getX()) * (x - p1.getX()) + (y - p1.getY()) * (y - p1.getY()));
		}

		return new Circle(new Point(x, y), radius);
	}

	/**
	 * Compares two {@code double} values to a certain threshold. This compensates for minute rounding errors.
	 *
	 * @param a the first {@code double} to compare
	 * @param b the second {@code double} to compare
	 * @return true if the difference of the two values is less than a small number; false otherwise
	 */
	public static boolean thresholdEquals(double a, double b)
	{
		return Math.abs(a - b) < EPSILON;
	}

	private static int crossProduct(Point p1, Point p2, Point p3)
	{
		double v = (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
		if (v > 0) return 1;
		else if (v < 0) return -1;
		else return 0;
	}
}

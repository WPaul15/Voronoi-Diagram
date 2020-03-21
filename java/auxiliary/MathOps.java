package auxiliary;

import voronoi.Point;

public class MathOps
{
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

	/**
	 * Calculates the cross product of the vectors defined by the three points to determine whether the points make a
	 * clockwise turn, a counterclockwise turn, or are collinear.
	 *
	 * @param a The origin point of the two vectors.
	 * @param b The endpoint of the first vector.
	 * @param c The endpoint of the second vector.
	 * @return -1 if the points make a clockwise turn, 0 if they are collinear, or 1 if they make a counterclockwise
	 * turn.
	 */
	public static int cross(Point a, Point b, Point c)
	{
		double v = (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
		if (v > 0) return 1;
		else if (v < 0) return -1;
		else return 0;
	}
}
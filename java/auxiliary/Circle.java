package auxiliary;

/**
 * @author Willem Paul
 */
public class Circle
{
	private final Point center;
	private final double radius;

	public Circle(Point center, double radius)
	{
		this.center = center;
		this.radius = radius;
	}

	public Point getBottomPoint()
	{
		return new Point(center.getX(), center.getY() - radius);
	}

	public Point getCenter()
	{
		return center;
	}
}
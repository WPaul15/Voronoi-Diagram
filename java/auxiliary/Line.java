package auxiliary;

public class Line
{
	private double slope, yIntercept;

	public Line(double slope, double yIntercept)
	{
		this.slope = slope;
		this.yIntercept = yIntercept;
	}

	public static Line perpendicularBisector(Point p1, Point p2)
	{
		double m = -1 / ((p1.getY() - p2.getY()) / (p1.getX() - p2.getX()));
		Point midpoint = Point.midpoint(p1, p2);
		double b = midpoint.getY() - (m * midpoint.getX());
		return new Line(m, b);
	}

	public double getYFromX(double x)
	{
		return (slope * x) + yIntercept;
	}

	public double getXFromY(double y)
	{
		return (y - yIntercept) / slope;
	}

	public double getSlope()
	{
		return slope;
	}

	public void setSlope(double slope)
	{
		this.slope = slope;
	}

	public double getYIntercept()
	{
		return yIntercept;
	}

	public void setYIntercept(double yIntercept)
	{
		this.yIntercept = yIntercept;
	}
}

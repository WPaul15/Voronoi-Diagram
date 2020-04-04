package auxiliary;

public class LineVector
{
	private double slope, yIntercept;
	private double vx, vy;

	private LineVector(double slope, double yIntercept, double vx, double vy)
	{
		this.slope = slope;
		this.yIntercept = yIntercept;
		this.vx = vx;
		this.vy = vy;
	}

	public LineVector(Point p1, Point p2)
	{
		this.vx = p1.getX() - p2.getX();
		this.vy = p1.getY() - p2.getY();
		this.slope = vy / vx;
		this.yIntercept = p1.getY() - (slope * p1.getX());
	}

	public static LineVector perpendicularBisector(Point p1, Point p2)
	{
		double vx = -(p1.getX() - p2.getX());
		double vy = p1.getY() - p2.getY();
		double m = vx / vy;
		Point midpoint = Point.midpoint(p1, p2);
		double b = midpoint.getY() - (m * midpoint.getX());
		return new LineVector(m, b, vy, vx);
	}

	public double[] getVector()
	{
		return new double[]{vx, vy};
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

	@Override
	public String toString()
	{
		return "y = " + slope + "x " + (yIntercept == 0 ? "" : yIntercept < 0 ? yIntercept : "+ " + yIntercept);
	}
}

package auxiliary;

public class LineVector
{
	private final boolean vertical;
	private final double slope, intercept;
	private final double vx, vy;

	public LineVector(double slope, double yIntercept, double vx, double vy)
	{
		this.vertical = false;
		this.slope = slope;
		this.intercept = yIntercept;
		this.vx = vx;
		this.vy = vy;
	}

	public LineVector(Point p1, Point p2)
	{
		this.vertical = p1.getY() == p2.getY();

		if (vertical)
		{
			this.vx = 0;
			this.vy = Double.POSITIVE_INFINITY;
			this.slope = 0;
			this.intercept = Point.midpoint(p1, p2).getX();
		}
		else
		{
			this.vx = p1.getX() - p2.getX();
			this.vy = p1.getY() - p2.getY();
			this.slope = vy / vx;
			this.intercept = p1.getY() - (slope * p1.getX());
		}
	}

	public static LineVector perpendicularBisector(Point p1, Point p2)
	{
		if (p1.getY() == p2.getY()) return new LineVector(p1, p2);
		else
		{
			double vx = -(p1.getX() - p2.getX());
			double vy = p1.getY() - p2.getY();
			double m = vx / vy;
			Point midpoint = Point.midpoint(p1, p2);
			double b = midpoint.getY() - (m * midpoint.getX());
			return new LineVector(m, b, vy, vx);
		}
	}

	public double[] getVector()
	{
		return new double[]{vx, vy};
	}

	public double getYFromX(double x)
	{
		if (vertical) return x == intercept ? x : Double.POSITIVE_INFINITY;
		return (slope * x) + intercept;
	}

	public double getXFromY(double y)
	{
		if (vertical) return intercept;
		return (y - intercept) / slope;
	}

	public boolean isVertical()
	{
		return vertical;
	}

	public double getSlope()
	{
		if (vertical) return Double.POSITIVE_INFINITY;
		return slope;
	}

	public double getIntercept()
	{
		return intercept;
	}

	@Override
	public String toString()
	{
		if (vertical) return "x = " + intercept;
		return "y = " + slope + "x " + (intercept == 0 ? "" : intercept < 0 ? intercept : "+ " + intercept);
	}
}

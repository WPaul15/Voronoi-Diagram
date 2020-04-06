package auxiliary;

public class Parabola
{
	private Point focus;
	private double a, b, c;

	public Parabola(Point focus, double directrix)
	{
		this.focus = focus;

		double x = focus.getX();
		double y = focus.getY();
		double denominator = (2 * (y - directrix));

		this.a = 1 / denominator;
		this.b = (-2 * x) / denominator;
		this.c = (x * x + y * y - directrix * directrix) / denominator;
	}

	public static double[] intersectionX(Parabola left, Parabola right)
	{
		double ai = left.a - right.a;
		double bi = left.b - right.b;
		double ci = left.c - right.c;

		double discriminant = bi * bi - 4 * ai * ci;
		double x1, x2;

		/* Correct small negative discriminants to 0 */
		if (discriminant <= 0)
		{
			x1 = -bi / (2 * ai);
			x2 = x1;
		}
		else
		{
			x1 = (-bi + Math.sqrt(discriminant)) / (2 * ai);
			x2 = (-bi - Math.sqrt(discriminant)) / (2 * ai);
		}

		return new double[]{x1, x2};
	}

	public double getYFromX(double x)
	{
		return (a * x * x) + (b * x) + c;
	}

	public double getA()
	{
		return a;
	}

	public double getB()
	{
		return b;
	}

	public double getC()
	{
		return c;
	}
}

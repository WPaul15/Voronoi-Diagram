package display;

import auxiliary.Point;
import dcel.DCELEdge;
import dcel.DCELVertex;
import dcel.DoublyConnectedEdgeList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.Set;

/**
 * @author Willem Paul
 */
public class Visualizer
{
	//private double windowWidth, windowHeight;
	private double windowWidthMid, windowHeightMid;
	private GraphicsContext graphicsContext;
	private double scale;

	private final double pointRadius = 6;

	// TODO Make sure all DCEL vertices are visible
	public Visualizer(double windowWidth, double windowHeight, GraphicsContext graphicsContext)
	{
		//this.windowWidth = windowWidth;
		//this.windowHeight = windowHeight;
		this.windowWidthMid = windowWidth / 2;
		this.windowHeightMid = windowHeight / 2;
		this.graphicsContext = graphicsContext;
	}

	public void plotSiteEvents(Set<Point> sitePoints)
	{
		graphicsContext.setFill(Color.RED);

		for (Point point : sitePoints)
		{
			Point2D scaledPoint = scalePoint(point);
			graphicsContext.fillOval(scaledPoint.getX(), scaledPoint.getY(), pointRadius, pointRadius);
		}
	}

	public void drawDCEL(DoublyConnectedEdgeList dcel)
	{
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setLineWidth(pointRadius / 3);

		// TODO Prevent each edge from being drawn twice
		for (DCELEdge edge : dcel.getEdges())
		{
			/* Don't display bounding box edges; they aren't visible anyway */
			if ((edge.getOrigin() != null && edge.getTwin().getOrigin() != null) && edge.isVoronoiEdge())
			{
				Point2D scaledOrigin = scalePoint(edge.getOrigin().getCoordinates());
				Point2D scaledTwinOrigin = scalePoint(edge.getTwin().getOrigin().getCoordinates());
				graphicsContext.strokeLine(scaledOrigin.getX() + (pointRadius / 2),
				                           scaledOrigin.getY() + (pointRadius / 2),
				                           scaledTwinOrigin.getX() + (pointRadius / 2),
				                           scaledTwinOrigin.getY() + (pointRadius / 2));
			}
		}

		for (DCELVertex vertex : dcel.getVertices())
		{
			/* Don't display bounding box vertices; they aren't visible anyway */
			if (vertex.isVoronoiVertex())
			{
				Point2D scaledPoint = scalePoint(vertex.getCoordinates());
				graphicsContext.fillOval(scaledPoint.getX(), scaledPoint.getY(), pointRadius, pointRadius);
			}
		}
	}

	public void setScale(double minX, double maxX, double minY, double maxY)
	{
		double maxMax = Math.max(maxX, maxY);
		double maxMin = Math.max(Math.abs(minX), Math.abs(minY));
		this.scale = Math.min(windowWidthMid * 0.85, windowHeightMid * 0.85) / Math.max(maxMax, maxMin);
	}

	@SafeVarargs
	public final void setScale(Collection<? extends Point>... pointLists)
	{
		double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

		for (Collection<? extends Point> pointList : pointLists)
		{
			for (Point p : pointList)
			{
				if (p.getX() < minX) minX = p.getX();
				else if (p.getX() > maxX) maxX = p.getX();

				if (p.getY() < minY) minY = p.getY();
				else if (p.getY() > maxY) maxY = p.getY();
			}
		}

		setScale(minX, maxX, minY, maxY);
	}

	private Point2D scalePoint(Point p)
	{
		return new Point2D((scale * p.getX()) + windowWidthMid,
		                   (-scale * p.getY()) + windowHeightMid);
	}
}
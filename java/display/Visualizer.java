package display;

import auxiliary.Point;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import voronoi.dcel.DCELEdge;
import voronoi.dcel.DCELVertex;
import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.Event;

import java.util.Set;

/**
 * @author Willem Paul
 */
public class Visualizer
{
	private double windowWidth;
	private double windowHeight;
	private GraphicsContext graphicsContext;
	private double scale;

	private final double pointRadius = 6;

	// TODO Make sure all DCEL vertices are visible
	public Visualizer(double windowWidth, double windowHeight, GraphicsContext graphicsContext, double minX, double maxX, double minY, double maxY)
	{
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.graphicsContext = graphicsContext;
		setScale(minX, maxX, minY, maxY);
	}

	public void plotSiteEvents(Set<Event> events)
	{
		graphicsContext.setFill(Color.RED);

		for (Event event : events)
		{
			Point2D scaledPoint = scalePoint(event.getCoordinates());
			graphicsContext.fillOval(scaledPoint.getX(), scaledPoint.getY(), pointRadius, pointRadius);
		}
	}

	public void drawDCEL(DoublyConnectedEdgeList dcel)
	{
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.setStroke(Color.BLACK);
		graphicsContext.setLineWidth(2);

		for (DCELVertex vertex : dcel.getVertices())
		{
			Point2D scaledPoint = scalePoint(vertex.getCoordinates());
			graphicsContext.fillOval(scaledPoint.getX(), scaledPoint.getY(), pointRadius, pointRadius);
		}

		// TODO Prevent each edge from being drawn twice
		for (DCELEdge edge : dcel.getEdges())
		{
			if (edge.getOrigin() != null && edge.getTwin().getOrigin() != null)
			{
				Point2D scaledOrigin = scalePoint(edge.getOrigin().getCoordinates());
				Point2D scaledTwinOrigin = scalePoint(edge.getTwin().getOrigin().getCoordinates());
				graphicsContext.strokeLine(scaledOrigin.getX() + (pointRadius / 2),
				                           scaledOrigin.getY() + (pointRadius / 2),
				                           scaledTwinOrigin.getX() + (pointRadius / 2),
				                           scaledTwinOrigin.getY() + (pointRadius / 2));
			}
		}
	}

	private void setScale(double minX, double maxX, double minY, double maxY)
	{
		double maxMax = Math.max(maxX, maxY);
		double maxMin = Math.max(Math.abs(minX), Math.abs(minY));
		this.scale = Math.min((windowWidth * 0.85) / 2.0, (windowHeight * 0.85) / 2.0) / Math.max(maxMax, maxMin);
	}

	private Point2D scalePoint(Point p)
	{
		return new Point2D((scale * p.getX()) + (windowWidth / 2.0), (-scale * p.getY()) + (windowHeight / 2.0));
	}
}
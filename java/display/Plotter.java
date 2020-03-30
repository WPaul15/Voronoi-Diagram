package display;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import voronoi.dcel.DCELVertex;
import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.Event;

import java.util.Set;

/**
 * @author Willem Paul
 */
public class Plotter
{
	private double windowWidth;
	private double windowHeight;
	private GraphicsContext graphicsContext;
	private double scale;

	public Plotter(double windowWidth, double windowHeight, GraphicsContext graphicsContext, double minX, double maxX, double minY, double maxY)
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
			Point2D scaledPoint = scalePoint(event.getCoordinates().getX(), event.getCoordinates().getY(), scale);
			graphicsContext.fillOval(scaledPoint.getX(), scaledPoint.getY(), 5, 5);
		}
	}

	public void plotDCEL(DoublyConnectedEdgeList dcel)
	{
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.setStroke(Color.BLACK);

		for (DCELVertex vertex : dcel.getVertices())
		{
			Point2D scaledPoint = scalePoint(vertex.getCoordinates().getX(), vertex.getCoordinates().getY(), scale);
			graphicsContext.fillOval(scaledPoint.getX(), scaledPoint.getY(), 5, 5);
		}
	}

	private void setScale(double minX, double maxX, double minY, double maxY)
	{
		double maxMax = Math.max(maxX, maxY);
		double maxMin = Math.max(Math.abs(minX), Math.abs(minY));
		this.scale = Math.min((windowWidth * 0.85) / 2.0, (windowHeight * 0.85) / 2.0) / Math.max(maxMax, maxMin);
	}

	private Point2D scalePoint(double x, double y, double scale)
	{
		return new Point2D((scale * x) + (windowWidth / 2.0), (-scale * y) + (windowHeight / 2.0));
	}
}
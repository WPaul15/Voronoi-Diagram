package voronoi.dcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Willem Paul
 */
public class DoublyConnectedEdgeList
{
	private List<DCELVertex> vertices;
	private List<DCELEdge> edges;
	private List<DCELFace> faces;

	public DoublyConnectedEdgeList()
	{
		this.vertices = new ArrayList<>();
		this.edges = new ArrayList<>();
		this.faces = new ArrayList<>();
	}

	// TODO Implement
	public void computeBoundingBox()
	{
		double minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		double minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

		for (DCELVertex v : vertices)
		{
			double x = v.getCoordinates().getX();
			double y = v.getCoordinates().getY();

			if (x < minX) minX = x;
			else if (x > maxX) maxX = x;

			if (y < minY) minY = y;
			else if (y > maxY) maxY = y;
		}

//		DCELVertex b1 = new DCELVertex(1, DCELVertex.VertexType.BOUNDING_VERTEX, new Point(minX - 5, minY - 5)); //Lower left
//		DCELVertex b2 = new DCELVertex(2, DCELVertex.VertexType.BOUNDING_VERTEX, new Point(maxX + 5, minY - 5)); //Lower right
//		DCELVertex b3 = new DCELVertex(3, DCELVertex.VertexType.BOUNDING_VERTEX, new Point(maxX + 5, maxY + 5)); //Upper right
//		DCELVertex b4 = new DCELVertex(4, DCELVertex.VertexType.BOUNDING_VERTEX, new Point(minX - 5, maxY + 5)); //Upper left
//
//		DCELEdge e1 = new DCELEdge(b1, null);
	}

	public List<DCELVertex> getVertices()
	{
		return vertices;
	}

	public void setVertices(List<DCELVertex> vertices)
	{
		this.vertices = vertices;
	}

	public List<DCELEdge> getEdges()
	{
		return edges;
	}

	public void setEdges(List<DCELEdge> edges)
	{
		this.edges = edges;
	}

	public List<DCELFace> getFaces()
	{
		return faces;
	}

	public void setFaces(List<DCELFace> faces)
	{
		this.faces = faces;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		for (DCELVertex v : vertices)
		{
			builder.append(v.toString()).append("\n\n");
		}

		for (DCELFace f : faces)
		{
			builder.append(f.toString()).append("\n\n");
		}

		for (DCELEdge e : edges)
		{
			builder.append(e.toString()).append("\n\n");
		}

		return builder.toString();
	}
}
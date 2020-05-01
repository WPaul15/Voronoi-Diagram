package delaunay;

import dcel.DCELEdge;
import dcel.DCELFace;
import dcel.DCELVertex;
import dcel.DoublyConnectedEdgeList;
import voronoi.VoronoiDiagram;

import java.util.List;

/**
 * @author Willem Paul
 */
public class DelaunayTriangulation extends DoublyConnectedEdgeList
{
	public DelaunayTriangulation()
	{
		super();
	}

	/**
	 * Creates a Delaunay triangulation, stored as a {@code DoublyConnectedEdgeList}, from the given Voronoi diagram.
	 *
	 * @param voronoiDiagram the Voronoi diagram from which to create the Delaunay trianguation
	 */
	public DelaunayTriangulation(VoronoiDiagram voronoiDiagram)
	{
		super();
		createFromVoronoiDiagram(voronoiDiagram);
	}

	private void createFromVoronoiDiagram(VoronoiDiagram voronoiDiagram)
	{
		for (DCELFace f : voronoiDiagram.getFaces())
		{
			if (f.getOuterComponent() != null) vertices.add(f.getSite());
		}

		boolean voronoiVertexExists = false;

		for (DCELVertex v : voronoiDiagram.getVertices())
		{
			if (v.isVoronoiVertex())
			{
				voronoiVertexExists = true;

				DCELFace delaunayFace = new DCELFace(DCELFace.FaceType.DELAUNAY_TRIANGLE, v.getIndex(), null);
				faces.add(delaunayFace);
				List<DCELFace> incidentFaces = v.getIncidentFaces();

				for (int i = 0; i < incidentFaces.size(); i++)
				{
					DCELVertex currentVertex = incidentFaces.get(i).getSite();
					DCELVertex counterclockwiseNeighbor = incidentFaces.get((i + 1) % incidentFaces.size()).getSite();

					/* The vertices are already connected, so we don't need to do anything. */
					if ((currentVertex.getIncidentEdge() != null && counterclockwiseNeighbor.getIncidentEdge() != null) &&
							(currentVertex.getIncidentEdge().equals(counterclockwiseNeighbor.getIncidentEdge().getTwin())))
						continue;

					DCELEdge e1 = new DCELEdge(DCELEdge.EdgeType.DELAUNAY_EDGE);
					DCELEdge e2 = new DCELEdge(DCELEdge.EdgeType.DELAUNAY_EDGE, e1);
					edges.add(e1);
					edges.add(e2);

					e1.setOrigin(currentVertex);
					e2.setOrigin(counterclockwiseNeighbor);

					e1.setIncidentFace(delaunayFace);
					e2.setIncidentFace(unboundedFace);

					if (currentVertex.getIncidentEdge() == null)
					{
						if (counterclockwiseNeighbor.getIncidentEdge() != null)
						{
							DCELEdge prevIncomingEdge = counterclockwiseNeighbor.getPreviousIncomingEdge();

							counterclockwiseNeighbor.getIncidentEdge().setIncidentFace(delaunayFace);

							e1.setNext(counterclockwiseNeighbor.getIncidentEdge());
							counterclockwiseNeighbor.getIncidentEdge().setPrev(e1);
							e2.setPrev(prevIncomingEdge);
							prevIncomingEdge.setNext(e2);
						}
						else counterclockwiseNeighbor.setIncidentEdge(e2);

						currentVertex.setIncidentEdge(e1);
					}
					else
					{
						delaunayFace.setOuterComponent(e1);
						unboundedFace.setInnerComponents(e2);

						if (counterclockwiseNeighbor.getIncidentEdge() == null)
						{
							DCELEdge prevIncomingEdge = currentVertex.getPreviousIncomingEdge();

							e2.setNext(currentVertex.getIncidentEdge());
							currentVertex.getIncidentEdge().setPrev(e2);
							e1.setPrev(prevIncomingEdge);
							prevIncomingEdge.setNext(e1);
						}
						else
						{
							DCELEdge nextOutgoingEdge = currentVertex.getNextOutgoingEdge();

							e1.setPrev(currentVertex.getIncidentEdge().getTwin());
							currentVertex.getIncidentEdge().getTwin().setNext(e1);
							e2.setNext(nextOutgoingEdge);
							nextOutgoingEdge.setPrev(e2);

							/* Close the triangle. */
							e1.setNext(counterclockwiseNeighbor.getIncidentEdge());
							counterclockwiseNeighbor.getIncidentEdge().setPrev(e1);
							e2.setPrev(counterclockwiseNeighbor.getIncidentEdge().getTwin());
							counterclockwiseNeighbor.getIncidentEdge().getTwin().setNext(e2);
						}

						currentVertex.setIncidentEdge(e1);
						counterclockwiseNeighbor.setIncidentEdge(e2);
					}
				}
			}
			/* If this is true, then we have the case in which all site points are collinear; there are no Voronoi
			vertices. */
			else if (v.isBoundingVertex() && !voronoiVertexExists && voronoiDiagram.getVertices().size() >= 4)
			{
				createLinearTriangulation(voronoiDiagram);
				break;
			}
			/* Since the Voronoi vertices are listed first, we can skip the rest once we hit a bounding vertex. */
			else break;
		}
	}

	private void createLinearTriangulation(VoronoiDiagram voronoiDiagram)
	{
		DCELEdge prevEdge = null;

		for (DCELEdge e : voronoiDiagram.getEdges())
		{
			if (e.isVoronoiEdge())
			{
				if (prevEdge == null) prevEdge = e;

				/* Hack to avoid computing duplicates; Each pair of twin half-edges in the Voronoi diagram is listed together */
				if (!prevEdge.equals(e.getTwin()))
				{
					DCELVertex v1 = e.getIncidentFace().getSite();
					DCELVertex v2 = e.getTwin().getIncidentFace().getSite();

					DCELEdge e1 = new DCELEdge(DCELEdge.EdgeType.DELAUNAY_EDGE);
					DCELEdge e2 = new DCELEdge(DCELEdge.EdgeType.DELAUNAY_EDGE, e1);
					edges.add(e1);
					edges.add(e2);

					e1.setOrigin(v1);
					e2.setOrigin(v2);

					e1.setIncidentFace(unboundedFace);
					e2.setIncidentFace(unboundedFace);
					unboundedFace.setInnerComponents(e1);

					e1.setNext(e2);
					e2.setPrev(e1);

					if (v1.getIncidentEdge() != null)
					{
						e2.setNext(v1.getIncidentEdge());
						v1.getIncidentEdge().setPrev(e2);
						e1.setPrev(v1.getIncidentEdge().getTwin());
						v1.getIncidentEdge().getTwin().setNext(e1);
					}
					else
					{
						e1.setPrev(e2);
						e2.setNext(e1);
					}

					v1.setIncidentEdge(e1);
					v2.setIncidentEdge(e2);
				}

				prevEdge = e;
			}
		}
	}
}

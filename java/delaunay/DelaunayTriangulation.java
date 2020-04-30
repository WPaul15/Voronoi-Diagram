package delaunay;

import dcel.DCELEdge;
import dcel.DCELFace;
import dcel.DCELVertex;
import dcel.DoublyConnectedEdgeList;
import voronoi.VoronoiDiagram;

import java.util.List;

public class DelaunayTriangulation extends DoublyConnectedEdgeList
{
	public DelaunayTriangulation()
	{
		super();
	}

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

		for (DCELVertex v : voronoiDiagram.getVertices())
		{
			if (v.isVoronoiVertex())
			{
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
			/* Since the Voronoi vertices are listed first, we can skip the rest once we hit a bounding vertex. */
			else if (v.isBoundingVertex()) break;
		}
	}
}

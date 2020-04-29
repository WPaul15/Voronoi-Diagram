package delaunay;

import dcel.DCELEdge;
import dcel.DCELFace;
import dcel.DCELVertex;
import dcel.DoublyConnectedEdgeList;
import voronoi.VoronoiDiagram;

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
		DCELFace uf = new DCELFace(DCELFace.FaceType.UNBOUNDED, 0, null);
		faces.add(uf);

		for (DCELFace f : voronoiDiagram.getFaces())
		{
			if (f.getOuterComponent() != null) vertices.add(f.getSite());
		}

		DCELEdge prevEdge = null;

		for (DCELEdge e : voronoiDiagram.getEdges())
		{
			if (e.isVoronoiEdge())
			{
				if (prevEdge == null) prevEdge = e;

				/* Hack to avoid computing duplicates; Each pair of twin half-edges in the Voronoi diagram is listed together */
				if (!prevEdge.equals(e.getTwin()))
				{
					// TODO Mostly Vertical Lines Test causes NullPointerException becuase an edge doesn't have an incident face
					DCELVertex v1 = e.getIncidentFace().getSite();
					DCELVertex v2 = e.getTwin().getIncidentFace().getSite();

					DCELEdge e1 = new DCELEdge();
					DCELEdge e2 = new DCELEdge(e1);
					edges.add(e1);
					edges.add(e2);

					e1.setOrigin(v1);
					e2.setOrigin(v2);

					if (v1.getIncidentEdge() != null && v2.getIncidentEdge() == null)
					{
						DCELEdge lastOutgoingEdge = v1.getIncidentEdge();
						DCELEdge lastIncomingEdge = v1.getLastIncomingEdge();

						e2.setNext(lastOutgoingEdge);
						lastOutgoingEdge.setPrev(e2);
						e1.setPrev(lastIncomingEdge);
						lastIncomingEdge.setNext(e1);
					}
					else if (v1.getIncidentEdge() != null && v2.getIncidentEdge() != null)
					{
						DCELEdge lastOutgoingEdge = v1.getLastOutgoingEdge();

						e1.setPrev(v1.getIncidentEdge().getTwin());
						v1.getIncidentEdge().getTwin().setNext(e1);

						e2.setNext(lastOutgoingEdge);
						lastOutgoingEdge.setPrev(e2);
					}

					if (v2.getIncidentEdge() != null)
					{
						e1.setNext(v2.getIncidentEdge());
						v2.getIncidentEdge().setPrev(e1);

						e2.setPrev(v2.getIncidentEdge().getTwin());
						v2.getIncidentEdge().getTwin().setNext(e2);
					}

					v1.setIncidentEdge(e1);
					v2.setIncidentEdge(e2);
				}

				prevEdge = e;
			}
		}


//		for (DCELVertex v : voronoiDiagram.getVertices())
//		{
//			if (v.isVoronoiVertex())
//			{
//				List<DCELFace> incidentFaces = v.getIncidentFaces();
//
//				/* If there are three incident faces, we are dealing with the general case. Otherwise, we need to add
//				   extra edges. */
//				if (faces.size() == 1 && incidentFaces.size() == 3)
//				{
//					DCELFace triangle = new DCELFace(DCELFace.FaceType.DELAUNAY_TRIANGLE, v.getIndex(), null);
//					faces.add(triangle);
//
//					List<DCELVertex> delaunayVertices = new ArrayList<>();
//
//					for (DCELFace f : incidentFaces)
//					{
//						delaunayVertices.add(f.getSite());
//					}

//					DCELVertex v1 = delaunayVertices.get(0);
//					DCELVertex v2 = delaunayVertices.get(1);
//					DCELVertex v3 = delaunayVertices.get(2);
//
//					DCELEdge e12 = new DCELEdge();
//					DCELEdge e21 = new DCELEdge(e12);
//
//					DCELEdge e23 = new DCELEdge();
//					DCELEdge e32 = new DCELEdge(e23);
//
//					DCELEdge e31 = new DCELEdge();
//					DCELEdge e13 = new DCELEdge(e31);
//
//					edges.add(e12);
//					edges.add(e21);
//					edges.add(e23);
//					edges.add(e32);
//					edges.add(e31);
//					edges.add(e13);
//
//					e12.setOrigin(v1);
//					e21.setOrigin(v2);
//					e23.setOrigin(v2);
//					e32.setOrigin(v3);
//					e31.setOrigin(v3);
//					e13.setOrigin(v1);
//
//					v1.setIncidentEdge(e12);
//					v2.setIncidentEdge(e23);
//					v3.setIncidentEdge(e31);
//
//					triangle.setOuterComponent(e21);
//
//					e12.setIncidentFace(uf);
//					e21.setIncidentFace(triangle);
//					e23.setIncidentFace(uf);
//					e32.setIncidentFace(triangle);
//					e31.setIncidentFace(uf);
//					e13.setIncidentFace(triangle);
//
//					e12.setNext(e23);
//					e21.setNext(e13);
//					e23.setNext(e31);
//					e32.setNext(e21);
//					e31.setNext(e12);
//					e13.setNext(e32);
//
//					e12.setPrev(e31);
//					e21.setPrev(e32);
//					e23.setPrev(e12);
//					e32.setPrev(e13);
//					e31.setPrev(e23);
//					e13.setPrev(e21);
	}
}

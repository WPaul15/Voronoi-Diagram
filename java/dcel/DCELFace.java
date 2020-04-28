package dcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Willem Paul
 */
public class DCELFace
{
	private final DCELVertex site;
	private final FaceType type;

	public DCELFace(FaceType type, int index, DCELEdge outerComponent, DCELEdge... innerComponents)
	{
		this.site = null;
		this.type = type;
		this.index = index;
		this.outerComponent = outerComponent;
		this.innerComponents = new ArrayList<>();
		Collections.addAll(this.innerComponents, innerComponents);
	}

	private final int index;
	private DCELEdge outerComponent;
	private List<DCELEdge> innerComponents;

	public DCELFace(DCELVertex site, FaceType type, int index, DCELEdge outerComponent, DCELEdge... innerComponents)
	{
		this.site = site;
		this.type = type;
		this.index = index;
		this.outerComponent = outerComponent;
		this.innerComponents = new ArrayList<>();
		Collections.addAll(this.innerComponents, innerComponents);
	}

	public DCELVertex getSite()
	{
		return site;
	}

	public String getName()
	{
		switch (type)
		{
			case VORONOI_CELL:
				return "c" + index;
			case DELAUNAY_TRIANGLE:
				return "t" + index;
			case UNBOUNDED:
				return "uf";
			default:
				return "f" + index;
		}
	}

	public int getIndex()
	{
		return index;
	}

	public enum FaceType
	{
		VORONOI_CELL,
		DELAUNAY_TRIANGLE,
		UNBOUNDED
	}

	public DCELEdge getOuterComponent()
	{
		return outerComponent;
	}

	public void setOuterComponent(DCELEdge outerComponent)
	{
		this.outerComponent = outerComponent;
	}

	public List<DCELEdge> getInnerComponents()
	{
		return innerComponents;
	}

	public void setInnerComponents(List<DCELEdge> innerComponents)
	{
		this.innerComponents = innerComponents;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getName()).append("  ");

		if (outerComponent == null) builder.append("nil").append("  ");
		else builder.append(outerComponent.getName()).append("  ");

		if (innerComponents.isEmpty()) builder.append("nil");
		else
		{
			if (innerComponents.size() == 1)
			{
				builder.append(innerComponents.get(0).getName());
			}
			else
			{
				builder.append('[');
				for (DCELEdge e : innerComponents)
				{
					builder.append(e.getName()).append("; ");
				}
				builder.append(']');
			}
		}

		return builder.toString();
	}
}

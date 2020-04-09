package dcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Willem Paul
 */
public class DCELFace
{
	private final String name;
	private DCELEdge outerComponent;
	private List<DCELEdge> innerComponents;

	public DCELFace(int index, DCELEdge outerComponent, DCELEdge... innerComponents)
	{
		this.name = outerComponent == null ? "uf" : "c" + index;
		this.outerComponent = outerComponent;
		this.innerComponents = new ArrayList<>();
		Collections.addAll(this.innerComponents, innerComponents);
	}

	public String getName()
	{
		return name;
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
		builder.append(name).append("  ");

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
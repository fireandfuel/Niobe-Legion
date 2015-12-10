package niobe.legion.shared.module;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ModuleDependencyItem<MI extends ModuleInstance>
{
	private MI moduleInstance;

	private boolean visited;

	private List<ModuleDependencyItem<MI>> parents = new ArrayList<ModuleDependencyItem<MI>>();

	private List<ModuleDependencyItem<MI>> children = new ArrayList<ModuleDependencyItem<MI>>();

	public ModuleDependencyItem(MI moduleInstance)
	{
		this.moduleInstance = moduleInstance;
	}

	public String getName()
	{
		return this.moduleInstance.getName();
	}

	public String getVersion()
	{
		return this.moduleInstance.getVersion();
	}

	public String[] getDependencies()
	{
		return this.moduleInstance.getDependencies();
	}

	public int getState()
	{
		return this.moduleInstance.getState();
	}

	public void addParent(ModuleDependencyItem<MI> parent)
	{
		this.parents.add(parent);
	}

	public void removeParent(ModuleDependencyItem<MI> parent)
	{
		this.parents.remove(parent);
	}

	public boolean containsParent(ModuleDependencyItem<MI> parent)
	{
		return this.parents.contains(parent);
	}

	public Stream<ModuleDependencyItem<MI>> getParents()
	{
		return this.parents.stream();
	}

	public void addChild(ModuleDependencyItem<MI> child)
	{
		this.children.add(child);
	}

	public void removeChild(ModuleDependencyItem<MI> child)
	{
		this.children.remove(child);
	}

	public boolean containsChild(ModuleDependencyItem<MI> child)
	{
		return this.children.contains(child);
	}

	public Stream<ModuleDependencyItem<MI>> getChildren()
	{
		return this.children.stream();
	}

	public boolean isVisited()
	{
		return visited;
	}

	public void setVisited(boolean visited)
	{
		this.visited = visited;
	}
}

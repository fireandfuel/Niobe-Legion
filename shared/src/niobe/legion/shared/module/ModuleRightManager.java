package niobe.legion.shared.module;

import niobe.legion.shared.data.IRight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by nk on 03.07.15.
 */
public class ModuleRightManager
{
	final static List<IRight> rights = new ArrayList<IRight>();

	private ModuleRightManager()
	{

	}

	public final static void addRights(IRight... rightsToAdd)
	{
		if (rightsToAdd != null && rightsToAdd.length > 0)
		{
			Stream.of(rightsToAdd).filter(right -> !rights.contains(right)).forEach(rights::add);
		}
	}

	public final static void removeRights(IRight... rightsToRemove)
	{
		if (rightsToRemove != null && rightsToRemove.length > 0)
		{
			Stream.of(rightsToRemove).forEach(rights::remove);
		}
	}

	public final static IRight[] getRights()
	{
		return rights.toArray(new IRight[rights.size()]);
	}
}

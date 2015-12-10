package niobe.legion.client.module;

import niobe.legion.shared.data.IRight;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
public interface IModuleTab
{
	String getTabFileName();

	boolean isCloseable();

	IRight[] getNeededGroupRights();
}

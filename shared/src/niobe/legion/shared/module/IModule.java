package niobe.legion.shared.module;

import niobe.legion.shared.ICommunicator;
import niobe.legion.shared.data.IRight;

/**
 * This Interface is part of the Niobe Legion Module API Level 1
 */
public interface IModule
{
    /**
     * starts the module
     *
     * @return module is started successfully
     */
    boolean startModule();

    /**
     * stops the module
     *
     * @return module is stopped successfully
     */
    boolean stopModule();

    /**
     * @return a module communicator, may be null if not needed
     */
    ICommunicator newCommunicator(ICommunicator parent);

    /**
     * @return the module communicator namespace, may be null if not needed
     */
    String getNamespace();

    /**
     * @return the module communicator namespace uri, may be null if not needed
     */
    String getNamespaceURI();

    /**
     * @return the rights the module sets at the host
     */
    IRight[] getRights();
}

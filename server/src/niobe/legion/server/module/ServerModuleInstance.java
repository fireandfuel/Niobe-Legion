package niobe.legion.server.module;

import java.io.File;
import niobe.legion.server.AbstractDatabase;
import niobe.legion.server.Server;
import niobe.legion.shared.module.ModuleInstance;
import niobe.legion.shared.module.ModuleRightManager;

public class ServerModuleInstance extends ModuleInstance
{

    private AbstractDatabase database;

    public ServerModuleInstance(String dependencies, String conflicts, String name, String version, String contact,
                                String description, File moduleFile, String moduleClass)
    {
        super(dependencies, conflicts, name, version, contact, description, moduleFile, moduleClass);
    }

    @Override
    public synchronized void stop()
    {
        if(this.module != null && this.state == ModuleInstance.RUNNING)
        {
            this.state = ModuleInstance.STOPPING;
            if(this.module.stopModule())
            {
                if(this.module.getNamespaceURI() != null && !this.module.getNamespaceURI().isEmpty())
                {
                    Server.getCommunicators().forEach(communicator -> communicator
                            .removeModuleCommunicator(this.module.getNamespaceURI()));
                }
                if(this.module.getRights() != null && this.module.getRights().length > 0)
                {
                    ModuleRightManager.removeRights(this.module.getRights());
                }
                this.state = ModuleInstance.INITIALIZED;
            }
        }
    }

    public void setDatabase(AbstractDatabase database)
    {
        this.database = database;
    }

    public AbstractDatabase getDatabase()
    {
        return database;
    }
}

/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ModuleInstance.java) is part of Niobe Legion (module niobe-legion-shared_main).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared.module;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Stream;
import niobe.legion.shared.data.IRight;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Module state transitions:<br>
 * Load module: UNINITIALIZED -> LOADING -> INITIALIZED<br>
 * Unload module: INITIALIZED -> UNLOADING -> UNINITIALIZED<br>
 * Start module: INITIALIZED <-> STARTING -> RUNNING<br>
 * Stop module: RUNNING <-> STOPPING -> INITIALIZED<br>
 * Forced stop module: RUNNING -> TERMINATED<br>
 * Module with missing dependencies: UNINITIALIZED -> MISSING_DEPENDENCIES<br>
 * Module with conflicts: UNINITIALIZED -> IN_CONFLICT<br>
 * Module with database conflict (server module only): UNINITIALIZED -> DATABASE_CONFLICT
 */
public abstract class ModuleInstance
{
    public final static Logger LOG = LogManager.getLogger(ModuleInstance.class);

    public final static int UNINITIALIZED = 0;
    public final static int LOADING = 1;
    public final static int INITIALIZED = 2;
    public final static int STARTING = 3;
    public final static int RUNNING = 4;
    public final static int STOPPING = 5;
    public final static int UNLOADING = 6;
    public final static int MISSING_DEPENDENCIES = -1;
    public final static int IN_CONFLICT = -2;
    public final static int DATABASE_CONFLICT = -3;
    public final static int TERMINATED = -4;
    public final static int MISSING_LIBRARIES = -5;
    public final static int DEPENDENCY_CYCLE = -6;

    private final String[] dependencies;
    private final String[] conflicts;
    private final String name;
    private final String version;
    private final String contact;
    private final String description;
    private URLClassLoader loader;

    private final File moduleFile;
    private final String moduleClass;
    private final String[] moduleLibraries;

    protected IModule module;
    protected int state;

    public ModuleInstance(String dependencies, String conflicts, String name, String version, String contact,
                          String description, File moduleFile, String moduleClass, String moduleLibraries)
    {
        this.dependencies = dependencies != null ? dependencies.split(", ") : new String[0];
        this.conflicts = conflicts != null ? conflicts.split(", ") : new String[0];
        this.name = name;
        this.version = version;
        this.contact = contact;
        this.description = description;
        this.moduleFile = moduleFile;
        this.moduleClass = moduleClass;
        this.moduleLibraries = moduleLibraries != null ? moduleLibraries.split(" ") : new String[0];
        this.state = ModuleInstance.UNINITIALIZED;
    }

    /**
     * @param loader the loader to set,
     *               only if this instance's loader is not set and parameter is not null
     */
    public void setLoader(URLClassLoader loader)
    {
        if(loader != null && this.loader == null)
        {
            this.loader = loader;
        }
    }

    /**
     * @param module the module to set
     */
    public void setModule(IModule module)
    {
        this.module = module;
        this.state = ModuleInstance.INITIALIZED;
    }

    /**
     * @return the depends
     */
    public String[] getDependencies()
    {
        return this.dependencies;
    }

    /**
     * @return the conflicts
     */
    public String[] getConflicts()
    {
        return this.conflicts;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * @return the contact
     */
    public String getContact()
    {
        return this.contact;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @return the full license text
     */
    public String getFullLicense()
    {
        return this.module.getLicenseText();
    }

    /**
     * @return the moduleFile
     */
    public File getModuleFile()
    {
        return this.moduleFile;
    }

    /**
     * @return the moduleClass
     */
    public String getModuleClass()
    {
        return this.moduleClass;
    }

    /**
     * @return the moduleLibraries
     */
    public String[] getModuleLibraries()
    {
        return this.moduleLibraries;
    }

    public URL[] buildClassLoaderURLs()
    {
        File folder = this.moduleFile.getParentFile();
        return Stream
                .concat(Stream.of(this.moduleLibraries).map(lib -> this.convertToURL(new File(folder, "lib/" + lib))),
                        Stream.of(this.convertToURL(this.moduleFile))).toArray(count -> new URL[count]);
    }

    private URL convertToURL(File file)
    {
        try
        {
            return file.toURI().toURL();
        } catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void start()
    {
        if(this.module != null && this.state == ModuleInstance.INITIALIZED)
        {
            this.state = ModuleInstance.STARTING;
            if(this.module.startModule())
            {
                this.state = ModuleInstance.RUNNING;
            } else
            {
                this.state = INITIALIZED;
            }
        }
    }

    public abstract void stop();

    public synchronized void unload()
    {
        if(this.loader != null)
        {
            if(this.state == ModuleInstance.INITIALIZED)
            {
                this.state = ModuleInstance.UNLOADING;
                try
                {
                    this.loader.close();
                    this.state = ModuleInstance.UNINITIALIZED;
                } catch(IOException e)
                {
                    LOG.catching(e);
                }
            }
        }
    }

    public synchronized int getState()
    {
        return this.state;
    }

    public synchronized void setState(int state)
    {
        if(this.state == UNINITIALIZED && (state == MISSING_DEPENDENCIES || state == IN_CONFLICT || state == DATABASE_CONFLICT || state == DEPENDENCY_CYCLE))
        {
            this.state = state;
        }
    }

    protected String getStateAsString()
    {
        switch(this.state)
        {
            case DEPENDENCY_CYCLE:
                return "cyclic dependencies";
            case MISSING_LIBRARIES:
                return "missing libraries";
            case TERMINATED:
                return "terminated";
            case MISSING_DEPENDENCIES:
                return "missing dependencies";
            case IN_CONFLICT:
                return "in conflict";
            case DATABASE_CONFLICT:
                return "database in conflict";
            case UNINITIALIZED:
                return "uninitialized";
            case INITIALIZED:
                return "initialized";
            case STARTING:
                return "starting";
            case RUNNING:
                return "running";
            case STOPPING:
                return "stopping";
            default:
                return "not set";
        }
    }

    public IRight[] getRights()
    {
        if(this.module == null || this.state == MISSING_DEPENDENCIES || this.state == IN_CONFLICT || this.state == UNINITIALIZED || this.state == DATABASE_CONFLICT || this.state == TERMINATED || this.state == MISSING_LIBRARIES || this.state == DEPENDENCY_CYCLE)
        {
            return null;
        }
        return this.module.getRights();
    }

    @Override
    public String toString()
    {
        return "ModuleInstance name=" + this.name + ", version=" + this.version + ", class=" + this.moduleClass + ", state=" + this
                .getStateAsString();
    }
}

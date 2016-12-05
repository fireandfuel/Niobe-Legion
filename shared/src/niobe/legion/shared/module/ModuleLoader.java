/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ModuleLoader.java) is part of Niobe Legion (module niobe-legion-shared_main).
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ModuleLoader<MI extends ModuleInstance>
{
    private final static Logger LOG = LogManager.getLogger(ModuleLoader.class);

    protected final static int MODULE_API_LEVEL = 1;
    protected final List<MI> MODULE_INSTANCES = new ArrayList<MI>();

    private String hostModuleName;
    private String hostModuleVersion;
    private final String modulePath;

    private final List<ModuleDependencyItem<MI>> moduleDependencyItems;

    protected ModuleLoader(String hostModuleName, String hostModuleVersion, String modulePath)
    {
        this.hostModuleName = hostModuleName;
        this.hostModuleVersion = hostModuleVersion;
        this.modulePath = modulePath;

        this.moduleDependencyItems = new ArrayList<ModuleDependencyItem<MI>>();

        File[] moduleFiles = this.findModuleFiles(this.modulePath);
        for(File moduleFile : moduleFiles)
        {
            try
            {
                LOG.debug("load module " + moduleFile.getName());
                this.loadModule(moduleFile);
            } catch(IOException e)
            {
                LOG.catching(e);
            }
        }

        buildDependencies(MODULE_INSTANCES.stream().filter(this::checkRequirements).collect(Collectors.toList()));

        ArrayBlockingQueue<List<MI>> initQueue = buildDependencyQueue();
        if(initQueue != null)
        {
            initQueue.forEach(instances -> instances.parallelStream().forEach(instance ->
                                                                              {
                                                                                  try
                                                                                  {
                                                                                      this.initModule(instance);
                                                                                      this.startModule(instance);
                                                                                  } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e)
                                                                                  {
                                                                                      instance.setState(ModuleInstance.UNINITIALIZED);
                                                                                      LOG.catching(e);
                                                                                  }
                                                                              }));
        }

    }

    protected abstract void initModule(MI instance) throws ClassNotFoundException, InstantiationException,
                                                           IllegalAccessException, IOException;

    private File[] findModuleFiles(String rootFolder)
    {
        if(rootFolder != null)
        {
            File root = new File(rootFolder);
            if(root.exists())
            {
                return root.listFiles(pathname -> pathname.getName().endsWith(".jar"));
            }
        }

        // Folder doesn't exists ...
        return new File[0];
    }

    protected abstract void loadModule(File jarFile) throws IOException;

    /**
     * Check all requirements of the plugin interface
     *
     * @param module
     * @return true, all requirements fulfilled; false else
     */
    private boolean checkRequirements(ModuleInstance module)
    {
        Optional<String> depends = this.checkDependencies(module.getDependencies());
        Optional<String> conflict = this.checkConflicts(module.getConflicts());

        if(depends.isPresent())
        {
            LOG.error("Dependency check for plugin " + module.getName() + " failed. It depends on " + depends
                    .get() + " !");
            module.setState(ModuleInstance.MISSING_DEPENDENCIES);
            return false;
        }
        if(conflict.isPresent())
        {
            LOG.error("Conflict check for plugin " + module.getName() + " failed. It conflicts with " + conflict
                    .get() + " !");
            module.setState(ModuleInstance.IN_CONFLICT);
            return false;
        }

        if(!this.checkLibraries(module.getModuleLibraries(), this.modulePath))
        {
            LOG.error("Library check for plugin " + module.getName() + " failed!");
            module.setState(ModuleInstance.MISSING_LIBRARIES);
            return false;
        }

        return true;
    }

    private boolean checkLibraries(String[] moduleLibraries, String rootFolder)
    {
        if(rootFolder != null)
        {
            File root = new File(rootFolder);
            if(root.exists())
            {
                if(moduleLibraries.length == 0) return true;

                return Stream.of(moduleLibraries).allMatch(lib -> new File(root, "lib/" + lib).exists());
            }
        }
        return false;
    }

    /**
     * Checks the dependencies for the plugin
     *
     * @param list
     * @return null, dependencies fulfilled; first entry which is missing, else
     */
    private Optional<String> checkDependencies(String[] list)
    {
        return Arrays.stream(list).filter(check.negate()).findFirst();
    }

    /**
     * Checks for conflicts of the plugin
     *
     * @param list
     * @return null, no conflict; first entry which conflicts, else
     */
    private Optional<String> checkConflicts(String[] list)
    {
        return Arrays.stream(list).filter(check.negate()).findFirst();
    }

    /**
     * Check whether the plugin is in the loadedPlugin list
     *
     * @param entry should be formatted in the manner name_of_plugin$version or name_of_plugin$version$minimal_status,
     * version is a regex
     * @return true, plugin found; false, else
     */
    private Predicate<String> check = (String entry) ->
    {
        String[] values = entry.split("\\$");

        String name = values[0];
        String versionRegex;
        if(values.length >= 2)
        {
            versionRegex = values[1];
        } else
        {
            versionRegex = null;
        }

        Optional<String> found = MODULE_INSTANCES.stream()
                .filter(module -> module.getName().equals(name) && versionRegex != null && module.getVersion()
                        .matches(versionRegex) && module.getState() != ModuleInstance.MISSING_DEPENDENCIES && module
                        .getState() != ModuleInstance.IN_CONFLICT).map(ModuleInstance::getName).findFirst();

        if(found.isPresent())
        {
            return true;
        }

        return this.hostModuleName != null && this.hostModuleVersion != null && this.hostModuleName
                .equals(this.hostModuleName) && this.hostModuleVersion.matches(versionRegex);

    };

    public List<String> getModuleNames()
    {
        List<String> moduleNames = MODULE_INSTANCES.stream().map(ModuleInstance::getName).collect(Collectors.toList());

        return moduleNames;
    }

    protected ArrayBlockingQueue<List<MI>> buildDependencyQueue()
    {
        Map<Integer, List<MI>> priorityMap = new TreeMap<Integer, List<MI>>();

        moduleDependencyItems.forEach(item ->
                                      {
                                          int priority = item.getPriority();
                                          if(item.getModuleInstance().getState() == ModuleInstance.UNINITIALIZED || item
                                                  .getModuleInstance().getState() == ModuleInstance.INITIALIZED || item
                                                  .getModuleInstance().getState() == ModuleInstance.RUNNING)
                                          {
                                              if(!priorityMap.containsKey(priority))
                                              {
                                                  priorityMap.put(priority, new ArrayList<MI>());
                                              }
                                              priorityMap.get(priority).add(item.getModuleInstance());
                                          }
                                      });

        if(priorityMap.isEmpty())
        {
            return null;
        }

        ArrayBlockingQueue<List<MI>> queue = new ArrayBlockingQueue<List<MI>>(priorityMap.size());
        priorityMap.forEach((priority, list) ->
                            {
                                queue.add(list);
                            });

        return queue;
    }

    protected void buildDependencies(List<MI> modules)
    {
        moduleDependencyItems.clear();
        modules.forEach(module ->
                        {
                            if(module.getState() == ModuleInstance.UNINITIALIZED || module
                                    .getState() == ModuleInstance.INITIALIZED || module
                                    .getState() == ModuleInstance.RUNNING)
                            {
                                moduleDependencyItems.add(new ModuleDependencyItem<MI>(module));
                            }
                        });

        moduleDependencyItems.forEach(this::setDependencyItem);
        moduleDependencyItems.stream().forEach(this::searchForCycles);
        moduleDependencyItems.stream().filter(item -> item.isDependsOnEmpty())
                .forEach(item -> this.prioritizeDependency(item, 0));
    }

    private void searchForCycles(ModuleDependencyItem<MI> moduleDependencyItem)
    {
        this.searchForCycles(moduleDependencyItem, moduleDependencyItem);
        moduleDependencyItems.forEach(item -> item.setVisited(false));
    }

    private boolean searchForCycles(ModuleDependencyItem<MI> moduleDependencyItem, ModuleDependencyItem<MI> searched)
    {
        if(!moduleDependencyItem.isVisited() && moduleDependencyItem.getState() != ModuleInstance.DEPENDENCY_CYCLE)
        {
            moduleDependencyItem.setVisited(true);
            moduleDependencyItem.getDependsOn().forEach(child ->
                                                        {
                                                            try
                                                            {
                                                                this.searchForCycles(child, searched);
                                                            } catch(Exception e)
                                                            {
                                                                e.printStackTrace();
                                                            }
                                                        });
        } else if(searched == moduleDependencyItem)
        {
            this.setState(moduleDependencyItem, ModuleInstance.DEPENDENCY_CYCLE);
            return false;
        }
        return true;
    }

    private void setState(ModuleDependencyItem<MI> moduleDependencyItem, int state)
    {
        if(moduleDependencyItem.getModuleInstance().getState() != state)
        {
            moduleDependencyItem.getModuleInstance().setState(state);

            if(!moduleDependencyItem.isDependencyOfEmpty())
            {
                moduleDependencyItem.getDependencyOf().forEach(module -> this.setState(module, state));
            }
        }
    }

    private void prioritizeDependency(ModuleDependencyItem<MI> item, int priority)
    {
        if(item.getPriority() <= priority)
        {
            item.setPriority(priority);
            item.getDependencyOf().forEach(dependencyItem -> this.prioritizeDependency(dependencyItem, priority + 1));
        }
    }

    private void setDependencyItem(ModuleDependencyItem<MI> item)
    {
        List<String> dependencies = Arrays.asList(item.getDependencies());

        moduleDependencyItems.stream().forEach(miModuleDependencyItem ->
                                               {
                                                   if(dependencies.contains(miModuleDependencyItem
                                                                                    .getName() + "$" + miModuleDependencyItem
                                                           .getVersion()))
                                                   {
                                                       if(item != miModuleDependencyItem)
                                                       {
                                                           item.addDependsOn(miModuleDependencyItem);
                                                           miModuleDependencyItem.addDependencyOf(item);
                                                       } else
                                                       {
                                                           LOG.warn("Module '" + item.getName() + "' Version '" + item
                                                                   .getVersion() + "' refers itself as dependency");
                                                       }
                                                   }
                                               });
    }

    protected void startModule(MI moduleInstance)
    {
        moduleInstance.start();
    }

    public void startModule(String moduleName)
    {
        Optional<MI> instance = this.getModule(moduleName);
        if(instance.isPresent())
        {
            instance.get().start();
        }
    }

    public void stopModule(String moduleName)
    {
        Optional<MI> instance = this.getModule(moduleName);
        if(instance.isPresent())
        {
            instance.get().stop();
        }
    }

    public void unloadModule(String moduleName)
    {
        Optional<MI> instance = this.getModule(moduleName);
        if(instance.isPresent())
        {
            instance.get().unload();
            MODULE_INSTANCES.remove(instance.get());
        }
    }

    private Optional<MI> getModule(String moduleName)
    {
        return MODULE_INSTANCES.stream().filter(instance -> moduleName.equals(instance.getName())).findFirst();
    }
}

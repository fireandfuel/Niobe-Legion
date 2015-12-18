package niobe.legion.client.module;

import java.net.URL;
import java.util.stream.Stream;
import niobe.legion.client.gui.tab.ITab;
import niobe.legion.shared.data.IRight;

/**
 * Wrap a IModuleTab implementation inside this ITab implementation.
 * With URL getTabURL() it's possible to load a fxml file from the internet and do some nasty things.
 * <p>
 * You can only get a location inside the module's jar file with String getTabFileName().
 */
public class ModuleTabWrapper implements ITab
{
    private IModuleTab moduleTab;
    private Class<?> moduleClass;

    private ModuleTabWrapper(IModuleTab moduleTab, Class<?> moduleClass)
    {
        this.moduleTab = moduleTab;
        this.moduleClass = moduleClass;
    }

    @Override
    public URL getTabURL()
    {
        if(moduleTab != null && moduleClass != null)
        {
            return moduleClass.getResource(moduleTab.getTabFileName());
        }
        return null;
    }

    @Override
    public boolean isCloseable()
    {
        return moduleTab != null && moduleTab.isCloseable();
    }

    @Override
    public IRight[] getNeededGroupRights()
    {
        return moduleTab != null ? moduleTab.getNeededGroupRights() : null;
    }

    protected static ITab[] wrap(IModuleTab[] moduleTabs, Class<?> moduleClass)
    {
        return Stream.of(moduleTabs).map(moduleTab -> new ModuleTabWrapper(moduleTab, moduleClass))
                .toArray(ITab[]::new);
    }
}

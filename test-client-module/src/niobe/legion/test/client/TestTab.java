package niobe.legion.test.client;

import niobe.legion.client.module.IModuleTab;
import niobe.legion.shared.data.IRight;

public class TestTab implements IModuleTab
{
    @Override
    public String getTabFileName()
    {
        return "/niobe/legion/test/client/fxml/TestTab.fxml";
    }

    @Override
    public boolean isCloseable()
    {
        return true;
    }

    @Override
    public IRight[] getNeededGroupRights()
    {
        return null;
//				new IRight[]{
//				new TestRight("test", null)
//		};
    }
}

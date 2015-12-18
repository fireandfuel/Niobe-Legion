package niobe.legion.test.client;

import java.util.ArrayList;
import java.util.List;
import niobe.legion.shared.data.IRight;

public class TestRight implements IRight
{

    private String name;
    private IRight parent;

    TestRight(String name, IRight parent)
    {
        this.name = name;
        this.parent = parent;
    }

    private transient List<IRight> children;

    @Override
    public String getName()
    {
        return (this.getParent() != null) ? (this.getParent().getName() + "." + this.name) : this.name;
    }

    @Override
    public IRight getParent()
    {
        return this.parent;
    }

    @Override
    public List<IRight> getChildren()
    {
        return this.children;
    }

    @Override
    public void addChildren(IRight right)
    {
        if(this.children == null)
        {
            this.children = new ArrayList<IRight>();
        }
        this.children.add(right);
    }
}
package niobe.legion.client.gui.admin;

import niobe.legion.client.gui.IMaskType;

public class AdminType implements IMaskType
{
    private String caption;
    private String maskURI;

    public AdminType(String caption, String maskURI)
    {
        this.caption = caption;
        this.maskURI = maskURI;
    }

    @Override
    public String getCaption()
    {
        return this.caption;
    }

    @Override
    public String getMaskURI()
    {
        return this.maskURI;
    }

    @Override
    public String toString()
    {
        return (this.caption != null) ? this.caption : super.toString();
    }
}

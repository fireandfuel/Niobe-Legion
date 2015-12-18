package niobe.legion.client.gui.locale;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class EncodedControl extends Control
{
    private final String encoding;

    public EncodedControl(String encoding)
    {
        this.encoding = encoding;
    }

    public List<String> getFormats(String basename)
    {

        if(basename == null)
        {
            throw new NullPointerException();
        }
        return Arrays.asList("properties");

    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
    {

        if(baseName == null || locale == null || format == null || loader == null)
        {
            throw new NullPointerException();
        }

        ResourceBundle bundle = null;

        if(format.equals("properties"))
        {

            String bundleName = this.toBundleName(baseName, locale);
            String resourceName = this.toResourceName(bundleName, format);
            InputStream stream = null;

            try
            {
                if(reload)
                {
                    URL url = loader.getResource(resourceName);
                    if(url != null)
                    {
                        URLConnection connection;
                        connection = url.openConnection();

                        if(connection != null)
                        {
                            connection.setUseCaches(false);
                            stream = connection.getInputStream();
                        }
                    }
                } else
                {
                    stream = loader.getResourceAsStream(resourceName);
                }

                if(stream != null)
                {
                    InputStreamReader is = new InputStreamReader(stream, this.encoding);
                    bundle = new PropertyResourceBundle(is);
                    is.close();
                }
            } catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return bundle;
    }
}

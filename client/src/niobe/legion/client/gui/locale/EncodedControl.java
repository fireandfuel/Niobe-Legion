/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (EncodedControl.java) is part of Niobe Legion (module niobe-legion-client).
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
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

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

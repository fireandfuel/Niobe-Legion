/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ReflectionUtils.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.gui.platform.osx.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils
{
    public static MethodHandle getHandle(Object obj, String name, Class<?>... args) throws ReflectiveOperationException
    {
        return MethodHandles.lookup().unreflect(getAccessibleMethod(obj, name, args));
    }

    public static Method getAccessibleMethod(Object obj, String name, Class<?>... args) throws NoSuchMethodException
    {
        Method method = obj.getClass().getDeclaredMethod(name, args);
        method.setAccessible(true);
        return method;
    }

    public static Field getAccessibleField(Object obj, String name) throws NoSuchFieldException, SecurityException
    {
        Field field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static void invokeQuietly(MethodHandle handle, Object instance)
    {
        try
        {
            handle.invoke(instance);
        } catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
}

/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (Logger.java) is part of Niobe Legion (module niobe-legion-shared).
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

package niobe.legion.shared.logger;

import javafx.fxml.LoadException;
import org.apache.logging.log4j.LogManager;

public class Logger
{
    public static void trace(ILogger logger, String message)
    {
        LogManager.getLogger(logger.getLoggerName()).trace(message);
    }

    public static void debug(ILogger logger, String message)
    {
        LogManager.getLogger(logger.getLoggerName()).debug(message);
    }

    public static void info(ILogger logger, String message)
    {
        LogManager.getLogger(logger.getLoggerName()).trace(message);
    }

    public static void warn(ILogger logger, String message)
    {
        LogManager.getLogger(logger.getLoggerName()).warn(message);
    }

    public static void error(ILogger logger, String message)
    {
        LogManager.getLogger(logger.getLoggerName()).error(message);
        if(logger != LegionLogger.STDERR)
        {
            LogManager.getLogger(LegionLogger.STDERR.getLoggerName()).error(message);
        }
    }

    public static void exception(ILogger logger, Throwable exception)
    {
        if(exception instanceof LoadException)
        {
            LogManager.getLogger(logger.getLoggerName()).error(exception.getMessage());
            if(logger != LegionLogger.STDERR)
            {
                LogManager.getLogger(LegionLogger.STDERR.getLoggerName()).error(exception.getMessage());
            }
        } else
        {
            LogManager.getLogger(logger.getLoggerName()).catching(exception);
            if(logger != LegionLogger.STDERR)
            {
                LogManager.getLogger(LegionLogger.STDERR.getLoggerName()).catching(exception);
            }
        }
    }
}

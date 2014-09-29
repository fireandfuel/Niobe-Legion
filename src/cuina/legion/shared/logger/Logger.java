package cuina.legion.shared.logger;

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
				LogManager.getLogger(LegionLogger.STDERR.getLoggerName()).error(
						exception.getMessage());
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

package reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    private final Logger logger;

    // Constructor binds logger to the calling class
    private LogUtil(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    // Factory method for creating logger instance
    public static LogUtil getLogger(Class<?> clazz) {
        return new LogUtil(clazz);
    }

    // --- Log levels ---
    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public void debug(String message) {
        logger.debug(message);
    }
}

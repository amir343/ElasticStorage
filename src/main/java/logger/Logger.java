package logger;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public interface Logger {

	void info(String text);

	void warn(String text);

	void error(String text);

	void debug(String text);

}

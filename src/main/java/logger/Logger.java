package logger;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public interface Logger {

	void raw(Object text);
	
	void info(Object text);

	void warn(Object text);

	void error(Object text);

	void debug(Object text);

    double getTime();
}

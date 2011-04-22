package logger;

import java.util.HashMap;
import java.util.Map;

import common.GUI;


/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class LoggerFactory {

	private static Long start = null;
	private static Map<String, ElasticLogger> loggers = new HashMap<String, ElasticLogger>();
	
	public static synchronized Logger getLogger(Class<?> class1, GUI guiLogger) {
		if (start == null)
			start = System.currentTimeMillis();
		if (loggers.containsKey(class1.getCanonicalName())) {
			return loggers.get(class1.getCanonicalName());
		}
		Logger logger = new ElasticLogger(class1, guiLogger, start);
		loggers.put(class1.getCanonicalName(), (ElasticLogger) logger);
		return logger;
	}

}

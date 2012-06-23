/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package logger;

import common.GUI;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */
@Deprecated
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

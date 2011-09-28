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

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class ElasticLogger implements Logger {

	private GUI gui;
	private String className;
	private long start;

	public ElasticLogger(Class<?> class1, GUI guiLogger, long start) {
		this.className = class1.getSimpleName();
		this.gui = guiLogger;
		this.start = start;
	}

	public synchronized void log(Object text, String level) {
		String log;
		if (level != null)
			log = String.format("[%010.4f] %s {%s} %s", now(), level, className, text);
		else
			log = String.format("[%010.4f] %s", now(), text);;
		gui.log(log);		
	}

	@Override
	public void raw(Object text) {
		log(text, null);		
	}

	@Override
	public void info(Object text) {
		log(text, "INFO");
	}

	@Override
	public void debug(Object text) {
		log(text, "DEBUG");
	}

    @Override
    public double getTime() {
        return now();
    }

    @Override
	public void warn(Object text) {
		log(text, "WARN");
	}

	@Override
	public void error(Object text) {
		log(text, "ERROR");
	}

	private double now() {
		long now = System.currentTimeMillis();
		Long diff = now - start;
	    return diff.doubleValue()/1000;
	}
	
	public long nowInSeconds() {
		long now = System.currentTimeMillis() - start;
		return now/1000;
	}


}

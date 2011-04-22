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

	public synchronized void log(String text, String level) {
		String log = String.format("%7d %s {%s} %s", now(), level, className, text);
		gui.log(log);		
	}

	@Override
	public void info(String text) {
		log(text, "INFO");
	}

	@Override
	public void debug(String text) {
		log(text, "DEBUG");
	}

	@Override
	public void warn(String text) {
		log(text, "WARN");
	}

	@Override
	public void error(String text) {
		log(text, "ERROR");
	}

	private long now() {
		long now = System.currentTimeMillis();
		long diff = now - start;
		return diff;
	}
	
	public long nowInSeconds() {
		long now = System.currentTimeMillis() - start;
		return now/1000;
	}

}

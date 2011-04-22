package econtroller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ControllerProcess extends Thread {

	
	private String classPath;
	private String configurationFilePath;
	private String mainClass;
	private Process process;
	private BufferedWriter input;

	public ControllerProcess(String classPath, String configurationFilePath, String mainClass) {
		this.classPath = classPath;
		this.configurationFilePath = configurationFilePath;
		this.mainClass = mainClass;
	}
	
	@Override
	public void run() {
		launchProcess();
		waitFor();
	}

	private void launchProcess() {
		
		ProcessBuilder processBuilder = new ProcessBuilder("java", "-classpath", classPath, "-Dlog4j.properties=log4j.properties", 
				"-DcontrollerConfiguration=" + configurationFilePath, mainClass, "" + UUID.randomUUID().toString(), "");
		processBuilder.redirectErrorStream(true);

		try {
			process = processBuilder.start();
			BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
			input = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

			String line;
			do {
				line = out.readLine();
				if (line != null) {
					if (line.equals("2DIE")) {
						if (process != null) {
							process.destroy();
							process = null;
						}
						break;
					}
				}
			} while (line != null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final void waitFor() {
		if (process != null) {
			while (true) {
				try {
					process.waitFor();
					break;
				} catch (InterruptedException e) {
					continue;
				}
			}
		}
	}
	
	
}

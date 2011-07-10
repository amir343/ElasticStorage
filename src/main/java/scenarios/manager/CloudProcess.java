package scenarios.manager;

import java.io.*;
import java.util.UUID;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-23
 *
 */

public class CloudProcess extends Thread {

	private String mainClass;
	private String cloudConfigurationFile;
	private String classPath;
	private Process process;
	private BufferedWriter input;

	public CloudProcess(String classPath, String topologyFile, String mainClass) {
		this.classPath = classPath;
		this.cloudConfigurationFile = topologyFile;
		this.mainClass = mainClass;
	}
	
	
	@Override
	public void run() {
		launchProcess();
		waitFor();
	}
	
	private void launchProcess() {
		
		ProcessBuilder processBuilder = new ProcessBuilder("java", "-classpath", classPath, "-Dlog4j.properties=log4j.properties", 
				"-DcloudConfiguration=" + cloudConfigurationFile, mainClass, "" + UUID.randomUUID().toString(), "");
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

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
package econtroller;

import java.io.*;
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

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
package instance;

import cloud.api.InstanceConfiguration;
import cloud.common.NodeConfiguration;
import scenarios.manager.CloudConfiguration;

import java.io.*;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class InstanceProcess extends Thread {
	
	private CloudConfiguration cloudConfiguration;
	private final String classPath = System.getProperty("java.class.path");
	private NodeConfiguration nodeConfiguration;
	private Process process;

	public InstanceProcess(CloudConfiguration cloudConfiguration, NodeConfiguration node) {
		this.cloudConfiguration = cloudConfiguration;
		this.nodeConfiguration = node;
	}
	
	public void run() {
		launchProcess();
		waitFor();
	}
	
	private void launchProcess() {
		ProcessBuilder processBuilder = new ProcessBuilder("java", "-classpath", classPath, "-Dlog4j.properties=log4j.properties", 
				"-DnodeConfiguration=" + getNodeConfigurationFile(nodeConfiguration), cloudConfiguration.getInstanceClass().getCanonicalName());
		processBuilder.redirectErrorStream(true);
		try {
            processBuilder.redirectErrorStream(true);
			process = processBuilder.start();
			BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));

            process.waitFor();
            String line;
            while( (line = out.readLine() ) != null)
                System.out.println(line);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	private String getNodeConfigurationFile(NodeConfiguration node) {
		InstanceConfiguration instanceTopology = new InstanceConfiguration(node, cloudConfiguration.getSelfAddress());
		File file = null;
		try {
			file = File.createTempFile("nodeConfiguration", ".bin");
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(instanceTopology);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return file.getAbsolutePath();
	}
	
	public final void kill() {
		if (process != null) {
			process.destroy();
			process = null;
			try {
				this.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

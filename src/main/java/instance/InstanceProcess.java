package instance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import scenarios.manager.CloudConfiguration;
import scenarios.manager.Cloud.Node;
import cloud.api.InstanceConfiguration;
import cloud.common.NodeConfiguration;

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
			process = processBuilder.start();
			BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedWriter input = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

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

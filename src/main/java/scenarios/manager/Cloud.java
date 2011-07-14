package scenarios.manager;

import cloud.CloudProvider;
import cloud.common.NodeConfiguration;
import instance.Instance;
import instance.Node;
import instance.common.Block;
import instance.common.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-23
 *
 */

public abstract class Cloud {

	private Class<? extends CloudProvider> cloudClass;
	private List<NodeConfiguration> nodeConfigurations = new ArrayList<NodeConfiguration>();
	private CloudConfiguration cloudConfiguration = new CloudConfiguration();
	private final String classPath = System.getProperty("java.class.path");
	private final List<String> blockNames = new ArrayList<String>();
	private final List<Block> blocks = new ArrayList<Block>();
	private File cloudConfigurationFile;

	public Cloud(Class<? extends CloudProvider> cloudClass, Class<? extends Instance> instanceClass) {
		this.cloudClass = cloudClass;
		this.cloudConfiguration.setInstanceClass(instanceClass);
	}
	
	protected NodeConfiguration node(String nodeName, String address, int port) {
		Node node = new Node(nodeName, address, port);
		NodeConfiguration nodeConfiguration = new NodeConfiguration();
		nodeConfiguration.setNodeInfo(node);
		nodeConfigurations.add(nodeConfiguration);
		return nodeConfiguration;
	}
	
	protected void cloudProviderAddress(String address, int port) {
		cloudConfiguration.setCloudProviderAddress(address, port);
	}
	
	protected void data(String name, int size, Size sizeInBytes) {
		if (blockNames.contains(name)) {
			throw new RuntimeException("Data block with name '" + name + "' already exists -> data(\"" + name+ "\", " + size +", Size." + sizeInBytes +");");
		}
		blockNames.add(name);
		Block block = new Block(name, size*sizeInBytes.getSize());
		blocks.add(block);
	}

    protected void headless() {
        cloudConfiguration.setHeadLess(true);
    }
	
	protected void replicationDegree(int replicationDegree) {
		cloudConfiguration.setReplicationDegree(replicationDegree);
	}
	
	protected void addressPoll(String addressPollXmlFileName) {
		cloudConfiguration.setAddressPollXmlFilename(addressPollXmlFileName);
	}

	public void start() {
		cloudConfiguration.addNodeConfiguration(nodeConfigurations);
		cloudConfiguration.addBlockData(blocks);
		cloudConfiguration.validate();
		writeCloudTopologyObjectIntoTempFile();
		startCloudProviderProcess();
	}
	
	private void startCloudProviderProcess() {
		CloudProcess cp = new CloudProcess(classPath, cloudConfigurationFile.getAbsolutePath(), cloudClass.getCanonicalName());
		cp.start();
	}

	private void writeCloudTopologyObjectIntoTempFile() {
		File file = null;
		try {
			file = File.createTempFile("cloudConfiguration", ".bin"); ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(cloudConfiguration);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		cloudConfigurationFile = file.getAbsoluteFile();
	}

}

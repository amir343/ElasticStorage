///**
// * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package scenarios.manager;
//
//import cloud.CloudProvider;
//import cloud.common.NodeConfiguration;
//import instance.Node;
//import instance.common.Block;
//import instance.common.Size;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// *
// * @author Amir Moulavi
// * @date 2011-03-23
// *
// */
//
//public abstract class Cloud {
//
//	private Class<? extends CloudProvider> cloudClass;
//	private List<NodeConfiguration> nodeConfigurations = new ArrayList<NodeConfiguration>();
//	private CloudConfiguration cloudConfiguration = new CloudConfiguration();
//	private final String classPath = System.getProperty("java.class.path");
//	private final List<String> blockNames = new ArrayList<String>();
//	private final List<Block> blocks = new ArrayList<Block>();
//	private File cloudConfigurationFile;
//    private SLA sla = new SLA();
//
//	public Cloud(Class<? extends CloudProvider> cloudClass, Class<? extends Instance> instanceClass) {
//		this.cloudClass = cloudClass;
//		this.cloudConfiguration.setInstanceClass(instanceClass);
//	}
//
//	protected NodeConfiguration node(String nodeName, String address, int port) {
//		Node node = new Node(nodeName, address, port);
//		NodeConfiguration nodeConfiguration = new NodeConfiguration();
//		nodeConfiguration.setNodeInfo(node);
//		nodeConfigurations.add(nodeConfiguration);
//		return nodeConfiguration;
//	}
//
//	protected void cloudProviderAddress(String address, int port) {
//		cloudConfiguration.setCloudProviderAddress(address, port);
//	}
//
//	protected void data(String name, int size, Size sizeInBytes) {
//		if (blockNames.contains(name)) {
//			throw new RuntimeException("Data block with name '" + name + "' already exists -> data(\"" + name+ "\", " + size +", Size." + sizeInBytes +");");
//		}
//		blockNames.add(name);
//		Block block = new Block(name, size*sizeInBytes.getSize(), 0, 0);
//		blocks.add(block);
//	}
//
//    protected void headless() {
//        cloudConfiguration.setHeadLess(true);
//    }
//
//	protected void replicationDegree(int replicationDegree) {
//		cloudConfiguration.setReplicationDegree(replicationDegree);
//	}
//
//	protected void addressPoll(String addressPollXmlFileName) {
//		cloudConfiguration.setAddressPollXmlFilename(addressPollXmlFileName);
//	}
//
//    protected SLA sla() {
//        cloudConfiguration.setSla(sla);
//        return sla;
//    }
//
//	public void start() {
//		cloudConfiguration.addNodeConfiguration(nodeConfigurations);
//		cloudConfiguration.addBlockData(blocks);
//		cloudConfiguration.validate();
//		writeCloudTopologyObjectIntoTempFile();
//		startCloudProviderProcess();
//	}
//
//	private void startCloudProviderProcess() {
//		CloudProcess cp = new CloudProcess(classPath, cloudConfigurationFile.getAbsolutePath(), cloudClass.getCanonicalName());
//		cp.start();
//	}
//
//	private void writeCloudTopologyObjectIntoTempFile() {
//		File file = null;
//		try {
//			file = File.createTempFile("cloudConfiguration", ".bin"); ObjectOutputStream oos = new ObjectOutputStream(
//					new FileOutputStream(file));
//			oos.writeObject(cloudConfiguration);
//			oos.flush();
//			oos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//		cloudConfigurationFile = file.getAbsoluteFile();
//	}
//
//}

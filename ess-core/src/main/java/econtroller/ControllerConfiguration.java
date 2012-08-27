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
//package econtroller;
//
//import se.sics.kompics.address.Address;
//
//import java.io.*;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
///**
// * 
// * @author Amir Moulavi
// * @date 2011-04-11
// *
// */
//
//public class ControllerConfiguration implements Serializable {
//
//	private static final long serialVersionUID = -3476537414695093840L;
//	private String ip;
//	private int port;
//
//	public void setControllerAddress(String ip, int port) {
//		this.ip = ip;
//		this.port = port;		
//	}
//
//	public String getIp() {
//		return ip;
//	}
//
//	public int getPort() {
//		return port;
//	}
//
//	public static ControllerConfiguration load(String topologyFile) {
//		ControllerConfiguration controllerConfiguration = null;
//		try {
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(topologyFile));
//			controllerConfiguration = (ControllerConfiguration) ois.readObject();
//			ois.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			System.exit(0);
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(0);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//		return controllerConfiguration;
//	}
//
//	public Address getSelfAddress() {
//		Address self = null;
//		try {
//			self = new Address(InetAddress.getByName(ip), port, 1);
//		} catch (UnknownHostException e) {
//			throw new RuntimeException("Host is not defined correctly: " + ip);
//		}
//		return self;
//	}
//
//}

package econtroller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import scenarios.manager.CloudConfiguration;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ControllerConfiguration implements Serializable {

	private static final long serialVersionUID = -3476537414695093840L;
	private String ip;
	private int port;

	public void setControllerAddress(String ip, int port) {
		this.ip = ip;
		this.port = port;		
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public static ControllerConfiguration load(String topologyFile) {
		ControllerConfiguration controllerConfiguration = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(topologyFile));
			controllerConfiguration = (ControllerConfiguration) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return controllerConfiguration;
	}

	public Address getSelfAddress() {
		Address self = null;
		try {
			self = new Address(InetAddress.getByName(ip), port, 1);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Host is not defined correctly: " + ip);
		}
		return self;
	}

}

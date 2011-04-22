package cloud.api.adress;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import com.thoughtworks.xstream.XStream;

import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.address.Address;
import cloud.gui.CloudGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class AddressManager {
	
	private Logger logger = LoggerFactory.getLogger(AddressManager.class, CloudGUI.getInstance());
	private AddressPoll addressPoll;
	private Set<Address> availableAddressSpace = new HashSet<Address>();
	private List<Address> busyAddresses = new ArrayList<Address>();
	private XStream xstream;
	private String addressPollXmlFilename;
	
	public AddressManager(String addressPollXmlFilename) {
		this.addressPollXmlFilename = addressPollXmlFilename;
		initializeXMLParser();
		retrieveAddresses();
		calculateAddressSpace();
	}
	
	private void initializeXMLParser() {
		xstream = new XStream();
		xstream.alias("addressPoll", AddressPoll.class);
		xstream.alias("addressRange", AddressRange.class);		
	}

	private void calculateAddressSpace() {
		if (addressPoll == null) {
			logger.error("There is something wrong with 'addresses.xml' file. Please check that out");
			throw new RuntimeException("There is something wrong with 'addresses' file. Please check that out");
		}
		Random random = new Random();
		for (AddressRange range : addressPoll.getList()) {
			if (range.getStartPort() > range.getEndPort() )
				throw new RuntimeException("Incorrect port range for address " + range.getIp() + " -> [" + range.getStartPort() + "," +  range.getEndPort() + "]");
			for (int i=range.getStartPort(); i <=range.getEndPort(); i++) {
				Address address = null;
				try {
					address = new Address(InetAddress.getByName(range.getIp()), i, 1);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				if (address != null ) availableAddressSpace.add(address);
			}
		}
	}

	private void retrieveAddresses() {
		String path = Thread.currentThread().getContextClassLoader().getResource(addressPollXmlFilename).getFile();
		if (path != null ) {
		    StringBuilder xml = new StringBuilder();
		    Scanner scanner = null;
			try {
				scanner = new Scanner(new FileInputStream(path));
			} catch (FileNotFoundException e) {
				logger.error("File 'addresses.xml' not found");
				e.printStackTrace();
			}
		    try {
		    	while (scanner.hasNextLine()){
		    		xml.append(scanner.nextLine());
		      	}
		    } finally {
		    	scanner.close();
		    }
		    addressPoll = (AddressPoll) xstream.fromXML(xml.toString());
		} else {
			logger.error("File 'addresses.xml' was not found!");
			throw new RuntimeException("File 'addresses' was not found!");
		}
	}

	public Address getAFreeAddress() {
		if (availableAddressSpace.size() != 0) {
			Address address = availableAddressSpace.iterator().next();
			availableAddressSpace.remove(address);
			busyAddresses.add(address);
			return address;
		} else 
			return null;
	}
	
	public void releaseAddress(Address address) {
		availableAddressSpace.add(address);
		removeFromBusyAddresses(address);		
	}
	
	private void removeFromBusyAddresses(Address address) {
		for (Address add : busyAddresses) {
			if (add.getIp().equals(address.getIp()) && add.getPort() == address.getPort()) {
				busyAddresses.remove(add);
				return;
			}
		}
	}
	
	public int getNrOfAvailableAddress() {
		return availableAddressSpace.size();
	}
	
}

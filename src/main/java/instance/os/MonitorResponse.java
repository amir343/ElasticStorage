package instance.os;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class MonitorResponse extends Message {

	private static final long serialVersionUID = 2502892632597196129L;
	private MonitorPacket monitorPacket;

	public MonitorResponse(Address source, Address destination, MonitorPacket monitorPacket) {
		super(source, destination);
		this.monitorPacket = monitorPacket;
	}

	public MonitorPacket getMonitorPacket() {
		return monitorPacket;
	}

}

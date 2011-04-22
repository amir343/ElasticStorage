package instance.common;

import instance.os.CPULoad;
import instance.os.RestartSignal;
import instance.os.SnapshotRequest;
import se.sics.kompics.PortType;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class CPUChannel extends PortType {
	{
		positive(Ready.class);
		positive(CPULoad.class);
		positive(SnapshotRequest.class);
		negative(StartProcess.class);
		negative(EndProcess.class);
		negative(AbstractOperation.class);
		negative(RestartSignal.class);
		negative(SnapshotRequest.class);
	}
}

package instance.common;

import instance.disk.StartDiskUnit;
import instance.os.RestartSignal;
import se.sics.kompics.PortType;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class DiskChannel extends PortType {
	{
		positive(Ready.class);
		positive(BlockResponse.class);
		negative(LoadBlock.class);
		negative(ReadBlock.class);
		negative(RestartSignal.class);
		negative(StartDiskUnit.class);
	}
}

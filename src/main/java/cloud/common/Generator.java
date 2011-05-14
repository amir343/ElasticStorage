package cloud.common;

import instance.common.Request;
import cloud.elb.BlocksActivated;
import cloud.requestengine.DownloadStarted;
import cloud.requestengine.RequestGeneratorInit;
import se.sics.kompics.PortType;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-30
 *
 */

public class Generator extends PortType {
	{
		negative(RequestGeneratorInit.class);
		negative(DownloadStarted.class);
		negative(SendRawData.class);
		negative(BlocksActivated.class);
		positive(SendRawData.class);
		positive(Request.class);
	}
}

package cloud.common;

import instance.common.Request;
import cloud.requestengine.RequestDone;
import cloud.requestengine.RequestGeneratorInit;
import cloud.requestengine.ResponseTimeTD;
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
		negative(RequestDone.class);
		negative(SendRawData.class);
		positive(ResponseTimeTD.class);
		positive(Request.class);
	}
}

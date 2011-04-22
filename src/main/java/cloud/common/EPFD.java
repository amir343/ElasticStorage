package cloud.common;

import cloud.epfd.HealthCheckerInit;
import se.sics.kompics.PortType;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class EPFD extends PortType {
	{
		positive(Suspect.class);
		positive(Restore.class);
		negative(HealthCheckerInit.class);
		negative(ConsiderInstance.class);
		negative(InstanceKilled.class);
	}
}

package econtroller.modeler;

import cloud.elb.SenseData;
import se.sics.kompics.PortType;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class ModelPort extends PortType {
	{
		negative(StartModeler.class);
        negative(SenseData.class);
	}
}

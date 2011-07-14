package scenarios.executor;

import cloud.CloudProvider;
import econtroller.ElasticController;
import instance.Instance;
import instance.common.Size;
import org.apache.log4j.PropertyConfigurator;
import scenarios.manager.Cloud;
import scenarios.manager.ControllerApplication;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-23
 *
 */

public class TestScenario {

    static {
        PropertyConfigurator.configureAndWatch("log4j.properties");
    }
  
    // TODO: Add nodeConfiguration to be enabled here also
    public static final void main(String[] args) {
    	Cloud cloud = new Cloud(CloudProvider.class, Instance.class) {
    		{
    			cloudProviderAddress("127.0.0.1", 23444);
                headless();
//    			node("node1", "127.0.0.1", 23445).
//    				cpu(2.2).
//    				memoryGB(8).
//    				bandwidthMB(2);
    			data("block1", 2, Size.MB);
    			data("block2", 4, Size.MB);
    			data("block3", 3, Size.MB);
    			data("block4", 5, Size.MB);
    			data("block5", 12, Size.MB);
    			replicationDegree(2);
    			addressPoll("addresses.xml");
    		}
    	};
    	
    	cloud.start();
    	
    	ControllerApplication controller = new ControllerApplication(ElasticController.class) {
    		{
    			controllerAddress("127.0.0.1", 23443);
    		}
    	};
    	
    	controller.start();
    	
    	
    }
    
}

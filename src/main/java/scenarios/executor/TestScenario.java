/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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

    // TODO: Add nodeConfiguration to be enabled here also
    public static final void main(String[] args) {
    	Cloud cloud = new Cloud(CloudProvider.class, Instance.class) {
    		{
    			cloudProviderAddress("127.0.0.1", 23444);
/*
                headless();
*/
//    			node("node1", "127.0.0.1", 23445)
//    				.cpu(2.2)
//    				.memoryGB(8)
//    				.bandwidthMB(2);
    			data("block1", 2, Size.MB);
    			data("block2", 4, Size.MB);
    			data("block3", 3, Size.MB);
    			data("block4", 1, Size.MB);
    			data("block5", 4, Size.MB);
    			data("block6", 5, Size.MB);
    			data("block7", 4, Size.MB);
    			data("block8", 3, Size.MB);
    			data("block9", 2, Size.MB);
    			data("block10", 1, Size.MB);
    			replicationDegree(2);
    			addressPoll("addresses.xml");
                sla()
                    .cpuLoad(55)
                    .responseTime(1500)
                    .bandwidth(200000);
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

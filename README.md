# Introduction

Project is an implementation to simulate the elasticity property for storage nodes in a Distributed Storage Systems (DSS) in the context of a cloud environment like Amazon. 

This project is built on top of Kompics [1] which is a distributed event-driven message-passing component model for building distributed systems.

# Check out and build

Check out the source code from GIT repository:

    # git clone git://github.com/amir343/ElasticStorage.git 

You should have Maven 2 installed in order to be able to build the source code:

    # mvn clean install

This will download the required libraries from Maven repositories and builds the source code.

[1]: Kompics: http://kompics.sics.se/

# How to construct the cloud?

A cloud environment consists of several elements that each one is described in the following can be passed to `Cloud` class:

*	`cloudProviderAddress(address, port)`: binds the cloud provider to this address and port. The address should refer to `localhost`.
*	`data(name, size, size unit)`: defines the data blocks that will be used during the simulation and their corresponding sizes. The names must be unique.
*	`replicationDegree(number)`: defines the replication degree to meet by Elastic Load Balancer.
*	`addressPoll(file name)`: points to address ranges that are available for this simulation.
*	`node(name, address, port)`: defines a storage instance with a name and address, port to bind to. The name should be unique.
	*	`cpu(n)`: defines the CPU for the instance with frequency of `n` GHz.
	*	`memoryGB(n)`: defines the memory for the instance with size of `n` GB.
	*	`bandwidthMB(n)`: defines the bandwidth for the instance with capacity of `n` MB.
*	`sla()`: enables the SLA violation calculation according to the parameter that will be defined as the following:
	*	`cpuLoad(n)`: SLA requirements for percentage of average CPU load in the system.
	*	`responseTime(n)`: SLA requirements for average response time (`ms`) in the system.
	*	`bandwidth(n)`: SLA requirements for average bandwidth per download (`B/s`) in the system.

Using these elements a complete cloud environment can be built. Note that elements related to instances are not mandatory. Instances can be launched by 
cloud provider as well. After this definition, the object that holds theses elements should be started by calling the method `start()`.

Controller also should be defined but separately. The class that is responsible for controller is `ControllerApplication` and has the following element:

*	`controllerAddress(address, port)`: defines the address and port that controller will be bind to.

Note that the address should be different from address that are included in `addressPoll` and cloud provider address. 
Controller is started with a method called `start()`. These definition can be put in a source code with desire name for class inside a `main` method.
It is a good practice to put your class in package `scenarios.executor`. To start the simulation it is enough to run this class. An example scenario can
be like the following:

```java
package scenarios.executor;

import cloud.CloudProvider;
import econtroller.ElasticController;
import instance.Instance;
import instance.common.Size;
import org.apache.log4j.PropertyConfigurator;
import scenarios.manager.Cloud;
import scenarios.manager.ControllerApplication;

public class TestScenario {

    public static final void main(String[] args) {
    	Cloud cloud = new Cloud(CloudProvider.class, Instance.class) {
    		{
    			cloudProviderAddress("127.0.0.1", 23444);
    			node("node1", "127.0.0.1", 23445).
    				cpu(2.2).
    				memoryGB(8).
    				bandwidthMB(2);
    			data("block1", 2, Size.MB);
    			data("block2", 4, Size.MB);
    			data("block3", 3, Size.MB);
    			data("block4", 1, Size.MB);
    			data("block5", 4, Size.MB);
    			replicationDegree(2);
    			addressPoll("addresses.xml");
    			sla()
    				.cpuLoad(30)
    				.responseTime(1000);
    		}
    	};
    	
    	cloud.start();
    	
    	ControllerApplication controller = 
		new ControllerApplication(ElasticController.class) {
    		{
    			controllerAddress("127.0.0.1", 23443);
    		}
    	};
    	
    	controller.start();
    }
```

## addressPoll}

`addressPoll` is an XML file that defines the elastic IP address ranges that can be used by cloud provider. A simple example is like:

```xml
<addressPoll>
  <addresses>
    <addressRange>
      <ip>127.0.0.1</ip>
      <startPort>37000</startPort>
      <endPort>38001</endPort>
    </addressRange>
    <addressRange>
      <ip>127.0.0.1</ip>
      <startPort>47000</startPort>
      <endPort>48005</endPort>
    </addressRange>
    <addressRange>
      <ip>127.0.0.1</ip>
      <startPort>51000</startPort>
      <endPort>51857</endPort>
    </addressRange>
  </addresses>
</addressPoll>
```

This definition includes 2866 unique elastic addresses. Note that `startPort` can not be greater than `endPort`. 


# License

Copyright (C) 2011 [Amir Moulavi](http://amirmoulavi.com)

Distributed under the [Apache Software License](http://www.apache.org/licenses/LICENSE-2.0.html).

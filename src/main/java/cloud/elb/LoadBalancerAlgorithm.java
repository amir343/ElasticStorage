package cloud.elb;

import java.util.List;

import scenarios.manager.Cloud.Node;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-04
 *
 */

public interface LoadBalancerAlgorithm {

	Node getNextNodeFrom(List<Node> replicas);

}

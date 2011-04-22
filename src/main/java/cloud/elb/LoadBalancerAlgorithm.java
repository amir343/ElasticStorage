package cloud.elb;

import instance.Node;

import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-04
 *
 */

public interface LoadBalancerAlgorithm {

	Node getNextNodeFrom(List<Node> replicas);

}

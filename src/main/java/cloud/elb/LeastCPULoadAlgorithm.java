package cloud.elb;

import instance.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-04
 * 
 * <code>LeastCPULoadAlgorithm</code> implements <code>LoadBalancerAlgorithm</code> interface and
 * it decides the next node for the next request based on two constraints:
 * - Least CPU load
 * - Least number of previously sent request
 *
 */

public class LeastCPULoadAlgorithm implements LoadBalancerAlgorithm {

	private static LeastCPULoadAlgorithm instance = new LeastCPULoadAlgorithm();
	private Map<String, NodeStatistics> stats = new HashMap<String, NodeStatistics>();
	
	public static LeastCPULoadAlgorithm getInstance() {
		return instance;
	}
	
	private LeastCPULoadAlgorithm() {
		
	}

	@Override
	public Node getNextNodeFrom(List<Node> replicas) {
		List<NodeStatistics> list = new ArrayList<NodeStatistics>();
		for (Node node : replicas) {
			NodeStatistics stat = getNodeStatisticsFor(node);
			list.add(stat);
		}
		NodeStatistics node = Collections.min(list, new LeastCPULoadAlgorithm.Comp());
		return node.getNode();
	}
	
	public void updateCPULoadFor(Node node, double cpuLoad) {
		NodeStatistics nodeStat = getNodeStatisticsFor(node);
		nodeStat.setCpuLoad(cpuLoad);	
	}

	
	public void increaseNrOfSentRequestFor(Node node) {
		NodeStatistics nodeStat = getNodeStatisticsFor(node);
		nodeStat.increaseNrOfSentRequest();
	}
	
	private NodeStatistics getNodeStatisticsFor(Node node) {
		NodeStatistics nodeStat = stats.get(node.getAddressStringWithoutName());
		if (nodeStat == null ) {
			nodeStat = new NodeStatistics(node);
			stats.put(node.getAddressStringWithoutName(), nodeStat);
		}
		return stats.get(node.getAddressStringWithoutName());
	}
	
	
	public static class Comp implements Comparator<NodeStatistics> {
		@Override
		public int compare(NodeStatistics ns1, NodeStatistics ns2) {
			if (ns1.getCpuLoad() < ns2.getCpuLoad()) return -1;
			else if (ns1.getCpuLoad() > ns2.getCpuLoad()) return 1;
			else {
				if (ns1.getNrOfSentRequest() < ns2.getNrOfSentRequest()) return -1;
				if (ns1.getNrOfSentRequest() > ns2.getNrOfSentRequest()) return 1;
				else return 0;
			}
		}
	}

	

}

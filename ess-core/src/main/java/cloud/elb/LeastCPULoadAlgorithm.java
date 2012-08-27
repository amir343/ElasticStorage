///**
// * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package cloud.elb;
//
//import instance.Node;
//
//import java.util.*;
//
//
///**
// * 
// * @author Amir Moulavi
// * @date 2011-04-04
// * 
// * <code>LeastCPULoadAlgorithm</code> implements <code>LoadBalancerAlgorithm</code> interface and
// * it decides the next node for the next request based on two constraints:
// * - Least CPU load
// * - Least number of previously sent request
// *
// */
//
//public class LeastCPULoadAlgorithm implements LoadBalancerAlgorithm {
//
//	private static LeastCPULoadAlgorithm instance = new LeastCPULoadAlgorithm();
//	private final Map<String, NodeStatistics> stats = new HashMap<String, NodeStatistics>();
//	
//	public static LeastCPULoadAlgorithm getInstance() {
//		return instance;
//	}
//	
//	private LeastCPULoadAlgorithm() {
//		
//	}
//
//	@Override
//	public synchronized Node getNextNodeFrom(List<Node> replicas) {
//		List<NodeStatistics> list = new ArrayList<NodeStatistics>();
//		for (Node node : replicas) {
//			NodeStatistics stat = getNodeStatisticsFor(node);
//			list.add(stat);
//		}
//		NodeStatistics node = Collections.min(list, new LeastCPULoadAlgorithm.Comp());
//		return node.node();
//	}
//
//    @Override
//    public List<Node> selectNodesToRemove(List<Node> nodes, int numberOfNodesToRemove) {
//        List<NodeStatistics> list = new ArrayList<NodeStatistics>();
//        for (Node node : nodes) {
//            NodeStatistics stat = getNodeStatisticsFor(node);
//            list.add(stat);
//        }
//        Collections.sort(list, new Comp());
//        List<Node> result = new ArrayList<Node>();
//        for (int i=0; i< numberOfNodesToRemove && i < list.size(); i++) {
//            result.add(list.get(i).node());
//        }
//        return result;
//    }
//
//    public synchronized void updateCPULoadFor(Node node, double cpuLoad) {
//		NodeStatistics nodeStat = getNodeStatisticsFor(node);
//		nodeStat.setCpuLoad(cpuLoad);	
//	}
//
//	
//	public synchronized void increaseNrOfSentRequestFor(Node node) {
//		NodeStatistics nodeStat = getNodeStatisticsFor(node);
//		nodeStat.increaseNrOfSentRequest();
//	}
//	
//	private NodeStatistics getNodeStatisticsFor(Node node) {
//		NodeStatistics nodeStat = stats.get(node.getAddressStringWithoutName());
//		if (nodeStat == null ) {
//			nodeStat = new NodeStatistics(node);
//			stats.put(node.getAddressStringWithoutName(), nodeStat);
//		}
//		return stats.get(node.getAddressStringWithoutName());
//	}
//
//    public void clear() {
//        stats.clear();
//    }
//
//    public void nodeRemoved(Node node) {
//        synchronized (stats) {
//            stats.remove(node.getAddressStringWithoutName());
//        }
//    }
//
//
//    public static class Comp implements Comparator<NodeStatistics> {
//		@Override
//		public int compare(NodeStatistics ns1, NodeStatistics ns2) {
//			if (ns1.getCpuLoad() < ns2.getCpuLoad()) return -1;
//			else if (ns1.getCpuLoad() > ns2.getCpuLoad()) return 1;
//			else {
//				if (ns1.getNrOfSentRequest() < ns2.getNrOfSentRequest()) return -1;
//				if (ns1.getNrOfSentRequest() > ns2.getNrOfSentRequest()) return 1;
//				else return 0;
//			}
//		}
//	}
//
//	
//
//}

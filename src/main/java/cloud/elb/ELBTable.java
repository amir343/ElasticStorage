package cloud.elb;

import instance.Node;
import instance.common.Block;

import java.util.ArrayList;
import java.util.List;

import cloud.gui.CloudGUI;

import logger.Logger;
import logger.LoggerFactory;


/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-28
 *
 */

public class ELBTable {

	private Logger logger = LoggerFactory.getLogger(ElasticLoadBalancer.class, CloudGUI.getInstance());
	
	private int desireReplicateDegree;
	private List<ELBEntry> entries = new ArrayList<ELBEntry>();
	
	public ELBTable(int desireReplicateDegree) {
		this.desireReplicateDegree = desireReplicateDegree;
	}
	
	public void addEntry(ELBEntry entry) {
		this.entries.add(entry);
	}
	
	public List<Block> prepareBlocksForNode(Node node) {
		List<Block> blocks = new ArrayList<Block>();
		for (ELBEntry entry : entries) {
			if (entry.getNrOfReplicas() < desireReplicateDegree) {
				blocks.add(entry.getBlock());
			}
		}
		if (blocks.size() == 0) {
			selectRandomNumberOfBlocks(blocks, node);
		}
		return blocks;
	}

	private void selectRandomNumberOfBlocks(List<Block> blocks, Node node) {
		for (int i=0; i<entries.size(); i+=2) {
			ELBEntry entry = entries.get(i);
			blocks.add(entry.getBlock());
		}
	}

	public int getDesireReplicateDegree() {
		return desireReplicateDegree;
	}

	public void suspectEntriesForNode(Node node) {
		for (ELBEntry entry : entries) {
			entry.suspect(node);
			logger.warn("Replica " + entry.getBlock().toString() + " for node " + node + " is suspected");
		}		
	}
	
	public void restoreEntriesForNode(Node node) {
		for (ELBEntry entry : entries) {
			entry.restore(node);
			logger.warn("Replica " + entry.getBlock().toString() + " for node " + node + " is restored");
		}		
	}

	public void removeReplicasForNode(Node node) {
		for (ELBEntry entry : entries) {
			entry.removeFor(node);
			logger.warn("Replica " + entry.getBlock().toString() + " for node " + node + " is removed");
		}	
	}

	public void activeBlocksForNode(Node node) {
		for (ELBEntry entry : entries) {
			if (!entry.isActive())
				logger.debug("Block " + entry.getBlock().toString() + " is activated");
			entry.activateFor(node);
		}
		
	}

	public List<Block> getblocks() {
		List<Block> blocks = new ArrayList<Block>();
		for (ELBEntry entry : entries) {
			blocks.add(entry.getBlock());
		}
		return blocks;
	}

	public Node getNextNodeToSendThisRequest(String blockId) {
		ELBEntry entry = getEntryForBlock(blockId);
		if (entry.isActive()) {
			return entry.getNextNodeToSendRequest();
		}
		logger.warn("Entry " + entry + " is not yet activated!");
		return null;
	}

	private ELBEntry getEntryForBlock(String blockId) {
		for (ELBEntry entry : entries) {
			if (entry.getName().equals(blockId))
				return entry;
		}
		return null;
	}

	public List<ELBEntry> getEntries() {
		return entries;
	}
	
}

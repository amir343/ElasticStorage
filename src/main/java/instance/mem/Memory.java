package instance.mem;

import instance.common.AckBlock;
import instance.common.Block;
import instance.common.MemChannel;
import instance.common.MemoryInit;
import instance.common.NAckBlock;
import instance.common.Ready;
import instance.common.Ready.Device;
import instance.common.RequestBlock;
import instance.common.Size;
import instance.common.WriteBlockIntoMemory;
import instance.gui.InstanceGUI;
import instance.os.RestartSignal;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import cloud.common.NodeConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 * <code>Memory></code> represents Memory unit in an OS
 * that implements LFU algorithm
 *
 */

public class Memory extends ComponentDefinition {

	private Logger logger;
	
	// Ports
	Negative<MemChannel> memory = provides(MemChannel.class);
	
	private ConcurrentMap<String, Block> blocks = new ConcurrentHashMap<String, Block>();
	private long capacity = 16000000000L;
	private long currentSize = 0;
	protected boolean enabled = false;

	public Memory() {
		subscribe(initHandler, control);
		
		subscribe(requestBlockHandler, memory);
		subscribe(writeMemoryHandler, memory);
		subscribe(restartSignalHandler, memory);
		subscribe(startMemoryUnit, memory);
	}
	
	Handler<MemoryInit> initHandler = new Handler<MemoryInit>() {
		@Override
		public void handle(MemoryInit event) {
			enabled = true;
			logger = LoggerFactory.getLogger(Memory.class, InstanceGUI.getInstance());
			setCapacity(event.getNodeConfiguration());
			logger.info("Memory with capacity " + Size.getSizeString(capacity) + " is started...");
			InstanceGUI.getInstance().updateMemoryInfoLabel(Size.getSizeString(capacity));
			sendReadySignal();
		}
	};
	
	Handler<StartMemoryUnit> startMemoryUnit = new Handler<StartMemoryUnit>() {
		@Override
		public void handle(StartMemoryUnit event) {
			enabled = true;
			sendReadySignal();
			logger.warn("Memory restarted...");
		}
	};

	Handler<RestartSignal> restartSignalHandler = new Handler<RestartSignal>() {
		@Override
		public void handle(RestartSignal event) {
			logger.warn("Memory shutting down...");
			enabled = false;
			blocks.clear();
			currentSize = 0;
		}
	};

	/**
	 * This handler is responsible for checking to see if the memory has the requested data block
	 */
	Handler<RequestBlock> requestBlockHandler = new Handler<RequestBlock>() {
		@Override
		public void handle(RequestBlock event) {
			if (enabled) {
				logger.debug("Received request " + event);
				if (blockExist(event.getProcess().getRequest().getBlockId())) {
					event.getProcess().setBlockSize(blocks.get(event.getProcess().getRequest().getBlockId()).getSize());
					AckBlock ack = new AckBlock(event.getProcess());
					trigger(ack, memory);
				} else {
					NAckBlock nack = new NAckBlock(event.getProcess());
					trigger(nack, memory);
				}
			}
		}
	};
	
	/**
	 * 
	 */
	Handler<WriteBlockIntoMemory> writeMemoryHandler = new Handler<WriteBlockIntoMemory>() {
		@Override
		public void handle(WriteBlockIntoMemory event) {
			if (enabled) {
				Block block = event.getBlock();
				if (blocks.get(block.getName()) == null) logger.debug("Block " + event.getBlock().getName() + " is now in memory");
				loadIntoMemory(event.getBlock());
			}
		}
	};

	/**
	 * Implements LFU (Least Frequently Used) algorithm
	 * @param block
	 */
	protected void loadIntoMemory(Block block) {
		while (currentSize + block.getSize() > capacity) {
			Block min = Collections.min(blocks.values(), new Block.FrequencyComparator());
			blocks.remove(min.getName());
			currentSize -= min.getSize();
			logger.debug("Block " + min + " removed");
		} 
		block.accessed();
		block.setTimeEnteredInMemory(System.currentTimeMillis());
		blocks.put(block.getName(), block);
		currentSize += block.getSize();
	}

	protected void sendReadySignal() {
		Ready ready = new Ready(Device.MEMORY);
		trigger(ready, memory);		
	}

	protected void setCapacity(NodeConfiguration nodeConfiguration) {
		capacity = nodeConfiguration.getMemory()*Size.GB.getSize();		
	}

	private boolean blockExist(String blockId) {
		if (blocks.get(blockId) != null) {
			blocks.get(blockId).accessed();
			return true;
		}
		else 
			return false;
	}

}

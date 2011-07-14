package instance.mem;

import cloud.common.NodeConfiguration;
import instance.common.*;
import instance.common.Ready.Device;
import instance.gui.DummyInstanceGUI;
import instance.gui.InstanceGUI;
import instance.os.RestartSignal;
import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
            if (event.getNodeConfiguration().getHeadLess()) {
                logger = LoggerFactory.getLogger(Memory.class, new DummyInstanceGUI());
            } else {
                logger = LoggerFactory.getLogger(Memory.class, InstanceGUI.getInstance());
            }
			setCapacity(event.getNodeConfiguration());
			if (!event.getNodeConfiguration().getHeadLess())
                InstanceGUI.getInstance().updateMemoryInfoLabel(Size.getSizeString(capacity));
			printMemoryLog();
			sendReadySignal();
		}

		private void printMemoryLog() {
			logger.raw(" Initializing HighMem for node 0 (00000000:00000000)");
			logger.raw(" Memory: " + Size.getSizeString(capacity) + " available (2759k kernel code, 13900k reserved, 1287k data, 408k init, 0k highmem)");
			logger.raw(" virtual kernel memory layout:");
			logger.raw("     fixmap  : 0xf5716000 - 0xf57ff000   ( 932 kB)");
			logger.raw("     pkmap   : 0xf5400000 - 0xf5600000   (2048 kB)");
			logger.raw("     vmalloc : 0xe6f00000 - 0xf53fe000   ( 228 MB)");
			logger.raw("     lowmem  : 0xc0000000 - 0xe6700000   ( 615 MB)");
			logger.raw("       .init : 0xc13f4000 - 0xc145a000   ( 408 kB)");
			logger.raw("       .data : 0xc12b1dcf - 0xc13f3cc8   (1287 kB)");
			logger.raw("       .text : 0xc1000000 - 0xc12b1dcf   (2759 kB)");
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
				logger.debug("Received request for data block " + event.getProcess().getRequest().getBlockId());
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

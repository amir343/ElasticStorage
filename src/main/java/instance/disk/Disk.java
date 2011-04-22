package instance.disk;

import instance.common.Block;
import instance.common.BlockResponse;
import instance.common.DiskChannel;
import instance.common.DiskInit;
import instance.common.LoadBlock;
import instance.common.ReadBlock;
import instance.common.Ready;
import instance.common.Ready.Device;
import instance.gui.InstanceGUI;
import instance.os.RestartSignal;

import java.util.ArrayList;
import java.util.List;

import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class Disk extends ComponentDefinition {

	private Logger logger;
	
	// Ports
	Negative<DiskChannel> disk = provides(DiskChannel.class);
	
	private boolean diskReady = false;
	protected List<Block> blocks = new ArrayList<Block>();
	private boolean enabled = false;

	public Disk() {
		subscribe(initHandler, control);
		subscribe(loadHandler, disk);
		subscribe(requestBlockHandler, disk);
		subscribe(startDiskUnitHandler, disk);
		subscribe(restartSignalHandler, disk);
	}
	
	Handler<DiskInit> initHandler = new Handler<DiskInit>() {
		@Override
		public void handle(DiskInit event) {
			enabled = true;
			logger = LoggerFactory.getLogger(Disk.class, InstanceGUI.getInstance());
			logger.info("Disk SCSI started...");
			sendReadySignal();
		}
	};
	
	Handler<StartDiskUnit> startDiskUnitHandler = new Handler<StartDiskUnit>() {
		@Override
		public void handle(StartDiskUnit event) {
			enabled = true;		
			logger.warn("Disk restarted...");
			sendReadySignal();
		}
	};
	
	Handler<RestartSignal> restartSignalHandler = new Handler<RestartSignal>() {
		@Override
		public void handle(RestartSignal event) {
			logger.warn("Disk shutting down...");
			enabled = true;
		}
	};
	
	/**
	 * Loads the Disk with initial data blocks
	 */
	Handler<LoadBlock> loadHandler = new Handler<LoadBlock>() {
		@Override
		public void handle(LoadBlock event) {
			if (enabled) {
				blocks = event.getBlocks();
				logger.debug(blocks.size() + " Block(s) are ready for use");
				diskReady = true;
			}
		}
	};
	
	/**
	 * Retrieves the requested data block from disk
	 */
	Handler<ReadBlock> requestBlockHandler = new Handler<ReadBlock>() {
		@Override
		public void handle(ReadBlock event) {
			if (enabled) {
				Block block = findBlock(event.getId());
				if (block == null) {
					logger.error("Block " + event.getId() + " does not exist on the disk");
				} else {
					logger.debug("Block " + event.getId() + " is read");
					BlockResponse blockResponse = new BlockResponse(block);
					blockResponse.setProcess(event.getProcess());
					trigger(blockResponse, disk);
				}
			}
		}
	};

	protected void sendReadySignal() {
		Ready ready = new Ready(Device.DISK);
		trigger(ready, disk);
		
	}

	protected Block findBlock(String id) {
		for (Block block : blocks) {
			if (block.getName().equals(id)) {
				return block;
			}
		}
		return null;
	}
}

package instance.common;

import instance.cpu.OperationDuration;


/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-21
 *
 */

public class DiskWriteOperation extends AbstractOperation {

	private long blockSize;

	public DiskWriteOperation(long blockSize) {
		this.blockSize = blockSize;
	}

	@Override
	public long getDuration(long cpuSpeed) {
		return OperationDuration.getDiskWriteDuration(cpuSpeed, blockSize);
	}

	@Override
	public int getNumberOfOperations() {
		return 1;
	}

}

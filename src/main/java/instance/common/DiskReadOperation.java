package instance.common;

import instance.cpu.OperationDuration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-20
 *
 */

public class DiskReadOperation extends AbstractOperation {

	private long blockSize;

	public DiskReadOperation(long blockSize) {
		this.blockSize = blockSize;
	}
	
	@Override
	public long getDuration(long cpuSpeed) {
		return OperationDuration.getDiskReadDuration(cpuSpeed, blockSize);
	}

	@Override
	public int getNumberOfOperations() {
		return 1;
	}
}

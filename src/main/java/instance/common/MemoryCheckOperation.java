package instance.common;

import instance.cpu.OperationDuration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-20
 *
 */

public class MemoryCheckOperation extends AbstractOperation {

	@Override
	public long getDuration(long cpuSpeed) {
		return OperationDuration.getMemoryCheckDuration();
	}

	@Override
	public int getNumberOfOperations() {
		return 1;
	}
}

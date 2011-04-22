package instance.common;

import instance.cpu.OperationDuration;


public class BootOperation extends AbstractOperation {

	public BootOperation() {
		
	}
	
	@Override
	public long getDuration(long cpuSpeed) {
		return OperationDuration.getBootOperation(cpuSpeed);
	}

	@Override
	public int getNumberOfOperations() {
		return 1;
	}

}

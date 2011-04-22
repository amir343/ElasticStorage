package instance.cpu;


/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-20
 *
 * execute typical instruction         1GHz = 1/1,000,000,000 sec = 1 nanosec
 * fetch from main memory              100 nanosec
 * send 2K bytes over 1Gbps network    20,000 nanosec
 * read 1MB sequentially from memory   250,000 nanosec
 * read 1MB sequentially from disk     20,000,000 nanosec
 *
 */

public class OperationDuration {

	private static Long baseCpuSpeed = 1000000000L;
	private static Long baseBlockSize = 1048576L;
	private static Long readOneMBfromMemory = 5L;
	private static Long readOneMBfromDisk = 200L;
	private static Long oneBootOperation = 10000L;
	
	public static long getMemoryCheckDuration() {
		return 1;
	}
	
	public static long getMemoryReadDuration(Long cpuSpeed, Long blockSize) {
		return (long) ((baseCpuSpeed.doubleValue()/cpuSpeed.doubleValue())*(blockSize.doubleValue()/baseBlockSize.doubleValue())*readOneMBfromMemory.doubleValue());
	}
	
	public static long getMemoryWriteDuration(Long cpuSpeed, Long blockSize) {
		return getMemoryReadDuration(cpuSpeed, blockSize);
	}
	
	public static long getDiskReadDuration(Long cpuSpeed, Long blockSize) {
		return (long) ((baseCpuSpeed.doubleValue()/cpuSpeed.doubleValue())*(blockSize.doubleValue()/baseBlockSize.doubleValue())*readOneMBfromDisk.doubleValue());
	}
	
	public static long getDiskWriteDuration(Long cpuSpeed, Long blockSize) {
		return getDiskReadDuration(cpuSpeed, blockSize);
	}

	public static long getBootOperation(Long cpuSpeed) {
		return (long) ((baseCpuSpeed.doubleValue()/cpuSpeed.doubleValue())*oneBootOperation.doubleValue());
	}
	
	public static void main(String[] args) {
		System.out.println(getBootOperation(2*1024*1024*1024L));
	}
}

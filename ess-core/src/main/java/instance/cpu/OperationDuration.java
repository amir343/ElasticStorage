/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
	
	public static long getExecutionOperation(Long cpuSpeed, Long baseTime) {
		return (long) ((baseTime.doubleValue()*baseCpuSpeed.doubleValue())/cpuSpeed.doubleValue());
	}
	
}

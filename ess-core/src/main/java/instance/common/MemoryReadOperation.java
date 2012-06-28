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
package instance.common;

import instance.cpu.OperationDuration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-20
 *
 */

public class MemoryReadOperation extends AbstractOperation {

	private long blockSize;

	public MemoryReadOperation(long blockSize) {
		this.blockSize = blockSize;
	}
	
	@Override
	public long getDuration(long cpuSpeed) {
		return OperationDuration.getMemoryReadDuration(cpuSpeed, blockSize);
	}

	@Override
	public int getNumberOfOperations() {
		return 1;
	}

}

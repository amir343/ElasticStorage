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
package instance.os;

import instance.common.Request;

import java.util.UUID;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class Process {

	private String pid = UUID.randomUUID().toString();
	private Request request;
	private long blockSize;
	private long remainingBlockSize;
	private long timeout;
	private long snapshot;
	private long currentBandwidth;
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("pid: ").append(pid).append(", ");
			sb.append("blockSize: ").append(blockSize).append(", ");
			sb.append("rBlockSize: ").append(remainingBlockSize).append(", ");
			sb.append("timeout: ").append(timeout).append(", ");
			sb.append("snapshot: ").append(snapshot).append(", ");
			sb.append("currentBandwidth: ").append(currentBandwidth).append(", ");
			sb.append("Request: ").append(request);
		sb.append("}");
		return sb.toString();
	}
	
	public static Process createAbstractProcess() {
		Process p = new Process();
		return p;
	}
	
	private Process() {
		
	}
	
	public Process(Request request) {
		this.request = request;
	}

	public String getPid() {
		return pid;
	}

	public Request getRequest() {
		return request;
	}
	
	public Process setBlockSize(long size) {
		this.blockSize = size;
		return this;
	}

	public long getBlockSize() {
		return blockSize;
	}

	public long getTimeout() {
		return timeout;
	}

	public Process setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	public long getSnapshot() {
		return snapshot;
	}

	public Process setSnapshot(long snapshot) {
		this.snapshot = snapshot;
		return this;
	}

	public long getCurrentBandwidth() {
		return currentBandwidth;
	}

	public Process setCurrentBandwidth(long currentBandwidth) {
		this.currentBandwidth = currentBandwidth;
		return this;
	}

	public long getRemainingBlockSize() {
		return remainingBlockSize;
	}

	public Process setRemainingBlockSize(long remainingBlockSize) {
		this.remainingBlockSize = remainingBlockSize;
		return this;
	}
	
}

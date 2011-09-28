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

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class Block implements Serializable {

	private static final long serialVersionUID = -9129663729280247524L;
	private String name;
	private long size;
	private long accessed = 0;
	private long timeEnteredInMemory = 0;
	
	public Block(String name, long size) {
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}
	
	public synchronized void accessed() {
		accessed++;
	}
	
	public long getNrOfAccessedTimes() {
		return accessed;
	}

	public long getTimeEnteredInMemory() {
		return timeEnteredInMemory;
	}

	public void setTimeEnteredInMemory(long timeEnteredInMemory) {
		this.timeEnteredInMemory = timeEnteredInMemory;
	}

	public boolean equals(Object an) {
		if (an == null)
			return false;
		if (!(an instanceof Block))
			return false;
		Block ob = (Block)an;
		if (this.name.equals(ob.getName()) && this.size == ob.getSize())
			return true;
		return false;
	}
	
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(size).toHashCode();
	}
	
	public static class FrequencyComparator implements Comparator<Block> {
		@Override
		public int compare(Block o1, Block o2) {
			if (o1.getNrOfAccessedTimes() < o2.getNrOfAccessedTimes()) return 1;
			else if (o1.getNrOfAccessedTimes() == o2.getNrOfAccessedTimes()) {
				if ( o1.getTimeEnteredInMemory() < o2.getTimeEnteredInMemory() ) return 1;
				else if (o1.getTimeEnteredInMemory() == o2.getTimeEnteredInMemory()) return -1;
				else return 0;
			}
			else return -1;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("name: ").append(name).append(", ");
			sb.append("size: ").append(size);
		sb.append("}");
		return sb.toString();
	}
}


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

import junit.framework.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-14
 *
 */

public class BlockTest {
	
	@Test
	public void test_equals() {
		Block block1 = new Block("test1", 1212333);
		Block block2 = new Block("test1", 1212333);
		
		Assert.assertEquals("Equals is not working", block1, block2);
	}
	
	@Test
	public void test_hashCode() {
		Block block1 = new Block("test1", 1212333);
		Block block2 = new Block("test1", 1212333);
		
		Assert.assertEquals("hashCode is not working", block1.hashCode(), block2.hashCode());
		
	}
	
	@Test
	public void test_equals_and_hashcode_methods_are_correct() {
		Set<Block> blocks = new HashSet<Block>();
		Block block = new Block("test1", 1212433);
		
		blocks.add(block);
		blocks.add(block);
		
		Assert.assertEquals(1, blocks.size());
	}

}

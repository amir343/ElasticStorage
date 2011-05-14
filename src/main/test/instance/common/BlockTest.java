package instance.common;

import instance.common.Block;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

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

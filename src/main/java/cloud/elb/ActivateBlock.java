package cloud.elb;

import instance.Node;
import instance.common.Block;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-14
 *
 */

public class ActivateBlock extends Message {

	private static final long serialVersionUID = 5597642865139182298L;
	private Node node;
	private Block block;

	public ActivateBlock(Address source, Address destination, Node node, Block block) {
		super(source, destination);
		this.node = node;
		this.block = block;
	}

	public Node getNode() {
		return node;
	}

	public Block getBlock() {
		return block;
	}
	
	

}

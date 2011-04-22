package cloud.api.adress;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class AddressPoll {
	
	private List<AddressRange> addresses = new ArrayList<AddressRange>();
	
	public AddressPoll() {
		
	}

	public List<AddressRange> getList() {
		return addresses;
	}

	public void setList(List<AddressRange> list) {
		this.addresses = list;
	}
	
}

package instance.os;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class Snapshot {

	private int id;
	private Date date = Calendar.getInstance().getTime();
	
	public Snapshot(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}


}

package cloud.requestengine;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class RequestStatistic {

	private long start;
	private long end;
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	
	public long getResponseTime() {
		return end - start;		
	}
	
	
}

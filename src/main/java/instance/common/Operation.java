package instance.common;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-20
 *
 */

public interface Operation {

	public long getDuration(long cpuSpeed);
	public int getNumberOfOperations();
}

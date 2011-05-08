package regression;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import statistics.methods.LeastSqauresRegression;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class RegressionTest {

	private LeastSqauresRegression regression;

	@Before
	public void setup() {
		regression = new LeastSqauresRegression();
		
		regression.setInputOperatingPoint(7.7d);
		regression.setOutputOperatingPoint(1.17d);
		
		regression.addRawData(4, 0.62);
		regression.addRawData(5, 0.72);
		regression.addRawData(5, 0.76);
		regression.addRawData(6, 0.52);
		regression.addRawData(7, 0.91);
		regression.addRawData(8, 0.92);
		regression.addRawData(9, 0.97);
		regression.addRawData(10, 1.52);
		regression.addRawData(11, 1.42);
		regression.addRawData(12, 1.99);
		regression.addRawData(13, 1.95);

	}
	
	@Test
	public void test_least_square_regression_returns_correct_a_b() {
		assertEquals(-0.05086422703356771, regression.get_a(), 0.001);
		assertEquals(0.18658288011609822, regression.get_b(), 0.001);
	}
	
	@Test
	public void test_least_square_regression_calculate_correct_s_i() {
		assertEquals(2.1231, regression.get_s1(), 0.001);
		assertEquals(10.465, regression.get_s2(), 0.001);
		assertEquals(68.1, regression.get_s3(), 0.001);
		assertEquals(1.8445, regression.get_s4(), 0.001);
		assertEquals(12.174, regression.get_s5(), 0.001);
	}
	
}

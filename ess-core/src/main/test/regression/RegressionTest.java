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
package regression;

import org.junit.Before;
import org.junit.Test;
import statistics.methods.LeastSqauresRegression;

import static org.junit.Assert.assertEquals;

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
		
		regression.addRawData(4d, 0.62d);
		regression.addRawData(5d, 0.72d);
		regression.addRawData(5d, 0.76d);
		regression.addRawData(6d, 0.52d);
		regression.addRawData(7d, 0.91d);
		regression.addRawData(8d, 0.92d);
		regression.addRawData(9d, 0.97d);
		regression.addRawData(10d, 1.52d);
		regression.addRawData(11d, 1.42d);
		regression.addRawData(12d, 1.99d);
		regression.addRawData(13d, 1.95d);

	}
	
	@Test
	public void test_least_square_regression_returns_correct_a_b() {
		assertEquals(-0.05669, regression.get_a(), 0.001);
		assertEquals(0.18658, regression.get_b(), 0.001);
	}
	
}

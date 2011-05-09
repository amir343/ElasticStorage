package statistics.methods;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class LeastSqauresRegression {

	private double a;
	private double b;
	
	private List<Double> rawInputs = new ArrayList<Double>();
	private List<Double> rawOutputs = new ArrayList<Double>();
	private List<Double> inputOffsets = new ArrayList<Double>();
	private List<Double> outputOffsets = new ArrayList<Double>();

	public void clear() {
		rawInputs.clear();
		rawOutputs.clear();
		inputOffsets.clear();
		outputOffsets.clear();
	}
	
	public void addRawData(double rawInput, double rawOutput) {
		rawInputs.add(rawInput);
		rawOutputs.add(rawOutput);
	}

	public double get_a() {
		calculateQuantities();
		return a;
	}

	private void calculateQuantities() {
		calculateInputOffsets();
		calculateOutputOffsets();
		double s1 = calculateS1();
		double s2 = calculateS2();
		double s3 = calculateS3();
		double s4 = calculateS4();
		double s5 = calculateS5();
		a = (s3*s4 - s2*s5) / (s1*s3 - s2*s2);
		b = (s1*s5 - s2*s4) / (s1*s3 - s2*s2);
	}

	private void calculateOutputOffsets() {
		outputOffsets.clear();
		double mean = getOutputOperatingPoint();
		for (double out : rawOutputs)
			outputOffsets.add(out - mean);
	}

	private void calculateInputOffsets() {
		inputOffsets.clear();
		double mean = getInputOperatingPoint();
		for (double in : rawInputs)
			inputOffsets.add(in - mean);
	}

	private double calculateS1() {
		double s1 = 0;
		for (int i=0; i<outputOffsets.size()-1; i++) {
			s1 += outputOffsets.get(i)*outputOffsets.get(i);
		}
		return s1;
	}

	private double calculateS2() {
		double s2 = 0;
		for (int i=0; i<outputOffsets.size()-1; i++) {
			s2 += outputOffsets.get(i)*inputOffsets.get(i);
		}
		return s2;
	}

	private double calculateS3() {
		double s3 = 0;
		for (int i=0; i<inputOffsets.size()-1; i++) {
			s3 += inputOffsets.get(i)*inputOffsets.get(i);
		}
		return s3;
	}

	private double calculateS4() {
		double s4 = 0;
		for (int i=0; i<outputOffsets.size()-1; i++) {
			s4 += outputOffsets.get(i)*outputOffsets.get(i+1);
		}
		return s4;
	}

	private double calculateS5() {
		double s5 = 0;
		for (int i=0; i<inputOffsets.size()-1; i++) {
			s5 += inputOffsets.get(i)*outputOffsets.get(i+1);
		}
		return s5;
	}

	public double get_b() {
		return b;
	}

	public double getInputOperatingPoint() {
		if (rawInputs.size() <= 1) return 0.0;
		double mean = 0.0;
		for (int i=0; i<rawInputs.size()-1; i++) {
			mean += rawInputs.get(i);
		}
		return mean/(rawInputs.size()-1);
	}

	public double getOutputOperatingPoint() {
		if (rawOutputs.size() <= 1) return 0.0;
		double mean = 0.0;
		for (int i=1; i<rawOutputs.size(); i++) {
			mean += rawOutputs.get(i);
		}
		return mean/(rawOutputs.size()-1);
	}
	
	public String print() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<rawInputs.size(); i++) {
			sb.append(((i+1) + "\t" + rawInputs.get(i) + "\t" + rawOutputs.get(i) + "\t" + inputOffsets.get(i) + "\t" + outputOffsets.get(i))).append("\n");
		}
		return sb.toString();
	}

	public int getNumberOfSamples() {
		return rawInputs.size();
	}

}

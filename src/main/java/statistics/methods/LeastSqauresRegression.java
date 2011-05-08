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

	private double inputOP;
	private double outputOP;

	public void addRawData(double rawInput, double rawOutput) {
		rawInputs.add(rawInput);
		rawOutputs.add(rawOutput);
		inputOffsets.add(rawInput - inputOP);
		outputOffsets.add(rawOutput - outputOP);
	}

	public void setInputOperatingPoint(double inputOP) {
		this.inputOP = inputOP;		
	}

	public void setOutputOperatingPoint(double outoutOP) {
		this.outputOP = outoutOP;
	}

	public double get_a() {
		calculateQuantities();
		return a;
	}

	private void calculateQuantities() {
		double s1 = calculateS1();
		double s2 = calculateS2();
		double s3 = calculateS3();
		double s4 = calculateS4();
		double s5 = calculateS5();
		a = (s3*s4 - s2*s5) / (s1*s3 - s2*s2);
		b = (s1*s5 - s2*s4) / (s1*s3 - s2*s2);
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

	public double get_s1() {
		return calculateS1();
	}

	public double get_s2() {
		return calculateS2();
	}

	public double get_s3() {
		return calculateS3();
	}

	public double get_s4() {
		return calculateS4();
	}

	public double get_s5() {
		return calculateS5();
	}
	
	public void print() {
		for (int i=0; i<rawInputs.size(); i++) {
			System.out.println((i+1) + "\t" + rawInputs.get(i) + "\t" + rawOutputs.get(i) + "\t" + inputOffsets.get(i) + "\t" + outputOffsets.get(i));
		}
	}

}

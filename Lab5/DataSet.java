package statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataSet {
	private ArrayList<Double> data;
	private int dataSize;

	private static DataSet instance = null;

	//DataSet constructs the dataset by reading from an input file via a scanner
////The file name is passed to the constructor as its only parameter
	private DataSet(String filename) throws FileNotFoundException{
		data = new ArrayList(10);
		try {
			File inputFile = new File(filename);
			Scanner sc = new Scanner(inputFile);

			while(sc.hasNextDouble()){
				data.add(sc.nextDouble());
				dataSize++;
			}

		} catch (FileNotFoundException fnf){
			System.out.println(fnf.getMessage());
		};
		System.out.println("There are " + dataSize + " values in the file");
	}

	public static DataSet getInstance(String filename) throws FileNotFoundException{

		if (instance == null) {

			instance = new DataSet(filename);
		}
		return instance;
	}

	//Mean() computes the arithmetic mean of the data set
	public double Mean(){
		double total = 0.0;
		int count = 0;

		for (Double data1 : data) {
			total += data1;
			count++;
		}
		return total/(count);
	}
	//Median finds the median value of the data set. If the data set has an odd
//number of elements the median is the middle element in a sorted data set
//If the data set has an even number of elements the median is the average
//of the two middle values (which may not be in the data set)
	public double Median(){
		int midPoint = data.size() / 2;
		if(data.size()%2==0)
			return (data.get(midPoint) + data.get((midPoint)-1))/2.0;
		else
			return data.get(midPoint);
	}
	//Minimum returns the smallest value of the data set assuming the data set
//is sorted in ascending order
	public double Minimum() {
		return data.get(0);
	}
	//Maximum returns the largest value of the data set assuming the data set
//is sorted in ascending order
	public double Maximum() {
		return data.get(data.size()-1);
	}
	//Variance computes the average of the variance of elements from the mean
	double Variance(){
		double m = Mean();
		double sum_of_sq = 0;

		for(int i=0;i<data.size();i++){
			sum_of_sq += Math.pow(data.get(i) - m, 2);
		}
		if(data.size()>1)
			return sum_of_sq / (data.size()-1);
		else return 0;

	}
	//StandardDeviation() computes to no great surprise the SD of the data set
	double StandardDeviation(){
		return Math.sqrt(Variance());
	}
	//Old-timey bubble sort in ascending order!
	void sort() {
		boolean swapped = true;

		while(swapped){
			swapped = false;
			for(int i=0;i<data.size()-1;i++)
				if(data.get(i)>data.get(i+1)){
					double temp=data.get(i);
					data.set(i,data.get(i+1));
					data.set(i+1, temp);
					swapped=true;
				}
		}
	}
	//Print(true) prints the data in a 2-d table format, 10 elements per row
//Print(false) prints the data one element per row
	public void Print(boolean table){
		for(int i=0; i < data.size(); i++){
			System.out.printf("%8.2f", data.get(i));
			if(table){
				if(i%10==9)
					System.out.println();
			}
			else
				System.out.println();
		}
		System.out.println();
	}
}

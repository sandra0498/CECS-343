/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package statistics;

		import java.io.FileNotFoundException;

public class Statistics {

	static DataSet ds;

	public static void main(String[] args) throws FileNotFoundException {
		ds = DataSet.getInstance("nums.dat");
		Obs1 observer1 = new Obs1("obs1");
		Obs2 observer2 = new Obs2("Obs2");
		ds.Attach(observer1);
		ds.Attach(observer2);
		ds.sort();
		ds.Print(true);
		System.out.println("mean=" + ds.Mean());
		System.out.println("median=" + ds.Median());
		System.out.println("minimum=" + ds.Minimum());
		System.out.println("maximum=" + ds.Maximum());
		System.out.println("variance=" + + ds.Variance());
		System.out.println("standard deviation=" + + ds.StandardDeviation());
		ds.Detach(observer2);
		ds.Print(false);
	}

}

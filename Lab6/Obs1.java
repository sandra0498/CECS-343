package statistics;

public class Obs1 extends Observer {
	String name;
	public Obs1(String n) {

		name = n;
	}

	@Override
	public void update(String s) {
		if (s.equalsIgnoreCase("Sort")) {
			System.err.println("Obs1 is told that that DataSet is Sorting");

		}

		else if (s.equalsIgnoreCase("print")) {

			System.err.println("Obs1 is told that the DataSet is Printing");
		}

		else if (s.equalsIgnoreCase("calculate")) {


			System.err.println("Obs1 is told that the DataSet is calculating SD");
		}


	}
}

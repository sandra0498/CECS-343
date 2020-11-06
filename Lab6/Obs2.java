package statistics;

public class Obs2 extends Observer {
	String name;
	public Obs2(String n) {

		name = n;
	}

	@Override
	public void update(String s) {
		if (s.equalsIgnoreCase("sort")) {
			System.err.println("Obs2 is trying to ignore the fact that DataSet is Sorting");
		}
		else if (s.equalsIgnoreCase("print")) {
			System.err.println("Obs2 is trying to ignore the fact that DataSet is Printing");
		}

		else if (s.equalsIgnoreCase("calculate")){

			System.err.println("Obs2 is trying to ignore the fact that DataSet is Calculating SD");
		}

	}
}

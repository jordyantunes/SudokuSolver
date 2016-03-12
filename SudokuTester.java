import java.util.*;
import java.util.zip.DataFormatException;

class SudokuTester {
	
	public static void main(String args[])
	{
		Board s;
		Long startTime, endTime;
		boolean verbose = false;

		try {

			s = new Board(args[0]);

			if (args.length == 2)
			{
				if (args[1].equals("v") || args[1].equals("verbose"))
				{
					verbose = true;
				}
			}

			startTime = System.currentTimeMillis();

			System.out.println( s.solve(verbose) );

			endTime = System.currentTimeMillis();
			
			System.out.println("Finished in " + (endTime - startTime) );

        	System.out.println("Time ellapsed : " + ( endTime - startTime) + " in milliseconds");
		}
		catch( DataFormatException e)
		{
			System.out.println(e.getMessage());
			return;
		}
		catch( ArrayIndexOutOfBoundsException e )
		{
			System.out.println("Error : No Input String");
		}

		return;
	}
}

/**
	First assignment for AI class, solving a sudoku using a depth-first algorithm.

	@author Jordy Antunes
	@version 1.0
	@since 01/29/2016
*/

import java.util.*;
import java.util.zip.DataFormatException;
 
class Board{

	//THE BOARD IS DIVIDED IN SECTIONS : 
	//
	// - - - - - - - - - - - - - 
	// | - - - | - - - | - - - |
	// | - 0 - | - 3 - | - 6 - |
	// | - - - | - - - | - - - |
	// - - - - - - - - - - - - - 
	// | - - - | - - - | - - - |
	// | - 1 - | - 4 - | - 7 - |
	// | - - - | - - - | - - - |
	// - - - - - - - - - - - - - 
	// | - - - | - - - | - - - |
	// | - 2 - | - 5 - | - 8 - |
	// | - - - | - - - | - - - |
	// - - - - - - - - - - - - - 

	// SOME COMMENTS THAT I USED TO DEBUG WHERE LEFT IN THE CODE, MAINLY IN THE 
	// solve_puzzle() FUNCTION

	// Point numbers_bucket KEEPS TRACK OF WHERE EACH NUM (1..9) IS IN A SECTION,
	// FILLING THIS IS O(n) WHERE N IS NUMBER OF FIELDS, BUT
	// WHEN CHECKING IF NUMBERS EXIST IN EACH SECTION, THEY
	// CAN BE ACCESSED IN O(1)
	// ACCESS IT BY numbers_bucket[SECTION][ N - 1 ] 
	// WHERE  N = (1..9)

   	private Point[][] numbers_bucket;

   	// int filled_fields KEEPS TRACK OF HOW MANY FIELDS ARE FILLED IN EACH SECTION
   	// EVERY TIME YOU FILL A FIELD, THIS WILL BE UPDATED

   	private int[] filled_fields;

   	private int fields[][];

   	private void initiate_board()
   	{
   		this.fields = new int[9][9];
   		this.numbers_bucket = new Point[9][9]; 

   		for (int i = 0; i < 9; i++)
   		{
   			for (int j = 0; j < 9; j++)
   			{
   				this.numbers_bucket[i][j] = new Point();
   			}
   		}

   		this.filled_fields = new int[9];
   	}
   
   public Board(int[][] fields)
   {
      this.initiate_board();
      
      for (int i = 0; i < 9 ; i++)
      {
         for (int j = 0; j < 9 ; j++ )
         {
            this.fields[i][j] = fields[i][j];
         }
      }
   }
   
   public Board(String fields) throws DataFormatException
   {
      this.initiate_board();
      
      fields = fields.replaceAll("\\s|[^0-9]", "");

      fields = fields.trim();

      if (fields.length() != 81)
      {
         throw new DataFormatException("Error : Wrong number of characters");
      }
      
      for ( int i = 0 ; i < 81 ; i++)
      {
         int num = Character.getNumericValue( fields.charAt(i) ) ;
         
         if ( ! this.insertIntoField(i / 9 , i % 9 , num) ) throw new DataFormatException("Error : This is not a valid Sudoku board"); //this.fields[ i / 9 ][ i % 9] = num;
      }
   }
   
   public Board()
   {  
      this.initiate_board();

      Random rn = new Random();
      rn.setSeed(123456789);
      
      for (int i = 0; i < 9 ; i++)
      {
         for (int j = 0; j < 9 ; j++ )
         {
            this.fields[i][j] = rn.nextInt(10);
         }
      }
   }
   
   @Override 
   public String toString() {
   
      StringBuilder str = new StringBuilder();
      
      for ( int i = 0; i < 9; i ++ )
      {
         if ( i == 0 || i == 3 || i == 6 )
         {
            str.append("- - - - - - - - - - - - -\n");   
         }
         for ( int j = 0; j < 9 ; j++ )
         {
            if ( j == 0 || j == 3 || j == 6 )
            {
               str.append("| ");
            }
            
            str.append( ( ( this.fields[i][j] == 0 )? " " : this.fields[i][j] ) + " ");
            
            if ( j == 8 )
            {
               str.append("|\n");
            }            
         }
         if ( i == 8 )
         {
            str.append("- - - - - - - - - - - - -\n");
         }
      }
      
      return str.toString();
   }

   private int getSection(int x, int y)
   {
   		return ( x / 3) + ( ( y / 3) * 3 ) ;
   }

   private boolean existsInSection(int val, int section)
   {
   		return ( ( this.numbers_bucket[section][val - 1].getX() != -1 )? true : false );
   }

   private boolean insertIntoField(int x, int y, int val)
   {
   		if ( val == 0)
   		{
   			this.fields[x][y] = val;

   			return true;
   		}

 		if ( this.fields[x][y] != 0 )
 		{
 			return false; // FIELD NOT EMPTY
 		} 		
 		else
 		{
 			if (this.existsInSection(val, this.getSection(x,y)))
 			{
 				return false;
 			}
 			// else if (this.canInsertInColumn(x, val) && this.canInsertInRow(y, val)) //NAIVE WAY, SEARCHES FOR 16 FIELDS EVERY TIME
 			else if (this.canInsertInPosition(x, y, val)) // IMPROVED WAY, MIN SEARCHES = 1, MAX = 9
 			{
 				int section = this.getSection(x, y);

				this.numbers_bucket[section][val - 1].setX(x);
				this.numbers_bucket[section][val - 1].setY(y);

				this.filled_fields[section]++;

				this.fields[x][y] = val;
				return true;
 			}
 			else
 			{
 				return false;
 			}
 		}
   }

   private void removeFromField(int x, int y)
   {
   		int val = this.fields[x][y];

   		this.fields[x][y] = 0;
   		this.filled_fields[this.getSection(x, y)]--;

   		this.numbers_bucket[this.getSection(x,y)][val - 1].setX(-1);
   		this.numbers_bucket[this.getSection(x,y)][val - 1].setY(-1);
   }

   private boolean canInsertInColumn(int x, int val)
   {
   		// NAIVE WAY, GOES THROUGH 16 BLOCKS EVERY TIME

   		for (int y = 0; y < 9; y++)
   		{
   			if (this.fields[x][y] == val )
   			{
   				return false;
   			}
   		}

   		return true;
   }

   private boolean canInsertInRow(int y, int val)
   {
   		for (int x = 0; x < 9; x++)
   		{
   			if (this.fields[x][y] == val )
   			{
   				return false;
   			}
   		}
   		return true;
   }

   private boolean canInsertInPosition(int x, int y, int val)
   {
   		int currentSection = this.getSection(x,y);

   		for ( int section = 0; section < 9; section++){
   			
   			if (section == currentSection) continue;

   			if ( x == numbers_bucket[section][val - 1].getX() || y == numbers_bucket[section][val - 1].getY() )
   			{
   				return false;
   			}
   		}
   		return true;
   }

   //THIS METHOD WILL RETURN -1 IF ALL SECTIONS AS FULL
   private int getMostFilledSection() 
   {
   		int max = 0;

   		for (int section = 1; section < 9; section++)
   		{

   			if (this.filled_fields[max] == 9){ //in case the first one is full
   				max = section;
   				continue;
   			} 

   			if (this.filled_fields[section] == 9 ) continue; //doesnt let max be replaced by a full section
   			

   			if (this.filled_fields[section] > this.filled_fields[max])
   			{
   				max = section;
   			}
   		}

   		if (this.filled_fields[max] == 9)
   		{
			return -1;
   		}

   		return max;
   }

   //PROBABLY NOT THE BEST WAY TO DO THIS
   private  int[] getSectionsInOrder(){

   		int[] sections = new int[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1};
   		int min = -1;

   		for (int section = 0; section < 9; section++) {

   			if ( this.filled_fields[section] == 9) continue;

   			for (int position = 0; position < 9; position++)
   			{
   				if ( sections[position] == -1 ) {
   					sections[position] = section;
   					min++;

   					break;
   				}

   				if ( this.filled_fields[section] > this.filled_fields[sections[position]])	
   				{
   					int aux = sections[position];
   					sections[position] = section;		

	   				for ( int i = position + 1; i <= min; i++ )
	   				{
	   					sections[i] = sections[i] + aux;
	   					aux = sections[i] - aux;
	   					sections[i] = sections[i] - aux;
	   				}
	   				min++;
   					
   					break;
   				}
   			}
   		}

   		return sections;
   }

   private ArrayList<Point> getPointsInSection(int section)
   {
   		ArrayList<Point> points = new ArrayList<Point>();

   		for (int x = 0; x < 3; x++)
   		{
   			for (int y = 0; y < 3; y++)
   			{
   				int x1 = x + ( (section % 3) * 3 );
   				int y1 = y + ( (section / 3) * 3 );

   				if ( this.fields[x1][y1] == 0 )
   					points.add(new Point(x1, y1));
   			}
   		}

   		return points;
   }

   private Point getFirstPointInSection(int section)
   {
      for (int x = 0; x < 3; x++)
         {
            for (int y = 0; y < 3; y++)
            {
               int x1 = x + ( (section % 3) * 3 );
               int y1 = y + ( (section / 3) * 3 );

               if ( this.fields[x1][y1] == 0 )
                  return new Point(x1, y1);
            }
         }

         return null;
   }

   public String solve(boolean verbose)
   {
   		this.solvePuzzle(verbose);

   		return this.toString();
   }

   private boolean solvePuzzle(boolean verbose)
   {
      if (verbose){
		    System.out.println(this.toString()); // PRINT CURRENT STATE OF SUDOKU
      }

		// THIS CAN CREATE A LOOP WHEN 2 SECTIONS HAD THE SAME NUMBER OF FILLED FIELDS
		int section = this.getMostFilledSection();
		if (section == -1) return true; //ALL SECTIONS AS FULL

      if (verbose)
      {
		    System.out.println("Section : " + section);
      }

      Point point = this.getFirstPointInSection(section); //this.getPointsInSection(section).get(0);

		//System.out.println("Point : (" + point.getX() + "," + point.getY() + ")");

		if (this.fields[point.getX()][point.getY()] != 0)
		{
         if (verbose){
			   System.out.println("Field is not 0");
         }
         return false;
		}

		for (int num = 1; num <= 9; num ++)
		{
			//System.out.print(" " + num);

			if ( this.insertIntoField(point.getX(), point.getY(), num) ) { //insertion successfull
				
				//System.out.println("\nInserted " + num + " in (" + point.getX() + "," + point.getY() + ")");			

				if ( this.solvePuzzle(verbose) ) {
					return true;
				}
				else {
					this.removeFromField(point.getX(), point.getY());
					//System.out.println("\nRemoved " + num + " from (" + point.getX() + "," + point.getY() + ")");
				}
			}
		}
   	
		return false;
   }
}
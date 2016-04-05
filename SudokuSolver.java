import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Class for solving a sudoku puzzle, the user will input the layout of the
 * puzzle.
 */
public class SudokuSolver
{
   private final static int MAX_NUM_OF_ROWS            = 8;
   private final static int MAX_NUM_OF_SQUARES         = 80;
   private final static int NUM_OF_ROW_COL_SUBSECTIONS = 3;
   private final static int NO_OPEN_SPACE_FOUND        = -1;

   /**
    * Creates a sudoku puzzle given the input from the user.
    * 
    * @param input
    *           The scanner that will read the user's input.
    * @param puzzleBoard
    *           The puzzleBooard that will be created given the user's input.
    * @return An array of the letters or digits that will be used in the puzzle.
    */
   private static String[] createPuzzle(Scanner input, String[][] puzzleBoard)
   {

      // The values that will be used for this puzzle.
      String[] puzzleValues;
      int row; // The row of the value
      int col; // The column of the value
      String value;

      // Set the delimiting pattern to include commas and new lines.
      input.useDelimiter("[\\r\\n,]+");
      
      // Get the first line of input that dictates the puzzle's layout.
      puzzleValues = input.next().split("");

      // If there is more input to get, then get it and configure the board
      // appropriately
      while (input.hasNext())
      {

         // Get the row, column, and value of the thing that is to be placed.
         row = input.nextInt();
         col = input.nextInt();
         value = input.next();

         // Take one away from row and col since the input that was given used
         // 1-based indexing.
         // Place value at the location on the board.
         puzzleBoard[row - 1][col - 1] = value;
      }

      input.close();
      return puzzleValues;
   }


   /**
    * Attempts to solve to sudoku puzzle. Returns true if the puzzle is solved,
    * otherwise false.
    * 
    * @param puzzleBoard
    *           the Sudoku puzzle board that is to be solved.
    * @param puzzleValues
    *           The values that are to be used with the puzzle
    * @param row
    *           The current row that is being tried for solutions.
    * @param col
    *           The current column that is being tried for solutions
    * @return True if the puzzle is solved, otherwise false.
    */
   private static boolean solvePuzzle(String[][] puzzleBoard,
         String[] puzzleValues, int row, int col)
   {
      boolean puzzleSolved = false;

      // If the entire board has been filled, then we know that it is solved.
      if (row > MAX_NUM_OF_ROWS && col > MAX_NUM_OF_SQUARES)
      {
         puzzleSolved = verifyBoard(puzzleBoard);
      }

      // Otherwise, if there is already a value in this space, then find the
      // next open space on the board.
      else if (puzzleBoard[row][col % 9] != null)
      {
         col = findNextOpenSpace(puzzleBoard);

         // If no open space exists, then verify if the board is solved.
         if (col == NO_OPEN_SPACE_FOUND)
         {
            puzzleSolved = verifyBoard(puzzleBoard);
         }

         // Otherwise try to solve the puzzle starting from the empty position.
         else
         {
            puzzleSolved = solvePuzzle(puzzleBoard, puzzleValues, col / 9, col);
         }
      }

      // Otherwise, then this space is empty and we'll try adding values to it.
      else
      {
         for (int i = 0; i < puzzleValues.length && !puzzleSolved; i++)
         {

            // If the placement is a valid move, then make the placement.
            if (isValidMove(puzzleBoard, puzzleValues[i], row, col))
            {
               puzzleBoard[row][col % 9] = puzzleValues[i];

               // Try to solve the puzzle with the new value having been placed.
               if (solvePuzzle(puzzleBoard, puzzleValues, (col + 1) / 9,
                     col + 1))
               {
                  puzzleSolved = true;
               }

               // Otherwise, if the puzzle couldn't be solved, remove the
               // placement from the board.
               else
               {
                  puzzleBoard[row][col % 9] = null;
               }
            }
         }
      }

      return puzzleSolved;
   }


   /**
    * Checks to see if the given value can be placed at the given position on
    * the given board.
    * 
    * @param puzzleBoard
    *           The board that has an opening at row & col
    * @param value
    *           The value that is to be placed at row & col
    * @param row
    *           The row of the empty space
    * @param col
    *           The column (% 9) of the empty space.
    * @return True if the value can be placed there, otherwise false
    */
   private static boolean isValidMove(String[][] puzzleBoard, String value,
         int row, int col)
   {
      boolean isPromising = true;
      int rowSection; // Which group of 3 is the row in
      int colSection; // Which group of 3 is the col in

      // Iterate through the row and col and make sure that no value appears
      // twice. Since we know that the puzzleBoard is a square, we can look at
      // both the column and row in the same iteration.
      for (int i = 0; i < puzzleBoard[row].length && isPromising; i++)
      {

         // if value is already present in the row then the placement is not
         // promising.
         if (puzzleBoard[row][i] != null)
         {
            if (puzzleBoard[row][i].equals(value))
            {
               isPromising = false;
            }
         }

         // If value is already present in the column, then the placement is not
         // promising.
         if (puzzleBoard[i][col % 9] != null && isPromising)
         {
            if (puzzleBoard[i][col % 9].equals(value))
            {
               isPromising = false;
            }
         }
      }

      // Now check for repeats in the 3x3 subsection.
      // Find which of the 3 subsections of rows and columns that row and col
      // reside in.
      rowSection = row / NUM_OF_ROW_COL_SUBSECTIONS;
      colSection = (col % 9) / NUM_OF_ROW_COL_SUBSECTIONS;

      // Iterate through the 3x3 subsection
      for (int i = 0; i < NUM_OF_ROW_COL_SUBSECTIONS && isPromising; i++)
      {
         for (int j = 0; j < NUM_OF_ROW_COL_SUBSECTIONS && isPromising; j++)
         {

            // If value is already present in the 3x3 subsection, then the
            // placement is not promising.
            if (puzzleBoard[(rowSection * NUM_OF_ROW_COL_SUBSECTIONS)
                  + i][(colSection * NUM_OF_ROW_COL_SUBSECTIONS) + j] != null)
            {
               if (puzzleBoard[(rowSection * NUM_OF_ROW_COL_SUBSECTIONS)
                     + i][(colSection * NUM_OF_ROW_COL_SUBSECTIONS) + j]
                           .equals(value))
               {
                  isPromising = false;
               }
            }
         }
      }

      return isPromising;
   }


   /**
    * Verifies if the given board is solved or not.
    * 
    * @param puzzleBoard
    *           A sudoku board.
    * @return True if the board is solved, otherwise false.
    */
   private static boolean verifyBoard(String[][] puzzleBoard)
   {
      HashSet<String> valuesInRow = new HashSet<>();
      HashSet<String> valuesInCol = new HashSet<>();
      HashSet<String> valuesInSquare = new HashSet<>();
      boolean isSolved = true;

      // Iterate through each row and column in the board and ensure that each
      // row or column doesn't have any duplicate values.
      for (int i = 0; i < puzzleBoard.length && isSolved; i++)
      {

         // Clear the valuesInRow and valuesInCol sets so that they are empty
         // for use with the ith row and column.
         valuesInRow.clear();
         valuesInCol.clear();

         for (int j = 0; j < puzzleBoard[i].length && isSolved; j++)
         {

            // Check the jth value in the ith row to see if it's a duplicate
            // value for its respective row
            if (puzzleBoard[i][j] != null)
            {
               isSolved = valuesInRow.add(puzzleBoard[i][j]);
            }

            // Check the jth value in the ith column to see if it's a duplicate
            // value for its respective row.
            if (isSolved && puzzleBoard[j][i] != null)
            {
               isSolved = valuesInCol.add(puzzleBoard[j][i]);
            }
         }
      }

      // For each of the 3 row sections, look at the 3 column sections.
      for (int i = 0; i < NUM_OF_ROW_COL_SUBSECTIONS && isSolved; i++)
      {
         for (int j = 0; j < NUM_OF_ROW_COL_SUBSECTIONS && isSolved; j++)
         {

            // Clear valuesInSquare so that it's empty for use with the ith, jth
            // 3x3 subsection.
            valuesInSquare.clear();

            // Now iterate through all 9 squares in the 3x3 subsection and look
            // for any duplicates.
            for (int k = 0; k < NUM_OF_ROW_COL_SUBSECTIONS && isSolved; k++)
            {
               for (int l = 0; l < NUM_OF_ROW_COL_SUBSECTIONS && isSolved; l++)
               {
                  // Check to see if the this value appears more than once in
                  // the 3x3 subsection.
                  if (puzzleBoard[(i * NUM_OF_ROW_COL_SUBSECTIONS)
                        + k][(j * NUM_OF_ROW_COL_SUBSECTIONS) + l] != null)
                  {
                     isSolved = valuesInSquare
                           .add(puzzleBoard[(i * NUM_OF_ROW_COL_SUBSECTIONS)
                                 + k][(j * NUM_OF_ROW_COL_SUBSECTIONS) + l]);
                  }
               }
            }
         }
      }

      return isSolved;
   }


   /**
    * Finds the first occurrence of an open space on the given puzzle board and
    * returns the location of this open space.
    * 
    * @param puzzleBoard
    *           The puzzle board with possibly open spaecs.F
    * @return The location of the open space (0 to 80), or -1 if no space was
    *         found.
    */
   private static int findNextOpenSpace(String[][] puzzleBoard)
   {
      int nextOpenSpace;
      int row = 0;
      int col = 0;
      boolean openSpaceFound = false;

      // Go through all the squares on the board and look for the first
      // occurrence of an empty one.
      for (int i = 0; i < puzzleBoard.length && !openSpaceFound; i++)
      {
         for (int j = 0; j < puzzleBoard[i].length && !openSpaceFound; j++)
         {

            // If an open space was found, then record the row and column that
            // it was found at.
            if (puzzleBoard[i][j] == null)
            {
               openSpaceFound = true;
               row = i;
               col = j;
            }
         }
      }

      // If an open space was found, then set the openSpace's location so that
      // it fits in the range of 0 to 80, and not based of row/col coordinates.
      if (openSpaceFound)
      {
         nextOpenSpace = (row * 9) + col;
      }

      // Otherwise, if no open space was found, then set nextOpenSpace to -1 to
      // signify that no space was found.
      else
      {
         nextOpenSpace = NO_OPEN_SPACE_FOUND;
      }

      return nextOpenSpace;
   }


   /**
    * Prints out the puzzle board to the given PrintStream
    * 
    * @param puzzleBoard
    *           The sudoku puzzle that will be printed out
    * @param output
    *           The PrintStream that will be printed to.
    */
   private static void displayBoard(String[][] puzzleBoard, PrintStream output)
   {

      // Print the top line of the puzzle board.
      output.print("-------------------------------------\n");

      // Iterate through all the squares in the puzzle, printing them out to
      // represent the sudoku board.
      for (int i = 0; i < puzzleBoard.length; i++)
      {
         for (int j = 0; j < puzzleBoard[i].length; j++)
         {
            output.print("| " + puzzleBoard[i][j] + " ");
         }

         // Print the horizontal line that separates the rows.
         output.print("|\n-------------------------------------\n");
      }
   }


   public static void main(String[] args)
   {
      boolean isSolved;
      String[][] puzzleBoard = new String[9][9];
      String[] puzzleValues;

      // Create the puzzle
      System.out.println("Enter the parameters for the sudoku puzzle: ");
      puzzleValues = createPuzzle(new Scanner(System.in), puzzleBoard);

      // Verify the initial configuration of the board. If it's bad, then don't
      // bother trying to solve it.
      if (!verifyBoard(puzzleBoard))
      {
         isSolved = false;
      }

      // Otherwise try to solve the puzzle
      else
      {
         isSolved = solvePuzzle(puzzleBoard, puzzleValues, 0, 0);
      }

      // If the puzzle was solved, then display the completed puzzle
      // Otherwise, display a message indicating that the puzzle can't be
      // solved.
      if (isSolved)
      {
         displayBoard(puzzleBoard, new PrintStream(System.out));
      }
      else
      {
         System.err.println("This puzzle can't be solved");
      }
   }
}
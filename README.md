# Sudoku-Solver
Sudoku puzzle solver, made for CSIS 430: Analysis of Algorithms

Compile as $ javac SudokuSolver.java
Run as $ java SudokuSolver

The program will wait for the user to enter the initial set up of the board which is as follows: nine characters representing the nine characters that will be present in the puzzle. Followed by 0 or more lines indicating the initial placement of values in the following form: row,col,value so entering 1,2,3 would place 3 in the second column of the first row.

So an example of input would be as follows:
123456789
1,1,1
4,3,5
9,9,9

The input can be entered either directly via the command line, or by using a text file and giving the input to the program (i.e. $ java SudokuSolver < samplePuzzle.txt)

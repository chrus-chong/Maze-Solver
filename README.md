### Maze Solver

Different variations of MazeSolvers created for the NUS CS2040S Algorithms and Data Structures module. Like most mazes, this mazes here consists of one or more rooms. Each of the rooms has doors to one or more other rooms. The aim of each MazeSolver would be to explore the maze, as is, and discover the shortest path from the start location to a destination location.

### Preliminaries
A maze consists of an r × c grid of rooms, with adjacent rooms separated by either a wall or an empty space. Furthermore, the entire maze is bordered by walls. The rooms are numbered starting from the top left corner (which is (0, 0)). The first coordinate specifies the row number, and the second coordinate specifies the column number. For example, in a 5 × 5 maze, the bottom left corner is (4, 0) and the top right corner is (0, 4). Here is a pictorial example of a 5 × 5 maze:	

![Maze Illustration](https://github.com/chrus-chong/Maze-Solver/blob/master/exampleImg/Maze%20illustration.png "Example")

In this diagram, each wall is depicted using a hash symbol, i.e., #, while each room is depicted using the letter R (this is for visualization purposes only – in the actual maze files, the rooms are represented as empty spaces as well!). Notice that there are exactly c rooms and at most c + 1 walls (including the left and right borders) on each row.
Movement is allowed between adjacent rooms in any of the four cardinal directions (north, south, east and west) if there is no wall in that direction. Diagonal movement is not allowed. In the above example, one can move from (0,0) to (0,1), but NOT from (0,0) to (1,0), nor from (0,0) to (1,1).

![Solved Maze](https://github.com/chrus-chong/Maze-Solver/blob/master/exampleImg/Solved%20maze.png "Figure 1")
Figure 1: Example printMaze output of a solved maze

The two classes Maze and Room that represent the maze that the program solves. The size of the maze is represented by the number of rows and columns in the maze (in the above example, both rows and columns are 5). The maze itself is represented by a matrix of rooms.

The Maze class has a static method readMaze(String fileName) that reads in a maze from a text file and returns the maze object. Several sample mazes are available in the maze templates folder to experiment with. The static class MazePrinter also provides a simplistic way of visualizing a maze. The static method void printMaze(Maze maze) of the MazePrinter class prints out a maze to the standard output1 , as per the example in Figure 1.

### How it works
The src folder contains 3 possible MazeSolvers
1. MazeSolverNaive.java
2. MazeSolver.java
3. MazeSolverWithPower.java

The MazeSolverNaive programme (courtesy of NUS School of Computing as a reference for students) does a depth-first search to solve the shortest path in a maze. The other 2 MazeSolvers (entirely my own work) run based on breadth first search to find the shortest path efficiently. The number of rooms reachable with each step is stored in a hashmap while computing the shortest path to solve the maze. This will reduce the computation needed. 

MazeSolverWithPower brings an added twist to the challenge of finding the shortest path by allowing the user to have a limited number of powers to break down walls. The difficulty, then lies in deciding which walls to break down to achieve the shortest path.

### Running the program
Run MazeSolver.java or MazeSolverWithPower.java 

or
```
Maze maze = Maze.readMaze("maze-empty.txt");
IMazeSolver solver = new MazeSolver();
solver.initialize(maze);
solver.pathSearch(0, 0, 3, 3);
MazePrinter.printMaze(maze);
```

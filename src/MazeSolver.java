import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MazeSolver implements IMazeSolver {
	private static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	private static int[][] DELTAS = new int[][] {
		{ -1, 0 }, // North
		{ 1, 0 }, // South
		{ 0, 1 }, // East
		{ 0, -1 } // West
	};

	private Maze maze;
	private boolean solved = false;
	private boolean[][] visited;
	private int pathStartRow, pathStartCol;
	public HashMap<Integer, ArrayList<Integer[]>> minStepsToReach = new HashMap<>();
	public MazeSolver() {
		solved = false;
		maze = null;
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		if (maze != null) {
			visited = new boolean[maze.getRows()][maze.getColumns()];
			solved = false;
		}
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		minStepsToReach = new HashMap<>();
		solved = false;
		if (maze == null) {
			return null;
		}
		this.pathStartRow = startRow;
		this.pathStartCol = startCol;
		// set all visited flag to false
		// before we begin our search
		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				this.visited[i][j] = false;
				maze.getRoom(i, j).onPath = false;
			}
		}
		if (startRow == endRow && startCol == endCol) {
			maze.getRoom(endRow, endCol).onPath = true;
		}

		boolean invalid = false;
		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			invalid = true;
			startRow = (startRow<0) ? 0 : startRow;
			startCol = (startCol<0) ? 0 : startCol;
			startRow = (startRow>= maze.getRows()) ? maze.getRows() : startRow;
			startCol = (startCol>= maze.getColumns()) ? maze.getColumns() : startCol;
			endRow = (endRow<0) ? 0 : endRow;
			endCol = (endCol<0) ? 0 : endCol;
			endRow = (endRow>= maze.getRows()) ? maze.getRows() : endRow;
			endCol = (endCol>= maze.getColumns()) ? maze.getColumns() : endCol;
		}



		int[] possibleDirection = {0,1,2,3};

		ArrayList<ArrayList<Integer[]>> routes = new ArrayList<ArrayList<Integer[]>>();
		ArrayList<ArrayList<Integer[]>> reachDst = new ArrayList<ArrayList<Integer[]>>();
		Integer[] helper = {startRow, startCol};
		visited[startRow][startCol] = true;

		int stepCount = 0;
		ArrayList<Integer[]> helper2 = new ArrayList<>();
		Integer[] temp = {startRow, startCol};
		helper2.add(temp);
		minStepsToReach.put(stepCount, helper2);
		stepCount++;

		ArrayList<Integer[]> starting = new ArrayList<Integer[]>();
		starting.add(helper);
		routes.add(starting);

		while (true) {
			ArrayList<ArrayList<Integer[]>> toRemove = new ArrayList<>();
			ArrayList<ArrayList<Integer[]>> toAdd = new ArrayList<>();
			ArrayList<Integer[]> newCoords = new ArrayList<>();

			for (ArrayList<Integer[]> path : routes) {
				for (int direction : possibleDirection) {
					int yCoord = path.get(path.size() - 1)[0];
					int xCoord = path.get(path.size() - 1)[1];
					if (canGo(yCoord, xCoord, direction)) {
						ArrayList<Integer[]> nextPath = new ArrayList<Integer[]>();
						nextPath.addAll(path);
						Integer[] nextCoord = {yCoord + DELTAS[direction][0], xCoord + DELTAS[direction][1]};
						if (visited[nextCoord[0]][nextCoord[1]]) {
							continue;
						} else {
							newCoords.add(nextCoord);
							visited[nextCoord[0]][nextCoord[1]] = true;
						}
						nextPath.add(nextCoord);
						if (nextCoord[0] == endRow && nextCoord[1] == endCol) {
							reachDst.add(nextPath);
						}
						toAdd.add(nextPath);
					}
				}
				toRemove.add(path);
			}
			minStepsToReach.put(stepCount, newCoords);
			stepCount++;

			for (ArrayList<Integer[]> np : toAdd) {
				routes.add(np);
			}

			for (ArrayList<Integer[]> extra : toRemove) {
				routes.remove(extra);
			}

			if (routes.size() == 0) {
				break;
			}
		}
		if (invalid) {
			return null;
		}
		if (reachDst.size()==0) {
			return (startRow == endRow && startCol == endCol) ? 0 : null;
		}
		solved = true;
		ArrayList<Integer[]> shortest = reachDst.get(0);
		int min = reachDst.get(0).size();
		for (ArrayList<Integer[]> p : reachDst) {
			if (p.size() < min) {
				min = p.size();
				shortest = p;
			}
		}

		changeOnPath(shortest);
		return shortest.size() - 1; //as shortest contains all of the points travelled to, inclusive of the starting point

		// TODO: Find shortest path.
	}


	public void changeOnPath(ArrayList<Integer[]> nextPath) {
		for (int i = 0; i < nextPath.size(); i++) {
			int row = nextPath.get(i)[0];
			int col = nextPath.get(i)[1];
			maze.getRoom(row, col).onPath = true;
		}
		return;
	}

	private boolean canGo(int row, int col, int dir) {
		if (row + DELTAS[dir][0] < 0 || row + DELTAS[dir][0] >= maze.getRows()) return false;
		if (col + DELTAS[dir][1] < 0 || col + DELTAS[dir][1] >= maze.getColumns()) return false;

		switch (dir) {
			case NORTH:
				return !maze.getRoom(row, col).hasNorthWall();
			case SOUTH:
				return !maze.getRoom(row, col).hasSouthWall();
			case EAST:
				return !maze.getRoom(row, col).hasEastWall();
			case WEST:
				return !maze.getRoom(row, col).hasWestWall();
		}

		return false;
	}

	@Override
	public Integer numReachable(int k) throws Exception {
		// TODO: Find number of reachable rooms.
		return (minStepsToReach.containsKey(k)) ? minStepsToReach.get(k).size() : 0;
	}

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("maze templates/maze-sample.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze);

			System.out.println(solver.pathSearch(2, 2, 4, 0));
			MazePrinter.printMaze(maze);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

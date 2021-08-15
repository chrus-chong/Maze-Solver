import java.util.ArrayList;
import java.util.HashMap;

public class MazeSolverWithPower extends MazeSolver implements IMazeSolverWithPower {
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
	public HashMap<Integer, Integer> minStepsToReach = new HashMap<>();



	public MazeSolverWithPower() {
		// TODO: Initialize variables.
		solved = false;
		maze = null;
	}

	@Override
	public void initialize(Maze maze) {
		// TODO: Initialize the solver.
		this.maze = maze;
		if (maze != null) {
			visited = new boolean[maze.getRows()][maze.getColumns()];
			solved = false;
		}
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
		return (minStepsToReach.containsKey(k)) ? minStepsToReach.get(k) : 0;
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow,
							  int endCol, int superpowers) throws Exception {
		// TODO: Find shortest path with powers allowed. REMEMBER TO CLEAR ALL OBJECT ATTRIBUTES WITH EACH CALL TO PATHSEARCH

		if (maze == null) {
			return null;
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

		this.pathStartRow = startRow;
		this.pathStartCol = startCol;

		if (superpowers <= 0) {
			return pathSearch(startRow, startCol, endRow, endCol);
		}


		Integer[] start = {startRow, startCol};
		Route startRoute = new Route(superpowers);
		startRoute.add(start);
		ArrayList<Route> frontier = new ArrayList<>();
		frontier.add(startRoute);
		ArrayList<Route> reachDst = new ArrayList<>();

		int steps = 1;
		int stepsToDst;

		this.minStepsToReach = new HashMap<>();
		minStepsToReach.put(0, 1);
		solved = false;
		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				this.visited[i][j] = false;
				maze.getRoom(i, j).onPath = false;
			}
		}
		visited[startRow][startCol] = true;

		if (endRow == startRow && endCol == startCol) {
			maze.getRoom(startRow, endRow).onPath = true;
		}

		while (true) {
			int roomsReachable = 0;
			if (frontier.isEmpty()) {
				break;
			}

			ArrayList<Route> toAdd = new ArrayList<>();
			ArrayList<Route> toRemove = new ArrayList<>();
			for (Route path : frontier) {
				Integer[] coord = path.coords.get(path.coords.size() - 1);
				for (int i = 0; i < 4; i++) { //this is for directions
					Integer[] nextCoord = {coord[0] + DELTAS[i][0], coord[1] + DELTAS[i][1]};
					if (canGo(coord[0], coord[1], i) && !coordInRoute(nextCoord, path)) {
						Route possibleP = new Route(path.coords, path.superpowers);
						possibleP.add(nextCoord);

						if (nextCoord[0] == endRow && nextCoord[1] == endCol) {
							reachDst.add(possibleP);
						}
						toAdd.add(possibleP);
					} else if (canUsePower(path, i) && !coordInRoute(nextCoord, path)) {
						Route possibleP = new Route(path.coords, path.superpowers);
						possibleP.addUsePower(nextCoord);

						if (nextCoord[0] == endRow && nextCoord[1] == endCol) {
							reachDst.add(possibleP);
						}
						toAdd.add(possibleP);
					}
				}
				toRemove.add(path);
			}

			for (Route re : toRemove) {
				frontier.remove(re);
			}
			for (Route ad : toAdd) {
				frontier.add(ad);
				Integer[] coord = ad.coords.get(ad.coords.size() - 1);
				if (!visited[coord[0]][coord[1]]) {
					roomsReachable++;
				}
				visited[coord[0]][coord[1]] = true;
			}

			minStepsToReach.put(steps, roomsReachable);
			steps++;
		}

		if (invalid) {
			maze.getRoom(startRow, endRow).onPath = false;
			return null;
		}

		if (reachDst.size() == 0) {
			return (startRow == endRow && startCol == endCol) ? 0 : null;
		}


		int smallest = reachDst.get(0).coords.size()-1;
		for (Route path : reachDst) {
			if (path.coords.size() - 1 < smallest) {
				smallest = path.coords.size() - 1;
			}
		}
		stepsToDst = smallest;

		for (Route path : reachDst) {
			if (path.coords.size() - 1 == smallest) {
				path.setOnPath(this.maze);
				break;
			}
		}

		if (stepsToDst < 0) {
			return null;
		}
		solved = true;
		return stepsToDst;
	}

	public boolean coordInRoute(Integer[] coord, Route path) {
		for (Integer[] c : path.coords) {
			if (c[0]==coord[0] && c[1]==coord[1]) {
				return true;
			}
		}
		return false;
	}

	public boolean canUsePower(Route path, int direction) {
		int superpowers = path.superpowers;
		int row = path.coords.get(path.coords.size() - 1)[0];
		int col = path.coords.get(path.coords.size() - 1)[1];

		if (superpowers == 0) {
			return false;
		} else {
			if (row + DELTAS[direction][0] < 0 || row + DELTAS[direction][0] >= maze.getRows()) return false;
			if (col + DELTAS[direction][1] < 0 || col + DELTAS[direction][1] >= maze.getColumns()) return false;

			if (superpowers > 0) {
				return true;
			}
			return false;
		}
	}

	public void printArrayList(ArrayList<Integer[]> list) { //purely for debugging purposes
		if (list == null) {
			System.out.print("null");
			return;
		}
		for (Integer[] a : list) {
			System.out.print("[ " + a[0] + ", " + a[1] + "],  ");
		}
		System.out.println();
	}

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("maze templates/maze-big.txt");
			IMazeSolverWithPower solver = new MazeSolverWithPower();
			solver.initialize(maze);


			System.out.println(solver.pathSearch(2, 7, 8, 7, 2));
			MazePrinter.printMaze(maze);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
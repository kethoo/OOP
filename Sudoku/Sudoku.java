import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.

	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
			"1 6 4 0 0 0 0 0 2",
			"2 0 0 4 0 3 9 1 0",
			"0 0 5 0 8 0 4 0 7",
			"0 9 0 0 0 6 5 0 0",
			"5 0 0 1 0 2 0 0 8",
			"0 0 8 9 0 0 0 3 0",
			"8 0 9 0 4 0 2 0 0",
			"0 7 3 5 0 9 0 0 1",
			"4 0 0 0 0 0 6 7 9");


	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
			"530070000",
			"600195000",
			"098000060",
			"800060003",
			"400803001",
			"700020006",
			"060000280",
			"000419005",
			"000080079");

	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
			"3 7 0 0 0 0 0 8 0",
			"0 0 1 0 9 3 0 0 0",
			"0 4 0 7 8 0 0 0 3",
			"0 9 3 8 0 0 0 1 2",
			"0 0 0 0 4 0 0 0 0",
			"5 2 0 0 0 6 7 9 0",
			"6 0 0 0 2 1 0 4 0",
			"0 0 0 5 3 0 9 0 0",
			"0 3 0 0 0 0 0 5 1");


	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;

	// Provided various static utility methods to
	// convert data formats to int[][] grid.

	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}


	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}

		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}


	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);

		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}

	private int[][] grid;
	private ArrayList<Spot> spots;
	private String solutionText;
	private long elapsedTime;

	public Sudoku(int[][] ints) {
		grid = ints;


		spots = new ArrayList<>();
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				if (grid[row][col] == 0) {  // Add only empty spots
					spots.add(new Spot(row, col));
				}
			}
		}
	}

	public Sudoku(String text) {
		this(textToGrid(text));
	}

	public class Spot implements Comparable<Spot> {
		private int row;
		private int col;

		public Spot(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public Set<Integer> getPossibleValues() {
			Set<Integer> possible = new HashSet<>();
			for (int i = 1; i <= SIZE; i++) {
				possible.add(i);
			}

			for (int c = 0; c < SIZE; c++) {
				if (grid[row][c] != 0) {
					possible.remove(grid[row][c]);
				}
			}

			for (int r = 0; r < SIZE; r++) {
				if (grid[r][col] != 0) {
					possible.remove(grid[r][col]);
				}
			}

			int boxRow = (row / PART) * PART;
			int boxCol = (col / PART) * PART;
			for (int r = boxRow; r < boxRow + PART; r++) {
				for (int c = boxCol; c < boxCol + PART; c++) {
					if (grid[r][c] != 0) {
						possible.remove(grid[r][c]);
					}
				}
			}

			return possible;
		}

		public void set(int value) {
			grid[row][col] = value;
		}

		public void clear() {
			grid[row][col] = 0;
		}

		@Override
		public int compareTo(Spot other) {
			int thisPossibleCount = this.getPossibleValues().size();
			int otherPossibleCount = other.getPossibleValues().size();
			return thisPossibleCount - otherPossibleCount;
		}
	}

	public int solve() {
		Collections.sort(spots);

		long startTime = System.currentTimeMillis();

		int[][] originalGrid = new int[SIZE][SIZE];
		for (int row = 0; row < SIZE; row++) {
			System.arraycopy(grid[row], 0, originalGrid[row], 0, SIZE);
		}

		int solutions = Math.min(solveRecursive(0, new ArrayList<>()),100);


		elapsedTime = System.currentTimeMillis() - startTime;

		grid = originalGrid;

		return solutions;
	}

	private int solveRecursive(int spotIndex, ArrayList<int[][]> solutions) {
		if (spotIndex >= spots.size()) {
			int[][] solution = new int[SIZE][SIZE];
			for (int row = 0; row < SIZE; row++) {
				System.arraycopy(grid[row], 0, solution[row], 0, SIZE);
			}
			solutions.add(solution);

			if (solutions.size() == 1) {
				solutionText = toString();
			}

			return 1;
		}

		if (solutions.size() >= MAX_SOLUTIONS) {
			return solutions.size();
		}

		Spot spot = spots.get(spotIndex);
		Set<Integer> possibleValues = spot.getPossibleValues();

		int count = 0;
		for (int value : possibleValues) {
			spot.set(value);
			count += solveRecursive(spotIndex + 1, solutions);
			spot.clear();
		}

		return count;
	}
	
	public String getSolutionText() {
		return solutionText == null ? "" : solutionText;
	}
	
	public long getElapsed() {
		return elapsedTime;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				sb.append(grid[row][col]);
				sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
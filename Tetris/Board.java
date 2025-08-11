// Board.java

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
 */
public class Board {
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;

	// These are the backup arrays for undo
	private boolean[][] xGrid;
	private int[] xWidths;
	private int[] xHeights;
	private int xMaxHeight;

	// Added for efficient operations
	private int[] widths;
	private int[] heights;
	private int maxHeight;

	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;

		xGrid = new boolean[width][height];
		widths = new int[height];
		xWidths = new int[height];
		heights = new int[width];
		xHeights = new int[width];

		maxHeight = 0;
	}

	/**
	 Returns the width of the board in blocks.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}

	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	 */
	public void sanityCheck() {
		if (DEBUG) {
			for (int y = 0; y < height; y++) {
				int calculatedWidth = 0;
				for (int x = 0; x < width; x++) {
					if (grid[x][y]) calculatedWidth++;
				}
				if (widths[y] != calculatedWidth) {
					throw new RuntimeException("widths array inconsistent at y=" + y +
							". Expected: " + calculatedWidth + ", Found: " + widths[y]);
				}
			}

			int calculatedMaxHeight = getCalculatedMaxHeight();

			if (maxHeight != calculatedMaxHeight) {
				throw new RuntimeException("maxHeight inconsistent. Expected: " +
						calculatedMaxHeight + ", Found: " + maxHeight);
			}
		}
	}

	private int getCalculatedMaxHeight() {
		int calculatedMaxHeight = 0;
		for (int x = 0; x < width; x++) {
			int calculatedHeight = 0;
			for (int y = 0; y < height; y++) {
				if (grid[x][y]) {
					calculatedHeight = Math.max(calculatedHeight, y + 1);
				}
			}
			if (heights[x] != calculatedHeight) {
				throw new RuntimeException("heights array inconsistent at x=" + x +
						". Expected: " + calculatedHeight + ", Found: " + heights[x]);
			}
			calculatedMaxHeight = Math.max(calculatedMaxHeight, calculatedHeight);
		}
		return calculatedMaxHeight;
	}

	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.

	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {
		int result = 0;
		int[] skirt = piece.getSkirt();

		for (int i = 0; i < piece.getWidth(); i++) {
			int y = heights[x + i] - skirt[i];
			result = Math.max(result, y);
		}

		return result;
	}

	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	 */
	public int getColumnHeight(int x) {
		return heights[x];
	}

	/**
	 Returns the number of filled blocks in
	 the given row.
	 */
	public int getRowWidth(int y) {
		return widths[y];
	}

	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	 */
	public boolean getGrid(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return true;
		}
		return grid[x][y];
	}

	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;

	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.

	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	 */
	public int place(Piece piece, int x, int y) {
		if (!committed) throw new RuntimeException("place commit problem");

		backup();

		int result = PLACE_OK;

		if (x < 0 || x + piece.getWidth() > width || y < 0 || y + piece.getHeight() > height) {
			return PLACE_OUT_BOUNDS;
		}

		TPoint[] body = piece.getBody();

		for (TPoint point : body) {
			int pieceX = x + point.x;
			int pieceY = y + point.y;

			if (grid[pieceX][pieceY]) {
				return PLACE_BAD;
			}
		}

		for (TPoint point : body) {
			int pieceX = x + point.x;
			int pieceY = y + point.y;

			grid[pieceX][pieceY] = true;

			widths[pieceY]++;
			if (widths[pieceY] == width) {
				result = PLACE_ROW_FILLED;
			}

			heights[pieceX] = Math.max(heights[pieceX], pieceY + 1);
		}

		updateMaxHeight();

		committed = false;
		sanityCheck();
		return result;
	}

	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	 */
	public int clearRows() {
		int rowsCleared = 0;

		if (committed) {
			backup();
			committed = false;
		}

		int writeY = 0;
		for (int readY = 0; readY < height; readY++) {
			// If this row is filled, skip it (don't copy it)
			if (widths[readY] == width) {
				rowsCleared++;
				continue;
			}

			if (writeY != readY) {
				for (int x = 0; x < width; x++) {
					grid[x][writeY] = grid[x][readY];
				}
				widths[writeY] = widths[readY];
			}
			writeY++;
		}

		for (int y = writeY; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid[x][y] = false;
			}
			widths[y] = 0;
		}

		for (int x = 0; x < width; x++) {
			heights[x] = 0;
			for (int y = height - 1; y >= 0; y--) {
				if (grid[x][y]) {
					heights[x] = y + 1;
					break;
				}
			}
		}

		updateMaxHeight();

		sanityCheck();
		return rowsCleared;
	}

	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	 */
	public void undo() {
		if (!committed) {
			boolean[][] tempGrid = grid;
			grid = xGrid;
			xGrid = tempGrid;

			int[] tempWidths = widths;
			widths = xWidths;
			xWidths = tempWidths;

			int[] tempHeights = heights;
			heights = xHeights;
			xHeights = tempHeights;

			maxHeight = xMaxHeight;

			committed = true;
			sanityCheck();
		}
	}

	/**
	 Puts the board in the committed state.
	 */
	public void commit() {
		committed = true;
	}

	/**
	 Makes a backup of the current board state.
	 */
	private void backup() {
		for (int x = 0; x < width; x++) {
			System.arraycopy(grid[x], 0, xGrid[x], 0, height);
		}

		System.arraycopy(widths, 0, xWidths, 0, height);

		System.arraycopy(heights, 0, xHeights, 0, width);

		xMaxHeight = maxHeight;
	}

	/**
	 Updates the maxHeight based on the heights array.
	 */
	private void updateMaxHeight() {
		maxHeight = 0;
		for (int x = 0; x < width; x++) {
			maxHeight = Math.max(maxHeight, heights[x]);
		}
	}

	/*
     Renders the board state as a big String, suitable for printing.
     This is the sort of print-obj-state utility that can help see complex
     state change over time.
     (provided debugging utility)
     */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}
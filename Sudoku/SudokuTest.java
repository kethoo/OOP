import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

public class SudokuTest {
    private Sudoku easy;
    private Sudoku medium;
    private Sudoku hard;

    @Before
    public void setUp() {
        easy = new Sudoku(Sudoku.easyGrid);
        medium = new Sudoku(Sudoku.mediumGrid);
        hard = new Sudoku(Sudoku.hardGrid);
    }

    @Test
    public void testConstructors() {
        Sudoku grid = new Sudoku(Sudoku.easyGrid);
        assertNotNull("Grid constructor should create a non-null object", grid);

        String text = "530070000600195000098000060800060003400803001700020006060000280000419005000080079";
        Sudoku string = new Sudoku(text);
        assertNotNull("String constructor should create a non-null object", string);
    }

    @Test
    public void testStringToInts() {
        int[] result = Sudoku.stringToInts("1 2 3");
        assertArrayEquals("Should extract numbers from string", new int[]{1, 2, 3}, result);

        result = Sudoku.stringToInts("1a2b3c");
        assertArrayEquals("Should ignore non-digit characters", new int[]{1, 2, 3}, result);

        result = Sudoku.stringToInts("");
        assertArrayEquals("Should handle empty string", new int[]{}, result);
    }

    @Test
    public void testStringsToGrid() {
        int[][] grid = Sudoku.stringsToGrid("1 2 3", "4 5 6");
        assertEquals("Grid should have correct number of rows", 2, grid.length);
        assertArrayEquals("First row should match", new int[]{1, 2, 3}, grid[0]);
        assertArrayEquals("Second row should match", new int[]{4, 5, 6}, grid[1]);
    }

    @Test
    public void testTextToGrid() {
        String text = "123456789";
        int[][] grid = Sudoku.textToGrid(text + text + text + text + text + text + text + text + text);
        assertEquals("Grid should be 9x9", 9, grid.length);
        assertEquals("Grid should be 9x9", 9, grid[0].length);

        // Test first row
        for (int i = 0; i < 9; i++) {
            assertEquals("Grid values should match input", i + 1, grid[0][i]);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testTextToGridWithInvalidLength() {
        Sudoku.textToGrid("123");
    }

    @Test
    public void testToString() {
        String output = easy.toString();
        assertNotNull("toString should return a non-null string", output);
        assertTrue("toString should contain grid values", output.contains("1") && output.contains("6"));
    }

    @Test
    public void testSpotPossibleValues() {
        int[][] testGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            testGrid[0][i] = i + 1;
        }
        Sudoku sudoku = new Sudoku(testGrid);

        Sudoku.Spot spot = sudoku.new Spot(1, 0);

        Set<Integer> possibleValues = spot.getPossibleValues();

        for (int i = 4; i <= 9; i++) {
            assertTrue("Value " + i + " should be possible", possibleValues.contains(i));
        }
    }

    @Test
    public void testSpotSetAndClear() {
        int[][] testGrid = new int[9][9];
        Sudoku sudoku = new Sudoku(testGrid);
        Sudoku.Spot spot = sudoku.new Spot(0, 0);

        spot.set(5);
        assertEquals("Grid should be updated after set", 5, testGrid[0][0]);

        spot.clear();
        assertEquals("Grid should be cleared after clear", 0, testGrid[0][0]);
    }

    @Test
    public void testSpotCompareTo() {
        // Create a test grid where one spot has fewer possibilities than another
        int[][] testGrid = new int[9][9];
        // Set up first row with 1-8 (leaving last cell empty)
        for (int i = 0; i < 8; i++) {
            testGrid[0][i] = i + 1;
        }
        // Set up first column with 1 in second row (creating a spot with only 9 as possibility)
        testGrid[1][0] = 1;

        Sudoku sudoku = new Sudoku(testGrid);
        Sudoku.Spot spotWithOnePossibility = sudoku.new Spot(0, 8); // Only 9 is possible here
        Sudoku.Spot spotWithManyPossibilities = sudoku.new Spot(2, 2); // More possibilities here

        assertTrue("Spot with one possibility should come before spot with many possibilities",
                spotWithOnePossibility.compareTo(spotWithManyPossibilities) < 0);
    }

    @Test
    public void testSolveEasy() {
        int solutions = easy.solve();
        assertTrue("Easy puzzle should have at least one solution", solutions >= 1);
        assertFalse("Solution text should not be empty", easy.getSolutionText().isEmpty());
        assertTrue("Elapsed time should be measured", easy.getElapsed() > -1);
    }

    @Test
    public void testSolveMedium() {
        int solutions = medium.solve();
        assertTrue("Medium puzzle should have at least one solution", solutions >= 1);
        assertFalse("Solution text should not be empty", medium.getSolutionText().isEmpty());
    }

    @Test
    public void testSolveHard() {
        int solutions = hard.solve();
        assertEquals("Hard puzzle should have exactly one solution", 1, solutions);
        assertFalse("Solution text should not be empty", hard.getSolutionText().isEmpty());
    }

    @Test
    public void testModifiedHardGridWithMultipleSolutions() {
        int[][] modifiedHardGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(Sudoku.hardGrid[i], 0, modifiedHardGrid[i], 0, 9);
        }
        modifiedHardGrid[0][1] = 0; // Replace the 7 with 0

        Sudoku modifiedHard = new Sudoku(modifiedHardGrid);
        int solutions = modifiedHard.solve();
        assertEquals("Modified hard puzzle should have 6 solutions", 6, solutions);
    }

    @Test
    public void testEmptyGrid() {
        int[][] emptyGrid = new int[9][9];
        Sudoku empty = new Sudoku(emptyGrid);
        int solutions = empty.solve();
        assertEquals("Empty grid should find MAX_SOLUTIONS solutions", Sudoku.MAX_SOLUTIONS, solutions);
    }

    @Test
    public void testCompletelyFilledGrid() {
        String filledSudoku =
                "534678912" +
                        "672195348" +
                        "198342567" +
                        "859761423" +
                        "426853791" +
                        "713924856" +
                        "961537284" +
                        "287419635" +
                        "345286179";

        Sudoku filled = new Sudoku(filledSudoku);
        int solutions = filled.solve();
        assertEquals("Already solved grid should have 1 solution", 1, solutions);
    }
}
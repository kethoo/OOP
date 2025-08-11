import junit.framework.TestCase;


public class BoardTest extends TestCase {
	Board b;
	Piece pyr1, pyr2, pyr3, pyr4, s, sRotated;
	Piece l, stick, square;

	protected void setUp() throws Exception {
		b = new Board(3, 6);

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		l = new Piece(Piece.L1_STR);
		stick = new Piece(Piece.STICK_STR);
		square = new Piece(Piece.SQUARE_STR);

		b.place(pyr1, 0, 0);
	}

	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(1, b.getColumnHeight(2));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}

	public void testSample2() {
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}

	public void testPlacement() {
		b.commit();

		int result = b.place(l, 1, 2);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(5, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(5, b.getMaxHeight());

		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(2, b.getRowWidth(2));
		assertEquals(1, b.getRowWidth(3));
		assertEquals(1, b.getRowWidth(4));

		b.undo();
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(1, b.getColumnHeight(2));
		assertEquals(2, b.getMaxHeight());
	}

	public void testOutOfBounds() {
		b.commit();

		int result = b.place(pyr1, 1, 0);
		assertEquals(Board.PLACE_OUT_BOUNDS, result);

		result = b.place(pyr1, -1, 0);
		assertEquals(Board.PLACE_OUT_BOUNDS, result);

		result = b.place(stick, 0, 3);
		assertEquals(Board.PLACE_OUT_BOUNDS, result);

		result = b.place(pyr1, 0, -1);
		assertEquals(Board.PLACE_OUT_BOUNDS, result);
	}

	public void testCollisions() {
		b.commit();

		int result = b.place(square, 0, 0);
		assertEquals(Board.PLACE_BAD, result);

		result = b.place(square, 1, 2);
		assertEquals(Board.PLACE_OK, result);
	}

	public void testClearRows() {
		b.commit();

		int result = b.place(stick.computeNextRotation(), 0, 0);
		assertEquals(Board.PLACE_OUT_BOUNDS, result);

		assertEquals(3, b.getRowWidth(0));

		int cleared = b.clearRows();
		assertEquals(1, cleared);

		assertEquals(1, b.getRowWidth(0));
		assertEquals(0, b.getColumnHeight(0));
		assertEquals(1, b.getColumnHeight(1));
		assertEquals(0, b.getColumnHeight(2));
		assertEquals(1, b.getMaxHeight());
	}

	public void testMultipleRowClear() {
		Board board = new Board(4, 6);

		Piece stick4 = stick.computeNextRotation();

		board.place(stick4, 0, 0);
		board.commit();

		board.place(stick4, 0, 1);

		assertEquals(4, board.getRowWidth(0));
		assertEquals(4, board.getRowWidth(1));

		int cleared = board.clearRows();
		assertEquals(2, cleared);

		assertEquals(0, board.getRowWidth(0));
		assertEquals(0, board.getRowWidth(1));
		assertEquals(0, board.getMaxHeight());
	}

	public void testDropHeight() {
		b.commit();

		assertEquals(2, b.dropHeight(square, 0));

		assertEquals(2, b.dropHeight(square, 0));

		Board board = new Board(5, 10);
		board.place(stick, 0, 0);
		try{
			board.place(stick, 4, 0);
		}
		catch(Exception e){
			assertTrue(true);
		}
		board.commit();

		assertEquals(4, board.dropHeight(pyr1, 0));
		assertEquals(0, board.dropHeight(pyr1, 1));
		assertEquals(4, board.dropHeight(stick.computeNextRotation(), 0));
	}

	public void testUndo() {
		Board board = new Board(4, 6);

		board.place(pyr1, 0, 0);
		board.commit();

		board.place(square, 0, 2);

		assertEquals(4, board.getMaxHeight());
		assertEquals(4, board.getColumnHeight(0));
		assertEquals(4, board.getColumnHeight(1));

		board.undo();
		assertEquals(2, board.getMaxHeight());
		assertEquals(1, board.getColumnHeight(0));
		assertEquals(2, board.getColumnHeight(1));
		assertEquals(1, board.getColumnHeight(2));

		board.undo();
		assertEquals(2, board.getMaxHeight());
		assertEquals(1, board.getColumnHeight(0));
		assertEquals(2, board.getColumnHeight(1));
		assertEquals(1, board.getColumnHeight(2));
	}

	public void testPlaceClearUndo() {
		Board board = new Board(4, 6);

		Piece stick4 = stick.computeNextRotation();
		board.place(stick4, 0, 0);
		board.commit();

		board.place(stick4, 0, 1);

		int cleared = board.clearRows();
		assertEquals(2, cleared);
		assertEquals(0, board.getMaxHeight());

		board.undo();
		assertEquals(1, board.getMaxHeight());
		assertEquals(4, board.getRowWidth(0));
		assertEquals(0, board.getRowWidth(1));
	}

	public void testGetGrid() {
		assertTrue(b.getGrid(0, 0));
		assertTrue(b.getGrid(1, 0));
		assertTrue(b.getGrid(2, 0));
		assertTrue(b.getGrid(1, 1));

		assertFalse(b.getGrid(0, 1));
		assertFalse(b.getGrid(2, 1));

		assertTrue(b.getGrid(-1, 0));
		assertTrue(b.getGrid(0, -1));
		assertTrue(b.getGrid(3, 0));
		assertTrue(b.getGrid(0, 6));
	}

	public void testRowFilledPlacement() {
		Board board = new Board(3, 6);

		board.place(pyr1, 0, 0);
		board.commit();

		int result = board.place(l, 0, 0);

		assertEquals(Board.PLACE_BAD, result);
		assertEquals(3, board.getRowWidth(0));
	}

	public void testComplexSequence() {
		Board board = new Board(4, 8);

		board.place(pyr1, 0, 0);
		board.commit();

		board.place(l, 2, 0);
		board.commit();

		board.place(square, 0, 2);
		board.commit();

		assertEquals(4, board.getMaxHeight());
		assertEquals(4, board.getColumnHeight(0));
		assertEquals(4, board.getColumnHeight(1));

		Piece stick4 = stick.computeNextRotation();
		board.place(stick4, 0, 0);

		int cleared = board.clearRows();
		assertEquals(0, cleared);

		assertEquals(3, board.getRowWidth(0));
		assertEquals(4, board.getMaxHeight());

		board.undo();
		assertEquals(4, board.getMaxHeight());
		assertEquals(3, board.getRowWidth(0));
	}
}
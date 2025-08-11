import junit.framework.TestCase;

import java.util.*;

/*
  Unit test for Piece class
 */
public class PieceTest extends TestCase {
	// You can create data to be used in your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s1, s2;
	private Piece l1, l2;
	private Piece stick1, stick2;
	private Piece square;

	protected void setUp() throws Exception {
		super.setUp();

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s1 = new Piece(Piece.S1_STR);
		s2 = s1.computeNextRotation();

		l1 = new Piece(Piece.L1_STR);
		l2 = l1.computeNextRotation();

		stick1 = new Piece(Piece.STICK_STR);
		stick2 = stick1.computeNextRotation();

		square = new Piece(Piece.SQUARE_STR);
	}

	public void testDimensions() {
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());

		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());

		assertEquals(3, pyr3.getWidth());
		assertEquals(2, pyr3.getHeight());

		assertEquals(2, pyr4.getWidth());
		assertEquals(3, pyr4.getHeight());

		assertEquals(3, s1.getWidth());
		assertEquals(2, s1.getHeight());

		assertEquals(2, s2.getWidth());
		assertEquals(3, s2.getHeight());

		assertEquals(2, l1.getWidth());
		assertEquals(3, l1.getHeight());

		assertEquals(3, l2.getWidth());
		assertEquals(2, l2.getHeight());

		assertEquals(1, stick1.getWidth());
		assertEquals(4, stick1.getHeight());

		assertEquals(4, stick2.getWidth());
		assertEquals(1, stick2.getHeight());

		assertEquals(2, square.getWidth());
		assertEquals(2, square.getHeight());
	}

	public void testSkirt() {
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, pyr2.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 1}, pyr4.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, s2.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0}, l1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, l2.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0}, stick1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, stick2.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0}, square.getSkirt()));
	}

	public void testComputeNextRotation() {
		Piece next = pyr1;
		for (int i = 0; i < 4; i++) {
			next = next.computeNextRotation();
		}
		assertEquals(pyr1, next);

		next = s1;
		for (int i = 0; i < 2; i++) {
			next = next.computeNextRotation();
		}
		assertEquals(s1, next);

		next = stick1;
		for (int i = 0; i < 2; i++) {
			next = next.computeNextRotation();
		}
		assertEquals(stick1, next);

		next = square.computeNextRotation();
		assertEquals(square, next);
	}

	public void testFastRotation() {
		Piece[] pieces = Piece.getPieces();

		Piece p = pieces[Piece.PYRAMID];
		assertEquals(3, p.getWidth());
		assertEquals(2, p.getHeight());

		Piece p2 = p.fastRotation();
		assertEquals(2, p2.getWidth());
		assertEquals(3, p2.getHeight());

		Piece p3 = p2.fastRotation();
		assertEquals(3, p3.getWidth());
		assertEquals(2, p3.getHeight());

		Piece p4 = p3.fastRotation();
		assertEquals(2, p4.getWidth());
		assertEquals(3, p4.getHeight());

		Piece p5 = p4.fastRotation();
		assertEquals(p, p5);

		Piece sq = pieces[Piece.SQUARE];
		Piece sq2 = sq.fastRotation();
		assertEquals(sq, sq2);

		Piece st = pieces[Piece.STICK];
		Piece st2 = st.fastRotation();
		Piece st3 = st2.fastRotation();
		assertEquals(st, st3);
	}

	public void testEquals() {
		Piece p1 = new Piece(Piece.PYRAMID_STR);
		Piece p2 = new Piece(Piece.PYRAMID_STR);
		assertTrue(p1.equals(p2));

		Piece p3 = new Piece("1 0 0 0 2 0 1 1");
		assertTrue(p1.equals(p3));

		Piece s = new Piece(Piece.S1_STR);
		assertFalse(p1.equals(s));

		assertFalse(p1.equals(p1.computeNextRotation()));

		assertTrue(p1.equals(p1));

		assertFalse(p1.equals(null));
		assertFalse(p1.equals("Not a piece"));
	}

	// Test getBody method
	public void testGetBody() {
		assertEquals(4, pyr1.getBody().length);
		assertEquals(4, s1.getBody().length);
		assertEquals(4, stick1.getBody().length);

		assertEquals(pyr1.getBody().length, pyr2.getBody().length);
		assertEquals(s1.getBody().length, s2.getBody().length);
		assertEquals(stick1.getBody().length, stick2.getBody().length);

		TPoint[] body = pyr1.getBody();
		boolean found0_0 = false;
		boolean found1_0 = false;
		boolean found1_1 = false;
		boolean found2_0 = false;

		for (TPoint p : body) {
			if (p.x == 0 && p.y == 0) found0_0 = true;
			if (p.x == 1 && p.y == 0) found1_0 = true;
			if (p.x == 1 && p.y == 1) found1_1 = true;
			if (p.x == 2 && p.y == 0) found2_0 = true;
		}

		assertTrue(found0_0);
		assertTrue(found1_0);
		assertTrue(found1_1);
		assertTrue(found2_0);
	}

	// Test getPieces method
	public void testGetPieces() {
		Piece[] pieces = Piece.getPieces();

		assertEquals(7, pieces.length);

		assertEquals(new Piece(Piece.STICK_STR), pieces[Piece.STICK]);
		assertEquals(new Piece(Piece.L1_STR), pieces[Piece.L1]);
		assertEquals(new Piece(Piece.L2_STR), pieces[Piece.L2]);
		assertEquals(new Piece(Piece.S1_STR), pieces[Piece.S1]);
		assertEquals(new Piece(Piece.S2_STR), pieces[Piece.S2]);
		assertEquals(new Piece(Piece.SQUARE_STR), pieces[Piece.SQUARE]);
		assertEquals(new Piece(Piece.PYRAMID_STR), pieces[Piece.PYRAMID]);
	}
}
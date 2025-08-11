import junit.framework.TestCase;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BankTest extends TestCase {
    private Bank bank;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @Override
    protected void setUp() throws Exception {
        bank = new Bank();

        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @Override
    protected void tearDown() throws Exception {
        System.setOut(originalOut);
    }

    public void testTransaction() {
        Transaction transaction = new Transaction(0, 1, 5000);
        assertEquals(0, transaction.from);
        assertEquals(1, transaction.to);
        assertEquals(5000, transaction.amount);
        assertEquals("from:0 to:1 amt:5000", transaction.toString());
    }

    public void testSmallTxt() throws Exception {
        bank.processFile("small.txt", 1);

        String output = outContent.toString();

        assertTrue(output.contains("acct:0 bal:999 trans:1"));
        assertTrue(output.contains("acct:1 bal:1001 trans:1"));
        assertTrue(output.contains("acct:18 bal:999 trans:1"));
        assertTrue(output.contains("acct:19 bal:1001 trans:1"));
    }

    public void testMultipleWorkers() throws Exception {
        bank.processFile("small.txt", 4);

        String output = outContent.toString();

        assertTrue(output.contains("acct:0 bal:999 trans:1"));
        assertTrue(output.contains("acct:1 bal:1001 trans:1"));
        assertTrue(output.contains("acct:18 bal:999 trans:1"));
        assertTrue(output.contains("acct:19 bal:1001 trans:1"));
    }

    public void test5kTxt() throws Exception {
        bank.processFile("5k.txt", 1);

        String output = outContent.toString();

        for (int i = 0; i < Bank.ACCOUNTS; i++) {
            assertTrue("Account " + i + " should have balance 1000",
                    output.contains("acct:" + i + " bal:1000"));
        }
    }

    public void test100kTxt() throws Exception {
        bank.processFile("100k.txt", 1);

        String output = outContent.toString();

        for (int i = 0; i < Bank.ACCOUNTS; i++) {
            assertTrue("Account " + i + " should have balance 1000",
                    output.contains("acct:" + i + " bal:1000"));
        }
    }
}
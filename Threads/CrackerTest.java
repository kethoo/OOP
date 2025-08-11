import junit.framework.TestCase;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;

public class CrackerTest extends TestCase {
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    protected void setUp() {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    protected void tearDown() {
        System.setOut(originalOut);
    }

    public void testHexToString() {
        byte[] input = {10, 20, 30, 40, 50};
        String result = Cracker.hexToString(input);
        assertEquals("0a141e2832", result);
    }

    public void testHexToArray() {
        String input = "0a141e2832";
        byte[] result = Cracker.hexToArray(input);
        assertEquals(5, result.length);
        assertEquals(10, result[0]);
        assertEquals(20, result[1]);
        assertEquals(30, result[2]);
        assertEquals(40, result[3]);
        assertEquals(50, result[4]);
    }

    public void testGenerationMode() {
        String[] args = {"a", "1"};
        Cracker.main(args);
        String output = outContent.toString().trim();
        assertEquals("86f7e437faa5a7fce15d1ddcb9eaeaea377667b8", output);
    }

    public void testCrackingSingleChar() {
        String targetHash = "86f7e437faa5a7fce15d1ddcb9eaeaea377667b8";
        String[] args = {targetHash, "1", "4"};
        Cracker.main(args);
        String output = outContent.toString();
        assertTrue(output.contains("a"));
        assertTrue(output.contains("Done!"));
    }

    public void testCrackingTwoChars() {
        String targetHash = "adeb6f2a18fe33af368d91b09587b68e3abcb9a7";
        String[] args = {targetHash, "2", "2"};
        Cracker.main(args);
        String output = outContent.toString();
        assertTrue(output.contains("fm"));
        assertTrue(output.contains("Done!"));
    }

    public void testUtilityFunctions() throws Exception {
        String password = "abc";

        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(password.getBytes());
        byte[] digestBytes = md.digest();

        String hexString = Cracker.hexToString(digestBytes);

        byte[] convertedBytes = Cracker.hexToArray(hexString);

        assertEquals(digestBytes.length, convertedBytes.length);
        for (int i = 0; i < digestBytes.length; i++) {
            assertEquals(digestBytes[i], convertedBytes[i]);
        }
    }

    public void testInvalidArguments() {
        String[] args = {};
        Cracker.main(args);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Args: target length [workers]"));
    }

    public void testWorkerDistribution() {
        int numWorkers = 4;
        int charsLength = Cracker.CHARS.length;
        int charsPerWorker = charsLength / numWorkers;
        int remaining = charsLength % numWorkers;

        int[] startIndices = new int[numWorkers];
        int[] endIndices = new int[numWorkers];

        int startIndex = 0;
        for (int i = 0; i < numWorkers; i++) {
            startIndices[i] = startIndex;
            int workerChars = charsPerWorker + (i < remaining ? 1 : 0);
            endIndices[i] = startIndex + workerChars;
            startIndex = endIndices[i];
        }

        assertEquals(0, startIndices[0]);
        assertEquals(charsLength, endIndices[numWorkers - 1]);

        for (int i = 1; i < numWorkers; i++) {
            assertEquals(endIndices[i - 1], startIndices[i]);
        }
    }
}
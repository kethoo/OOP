import java.security.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();

	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;
			if (val<16) buff.append('0');
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}

	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}

	private static class Worker extends Thread {
		private final int startIndex;
		private final int endIndex;
		private final int maxLen;
		private final byte[] targetHash;
		private final CountDownLatch latch;

		public Worker(int startIndex, int endIndex, String targetHashStr, int maxLen, CountDownLatch latch) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.maxLen = maxLen;
			this.targetHash = hexToArray(targetHashStr);
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA");
				crackRecursive(md, "", 0);
			} catch (NoSuchAlgorithmException ignored) {
			} finally {
				latch.countDown();
			}
		}

		private void crackRecursive(MessageDigest md, String str, int length) {
			if (length == maxLen) {
				return;
			}

			for (int i = (length == 0 ? startIndex : 0); i < (length == 0 ? endIndex : CHARS.length); i++) {
				String newStr = str + CHARS[i];
				byte[] bytes = newStr.getBytes();
				md.reset();
				md.update(bytes);
				byte[] digest = md.digest();

				if (Arrays.equals(digest, targetHash)) {
					System.out.println(newStr);
				}

				crackRecursive(md, newStr, length + 1);
			}
		}
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Args: target length [workers]");
			return;
		}

		String target = args[0];
		int len = Integer.parseInt(args[1]);
		int workers = 1;
		if (args.length > 2) {
			workers = Integer.parseInt(args[2]);
		}

		if (args.length == 2 && (target.length() != 40 || !target.matches("[0-9a-f]+"))) {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(target.getBytes());
				String hash = hexToString(md.digest());
				System.out.println(hash);
			} catch (NoSuchAlgorithmException ignored) {
			}
		} else {
			workers = Math.min(workers, CHARS.length);
			int charsPerWorker = CHARS.length / workers;
			int remChars = CHARS.length % workers;

			CountDownLatch latch = new CountDownLatch(workers);

			int startIndex = 0;
			for (int i = 0; i < workers; i++) {
				int workerChars = charsPerWorker + (i < remChars ? 1 : 0);
				int endIndex = startIndex + workerChars;

				Worker worker = new Worker(startIndex, endIndex, target, len, latch);
				worker.start();

				startIndex = endIndex;
			}

			try {
				latch.await();
				System.out.println("Done!");
			} catch (InterruptedException ignored) {
			}
		}
	}
}
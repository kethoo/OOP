import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Bank {
	public static final int ACCOUNTS = 20;
	private final Account[] accounts;
	private BlockingQueue<Transaction> queue;
	private final Transaction nullTrans = new Transaction(-1, 0, 0);
	private CountDownLatch latch;

	public Bank() {
		accounts = new Account[ACCOUNTS];
		for (int i = 0; i < ACCOUNTS; i++) {
			accounts[i] = new Account(this, i, 1000);
		}
	}

	public void readFile(String file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			StreamTokenizer tokenizer = new StreamTokenizer(reader);

			while (true) {
				int read = tokenizer.nextToken();
				if (read == StreamTokenizer.TT_EOF) break;
				int from = (int)tokenizer.nval;

				tokenizer.nextToken();
				int to = (int)tokenizer.nval;

				tokenizer.nextToken();
				int amount = (int)tokenizer.nval;

				Transaction transaction = new Transaction(from, to, amount);
				try {
					queue.put(transaction);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}

			for (int i = 0; i < latch.getCount(); i++) {
				try {
					queue.put(nullTrans);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
		catch (Exception e) {
			System.exit(1);
		}
	}

	public void processFile(String file, int numWorkers) {
		queue = new ArrayBlockingQueue<>(100);
		latch = new CountDownLatch(numWorkers);

		for (int i = 0; i < numWorkers; i++) {
			Worker worker = new Worker();
			worker.start();
		}

		readFile(file);

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < ACCOUNTS; i++) {
			System.out.println("acct:" + i + " bal:" + accounts[i].getBalance() + " trans:" + accounts[i].getTransactions());
		}
	}

	private class Worker extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					Transaction trans = queue.take();

					if (trans == nullTrans) {
						break;
					}

					processTransaction(trans);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				latch.countDown();
			}
		}

		private void processTransaction(Transaction trans) {
			int fromAccount = trans.from;
			int toAccount = trans.to;
			int amount = trans.amount;

			accounts[fromAccount].withdraw(amount);
			accounts[toAccount].deposit(amount);
		}
	}

//	public static void main(String[] args) {
//		if (args.length == 0) {
//			System.out.println("Args: transaction-file [num-workers [limit]]");
//			System.exit(1);
//		}
//
//		String file = args[0];
//		int numWorkers = 1;
//		if (args.length >= 2) {
//			numWorkers = Integer.parseInt(args[1]);
//		}
//
//		Bank bank = new Bank();
//		bank.processFile(file, numWorkers);
//	}
}

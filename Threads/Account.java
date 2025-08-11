class Account {
	private int id;
	private int balance;
	private int transactions;

	private Bank bank;

    public Account(Bank bank, int id, int balance) {
		this.bank = bank;
        this.id = id;
		this.balance = balance;
		transactions = 0;
	}

	public synchronized void withdraw(int amount) {
		balance -= amount;
		transactions++;
	}

	public synchronized void deposit(int amount) {
		balance += amount;
		transactions++;
	}

	public synchronized int getBalance() {
		return balance;
	}

	public synchronized int getTransactions() {
		return transactions;
	}

	@Override
	public String toString() {
		return "acct:" + id + " bal:" + balance + " trans:" + transactions;
	}
}

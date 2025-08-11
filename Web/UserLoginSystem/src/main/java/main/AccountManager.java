package main;

import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    private final Map<String, String> accounts;

    public AccountManager() {
        accounts = new HashMap<>();
        accounts.put("Patrick", "1234");
        accounts.put("Molly", "FloPup");
    }

    public boolean accountExists(String username) {
        return accounts.containsKey(username);
    }

    public boolean isValidPassword(String username, String password) {
        if (!accountExists(username)) {
            return false;
        }
        return accounts.get(username).equals(password);
    }

    public boolean createAccount(String username, String password) {
        if (accountExists(username)) {
            return false;
        }
        accounts.put(username, password);
        return true;
    }

    public int getAccountCount() {
        return accounts.size();
    }
}
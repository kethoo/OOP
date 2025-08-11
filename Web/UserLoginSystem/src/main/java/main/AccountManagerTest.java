package main;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AccountManagerTest {
    private AccountManager accountManager;

    @Before
    public void setUp() {
        accountManager = new AccountManager();
    }

    @Test
    public void testInitialAccounts() {
        assertTrue(accountManager.accountExists("Patrick"));
        assertTrue(accountManager.accountExists("Molly"));
        assertEquals(2, accountManager.getAccountCount());
    }

    @Test
    public void testValidPasswords() {
        assertTrue(accountManager.isValidPassword("Patrick", "1234"));
        assertTrue(accountManager.isValidPassword("Molly", "FloPup"));
    }

    @Test
    public void testInvalidPasswords() {
        assertFalse(accountManager.isValidPassword("Patrick", "wrong"));
        assertFalse(accountManager.isValidPassword("Molly", "wrong"));
        assertFalse(accountManager.isValidPassword("NonExistent", "password"));
    }

    @Test
    public void testCreateNewAccount() {
        assertTrue(accountManager.createAccount("TestUser", "testpass"));
        assertTrue(accountManager.accountExists("TestUser"));
        assertTrue(accountManager.isValidPassword("TestUser", "testpass"));
        assertEquals(3, accountManager.getAccountCount());
    }

    @Test
    public void testDuplicateAccount() {
        assertFalse("Should not be able to create duplicate Patrick account",
                accountManager.createAccount("Patrick", "newpassword"));
        assertTrue("Original Patrick password should still work",
                accountManager.isValidPassword("Patrick", "1234"));
    }
}
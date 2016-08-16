package main.utilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/**
 * This class uses the AccountManager to get the primary email address of the
 * current user.
 */
public class UserEmailFetcher {

    public static String[] getEmail(Context context) {
        try {
            AccountManager accountManager = AccountManager.get(context);
            Account account = getAccount(accountManager);

            if (account == null) {
                return null;
            } else {

                return new String[]{account.type, account.name, account.toString()};
            }
        } catch (Exception error) {
            error.printStackTrace();
            LogHelper.e(UserEmailFetcher.class, error.getMessage());
            return null;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }
}

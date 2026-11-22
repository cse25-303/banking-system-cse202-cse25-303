package bank;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** The parent of everyone who logs in. It is abstract: a person is always a Customer or an Administrator. */
public abstract class User {

    private int userId;
    private String username;
    private String passwordHash;          // we never keep the real password, only its hash
    private boolean loggedIn;

    /**
     * Pass in the password HASH, not the real password.
     * Create the hash with User.hashPassword("real password") when registering.
     * When loading from the database later, pass the stored hash straight in.
     */
    protected User(int userId, String username, String passwordHash) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /** Turns a password into scrambled text (SHA-256). The same password always gives the same text. */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("The password cannot be empty");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean login(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (hashPassword(password).equals(passwordHash)) {
            loggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedIn = false;
    }

    public boolean changePassword(String oldPwd, String newPwd) {
        if (oldPwd == null || oldPwd.isEmpty() || newPwd == null || newPwd.isEmpty()) {
            return false;
        }
        if (!hashPassword(oldPwd).equals(passwordHash)) {
            return false;
        }
        passwordHash = hashPassword(newPwd);
        return true;
    }

    /** Each type of user decides how its name is shown. */
    public abstract String getDisplayName();

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }     // the database code will use this to give the new id
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isLoggedIn() { return loggedIn; }
}

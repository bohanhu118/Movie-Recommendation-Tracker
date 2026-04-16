import java.util.*;

/**
 * Represents a user in the movie recommendation system.
 * This is the base class for all user types in the system.
 */

public class User {
    /**
     * Protected fields let subclasses (PremiumUser and BasicUser) access
     * these directly, but keeps them hidden from outside classes.
     * This way subclasses can customize behavior while keeping data safe.
     */
    protected String username;
    protected String password;
    protected Watchlist watchlist;
    protected History history;

    public User(String username, String password, Watchlist watchlist, History history) {
        this.username = username;
        this.password = password;
        this.watchlist = watchlist;
        this.history = history;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public History getHistory() {
        return history;
    }

    /**
     * Checks if this user can add movies to their watchlist.
     * Subclasses can override it for different rules.
     * @return true if user can add to watchlist, false otherwise
     */
    public boolean canAddToWatchlist() {
        return true;
    }

    /**
     * Checks if this user can use advanced recommendation features.
     * Default is false for basic users. PremiumUser subclass should override this to return true.
     * @return true if advanced recommendations are available, false otherwise
     */
    public boolean canUseAdvancedRecommendations() {
        return false;
    }

    /**
     * Gets the type of account this user has.
     * Lets us check user type without using "instanceof".
     * Base class returns empty string. Subclasses should override this to return their type.
     * @return the account type string (default return to "Basic")
     */
    public String getAccountType() {
        return "Basic";
    }

    /**
     * Converts the user data to CSV format for saving to a file.
     * Format: username,password,accountType,watchlist,history
     * Calls toCSVString() on CSVHandler.
     * @return CSV string with all user data
     */
    public String toCSVString() {
        return username + "," + password + "," + getAccountType() + "," +
                watchlist.toCSVString() + "," + history.toCSVString();
    }

    @Override
    public String toString() {
        return String.format("User{username='%s', accountType='%s'}",
                username, getAccountType());
    }
}
/**
 * Represents a basic user with standard privileges.
 * Basic users have limited access to recommendation features.
 */

public class BasicUser extends User {

    public BasicUser(String username, String password, Watchlist watchlist, History history) {
        super(username, password, watchlist, history);
    }

    /**
     * Basic users don't have access to premium recommendation features.
     * @return false since basic users cannot use advanced recommendations
     */
    @Override
    public boolean canUseAdvancedRecommendations() {
        return false;
    }

    @Override
    public String getAccountType() {
        return "Basic";
    }

    @Override
    public String toString() {
        return String.format("BasicUser{username='%s', watchlistSize=%d, historySize=%d}",
                username, watchlist.getMovies().size(), history.getMovies().size());
    }
}

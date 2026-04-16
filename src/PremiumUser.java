/**
 * Represents a premium user with enhanced privileges.
 * Premium users have access to advanced recommendation strategies.
 */

public class PremiumUser extends User {

    public PremiumUser(String username, String password, Watchlist watchlist, History history) {
        super(username, password, watchlist, history);
    }

    /**
     * Premium users get access to all advanced recommendation features.
     * @return true since premium users can use advanced recommendations
     */
    @Override
    public boolean canUseAdvancedRecommendations() {
        return true;
    }

    @Override
    public String getAccountType() {
        return "Premium";
    }

    @Override
    public String toString() {
        return String.format("PremiumUser{username='%s', watchlistSize=%d, historySize=%d}",
                username, watchlist.getMovies().size(), history.getMovies().size());
    }
}

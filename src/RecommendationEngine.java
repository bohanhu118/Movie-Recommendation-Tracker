import java.util.*;

/**
 * Provides movie recommendation functionality using multiple strategies.
 * Supports genre-based, rating-based, year-based, and hybrid recommendations.
 */
public class RecommendationEngine {

    /**
     * Recommends movies to a user based on the chosen strategy.
     * Returns up to N movies that the user hasn't watched yet.
     *
     * @param user the user to recommend movies for
     * @param allMovies the complete list of available movies
     * @param N the maximum number of movies to recommend
     * @param strategy the recommendation strategy ("By Genre", "By Rating", "By Year", or "Hybrid")
     * @return a list of recommended movies (empty list if N is invalid or no movies available)
     */
    public List<Movie> recommend(User user, List<Movie> allMovies, int N, String strategy) {
        if (N < 0) {
            throw new IllegalArgumentException("Number of recommendations must be non-negative");
        }

        if (allMovies == null || user == null) {
            throw new IllegalArgumentException("User and movie list cannot be null");
        }

        /**
         * Used an enhanced switch-case statement.
         * (introduced in the lecture on week 12)
         */
        return switch (strategy) {
            case "By Rating" -> recommendByRating(user, allMovies, N);
            case "By Year" -> recommendByYear(user, allMovies, N);
            case "Hybrid" -> recommendHybrid(user, allMovies, N);
            case "By Genre" -> recommendByGenre(user, allMovies, N);
            default -> recommendByGenre(user, allMovies, N);
        };
    }

    private List<Movie> recommendByGenre(User user, List<Movie> allMovies, int N) {
        List<Movie> userMovies = getUserMovies(user);
        Map<String, Integer> genreCount = calculateGenreFrequency(userMovies);

        String favoriteGenre = findFavoriteGenre(genreCount);
        List<Movie> recommended = filterMoviesByGenre(allMovies, favoriteGenre, userMovies);

        // If no movies found for favorite genre, fall back to highest rated movies
        if (recommended.isEmpty()) {
            recommended = getHighestRatedMovies(allMovies, userMovies);
        }

        sortByRatingDescending(recommended);
        return getTopN(recommended, N);
    }

    private List<Movie> recommendByRating(User user, List<Movie> allMovies, int N) {
        List<Movie> userMovies = getUserMovies(user);
        List<Movie> recommended = excludeWatchedMovies(allMovies, userMovies);

        sortByRatingDescending(recommended);
        return getTopN(recommended, N);
    }

    private List<Movie> recommendByYear(User user, List<Movie> allMovies, int N) {
        List<Movie> userMovies = getUserMovies(user);
        List<Movie> recommended = excludeWatchedMovies(allMovies, userMovies);

        sortByYearDescending(recommended);
        return getTopN(recommended, N);
    }

    private List<Movie> recommendHybrid(User user, List<Movie> allMovies, int N) {
        List<Movie> userMovies = getUserMovies(user);
        List<Movie> recommended = excludeWatchedMovies(allMovies, userMovies);

        // Hybrid score: 70% rating + 30% recency (normalized year)
        sortByHybridScore(recommended);
        return getTopN(recommended, N);
    }

    private void sortByHybridScore(List<Movie> movies) {
        for (int i = 0; i < movies.size() - 1; i++) {
            for (int j = 0; j < movies.size() - i - 1; j++) {
                double score1 = calculateHybridScore(movies.get(j));
                double score2 = calculateHybridScore(movies.get(j + 1));
                if (score1 < score2) {
                    // Swap movies
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    private double calculateHybridScore(Movie movie) {
        double ratingWeight = 0.7;
        double yearWeight = 0.3;

        // Normalize year (assuming movies from 2000-2025)
        double normalizedYear = (movie.getYear() - 2000) / 25.0;

        return movie.getRating() * ratingWeight + normalizedYear * yearWeight;
    }

    /**
     * Gets all movies from both the user's history and watchlist.
     *
     * @param user the user whose movies to retrieve
     * @return combined list of movies from history and watchlist
     */
    private List<Movie> getUserMovies(User user) {
        List<Movie> userMovies = new ArrayList<>();
        userMovies.addAll(user.getHistory().getMovies());
        userMovies.addAll(user.getWatchlist().getMovies());
        return userMovies;
    }

    /**
     * Counts how many times each genre appears in the user's movies.
     *
     * @param userMovies the user's watched and watchlisted movies
     * @return a map of genres to their frequency counts
     */
    private Map<String, Integer> calculateGenreFrequency(List<Movie> userMovies) {
        Map<String, Integer> genreCount = new HashMap<>();
        for (int i = 0; i < userMovies.size(); i++) {
            Movie movie = userMovies.get(i);
            String genre = movie.getGenre();
            Integer count = genreCount.get(genre);
            if (count == null) {
                genreCount.put(genre, 1);
            } else {
                genreCount.put(genre, count + 1);
            }
        }
        return genreCount;
    }

    private String findFavoriteGenre(Map<String, Integer> genreCount) {
        String favoriteGenre = "";
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : genreCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                favoriteGenre = entry.getKey();
            }
        }
        return favoriteGenre;
    }

    /**
     * Filters movies to find unwatched ones matching a specific genre.
     *
     * @param allMovies all available movies
     * @param genre the genre to filter by
     * @param userMovies movies the user has already watched or added
     * @return list of unwatched movies in the specified genre
     */
    private List<Movie> filterMoviesByGenre(List<Movie> allMovies, String genre, List<Movie> userMovies) {
        List<Movie> filtered = new ArrayList<>();
        for (int i = 0; i < allMovies.size(); i++) {
            Movie movie = allMovies.get(i);
            if (movie.getGenre().equals(genre) && !userMovies.contains(movie)) {
                filtered.add(movie);
            }
        }
        return filtered;
    }

    private List<Movie> getHighestRatedMovies(List<Movie> allMovies, List<Movie> userMovies) {
        List<Movie> available = excludeWatchedMovies(allMovies, userMovies);
        sortByRatingDescending(available);
        return available;
    }

    /**
     * Removes movies the user has already watched or added to watchlist.
     *
     * @param allMovies all available movies
     * @param userMovies movies to exclude
     * @return list of movies the user hasn't interacted with yet
     */
    private List<Movie> excludeWatchedMovies(List<Movie> allMovies, List<Movie> userMovies) {
        List<Movie> available = new ArrayList<>();
        for (int i = 0; i < allMovies.size(); i++) {
            Movie movie = allMovies.get(i);
            if (!userMovies.contains(movie)) {
                available.add(movie);
            }
        }
        return available;
    }

    private void sortByRatingDescending(List<Movie> movies) {
        for (int i = 0; i < movies.size() - 1; i++) {
            for (int j = 0; j < movies.size() - i - 1; j++) {
                if (movies.get(j).getRating() < movies.get(j + 1).getRating()) {
                    // Swap movies
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    private void sortByYearDescending(List<Movie> movies) {
        for (int i = 0; i < movies.size() - 1; i++) {
            for (int j = 0; j < movies.size() - i - 1; j++) {
                if (movies.get(j).getYear() < movies.get(j + 1).getYear()) {
                    // Swap movies
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    private List<Movie> getTopN(List<Movie> movies, int N) {
        int limit = Math.min(N, movies.size());
        List<Movie> result = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            result.add(movies.get(i));
        }
        return result;
    }

    @Override
    public String toString() {
        return "RecommendationEngine{strategies=[By Genre, By Rating, By Year, Hybrid]}";
    }
}
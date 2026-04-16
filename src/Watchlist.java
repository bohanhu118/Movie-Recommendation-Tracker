import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's movie watchlist.
 * Provides functionality to manage movies the user wants to watch.
 */
public class Watchlist {
    private List<Movie> movies;

    public Watchlist() {
        this.movies = new ArrayList<>();
    }

    /**
     * Adds a movie to the watchlist if it's not already there.
     *
     * @param movie the movie to add
     * @return true if the movie was added, false if it was already in the list
     */
    public boolean addMovie(Movie movie) {
        if (!movies.contains(movie)) {
            movies.add(movie);
            return true;
        }
        return false;
    }

    /**
     * Removes a movie from the watchlist.
     *
     * @param movie the movie to remove
     * @return true if the movie was removed, false if it wasn't in the list
     */
    public boolean removeMovie(Movie movie) {
        return movies.remove(movie);
    }

    public List<Movie> getMovies() {
        return new ArrayList<>(movies);
    }

    public boolean contains(Movie movie) {
        return movies.contains(movie);
    }

    public int size() {
        return movies.size();
    }

    public boolean isEmpty() {
        return movies.isEmpty();
    }

    /**
     * Converts the watchlist to a CSV string format.
     * Used for saving watchlist data to files.
     * @return a CSV string with movie IDs, or empty string if watchlist is empty
     */
    public String toCSVString() {
        if (movies.isEmpty()) {
            return "";
        }

        String csv = "";
        for (int i = 0; i < movies.size(); i++) {
            // [IMPORTANT] Split the ID string by semicolons to obtain all movie IDs
            csv += movies.get(i).getID() + ";";
        }

        // Remove trailing comma
        if (csv.length() > 0) {
            csv = csv.substring(0, csv.length() - 1);
        }
        return csv;
    }

    @Override
    public String toString() {
        return "Watchlist{size=" + movies.size() + ", movies=" + movies.toString() + "}";
    }
}
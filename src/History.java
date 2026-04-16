import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Represents a user's movie viewing history.
 * Tracks movies that the user has watched.
 */
public class History {
    private List<WatchRecord> watchRecords;

    public History() {
        this.watchRecords = new ArrayList<>();
    }

    /**
     * Adds a movie to the history with today's date.
     * Won't add if the movie is already in the history.
     *
     * @param movie the movie to add
     * @return true if the movie was added, false if it was already in the history
     */
    public boolean addMovie(Movie movie) {
        WatchRecord record = new WatchRecord(movie, LocalDate.now());
        for (WatchRecord existing : watchRecords) {
            if (existing.getMovie().getID().equals(movie.getID())) {
                return false;
            }
        }
        watchRecords.add(record);
        return true;
    }

    public List<Movie> getMovies() {
        List<Movie> movies = new ArrayList<>();
        for (WatchRecord record : watchRecords) {
            movies.add(record.getMovie());
        }
        return new ArrayList<>(movies);
    }

    public boolean contains(Movie movie) {
        for (WatchRecord record : watchRecords) {
            if (record.getMovie().getID().equals(movie.getID())) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return watchRecords.size();
    }

    public boolean isEmpty() {
        return watchRecords.isEmpty();
    }

    /**
     * Adds a watch record to the history.
     * Won't add if the same movie and date combination already exists.
     *
     * @param record the watch record to add
     * @return true if the record was added, false if it was a duplicate
     */
    public boolean addWatchRecord(WatchRecord record) {
        for (WatchRecord existing : watchRecords) {
            if (existing.getMovie().getID().equals(record.getMovie().getID())
                    && existing.getWatchDate().equals(record.getWatchDate())) {
                return false;
            }
        }
        return watchRecords.add(record);
    }

    public List<WatchRecord> getWatchRecords() {
        return new ArrayList<>(watchRecords);
    }

    /**
     * Converts the history to a CSV string format.
     * Used for saving history data to files.
     *
     * @return a CSV string with watch records, or empty string if history is empty
     */
    public String toCSVString() {
        if (watchRecords.isEmpty()) {
            return "";
        }

        String csv = "";
        for (int i = 0; i < watchRecords.size(); i++) {
            WatchRecord record = watchRecords.get(i);
            // [IMPORTANT] Split by "@" and ";"
            String item = record.getMovie().getID() + "@" + record.getWatchDate();
            if (i > 0) {
                csv += ";";
            }
            csv += item;
        }
        return csv;
    }

    @Override
    public String toString() {
        return "History{size=" + watchRecords.size() + ", watchRecords=" + watchRecords.toString() + "}";
    }
}

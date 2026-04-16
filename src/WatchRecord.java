import java.time.LocalDate;

/**
 * Represents a record of when a user watched a specific movie.
 * Links a movie with the date it was watched.
 */
public class WatchRecord {
    private Movie movie;
    private LocalDate watchDate;

    public WatchRecord(Movie movie, LocalDate watchDate) {
        this.movie = movie;
        this.watchDate = watchDate;
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDate getWatchDate() {
        return watchDate;
    }

    @Override
    public String toString() {
        return String.format("%s_%s (%d/%.1f) | Watched on: %s",
                movie.getID(),
                movie.getTitle(),
                movie.getYear(),
                movie.getRating(),
                watchDate
        );
    }
}
/**
 * Represents a movie with basic information and metadata.
 * Stores core details like title, genre, year, and rating.
 */
public class Movie {
    private final String id;
    private final String title;
    private final String genre;
    private final int year;
    private final double rating;

    public Movie(String id, String title, String genre, int year, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
    }

    public String getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Title: %s | Genre: %s | Year: %d | Rating: %.1f",
                id, title, genre, year, rating);
    }
}
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

/**
 * Handles reading and writing of CSV files for movie and user data.
 * Provides methods to load movies and users from files and save user data back.
 */
public class CSVHandler {
    public static List<Movie> readMovies(String filePath) throws IOException {
        List<Movie> movies = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("Movie file not found: " + filePath);
        }

        try (Scanner scanner = new Scanner(file)) {
            // Skip header line
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            int lineNumber = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                try {
                    Movie movie = parseMovieLine(line, lineNumber);
                    if (movie != null) {
                        movies.add(movie);
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Skipping invalid movie data at line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Successfully loaded " + movies.size() + " movies from " + filePath);
        return movies;
    }

    /**
     * Parses a single line of movie data from the CSV.
     * Expected format: id,title,genre,year,rating
     * Validates that year is between 1900-2030 and rating is between 0-10.
     *
     * @param line the CSV line to parse
     * @param lineNumber the line number (for error reporting)
     * @return a Movie object created from the parsed data
     * @throws IllegalArgumentException if the data is invalid or malformed
     */
    private static Movie parseMovieLine(String line, int lineNumber) {
        String[] parts = line.split(",");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid number of fields. Expected 5, found " + parts.length);
        }

        try {
            String id = parts[0].trim();
            String title = parts[1].trim();
            String genre = parts[2].trim();
            int year = Integer.parseInt(parts[3].trim());
            double rating = Double.parseDouble(parts[4].trim());

            // Validate data ranges
            if (year < 1900 || year > 2030) {
                throw new IllegalArgumentException("Invalid year: " + year);
            }
            if (rating < 0.0 || rating > 10.0) {
                throw new IllegalArgumentException("Invalid rating: " + rating);
            }
            if (id.isEmpty() || title.isEmpty() || genre.isEmpty()) {
                throw new IllegalArgumentException("Required fields cannot be empty");
            }

            return new Movie(id, title, genre, year, rating);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in movie data");
        }
    }


    public static List<User> readUsers(String filePath, List<Movie> allMovies) throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("User file not found: " + filePath);
        }

        try (Scanner scanner = new Scanner(file)) {
            // Skip header line
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            int lineNumber = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                try {
                    User user = parseUserLine(line, allMovies, lineNumber);
                    if (user != null) {
                        users.add(user);
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Skipping invalid user data at line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Successfully loaded " + users.size() + " users from " + filePath);
        return users;
    }

    /**
     * Parses a single line of user data from the CSV.
     * Expected format: username,password,accountType,watchlist,history
     * Creates either a BasicUser or PremiumUser based on account type.
     *
     * @param line the CSV line to parse
     * @param allMovies the movie database for looking up movie IDs
     * @param lineNumber the line number (for error reporting)
     * @return a User object (BasicUser or PremiumUser) created from the parsed data
     * @throws IllegalArgumentException if the data is invalid or malformed
     */
    private static User parseUserLine(String line, List<Movie> allMovies, int lineNumber) {
        // Use limit to handle commas in username/password
        String[] parts = line.split(",", 5);

        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid number of fields. Expected 5, found " + parts.length);
        }

        try {
            String username = parts[0].trim();
            String password = parts[1].trim();
            String accountType = parts[2].trim();
            String watchlistIds = parts[3].trim();
            String historyIds = parts[4].trim();

            if (username.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("Username and password cannot be empty");
            }

            Watchlist watchlist = buildWatchlistFromIds(watchlistIds, allMovies);
            History history = buildHistoryFromIds(historyIds, allMovies);

            // Determine user type
            if ("Premium".equals(accountType)) {
                return new PremiumUser(username, password, watchlist, history);
            } else {
                return new BasicUser(username, password, watchlist, history);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing user data: " + e.getMessage());
        }
    }

    /**
     * Builds a Watchlist from semicolon-separated movie IDs.
     * Looks up each movie ID in the movie database and adds it to the watchlist.
     *
     * @param watchlistIds semicolon-separated string of movie IDs
     * @param allMovies the movie database to search
     * @return a Watchlist containing the found movies
     */
    private static Watchlist buildWatchlistFromIds(String watchlistIds, List<Movie> allMovies) {
        Watchlist watchlist = new Watchlist();

        if (watchlistIds.isEmpty()) {
            return watchlist;
        }

        String[] ids = watchlistIds.split(";");
        for (int i = 0; i < ids.length; i++) {
            String trimmedId = ids[i].trim();
            if (!trimmedId.isEmpty()) {
                Movie movie = findMovieById(trimmedId, allMovies);
                if (movie != null) {
                    watchlist.addMovie(movie);
                } else {
                    System.out.println("Warning: Movie ID '" + trimmedId + "' not found in movie database");
                }
            }
        }

        return watchlist;
    }

    /**
     * Builds a History from semicolon-separated watch records.
     * Each record has format "movieID@date". Parses both and creates WatchRecord objects.
     *
     * @param historyIds semicolon-separated string of "movieID@date" records
     * @param allMovies the movie database to search
     * @return a History containing the watch records
     */
    private static History buildHistoryFromIds(String historyIds, List<Movie> allMovies) {
        History history = new History();

        if (historyIds.isEmpty()) {
            return history;
        }

        String[] historyItems = historyIds.split(";");
        for (int i = 0; i < historyItems.length; i++) {
            String trimmedItem = historyItems[i].trim();
            if (trimmedItem.isEmpty()) {
                continue;
            }

            String[] idAndDate = trimmedItem.split("@");
            if (idAndDate.length != 2) {
                System.out.println("Warning: Invalid history format '" + trimmedItem + "' - expected 'ID@Date'");
                continue;
            }

            String movieId = idAndDate[0].trim();
            String dateStr = idAndDate[1].trim();

            Movie movie = findMovieById(movieId, allMovies);
            if (movie == null) {
                System.out.println("Warning: Movie ID '" + movieId + "' not found in movie database");
                continue;
            }

            LocalDate watchDate = null;
            try {
                watchDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.out.println("Warning: Invalid date format '" + dateStr + "' for movie ID '" + movieId + "' - expected 'yyyy-MM-dd'");
                continue;
            }

            history.addWatchRecord(new WatchRecord(movie, watchDate));
        }
        return history;
    }


    private static Movie findMovieById(String id, List<Movie> allMovies) {
        for (int i = 0; i < allMovies.size(); i++) {
            Movie movie = allMovies.get(i);
            if (movie.getID().equals(id)) {
                return movie;
            }
        }
        return null;
    }


    public static void writeUsers(String filePath, List<User> users) throws IOException {
        File file = new File(filePath);

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("Username,Password,AccountType,Watchlist,History");

            for (int i = 0; i < users.size(); i++) {
                writer.println(users.get(i).toCSVString());
            }
        }

        System.out.println("Successfully saved " + users.size() + " users to " + filePath);
    }

    @Override
    public String toString() {
        return "CSVHandler{handles=.csv files for movies and users}";
    }
}
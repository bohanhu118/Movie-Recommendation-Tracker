import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Command-line version of the Movie Recommendation and Tracking System.
 * Provides a text-based interface for users.
 */
public class MovieSystemCLI {
    private static final String MOVIE_CSV_PATH = "data/movies.csv";
    private static final String USER_CSV_PATH = "data/users.csv";
    private List<Movie> allMovies;
    private List<User> allUsers;
    private User currentUser;
    private RecommendationEngine recommendationEngine;
    private Scanner scanner;

    public void start() {
        scanner = new Scanner(System.in);
        recommendationEngine = new RecommendationEngine();

        if (!initializeData()) {
            return; // Exit if data loading fails
        }

        while (true) {
            // Show login scene
            if (!handleLogin()) {
                System.out.println("Exiting application...");
                break;
            }

            // Show main menu scene
            showMainMenu();
        }

        scanner.close();
    }


    private boolean initializeData() {
        try {
            allMovies = CSVHandler.readMovies(MOVIE_CSV_PATH);
            allUsers = CSVHandler.readUsers(USER_CSV_PATH, allMovies);
            System.out.println("Data initialization successful: " +
                    allMovies.size() + " movies, " + allUsers.size() + " users");
            System.out.println("==================================================");
            return true;
        } catch (IOException e) {
            System.out.println("\n[ERROR] Initialization Failed");
            System.out.println("Data loading error: " + e.getMessage());
            return false;
        }
    }

    // Handles the login process.
    private boolean handleLogin() {
        while (true) {
            System.out.println("==================================================");
            System.out.println("Movie Recommendation System - Login");
            System.out.println("==================================================");
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("\n[ERROR] Login Failed");
                System.out.println("Please enter both username and password");
                continue;
            }

            currentUser = null;
            for (User user : allUsers) {
                if (user.getUsername().equals(username) && SaltHash.check(user.getPassword(), password)) {
                    currentUser = user;
                    if (user.getPassword().indexOf(':') < 0) {
                        user.setPassword(SaltHash.make(password));
                        saveUsers();
                    }
                    break;
                }
            }

            if (currentUser != null) {
                System.out.println("\n[INFO] Login Successful");
                System.out.println("Welcome, " + username + "!\nAccount Type: " +
                        currentUser.getAccountType());
                return true;
            } else {
                System.out.println("\n[ERROR] Login Failed");
                System.out.println("Invalid username or password");
            }

            System.out.println("\n1. Try again");
            System.out.println("2. Create New Account");
            System.out.println("3. Exit");
            System.out.print("Please choose an option: ");
            String choice = scanner.nextLine().trim();

            if ("2".equals(choice)) {
                handleRegistration();
            } else if ("3".equals(choice)) {
                return false; // Exit login loop
            }
            // Else, loop back to login
        }
    }

    // Handles the user registration process.
    private void handleRegistration() {
        System.out.println("==================================================");
        System.out.println("Create New Account");
        System.out.println("==================================================");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine().trim();

        System.out.println("\nAccount Type:");
        System.out.println("1. Basic User");
        System.out.println("2. Premium User");
        System.out.print("Please choose an option: ");
        String typeChoice = scanner.nextLine().trim();
        String accountType = "Basic User";
        if ("2".equals(typeChoice)) {
            accountType = "Premium User";
        }

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("\n[ERROR] Registration Failed");
            System.out.println("Please fill in all fields");
            return;
        }
        if (!password.equals(confirmPassword)) {
            System.out.println("\n[ERROR] Registration Failed");
            System.out.println("Passwords do not match");
            return;
        }
        if (usernameExists(username)) {
            System.out.println("\n[ERROR] Registration Failed");
            System.out.println("Username already exists");
            return;
        }

        User newUser = createUserAccount(username, password, accountType);
        allUsers.add(newUser);
        saveUsers();

        System.out.println("\n[INFO] Registration Successful");
        System.out.println("Account created successfully!\nUsername: " + username +
                "\nAccount Type: " + accountType);
    }

    // Displays the main menu and processes user input.
    private void showMainMenu() {
        displayWelcomeMessage();

        while (true) {
            System.out.println("==================================================");
            System.out.println("Main Menu");
            System.out.println("==================================================");
            System.out.println("Please select an option by entering the corresponding number:");
            System.out.println("1. Browse Movies");
            System.out.println("2. Add to Watchlist");
            System.out.println("3. Remove from Watchlist");
            System.out.println("4. View Watchlist");
            System.out.println("5. Mark as Watched");
            System.out.println("6. View History");
            System.out.println("7. Get Recommendations");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");

            System.out.print("Your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> displayAllMovies();
                case "2" -> addToWatchlist();
                case "3" -> removeFromWatchlist();
                case "4" -> viewWatchlist();
                case "5" -> markAsWatched();
                case "6" -> viewHistory();
                case "7" -> getRecommendations();
                case "8" -> changePassword();
                case "9" -> {
                    logout();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    // Displays welcome message with account privileges.
    private void displayWelcomeMessage() {
        System.out.println("==================================================");
        System.out.println("Welcome!");
        System.out.println("==================================================");
        String message = "Login successful! You can now use all features.\n\n";
        message += "Account Privileges:\n";
        message += "- Browse all movies\n";
        message += "- Manage personal watchlist\n";
        message += "- Track viewing history\n";
        message += "- Get personalized recommendations\n";

        if (currentUser instanceof PremiumUser) {
            message += "- PREMIUM: Access to all recommendation strategies\n";
            message += "- PREMIUM: Priority movie suggestions\n";
        } else {
            message += "- BASIC: Standard genre-based recommendations only\n";
        }
        System.out.println(message);
    }

    // Displays all movies in the system.
    private void displayAllMovies() {
        System.out.println("==================================================");
        System.out.println("===== ALL MOVIES ===== ");
        System.out.println("==================================================");
        for (int i = 0; i < allMovies.size(); i++) {
            System.out.printf("%3d. %s%n", i + 1, allMovies.get(i));
        }
    }

    // Adds a movie to the user's watchlist.
    private void addToWatchlist() {
        System.out.println("==================================================");
        System.out.println("Add to Watchlist");
        System.out.println("==================================================");
        System.out.print("Enter movie number (1-" + allMovies.size() + "): ");
        String input = scanner.nextLine().trim();
        handleAddToWatchlist(input);
    }

    // Handles the logic for adding a movie to watchlist.
    private void handleAddToWatchlist(String input) {
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < allMovies.size()) {
                Movie movie = allMovies.get(index);
                if (currentUser.getWatchlist().addMovie(movie)) {
                    saveUsers();
                    System.out.println("Successfully added \"" + movie.getTitle() + "\" to your watchlist!");
                } else {
                    System.out.println("\"" + movie.getTitle() + "\" is already in your watchlist!");
                }
            } else {
                System.out.println("Invalid number! Please enter between 1 and " + allMovies.size());
            }
        } catch (NumberFormatException ex) {
            System.out.println("Please enter a valid number!");
        }
    }

    // Removes a movie from the user's watchlist.
    private void removeFromWatchlist() {
        List<Movie> watchlist = currentUser.getWatchlist().getMovies();
        if (watchlist.isEmpty()) {
            System.out.println("Your watchlist is empty!");
            return;
        }

        System.out.println("==================================================");
        System.out.println("Remove from Watchlist");
        System.out.println("==================================================");
        displayWatchlistForRemoval();
        System.out.print("Enter the number of movie to remove: ");
        String input = scanner.nextLine().trim();
        handleRemoveFromWatchlist(input);
    }

    // Handles the logic for removing a movie from watchlist.
    private void handleRemoveFromWatchlist(String input) {
        try {
            List<Movie> watchlist = currentUser.getWatchlist().getMovies();
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < watchlist.size()) {
                Movie movie = watchlist.get(index);
                currentUser.getWatchlist().removeMovie(movie);
                saveUsers();
                System.out.println("Successfully removed \"" + movie.getTitle() + "\" from watchlist!");
            } else {
                System.out.println("Invalid number! Please enter between 1 and " + watchlist.size());
            }
        } catch (NumberFormatException ex) {
            System.out.println("Please enter a valid number!");
        }
    }

    // Displays the user's watchlist.
    private void viewWatchlist() {
        List<Movie> watchlist = currentUser.getWatchlist().getMovies();
        System.out.println("==================================================");
        System.out.println("===== YOUR WATCHLIST ===== ");
        System.out.println("==================================================");
        if (watchlist.isEmpty()) {
            System.out.println("Your watchlist is empty!");
            return;
        }

        for (int i = 0; i < watchlist.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, watchlist.get(i));
        }
    }

    // Displays watchlist with removal instructions.
    private void displayWatchlistForRemoval() {
        List<Movie> watchlist = currentUser.getWatchlist().getMovies();
        for (int i = 0; i < watchlist.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, watchlist.get(i));
        }
    }

    // Marks a movie as watched and moves it to history.
    private void markAsWatched() {
        System.out.println("==================================================");
        System.out.println("Mark as Watched");
        System.out.println("==================================================");
        System.out.print("Enter movie number (1-" + allMovies.size() + "): ");
        String input = scanner.nextLine().trim();
        handleMarkAsWatched(input);
    }

    // Handles the logic for marking a movie as watched.
    private void handleMarkAsWatched(String input) {
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < allMovies.size()) {
                Movie movie = allMovies.get(index);
                if (currentUser.getHistory().addMovie(movie)) {
                    currentUser.getWatchlist().removeMovie(movie);
                    saveUsers();
                    System.out.println("Successfully marked \"" + movie.getTitle() + "\" as watched!");
                } else {
                    System.out.println("\"" + movie.getTitle() + "\" is already in your watch history!");
                }
            } else {
                System.out.println("Invalid number! Please enter between 1 and " + allMovies.size());
            }
        } catch (NumberFormatException ex) {
            System.out.println("Please enter a valid number!");
        }
    }

    // Displays the user's watch history.
    private void viewHistory() {
        List<WatchRecord> watchRecords = currentUser.getHistory().getWatchRecords();
        System.out.println("==================================================");
        System.out.println("===== YOUR WATCH HISTORY ===== ");
        System.out.println("==================================================");
        if (watchRecords.isEmpty()) {
            System.out.println("Your watch history is empty!");
            return;
        }

        for (int i = 0; i < watchRecords.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, watchRecords.get(i));
        }
    }

    // Gets movie recommendations based on selected strategy.
    private void getRecommendations() {
        if (currentUser instanceof BasicUser) {
            System.out.println("\n[INFO] Recommendation Strategy");
            System.out.println("Basic users can only use 'By Genre' strategy.");
        }

        System.out.println("==================================================");
        System.out.println("Get Recommendations");
        System.out.println("==================================================");
        System.out.println("Choose a strategy:");
        System.out.println("1. By Genre");
        if (currentUser instanceof PremiumUser) {
            System.out.println("2. By Rating");
            System.out.println("3. By Year");
            System.out.println("4. Hybrid");
        }
        System.out.print("Your choice: ");
        String strategyChoice = scanner.nextLine().trim();
        String strategy = "By Genre";
        if (currentUser instanceof PremiumUser) {
            strategy = switch (strategyChoice) {
                case "2" -> "By Rating";
                case "3" -> "By Year";
                case "4" -> "Hybrid";
                default -> "By Genre";
                // default remains "By Genre"
            };
        }

        System.out.print("Enter number of recommendations: ");
        String input = scanner.nextLine().trim();
        handleGetRecommendations(input, strategy);
    }

    // Handles the logic for getting recommendations.
    private void handleGetRecommendations(String input, String strategy) {
        try {
            int N = Integer.parseInt(input);
            if (N > 0) {
                List<Movie> recommendations = recommendationEngine.recommend(
                        currentUser, allMovies, N, strategy);
                displayRecommendations(recommendations, strategy);
            } else {
                System.out.println("Please enter a positive integer!");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Please enter a valid number!");
        }
    }

    // Displays the generated recommendations.
    private void displayRecommendations(List<Movie> recommendations, String strategy) {
        System.out.println("==================================================");
        System.out.println("===== RECOMMENDATIONS FOR YOU ===== ");
        System.out.println("==================================================");
        System.out.println("Strategy: " + strategy);
        System.out.println("Account Type: " + currentUser.getAccountType() + "\n");

        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available.");
            return;
        }

        for (int i = 0; i < recommendations.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, recommendations.get(i));
        }
    }

    // Handles password change functionality.
    private void changePassword() {
        System.out.println("==================================================");
        System.out.println("Change Password");
        System.out.println("==================================================");
        System.out.print("Current Password: ");
        String oldPassword = scanner.nextLine().trim();
        System.out.print("New Password: ");
        String newPassword = scanner.nextLine().trim();
        System.out.print("Confirm New Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!SaltHash.check(currentUser.getPassword(), oldPassword)) {
            System.out.println("\n[ERROR] Password Change Failed");
            System.out.println("Current password is incorrect");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("\n[ERROR] Password Change Failed");
            System.out.println("New passwords do not match");
            return;
        }
        if (newPassword.isEmpty()) {
            System.out.println("\n[ERROR] Password Change Failed");
            System.out.println("New password cannot be empty");
            return;
        }

        currentUser.setPassword(SaltHash.make(newPassword));
        saveUsers();
        System.out.println("\n[INFO] Password Changed");
        System.out.println("Password updated successfully!");
    }

    // Logs out the current user and returns to login screen.
    private void logout() {
        currentUser = null;
        System.out.println("You have been logged out successfully.");
    }

    // Saves user data to CSV file.
    private void saveUsers() {
        try {
            CSVHandler.writeUsers(USER_CSV_PATH, allUsers);
        } catch (IOException e) {
            System.out.println("\n[ERROR] Save Failed");
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    // Checks if a username already exists in the system.
    private boolean usernameExists(String username) {
        for (User user : allUsers) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // Creates a new user account based on the specified type.
    private User createUserAccount(String username, String password, String accountType) {
        Watchlist watchlist = new Watchlist();
        History history = new History();

        if ("Premium User".equals(accountType)) {
            return new PremiumUser(username, SaltHash.make(password), watchlist, history);
        } else {
            return new BasicUser(username, SaltHash.make(password), watchlist, history);
        }
    }

    public static void main(String[] args) {
        MovieSystemCLI app = new MovieSystemCLI();
        app.start();
    }
}

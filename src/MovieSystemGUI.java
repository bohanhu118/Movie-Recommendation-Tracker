import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.util.List;

/**
 * Main application class for the Movie Recommendation and Tracking System.
 *
 * This JavaFX-based application provides a comprehensive movie management platform that allows
 * users to browse movies, maintain watchlists, track viewing history, and receive personalized
 * movie recommendations based on different strategies.
 *
 * Key Features:
 * - User authentication system with registration and login functionality
 * - Movie browsing and search capabilities
 * - Watchlist management (add/remove movies)
 * - Watch history tracking with timestamps
 * - Multiple recommendation strategies (by genre, rating, year, and hybrid)
 * - User account management (password change, account types)
 * - Data persistence using CSV files
 *
 * Technical Implementation:
 * This application demonstrates important JavaFX concepts introduced in the Week 12 lecture:
 *
 * 1. EVENT HANDLING WITH ANONYMOUS INNER CLASSES:
 *    Throughout this application, event handlers are implemented using anonymous inner classes
 *    that implement EventHandler<ActionEvent>. This approach allows inline event handling
 *    without creating separate named classes. Examples can be found in:
 *    - buildLoginScene(): Login, register, and exit button handlers
 *    - buildRegisterScene(): Registration and back button handlers
 *    - bindMenuEvents(): All main menu button handlers (browse, add, remove, etc.)
 *
 * 2. ACTION EVENTS:
 *    ActionEvent objects are used to capture user interactions with buttons and UI components.
 *    Each button in the interface has an associated ActionEvent handler that responds to
 *    user clicks and executes the corresponding functionality.
 *
 * 3. SCENE MANAGEMENT:
 *    The application uses multiple Scene objects (loginScene, registerScene, mainScene) to
 *    organize different views and provides smooth transitions between them.
 *
 * 4. UI COMPONENTS:
 *    Utilizes various JavaFX controls including Button, TextField, PasswordField, TextArea,
 *    ComboBox, Dialog, and layout managers (BorderPane, GridPane, VBox).
 */
public class MovieSystemGUI extends Application {
    private static final String MOVIE_CSV_PATH = "data/movies.csv";
    private static final String USER_CSV_PATH = "data/users.csv";

    private List<Movie> allMovies;
    private List<User> allUsers;
    private User currentUser;
    private RecommendationEngine recommendationEngine;

    private Stage primaryStage;
    private Scene loginScene, mainScene, registerScene;
    private TextArea displayArea;
    private ComboBox<String> strategyComboBox;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Movie Recommendation and Tracking System");
        recommendationEngine = new RecommendationEngine();

        initializeData();
        buildLoginScene();
        buildRegisterScene();
        buildMainScene();

        primaryStage.setScene(loginScene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.show();
    }

    // Initializes movie and user data from CSV files.
    private void initializeData() {
        try {
            allMovies = CSVHandler.readMovies(MOVIE_CSV_PATH);
            allUsers = CSVHandler.readUsers(USER_CSV_PATH, allMovies);
            System.out.println("Data initialization successful: " +
                    allMovies.size() + " movies, " + allUsers.size() + " users");
        } catch (IOException e) {
            showErrorAlert("Initialization Failed", "Data loading error: " + e.getMessage());
            System.exit(1);
        }
    }

    // Builds the login scene with username and password fields.
    private void buildLoginScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(25));
        grid.setHgap(15);
        grid.setVgap(15);

        Label titleLabel = new Label("Movie Recommendation System");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label pwdLabel = new Label("Password:");
        PasswordField pwdField = new PasswordField();

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Create New Account");
        Button exitBtn = new Button("Exit");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(userLabel, 0, 1);
        grid.add(userField, 1, 1);
        grid.add(pwdLabel, 0, 2);
        grid.add(pwdField, 1, 2);
        grid.add(loginBtn, 0, 3);
        grid.add(registerBtn, 1, 3);
        grid.add(exitBtn, 0, 4, 2, 1);

        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                handleLogin(userField.getText().trim(), pwdField.getText().trim());
            }
        });

        registerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                primaryStage.setScene(registerScene);
            }
        });

        exitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });

        loginScene = new Scene(grid, 500, 300);
    }

    // Builds the user registration scene.
    private void buildRegisterScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(25));
        grid.setHgap(15);
        grid.setVgap(15);

        Label titleLabel = new Label("Create New Account");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label pwdLabel = new Label("Password:");
        PasswordField pwdField = new PasswordField();
        Label confirmLabel = new Label("Confirm Password:");
        PasswordField confirmField = new PasswordField();
        Label typeLabel = new Label("Account Type:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Basic User", "Premium User");
        typeComboBox.setValue("Basic User");

        Button registerBtn = new Button("Register");
        Button backBtn = new Button("Back to Login");

        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(userLabel, 0, 1);
        grid.add(userField, 1, 1);
        grid.add(pwdLabel, 0, 2);
        grid.add(pwdField, 1, 2);
        grid.add(confirmLabel, 0, 3);
        grid.add(confirmField, 1, 3);
        grid.add(typeLabel, 0, 4);
        grid.add(typeComboBox, 1, 4);
        grid.add(registerBtn, 0, 5);
        grid.add(backBtn, 1, 5);

        registerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                handleRegistration(userField.getText().trim(),
                        pwdField.getText().trim(), confirmField.getText().trim(), typeComboBox.getValue());
            }
        });

        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                primaryStage.setScene(loginScene);
            }
        });

        registerScene = new Scene(grid, 500, 350);
    }

    // Builds the main application scene with all functionality buttons.
    private void buildMainScene() {
        BorderPane borderPane = new BorderPane();

        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(20));
        menuBox.setStyle("-fx-background-color: #f8f9fa;");

        Label strategyLabel = new Label("Recommendation Strategy:");
        strategyComboBox = new ComboBox<>();
        strategyComboBox.getItems().addAll("By Genre", "By Rating", "By Year", "Hybrid");
        strategyComboBox.setValue("By Genre");

        Button browseBtn = createMenuButton("Browse Movies");
        Button addWatchBtn = createMenuButton("Add to Watchlist");
        Button removeWatchBtn = createMenuButton("Remove from Watchlist");
        Button viewWatchBtn = createMenuButton("View Watchlist");
        Button markWatchedBtn = createMenuButton("Mark as Watched");
        Button viewHistoryBtn = createMenuButton("View History");
        Button recommendBtn = createMenuButton("Get Recommendations");
        Button changePwdBtn = createMenuButton("Change Password");
        Button logoutBtn = createMenuButton("Logout");

        menuBox.getChildren().addAll(strategyLabel, strategyComboBox, browseBtn, addWatchBtn,
                removeWatchBtn, viewWatchBtn, markWatchedBtn, viewHistoryBtn,
                recommendBtn, changePwdBtn, logoutBtn);

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPadding(new Insets(10));
        displayArea.setStyle("-fx-font-family: 'Monospaced';");

        borderPane.setLeft(menuBox);
        borderPane.setCenter(displayArea);

        bindMenuEvents(browseBtn, addWatchBtn, removeWatchBtn, viewWatchBtn,
                markWatchedBtn, viewHistoryBtn, recommendBtn, changePwdBtn, logoutBtn);

        mainScene = new Scene(borderPane, 900, 700);
    }

    // Creates a standardized menu button.
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(35);
        return button;
    }

    // Handles user login authentication.
    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showErrorAlert("Login Failed", "Please enter both username and password");
            return;
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
            showInfoAlert("Login Successful", "Welcome, " + username + "!\nAccount Type: " +
                    currentUser.getAccountType());
            primaryStage.setScene(mainScene);
            displayWelcomeMessage();
        } else {
            showErrorAlert("Login Failed", "Invalid username or password");
        }
    }

    // Displays welcome message with account privileges.
    private void displayWelcomeMessage() {
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

        displayArea.setText(message);
    }

    // Handles new user registration.
    private void handleRegistration(String username, String password, String confirmPassword, String accountType) {
        if (username.isEmpty() || password.isEmpty()) {
            showErrorAlert("Registration Failed", "Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorAlert("Registration Failed", "Passwords do not match");
            return;
        }

        if (usernameExists(username)) {
            showErrorAlert("Registration Failed", "Username already exists");
            return;
        }

        User newUser = createUserAccount(username, password, accountType);
        allUsers.add(newUser);
        saveUsers();

        showInfoAlert("Registration Successful",
                "Account created successfully!\nUsername: " + username +
                        "\nAccount Type: " + accountType);
        primaryStage.setScene(loginScene);
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

    // Binds event handlers to all menu buttons.
    private void bindMenuEvents(Button browseBtn, Button addWatchBtn, Button removeWatchBtn,
                                Button viewWatchBtn, Button markWatchedBtn, Button viewHistoryBtn,
                                Button recommendBtn, Button changePwdBtn, Button logoutBtn) {

        browseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                displayAllMovies();
            }
        });

        addWatchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                addToWatchlist();
            }
        });

        removeWatchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                removeFromWatchlist();
            }
        });

        viewWatchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                viewWatchlist();
            }
        });

        markWatchedBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                markAsWatched();
            }
        });

        viewHistoryBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                viewHistory();
            }
        });

        recommendBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                getRecommendations();
            }
        });

        changePwdBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                changePassword();
            }
        });

        logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                logout();
            }
        });
    }

    // Displays all movies in the system.
    private void displayAllMovies() {
        String text = "===== ALL MOVIES =====\n\n";
        for (int i = 0; i < allMovies.size(); i++) {
            text += String.format("%3d. %s\n", i + 1, allMovies.get(i));
        }
        displayArea.setText(text);
    }

    // Adds a movie to the user's watchlist.
    private void addToWatchlist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add to Watchlist");
        dialog.setHeaderText("Enter movie number (1-" + allMovies.size() + "):");

        String input = dialog.showAndWait().orElse(null);
        if (input != null) {
            handleAddToWatchlist(input);
        }
    }

    // Handles the logic for adding a movie to watchlist.
    private void handleAddToWatchlist(String input) {
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < allMovies.size()) {
                Movie movie = allMovies.get(index);
                if (currentUser.getWatchlist().addMovie(movie)) {
                    saveUsers();
                    displayArea.setText("Successfully added \"" + movie.getTitle() + "\" to your watchlist!");
                } else {
                    displayArea.setText("\"" + movie.getTitle() + "\" is already in your watchlist!");
                }
            } else {
                displayArea.setText("Invalid number! Please enter between 1 and " + allMovies.size());
            }
        } catch (NumberFormatException ex) {
            displayArea.setText("Please enter a valid number!");
        }
    }

    // Removes a movie from the user's watchlist.
    private void removeFromWatchlist() {
        List<Movie> watchlist = currentUser.getWatchlist().getMovies();
        if (watchlist.isEmpty()) {
            displayArea.setText("Your watchlist is empty!");
            return;
        }

        displayWatchlistForRemoval();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove from Watchlist");
        dialog.setHeaderText("Enter the number of movie to remove:");

        String input = dialog.showAndWait().orElse(null);
        if (input != null) {
            handleRemoveFromWatchlist(input);
        }
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
                displayArea.setText("Successfully removed \"" + movie.getTitle() + "\" from watchlist!");
            } else {
                displayArea.setText("Invalid number! Please enter between 1 and " + watchlist.size());
            }
        } catch (NumberFormatException ex) {
            displayArea.setText("Please enter a valid number!");
        }
    }

    // Displays the user's watchlist.
    private void viewWatchlist() {
        List<Movie> watchlist = currentUser.getWatchlist().getMovies();
        if (watchlist.isEmpty()) {
            displayArea.setText("===== YOUR WATCHLIST =====\n\nYour watchlist is empty!");
            return;
        }

        String text = "===== YOUR WATCHLIST =====\n\n";
        for (int i = 0; i < watchlist.size(); i++) {
            text += String.format("%2d. %s\n", i + 1, watchlist.get(i));
        }
        displayArea.setText(text);
    }

    // Displays watchlist with removal instructions.
    private void displayWatchlistForRemoval() {
        List<Movie> watchlist = currentUser.getWatchlist().getMovies();
        String text = "===== YOUR WATCHLIST =====\n\n";
        for (int i = 0; i < watchlist.size(); i++) {
            text += String.format("%2d. %s\n", i + 1, watchlist.get(i));
        }
        text += "\nEnter the number of movie to remove:";
        displayArea.setText(text);
    }

    // Marks a movie as watched and moves it to history.
    private void markAsWatched() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mark as Watched");
        dialog.setHeaderText("Enter movie number (1-" + allMovies.size() + "):");

        String input = dialog.showAndWait().orElse(null);
        if (input != null) {
            handleMarkAsWatched(input);
        }
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
                    displayArea.setText("Successfully marked \"" + movie.getTitle() + "\" as watched!");
                } else {
                    displayArea.setText("\"" + movie.getTitle() + "\" is already in your watch history!");
                }
            } else {
                displayArea.setText("Invalid number! Please enter between 1 and " + allMovies.size());
            }
        } catch (NumberFormatException ex) {
            displayArea.setText("Please enter a valid number!");
        }
    }

    // Displays the user's watch history.
    private void viewHistory() {
        List<WatchRecord> watchRecords = currentUser.getHistory().getWatchRecords();
        if (watchRecords.isEmpty()) {
            displayArea.setText("===== YOUR WATCH HISTORY =====\n\nYour watch history is empty!");
            return;
        }

        String text = "===== YOUR WATCH HISTORY =====\n\n";
        for (int i = 0; i < watchRecords.size(); i++) {
            text += String.format("%2d. %s\n", i + 1, watchRecords.get(i));
        }
        displayArea.setText(text);
    }

    // Gets movie recommendations based on selected strategy.
    private void getRecommendations() {
        if (currentUser instanceof BasicUser && !"By Genre".equals(strategyComboBox.getValue())) {
            showErrorAlert("Premium Feature",
                    "Advanced recommendation strategies are only available for Premium users.");
            strategyComboBox.setValue("By Genre");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("5");
        dialog.setTitle("Get Recommendations");
        dialog.setHeaderText("Enter number of recommendations:");

        String input = dialog.showAndWait().orElse(null);
        if (input != null) {
            handleGetRecommendations(input);
        }
    }

    // Handles the logic for getting recommendations.
    private void handleGetRecommendations(String input) {
        try {
            int N = Integer.parseInt(input);
            if (N > 0) {
                String strategy = strategyComboBox.getValue();
                List<Movie> recommendations = recommendationEngine.recommend(
                        currentUser, allMovies, N, strategy);
                displayRecommendations(recommendations, strategy);
            } else {
                displayArea.setText("Please enter a positive integer!");
            }
        } catch (NumberFormatException ex) {
            displayArea.setText("Please enter a valid number!");
        }
    }

    // Displays the generated recommendations.
    private void displayRecommendations(List<Movie> recommendations, String strategy) {
        String text = "===== RECOMMENDATIONS FOR YOU =====\n\n";
        text += "Strategy: " + strategy + "\n";
        text += "Account Type: " + currentUser.getAccountType() + "\n\n";

        for (int i = 0; i < recommendations.size(); i++) {
            text += String.format("%2d. %s\n", i + 1, recommendations.get(i));
        }
        displayArea.setText(text);
    }

    // Handles password change functionality.
    private void changePassword() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current and new passwords");

        ButtonType confirmBtn = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBtn, ButtonType.CANCEL);

        PasswordField oldPwd = new PasswordField();
        PasswordField newPwd = new PasswordField();
        PasswordField confirmPwd = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(oldPwd, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPwd, 1, 1);
        grid.add(new Label("Confirm New Password:"), 0, 2);
        grid.add(confirmPwd, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait();
        ButtonType clickedBtn = dialog.getResult();

        if (clickedBtn != null && clickedBtn == confirmBtn) {
            PasswordChangeData data = new PasswordChangeData(
                    oldPwd.getText(),
                    newPwd.getText(),
                    confirmPwd.getText()
            );
            handlePasswordChange(data);
        }
    }


    // Handles the password change logic.
    private void handlePasswordChange(PasswordChangeData data) {
        if (!SaltHash.check(currentUser.getPassword(), data.oldPassword)) {
            showErrorAlert("Password Change Failed", "Current password is incorrect");
            return;
        }
        if (!data.newPassword.equals(data.confirmPassword)) {
            showErrorAlert("Password Change Failed", "New passwords do not match");
            return;
        }
        if (data.newPassword.isEmpty()) {
            showErrorAlert("Password Change Failed", "New password cannot be empty");
            return;
        }

        currentUser.setPassword(SaltHash.make(data.newPassword));
        saveUsers();
        showInfoAlert("Password Changed", "Password updated successfully!");
    }

    // Logs out the current user and returns to login screen.
    private void logout() {
        currentUser = null;
        primaryStage.setScene(loginScene);
        displayArea.clear();
    }

    // Saves user data to CSV file.
    private void saveUsers() {
        try {
            CSVHandler.writeUsers(USER_CSV_PATH, allUsers);
        } catch (IOException e) {
            showErrorAlert("Save Failed", "Error saving user data: " + e.getMessage());
        }
    }

    // Shows an error alert dialog.
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Shows an information alert dialog.
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data container for password change information.
    private static class PasswordChangeData {
        final String oldPassword;
        final String newPassword;
        final String confirmPassword;

        PasswordChangeData(String oldPassword, String newPassword, String confirmPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

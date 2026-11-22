package bank.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Banking System");
        showLogin();
        stage.show();
    }

    // ---------- LOGIN SCREEN ----------
    private void showLogin() {
        Label title = new Label("Bank Login");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label message = new Label();
        message.setStyle("-fx-text-fill: red;");

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();

            // PLACEHOLDER: we will replace this with your real login logic
            if (user.equals("admin") && pass.equals("1234")) {
                showDashboard(user);
            } else {
                message.setText("Invalid username or password");
            }
        });

        VBox root = new VBox(12, title, usernameField, passwordField, loginBtn, message);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setMaxWidth(300);

        VBox wrapper = new VBox(root);
        wrapper.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(wrapper, 420, 360));
    }

    // ---------- DASHBOARD (temporary) ----------
    private void showDashboard(String user) {
        Label welcome = new Label("Welcome, " + user);
        welcome.setStyle("-fx-font-size: 20px;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> showLogin());

        VBox root = new VBox(15, welcome, logoutBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        stage.setScene(new Scene(root, 420, 360));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
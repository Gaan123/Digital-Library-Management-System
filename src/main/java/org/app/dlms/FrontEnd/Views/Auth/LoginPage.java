package org.app.dlms.FrontEnd.Views.Auth;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.app.dlms.Backend.Dao.UserDAO;
import org.app.dlms.Backend.Model.User;
import org.app.dlms.FrontEnd.Views.Dashboard.AdminDashboard;
import org.app.dlms.Middleware.Services.AlertService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LoginPage extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
//        primaryStage.initStyle(StageStyle.UNDECORATED); // Remove default window borders

        // Create a BorderPane as the main layout
        BorderPane borderPane = new BorderPane();

        // Create a gradient background
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#1a237e")),
                new Stop(1, Color.web("#283593"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        borderPane.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create a VBox for the login form
        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(30));
        loginBox.setMaxWidth(400);
        loginBox.setMaxHeight(500);

        // Apply a white, semi-transparent background with rounded corners to the login box
        loginBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.9), new CornerRadii(10), Insets.EMPTY)));

        // Add drop shadow to the login box
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(10);
        loginBox.setEffect(shadow);

        // Create a title
        Text title = new Text("Welcome Back");
        title.setFont(Font.font("Montserrat", FontWeight.BOLD, 28));
        title.setFill(Color.web("#303f9f"));

        // Create a subtitle
        Text subtitle = new Text("Sign in to continue");
        subtitle.setFont(Font.font("Montserrat", FontWeight.NORMAL, 14));
        subtitle.setFill(Color.web("#757575"));

        // Create a container for the title and subtitle
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(title, subtitle);

        // Try to add a user icon
        VBox iconBox = new VBox(10);
        iconBox.setAlignment(Pos.CENTER);
        try {
            // Note: In a real app, you would package this with your application
            // This is just for demonstration - comment out if you don't have this image
            ImageView userIcon = new ImageView(new Image(new FileInputStream("user_icon.png")));
            userIcon.setFitHeight(80);
            userIcon.setFitWidth(80);
            iconBox.getChildren().add(userIcon);
        } catch (FileNotFoundException e) {
            // If image not found, use a text placeholder
            Text iconPlaceholder = new Text("👤");
            iconPlaceholder.setFont(Font.font("Arial", FontWeight.NORMAL, 50));
            iconBox.getChildren().add(iconPlaceholder);
        }

        // Username field with styling
        TextField userTextField = new TextField();
        userTextField.setPromptText("Username");
        userTextField.setStyle(
                "-fx-background-color: #f5f5f5;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10px;"
        );

        // Password field with styling
        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Password");
        pwField.setStyle(
                "-fx-background-color: #f5f5f5;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10px;"
        );

        // Login button with styling
        Button loginBtn = new Button("SIGN IN");
        loginBtn.setStyle(
                "-fx-background-color: #303f9f;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        loginBtn.setPrefWidth(Double.MAX_VALUE);

        // Hover effect for login button
        loginBtn.setOnMouseEntered(e ->
                loginBtn.setStyle(
                        "-fx-background-color: #3949ab;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 10 20 10 20;" +
                                "-fx-background-radius: 5;" +
                                "-fx-cursor: hand;"
                )
        );
        loginBtn.setOnMouseExited(e ->
                loginBtn.setStyle(
                        "-fx-background-color: #303f9f;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 10 20 10 20;" +
                                "-fx-background-radius: 5;" +
                                "-fx-cursor: hand;"
                )
        );

        // Create account link
//        Text createAccountLink = new Text("Don't have an account? Create one");
//        createAccountLink.setFont(Font.font("Montserrat", 12));
//        createAccountLink.setFill(Color.web("#3949ab"));
//        createAccountLink.setStyle("-fx-cursor: hand;");
//
//        // Hover effect for create account link
//        createAccountLink.setOnMouseEntered(e ->
//                createAccountLink.setFill(Color.web("#5c6bc0"))
//        );
//        createAccountLink.setOnMouseExited(e ->
//                createAccountLink.setFill(Color.web("#3949ab"))
//        );



        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));
        borderPane.setTop(topBar);

        // Add components to the login box
        loginBox.getChildren().addAll(
                iconBox,
                titleBox,
                userTextField,
                pwField,
                loginBtn
//                ,
//                createAccountLink
        );
        AlertService alertService= new AlertService();
        // Set action for login button
        loginBtn.setOnAction(e -> {
            if (validateLogin(userTextField.getText(), pwField.getText())) {
                alertService.showAlert(Alert.AlertType.INFORMATION, "Login Successful",
                        "Welcome " + userTextField.getText() + "!");
                AdminDashboard dashboard = new AdminDashboard();
                Stage dashboardStage = new Stage();
                dashboard.start(dashboardStage);

                // Optionally hide the login window instead of closing it
                primaryStage.hide();
            } else {
                alertService.showAlert(Alert.AlertType.ERROR, "Login Failed",
                        "Invalid username or password.");
            }
        });

        // Set action for create account link
//        createAccountLink.setOnMouseClicked(e -> {
//            showAlert(Alert.AlertType.INFORMATION, "Create Account",
//                    "Create account functionality would be implemented here.");
//        });

        // Make the window draggable
        borderPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        borderPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        // Center the login box
        borderPane.setCenter(loginBox);

        // Set the scene
        Scene scene = new Scene(borderPane, 1920, 1080);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private boolean validateLogin(String username, String password) {
        // For demonstration only - in a real application you would validate against a database
        UserDAO userDAO = new UserDAO();
        try {
             User user = userDAO.login(username, password);
            return user != null;
        }catch (Exception e) {
            return false;
        }
    }



}
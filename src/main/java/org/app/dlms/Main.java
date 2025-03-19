package org.app.dlms;

import javafx.application.Application;
import javafx.stage.Stage;
import org.app.dlms.FrontEnd.Views.Auth.LoginPage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginPage loginPage = new LoginPage();
        loginPage.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args); // This ensures JavaFX starts on the correct thread
    }
}

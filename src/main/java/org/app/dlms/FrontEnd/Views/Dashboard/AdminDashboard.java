package org.app.dlms.FrontEnd.Views.Dashboard;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

import org.app.dlms.Backend.Model.Admin;
import org.app.dlms.FrontEnd.Views.Components.Sidebar;
import org.app.dlms.FrontEnd.Views.Components.TopBar;
import org.app.dlms.FrontEnd.Views.Components.ContentArea;

public class AdminDashboard extends Application {

    private String currentUser = "Admin User";
    private Popup profileDropdown;
    private Stage primaryStage;
    private Admin admin;

    private void commonConstructed() {
        primaryStage.setTitle("Admin Dashboard");

        // Create the main layout
        BorderPane mainLayout = new BorderPane();

        // Create and set the sidebar
        Sidebar sidebar = new Sidebar();
        mainLayout.setLeft(sidebar.getComponent());

        // Create and set the top bar
        TopBar topBar = new TopBar(currentUser);
        profileDropdown = topBar.getProfileDropdown();
        topBar.setProfileClickHandler(e -> handleProfileClick(e, topBar));
        mainLayout.setTop(topBar.getComponent());

        // Create a content area
        ContentArea contentArea = new ContentArea();
        mainLayout.setCenter(contentArea.getComponent());

        // Set the scene
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(scene);

        // Close dropdown when clicking outside
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (profileDropdown.isShowing()) {
                profileDropdown.hide();
            }
        });

        primaryStage.show();
    }

    public void start(Stage primaryStage, Admin admin) {
        this.primaryStage = primaryStage;
        this.admin = admin;
        commonConstructed();
    }
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        commonConstructed();
    }

    private void handleProfileClick(MouseEvent e, TopBar topBar) {
        double x = topBar.getProfileSectionBounds().getMinX();
        double y = topBar.getProfileSectionBounds().getMaxY();

        // Show dropdown at those coordinates
        if (profileDropdown.isShowing()) {
            profileDropdown.hide();
        } else {
            profileDropdown.show(primaryStage, x, y);
        }

        // Prevent event from propagating to scene, which would close the dropdown
        e.consume();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
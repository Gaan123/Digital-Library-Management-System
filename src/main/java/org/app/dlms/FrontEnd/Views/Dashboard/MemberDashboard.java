package org.app.dlms.FrontEnd.Views.Dashboard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.app.dlms.Backend.Model.Librarian;
import org.app.dlms.Backend.Model.Member;
import org.app.dlms.FrontEnd.Views.Auth.LoginPage;
import org.app.dlms.FrontEnd.Views.Components.ContentArea;
import org.app.dlms.FrontEnd.Views.Components.DashboardComponents;
import org.app.dlms.FrontEnd.Views.Components.Sidebar;
import org.app.dlms.FrontEnd.Views.Components.TopBar;

public class MemberDashboard extends Application {
    private BorderPane mainLayout;
    private Sidebar sidebar;
    private ContentArea contentArea;
    private String currentUser = "Admin User";
    private Popup profileDropdown;
    private Stage primaryStage;
    private Member member;
    private DashboardComponents components;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        primaryStage.setTitle("Member Dashboard");
        if (member == null) {
            // Create and show login page
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
            return; // Important: exit this method to prevent dashboard from loading
        }
        // Create the main layout
        mainLayout = new BorderPane();

        // Create the sidebar
        sidebar = new Sidebar(member);
        mainLayout.setLeft(sidebar.getComponent());

        // Create the content area
        contentArea = new ContentArea();
        mainLayout.setCenter(contentArea.getComponent());

        // Create and set the top bar
        TopBar topBar = new TopBar(currentUser);
        profileDropdown = topBar.getProfileDropdown();
        topBar.setProfileClickHandler(e -> handleProfileClick(e, topBar));
        mainLayout.setTop(topBar.getComponent());

        // Initialize components
        components = new DashboardComponents(contentArea);

        // Set the initial content
        contentArea.setContent(components.getDashboardComponent());

        // Configure menu listeners
        configureMenuListeners();

        // Set the scene
        Scene scene = new Scene(mainLayout, 1920, 1080);
        primaryStage.setScene(scene);

        // Close dropdown when clicking outside
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (profileDropdown.isShowing()) {
                profileDropdown.hide();
            }
        });

        primaryStage.show();
    }

    public void start(Stage primaryStage, Member member) {
        this.primaryStage = primaryStage;
        this.member = member;
        start(primaryStage);
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

    private void configureMenuListeners() {
        // Add menu change listeners to the sidebar
        sidebar.setOnMenuItemClicked((menuItem) -> {

            switch(menuItem) {
                case "Dashboard":
                    System.out.println("Dashboard");
                    contentArea.setContent(components.getDashboardComponent());
                    break;
                case "Users":
                    contentArea.setContent(components.getUsersComponent());
                    break;
                case "Books":
                    contentArea.setContent(components.getBooksComponent());
                    break;
                case "Borrowed Books":
                    contentArea.setContent(components.getBorrowedBooksComponent());
                    break;
                case "Payments":
                    contentArea.setContent(components.getPaymentsComponent());
                    break;
                case "Logout":
                    // Handle logout action
                    LoginPage loginPage = new LoginPage();
                    loginPage.start(primaryStage);
                    break;
                default:
                    contentArea.setContent(ContentArea.createWelcomeContent(
                            "Page Not Found",
                            "The requested page does not exist"
                    ));
            }
        });
    }

    public BorderPane getLayout() {
        return mainLayout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package org.app.dlms.FrontEnd.Views.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * A reusable content area component for the admin dashboard
 */
public class ContentArea {
    private StackPane component;

    public ContentArea() {
        initializeComponent();
    }

    private void initializeComponent() {
        // Create a simple content area as a placeholder
        component = new StackPane();
        component.setBackground(new Background(new BackgroundFill(Color.web("#f5f5f5"), CornerRadii.EMPTY, Insets.EMPTY)));
        component.setPadding(new Insets(20));

        // Create a dashboard welcome message
        VBox welcomeBox = new VBox(10);
        welcomeBox.setAlignment(Pos.CENTER);

        Text welcomeTitle = new Text("Welcome to Admin Dashboard");
        welcomeTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        welcomeTitle.setFill(Color.web("#303f9f"));

        Text welcomeSubtitle = new Text("Select an option from the sidebar to get started");
        welcomeSubtitle.setFont(Font.font("Montserrat", FontWeight.NORMAL, 16));
        welcomeSubtitle.setFill(Color.web("#757575"));

        welcomeBox.getChildren().addAll(welcomeTitle, welcomeSubtitle);

        component.getChildren().add(welcomeBox);
    }

    /**
     * Gets the content area component
     * @return The StackPane containing the content area
     */
    public StackPane getComponent() {
        return component;
    }

    /**
     * Sets the content of the content area
     * @param content The content to set
     */
    public void setContent(javafx.scene.Node content) {
        component.getChildren().clear();
        component.getChildren().add(content);
    }

    /**
     * Creates a default welcome content
     * @param title The welcome title
     * @param subtitle The welcome subtitle
     * @return The welcome content VBox
     */
    public static VBox createWelcomeContent(String title, String subtitle) {
        VBox welcomeBox = new VBox(10);
        welcomeBox.setAlignment(Pos.CENTER);

        Text welcomeTitle = new Text(title);
        welcomeTitle.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        welcomeTitle.setFill(Color.web("#303f9f"));

        Text welcomeSubtitle = new Text(subtitle);
        welcomeSubtitle.setFont(Font.font("Montserrat", FontWeight.NORMAL, 16));
        welcomeSubtitle.setFill(Color.web("#757575"));

        welcomeBox.getChildren().addAll(welcomeTitle, welcomeSubtitle);
        
        return welcomeBox;
    }
}
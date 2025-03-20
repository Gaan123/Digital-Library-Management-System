package org.app.dlms.FrontEnd.Views.Components;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.event.EventHandler;

/**
 * A reusable top bar component for the admin dashboard
 */
public class TopBar {
    private HBox component;
    private HBox profileSection;
    private Popup profileDropdown;
    private String currentUser;
    private EventHandler<MouseEvent> profileClickHandler;

    public TopBar(String currentUser) {
        this.currentUser = currentUser;
        initializeComponent();
        createProfileDropdown();
    }

    private void initializeComponent() {
        // Create the top bar container
        component = new HBox();
        component.setPadding(new Insets(15, 25, 15, 25));
        component.setAlignment(Pos.CENTER_LEFT);
        component.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Add drop shadow to top bar
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setRadius(5);
        shadow.setOffsetY(1);
        component.setEffect(shadow);

        // Create search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
                "-fx-background-color: #f5f5f5;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 8 15 8 15;"
        );

        // Create notification icon
        Text notificationIcon = new Text("🔔");
        notificationIcon.setFont(Font.font("Arial", 16));
        notificationIcon.setFill(Color.web("#757575"));
        notificationIcon.setStyle("-fx-cursor: hand;");

        // Create message icon
        Text messageIcon = new Text("✉️");
        messageIcon.setFont(Font.font("Arial", 16));
        messageIcon.setFill(Color.web("#757575"));
        messageIcon.setStyle("-fx-cursor: hand;");

        // Create a spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create user profile section
        profileSection = createProfileSection();

        // Organize top bar elements with appropriate spacing
        HBox iconsBox = new HBox(20);
        iconsBox.setAlignment(Pos.CENTER);
        iconsBox.getChildren().addAll(notificationIcon, messageIcon);

        component.getChildren().addAll(searchField, spacer, iconsBox, profileSection);
    }

    private HBox createProfileSection() {
        HBox profileSection = new HBox(15);
        profileSection.setAlignment(Pos.CENTER);
        profileSection.setPadding(new Insets(0, 0, 0, 20));

        // Create profile image with circular clip
        StackPane profileImageContainer = new StackPane();
        profileImageContainer.setMinSize(40, 40);
        profileImageContainer.setMaxSize(40, 40);

        // Set default profile display (text initials)
        StackPane defaultProfile = new StackPane();
        defaultProfile.setBackground(new Background(new BackgroundFill(Color.web("#303f9f"), new CornerRadii(50), Insets.EMPTY)));
        defaultProfile.setMinSize(40, 40);
        defaultProfile.setMaxSize(40, 40);

        Text initials = new Text(getInitials(currentUser));
        initials.setFont(Font.font("Montserrat", FontWeight.BOLD, 16));
        initials.setFill(Color.WHITE);
        defaultProfile.getChildren().add(initials);

        profileImageContainer.getChildren().add(defaultProfile);

        // Create profile text info
        VBox profileInfo = new VBox(2);

        Text nameText = new Text(currentUser);
        nameText.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
        nameText.setFill(Color.web("#212121"));

        Text roleText = new Text("Administrator");
        roleText.setFont(Font.font("Montserrat", FontWeight.NORMAL, 12));
        roleText.setFill(Color.web("#757575"));

        profileInfo.getChildren().addAll(nameText, roleText);

        // Create dropdown indicator
        Text dropdownIcon = new Text("▼");
        dropdownIcon.setFont(Font.font("Arial", 8));
        dropdownIcon.setFill(Color.web("#757575"));

        // Combine all elements
        profileSection.getChildren().addAll(profileImageContainer, profileInfo, dropdownIcon);
        profileSection.setStyle("-fx-cursor: hand;");

        return profileSection;
    }

    private void createProfileDropdown() {
        profileDropdown = new Popup();
        profileDropdown.setAutoHide(true);

        // Create dropdown content
        VBox dropdownContent = new VBox();
        dropdownContent.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
        dropdownContent.setPadding(new Insets(5));
        dropdownContent.setMinWidth(200);

        // Add drop shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        dropdownContent.setEffect(shadow);

        // Add dropdown items
        String[] dropdownItems = {"My Profile", "Account Settings", "Notifications", "Help"};
        String[] dropdownIcons = {"👤", "⚙️", "🔔", "❓"};

        for (int i = 0; i < dropdownItems.length; i++) {
            HBox item = createDropdownItem(dropdownIcons[i], dropdownItems[i]);

            // Add separator for the last item
            if (i == dropdownItems.length - 1) {
                // Add a separator line
                HBox separator = new HBox();
                separator.setPrefHeight(1);
                separator.setBackground(new Background(new BackgroundFill(Color.web("#e0e0e0"), CornerRadii.EMPTY, Insets.EMPTY)));
                separator.setOpacity(0.7);
                dropdownContent.getChildren().add(separator);
            }

            dropdownContent.getChildren().add(item);
        }

        profileDropdown.getContent().add(dropdownContent);
    }

    private HBox createDropdownItem(String icon, String text) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 15, 10, 15));
        item.setPrefWidth(200);
        item.setStyle("-fx-cursor: hand;");

        // The icon
        Text iconText = new Text(icon);
        iconText.setFont(Font.font("Arial", 14));
        iconText.setFill(Color.web("#757575"));

        // The item text
        Text itemText = new Text(text);
        itemText.setFont(Font.font("Montserrat", FontWeight.NORMAL, 13));
        itemText.setFill(Color.web("#212121"));

        item.getChildren().addAll(iconText, itemText);

        // Add hover effect
        item.setOnMouseEntered(e ->
                item.setBackground(new Background(new BackgroundFill(
                        Color.web("#f5f5f5"), new CornerRadii(5), Insets.EMPTY)))
        );

        item.setOnMouseExited(e ->
                item.setBackground(new Background(new BackgroundFill(
                        Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)))
        );

        // Set click event
        item.setOnMouseClicked(e -> {
            System.out.println(text + " clicked from dropdown");
            profileDropdown.hide();

            // If logout is clicked, could implement actual logout here
            if (text.equals("Logout")) {
                System.out.println("Performing logout...");
            }

            e.consume(); // Prevent event bubbling
        });

        return item;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";

        String[] nameParts = name.trim().split("\\s+");
        if (nameParts.length == 1) {
            return String.valueOf(nameParts[0].charAt(0)).toUpperCase();
        } else {
            return (String.valueOf(nameParts[0].charAt(0)) + String.valueOf(nameParts[nameParts.length - 1].charAt(0))).toUpperCase();
        }
    }

    /**
     * Sets the event handler for profile click
     * @param handler The event handler to set
     */
    public void setProfileClickHandler(EventHandler<MouseEvent> handler) {
        this.profileClickHandler = handler;
        profileSection.setOnMouseClicked(handler);
    }

    /**
     * Gets the bounds of the profile section in screen coordinates
     * @return The bounds of the profile section
     */
    public Bounds getProfileSectionBounds() {
        return profileSection.localToScreen(profileSection.getBoundsInLocal());
    }

    /**
     * Gets the top bar component
     * @return The HBox containing the top bar
     */
    public HBox getComponent() {
        return component;
    }

    /**
     * Gets the profile dropdown
     * @return The profile dropdown popup
     */
    public Popup getProfileDropdown() {
        return profileDropdown;
    }
}
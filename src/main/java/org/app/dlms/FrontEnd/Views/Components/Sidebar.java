package org.app.dlms.FrontEnd.Views.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
/**
 * Modified Sidebar with menu click event handler
 */
public class Sidebar {
    private VBox component;
    private MenuItemClickListener menuItemClickListener;

    // Menu items for the sidebar
    private final String[] menuItems = {
            "Dashboard", "Users", "Books", "Borrowed Books", "Payments"
    };

    // Icons for menu items
    private final String[] menuIcons = {
            "📊", "👥", "📚", "📋", "💰", "⚙️"
    };

    public Sidebar() {
        initializeComponent();
    }

    private void initializeComponent() {
        // Create the sidebar container
        component = new VBox(15);
        component.setPrefWidth(250);
        component.setPadding(new Insets(20, 15, 20, 15));

        // Set the background color for sidebar
        component.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #283593);");

        // Create logo/brand area
        Text brandName = new Text("ADMIN PANEL");
        brandName.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        brandName.setFill(Color.WHITE);

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 0, 30, 0));
        brandBox.getChildren().add(brandName);

        component.getChildren().add(brandBox);

        // Create menu items
        for (int i = 0; i < menuItems.length; i++) {
            HBox menuItem = createMenuItem(menuIcons[i], menuItems[i]);
            // Set the first item as active by default
            if (i == 0) {
                menuItem.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.2);" +
                                "-fx-background-radius: 5;" +
                                "-fx-cursor: hand;"
                );
            }
            component.getChildren().add(menuItem);
        }

        // Add a spacer
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        component.getChildren().add(spacer);

        // Add logout button at the bottom
        HBox logoutItem = createMenuItem("🚪", "Logout");
        logoutItem.setStyle("-fx-cursor: hand;");
        component.getChildren().add(logoutItem);
    }

    private HBox createMenuItem(String icon, String text) {
        HBox menuItem = new HBox(15);
        menuItem.setAlignment(Pos.CENTER_LEFT);
        menuItem.setPadding(new Insets(10, 15, 10, 15));
        menuItem.setStyle("-fx-cursor: hand;");

        // The icon
        Text iconText = new Text(icon);
        iconText.setFont(Font.font("Arial", 18));
        iconText.setFill(Color.WHITE);

        // The menu text
        Text menuText = new Text(text);
        menuText.setFont(Font.font("Montserrat", FontWeight.MEDIUM, 14));
        menuText.setFill(Color.WHITE);

        menuItem.getChildren().addAll(iconText, menuText);

        // Add hover effect
        menuItem.setOnMouseEntered(e -> {
            if (!menuItem.getStyle().contains("rgba(255, 255, 255, 0.2)")) {
                menuItem.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                                "-fx-background-radius: 5;" +
                                "-fx-cursor: hand;"
                );
            }
        });

        menuItem.setOnMouseExited(e -> {
            if (!menuItem.getStyle().contains("rgba(255, 255, 255, 0.2)")) {
                menuItem.setStyle("-fx-cursor: hand;");
            }
        });

        // Set click event
        menuItem.setOnMouseClicked(e -> {
            // Reset all menu items
            for (Node node : component.getChildren()) {
                if (node instanceof HBox && ((HBox) node).getChildren().size() >= 2) {
                    node.setStyle("-fx-cursor: hand;");
                }
            }

            // Set this item as active
            menuItem.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.2);" +
                            "-fx-background-radius: 5;" +
                            "-fx-cursor: hand;"
            );

            // Notify the listener
            if (menuItemClickListener != null) {
                menuItemClickListener.onMenuItemClicked(text);
            }
        });

        return menuItem;
    }

    /**
     * Sets the menu item click listener
     * @param listener The listener to notify when a menu item is clicked
     */
    public void setOnMenuItemClicked(MenuItemClickListener listener) {
        System.out.println(22);

        this.menuItemClickListener = listener;
    }

    /**
     * Gets the sidebar component
     * @return The VBox containing the sidebar
     */
    public VBox getComponent() {
        return component;
    }

    /**
     * Interface for menu item click events
     */
    public interface MenuItemClickListener {
        void onMenuItemClicked(String menuItem);
    }
}
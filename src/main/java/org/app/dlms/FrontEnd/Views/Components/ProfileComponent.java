package org.app.dlms.FrontEnd.Views.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.app.dlms.Backend.Dao.UserDAO;
import org.app.dlms.Backend.Model.Member;
import org.app.dlms.Backend.Model.User;
import org.app.dlms.Middleware.Enums.MembershipType;
import org.app.dlms.Middleware.Enums.UserRole;

/**
 * Component for viewing and editing user profiles
 */
public class ProfileComponent {
    private final UserDAO userDAO;
    private final User currentUser;

    public ProfileComponent(User user) {
        this.userDAO = new UserDAO();
        this.currentUser = user;
    }

    public Node createProfileComponent() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Profile header
        Text header = new Text("My Profile");
        header.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        header.setFill(Color.web("#303f9f"));

        // Create form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        // User info fields
        TextField nameField = new TextField(currentUser.getName());
        TextField usernameField = new TextField(currentUser.getUsername());
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        TextField emailField = new TextField(currentUser.getEmail());
        TextField phoneField = new TextField(currentUser.getPhone());
        
        // Create labels with styling
        Label nameLabel = createStyledLabel("Name:");
        Label usernameLabel = createStyledLabel("Username:");
        Label passwordLabel = createStyledLabel("New Password:");
        Label confirmPasswordLabel = createStyledLabel("Confirm Password:");
        Label emailLabel = createStyledLabel("Email:");
        Label phoneLabel = createStyledLabel("Phone:");
        
        // Add membership type field for members
        ComboBox<MembershipType> membershipTypeComboBox = null;
        if (currentUser.getRole() == UserRole.Member) {
            Label membershipLabel = createStyledLabel("Membership Type:");
            membershipTypeComboBox = new ComboBox<>();
            membershipTypeComboBox.getItems().addAll(MembershipType.values());
            membershipTypeComboBox.setValue(((Member) currentUser).getMembershipType());
            membershipTypeComboBox.setDisable(true); // Only admin can change membership type
            
            formGrid.addRow(6, membershipLabel, membershipTypeComboBox);
        }
        
        // Read-only username field since it's used for login
        usernameField.setDisable(true);
        
        // Add fields to grid
        formGrid.addRow(0, nameLabel, nameField);
        formGrid.addRow(1, usernameLabel, usernameField);
        formGrid.addRow(2, passwordLabel, passwordField);
        formGrid.addRow(3, confirmPasswordLabel, confirmPasswordField);
        formGrid.addRow(4, emailLabel, emailField);
        formGrid.addRow(5, phoneLabel, phoneField);
        
        // Add column constraints
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(150);
        
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setMinWidth(250);
        fieldCol.setMaxWidth(350);
        
        formGrid.getColumnConstraints().addAll(labelCol, fieldCol);
        
        // Buttons for actions
        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        // Show current role
        Label roleLabel = createStyledLabel("Role:");
        Label roleValueLabel = new Label(currentUser.getRole().toString());
        roleValueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #303f9f;");
        
        HBox roleBox = new HBox(10, roleLabel, roleValueLabel);
        roleBox.setAlignment(Pos.CENTER_LEFT);
        
        // Handle save button action
        saveButton.setOnAction(e -> {
            if (validateForm(nameField, passwordField, confirmPasswordField, emailField, phoneField)) {
                // Update user details
                currentUser.setName(nameField.getText().trim());
                currentUser.setEmail(emailField.getText().trim());
                currentUser.setPhone(phoneField.getText().trim());
                
                // Only update password if new one is provided
                if (!passwordField.getText().isEmpty()) {
                    currentUser.setPassword(passwordField.getText().trim());
                }
                
                boolean updated = userDAO.updateUser(currentUser);
                if (updated) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Your profile has been updated successfully.");
                    // Clear password fields after successful update
                    passwordField.clear();
                    confirmPasswordField.clear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile. Please try again.");
                }
            }
        });
        
        // Handle cancel button action
        cancelButton.setOnAction(e -> {
            // Reset fields to original values
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            passwordField.clear();
            confirmPasswordField.clear();
        });
        
        // Add components to container
        container.getChildren().addAll(header, roleBox, formGrid, buttonBox);
        
        return container;
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }
    
    private boolean validateForm(TextField nameField, PasswordField passwordField, 
                               PasswordField confirmPasswordField, TextField emailField, TextField phoneField) {
        StringBuilder errorMessage = new StringBuilder();
        
        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Name cannot be empty.\n");
        }
        
        if (!passwordField.getText().isEmpty() && !passwordField.getText().equals(confirmPasswordField.getText())) {
            errorMessage.append("Passwords do not match.\n");
        }
        
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errorMessage.append("Email cannot be empty.\n");
        } else if (!email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorMessage.append("Please enter a valid email address.\n");
        }
        
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^[0-9\\-\\+\\s\\(\\)]{10,15}$")) {
            errorMessage.append("Please enter a valid phone number.\n");
        }
        
        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
package org.app.dlms.FrontEnd.Views.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.app.dlms.Backend.Dao.UserDAO;
import org.app.dlms.Backend.Model.Admin;
import org.app.dlms.Backend.Model.Librarian;
import org.app.dlms.Backend.Model.Member;
import org.app.dlms.Backend.Model.User;
import org.app.dlms.Middleware.Enums.UserRole;
import org.app.dlms.Middleware.Enums.MembershipType;

/**
 * Component for adding a new user to the system
 */
public class AddUserForm {

    private final UserDAO userDAO;

    public AddUserForm() {
        userDAO = new UserDAO();
    }

    public Node createAddUserForm() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Form header
        Text header = new Text("Add New User");
        header.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        header.setFill(Color.web("#303f9f"));

        // Form container
        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        // Username field
        Label usernameLabel = new Label("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);

        // Password field
        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);

        // Confirm password field
        Label confirmPasswordLabel = new Label("Confirm Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");
        formGrid.add(confirmPasswordLabel, 0, 2);
        formGrid.add(confirmPasswordField, 1, 2);

        // Full name field
        Label nameLabel = new Label("Full Name");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter full name");
        formGrid.add(nameLabel, 0, 3);
        formGrid.add(nameField, 1, 3);

        // Email field
        Label emailLabel = new Label("Email");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");
        formGrid.add(emailLabel, 0, 4);
        formGrid.add(emailField, 1, 4);

        // Gender selection
        Label genderLabel = new Label("Gender");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setPromptText("Select gender");
        formGrid.add(genderLabel, 0, 5);
        formGrid.add(genderComboBox, 1, 5);

        // Phone field
        Label phoneLabel = new Label("Phone");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");
        formGrid.add(phoneLabel, 0, 6);
        formGrid.add(phoneField, 1, 6);

        // Address field
        Label addressLabel = new Label("Address");
        TextArea addressArea = new TextArea();
        addressArea.setPromptText("Enter address");
        addressArea.setPrefRowCount(3);
        formGrid.add(addressLabel, 0, 7);
        formGrid.add(addressArea, 1, 7);

        // User role selection
        Label roleLabel = new Label("User Role");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Member", "Librarian", "Admin");
        roleComboBox.setValue("Member"); // Default role
        formGrid.add(roleLabel, 0, 8);
        formGrid.add(roleComboBox, 1, 8);

        // Membership type selection (only for Members)
        Label membershipTypeLabel = new Label("Membership Type");
        ComboBox<MembershipType> membershipTypeComboBox = new ComboBox<>();
        membershipTypeComboBox.getItems().addAll(MembershipType.values());
        membershipTypeComboBox.setValue(MembershipType.Bronze); // Default membership type
        formGrid.add(membershipTypeLabel, 0, 9);
        formGrid.add(membershipTypeComboBox, 1, 9);

        // Membership fee display
        Label membershipFeeLabel = new Label("Membership Fee");
        Label membershipFeeValue = new Label("$50.00"); // Default fee for Bronze
        formGrid.add(membershipFeeLabel, 0, 10);
        formGrid.add(membershipFeeValue, 1, 10);

        // Only show membership options for Member role
        membershipTypeLabel.setVisible(true);
        membershipTypeComboBox.setVisible(true);
        membershipFeeLabel.setVisible(true);
        membershipFeeValue.setVisible(true);

        // Update membership fee when membership type changes
        membershipTypeComboBox.setOnAction(event -> {
            MembershipType selectedType = membershipTypeComboBox.getValue();
            double fee = 0.0;

            switch (selectedType) {
                case Bronze:
                    fee = 50.00;
                    break;
                case Silver:
                    fee = 100.00;
                    break;
                case Gold:
                    fee = 200.00;
                    break;
                case Platinum:
                    fee = 350.00;
                    break;
            }

            membershipFeeValue.setText("$" + fee);
        });

        // Show/hide membership options based on selected role
        roleComboBox.setOnAction(event -> {
            boolean isMember = "Member".equals(roleComboBox.getValue());
            membershipTypeLabel.setVisible(isMember);
            membershipTypeComboBox.setVisible(isMember);
            membershipFeeLabel.setVisible(isMember);
            membershipFeeValue.setVisible(isMember);
        });

        // Error message display
        Text errorMessage = new Text();
        errorMessage.setFill(Color.RED);
        errorMessage.setVisible(false);

        // Button controls
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #e0e0e0;");

        Button saveButton = new Button("Save User");
        saveButton.setStyle("-fx-background-color: #303f9f; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        // Add elements to container
        container.getChildren().addAll(header, formGrid, errorMessage, buttonBox);

        // Event handling for save button
        saveButton.setOnAction(event -> {
            // Clear previous error
            errorMessage.setVisible(false);

            // Validate form
            if (!validateForm(usernameField, passwordField, confirmPasswordField, nameField,
                    emailField, genderComboBox, phoneField, addressArea, errorMessage)) {
                return;
            }

            // Create user object based on selected role
            User newUser = createUserFromForm(
                    usernameField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    genderComboBox.getValue(),
                    addressArea.getText(),
                    phoneField.getText(),
                    roleComboBox.getValue(),
                    membershipTypeComboBox.getValue()
            );

            // Save user to database
            int userId = userDAO.addUser(newUser);

            if (userId > 0) {
                // Show success message
                String successMessage = "User created successfully!";

                // Add membership details if it's a member
                if (newUser instanceof Member) {
                    Member member = (Member) newUser;
                    successMessage += "\nMembership Type: " + member.getMembershipType() +
                            "\nMembership Fee: $" + member.getMembershipFee();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText(successMessage);
                alert.showAndWait();

                // Clear form
                clearForm(usernameField, passwordField, confirmPasswordField, nameField,
                        emailField, genderComboBox, phoneField, addressArea);

            } else {
                // Show error
                errorMessage.setText("Failed to create user. Please try again.");
                errorMessage.setVisible(true);
            }
        });

        // Event handling for cancel button
        cancelButton.setOnAction(event -> {
            clearForm(usernameField, passwordField, confirmPasswordField, nameField,
                    emailField, genderComboBox, phoneField, addressArea);
        });

        return container;
    }

    /**
     * Create appropriate user object based on selected role
     */
    private User createUserFromForm(String username, String password, String name, String email,
                                    String gender, String address, String phone, String role,
                                    MembershipType membershipType) {

        switch (role) {
            case "Admin":
                return new Admin(0, username, password, name, email, gender, address, phone);
            case "Librarian":
                return new Librarian(0, username, password, name, email, gender, address, phone);
            default:
                return new Member(0, username, password, name, email, gender, address, phone, membershipType);
        }
    }

    /**
     * Validate form inputs
     */
    private boolean validateForm(TextField usernameField, PasswordField passwordField,
                                 PasswordField confirmPasswordField, TextField nameField,
                                 TextField emailField, ComboBox<String> genderComboBox,
                                 TextField phoneField, TextArea addressArea, Text errorMessage) {

        // Check for empty fields
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty() || nameField.getText().isEmpty() ||
                emailField.getText().isEmpty() || genderComboBox.getValue() == null ||
                phoneField.getText().isEmpty() || addressArea.getText().isEmpty()) {

            errorMessage.setText("Please fill in all fields");
            errorMessage.setVisible(true);
            return false;
        }

        // Check if passwords match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorMessage.setText("Passwords do not match");
            errorMessage.setVisible(true);
            return false;
        }

        // Basic email validation
        if (!emailField.getText().contains("@") || !emailField.getText().contains(".")) {
            errorMessage.setText("Please enter a valid email address");
            errorMessage.setVisible(true);
            return false;
        }

        return true;
    }

    /**
     * Clear all form fields
     */
    private void clearForm(TextField usernameField, PasswordField passwordField,
                           PasswordField confirmPasswordField, TextField nameField,
                           TextField emailField, ComboBox<String> genderComboBox,
                           TextField phoneField, TextArea addressArea) {

        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        nameField.clear();
        emailField.clear();
        genderComboBox.setValue(null);
        phoneField.clear();
        addressArea.clear();
    }
}
package org.app.dlms.FrontEnd.Views.Components;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Class containing all dashboard components
 */
public class DashboardComponents {
    private ContentArea contentArea;
    private User currentUser;

    // Components for each section
    private Node dashboardComponent;
    private Node usersComponent;
    private Node booksComponent;
    private Node borrowedBooksComponent;
    private Node paymentsComponent;

    // Add User Form component
    private AddUserForm addUserForm;
    private boolean isAddUserFormVisible = false;

    public DashboardComponents(ContentArea contentArea,User user) {
        this.contentArea = contentArea;
        this.addUserForm = new AddUserForm(user);
        this.currentUser = user;
        initializeComponents();
    }

    private void initializeComponents() {
        // Initialize all components
        dashboardComponent = createDashboardComponent();

        usersComponent = createUsersComponent();
        BookInventoryComponent bookComponent = new BookInventoryComponent(currentUser);
        BorrowedBooksComponent borrowedBookComponent = new BorrowedBooksComponent(currentUser);
        booksComponent = bookComponent.createBooksComponent();
//        booksComponent = createBooksComponent();
        borrowedBooksComponent = borrowedBookComponent.createBorrowedBooksComponent();
        paymentsComponent = createPaymentsComponent();
    }

    private Node createDashboardComponent() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Dashboard header
        Text header = new Text("Dashboard Overview");
        header.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        header.setFill(Color.web("#303f9f"));

        // Stats summary
        HBox statsBox = new HBox(20);
        statsBox.setPrefHeight(120);

        // Create stat cards
        StackPane totalUsers = createStatCard("Total Users", "256", "👥");
        StackPane totalBooks = createStatCard("Total Books", "1,458", "📚");
        StackPane activeLoans = createStatCard("Active Loans", "87", "📋");
        StackPane monthlyRevenue = createStatCard("Monthly Revenue", "$1,250", "💰");

        statsBox.getChildren().addAll(totalUsers, totalBooks, activeLoans, monthlyRevenue);
        HBox.setHgrow(totalUsers, Priority.ALWAYS);
        HBox.setHgrow(totalBooks, Priority.ALWAYS);
        HBox.setHgrow(activeLoans, Priority.ALWAYS);
        HBox.setHgrow(monthlyRevenue, Priority.ALWAYS);

        // Charts section
        HBox chartsBox = new HBox(20);
        chartsBox.setPrefHeight(300);

        // Line chart for loans over time
        NumberAxis xAxis = new NumberAxis(1, 12, 1);
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Month");
        yAxis.setLabel("Number of Loans");

        LineChart<Number, Number> loanChart = new LineChart<>(xAxis, yAxis);
        loanChart.setTitle("Monthly Loans");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("2024");

        series.getData().add(new XYChart.Data<>(1, 45));
        series.getData().add(new XYChart.Data<>(2, 52));
        series.getData().add(new XYChart.Data<>(3, 60));
        series.getData().add(new XYChart.Data<>(4, 75));
        series.getData().add(new XYChart.Data<>(5, 68));
        series.getData().add(new XYChart.Data<>(6, 72));

        loanChart.getData().add(series);

        // Pie chart for book categories
        PieChart bookCategories = new PieChart();
        bookCategories.setTitle("Book Categories");

        bookCategories.getData().add(new PieChart.Data("Fiction", 35));
        bookCategories.getData().add(new PieChart.Data("Science", 25));
        bookCategories.getData().add(new PieChart.Data("History", 15));
        bookCategories.getData().add(new PieChart.Data("Technology", 20));
        bookCategories.getData().add(new PieChart.Data("Other", 5));

        VBox chartContainer1 = new VBox(loanChart);
        VBox chartContainer2 = new VBox(bookCategories);

        chartsBox.getChildren().addAll(chartContainer1, chartContainer2);
        HBox.setHgrow(chartContainer1, Priority.ALWAYS);
        HBox.setHgrow(chartContainer2, Priority.ALWAYS);

        // Recent activities section
        VBox activitiesBox = new VBox(10);
        Text activitiesHeader = new Text("Recent Activities");
        activitiesHeader.setFont(Font.font("Montserrat", FontWeight.BOLD, 18));

        VBox activitiesList = new VBox(5);
        activitiesList.getChildren().addAll(
                createActivityItem("John Smith borrowed 'The Great Gatsby'", "10 minutes ago"),
                createActivityItem("New user registered: Emma Wilson", "45 minutes ago"),
                createActivityItem("Book returned: 'To Kill a Mockingbird'", "2 hours ago"),
                createActivityItem("Late fee payment received: $12.50", "5 hours ago")
        );

        activitiesBox.getChildren().addAll(activitiesHeader, activitiesList);

        // Add all components to container
        container.getChildren().addAll(header, statsBox, chartsBox, activitiesBox);

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        return scrollPane;
    }

    private StackPane createStatCard(String title, String value, String icon) {
        StackPane card = new StackPane();
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5); -fx-background-radius: 8;");

        VBox content = new VBox(5);
        content.setPadding(new Insets(15));
        content.setAlignment(Pos.CENTER_LEFT);

        Text iconText = new Text(icon);
        iconText.setFont(Font.font("Arial", 24));

        Text valueText = new Text(value);
        valueText.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        valueText.setFill(Color.web("#303f9f"));

        Text titleText = new Text(title);
        titleText.setFont(Font.font("Montserrat", FontWeight.NORMAL, 14));
        titleText.setFill(Color.web("#757575"));

        content.getChildren().addAll(iconText, valueText, titleText);
        card.getChildren().add(content);

        return card;
    }

    private HBox createActivityItem(String activity, String time) {
        HBox item = new HBox();
        item.setPadding(new Insets(10));
        item.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 3, 0, 0, 3);");

        VBox content = new VBox(5);
        HBox.setHgrow(content, Priority.ALWAYS);

        Text activityText = new Text(activity);
        activityText.setFont(Font.font("Montserrat", FontWeight.NORMAL, 14));

        Text timeText = new Text(time);
        timeText.setFont(Font.font("Montserrat", FontWeight.NORMAL, 12));
        timeText.setFill(Color.web("#9e9e9e"));

        content.getChildren().addAll(activityText, timeText);
        item.getChildren().add(content);

        return item;
    }

    private Node createUsersComponent() {
        BorderPane mainContainer = new BorderPane();

        // Create main users view
        VBox usersView = new VBox(20);
        usersView.setPadding(new Insets(20));

        // Users header
        Text header = new Text("Users Management");
        header.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        header.setFill(Color.web("#303f9f"));

        // Search and actions bar
        HBox actionsBar = new HBox(10);
        actionsBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(300);

        Button addUserBtn = new Button("Add New User");
        addUserBtn.setStyle("-fx-background-color: #303f9f; -fx-text-fill: white;");

        actionsBar.getChildren().addAll(searchField, addUserBtn);

        // Create a typed TableView for User objects
        TableView<User> usersTable = new TableView<>();
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create properly typed columns
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<User, UserRole> roleCol = new TableColumn<>("User Type");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        // For membership type, assuming you have a method to get membership type from the user
        TableColumn<User, String> membershipCol = new TableColumn<>("Membership Type");
        membershipCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String membershipType = "N/A";
            // This would depend on your actual implementation
            // For example, you might check if the user is a member and query a membership table
            if (user.getRole() == UserRole.Member) {
                // You might need to call a method from your MembershipDAO here
                // membershipType = membershipDao.getMembershipTypeForUser(user.getId());
                membershipType = "Standard"; // Default fallback
            }
            return new SimpleStringProperty(membershipType);
        });

        // Actions column with edit and delete buttons
        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox actionButtons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    // Handle edit action
                    System.out.println("Edit user: " + user.getId());
                    // Show edit form
//                    showEditUserForm(mainContainer, usersView, user);
                });

                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    // Show confirmation dialog
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete User");
                    alert.setHeaderText("Delete User: " + user.getName());
                    alert.setContentText("Are you sure you want to delete this user?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // User confirmed, delete the user
                        UserDAO userDAO = new UserDAO();
                        boolean deleted = userDAO.deleteUser(user.getId());
                        if (deleted) {
                            // Refresh the table
                            loadUsers(usersTable);
                        } else {
                            showErrorAlert("Failed to delete user.");
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });

        usersTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, roleCol, membershipCol, actionCol);
        VBox.setVgrow(usersTable, Priority.ALWAYS);

        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers(usersTable, newValue);
        });
        System.out.println(usersTable);
        // Populate the table with users
        loadUsers(usersTable);

        usersView.getChildren().addAll(header, actionsBar, usersTable);

        // Set the main users view as the default center content
        mainContainer.setCenter(usersView);

        // Add user button event handler
        addUserBtn.setOnAction(e -> {
            if (!isAddUserFormVisible) {
                // If add form is not visible, show it
                Node addUserFormNode = addUserForm.createAddUserForm();
                mainContainer.setCenter(addUserFormNode);
                isAddUserFormVisible = true;

                // Create a back button
                Button backButton = new Button("← Back to Users List");
                backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #303f9f; -fx-font-weight: bold;");
                backButton.setOnAction(backEvent -> {
                    mainContainer.setCenter(usersView);
                    isAddUserFormVisible = false;
                    mainContainer.setTop(null);
                    // Refresh the table after returning to it
                    loadUsers(usersTable);
                });

                HBox backButtonContainer = new HBox(backButton);
                backButtonContainer.setPadding(new Insets(10, 0, 0, 20));
                mainContainer.setTop(backButtonContainer);
            }
        });

        return mainContainer;
    }

    // Method to load users from UserDAO
    private void loadUsers(TableView<User> usersTable) {
        // Clear existing items
        usersTable.getItems().clear();

        // Create UserDAO instance
        UserDAO userDAO = new UserDAO();

        // Fetch users from database through DAO
        List<User> users = userDAO.getAllUsers();

        // Add users to the table
        usersTable.getItems().addAll(users);
    }

    // Method to filter users based on search text
    private void filterUsers(TableView<User> usersTable, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            // If search text is empty, reload all users
            loadUsers(usersTable);
            return;
        }

        // Create UserDAO instance
        UserDAO userDAO = new UserDAO();

        // Use UserDAO to search for users
        // This assumes you have a searchUsers method in your UserDAO
        List<User> filteredUsers = userDAO.searchUsers(searchText);

        // Update table with filtered users
        usersTable.getItems().clear();
        usersTable.getItems().addAll(filteredUsers);
    }

    // Helper method to show error alerts
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

//     Method to show edit user form
 /*   private void showEditUserForm(BorderPane mainContainer, Node usersView, User user) {
        // This assumes you have an EditUserForm class or similar
        // You could also modify your AddUserForm to accept a User object for editing
        Node editUserFormNode = editUserForm.createEditUserForm(user);
        mainContainer.setCenter(editUserFormNode);

        // Create a back button
        Button backButton = new Button("← Back to Users List");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #303f9f; -fx-font-weight: bold;");
        backButton.setOnAction(event -> {
            mainContainer.setCenter(usersView);
            mainContainer.setTop(null);
            // Refresh the table after returning to it
            loadUsers((TableView<User>) ((VBox) usersView).getChildren().get(2));
        });

        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setPadding(new Insets(10, 0, 0, 20));
        mainContainer.setTop(backButtonContainer);
    }*/


    private Node createBorrowedBooksComponent() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Borrowed books header
        Text header = new Text("Borrowed Books");
        header.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        header.setFill(Color.web("#303f9f"));

        // Filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by user or book...");
        searchField.setPrefWidth(300);

        Button allBtn = new Button("All");
        allBtn.setStyle("-fx-background-color: #303f9f; -fx-text-fill: white;");

        Button activeBtn = new Button("Active");
        activeBtn.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white;");

        Button overdueBtn = new Button("Overdue");
        overdueBtn.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white;");

        Button recordBtn = new Button("Issue Book");
        recordBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");

        filterBar.getChildren().addAll(searchField, allBtn, activeBtn, overdueBtn, recordBtn);

        // Borrowing records table
        TableView<Object> borrowingTable = new TableView<>();
        borrowingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Object, String> idCol = new TableColumn<>("ID");
        TableColumn<Object, String> userCol = new TableColumn<>("User");
        TableColumn<Object, String> bookCol = new TableColumn<>("Book");
        TableColumn<Object, String> borrowDateCol = new TableColumn<>("Borrow Date");
        TableColumn<Object, String> dueDateCol = new TableColumn<>("Due Date");
        TableColumn<Object, String> statusCol = new TableColumn<>("Status");
        TableColumn<Object, String> actionCol = new TableColumn<>("Actions");

        borrowingTable.getColumns().addAll(idCol, userCol, bookCol, borrowDateCol, dueDateCol, statusCol, actionCol);
        VBox.setVgrow(borrowingTable, Priority.ALWAYS);

        container.getChildren().addAll(header, filterBar, borrowingTable);

        return container;
    }

    private Node createPaymentsComponent() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Payments header
        Text header = new Text("Payments & Fines");
        header.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        header.setFill(Color.web("#303f9f"));

        // Stats summary
        HBox statsBox = new HBox(20);
        statsBox.setPrefHeight(100);

        // Create stat cards
        StackPane totalCollected = createStatCard("Total Collected", "$2,450", "💰");
        StackPane pendingPayments = createStatCard("Pending", "$350", "⏳");
        StackPane overdueAmounts = createStatCard("Overdue Fines", "$175", "⚠️");

        statsBox.getChildren().addAll(totalCollected, pendingPayments, overdueAmounts);
        HBox.setHgrow(totalCollected, Priority.ALWAYS);
        HBox.setHgrow(pendingPayments, Priority.ALWAYS);
        HBox.setHgrow(overdueAmounts, Priority.ALWAYS);

        // Search and actions bar
        HBox actionsBar = new HBox(10);
        actionsBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search payments...");
        searchField.setPrefWidth(300);

        Button recordPaymentBtn = new Button("Record Payment");
        recordPaymentBtn.setStyle("-fx-background-color: #303f9f; -fx-text-fill: white;");

        Button generateReportBtn = new Button("Generate Report");
        generateReportBtn.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white;");

        actionsBar.getChildren().addAll(searchField, recordPaymentBtn, generateReportBtn);

        // Payments table
        TableView<Object> paymentsTable = new TableView<>();
        paymentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Object, String> idCol = new TableColumn<>("ID");
        TableColumn<Object, String> userCol = new TableColumn<>("User");
        TableColumn<Object, String> amountCol = new TableColumn<>("Amount");
        TableColumn<Object, String> typeCol = new TableColumn<>("Type");
        TableColumn<Object, String> dateCol = new TableColumn<>("Date");
        TableColumn<Object, String> statusCol = new TableColumn<>("Status");
        TableColumn<Object, String> actionCol = new TableColumn<>("Actions");

        paymentsTable.getColumns().addAll(idCol, userCol, amountCol, typeCol, dateCol, statusCol, actionCol);
        VBox.setVgrow(paymentsTable, Priority.ALWAYS);

        container.getChildren().addAll(header, statsBox, actionsBar, paymentsTable);

        return container;
    }

    // Getter methods for each component
    public Node getDashboardComponent() {
        return dashboardComponent;
    }

    public Node getUsersComponent() {
        return usersComponent;
    }

    public Node getBooksComponent() {
        return booksComponent;
    }

    public Node getBorrowedBooksComponent() {
        return borrowedBooksComponent;
    }

    public Node getPaymentsComponent() {
        return paymentsComponent;
    }
}
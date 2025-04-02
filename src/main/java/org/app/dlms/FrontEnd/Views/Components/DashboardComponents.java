package org.app.dlms.FrontEnd.Views.Components;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.app.dlms.Middleware.Services.ComponentService;
import org.app.dlms.Backend.Dao.PaymentDAO;
import org.app.dlms.Backend.Dao.FineDAO;
import org.app.dlms.Backend.Model.Payment;
import org.app.dlms.Backend.Model.Fine;
import org.app.dlms.Backend.Model.PaymentViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

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
    private ComponentService componentService;

    // Add User Form component
    private AddUserForm addUserForm;
    private boolean isAddUserFormVisible = false;

    public DashboardComponents(ContentArea contentArea, User user) {
        componentService = new ComponentService();
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
        StackPane totalUsers = componentService.createStatCard("Total Users", "256", "👥");
        StackPane totalBooks = componentService.createStatCard("Total Books", "1,458", "📚");
        StackPane activeLoans = componentService.createStatCard("Active Loans", "87", "📋");
        StackPane monthlyRevenue = componentService.createStatCard("Monthly Revenue", "$1,250", "💰");

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

        // For membership type
        TableColumn<User, String> membershipCol = new TableColumn<>("Membership Type");
        membershipCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String membershipType = "N/A";
            // check if the user is a member and query a membership table
            if (user.getRole() == UserRole.Member) {
                Member member= (Member) user;
                 membershipType = String.valueOf(member.getMembershipType());
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
                    showEditUserForm(mainContainer, usersView, user);
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


    private void showEditUserForm(BorderPane mainContainer, VBox usersView, User user) {
        // Create an edit user form
        AddUserForm editUserForm = new AddUserForm(currentUser, user);
        Node editFormNode = editUserForm.createAddUserForm();

        // Create a back button
        Button backButton = new Button("← Back to Users List");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #303f9f; -fx-font-weight: bold;");
        backButton.setOnAction(backEvent -> {
            mainContainer.setCenter(usersView);
            mainContainer.setTop(null);
            // Refresh the table after returning to it
//            loadUsers(usersTable);
        });

        // Add the back button to the top of the form
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setPadding(new Insets(10, 0, 0, 20));

        // Update the container
        mainContainer.setTop(backButtonContainer);
        mainContainer.setCenter(editFormNode);
    }

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
        // Initialize DAOs
        PaymentDAO paymentDAO = new PaymentDAO();
        FineDAO fineDAO = new FineDAO();
        UserDAO userDAO = new UserDAO();
        
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Payments header
        Text header = new Text("Payments & Fines");
        header.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        header.setFill(Color.web("#303f9f"));

        // Stats summary
        HBox statsBox = new HBox(20);
        statsBox.setPrefHeight(100);

        // Create the stat cards with real data
        double subscriptionTotal = 0.0;
        double fineTotal = 0.0;
        double pendingFines = 0.0;
        
        // Fetch actual data if current user is a member
        if (currentUser.getRole() == UserRole.Member) {
            // For a member, only show their own payment stats
            subscriptionTotal = paymentDAO.getTotalPaymentsByMemberId(currentUser.getId());
            pendingFines = fineDAO.getTotalUnpaidFinesByMemberId(currentUser.getId());
        } else {
            // For admin/librarian, show all payment stats
            // Implement this with aggregation queries in DAO classes
            // For now, we'll use placeholder data
            subscriptionTotal = 2450.0;
            fineTotal = 350.0;
            pendingFines = 175.0;
        }

        // Create stat cards with actual data
        StackPane subscriptionTotalCard = componentService.createStatCard(
            "Subscription Total", 
            "$" + String.format("%.2f", subscriptionTotal), 
            "💳"
        );
        
        StackPane finesTotalCard = componentService.createStatCard(
            "Fines Total", 
            "$" + String.format("%.2f", fineTotal), 
            "💰"
        );
        
        StackPane pendingFinesCard = componentService.createStatCard(
            "Pending Fines", 
            "$" + String.format("%.2f", pendingFines), 
            "⚠️"
        );

        statsBox.getChildren().addAll(subscriptionTotalCard, finesTotalCard, pendingFinesCard);
        HBox.setHgrow(subscriptionTotalCard, Priority.ALWAYS);
        HBox.setHgrow(finesTotalCard, Priority.ALWAYS);
        HBox.setHgrow(pendingFinesCard, Priority.ALWAYS);

        // Search and filter bar
        HBox actionsBar = new HBox(10);
        actionsBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search payments...");
        searchField.setPrefWidth(200);

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("All", "Subscriptions", "Fines");
        typeFilter.setValue("All");
        typeFilter.setPrefWidth(150);

        Button recordPaymentBtn = new Button("Record Payment");
        recordPaymentBtn.setStyle("-fx-background-color: #303f9f; -fx-text-fill: white;");

        Button generateReportBtn = new Button("Generate Report");
        generateReportBtn.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white;");

        actionsBar.getChildren().addAll(searchField, typeFilter, recordPaymentBtn, generateReportBtn);

        // Create a more specific, typed table for payments
        TableView<PaymentViewModel> paymentsTable = new TableView<>();
        paymentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PaymentViewModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<PaymentViewModel, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        
        TableColumn<PaymentViewModel, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(column -> new TableCell<PaymentViewModel, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (amount == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });
        
        TableColumn<PaymentViewModel, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        TableColumn<PaymentViewModel, Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(column -> new TableCell<PaymentViewModel, Date>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (date == null || empty) {
                    setText(null);
                } else {
                    setText(new java.text.SimpleDateFormat("MM/dd/yyyy").format(date));
                }
            }
        });
        
        TableColumn<PaymentViewModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(column -> new TableCell<PaymentViewModel, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (status == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Paid":
                            setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: #2e7d32;");
                            break;
                        case "Pending":
                            setStyle("-fx-background-color: #fff9c4; -fx-text-fill: #ff6f00;");
                            break;
                        case "Overdue":
                            setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #b71c1c;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        TableColumn<PaymentViewModel, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Add payment action buttons 
        TableColumn<PaymentViewModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(column -> new TableCell<PaymentViewModel, Void>() {
            private final Button viewBtn = new Button("View");
            private final Button payBtn = new Button("Pay");
            
            {
                viewBtn.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white; -fx-font-size: 10px;");
                payBtn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-size: 10px;");
                
                viewBtn.setOnAction(event -> {
                    PaymentViewModel payment = getTableView().getItems().get(getIndex());
                    showPaymentDetails(payment);
                });
                
                payBtn.setOnAction(event -> {
                    PaymentViewModel payment = getTableView().getItems().get(getIndex());
                    if ("Fine".equals(payment.getType()) && "Pending".equals(payment.getStatus())) {
                        processFinePayment(payment);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PaymentViewModel payment = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);
                    buttons.getChildren().add(viewBtn);
                    
                    // Only show Pay button for unpaid fines
                    if ("Fine".equals(payment.getType()) && "Pending".equals(payment.getStatus())) {
                        buttons.getChildren().add(payBtn);
                    }
                    
                    setGraphic(buttons);
                }
            }
        });

        paymentsTable.getColumns().addAll(idCol, userCol, amountCol, typeCol, dateCol, statusCol, descriptionCol, actionCol);
        VBox.setVgrow(paymentsTable, Priority.ALWAYS);
        
        // Load initial data
        loadPaymentsData(paymentsTable, userDAO, paymentDAO, fineDAO);
        
        // Add event handlers
        searchField.textProperty().addListener((obs, oldVal, newVal) -> 
            filterPayments(paymentsTable, newVal, typeFilter.getValue()));
            
        typeFilter.setOnAction(e -> {
            String selectedType = typeFilter.getValue();
            System.out.println("Type filter changed to: " + selectedType);
            // Reload data to ensure we have the complete set of payments
            loadPaymentsData(paymentsTable, userDAO, paymentDAO, fineDAO);
            // Then apply the filter
            filterPayments(paymentsTable, searchField.getText(), selectedType);
        });
            
        recordPaymentBtn.setOnAction(e -> 
            showRecordPaymentDialog(paymentsTable, userDAO, paymentDAO, fineDAO));

        container.getChildren().addAll(header, statsBox, actionsBar, paymentsTable);
        
        return container;
    }
    
    private void loadPaymentsData(TableView<PaymentViewModel> paymentsTable, UserDAO userDAO, 
                                 PaymentDAO paymentDAO, FineDAO fineDAO) {
        List<PaymentViewModel> paymentViewModels = new ArrayList<>();
        
        // Load payment data based on user role
        if (currentUser.getRole() == UserRole.Member) {
            // For members, only show their own payments
            int memberId = currentUser.getId();
            
            // Load subscription payments
            List<Payment> payments = paymentDAO.getPaymentsByMemberId(memberId);
            for (Payment payment : payments) {
                User user = userDAO.getUserById(memberId);
                paymentViewModels.add(new PaymentViewModel(
                    payment.getId(),
                    user.getName(),
                    payment.getAmount(),
                    "Subscription",
                    payment.getPaymentDate(),
                    "Paid",
                    "Membership payment",
                    0
                ));
            }
            
            // Load fines
            List<Fine> fines = fineDAO.getFinesByMemberId(memberId);
            for (Fine fine : fines) {
                User user = userDAO.getUserById(memberId);
                paymentViewModels.add(new PaymentViewModel(
                    fine.getId(),
                    user.getName(),
                    fine.getAmount(),
                    "Fine",
                    new Date(), // We don't have a date in the Fine model, so use current date
                    fine.isPaid() ? "Paid" : "Pending",
                    "Fine for book return delay",
                    fine.getBorrowRecordId()
                ));
            }
        } else {
            // For admin/librarian, show all payments
            // For demo purposes, let's add some sample data
            // In a real implementation, you would fetch this from the database
            
            // Sample subscription payments
            paymentViewModels.add(new PaymentViewModel(
                1, "John Doe", 50.00, "Subscription", new Date(), "Paid", 
                "Annual membership", 0
            ));
            
            paymentViewModels.add(new PaymentViewModel(
                2, "Jane Smith", 25.00, "Subscription", new Date(), "Paid", 
                "Monthly membership", 0
            ));
            
            // Sample fines
            paymentViewModels.add(new PaymentViewModel(
                101, "John Doe", 10.00, "Fine", new Date(), "Pending", 
                "Late return fine - 5 days overdue", 201
            ));
            
            paymentViewModels.add(new PaymentViewModel(
                102, "Jane Smith", 15.00, "Fine", new Date(), "Paid", 
                "Late return fine - 7 days overdue", 202
            ));
        }
        
        paymentsTable.setItems(FXCollections.observableArrayList(paymentViewModels));
    }
    
    private void filterPayments(TableView<PaymentViewModel> paymentsTable, String searchText, String filterType) {
        // Get a reference to the original, unfiltered data
        ObservableList<PaymentViewModel> allPayments = paymentsTable.getItems();
        
        System.out.println("Filtering payments. Filter type: " + filterType + ", Search text: " + searchText);
        System.out.println("Total payments before filter: " + allPayments.size());
        
        // Create a filtered list
        List<PaymentViewModel> filteredList = new ArrayList<>();
        
        for (PaymentViewModel payment : allPayments) {
            // Apply type filter
            boolean matchesType = "All".equals(filterType) 
                || ("Subscriptions".equals(filterType) && "Subscription".equals(payment.getType()))
                || ("Fines".equals(filterType) && "Fine".equals(payment.getType()));
            
            // Apply text search
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                payment.getUserName().toLowerCase().contains(searchText.toLowerCase()) ||
                payment.getDescription().toLowerCase().contains(searchText.toLowerCase());
            
            if (matchesType && matchesSearch) {
                filteredList.add(payment);
                System.out.println("Added payment to filtered list: ID=" + payment.getId() + 
                    ", Type=" + payment.getType() + ", User=" + payment.getUserName());
            } else {
                System.out.println("Filtered out payment: ID=" + payment.getId() + 
                    ", Type=" + payment.getType() + ", matchesType=" + matchesType + 
                    ", matchesSearch=" + matchesSearch);
            }
        }
        
        System.out.println("Total payments after filter: " + filteredList.size());
        paymentsTable.setItems(FXCollections.observableArrayList(filteredList));
    }
    
    private void showPaymentDetails(PaymentViewModel payment) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Payment Details");
        dialog.setHeaderText("View Payment Information");
        
        // Create dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Payment details
        grid.add(new Label("ID:"), 0, 0);
        grid.add(new Label(String.valueOf(payment.getId())), 1, 0);
        
        grid.add(new Label("User:"), 0, 1);
        grid.add(new Label(payment.getUserName()), 1, 1);
        
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(new Label(String.format("$%.2f", payment.getAmount())), 1, 2);
        
        grid.add(new Label("Type:"), 0, 3);
        grid.add(new Label(payment.getType()), 1, 3);
        
        grid.add(new Label("Date:"), 0, 4);
        grid.add(new Label(new java.text.SimpleDateFormat("MM/dd/yyyy").format(payment.getDate())), 1, 4);
        
        grid.add(new Label("Status:"), 0, 5);
        grid.add(new Label(payment.getStatus()), 1, 5);
        
        grid.add(new Label("Description:"), 0, 6);
        grid.add(new Label(payment.getDescription()), 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }
    
    private void processFinePayment(PaymentViewModel fineViewModel) {
        FineDAO fineDAO = new FineDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        
        // Confirm payment
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Payment");
        alert.setHeaderText("Pay Fine");
        alert.setContentText("Are you sure you want to pay this fine of $" + 
                            String.format("%.2f", fineViewModel.getAmount()) + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Update fine status
            Fine fine = fineDAO.getFineById(fineViewModel.getId());
            if (fine != null) {
                // Mark fine as paid
                boolean updateSuccess = fineDAO.updateFinePaymentStatus(fine.getId(), true);
                
                // Record the payment
                if (updateSuccess) {
                    Payment finePayment = new Payment(
                        0, // ID will be generated by the database
                        fine.getMemberId(),
                        new Date(), // Current date
                        fine.getAmount(),
                        "Fine",
                        "Payment for fine ID: " + fine.getId(),
                        fine.getId()
                    );
                    
                    int paymentId = paymentDAO.addPayment(finePayment);
                    
                    if (paymentId > 0) {
                        showSuccessAlert("Fine payment successfully processed.");
                        
                        // Refresh the payment list
                        refreshPaymentsTable();
                    } else {
                        showErrorAlert("Failed to record the payment.");
                    }
                } else {
                    showErrorAlert("Failed to update fine status.");
                }
            } else {
                showErrorAlert("Fine not found.");
            }
        }
    }
    
    private void showRecordPaymentDialog(TableView<PaymentViewModel> paymentsTable, UserDAO userDAO, 
                                        PaymentDAO paymentDAO, FineDAO fineDAO) {
        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle("Record Payment");
        dialog.setHeaderText("Enter Payment Details");
        
        // Create dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Payment type selection
        ComboBox<String> paymentTypeCombo = new ComboBox<>();
        paymentTypeCombo.getItems().addAll("Subscription", "Fine");
        paymentTypeCombo.setValue("Subscription");
        
        // User selection
        ComboBox<User> userCombo = new ComboBox<>();
        userCombo.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? "" : user.getName());
            }
        });
        userCombo.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? "" : user.getName());
            }
        });
        
        // Populate user list - in a real app, you'd filter to just show members
        List<User> users = userDAO.getAllUsers();
        List<User> members = users.stream()
            .filter(u -> u.getRole() == UserRole.Member)
            .collect(Collectors.toList());
        userCombo.setItems(FXCollections.observableArrayList(members));
        
        // Amount field
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        
        // Description field
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter payment description");
        
        // For Fine payments, show a dropdown of unpaid fines
        ComboBox<Fine> fineCombo = new ComboBox<>();
        fineCombo.setVisible(false); // Initially hidden
        fineCombo.setCellFactory(lv -> new ListCell<Fine>() {
            @Override
            protected void updateItem(Fine fine, boolean empty) {
                super.updateItem(fine, empty);
                setText(empty ? "" : "Fine #" + fine.getId() + " - $" + fine.getAmount());
            }
        });
        
        // Show/hide fine selection based on payment type
        paymentTypeCombo.setOnAction(e -> {
            boolean isFine = "Fine".equals(paymentTypeCombo.getValue());
            fineCombo.setVisible(isFine);
            
            if (isFine && userCombo.getValue() != null) {
                // Load unpaid fines for selected user
                User selectedUser = userCombo.getValue();
                List<Fine> unpaidFines = fineDAO.getFinesByMemberId(selectedUser.getId()).stream()
                    .filter(fine -> !fine.isPaid())
                    .collect(Collectors.toList());
                fineCombo.setItems(FXCollections.observableArrayList(unpaidFines));
                
                // Update amount field if a fine is selected
                if (!unpaidFines.isEmpty()) {
                    fineCombo.setValue(unpaidFines.get(0));
                    amountField.setText(String.valueOf(unpaidFines.get(0).getAmount()));
                }
            }
        });
        
        // Update fine list when user selection changes
        userCombo.setOnAction(e -> {
            if ("Fine".equals(paymentTypeCombo.getValue()) && userCombo.getValue() != null) {
                User selectedUser = userCombo.getValue();
                List<Fine> unpaidFines = fineDAO.getFinesByMemberId(selectedUser.getId()).stream()
                    .filter(fine -> !fine.isPaid())
                    .collect(Collectors.toList());
                fineCombo.setItems(FXCollections.observableArrayList(unpaidFines));
            }
        });
        
        // When fine selection changes, update amount
        fineCombo.setOnAction(e -> {
            Fine selectedFine = fineCombo.getValue();
            if (selectedFine != null) {
                amountField.setText(String.valueOf(selectedFine.getAmount()));
            }
        });
        
        // Add fields to grid
        grid.add(new Label("Payment Type:"), 0, 0);
        grid.add(paymentTypeCombo, 1, 0);
        
        grid.add(new Label("User:"), 0, 1);
        grid.add(userCombo, 1, 1);
        
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);
        
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionField, 1, 3);
        
        grid.add(new Label("Select Fine:"), 0, 4);
        grid.add(fineCombo, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons
        ButtonType recordButtonType = new ButtonType("Record Payment", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(recordButtonType, ButtonType.CANCEL);
        
        // Enable/disable record button depending on whether amount is entered
        Node recordButton = dialog.getDialogPane().lookupButton(recordButtonType);
        recordButton.setDisable(true);
        
        // Validate amount field - must be a valid number > 0
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = false;
            try {
                double amount = Double.parseDouble(newValue);
                valid = amount > 0;
            } catch (NumberFormatException e) {
                valid = false;
            }
            recordButton.setDisable(!valid || userCombo.getValue() == null);
        });
        
        // Also validate user selection
        userCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean amountValid = false;
            try {
                if (!amountField.getText().isEmpty()) {
                    double amount = Double.parseDouble(amountField.getText());
                    amountValid = amount > 0;
                }
            } catch (NumberFormatException e) {
                amountValid = false;
            }
            recordButton.setDisable(!amountValid || newValue == null);
        });
        
        // Convert dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == recordButtonType) {
                try {
                    User selectedUser = userCombo.getValue();
                    double amount = Double.parseDouble(amountField.getText());
                    String paymentType = paymentTypeCombo.getValue();
                    String description = descriptionField.getText();
                    
                    Payment payment = new Payment(
                        0, // ID will be generated
                        selectedUser.getId(),
                        new Date(), // Current date
                        amount,
                        paymentType,
                        description,
                        "Fine".equals(paymentType) && fineCombo.getValue() != null 
                            ? fineCombo.getValue().getId() : 0
                    );
                    
                    return payment;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });
        
        // Process result
        Optional<Payment> result = dialog.showAndWait();
        result.ifPresent(payment -> {
            // Record the payment
            int paymentId = paymentDAO.addPayment(payment);
            
            if (paymentId > 0) {
                // If this is a fine payment, update the fine status
                if ("Fine".equals(payment.getType()) && payment.getRelatedRecordId() > 0) {
                    fineDAO.updateFinePaymentStatus(payment.getRelatedRecordId(), true);
                }
                
                showSuccessAlert("Payment successfully recorded.");
                
                // Refresh the payment table
                refreshPaymentsTable();
            } else {
                showErrorAlert("Failed to record payment.");
            }
        });
    }
    
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void refreshPaymentsTable() {
        // Recreate and reload the payments component
        paymentsComponent = createPaymentsComponent();
        
        // If the payments tab is currently active, update it
        if (contentArea.getCurrentComponent() == paymentsComponent) {
            contentArea.setContent(paymentsComponent);
        }
    }
    
    private void refreshPaymentsComponent() {
        paymentsComponent = createPaymentsComponent();
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
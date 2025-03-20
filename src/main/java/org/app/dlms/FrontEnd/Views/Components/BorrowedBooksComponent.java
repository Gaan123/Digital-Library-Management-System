package org.app.dlms.FrontEnd.Views.Components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.app.dlms.Backend.Dao.BookDAO;
import org.app.dlms.Backend.Dao.BorrowRecordDAO;
import org.app.dlms.Backend.Dao.UserDAO;
import org.app.dlms.Backend.Model.Book;
import org.app.dlms.Backend.Model.BorrowRecord;
import org.app.dlms.Backend.Model.User;
import org.app.dlms.Middleware.Enums.UserRole;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Component for managing borrowed books
 */
public class BorrowedBooksComponent {
    private final BorrowRecordDAO borrowRecordDAO;
    private final UserDAO userDAO;
    private final BookDAO bookDAO;
    private TableView<BorrowRecord> borrowingTable;
    
    // For the add/edit form
    private ComboBox<User> memberComboBox;
    private ComboBox<Book> bookComboBox;
    private DatePicker borrowDatePicker;
    private DatePicker dueDatePicker;
    private DatePicker returnDatePicker;
    
    private static final long OVERDUE_DAYS = 30;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // Class-level variable
    private Map<Integer, User> userCache = new HashMap<>();
    private Map<Integer, Book> bookCache = new HashMap<>();

    // Load data once when component is initialized
    private void preloadData() {
        try {
            List<User> allUsers = userDAO.getAllUsers();
            for (User user : allUsers) {
                userCache.put(user.getId(), user);
            }

            List<Book> allBooks = bookDAO.getAllBooks();
            for (Book book : allBooks) {
                bookCache.put(book.getId(), book);
            }
        } catch (Exception e) {
            System.err.println("Error preloading data: " + e.getMessage());
        }
    }
    public BorrowedBooksComponent() {
        this.borrowRecordDAO = new BorrowRecordDAO();
        this.userDAO = new UserDAO();
        this.bookDAO = new BookDAO();
        preloadData();
    }

    public Node createBorrowedBooksComponent() {
        BorderPane mainContainer = new BorderPane();

        // Create the main view
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

        Button issueBookBtn = new Button("Issue Book");
        issueBookBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");

        filterBar.getChildren().addAll(searchField, allBtn, activeBtn, overdueBtn, issueBookBtn);

        // Create the borrowing records table
        borrowingTable = new TableView<>();
        borrowingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // ID column
        TableColumn<BorrowRecord, Integer> idCol = new TableColumn<>("ID");
// If idCol is of type TableColumn<YourClass, Integer>
        idCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));        idCol.setPrefWidth(50);
        
        // Member column - show member name
        TableColumn<BorrowRecord, String> userCol = new TableColumn<>("Member");
        userCol.setCellValueFactory(cellData -> {
            int memberId = cellData.getValue().getMemberId();
            User member = userCache.get(memberId);
            return new SimpleStringProperty(member != null ? member.getName() : "Unknown");
        });
        userCol.setPrefWidth(150);
        
        // Book column - show book title
        TableColumn<BorrowRecord, String> bookCol = new TableColumn<>("Book");
        // Book column using cache
        bookCol.setCellValueFactory(cellData -> {
            int bookId = cellData.getValue().getBookId();
            Book book = bookCache.get(bookId);
            return new SimpleStringProperty(book != null ? book.getTitle() : "Unknown");
        });
        bookCol.setPrefWidth(200);
        
        // Borrow date column
        TableColumn<BorrowRecord, String> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatDate(cellData.getValue().getBorrowDate())));
        borrowDateCol.setPrefWidth(120);
        
        // Due date column
        TableColumn<BorrowRecord, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatDate(cellData.getValue().getDueDate())));
        dueDateCol.setPrefWidth(120);
        
        // Return date column
        TableColumn<BorrowRecord, String> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatDate(cellData.getValue().getReturnDate())));
        returnDateCol.setPrefWidth(120);
        
        // Status column
        TableColumn<BorrowRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            BorrowRecord record = cellData.getValue();
            String status;
            
            if (record.getReturnDate() != null) {
                status = "Returned";
            } else {
                Date dueDate = record.getDueDate();
                Date currentDate = new Date();
                
                if (currentDate.after(dueDate)) {
                    // Calculate days overdue
                    long daysOverdue = ChronoUnit.DAYS.between(
                        dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    );
                    
                    if (daysOverdue > OVERDUE_DAYS) {
                        status = "Severely Overdue (" + daysOverdue + " days)";
                    } else {
                        status = "Overdue (" + daysOverdue + " days)";
                    }
                } else {
                    status = "Active";
                }
            }
            
            return new SimpleStringProperty(status);
        });
        statusCol.setPrefWidth(150);
        
        // Style the status cells
        statusCol.setCellFactory(column -> new TableCell<BorrowRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    // Set background color based on status
                    if (item.contains("Severely Overdue")) {
                        setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #b71c1c;");
                    } else if (item.contains("Overdue")) {
                        setStyle("-fx-background-color: #fff9c4; -fx-text-fill: #ff6f00;");
                    } else if (item.equals("Returned")) {
                        setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: #2e7d32;");
                    } else {
                        setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #0d47a1;");
                    }
                }
            }
        });
        
        // Actions column with return, edit and delete buttons
        TableColumn<BorrowRecord, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button returnBtn = new Button("Return");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox actionButtons = new HBox(5);
            
            {
                returnBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
                editBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                
                returnBtn.setOnAction(event -> {
                    BorrowRecord record = getTableView().getItems().get(getIndex());
                    if (record.getReturnDate() == null) {
                        // Mark as returned today
                        record.setReturnDate(new Date());
                        boolean updated = borrowRecordDAO.updateBorrowRecord(record);
                        if (updated) {
                            refreshTable();
                        } else {
                            showErrorAlert("Failed to update return status.");
                        }
                    }
                });
                
                editBtn.setOnAction(event -> {
                    BorrowRecord record = getTableView().getItems().get(getIndex());
                    showEditForm(mainContainer, container, record);
                });
                
                deleteBtn.setOnAction(event -> {
                    BorrowRecord record = getTableView().getItems().get(getIndex());
                    
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Borrow Record");
                    alert.setHeaderText("Delete Record #" + record.getId());
                    alert.setContentText("Are you sure you want to delete this borrowing record?");
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        boolean deleted = borrowRecordDAO.deleteBorrowRecord(record.getId());
                        if (deleted) {
                            refreshTable();
                        } else {
                            showErrorAlert("Failed to delete borrowing record.");
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
                    BorrowRecord record = getTableView().getItems().get(getIndex());
                    actionButtons.getChildren().clear();
                    
                    if (record.getReturnDate() == null) {
                        actionButtons.getChildren().add(returnBtn);
                    }
                    
                    actionButtons.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(actionButtons);
                }
            }
        });
        actionCol.setPrefWidth(150);
        
        borrowingTable.getColumns().addAll(idCol, userCol, bookCol, borrowDateCol, dueDateCol, returnDateCol, statusCol, actionCol);
        VBox.setVgrow(borrowingTable, Priority.ALWAYS);
        
        // Populate the table
        refreshTable();
        
        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRecords(newValue);
        });
        
        // Add filter button functionality
        allBtn.setOnAction(e -> {
            updateButtonStyles(allBtn, activeBtn, overdueBtn);
            refreshTable();
        });
        
        activeBtn.setOnAction(e -> {
            updateButtonStyles(activeBtn, allBtn, overdueBtn);
            filterActiveRecords();
        });
        
        overdueBtn.setOnAction(e -> {
            updateButtonStyles(overdueBtn, allBtn, activeBtn);
            filterOverdueRecords();
        });
        
        // Add issue book button functionality
        issueBookBtn.setOnAction(e -> showAddForm(mainContainer, container));
        
        container.getChildren().addAll(header, filterBar, borrowingTable);
        mainContainer.setCenter(container);
        
        return mainContainer;
    }
    
    private void updateButtonStyles(Button activeButton, Button... inactiveButtons) {
        activeButton.setStyle("-fx-background-color: #303f9f; -fx-text-fill: white;");
        for (Button button : inactiveButtons) {
            button.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white;");
        }
    }
    
    private void refreshTable() {
        List<BorrowRecord> records = borrowRecordDAO.getAllBorrowRecords();
        borrowingTable.setItems(FXCollections.observableArrayList(records));
    }
    
    private void filterRecords(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            refreshTable();
            return;
        }
        
        List<BorrowRecord> allRecords = borrowRecordDAO.getAllBorrowRecords();
        List<BorrowRecord> filteredRecords = new ArrayList<>();
        
        for (BorrowRecord record : allRecords) {
            User member = userDAO.getUserById(record.getMemberId());
            Book book = bookDAO.getBookById(record.getBookId());
            
            String memberName = member != null ? member.getName().toLowerCase() : "";
            String bookTitle = book != null ? book.getTitle().toLowerCase() : "";
            
            if (memberName.contains(searchText.toLowerCase()) || 
                bookTitle.contains(searchText.toLowerCase())) {
                filteredRecords.add(record);
            }
        }
        
        borrowingTable.setItems(FXCollections.observableArrayList(filteredRecords));
    }
    
    private void filterActiveRecords() {
        List<BorrowRecord> allRecords = borrowRecordDAO.getAllBorrowRecords();
        List<BorrowRecord> activeRecords = allRecords.stream()
            .filter(record -> record.getReturnDate() == null)
            .collect(Collectors.toList());
        
        borrowingTable.setItems(FXCollections.observableArrayList(activeRecords));
    }
    
    private void filterOverdueRecords() {
        List<BorrowRecord> allRecords = borrowRecordDAO.getAllBorrowRecords();
        Date currentDate = new Date();
        
        List<BorrowRecord> overdueRecords = allRecords.stream()
            .filter(record -> record.getReturnDate() == null && currentDate.after(record.getDueDate()))
            .collect(Collectors.toList());
        
        borrowingTable.setItems(FXCollections.observableArrayList(overdueRecords));
    }
    
    private void showAddForm(BorderPane mainContainer, Node mainView) {
        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(20));
        
        Text formHeader = new Text("Issue Book");
        formHeader.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        formHeader.setFill(Color.web("#303f9f"));
        
        // Create form fields
        Label memberLabel = new Label("Member:");
        memberComboBox = new ComboBox<>();
        loadMembersIntoComboBox();
        memberComboBox.setMaxWidth(Double.MAX_VALUE);
        
        Label bookLabel = new Label("Book:");
        bookComboBox = new ComboBox<>();
        loadBooksIntoComboBox();
        bookComboBox.setMaxWidth(Double.MAX_VALUE);
        
        Label borrowDateLabel = new Label("Borrow Date:");
        borrowDatePicker = new DatePicker(LocalDate.now());
        borrowDatePicker.setMaxWidth(Double.MAX_VALUE);
        
        Label dueDateLabel = new Label("Due Date:");
        dueDatePicker = new DatePicker(LocalDate.now().plusDays(14)); // Default to 2 weeks
        dueDatePicker.setMaxWidth(Double.MAX_VALUE);
        
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.addRow(0, memberLabel, memberComboBox);
        formGrid.addRow(1, bookLabel, bookComboBox);
        formGrid.addRow(2, borrowDateLabel, borrowDatePicker);
        formGrid.addRow(3, dueDateLabel, dueDatePicker);
        
        formGrid.getColumnConstraints().addAll(
            new ColumnConstraints(100), // Label column
            new ColumnConstraints(300)  // Field column
        );
        
        formContainer.getChildren().addAll(formHeader, formGrid, buttonBox);
        
        // Handle save button
        saveButton.setOnAction(e -> {
            if (validateForm()) {
                // Create new borrow record
                BorrowRecord newRecord = new BorrowRecord(
                    0, // ID will be assigned by database
                    memberComboBox.getValue().getId(),
                    bookComboBox.getValue().getId(),
                    Date.from(borrowDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(dueDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    null // Not returned yet
                );
                
                boolean saved = borrowRecordDAO.addBorrowRecord(newRecord);
                if (saved) {
                    mainContainer.setCenter(mainView);
                    mainContainer.setTop(null);
                    refreshTable();
                } else {
                    showErrorAlert("Failed to save borrowing record.");
                }
            }
        });
        
        // Handle cancel button
        cancelButton.setOnAction(e -> {
            mainContainer.setCenter(mainView);
            mainContainer.setTop(null);
        });
        
        // Add back button
        Button backButton = new Button("← Back to Borrowing Records");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #303f9f; -fx-font-weight: bold;");
        backButton.setOnAction(e -> {
            mainContainer.setCenter(mainView);
            mainContainer.setTop(null);
        });
        
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setPadding(new Insets(10, 0, 0, 20));
        
        mainContainer.setTop(backButtonContainer);
        mainContainer.setCenter(formContainer);
    }
    
    private void showEditForm(BorderPane mainContainer, Node mainView, BorrowRecord record) {
        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(20));
        
        Text formHeader = new Text("Edit Borrow Record");
        formHeader.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        formHeader.setFill(Color.web("#303f9f"));
        
        // Create form fields
        Label memberLabel = new Label("Member:");
        memberComboBox = new ComboBox<>();
        loadMembersIntoComboBox();
        memberComboBox.setMaxWidth(Double.MAX_VALUE);
        
        // Set selected member
        User selectedMember = userDAO.getUserById(record.getMemberId());
        if (selectedMember != null) {
            memberComboBox.getSelectionModel().select(selectedMember);
        }
        
        Label bookLabel = new Label("Book:");
        bookComboBox = new ComboBox<>();
        loadBooksIntoComboBox();
        bookComboBox.setMaxWidth(Double.MAX_VALUE);
        
        // Set selected book
        Book selectedBook = bookDAO.getBookById(record.getBookId());
        if (selectedBook != null) {
            bookComboBox.getSelectionModel().select(selectedBook);
        }
        
        Label borrowDateLabel = new Label("Borrow Date:");
        borrowDatePicker = new DatePicker();
        borrowDatePicker.setValue(record.getBorrowDate().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate());
        borrowDatePicker.setMaxWidth(Double.MAX_VALUE);
        
        Label dueDateLabel = new Label("Due Date:");
        dueDatePicker = new DatePicker();
        dueDatePicker.setValue(record.getDueDate().toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate());
        dueDatePicker.setMaxWidth(Double.MAX_VALUE);
        
        Label returnDateLabel = new Label("Return Date:");
        returnDatePicker = new DatePicker();
        if (record.getReturnDate() != null) {
            returnDatePicker.setValue(record.getReturnDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        }
        returnDatePicker.setMaxWidth(Double.MAX_VALUE);
        
        CheckBox notReturnedCheckBox = new CheckBox("Not Returned Yet");
        notReturnedCheckBox.setSelected(record.getReturnDate() == null);
        
        // Update return date picker based on checkbox
        notReturnedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            returnDatePicker.setDisable(newVal);
            if (newVal) {
                returnDatePicker.setValue(null);
            } else if (returnDatePicker.getValue() == null) {
                returnDatePicker.setValue(LocalDate.now());
            }
        });
        
        returnDatePicker.setDisable(notReturnedCheckBox.isSelected());
        
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.addRow(0, memberLabel, memberComboBox);
        formGrid.addRow(1, bookLabel, bookComboBox);
        formGrid.addRow(2, borrowDateLabel, borrowDatePicker);
        formGrid.addRow(3, dueDateLabel, dueDatePicker);
        formGrid.addRow(4, returnDateLabel, returnDatePicker);
        formGrid.addRow(5, new Label(""), notReturnedCheckBox);
        
        formGrid.getColumnConstraints().addAll(
            new ColumnConstraints(100), // Label column
            new ColumnConstraints(300)  // Field column
        );
        
        formContainer.getChildren().addAll(formHeader, formGrid, buttonBox);
        
        // Handle save button
        saveButton.setOnAction(e -> {
            if (validateForm()) {
                // Update borrow record
                record.setMemberId(memberComboBox.getValue().getId());
                record.setBookId(bookComboBox.getValue().getId());
                record.setBorrowDate(Date.from(borrowDatePicker.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                record.setDueDate(Date.from(dueDatePicker.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                
                if (notReturnedCheckBox.isSelected()) {
                    record.setReturnDate(null);
                } else {
                    record.setReturnDate(Date.from(returnDatePicker.getValue()
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
                
                boolean updated = borrowRecordDAO.updateBorrowRecord(record);
                if (updated) {
                    mainContainer.setCenter(mainView);
                    mainContainer.setTop(null);
                    refreshTable();
                } else {
                    showErrorAlert("Failed to update borrowing record.");
                }
            }
        });
        
        // Handle cancel button
        cancelButton.setOnAction(e -> {
            mainContainer.setCenter(mainView);
            mainContainer.setTop(null);
        });
        
        // Add back button
        Button backButton = new Button("← Back to Borrowing Records");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #303f9f; -fx-font-weight: bold;");
        backButton.setOnAction(e -> {
            mainContainer.setCenter(mainView);
            mainContainer.setTop(null);
        });
        
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setPadding(new Insets(10, 0, 0, 20));
        
        mainContainer.setTop(backButtonContainer);
        mainContainer.setCenter(formContainer);
    }
    
    private void loadMembersIntoComboBox() {
        // Get all users from DAO
        List<User> allUsers = userDAO.getAllUsers();
        
        // Filter to only include members
        List<User> memberUsers = allUsers.stream()
            .filter(user -> user.getRole() == UserRole.Member)
            .collect(Collectors.toList());
        
        // Set items
        memberComboBox.setItems(FXCollections.observableArrayList(memberUsers));
        
        // Set cell factory to display user name
        memberComboBox.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });
        
        // Set button cell to display selected user name
        memberComboBox.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }
    
    private void loadBooksIntoComboBox() {
        // Get all books from DAO
        List<Book> allBooks = bookDAO.getAllBooks();
        
        // Filter to only include available books
        List<Book> availableBooks = allBooks.stream()
            .filter(Book::isAvailable)  // Assuming your Book class has an isAvailable method
            .collect(Collectors.toList());
        
        // Set items
        bookComboBox.setItems(FXCollections.observableArrayList(availableBooks));
        
        // Set cell factory to display book title
        bookComboBox.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> param) {
                return new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getTitle());
                        }
                    }
                };
            }
        });
        
        // Set button cell to display selected book title
        bookComboBox.setButtonCell(new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });
    }
    
    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (memberComboBox.getValue() == null) {
            errorMessage.append("Please select a member.\n");
        }
        
        if (bookComboBox.getValue() == null) {
            errorMessage.append("Please select a book.\n");
        }
        
        if (borrowDatePicker.getValue() == null) {
            errorMessage.append("Please select a borrow date.\n");
        }
        
        if (dueDatePicker.getValue() == null) {
            errorMessage.append("Please select a due date.\n");
        }
        
        if (borrowDatePicker.getValue() != null && dueDatePicker.getValue() != null) {
            if (borrowDatePicker.getValue().isAfter(dueDatePicker.getValue())) {
                errorMessage.append("Borrow date cannot be after due date.\n");
            }
        }
        
        if (returnDatePicker != null && returnDatePicker.getValue() != null && borrowDatePicker.getValue() != null) {
            if (returnDatePicker.getValue().isBefore(borrowDatePicker.getValue())) {
                errorMessage.append("Return date cannot be before borrow date.\n");
            }
        }
        
        if (errorMessage.length() > 0) {
            showErrorAlert(errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private String formatDate(Date date) {
        if (date == null) {
            return "N/A";
        }
        return dateFormat.format(date);
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
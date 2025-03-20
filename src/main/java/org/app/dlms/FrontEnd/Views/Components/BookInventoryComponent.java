package org.app.dlms.FrontEnd.Views.Components;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.app.dlms.Backend.Dao.BookDAO;
import org.app.dlms.Backend.Dao.GenreDAO;
import org.app.dlms.Backend.Model.Book;
import org.app.dlms.Backend.Model.Genre;

import java.util.List;
import java.util.function.Consumer;

public class BookInventoryComponent {

    private final BookDAO bookDAO;
    private final GenreDAO genreDAO;
    private ObservableList<Book> booksList;
    private ObservableList<Genre> genresList;
    private TableView<Book> booksTable;
    private TextField searchField;

    public BookInventoryComponent() {
        this.bookDAO = new BookDAO();
        this.genreDAO = new GenreDAO();
        this.booksList = FXCollections.observableArrayList();
        this.genresList = FXCollections.observableArrayList();
    }

    public Node createBooksComponent() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Books header
        Text header = new Text("Books Inventory");
        header.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        header.setFill(Color.web("#303f9f"));

        // Search and actions bar
        HBox actionsBar = new HBox(10);
        actionsBar.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Search books...");
        searchField.setPrefWidth(300);

        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                loadAllBooks();
            } else {
                searchBooks(newValue);
            }
        });

        Button addBookBtn = new Button("Add New Book");
        addBookBtn.setStyle("-fx-background-color: #303f9f; -fx-text-fill: white;");
        addBookBtn.setOnAction(e -> showAddBookDialog());

        Button categoryBtn = new Button("Categories");
        categoryBtn.setStyle("-fx-background-color: #5c6bc0; -fx-text-fill: white;");
        categoryBtn.setOnAction(e -> showGenreManagementDialog());

        actionsBar.getChildren().addAll(searchField, addBookBtn, categoryBtn);

        // Books table setup
        setupBooksTable();

        // Load initial data
        loadAllBooks();
        loadAllGenres();

        container.getChildren().addAll(header, actionsBar, booksTable);
        VBox.setVgrow(booksTable, Priority.ALWAYS);

        return container;
    }

    private void setupBooksTable() {
        booksTable = new TableView<>();
        booksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        booksTable.setItems(booksList);

        // Define columns with appropriate property value factories
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        isbnCol.setPrefWidth(100);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(200);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        authorCol.setPrefWidth(150);

        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(cellData -> {
            Genre genre = cellData.getValue().getGenre();
            return new SimpleStringProperty(genre != null ? genre.getName() : "N/A");
        });
        genreCol.setPrefWidth(100);

        TableColumn<Book, Integer> stockCol = new TableColumn<>("Copies");
        stockCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
        stockCol.setPrefWidth(60);

        TableColumn<Book, Boolean> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(cellData.getValue().isAvailable()));
        statusCol.setCellFactory(col -> new TableCell<Book, Boolean>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(available ? "Available" : "Unavailable");
                    setStyle(available
                            ? "-fx-text-fill: green; -fx-font-weight: bold;"
                            : "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });
        statusCol.setPrefWidth(100);

        TableColumn<Book, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<Book, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox actionButtons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editBtn.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showEditBookDialog(book);
                });

                deleteBtn.setOnAction(e -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(book);
                });

                actionButtons.setAlignment(Pos.CENTER);
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
        actionCol.setPrefWidth(150);

        booksTable.getColumns().addAll(isbnCol, titleCol, authorCol, genreCol, stockCol, statusCol, actionCol);
        VBox.setVgrow(booksTable, Priority.ALWAYS);
    }

    private void loadAllBooks() {
        // Run database query in background thread
        new Thread(() -> {
            List<Book> books = bookDAO.getAllBooks();
            Platform.runLater(() -> {
                booksList.clear();
                booksList.addAll(books);
            });
        }).start();
    }

    private void loadAllGenres() {
        // Run database query in background thread
        new Thread(() -> {
            List<Genre> genres = genreDAO.getAllGenres();
            Platform.runLater(() -> {
                genresList.clear();
                genresList.addAll(genres);
            });
        }).start();
    }

    private void searchBooks(String searchTerm) {
        // Run database query in background thread
        new Thread(() -> {
            List<Book> books = bookDAO.searchBooks(searchTerm);
            Platform.runLater(() -> {
                booksList.clear();
                booksList.addAll(books);
            });
        }).start();
    }

    private void showAddBookDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create form fields
        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");

        TextField publisherField = new TextField();
        publisherField.setPromptText("Publisher");

        TextField yearField = new TextField();
        yearField.setPromptText("Publication Year");

        ComboBox<Genre> genreComboBox = new ComboBox<>();
        genreComboBox.setItems(genresList);
        genreComboBox.setCellFactory(param -> new ListCell<Genre>() {
            @Override
            protected void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        genreComboBox.setButtonCell(new ListCell<Genre>() {
            @Override
            protected void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        genreComboBox.setPromptText("Select Genre");

        TextField stockField = new TextField();
        stockField.setPromptText("Stock Quantity");

        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        grid.add(new Label("Publisher:"), 0, 3);
        grid.add(publisherField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Genre:"), 0, 5);
        grid.add(genreComboBox, 1, 5);
        grid.add(new Label("Stock:"), 0, 6);
        grid.add(stockField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the title field by default
        Platform.runLater(titleField::requestFocus);

        // Convert the result when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String title = titleField.getText().trim();
                    String author = authorField.getText().trim();
                    String isbn = isbnField.getText().trim();
                    String publisher = publisherField.getText().trim();
                    int year = Integer.parseInt(yearField.getText().trim());
                    Genre genre = genreComboBox.getValue();
                    int stock = Integer.parseInt(stockField.getText().trim());

                    // Validate fields
                    if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() ||
                            publisher.isEmpty() || genre == null) {
                        showAlert("Validation Error", "All fields must be filled.");
                        return null;
                    }

                    // Create new book object
                    Book newBook = new Book(title, author, isbn, publisher, year, genre.getName(), stock > 0, stock);
                    newBook.setGenre(genre);

                    // Save to database in background thread
                    new Thread(() -> {
                        int id = bookDAO.addBook(newBook);
                        if (id > 0) {
                            newBook.setId(id);
                            Platform.runLater(() -> {
                                booksList.add(newBook);
                                showAlert("Success", "Book added successfully!");
                            });
                        } else {
                            Platform.runLater(() ->
                                    showAlert("Error", "Failed to add book. Please try again."));
                        }
                    }).start();

                    return newBook;
                } catch (NumberFormatException e) {
                    showAlert("Validation Error", "Year and Stock must be valid numbers.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showEditBookDialog(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Update book details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create form fields with existing values
        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField isbnField = new TextField(book.getIsbn());
        isbnField.setDisable(true); // ISBN shouldn't be changed
        TextField publisherField = new TextField(book.getPublisher());
        TextField yearField = new TextField(String.valueOf(book.getYear()));

        ComboBox<Genre> genreComboBox = new ComboBox<>();
        genreComboBox.setItems(genresList);
        genreComboBox.setCellFactory(param -> new ListCell<Genre>() {
            @Override
            protected void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        genreComboBox.setButtonCell(new ListCell<Genre>() {
            @Override
            protected void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        // Find and select the matching genre
        for (Genre genre : genresList) {
            if (book.getGenre() != null && book.getGenre().getId() == genre.getId()) {
                genreComboBox.setValue(genre);
                break;
            }
        }

        TextField stockField = new TextField(String.valueOf(book.getStock()));

        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        grid.add(new Label("Publisher:"), 0, 3);
        grid.add(publisherField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Genre:"), 0, 5);
        grid.add(genreComboBox, 1, 5);
        grid.add(new Label("Stock:"), 0, 6);
        grid.add(stockField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the title field by default
        Platform.runLater(titleField::requestFocus);

        // Convert the result when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Update book with new values
                    book.setTitle(titleField.getText().trim());
                    book.setAuthor(authorField.getText().trim());
                    book.setPublisher(publisherField.getText().trim());
                    book.setYear(Integer.parseInt(yearField.getText().trim()));
                    book.setGenre(genreComboBox.getValue());
                    book.setStock(Integer.parseInt(stockField.getText().trim()));
                    book.setAvailable(book.getStock() > 0);

                    // Update in database in background thread
                    new Thread(() -> {
                        boolean success = bookDAO.updateBook(book);
                        Platform.runLater(() -> {
                            if (success) {
                                // Refresh table to show updated data
                                booksTable.refresh();
                                showAlert("Success", "Book updated successfully!");
                            } else {
                                showAlert("Error", "Failed to update book. Please try again.");
                            }
                        });
                    }).start();

                    return book;
                } catch (NumberFormatException e) {
                    showAlert("Validation Error", "Year and Stock must be valid numbers.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showDeleteConfirmation(Book book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Book");
        alert.setContentText("Are you sure you want to delete \"" + book.getTitle() + "\"?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Delete book in background thread
                new Thread(() -> {
                    boolean success = bookDAO.deleteBook(book.getIsbn());
                    Platform.runLater(() -> {
                        if (success) {
                            booksList.remove(book);
                            showAlert("Success", "Book deleted successfully!");
                        } else {
                            showAlert("Error", "Failed to delete book. Please try again.");
                        }
                    });
                }).start();
            }
        });
    }

    private void showGenreManagementDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Genre Management");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Create table for genres
        TableView<Genre> genreTable = new TableView<>();
        genreTable.setItems(genresList);

        TableColumn<Genre, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Genre, String> nameColumn = new TableColumn<>("Genre Name");
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(200);

        TableColumn<Genre, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<Genre, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox actionButtons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editBtn.setOnAction(e -> {
                    Genre genre = getTableView().getItems().get(getIndex());
                    showEditGenreDialog(genre);
                });

                deleteBtn.setOnAction(e -> {
                    Genre genre = getTableView().getItems().get(getIndex());
                    showDeleteGenreConfirmation(genre);
                });

                actionButtons.setAlignment(Pos.CENTER);
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

        genreTable.getColumns().addAll(idColumn, nameColumn, actionColumn);
        genreTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add new genre controls
        HBox addGenreBox = new HBox(10);
        addGenreBox.setAlignment(Pos.CENTER);

        TextField genreNameField = new TextField();
        genreNameField.setPromptText("New genre name");

        Button addGenreBtn = new Button("Add Genre");
        addGenreBtn.setStyle("-fx-background-color: #303f9f; -fx-text-fill: white;");
        addGenreBtn.setOnAction(e -> {
            String genreName = genreNameField.getText().trim();
            if (!genreName.isEmpty()) {
                Genre newGenre = new Genre();
                newGenre.setName(genreName);

                new Thread(() -> {
                    int id = genreDAO.addGenre(newGenre);
                    if (id > 0) {
                        newGenre.setId(id);
                        Platform.runLater(() -> {
                            genresList.add(newGenre);
                            genreNameField.clear();
                            showAlert("Success", "Genre added successfully!");
                        });
                    } else {
                        Platform.runLater(() ->
                                showAlert("Error", "Failed to add genre. Please try again."));
                    }
                }).start();
            } else {
                showAlert("Validation Error", "Genre name cannot be empty.");
            }
        });

        addGenreBox.getChildren().addAll(genreNameField, addGenreBtn);

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");
        closeButton.setOnAction(e -> dialog.close());

        root.getChildren().addAll(
                new Label("Manage Book Genres"),
                genreTable,
                addGenreBox,
                closeButton
        );

        Scene scene = new Scene(root, 500, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showEditGenreDialog(Genre genre) {
        Dialog<Genre> dialog = new Dialog<>();
        dialog.setTitle("Edit Genre");
        dialog.setHeaderText("Update genre name");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(genre.getName());
        grid.add(new Label("Genre Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(nameField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty()) {
                    genre.setName(newName);

                    new Thread(() -> {
                        boolean success = genreDAO.updateGenre(genre);
                        Platform.runLater(() -> {
                            if (success) {
                                genresList.set(genresList.indexOf(genre), genre);
                                showAlert("Success", "Genre updated successfully!");
                            } else {
                                showAlert("Error", "Failed to update genre. Please try again.");
                            }
                        });
                    }).start();

                    return genre;
                } else {
                    showAlert("Validation Error", "Genre name cannot be empty.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showDeleteGenreConfirmation(Genre genre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Genre");
        alert.setContentText("Are you sure you want to delete the genre \"" + genre.getName() +
                "\"?\nThis may affect books that are assigned to this genre.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    boolean success = genreDAO.deleteGenre(genre.getId());
                    Platform.runLater(() -> {
                        if (success) {
                            genresList.remove(genre);
                            showAlert("Success", "Genre deleted successfully!");
                        } else {
                            showAlert("Error", "Failed to delete genre. This genre may be in use by some books.");
                        }
                    });
                }).start();
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
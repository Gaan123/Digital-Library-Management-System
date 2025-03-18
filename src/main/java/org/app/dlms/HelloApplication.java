package org.app.dlms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.app.dlms.Backend.Dao.BookDAO;
import org.app.dlms.Backend.Model.Book;
import org.app.dlms.Middleware.DatabaseConnection;

import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(HelloApplication.class.getResource("hello-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        BookDAO bookDAO=new BookDAO();
        List<Book> books =  bookDAO.getAllBooks();
        System.out.println(books);
    }
}
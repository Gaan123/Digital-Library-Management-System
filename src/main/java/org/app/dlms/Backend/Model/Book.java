package org.app.dlms.Backend.Model;

/**
 * Represents a book entity in the library management system
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private int year;
    private Genre genre;
    private boolean available;
    private int stock;

    /**
     * Default constructor
     */
    public Book() {
    }

    /**
     * Parameterized constructor
     *
     * @param title Book title
     * @param author Book author
     * @param isbn Book ISBN
     * @param publisher Book publisher
     * @param year Publication year
     * @param category Book category
     * @param available Availability status
     * @param stock Number of copies available
     */
    public Book(String title, String author, String isbn, String publisher, int year, String category, boolean available, int stock) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.year = year;
        this.available = available;
        this.stock = stock;
    }

    /**
     * Full parameterized constructor including ID
     *
     * @param id Book ID
     * @param title Book title
     * @param author Book author
     * @param isbn Book ISBN
     * @param publisher Book publisher
     * @param year Publication year
     * @param category Book category
     * @param available Availability status
     * @param stock Number of copies available
     */
    public Book(int id, String title, String author, String isbn, String publisher, int year, String category, boolean available, int stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.year = year;
        this.available = available;
        this.stock = stock;
    }

    /**
     * Get the book ID
     *
     * @return The book ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set the book ID
     *
     * @param id The book ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the book title
     *
     * @return The book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the book title
     *
     * @param title The book title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the book author
     *
     * @return The book author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the book author
     *
     * @param author The book author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Get the book ISBN
     *
     * @return The book ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Set the book ISBN
     *
     * @param isbn The book ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Get the book publisher
     *
     * @return The book publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Set the book publisher
     *
     * @param publisher The book publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Get the publication year
     *
     * @return The publication year
     */
    public int getYear() {
        return year;
    }

    /**
     * Set the publication year
     *
     * @param year The publication year
     */
    public void setYear(int year) {
        this.year = year;
    }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    /**
     * Check if the book is available
     *
     * @return True if available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Set the availability status
     *
     * @param available The availability status
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Get the number of copies in stock
     *
     * @return The stock quantity
     */
    public int getStock() {
        return stock;
    }

    /**
     * Set the number of copies in stock
     *
     * @param stock The stock quantity
     */
    public void setStock(int stock) {
        this.stock = stock;
        // Update availability based on stock
        this.available = (stock > 0);
    }

    /**
     * Add to the stock quantity
     *
     * @param quantity Quantity to add (positive) or remove (negative)
     * @return True if stock was updated successfully, false if operation would result in negative stock
     */
    public boolean updateStock(int quantity) {
        if (stock + quantity < 0) {
            return false;
        }
        stock += quantity;
        available = (stock > 0);
        return true;
    }

    /**
     * String representation of the Book object
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publisher='" + publisher + '\'' +
                ", year=" + year +
                ", available=" + available +
                ", stock=" + stock +
                '}';
    }

    /**
     * Equals method for Book objects
     *
     * @param o Object to compare
     * @return True if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return isbn != null ? isbn.equals(book.isbn) : book.isbn == null;
    }

    /**
     * HashCode implementation for Book objects
     *
     * @return HashCode value
     */
    @Override
    public int hashCode() {
        return isbn != null ? isbn.hashCode() : 0;
    }
}
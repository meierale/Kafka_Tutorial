package org.meierale.kafkademoproducer;

// Class
public class Book {

    // Class data members
    private String bookName;
    private String isbn;

    // Constructor 1
    public Book() {}

    // Constructor 2
    public Book(String bookName, String isbn)
    {
        this.bookName = bookName;
        this.isbn = isbn;
    }

    // Getter
    public String getBookName() { return bookName; }

    // Setter
    public void setBookName(String bookName)
    {
        this.bookName = bookName;
    }

    // Getter
    public String getIsbn() { return isbn; }

    // Setter
    public void setIsbn(String isbn) { this.isbn = isbn; }
}

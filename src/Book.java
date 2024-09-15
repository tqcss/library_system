package src;

public class Book {
    private final String title;
    private final String genre;
    private final Author author;
    private final int publicationYear;

    public Book(String title, String genre, Author author, int publicationYear) {
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public String getTitle() { return this.title; }
    public String getGenre() { return this.genre; }
    public Author getAuthor() { return this.author; }
    public int getPublicationYear() { return this.publicationYear; }
}

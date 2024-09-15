package src;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.function.Function;

public class Library {
    private static final String AUTHOR_FILE_PATH = "data/authors.csv";
    private static final String BOOK_FILE_PATH = "data/books.csv";

    public static ArrayList<Author> authors = new ArrayList<>();
    public static ArrayList<Book> books = new ArrayList<>();

    // DATA LOADING METHODS
    public static void loadLibrary() {
        createFileIfNotExists(AUTHOR_FILE_PATH);
        createFileIfNotExists(BOOK_FILE_PATH);

        loadData(AUTHOR_FILE_PATH, Library::loadAuthor, authors);
        loadData(BOOK_FILE_PATH, Library::loadBook, books);
    }

    private static void createFileIfNotExists(String filePath) {
        File file = new File(filePath);
        if (file.exists()) { return; }
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (java.io.IOException e) {
            System.err.println("An error occurred while trying to create file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static <T> void loadData(String filePath, Function<String[], T> dataMap, ArrayList<T> collection) {
        String currentLine;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(",");
                collection.add(dataMap.apply(data));
            }
        } catch (java.io.IOException e) {
            System.err.println("An error occurred while trying to read file.");
            System.exit(1);
        }
    }

    private static Author loadAuthor(String[] data) {
        // elements in data should be:
        // {name, nationality}
        String name = data[0];
        String nationality = data[1];

        return new Author(name, nationality);
    }

    private static Book loadBook(String[] data) {
        // elements in data should be:
        // {title, author, genre, publicationYear}
        String title = data[0];
        String genre = data[1];
        Author author = authors.get(Integer.parseInt(data[2]));
        int publicationYear = Integer.parseInt(data[3]);

        Book book = new Book(title, genre, author, publicationYear);
        author.works.add(book);
        return book;
    }

    // DATA SAVING METHODS
    public static void saveLibrary() {
        saveData(AUTHOR_FILE_PATH, Library::saveAuthor, authors);
        saveData(BOOK_FILE_PATH, Library::saveBook, books);
    }

    private static <T> void saveData(String filePath, Function<T, String> dataMap, ArrayList<T> collection) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T element : collection) {
                String data = dataMap.apply(element);
                writer.write(data);
                writer.newLine();
            }
        } catch (java.io.IOException e) {
            System.err.println("An error occurred while trying to read file.");
            System.exit(1);
        }
    }

    private static String saveAuthor(Author author) {
        String name = author.getName();
        String nationality = author.getNationality();

        return String.format("%s,%s", name, nationality);
    }

    private static String saveBook(Book book) {
        String title = book.getTitle();
        String genre = book.getGenre();
        int author = authors.indexOf(book.getAuthor());
        int publicationYear = book.getPublicationYear();

        return String.format("%s,%s,%d,%d", title, genre, author, publicationYear);
    }

    // INSTANTIATING METHODS
    public static Author addAuthor(String name, String nationality) {
        Author author = new Author(name, nationality);
        authors.add(author);
        return author;
    }

    public static Book addBook(String title, String genre, String authorName, String authorNationality, int publicationYear) {
        Author author = findAuthor(authorName, authorNationality);
        if (author == null) { author = addAuthor(authorName, authorNationality); }
        return addBook(title, genre, author, publicationYear);
    }

    public static Book addBook(String title, String genre, Author author, int publicationYear) {
        Book book = new Book(title, genre, author, publicationYear);
        author.works.add(book);
        books.add(book);
        return book;
    }

    // DELETE METHODS
    public static void removeAuthor(Author author) {
        for (Book book : author.works) {
            books.remove(book);
        }
        authors.remove(author);
    }

    public static void removeBook(Book book) {
        book.getAuthor().works.remove(book);
        books.remove(book);
    }

    // SEARCH METHODS
    public static Author findAuthor(String name, String nationality) {
        for (Author author : authors) {
            boolean nameMatches = author.getName().equalsIgnoreCase(name);
            boolean nationalityMatches = author.getNationality().equalsIgnoreCase(nationality);
            if (nameMatches && nationalityMatches) { return author; }
        }
        return null;
    }

    public static Author findAuthor(String name) {
        for (Author author : authors) {
            boolean nameMatches = author.getName().equalsIgnoreCase(name);
            if (nameMatches) { return author; }
        }
        return null;
    }

    public static Book[] findBookByTitle(String title) {
        for (Book book : books) {
            boolean titleMatches = book.getTitle().equalsIgnoreCase(title);
            if (titleMatches) { return new Book[]{book}; }
        }
        return null;
    }

    public static Book[] findBooksByGenre(String genre) {
        ArrayList<Book> results = new ArrayList<>();
        for (Book book : books) {
            boolean genreMatches = book.getGenre().equalsIgnoreCase(genre);
            if (genreMatches) { results.add(book); }
        }
        return results.toArray(new Book[]{});
    }

    // DISPLAY METHODS
    public static void displayBookCollection(ArrayList<Book> bookCollection) {
        int bookSize = bookCollection.size();
        if (bookSize == 0) {
            System.out.println("No results found.");
            return;
        }

        Book currentBook; int i;
        for (i = 0; i < bookSize - 1; i++) {
            currentBook = bookCollection.get(i);
            System.out.printf("├─[TITLE: \"%s\"]\n", currentBook.getTitle());
            System.out.printf("│ ├─[GENRE: \"%s\"]\n", currentBook.getGenre());
            System.out.printf("│ ├─[AUTHOR: \"%s\"]\n", currentBook.getAuthor().getName());
            System.out.printf("│ └─[PUBLICATION YEAR: %d]\n", currentBook.getPublicationYear());
            System.out.println("│");
        }
        currentBook = bookCollection.get(bookSize - 1);
        System.out.printf("└─[TITLE: \"%s\"]\n", currentBook.getTitle());
        System.out.printf("  ├─[GENRE: \"%s\"]\n", currentBook.getGenre());
        System.out.printf("  ├─[AUTHOR: \"%s\"]\n", currentBook.getAuthor().getName());
        System.out.printf("  └─[PUBLICATION YEAR: %d]\n", currentBook.getPublicationYear());
    }

    public static void displayAllAuthors() {
        int size = Library.authors.size();
        for (int i = 0; i < size; i++) {
            Author currentAuthor = Library.authors.get(i);
            System.out.printf("%d - %s (%s Author)\n", i + 1, currentAuthor.getName(), currentAuthor.getNationality());
        }
    }
}

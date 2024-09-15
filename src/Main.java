package src;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Consumer;

public class Main {
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        loadLibraryData();

        while (true) {
            clearTerminal();
            displayOptions();
            executeAction(getAction());
        }
    }

    private static void clearTerminal() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            ProcessBuilder processBuilder = os.contains("win") ? new ProcessBuilder("cmd", "/c", "cls") : new ProcessBuilder("clear");
            Process process = processBuilder.inheritIO().start();
            process.waitFor();
        } catch (Exception e) {
            System.err.printf("An unexpected error occurred.\n%s\n", e.getMessage());
            System.exit(1);
        }
    }

    private static void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (java.lang.InterruptedException e) {
            // pass
        }
    }

    private static void pause() {
        System.out.print("\nPress Enter to continue...");
        input.nextLine();
    }

    private static void loadLibraryData() {
        clearTerminal();
        System.out.println("Loading Library Data...");
        Library.loadLibrary();
        clearTerminal();
        System.out.println("Library Data loaded successfully!");
        wait(2000);
    }

    private static void displayOptions() {
        System.out.println("──────────[ WELCOME TO THE ARCHIVE ]──────────");
        System.out.println("Enter the number of the action you want to do.");
        System.out.println("  1 - Display All Books");
        System.out.println("  2 - Add a Book");
        System.out.println("  3 - Add an Author");
        System.out.println("  4 - Remove a Book");
        System.out.println("  5 - Remove an Author");
        System.out.println("  6 - Find Book by Title");
        System.out.println("  7 - Find Books by Genre");
        System.out.println("  8 - View Author Biography");
        System.out.println("  9 - Save and Exit Library");
        System.out.println("──────────────────────────────────────────────");
    }

    private static int getAction() {
        System.out.print("> ");
        int action = input.nextInt();
        input.nextLine();

        return action;
    }

    private static void executeAction(int action) {
        System.out.println();
        clearTerminal();

        switch (action) {
            case 1: displayAllBooks(); break;
            case 2: addBook(); break;
            case 3: addAuthor(); break;
            case 4:
                String bElementName = "Book";
                ArrayList<Book> bCollection = Library.books;
                Consumer<String[]> bDisplayMethod = Main::displayBookInfo;
                Consumer<Book> bRemoveMethod = Library::removeBook;
                String bDisplayFormat = "%d - \"%s\" by %s\n";
                Function<Book, String[]> bDataMap = data -> new String[]{
                        data.getTitle(),
                        data.getAuthor().getName(),
                        data.getGenre(),
                        Integer.toString(data.getPublicationYear())
                };

                removeElement(bElementName, bCollection, bDisplayMethod, bRemoveMethod, bDisplayFormat, bDataMap);
                break;
            case 5:
                String aElementName = "Author";
                ArrayList<Author> aCollection = Library.authors;
                Consumer<String[]> aDisplayMethod = Main::displayAuthorInfo;
                Consumer<Author> aRemoveMethod = Library::removeAuthor;
                String aDisplayFormat = "%d - %s (%s Author)\n";
                Function<Author, String[]> aDataMap = data -> new String[]{
                        data.getName(),
                        data.getNationality()
                };

                removeElement(aElementName, aCollection, aDisplayMethod, aRemoveMethod, aDisplayFormat, aDataMap);
                break;
            case 6:
                String tFilterName = "Title";
                Function<String, Book[]> tSearchMethod = Library::findBookByTitle;

                findBook(tFilterName, tSearchMethod);
                break;
            case 7:
                String gFilterName = "Genre";
                Function<String, Book[]> gSearchMethod = Library::findBooksByGenre;

                findBook(gFilterName, gSearchMethod);
                break;
            case 8: viewAuthorBiography(); break;
            case 9: saveAndExitLibrary(); break;
            default:
                System.out.println("Invalid Input.");
                wait(1000);
        }
    }

    private static void displayAllBooks() {
        System.out.println("[ALL BOOKS IN LIBRARY]");
        Library.displayBookCollection(Library.books);
        pause();
    }

    private static void addBook() {
        String title, genre;
        Author author;
        int publicationYear;

        System.out.print("Enter the Book Title.\n> ");
        title = input.nextLine();

        System.out.print("\nEnter the Book's genre.\n> ");
        genre = input.nextLine();

        int size = Library.authors.size();
        while (true) {
            System.out.println("\n[LIST OF AUTHORS]");
            Library.displayAllAuthors();
            System.out.printf("%d - Cancel Process\n", size + 1);
            System.out.print("\nSelect an author from the list above. (Is the author of the book not in the list? Cancel this process and add a new author.\n> ");

            int index = input.nextInt();
            input.nextLine();

            if (index == size + 1) { return; }
            if (index <= 0 || index > size) {
                System.out.println("Author not found.");
                wait(1000);
                clearTerminal();
                continue;
            }
            author = Library.authors.get(index - 1);
            break;
        }

        System.out.print("\nEnter the year the book was published.\n> ");
        publicationYear = input.nextInt();
        input.nextLine();

        Book book = Library.addBook(title, genre, author, publicationYear);
        clearTerminal();
        System.out.println("[A new book has been added to the library]");
        System.out.printf("TITLE: \"%s\"\n", book.getTitle());
        System.out.printf("GENRE: \"%s\"\n", book.getGenre());
        System.out.printf("AUTHOR: \"%s\"\n", book.getAuthor().getName());
        System.out.printf("PUBLICATION YEAR: %s\n", book.getPublicationYear());
        pause();
    }

    private static void addAuthor() {
        String name, nationality;

        System.out.print("Enter the Author's name\n> ");
        name = input.nextLine();

        System.out.print("\nEnter the Author's nationality\n> ");
        nationality = input.nextLine();

        Author author = Library.addAuthor(name, nationality);
        clearTerminal();
        System.out.println("[A new author has been added to the library]");
        System.out.printf("NAME: \"%s\"\n", author.getName());
        System.out.printf("NATIONALITY: \"%s\"\n", author.getNationality());
        pause();
    }

    private static <T> void removeElement(
            String elementName,
            ArrayList<T> collection,
            Consumer<String[]> displayMethod,
            Consumer<T> removeMethod,
            String displayFormat,
            Function<T, String[]> dataMap
    ) {
        int index, size = collection.size();

        while (true) {
            for (int i = 0; i < size; i++) {
                T element = collection.get(i);
                String[] data = dataMap.apply(element);
                System.out.printf(displayFormat, i + 1, data[0], data[1]);
            }
            System.out.printf("%d - Cancel Process\n", size + 1);

            System.out.printf("Enter the index of the %s you want to remove.\n> ", elementName);
            index = input.nextInt();
            input.nextLine();

            if (index == size + 1) {
                return;
            }
            if (index <= 0 || index > size) {
                System.out.printf("%s with the index `%d` not found.\n", elementName, index);
                wait(1000);
                clearTerminal();
                continue;
            }
            break;
        }

        T element = collection.get(index - 1);
        String[] data = dataMap.apply(element);
        removeMethod.accept(element);

        clearTerminal();
        System.out.printf("[%s has been removed]\n", elementName);
        displayMethod.accept(data);
        pause();
    }

    private static void findBook(String filterName, Function<String, Book[]> searchMethod) {
        Book[] results;
        String searchInput;

        System.out.printf("Enter the %s of the Book you want to find.\n> ", filterName);
        searchInput = input.nextLine();
        results = searchMethod.apply(searchInput);

        clearTerminal();
        System.out.printf("Returning results from \"%s\" in %ss.\n", searchInput, filterName);
        for (Book result : results) {
            int index = Library.books.indexOf(result) + 1;
            String title = result.getTitle();
            String authorName = result.getAuthor().getName();

            System.out.printf("INDEX %d - \"%s\" by %s\n", index, title, authorName);
        }
        pause();
    }

    private static void viewAuthorBiography() {
        Library.displayAllAuthors();

        System.out.print("Enter the index of an Author.\n> ");
        int index = input.nextInt();
        input.nextLine();

        clearTerminal();
        Author author = Library.authors.get(index - 1);
        displayAuthorInfo(new String[]{author.getName(), author.getNationality()});
        System.out.println("[AUTHOR'S WORKS]");
        Library.displayBookCollection(author.works);
        pause();
    }

    private static void saveAndExitLibrary() {
        System.out.println("Saving Library...");
        Library.saveLibrary();

        clearTerminal();
        System.out.println("Library Saved Successfully! Closing Program.");
        wait(2000);

        System.exit(0);
    }

    private static void displayBookInfo(String[] data) {
        System.out.printf("TITLE: \"%s\"\n", data[0]);
        System.out.printf("GENRE: \"%s\"\n", data[2]);
        System.out.printf("AUTHOR: \"%s\"\n", data[1]);
        System.out.printf("PUBLICATION YEAR: \"%s\"\n", data[3]);
    }

    private static void displayAuthorInfo(String[] data) {
        System.out.printf("NAME: \"%s\"\n", data[0]);
        System.out.printf("NATIONALITY: \"%s\"\n", data[1]);
    }
}

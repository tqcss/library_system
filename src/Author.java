package src;
import java.util.ArrayList;

public class Author {
    private final String name;
    private final String nationality;
    public ArrayList<Book> works = new ArrayList<>();

    public Author(String name, String nationality) {
        this.name = name;
        this.nationality = nationality;
    }

    public String getName() { return this.name; }
    public String getNationality() { return this.nationality; }
}

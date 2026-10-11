import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

// MemberStore = keeps the list of members and saves/loads it from members.txt.
// Jeet's MemberDAO extends this class, so "members" is protected.
public class MemberStore {

    // protected = this class AND its child class (MemberDAO) can use it
    protected ArrayList<Member> members = new ArrayList<Member>();

    // Constructor: runs when the object is created, loads the saved members
    public MemberStore() {
        load();
    }

    // Reads members.txt line by line and fills the list
    protected void load() {
        members.clear();

        File file = new File("members.txt");
        if (!file.exists()) {
            return;   // no file yet (first run), so just keep the empty list
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                if (!line.trim().isEmpty()) {
                    members.add(Member.fromFileString(line));
                }
                line = reader.readLine();   // go to the next line
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Could not read members.txt: " + e.getMessage());
        }
    }

    // Writes the whole list into members.txt (one member per line)
    protected void save() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("members.txt"));
            for (Member m : members) {
                writer.println(m.toFileString());
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Could not write members.txt: " + e.getMessage());
        }
    }

    // Adds a new member to the list, then saves the file
    public void add(Member m) {
        members.add(m);
        save();
    }

    // Gives back the whole list
    public ArrayList<Member> getAll() {
        return members;
    }

    // Gives the next free id: highest id + 1 (or 1 if the list is empty)
    public int getNextId() {
        int highest = 0;
        for (Member m : members) {
            if (m.getId() > highest) {
                highest = m.getId();
            }
        }
        return highest + 1;
    }
} 


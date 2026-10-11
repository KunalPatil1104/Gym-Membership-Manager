import java.time.LocalDate;

// Member = the details of ONE gym member.
// One line in members.txt looks like:  1,Rahul,Monthly,2025-01-10,2025-02-10,true
public class Member {

    private int id;
    private String name;
    private String plan;
    private LocalDate joinDate;
    private LocalDate expiryDate;
    private boolean feePaid;

    // Constructor: fills in all the details when we create a member
    public Member(int id, String name, String plan, LocalDate joinDate,
                  LocalDate expiryDate, boolean feePaid) {
        this.id = id;
        this.name = name;
        this.plan = plan;
        this.joinDate = joinDate;
        this.expiryDate = expiryDate;
        this.feePaid = feePaid;
    }

    // Getters: let other classes READ the details
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPlan() { return plan; }
    public LocalDate getJoinDate() { return joinDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public boolean isFeePaid() { return feePaid; }

    // Setters: let other classes CHANGE the details (id never changes)
    public void setName(String name) { this.name = name; }
    public void setPlan(String plan) { this.plan = plan; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setFeePaid(boolean feePaid) { this.feePaid = feePaid; }

    // Turns the member into ONE line of text to store in members.txt
    public String toFileString() {
        return id + "," + name + "," + plan + "," + joinDate + "," + expiryDate + "," + feePaid;
    }

    // Does the opposite: takes one line from members.txt and builds a Member
    public static Member fromFileString(String line) {
        String[] parts = line.split(",");   // cut the line at every comma

        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String plan = parts[2];
        LocalDate joinDate = LocalDate.parse(parts[3]);
        LocalDate expiryDate = LocalDate.parse(parts[4]);
        boolean feePaid = Boolean.parseBoolean(parts[5]);

        return new Member(id, name, plan, joinDate, expiryDate, feePaid);
    }

    // Days from today to the expiry date. Negative means already expired.
    public int getDaysLeft() {
        long today = LocalDate.now().toEpochDay();   // today as a day number
        long expiry = expiryDate.toEpochDay();       // expiry as a day number
        return (int) (expiry - today);
    }
}

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

// MemberForm = the small window used for Add and Update.
// existing == null  -> Add (empty form)
// existing != null  -> Update (form is pre-filled with that member)
public class MemberForm extends JDialog {

    private MemberDAO dao;       // used to save the member
    private MainFrame parent;    // used to refresh the table afterwards
    private Member existing;

    private JTextField nameField;
    private JComboBox<String> planBox;
    private JTextField joinField;
    private JCheckBox paidBox;

    public MemberForm(MemberDAO dao, MainFrame parent, Member existing) {
        super(parent, "Member Form", true);   // true = user must close it first
        this.dao = dao;
        this.parent = parent;
        this.existing = existing;

        // Create the input fields
        nameField = new JTextField();
        planBox = new JComboBox<String>(new String[]{"Monthly", "Quarterly", "Yearly"});
        joinField = new JTextField(LocalDate.now().toString());   // default = today
        paidBox = new JCheckBox("Fee paid");

        // If we are updating, fill the fields with the old values
        if (existing != null) {
            nameField.setText(existing.getName());
            planBox.setSelectedItem(existing.getPlan());
            joinField.setText(existing.getJoinDate().toString());
            paidBox.setSelected(existing.isFeePaid());
        }

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Put everything in the window (5 rows, 2 columns)
        setLayout(new GridLayout(5, 2, 8, 8));
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Plan:"));
        add(planBox);
        add(new JLabel("Join date (yyyy-MM-dd):"));
        add(joinField);
        add(new JLabel("Fee:"));
        add(paidBox);
        add(saveButton);
        add(cancelButton);

        // What happens when Save is clicked
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveMember();
            }
        });

        // What happens when Cancel is clicked: just close the window
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setSize(380, 240);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Works out the expiry date from the join date and the plan
    private LocalDate calculateExpiry(LocalDate join, String plan) {
        if (plan.equals("Monthly")) {
            return join.plusMonths(1);
        } else if (plan.equals("Quarterly")) {
            return join.plusMonths(3);
        } else {
            return join.plusMonths(12);   // Yearly
        }
    }

    // Checks the input. Shows an error message and returns false if something is wrong.
    private boolean validateInput() {
        // Check 1: name must not be empty
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.");
            return false;
        }

        // Check 2: date must be in the format yyyy-MM-dd
        try {
            LocalDate.parse(joinField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Date must look like 2025-01-31.");
            return false;
        }

        return true;   // everything is fine
    }

    // Runs when Save is clicked
    private void saveMember() {
        if (!validateInput()) {
            return;   // error was shown, so save nothing
        }

        // Read the values from the fields
        // (commas are replaced because our file uses commas to separate values)
        String name = nameField.getText().trim().replace(",", " ");
        String plan = (String) planBox.getSelectedItem();
        LocalDate join = LocalDate.parse(joinField.getText().trim());
        LocalDate expiry = calculateExpiry(join, plan);
        boolean paid = paidBox.isSelected();

        if (existing == null) {
            // ADD: make a new member with a new id
            Member m = new Member(dao.getNextId(), name, plan, join, expiry, paid);
            dao.add(m);
        } else {
            // UPDATE: keep the same id, change the other details
            Member m = new Member(existing.getId(), name, plan, join, expiry, paid);
            dao.update(m);
        }

        parent.refreshTable();   // show the change in the main table
        dispose();               // close the form
    }
}
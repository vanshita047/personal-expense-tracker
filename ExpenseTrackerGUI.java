import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class Expense {
    LocalDate date;
    String category;
    double amount;
    String description;

    public Expense(LocalDate date, String category, double amount, String description) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public String toCSV() {
        return date + "," + category + "," + amount + "," + description;
    }

    public Object[] toRow() {
        return new Object[]{date.toString(), category, amount, description};
    }
}

public class ExpenseTrackerGUI extends JFrame {
    private static final String FILE_NAME = "expenses.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ArrayList<Expense> expenses = new ArrayList<>();
    private DefaultTableModel tableModel;

    private JTextField dateField, categoryField, amountField, descriptionField;

    public ExpenseTrackerGUI() {
        setTitle("Personal Expense Tracker");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        inputPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        JButton addButton = new JButton("Add Expense");
        inputPanel.add(addButton);

        JButton saveButton = new JButton("Save & Exit");
        inputPanel.add(saveButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Date", "Category", "Amount", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load existing data
        loadExpensesFromFile();

        // Event: Add button
        addButton.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText(), formatter);
                String category = categoryField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();

                Expense expense = new Expense(date, category, amount, description);
                expenses.add(expense);
                tableModel.addRow(expense.toRow());

                dateField.setText("");
                categoryField.setText("");
                amountField.setText("");
                descriptionField.setText("");

                JOptionPane.showMessageDialog(this, "✅ Expense added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid input. Please check your fields.");
            }
        });

        // Event: Save button
        saveButton.addActionListener(e -> {
            saveExpensesToFile();
            JOptionPane.showMessageDialog(this, "💾 Expenses saved. Goodbye!");
            System.exit(0);
        });

        setVisible(true);
    }

    private void loadExpensesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                LocalDate date = LocalDate.parse(parts[0], formatter);
                String category = parts[1];
                double amount = Double.parseDouble(parts[2]);
                String description = parts[3];

                Expense e = new Expense(date, category, amount, description);
                expenses.add(e);
                tableModel.addRow(e.toRow());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading expenses.");
        }
    }

    private void saveExpensesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                writer.println(e.toCSV());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Error saving expenses.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerGUI());
    }
}

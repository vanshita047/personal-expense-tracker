import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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

    public Object[] toRow() {
        return new Object[]{date.toString(), category, amount, description};
    }

    public String toCSV() {
        return date + "," + category + "," + amount + "," + description;
    }
}

public class AdvancedExpenseTracker extends JFrame {
    private List<Expense> expenses = new ArrayList<>();
    private DefaultTableModel tableModel;

    private JTextField dateField, categoryField, amountField, descriptionField, searchField;
    private JLabel totalLabel, categoryLabel;

    private final String FILE_NAME = "advanced_expenses.csv";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AdvancedExpenseTracker() {
        setTitle("Advanced Personal Expense Tracker");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inputs
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Expense"));

        dateField = new JTextField();
        categoryField = new JTextField();
        amountField = new JTextField();
        descriptionField = new JTextField();

        inputPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);

        JButton addButton = new JButton("Add Expense");
        JButton exportButton = new JButton("Save");

        inputPanel.add(addButton);
        inputPanel.add(exportButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Date", "Category", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Footer Panel: Search, Totals
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        JPanel searchPanel = new JPanel();
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search by Category");
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        JPanel totalsPanel = new JPanel();
        totalLabel = new JLabel("Total: ₹0.00");
        categoryLabel = new JLabel("Category Total: ₹0.00");
        totalsPanel.add(totalLabel);
        totalsPanel.add(categoryLabel);

        bottomPanel.add(searchPanel);
        bottomPanel.add(totalsPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load from file
        loadExpensesFromFile();
        updateTotals();

        // Add Expense Action
        addButton.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText());
                String category = categoryField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();

                Expense ex = new Expense(date, category, amount, description);
                expenses.add(ex);
                tableModel.addRow(ex.toRow());

                clearInputs();
                updateTotals();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid input.");
            }
        });

        // Export/save
        exportButton.addActionListener(e -> {
            saveExpensesToFile();
            JOptionPane.showMessageDialog(this, "💾 Expenses saved to file!");
        });

        // Search by category
        searchButton.addActionListener(e -> {
            String searchCat = searchField.getText().trim().toLowerCase();
            List<Expense> filtered = expenses.stream()
                    .filter(x -> x.category.toLowerCase().contains(searchCat))
                    .collect(Collectors.toList());

            showFilteredExpenses(filtered);
        });

        // Show all
        showAllButton.addActionListener(e -> showFilteredExpenses(expenses));

        setVisible(true);
    }

    private void clearInputs() {
        dateField.setText("");
        categoryField.setText("");
        amountField.setText("");
        descriptionField.setText("");
    }

    private void showFilteredExpenses(List<Expense> list) {
        tableModel.setRowCount(0);
        double categoryTotal = 0;

        for (Expense e : list) {
            tableModel.addRow(e.toRow());
            categoryTotal += e.amount;
        }

        categoryLabel.setText("Category Total: ₹" + String.format("%.2f", categoryTotal));
    }

    private void updateTotals() {
        double total = 0;
        for (Expense e : expenses) {
            total += e.amount;
        }
        totalLabel.setText("Total: ₹" + String.format("%.2f", total));
    }

    private void loadExpensesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                LocalDate date = LocalDate.parse(parts[0]);
                String category = parts[1];
                double amount = Double.parseDouble(parts[2]);
                String description = parts[3];

                Expense e = new Expense(date, category, amount, description);
                expenses.add(e);
                tableModel.addRow(e.toRow());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Failed to load data.");
        }
    }

    private void saveExpensesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                writer.println(e.toCSV());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Failed to save data.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdvancedExpenseTracker::new);
    }
}

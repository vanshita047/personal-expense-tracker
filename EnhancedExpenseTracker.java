import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EnhancedExpenseTracker extends JFrame {
    private JComboBox<String> categoryDropdown;
    private JTextField amountField;
    private JTextField dateField;
    private JTable table;
    private JLabel totalLabel, budgetLabel;
    private ArrayList<Expense> expenses;
    private double monthlyBudget = 0.0;

    public EnhancedExpenseTracker() {
        setTitle("Enhanced Expense Tracker");
        setSize(800, 600);
        setLayout(new BorderLayout());

        expenses = new ArrayList<>();

        JPanel topPanel = new JPanel(new GridLayout(2, 3, 4, 4));
        topPanel.setBorder(BorderFactory.createTitledBorder("Add New Expense"));

        categoryDropdown = new JComboBox<>(new String[]{"Food", "Transport", "Shopping", "Bills", "Others"});
        amountField = new JTextField();
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryDropdown);
        topPanel.add(new JLabel("Amount:"));
        topPanel.add(amountField);
        topPanel.add(new JLabel("Date (dd-MM-yyyy):"));
        topPanel.add(dateField);

        JButton addButton = new JButton("Add Expense");
        topPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Category", "Amount", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel midPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        midPanel.setBorder(BorderFactory.createTitledBorder("Filters and Search"));

        JComboBox<String> filterCategory = new JComboBox<>(new String[]{"All", "Food", "Transport", "Shopping", "Bills", "Others"});
        JComboBox<String> filterMonth = new JComboBox<>(new String[]{"All", "January", "February", "March", "April", "May", "June", 
                                                                       "July", "August", "September", "October", "November", "December"});
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");

        midPanel.add(new JLabel("Filter by Category:"));
        midPanel.add(filterCategory);
        midPanel.add(new JLabel("Filter by Month:"));
        midPanel.add(filterMonth);
        midPanel.add(new JLabel("Search (Category):"));
        midPanel.add(searchField);
        midPanel.add(searchButton);

        add(midPanel, BorderLayout.WEST);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        totalLabel = new JLabel("Total Expense: ₹0.00");
        budgetLabel = new JLabel("Monthly Budget: ₹0.00");
        JButton setBudgetBtn = new JButton("Set Budget");
        JButton clearBtn = new JButton("Clear All");
        JButton exitBtn = new JButton("Exit");

        bottomPanel.add(totalLabel);
        bottomPanel.add(budgetLabel);
        bottomPanel.add(setBudgetBtn);
        bottomPanel.add(clearBtn);
        bottomPanel.add(exitBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // Add Listeners
        addButton.addActionListener(e -> {
            try {
                String category = (String) categoryDropdown.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                Expense ex = new Expense(category, amount, date);
                expenses.add(ex);
                model.addRow(new Object[]{category, amount, date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))});
                updateTotal();
                amountField.setText("");
                dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            } catch (Exception ex1) {
                JOptionPane.showMessageDialog(this, "Invalid data format.");
            }
        });

        setBudgetBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter monthly budget:");
            try {
                monthlyBudget = Double.parseDouble(input);
                budgetLabel.setText("Monthly Budget: ₹" + monthlyBudget);
                updateTotal();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid budget.");
            }
        });

        clearBtn.addActionListener(e -> {
            expenses.clear();
            model.setRowCount(0);
            updateTotal();
        });

        exitBtn.addActionListener(e -> System.exit(0));

        searchButton.addActionListener(e -> {
            String search = searchField.getText().toLowerCase();
            model.setRowCount(0);
            for (Expense ex : expenses) {
                if (ex.getCategory().toLowerCase().contains(search) || ex.getDate().toString().contains(search)) {
                    model.addRow(new Object[]{ex.getCategory(), ex.getAmount(), ex.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))});
                }
            }
        });

        filterCategory.addActionListener(e -> {
            String selected = (String) filterCategory.getSelectedItem();
            model.setRowCount(0);
            for (Expense ex : expenses) {
                if (selected.equals("All") || ex.getCategory().equals(selected)) {
                    model.addRow(new Object[]{ex.getCategory(), ex.getAmount(), ex.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))});
                }
            }
        });

        filterMonth.addActionListener(e -> {
            String selected = (String) filterMonth.getSelectedItem();
            model.setRowCount(0);
            for (Expense ex : expenses) {
                if (selected.equals("All") || ex.getDate().getMonth().name().equalsIgnoreCase(selected)) {
                    model.addRow(new Object[]{ex.getCategory(), ex.getAmount(), ex.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))});
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void updateTotal() {
        double total = 0.0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        totalLabel.setText("Total Expense: ₹" + total);
        if (monthlyBudget > 0 && total > monthlyBudget) {
            totalLabel.setForeground(Color.RED);
        } else {
            totalLabel.setForeground(Color.BLACK);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EnhancedExpenseTracker::new);
    }

    class Expense {
        private String category;
        private double amount;
        private LocalDate date;

        public Expense(String category, double amount, LocalDate date) {
            this.category = category;
            this.amount = amount;
            this.date = date;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }

        public LocalDate getDate() {
            return date;
        }
    }
}